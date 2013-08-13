package com.kizzlebot.phonetic;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class as a whole is in charge of searching the sqlite database and returning instances of word
 * that contains all the relevant information.
 */
// Constructor
public class PhoneticDatabaseHandler extends SQLiteOpenHelper{
	/**
	 * The Word enum maps the String representation of a phoneme to the raw asset id.
	 * This is enum is used to get the correct "raw asset ID" given a Phoneme as a string.
	 *
	 * In other words, it is used as a means of getting the value of R.raw.<Phoneme> given
	 * a string Phoneme ( such as "AH0" or "EH").
	 */
	private static enum RAWID{

		A("AA",R.raw.aa),
		AA1("AA1", R.raw.aa1),
		AH0("AH0", R.raw.ah0),
		AH1("AH1", R.raw.ah1),
		AE("AE", R.raw.ae),
		AE1("AE1", R.raw.ae1),
		AO1("AO1",R.raw.ao1),
		AW("AW",R.raw.aw),
		AW1("AW1",R.raw.aw1),
		AXR("AXR",R.raw.axr),
		AX0("AX0",R.raw.ax0),
		AY("AY",R.raw.ay),
		B("B", R.raw.b),
		CH("CH", R.raw.ch),
		D("D", R.raw.d),
		DH("DH", R.raw.dh),
		EH("EH", R.raw.eh),
		EH1("EH1",R.raw.eh1),
		ER("ER",R.raw.er),
		ER1("ER1",R.raw.er_),
		F("F",R.raw.f),
		G("G", R.raw.g),
		HH("HH", R.raw.hh),
		IH("IH", R.raw.ih),
		IH1("IH1", R.raw.ih1),
		IY("IY", R.raw.iy),
		IY1("IY1", R.raw.iy1),

		JH("JH", R.raw.jh),
		K("K", R.raw.k),
		L("L", R.raw.l),
		M("M", R.raw.m),
		N("N", R.raw.n),
		NG("NG", R.raw.ng),
		OW1("OW1", R.raw.ow1),
		OY("OY", R.raw.oy),
		OY1("OY1", R.raw.oy1),
		P("P", R.raw.p),
		r("R",R.raw.r),
		S("S", R.raw.s),
		SH("SH", R.raw.sh),
		T("T", R.raw.t),
		TH("TH", R.raw.th),
		UH("UH", R.raw.uh),
		UH1("UH1", R.raw.uh1),
		UW("UW", R.raw.uw),
		UW1("UW", R.raw.uw1),
		V("V", R.raw.v),
		W("W", R.raw.w),
		Y("Y", R.raw.y),
		Z("Z", R.raw.z),
		ZH("ZH", R.raw.zh);

		private final int rawAssetID;
		private final String stringPhoneme ;
		RAWID(String name ,int raw){
			this.stringPhoneme = name ;
			this.rawAssetID    = raw ;
		}
		private String getPhoneme(){
			return this.stringPhoneme;
		}
		private int getAssetID(){
			return this.rawAssetID;
		}

		/**
		 * This method
		 * @param queryPhoneme This argument is the string we want to find the matching raw asset id of
		 *                     so if Word.getRaw("AH0") is called, it should return the value of R.raw.ah0
		 * @return The raw asset ID given by R.raw.# or 0 if not found
		 */
		private static int getRaw(String queryPhoneme){
			for ( RAWID wrd : values()){
				if ( wrd.getPhoneme().equals(queryPhoneme)){
					return wrd.getAssetID() ;
				}
			}
			return 0;
		}
	}
	//private static final String DATABASE_NAME = "@string/db_name";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_PATH = "/data/data/com.kizzlebot.phonetic/databases/";

	private static final String DATABASE_NAME = "cmudict.0.7a.sqlite";

