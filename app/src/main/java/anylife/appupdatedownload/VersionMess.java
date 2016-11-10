package anylife.appupdatedownload;

/**
 *
 * Created by zenglb on 2016/11/9.
 */
public class VersionMess {

	/**
	 * type : update
	 * appVersion : 38
	 * appMessage : WHAT IS NEW
	 • Now you can draw or add text and emojis to photos
	 • In groups, you can now mention specific people by typing the @ symbol
	 * downLoadUrl : http//123456789
	 * isForceUpdate : true
	 */

	private String type;
	private int appVersion;
	private String appMessage;
	private String downLoadUrl;
	private String isForceUpdate;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(int appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppMessage() {
		return appMessage;
	}

	public void setAppMessage(String appMessage) {
		this.appMessage = appMessage;
	}

	public String getDownLoadUrl() {
		return downLoadUrl;
	}

	public void setDownLoadUrl(String downLoadUrl) {
		this.downLoadUrl = downLoadUrl;
	}

	public String getIsForceUpdate() {
		return isForceUpdate;
	}

	public void setIsForceUpdate(String isForceUpdate) {
		this.isForceUpdate = isForceUpdate;
	}
}
