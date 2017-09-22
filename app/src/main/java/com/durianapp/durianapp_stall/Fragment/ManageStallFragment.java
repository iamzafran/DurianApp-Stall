package com.durianapp.durianapp_stall.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.durianapp.durianapp_stall.Fragment.DialogFragment.CameraOrGalleryDialog;
import com.durianapp.durianapp_stall.Model.DurianAppSharedPreferences;
import com.durianapp.durianapp_stall.Model.Stall;
import com.durianapp.durianapp_stall.R;
import com.durianapp.durianapp_stall.SelectDurianActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 6/20/2017.
 */

public class ManageStallFragment extends Fragment {
    private static final String TAG = ManageStallFragment.class.getSimpleName() ;
    private static final int WRITE_EXTERNAL_PERMISSION = 0;
    private static final int READ_EXTERNAL_PERMISSION = 1;
    private static final int LOCATION_PERMISSION = 2 ;
    private static final int REQUEST_NEW_OR_EXISTING = 3;
    private static final String NEW_OR_EXISTING = "REQUEST_NEW_OR_EXISTING";
    private static final int CAMERA_PERMISSION = 4;
    private static final int REQUEST_PHOTO = 5;
    private static final int REQUEST_TAKE_PHOTO = 6;
    private static final String GET_STALL_INFO = "http://durianapp.esy.es/getManageStallInfo.php?firebaseID=";
    private static final String STORE_UPDATE_URL = "http://durianapp.esy.es/updateStall.php" ;
    private static final String UPDATE_STALL_IMAGE_URL = "http://durianapp.esy.es/updateStallProfileUrl.php";
    private ImageView mStallImageView;
    private ImageButton mAddImageImageButton;

    @NotEmpty
    private EditText mStallNameEditText;

    @NotEmpty
    private EditText mStallPhoneNumberEditText;
    private Button mFindAddressButton;


    @NotEmpty
    private EditText mAddressEditText;

    private EditText mCityEditText;

    private EditText mPostCodeEditText;

    private EditText mStateEditText;

    @NotEmpty
    private EditText mLocalityEditText;
    @NotEmpty
    private Button mNextButton;




    private Uri mStallImageUri;
    private File mStallPhotoFile;
    private GoogleApiClient mClient;
    private Location mLocation;
    private String mStallname;
    private String mStallphonenumber;
    private String mStallAddress;
    private String mStallLocality;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private boolean isImageAdded = false;
    private int mStoreID;
    private Validator mValidator;
    private String mStallCity;
    private String mStallPostcode;
    private String mStallState;
    private Stall mStall;


