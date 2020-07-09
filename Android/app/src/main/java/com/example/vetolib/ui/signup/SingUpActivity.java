package com.example.vetolib.ui.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.vetolib.R;
import com.example.vetolib.ui.login.LoginActivity;

public class SingUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        final TextView user_login = findViewById(R.id.textView);
        user_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
