package anylife.appupdatedownload;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

import anylife.appupdatedownload.download.AppUpdateUtils;
import anylife.appupdatedownload.download.FileUtil;
import anylife.appupdatedownload.download.ProgressResponseBody;
import anylife.appupdatedownload.retrofit.HttpCall;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * use retrofit2 to downLoad app from ds
 */
public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static String getUpdateJsonStr = "{\n" +
			"    \"type\": \"update\",\n" +
			"    \"appVersion\": 38,\n" +
			"    \"appMessage\": \"WHAT IS NEW\\n• Now you can draw or add text and emojis to photos\\n• In groups, you can now mention specific people by typing the @ symbol\",\n" +
			"    \"downLoadUrl\": \"http://test-default-1.oss-cn-shenzhen.aliyuncs.com/201603/APP/38_BipbipMain030301.apk\",\n" +
			"    \"isForceUpdate\": \"true\"\n" +
			"}";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.checkupdate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				checkUpdate();
			}
		});
	}

	private void checkUpdate(){
		final VersionMess versionMess = new Gson().fromJson(getUpdateJsonStr, VersionMess.class);
		if (versionMess != null && versionMess.getAppVersion() > 0 && !TextUtils.isEmpty(versionMess.getDownLoadUrl())) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("UPDATE")
					.setMessage(versionMess.getAppMessage())
					.setCancelable(false)
					.setPositiveButton("update", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(MainActivity.this, versionMess.getDownLoadUrl(), Toast.LENGTH_SHORT).show();
							downLoadApp(versionMess.getDownLoadUrl());
						}
					})
					.show();
		}
	}


	/**
	 * download app
	 */
	private void downLoadApp(String downloadUrl) {
		final ProgressDialog dialog = AppUpdateUtils.getDownLoadProgressDialog(MainActivity.this);

		HttpCall.getApiService(
				new ProgressResponseBody.ProgressListener() {
					@Override
					public void update(long bytesRead, long contentLength, boolean done) {
						dialog.setMax((int) (contentLength / 1024));
						dialog.setProgress((int) (bytesRead / 1024));
					}
				}
		).downloadApp(downloadUrl).subscribeOn(Schedulers.io())
				.map(new Func1<ResponseBody, File>() {
					@Override
					public File call(ResponseBody responseBody) {
						File apk = new File(Environment.getExternalStorageDirectory(), "WeChart");
						FileUtil.save(responseBody.byteStream(), apk);
						return apk;
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<File>() {
					@Override
					public void call(File apk) {
						dialog.dismiss();
						AppUpdateUtils.getInstallAppDialog(MainActivity.this, apk).show();
					}
				});
	}

}
