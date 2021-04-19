package android.helper.test;

import android.graphics.Bitmap;
import android.helper.R;
import android.widget.ImageView;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.interfaces.listener.CallBackListener;
import com.android.helper.utils.BitmapUtil;
import com.android.helper.utils.LogUtil;

public class MoreAdapterActivity extends BaseTitleActivity {

    private androidx.recyclerview.widget.RecyclerView mRvList;
    private ImageView iv_image;

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_more_adapter;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitleContent("测试多布局的类型");

        mRvList = findViewById(R.id.rv_list);
        iv_image = findViewById(R.id.iv_image);
    }

    @Override
    protected void initData() {
        super.initData();
        String url = "http://file.jollyeng.com/anims/201903/1552874954.jpg";

        BitmapUtil.getBitmapForService(mContext, "", new CallBackListener<Bitmap>() {
            @Override
            public void onBack(boolean successful, Object tag, Bitmap bitmap) {
                LogUtil.e("successful:" + successful + " --->tag:" + tag + "  --->bitmap:" + bitmap);

                if (successful) {
                    iv_image.setImageBitmap(bitmap);
                }
            }
        });
    }
}