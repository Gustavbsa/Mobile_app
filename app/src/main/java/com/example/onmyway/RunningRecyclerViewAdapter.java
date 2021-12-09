package com.example.onmyway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicReference;


public class RunningRecyclerViewAdapter extends FirebaseRecyclerAdapter<
        runningFirebase, RunningRecyclerViewAdapter.myViewHolder> {

    private DatabaseReference runningDatabase = FirebaseDatabase.getInstance().getReference("running");
    private DatabaseReference savedDatabase = FirebaseDatabase.getInstance().getReference("saved");
    private static final String TAG = runningFragment.class.getName();

    public static Boolean cancel = false;
    AtomicReference<Boolean> flagSaved = new AtomicReference<>(false);
    RunningRecyclerViewAdapter(@NonNull FirebaseRecyclerOptions<runningFirebase> options){
        super(options);
    }

    @NonNull
    @Override
    public RunningRecyclerViewAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_running_list_item, parent, false);
        return new RunningRecyclerViewAdapter.myViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull runningFirebase model) {

        holder.name.setText(model.getName());

        holder.address.setText(model.getAddress());

        holder.distance.setText(model.getDistance());

        holder.saved.setOnClickListener(view -> { // When the user press the bookmark
            if(flagSaved.get()){
                view.setBackgroundResource(R.drawable.ic_not_saved);
                flagSaved.set(false);
            }else{
                view.setBackgroundResource(R.drawable.ic_saved);
                flagSaved.set(true);
                final DatabaseReference itemRef = getRef(position);
                final String myKey = itemRef.getKey();

                //Adding the journey from running to saved
                runningDatabase.child(myKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }else{
                            String name = task.getResult().child("name").getValue(String.class);
                            String number = task.getResult().child("number").getValue(String.class);
                            String address = task.getResult().child("address").getValue(String.class);
                            String message = task.getResult().child("message").getValue(String.class);
                            String distance = task.getResult().child("distance").getValue(String.class);
                            runningFirebase firebase = new runningFirebase(name,number,address,message,distance);
                            savedDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    } else {
                                        Long amount = task.getResult().getChildrenCount()+1;
                                        //Making sure that the new ID is unique using the loops below
                                        Boolean notSame = false;
                                        if(amount>1){
                                        while(true){
                                            for (DataSnapshot ds : task.getResult().getChildren()) {
                                                if (amount!=Long.valueOf(ds.getKey())){
                                                    notSame=true;
                                                }else {
                                                    notSame=false;
                                                    break;
                                                }
                                            }
                                            if(notSame){
                                                break;
                                            }else{
                                                amount++;
                                            }
                                        }}
                                        savedDatabase.child(String.valueOf(amount)).setValue(firebase);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() { //When the user press the X icon
            @Override
            public void onClick(View v) {
                // the background thread that is running is stopped early.
                cancel=true;
                // remove journey from running
                final DatabaseReference itemRef = getRef(position);
                final String myKey = itemRef.getKey();
                Log.d("what is the position", ":"+myKey);
                runningDatabase.child(myKey).removeValue();
            }
        });

    }
    //the background thread in setUpFragment calls this method in case of the user cancelling the journey
    public static Boolean getCancel(){
        return cancel;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, distance;
        ImageButton saved,delete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameEx);
            address = itemView.findViewById(R.id.adress);
            distance = itemView.findViewById(R.id.distance);

            saved = itemView.findViewById(R.id.savedRBtn);
            delete = itemView.findViewById(R.id.deleteRBtn);
        }
    }
}
