package com.cut.campusclinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.ViewHolder> {

    private List<Appointments> appList;
    private ItemClicked activity;
    public interface ItemClicked
    {
        void onItemClicked(int position);
    }
    public PopupAdapter(Context activity, List<Appointments> appList)
    {
        this.appList = appList;
        this.activity = (ItemClicked) activity;
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView popAppTime, popAppPatient;
        Button btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            popAppTime = itemView.findViewById(R.id.pop_appTime);
            popAppPatient = itemView.findViewById(R.id.pop_appPatient);
            btnDetails = itemView.findViewById(R.id.pop_btnDetails);

            btnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onItemClicked(appList.indexOf((Appointments)view.getTag()));
                    //activity.onItemClicked(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public PopupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_row_layout, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PopupAdapter.ViewHolder holder, int position) {
        holder.btnDetails.setTag(appList.get(position));
        holder.popAppTime.setText(appList.get(position).getTime());
        holder.popAppPatient.setText(appList.get(position).getNames());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
