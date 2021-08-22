package com.android.helper.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;

import com.android.helper.common.EventMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 蓝牙的工具类
 */
public class BluetoothUtil {
    /**
     * 写入日志的文件名字
     */
    public static final String FILE_NAME = "AppLifecycle";

    private BluetoothManager mBluetoothManager;
    private final Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static BluetoothUtil bluetoothUtil;
    private BluetoothAdapter mAdapter;
    private BluetoothLeScanner mScanner;
    private Intent mIntentBluetooth;
    private boolean isScan;// 是否正在扫描中
    private LocationManager locationManager;
    private BluetoothReceiver mBluetoothReceiver;
    private long mStartScan;

    public static BluetoothUtil getInstance(Context context) {
        if (bluetoothUtil == null) {
            bluetoothUtil = new BluetoothUtil(context);
        }
        return bluetoothUtil;
    }

    private BluetoothUtil(Context context) {
        mContext = context;
        if (mBluetoothManager == null) {
            if (mContext != null) {
                mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            }
        }

        if (mBluetoothManager != null) {
            if (mAdapter == null) {
                mAdapter = mBluetoothManager.getAdapter();
            }
        }

        if (mIntentBluetooth == null) {
            if (mContext != null) {
                LogUtil.e("重新去注册蓝牙广播！");
                mBluetoothReceiver = new BluetoothReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

                intentFilter.addAction("android.bluetoothReceiver.BluetoothAdapter.STATE_OFF");
                intentFilter.addAction("android.bluetoothReceiver.BluetoothAdapter.STATE_ON");

                // 定位信息
                intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
                mIntentBluetooth = mContext.registerReceiver(mBluetoothReceiver, intentFilter);
            }
        }
    }

    /**
     * @return 蓝牙是否已经打开了
     */
    public boolean isOpenBluetooth() {
        if (mBluetoothManager == null) {
            return false;
        }
        if (mAdapter == null) {
            mAdapter = mBluetoothManager.getAdapter();
        }
        return (mAdapter != null) && (mAdapter.isEnabled());
    }

    /**
     * 蓝牙打开
     */
    public void openBluetooth() {
        LogUtil.e("手动去打开蓝牙！");
        boolean openBluetooth = isOpenBluetooth();
        if (openBluetooth) {
            Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (mContext != null) {
                mContext.startActivity(enable);
            }
        }
    }

    /**
     * 开始扫描
     */
    public void startScan() {
        // 监听蓝牙状态
        boolean openBluetooth = isOpenBluetooth();
        boolean locationEnable = checkLocationEnable();

        if (!locationEnable) {
            ToastUtil.show("请打开定位权限！");
            return;
        }

        if (openBluetooth) {
            if (mAdapter != null) {
                // getPdList();

                // 搜索附近的设备
//                  startDiscovery();
                startScanBackground();
            }
        } else {
            openBluetooth();
        }
    }

    // 蓝牙扫描回调
    private final ScanCallback mScanCallback = new ScanCallback() {

        //当一个蓝牙ble广播被发现时回调
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            isScan = false;
            if (result != null) {
                BluetoothDevice device = result.getDevice();
                if (device != null) {
                    String address = device.getAddress();
                    String name = device.getName();

                    sendDevice(device);

                    LogUtil.writeDe(FILE_NAME, "蓝牙扫描回调---成功：" + name + "  描到的蓝牙地址为：" + address);
                    LogUtil.e("蓝牙扫描回调---成功：" + name + "  描到的蓝牙地址为：" + address);
                    if (mScanner != null) {
                        if (isOpenBluetooth()) {
                            mScanner.stopScan(mScanCallback);
                        }
                    }
                }
            }
        }

        //批量返回扫描结果
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            isScan = false;

