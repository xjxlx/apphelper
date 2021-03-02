package android.helper.adapters;

import android.app.Activity;
import android.helper.R;
import android.helper.base.BaseExpandableAdapter;
import android.helper.bean.ExpandableBean;
import android.helper.utils.TextViewUtil;
import android.helper.utils.photo.GlideUtil;
import android.helper.widget.MultipleListView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 高频词的分组adapter
 */
public class HighWordExpandableAdapter extends BaseExpandableAdapter<ExpandableBean.Data.Content> {

    private static int width;
    private FlexboxLayout.LayoutParams layoutParams;

    public HighWordExpandableAdapter(@NotNull Activity activity) {
        super(activity);
    }

    public HighWordExpandableAdapter(@NotNull Activity activity, @NotNull List<ExpandableBean.Data.Content> list) {
        super(activity, list);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mList != null && mList.size() > 0) {
            ExpandableBean.Data.Content contentBeanX = mList.get(groupPosition);
            if (contentBeanX != null) {
                List<ExpandableBean.Data.Content.ContentBean> content = contentBeanX.getContent();
                if (content != null) {
                    return content.size();
                }
            }
        }
        return 0;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ExpandableBean.Data.Content contentBeanX = mList.get(groupPosition);
        if (contentBeanX != null) {
            List<ExpandableBean.Data.Content.ContentBean> content = contentBeanX.getContent();
            if (content != null && content.size() > 0) {
                return content.get(childPosition);
            }
        }
        return null;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        VHP vhp;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expandable_gpc_parent, null);
            vhp = new VHP();
            vhp.tv_parent_title = convertView.findViewById(R.id.tv_parent_title);
            convertView.setTag(vhp);
        } else {
            vhp = (VHP) convertView.getTag();
        }

        ExpandableBean.Data.Content contentBeanX = mList.get(groupPosition);
        if (contentBeanX != null) {
            String unit_name = contentBeanX.getUnit_name();
            TextViewUtil.setText(vhp.tv_parent_title, unit_name);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        VHC vhc;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expandable_gpc_child, null);

            vhc = new VHC();
            vhc.tvTime = convertView.findViewById(R.id.tv_time);
            vhc.ivImage = convertView.findViewById(R.id.iv_image);
            vhc.tvContentType = convertView.findViewById(R.id.tv_content_type);
            vhc.rvContent = convertView.findViewById(R.id.rv_content);
            vhc.ivCollection = convertView.findViewById(R.id.iv_collection);
            vhc.tv_web_content = convertView.findViewById(R.id.tv_web_content);
            vhc.mlv_list = convertView.findViewById(R.id.mlv_list);
            convertView.setTag(vhc);
        } else {
            vhc = (VHC) convertView.getTag();
        }

        ExpandableBean.Data.Content contentBeanX = mList.get(groupPosition);
        if (contentBeanX != null) {
            List<ExpandableBean.Data.Content.ContentBean> content = contentBeanX.getContent();
            if (content != null && content.size() > 0) {

                ExpandableBean.Data.Content.ContentBean contentBean = content.get(childPosition);
                if (contentBean != null) {

                    String title = contentBean.getTitle();
                    // 标题
                    if (!TextUtils.isEmpty(title)) {
                        vhc.tvTime.setText(title);
                    }

                    String content_img = contentBean.getContent_img();
                    // LogUtil.e("content:Image:" + content_img);
                    // 图片
                    if (!TextUtils.isEmpty(content_img)) {
                        GlideUtil.loadView(mContext, content_img, vhc.ivImage);
                    }
                    // 类型
                    String content_type = contentBean.getContent_type();
                    if (!TextUtils.isEmpty(content_type)) {
                        if (vhc.tvContentType.getVisibility() != View.VISIBLE) {
                            vhc.tvContentType.setVisibility(View.VISIBLE);
                        }
                        vhc.tvContentType.setText(content_type);
                    } else {
                        vhc.tvContentType.setVisibility(View.GONE);
                    }

                    // 内容
                    String content1 = contentBean.getContent();

                    String content_type_id = contentBean.getContent_type_id();
                    if (TextUtils.equals(content_type_id, "12")) {
                        // web 的界面
                        vhc.rvContent.setVisibility(View.GONE);
                        vhc.mlv_list.setVisibility(View.GONE);

                        vhc.tv_web_content.setVisibility(View.VISIBLE);
                        vhc.tv_web_content.setText(content1);
                        TextViewUtil.setTextFont(mContext, vhc.tv_web_content, "FZY4K_GBK1_0.ttf");

                    } else if (TextUtils.equals(content_type_id, "11") || TextUtils.equals(content_type_id, "13")) {
                        // 6个图片的界面
                        vhc.rvContent.setVisibility(View.VISIBLE);
                        vhc.mlv_list.setVisibility(View.VISIBLE);
                        vhc.tv_web_content.setVisibility(View.GONE);

                        if (!TextUtils.isEmpty(content1)) {
                            String[] split = content1.split(",");
                            List<String> strings = Arrays.asList(split);

                            vhc.mlv_list.setList(strings);

                            //纵向线性布局
//                            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
//                            vhc.rvContent.setLayoutManager(layoutManager);
//                            GpcItemChildAdapter childAdapter = new GpcItemChildAdapter((BaseActivity) mContext, strings);
//                            vhc.rvContent.setAdapter(childAdapter);

//                            vhc.rvContent.setOnTouchListener((v, event) -> vhc.itemView.onTouchEvent(event));

                        } else {
                            vhc.mlv_list.setList(null);
                        }
                    }

                    // 是否已经学习过了课程
                    int flag = contentBean.getFlag();
                    vhc.ivCollection.setSelected(flag == 2);

                    // 点击事件
//                    if (mItemClickListener != null) {
//                        vhc.itemView.setOnClickListener(v -> mItemClickListener.onItemClick(vhc.itemView, i, contentBean));
//                    }

                    TextViewUtil.setTextFont(mContext, vhc.tvTime, "FZY4K_GBK1_0.ttf");

                }
            }
        }

        return convertView;
    }

    static class VHP {
        public TextView tv_parent_title;
    }

    static class VHC {
        private TextView tvTime;
        private ImageView ivImage;
        private RecyclerView rvContent;
        private TextView tvContentType;
        private TextView tv_web_content;
        private ImageView ivCollection;
        private MultipleListView mlv_list;
    }

    public void setList(List<ExpandableBean.Data.Content> list) {
        mList = list;
        notifyDataSetChanged();
    }
}
