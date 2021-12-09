package com.example.onmyway;

import android.app.Activity;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class contactAdapter extends RecyclerView.Adapter<contactAdapter.viewHolder> {

    Activity activity;
    ArrayList<ContactModel> arrayList;
    private ItemClickListener mClickListener;

    public contactAdapter(Activity activity, ArrayList<ContactModel> arrayList, ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.mClickListener = itemClickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.contacts_list_item, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ContactModel model = arrayList.get(position);

        holder.contactName.setText(model.getName());
        holder.contactNumber.setText(model.getNumber());

        holder.itemView.setOnClickListener(view -> {
            mClickListener.onItemClick(arrayList.get(position));
        });
    }
    public interface ItemClickListener{
        void onItemClick(ContactModel contactModel);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView contactName, contactNumber;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.contactName);
            contactNumber = itemView.findViewById(R.id.contactNumber);

        }
    }
}
