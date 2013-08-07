package com.bowser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bowser.preferences.BowserPreferences;
import com.bowser.utils.BowserUtils;
import com.bowser.utils.bluetooth.BluetoothHandler;

public class Main extends Activity {

	/**
	 * The {@link SharedPreferences} for this application
	 */
	private SharedPreferences settings;
	private BowserShoulderObject bShoulderObject;
	private String debugClarity = "CCCLARITY===>";
	private BluetoothHandler bHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bHandler = new BluetoothHandler(this);
		bHandler.connectToClient();

		// Setup the settings
		if (null == this.settings)
			this.settings = PreferenceManager.getDefaultSharedPreferences(this);

		// Get the editor
		Editor settingsEditor = this.settings.edit();

		// Set defaults if they don't exit, they most likely do.
		if (!this.settings.contains(BowserUtils.BLUETOOTH_LOCAL_MAC))
			settingsEditor.putString(BowserUtils.BLUETOOTH_LOCAL_MAC,
					bHandler.mBluetoothAdapter.getAddress());
		// Pulls from bHandlers bluetooth adapter, the local mac of the phone
		if (!this.settings.contains(BowserUtils.BLUETOOTH_REMOTE_MAC))
			settingsEditor.putString(BowserUtils.BLUETOOTH_REMOTE_MAC,
					bHandler.getRemoteMacAddress());
		// Pulls from bHandlers address
		if (!this.settings.contains(BowserUtils.MODE))
			settingsEditor.putString(BowserUtils.MODE, "0");
		if (!this.settings.contains(BowserUtils.SPEED))
			settingsEditor.putString(BowserUtils.SPEED, "75");
		if (!this.settings.contains(BowserUtils.COLOR_ONE))
			settingsEditor.putString(BowserUtils.COLOR_ONE, "0");
		if (!this.settings.contains(BowserUtils.COLOR_TWO))
			settingsEditor.putString(BowserUtils.COLOR_TWO, "0");

		// Close the editor
		settingsEditor.commit();

		// Setup the object to use from the preferences
		bShoulderObject = new BowserShoulderObject();
		// Log.v(debugClarity, settings.getString(BowserUtils.MODE, "0"));
		bShoulderObject.mode = Integer.parseInt(settings.getString(
				BowserUtils.MODE, "0"));
		// Log.v(debugClarity, settings.getInt(BowserUtils.SPEED, 0));
		// Integer a = settings.getInt(BowserUtils.SPEED, 0);
		// Log.v("TEST", String.valueOf(a));
		bShoulderObject.speed = Integer.parseInt(settings.getString(
				BowserUtils.SPEED, "0"));
		bShoulderObject.colorOne = Integer.parseInt(settings.getString(
				BowserUtils.COLOR_ONE, "0"));
		bShoulderObject.colorTwo = Integer.parseInt(settings.getString(
				BowserUtils.COLOR_TWO, "0"));
		Log.v(debugClarity, String.valueOf(bShoulderObject.mode));
		Log.v(debugClarity, String.valueOf(bShoulderObject.speed));
		Log.v(debugClarity, String.valueOf(bShoulderObject.colorOne));
		Log.v(debugClarity, String.valueOf(bShoulderObject.colorTwo));

		setContentView(R.layout.main);

