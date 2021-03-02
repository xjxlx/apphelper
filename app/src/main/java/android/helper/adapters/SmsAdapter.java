package android.helper.adapters;

import android.helper.R;
import android.helper.base.BaseActivity;
import android.helper.base.BaseRecycleAdapter;
import android.helper.base.BaseVH;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class SmsAdapter extends BaseRecycleAdapter<String, SmsAdapter.SmsVH> {
    private int type;

    public SmsAdapter(BaseActivity mContext, int type) {
        super(mContext);
        this.type = type;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_sms;
    }

    @Override
    protected SmsVH createViewHolder(View inflate) {
        return new SmsVH(inflate);
    }

    @Override
    protected void onBindHolder(@NonNull SmsVH holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull SmsVH holder, int position) {
        if (type == 1) {
            holder.tv_content.setText("地址：" + mList.get(position));
        } else {
            holder.tv_content.setText("结果：" + mList.get(position));
        }
    }

    static class SmsVH extends BaseVH {
        private final TextView tv_content;

        public SmsVH(@NonNull View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
        }
    }
}
