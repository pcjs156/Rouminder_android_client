package com.example.rouminder.fragments;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.rouminder.MainApplication;
import com.example.rouminder.R;
import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.LocationGoal;

import javax.annotation.Nullable;

public class GoalDescribeFragment extends DialogFragment {
    private final static int PERMIT_DISTANCE = 500; // meter 단위

    Goal goal;
    View root;

    Context context;

    private LocationManager locationManager;

    public GoalDescribeFragment(Goal goal) {
        this.goal = goal;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_goal_describe, container);

        context = getContext();

        // radius 조정을 위한 코드
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ImageView buttonClose = (ImageView) root.findViewById(R.id.buttonClose);
        TextView buttonGoalChange = (TextView) root.findViewById(R.id.buttonGoalChange);
        TextView buttonGoalDelete = (TextView) root.findViewById(R.id.buttonGoalDelete);
        TextView textViewName = (TextView) root.findViewById(R.id.textViewName);
        TextView textViewTime = (TextView) root.findViewById(R.id.textViewTime);
        TextView textViewEndTime = (TextView) root.findViewById(R.id.textViewEndTime);
        TextView textViewProgress = (TextView) root.findViewById(R.id.textViewProgress);

        textViewName.setText(goal.getName());
        textViewTime.setText(goal.getStartTime().toString());
        textViewEndTime.setText(goal.getEndTime().toString());
        textViewProgress.setText(goal.progressToString());

        ImageView neg1 = (ImageView) root.findViewById(R.id.neg1);
        ImageView plus1 = (ImageView) root.findViewById(R.id.plus1);
        TextView location = (TextView) root.findViewById(R.id.location);

        if (goal instanceof CheckGoal) {

        } else if (goal instanceof CountGoal) {
            neg1.setVisibility(View.VISIBLE);
            plus1.setVisibility(View.VISIBLE);
        } else {
            location.setVisibility(View.VISIBLE);
        }

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        buttonGoalChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalDescribeFragment", "Change");
                GoalModifyFragment goalModifyFragment = new GoalModifyFragment(goal);
                goalModifyFragment.show(getActivity().getSupportFragmentManager(), null);
                dismiss();
            }
        });
        buttonGoalDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("GoalDescribeFragment", "Delete Button Clicked");
                GoalManager goalManager = ((MainApplication) getActivity().getApplication()).getGoalManager();
                int id = goal.getId();
                Log.d("GoalDescribeFragment", Integer.toString(id));
                goalManager.removeGoal(id);
                Log.d("GoalDescribeFragment", goalManager.getGoals().toString());
                dismiss();
            }
        });

        if (goal instanceof CountGoal) {
            CountGoal countGoal = (CountGoal) goal;
            neg1.setClickable(true);
            neg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    countGoal.setCount(countGoal.getCount() - 1);
                    textViewProgress.setText(goal.progressToString());
                }
            });
            plus1.setClickable(true);
            plus1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    countGoal.setCount(countGoal.getCount() + 1);
                    textViewProgress.setText(goal.progressToString());
                }
            });
        }

        location.setClickable(true);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = locationCheck();

                if (result) {
                    ((LocationGoal) goal).setChecked(true);
                    dismiss();
                }
                else Toast.makeText(context, "거리가 너무 멉니다", Toast.LENGTH_SHORT).show();
            }
        });

        setCancelable(true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        int width = size.x;
        int height = size.y;

        root.getLayoutParams().width = (int) (width * 0.9);
        root.getLayoutParams().height = (int) (height * 0.8);
    }

    public boolean locationCheck() {
        Location currentLocation = getMyLocation();

        if (currentLocation != null) {
            Location ret = new Location("test");
            ret.setLatitude(((LocationGoal) goal).getLat());
            ret.setLongitude(((LocationGoal) goal).getLng());

            float distance = currentLocation.distanceTo(ret);
            Log.i("test", "distance : " + distance);

            if (distance < PERMIT_DISTANCE) {
                return true;
            }
        }

        return false;
    }

    @SuppressLint("MissingPermission")
    private Location getMyLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Location currentLocation;

        // 만약 위치 권한 허용을 안 했으면 권한 메시지를 다시 띄웁니다.
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        currentLocation = locationManager.getLastKnownLocation(locationProvider);
        if (currentLocation != null) {
            double lat = currentLocation.getLatitude();
            double lng = currentLocation.getLongitude();
            Log.d("test", "current : latitude=" + lat + ", longitude=" + lng);
        }

        // location update stop
        locationManager.removeUpdates(locationListener);

        return currentLocation;
    }

}
