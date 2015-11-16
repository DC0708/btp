/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import com.google.android.gms.analytics.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.example.mapdemo.PlayerBubble;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DC
 */


public class TopView extends AppCompatActivity implements LocationListener,SeekBar.OnSeekBarChangeListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    GoogleMap googleMap;
    private static LatLng gpsLocation;
    private LatLng InitialLoc;

//    private static final LatLng INDIA = new LatLng(-33.86365, 151.20589);
//
//    private static final LatLng PAK = new LatLng(-33.88365, 151.20389);
//
//    private static final LatLng ENG = new LatLng(-33.87365, 151.21689);
//
//    private static final LatLng DC = new LatLng(-33.86165, 151.21892);

    private List<DraggableCircle> circles = new ArrayList<DraggableCircle>(10);

    private static final double DEFAULT_RADIUS = 1;

    public static final double RADIUS_OF_EARTH_METERS = 6371009;

    private static final int WIDTH_MAX = 50;

    private static final int HUE_MAX = 360;

    private int numberOfBubbles;

    private static final int ALPHA_MAX = 255;

    private GoogleMap mMap;
    public int checker =1;
    String BoundaryType;
    private List<DraggableCircle> mCircles = new ArrayList<DraggableCircle>(1);

    double width,height;

    private SeekBar mColorBar;

    private SeekBar mAlphaBar;

    private SeekBar mWidthBar;

    private int mStrokeColor;

    private int mFillColor;

    private int mWidth;

    private double maxBubbleSize = 2.5;

    private double playerRadius;

    private class DraggableCircle {

        //private final Marker centerMarker;

        //private final Marker radiusMarker;

        private final Circle circle;

        private double radius;

        private int direction;

        private int bTag;

        public DraggableCircle(LatLng center, double radius, int direction, int bTag) {
            this.radius = radius;
            this.direction = direction;
            this.bTag = bTag;
//            centerMarker = mMap.addMarker(new MarkerOptions()
//                    .position(center)
//                    .draggable(true));
//            radiusMarker = mMap.addMarker(new MarkerOptions()
//                    .position(toRadiusLatLng(center, radius))
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.defaultMarker(
//                            BitmapDescriptorFactory.HUE_AZURE)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(mWidth)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor));
        }

        public DraggableCircle(LatLng center, LatLng radiusLatLng,int direction, int bTag) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            this.direction = direction;
            this.bTag = bTag;
//            centerMarker = mMap.addMarker(new MarkerOptions()
//                    .position(center)
//                    .draggable(true));
//            radiusMarker = mMap.addMarker(new MarkerOptions()
//                    .position(radiusLatLng)
//                    .draggable(true)
//                    .icon(BitmapDescriptorFactory.defaultMarker(
//                            BitmapDescriptorFactory.HUE_AZURE)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(mWidth)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor));
        }

//        public boolean onMarkerMoved(Marker marker) {
//            if (marker.equals(centerMarker)) {
//                circle.setCenter(marker.getPosition());
//                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radius));
//                return true;
//            }
//            if (marker.equals(radiusMarker)) {
//                radius = toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
//                circle.setRadius(radius);
//                return true;
//            }
//            return false;
//        }

        public void onStyleChange() {
            circle.setStrokeWidth(mWidth);
            circle.setFillColor(mFillColor);
            circle.setStrokeColor(mStrokeColor);
        }
    }


    /** Generate LatLng of radius marker */
    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }
//small --> 0.00025 , 0.00025
//medium --> 0.00035 , 0.00035
//large --> 0.00050 , 0.00050

    GeolocationService gps;

    private CheckBox mMyLocationCheckbox;

    protected LocationListener locationListener;
    protected LocationManager locationManager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
           BoundaryType = extras.getString("Boundary");

        mMyLocationCheckbox = (CheckBox) findViewById(R.id.my_location);

        final SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleMap = mapFragment.getMap();
        googleMap.setMyLocationEnabled(false);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        InitialLoc = new LatLng(location.getLatitude(),location.getLongitude());

 //       mFillColor=Color.MAGENTA;
