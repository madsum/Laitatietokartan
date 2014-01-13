package fi.espoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements OnMapClickListener, View.OnTouchListener  {
	private GoogleMap googleMap;
	private final NotesDbAdapter mDbHelper = new NotesDbAdapter(this);
	LocationInfo lacationInfo;
	private LatLng position;
	private HashMap<Marker, EventInfo> allMarkInfo;
	List<EventInfo> allEventInfo;
	boolean currentLocation = false;
	Dialog dialog;
	TextView remove_marker;
	TextView add_note;
	TextView edit_note;
	Marker selectedMarker = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
        LocationLibrary.initialiseLibrary(getBaseContext(), 60 * 1000, 2 * 60 * 1000, "mobi.littlefluffytoys.littlefluffytestclient");
        lacationInfo = (new LocationInfo(this));	

		// get action bar
		ActionBar actionBar = getActionBar();
		// Enabling Up / Back navigation
		actionBar.setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			position = new LatLng(extras.getDouble("lat"),
					extras.getDouble("lon"));
			currentLocation = extras.getBoolean("bool");									
		}
		if(currentLocation)
		{
			// current location cliked
			position = new LatLng(lacationInfo.lastLat, lacationInfo.lastLong );
		}

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}		
		mDbHelper.open();
		allMarkInfo = new HashMap<Marker, EventInfo>();
		placeAllMarker();		
		setMapInfoWindowListener();
	}
	
	private void updateCurrentLocation(final LocationInfo locationInfo)
	{
		if (locationInfo.anyLocationDataReceived()) 
		{
			position = new LatLng(locationInfo.lastLat, locationInfo.lastLong );
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateCurrentLocation(new LocationInfo(this));
		//placeAllMarker();
	}

	private void placeAllMarker() {
		Marker marker = null;
		boolean marked = false;
		allEventInfo = new ArrayList<EventInfo>();
		mDbHelper.getLocationInfo(allEventInfo);

		for (int i = 0; i < allEventInfo.size(); i++) {
			
			if(!allEventInfo.get(i).getPosition().equals(position))
			{
				marker = googleMap.addMarker(new MarkerOptions().position(
								allEventInfo.get(i).getPosition()).title(
								allEventInfo.get(i).getInfo()));
				allMarkInfo.put(marker, allEventInfo.get(i));
			}
			else
			{
				if(currentLocation)
				{
					marker = googleMap.addMarker(new MarkerOptions().position(position).icon(
							BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
							.title("nykyinen sijainti"));					
					marked = true;			
				}
				else
				{
					marker = googleMap.addMarker(new MarkerOptions().position(position).icon(
							BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
							.title(allEventInfo.get(i).getInfo()));
				}
				
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(position).zoom(16).build();

				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));	
				
				allMarkInfo.put(marker, allEventInfo.get(i));
			}
		}
		
		if(!marked)
		{
			if(currentLocation)
			{
				marker = googleMap.addMarker(new MarkerOptions().position(position).icon(
						BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
						.title("nykyinen sijainti"));
				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(position).zoom(16).build();

				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
		if (googleMap != null) {
			googleMap.setOnMapClickListener(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_map_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.location_found:
			updateCurrentLocation(new LocationInfo(this));
			setCurrentLocation(position);
			break;
		
		case R.id.edit:
			startNoteEditor(position);
			break;

		case R.id.all_note:
			startNoteList();
			break;
		
		case R.id.all_marker:
			placeAllMarker();
			break;
		
		case R.id.refresh:
			googleMap.clear();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void startNoteEditor(LatLng aposition) {
		Intent intent = new Intent(this, NoteEdit.class);
		Bundle b = new Bundle();
		b.putDouble("lat", aposition.latitude);
		b.putDouble("lon", aposition.longitude);
		intent.putExtras(b);
		startActivity(intent);
	}

	private void startNoteList() {
		Intent intent = new Intent(this, NoteList.class);
		startActivity(intent);
	}
	
	private void setCurrentLocation(LatLng aposition)
	{
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(aposition).zoom(16).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		googleMap.addMarker(new MarkerOptions().position(aposition).icon(
				BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("nykyinen sijainti"));
	}

	@Override
	public void onMapClick(LatLng aposition) {
		position = aposition;		
		selectedMarker = googleMap.addMarker(new MarkerOptions().position(position).icon(
				BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
				.title("Napsauttaa"));
	}

	private void setMapInfoWindowListener() {
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				showUpDialog(marker);
			}
		});
	}

	void showUpDialog(Marker amarker) {
		final Marker marker = amarker;  
		final EventInfo eventInfo = allMarkInfo.get(marker);
		// set up dialog
		dialog = new Dialog(this);
		
		if(eventInfo == null)
		{
			dialog.setContentView(R.layout.dialog_context);
			dialog.setTitle("Poistaa " + " ");
			dialog.setCancelable(true);
			selectedMarker =  amarker;
			
			//dialog.
			/*
			remove_marker = (TextView) dialog.findViewById(R.id.remove_marker);
			remove_marker.setOnTouchListener(this);
			add_note = (TextView) dialog.findViewById(R.id.add_note);
			add_note.setOnTouchListener(this);
			edit_note = (TextView) dialog.findViewById(R.id.edit_note);
			edit_note.setOnTouchListener(this);
			*/

			Button remove_marker = (Button) dialog.findViewById(R.id.remove_marker);   
		    remove_marker.setOnClickListener(new View.OnClickListener() {
					   @Override
					   public void onClick(View v) 
					   {
						   marker.remove();
						   dialog.dismiss();
						   //removeMarker(marker);
						   //currentLocationMap();
					   }
					});
		    
			Button btn_new_note = (Button) dialog.findViewById(R.id.btn_new_note);   
			btn_new_note.setOnClickListener(new View.OnClickListener() {
					   @Override
					   public void onClick(View v) 
					   {
						   startNoteEditor(marker.getPosition());
						   dialog.dismiss();
						   //removeMarker(marker);
						   //currentLocationMap();
					   }
					});
		       
		      
		      
		    dialog.show();
			return;
		}
			

		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Otsikko: " + eventInfo.getInfo());
		dialog.setCancelable(true);
		// there are a lot of settings, for dialog, check them all out!
		// set up text
		TextView text = (TextView) dialog.findViewById(R.id.DialogText);
		text.setText(" Tiedot: " + eventInfo.getDate());
		// now that the dialog is set up, it's time to show it
		  //set up button
        Button delted_note = (Button) dialog.findViewById(R.id.delted_note);
        delted_note.setOnClickListener(new View.OnClickListener()  {
        @Override
            public void onClick(View v) {
                //dialog.cancel();
        		marker.remove();
        		mDbHelper.deleteNote(eventInfo.getId());
			    dialog.dismiss();
			   
            }
        });
        
        Button modify_note = (Button) dialog.findViewById(R.id.modify_note);
        modify_note.setOnClickListener(new View.OnClickListener()  {
        @Override
            public void onClick(View v) {
                //dialog.cancel();
        		editNote(eventInfo.getId());
			    dialog.dismiss();
            }
        });
        
        
        
		dialog.show();
	}
	
	private void editNote(long id)
	{
		Intent i = new Intent(this, NoteEdit.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, 1);				
	}

	@Override 
	  public boolean onTouch(View v, MotionEvent event) {
		
		if(v == remove_marker)
		{
			if(selectedMarker != null)
			{
				selectedMarker.remove();
				selectedMarker = null;
			}
			Log.d("asf", "fasdf");
		}
		else if (v == add_note)
		{
			startNoteEditor(selectedMarker.getPosition());
			Log.d("asf", "fasdf");
		}
		else if (v == edit_note)
		{
			Log.d("asf", "fasdf");
			
		}
		dialog.dismiss();
	    // check which textview it is and do what you need to do

	    // return true if you don't want it handled by any other touch/click events after this
	    return true; 
	  } 	
	
	private void removeMarker(Marker marker)
	{		
		marker.remove();
	}
}
