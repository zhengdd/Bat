package com.dongdong.animal.bat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.dongdong.animal.bat.model.WifiEntity;

import java.util.List;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    private List<WifiEntity> list;
    private View.OnClickListener listener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_wifi_details, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WifiEntity entity = getWifiEntity(position);

        holder.itemView.setTag(position);
        if (listener != null) {
            holder.itemView.setOnClickListener(listener);
        }

        holder.tvWifiIcon.setBackgroundResource(entity.getIconId());
        if (entity.isSafe()) {
            holder.tvSafeIcon.setVisibility(View.VISIBLE);
        } else {
            holder.tvSafeIcon.setVisibility(View.GONE);
        }

        holder.tvWifiName.setText(entity.getSSID());
        String status = "";
        if (!TextUtils.isEmpty(entity.getStatus())) {
            status = entity.getStatus();
        } else {
            status = entity.getSafeType();
        }
        holder.tvWifiStatus.setText(status);

    }

    private WifiEntity getWifiEntity(int i) {
        if (list != null) {
            return list.get(i);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWifiIcon;
        private TextView tvSafeIcon;
        private TextView tvWifiName;
        private TextView tvWifiStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWifiIcon = $(itemView, R.id.tvWifiIcon);
            tvSafeIcon = $(itemView, R.id.tvSafeIcon);
            tvWifiName = $(itemView, R.id.tvWifiName);
            tvWifiStatus = $(itemView, R.id.tvWifiStatus);

        }


        private <T> T $(View view, int id) {
            return (T) view.findViewById(id);
        }
    }

    public void setList(List<WifiEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
