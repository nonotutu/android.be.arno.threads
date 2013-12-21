package be.arno.threads;

import java.util.Calendar;
import java.util.Random;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ProgressBar pgbr;
	private TextView txvw;
	
	NotificationManager notifManag;
	NotificationCompat.Builder mBuilder;
	final int notificationRef = 2013;
	final int progressNotificationRef = 2014;
	
	Thread_AsyncTask thread;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    // prevent app reset when orientation changed
	    Log.i("onConfigurationChanged", newConfig.toString());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		Button bttn2 = (Button)findViewById(R.id.main_bttn2);
		pgbr = (ProgressBar)findViewById(R.id.main_pgbr);
		txvw = (TextView)findViewById(R.id.main_txvw);
		
		bttn2.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if ( thread != null ) {
							Log.i("thread.getStatus()", ""+thread.getStatus());
							switch (thread.getStatus()) {
							case RUNNING:
							case PENDING:
								thread.cancel(true);
								txvw.setText(txvw.getText().toString() + " CANCELLED");
								break;
							case FINISHED:
								thread = new Thread_AsyncTask();
								txvw.setText("");
								pgbr.setProgress(0);
							    thread.execute();
							    break;
							}
						} else {
							thread = new Thread_AsyncTask();
							txvw.setText("");
							pgbr.setProgress(0);
						    thread.execute();					    
						}
					}
				});
		
		notifManag = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	}

													// paramètre d'entrée
															// paramètre de progression
																	// paramètre de retour
	private class Thread_AsyncTask extends AsyncTask<String, Integer, String>{
		
		long time;

		
		
		@Override
		protected void onPreExecute() {								// TODO why Base ?
			// makeNotif(notificationRef, "New thread", "New thread", MainActivity.class);
			time = Calendar.getInstance().getTimeInMillis();
		}
		
		@Override
		protected String doInBackground(String... str) {
			
			Random rand = new Random();
			
			// This gives a random integer between 65 (inclusive) and 80 (exclusive), one of 65,66,...,78,79.
			// int r = rand.nextInt(80-65) + 65;

			
			int r1 = 0, r2 = 0;
			int k1 = 0;
			int[] k2 = new int[12];
			float k3 = 0;
			int max = 10000000;
			for ( int i = 1 ; i <= max && !isCancelled() ; i+=1 ) {
				r1 = rand.nextInt(6)+1;
				r2 = rand.nextInt(6)+1;
				k1 += 1;
				//if ( r == 0 ) k2 += 1;
				k2[r1+r2-1] += 1;
				if ( i % ( max / 100 ) == 100 )
					publishProgress( i / ( max / 100 ) + 1);
			}
			
			// k3 = k1 / k2;
					// paramètre de retour
			return ""
					+ k1 + "\n"
					+ k2[0] + " " + k2[0] / (float) k1 * 100 + "\n" 
					+ k2[1] + " " + k2[1] / (float) k1 * 100 + "\n"
					+ k2[2] + " " + k2[2] / (float) k1 * 100 + "\n"
					+ k2[3] + " " + k2[3] / (float) k1 * 100 + "\n"
					+ k2[4] + " " + k2[4] / (float) k1 * 100 + "\n"
					+ k2[5] + " " + k2[5] / (float) k1 * 100 + "\n"
					+ k2[6] + " " + k2[6] / (float) k1 * 100 + "\n"
					+ k2[7] + " " + k2[7] / (float) k1 * 100  + "\n"
					+ k2[8] + " " + k2[8] / (float) k1 * 100  + "\n"
					+ k2[9] + " " + k2[9] / (float) k1  * 100 + "\n"
					+ k2[10] + " " + k2[10] / (float) k1  * 100 + "\n"
					+ k2[11] + " " + k2[11] / (float) k1  * 100 
					;
		}
		
		@Override						// paramètre de progression
		protected void onProgressUpdate(Integer... i) {
			//makeNotif(notificationRef, "Threading", "I'm threading : " + i[0], MainActivity.class);
			notificationProgress(i[0]);
			pgbr.setProgress(i[0]);
			txvw.setText(txvw.getText().toString() + " " + i[0] + " ");
		}
		
		@Override
		protected void onPostExecute(String result) {
			notifManag.cancel(progressNotificationRef);
			makeNotif(notificationRef, "Calculate", "Calculation finished", null, null, MainActivity.class);
			time = Calendar.getInstance().getTimeInMillis() - time;
			txvw.setText(result + "\n" + millis2HR(time));
			// makeProgressNotification();
		}
	}
	
	
	public void notificationProgress(int i) {
		
		mBuilder = new NotificationCompat.Builder(this);

		mBuilder.setContentTitle("Calculate")
				.setContentText("Calculation in progress")
				.setSmallIcon(R.drawable.ic_launcher);

		mBuilder.setProgress(100, i, false);
		
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent onNotificationClickPentingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
		mBuilder.setContentIntent(onNotificationClickPentingIntent);
		
		notifManag.notify(progressNotificationRef, mBuilder.build());
	}
	
	
	public void makeNotif(int notificationRef, String contentTitle, String contentText, String subText, String contentInfo, Class<MainActivity> classToLauchOnClick) {
		mBuilder = new NotificationCompat.Builder(this)
													.setSmallIcon(R.drawable.ic_launcher)
													.setContentTitle(contentTitle)
													.setSubText(subText)
													.setContentInfo(contentInfo)
													.setContentText(contentText);

		Intent intent = new Intent(getApplicationContext(), classToLauchOnClick);
		PendingIntent onNotificationClickPentingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
		mBuilder.setContentIntent(onNotificationClickPentingIntent);

		Notification mNotification = mBuilder.build();
		//mNotification.flags |= Notification.FLAG_NO_CLEAR;
		notifManag.notify(notificationRef, mNotification);
	}
	
	
	private String millis2HR(long time){
		
		long days  = time / 1000 / 60 / 60 / 24;
		time = time - days * 1000 * 60 * 60 * 24;
		long hours = time / 1000 / 60 / 60;
		time = time - hours * 1000 * 60 * 60;
		long mins  = time / 1000 / 60;
		time = time - mins * 1000 * 60;
		long secs  = time / 1000;
		time = time - secs * 1000;
		long msecs = time;
		time = time - msecs;
		
		String HR = "";
		
		if ( days  > 0 ) HR += days  + "d ";
		if ( hours > 0 ) HR += hours + "h ";
		if ( mins  > 0 ) HR += mins  + "m ";
		if ( secs  > 0 ) HR += secs  + "s ";
		if ( msecs > 0 ) HR += msecs + "µs ";
				
		return HR;
	}
}

	
