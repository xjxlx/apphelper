package android.helper.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.helper.R;

import android.helper.adapters.UploadAdapter;
import android.helper.bean.DownLoadBean;
import android.helper.base.BaseTitleActivity;

import java.util.ArrayList;

public class UploadingActivity extends BaseTitleActivity {
    
    private RecyclerView rv_upload_list;
    
    @Override
    protected int getTitleLayout() {
        return R.layout.activity_uploading;
    }
    
    @Override
    protected void initView() {
        super.initView();
        setTitleContent("带进度条的文件上传");
        
        rv_upload_list = findViewById(R.id.rv_upload_list);
        
        ArrayList<DownLoadBean> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DownLoadBean downLoadBean = new DownLoadBean();
            arrayList.add(downLoadBean);
        }
        UploadAdapter adapter = new UploadAdapter(mContext, arrayList);
        
        rv_upload_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rv_upload_list.setAdapter(adapter);
    }
    
}
