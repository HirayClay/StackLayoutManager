package com.hirayclay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

/**
 * Created by CJJ on 2017/3/7.
 */

public class StackAdapter extends RecyclerView.Adapter<StackAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<String> datas;
    private Context context;
    private List<String> imageUrls = Arrays.asList(
            "http://img.hb.aicdn.com/10dd7b6eb9ca02a55e915a068924058e72f7b3353a40d-ZkO3ko_fw658",
            "http://img.hb.aicdn.com/a3a995b26bd7d58ccc164eafc6ab902601984728a3101-S2H0lQ_fw658",
            "http://pic4.nipic.com/20091124/3789537_153149003980_2.jpg",
            "http://img.hb.aicdn.com/4ba573e93c6fe178db6730ba05f0176466056dbe14905-ly0Z43_fw658",
            "http://img.hb.aicdn.com/4bc60d00aa3184f1f98e418df6fb6abc447dc814226ef-ZtS8hB_fw658",
            "http://img.hb.aicdn.com/d9a48c272914c5253eceac26c51a56a26f4e50d048ba7-IJsbou_fw658",
            "http://img.hb.aicdn.com/03d474bbe20efb7df9aed4541ace70b53b53c70bdfe3-8djYVv_fw658",
            "http://img.hb.aicdn.com/004cddd40519846281526b4b25fbdea36b31d01e190dd-7zlmuG_fw658",
            "http://img.hb.aicdn.com/a58eda8a9a2a3f30f0a694c2702e1aba71d97d616d34f-rqv6FA_fw658",
            "http://img.hb.aicdn.com/41ff5110b4ecdec24e14f767e83c1659c2e8a180f3df-QqUAgk_fw658",
            "http://img.hb.aicdn.com/80006ed344ed8dee7ad8142b3c4dc1b51cbf207c3097a-SGiu5P_fw658"
    );

    public StackAdapter(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            context = parent.getContext();
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(R.layout.item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(datas.get(holder.getAdapterPosition()));
        Glide.with(context).load(imageUrls.get(position)).into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView cover;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.name);
            cover = (ImageView) itemView.findViewById(R.id.cover);
        }
    }
}
