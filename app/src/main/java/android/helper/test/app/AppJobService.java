package android.helper.test.app;

import static android.helper.test.app.AppLifecycleActivity.FILE_NAME;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.helper.utils.LogUtil;

/**
 * 轮询的后台服务进程
 */
public class AppJobService extends JobService {
    public static int AppJobId = 100;

    public AppJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        LogUtil.e("------>:onStartJob");
        LogUtil.e("------>:jobParameters:" + jobParameters.toString());
        LogUtil.writeDe(FILE_NAME, "我是JobService的服务！");
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, jobParameters));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        LogUtil.e("------>:onStopJob");
        mJobHandler.removeMessages(1);

        return true;
    }

    // 创建一个handler来处理对应的job
    private Handler mJobHandler = new Handler(new Handler.Callback() {
        // 在Handler中，需要实现handleMessage(Message msg)方法来处理任务逻辑。
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "JobService task running", Toast.LENGTH_SHORT).show();
            // 调用jobFinished
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

}