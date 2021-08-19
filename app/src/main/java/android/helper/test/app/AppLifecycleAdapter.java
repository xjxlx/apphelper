package android.helper.test.app;

import android.app.Activity;
import android.helper.R;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.helper.base.BaseRecycleAdapter;
import com.android.helper.base.BaseVH;

public class AppLifecycleAdapter extends BaseRecycleAdapter<String, AppLifecycleAdapter.VH> {

    public AppLifecycleAdapter(Activity mContext) {
        super(mContext);
    }

    @Override
    protected int getLayout() {
        return R.layout.item_app_licycle;
    }

    @Override
    protected VH createViewHolder(View inflate) {
        return new VH(inflate);
    }

    @Override
    protected void onBindHolder(@NonNull VH holder, int position) {
        String s = mList.get(position);
        holder.mTvTest.setText(s);
    }

    static class VH extends BaseVH {
        private TextView mTvTest;

        public VH(@NonNull View itemView) {
            super(itemView);
            mTvTest = itemView.findViewById(R.id.tv_test);
        }
    }
}
