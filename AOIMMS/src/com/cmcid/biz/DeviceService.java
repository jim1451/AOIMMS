package com.cmcid.biz;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cmcid.R;
import com.cmcid.util.logMa;
import com.cmcid.view.MainActivity;
import com.ftdi.j2xx.D2xxManager;
import com.handset.Device;

public class DeviceService extends Service{
	
	Handler FT_handler;
	Context FT_Context;
	
	public static D2xxManager ftD2xx = null;
	public Device device;
	
	private final IBinder binder = new MyBinder();
	
	public class MyBinder extends Binder{
		public DeviceService GetService(){
			return DeviceService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("Service is binder!");
		logMa.d("martrin", "----Service is binder----");
		return binder;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		logMa.d("martrin", "----onCreate----");
		Notification notification = new Notification(R.drawable.ic_launcher, "OIMMS_SERVICE", System.currentTimeMillis());
		Intent notificationIntent = new Intent(this,MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "OIMMS", "OIMMS-Service", pendingIntent);
		startForeground(1, notification);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		logMa.d("martrin", "----onDestroy----");
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		System.out.println("Service is unBinder!");
		logMa.d("martrin", "----Service is unBinder!----");
		return true;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		logMa.d("martrin", "----onStartCommand----");
		return super.onStartCommand(intent, flags, startId);
	}
	public void setBinder(Context context,Handler handler){
		try {
			//»ñÈ¡D2xxÊµÀý
			logMa.d("martrin", "----SetBinder Start----");
			ftD2xx = D2xxManager.getInstance(this);
			FT_Context = context;
			FT_handler = handler;
			logMa.d("martrin", "----SetBinder Enddd----");
		} catch (D2xxManager.D2xxException e) {
			Log.e("TT", "getInstance fail!!");
			logMa.d("martrin", "----getInstance fail!!----");
		}
		device = new Device(ftD2xx, getApplicationContext(), FT_handler);
	}
}
