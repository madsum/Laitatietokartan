package fi.espoo;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.google.android.gms.maps.model.LatLng;

public class NoteList extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int MAP_ID = Menu.FIRST;
	private static final int EDIT_ID = MAP_ID+1;
	private static final int DELETE_ID = MAP_ID+2;
	private int mNoteNumber = 1;
	private NotesDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());
		// get action bar
		ActionBar actionBar = getActionBar();
		// Enabling Up / Back navigation
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.notes_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
		
		case R.id.location_found:
			startCurrentMap();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
	return true;
	}
	
	private void startCurrentMap()
	{
		Intent intent = new Intent(this, MapActivity.class);
		Bundle b = new Bundle();
		b.putBoolean("bool", true);
		intent.putExtras(b);
		startActivity(intent);		
	}

	private void createNote() {
		Intent i = new Intent(this, NoteEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		LatLng ll = mDbHelper.getLatLon(id);
		
		if( ll != null)
		{
			// start selected map
			showSelectedMap(id);
		}
	}
	
	private void showSelectedMap(long id)
	{
		LatLng ll = mDbHelper.getLatLon(id);
		if( ll != null)
		{
			Intent intent = new Intent(this, MapActivity.class);
			Bundle b = new Bundle();
			b.putDouble("lat", ll.latitude );
			b.putDouble("lon", ll.longitude);
			intent.putExtras(b);
			startActivity(intent);
		}
	}
	
	private void editNote(long id)
	{
		Intent i = new Intent(this, NoteEdit.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);				
	}

	@SuppressWarnings("deprecation")
	private void fillData() {
		// Get all of the notes from the database and create the item list
		Cursor notesCursor = mDbHelper.fetchAllNotes();
		//startManagingCursor(notesCursor);

		String[] from = new String[] { NotesDbAdapter.KEY_TITLE,
				NotesDbAdapter.KEY_DATE };
		int[] to = new int[] { R.id.text1, R.id.date_row };

		// Now create an array adapter and set it to display using our row
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.notes_row, notesCursor, from, to);
		setListAdapter(notes);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MAP_ID, 0, R.string.n_yt_kartta);
		menu.add(0, EDIT_ID, 0, R.string.muokata);
		menu.add(0, DELETE_ID, 0, R.string.poistaa_tietoja);    
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case MAP_ID:
			showSelectedMap(info.id);
			break;
			
		case EDIT_ID:
			editNote(info.id);
			break;
		
		case DELETE_ID:
			mDbHelper.deleteNote(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
}
