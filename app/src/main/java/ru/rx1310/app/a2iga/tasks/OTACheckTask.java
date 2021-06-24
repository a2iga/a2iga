// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.tasks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.activities.MainActivity;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.HttpUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;

public class OTACheckTask extends AsyncTask<Void, Void, String> {

    private Context oContext;
	private boolean oProgressDialog;

	private ProgressDialog progressDialog;

    public OTACheckTask(Context context, boolean isProgressDialogEnabled) {
        this.oContext = context;
		this.oProgressDialog = isProgressDialogEnabled;
    }

    protected void onPreExecute() {

		if (oProgressDialog) {
			progressDialog = new ProgressDialog(oContext, R.style.AppTheme_Dialog_Alert);
			progressDialog.setTitle(oContext.getString(R.string.ota_dlg_checking));
			progressDialog.setMessage(oContext.getString(R.string.ota_dlg_checking_description));
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

    }

    @Override
    protected void onPostExecute(String result) {

		if (oProgressDialog) progressDialog.dismiss();
        if (!TextUtils.isEmpty(result)) parseJson(result);
		//updateNotification("12", 583, "Это небольшое минорное обновление, которое исправляет старые болячки приложения. Теперь, например, при отсутствии у модуля класса «ModuleSettings» выводится сообщение об этом, а не крашится все приложение. Также стоит отметить исправления фризов AppsList. Теперь их быть не должно, вроде исправил.");
    }

    private void parseJson(String result) {

        try {

            JSONObject obj = new JSONObject(result);

			String 
				versionName = obj.getString(Constants.OTA.VERSION_NAME),
				updateMessage = obj.getString(Constants.OTA.MESSAGE),
				urlApk = obj.getString(Constants.OTA.URL_APK),
				urlChangelog = obj.getString(Constants.OTA.URL_CHANGELOG);

			int 
				versionCode = obj.getInt(Constants.OTA.VERSION_CODE),
				versionCodeInstalled = AppUtils.getVersionCode(oContext, oContext.getPackageName());

			if (versionCode > versionCodeInstalled) {

				if (oProgressDialog) updateDialog(oContext, versionName, versionCode, updateMessage, urlApk, urlChangelog);
				else updateNotification(versionName, versionCode, updateMessage, urlApk, urlChangelog);

			} else {
				AppUtils.showToast(oContext, oContext.getString(R.string.ota_msg_used_latest_release));
			}

			AppUtils.Log(oContext, "d", "Parse JSON: " + result);

        } catch (JSONException e) {
			AppUtils.Log(oContext, "e", "parseJson: " + e);
        }

    }

	void updateDialog(final Context context, String updateVersion, int updateVersionCode, String updateMessage, final String apkUrl, final String changelogUrl) {

		android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.AppTheme_Dialog_Alert);

		alertBuilder.setIcon(R.drawable.ic_logo);
		alertBuilder.setTitle(oContext.getString(R.string.app_name) + " " + updateVersion + "." + updateVersionCode);
		alertBuilder.setMessage(updateMessage);
		alertBuilder.setPositiveButton(R.string.ota_action_download, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int i) {
				oContext.startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse(apkUrl)));
			}
		});
		alertBuilder.setNegativeButton(R.string.ota_action_changelog, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int i) {
				context.startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse(changelogUrl)));
			}
		});
		alertBuilder.setNeutralButton(R.string.ota_action_copy_url, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int i) {
				ClipboardManager mClipboardMng = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData mClipData = ClipData.newPlainText(null, apkUrl);
				mClipboardMng.setPrimaryClip(mClipData);
				AppUtils.showToast(context, context.getString(R.string.ota_msg_url_copied));
			}
		});
		alertBuilder.show();

		AppUtils.Log(oContext, "d", "updateDialog = show()");

		// ? Сохранение URL файла со списком изменений
		// SharedPrefUtils.saveData(oContext, "ota.changelogUrl", changelogUrl);
		
	}
	
	void updateNotification(String versionName, int versionCode, String updateMessage, String apkUrl, String changelogUrl) {
		
		/*RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_custom_view);
		remoteViews.setImageViewResource(R.id.image_icon, iconResource);
		remoteViews.setTextViewText(R.id.text_title, title);
		remoteViews.setTextViewText(R.id.text_message, message);
		remoteViews.setImageViewResource(R.id.image_end, imageResource);*/
		
		Intent dlApkIntent = new Intent (Intent.ACTION_VIEW, Uri.parse(apkUrl));
		Intent openChangelogIntent = new Intent (Intent.ACTION_VIEW, Uri.parse(changelogUrl));
		Intent openAppIntent = new Intent(oContext, MainActivity.class);
		
		PendingIntent dlApkPendingIntent = PendingIntent.getActivity(oContext, 0, dlApkIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent openChangelogPendingIntent = PendingIntent.getActivity(oContext, 0, openChangelogIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingOpenAppIntent = PendingIntent.getActivity(oContext, 0, openAppIntent, PendingIntent.FLAG_ONE_SHOT);
		
		String notifChannelID = "a2iga_updater";
		
		NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(oContext, notifChannelID);
		
		notifBuilder.setSmallIcon(R.drawable.ic_logo);
		//notifBuilder.setLargeIcon(BitmapFactory.decodeResource(oContext.getResources(),R.drawable.ic_info));
		notifBuilder.setColorized(true);
		notifBuilder.setSubText(versionName + "." + versionCode);
		notifBuilder.setTicker("ticker");
		notifBuilder.setContentTitle(oContext.getString(R.string.ota_msg_update_available));
		notifBuilder.setContentText(oContext.getString(R.string.ota_notif_desc));
		notifBuilder.setColor(AppUtils.getSystemAccentColor(oContext));
		notifBuilder.setContentInfo("content_info");
		notifBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
		notifBuilder.setAutoCancel(false);
		//notifBuilder.setOngoing(true);
		notifBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
		notifBuilder.setContentIntent(pendingOpenAppIntent);
		notifBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(updateMessage));
		notifBuilder.addAction(R.drawable.ic_download, oContext.getString(R.string.ota_action_download), dlApkPendingIntent);
		notifBuilder.addAction(R.drawable.ic_info, oContext.getString(R.string.ota_action_changelog), openChangelogPendingIntent);
		
		NotificationManager notifManager = (NotificationManager) oContext.getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel notifChannel = new NotificationChannel(notifChannelID, oContext.getString(R.string.ota), NotificationManager.IMPORTANCE_MAX);
			notifChannel.setShowBadge(true);
			notifChannel.setDescription(oContext.getString(R.string.ota_notif_channel_desc));
			assert notifManager != null;
			notifManager.createNotificationChannel(notifChannel);
		}

		assert notifManager != null;
		notifManager.notify(0, notifBuilder.build());
		
	}

    @Override
    protected String doInBackground(Void... args) {
		
		return HttpUtils.get(oContext, Constants.OTA.URL_JSON);

	}

	// ? Проверка обновленмй
	public static void checkUpdates(Context context, boolean isProgressDialogEnabled) {

		SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy (HH:mm:ss)", Locale.getDefault());
		String isLastCheckDate = mDateFormat.format(new Date());

		if (AppUtils.isNetworkAvailable(context)) {
			SharedPrefUtils.saveData(context, "ota.lastCheckDate", isLastCheckDate);
			new OTACheckTask(context, isProgressDialogEnabled).execute();
		} else AppUtils.showToast(context, context.getString(R.string.ota_msg_no_network));
		
	}
	
}