    public static ManageStallFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ManageStallFragment fragment = new ManageStallFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        //findLocation();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);


                            } else if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                            }
                        } else {

                        }


                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }


                }).build();





        //check and request for storage permissions
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION);
            }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


            }

            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_PERMISSION);

            }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Log.v(TAG,"Read Permission Granted");
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.manage_stall_fragment_layout,container,false);
        mStallImageView = (ImageView) v.findViewById(R.id.stall_image_view);
        mAddImageImageButton = (ImageButton) v.findViewById(R.id.add_image_image_button);
        mStallNameEditText = (EditText) v.findViewById(R.id.stall_name_edit_text);
        mStallPhoneNumberEditText = (EditText) v.findViewById(R.id.stall_phone_number_edit_text);
        mFindAddressButton = (Button) v.findViewById(R.id.stall_find_address_button);
        mAddressEditText = (EditText) v.findViewById(R.id.stall_address_edit_text);
        mCityEditText = (EditText) v.findViewById(R.id.stall_city_edit_text);
        mLocalityEditText = (EditText) v.findViewById(R.id.locality_edit_text);
        mPostCodeEditText = (EditText) v.findViewById(R.id.stall_postcode_edit_text);
        mStateEditText = (EditText) v.findViewById(R.id.stall_state_edit_text);
        mNextButton = (Button) v.findViewById(R.id.next_button);

        mAddImageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Allow users to choose image from camera or gallery
                cameraOrGalleryDialog();
            }
        });


        mFindAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                updateStallLocation();
            }
        });



        mStallNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStallname = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mStallPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStallphonenumber = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStallAddress = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mCityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStallCity = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLocalityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStallLocality = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mPostCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStallPostcode = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mStateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStallState = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateStallTask().execute();
            }
        });



        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        new GetStallInfoTask().execute();


    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.manage_store,menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                stallLogOut();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void stallLogOut() {
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
    }

    private void getStallLocation(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("store_location");

        GeoFire georef = new GeoFire(reference);

        georef.getLocation(mStall.getId() + "", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                mFindAddressButton.setText("lat:"+location.latitude+", lon:"+location.longitude);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cameraOrGalleryDialog() {
        FragmentManager manager = getFragmentManager();
        CameraOrGalleryDialog newImageOrCurrentImageDialog = CameraOrGalleryDialog.newInstance();
        newImageOrCurrentImageDialog.setTargetFragment(ManageStallFragment.this,REQUEST_NEW_OR_EXISTING);
        newImageOrCurrentImageDialog.show(manager,NEW_OR_EXISTING);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Permission granted");
                    //takePhoto();
                }
                break;
            case WRITE_EXTERNAL_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Permission Storage Granted");
                }
                break;
            case READ_EXTERNAL_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Write Permission Storage Granted");
                }
            case LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Permission granted");

                }
                break;

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK)
        {

            if(requestCode==REQUEST_NEW_OR_EXISTING){
                boolean isNew;

                isNew = data.getBooleanExtra(CameraOrGalleryDialog.EXTRA_NEW_OR_EXISTING,false);
                if(isNew){
                    //new image
                    startCamera();



                }else{
                    //existing image
                    chooseImage();

                }
            }else if(requestCode==REQUEST_TAKE_PHOTO){

                isImageAdded = true;
                Picasso.with(getActivity()).load(mStallImageUri).fit().centerCrop().into(mStallImageView);
            }else if(requestCode==REQUEST_PHOTO){
                isImageAdded = true;
                mStallImageUri = data.getData();
                Log.v(TAG,"Gallery : "+mStallImageUri.getPath());
                Picasso.with(getActivity()).load(mStallImageUri).fit().centerCrop().into(mStallImageView);
            }
        }


    }


    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_PHOTO);
    }



    private void startCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            try {
                mStallPhotoFile = createImageFile();
            } catch (IOException e) {

                Toast.makeText(getActivity(), "Unable To Create File", Toast.LENGTH_SHORT).show();
            }

            if (mStallPhotoFile != null) {
                Log.v(TAG, mStallPhotoFile.getPath());
                Uri uri = Uri.fromFile(mStallPhotoFile);
                mStallImageUri = uri;
                Log.v(TAG,mStallImageUri.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                    } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                    }
                } else {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }


        }
    }

    private File createImageFile() throws IOException {
        File externalFilesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_"+".jpg";



        // Save a file: path for use with ACTION_VIEW intents

        if(externalFilesDir==null)
        {
            return null;
        }
        return new File(externalFilesDir,imageFileName);
    }



    private void updateStallLocation() {
        checkLocationPermission();

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG,"location:" + location);
                    mLocation = location;

                    mFindAddressButton.setText("lat:"+mLocation.getLatitude()+", lon:"+mLocation.getLongitude());
                }


            });
        }catch(SecurityException e){

            Toast.makeText(getActivity(), "Location permission missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);


            } else if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

            }
        } else {

        }

    }


    private class GetStallInfoTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            getStoreInfo();
            return null;
        }

        private void getStoreInfo(){

            String firebaseID = mFirebaseUser.getUid();
            OkHttpClient client = new OkHttpClient();
            String request_url = GET_STALL_INFO+""+firebaseID;
            Request request = new Request.Builder().url(request_url).build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful())
                {

                    String responseBody = response.body().string();

                    Log.v(TAG,responseBody);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    parseStall(jsonObject);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void parseStall(JSONObject jsonObject)throws JSONException{

            mStall = new Stall();
            mStall.setId(jsonObject.getInt("stall_id"));
            mStall.setName(jsonObject.getString("stall_name"));
            mStall.setAddress(jsonObject.getString("stall_address"));
            mStall.setCity(jsonObject.getString("stall_city"));
            mStall.setLocality(jsonObject.getString("stall_locality"));
            mStall.setPhone(jsonObject.getString("stall_phone"));
            mStall.setPostcode(jsonObject.getString("postcode"));
            mStall.setState(jsonObject.getString("stall_state"));
            mStall.setPictureUrl(jsonObject.getString("picture_url"));


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();
        }
    }

    private void updateUI() {

        if(mStall.getPictureUrl()!=null){
            Uri uri = Uri.parse(mStall.getPictureUrl());
            Picasso.with(getActivity()).load(uri).fit().centerInside().into(mStallImageView);
        }

        mStallNameEditText.setText(mStall.getName());
        mAddressEditText.setText(mStall.getAddress());
        mPostCodeEditText.setText(mStall.getPostcode());
        mCityEditText.setText(mStall.getCity());
        mStateEditText.setText(mStall.getState());
        mStallPhoneNumberEditText.setText(mStall.getPhone());
        mLocalityEditText.setText(mStall.getLocality());

        getStallLocation();

    }
    
    private class UpdateStallTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            updateStall();
            return null;
        }
        
        
        private void updateStall(){
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("stall_name",mStallname)
                    .add("stall_phone",mStallphonenumber)
                    .add("stall_address",mStallAddress)
                    .add("stall_city",mStallCity)
                    .add("stall_locality",mStallLocality)
                    .add("postcode",mStallPostcode)
                    .add("stall_state",mStallState)
                    .add("firebase_id",mFirebaseUser.getUid())
                    .build();

            Request request = new Request.Builder().url(STORE_UPDATE_URL).post(requestBody).build();


            try {
                Call call = client.newCall(request);
                Response registerResponse =  call.execute();
                addLocationToFirebase();

                if(registerResponse.isSuccessful()){


                    if(isImageAdded){
                        uploadImage();
                    }else {
                        //selectDurian();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectDurian() {
        Intent i = SelectDurianActivity.newIntent(getActivity());
        startActivity(i);
    }

    private void uploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReferenceFromUrl("gs://durianapp-3602b.appspot.com");
        StorageReference stallReference = storageReference.child("DurianStall/"+"IMG_"+mFirebaseUser.getUid()+".jpg");


        UploadTask uploadTask = stallReference.putFile(mStallImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to upload picture", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.v(TAG,downloadUrl.toString());
                updateProfileURL(downloadUrl);

            }
        });
    }

    private void updateProfileURL(Uri downloadUrl) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("url",downloadUrl.toString())
                .add("store_id",mStall.getId()+"")
                .build();

        Request request = new Request.Builder().url(UPDATE_STALL_IMAGE_URL).post(requestBody).build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.v(TAG,response.body().toString());
                    selectDurian();
                }
            }
        });

    }

    private void addLocationToFirebase()
    {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("store_location");
        GeoFire stall = new GeoFire(database);

        if(mLocation!=null){
            stall.setLocation(mStall.getId()+"",new GeoLocation(mLocation.getLatitude(),mLocation.getLongitude()));

        }
    }



}
