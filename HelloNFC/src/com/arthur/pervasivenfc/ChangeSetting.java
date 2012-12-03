package com.arthur.pervasivenfc;

import com.arthur.pervasivenfc.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
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
public class ChangeSetting extends Activity {

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
        //setContentView(R.layout.activity_change_setting);
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
			 
			contenu = intent.getStringExtra(EXTRA_TAG);
			// Change the Brightness
	        changeBrightness(contenu);
	        displayNotification(contenu);
	        moveTaskToBack(true);

    	
	        
    }
        
	void changeBrightness(String contenu) {
    	    	 
    	 //Here we will try to change the setting of the brightness
    	 
    	 //First we have to parse the records
    	 
    	 //And finaly give the parameters to the application
    	 
    	//get the content resolver  
         cResolver = getContentResolver();  
         
         //get the current window  
         window = getWindow();
         
         try   
         {  
             //get the current system brightness  
             brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);  
         }   
         catch (SettingNotFoundException e)   
         {  
             //throw an error case it couldn't be retrieved  
             Log.e("Error", "Cannot access system brightness");  
             e.printStackTrace();  
         }  
         
         brightness = Integer.parseInt(contenu);
    	 
         //set the system brightness using the brightness variable value  
         Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);  
           
         //preview brightness changes at this window  
         //get the current window attributes  
         LayoutParams layoutpars = window.getAttributes();  
         //set the brightness of this window  
         layoutpars.screenBrightness =  brightness / (float)255;
         //apply attribute changes to this window  
         window.setAttributes(layoutpars);
         
         /*AlertDialog alertDialog = new AlertDialog.Builder(ChangeSetting.this).create();
         alertDialog.setTitle("Changing brightness setting");
         alertDialog.setMessage("Set to " + contenu);
         alertDialog.show();*/
	}
	
	void displayNotification(String contenu) {
         //Display a notification when the job is done

         NotificationCompat.Builder mBuilder =
        	        new NotificationCompat.Builder(this)
        	        .setSmallIcon(R.drawable.ic_launcher)
        	        .setContentTitle("Pervasive project")
        	        .setContentText("Changing display setting to: " + contenu );
        	// Creates an explicit intent for an Activity in your app
         Intent resultIntent = new Intent(this, MainActivity.class);

        	// The stack builder object will contain an artificial back stack for the
        	// started Activity.
        	// This ensures that navigating backward from the Activity leads out of
        	// your application to the Home screen.
        	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        	// Adds the back stack for the Intent (but not the Intent itself)
        	stackBuilder.addParentStack(MainActivity.class);
        	// Adds the Intent that starts the Activity to the top of the stack
        	stackBuilder.addNextIntent(resultIntent);
        	PendingIntent resultPendingIntent =
        	        stackBuilder.getPendingIntent(
        	            0,
        	            PendingIntent.FLAG_UPDATE_CURRENT
        	        );
        	mBuilder.setContentIntent(resultPendingIntent);
        	NotificationManager mNotificationManager =
        	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        	int mId = 0;
			// mId allows you to update the notification later on.
        	mNotificationManager.notify(mId, mBuilder.build());
       
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
  		//WriteModeOn();
  		
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
