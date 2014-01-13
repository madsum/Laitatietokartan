package fi.espoo;

import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        Button on_map = (Button)findViewById(R.id.curr_map);
        on_map.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View v) 
			   {
				   currentLocationMap();
			   }
			});
        
        Button on_all_note = (Button)findViewById(R.id.all_note);
        on_all_note.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View v) 
			   {
				   startAllNote();
			   }
			});      
        
        Button email = (Button)findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View v) 
			   {
				   sendEmail();
			   }
			});  
		
	}
	
	private void sendEmail()
	{
		Intent intent = new Intent(this, Email.class);
		startActivity(intent);		
	}
	
	private void startAllNote()
	{
		Intent intent = new Intent(this, NoteList.class);
		startActivity(intent);
	}
	private void currentLocationMap()
	{
		Intent intent = new Intent(MainActivity.this, MapActivity.class);
		Bundle b = new Bundle();
		b.putBoolean("bool", true);
		intent.putExtras(b);
		startActivity(intent);
	}

	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.activity_main_actions, menu);
	 
	        return super.onCreateOptionsMenu(menu);
	    }
	 
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Take appropriate action for each action item click
	        switch (item.getItemId()) {
	        case R.id.location_found:
	        	currentLocationMap();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }	 
	
	 @Override
	public void onResume() {
	        super.onResume();
	 }	
}
