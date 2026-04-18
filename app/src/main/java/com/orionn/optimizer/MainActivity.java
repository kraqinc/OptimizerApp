package com.orionn.optimizer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import com.orionn.optimizer.core.TWSRepository;
import com.orionn.optimizer.tweaks.TweakEngine;

import java.io.File;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Button gaming = new Button(this);
        gaming.setText("Gaming Mode");

        Button battery = new Button(this);
        battery.setText("Battery Mode");

        gaming.setOnClickListener(v -> {
            File file = TWSRepository.getTweak("gaming-mode");
            TweakEngine.execute(file);
        });

        battery.setOnClickListener(v -> {
            File file = TWSRepository.getTweak("battery-mode");
            TweakEngine.execute(file);
        });

        layout.addView(gaming);
        layout.addView(battery);

        setContentView(layout);
    }
}
