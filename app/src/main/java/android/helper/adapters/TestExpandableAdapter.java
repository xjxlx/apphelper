package android.helper.adapters;

import android.app.Activity;
import android.helper.R;
import android.helper.bean.ExpandableBean;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.helper.base.BaseExpandableAdapter;
import com.android.helper.utils.LogUtil;
import com.android.helper.utils.TextViewUtil;

import java.util.List;

public class TestExpandableAdapter extends BaseExpandableAdapter<ExpandableBean.Data.Content> {

    public TestExpandableAdapter(Activity activity, List<ExpandableBean.Data.Content> list) {
        super(activity, list);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mList != null) {
            ExpandableBean.Data.Content content = mList.get(groupPosition);
            if (content != null) {
                List<ExpandableBean.Data.Content.ContentBean> contentBean = content.getContent();
                if (contentBean != null && contentBean.size() > 0) {
                    return contentBean.size();
                }
            }
        }
        return 0;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (mList != null) {
            ExpandableBean.Data.Content content = mList.get(groupPosition);
            if (content != null) {
                List<ExpandableBean.Data.Content.ContentBean> contentBean = content.getContent();
                if (contentBean != null && contentBean.size() > 0) {
                    return contentBean.get(childPosition);
                }
            }
        }
        return null;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        PVH pvh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expandable_parent, null, false);
            pvh = new PVH();

            pvh.tvP = convertView.findViewById(R.id.tv_p);

            convertView.setTag(pvh);
        } else {
            pvh = (PVH) convertView.getTag();
        }

        String unit_name = mList.get(groupPosition).getUnit_name();
        TextViewUtil.setText(pvh.tvP, unit_name);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LogUtil.e("getChildView  ---> groupPosition:" + groupPosition + "  ---> childPosition:" + childPosition + "  isLastChild:" + isLastChild);
        CVH cvh;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expandable_child, null, false);

            cvh = new CVH();
            cvh.tvC = convertView.findViewById(R.id.tv_c);
            convertView.setTag(cvh);
        } else {
            cvh = (CVH) convertView.getTag();
        }
        String content_name = mList.get(groupPosition).getContent().get(childPosition).getContent_name();
        TextViewUtil.setText(cvh.tvC, content_name);

        return convertView;
    }

    static class PVH {
        TextView tvP;
    }

    static class CVH {
        TextView tvC;
    }

}
