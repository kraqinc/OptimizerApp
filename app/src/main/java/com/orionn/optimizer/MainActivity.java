package com.orionn.optimizer;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.orionn.optimizer.core.TWSAssetInstaller;
import com.orionn.optimizer.core.TWSParser;
import com.orionn.optimizer.core.TWSProfile;
import com.orionn.optimizer.core.TWSRepository;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView ramPanel;
    private TextView tweaksPanel;
    private TextView systemPanel;

    private LinearLayout ramPage;
    private LinearLayout tweaksPage;
    private LinearLayout systemPage;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TWSAssetInstaller.installBundledTweaks(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        ramPage = createPage();
        tweaksPage = createPage();
        systemPage = createPage();

        ramPanel = createPanelText();
        tweaksPanel = createPanelText();
        systemPanel = createPanelText();

        ramPage.addView(ramPanel);
        tweaksPage.addView(tweaksPanel);
        systemPage.addView(systemPanel);

        content.addView(ramPage);
        content.addView(tweaksPage);
        content.addView(systemPage);

        LinearLayout bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.CENTER);
        bottomBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        bottomBar.setPadding(16, 16, 16, 16);

        Button btnRam = new Button(this);
        btnRam.setText("RAM");

        Button btnTweaks = new Button(this);
        btnTweaks.setText("TWEAKS");

        Button btnSystem = new Button(this);
        btnSystem.setText("SISTEMA");

        LinearLayout.LayoutParams navParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        navParams.setMargins(8, 0, 8, 0);

        btnRam.setLayoutParams(navParams);
        btnTweaks.setLayoutParams(navParams);
        btnSystem.setLayoutParams(navParams);

        btnRam.setOnClickListener(v -> showPage(0));
        btnTweaks.setOnClickListener(v -> showPage(1));
        btnSystem.setOnClickListener(v -> showPage(2));

        bottomBar.addView(btnRam);
        bottomBar.addView(btnTweaks);
        bottomBar.addView(btnSystem);

        root.addView(content);
        root.addView(bottomBar);

        setContentView(root);

        showPage(0);
        refreshAll();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshAll();
                handler.postDelayed(this, 1500);
            }
        }, 1500);
    }

    private LinearLayout createPage() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(32, 32, 32, 32);
        page.setVisibility(View.GONE);

        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        scroll.addView(inner);
        page.addView(scroll);

        return page;
    }

    private TextView createPanelText() {
        TextView text = new TextView(this);
        text.setTextSize(15f);
        text.setPadding(8, 8, 8, 24);
        return text;
    }

    private void showPage(int index) {
        ramPage.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        tweaksPage.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        systemPage.setVisibility(index == 2 ? View.VISIBLE : View.GONE);
    }

    private void refreshAll() {
        refreshRam();
        refreshTweaks();
        refreshSystem();
    }

    private void refreshRam() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
        }

        long total = memoryInfo.totalMem;
        long avail = memoryInfo.availMem;
        long used = total - avail;

        long dataTotal = getTotalStorage();
        long dataFree = getFreeStorage();
        long dataUsed = dataTotal - dataFree;

        ramPanel.setText(
                "RAM\n\n" +
                "Total: " + formatBytes(total) + "\n" +
                "Usada: " + formatBytes(used) + "\n" +
                "Libre: " + formatBytes(avail) + "\n\n" +
                "Almacenamiento\n\n" +
                "Total: " + formatBytes(dataTotal) + "\n" +
                "Usado: " + formatBytes(dataUsed) + "\n" +
                "Libre: " + formatBytes(dataFree) + "\n"
        );
    }

    private void refreshTweaks() {
        StringBuilder builder = new StringBuilder();

        builder.append("TWEAKS INSTALADOS\n\n");

        List<File> files = TWSRepository.listTweaks(this);
        if (files.isEmpty()) {
            builder.append("No hay tweaks instalados.\n");
        } else {
            for (File file : files) {
                TWSProfile profile = TWSParser.parse(file);
                builder.append("Nombre: ").append(profile.name).append('\n');
                builder.append("Riesgo: ").append(profile.risk).append('\n');
                builder.append("Acciones: ").append(profile.actions.size()).append('\n');
                builder.append("Archivo: ").append(file.getName()).append('\n');
                builder.append('\n');
            }
        }

        builder.append("LECTOR .TWS\n\n");
        File defaultFile = TWSRepository.getTweakFile(this, "gaming-mode");
        if (defaultFile.exists()) {
            TWSProfile profile = TWSParser.parse(defaultFile);
            builder.append("Nombre: ").append(profile.name).append('\n');
            builder.append("Descripción: ").append(profile.description).append('\n');
            builder.append("Riesgo: ").append(profile.risk).append('\n');
            builder.append("Compatibilidad mínima: Android ").append(profile.minAndroid).append('\n');
            builder.append("Shizuku: ").append(profile.requiresShizuku ? "Sí" : "No").append('\n');
            builder.append("Acciones:\n");
            for (String action : profile.actions) {
                builder.append(" - ").append(action).append('\n');
            }
        } else {
            builder.append("gaming-mode.tws no existe todavía.\n");
        }

        tweaksPanel.setText(builder.toString());
    }

    private void refreshSystem() {
        StringBuilder builder = new StringBuilder();

        builder.append("SISTEMA\n\n");
        builder.append("Modelo: ").append(Build.MANUFACTURER).append(' ').append(Build.MODEL).append('\n');
        builder.append("Android: ").append(Build.VERSION.RELEASE).append('\n');
        builder.append("SDK: ").append(Build.VERSION.SDK_INT).append('\n');
        builder.append("ABI: ").append(Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "desconocida").append('\n');
        builder.append('\n');
        builder.append("Idioma: ").append(Locale.getDefault().getLanguage()).append('\n');

        systemPanel.setText(builder.toString());
    }

    private long getTotalStorage() {
        StatFs statFs = new StatFs(getFilesDir().getAbsolutePath());
        long blockSize = statFs.getBlockSizeLong();
        long blockCount = statFs.getBlockCountLong();
        return blockSize * blockCount;
    }

    private long getFreeStorage() {
        StatFs statFs = new StatFs(getFilesDir().getAbsolutePath());
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        return blockSize * availableBlocks;
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
}
