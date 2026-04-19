package com.orionn.optimizer;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.card.MaterialCardView;
import com.orionn.optimizer.core.TwsAssetInstaller;
import com.orionn.optimizer.core.TwsRepository;
import com.orionn.optimizer.tweaks.TweakAdapter;
import com.orionn.optimizer.utils.BlurUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private View pageRam;
    private View pageTweaks;
    private View pageSystem;

    private TextView ramInfo;
    private TextView storageInfo;
    private TextView systemInfo;
    private androidx.recyclerview.widget.RecyclerView recyclerTweaks;
    private TweakAdapter adapter;

    private LinearLayout tabRam;
    private LinearLayout tabTweaks;
    private LinearLayout tabSystem;

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

        recyclerTweaks = findViewById(R.id.recyclerTweaks);
        recyclerTweaks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TweakAdapter(this, (item, enabled, result) ->
                Toast.makeText(this, item.name + ": " + result, Toast.LENGTH_LONG).show()
        );
        recyclerTweaks.setAdapter(adapter);

        tabRam = findViewById(R.id.tabRam);
        tabTweaks = findViewById(R.id.tabTweaks);
        tabSystem = findViewById(R.id.tabSystem);

        tabRam.setOnClickListener(v -> showPage(0));
        tabTweaks.setOnClickListener(v -> showPage(1));
        tabSystem.setOnClickListener(v -> showPage(2));

        BlurUtils.applySoftBlur(findViewById(R.id.blurLayer));
        animateIn(findViewById(R.id.headerCard));

        refreshRam();
        refreshTweaks();
        refreshSystem();

        showPage(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRam();
        refreshTweaks();
        refreshSystem();
    }

    private void showPage(int index) {
        pageRam.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        recyclerTweaks.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        pageSystem.setVisibility(index == 2 ? View.VISIBLE : View.GONE);

        tabRam.setActivated(index == 0);
        tabTweaks.setActivated(index == 1);
        tabSystem.setActivated(index == 2);
    }

    private void refreshRam() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            am.getMemoryInfo(memoryInfo);
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

    private void animateIn(View view) {
        if (view == null) {
            return;
        }
        view.setAlpha(0f);
        view.setTranslationY(30f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(420)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }
}
