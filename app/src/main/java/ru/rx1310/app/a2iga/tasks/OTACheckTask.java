package ru.rx1310.app.a2iga.tasks;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.R;
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

				updateDialog(oContext, versionName, versionCode, updateMessage, urlApk, urlChangelog);

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
		} else { AppUtils.showToast(context, context.getString(R.string.ota_msg_no_network)); }
		

	}
	
}
