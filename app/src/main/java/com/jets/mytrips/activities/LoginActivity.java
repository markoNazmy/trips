package com.jets.mytrips.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jets.mytrips.R;
import com.jets.mytrips.beans.User;
import com.jets.mytrips.controllers.UserController;
import com.jets.mytrips.services.Validator;
import com.jets.mytrips.services.VolleyCallback;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 6;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userController = UserController.getInstance(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize google sign-in button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        Button signInBttn = (Button) findViewById(R.id.sign_in);
        signInBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
                String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
                /*Validator validator = Validator.getInstance();

                if (!validator.isValidInput(email) && !validator.isValidInput(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!validator.isValidEmail(email)) {
                    Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                userController.login(email, password, new VolleyCallback() {
                    @Override
                    public void onSuccess(User user) {
                        // Switch to CurrentTripsActivity activity
                        switchToCurrentTripsActivity(user.getFullName());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Something wrong happened, cannot connect to google services", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount userAccount = result.getSignInAccount();
            final User user = new User(userAccount.getId(), userAccount.getId(), userAccount.getGivenName() + " " + userAccount.getFamilyName());

            // Send user data to the backend server
            userController.login(user.getEmail(), user.getPassword(), new VolleyCallback() {
                @Override
                public void onSuccess(User user) {
                    // Switch to CurrentTripsActivity activity
                    switchToCurrentTripsActivity(user.getFullName());
                }

                @Override
                public void onFailure(String errorMessage) {
                    UserController.getInstance(LoginActivity.this).registerUser(user,
                            new VolleyCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    // Save user data in preferences file
                                    userController.saveUserSession(user);

                                    // Switch to CurrentTripsActivity activity
                                    switchToCurrentTripsActivity(user.getFullName());
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }

    private void switchToCurrentTripsActivity(String userFullName) {
        Intent intent = new Intent(LoginActivity.this, CurrentTripsActivity.class);
        intent.putExtra("username", userFullName);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
    }
}
