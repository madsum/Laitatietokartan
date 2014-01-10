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

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements OnMapClickListener {
	private GoogleMap googleMap;
	private NotesDbAdapter mDbHelper;
	private LatLng position;
	private HashMap<Marker, EventInfo> allMarkInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// get action bar
		ActionBar actionBar = getActionBar();
		// Enabling Up / Back navigation
		actionBar.setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			position = new LatLng(extras.getDouble("lat"), extras.getDouble("lon"));
		}

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
		setConfigurtion();
		setLocation(position);

		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		allMarkInfo = new HashMap<Marker, EventInfo>();
		placeAllMarker();
	}

	private void placeAllMarker() 
	{
		List<EventInfo> allEventInfo = new ArrayList<EventInfo>();
		mDbHelper.getLocationInfo(allEventInfo);

		for (int i = 0; i < allEventInfo.size(); i++) {
			Marker marker = googleMap.addMarker(new MarkerOptions().position(
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
		inflater.inflate(R.menu.activity_map_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit:
			//Location.setlatitude(latitude);
			//Location.setlongitude(longitude);
			startNoteEditor(position);
			return true;

		case R.id.all_note:
			startNoteList();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onMapClick(LatLng aposition) {
		/*
		position = (new Double(position.latitude)).doubleValue();
		longitude = (new Double(position.longitude)).doubleValue();
		Location.setlatitude(latitude);
		Location.setlongitude(longitude);
		 */
		position = aposition;
		googleMap.addMarker(new MarkerOptions().position(position).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		startNoteEditor(position);
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

	private void setLocation(LatLng aposition) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(aposition).zoom(16).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
		
		googleMap.addMarker(new MarkerOptions().position(aposition).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	}

	private void setConfigurtion() {
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				showMarkerInfo(marker);
			}
		});
	}

	private void showMarkerInfo(Marker marker) 
	{
		EventInfo eventInfo = this.allMarkInfo.get(marker);

	    Toast.makeText(getBaseContext(),

	      "The date of " + eventInfo.getInfo() + " is " + eventInfo.getDate().toString(),

	      Toast.LENGTH_LONG).show();
	}

}
