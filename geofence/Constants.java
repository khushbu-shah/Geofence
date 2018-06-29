package com.softweb.smartoffice.geofence;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**************************************************************************************************
 * class name :: Constants
 * Constants used in this sample.
  **************************************************************************************************/
public final class Constants {

    private Constants() {
    }

    // public static final String PACKAGE_NAME = "com.google.android.gms.location.geofence";
    // public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    // public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    //Used to set an expiration time for a geofence. After this amount of time Location Services
    //stops tracking the geofence.
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    //For this sample, geofences expire after twelve hours.
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 50; // 1 mile, 1.6 km


    //Map for storing information about airports in the San Francisco bay area.
    public static final HashMap<String, LatLng> GEOFENCE_REGION = new HashMap<String, LatLng>();


    /**********************************************************************************************
     * static block will execute first
     *********************************************************************************************/
    static {

        //72.624649,23.072967
        GEOFENCE_REGION.put("Sea ", new LatLng(72.624649,23.072967));
        GEOFENCE_REGION.put("Sardar vallabh ", new LatLng(23.0734311,72.6243823));
        GEOFENCE_REGION.put("Gandhi ashram 1", new LatLng(23.060976, 72.580889));

        GEOFENCE_REGION.put("Priya soft", new LatLng(23.041682, 72.507683));

    }//end of static block

}//end of Constant class

