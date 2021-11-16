package com.example.androidassignments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MessageFragment extends Fragment {

    ChatWindow chatActivity;

    public MessageFragment(ChatWindow chatActivity) {
        // Required empty public constructor
        this.chatActivity = chatActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ((TextView) view.findViewById(R.id.message_id_view)).setText(Long.toString(bundle.getLong("database_id")));
            ((TextView) view.findViewById(R.id.message_text_view)).setText(bundle.getString("message_string"));
            Button deleteButton = (Button) view.findViewById(R.id.delete_message);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Do something in response to button click
                    Intent data = new Intent();
                    data.putExtra("database_id", bundle.getLong("database_id"));
                    getActivity().setResult(Activity.RESULT_OK, data);
//                    getActivity().finish();
                }
            });
        }
        return view;
    }
}