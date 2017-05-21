package com.jets.mytrips.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jets.mytrips.R;
import com.jets.mytrips.beans.User;
import com.jets.mytrips.controllers.UserController;
import com.jets.mytrips.services.Validator;
import com.jets.mytrips.services.VolleyCallback;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        LayoutInflater inflater = getLayoutInflater();
        getWindow().addContentView(inflater.inflate(R.layout.activity_register, null),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
                String fullName = ((EditText) findViewById(R.id.fullNameEditText)).getText().toString();
                String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
                String confirmPassword = ((EditText) findViewById(R.id.confirmPasswordEditText)).getText().toString();

                Validator validator = Validator.getInstance();
                boolean validationFlag = true;

                if (!validator.isValidInput(email)) {
                    validationFlag = false;
                }

                if (!validator.isValidInput(fullName)) {
                    validationFlag = false;
                }

                if (!validator.isValidInput(password)) {
                    validationFlag = false;
                }

                if (!validator.isValidInput(confirmPassword)) {
                    validationFlag = false;
                }

                if (!validationFlag) {
                    Toast.makeText(RegisterActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validator.isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send user data to the backend server
                UserController.getInstance(RegisterActivity.this).registerUser(new User(email, password, fullName),
                        new VolleyCallback() {
                            @Override
                            public void onSuccess(Object response) {
                                RegisterActivity.this.finish();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ImageView) findViewById(R.id.photo_1)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_2)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_3)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_4)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_5)).setImageDrawable(null);
        ((ImageView) findViewById(R.id.photo_6)).setImageDrawable(null);
        Runtime.getRuntime().gc();
    }
}
