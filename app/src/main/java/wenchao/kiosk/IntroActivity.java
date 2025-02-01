package wenchao.kiosk;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

public class IntroActivity extends AppCompatActivity {
    private Handler handler;
    private SimpleExoPlayer simpleExoPlayer;
    private MediaPlayer mediaPlayer;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        DevicePolicyManager myDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDPM = new ComponentName(this, MyAdmin.class);
        if (myDevicePolicyManager.isDeviceOwnerApp(this.getPackageName())) {
            startLockTask();
        }

        playerView = findViewById(R.id.playerView);

        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();

        playerView.setPlayer(simpleExoPlayer);


        playerView.setUseController(false);
        playIntroSound();
        playVideo(R.raw.vd1);

        handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 14000);
    }

    private void playVideo(int videoResourceId) {
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId));
        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.play();
    }
    private void playIntroSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.intro);
        mediaPlayer.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
