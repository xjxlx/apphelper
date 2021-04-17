package android.helper.ui.activity;

import android.Manifest;
import android.helper.R;
import android.helper.databinding.ActivityWriteXmlBinding;
import android.view.View;

import com.android.helper.base.BaseTitleActivity;
import com.android.helper.utils.FileUtil;
import com.android.helper.utils.RxPermissionsUtil;
import com.android.helper.utils.XmlUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WriteXmlTitleActivity extends BaseTitleActivity {

    private ActivityWriteXmlBinding binding;
    private File file1;

    @Override
    protected void initView() {
        super.initView();
        binding = ActivityWriteXmlBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initData() {
        super.initData();
        XmlUtil xmlUtil = new XmlUtil();
        List<Float> integers = new ArrayList<>();
        for (float i = 0; i < 500; ) {
            i += 0.5;
            integers.add(i);
        }

        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil fileUtil = new FileUtil();
                File rootFileForApp = fileUtil.getRootFileForSd();
                File file = new File(rootFileForApp, "write");
                if (!file.exists()) {
                    file.mkdirs();
                }
                file1 = new File(file, "dimens.txt");
                RxPermissionsUtil util = new RxPermissionsUtil(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                util.setSinglePermissionListener(havePermission -> {
                    if (havePermission) {
                        xmlUtil.writeDat(file1.getAbsolutePath(), "<dimen name=\"dp_", "\">", "dp</dimen>\n", integers);
                    }
                });
            }
        });
    }

    @Override
    protected int getTitleLayout() {
        return R.layout.activity_write_xml;
    }
}