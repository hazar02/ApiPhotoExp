package com.example.azarchatit.facebookphotosexp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
     static GraphResponse response2;
     LoginButton loginButton;
    private TextView textView;
    private TextView textInfos;
    private Button goAlbumButton;
     CallbackManager callbackManager;
    private  String Message;
    private ProfileTracker profiletracker;
    AccessTokenTracker tracker;
    private static final String TAG = "MainActivity";
    
    //public static ArrayList<Photo> arrayList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

       textView = (TextView) findViewById(R.id.textView2);
        textInfos = (TextView) findViewById(R.id.textInfos);
        goAlbumButton =(Button)findViewById(R.id.button2);


        textView.setText("Welcome to PhotoExp");
        /**
         * The Facebook login
         */
        callbackManager = CallbackManager.Factory.create();


        loginButton = (LoginButton) findViewById(R.id.loginButton);
        //loginButton.setReadPermissions(Arrays.asList("public_profile","user_photos"));
        LoginManager.getInstance().logInWithReadPermissions(
                MainActivity.this,
                Arrays.asList("public_profile","user_photos"));



        tracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        profiletracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                Profile profile=Profile.getCurrentProfile();
                if (profile==null){

                    textView.setText("Welcome to PhotoExp");
                    goAlbumButton.setVisibility(View.INVISIBLE);

                }

            }
        };


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(LoginResult loginResult) {


                Log.d(TAG, "onSuccess: this is the token" + loginResult.getAccessToken().getUserId());
                Profile profile=Profile.getCurrentProfile();

                    textView.setText("Welcome : " +" "+profile.getName());
                    goAlbumButton.setVisibility(View.VISIBLE);

                    Context context = getApplicationContext();
                    Message = "login was successful";
                    Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();

                    GraphRequest request = GraphRequest.newGraphPathRequest(
                            loginResult.getAccessToken(),
                            "/me/albums",
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    Log.d(TAG, "onCompleted: " + response);
                                    response2 = response;
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "name,photo_count,photos{webp_images}");
                    request.setParameters(parameters);
                    request.executeAsync();

            }
            @Override
            public void onCancel() {

                Context context = getApplicationContext();
                 Message = "login canceled" ;
                Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
            }
          @Override
            public void onError(FacebookException error) {
                Context context = getApplicationContext();
                Message = "an Error occurred during Authentication" + error;
                Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
            }
        });
        /**
         * The Album Activity Listener
         */


        View.OnClickListener listener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAlbums();
            }
        };
        goAlbumButton.setOnClickListener(listener);
    }




    @Override
    public void onResume() {
        super.onResume();
        if (AccessToken.getCurrentAccessToken() != null){
            textView.setText("Welcome "+" "+Profile.getCurrentProfile().getName());
            goAlbumButton.setVisibility(View.VISIBLE);

        }else {
            textView.setText("Welcome to PhotoExp");
            goAlbumButton.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();



    }
    private void goAlbums() {
        Intent intent = new Intent(this, ActivityAlbums.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




}
