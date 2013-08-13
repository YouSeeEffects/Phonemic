package com.kizzlebot.phonetic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.kizzlebot.phonetic.MainActivity.ResponseReceiver;

import java.io.IOException;

public class MuzakService extends Service {

	private static final String DEBUG_TAG = "MuzakService";
	private static MediaPlayer mp;
	public static final String PARAM_OUT_MSG = "omsg";
	private int currentTrack = 0;
	private String[] tracks ;


	@Override
	public void onCreate() {
		super.onCreate();
		Log.d( DEBUG_TAG, "In onCreate." );

		mp = new MediaPlayer();
		mp.setOnCompletionListener( new OnCompletionListener() {

			@Override
			public void onCompletion ( MediaPlayer mp ) {
				currentTrack = ( currentTrack + 1 ) % tracks.length;
				if (currentTrack == 0 ){
					Log.d(DEBUG_TAG,DEBUG_TAG+" executed mp.release()");
					//mp.release();
				}else{
					Uri nextTrack = Uri.parse( tracks[currentTrack] );
					Log.i( DEBUG_TAG, "nextTrack=" + nextTrack );
					try {
						mp.reset();
						mp.setDataSource( MuzakService.this, nextTrack );
						mp.prepare();
						//mp.start();
					} catch ( Exception e ) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e( DEBUG_TAG, "Player failed1111", e );
					}
				}
			}

		} );
		mp.setOnPreparedListener( new OnPreparedListener() {
			@Override
			public void onPrepared ( MediaPlayer mp ) {
				mp.start();
			}
		} );
	}
	@Override
	public int onStartCommand ( Intent intent, int flags, int startId ) {
		tracks = intent.getStringArrayExtra( "tracks" );
		Uri file = Uri.parse(tracks[this.currentTrack]);
		Log.d( DEBUG_TAG, "In onStart. "+file );
		try{
			mp.reset();
			mp.setDataSource(this.getBaseContext(), file);
			mp.prepare();
			//mp.start();
		}catch(IOException e){
			Log.e( DEBUG_TAG, "IOException raised" );
		}
		return super.onStartCommand( intent, flags, startId );
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d( DEBUG_TAG, "In onDestroy." );
		if(mp != null) {
			Log.d( DEBUG_TAG, "In onDestroy::if (mp!=null)." );
			mp.stop();
			mp.reset();
		}
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction( ResponseReceiver.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PARAM_OUT_MSG, 1);
		sendBroadcast(broadcastIntent);
		Log.d( DEBUG_TAG, "In onDestroy::sendBroadcast" );
	}



	@Override
	public IBinder onBind(Intent intent) {
		Log.d( DEBUG_TAG, "In onBind with intent=" + intent.getAction() );
		return null;
	}

}