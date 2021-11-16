package com.example.androidassignments;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "ChatWindow";

    ListView listView;
    EditText textInput;
    Button sendButton;
    ArrayList<String> messages = new ArrayList<String>();
    ChatAdapter messageAdapter;
    ChatDatabaseHelper dbHelper;
    SQLiteDatabase db;
    Boolean isPhoneLayout;
    Cursor cursor;

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

        public long getItemId(int position) {
            cursor.moveToPosition(position);
           return cursor.getLong(cursor.getColumnIndex(ChatDatabaseHelper.KEY_ID));
        }
    }

    public void deleteMessages(Long messageId, String message) {
        db = dbHelper.getWritableDatabase();
        messages.remove(message);
        Log.i(ACTIVITY_NAME, "Deleting message" + message);
        db.delete(ChatDatabaseHelper.TABLE_MESSAGES, ChatDatabaseHelper.KEY_ID + "=?", new String[]{ String.valueOf(messageId) } );
        messageAdapter.notifyDataSetChanged(); // this restarts the process of getCount()/getView()
        cursor.requery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        isPhoneLayout = ((FrameLayout) findViewById(R.id.messageView) == null);

        dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Select All Query
        String selectQuery = "SELECT * FROM " + ChatDatabaseHelper.TABLE_MESSAGES;
        cursor = db.rawQuery(selectQuery, null);
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

        listView = (ListView) findViewById(R.id.chatView);
        textInput = (EditText) findViewById(R.id.chatText);
        sendButton = (Button) findViewById(R.id.sendBtn);

        // in this case, “this” is the ChatWindow, which is-A Context object
        messageAdapter = new ChatAdapter( this );
        listView.setAdapter(messageAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (isPhoneLayout) {
                    Intent intent = new Intent(ChatWindow.this, MessageDetails.class);
                    intent.putExtra("database_id", messageAdapter.getItemId(position));
                    intent.putExtra("message_string", messageAdapter.getItem(position));
                    startActivityForResult(intent, 100);
                }
                else {
                    MessageFragment messageFragment = new MessageFragment(ChatWindow.this);
                    Bundle args = new Bundle();
                    args.putLong("database_id", messageAdapter.getItemId(position));
                    args.putString("message_string", messageAdapter.getItem(position));
                    messageFragment.setArguments(args);   // (1) Communicate with Fragment using Bundle
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction(); // begin FragmentTransaction
                    ft2.replace(R.id.messageView, messageFragment);     // add Fragment
                    ft2.commit();                                  // commit FragmentTransaction
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Log.i(ACTIVITY_NAME, "User clicked Start Chat");
                messages.add(textInput.getText().toString());
                ContentValues values = new ContentValues();
                values.put(ChatDatabaseHelper.KEY_MESSAGE, textInput.getText().toString());
                db.insert(ChatDatabaseHelper.TABLE_MESSAGES, null, values);
                messageAdapter.notifyDataSetChanged(); // this restarts the process of getCount()/getView()
                cursor.requery();
                textInput.setText("");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 200){
            Bundle extras = data.getExtras();
            Long messageId = extras.getLong("database_id");
            String message = extras.getString("message_string");
            deleteMessages(messageId, message);
//            db = dbHelper.getWritableDatabase();
//            messages.remove(message);
//            Log.i(ACTIVITY_NAME, "Deleting message" + message);
//            db.delete(ChatDatabaseHelper.TABLE_MESSAGES, ChatDatabaseHelper.KEY_ID + "=?", new String[]{ messageId } );
//            messageAdapter.notifyDataSetChanged(); // this restarts the process of getCount()/getView()
//            cursor.requery();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
        dbHelper.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        messageAdapter.notifyDataSetChanged();

    }
}