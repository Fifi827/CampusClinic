package com.cut.campusclinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAppointmentAdapter extends RecyclerView.Adapter<BookAppointmentAdapter.ViewHolder> {
    private List<Doctor> appList;
    private ItemClicked activity;
    private Context context;
    public interface ItemClicked
    {
        void onItemClicked(int position);
    }
    public BookAppointmentAdapter(Context activity, List<Doctor> appList)
    {
        this.appList = appList;
        this.context = activity;
        this.activity = (ItemClicked) activity;

    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_appoint_qualifications, tv_appoint_experience, tv_drNames, tv_DrAge,tv_DrCell,
                tv_DrSpecialty;
        ImageView iv_DrPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_appoint_qualifications = itemView.findViewById(R.id.tv_appoint_qualifications);
            tv_appoint_experience = itemView.findViewById(R.id.tv_appoint_experience);
            tv_drNames = itemView.findViewById(R.id.tv_drNames);
            tv_DrAge = itemView.findViewById(R.id.tv_DrAge);
            tv_DrCell = itemView.findViewById(R.id.tv_DrCell);
            tv_DrSpecialty = itemView.findViewById(R.id.tv_DrSpecialty);
            iv_DrPicture = itemView.findViewById(R.id.iv_DrPicture);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onItemClicked(appList.indexOf((Doctor) view.getTag()));
                }
            });
        }
    }
    @NonNull
    @Override
    public BookAppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appoint_demo, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAppointmentAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(appList.get(position));
        String tempValue = "None";


            holder.tv_drNames.setText(appList.get(position).getUserFirstName() + " "+ appList.get(position)
                    .getUserLastName());
           if(appList.get(position).getQualifications().equals("default"))
            {
                holder.tv_appoint_qualifications.setText(tempValue);
                holder.tv_appoint_experience.setText(tempValue);
            }
           else
           {
               holder.tv_appoint_qualifications.setText(appList.get(position).getQualifications());
               holder.tv_appoint_experience.setText(appList.get(position).getExperience());
           }
            holder.tv_DrAge.setText(appList.get(position).getAge());
            holder.tv_DrCell.setText(appList.get(position).getContactNumber());

            if(appList.get(position).getSpecialty().equals("default"))
            {
                holder.tv_DrSpecialty.setText(tempValue);
            }
            else
            {
                holder.tv_DrSpecialty.setText(appList.get(position).getSpecialty());
            }
            if(!appList.get(position).getPhotoUrl().equals("default"))
                Picasso.with(context).load(appList.get(position).getPhotoUrl()).into(holder.iv_DrPicture);

    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
