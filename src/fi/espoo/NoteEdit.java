package fi.espoo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class NoteEdit extends Activity {

	public static int numTitle = 1;
	public static String curDate = "";
	public static String curText = "";
	private EditText mTitleText;
	private EditText mBodyText;
	private TextView mDateText;
	private Long mRowId;
	private Cursor note;
	private NotesDbAdapter mDbHelper;
	private LatLng position = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.note_edit);
		setTitle(R.string.app_name);

		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		mDateText = (TextView) findViewById(R.id.notelist_date);

		long msTime = System.currentTimeMillis();
		Date curDateTime = new Date(msTime);

		SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");
		curDate = formatter.format(curDateTime);

		mDateText.setText("" + curDate);

		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			try
			{
				mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
					: null;
			}
			catch(Exception ex  )
			{
				
				mRowId = null;
			}
		}
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			try
			{
				position = new LatLng(extras.getDouble("lat"), extras.getDouble("lon"));
			}
			catch(Exception ex)
			{
				Log.d("error", "no poistion is given in bundle");
			}
		}

		populateFields();
		
		// get action bar
		ActionBar actionBar = getActionBar();
		// Enabling Up / Back navigation
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	public static class LineEditText extends EditText {
		private Rect mRect;
		private Paint mPaint;
		// we need this constructor for LayoutInflater
		public LineEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			mRect = new Rect();
			mPaint = new Paint();
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setColor(Color.BLUE);
		}

		@Override
		protected void onDraw(Canvas canvas) {

			int height = getHeight();
			int line_height = getLineHeight();

			int count = height / line_height;

			if (getLineCount() > count)
				count = getLineCount();

			Rect r = mRect;
			Paint paint = mPaint;
			int baseline = getLineBounds(0, r);

			for (int i = 0; i < count; i++) {

				canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1,
						paint);
				baseline += getLineHeight();

				super.onDraw(canvas);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.noteedit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.delete:
			if (note != null) {
				note.close();
				note = null;
			}
			if (mRowId != null) {
				mDbHelper.deleteNote(mRowId);
			}
			finish();
			return true;
		case R.id.save:
			saveState();
			finish();

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void saveState() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		String lat = String.valueOf(position.latitude);
		String lon = String.valueOf(position.longitude);

		if (mRowId == null || mRowId == 0) {
			long id = mDbHelper.createNote(title, body, curDate, lat, lon);
			if (id > 0) {
				mRowId = id;
			} else {
				Log.e("saveState", "failed to create note");
			}
		} else {
			if (!mDbHelper.updateNote(mRowId, title, body, curDate)) {
				Log.e("saveState", "failed to update note");
			}
		}
		Intent intent = new Intent(this, NoteList.class);
		startActivity(intent);
	}

	private void populateFields() {
		if (mRowId != null && mRowId != 0 ) {
			note = mDbHelper.fetchNote(mRowId);
			mTitleText.setText(note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
			mBodyText.setText(note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
			curText = note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
		}
	}	
}
