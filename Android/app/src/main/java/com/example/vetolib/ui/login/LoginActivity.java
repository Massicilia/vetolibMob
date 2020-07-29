package com.example.vetolib.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.example.vetolib.MainActivity;
import com.example.vetolib.R;
import com.example.vetolib.ui.MainMenuActivity;
import com.example.vetolib.ui.signup.SingUpActivity;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private Auth0 auth0;
    Button loginButton;


    final static String url_Login = "https://vetolibapi.herokuapp.com/api/v1/petowner/login";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final TextView passwordResetEt = findViewById(R.id.etPasswordReset);
        loginButton = (Button) findViewById(R.id.login);
        final TextView login_user = findViewById(R.id.textView);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("BUTTON", "LOGIIIIIN");

                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Log.i("Email", email);
                Log.i("Password", password);


                // Show toast if email or password not filled
                if (email.equals("") || password.equals("")) {
                    showToast("Email ou mot de passe incorrect");
                } else {
                    new LoginUser().execute(email, password);
                }
            }
        });

        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);

        //login();

        login_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("BUTTON", "SIGNUP");
                Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
                startActivity(intent);
            }
        });

    }


    public class LoginUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.i("BUTTON", "API");

            String Email = strings[0];
            String Password = strings[1];

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                   // .connectTimeout(500, TimeUnit.SECONDS)
                   // .writeTimeout(500, TimeUnit.SECONDS)
                    //.readTimeout(500, TimeUnit.SECONDS)
                    .build();

            Log.i("Requete", okHttpClient.toString());

            RequestBody formBody = new FormBody.Builder()
                    .add("email", Email)
                    .add("password", Password)
                    .build();

            Log.i("Form body", formBody.toString());

            Request request = new Request.Builder()
                    .url(url_Login)
                    .post(formBody)
                    .build();

            Log.i("Request", request.toString());

            Response response;

            try {

                response = okHttpClient.newCall(request).execute();

                Log.i("Response", response.toString());


                if (response.isSuccessful()) {

                    String result = null;

                    if (response.body() != null) {
                        result = response.body().string();
                    }

                    JSONObject Jobject = new JSONObject(result);


                        String token = Jobject.getString("token");
                        int idpetowner = Jobject.getInt("idpetowner");
                        Log.i("LOGIN POST", "idpetowner: " + idpetowner + "   token: " + token);

                        saveOnSharedPreferences(token,idpetowner);

                        Intent i = new Intent(LoginActivity.this,
                                MainMenuActivity.class);
                        startActivity(i);
                        finish();

                } else {
                    String result = null;

                    if (response.body() != null) {
                        result = response.body().string();
                    }

                    JSONObject Jobject = new JSONObject(result);
                    String error  = Jobject.getString("error");
                    showToast(error);
                    Log.i("LOGIN POST", "Badd id or pass ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Connexion problem");
                Log.i("LOGIN POST", "Ceonnexion problem ");
            }
            return null;
        }
    }


    private void login() {
        WebAuthProvider.login(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .start(LoginActivity.this, new AuthCallback() {

                    @Override
                    public void onFailure(@NonNull Dialog dialog) {
                        Log.i("SUCESS", "NOTEE");
                    }

                    @Override
                    public void onFailure(AuthenticationException exception) {
                        Log.i("SUCESS", "ERROOOOR");
                    }

                    @Override
                    public void onSuccess(@NonNull Credentials credentials) {
                        Log.i("SUCESS", "DONEEEE");
                    }
                });
    }

    public void showToast(final String Text) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,
                        Text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveOnSharedPreferences(String token, int idpetowner){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.putInt("idpetowner",idpetowner);
        editor.apply();
    }

}
