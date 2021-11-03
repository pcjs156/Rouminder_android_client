package com.example.rouminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rouminder.firebase.Manager;

public class LoginActivity extends AppCompatActivity {
    Button btnlogin;
    Button addIdBtn;
    EditText editTextId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnlogin = (Button) findViewById(R.id.btnLogin);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "로그인", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
            }
        });

        editTextId = (EditText) findViewById(R.id.editTextId);

        Manager fbManager = Manager.getInstance();
        addIdBtn = (Button) findViewById(R.id.addIdBtn);
        addIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = editTextId.getText().toString();
                Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
                fbManager.writeNewTestObject(body);
            }
        });
    }
}