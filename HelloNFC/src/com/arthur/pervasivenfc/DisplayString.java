package com.arthur.pervasivenfc;

import java.util.List;

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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
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
public class DisplayString extends Activity {

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
			 
    	// Change the view
		contenu = intent.getStringExtra(EXTRA_TAG);
	    displayNotification(contenu);
	    changeTextView(contenu);
	    //moveTaskToBack(true);
	        
    }
    
    void changeTextView(String contenu) {
    	    	
    	//Just display the content of the tag
    	TextView currentRankText = (TextView)  this.findViewById(R.id.currentRankLabel);
    	
    	
    	
    	//We change the text of the view
    	currentRankText.setText(contenu);
    	/*try {
			listAllActivities();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
    	//Intent intent = new Intent("android.intent.category.LAUNCHER");
    	//intent.setClassName("com.facebook.katana", "com.facebook.katana.activity.events.EventsActivity");
    	String uri = "facebook://facebook.com/events/217879055009781/";
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    	startActivity(intent);
    }
    
    //This function return all the packages installed on the device
    public void listAllActivities() throws NameNotFoundException
    {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for(PackageInfo pack : packages)
        {
            ActivityInfo[] activityInfo = getPackageManager().getPackageInfo(pack.packageName, PackageManager.GET_ACTIVITIES).activities;
            Log.i("Pranay", pack.packageName + " has total " + ((activityInfo==null)?0:activityInfo.length) + " activities");
            if(activityInfo!=null)
            {
                for(int i=0; i<activityInfo.length; i++)
                {
                    Log.i("PC", pack.packageName + " ::: " + activityInfo[i].name);
                }
            }
        }
    }
    
	void displayNotification(String contenu) {
         //Display a notification when the job is done

         NotificationCompat.Builder mBuilder =
        	        new NotificationCompat.Builder(this)
        	        .setSmallIcon(R.drawable.ic_launcher)
        	        .setContentTitle("Pervasive project")
        	        .setContentText("Content: " + contenu );
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
