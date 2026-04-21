package com.orionn.optimizer.tweaks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.orionn.optimizer.core.TweakItem;
import com.orionn.optimizer.core.TwsParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TweakAdapter extends RecyclerView.Adapter<TweakAdapter.VH> {

    public interface Listener {
        void onChanged(TweakItem item, boolean enabled, String result);
    }

    private final Context context;
    private final SharedPreferences prefs;
    private final Listener listener;
    private final List<TweakItem> items = new ArrayList<>();

    public TweakAdapter(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
        this.prefs = context.getSharedPreferences("optimizer_tweaks", Context.MODE_PRIVATE);
    }

    public void setFiles(List<File> files) {
        items.clear();

        if (files != null) {
            for (File file : files) {
                TweakItem item = TwsParser.parse(file);
                item.enabled = prefs.getBoolean(item.sourceFileName, false);
                items.add(item);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView card = new CardView(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = dp(12);
        card.setLayoutParams(lp);
        card.setUseCompatPadding(true);
        card.setRadius(dp(20));
        card.setCardBackgroundColor(Color.parseColor("#151515"));

        LinearLayout root = new LinearLayout(parent.getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.setLayoutParams(new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView name = new TextView(parent.getContext());
        name.setTextColor(Color.WHITE);
        name.setTextSize(17f);
        name.setText("Tweak");
        name.setTypeface(name.getTypeface(), android.graphics.Typeface.BOLD);

        TextView desc = new TextView(parent.getContext());
        desc.setTextColor(Color.parseColor("#B0B0B0"));
        desc.setTextSize(13f);
        desc.setText("Descripción");

        TextView meta = new TextView(parent.getContext());
        meta.setTextColor(Color.parseColor("#7C4DFF"));
        meta.setTextSize(11f);
        meta.setPadding(0, dp(6), 0, 0);

        Switch toggle = new Switch(parent.getContext());
        LinearLayout.LayoutParams toggleLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        toggleLp.gravity = Gravity.END;
        toggleLp.topMargin = dp(10);
        toggle.setLayoutParams(toggleLp);

        root.addView(name);
        root.addView(desc);
        root.addView(meta);
        root.addView(toggle);
        card.addView(root);

        return new VH(card, name, desc, meta, toggle);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TweakItem item = items.get(position);

        holder.name.setText(item.name);
        holder.desc.setText(item.description == null || item.description.isEmpty() ? "Sin descripción" : item.description);
        holder.meta.setText("Riesgo: " + item.risk + "   Archivo: " + item.sourceFileName);

        holder.toggle.setOnCheckedChangeListener(null);
        holder.toggle.setChecked(item.enabled);

        holder.itemView.setAlpha(item.enabled ? 1f : 0.88f);

        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.enabled = isChecked;
            prefs.edit().putBoolean(item.sourceFileName, isChecked).apply();

            String result = ActionOnJava.applyTweak(item, isChecked);
            if (listener != null) {
                listener.onChanged(item, isChecked, result);
            }

            holder.itemView.setAlpha(isChecked ? 1f : 0.88f);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class VH extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView desc;
        final TextView meta;
        final Switch toggle;

        VH(View itemView, TextView name, TextView desc, TextView meta, Switch toggle) {
            super(itemView);
            this.name = name;
            this.desc = desc;
            this.meta = meta;
            this.toggle = toggle;
        }
    }

    private int dp(int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}
