package scu.csci187.fall2018.mealtracker.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import scu.csci187.fall2018.mealtracker.Classes.SQLiteUserManager;
import scu.csci187.fall2018.mealtracker.R;

public class LoginActivity extends AppCompatActivity {

    Button loginButton, createAccountButton;
    EditText emailInput, pwInput;

    Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        /* TEST STUFF

         */

        go = findViewById(R.id.goToTest);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), TestActivity.class);
                startActivity(intent);
            }
        });

        /*
            END TEST STUFF
         */

        loginButton = findViewById(R.id.login);
        createAccountButton = findViewById(R.id.createAccount);
        emailInput = findViewById(R.id.email);
        pwInput = findViewById(R.id.pw);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, pw;

                email = emailInput.getText().toString();
                pw = pwInput.getText().toString();
                if(loginSuccessful(email, pw)) {

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    // instantiate user obj, send it in bundle
                    startActivity(intent);
                }
                else {
                    Toast invalidInput = Toast.makeText(LoginActivity.this,
                            "E-mail/password is not valid", Toast.LENGTH_SHORT);
                    invalidInput.setGravity(Gravity.CENTER, 0,60);
                    invalidInput.show();
                    emailInput.setText("");
                    pwInput.setText("");
                }

            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    // to add if more time: salt + hashing function for password
    // store only hashed pw in DB, hash inputtedPw and then check against DB
    boolean loginSuccessful(String email, String pw) {
        SQLiteUserManager myDB = new SQLiteUserManager(this);
        boolean success = myDB.login(email, pw);
        //if (success)
            //myDB.setEmail(email);
        myDB.close();
        return success;
    }
}

