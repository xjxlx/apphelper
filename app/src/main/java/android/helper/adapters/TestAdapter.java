package android.helper.adapters;

import android.app.Activity;
import android.helper.R;
import android.helper.bean.HomeBean;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.helper.base.BaseRecycleAdapter;
import com.android.helper.base.BaseVH;
import com.android.helper.utils.TextViewUtil;
import com.android.helper.utils.ToastUtil;
import com.android.helper.utils.photo.GlideUtil;

import java.util.List;

public class TestAdapter extends BaseRecycleAdapter<HomeBean.ReturnDataList.Data, TestAdapter.VHHome> {

    public TestAdapter(Activity mContext) {
        super(mContext);
    }

    public TestAdapter(Activity mContext, List<HomeBean.ReturnDataList.Data> mList) {
        super(mContext, mList);
    }

    @Override
    protected int getLayout() {
        return R.layout.item_test;
    }

    @Override
    protected VHHome createViewHolder(View inflate) {
        return new VHHome(inflate);
    }

    @Override
    protected void onBindHolder(@NonNull VHHome holder, int position) {
        HomeBean.ReturnDataList.Data data = mList.get(position);
        if (data == null) {
            return;
        }
        // 封面
        GlideUtil.loadView(mContext, data.getImg(), holder.iv_activity);

        //  显示状态：-1不现实0已结束1进行中
        int showStatus = data.getShowStatus();
        switch (showStatus) {
            case -1:
                holder.tv_status.setVisibility(View.GONE);
                break;

            case 0:
                holder.tv_status.setVisibility(View.VISIBLE);
                holder.tv_status.setText("已结束");
                holder.tv_status.setBackgroundResource(R.drawable.bg_radius_left_50dp_gray);
                break;

            case 1:
                holder.tv_status.setVisibility(View.VISIBLE);
                holder.tv_status.setText("进行中");
                holder.tv_status.setBackgroundResource(R.drawable.bg_radius_left_50dp_gold);
                break;
        }
        // 活动的名字
        TextViewUtil.setText(holder.tv_activity_title, data.getName());

        holder.iv_share.setOnClickListener(view -> {
            ToastUtil.show("分享");
        });
    }

    static class VHHome extends BaseVH {
        private final ImageView iv_activity;
        private final TextView tv_status;
        private final TextView tv_activity_title;
        private final View iv_share;

        public VHHome(@NonNull View itemView) {
            super(itemView);
            iv_activity = itemView.findViewById(R.id.iv_activity);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_activity_title = itemView.findViewById(R.id.tv_activity_title);
            iv_share = itemView.findViewById(R.id.iv_share);
        }
    }
}