//        playerRadius=1.5;
//        PlayerBubble player =new PlayerBubble(new LatLng(location.getLatitude(),location.getLongitude()),playerRadius,googleMap);

        //PlayerBubble.DraggableCirclePlayer draggableCirclePlayer = new PlayerBubble.DraggableCirclePlayer(new LatLng(location.getLatitude(),location.getLongitude()),playerRadius);
        //player.DraggableCirclePlayer= new player.DraggableCirclePlayer(new LatLng(location.getLatitude()))
        //circles.add(new PlayerBubble.DraggableCirclePlayer(new LatLng(location.getLatitude(),location.getLongitude()),playerRadius));

        //PlayerBubble.DraggableCirclePlayer() = new PlayerBubble.DraggableCirclePlayer(new LatLng(location.getLatitude(),location.getLongitude()),playerRadius);


//        circles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12),tag));

      //  player.draggableCirclePlayer = new DraggableCirclePlayer(new LatLng(location.getLatitude(),location.getLongitude()),playerRadius);

        Log.d(" initial location set ", location.getLatitude() + " " + location.getLongitude());
        if (location != null) {
            onLocationChanged(location);
//            player.circle.setCenter(new LatLng(location.getLatitude(),location.getLongitude()));
            Log.d("Location is if changed ", location.getLatitude() + " " + location.getLongitude());
        }

            locationManager.requestLocationUpdates(bestProvider,100,0,this); // once every second
            //Log.d("current location ", location.getLatitude()+ " " + location.getLongitude());
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                    public void run() {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                //   Log.d("size ",String.valueOf(circles.size()));


                                /*******Collission with Boundary ************/
                                for(int i=0;i<circles.size();i++){
                                    // circles.g
                                    DraggableCircle cir = circles.get(i);
//                            Log.d("tagg",cir.toString());

                                    //            System.out.println(cir.circle.getCenter().latitude);
                                    LatLng loca =  cir.circle.getCenter();
                                    double latit = loca.latitude;
                                    double longit = loca.longitude;
                                    // Log.d("angle"," "+cir.direction);
                                    LatLng finalLoc = new LatLng(latit+Math.sin(Math.toRadians(cir.direction))/50000,longit+Math.cos(Math.toRadians(cir.direction))/50000);
                                    cir.circle.setCenter(finalLoc);
                                  //  Log.d("inital location", InitialLoc.latitude + " " + InitialLoc.longitude);
               // Log.d(" direction "," " +cir.direction);
               // Log.d("collision", latit + " " + longit + " "+ cir.direction + " " + (InitialLoc.latitude +width/2));
                if(latit > InitialLoc.latitude +width/2){
                    cir.direction= 360-cir.direction;
                                cir.circle.setCenter(new LatLng(InitialLoc.latitude +width/2,longit));

                            //    Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
                            }
                            else if(longit > InitialLoc.longitude +width/2){
                                if(cir.direction<180)
                                cir.direction= 180-cir.direction;
                                else
                                cir.direction = 540-cir.direction;
                                cir.circle.setCenter(new LatLng(latit,InitialLoc.longitude +width/2));
                             //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
                            }
                            else if(latit < InitialLoc.latitude -width/2){
                                cir.direction= 360-cir.direction;

                                cir.circle.setCenter(new LatLng(InitialLoc.latitude -width/2,longit));

                             //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
                            }
                            else if(longit < InitialLoc.longitude -width/2){
                                if(cir.direction <180)
                                cir.direction= 180-cir.direction;
                                else
                                cir.direction = 540-cir.direction;
                                cir.circle.setCenter(new LatLng(latit,InitialLoc.longitude -width/2));

                             //   Log.d("actual collision", latit + " " + longit + " "+ cir.direction);
                            }


                        }

                        /*******  -------  ************/
                        int check=0;
                       /****** ----- Collision of non player bubbles ****/
                        for(int i=0;i<circles.size();i++){

                            for(int j=0;j<circles.size();j++){

                                if(i!=j){
                                    double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, circles.get(j).circle.getCenter().latitude, circles.get(j).circle.getCenter().longitude);

                                    if(circles.get(i).radius+circles.get(j).radius>=Math.abs(distanceBwBubbles)){


                                        double r1 = circles.get(i).radius;
                                        double r2 = circles.get(j).radius;

                                        double lat = r1>=r2?circles.get(i).circle.getCenter().latitude:circles.get(j).circle.getCenter().latitude;
                                        double lon = r1>=r2?circles.get(i).circle.getCenter().longitude:circles.get(j).circle.getCenter().longitude;
                                        int dir = r1>=r2?circles.get(i).direction:circles.get(j).direction;
                                        int tag = r1>=r2?circles.get(i).bTag:circles.get(j).bTag;
                                        double newRadius;
                                        if (circles.get(i).bTag==circles.get(j).bTag)
                                            newRadius = Math.pow((Math.pow(r1,3)+Math.pow(r2,3)),1.0/3.0);
                                        else
                                            newRadius = Math.pow(Math.abs(Math.pow(r1,3)-Math.pow(r2,3)),1.0/3.0);
                                    //    Log.d("New Radius is"," "+newRadius);

                                    //    Log.d("size of circles before removal", " "+ circles.size());
                                        //circles.get(i).
                                        if(i<j)
                                            j-=1;
                                        circles.get(i).circle.remove();

                                        circles.remove(i);

                                        circles.get(j).circle.remove();


                                        circles.remove(j);
                                      //  Log.d("size of circles", " "+ circles.size());
                                        if (tag==1)
                                            mFillColor = Color.YELLOW;
                                        else
                                            mFillColor = Color.GREEN;
                                        circles.add(new DraggableCircle(new LatLng(lat,lon), newRadius,dir,tag));
                                        check=1;
                                        break;
                                    }


                                }


                            }

                            if(check==1)
                                break;



                        }
                     /****** ----- Collision of non player bubbles ****/
                        if(mCircles.size()>0){
                        DraggableCircle player = mCircles.get(mCircles.size()-1);

                        for(int i =0 ; i <circles.size();i++){

                            double distanceBwBubbles = distFrom(circles.get(i).circle.getCenter().latitude, circles.get(i).circle.getCenter().longitude, mCircles.get(mCircles.size()-1).circle.getCenter().latitude, mCircles.get(mCircles.size()-1).circle.getCenter().longitude);

                            if(circles.get(i).radius+mCircles.get(mCircles.size()-1).radius>=Math.abs(distanceBwBubbles)) {
                                Log.d("collision with player", " " + circles.get(i).bTag);
                                if (player.radius < circles.get(i).radius) {
                                    int size = mCircles.size();
                                    mCircles.get(size - 1).circle.remove();
                                    mCircles.remove(size - 1);
                                    break;
                                } else {
                                    if (circles.get(i).bTag == 1) {
                                        double r1 = player.radius;
                                        double r2 = circles.get(i).radius;

                                        double newRadius = Math.pow((Math.pow(r1, 3) + Math.pow(r2, 3)), 1.0 / 3.0);
                                        player.circle.setRadius(newRadius);
                                        circles.get(i).circle.remove();
                                        circles.remove(i);
                                        break;
                                    } else if (circles.get(i).bTag == 2) {
                                        double r1 = player.radius;
                                        double r2 = circles.get(i).radius;

                                        double newRadius = Math.pow(Math.abs(Math.pow(r1, 3) - Math.pow(r2, 3)), 1.0 / 3.0);
                                        player.circle.setRadius(newRadius);
                                        circles.get(i).circle.remove();
                                        circles.remove(i);
                                        break;
                                    } else {

                                    }

                                }

                            }








                            }




                            }
                            else{
                               // Toast.makeText(getApplicationContext(),"Game Over!!!", Toast.LENGTH_SHORT).show();
                            //Intent i = new Intent(TopView.this, MainActivity.class);
                            String strName = null;
                            //i.putExtra("Boundary", radioBoundaryButton.getText());
                            //            DemoDetails demo = (DemoDetails) parent.getAdapter().getItem(0);
//                startActivity(new Intent(MainActivity.this, TopView.class));
                           // startActivity(i);
                            }


                     }
                });
            }
        }, 1000, 1000);
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist =  (earthRadius * c);

        return dist;
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     */
    int checking=1;
    @Override
    public void onMapReady(GoogleMap map) {
        //   map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

//        updateMyLocation();
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 19.5f));

        gps = new GeolocationService(TopView.this);


        // check if GPS enabled
        if(gps.canGetLocation()){

        //    double latitude = gps.getLatitude();
        //    double longitude = gps.getLongitude();

          //  InitialLoc = new LatLng(latitude, longitude);

           /// gpsLocation = new LatLng(latitude,longitude);

            //Log.d("location",latitude+ " ");
       // PolygonOptions options = new PolygonOptions().addAll(createRectangle(playerLocation, 500, 8));
          //  Log.d(" initial location  ", InitialLoc.latitude + "  " + InitialLoc.longitude);
            if(BoundaryType.equals("Large Boundary")){

                Log.d("boundary is","Large" + BoundaryType);

                width = 2*0.0005;
                height= 2*0.0005;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 18.8f));
                numberOfBubbles = 21;
            }
            else if(BoundaryType.equals("Medium Boundary")){
                Log.d("boundary is","medium" + BoundaryType);

                width = 2*0.00035;
                height= 2*0.00035;
                numberOfBubbles = 15;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.2f));

            }
            else{

                Log.d("boundary is","small" + BoundaryType);
                width = 2*0.00025;
                height= 2*0.00025;
                numberOfBubbles = 9;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(InitialLoc.latitude, InitialLoc.longitude), 19.5f));

            }

            map.addPolygon(new PolygonOptions()
                    .addAll(createRectangle(new LatLng(InitialLoc.latitude, InitialLoc.longitude), width/2, height/2))
                    .strokeColor(Color.BLUE)
                    .strokeWidth(5));

          //  Log.d(" gps ",gpsLocation.latitude + " fssfd "+ gpsLocation.longitude);

            UpdateBubbles(InitialLoc.latitude,InitialLoc.longitude,BoundaryType);

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


    }


    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {

      //  Log.d ("Rectangle created ",center.latitude+ " " + center.longitude + " " + halfWidth + " " + halfHeight);

//        circles.add(new DraggableCircle(new LatLng(center.latitude , center.longitude ), 2 * DEFAULT_RADIUS,0,0));
     //   circles.add(new DraggableCircle(new LatLng(center.latitude + halfHeight, center.longitude + halfWidth), 2 * DEFAULT_RADIUS,0));
     //   circles.add(new DraggableCircle(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth), 2 * DEFAULT_RADIUS,0));
     //   circles.add(new DraggableCircle(new LatLng(center.latitude -halfHeight, center.longitude + halfWidth), 2 * DEFAULT_RADIUS,0));
     //   circles.add(new DraggableCircle(new LatLng(center.latitude -halfHeight, center.longitude - halfWidth), 2 * DEFAULT_RADIUS,0));


        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mFillColor = Color.RED;

        for (DraggableCircle draggableCircle : mCircles) {
            draggableCircle.onStyleChange();
        }
    }

    private void updateMapType(){

        mMap.setMapType(2);
        // 2 IS CONSTANT VALUE FOR SATELLITE MAP

    }

    private void UpdateBubbles(double lat,double lon,String BoundaryType){

        mMap.setOnMapLongClickListener(this);
        mFillColor = Color.BLUE;
        mStrokeColor = Color.BLACK;
        mWidth = 2;


        if(BoundaryType.equals("Large Boundary")){

            Log.d("boundary is","Large" + BoundaryType);

            width = 2*0.0005;
            height= 2*0.0005;
        }
        else if(BoundaryType.equals("Medium Boundary")){
            Log.d("boundary is","medium" + BoundaryType);

            width = 2*0.00035;
            height= 2*0.00035;

        }
        else{

            Log.d("boundary is","small" + BoundaryType);
            width = 2*0.00025;
            height= 2*0.00025;
        }
        Random r = new Random();

        Boolean[][] isPlacedBubble = new Boolean[10][10];

        for(int i=0;i<10;i++){

            for (int j=0;j<10;j++){

                isPlacedBubble[i][j]=false;

            }

        }



        // Creating Matter Bubbles

        for(int i=0;i<2*numberOfBubbles/3;i++){

            int x = (r.nextInt(10)+0)%10;
            int y = (r.nextInt(10)+0)%10;
            int dir = (r.nextInt(360)+0)%360;
            int tag=1;
            double radius = 1 + (0.01 * (double)((r.nextInt(100)+0)%100));

            //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
            while(isPlacedBubble[x][y]) {
                x = (r.nextInt(10)+0)%10;
                y = (r.nextInt(10)+0)%10;

            }
            mFillColor = Color.YELLOW;
           // mCircles.add(circles.get(i));
            circles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), radius,dir,tag));
            isPlacedBubble[x][y]=true;

        }



        // Creating Anti-Matter Bubbles

        for(int i=0;i<numberOfBubbles/3;i++){

            int x = (r.nextInt(10)+0)%10;
            int y = (r.nextInt(10)+0)%10;
            int dir = (r.nextInt(360)+0)%360;
            int tag = 2;
            double radius = 1 + (0.01 * (double)((r.nextInt(100)+0)%100));

            //   Log.d("checkValue",String.valueOf(x)+" " +y+" "+dir);
            while(isPlacedBubble[x][y]) {
                x = (r.nextInt(10)+0)%10;
                y = (r.nextInt(10)+0)%10;

            }
            mFillColor = Color.GREEN;
            circles.add(new DraggableCircle(new LatLng((lat - width / 2) + (x + 1) * width / 12, (lon - height / 2) + (y + 1) * height / 12), radius,dir,tag));
            // mCircles.add(circles.get(i));
            isPlacedBubble[x][y]=true;

        }


        //circles.add(new DraggableCircle(new LatLng(lat+0.00006, lon+0.00009),2* DEFAULT_RADIUS));
        //mCircles.add(circles.get(1));
