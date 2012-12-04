package com.arthur.pervasivenfc;

import com.arthur.pervasivenfc.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
@TargetApi(16) //Quiet compilator
public class SelectActivity extends Activity {

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;
    //a variable to store the system brightness  
    private int brightness;  
    //the content resolver used as a handle to the system's settings  
    private ContentResolver cResolver;  
    //a window object, that will store a reference to the current window  
    private Window window; 
    final String EXTRA_TAG = "new_tag";
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_setting);
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
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };
		resolveIntent(this.getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_change_setting, menu);
        return false;
    }
    
    @Override
	protected void onNewIntent(Intent intent){
		
			//setIntent(intent);
			//Process payload or msgs
	        resolveIntent(intent);
	        		
	}
    
    private void resolveIntent(Intent intent) {
    	
    	 String action = intent.getAction();
    	 String contenu = null;
    
    	if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
    			|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
    		
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			Parcelable[] rawMsgs2 = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msg = null ;
			
			if (rawMsgs2 != null) {
	            msg = new NdefMessage[rawMsgs2.length];
	            for (int i = 0; i < rawMsgs2.length; i++) {
	                msg[i] = (NdefMessage) rawMsgs2[i];
	            }
			}
			 // Change the view
	        chooseApplication(msg);
	        moveTaskToBack(true);

    	}
	        
    }
       
	String getPayloadContent(NdefMessage[] msgs) {
    	
		String contenu = null;
		
    	if (msgs == null || msgs.length == 0) {
            return "Message is null, there is no payload";
        }
    	
    	NdefMessage message = msgs[0];
    	
    	NdefRecord[] records = message.getRecords();
    	 for (final NdefRecord record : records) {
    		 //We take only the payload. See getId(), getTnf() and getType() to get the header
    		 contenu = new String(record.getPayload());
    	 }
    	 return contenu;
	}
    
	void chooseApplication(NdefMessage[] msgs){
		//We will split the string to determinate which application will be used
		//Then we will pass the intent to the chosen application.
		String contenu = getPayloadContent(msgs);
		String str[]=contenu.split("/");
		//toast("avant les if " + str[1]);
		
		//Write a String
		if(str[1].equals("str"))
		{
			toast("Display string: " + str[2]);
			    Intent intent = new Intent(this, DisplayString.class);			    
			    intent.putExtra(EXTRA_TAG, str[2]);
			    startActivity(intent);
		}
		
		//Brightness
		else if(str[1].equals("set"))
		{
			//toast("Changing brigthness setting to: " + str[2]);
			    Intent intent = new Intent(this, ChangeSetting.class);
			    intent.putExtra(EXTRA_TAG, str[2]);
			    startActivity(intent);
		}
		
		//Add a contact
		else if(str[1].equals("ctc"))
		{
			Intent intent = new Intent(this, ReadContact.class);
		    intent.putExtra(EXTRA_TAG, str[2]);
		    startActivity(intent);
		}
		
		//Make a call
		else if(str[1].equals("cal"))
		{
			Intent intent = new Intent(this, MakeCall.class);
		    intent.putExtra(EXTRA_TAG, str[2]);
		    startActivity(intent);
		}
		
		//Activate/desactivate Bleutooth
		else if(str[1].equals("blue"))
		{
			Intent intent = new Intent(this, EnableBluetooth.class);
		    startActivity(intent);
		}
		
		//Connect to Wi-Fi
		else if(str[1].equals("w"))
		{
			Intent intent = new Intent(this, ConnectWifi.class);
		    intent.putExtra(EXTRA_TAG, str[2]);
		    startActivity(intent);
		}
		
		else if(str[1].equals("fac"))
		{
			Intent intent = new Intent(this, OpenFacebook.class);
		    intent.putExtra(EXTRA_TAG, str[2]);
		    startActivity(intent);
		}
		
		//Use Facebook
		else if(str[1].equals("dst"))
		{
			Intent intent = new Intent(this, ReadDestination.class);
		    intent.putExtra(EXTRA_TAG, str[2]);
		    startActivity(intent);
		}
	}
	
    @Override
	public void onPause(){
		super.onPause();
		//disableForegroundDispatch
		//WriteModeOff(); 
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
		Toast.makeText(ctx, text , Toast.LENGTH_LONG ).show();
    }
}
