package com.example.onmyway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SavedRecyclerViewAdapter extends FirebaseRecyclerAdapter<
        runningFirebase, SavedRecyclerViewAdapter.savedViewHolder>{

    private DatabaseReference savedDatabase = FirebaseDatabase.getInstance().getReference("saved");
    private DatabaseReference runningDatabase = FirebaseDatabase.getInstance().getReference("running");

    public SavedRecyclerViewAdapter(
            @NonNull FirebaseRecyclerOptions<runningFirebase> options)
    {
        super(options);
    }

    @NonNull
    @Override
    public SavedRecyclerViewAdapter.savedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_saved_list_item, parent, false);
        return new SavedRecyclerViewAdapter.savedViewHolder(view);
    }
    @Override
    protected void onBindViewHolder(@NonNull savedViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull runningFirebase model) {
        Log.d("test",position+"");
        holder.name.setText(model.getName());

        holder.address.setText(model.getAddress());

        holder.distance.setText(model.getDistance());

        holder.playBtn.setOnClickListener(view -> { //When the user press the play button
            final DatabaseReference itemRef = getRef(position);
            final String myKey = itemRef.getKey();

            savedDatabase.child(myKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        String name = task.getResult().child("name").getValue(String.class);
                        String number = task.getResult().child("number").getValue(String.class);
                        String address = task.getResult().child("address").getValue(String.class);
                        String message = task.getResult().child("message").getValue(String.class);
                        String distance = task.getResult().child("distance").getValue(String.class);
                        //send the info above to setUpFragment
                        Bundle bundle = new Bundle();
                        bundle.putString("arg_name",name);
                        bundle.putString("arg_number",number);
                        bundle.putString("arg_address",address);
                        bundle.putString("arg_message",message);
                        bundle.putString("arg_distance",distance);
                        Navigation.findNavController(view).navigate(R.id.action_savedFragment2_to_setUpFragment, bundle);
                    }
                }
            });
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sometimes when deleting the last item it crashes.
                final DatabaseReference itemRef = getRef(position);
                final String myKey = itemRef.getKey();
                Log.d("what is the position", ":"+myKey);

                savedDatabase.child(myKey).removeValue();
            }
        });
    }


    public class savedViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, distance;
        ImageButton playBtn, deleteBtn;

        public savedViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.savedName);
            address = itemView.findViewById(R.id.savedAdress);
            distance = itemView.findViewById(R.id.savedDistance);

            playBtn = itemView.findViewById(R.id.playBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

}
