package fi.espoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDbAdapter {

	public static final String KEY_TITLE = "title";
	public static final String KEY_DATE = "date";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";

	/*
	 * public static final String KEY_BODY = "body"; public static final String
	 * KEY_TITLE = "title"; public static final String KEY_DATE = "date"; public
	 * static final String KEY_DETAILS = "details"; public static final String
	 * KEY_ROWID = "id"; public static final String KEY_LAT = "lat"; public
	 * static final String KEY_LON = "lat";
	 */
	private static final String TAG = "NotesDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private LocationInfo lacationInfo;

	/**
	 * Database creation sql statement
	 */

	private static final String DATABASE_CREATE = "create table notes (_id integer primary key autoincrement, "
			+ "title text not null, body text not null, date text not null, lat text not null, lng text not null);";

	/*
	 * private static final String DATABASE_CREATE2 ="CREATE TABLE `markers` ("
	 * + "`id` integer primary key autoincrement, "+
	 * "`title` VARCHAR( 60 ) NOT NULL ,"+ "`details` VARCHAR( 80 ),"+
	 * "`date` text, "+ " `lat` FLOAT( 10, 6 ),"+ "`lng` FLOAT( 10, 6 ))";
	 */
	/*
	 * private static final String DATABASE_CREATE3 ="CREATE TABLE markers (" +
	 * "id integer primary key autoincrement, "+
	 * "title VARCHAR( 60 ) NOT NULL ,"+ "details VARCHAR( 80 ),"+
	 * "date text, "+ "lat FLOAT( 10, 6 ),"+ "lng FLOAT( 10, 6 ))";
	 */

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "notes";
	// private static final String DATABASE_TABLE2 = "markers";
	private static final int DATABASE_VERSION = 2;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			try {
				// db.execSQL(DATABASE_CREATE3);
				db.execSQL(DATABASE_CREATE);
			} catch (SQLException ex) {
				Log.d("TAG", ex.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			// db.execSQL("DROP TABLE IF EXISTS markers");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public NotesDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public NotesDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new note using the title and body provided. If the note is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param title
	 *            the title of the note
	 * @param body
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */
	public long createNote(String title, String body, String date, String lat,
			String lon) {
		// ContentValues initialValues = new ContentValues();
		ContentValues initialValues2 = new ContentValues();

		// INSERT INTO `markers` (`name`, `address`, `lat`, `lng`) VALUES
		// ('Frankie Johnnie & Luigo Too','939 W El Camino Real, Mountain View,
		// CA','37.386339','-122.085823');
		/*
		 * initialValues.put(KEY_TITLE, title); initialValues.put(KEY_DETAILS,
		 * body); initialValues.put(KEY_DATE, date); initialValues.put(KEY_LAT,
		 * lat); initialValues.put(KEY_LON, lon);
		 */
		initialValues2.put(KEY_TITLE, title);
		initialValues2.put(KEY_BODY, body);
		initialValues2.put(KEY_DATE, date);
		initialValues2.put(KEY_LAT, lat);
		initialValues2.put(KEY_LNG, lon);

		long ret = 0;
		long ret2 = 0;

		try {
			ret = mDb.insert(DATABASE_TABLE, null, initialValues2);
			// ret2 = mDb.insert(DATABASE_TABLE3, null, initialValues2);
		} catch (Exception ex) {
			Log.d("asfasf", ex.getMessage());
		}
		return ret;

		// return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	public long createNote2(String title, String body, String date) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_BODY, body);
		initialValues.put(KEY_DATE, date);

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId
	 *            id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteNote(long rowId) {

		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllNotes() {

		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
				KEY_BODY, KEY_DATE, KEY_LAT, KEY_LNG }, null, null, null, null,
				null);
	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchNote(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
				KEY_BODY, KEY_DATE, KEY_LAT, KEY_LNG },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public void fillData() {
		String msg = new String();
		Cursor notesCursor = fetchAllNotes();
		// startManagingCursor(notesCursor);
		int count = 1;
		while (notesCursor.moveToNext()) {
			String title = notesCursor.getString(notesCursor
					.getColumnIndex(KEY_TITLE));
			String body = notesCursor.getString(notesCursor
					.getColumnIndex(KEY_BODY));
			String lat = notesCursor.getString(notesCursor
					.getColumnIndex(KEY_LAT));
			String lng = notesCursor.getString(notesCursor
					.getColumnIndex(KEY_LNG));
			msg += Integer.toString(count) + " " + "otsikko: " + title + " "
					+ "tiedot: " + body + "Lat: " + lat + "Lng: " + lng + "\n";
			count++;
		}
	}

	public LatLng getLatLon(long rowId) {

		Cursor cursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_LAT, KEY_LNG },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		try {
			String title = (cursor.getString(cursor
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));

			String lat = cursor.getString(cursor
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_LAT));
			String lon = (cursor.getString(cursor
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_LNG)));

			if (lat != null && lon != null) {
				double dlat = Double.parseDouble(lat);
				double dlon = Double.parseDouble(lon);
				LatLng ll = new LatLng(dlat, dlon);
				return ll;

			} else {
				return null;
			}

			// return null;
		} catch (Exception ex) {
			Log.d("Masum", ex.getMessage());
			return null;
		}
	}

	public void getLocationInfo(List<EventInfo> allEventInfo) {
		Cursor cursor = fetchAllNotes();
		
		try {
			cursor = fetchAllNotes();
		} catch (Exception ex) {
			Log.d("Masum", ex.getMessage());
			return;
		}

		LatLng ll = null;
		String title = null;

		if (cursor == null) {
			return;
		}

		if (cursor != null) {
			cursor.moveToFirst();
		}

		while (cursor.moveToNext()) {
			try {
				title = (cursor.getString(cursor
						.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
				String lat = cursor.getString(cursor
						.getColumnIndexOrThrow(NotesDbAdapter.KEY_LAT));
				String lon = (cursor.getString(cursor
						.getColumnIndexOrThrow(NotesDbAdapter.KEY_LNG)));

				if (lat != null && lon != null) {
					double dlat = Double.parseDouble(lat);
					double dlon = Double.parseDouble(lon);
					ll = new LatLng(dlat, dlon);
					// when we got ll we must add to container
					EventInfo eventInfo = new EventInfo(ll, title);
					allEventInfo.add(eventInfo);
				}
			} catch (Exception ex) {
				Log.d("Masum", ex.getMessage());
			}
		}
	}

	/**
	 * Update the note using the details provided. The note to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId
	 *            id of note to update
	 * @param title
	 *            value to set note title to
	 * @param body
	 *            value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateNote(long rowId, String title, String body, String date) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
		args.put(KEY_BODY, body);

		// This lines is added for personal reason
		args.put(KEY_DATE, date);

		// One more parameter is added for data
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}