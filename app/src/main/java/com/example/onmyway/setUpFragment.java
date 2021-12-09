package com.example.onmyway;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.provider.ContactsContract;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.google.android.gms.location.LocationRequest;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link setUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class setUpFragment<lock> extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    contactAdapter adapter;

    DatabaseReference mbase; // Create object of the
    // Firebase Realtime Database
    FirebaseDatabase database;
    EditText regName, regNumber, regAddress, regMessage;
    Spinner regDistance;

    private FusedLocationProviderClient mFusedLocationClient; //Location

    public volatile Float distanceBetween = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "arg_name";
    private static final String ARG_NUMBER = "arg_number";
    private static final String ARG_ADDRESS = "arg_address";
    private static final String ARG_MESSAGE = "arg_message";
    private static final String ARG_DISTANCE = "arg_distance";

    // TODO: Rename and change types of parameters
    private String changeName;
    private String changeNumber;
    private String changeAddress;
    private String changeMessage;
    private String changeDistance;

    public setUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment setUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static setUpFragment newInstance(String param1, String param2, String param3, String param4, String param5) {
        setUpFragment fragment = new setUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, param1);
        args.putString(ARG_NUMBER, param2);
        args.putString(ARG_ADDRESS, param3);
        args.putString(ARG_MESSAGE, param4);
        args.putString(ARG_DISTANCE, param5);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            changeName = getArguments().getString(ARG_NAME);
            changeNumber = getArguments().getString(ARG_NUMBER);
            changeAddress = getArguments().getString(ARG_ADDRESS);
            changeMessage = getArguments().getString(ARG_MESSAGE);
            changeDistance = getArguments().getString(ARG_DISTANCE);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    private void getIncomingInt() {
        //send over information from adapter
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_up, container, false);
        Spinner spinnerNumbers = view.findViewById(R.id.kmBtnSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumbers.setAdapter(adapter);

        //If the bundle contained values
        regName = view.findViewById(R.id.exPerson);
        regNumber = view.findViewById(R.id.exPhoneNumber);
        regAddress = view.findViewById(R.id.address);
        regMessage = view.findViewById(R.id.exMessageTxt);
        regDistance = view.findViewById(R.id.kmBtnSpinner);

        regName.setText(changeName);
        regNumber.setText(changeNumber);
        regAddress.setText(changeAddress);
        regMessage.setText(changeMessage);
        if (changeDistance == null) {
            regDistance.setSelection(1);
        } else {
            String distanceIndex = changeDistance.replaceAll("[^\\d.]", "");
            regDistance.setSelection((Integer.valueOf(distanceIndex) - 1));
        }


        ImageButton startBtnSetUp = view.findViewById(R.id.startBtnSetUp);
        startBtnSetUp.setOnClickListener(this);

        Button contactBook = view.findViewById(R.id.phoneBookBtn);
        contactBook.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerViewContact);
        //get permissions
        checkPermissionContact();
        getPermissionLocation();
        requestPermissionPhone();
        requestPermissionSms();
        return view;
    }
    //Permission
    private void checkPermissionContact() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }
    //Permission
    private void getPermissionLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            //Log.d("Permission Location", "got permission");
        }
    }
    //Permission
    private void requestPermissionPhone() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_PHONE_STATE},100);
    }
    //Permission
    private void requestPermissionSms() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},100);
    }

    /*Contact part*/
    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range")
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                @SuppressLint("Range")
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                Cursor phoneCursor = getContext().getContentResolver().query(
                        uriPhone, null, selection, new String[]{id}, null);

                if (phoneCursor.moveToNext()) {
                    @SuppressLint("Range")
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    ContactModel model = new ContactModel();
                    model.setName(name);
                    model.setNumber(number);
                    arrayList.add(model);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new contactAdapter(getActivity(), arrayList, new contactAdapter.ItemClickListener() {
            @Override
            public void onItemClick(ContactModel contactModel) {
                regName = requireView().findViewById(R.id.exPerson);
                regNumber = requireView().findViewById(R.id.exPhoneNumber);
                regName.setText(contactModel.getName());
                regNumber.setText(contactModel.getNumber());
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 & grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContactList();
        }else{
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            checkPermissionContact();
        }
    }
    /*End of contact*/

    /*Position part*/
    public synchronized void getPosition(String address) {
        GoogleApiAvailability gaa = new GoogleApiAvailability();
        if (ConnectionResult.SUCCESS == gaa.isGooglePlayServicesAvailable(getContext())) {
            // use Google play services
            //get users location
            if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    getPermissionLocation();
                    return;
            }
            //Log.d("permission location","granted");

            Executor executor = Executors.newSingleThreadExecutor();

            mFusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,null)
                    .addOnSuccessListener(executor, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            //Log.d(TAG, "Play Location Services Location " + location.getLatitude() + ", " + location.getLongitude());
                            checkDistance(location,address);
                        } else {
                            //Log.d(TAG, "Play Location Services Location did not return a location ");
                        }
                    }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.d(TAG,"fail");
                        }
                    }).addOnCompleteListener(executor, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                        //Log.d(TAG,"complete and the distanceBetween is: "+distanceBetween);
                    }
                    });
            //If the device does not have Google play services
        } else {
            //Log.d(TAG,"else");
            LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            //check if the GPS is available
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gpsEnabled){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null, Executors.newSingleThreadExecutor(),
                            new Consumer<Location>() {
                                @Override
                                public void accept(Location location) {
                                    if (location != null) {
                                        // Logic to handle location object
                                        Log.d(TAG, "Location manager Location " + location.getLatitude() + ", " + location.getLongitude());
                                        checkDistance(location,address);
                                    } else {
                                        Log.d(TAG, "Location manager Location did not return a location ");
                                    }
                                }
                            });
                }
            }
        }
        //Log.d(TAG, "end of method");
    }
    public synchronized void checkDistance(Location location, String destination){
       ArrayList endDestination = getLocationFromAddress(destination);
       Location finalDestination = new Location("point B");
       finalDestination.setLatitude((Double) endDestination.get(0));
       finalDestination.setLongitude((Double)endDestination.get(1));
       Float distance = location.distanceTo(finalDestination) / 1000; // in km
       distanceBetween = distance; // The distance between the two locations
    }
    //change String address to Location
    public synchronized ArrayList getLocationFromAddress(String address){
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList;
        ArrayList locationList = new ArrayList();
        address = address+" Aberdeen";
        try {
            addressList = geocoder.getFromLocationName(address,1);
            //Log.d(TAG,": "+addressList);
            if (addressList==null){
                return null;
            }
            Address location = addressList.get(0);
            location.getLatitude();
            location.getLongitude();
            locationList.add(location.getLatitude());
            locationList.add(location.getLongitude());
            return locationList;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*End of position part*/

    /*When the user starts a journey*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtnSetUp:

                String name = regName.getText().toString();
                String number = regNumber.getText().toString();
                String address = regAddress.getText().toString();
                String message = regMessage.getText().toString();
                String distance = regDistance.getSelectedItem().toString()+" km";

                //Adding the journey into the Firebase
                if(!name.isEmpty() && !number.isEmpty() && !address.isEmpty() && !message.isEmpty()) {
                Navigation.findNavController(v).navigate(R.id.action_setUpFragment_to_runningFragment2);
                database = FirebaseDatabase.getInstance();
                mbase = database.getReference("running");

                    Log.d("message","name: "+name+" number: "+number+" address: "+address
                    +" message: "+message);
                    runningFirebase firebase = new runningFirebase(name, number, address, message, distance);

                    mbase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                Long amount = task.getResult().getChildrenCount() + 1;
                                //Making sure that the new ID is unique using the loops below
                                Boolean notSame = false;
                                if (amount > 1) {
                                    while (true) {
                                        for (DataSnapshot ds : task.getResult().getChildren()) {
                                            if (amount != Long.valueOf(ds.getKey())) {
                                                notSame = true;
                                            } else {
                                                notSame = false;
                                                break;
                                            }
                                        }
                                        if (notSame) {
                                            break;
                                        } else {
                                            amount++;
                                        }
                                    }
                                }
                                mbase.child(String.valueOf(amount)).setValue(firebase);
                            }
                        }
                    });
                    String distanceString = String.valueOf(regDistance.getSelectedItem());
                    getPermissionLocation();

                    //Start the journey in the background
                    background(address,Float.parseFloat(distanceString),number,message);
                    break;
                }else{
                    final Dialog dialog = new Dialog(getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog);
                    Button closeBtn = dialog.findViewById(R.id.dialogBtn);
                    closeBtn.setOnClickListener(view -> {
                        dialog.dismiss();
                    });
                    dialog.show();
                    break;
                }
                //When the user press the button to show his/her own contacts
            case R.id.phoneBookBtn:
                RecyclerView contactRecycler = requireView().findViewById(R.id.recyclerViewContact);
                contactRecycler.setVisibility(View.VISIBLE);
            default:
                break;
        }
    }
    /*Background starts*/
    public void background(String address, Float whenToSendMessage, String number, String message){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    getPosition(address);
                    Log.d(TAG,"distanceBetween: "+distanceBetween+" Address: "+address);
                    while(distanceBetween==null);
                    Log.d(TAG, "when should the message be sent: " + whenToSendMessage + " what is the distance: " + distanceBetween);
                    if (whenToSendMessage >= distanceBetween) {
                        Log.d(TAG, "check distance: " + whenToSendMessage + " : " + distanceBetween);
                        Integer sent = null;
                        // The process of sending a message is starting
                        sent = checkPermissionSms(number,message);
                        while(sent == null); //waiting until the methods are finished
                        // if sent = 1 then the message has been sent
                        // if sent = 0 the the message has not been sent
                        Log.d(TAG,"value of sent: "+sent);
                        return;
                    }
                }
            }
        });
    }

    private Integer checkPermissionSms(String number, String message) {
        Log.d(TAG,"entering checkPermissionSms");
        Integer sent;
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED){
            //permission granted
            sendMessage(number,message);
            sent = 1;
        }else{
            requestPermissionSms();
            sent = 0;
        }
        return sent;
    }

    private void sendMessage(String number, String message) {
       SmsManager smsManager = SmsManager.getDefault();
       smsManager.sendTextMessage(number,null,message,null,null);
       Log.d(TAG, "sent message containing message: "+message+" to number: "+number);
       getActivity().runOnUiThread(new Runnable() {
           @Override
           public void run() {
               Toast.makeText(getActivity(),"SMS sent successfully",Toast.LENGTH_LONG).show();
           }
       });

    }
    /*Background ends*/
}