package wenchao.kiosk;

import com.dcastalia.localappupdate.DownloadApk;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.BuildConfig;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

import com.google.android.exoplayer2.ui.PlayerView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import android.app.admin.DevicePolicyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import android.Manifest;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private SimpleExoPlayer simpleExoPlayer;
    private static final String BASE_URL = "http://51.83.97.190/";
    private ApiService apiService;
    private boolean isPlaying = false;
    private PlayerView playerView;
    private static final int CHECK_INTERVAL = 60000; // 60 seconds
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private List<VideoInfo> videoInfos;
    private int currentVideoIndex = 0;
    private TextView newsTextView;
    private TextView tvWelcome;
    private final String[] welcomeMessages = {
            "Welcome To Our Hotel", // English
            "Bienvenue À Notre Hôtel", // French
            "Willkommen In Unserem Hotel", // German
            "مرحبًا بكم في فندقنا", // Arabic
            "Добро Пожаловать В Наш Отель", // Russian
            "Benvenuti Nel Nostro Hotel", // Italian
            "Bienvenidos A Nuestro Hotel" // Spanish
    };


    private int currentMessageIndex = 0;
    private static final int WELCOME_MESSAGE_INTERVAL = 3000; // 3 seconds
    private Handler welcomeMessageHandler = new Handler(Looper.getMainLooper());

    Api apI;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        startWelcomeMessageRotation();

        apI = Client.getClient().create(Api.class);

        playerView = findViewById(R.id.playerView);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(simpleExoPlayer);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Initialize your ApiService
        apiService = retrofit.create(ApiService.class);

        // Call the method to fetch and play the video
        setupPlayerEventListener(); // Ensure this is called after the player is initialized
        playerView.setUseController(false);

        checkUpdate();

        Button btnOpenChannels = findViewById(R.id.btnOpenChannels);
        btnOpenChannels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAndReleasePlayer();
                Intent intent = new Intent(MainActivity.this, ChannelsActivity.class);
                startActivity(intent);
            }
        });

        startPeriodicUpdateCheck();
    }

    private void stopAndReleasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }


    private void startWelcomeMessageRotation() {
        welcomeMessageHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateWelcomeMessage();
                welcomeMessageHandler.postDelayed(this, WELCOME_MESSAGE_INTERVAL);
            }
        }, WELCOME_MESSAGE_INTERVAL);
    }

    private void updateWelcomeMessage() {
        tvWelcome.setText(welcomeMessages[currentMessageIndex]);
        currentMessageIndex = (currentMessageIndex + 1) % welcomeMessages.length;
    }
    private void checkUpdate() {
        int currentVersionCode = getVersionCode(this); // Get current version code
        Call<List<UpdateiNFO>> updateInfo = apI.getUpdateInfo();

        updateInfo.enqueue(new Callback<List<UpdateiNFO>>() {
            @Override
            public void onResponse(Call<List<UpdateiNFO>> call, Response<List<UpdateiNFO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UpdateiNFO> updateiNFOS = response.body();
                    if (!updateiNFOS.isEmpty()) {
                        UpdateiNFO latestUpdate = updateiNFOS.get(0); // Assuming first is the latest

                        if (latestUpdate.getVerCode() > currentVersionCode) {
                            if (checkPer()) {
                                DownloadApk downloadApk = new DownloadApk(MainActivity.this);
                                downloadApk.startDownloadingApk(latestUpdate.getAppUrl(), "Update");
                            } else {
                                requestPer();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "App is up-to-date", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No update information available", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UpdateiNFO>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Request permissions if needed
    void requestPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPer()) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
            }
        }
    }

    // Check if permissions are granted
    boolean checkPer() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkUpdate(); // Retry update check if permission is granted
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void sendStatusToServer() {
        String ipAddress = getIPAddress(true); // true for IPv4 address, false for IPv6
        updateOnlineStatus(true, ipAddress);
    }

    public void updateOnlineStatus(final boolean isOnline, final String ipAddress) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL("http://192.168.0.8/APKServeur/update-status.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                String data = "{\"client_id\": \"" + ipAddress + "\", \"online\": " + isOnline + "}";
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(data.getBytes(StandardCharsets.UTF_8));
                    os.close();

                    int responseCode = conn.getResponseCode();
                    Log.i("Network Status Update", "Response code: " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.i("Network Status Update", "Successfully updated status.");
                    } else {
                        Log.e("Network Status Update", "Failed to update status.");
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("Network Status Update", "Error sending status update", e);
            }
        });
    }

    public int getVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) info.getLongVersionCode(); // Returns long in Android P+
            } else {
                return info.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void showSimpleDialog(UpdateiNFO updateiNFO) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_update);

        TextView title = dialog.findViewById(R.id.textViewUpdateTitle);
        TextView message = dialog.findViewById(R.id.textViewUpdateMessage);
        Button updateButton = dialog.findViewById(R.id.buttonUpdate);

        title.setText("New Update Available - " + updateiNFO.getVerName());
        message.setText(updateiNFO.getUpNotes());

        updateButton.setOnClickListener(v -> {
            if (checkPer()) {
                DownloadApk downloadApk = new DownloadApk(MainActivity.this);
                downloadApk.startDownloadingApk(updateiNFO.getAppUrl(), "Update");
            } else {
                requestPer();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    void startDownload(String url, String title) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription("Downloading update...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedOverMetered(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "final.apk");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void promptForInstall(Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", new File(apkUri.getPath()));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "final.apk");
            Uri apkUri = FileProvider.getUriForFile(ctxt, ctxt.getPackageName() + ".provider", apkFile);
            promptForInstall(apkUri);
        }
    };




    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop the zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }


    @Override
    protected void onUserLeaveHint() {
        bringToFront();
    }


    private void bringToFront() {
        Intent bringToFrontIntent = new Intent(getApplicationContext(), ChannelsActivity.class);
        bringToFrontIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(bringToFrontIntent);
    }

    private void playVideo(String videoUrl) {
        if (simpleExoPlayer != null && videoUrl != null && !videoUrl.isEmpty()) {
            Log.d("playyy", "Preparing to play video: " + videoUrl);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            simpleExoPlayer.setMediaItem(mediaItem);
            simpleExoPlayer.prepare();
            simpleExoPlayer.setPlayWhenReady(true);
        } else {
            Log.e("playyy", "Invalid video URL or ExoPlayer is null");
            // Handle the case where the videoUrl is null or empty, or ExoPlayer is null
        }
    }


    private void setupPlayerEventListener() {
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    // When a video ends, start the next one
                    currentVideoIndex++;
                    if (currentVideoIndex < videoInfos.size()) {
                        playVideo(videoInfos.get(currentVideoIndex).getVideoUrl());
                    } else {
                        currentVideoIndex = 0; // Reset to the first video (looping)
                        playVideo(videoInfos.get(currentVideoIndex).getVideoUrl());
                    }
                }
            }
        });
    }

    private void fetchAndPlayVideo() {
        // First, try to read from local JSON file
        videoInfos = readJsonFromFile();
        if (!videoInfos.isEmpty()) {
            playVideo(videoInfos.get(currentVideoIndex).getVideoUrl());
        } else {
            // If local JSON file is empty or doesn't exist, fetch from server
            fetchAndSaveVideoInfo();
        }
    }

    private void playNextVideo() {
        if (currentVideoIndex < videoInfos.size()) {
            VideoInfo videoInfo = videoInfos.get(currentVideoIndex);
            String videoUrl = videoInfo.getVideoUrl();

            if (videoUrl != null && !videoUrl.isEmpty()) {
                Log.d("playyy", "Preparing to play video: " + videoUrl);
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                simpleExoPlayer.setMediaItem(mediaItem);
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(true);

                currentVideoIndex++; // Move to the next video
            }

            // Listen for playback end and play the next video
            simpleExoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        playNextVideo(); // Play the next video when the current one ends
                    }
                }
            });
        } else {
            // All videos have been played, you can handle this as needed
            Toast.makeText(MainActivity.this, "All videos played", Toast.LENGTH_SHORT).show();
        }
    }

    public interface ApiService {
        @GET("publicity.json")
        Call<List<VideoInfo>> getVideoInfo();
        @GET("news.json")
        Call<List<NewsInfo>> getNewsInfo();
    }



    private List<VideoInfo> readJsonFromFile() {
        List<VideoInfo> videoInfoList = new ArrayList<>();
        try (FileInputStream fis = openFileInput(LOCAL_JSON_FILE);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            String jsonString = jsonStringBuilder.toString();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<VideoInfo>>() {}.getType();
            videoInfoList = gson.fromJson(jsonString, listType);
        } catch (IOException e) {
            Log.e("JSON Read", "Error reading JSON file locally.", e);
        }
        return videoInfoList;
    }

    private static final String LOCAL_JSON_FILE = "publicity.json";

    private void fetchAndSaveVideoInfo() {
        apiService.getVideoInfo().enqueue(new Callback<List<VideoInfo>>() {
            @Override
            public void onResponse(Call<List<VideoInfo>> call, Response<List<VideoInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    videoInfos = response.body();
                    saveJsonToFile(response.body());
                    if (!videoInfos.isEmpty()) {
                        playVideo(videoInfos.get(currentVideoIndex).getVideoUrl());
                    }
                } else {
                    // Handle the case where the response is unsuccessful or the body is null
                    Log.e("Video Fetch", "Failed to fetch video list.");
                }
            }

            @Override
            public void onFailure(Call<List<VideoInfo>> call, Throwable t) {
                // Handle the failure case
                Log.e("Video Fetch", "Error fetching video list.", t);
            }
        });
    }

    private void saveJsonToFile(List<VideoInfo> videoInfoList) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(videoInfoList);
        try (FileOutputStream fos = openFileOutput(LOCAL_JSON_FILE, Context.MODE_PRIVATE)) {
            fos.write(jsonString.getBytes());
            Log.d("JSON Save", "JSON file saved locally.");
        } catch (IOException e) {
            Log.e("JSON Save", "Error saving JSON file locally.", e);
        }
    }



    private void stopVideo() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.stop();
            simpleExoPlayer.clearMediaItems();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void hideSystemUI() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }
    private void startPeriodicUpdateCheck() {
        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForJsonUpdate();
                updateHandler.postDelayed(this, CHECK_INTERVAL);
            }
        }, CHECK_INTERVAL);
    }

    private void checkForJsonUpdate() {
        apiService.getVideoInfo().enqueue(new Callback<List<VideoInfo>>() {
            @Override
            public void onResponse(Call<List<VideoInfo>> call, Response<List<VideoInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<VideoInfo> fetchedVideoInfos = response.body();
                    List<VideoInfo> localVideoInfos = readJsonFromFile();
                    if (!fetchedVideoInfos.equals(localVideoInfos)) {
                        saveJsonToFile(fetchedVideoInfos);
                        videoInfos = fetchedVideoInfos;
                        currentVideoIndex = 0;
                        playVideo(videoInfos.get(currentVideoIndex).getVideoUrl());
                    }
                } else {
                    Log.e("JSON Update", "Failed to fetch video list from server.");
                }
            }

            @Override
            public void onFailure(Call<List<VideoInfo>> call, Throwable t) {
                Log.e("JSON Update", "Error fetching video list from server.", t);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lock_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_channels) {
            Intent intent = new Intent(this, ChannelsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        welcomeMessageHandler.removeCallbacksAndMessages(null);
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        if (simpleExoPlayer == null) {
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(simpleExoPlayer);
            setupPlayerEventListener();
        }
        if (isPlaying) {
            simpleExoPlayer.prepare();
            simpleExoPlayer.setPlayWhenReady(true);
        } else if (simpleExoPlayer.getMediaItemCount() == 0) {
            fetchAndPlayVideo();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(onComplete);
        if (simpleExoPlayer != null) {
            isPlaying = simpleExoPlayer.getPlayWhenReady();
            stopAndReleasePlayer();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (simpleExoPlayer != null) {
            stopAndReleasePlayer();
        }
    }

}