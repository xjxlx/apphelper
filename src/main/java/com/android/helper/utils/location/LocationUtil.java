package com.android.helper.utils.location;

import android.Manifest;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.helper.interfaces.lifecycle.BaseLifecycleObserver;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.RxPermissionsUtil;
import com.android.helper.utils.ToastUtil;

/**
 * 定位的工具类
 */
public class LocationUtil implements BaseLifecycleObserver {

    private boolean isLoop; // 是否轮询
    private int interval;   // 间隔的时间

    //声明mLocationClient对象
    public AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private LocationUtil() {
    }

    public LocationUtil(Builder builder) {
        this.interval = builder.interval;
        this.isLoop = builder.isLoop;
    }

    /**
     * 逆地理编码（坐标转地址）
     *
     * @param context                 上下文
     * @param latitude                纬度
     * @param longitude               经度
     * @param searchType              传入坐标系的类型，参数表示是火系坐标系还是GPS原生坐标系，，固定只能选择这两种类型
     *                                {@link GeocodeSearch#AMAP }代表传入的是火星（高德）坐标系
     *                                {@link GeocodeSearch#GPS}代表传入的是GPS原生坐标系
     * @param reGeocodeResultListener 解析出来的事件回调
     */
    public static void getAddressForLatitude(Context context, double latitude, double longitude, String searchType, ReGeocodeResultListener reGeocodeResultListener) {
        try {
            // 设置地理编码（正向和逆向）查询监听
            GeocodeSearch geocodeSearch = new GeocodeSearch(context);
            LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
            // 第一个参数表示一个LatLng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200f, searchType);
            // 设置监听
            geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
                    // 返回结果成功或者失败的响应码。1000为成功，其他为失败
                    if (rCode == 1000) {
                        if (reGeocodeResultListener != null) {
                            reGeocodeResultListener.onReGeocodeSearched(regeocodeResult);
                        }
                    } else {
                        LogUtil.e("逆地理编码获取地址失败：code:" + rCode);
                    }
                    //  ReGeocodeAddress reGeocodeAddress = reGeocodeResult.getReGeocodeAddress();
                    //  String formatAddress = reGeocodeAddress.getFormatAddress();
                    //  LogUtil.e("查询经纬度对应详细地址:" + formatAddress);

                    // ReGeocodeAddress reGeocodeAddress = reGeocodeResult.getReGeocodeAddress();
                    // getAdCode() 返回逆地理编码结果所在区（县）的编码。
                    // getAois() 返回AOI（面状数据）的数据，如POI名称、区域编码、中心点坐标、POI类型等。
                    // getBuilding() 逆地理编码返回的建筑物名称。
                    // getBusinessAreas() 返回商圈对象列表，若服务没有相应数据，则返回列表长度为0。
                    // getCity() 逆地理编码返回的所在城市名称。
                    // getCityCode() 返回逆地理编码结果所在城市编码。
                    // getCountry() 获取国家名称。
                    // getCountryCode() 海外生效 国家简码
                    // getCrossroads() 逆地理编码返回的交叉路口列表。
                    // getDistrict() 逆地理编码返回的所在区（县）名称。
                    // getFormatAddress() 逆地理编码返回的格式化地址。
                    // getNeighborhood() 逆地理编码返回的社区名称。
                    // getPois() 逆地理编码返回的POI(兴趣点)列表。
                    // getProvince() 逆地理编码返回的所在省名称、直辖市的名称 。
                    // getRoads() 逆地理编码返回的道路列表。
                    // getStreetNumber() 逆地理编码返回的门牌信息。
                    // getTowncode() 返回乡镇街道编码。
                    // getTownship() 逆地理编码返回的乡镇名称。

                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                }

            });
            //异步查询
            geocodeSearch.getFromLocationAsyn(query);
        } catch (Exception ignored) {
        }
    }

    /**
     * @param context        上下文
     * @param address        指定的地址
     * @param city           city - 可选值：cityname（中文或中文全拼）、citycode、adcode。如传入null或空字符串则为“全国”，
     * @param resultListener 返回数据的监听
     */
    public static void getLocationForAddress(Context context, String address, String city, GeocodeResultListener resultListener) {
        try {
            GeocodeSearch geocoderSearch = new GeocodeSearch(context);
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
                    // 定位信息查询
                    if (rCode == 1000) {
                        //   List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList(); 返回地理编码搜索的地理结果。
                        //   getAdcode()  地理编码返回的区域编码。
                        //   getBuilding() 地理编码返回的建筑物名称。
                        //   getCity() 地理编码返回的所在城市名称。
                        //   getCountry() 海外生效 国家名称
                        //   getDistrict() 地理编码返回的所在区（县）名称。
                        //   getFormatAddress() 地理编码返回的格式化地址。
                        //   getLatLonPoint() 地理编码返回的经纬度坐标。
                        //   getLevel() 地理编码返回的匹配级别。
                        //   getNeighborhood() 地理编码返回的社区名称。
                        //   getPostcode() 海外生效 邮政编码
                        //   getProvince() 地理编码返回的所在省名称、直辖市的名称 。
                        //   getTownship() 地理编码返回的乡镇名称。
                        //   setCountry(java.lang.String country) 海外生效 国家名称
                        //   setPostcode(java.lang.String postcode) 海外生效 邮政编码

                        if (resultListener != null) {
                            resultListener.onGeocodeSearched(geocodeResult);
                        }
                    } else {
                        LogUtil.e("地址转换经纬度，查询失败：code:" + rCode);
                    }
                }
            });

            // name表示地址，第二个参数表示查询城市，city - 可选值：cityname（中文或中文全拼）、citycode、adcode。如传入null或空字符串则为“全国”，
            GeocodeQuery query = new GeocodeQuery(address, city);
            geocoderSearch.getFromLocationNameAsyn(query);
        } catch (Exception ignored) {
        }
    }

    /**
     * 接口可以用来判断指定位置是否在大陆以及港、澳地区。
     *
     * @return 国内和国外处理是不一样，国内需要转换，国外GPS坐标可以直接使用的，如果要转换坐标，首先要判断是否是在国内，这里是判断方法
     */
    public boolean isChina(Context context, double latitude, double longitude) {
        CoordinateConverter converter = new CoordinateConverter(context);
        //返回true代表当前位置在大陆、港澳地区，反之不在。
        //第一个参数为纬度，第二个为经度，纬度和经度均为高德坐标系。
        return CoordinateConverter.isAMapDataAvailable(latitude, longitude);
    }

    /**
     * @return 转换国内GPS原生的坐标
     */
    public DPoint convert(Context context, double latitude, double longitude) {
        // 构造数据源
        DPoint dPoint = new DPoint(latitude, longitude);

        try {
            if ((context != null) && (latitude != 0) && (longitude != 0)) {
                if (isChina(context, latitude, longitude)) { // 国内的转换一下坐标
                    CoordinateConverter converter = new CoordinateConverter(context);
                    // CoordType.GPS 待转换坐标类型
                    converter.from(CoordinateConverter.CoordType.GPS);
                    // sourceLatLng待转换坐标点 DPoint类型
                    converter.coord(dPoint);
                    // 执行转换操作
                    return converter.convert();
                } else { // 国外的直接返回数据
                    return dPoint;
                }
            }
        } catch (Exception e) {
            LogUtil.e("坐标系转换失败：" + e.getMessage());
        }
        return dPoint;
    }

    /**
     * 计算两个地方之间的距离，这个是直线距离
     *
     * @param startLat 开始的位置
     * @param endLat   结束的位置
     */
    public float CalculationDistance(Context context, DPoint startLat, DPoint endLat) {
        return CoordinateConverter.calculateLineDistance(startLat, endLat);
    }

    /**
     * 获取当前定位的信息，里面包含了经纬度，省市区、街道、门牌号、地区编码等功能的信息
     */
    public void getCurrentLocalInfo(FragmentActivity activity, LocationListener listener) {
        if (activity != null) {
            Lifecycle lifecycle = activity.getLifecycle();
            lifecycle.addObserver(this);

            RxPermissionsUtil permissionsUtil = new RxPermissionsUtil(activity, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsUtil.setAllPermissionListener((havePermission, permission) -> {
                if (havePermission) {
                    getLocalInfo(activity, listener);
                } else {
                    ToastUtil.show("请给予定位权限，否则无法使用定位功能");
                }
            });
        }
    }

    /**
     * 获取当前定位的信息，里面包含了经纬度，省市区、街道、门牌号、地区编码等功能的信息
     * 如果要使用这个方法的话，普通的fragment的话，不用去管他，如果是show or hine  或者是viewPager 的fragment的话，
     * 需要手动调用{{@link #isVisibility(boolean)}}方法告诉管理类，当前的fragment是处于什么状态，可见或者不可见，去做一些逻辑性的处理。
     */
    public void getCurrentLocalInfo(Fragment fragment, LocationListener listener) {
        if (fragment != null) {
            Lifecycle lifecycle = fragment.getLifecycle();
            lifecycle.addObserver(this);

            FragmentActivity activity = fragment.getActivity();
            if (activity != null) {
                RxPermissionsUtil permissionsUtil = new RxPermissionsUtil(activity, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
                permissionsUtil.setAllPermissionListener((havePermission, permission) -> {
                    if (havePermission) {
                        getLocalInfo(activity, listener);
                    } else {
                        ToastUtil.show("请给予定位权限，否则无法使用定位功能");
                    }
                });
            }
        }
    }

    /**
     * @param isVisibility 当前的view是否可见
     */
    public void isVisibility(boolean isVisibility) {
        if (isVisibility) {
            onResume();
        } else {
            onPause();
        }
    }

    /**
     * @param context  上下文
     * @param listener 数据返回的监听
     */
    private void getLocalInfo(Context context, LocationListener listener) {
        try {
            if (mLocationClient == null) {
                mLocationClient = new AMapLocationClient(context);
            }
            //初始化定位参数
            if (mLocationOption == null) {
                mLocationOption = new AMapLocationClientOption();
            }
            //设置返回地址信息，默认为true
            mLocationOption.setNeedAddress(true);
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            if (isLoop) {
                //设置定位间隔,单位毫秒,默认为2000ms
                if (interval == 0) {
                    mLocationOption.setInterval(2000);
                } else {
                    mLocationOption.setInterval(interval);
                }
            }
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //设置定位监听
            mLocationClient.setLocationListener(amapLocation -> {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        // amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        // amapLocation.getLatitude();//获取纬度
                        // amapLocation.getLongitude();//获取经度
                        // amapLocation.getAccuracy();//获取精度信息
                        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        // Date date = new Date(amapLocation.getTime());
                        // df.format(date);//定位时间

                        // amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        // amapLocation.getCountry();//国家信息
                        // amapLocation.getProvince();//省信息
                        // amapLocation.getCity();//城市信息
                        // amapLocation.getDistrict();//城区信息
                        // amapLocation.getStreet();//街道信息
                        // amapLocation.getStreetNum();//街道门牌号信息
                        // amapLocation.getCityCode();//城市编码
                        // amapLocation.getAdCode();//地区编码
                        // amapLocation.getAoiName();//获取当前定位点的AOI信息

                        if (listener != null) {
                            listener.onLocationChanged(amapLocation);
                        }

                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        LogUtil.e("获取定位信息错误：AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            });

            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //启动定位
            mLocationClient.startLocation();
        } catch (Exception ignored) {
        }
    }

    /**
     * 采用builder的设计模式去处理
     */
    public static class Builder {
        private boolean isLoop; // 是否轮询
        private int interval;   // 间隔的时间

        /**
         * @param loop 开启轮询
         * @return 是否轮询获取当前的定位信息
         */
        public Builder setLoop(boolean loop) {
            isLoop = loop;
            return this;
        }

        /**
         * @param interval 具体的时间间隔
         * @return 轮询的时间间隔，最小间隔支持为1000ms
         */
        public Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        public LocationUtil Build() {
            return new LocationUtil(this);
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        LogUtil.e("LocationUtil---onStart");
    }

    @Override
    public void onResume() {
        LogUtil.e("LocationUtil---onResume");
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void onPause() {
        LogUtil.e("LocationUtil---onPause");
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    @Override
    public void onStop() {
        LogUtil.e("LocationUtil---onStop");
        onPause();
    }

    @Override
    public void onDestroy() {
        LogUtil.e("LocationUtil---onDestroy");
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }
}
