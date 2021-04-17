package com.android.helper.utils.location;

/**
 * 定位的工具类
 */
public class LocationUtil {

    private LocationUtil() {
    }

    /**
     * @param context          上下文
     * @param latitude         纬度
     * @param longitude        经度
     * @param searchType       传入坐标系的类型，参数表示是火系坐标系还是GPS原生坐标系，，固定只能选择这两种类型，
     *                         GeocodeSearchType.GPS：代表传入的是GPS原生坐标系，
     *                         GeocodeSearchType.AMAP：代表传入的是火星（高德）坐标系
     * @param locationListener 解析出来的事件回调
     */
//    public static void getAddressForLatitude(Context context, double latitude, double longitude, @GeocodeSearchType String searchType, LocationListener locationListener) {
//        // 设置地理编码（正向和逆向）查询监听
//        GeocodeSearch geocodeSearch = new GeocodeSearch(context);
//        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
//            @Override
//            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
//                // 返回结果成功或者失败的响应码。1000为成功，其他为失败
//                if (rCode == 1000) {
//                    if (locationListener != null) {
//                        locationListener.onLocationSuccess(regeocodeResult);
//                    }
//                } else {
//                    LogUtil.e("获取位置信息失败");
//                }
//                // RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
//                // String formatAddress = regeocodeAddress.getFormatAddress();
//                //  LogUtil.e("查询经纬度对应详细地址:" + formatAddress);
//            }
//
//            @Override
//            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//
//            }
//        });
//
//        LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200f, searchType);
//        //异步查询
//        geocodeSearch.getFromLocationAsyn(query);
//    }
//
//    /**
//     * 是否是在国内
//     *
//     * @return 国内和国外处理是不一样，国内需要转换，国外GPS坐标可以直接使用的，如果要转换坐标，首先要判断是否是在国内，这里是判断方法
//     */
//    public static boolean isChina(Context context, double latitude, double longitude) {
//        CoordinateConverter converter = new CoordinateConverter(context);
//        //返回true代表当前位置在大陆、港澳地区，反之不在。
//        boolean isAMapDataAvailable = converter.isAMapDataAvailable(latitude, longitude);
//        //第一个参数为纬度，第二个为经度，纬度和经度均为高德坐标系。
//        return isAMapDataAvailable;
//    }
//
//    /**
//     * @return 转换国内GPS原生的坐标
//     */
//    public static LatLng conver(Context context, double latitude, double longitude) {
//        if (isChina(context, latitude, longitude)) {
//            // 转换坐标系的对象
//            CoordinateConverter converter = new CoordinateConverter(context);
//            // 设置源坐标类型
//            converter.from(CoordinateConverter.CoordType.GPS);
//            // 设置源坐标数据
//            converter.coord(new LatLng(latitude, longitude));
//            // 获取转换后的数据
//            LatLng desLatLng = converter.convert();
//
//            return desLatLng;
//        } else {
//            LatLng latLng = new LatLng(latitude, longitude);
//            return latLng;
//        }
//    }
//
//    @Retention(RetentionPolicy.SOURCE)
//    // 限定取值范围为{STATUS_OPEN, STATUS_CLOSE}
//    @StringDef({GeocodeSearchType.GPS, GeocodeSearchType.AMAP})
//    public @interface GeocodeSearchType {
//        String GPS = GeocodeSearch.GPS;
//        String AMAP = GeocodeSearch.AMAP;
//    }
//
//    public interface LocationListener {
//        void onLocationSuccess(RegeocodeResult regeocodeResult);
//    }

}
