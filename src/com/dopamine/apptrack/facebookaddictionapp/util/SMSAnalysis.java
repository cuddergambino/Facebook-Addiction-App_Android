package com.dopamine.apptrack.facebookaddictionapp.util;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SMSAnalysis implements Runnable{
	
//	static final int smsHistoryLimit = 100;
	static final int lastXdays = 7;		// look at sms from past x days
	static final int millisPerDay = 1000*60*60*24;
	static final boolean printList = false;
	
	Context context;
	Thread contactRetrievalThread = null;
	
	public SMSAnalysis(Context c){
		context = c;
	}
	
	public SMSAnalysis(Context c, Thread thread){
		context = c;
		contactRetrievalThread = thread;
	}

	@Override
	public void run() {
		if (contactRetrievalThread != null) {
			try {
				contactRetrievalThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
		DataManager.clearContactIntervals();
		
		SMSapp smsApp = new SMSapp();
		
		ContentResolver contResv = context.getContentResolver();
		String selection = "date>?";
		Long earliestDateForSMS = System.currentTimeMillis() - lastXdays*millisPerDay;
		String selectionArgs[] = {earliestDateForSMS.toString()};
		Cursor smsCursor = contResv.query(Uri.parse("content://sms"), null, selection, selectionArgs, null);
//		Cursor smsCursor = contResv.query(Uri.parse("content://sms"), null, null, null, null);
		
		if(printList) {
			System.out.println("Date\tPhone Number");
		}
		if(printList) {
			System.out.println("=======================================");
		}
		if(smsCursor.moveToFirst()){
//			for(int i = 0; i < smsCursor.getCount() && i < smsHistoryLimit; i++){
			for(int i = 0; i < smsCursor.getCount(); i++){
				int dateIndex = smsCursor.getColumnIndex("date");
				int numberIndex = smsCursor.getColumnIndex("address");
				int typeIndex = smsCursor.getColumnIndex("type");	// 1 is received, 2 is sent
				if(dateIndex == -1 || numberIndex == -1 || smsCursor.isNull(dateIndex) || smsCursor.isNull(typeIndex) || smsCursor.isNull(numberIndex)){
					
				}
				else{
					long date = Long.valueOf( smsCursor.getString(dateIndex) );
					String phoneNumber = smsCursor.getString(numberIndex);
					int type = Integer.valueOf( smsCursor.getString(typeIndex) );
					
					if(printList) {
						System.out.println(type + "\t" + date + "\t" + phoneNumber);
					}
					
					// sms arrives in order: newest first
					SMS sms = new SMS(date, type);
					Contact contact = DataManager.getContact(phoneNumber, context);
					
					// Only add texts for people in contact list
					if(contact != null) {
						smsApp.addSMS(contact, sms);
					} else{
						System.out.println(phoneNumber + " is null contact");
					}
				}
				smsCursor.moveToNext();
			}
			smsCursor.close();
		}
		
		DataManager.contactsUpdated(context);
		
		Contact[] topContacts = new Contact[6];
        topContacts = DataManager.getTopContacts(6, context);
        for(Contact c : topContacts) {
			System.out.println(c.name + " " + c.numIntervals());
		}
        
		
//		 Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts., lookupKey);
//	 Uri res = ContactsContract.Contacts.lookupContact(getContentResolver(), lookupUri);
	}
	
	private class SMSapp{
		HashMap<Contact , SMSthread> smsThreads;
		SMSapp() {
			smsThreads = new HashMap<Contact, SMSAnalysis.SMSthread>();
		}
		
		void addSMS(Contact contact, SMS sms){
			SMSthread smsThread;
			
			if(smsThreads.containsKey(contact)){
				smsThread = smsThreads.get(contact);
				smsThread.add(sms);
			}
			else{
				smsThread = new SMSthread(sms);
				smsThreads.put(contact, smsThread);
			}
			
			// Contact relationship analysis methods go here
			addIntervalToContact(contact, smsThread);	// use median of intervals as scale for reliability
		}
		
		private void addIntervalToContact(Contact contact, SMSthread sthread){
			if(sthread.conversationToggled)
			 {
				contact.addInterval(sthread.lastInterval);
				System.out.println(contact.name + " - " + contact.numIntervals());
//				if(BuildConfig.DEBUG) System.out.println("Contact Interval: " + contact.getScrubbedPhoneNumber() + "->" + sthread.lastInterval);
			}
		}
		
	}
	
	private class SMSthread{
		LinkedList<SMS> thread;
		SMS lastSMS;
		boolean conversationToggled;
		long lastInterval;
		
		SMSthread(SMS sms){
			thread = new LinkedList<SMSAnalysis.SMS>();
			
			thread.add(sms);
			lastSMS = sms;
			conversationToggled = false;
		}
		
		void add(SMS sms){
			thread.add(sms);
			if(lastSMS.type != sms.type){
				conversationToggled = true;
				lastInterval = lastSMS.date - sms.date;
			} else {
				conversationToggled = false;
			}
			lastSMS = sms;
		}
	}
	
	private class SMS{
		long date;
		int type;
		SMS(long date, int type){
			this.date = date;
			this.type = type;
		}
	}

}
