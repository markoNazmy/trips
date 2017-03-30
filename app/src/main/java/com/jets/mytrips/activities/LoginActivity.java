package com.jets.mytrips.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jets.mytrips.R;
import com.jets.mytrips.beans.Note;
import com.jets.mytrips.beans.Trip;
import com.jets.mytrips.beans.User;
import com.jets.mytrips.controllers.TripController;
import com.jets.mytrips.controllers.UserController;
import com.jets.mytrips.database.DBAdapter;
import com.jets.mytrips.services.AlarmManager;
import com.jets.mytrips.services.Switcher;
import com.jets.mytrips.services.Validator;
import com.jets.mytrips.services.VolleyCallback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, Switcher {

    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 6;
    private UserController userController;
    private TripController tripController;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userController = UserController.getInstance(this);
        tripController = TripController.getInstance(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        // Customize google sign-in button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.clearDefaultAccountAndReconnect();
                // Disable the user interaction
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

                Validator validator = Validator.getInstance();
                boolean validationFlag = true;

                if (!validator.isValidInput(email)) {
                    validationFlag = false;
                }
                if (!validator.isValidInput(password)) {
                    validationFlag = false;
                }

                if (!validationFlag) {
                    Toast.makeText(LoginActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validator.isValidEmail(email)) {
                    Toast.makeText(LoginActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show progress bar
                progressBar.setVisibility(View.VISIBLE);
                // Disable the user interaction
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                userController.login(email, password, false, new VolleyCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        // Get user trips from backend and insert them in SQLite database
                        getUserTrips((User) response);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);
                        // Get user interaction back
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // Show the error message
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
            if (resultCode == RESULT_CANCELED) {
                // Get user interaction back
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount userAccount = result.getSignInAccount();
            final User user = new User(userAccount.getId(), userAccount.getId(), userAccount.getGivenName() + " " + userAccount.getFamilyName());

            /* Send user data to the backend server */

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            userController.login(user.getEmail(), user.getPassword(), true, new VolleyCallback() {
                @Override
                public void onSuccess(Object response) {
                    // Get user trips from backend and insert them in SQLite database
                    getUserTrips((User) response);
                }

                @Override
                public void onFailure(String errorMessage) {
                    if (errorMessage != null && !errorMessage.equals("Invalid email or password")) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);
                        // Get user interaction back
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // Show the error message
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    UserController.getInstance(LoginActivity.this).registerUser(user,
                            new VolleyCallback() {
                                @Override
                                public void onSuccess(Object response) {

                                    userController.login(user.getEmail(), user.getPassword(), true, new VolleyCallback() {
                                        @Override
                                        public void onSuccess(Object response) {
                                            // Get user trips from backend and insert them in SQLite database
                                            getUserTrips((User) response);
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            // Hide progress bar
                                            progressBar.setVisibility(View.GONE);
                                            // Get user interaction back
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            // Show the error message
                                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    // Hide progress bar
                                    progressBar.setVisibility(View.GONE);
                                    // Get user interaction back
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    // Show the error message
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }

    @Override
    public void switchToCurrentTripsActivity(String userFullName) {
        Intent intent = new Intent(LoginActivity.this, CurrentTripsActivity.class);
        intent.putExtra("username", userFullName);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
    }

    private void getUserTrips(final User user) {

        tripController.getUserTrips(user.getId(), new VolleyCallback() {
            @Override
            public void onSuccess(Object response) {
                DBAdapter dbAdapter = new DBAdapter(getBaseContext());
                for (Trip trip : (ArrayList<Trip>) response) {
                    if (AddOrEditTrip.checkTimeUpcoming(trip.getDate(), trip.getTime())) {
                        trip.setStatus("done");
                        trip.setDone(1);
                    } else {
                        AlarmManager.setTask(trip, getApplicationContext(), calcTimeUpcoming(trip.getDate(), trip.getTime()));
                    }
                    dbAdapter.addTrip(trip);
                    ArrayList<Note> tripNotes = trip.getNotes();
                    if (!tripNotes.isEmpty()) {
                        for (Note note : tripNotes) {
                            dbAdapter.addNote(note);
                        }
                    }
                }
                tripController.setUserTripsSynchronized(true);
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Switch to CurrentTripsActivity activity
                switchToCurrentTripsActivity(user.getFullName());
            }

            @Override
            public void onFailure(String errorMessage) {
                tripController.setUserTripsSynchronized(true);

                if (!errorMessage.equals("")) {
                    // Hide progress bar
                    progressBar.setVisibility(View.GONE);
                    // Get user interaction back
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    // Show the error message
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Switch to CurrentTripsActivity activity
                switchToCurrentTripsActivity(user.getFullName());
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Something wrong happened, cannot connect to google services", Toast.LENGTH_SHORT).show();
    }

    private long calcTimeUpcoming(String date, String time) {

        long difference;
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date dateToValidate = null;
        try {
            dateToValidate = df.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar nextDay = new GregorianCalendar();
        nextDay.setTime(dateToValidate);
        difference = nextDay.getTimeInMillis() - System.currentTimeMillis();
        return difference;
    }
}
