package android.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.helper.ui.fragment.HomeFragment;
import android.helper.ui.fragment.PersonalFragment;
import android.helper.ui.fragment.TodoFragment;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.helper.base.BaseFragmentPagerAdapter;
import com.android.helper.base.BaseTitleActivity;
import com.android.helper.interfaces.listener.AllPermissionsListener;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.RxPermissionsUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tbruyelle.rxpermissions.Permission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseTitleActivity {

    private final List<Fragment> mListFragments = new ArrayList<>();
    private final List<String> mListTitle = new ArrayList<>();
    private ViewPager vpContent;
    private BottomNavigationView navigation;

    @Override
    protected void initView() {
        super.initView();
        LogUtil.e("当前的页面：Activity：--->  MainActivity");
        vpContent = findViewById(R.id.vp_content);
        navigation = findViewById(R.id.navigation);
    }

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_main;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void initData() {
        super.initData();

        String[] strings = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE
        };

        RxPermissionsUtil permissionsUtil = new RxPermissionsUtil(mContext, strings);
        permissionsUtil.setAllPermissionListener(new AllPermissionsListener() {
            @Override
            public void onRxPermissions(boolean havePermission, Permission permission) {
                LogUtil.e("是否拥有读写权限：" + havePermission);
            }
        });

        mListFragments.add(new HomeFragment());
        mListFragments.add(new TodoFragment());
        mListFragments.add(new PersonalFragment());

        mListTitle.add("首页");
        mListTitle.add("待办");
        mListTitle.add("个人中心");

        BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), mListFragments, mListTitle);

        vpContent.setAdapter(pagerAdapter);
        // 避免重复创建加载数据
        vpContent.setOffscreenPageLimit(mListFragments.size());
        // viewPager选中的监听
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //设置默认选中item
                navigation.getMenu().getItem(position).setChecked(true);
                switch (position) {
                    case 0:
                        setTitleContent("首页");
                        break;
                    case 1:
                        setTitleContent("代办");
                        break;
                    case 2:
                        setTitleContent("个人中心");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // 底部导航器选中的监听事件
        navigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                // 首页
                case R.id.navigation_home:
                    vpContent.setCurrentItem(0);
                    break;

                // 待办
                case R.id.navigation_moment:
                    vpContent.setCurrentItem(1);
                    break;

                // 个人中心
                case R.id.navigation_personal:
                    vpContent.setCurrentItem(2);
                    break;
            }
            return false;
        });

        // 设置默认数据
        navigation.getMenu().getItem(1).setChecked(true);
        vpContent.setCurrentItem(1);
        setTitleContent("代办");
    }
}