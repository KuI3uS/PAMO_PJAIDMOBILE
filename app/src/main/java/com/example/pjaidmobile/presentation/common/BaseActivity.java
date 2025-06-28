package com.example.pjaidmobile.presentation.common;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.pjaidmobile.R;

/**
 * BaseActivity - the base activity containing the fragment container.
 * Defaults to loading LoginFragment on startup.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        loadFragment(new com.example.pjaidmobile.presentation.features.auth.LoginFragment());
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
