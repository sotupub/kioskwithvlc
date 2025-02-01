package wenchao.kiosk;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class AppStartService extends Service {

    private static final String TAG = "AppStartService";
    private static final String CHANNEL_ID = "app-start-service";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, createNotification());

        new Thread(() -> {
            if (!isMainActivityRunning()) {
                Intent mainIntent = new Intent(AppStartService.this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(mainIntent);
                } catch (Exception e) {
                    Log.e(TAG, "onCreate: Failed to start MainActivity", e);
                }
            }
        }).start();

    }
    private boolean isMainActivityRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (MainActivity.class.getName().equals(task.baseActivity.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private Notification createNotification() {
        NotificationChannel serviceChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name) + " Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(serviceChannel);
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name) + " Service")
                .setSilent(true)
                .setContentText("Please restart this device if this service is not running")
                .build();
    }


}
