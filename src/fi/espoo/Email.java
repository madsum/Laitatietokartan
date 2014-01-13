package fi.espoo;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class Email extends Activity {
	 
    private EditText toEmail = null;
    private EditText emailSubject = null;
    private EditText emailBody = null;
   
    private NotesDbAdapter mDbHelper;    
    private String msg;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
 
        toEmail = (EditText) findViewById(R.id.toEmail);
        emailSubject = (EditText) findViewById(R.id.subject);
        emailBody = (EditText) findViewById(R.id.emailBody);
		
        
        mDbHelper = new NotesDbAdapter (this);
		mDbHelper.open();
		msg = " ";
		fillData();
		// fill email body with database data
		emailBody.setText(msg);
        // get action bar   
        //ActionBar actionBar = getActionBar();	 
        // Enabling Up / Back navigation
        //actionBar.setDisplayHomeAsUpEnabled(true);
 
    }
    
	private void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllNotes();
        int count = 1;
        while( notesCursor.moveToNext() )
        {
        	String title = notesCursor.getString(notesCursor.getColumnIndex(mDbHelper.KEY_TITLE));
        	String body = notesCursor.getString(notesCursor.getColumnIndex(mDbHelper.KEY_BODY));
        	msg += Integer.toString(count) + " "+ "otsikko: " +  title + " " + "tiedot: " + body + "\n";
        	count++;
        }
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_action, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.clear:
            toEmail.setText("");
            emailBody.setText("");
            emailSubject.setText("");
            Intent intent = new Intent(this, MainActivity.class);
    		startActivity(intent);		
            break;
        case R.id.send:
            String to = toEmail.getText().toString();
            String subject = emailSubject.getText().toString();
            String message = emailBody.getText().toString();
 
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
            email.putExtra(Intent.EXTRA_TEXT, message);
            // need this to prompts email client only
            email.setType("message/rfc822"); 
            startActivity(Intent.createChooser(email, "Choose an Email client"));
            break;
        }
        return true;
    }
}