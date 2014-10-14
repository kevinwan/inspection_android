package com.gongpingjia.gpjdetector.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.activity.MainActivity;


public class UpdateService extends Service {
	private NotificationManager notificationManager;
	private Notification notification;
	private File tempFile = null;
	private final boolean cancelUpdate = false;
	private MyHandler myHandler;
	private int download_percent = 0;
	private RemoteViews views;
	private final int notificationId = 1234;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = android.R.drawable.stat_sys_download;
		notification.tickerText = getString(R.string.app_name) + "正在下载新版本...";
		notification.when = System.currentTimeMillis();
		notification.defaults = Notification.DEFAULT_LIGHTS;
		views = new RemoteViews(getPackageName(), R.layout.update_bar);
		notification.contentView = views;

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		notification.setLatestEventInfo(this, "", "", contentIntent);
		notificationManager.notify(notificationId, notification);

		myHandler = new MyHandler(Looper.myLooper(), this);
		Message message = myHandler.obtainMessage(3, 0);
		myHandler.sendMessage(message);
		downFile(intent.getStringExtra("url"));
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void downFile(final String url) {
		new Thread() {
			@Override
			public void run() {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					if (is != null) {
						File rootFile = new File(Environment.getExternalStorageDirectory(), "/gongpingjia");
						if (!rootFile.exists() && !rootFile.isDirectory())
							rootFile.mkdir();

						tempFile = new File(Environment.getExternalStorageDirectory(), "/gongpingjia/" + url.substring(url.lastIndexOf("/") + 1));
						if (tempFile.exists())
							tempFile.delete();
						tempFile.createNewFile();

						BufferedInputStream bis = new BufferedInputStream(is);
						FileOutputStream fos = new FileOutputStream(tempFile);
						BufferedOutputStream bos = new BufferedOutputStream(fos);

						int read;
						long count = 0;
						int precent = 0;
						byte[] buffer = new byte[1024];
						while ((read = bis.read(buffer)) != -1 && !cancelUpdate) {
							bos.write(buffer, 0, read);
							count += read;
							precent = (int) (((double) count / length) * 100);
							if (precent - download_percent >= 5) {
								download_percent = precent;
								Message message = myHandler.obtainMessage(3, precent);
								myHandler.sendMessage(message);
							}
						}
						bos.flush();
						bos.close();
						fos.flush();
						fos.close();
						is.close();
						bis.close();
					}

					if (!cancelUpdate) {
						Message message = myHandler.obtainMessage(2, tempFile);
						myHandler.sendMessage(message);
					} else {
						tempFile.delete();
					}
				} catch (ClientProtocolException e) {
					Message message = myHandler.obtainMessage(4, "下载更新文件失败");
					myHandler.sendMessage(message);
				} catch (IOException e) {
					Message message = myHandler.obtainMessage(4, "下载更新文件失败");
					myHandler.sendMessage(message);
				} catch (Exception e) {
					Message message = myHandler.obtainMessage(4, "下载更新文件失败");
					myHandler.sendMessage(message);
				}
			}
		}.start();
	}

	private void Instanll(File file, Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	class MyHandler extends Handler {
		private final Context context;

		public MyHandler(Looper looper, Context c) {
			super(looper);
			this.context = c;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg != null) {
				switch (msg.what) {
				case 0:
					Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				case 1:
					break;
				case 2:
					download_percent = 0;
					notificationManager.cancel(notificationId);
					Instanll((File) msg.obj, context);
					stopSelf();
					break;
				case 3:
					views.setTextViewText(R.id.tvProcess, "已下载" + download_percent + "%");
					views.setProgressBar(R.id.pbDownload, 100, download_percent, false);
					notification.contentView = views;
					notificationManager.notify(notificationId, notification);
					break;
				case 4:
					notificationManager.cancel(notificationId);
					break;
				}
			}
		}
	}

}
