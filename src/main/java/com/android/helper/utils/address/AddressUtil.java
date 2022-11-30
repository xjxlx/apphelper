package com.android.helper.utils.address;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.android.helper.utils.JsonUtil;
import com.android.helper.utils.LogUtil;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : 流星
 * @CreateDate: 2022/11/30-01:56
 * @Description:
 */
public class AddressUtil {

    private List<JsonBean> options1Items = new ArrayList<>();
    private final List<List<String>> options2Items = new ArrayList<>();
    private final List<List<List<String>>> options3Items = new ArrayList<>();
    private OnSelectorListener mListener;
    private CreateBuilderCreatedListener mCreatedListener;

    public interface OnSelectorListener {
        void onSelector(AddressUtil  addressUtil,String option1,String option2,String option3,int index1,int index2,int index3);
    }

    public interface CreateBuilderCreatedListener{
        void onBuilderCreated(AddressUtil  addressUtil,OptionsPickerBuilder pickerBuilder);
    }

    public void setCreateBuilderCreatedListener(CreateBuilderCreatedListener createdListener){
        mCreatedListener=createdListener;
    }

    public void setOnSelectorListener(OnSelectorListener listener){
        mListener =listener;
    }

   private OptionsPickerBuilder pickerBuilder;

    public void parseAddress(Context context,String fileName){
        Observable
                .create((ObservableOnSubscribe<List<JsonBean>>) emitter -> {
                    try{
                        String JsonData =JsonUtil.getJsonForAssets(context, fileName);//获取assets目录下的json文件数据

                        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

                        /*
                         * 添加省份数据
                         *
                         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
                         * PickerView会通过getPickerViewText方法获取字符串显示出来。
                         */
                        options1Items = jsonBean;

                        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
                            List<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
                            List<List<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

                            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                                cityList.add(cityName);//添加城市
                                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                                province_AreaList.add(city_AreaList);//添加该省所有地区数据
                            }

                            /**
                             * 添加城市数据
                             */
                            options2Items.add(cityList);

                            /**
                             * 添加地区数据
                             */
                            options3Items.add(province_AreaList);
                        }

                        // 发送数据到下游
                        emitter.onNext(options1Items);
                    }catch (Exception e){
                        LogUtil.e("解析数据失败 ："+e.getMessage());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<JsonBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<JsonBean> jsonBeans) {

                        pickerBuilder = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
                            @Override
                            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                                //返回的分别是三个级别的选中位置
                                String opt1tx = options1Items.size() > 0 ? options1Items
                                        .get(options1)
                                        .getPickerViewText() : "";

                                String opt2tx = options2Items.size() > 0 && options2Items
                                                                                    .get(options1)
                                                                                    .size() > 0 ? options2Items
                                        .get(options1)
                                        .get(options2) : "";

                                String opt3tx = options2Items.size() > 0 && options3Items
                                                                                    .get(options1)
                                                                                    .size() > 0 && options3Items
                                                                                                           .get(options1)
                                                                                                           .get(options2)
                                                                                                           .size() > 0 ? options3Items
                                        .get(options1)
                                        .get(options2)
                                        .get(options3) : "";

                                if (mListener != null) {
                                    mListener.onSelector(AddressUtil.this, opt1tx, opt2tx, opt3tx,options1,options2,options3);
                                }
                            }
                        })
                                .setTitleText("城市选择")
                                .setDividerColor(Color.BLACK)
                                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                                .setContentTextSize(20);

                        if (mCreatedListener!=null){
                            mCreatedListener.onBuilderCreated(AddressUtil.this,pickerBuilder);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                         LogUtil.e("解析数据出错："+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public void show() {// 弹出选择器
        if (pickerBuilder!=null){
            OptionsPickerView build = pickerBuilder.build();
            build.setPicker(options1Items, options2Items, options3Items);//三级选择器
            build.show();
        }
    }

}
