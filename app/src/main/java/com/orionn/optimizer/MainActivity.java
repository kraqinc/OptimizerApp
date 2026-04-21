package com.orionn.optimizer;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orionn.optimizer.core.TwsAssetInstaller;
import com.orionn.optimizer.core.TwsRepository;
import com.orionn.optimizer.tweaks.TweakAdapter;
import com.orionn.optimizer.utils.BlurUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private View pageRam;
    private RecyclerView recyclerTweaks;
    private View pageSystem;

    private TextView ramInfo;
    private TextView storageInfo;
    private TextView systemInfo;
    private TextView statusInfo;

    private TextView tabRam;
    private TextView tabTweaks;
    private TextView tabSystem;

    private TweakAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private CardView headerCard;
    private CardView bottomCard;

    private final Runnable refresher = new Runnable() {
        @Override
        public void run() {
            refreshRam();
            refreshTweaks();
            refreshSystem();
            handler.postDelayed(this, 1500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwsAssetInstaller.install(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0A0A0A"));
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        headerCard = new CardView(this);
        LinearLayout.LayoutParams headerLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        headerLp.setMargins(dp(16), dp(16), dp(16), dp(10));
        headerCard.setLayoutParams(headerLp);
        headerCard.setUseCompatPadding(true);
        headerCard.setRadius(dp(24));
        headerCard.setCardBackgroundColor(Color.parseColor("#151515"));

        LinearLayout headerContent = new LinearLayout(this);
        headerContent.setOrientation(LinearLayout.VERTICAL);
        headerContent.setPadding(dp(18), dp(18), dp(18), dp(18));

        TextView title = new TextView(this);
        title.setText("Optimizer");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22f);
        title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("Panel de optimización y tweaks");
        subtitle.setTextColor(Color.parseColor("#A0A0A0"));
        subtitle.setTextSize(13f);
        subtitle.setPadding(0, dp(4), 0, 0);

        statusInfo = new TextView(this);
        statusInfo.setTextColor(Color.parseColor("#7C4DFF"));
        statusInfo.setTextSize(12f);
        statusInfo.setPadding(0, dp(10), 0, 0);

        headerContent.addView(title);
        headerContent.addView(subtitle);
        headerContent.addView(statusInfo);
        headerCard.addView(headerContent);

        FrameLayout content = new FrameLayout(this);
        LinearLayout.LayoutParams contentLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        content.setLayoutParams(contentLp);

        pageRam = createRamPage();
        recyclerTweaks = createTweaksPage();
        pageSystem = createSystemPage();

        content.addView(pageRam);
        content.addView(recyclerTweaks);
        content.addView(pageSystem);

        bottomCard = new CardView(this);
        LinearLayout.LayoutParams bottomLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(76)
        );
        bottomLp.setMargins(dp(16), dp(10), dp(16), dp(16));
        bottomCard.setLayoutParams(bottomLp);
        bottomCard.setUseCompatPadding(true);
        bottomCard.setRadius(dp(22));
        bottomCard.setCardBackgroundColor(Color.parseColor("#151515"));

        LinearLayout bottom = new LinearLayout(this);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        bottom.setGravity(Gravity.CENTER_VERTICAL);
        bottom.setLayoutParams(new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        tabRam = createTab("RAM", true);
        tabTweaks = createTab("TWEAKS", false);
        tabSystem = createTab("SYSTEM", false);

        bottom.addView(tabRam);
        bottom.addView(tabTweaks);
        bottom.addView(tabSystem);
        bottomCard.addView(bottom);

        root.addView(headerCard);
        root.addView(content);
        root.addView(bottomCard);

        setContentView(root);

        BlurUtils.applySoftBlur(headerCard);
        BlurUtils.applySoftBlur(bottomCard);

        adapter = new TweakAdapter(this, (item, enabled, result) ->
                Toast.makeText(this, item.name + ": " + result, Toast.LENGTH_LONG).show()
        );
        recyclerTweaks.setLayoutManager(new LinearLayoutManager(this));
        recyclerTweaks.setAdapter(adapter);

        tabRam.setOnClickListener(v -> showPage(0));
        tabTweaks.setOnClickListener(v -> showPage(1));
        tabSystem.setOnClickListener(v -> showPage(2));

        showPage(0);
        refreshAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(refresher);
        refreshAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refresher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refresher);
    }

    private View createRamPage() {
        ScrollView scroll = new ScrollView(this);
        scroll.setVisibility(View.VISIBLE);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(16), dp(16), dp(16), dp(16));

        CardView card = createCard();

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(18), dp(18), dp(18), dp(18));

        TextView ramTitle = createSectionTitle("RAM");
        ramInfo = createSectionText("");
        storageInfo = createSectionText("");

        inner.addView(ramTitle);
        inner.addView(ramInfo);
        inner.addView(storageInfo);
        card.addView(inner);

        body.addView(card);
        scroll.addView(body);

        return scroll;
    }

    private RecyclerView createTweaksPage() {
        RecyclerView rv = new RecyclerView(this);
        rv.setVisibility(View.GONE);
        rv.setPadding(dp(16), dp(16), dp(16), dp(16));
        rv.setClipToPadding(false);
        return rv;
    }

    private View createSystemPage() {
        ScrollView scroll = new ScrollView(this);
        scroll.setVisibility(View.GONE);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(16), dp(16), dp(16), dp(16));

        CardView card = createCard();

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(dp(18), dp(18), dp(18), dp(18));

        TextView sysTitle = createSectionTitle("SYSTEM");
        systemInfo = createSectionText("");

        inner.addView(sysTitle);
        inner.addView(systemInfo);
        card.addView(inner);

        body.addView(card);
        scroll.addView(body);

        return scroll;
    }

    private CardView createCard() {
        CardView card = new CardView(this);
        card.setUseCompatPadding(true);
        card.setRadius(dp(24));
        card.setCardBackgroundColor(Color.parseColor("#151515"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(lp);
        return card;
    }

    private TextView createSectionTitle(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.WHITE);
        view.setTextSize(18f);
        view.setTypeface(view.getTypeface(), android.graphics.Typeface.BOLD);
        return view;
    }

    private TextView createSectionText(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.parseColor("#A0A0A0"));
        view.setTextSize(14f);
        view.setPadding(0, dp(10), 0, 0);
        return view;
    }

    private TextView createTab(String text, boolean selected) {
        TextView tab = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
        );
        tab.setLayoutParams(lp);
        tab.setGravity(Gravity.CENTER);
        tab.setText(text);
        tab.setTextSize(12f);
        tab.setTextColor(selected ? Color.WHITE : Color.parseColor("#A0A0A0"));
        return tab;
    }

    private void showPage(int index) {
        pageRam.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        recyclerTweaks.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        pageSystem.setVisibility(index == 2 ? View.VISIBLE : View.GONE);

        updateTab(tabRam, index == 0);
        updateTab(tabTweaks, index == 1);
        updateTab(tabSystem, index == 2);
    }

    private void updateTab(TextView tab, boolean selected) {
        if (tab == null) {
            return;
        }

        tab.setTextColor(selected ? Color.WHITE : Color.parseColor("#A0A0A0"));
        tab.animate()
                .scaleX(selected ? 1.05f : 1f)
                .scaleY(selected ? 1.05f : 1f)
                .setDuration(180)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void refreshAll() {
        refreshRam();
        refreshTweaks();
        refreshSystem();
    }

    private void refreshRam() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
        }

        long totalRam = memoryInfo.totalMem;
        long freeRam = memoryInfo.availMem;
        long usedRam = totalRam - freeRam;

        long totalStorage = getStorageTotal();
        long freeStorage = getStorageFree();
        long usedStorage = totalStorage - freeStorage;

        ramInfo.setText(
                "Total: " + formatBytes(totalRam) + "\n" +
                "Usada: " + formatBytes(usedRam) + "\n" +
                "Libre: " + formatBytes(freeRam)
        );

        storageInfo.setText(
                "Almacenamiento total: " + formatBytes(totalStorage) + "\n" +
                "Almacenamiento usado: " + formatBytes(usedStorage) + "\n" +
                "Almacenamiento libre: " + formatBytes(freeStorage)
        );
    }

    private void refreshTweaks() {
        List<File> files = TwsRepository.listTweaks(this);
        adapter.setFiles(files);

        statusInfo.setText("Tweaks instalados: " + files.size());
    }

    private void refreshSystem() {
        systemInfo.setText(
                "Modelo: " + Build.MANUFACTURER + " " + Build.MODEL + "\n" +
                "Android: " + Build.VERSION.RELEASE + "\n" +
                "SDK: " + Build.VERSION.SDK_INT + "\n" +
                "ABI: " + (Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "desconocida") + "\n" +
                "Idioma: " + Locale.getDefault().getLanguage()
        );
    }

    private long getStorageTotal() {
        StatFs statFs = new StatFs(getFilesDir().getAbsolutePath());
        return statFs.getBlockSizeLong() * statFs.getBlockCountLong();
    }

    private long getStorageFree() {
        StatFs statFs = new StatFs(getFilesDir().getAbsolutePath());
        return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
    }

    private String formatBytes(long bytes) {
        double value = bytes;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unit = 0;

        while (value >= 1024 && unit < units.length - 1) {
            value /= 1024;
            unit++;
        }

        return String.format(Locale.US, "%.2f %s", value, units[unit]);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}
