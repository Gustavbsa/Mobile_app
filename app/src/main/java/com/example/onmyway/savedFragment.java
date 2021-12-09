package com.example.onmyway;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link savedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class savedFragment extends Fragment{

    private RecyclerView recyclerView;
    SavedRecyclerViewAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the
    // Firebase Realtime Database
    String s1 [], s2[], s3[];
    ArrayList<runningFirebase> arrayList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public savedFragment() {
        // Required empty public constructor
    }

    public static savedFragment newInstance(String param1, String param2) {
        savedFragment fragment = new savedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList = new ArrayList<>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_saved, container, false);

        mbase = FirebaseDatabase.getInstance().getReference("saved");


        recyclerView = view.findViewById(R.id.savedList);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<runningFirebase> options
                = new FirebaseRecyclerOptions.Builder<runningFirebase>()
                .setQuery(mbase, runningFirebase.class)
                .build();

        adapter = new SavedRecyclerViewAdapter(options);
        recyclerView.setAdapter(adapter);

        return  view;
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
