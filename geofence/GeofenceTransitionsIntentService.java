/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softweb.smartoffice.geofence;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.softweb.smartoffice.R;
import com.softweb.smartoffice.activity.HomeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/***************************************************************************************************
 * Listener for geofence transition changes.
 * <p/>
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence busMasterId(s) that triggered the transition. Creates a notification
 * as the output.
 ***************************************************************************************************/
public class GeofenceTransitionsIntentService extends IntentService
{

    protected static final String TAG = "GeofenceTransitionsIntentService";

    public static String ACTION = "ACTION_SEND";
    public static String REGION_NAME = "REGION_NAME";
    public static String EVENT = "EVENT";


    @SuppressLint("LongLogTag")
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate()");

    }

    /************************************************************************
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     *************************************************************************/
    public GeofenceTransitionsIntentService()
    {
        // Use the TAG to name the worker thread.
        super(TAG);
    }//end of GeofenceTransitionsIntentService()

    /*************************************************************************
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     **************************************************************************/
    public GeofenceTransitionsIntentService(String name)
    {
        super(name);
    }//end of GeofenceTransitionsIntentService()

    /***********************************************************************************************
     * onHandleIntent()
     * Handles incoming intents
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     **********************************************************************************************/
    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent)
    {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.d(TAG, "geofencingEvent :: " + geofencingEvent);

        if (geofencingEvent.hasError())
        {
            String errorMessage = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.d(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "geofenceTransition :: " + geofenceTransition);

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences);

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            Log.d(TAG, "geofenceTransitionDetails :: " + geofenceTransitionDetails);

            /*******************************************************************************************************************
             *Add your entry exit code here
             * db insert
             ********************************************************************************************************************/

        } else
        {
            // Log the error.
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

        }
    }//end of onHandleIntent

    /**********************************************************************************************
     * Gets transition details and returns them as a formatted string.
     *
     * @param context             The app context.
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     **********************************************************************************************/
    private String getGeofenceTransitionDetails(Context context, int geofenceTransition, List<Geofence> triggeringGeofences)
    {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences)
        {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(",", triggeringGeofencesIdsList);

        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(ACTION);
        broadCastIntent.putExtra(REGION_NAME, triggeringGeofencesIdsString);
        broadCastIntent.putExtra(EVENT, geofenceTransitionString);

        sendBroadcast(broadCastIntent);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }//end of getGeofenceTransitionDetails()

    /***********************************************************************************************
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     ***********************************************************************************************/
    private void sendNotification(String notificationDetails)
    {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(HomeActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.MAGENTA)
                .setContentTitle(notificationDetails)
                .setContentText("don't click activity wiLl restarted")
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random rand = new Random();
        int x = rand.nextInt(1000);

        // Issue the notification
        mNotificationManager.notify(x, builder.build());

    }//end of sendNotification()

    /********************************************************************************************
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     ********************************************************************************************/
    private String getTransitionString(int transitionType)
    {
        switch (transitionType)
        {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exit";
            default:
                return "Unknown";
        }
    }//end of getTransitionString()

    public String getCurrentTime()
    {
        Date date = new Date();
        SimpleDateFormat simpDate;

        simpDate = new SimpleDateFormat("hh:mm:ss a");
        return simpDate.format(date);

    }

}//end of GeofenceTransitionsIntentService class
