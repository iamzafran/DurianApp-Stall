package com.durianapp.durianapp_stall;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.durianapp.durianapp_stall.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amer S Alkatheri on 14-Jun-17.
 */

public class Search extends Fragment {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView mRVFish;
    private AdapterDurianSearch mAdapter;
    View rootView;
    Spinner sSpinner, bSpinner;
    List<DataDurianSearch> data, newData;
    String var1, var2;
    Button mShowDialogSearch;

    SearchView search;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Search");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get the context of the HomeScreen Activity
        rootView = inflater.inflate(R.layout.search, container, false);

        //Setup and Handover data to RecyclerView
        mRVFish = (RecyclerView) rootView.findViewById(R.id.fishPriceList);
        newData = new ArrayList<>();
        /*
        *******************************************************************
         */

        mShowDialogSearch = (Button) rootView.findViewById(R.id.btnSearchSetting);
        mShowDialogSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_search, null);
                mBuilder.setTitle("Search Options");

                final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinnerSearch);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.dSweetness));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);

                final Spinner mSpinner2 = (Spinner) mView.findViewById(R.id.spinnerSearch2);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.dBitterness));
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner2.setAdapter(adapter2);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showMessageInSearch(mSpinner.getSelectedItem().toString(), mSpinner2.getSelectedItem().toString());
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });


        /*
        *******************************************************************
         */

        search = (SearchView) rootView.findViewById(R.id.searchView25);
        search.setQueryHint("Start typing to search ...");
        search.setIconifiedByDefault(false);


        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                //Toast.makeText(activity, String.valueOf(hasFocus),Toast.LENGTH_SHORT).show();
            }
        });

        search.setOnQueryTextListener(new OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1)
                {
                    mRVFish.setVisibility(rootView.VISIBLE);
                    new AsyncFetch(newText).execute();
                }
                else
                {
                    mRVFish.setVisibility(rootView.INVISIBLE);
                }
                return false;
            }
        });
        return rootView;
    }

    public void showMessageInSearch(String one, String two) {
        var1 = one; var2 = two;

        if (data == null) {
            Toast.makeText(getContext(), "Start typing to search ...", Toast.LENGTH_LONG).show();
        }
        else {
            newData.clear();
            for (DataDurianSearch names : data) {
                if (var1.equals("Any") &&
                        var2.equals("Any")) {
                    //Nothing Happen
                    newData.add(names);
                } else if (var1.equals("Any") && names.getDurianBitterness().equals(var2)) {
                    newData.add(names);
                } else if (names.getDurianSweetness().equals(var1) && var2.equals("Any")) {
                    newData.add(names);
                } else if (names.getDurianSweetness().equals(var1) &&
                        names.getDurianBitterness().equals(var2)) {
                    newData.add(names);
                }
            }
        }
        mAdapter = new AdapterDurianSearch(getContext(), newData);
        mRVFish.setAdapter(mAdapter);


        //Toast.makeText(getContext(), var1 + " -IN SEARCH- " + var2, Toast.LENGTH_LONG).show();
    }




    /*
    *******************************************************************
     */


    // Create class AsyncFetch
    public class AsyncFetch extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(getContext());
        HttpURLConnection conn;
        URL url = null;
        String searchQuery;

        public AsyncFetch(String searchQuery){
            this.searchQuery=searchQuery;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(Config.DURIAN_SEARCH_URL);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput to true as we send and recieve data
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // add parameter to our above url
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("searchQuery", searchQuery);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    // Pass data to onPostExecute method
                    return (result.toString());
                } else {
                    return("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            pdLoading.dismiss();
            data = new ArrayList<>();

            pdLoading.dismiss();
            if (result.equals("no rows")) {
                Toast.makeText(getContext(), "No Results found for entered query", Toast.LENGTH_LONG).show();
            } else {

                try {
                    JSONArray jArray = new JSONArray(result);

                    // Extract data from json and store into ArrayList as class objects
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        DataDurianSearch fishData = new DataDurianSearch();
                        fishData.durianId = json_data.getInt("id");
                        fishData.durianName = json_data.getString("name");
                        fishData.durianSweetness = json_data.getString("sweetness");
                        fishData.durianBitterness = json_data.getString("bitterness");
                        fishData.durianImage = json_data.getString("img_1");
                        data.add(fishData);
                    }

                    mAdapter = new AdapterDurianSearch(getContext(), data);
                    mRVFish.setAdapter(mAdapter);
                    mRVFish.setLayoutManager(new LinearLayoutManager(getContext()));

                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), result.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
