package com.example.rouminder.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.rouminder.MainApplication;
import com.example.rouminder.firebase.manager.RepeatPlanModelManager;
import com.example.rouminder.helpers.RepeatPlanHelper;

public class RepeatPlanUpdateHandlerService extends JobIntentService {
    private static final int JOB_ID = 2004;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, RepeatPlanUpdateHandlerService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("plan_update", "started");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        RepeatPlanModelManager.getInstance().get()
                .forEach(m -> RepeatPlanHelper.generateGoals(((MainApplication)getApplication()).getGoalManager(), m));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("plan_update", "ended");
    }
}
