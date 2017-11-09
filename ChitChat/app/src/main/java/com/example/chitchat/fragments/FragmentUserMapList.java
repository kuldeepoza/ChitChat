package com.example.chitchat.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.chitchat.R;
import com.example.chitchat.activities.MapChatActivity;
import com.example.chitchat.activities.UsersActivity;
import com.example.chitchat.models.UserBean;
import com.example.chitchat.utils.Constants;
import com.example.chitchat.utils.Util;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by KD on 23-Jun-17.
 */
public class FragmentUserMapList extends Fragment  implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener,GeoQueryEventListener, GoogleMap.OnCameraChangeListener,GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleMap mMap;
    GeoFire geoFire;
    Location location;
    LocationManager locationManager;
    List<UserBean> array;
    View v;
    UserBean bean;
    View customMarkerView;
    CircleImageView markerImageView;
    SupportMapFragment mapFragment;
    FragmentManager fmanager;
    Fragment fragment;
    FirebaseUser user;
    GeoQuery query;
    DatabaseReference databaseReference;
    Double moveLat,moveLang;
    Map<String,Marker> markerMap;
    DatabaseReference myRef;
     FirebaseDatabase mFirebaseInstance;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    int m=1;
    View customInfo;
    PendingResult<LocationSettingsResult> result;
    String status;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {
            v = inflater.inflate(R.layout.fragment_user_map_list, container, false);
        } catch (InflateException e) {
        }
        if (Build.VERSION.SDK_INT < 21) {
            fmanager = getChildFragmentManager();
            fragment = fmanager.findFragmentById(R.id.map);
            mapFragment = (SupportMapFragment) fragment;
        } else {
            mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        user=FirebaseAuth.getInstance().getCurrentUser();
            databaseReference= FirebaseDatabase.getInstance().getReference();
            mFirebaseInstance = FirebaseDatabase.getInstance();
            myRef = mFirebaseInstance.getReference();
            array = new ArrayList<>();
            this.markerMap=new HashMap<String,Marker>();
            geoFire = new GeoFire(databaseReference.child("location"));
        return v;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }
    public Location getLocation() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
                if (locationManager != null) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        setGeoLocation(user.getUid(), location.getLatitude(), location.getLongitude());
                        query = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 1);
                        query.addGeoQueryEventListener(this);
                    } else {
                        Toast.makeText(getContext(), "Location  not find", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "LocationManager  not find", Toast.LENGTH_SHORT).show();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    getActivity(),
                                    Constants.REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        switch (requestCode)
        {
            case Constants.REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        Toast.makeText(getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private void setGeoLocation(String key,Double lat, Double lang) {
        geoFire.setLocation(key, new GeoLocation(lat, lang), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }
        });
    }
    private void infoWindowCall() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                customInfo = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_custom_infowindow, null);
                TextView name = (TextView) customInfo.findViewById(R.id.tvInfoWinName);
                TextView area = (TextView) customInfo.findViewById(R.id.tvInfoWinArea);
                TextView mob = (TextView) customInfo.findViewById(R.id.tvInfoWinMob);
                ImageView iv = (ImageView) customInfo.findViewById(R.id.ivCustomInfo);
                List<String> elephantList = Arrays.asList(marker.getSnippet().split(","));
                name.setText(marker.getTitle());
                area.setText(elephantList.get(0));
                mob.setText(elephantList.get(1));
                status=elephantList.get(3);
                if(status!=null) {
                    if (status.equals("true")) {
                        iv.setImageResource(R.drawable.ic_user_online);
                    } else {
                        iv.setImageResource(R.drawable.ic_user_offline);
                    }
                    infoWindowCall();
                }
                return customInfo;
            }
        });
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        infoWindowCall();
        return false;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        List<String> elephantList = Arrays.asList(marker.getSnippet().split(","));
        Intent intent=new Intent(getContext(),MapChatActivity.class);
        intent.putExtra("key",elephantList.get(2));
        if(!elephantList.get(2).equals(user.getUid())) {
            startActivity(intent);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if(query!=null) {
            query.removeAllListeners();
            for (Marker marker : this.markerMap.values()) {
                marker.remove();
            }
        }
        this.markerMap.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        getLocation();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onKeyEntered(final String key, final GeoLocation location) {
        m=1;
        myRef.child("users").child(key).child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                    bean = new UserBean();
                    bean = dataSnapshot.getValue(UserBean.class);
                    bean.setName(bean.getName());
                    bean.setEmail(bean.getEmail());
                    bean.setMobile_no(bean.getMobile_no());
                    bean.setProfile_url(bean.getProfile_url());
                if(!key.equals(user.getUid()))
                {
                    if(bean!=null) {
                        LatLng ll = new LatLng(location.latitude, location.longitude);
                        if (getActivity() != null) {
                            customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_custom_marker, null);
                            markerImageView = (CircleImageView) customMarkerView.findViewById(R.id.civCusMarker);
                            Glide
                                    .with(getActivity())
                                    .load(bean.getProfile_url())
                                    .into(markerImageView);
                            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
                            customMarkerView.buildDrawingCache();
                            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getWidth(), customMarkerView.getHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(returnedBitmap);
                            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            Drawable drawable = customMarkerView.getBackground();
                            if (drawable != null)
                                drawable.draw(canvas);
                            customMarkerView.draw(canvas);
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .title(bean.getName())
                                    .snippet(bean.getMobile_no() + "," + bean.getEmail() + "," + key + "," + bean.getOnline())
                                    .position(ll)
                                    .icon(BitmapDescriptorFactory
                                            .fromBitmap(returnedBitmap)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 14));
                            markerMap.put(key, marker);
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "name and url null", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    if(m==1)
                    {
                    if(bean!=null) {
                        LatLng ll = new LatLng(location.latitude, location.longitude);
                        if (getActivity() != null) {
                            customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_custom_marker, null);
                            customMarkerView.setBackground(getResources().getDrawable(R.drawable.ic_my_loc));
                            markerImageView = (CircleImageView) customMarkerView.findViewById(R.id.civCusMarker);
                            Glide
                                    .with(getActivity())
                                    .load(bean.getProfile_url())
                                    .into(markerImageView);
                            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
                            customMarkerView.buildDrawingCache();
                            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getWidth(), customMarkerView.getHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(returnedBitmap);
                            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            Drawable drawable = customMarkerView.getBackground();
                            if (drawable != null)
                                drawable.draw(canvas);
                            customMarkerView.draw(canvas);
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .title(bean.getName())
                                    .draggable(true)
                                    .snippet("Your Location" + "," + "" + "," + user.getUid() + "," + "true")
                                    .position(ll)
                                    .icon(BitmapDescriptorFactory
                                            .fromBitmap(returnedBitmap)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 14));
                            markerMap.put(user.getUid(), marker);
                            m=0;
                        }
                    }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        System.out.println("Detail LatLong entered^^^^^" + key + location.latitude + " , " + location.longitude);
    }

    @Override
    public void onKeyExited(String key) {
        Marker marker=this.markerMap.get(key);
        if(marker!=null)
        {
            marker.remove();
            this.markerMap.remove(key);
        }
        System.out.println("Ket existed^^^^^");
    }
    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Marker marker = this.markerMap.get(key);
        if (marker != null) {
            this.animateMarkerTo(marker,location.latitude,location.longitude);
        }
        System.out.println("Detail LatLong Moved^^^^^" +key+ location.latitude + " , " + location.longitude);
    }

    @Override
    public void onGeoQueryReady() {
            System.out.println("Readyyy query ^^^^^");
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        System.out.println("query  eroorrr^^^^^");
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLng center = cameraPosition.target;
        double radius = zoomLevelToRadius(cameraPosition.zoom);
        this.query.setCenter(new GeoLocation(center.latitude, center.longitude));
        this.query.setRadius(radius/1000);
    }
    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
    private double zoomLevelToRadius(double zoomLevel) {
        return 16384000/Math.pow(2, zoomLevel);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latlang=marker.getPosition();
        setGeoLocation(user.getUid(),latlang.latitude,latlang.longitude);
    }
}
