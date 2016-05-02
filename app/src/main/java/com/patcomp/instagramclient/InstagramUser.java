package com.patcomp.instagramclient;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InstagramUser extends Activity {

    private String username;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_user);

       username = getIntent().getExtras().getString("username");
        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(username);

    }

}
