package com.orionn.optimizer.tweaks;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.orionn.optimizer.core.TweakItem;
import com.orionn.optimizer.core.TwsParser;
import com.orionn.optimizer.databinding.ItemTweakBinding;
import com.orionn.optimizer.utils.BlurUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TweakAdapter extends RecyclerView.Adapter<TweakAdapter.VH> {

    public interface OnTweakChanged {
        void onChanged(TweakItem item, boolean enabled, String result);
    }

    private final Context context;
    private final List<TweakItem> items = new ArrayList<>();
    private final SharedPreferences prefs;
    private final OnTweakChanged callback;

    public TweakAdapter(Context context, OnTweakChanged callback) {
        this.context = context;
        this.callback = callback;
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
        ItemTweakBinding binding = ItemTweakBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TweakItem item = items.get(position);

        holder.binding.tweakName.setText(item.name);
        holder.binding.tweakDesc.setText(item.description);
        holder.binding.tweakMeta.setText("Riesgo: " + item.risk + "   Archivo: " + item.sourceFileName);
        holder.binding.tweakSwitch.setChecked(item.enabled);

        BlurUtils.applySoftBlur(holder.binding.getRoot());

        holder.binding.tweakSwitch.setOnCheckedChangeListener(null);
        holder.binding.tweakSwitch.setChecked(item.enabled);
        holder.binding.tweakSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.enabled = isChecked;
            prefs.edit().putBoolean(item.sourceFileName, isChecked).apply();

            String result = ActionOnJava.applyTweak(item);
            if (callback != null) {
                callback.onChanged(item, isChecked, result);
            } else {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static final class VH extends RecyclerView.ViewHolder {
        final ItemTweakBinding binding;

        VH(ItemTweakBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
