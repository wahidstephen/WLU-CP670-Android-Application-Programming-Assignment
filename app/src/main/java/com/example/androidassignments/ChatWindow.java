package com.example.androidassignments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "ChatWindow";

    ListView listView;
    EditText textInput;
    Button sendButton;
    ArrayList<String> messages = new ArrayList<>();
    ChatDatabaseHelper dbHelper;

    private class ChatAdapter extends ArrayAdapter<String> {

        public ChatAdapter(Context ctx) {
            super(ctx, 0);
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return messages.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;
            if(position % 2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText(getItem(position)); // get the string at position
            return result;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        dbHelper = new ChatDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ChatDatabaseHelper.TABLE_MESSAGES;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            String message = cursor.getString( cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE) );
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" +  message);
            messages.add(message);
            cursor.moveToNext();
        }
        Log.i(ACTIVITY_NAME, "Cursor's column count = " + cursor.getColumnCount() );
        for (int i = 0; i < cursor.getColumnCount(); i++)
            Log.i(ACTIVITY_NAME, "Cursor's column name = " + cursor.getColumnName(i) );
        cursor.close();

        listView = (ListView) findViewById(R.id.chatView);
        textInput = (EditText) findViewById(R.id.chatText);
        sendButton = (Button) findViewById(R.id.sendBtn);

        // in this case, “this” is the ChatWindow, which is-A Context object
        ChatAdapter messageAdapter = new ChatAdapter( this );
        listView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Log.i(ACTIVITY_NAME, "User clicked Start Chat");
                messages.add(textInput.getText().toString());
                ContentValues values = new ContentValues();
                values.put(ChatDatabaseHelper.KEY_MESSAGE, textInput.getText().toString());
                db.insert(ChatDatabaseHelper.TABLE_MESSAGES, null, values);
                messageAdapter.notifyDataSetChanged(); // this restarts the process of getCount()/getView()
                textInput.setText("");
            }
        });

    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}