		createItems();

	}

	private void createItems() {

		// Create spinner
		Spinner spinner = (Spinner) findViewById(R.id.modeSpinner);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.modes_array,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(R.layout.spinner_items);
		spinner.setAdapter(adapter);
		spinner.setSelection(bShoulderObject.mode);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				// Toast.makeText(Main.this,
				// adapter.getItem(position).toString(),
				// Toast.LENGTH_SHORT).show();
				bShoulderObject.mode = position;
				String message = "m";
				Log.v(debugClarity, Integer.toString(Integer.toString(position).length()));
				if (Integer.toString(position).length() < 3) {
					Log.v(debugClarity, message);
					while (message.length() + Integer.toString(position).length() < 4)
					{						
						message = message + "0";
						Log.v(debugClarity, message);
					}
				}
				message = message + Integer.toString(position);
				Log.v(debugClarity, message);
				bHandler.sendMessage(message);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// Create Speed SeekBar
		final TextView speedReading = (TextView) findViewById(R.id.speedReading);
		speedReading.setText(String.valueOf(bShoulderObject.speed));
		SeekBar speedSeekBar = (SeekBar) findViewById(R.id.speedSeekBar);
		speedSeekBar.setMax(256);
		speedSeekBar.setProgress(bShoulderObject.speed);
		speedSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Integer progress = seekBar.getProgress();
				if (progress < 25) {
					progress = 25;
				}
				// Toast.makeText(Main.this,
				// "STOPPED " + Integer.toString(progress),
				// Toast.LENGTH_SHORT).show();
				String message = "s";
				if (Integer.toString(progress).length() < 3) {
					while (message.length()
							+ Integer.toString(progress).length() < 4)
						message = message + "0";
				}
				message = message + Integer.toString(progress);
				bHandler.sendMessage(message);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress < 25)
					progress = 25;
				bShoulderObject.speed = progress;
				speedReading.setText(Integer.toString(progress));
			}
		});

		// Create Color One SeekBar
		final TextView colorOneReading = (TextView) findViewById(R.id.colorOneReading);
		colorOneReading.setText(String.valueOf(bShoulderObject.colorOne));
		SeekBar colorOneSeekBar = (SeekBar) findViewById(R.id.colorOneSeekBar);
		colorOneSeekBar.setMax(80);
		colorOneSeekBar.setProgress(bShoulderObject.colorOne);
		colorOneSeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// Toast.makeText(
						// Main.this,
						// "STOPPED "
						// + Integer.toString(seekBar
						// .getProgress()),
						// Toast.LENGTH_SHORT).show();
						String message = "c";
						if (Integer.toString(seekBar.getProgress()).length() < 3) {
							while (message.length()
									+ Integer.toString(seekBar.getProgress())
											.length() < 4)
								message = message + "0";
						}
						message = message
								+ Integer.toString(seekBar.getProgress());
						bHandler.sendMessage(message);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						bShoulderObject.colorOne = progress;
						colorOneReading.setText(Integer.toString(progress));
					}
				});

		// Create Color Two SeekBar
		final TextView colorTwoReading = (TextView) findViewById(R.id.colorTwoReading);
		colorTwoReading.setText(String.valueOf(bShoulderObject.colorTwo));
		SeekBar colorTwoSeekBar = (SeekBar) findViewById(R.id.colorTwoSeekBar);
		colorTwoSeekBar.setMax(80);
		colorTwoSeekBar.setProgress(bShoulderObject.colorTwo);
		colorTwoSeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// Toast.makeText(
						// Main.this,
						// "STOPPED "
						// + Integer.toString(seekBar
						// .getProgress()),
						// Toast.LENGTH_SHORT).show();
						String message = "d";
						if (Integer.toString(seekBar.getProgress()).length() < 3) {
							while (message.length()
									+ Integer.toString(seekBar.getProgress())
											.length() < 4)
								message = message + "0";
						}
						message = message
								+ Integer.toString(seekBar.getProgress());
						bHandler.sendMessage(message);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						bShoulderObject.colorTwo = progress;
						colorTwoReading.setText(Integer.toString(progress));
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.settings_button:
			savePreferences(bShoulderObject);
			intent = new Intent(this, BowserPreferences.class);
			startActivity(intent);
			return true;
		case R.id.refresh_bt_button:
			Toast.makeText(this, "Retrying bluetooth connection.",
					Toast.LENGTH_SHORT);
			bHandler.closeConnection();
			bHandler.connectToClient();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Test to save preferences before entering preferences screen
	 * 
	 * @param currentSettings
	 */
	private void savePreferences(BowserShoulderObject currentSettings) {
		Editor settingsEditor = this.settings.edit();
		settingsEditor.putString(BowserUtils.MODE,
				String.valueOf(currentSettings.mode));
		settingsEditor.putString(BowserUtils.SPEED,
				String.valueOf(currentSettings.speed));
		settingsEditor.putString(BowserUtils.COLOR_ONE,
				String.valueOf(currentSettings.colorOne));
		settingsEditor.putString(BowserUtils.COLOR_TWO,
				String.valueOf(currentSettings.colorTwo));

		// Close the editor
		settingsEditor.commit();
	}

//	@Override
//	protected void onResume() {
//		bHandler.connectToClient();
//		super.onResume();
//	}
	
	@Override
	protected void onStop() {
		bHandler.closeConnection();
		super.onStop();
	}

	@Override
	protected void onPause() {
		bHandler.closeConnection();
		super.onPause();
	}
}
