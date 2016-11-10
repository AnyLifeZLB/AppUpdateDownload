package anylife.appupdatedownload.download;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;

import anylife.appupdatedownload.R;


/**
 *
 *
 */
public class AppUpdateUtils {

	/**
	 * show Download ProgressDialog
	 *
	 * @param mContext
	 * @return
	 */
	public static ProgressDialog getDownLoadProgressDialog(Context mContext) {
		ProgressDialog dialog = new ProgressDialog(mContext);
		dialog.setProgressNumberFormat("%1d KB / %2d KB");
		dialog.setTitle(mContext.getString(R.string.download));
		dialog.setMessage(mContext.getString(R.string.download_in_progress));
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);  //不显示后到通知栏
		dialog.show();
		return dialog;
	}

	/**
	 * do you want to install app? yes ,of couse
	 * if no need,you can call AppUpdateUtils.install(mContext, apk) direct
	 *
	 * @param mContext
	 * @param apk
	 * @return
	 */
	public static AlertDialog getInstallAppDialog(final Context mContext, final File apk) {
		return new AlertDialog.Builder(mContext).
				setTitle(mContext.getString(R.string.download_completed)).
				setPositiveButton(mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (apk == null || !apk.exists()) {
							Toast.makeText(mContext, R.string.app_not_downloaded, Toast.LENGTH_SHORT).show();
							return;
						}
						AppUpdateUtils.install(mContext, apk);
					}
				}).
				setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
	}


	/**
	 * Launch os install ui
	 *
	 * @param context
	 * @param apk
	 */
	public static void install(Context context, File apk) {
		if (apk == null || !apk.exists()) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apk.toString()), "application/vnd.android.package-archive");
		context.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
