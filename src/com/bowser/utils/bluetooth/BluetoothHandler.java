package com.bowser.utils.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bowser.Modes;

public class BluetoothHandler {

	private static final String TAG = "CCCTHINBTCLIENT";
	private static final boolean D = true;
	public BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	// Well known SPP UUID (will *probably* map to
	// RFCOMM channel 1 (default) if not in use);
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");


	// ==> hardcode your server's MAC address here <==
	// private static String address = "00:24:BA:B7:57:81";
	// private static String address = "00:10:12:31:04:42";
	private static String remoteMacAddress = "20:11:02:15:01:27";

	public String getRemoteMacAddress() {
		return remoteMacAddress;
	}

	public void setRemoteMacAddress(String remoteMacAddress) {
		BluetoothHandler.remoteMacAddress = remoteMacAddress;
	}
	
	private final Context bCtx;

	/** Called when first created. */
	public void loadBluetoothModule() {

		if (D)
			Log.e(TAG, "+++ LOADING BT MODULE +++");

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(bCtx, "Bluetooth is not available.",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(bCtx,
					"Please enable your BT and retry.",
					Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(bCtx,
				"Bluetooth enabled.",
				Toast.LENGTH_LONG).show();
		if (D)
			Log.e(TAG, "+++ DONE LOADING, GOT LOCAL BT ADAPTER +++");
	}

    public BluetoothHandler(Context ctx) {
        this.bCtx = ctx;
        this.loadBluetoothModule();
    }
    
	public void connectToClient() {

		if (D) {
			Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
		}
		mBluetoothAdapter.cancelDiscovery();
		// When this returns, it will 'know' about the server,
		// via it's MAC address.
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(remoteMacAddress);

		// We need two things before we can successfully connect
		// (authentication issues aside): a MAC address, which we
		// already have, and an RFCOMM channel.
		// Because RFCOMM channels (aka ports) are limited in
		// number, Android doesn't allow you to use them directly;
		// instead you request a RFCOMM mapping based on a service
		// ID. In our case, we will use the well-known SPP Service
		// ID. This ID is in UUID (GUID to you Microsofties)
		// format. Given the UUID, Android will handle the
		// mapping for you. Generally, this will return RFCOMM 1,
		// but not always; it depends what other BlueTooth services
		// are in use on your Android device.
		mBluetoothAdapter.cancelDiscovery();
		
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			Log.e(TAG, "CONNECT: Socket creation failed.", e);
			Toast.makeText(bCtx, "Bluetooth is off!", Toast.LENGTH_SHORT);
		}

		// Discovery may be going on, e.g., if you're running a
		// 'scan for devices' search from your handset's Bluetooth
		// settings, so we call cancelDiscovery(). It doesn't hurt
		// to call it, but it might hurt not to... discovery is a
		// heavyweight process; you don't want it in progress when
		// a connection attempt is made.
		mBluetoothAdapter.cancelDiscovery();

		// Blocking connect, for a simple client nothing else can
		// happen until a successful connection is made, so we
		// don't care if it blocks.
		try {
			btSocket.connect();
			Log.e(TAG,
					"CONNECT: BT connection established, data transfer link open.");
		} catch (IOException e) {
		try {
			btSocket.close();
		} catch (IOException e2) {
			Log.e(TAG,
					"CONNECT: Unable to close socket during connection failure", e2);
			}
		}

		// Create a data stream so we can talk to server.
		if (D)
			Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");
		
		//This is for writing to the bluetooth device, getting the socket
		try {
			outStream = btSocket.getOutputStream();
			inStream = btSocket.getInputStream();
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
		}
	}

	public void readSettings() {
		
		(new Thread() {
			public void run() {
				try {


					InputStream is = btSocket.getInputStream();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(is));
					String buf = "";

						buf = "";

						while (null != (buf = br.readLine())) {
							Log.e(TAG, "READ ++: " + buf);
							
							Scanner src = new Scanner(buf);
							src.useDelimiter(" ");
						    while (src.hasNext()) {
						    	
						    	Modes.valueOf("1");
		
						    }
						}
                    
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
		
	}
	
	
	//This is for writing to the bluetooth device
	public void sendMessage(String message) {
		  byte[] msgBuffer = message.getBytes(); 
		  try { 
			  outStream.write(msgBuffer); 
			  }
		  catch (IOException e) { 
			  Log.e(TAG, "WRITE: Exception during write. Message was: " + message, e); }
		 

	}
	
	//This is for reading to the bluetooth device
	@SuppressWarnings("null")
	public String readMessage() {
		  byte[] msgBuffer = null;
		  try { 
			  inStream.read(msgBuffer); 
			  }
		  catch (IOException e) { 
			  Log.e(TAG, "READ: Exception during read. ", e); }
		  return msgBuffer.toString();
	}

	//Close the socket and flush
	public void closeConnection() {

		if (D)
			Log.e(TAG, "- CLOSING CONNECTION -");

		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				Log.e(TAG, "ON CLOSE: Couldn't flush output stream.", e);
			}
		}

		try {
			btSocket.close();
		} catch (IOException e2) {
			Log.e(TAG, "ON CLOSE: Unable to close socket.", e2);
		}
	}
}