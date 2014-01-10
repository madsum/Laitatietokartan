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
	

	List<Address> currn_addresses = null;
	//double latitude;
	//double longitude;
	LatLng position;
	LocationInfo lacationInfo;
	//Location location;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        LocationLibrary.initialiseLibrary(getBaseContext(), 60 * 1000, 2 * 60 * 1000, "mobi.littlefluffytoys.littlefluffytestclient");
        lacationInfo = (new LocationInfo(this));	
        
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
		b.putDouble("lat", position.latitude);
		b.putDouble("lon", position.longitude);
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
	        // cancel any notification we may have received from TestBroadcastReceiver
	        //((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1234);
	        refreshDisplay();
	 }
	
	
    private void refreshDisplay() {
    	refreshDisplay(new LocationInfo(this));
    }

    private void refreshDisplay(final LocationInfo locationInfo) {

        if (locationInfo.anyLocationDataReceived()) {
        	//latitude = locationInfo.lastLat;
        	//longitude = locationInfo.lastLong;
        	position = new LatLng(locationInfo.lastLat, locationInfo.lastLong );
        	// we consturect it here becasue more accurate location here.
            //location = new Location(lacationInfo);
        	
        	if(covert_to_address(locationInfo.lastLat, locationInfo.lastLong))
        	{
        		String street = currn_addresses.get(0).getAddressLine(0);
        		String city = currn_addresses.get(0).getAddressLine(1);
        		String country = currn_addresses.get(0).getAddressLine(2);
        	}
        }
     }
    
    private boolean  covert_to_address(double latitude, double longitude)
    {
    	Geocoder geocoder;
    	geocoder = new Geocoder(this, Locale.getDefault());
    	try
    	{
    		currn_addresses = geocoder.getFromLocation(latitude, longitude, 1);
    		return true;
    	}
    	catch(Exception ex)
    	{
    		return false;
    		
    	}
    	
    }
}
