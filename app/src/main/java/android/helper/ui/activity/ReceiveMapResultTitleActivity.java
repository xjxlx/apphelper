package android.helper.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import android.helper.R;
import android.helper.databinding.ActivityReceiveMapResultBinding;
import android.helper.base.BaseTitleActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReceiveMapResultTitleActivity extends BaseTitleActivity {
    
    private ActivityReceiveMapResultBinding binding;
    
    @Override
    protected int getTitleLayout() {
        return R.layout.activity_receive_map_result;
    }
    
    @Override
    protected void initView() {
        super.initView();
        binding = ActivityReceiveMapResultBinding.inflate(getLayoutInflater());
    }
    
    @Override
    protected void initData() {
        super.initData();
        
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            //获得Intent的MIME type
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                //我们这里处理所有的文本类型
                if (type.startsWith("text/")) {
                    //处理获取到的文本，这里我们用TextView显示
                    handleSendText(intent);
                }
                //图片的MIME type有 image/png , image/jepg, image/gif 等，
                else if (type.startsWith("image/")) {
                    //处理获取到图片，我们用ImageView显示
                    handleSendImage(intent);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    //处理多张图片，我们用一个GridView来显示
                    handleSendMultipleImages(intent);
                }
            }
        }
    }
    
    /**
     * 用TextView显示文本
     * 可以打开一般的文本文件
     *
     * @param intent
     */
    private void handleSendText(Intent intent) {
        TextView textView = new TextView(this);
        
        //一般的文本处理，我们直接显示字符串
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            textView.setText(sharedText);
        }
        
        //文本文件处理，从Uri中获取输入流，然后将输入流转换成字符串
        Uri textUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (textUri != null) {
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(textUri);
                textView.setText(inputStream2Byte(inputStream));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //设置给Activity
        setContentView(textView);
    }
    
    /**
     * 将输入流转换成字符串
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String inputStream2Byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        int len = -1;
        
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        
        bos.close();
        
        //指定编码格式为UIT-8
        return new String(bos.toByteArray(), "UTF-8");
    }
    
    /**
     * 用ImageView显示单张图片
     *
     * @param intent
     */
    private void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            ImageView imageView = new ImageView(this);
            imageView.setImageURI(imageUri);
            setContentView(imageView);
        }
    }
    
    /**
     * 用GridView显示多张图片
     *
     * @param intent
     */
    private void handleSendMultipleImages(Intent intent) {
        final ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            GridView gridView = new GridView(this);
            //设置item的宽度
            gridView.setColumnWidth(130);
            //设置列为自动适应
            gridView.setNumColumns(GridView.AUTO_FIT);
            gridView.setAdapter(new GridAdapter(this, imageUris));
            setContentView(gridView);
            
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    
                    //点击GridView的item 可以分享图片给其他应用
                    //这里可以参考http://blog.csdn.net/xiaanming/article/details/9395991
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(position));
                    intent.setType("image/*");
                    startActivity(Intent.createChooser(intent, "共享图片"));
                }
            });
            
        }
    }
    
    /**
     * 重写BaseAdapter
     *
     * @author xiaanming
     */
    public class GridAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Uri> list;
        
        public GridAdapter(Context mContext, ArrayList<Uri> list) {
            this.list = list;
            this.mContext = mContext;
        }
        
        @Override
        public int getCount() {
            return list.size();
        }
        
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageURI(list.get(position));
            return imageView;
        }
    }
}