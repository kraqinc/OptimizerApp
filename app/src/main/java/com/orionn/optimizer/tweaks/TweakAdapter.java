package com.orionn.optimizer.tweaks;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orionn.optimizer.R;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweak, parent, false);
        return new VH(view);
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
        holder.itemView.setScaleX(item.enabled ? 1.01f : 1f);
        holder.itemView.setScaleY(item.enabled ? 1.01f : 1f);

        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.enabled = isChecked;
            prefs.edit().putBoolean(item.sourceFileName, isChecked).apply();

            String result = ActionOnJava.applyTweak(item, isChecked);
            if (listener != null) {
                listener.onChanged(item, isChecked, result);
            }
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

        VH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tweakName);
            desc = itemView.findViewById(R.id.tweakDesc);
            meta = itemView.findViewById(R.id.tweakMeta);
            toggle = itemView.findViewById(R.id.tweakSwitch);
        }
    }
}
