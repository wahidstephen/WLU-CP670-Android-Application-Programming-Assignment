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
    TextView messageID, messageText;

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
        messageID = (TextView) view.findViewById(R.id.message_id_view);
        messageText = (TextView) view.findViewById(R.id.message_text_view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Long messageId = bundle.getLong("database_id");
            String message = bundle.getString("message_string");
            messageID.setText(String.valueOf(messageId));
            messageText.setText(message);
            Button deleteButton = (Button) view.findViewById(R.id.delete_message);
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener((View v) -> {
                // Do something in response to button click
                if (chatActivity == null) {
                    Intent data = new Intent();
                    data.putExtra("database_id", messageId);
                    data.putExtra("message_string", message);
                    getActivity().setResult(200, data);
                    getActivity().finish();
                }
                else {
                    chatActivity.deleteMessages(messageId, message);
                    messageID.setText("");
                    messageText.setText("");
                    deleteButton.setVisibility(View.INVISIBLE);
                }
            });
        }
        return view;
    }
}