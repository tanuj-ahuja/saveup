package com.example.android.ebaysearchresults.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

/**
 * Created by tanujanuj on 14/07/17.
 */

public class EbayFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchItemTask;


    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mFetchItemTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                EbaySyncTask.syncEbayItem(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mFetchItemTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mFetchItemTask != null) {
            mFetchItemTask.cancel(true);
        }
        return true;
    }
}
