package com.example.rouminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

import com.example.rouminder.firebase.Manager;
import com.example.rouminder.firebase.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    Button btnlogin;
    private AlarmManager alarmManager;
    private GoalManager goalManager;

    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("global", MODE_PRIVATE);
        String uid = prefs.getString("uid", null);
        boolean isLoggedBefore = uid != null;

        setContentView(R.layout.activity_login);

        goalManager = new GoalManager();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // 알람 매니저 초기화
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0); // 인덴트 생성
        // 현재 시간 받아오기
        LocalDateTime end = LocalDateTime.now();
        // 현재 시간에서 5분 20초 추가
        end = end.plusMinutes(5);
        end = end.plusSeconds(10);
        // 목표 설정
        Goal goal = new Goal(goalManager,0,"응애",LocalDateTime.now(), end,10,100);
        goalManager.addGoal(goal);

        // 목표에서 마감 시간 받아옴.
        LocalDateTime end2 = goal.getEndTime();
        // 목표에서 5분 차감,
        end2 = end2.minusMinutes(5);
        // 캘린더 객체에 목표 시간 적용
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, end2.getYear());
        calendar.set(Calendar.MONTH, end2.getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, end2.getDayOfMonth() - 1);
        calendar.set(Calendar.HOUR, end2.getHour());
        calendar.set(Calendar.MINUTE, end2.getMinute());
        calendar.set(Calendar.SECOND, end2.getSecond());

        Long aa = calendar.getTimeInMillis();
        Log.e("Create Time Stamp", Long.toString(aa));

        // 5분 차감된 시간으로 브로드캐스트를 울리도록 알아서 설정.
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , pendingIntent); // 해당 시간으로 알림

        btnlogin = (Button) findViewById(R.id.signInButton);
        signInButton = findViewById(R.id.signInButton);

        if (isLoggedBefore) {
            User.getInstance().setInfo(uid);
            Manager.getInstance().createUser(uid);
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        } else {
            setAuthEnvironment();
        }
    }

    // [START signin]
    private void setAuthEnvironment() {
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            User.getInstance().setInfo(uid);
                            Manager.getInstance().createUser(uid);

                            SharedPreferences prefs = getSharedPreferences("global", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("uid", uid);
                            editor.apply();

                            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(mainActivityIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("FB", "Login Failed");
                        }
                    }
                });
    }
}