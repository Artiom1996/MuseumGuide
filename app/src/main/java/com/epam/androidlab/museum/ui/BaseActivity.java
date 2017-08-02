package com.epam.androidlab.museum.ui;


import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.epam.androidlab.museum.R;

public class BaseActivity extends AppCompatActivity {
    protected void showError(int resourceId, View root){
        Snackbar.make(root, resourceId, Snackbar.LENGTH_LONG).show();
    }

    protected void showDefaultError(View root){
        showError(R.string.error_failure, root);
    }
}
