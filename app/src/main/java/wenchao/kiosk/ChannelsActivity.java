package wenchao.kiosk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayer.Event;
import org.videolan.libvlc.MediaPlayer.EventListener;
import org.videolan.libvlc.interfaces.IMedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Assertions;

import android.media.MediaMetadataRetriever;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.Callback;
import android.content.res.Configuration;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.Locale;
public class ChannelsActivity extends Activity implements ChannelListAdapter.ChannelSelectionListener {
    private LinearLayout channelInfoBar;
    private static final String[] LANGUAGES = {"EN", "FR", "AR", "DE", "RU", "IT", "ES"};
    private static final String[] LANGUAGE_NAMES = {"English", "Français", "عربى", "Deutsch", "Русский", "Italiano", "Español"};

    private static final String ACCESS_CODE = "999999";
    private ImageView channelLogo;
    private ImageView channelLogoSide;
    private TextView channelName;
    private TextView fix_news;
    private TextView epgText;
    private Handler channelInfoHandler;
    private Runnable channelInfoRunnable;
    private ArrayList<Channel> channelList = new ArrayList<>();
    private ChannelListAdapter adapter;
    private GridView gridViewChannels;
    private GridView gridViewChannels1;
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private boolean isFullScreen = false;
    private String selectedCategoryId;
    private int selectedChannelPosition = -1;
    private long lastClickTime = 0;
    private TextView textViewChannelName;
    private TextView Epg;
    private boolean isFirstLoad = true;
    private TextView CategorySideBar;
    private TextView textViewCurrentChannel;

    private static final long DOUBLE_CLICK_TIME_DELTA = 200;
    private RelativeLayout navigationBar;
    private RelativeLayout infoB;
    private Button btnHome;
    private Handler handler;
    private Runnable epgRunnable;
    private LinearLayout rightSidebar;
    private LinearLayout leftSidebar;
    private Button btnSubtitles;
    private DefaultTrackSelector trackSelector;
    private Button btnAudioTracks;
    private TextView newsTextView;
    private LinearLayout channelItemLayout;
    private RelativeLayout newsTickerLayout;
    String nowText;
    private View divider;
    private ApiService1 apiService;
    private TextView subtitleView;
    private ImageView newsIcon;
    private Handler newsHandler;
    private Runnable newsRunnable;
    private boolean isNewsTickerVisible = false;
    private long lastNewsFetchTime = 0;
    private static final long NEWS_FETCH_INTERVAL = 60000;
    String loadingText;
    private Context context;
    private List<NewsInfo> lastDisplayedNewsList;

    private List<NewsInfo> newsList = new ArrayList<>();
    private Handler fetchChannelsHandler;
    private Runnable fetchChannelsRunnable;
    private int muteClickCount = 0;
    private static final int MAX_MUTE_CLICKS = 15;
    private String serverUrl = "http://51.83.97.190/rmd.php";


