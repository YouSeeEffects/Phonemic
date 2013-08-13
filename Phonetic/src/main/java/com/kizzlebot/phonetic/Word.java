package com.kizzlebot.phonetic;


/**
 * What a Word knows about itself:
 * - The word ("HELLO")
 * - Phonetic of the word ("HH AH0 L OH")
 * - Raw asset id associated with each Phoneme (R.raw.HH, R.raw.AH0, R.raw.L, R.raw.OH).
 * What a Word can do:
 * - Return the word ("HELLO")
 * - Return the Phonetic of the word ("HH AH0 L OH")
 * - Return an array of ints of the Phonemes of the word {"HH", "AH0", "L", "OH"}
 * -
 */
public class Word{

	/**
	 * Private variables
	 */
	private String _word;
	private String _phonetic;
	private int[] _rawID;

	/**
	 * Overloaded Constructor
	 */
	public Word(String id, String code){
		this._word = id;
		this._phonetic = code;
	}
	public Word(String id, String code,int[] rawID){
		this._word = id;
		this._phonetic = code;
		this._rawID = rawID;
	}
	// getting ID
	public String getWord(){
		return this._word;
	}

	// getting phone number
	public String getPhonetic(){
		return this._phonetic;
	}
	public String[] getPhoneticArray(){
		return this._phonetic.split("\\s+");
	}
	public int[] getRawID(){
		return this._rawID;
	}
}