package com.example.socialmedia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private TextView name,userEmail;
    private LoginButton loginButton;
    private CircleImageView circleImageView;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.username);
        userEmail = findViewById(R.id.userId);
        loginButton = findViewById(R.id.login_button);
        circleImageView= findViewById(R.id.profile);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        checkLoginStatus();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {
                if(currentAccessToken == null)
                {
                    name.setText("");
                    userEmail.setText("");
                    circleImageView.setImageResource(1);
                    Toast.makeText(MainActivity.this, "User Logged Out", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    loadUserProfile(currentAccessToken);
                }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request =GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";

                    userEmail.setText(email);
                    name.setText(first_name+""+last_name);
                    RequestOptions requestOption = new RequestOptions();
                    requestOption.dontAnimate();

                    Glide.with(MainActivity.this).load(image_url).into(circleImageView);


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameter = new Bundle();
        parameter.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameter);
        request.executeAsync();
    }

    public void checkLoginStatus()
    {
        if(AccessToken.getCurrentAccessToken() !=null)
        {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }
}

