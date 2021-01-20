package com.cut.campusclinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private List<Record> appList;
    private ItemClicked activity;

    public interface ItemClicked
    {
        void onItemClicked(int position);
    }
    public RecordAdapter(Context activity, List<Record> appList)
    {
        this.appList = appList;
        this.activity = (ItemClicked) activity;
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_recrow_diag, tv_recrow_dr, tv_recrow_pres;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_recrow_diag = itemView.findViewById(R.id.tv_recrow_diag);
            tv_recrow_dr = itemView.findViewById(R.id.tv_recrow_dr);
            tv_recrow_pres = itemView.findViewById(R.id.tv_recrow_pres);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onItemClicked(appList.indexOf((Record) view.getTag()));
                }
            });
        }
    }
    @NonNull
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_row, parent,false);
        return new RecordAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(appList.get(position));
        holder.tv_recrow_diag.setText(appList.get(position).getDiagnosis());
        holder.tv_recrow_pres.setText(appList.get(position).getPrescription());
        holder.tv_recrow_dr.setText(appList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
