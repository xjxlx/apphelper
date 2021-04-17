package android.helper.adapters;

import android.content.Context;
import android.helper.R;
import android.helper.bean.AppInfoBean;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.helper.base.BaseVH;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.SpUtil;

import java.util.List;

public class AppInfoAdapter extends RecyclerView.Adapter<BaseVH> {

    private Context mContext;
    private List<AppInfoBean> mListSystem;
    private List<AppInfoBean> mListUser;

    public AppInfoAdapter(Context context, List<AppInfoBean> mListSystem, List<AppInfoBean> mListUser) {
        mContext = context;
        this.mListSystem = mListSystem;
        this.mListUser = mListUser;

        LogUtil.e("appInfo:--->System:" + mListSystem.size());
        LogUtil.e("appInfo:--->User:" + mListUser.size());
        for (int i = 0; i < mListSystem.size(); i++) {
            LogUtil.e("appInfo:--->系统应用:" + mListSystem.get(i).getAppName());
        }
        for (int i = 0; i < mListUser.size(); i++) {
            LogUtil.e("appInfo:--->第三方应用:" + mListUser.get(i).getAppName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else if (position > 0 && position < mListSystem.size() + 1) {
            return 2;
        } else if (position == mListSystem.size() + 1) {
            return 3;
        } else if (position > mListSystem.size() + 1) {
            return 4;
        } else return 0;
    }

    @NonNull
    @Override
    public BaseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1 || viewType == 3) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_appinfo_f, parent, false);
            VHTitle vhf = new VHTitle(inflate);
            return vhf;
        } else {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_appinfo_s, parent, false);
            VHItem vhs = new VHItem(inflate);
            return vhs;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseVH holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 1) {
            if (holder instanceof AppInfoAdapter.VHTitle) {
                VHTitle holder1 = (VHTitle) holder;
                holder1.tv_type.setText("系统应用");
            }
        } else if (itemViewType == 2) {
            if (holder instanceof AppInfoAdapter.VHItem) {
                VHItem holder1 = (VHItem) holder;
                AppInfoBean bean = mListSystem.get(position - 1);
                holder1.iv_app_info.setImageDrawable(bean.getAppIcon());
                holder1.tv_app_name.setText(bean.getAppName());
                boolean aBoolean = SpUtil.getBoolean(bean.getPackageName());
                holder1.s_switch.setChecked(aBoolean);

                holder1.s_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        LogUtil.e("当前操作的应用是：" + bean.getAppName() + "  选中的状态为：" + isChecked);
                        SpUtil.putBoolean(bean.getPackageName(), isChecked);
                    }
                });
            }
        } else if (itemViewType == 3) {
            if (holder instanceof AppInfoAdapter.VHTitle) {
                VHTitle holder1 = (VHTitle) holder;
                holder1.tv_type.setText("第三方应用");
            }
        } else if (itemViewType == 4) {
            if (holder instanceof AppInfoAdapter.VHItem) {
                VHItem holder1 = (VHItem) holder;
                AppInfoBean bean = mListUser.get(position - (mListSystem.size() + 2));
                holder1.iv_app_info.setImageDrawable(bean.getAppIcon());
                holder1.tv_app_name.setText(bean.getAppName());

                boolean aBoolean = SpUtil.getBoolean(bean.getPackageName());
                holder1.s_switch.setChecked(aBoolean);

                holder1.s_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        LogUtil.e("当前操作的应用是：" + bean.getAppName() + "  选中的状态为：" + isChecked);
                        SpUtil.putBoolean(bean.getPackageName(), isChecked);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListSystem.size() + mListUser.size() + 2;
    }

    static class VHTitle extends BaseVH {
        private final TextView tv_type;

        public VHTitle(@NonNull View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
        }
    }

    static class VHItem extends BaseVH {
        private final ImageView iv_app_info;
        private final TextView tv_app_name;
        private final Switch s_switch;

        public VHItem(@NonNull View itemView) {
            super(itemView);
            iv_app_info = itemView.findViewById(R.id.iv_app_info);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
            s_switch = itemView.findViewById(R.id.s_switch);
        }
    }

}
