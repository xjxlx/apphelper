package android.helper.test.app;

import static android.helper.test.app.AppLifecycleActivity.FILE_NAME;
import static android.helper.test.app.AppLifecycleService.KEY_LIFECYCLE_JOB;
import static android.helper.test.app.AppLifecycleService.KEY_LIFECYCLE_TYPE;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.android.helper.utils.LogUtil;
import com.android.helper.utils.ServiceUtil;

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

        LogUtil.writeDe(FILE_NAME, "onStartJob ---> 我是JobService的服务，我在正常的运行着！");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            startJob(getBaseContext());
        }

        /*启动应用*/
        boolean serviceRunning = ServiceUtil.isServiceRunning(getBaseContext(), AppLifecycleService.class);
        if (!serviceRunning) {
            Intent intent = new Intent(getBaseContext(), AppLifecycleService.class);
            intent.putExtra(KEY_LIFECYCLE_TYPE, KEY_LIFECYCLE_JOB);
            ServiceUtil.startService(getBaseContext(), intent);
            LogUtil.writeDe(FILE_NAME, "检测到后台服务被杀死了，JobService主动去拉起后台服务！");
        }
        return false;
    }

    public static void startJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        // 创建JobService的类对象
        ComponentName appJobComponentName = new ComponentName(context, AppJobService.class);
        // 2：设置JobInfo 的参数信息
        JobInfo.Builder builder = new JobInfo.Builder(AppJobService.AppJobId, appJobComponentName);

        builder.setPersisted(true);  // 设置设备重启时，执行该任务

        int interval = 15 * 1000;

        // 7.0 之前没有任何的限制
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            builder.setPeriodic(interval); // 轮询的间隔
        } else {
            builder.setMinimumLatency(interval); // 延时的时间
        }

        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        LogUtil.e("------>:onStopJob");
        return false;
    }

}