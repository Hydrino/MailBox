package com.ninad.ninhydrin.mailbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ninad.ninhydrin.signup.SignUpActivity;


public class StartActivityChooser extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences God = getSharedPreferences(getString(R.string.god), MODE_PRIVATE);
        boolean isUserRegistered = God.getBoolean(getString(R.string.is_user_registered), false);
        if (isUserRegistered)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        else
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        finish();
    }
}
