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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements OnMapClickListener {
	private GoogleMap googleMap;
	private NotesDbAdapter mDbHelper;
	LocationInfo lacationInfo;
	private LatLng position;
	private HashMap<Marker, EventInfo> allMarkInfo;
	List<EventInfo> allEventInfo;
	boolean currentLocation = false;
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

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}		
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		allMarkInfo = new HashMap<Marker, EventInfo>();
		//placeAllMarker();
		
		setConfigurtion();
		if(currentLocation)
		{
			position = new LatLng(lacationInfo.lastLat, lacationInfo.lastLong);
			setCurrentLocation(position);
		}
		else
		{
			setLocation(position);			
		}
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
		allEventInfo = new ArrayList<EventInfo>();
		mDbHelper.getLocationInfo(allEventInfo);

		for (int i = 0; i < allEventInfo.size(); i++) {
			marker = googleMap.addMarker(new MarkerOptions().position(
								allEventInfo.get(i).getPosition()).title(
								allEventInfo.get(i).getInfo()));
			allMarkInfo.put(marker, allEventInfo.get(i));

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
		inflater.inflate(R.menu.map_action_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.location_found:
			setCurrentLocation(position);
			break;
			
		case R.id.:
			placeAllMarker();
			break;
			
		
		case R.id.edit:
			startNoteEditor(position);
			break;

		case R.id.all_note:
			startNoteList();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void startNoteEditor(LatLng aposition) {
		Intent intent = new Intent(this, NoteEdit.class);
		// Intent intent = new Intent(this, NoteList.class);
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

	private void setLocation(LatLng aposition) 
	{
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(aposition).zoom(16).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		googleMap.addMarker(new MarkerOptions().position(aposition).icon(
				BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
/*
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
		@Override
		public void onInfoWindowClick(Marker marker) {
			showUpDialog(marker);
		}
		});	
		*/
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

/*
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
		@Override
		public void onInfoWindowClick(Marker marker) {
			showUpDialog(marker);
		}
		});
		*/
	}

	@Override
	public void onMapClick(LatLng aposition) {
		position = aposition;
		googleMap.addMarker(new MarkerOptions().position(position).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		//startNoteEditor(position);
	}

	private void setConfigurtion() {
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				showUpDialog(marker);
			}
		});
	}

	void showUpDialog(Marker marker) {
		EventInfo eventInfo = allMarkInfo.get(marker);
		// set up dialog
		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Otsikko: " + eventInfo.getInfo());
		dialog.setCancelable(true);
		// there are a lot of settings, for dialog, check them all out!
		// set up text
		TextView text = (TextView) dialog.findViewById(R.id.DialogText);
		text.setText(" Tiedot: " + eventInfo.getDate());

		// now that the dialog is set up, it's time to show it
		dialog.show();
	}
}
