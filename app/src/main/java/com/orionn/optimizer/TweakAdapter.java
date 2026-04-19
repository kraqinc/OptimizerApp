package com.orionn.optimizer;

import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class TweakAdapter extends RecyclerView.Adapter<TweakAdapter.VH> {

    List<String> data;

    public TweakAdapter(List<String> d) {
        data = d;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView name;
        Switch toggle;

        public VH(View v) {
            super(v);
            name = v.findViewById(R.id.tweakName);
            toggle = v.findViewById(R.id.toggle);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_tweak, p, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH h, int i) {
        h.name.setText(data.get(i));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
