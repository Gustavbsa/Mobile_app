package com.example.onmyway;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link runningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class runningFragment extends Fragment {
    private RecyclerView recyclerView;
    RunningRecyclerViewAdapter adapter; // Create Object of the Adapter class
    String s1 [], s2[], s3[];

    DatabaseReference mbase;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public runningFragment() {
        // Required empty public constructor
    }

    public static runningFragment newInstance(String param1, String param2) {
        runningFragment fragment = new runningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_running, container, false);


        mbase = FirebaseDatabase.getInstance().getReference("running");


        recyclerView = view.findViewById(R.id.runningList);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<runningFirebase> options
                = new FirebaseRecyclerOptions.Builder<runningFirebase>()
                .setQuery(mbase, runningFirebase.class)
                .build();

        adapter = new RunningRecyclerViewAdapter(options);
        recyclerView.setAdapter(adapter);

        return view;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

}