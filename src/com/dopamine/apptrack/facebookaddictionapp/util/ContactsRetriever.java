package com.dopamine.apptrack.facebookaddictionapp.util;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactsRetriever implements Runnable{
	Context context;
	
	public ContactsRetriever(Context c){
		context = c;
	}

	@Override
	public void run() {
		
		ContentResolver contResv = context.getContentResolver();
		Cursor cursor = contResv.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
		HashMap<String, Contact> contacts = new HashMap<String, Contact>();
		
		if(cursor.moveToFirst())
		{
		    do{
		    	String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

		    	if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) == 1)
		    	{
		    		Cursor pCur = contResv.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
		    		while (pCur.moveToNext())
		    		{
		    			String lookupKey = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
		    			String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		    			String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		    			contactNumber = Contact.scrubPhoneNumber(contactNumber);
		    			Contact contact = new Contact(lookupKey, contactName, contactNumber);
		    			contacts.put(contactNumber, contact);
//		    			break;	// gets only first phone number
		    		}
		    		pCur.close();
		    	}

		    } while (cursor.moveToNext()) ;
		    
		    DataManager.updateContacts(contacts, context);
		    
		}
		
		cursor.close();
		
	}

}
