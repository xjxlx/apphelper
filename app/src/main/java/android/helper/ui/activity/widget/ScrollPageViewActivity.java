package android.helper.ui.activity.widget;

import android.helper.R;
import android.view.View;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.utils.ToastUtil;
import com.android.helper.widget.PageView;

import java.util.ArrayList;

public class ScrollPageViewActivity extends BaseTitleActivity {

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_scroll_page_view;
    }

    @Override
    protected void initData() {
        super.initData();

        ArrayList<String> list = new ArrayList<>();

        setTitleContent("滑动的pageView");

        int[] resources = new int[]{
                R.drawable.c62_control_air_control_normal,
                R.drawable.c62_control_automatic_parking_normal,
                R.drawable.c62_control_back_window_normal,
                R.drawable.c62_control_car_lock_normal,
                R.drawable.c62_control_car_window_normal,
                R.drawable.c62_control_charge_un_selector,
                R.drawable.c62_control_engine_normal,
                R.drawable.c62_control_find_car_normal,
                R.drawable.c62_control_one_cold_normal,
                R.drawable.c62_control_one_hot_normal,
                R.drawable.c62_control_seat_cold_normal,
                R.drawable.c62_control_seat_hot_normal,
                R.drawable.c62_control_sky_light_normal,
                R.drawable.c62_control_truck_normal
        };

        for (int resource : resources) {
            list.add(String.valueOf(resource));
        }

        PageView pv_group = findViewById(R.id.pv_group);
        pv_group.setLayout(R.layout.item_page_view);
        pv_group.setOnItemClickListener(new PageView.ItemClickListener<String>() {
            @Override
            public void onItemClickListener(int position, View view, String s) {
                ToastUtil.show(view.getTag().toString());
            }
        });
        pv_group.setDataList(list);
    }
}