/*        circles.add(new DraggableCircle(new LatLng(lat-0.00006, lon+0.00015),1.1* DEFAULT_RADIUS));
        circles.add(new DraggableCircle(new LatLng(lat-0.00010, lon-0.00015), 1.5*DEFAULT_RADIUS));
        circles.add(new DraggableCircle(new LatLng(lat+0.00020, lon+0.00019), 1.5*DEFAULT_RADIUS));

        mFillColor = Color.YELLOW;
        mStrokeColor = Color.BLACK;
        mWidth = 2;


        circles.add(new DraggableCircle(new LatLng(lat + 0.00010, lon - 0.00012), 0.8 * DEFAULT_RADIUS));
        circles.add(new DraggableCircle(new LatLng(lat-0.0002, lon+0.00012),0.5* DEFAULT_RADIUS));
        circles.add(new DraggableCircle(new LatLng(lat+0.00011, lon),2.5* DEFAULT_RADIUS));
        circles.add(new DraggableCircle(new LatLng(lat+0.00021, lon-0.00020),1.4* DEFAULT_RADIUS));

        //  Log.d("location",longitude+ " ");
        //mCircles.add(circle);
        mCircles.add(circles.get(0));
        mCircles.add(circles.get(1));

        mCircles.add(circles.get(2));

        mCircles.add(circles.get(3));
        //    Log.d("location4234",longitude+ " 111 ");
        //UpdateBubbles(latitude,longitude);
        mCircles.add(circles.get(4));
        mCircles.add(circles.get(5));
        mCircles.add(circles.get(6));
*/
//            DraggableCircle circle2 = new DraggableCircle(INDIA, DEFAULT_RADIUS);
//            DraggableCircle circle3 = new DraggableCircle(PAK, DEFAULT_RADIUS);
//            DraggableCircle circle4 = new DraggableCircle(ENG, DEFAULT_RADIUS);
//            DraggableCircle circle5 = new DraggableCircle(DC, DEFAULT_RADIUS);
//        mCircles.add(circle);

    }

    public void onLocationChanged(Location location) {
        //  TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        // googleMap.addMarker(new MarkerOptions().position(latLng));
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
        //return latLng;

        gpsLocation = new LatLng(latitude,longitude);

    //    Log.d("location",latitude+ " ");

        mMap = googleMap;
        updateMapType();
        mMap.setOnMapLongClickListener(this);

        mFillColor = Color.MAGENTA;
        mStrokeColor = Color.BLACK;
        mWidth = 2;
        // PolygonOptions options = new PolygonOptions().addAll(createRectangle(playerLocation, 500, 8));
        Log.d(" location on changed is called ", gpsLocation.latitude + " lat " );
     //   PlayerBubble play = new PlayerBubble(gpsLocation, DEFAULT_RADIUS,mMap);
            if(checker==1) {
                DraggableCircle circle = new DraggableCircle(gpsLocation, 1.5 * DEFAULT_RADIUS, 0, 0);
                checker+=1;
                mCircles.add(circle);
            }
            else {
                Log.d(" size of mcircles ", mCircles.size() + " size " + checker);
                if(mCircles.size()>0)
                mCircles.get(mCircles.size()-1).circle.setCenter(new LatLng(gpsLocation.latitude,gpsLocation.longitude));

            }
//            Log.d(" Else of size of mcircles ",mCircles.size()+ " size "+ checker );
  //               //  UpdateBubbles(latitude,longitude);
     //   play.circle.setCenter(new LatLng(latitude,longitude));


    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    /*
    var marker = new google.maps.Marker({
  map: map,
  position: new google.maps.LatLng(53, -2.5),
  title: 'Some location'
});

// Add circle overlay and bind to marker
var circle = new google.maps.Circle({
  map: map,
  radius: 16093,    // 10 miles in metres
  fillColor: '#AA0000'
});
circle.bindTo('center', marker, 'position');


     */
/*
    private class MyTimerTask extends TimerTask {
        @Override
        runOnUiThread(new Runnable(){
            public void run() {
                Log.d("tagg",String.valueOf(circles.size()));
                for(int i=0;i<circles.size();i++){
                   // circles.g
                    DraggableCircle cir = circles.get(i);
                    Log.d("tagg",cir.toString());

                    System.out.println(cir.circle.getCenter().latitude);
                /*    LatLng loca =  cir.circle.getCenter();
                    double latit = loca.latitude;
                    double longit = loca.longitude;
                    LatLng finalLoc = new LatLng(latit+.00002,longit+.00002);
                    cir.circle.setCenter(finalLoc);


                }

            });

            //get and send location information
        }
    }
*/
    public void onMyLocationToggled(View view) {
        updateMyLocation();
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateMyLocation() {
        if (!checkReady()) {
            return;
        }

        if (!mMyLocationCheckbox.isChecked()) {
            mMap.setMyLocationEnabled(false);
            return;
        }

        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Uncheck the box until the layer has been enabled and request missing permission.
            mMyLocationCheckbox.setChecked(false);
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }


    @Override
    public void onMapLongClick(LatLng point) {
//        // We know the center, let's place the outline at a point 3/4 along the view.
//        View view = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                .getView();
//        LatLng radiusLatLng = mMap.getProjection().fromScreenLocation(new Point(
//                view.getHeight() * 3 / 4, view.getWidth() * 3 / 4));
//
//        // ok create it
//        DraggableCircle circle = new DraggableCircle(point, radiusLatLng,0);
//        mCircles.add(circle);
    }



}