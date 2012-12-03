package com.arthur.pervasivenfc;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.arthur.pervasivenfc.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
@SuppressLint({ "ParserError" })
public class MainActivity extends Activity{

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ctx=this;
				
		adapter = NfcAdapter.getDefaultAdapter(this);
		if(adapter == null) {
			// NFC is not available
			finish();
			return;
		}
		if(!adapter.isEnabled()) {
			// NFC is disabled
			finish();
			return;
		}
		
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };
		
	}
	
	/** Called when the user clicks the Send button */
	public void writeString(View view) {
	    Intent intent = new Intent(this, WriteString.class);
	    startActivity(intent);
	}
	
	public void writeContact(View view) {
	    Intent intent = new Intent(this, AddContact.class);
	    startActivity(intent);
	}
	
	public void writeUri(View view) {
	    Intent intent = new Intent(this, WriteUri.class);
	    startActivity(intent);
	}
	
	public void writeBrightness(View view) {
	    Intent intent = new Intent(this, WriteSetting.class);
	    startActivity(intent);
	}
	
	public void writeCall(View view) {
	    Intent intent = new Intent(this, WriteCall.class);
	    startActivity(intent);
	}
	
	public void writeDestination(View view) {
	    Intent intent = new Intent(this, WriteDestination.class);
	    startActivity(intent);
	}
	
	public void beam(View view) {
		Intent intent = new Intent(this, Beam.class);
		startActivity(intent);
	}
	
	public void morpion(View view) {
		Intent intent = new Intent(this, MorpionActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onNewIntent(Intent intent){
		//Toast.makeText(ctx, ctx.getString(R.string.app_name) , Toast.LENGTH_LONG ).show();
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);    
			//Toast.makeText(this, this.getString(R.string.ok_detection) + mytag.toString(), Toast.LENGTH_LONG ).show();
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		WriteModeOff(); //disableForegroundDispatch
	}

	@Override
	public void onResume(){
		super.onResume();
		//enableForegroundDispatch
		WriteModeOn();
		
		}

	private void WriteModeOn(){
		writeMode = true;
		adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
	}

	private void WriteModeOff(){
		writeMode = false;
		adapter.disableForegroundDispatch(this);
	}

	private void toast(String text) {
		Toast.makeText(ctx, ctx.getString(R.string.hello_world) , Toast.LENGTH_LONG ).show();
    }
}