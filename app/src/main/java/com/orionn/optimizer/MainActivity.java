package com.orionn.optimizer;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
    private RecyclerView pageTweaks;
    private View pageSystem;

    private TextView ramInfo;
    private TextView storageInfo;
    private TextView systemInfo;

    private LinearLayout tabRam;
    private LinearLayout tabTweaks;
    private LinearLayout tabSystem;

    private TweakAdapter adapter;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwsAssetInstaller.install(this);
        setContentView(R.layout.activity_main);

        pageRam = findViewById(R.id.pageRam);
        pageTweaks = findViewById(R.id.recyclerTweaks);
        pageSystem = findViewById(R.id.pageSystem);

        ramInfo = findViewById(R.id.ramInfo);
        storageInfo = findViewById(R.id.storageInfo);
        systemInfo = findViewById(R.id.systemInfo);

        tabRam = findViewById(R.id.tabRam);
        tabTweaks = findViewById(R.id.tabTweaks);
        tabSystem = findViewById(R.id.tabSystem);

        BlurUtils.applySoftBlur(findViewById(R.id.blurLayer));
        animateEntry(findViewById(R.id.headerCard));

        pageTweaks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TweakAdapter(this, (item, enabled, result) ->
                Toast.makeText(this, item.name + ": " + result, Toast.LENGTH_LONG).show()
        );
        pageTweaks.setAdapter(adapter);

        tabRam.setOnClickListener(v -> showPage(0));
        tabTweaks.setOnClickListener(v -> showPage(1));
        tabSystem.setOnClickListener(v -> showPage(2));

        showPage(0);
        refreshAll();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshRam();
                refreshTweaks();
                refreshSystem();
                handler.postDelayed(this, 1500);
            }
        }, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAll();
    }

    private void refreshAll() {
        refreshRam();
        refreshTweaks();
        refreshSystem();
    }

    private void showPage(int index) {
        pageRam.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        pageTweaks.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        pageSystem.setVisibility(index == 2 ? View.VISIBLE : View.GONE);

        setSelected(tabRam, index == 0);
        setSelected(tabTweaks, index == 1);
        setSelected(tabSystem, index == 2);
    }

    private void setSelected(View view, boolean selected) {
        if (view != null) {
            view.setAlpha(selected ? 1f : 0.65f);
            view.animate()
                    .scaleX(selected ? 1.05f : 1f)
                    .scaleY(selected ? 1.05f : 1f)
                    .setDuration(180)
                    .start();
        }
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

    private void animateEntry(View view) {
        if (view == null) {
            return;
        }

        view.setAlpha(0f);
        view.setTranslationY(28f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(380)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }
}
// trigger build