    private Handler updateHandler = new Handler(Looper.getMainLooper());
    private static final int CHECK_INTERVAL = 60000;

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            checkForChannelUpdates();
            updateHandler.postDelayed(this, CHECK_INTERVAL);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        SharedPreferences sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE);
        String savedLanguage = sharedPreferences.getString("selectedLanguage", "en");
        long languageSelectionTime = sharedPreferences.getLong("languageSelectionTime", 0);

        long oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L;
        long currentTime = System.currentTimeMillis();
        if (currentTime - languageSelectionTime < oneWeekInMillis) {
            setDefaultLanguage(savedLanguage);
        } else {

            setDefaultLanguage("en");
        }

        serverUrl = sharedPreferences.getString("serverUrl", "http://51.83.97.190/rmd.php");
        lastDisplayedNewsList = JsonUtils.loadNewsList(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI();
        setContentView(R.layout.activity_channels);

        initializeViews();
        playerView.setUseController(false);
        setupPlayer();
        setupGridView();
        setupVLC();

        fetchChannels();
        initializeRetrofit();

        Button btnAllChannels = findViewById(R.id.btnAllChannels);
        Button btnEntertainment = findViewById(R.id.btnEntertainment);
        Button btnNews = findViewById(R.id.btnNews);
        Button btnKids = findViewById(R.id.btnKids);
        Button btnMovies = findViewById(R.id.btnMovies);
        Button btnSports = findViewById(R.id.btnSports);

        btnAllChannels.setOnClickListener(v -> showChannelsForCategory(null));
        btnEntertainment.setOnClickListener(v -> showChannelsForCategory("1"));
        btnNews.setOnClickListener(v -> showChannelsForCategory("4"));
        btnKids.setOnClickListener(v -> showChannelsForCategory("2"));
        btnMovies.setOnClickListener(v -> showChannelsForCategory("3"));
        btnSports.setOnClickListener(v -> showChannelsForCategory("5"));

        btnAudioTracks = findViewById(R.id.btnAudioTracks);
        View btnSubtitles = findViewById(R.id.btnSubtitles);
        btnAudioTracks.setOnClickListener(v -> showAudioTracksDialog());
        fetchChannelsHandler = new Handler();
        fetchChannelsRunnable = new Runnable() {
            @Override
            public void run() {
                checkForChannelUpdates();
                fetchChannelsHandler.postDelayed(this, 60000); // Check every 60 seconds
            }
        };
        fetchChannelsHandler.post(fetchChannelsRunnable);
        Button btnSelectLanguage = findViewById(R.id.btnSelectLanguage);
        btnSelectLanguage.setOnClickListener(v -> showLanguageSelectionDialog());

    }
    private void setDefaultLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void showLanguageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_language);

        // Create an array that combines language codes and full names
        String[] displayLanguages = new String[LANGUAGES.length];
        for (int i = 0; i < LANGUAGES.length; i++) {
            displayLanguages[i] = LANGUAGES[i].toUpperCase() + " - " + LANGUAGE_NAMES[i];
        }

        builder.setItems(displayLanguages, (dialog, which) -> {
            String selectedLanguage = LANGUAGES[which];
            changeLanguage(selectedLanguage);
        });
        builder.show();
    }
    private void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Save the selected language and the timestamp to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedLanguage", languageCode);
        editor.putLong("languageSelectionTime", System.currentTimeMillis());
        editor.apply();

        // Restart the activity to apply the new language
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }



    private void initializeViews() {
        textViewChannelName = findViewById(R.id.textViewChannelName);
        Epg = findViewById(R.id.Epg);
        textViewCurrentChannel = findViewById(R.id.textViewCurrentChannel);
        BigImageViewer.initialize(GlideImageLoader.with(this));
        channelLogoSide = findViewById(R.id.channelLogoSide);
        gridViewChannels = findViewById(R.id.gridViewChannels);
        gridViewChannels1 = findViewById(R.id.gridViewChannels1);
        playerView = findViewById(R.id.playerView);
        loadingText = getString(R.string.loading);
        nowText = getString(R.string.epg_no);
        rightSidebar = findViewById(R.id.rightSidebar);
        leftSidebar = findViewById(R.id.leftSidebar);
        CategorySideBar = findViewById(R.id.CategorySideBar);
        navigationBar = findViewById(R.id.navigationBar);
        btnHome = findViewById(R.id.btnHome);
        btnSubtitles = findViewById(R.id.btnSubtitles);
        btnAudioTracks = findViewById(R.id.btnAudioTracks);
        divider = findViewById(R.id.divider);
        infoB = findViewById(R.id.infoBar);
        btnHome.setOnClickListener(v -> openMainActivity());

        btnSubtitles.setOnClickListener(v -> showSubtitleTracksDialog());

        btnAudioTracks.setOnClickListener(v -> showAudioTracksDialog());
        newsTickerLayout = findViewById(R.id.newsTickerLayout);
        newsTextView = findViewById(R.id.newsTextView);
        fix_news = findViewById(R.id.fix_news);
        newsTextView.setVisibility(View.GONE);
        newsTickerLayout.setVisibility(View.GONE);
        fix_news.setVisibility(View.GONE);
    }
    private void showServerUrlPrompt() {
        AlertDialog.Builder codeDialogBuilder = new AlertDialog.Builder(this);
        codeDialogBuilder.setTitle("Enter Access Code");

        // Set up the input for the access code
        final EditText inputCode = new EditText(this);
        inputCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputCode.setHint("Enter access code");
        codeDialogBuilder.setView(inputCode);

        // Set up the buttons for the code dialog
        codeDialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            String enteredCode = inputCode.getText().toString();
            if (ACCESS_CODE.equals(enteredCode)) {
                // Show the URL dialog if the code is correct
                showUrlDialog();
            } else {
                Toast.makeText(this, "Invalid access code", Toast.LENGTH_SHORT).show();
            }
        });
        codeDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        codeDialogBuilder.show();
    }

    private void showUrlDialog() {
        AlertDialog.Builder urlDialogBuilder = new AlertDialog.Builder(this);
        urlDialogBuilder.setTitle("Change Server URL");

        // Set up the input for the URL
        final EditText inputUrl = new EditText(this);
        inputUrl.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        inputUrl.setText(serverUrl); // Pre-fill with the current URL
        urlDialogBuilder.setView(inputUrl);

        // Set up the buttons for the URL dialog
        urlDialogBuilder.setPositiveButton("OK", (dialog, which) -> {
            serverUrl = inputUrl.getText().toString();
            // Save the new URL to shared preferences or another persistent storage if needed
            SharedPreferences sharedPreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("serverUrl", serverUrl);
            editor.apply();
            checkForChannelUpdates();
        });
        urlDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        urlDialogBuilder.show();
    }

    public interface ApiService1 {
        @GET("news.json")
        Call<List<NewsInfo>> getNewsInfo();
    }

    private void fetchAndDisplayNews() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastNewsFetchTime < NEWS_FETCH_INTERVAL && isNewsTickerVisible) {
            // If it's been less than the fetch interval and the ticker is already visible, skip fetching
            return;
        }

        apiService.getNewsInfo().enqueue(new Callback<List<NewsInfo>>() {
            @Override
            public void onResponse(Call<List<NewsInfo>> call, Response<List<NewsInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsInfo> fetchedNewsList = response.body();
                    if (fetchedNewsList == null || fetchedNewsList.isEmpty()) {
                        runOnUiThread(ChannelsActivity.this::hideNewsTicker);
                    } else if (isNewsListDifferent(fetchedNewsList, lastDisplayedNewsList)) {
                        newsList = fetchedNewsList;
                        runOnUiThread(() -> {
                            if (!newsList.isEmpty()) {
                                displayNewsTicker();
                                lastNewsFetchTime = System.currentTimeMillis();
                                lastDisplayedNewsList = new ArrayList<>(newsList);
                                JsonUtils.saveNewsList(context, lastDisplayedNewsList);
                            } else {
                                hideNewsTicker();
                            }
                        });
                    }
                } else {
                    Log.e("News Fetch", "Failed to fetch news.");
                    runOnUiThread(ChannelsActivity.this::hideNewsTicker);
                }
            }

            @Override
            public void onFailure(Call<List<NewsInfo>> call, Throwable t) {
                Log.e("News Fetch", "Error fetching news.", t);
                runOnUiThread(ChannelsActivity.this::hideNewsTicker);
            }
        });
    }


    private boolean isNewsListDifferent(List<NewsInfo> fetchedNewsList, List<NewsInfo> lastDisplayedNewsList) {
        if (lastDisplayedNewsList == null || fetchedNewsList.size() != lastDisplayedNewsList.size()) {
            return true;
        }

        for (int i = 0; i < fetchedNewsList.size(); i++) {
            NewsInfo fetchedNews = fetchedNewsList.get(i);
            NewsInfo lastDisplayedNews = lastDisplayedNewsList.get(i);
            if (!fetchedNews.getContent().equals(lastDisplayedNews.getContent())) {
                return true;
            }
        }

        return false;
    }

    private void checkForChannelUpdates() {
        new FetchChannelDataTask().execute(serverUrl);
    }





    private void hideNewsTicker() {
        runOnUiThread(() -> {
            newsTickerLayout.setVisibility(View.GONE);
            fix_news.setVisibility(View.GONE);
            newsTextView.setVisibility(View.GONE);
            isNewsTickerVisible = false;
        });
    }




    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://51.83.97.190/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService1.class);
    }

    private void displayNewsTicker() {
        runOnUiThread(() -> {
            if (!newsList.isEmpty()) {
                newsTickerLayout.setVisibility(View.VISIBLE);
                fix_news.setVisibility(View.VISIBLE);
                newsTextView.setVisibility(View.VISIBLE);
                showNextNews(0);
                newsHandler.postDelayed(this::hideNewsTicker, NEWS_FETCH_INTERVAL);
                isNewsTickerVisible = true;
            } else {
                hideNewsTicker();
            }
        });
    }



    private void showNextNews(int index) {
        if (index >= newsList.size()) {
            index = 0;
        }

        NewsInfo newsInfo = newsList.get(index);
        newsTextView.setText(newsInfo.getContent());
        newsTextView.setSelected(true);
        int duration = calculateScrollDuration(newsTextView.getText().length());
        int finalIndex = index;
        newsTextView.postDelayed(() -> showNextNews(finalIndex + 1), duration);
    }

    private int calculateScrollDuration(int textLength) {
        int baseDuration = 10000;
        int multiplier = 50;
        return baseDuration + textLength * multiplier;
    }

    private void setupNewsFetchHandler() {
        newsHandler = new Handler();
        newsRunnable = new Runnable() {
            @Override
            public void run() {
                fetchAndDisplayNews();
                newsHandler.postDelayed(this, NEWS_FETCH_INTERVAL);
            }
        };
        newsHandler.post(newsRunnable);
    }



    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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

    @Override
    public void onBackPressed() {
        if (newsTextView != null && newsTextView.getVisibility() == View.VISIBLE) {
            hideNewsTicker();
        } else {
            super.onBackPressed();
        }
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

    private void setupPlayer() {
        LoadControl loadControl = new DefaultLoadControl();
        trackSelector = new DefaultTrackSelector(this);
        trackSelector.setParameters(
                trackSelector.buildUponParameters()
                        .setForceHighestSupportedBitrate(true)
                        .setMaxVideoSizeSd()
                        .setSelectUndeterminedTextLanguage(true)
        );
        simpleExoPlayer = new SimpleExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .setTrackSelector(trackSelector)
                .build();

        playerView.setPlayer(simpleExoPlayer);
    }

    private void setupVLC() {
        ArrayList<String> options = new ArrayList<>();
        options.add("--no-drop-late-frames");
        options.add("--avcodec-hw=any");
        libVLC = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.setEventListener(event -> {
            switch (event.type) {
                case Event.Buffering:
                    Log.d("VLC", "Buffering: " + event.getBuffering());
                    break;
                case Event.Playing:
                    Log.d("VLC", "Playing");
                    break;
                case Event.EncounteredError:
                    Log.e("VLC", "Encountered Error");
                    break;
                case Event.Opening:
                    Log.d("VLC", "Opening");
                    break;
                case Event.Paused:
                    Log.d("VLC", "Paused");
                    break;
                case Event.Stopped:
                    Log.d("VLC", "Stopped");
                    break;
                case Event.TimeChanged:
                    Log.d("VLC", "Time Changed: " + event.getTimeChanged());
                    break;
                case Event.PositionChanged:
                    Log.d("VLC", "Position Changed: " + event.getPositionChanged());
                    break;
                case Event.Vout:
                    Log.d("VLC", "Video Output: " + event.getVoutCount());
                    break;
                case Event.ESAdded:
                    Log.d("VLC", "ES Added: " + event.getEsChangedType());
                    break;
                case Event.ESDeleted:
                    Log.d("VLC", "ES Deleted: " + event.getEsChangedType());
                    break;
                case Event.ESSelected:
                    Log.d("VLC", "ES Selected: " + event.getEsChangedType());
                    break;
                default:
                    break;
            }
        });
    }

    private void updateEPG() {
        Media media = (Media) mediaPlayer.getMedia();
        if (media != null) {
            media.parse(Media.Parse.ParseNetwork);
            new Handler().postDelayed(() -> {
                String title = media.getMeta(Media.Meta.Title);
                String nowPlaying = media.getMeta(Media.Meta.NowPlaying);
                runOnUiThread(() -> {
                    if (nowPlaying == null) {
                        Epg.setText(nowText +" :");
                    } else {
                        Epg.setText(nowText +" : " + nowPlaying);
                    }
                });
            }, 1000); // Adjust delay as needed to ensure parsing completes
        } else {
            Log.d("VLC", "Media is null, cannot get metadata");
            runOnUiThread(() -> {
                Epg.setText(nowText + ":");
            });
        }
    }

    private void showSubtitleTracksDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_subtitle));

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            TrackGroupArray subtitleTrackGroups = mappedTrackInfo.getTrackGroups(C.TRACK_TYPE_TEXT);
            if (subtitleTrackGroups.length > 0) {
                ArrayList<CharSequence> subtitleDescriptions = new ArrayList<>();
                DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                int selectedTrackIndex = parameters.getSelectionOverride(C.TRACK_TYPE_TEXT, subtitleTrackGroups) != null ?
                        parameters.getSelectionOverride(C.TRACK_TYPE_TEXT, subtitleTrackGroups).groupIndex : -1;

                for (int groupIndex = 0; groupIndex < subtitleTrackGroups.length; groupIndex++) {
                    TrackGroup trackGroup = subtitleTrackGroups.get(groupIndex);
                    for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                        Format format = trackGroup.getFormat(trackIndex);
                        String description = String.format(Locale.US, "Track %d: %s (%s)",
                                trackIndex + 1,
                                format.language != null ? new Locale(format.language).getDisplayLanguage() : "Unknown",
                                format.id != null ? format.id : "N/A");
                        subtitleDescriptions.add(description);
                    }
                }

                CharSequence[] subtitleDescriptionsArray = subtitleDescriptions.toArray(new CharSequence[0]);
                builder.setSingleChoiceItems(subtitleDescriptionsArray, selectedTrackIndex, (dialog, which) -> {
                    DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(which, 0);
                    DefaultTrackSelector.Parameters newParams = parameters.buildUpon()
                            .setSelectionOverride(C.TRACK_TYPE_TEXT, subtitleTrackGroups, override)
                            .build();
                    trackSelector.setParameters(newParams);
                    dialog.dismiss();
                });
            } else {
                builder.setMessage(getString(R.string.no_subtitles_available));
            }
        } else {
            builder.setMessage(getString(R.string.no_audio_tracks_available));
        }

        builder.create().show();
    }

    private void showAudioTracksDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_audio_track));

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            boolean audioTracksAvailable = false;
            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_AUDIO) {
                    TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
                    if (trackGroups.length > 0) {
                        List<String> trackDescriptions = new ArrayList<>();
                        List<Pair<Integer, Integer>> trackInfo = new ArrayList<>();

                        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
                            TrackGroup trackGroup = trackGroups.get(groupIndex);
                            Format format = trackGroup.getFormat(0);
                            String language = format.language != null ? new Locale(format.language).getDisplayLanguage() : "Unknown";
                            String description = String.format(Locale.US, "Language: %s",
                                    language,
                                    format.id != null ? format.id : "N/A");
                            trackDescriptions.add(description);
                            trackInfo.add(new Pair<>(groupIndex, 0));
                        }

                        int finalRendererIndex = rendererIndex;
                        builder.setSingleChoiceItems(trackDescriptions.toArray(new String[0]), -1, (dialog, which) -> {
                            Pair<Integer, Integer> selectedTrack = trackInfo.get(which);
                            setAudioTrack(finalRendererIndex, selectedTrack.first);
                            dialog.dismiss();
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        audioTracksAvailable = true;
                        return;
                    }
                }
            }

            if (!audioTracksAvailable) {
                Toast.makeText(this, getString(R.string.no_audio_tracks_available), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_audio_tracks_available), Toast.LENGTH_SHORT).show();
        }
    }

    private void setAudioTrack(int rendererIndex, int groupIndex) {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
        DefaultTrackSelector.Parameters.Builder builder = parameters.buildUpon();

        TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
        if (groupIndex >= 0 && groupIndex < trackGroups.length && trackGroups.get(groupIndex).length > 0) {
            DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(groupIndex, 0);
            builder.setSelectionOverride(rendererIndex, trackGroups, override);
            trackSelector.setParameters(builder.build());
            Log.d("TrackChange", "Attempting to change to Group: " + groupIndex);

            Format format = trackGroups.get(groupIndex).getFormat(0);
            String language = format.language != null ? new Locale(format.language).getDisplayLanguage() : "Unknown";

            Toast.makeText(this, "Audio track set to " + language, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Invalid group index or no tracks in group", Toast.LENGTH_LONG).show();
        }
    }

    private void showChannelInfoBar(String logoUrl, String channelName, String epgText) {
        ImageView channelLogo = findViewById(R.id.channelLogo);
        TextView channelNameTextView = findViewById(R.id.channelName);
        TextView epgTextView = findViewById(R.id.epgText);

        Glide.with(this)
                .load(logoUrl)
                .placeholder(R.drawable.intro)
                .error(R.drawable.intro)
                .into(channelLogo);
        channelNameTextView.setText(channelName);
        epgTextView.setText(epgText);
    }

    private void setupGridView() {
        adapter = new ChannelListAdapter(this, channelList, this);
        gridViewChannels.setAdapter(adapter);
        gridViewChannels1.setAdapter(adapter);

        gridViewChannels.setOnItemClickListener((parent, view, position, id) -> handleChannelSelection(position));

        gridViewChannels1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ChannelsActivity", "Clicked item from sidebar at position: " + position);
                handleChannelSelectionFromSidebar(position);
            }
        });
    }

    private boolean isSidebarVisible = false;
    private String currentChannelName = "";
    private List<Integer> externalPositionList;

    private void handleChannelSelectionFromSidebar(int position) {
        long clickTime = System.currentTimeMillis();
        handleChannelSelectionInternal(position);

        if (selectedChannelPosition == position && currentChannelName.equals(channelList.get(position).getName())) {
            toggleSidebar(leftSidebar, !isSidebarVisible);

            handler = new Handler();
            handler.postDelayed(() -> {
                Channel currentChannel = channelList.get(selectedChannelPosition);
                showChannelInfoBar(currentChannel.getLogoUrl(), currentChannel.getName(), (String) Epg.getText());
                infoB.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> infoB.setVisibility(View.GONE), 5000);
            }, 0);
            isSidebarVisible = !isSidebarVisible;
        } else {
            if (!isSidebarVisible || !currentChannelName.equals(channelList.get(position).getName())) {
                updateSidebarForChannelName(channelList.get(position).getName());
                toggleSidebar(leftSidebar, true);
                isSidebarVisible = true;
            }
        }

        lastClickTime = clickTime;
    }

    private void handleChannelSelection(int position) {
        if (selectedChannelPosition == position && simpleExoPlayer.getPlayWhenReady()) {
            toggleFullScreen();
            handleChannelSelectionInternal(position);
            Log.d("Position", "Position inside: " + position);
        } else {
            handleChannelSelectionInternal(position);
            Log.d("Position", "Position inside: " + position);
        }
    }

    private void handleChannelSelectionInternal(int position) {
        Channel channel = channelList.get(position);
        String newChannelName = channel.getName();

        if (!currentChannelName.equals(newChannelName)) {
            selectedChannelPosition = -1;
        }
        currentChannelName = newChannelName;

        selectedChannelPosition = position;

        textViewChannelName.setText(getString(R.string.channel) + channel.getName());
        textViewCurrentChannel.setText(channel.getName());

        Glide.with(this)
                .load(channel.getLogoUrl())
                .placeholder(R.drawable.intro)
                .error(R.drawable.intro)
                .into(channelLogoSide);
        playChannel(channel.getChannelUrl());

        adapter.setSelectedPosition(position);
        adapter.notifyDataSetChanged();
    }

    private void updateSidebarForChannelName(String channelName) {
        List<Channel> filteredChannels = filterChannelsByChannelName(channelName);
        adapter.updateChannelList((ArrayList<Channel>) filteredChannels);
        adapter.setSelectedPosition(-1);
        adapter.notifyDataSetChanged();
    }

    private List<Channel> filterChannelsByChannelName(String channelName) {
        List<Channel> filteredChannels = new ArrayList<>();
        for (Channel channel : channelList) {
            if (channel.getName().equals(channelName)) {
                filteredChannels.add(channel);
            }
        }
        return filteredChannels;
    }

    private void selectChannelFromExternalPositionList(int externalPosition) {
        if (externalPosition < 0 || externalPosition >= externalPositionList.size()) {
            return;
        }
        int channelPosition = externalPositionList.get(externalPosition);
        handleChannelSelectionInternal(channelPosition);
    }

    private void playChannel(String videoUrl) {
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));

        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);

        Uri uri = Uri.parse(videoUrl);
        Media media = new Media(libVLC, uri);
        mediaPlayer.setMedia(media);
        mediaPlayer.play();
        mediaPlayer.setVolume(0);

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        runOnUiThread(() -> {
            Epg.setText(nowText + " : " + loadingText);
        });
        handler = new Handler();

        handler.postDelayed(() -> updateEPG(), 5000);
    }

    private void toggleFullScreen() {
        FrameLayout frameLayoutPlayer = findViewById(R.id.frameLayoutPlayer);
        RelativeLayout infoBar = findViewById(R.id.infoBar);
        isFullScreen = !isFullScreen;
        adjustPlayerLayout(true, false);

        if (isFullScreen) {
            enterFullScreen();
            adapter.setLeftSidebarActive(true);

            handler = new Handler();
            handler.postDelayed(() -> {
                Channel currentChannel = channelList.get(selectedChannelPosition);
                showChannelInfoBar(currentChannel.getLogoUrl(), currentChannel.getName(), (String) Epg.getText());
                infoB.setVisibility(View.VISIBLE);
                handler.postDelayed(() -> infoB.setVisibility(View.GONE), 5000);
            }, 0);
        } else {
            exitFullScreen();
        }
        adapter.notifyDataSetChanged();
    }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
    private void enterFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        LinearLayout contentLayout = findViewById(R.id.contentLayout);
        FrameLayout frameLayoutPlayer = findViewById(R.id.frameLayoutPlayer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        isFullScreen = true;
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        initializeRetrofit();
        fetchAndDisplayNews();
        setupNewsFetchHandler();

        gridViewChannels.setVisibility(View.GONE);
        navigationBar.setVisibility(View.GONE);
        textViewChannelName.setVisibility(View.GONE);
        Epg.setVisibility(View.GONE);

        ViewGroup.MarginLayoutParams contentLayoutParams = (ViewGroup.MarginLayoutParams) contentLayout.getLayoutParams();
        contentLayoutParams.setMargins(0, 0, 0, 0);
        contentLayout.setLayoutParams(contentLayoutParams);

        ViewGroup.LayoutParams playerLayoutParams = playerView.getLayoutParams();
        playerLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        playerLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.full);
        playerView.setLayoutParams(playerLayoutParams);
        ViewGroup.MarginLayoutParams frameLayoutParams = (ViewGroup.MarginLayoutParams) frameLayoutPlayer.getLayoutParams();
        frameLayoutParams.setMargins(0, 0, 0, 0);
        frameLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.full);
        frameLayoutPlayer.setLayoutParams(frameLayoutParams);

        adjustPlayerLayout(false, false);
    }

    private void exitFullScreen() {
        LinearLayout contentLayout = findViewById(R.id.contentLayout);
        FrameLayout frameLayoutPlayer = findViewById(R.id.frameLayoutPlayer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        isFullScreen = false;
        newsTickerLayout.setVisibility(View.GONE);
        newsTextView.setVisibility(View.GONE);
        fix_news.setVisibility(View.GONE);
        infoB.setVisibility(View.GONE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        gridViewChannels.setVisibility(View.VISIBLE);
        navigationBar.setVisibility(View.VISIBLE);
        textViewChannelName.setVisibility(View.VISIBLE);
        Epg.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams frameLayoutParams = (ViewGroup.MarginLayoutParams) frameLayoutPlayer.getLayoutParams();
        frameLayoutParams.setMargins(0, 0, 10, 0);
        frameLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.exit);
        frameLayoutPlayer.setLayoutParams(frameLayoutParams);

        ViewGroup.MarginLayoutParams playerLayoutParams = (ViewGroup.MarginLayoutParams) playerView.getLayoutParams();
        playerLayoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        playerLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.player_height);
        playerView.setLayoutParams(playerLayoutParams);

        ViewGroup.MarginLayoutParams contentLayoutParams = (ViewGroup.MarginLayoutParams) contentLayout.getLayoutParams();
        contentLayoutParams.setMargins(30, 0, 30, 0);
        contentLayout.setLayoutParams(contentLayoutParams);

        adjustPlayerLayout(false, false);
        if (newsHandler != null) {
            newsHandler.removeCallbacks(newsRunnable);
        }
    }


    private void adjustPlayerLayout(boolean showLeft, boolean showRight) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        playerView.setLayoutParams(params);

        infoB.setVisibility(showRight ? View.VISIBLE : View.GONE);
        leftSidebar.setVisibility(showLeft ? View.VISIBLE : View.GONE);

        adapter.setLeftSidebarActive(showLeft);
    }

    private void startDividerAnimation() {
        AlphaAnimation fadeAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeAnimation.setDuration(1000);
        fadeAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        fadeAnimation.setRepeatMode(AlphaAnimation.REVERSE);
        divider.startAnimation(fadeAnimation);
    }

    @Override
    public void onChannelSelected(Channel channel, int position) {
        if (!isFullScreen) {
            enterFullScreen();
        }
        playChannel(channel.getChannelUrl());
    }

    private void toggleSidebar(View sidebar, boolean isVisible) {
        sidebar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (isFullScreen && leftSidebar.getVisibility() == View.GONE) {
                        toggleSidebar(leftSidebar, false);
                        Channel currentChannel = channelList.get(selectedChannelPosition);
                        showChannelInfoBar(currentChannel.getLogoUrl(), currentChannel.getName(), (String) Epg.getText());
                        infoB.setVisibility(View.VISIBLE);
                        Button btnAudioTracks = findViewById(R.id.btnAudioTracks);
                        btnAudioTracks.requestFocus();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (isFullScreen && infoB.getVisibility() == View.GONE) {

                        if (selectedChannelPosition >= 0 && selectedChannelPosition < channelList.size()) {
                            Channel currentChannel = channelList.get(selectedChannelPosition);
                            showChannelInfoBar(currentChannel.getLogoUrl(), currentChannel.getName(), (String) Epg.getText());
                            adjustPlayerLayout(true, false);
                            toggleSidebar(leftSidebar, true);
                            infoB.setVisibility(View.VISIBLE);
                            handler = new Handler();
                            handler.postDelayed(() -> infoB.setVisibility(View.GONE), 5000);
                        } else {
                            handleChannelSelection(selectedChannelPosition);
                        }
                        return true;
                    }
                    break;
                case 4137:
                    muteClickCount++;
                    Log.d("mute", "mute click: "+muteClickCount);
                    if (muteClickCount >= MAX_MUTE_CLICKS) {
                        showServerUrlPrompt();
                        muteClickCount = 0;
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (isFullScreen && leftSidebar.getVisibility() == View.GONE && infoB.getVisibility() == View.GONE) {
                        if (selectedChannelPosition > 0) {
                            handleChannelSelection(selectedChannelPosition - 1);
                            handler = new Handler();
                            handler.postDelayed(() -> {
                                Channel currentChannel = channelList.get(selectedChannelPosition);
                                showChannelInfoBar(currentChannel.getLogoUrl(), currentChannel.getName(), (String) Epg.getText());
                                infoB.setVisibility(View.VISIBLE);
                                handler.postDelayed(() -> infoB.setVisibility(View.GONE), 5000);
                            }, 0);
                            return true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (isFullScreen && leftSidebar.getVisibility() == View.GONE && infoB.getVisibility() == View.GONE) {
                        if (selectedChannelPosition < channelList.size() - 1) {
                            handleChannelSelection(selectedChannelPosition + 1);
                            handler = new Handler();
                            handler.postDelayed(() -> {
                                Channel currentChannel = channelList.get(selectedChannelPosition);
                                showChannelInfoBar(currentChannel.getLogoUrl(), currentChannel.getName(), (String) Epg.getText());
                                infoB.setVisibility(View.VISIBLE);
                                handler.postDelayed(() -> infoB.setVisibility(View.GONE), 5000);
                            }, 0);
                            return true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_BACK:
                    if (isFullScreen) {
                        if (infoB.getVisibility() == View.VISIBLE || leftSidebar.getVisibility() == View.VISIBLE) {
                            adjustPlayerLayout(false, false);
                            infoB.setVisibility(View.GONE);
                            return true;
                        } else if (newsTextView != null && newsTextView.getVisibility() == View.VISIBLE) {
                            hideNewsTicker();
                            return true;
                        } else {
                            exitFullScreen();
                            return true;
                        }
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    private void fetchChannels() {
        SharedPreferences sharedPreferences = getSharedPreferences("ChannelData", Context.MODE_PRIVATE);
        String savedJson = sharedPreferences.getString("jsonData", null);

        if (savedJson != null) {
            try {
                ArrayList<Channel> channels = parseJsonData(savedJson);
                updateChannelList(channels);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new FetchChannelDataTask().execute(serverUrl);
    }


    private class FetchChannelDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
                return response.toString();
            } catch (IOException e) {
                Log.e("ChannelsActivity", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            SharedPreferences sharedPreferences = getSharedPreferences("ChannelData", Context.MODE_PRIVATE);
            String savedJson = sharedPreferences.getString("jsonData", null);

            if (result != null) {
                try {
                    ArrayList<Channel> fetchedChannels = parseJsonData(result);
                    ArrayList<Channel> localChannels = new ArrayList<>();

                    if (savedJson != null) {
                        localChannels = parseJsonData(savedJson);
                    }

                    if (!fetchedChannels.equals(localChannels)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("jsonData", result);
                        editor.apply();
                        updateChannelList(fetchedChannels);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Use local JSON data if available
                if (savedJson != null) {
                    try {
                        ArrayList<Channel> localChannels = parseJsonData(savedJson);
                        updateChannelList(localChannels);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ChannelsActivity", "No local JSON data available.");
                }
            }
        }
    }





    private void playFirstChannel() {
        if (!channelList.isEmpty()) {
            Channel firstChannel = channelList.get(0);
            textViewChannelName.setText(getString(R.string.channel) + firstChannel.getName());
            textViewCurrentChannel.setText(firstChannel.getName());
            playChannel(firstChannel.getChannelUrl());
        }
    }

    private ArrayList<Channel> parseJsonData(String jsonString) throws JSONException {
        ArrayList<Channel> channels = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObject = dataArray.getJSONObject(i);
            JSONArray channelsArray = dataObject.getJSONArray("channels");
            for (int k = 0; k < channelsArray.length(); k++) {
                JSONObject channelObject = channelsArray.getJSONObject(k);
                String id = channelObject.getString("id");
                String name = channelObject.getString("name");
                String logo = channelObject.getString("logo");
                String channelUrl = channelObject.getString("ch");
                String category = channelObject.getString("categ");

                Channel channel = new Channel(id, name, logo, channelUrl, category);
                channels.add(channel);
            }
        }
        return channels;
    }

    private void updateChannelList(ArrayList<Channel> channels) {
        if (channels != null && !channels.isEmpty()) {
            channelList.clear();
            channelList.addAll(channels);
            if (selectedCategoryId != null) {
                filterChannelsByCategory(selectedCategoryId);
            } else {
                adapter.notifyDataSetChanged();
            }
            if (isFirstLoad) {
                playFirstChannel();
                isFirstLoad = false;
            }
        }
    }


    public void showChannelsForCategory(String categoryId) {
        selectedCategoryId = categoryId;
        if (categoryId == null) {
            CategorySideBar.setText(getString(R.string.all_channels));
        } else if (categoryId.equals("1")) {
            CategorySideBar.setText(getString(R.string.entertainment));
        } else if (categoryId.equals("2")) {
            CategorySideBar.setText(getString(R.string.kids));
        } else if (categoryId.equals("3")) {
            CategorySideBar.setText(getString(R.string.movies));
        } else if (categoryId.equals("4")) {
            CategorySideBar.setText(getString(R.string.news));
        } else if (categoryId.equals("5")) {
            CategorySideBar.setText(getString(R.string.sports));
        }
        fetchChannels();
    }


    private void filterChannelsByCategory(String categoryId) {
        ArrayList<Channel> filteredList = new ArrayList<>();
        if (categoryId == null || categoryId.equals("all")) {
            filteredList.addAll(channelList);
        } else {
            for (Channel channel : channelList) {
                if (channel.getCategory().equals(categoryId)) {
                    filteredList.add(channel);
                }
            }
        }
        adapter.updateChannelList(filteredList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (libVLC != null) {
            libVLC.release();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (newsHandler != null) {
            newsHandler.removeCallbacks(newsRunnable);
        }
        if (fetchChannelsHandler != null) {
            fetchChannelsHandler.removeCallbacks(fetchChannelsRunnable);
        }
    }
}