	public PhoneticDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		copyDataBase( context );
	}
	@Override
	public void onCreate ( SQLiteDatabase db ) {

	}
	@Override
	public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {}


	/**
	 * Copies the contents of "Source code asset" (cmudict.0.7a.sqlite) to "Client's device asset location" on the device
	 * located at the path "/data/data/<PackageName>/databases".
	 * On install/execution of this application, this method will be called if cmudict.0.7a.sqlite file is not located in application assets.
	 * /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 *  1.) Declare a File object called file
	 *  2.) Uses the File object to create a directory on the user's device at the absolute path "/data/data/<PackageName>/databases
	 *      via the method file.mkdir().
	 *      - You can "adb shell" and do "ls /data/data/com.kizzlebot.Phentics/databases" to check if "cmudict.0.7a.sqlite exists
	 * /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 *  1.) Gets the AssetManager instance (assetMgr) from the calling context.
	 *  2.) Uses assetMgr to get the InputStream instance ( thisInputStream ) associated with the
	 *      given Context by asking assetMgr to open DATABASE_NAME.
	 *  3.) Creates a OutputStream instance (myOutput) at the location where the sqlite file should be (or is) located
	 *      which is at the path given by ( DATABASE_PATH+DATABASE_NAME = "/data/data/com.kizzlebot.Phonetics/databases/cmudict0.7a.sqlite")
	 *  4.) Uses myOutput to write what inputStream reads in (2048 bytes at a time).
	 *  5.) Cleans up by flushing the OutputStream and closing the inputStream.
	 * @param myContext Context of caller
	 */
	private void copyDataBase(Context myContext){
		if (!checkFileExists(DATABASE_PATH+DATABASE_NAME)){
			File file = new File(DATABASE_PATH); // Declare that there is a folder called databases
			file.mkdir();                       // Make that database on install
			try{
				AssetManager assetMgr= myContext.getAssets();
				// Open your local db as the input stream
				InputStream thisInputStream = assetMgr.open( DATABASE_NAME );
				//Open the empty db as the output stream
				OutputStream myOutput = new FileOutputStream(DATABASE_PATH+DATABASE_NAME);
				//transfer bytes from the inputfile to the outputfile
				byte[] buffer = new byte[2048];
				int length;
				while ((length = thisInputStream.read( buffer ))>0){
					myOutput.write(buffer, 0, length);
				}
				//Close the streams
				myOutput.flush();
				myOutput.close();
				thisInputStream.close();
			}
			catch(IOException e){
				Log.d("Something wrong","Database IOException");
			}
		}
		else{
			Log.i("File Existance","The File at "+DATABASE_PATH+DATABASE_NAME+" exists.");
		}
	}
	private boolean checkFileExists(String filePath){
		File f = new File(filePath);
		if (f.exists()){
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * This method returns a Word object, which contains the word (ie "HELLO") and the Phonetic found
	 * in the sqlite database "HH AH0 L OH", as well as an array containing all the rawID of the sounds
	 * associated with each phonetic.
	 * @param queryWord String of the word to search for in the sqlite database containing mapping
	 *                  of phonemes.  ("HELLO" == "HH AH0 L OW1")
	 * @return The string arpabet/sampa representation of what was found for the requested query
	 */
	public Word getWord( String queryWord ) {
		String db_path = DATABASE_PATH+DATABASE_NAME;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(db_path,null ,SQLiteDatabase.OPEN_READONLY );
		//String sqlQuery = "SELECT "+KEY_CODE+" FROM "+TABLE_DICTS+" WHERE "+KEY_ID+"=\""+word.toUpperCase()+"\"";
		String sqlQuery = "SELECT code from cmudict WHERE _id=\""+queryWord.toUpperCase()+"\"";
		Cursor cursor = db.rawQuery(sqlQuery,null);

		if (cursor.moveToFirst() ){
			String[] str = cursor.getString(0).split( "\\s+" );
			int[] rawIDofWord = new int[str.length];
			for (int i = 0 ; i < str.length ; i++){
				rawIDofWord[i] = RAWID.getRaw(str[i]);
			}
			return new Word(queryWord,cursor.getString(0),rawIDofWord);
		}
		else{
			return new Word("N/A","N/A");
		}
	}

}
