package com.example.android.ebaysearchresults.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.ebaysearchresults.data.EbayDbHelper;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;


/**
 * Created by tanujanuj on 14/07/17.
 */

public class EbaySyncUtils {

    private static final int REMINDER_INTERVAL_MINUTES = 1;
    private static final int SYNC_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS;

    private static boolean sInitialised;
    private static final String EBAY_SYNC_TAG = "ebay-sync";

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncItemJob = dispatcher.newJobBuilder()
                .setService(EbayFirebaseJobService.class)
                .setTag(EBAY_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        0,
                        SYNC_INTERVAL_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncItemJob);
    }




    synchronized public static void initialise(@NonNull final Context context){
        if(sInitialised)
            return;

        sInitialised=true;

        scheduleFirebaseJobDispatcherSync(context);

    }
}
