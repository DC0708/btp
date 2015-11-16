package com.example.mapdemo;

import android.graphics.Color;
import android.location.Location;
import android.widget.SeekBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DC0708 on 16-Nov-15.
 */
public class PlayerBubble {


//    GoogleMap googleMap;
//    private static LatLng gpsLocation;
//    private LatLng InitialLoc;
//
////    private static final LatLng INDIA = new LatLng(-33.86365, 151.20589);
////
////    private static final LatLng PAK = new LatLng(-33.88365, 151.20389);
////
////    private static final LatLng ENG = new LatLng(-33.87365, 151.21689);
////
////    private static final LatLng DC = new LatLng(-33.86165, 151.21892);
//
//  //  private List<DraggableCircle> circles = new ArrayList<DraggableCircle>(10);
//    public DraggableCirclePlayer draggableCirclePlayer;
//
//    private static final double DEFAULT_RADIUS = 1;
//
    public static final double RADIUS_OF_EARTH_METERS = 6371009;
//
//    private static final int WIDTH_MAX = 50;
//
//    private static final int HUE_MAX = 360;
//
//    private static final int ALPHA_MAX = 255;
//
//    private GoogleMap mMap;
//
//    String BoundaryType;
//    //private List<DraggableCircle> mCircles = new ArrayList<DraggableCircle>(1);
//
//    double width,height;
//
//    private SeekBar mColorBar;
//
//    private SeekBar mAlphaBar;
//
//    private SeekBar mWidthBar;
//
    private final int mStrokeColor = Color.BLACK;
//
    private final int mFillColor = Color.MAGENTA;
//
    private final int mWidth = 2;
//
//    public PlayerBubble(LatLng latLng,double radius)
//    {
//        DraggableCirclePlayer draggableCirclePlayer = new DraggableCirclePlayer(latLng,radius);
//    }

//    public class DraggableCirclePlayer {

        //private final Marker centerMarker;

        //private final Marker radiusMarker;

        public final Circle circle;

        private double radius;

        private int direction;

        private int bTag;

        public PlayerBubble(LatLng center, double radius, GoogleMap mMap) {
            this.radius = radius;

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

        public PlayerBubble(LatLng center, LatLng radiusLatLng,GoogleMap mMap) {
            this.radius = toRadiusMeters(center, radiusLatLng);
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



}


