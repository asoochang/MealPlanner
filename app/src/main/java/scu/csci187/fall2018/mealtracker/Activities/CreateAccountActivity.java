package scu.csci187.fall2018.mealtracker.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import scu.csci187.fall2018.mealtracker.Classes.SQLiteUserManager;
import scu.csci187.fall2018.mealtracker.R;

public class CreateAccountActivity extends AppCompatActivity {

    EditText inFirstName, inLastName, inEmail, inPassword, inHeight, inWeight;
    Button buttonRegister, buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_layout);

        inFirstName = findViewById(R.id.inputFirstName);
        inLastName = findViewById(R.id.inputLastName);
        inHeight = findViewById(R.id.inputHeight);
        inWeight = findViewById(R.id.inputWeight);
        inEmail = findViewById(R.id.inputEmail);
        inPassword = findViewById(R.id.inputPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonBack = findViewById(R.id.buttonBack);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname, lname, email, pw;
                int height, weight;

                fname = inFirstName.getText().toString();
                lname = inLastName.getText().toString();
                email = inEmail.getText().toString();
                pw = inPassword.getText().toString();
                height = Integer.parseInt(inHeight.getText().toString());
                weight = Integer.parseInt(inWeight.getText().toString());

                tryToRegister(fname, lname, height, weight, email, pw);
                finish(); // go back to LoginActivity
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // go back to LoginActivity
            }
        });
    }

    private void tryToRegister(String fname, String lname, int height, int weight,
                                  String email, String pw) {
        SQLiteUserManager myDB = new SQLiteUserManager(this);
        myDB.initUser(email, pw);
        myDB.close();
    }
}
