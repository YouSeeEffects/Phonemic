package com.kizzlebot.phonetic;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;



public class MainActivity extends Activity {

	private PhoneticDatabaseHandler phonemeDB ;

	private ResponseReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate( savedInstanceState );
		setContentView(R.layout.activity_main);
		phonemeDB = new PhoneticDatabaseHandler(this);
		IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new ResponseReceiver();
		registerReceiver(receiver, filter);
	}
	public void onButtonClick(View v){
		// Get the text inside the EditText instance and create a Word object out of it
		EditText eTxt = (EditText)findViewById( R.id.editText );
		Log.i( "MainActivity", "EditTxt = "+eTxt.getText().toString());
		Word wrd = phonemeDB.getWord(eTxt.getText().toString());

		// Use the word object set the TextField to result
		TextView txtView= (TextView)findViewById(R.id.text);
		txtView.setText( wrd.getPhonetic() );



		String[] strID = new String[wrd.getRawID().length];
		int[] rawID = wrd.getRawID();
		for (int i = 0 ; i < wrd.getRawID().length ; i++){
			strID[i] = "android.resource://com.kizzlebot.phonetic/"+String.valueOf( rawID[i] );
		}
		Intent serviceIntent = new Intent(this, MuzakService.class);
		serviceIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		serviceIntent.putExtra("tracks",strID);
		startService( serviceIntent );
		//stopService( serviceIntent );
	}
	public class ResponseReceiver extends BroadcastReceiver {
		private String DTAG = "ResponseReceiver";

		public static final String ACTION_RESP =
				"com.kizzlebot.phonetics.intent.action.MESSAGE_PROCESSED";
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra(MuzakService.PARAM_OUT_MSG,0) == 1){
				stopService( intent );
				Log.i("ResponseReceiver","inside of ResponseReceiver.onReceive, just called stopService");
			}

		}
	}
}
