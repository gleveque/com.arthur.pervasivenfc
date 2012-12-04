package com.arthur.pervasivenfc;

import java.util.ArrayList;

import com.arthur.pervasivenfc.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentProviderOperation;
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
import android.provider.ContactsContract;
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
public class ReadContact extends Activity {

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
	    //changeTextView(contenu);
	    addNewContact(contenu);
	    moveTaskToBack(true);
	        
    }
    
    void changeTextView(String contenu) {
    	    	
    	//Just display the content of the tag
    	TextView currentRankText = (TextView)  this.findViewById(R.id.currentRankLabel);
    	
    	String contact[] = contenu.split(":");
    	String nom = contact[1];
    	String prenom = contact[2];
    	String tel = contact[0];
    	
    	//We change the text of the view
    	currentRankText.setText("Nom: "+ nom + ", Prenom: " + prenom + ", Tel: " + tel);
    	
    }
    
    void addNewContact(String contenu){
    	    	
    	String contact[] = contenu.split(":");
    	String nom = contact[1];
    	String prenom = contact[2];
    	String tel = contact[0];
    	
    	String DisplayName = nom;
    	String MobileNumber = tel;
    	String HomeNumber = "1111";
    	String WorkNumber = "2222";
    	String emailID = "email@nomail.com";
    	String company = "bad";
    	String jobTitle = "abcd";
    	
		//Toast.makeText(ctx, ctx.getString(R.string.new_contact) , Toast.LENGTH_LONG ).show();

    	ArrayList<ContentProviderOperation> ops = 
    	    new ArrayList<ContentProviderOperation>();

    	ops.add(ContentProviderOperation.newInsert(
    	    ContactsContract.RawContacts.CONTENT_URI)
    	    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
    	    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
    	    .build()
    	);

    	//------------------------------------------------------ Names
    	if(DisplayName != null)
    	{           
    	    ops.add(ContentProviderOperation.newInsert(
    	        ContactsContract.Data.CONTENT_URI)              
    	        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	        .withValue(ContactsContract.Data.MIMETYPE,
    	            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
    	        .withValue(
    	            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,     
    	            DisplayName).build()
    	    );
    	} 

    	//------------------------------------------------------ Mobile Number                      
    	if(MobileNumber != null)
    	{
    	    ops.add(ContentProviderOperation.
    	        newInsert(ContactsContract.Data.CONTENT_URI)
    	        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	        .withValue(ContactsContract.Data.MIMETYPE,
    	        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
    	        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
    	        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
    	        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
    	        .build()
    	    );
    	}

    	                    //------------------------------------------------------ Home Numbers
    	                    if(HomeNumber != null)
    	                    {
    	                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                                .withValue(ContactsContract.Data.MIMETYPE,
    	                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
    	                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
    	                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
    	                                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
    	                                .build());
    	                    }

    	                    //------------------------------------------------------ Work Numbers
    	                    if(WorkNumber != null)
    	                    {
    	                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                                .withValue(ContactsContract.Data.MIMETYPE,
    	                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
    	                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
    	                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
    	                                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
    	                                .build());
    	                    }

    	                    //------------------------------------------------------ Email
    	                    if(emailID != null)
    	                    {
    	                         ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                                    .withValue(ContactsContract.Data.MIMETYPE,
    	                                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
    	                                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
    	                                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
    	                                    .build());
    	                    }

    	                    //------------------------------------------------------ Organization
    	                    if(!company.equals("") && !jobTitle.equals(""))
    	                    {
    	                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                                .withValue(ContactsContract.Data.MIMETYPE,
    	                                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
    	                                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
    	                                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
    	                                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
    	                                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
    	                                .build());
    	                    }

    	                    // Asking the Contact provider to create a new contact                  
    	                    try 
    	                    {
    	                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    	                    } 
    	                    catch (Exception e) 
    	                    {               
    	                        e.printStackTrace();
    	                        Toast.makeText(ctx, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    	                    }
    	                    
    	                   
    }
    
	void displayNotification(String contenu) {
         //Display a notification when the job is done

         NotificationCompat.Builder mBuilder =
        	        new NotificationCompat.Builder(this)
        	        .setSmallIcon(R.drawable.ic_launcher)
        	        .setContentTitle("Pervasive project")
        	        .setContentText("New contact added! " );
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
		Toast.makeText(ctx, ctx.getString(R.string.new_contact) , Toast.LENGTH_LONG ).show();
    }
}