package com.viintro.Viintro.Login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.viintro.Viintro.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edt_old_Password, edt_new_Password;
    private Button btn_chng_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initializeUI();
    }

    public void initializeUI(){

        edt_old_Password = (EditText) findViewById(R.id.edt_old_Password);
        edt_new_Password = (EditText) findViewById(R.id.edt_new_Password);
        btn_chng_password = (Button) findViewById(R.id.btn_chng_password);

    }
}