            if (results != null) {
                for (int i = 0; i < results.size(); i++) {
                    ScanResult scanResult = results.get(i);
                    BluetoothDevice device = scanResult.getDevice();
                    if (device != null) {
                        String address = device.getAddress();
                        String name = device.getName();
                        LogUtil.writeDe(FILE_NAME, "蓝牙扫描回调---批量成功：" + name + "  描到的蓝牙地址为：" + address);
                        LogUtil.e("蓝牙扫描回调---批量成功：" + name + "  描到的蓝牙地址为：" + address);
                    }
                }
            }
        }

        //当扫描不能开启时回调
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            isScan = false;

            LogUtil.writeDe(FILE_NAME, "蓝牙扫描回调---异常：onScanFailed！" + errorCode);
            LogUtil.e("蓝牙扫描回调---异常：onScanFailed！" + errorCode);

            if (mScanner != null) {
                if (isOpenBluetooth()) {
                    mScanner.stopScan(mScanCallback);
                }
            }
        }
    };

    class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if ((intent != null) && (intent.getAction() != null)) {
                String action = intent.getAction();
                LogUtil.e("action:" + action);
                switch (action) {
                    case LocationManager.PROVIDERS_CHANGED_ACTION:// 定位的监听

                        break;

                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        LogUtil.e("蓝牙设备: 已链接");
                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        LogUtil.e("蓝牙设备: 已断开");
                        break;

                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int intExtra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (intExtra) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                LogUtil.e("蓝牙设备: 正在打开蓝牙");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                LogUtil.e("蓝牙设备: 蓝牙已打开");
                                startScan();
                                break;

                            case BluetoothAdapter.STATE_TURNING_OFF:
                                LogUtil.e("蓝牙设备: 正在关闭蓝牙");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                LogUtil.e("蓝牙设备: 蓝牙已关闭");
                                isScan = false;
                                break;
                        }
                        break;

                    case BluetoothDevice.ACTION_FOUND: { //found device
                        Parcelable parcelableExtra = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (parcelableExtra instanceof BluetoothDevice) {
                            BluetoothDevice device = (BluetoothDevice) parcelableExtra;
                            String address = device.getAddress();
                            String name = device.getName();
                            LogUtil.e(" --> 发现的蓝牙名字：" + name + "  发现到的蓝牙地址：" + address);

                            sendDevice(device);
                        }
                    }
                    break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        LogUtil.e("---> 蓝牙扫描中！");
                        isScan = true;
                        break;

                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        LogUtil.e("---> 扫描完成，点击列表中的设备来尝试连接！");
                        isScan = false;
                        mStartScan = System.currentTimeMillis();
                        break;
                }
            }
        }

    }

    /**
     * 检测定位是否开启
     */
    private boolean checkLocationEnable() {
        if (locationManager == null) {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean netWork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps && netWork;
    }

    /**
     * 获取已经配对的设备
     */
    public void getPdList() {
        if (mAdapter != null) {
            Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    // 把名字和地址取出来添加到适配器中
                    sendDevice(device);
                }
            }
        }
    }

    public void startDiscovery() {
        if (mAdapter != null) {
            if (isScan) {
                long end = System.currentTimeMillis();
                if (((end - mStartScan) / 1000) > 20) {
                    mAdapter.cancelDiscovery();
                    LogUtil.e("扫描的时候，发现20秒还没有扫描到，就先关闭，下次重新再扫描！");
                    mStartScan = end;
                    isScan = false;
                } else {
                    LogUtil.e("蓝牙正在扫描中~~");
                }
            } else {
                mAdapter.startDiscovery();
                LogUtil.e("------开始扫描蓝牙------");
                isScan = true;
                mStartScan = System.currentTimeMillis();
            }
        }
    }

    /**
     * 可以设置后台模式
     */
    private void startScanBackground() {
        mScanner = mAdapter.getBluetoothLeScanner();
        if (isScan) {
            long end = System.currentTimeMillis();
            if (((end - mStartScan) / 1000) > 20) {
                if (mScanner != null) {
                    if (isOpenBluetooth()) {
                        mScanner.stopScan(mScanCallback);
                        LogUtil.e("扫描的时候，发现20秒还没有扫描到，就先关闭，下次重新再扫描！");
                    }
                }
                mStartScan = end;
                isScan = false;
            } else {
                LogUtil.e("蓝牙正在扫描中~~");
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                ScanSettings mScannerSetting = new ScanSettings.Builder()
                        //  退到后台时设置扫描模式为低功耗
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                        //  使用最高占空比进行扫描。建议只在应用程序处于此模式时使用此模式在前台运行
                        //  .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        //  设置蓝牙LE扫描滤波器硬件匹配的匹配模式
                        //  在主动模式下，即使信号强度较弱，hw也会更快地确定匹配.在一段时间内很少有目击/匹配。
                        //  .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                        .build();

                List<ScanFilter> scanFilterList = new ArrayList<>();
                // 通过服务 uuid 过滤自己要连接的设备   过滤器搜索GATT服务UUID
//                ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
//                ParcelUuid parcelUuidMask = ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
//                ParcelUuid parcelUuid = ParcelUuid.fromString("0000ff07-0000-1000-8000-00805f9b34fb");
//                scanFilterBuilder.setServiceUuid(parcelUuid, parcelUuidMask);
//                scanFilterList.add(scanFilterBuilder.build());

                //  scanFilterList.add(new ScanFilter.Builder()
                //  过滤扫描蓝牙设备的主服务
                // .setServiceUuid(ParcelUuid.fromString("0000ffff-0000-1000-8000-00805f9bfffb"))
                // .build());
                mScanner.startScan(scanFilterList, mScannerSetting, mScanCallback);
            } else {
                mScanner.startScan(mScanCallback);
            }
            LogUtil.e("------开始扫描蓝牙------");
            isScan = true;
            mStartScan = System.currentTimeMillis();
        }
    }

    private void sendDevice(BluetoothDevice device) {
        EventMessage message = new EventMessage(111);
        Bundle bundle = new Bundle();
        bundle.putString("name", device.getName());
        bundle.putString("address", device.getAddress());
        message.setBundle(bundle);
        EventBus.getDefault().post(message);
    }

    /**
     * 是否可以被别人发现使用
     */
    private void canOtherUser() {
        if (mAdapter != null) {
            if (mAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                //不在可被搜索的范围
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);//设置本机蓝牙在300秒内可见
                mContext.startActivity(discoverableIntent);
            }
        }
    }

    /**
     * 搜索完设备后，要记得注销广播。注册后的广播对象在其他地方有强引用，如果不取消，activity会释放不了资源 。
     */
    public void destroy() {
        if (mContext != null && mBluetoothReceiver != null) {
            mContext.unregisterReceiver(mBluetoothReceiver);
        }

        if (mIntentBluetooth != null) {
            mIntentBluetooth = null;
        }
    }

}
