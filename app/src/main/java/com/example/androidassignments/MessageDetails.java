package com.example.androidassignments;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MessageDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            MessageFragment messageFragment = new MessageFragment(null);
            messageFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.message_frame, messageFragment);
            ft.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(MessageDetails.this, ChatWindow.class);
            intent.putExtra("resultCode", 420);
            intent.putExtras(data);
            startActivity(intent);
        }
    }
}