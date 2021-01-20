package com.cut.campusclinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {
    private List<Appointments> appList;
    private ItemClicked activity;
    public interface ItemClicked
    {
        void onItemClicked(int position);
    }
    public AppointmentsAdapter(Context activity, List<Appointments> appList)
    {
        this.appList = appList;
        this.activity = (ItemClicked) activity;
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_ClientNames, tv_DoctorNames, tv_Date;
        ImageView ivAppLogo,ivPending;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_ClientNames = itemView.findViewById(R.id.tv_rowClientNames);
            tv_DoctorNames = itemView.findViewById(R.id.tv_rowDoctorNames);
            tv_Date = itemView.findViewById(R.id.tv_rowDate);
            ivPending = itemView.findViewById(R.id.ivPending);
            ivAppLogo = itemView.findViewById(R.id.ivAppLogo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onItemClicked(appList.indexOf((Appointments)view.getTag()));
                }
            });
        }
    }
    @NonNull
    @Override
    public AppointmentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_row, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(appList.get(position));
        holder.tv_ClientNames.setText(appList.get(position).getNames());
        holder.tv_DoctorNames.setText(appList.get(position).getEmail());
        holder.tv_Date.setText(appList.get(position).getDate());
        if(appList.get(position).isConfirm())
            holder.ivPending.setImageResource(R.drawable.ic_approve);
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
