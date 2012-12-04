package com.arthur.pervasivenfc;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.arthur.pervasivenfc.R;
import com.arthur.pervasivenfc.ArcMenu;
import com.arthur.pervasivenfc.MainActivity;
import com.arthur.pervasivenfc.RayMenu;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
@SuppressLint({ "ParserError" })
public class MainActivity extends Activity{
	
	private static final int[] ITEM_DRAWABLES = { R.drawable.string_button, R.drawable.url,
		R.drawable.brightness_button_2, R.drawable.contact_image, R.drawable.phone_image, R.drawable.google_maps_button };
	private static final int[] ITEM_DRAWABLES_1 = { R.drawable.facebook_icon, R.drawable.bluetooth_icon,
		R.drawable.wifi_icon, R.drawable.beam_button, R.drawable.morpion_button };
	
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
		
		ArcMenu arcMenu = (ArcMenu) findViewById(R.id.arc_menu);

		final int itemCount = ITEM_DRAWABLES_1.length;
		for (int i = 0; i < itemCount; i++) {
			ImageView item = new ImageView(this);
			item.setImageResource(ITEM_DRAWABLES_1[i]);

			final int position = i;
			arcMenu.addItem(item, new OnClickListener() {

				public void onClick(View v) {
					Toast.makeText(MainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
					if(position==0)
					{
						Intent intent = new Intent(ctx, WriteFacebook.class);
					    startActivity(intent);
					}
					if(position==1)
					{
						Intent intent = new Intent(ctx, WriteBluetooth.class);
						startActivity(intent);
					}
					if(position==2)
					{
						Intent intent = new Intent(ctx, WriteWifi.class);
						startActivity(intent);
					}
					if(position==3)
					{
						Intent intent = new Intent(ctx, Beam.class);
						startActivity(intent);
					}
					if(position==4)
					{
						Intent intent = new Intent(ctx, MorpionActivity.class);
						startActivity(intent);
					}
				}
			});// Add a menu item
		}

		RayMenu rayMenu = (RayMenu) findViewById(R.id.ray_menu);
		for (int i = 0; i < itemCount; i++) {
			ImageView item = new ImageView(this);
			item.setImageResource(ITEM_DRAWABLES[i]);

			final int position = i;
			rayMenu.addItem(item, new OnClickListener() {

				public void onClick(View v) {
					Toast.makeText(MainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
					if(position==0)
					{
						Intent intent = new Intent(ctx, WriteString.class);
					    startActivity(intent);
					}
					if(position==1)
					{
						Intent intent = new Intent(ctx, WriteUri.class);
						startActivity(intent);
					}
					if(position==2)
					{
						Intent intent = new Intent(ctx, WriteSetting.class);
						startActivity(intent);
					}
					if(position==3)
					{
						Intent intent = new Intent(ctx, AddContact.class);
						startActivity(intent);
					}
					if(position==4)
					{
						Intent intent = new Intent(ctx, WriteCall.class);
						startActivity(intent);
					}
					if(position==5)
					{
						Intent intent = new Intent(ctx, WriteDestination.class);
						startActivity(intent);
					}
				}
			});// Add a menu item
		}

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