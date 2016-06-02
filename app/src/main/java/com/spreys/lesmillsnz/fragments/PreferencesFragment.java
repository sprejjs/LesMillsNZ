package com.spreys.lesmillsnz.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.spreys.lesmillsnz.R;
import com.spreys.lesmillsnz.activities.MainActivity;
import com.spreys.lesmillsnz.data.DataContract.ClubEntry;
import com.spreys.lesmillsnz.model.Club;
import com.spreys.lesmillsnz.sync.GetGymsService;
import com.spreys.lesmillsnz.sync.LesMillsSyncAdapter;
import com.spreys.lesmillsnz.utils.DisplayFragmentInterface;
import com.spreys.lesmillsnz.utils.NetworkUtils;
import com.spreys.lesmillsnz.utils.PreferenceProvider;
import com.spreys.lesmillsnz.utils.TabManager;
import com.spreys.lesmillsnz.utils.UiUtils;
import com.spreys.lesmillsnz.utils.UrlProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with Android Studio
 *
 * @author vladspreys
 *         Date: 27/09/14
 *         Project: Les Mills NZ
 *         Contact by: vlad@spreys.com
 */
public class PreferencesFragment extends Fragment implements DisplayFragmentInterface,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FINE_LOCATION_REQUEST = 41245;

    private ArrayList<Club> mClubs;
    private static final String TAG = PreferencesFragment.class.getSimpleName();
    private static final int CLUB_LOADER = 0;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private Club selectedClub;
    private boolean data_displayed = false;
    private LocationListener locationListener;

    Marker mCurrLocationMarker;
    MapView mapView;
    GoogleMap map;

    public static final String[] CLUB_COLUMNS = {
            ClubEntry.TABLE_NAME + "." + ClubEntry._ID,
            ClubEntry.COLUMN_LAT,
            ClubEntry.COLUMN_LONG,
            ClubEntry.COLUMN_NAME
    };

    public static final int COL_CLUB_ID = 0;
    public static final int COL_CLUB_LAT = 1;
    public static final int COL_CLUB_LONG = 2;
    public static final int COL_CLUB_NAME = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preferences_fragment_layout, container, false);
        getLoaderManager().initLoader(CLUB_LOADER, null, this);
        initialiseMap(savedInstanceState);

        //Save preferences onClick listener
        Button button = (Button) rootView.findViewById(R.id.save_preferences);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });

        filter = new IntentFilter();
        filter.addAction(GetGymsService.ACTION_GYMS_UPDATED);
        filter.addAction(GetGymsService.ACTION_UNABLE_TO_GET_GYMS);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleGetGymsServiceAction(intent);
            }
        };

        //Read NFC tag
        rootView.findViewById(R.id.btnNfcRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
                if (mNfcAdapter == null) {
                    // Stop here, we definitely need NFC
                    Toast.makeText(getActivity(), "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!mNfcAdapter.isEnabled()) {
                    Toast.makeText(getActivity(), "NFC is disabled.", Toast.LENGTH_SHORT).show();
                }

                ((MainActivity)getActivity()).handleIntent(getActivity().getIntent());
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * Checks the actions from the GetGymsService.
     *      ACTION_GYMS_UPDATED - completed the UI with new information
     *      ACTION_UNABLE_TO_GET_GYMS if data is not displayed, then request an immediate sync
     * @param intent Intent received from the service
     */
    private void handleGetGymsServiceAction(Intent intent) {
        String action = intent.getAction();
        UiUtils.HideLoadingSpinner(getActivity());

        if (action.equals(GetGymsService.ACTION_GYMS_UPDATED)) {
            if (!data_displayed) {
                displayData();
            }
        } else if (action.equals(GetGymsService.ACTION_UNABLE_TO_GET_GYMS)) {
            if (!data_displayed) {
                LesMillsSyncAdapter.syncImmediately(getActivity());
            }
        }
    }

    private void displayData() {
        //Drop down menu
        Spinner gymSelectionDropDown = (Spinner) getActivity().findViewById(R.id.gymDropdown);
        gymSelectionDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int row_id, long l) {
                selectClub(row_id);
                int newPreferredClub = row_id + 1;

                int oldPreferredClub = PreferenceProvider.GetPreferredClub(getActivity());
                if (oldPreferredClub != newPreferredClub) {
                    PreferenceProvider.SavePreferredClub(newPreferredClub, getActivity());
                    LesMillsSyncAdapter.syncImmediately(getActivity());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterViewICS) {
            }
        });
        List<String> list = new ArrayList<>();

        for (Club club : mClubs) {
            list.add(club.getName());
        }

        if (list.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gymSelectionDropDown.setAdapter(dataAdapter);
            data_displayed = true;
        }

        gymSelectionDropDown.setSelection(PreferenceProvider.GetPreferredClub(getActivity()) - 1);
    }

    /**
     * Gets a club with the specified ID from the list of the clubs and stores it as a selected
     * club, clears the map, navigates to the selected club and drops the new market on the map
     * @param gym_id unique identifier of the club
     */
    private void selectClub(int gym_id) {
        this.selectedClub = mClubs.get(gym_id);

        //Drop the new market if map is already initialised
        if (map != null) {
            map.clear();
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(selectedClub.getLatitude(), selectedClub.getLongitude()))
                    .title(selectedClub.getName()));

            //Navigate to the new location
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(selectedClub.getLatitude(), selectedClub.getLongitude()),
                    15
            );
            map.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume();
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
    }

    private void displayMyLocation(){
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = map.addMarker(markerOptions);

                //move map camera
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(11));
                locationListener = null;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //This method is intentionally left empty
            }

            @Override
            public void onProviderEnabled(String provider) {
                //This method is intentionally left empty
            }

            @Override
            public void onProviderDisabled(String provider) {
                //This method is intentionally left empty
            }
        };
    }

    private void initialiseMap(final Bundle bundle) {
        int checkGooglePlayServices =    GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            // google play services is missing!!!!
            /* Returns status code indicating whether there was an error.
            Can be one of following in ConnectionResult: SUCCESS, SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED, SERVICE_INVALID.
            */
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, getActivity(), 1122).show();
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                MapsInitializer.initialize(getActivity());
                mapView = (MapView) getActivity().findViewById(R.id.map_view);
                mapView.onCreate(bundle);
                mapView.onResume();
                // Gets to GoogleMap from the MapView and does initialization stuff
                map = mapView.getMap();

                if (map != null) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        displayMyLocation();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST);
                        }
                    }

                    // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
                    MapsInitializer.initialize(getActivity());
                } else {
                    mapView.setVisibility(View.GONE);
                }

                //Select the currently selected gym on the map
                int selectedPos = ((Spinner)getActivity().findViewById(R.id.gymDropdown))
                        .getSelectedItemPosition();

                if(selectedPos != -1){
                    selectClub(selectedPos);
                }
            }
        }, 1000);
    }

    /**
     * Saves the currently selected gym into the shared preferences
     * and makes the Registration service call to the API
     */
    public void savePreferences() {
        String email = ((TextView)getActivity().findViewById(R.id.users_email)).getText().toString();

        //Update shared preferences
        if(email != null && !email.equals("")){
            PreferenceProvider.SaveEmailAddress(email, getActivity());
        }

        //Navigate to the first tab
        ((TabManager)getActivity()).openTab(0);

        //Make network call
        if(email == null || email.equals("")){
            return;
        }

        final HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("gym_id", String.valueOf(this.selectedClub.getId()));

        new Thread(new Runnable() {
            public void run() {
                try {
                    NetworkUtils.MakeHttpRequest(
                            UrlProvider.GetRegistrationUrl(),
                            NetworkUtils.REQUEST_POST,
                            params
                    );
                } catch (Exception ex){
                    Log.d(TAG, "Unable to register");
                }
            }
        }).start();

    }

    @Override
    public void fragmentDisplayed() {
        String email = PreferenceProvider.RetrieveEmailAddress(getActivity());
        ((TextView)getActivity().findViewById(R.id.users_email)).setText(email);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                ClubEntry.CONTENT_URI,
                CLUB_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mClubs = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            double latitude = cursor.getDouble(COL_CLUB_LAT);
            double longitude = cursor.getDouble(COL_CLUB_LONG);
            String name = cursor.getString(COL_CLUB_NAME);
            int clubId = cursor.getInt(COL_CLUB_ID);

            Club tempClub = new Club(clubId, latitude, longitude, name);
            mClubs.add(tempClub);

            cursor.moveToNext();
        }

        if(mClubs.size() > 0){
            displayData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        getLoaderManager().restartLoader(CLUB_LOADER, null, this);
    }
}
