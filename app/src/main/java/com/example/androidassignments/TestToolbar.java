package com.example.androidassignments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TestToolbar extends AppCompatActivity {

    public static String messageCaptured = "You selected item 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Floating action button clicked", Snackbar.LENGTH_LONG)
                        .setAction("Do nothing", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the Toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int id = mi.getItemId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case R.id.action_one:
                Log.d("Toolbar", "Option 1 selected");
                Snackbar.make(findViewById(R.id.toolbarLayout), messageCaptured, Snackbar.LENGTH_LONG)
                        .show();
                break;
            case R.id.action_two:
                // Start an activity
                Log.d("Toolbar", "Option 2 selected");
                builder.setTitle("Do you want to go back?"); // Text

                // Add the buttons
                builder.setPositiveButton(R.string.ok, new /*positive Button*/
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                finish();
                            }
                        });

                builder.setNegativeButton(R.string.cancel, new /*negative Button */
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog, do nothing
                                Intent intent = new Intent(TestToolbar.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

                case R.id.action_three:
                    Log.d("Toolbar", "Option 3 selected");
                    // Get the layout inflater
                    LayoutInflater inflater = this.getLayoutInflater();
                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    View mView = inflater.inflate(R.layout.dialog_signin, null);
                    builder.setView(mView)
                    // Add the buttons
                        .setPositiveButton(R.string.ok, new /*positive Button*/
                            DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    EditText messageBox = (EditText) mView.findViewById(R.id.messageBox);
                                    messageCaptured = messageBox.getText().toString();
                                }
                            })
                        .setNegativeButton(R.string.cancel, new /*negative Button */
                            DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog, do nothing
                                }
                            })
                    .create() // Create the AlertDialog
                    .show();
                    break;

                    case R.id.action_about:
                        Log.d("Toolbar", "About selected");
                        Toast.makeText(this, "Version 1.0, Stephen Wahid",
                        Toast.LENGTH_LONG).show();
        }
        return true;
    }

}