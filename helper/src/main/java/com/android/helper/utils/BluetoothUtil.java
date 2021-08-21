package com.android.helper.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Parcelable;

import com.android.helper.common.EventMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 蓝牙的工具类
 */
public class BluetoothUtil {
    /**
     * 写入日志的文件名字
     */
    public static final String FILE_NAME = "AppLifecycle";

    private BluetoothManager mBluetoothManager;
    private Context mContext;
    private static BluetoothUtil bluetoothUtil;
    private BluetoothAdapter mAdapter;
    private BluetoothLeScanner mScanner;
    private Intent mIntentBluetooth;
    private boolean isScan;// 是否正在扫描中
    private LocationManager locationManager;

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
                MyBluetooth bluetooth = new MyBluetooth();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

                intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
                intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");

                // 定位信息
                intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
                mIntentBluetooth = mContext.registerReceiver(bluetooth, intentFilter);
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
                mScanner = mAdapter.getBluetoothLeScanner();
                if (mScanner != null) {
                    if (!isScan) {
                        mScanner.startScan(mScanCallback);
                        LogUtil.e("开始去扫描蓝牙！");
                        isScan = true;
                    } else {
                        LogUtil.e("正在扫描中，请等待~");
                    }
                }
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
            isScan = false;
            LogUtil.writeDe(FILE_NAME, "蓝牙扫描回调：onScanResult！");
            LogUtil.e("蓝牙扫描回调：onScanResult！");
            super.onScanResult(callbackType, result);
            if (result != null) {
                BluetoothDevice device = result.getDevice();
                if (device != null) {
                    String address = device.getAddress();
                    String name = device.getName();

                    EventBus.getDefault().post(new EventMessage(111, name + "/" + address));

                    LogUtil.writeDe(FILE_NAME, "当前扫描到的蓝牙名字：" + name + "  描到的蓝牙地址为：" + address);
                    LogUtil.e("当前扫描到的蓝牙名字：" + name + "  描到的蓝牙地址为：" + address);
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

            LogUtil.writeDe(FILE_NAME, "蓝牙扫描回调：onBatchScanResults！");
            LogUtil.e("蓝牙扫描回调：onBatchScanResults！");
        }

        //当扫描不能开启时回调
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            isScan = false;

            LogUtil.writeDe(FILE_NAME, "蓝牙扫描异常的回调：onScanFailed！" + errorCode);
            LogUtil.e("蓝牙扫描异常的回调：onScanFailed！" + errorCode);

            if (mScanner != null) {
                if (isOpenBluetooth()) {
                    mScanner.stopScan(mScanCallback);
                }
            }
        }
    };

    class MyBluetooth extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if ((intent != null) && (intent.getAction() != null)) {
                String action = intent.getAction();
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
                            LogUtil.e(" --> 扫描到的蓝牙名字：" + name + "  扫描到的蓝牙地址：" + address);

                            EventBus.getDefault().post(new EventMessage(111, name + "/" + address));
                        }
                    }
                    break;
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        LogUtil.e("---> 蓝牙扫描中！");
                        break;

                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        LogUtil.e("---> 扫描完成，点击列表中的设备来尝试连接！");
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

}
