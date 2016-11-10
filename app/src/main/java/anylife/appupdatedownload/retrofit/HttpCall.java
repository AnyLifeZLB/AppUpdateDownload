package anylife.appupdatedownload.retrofit;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import anylife.appupdatedownload.download.ProgressResponseBody;
import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Http 请求的设置
 * 1.Cancel req
 * Created by Anylife.zlb@gmail.com on 2016/7/11.
 */
public class HttpCall {
	//1.测试数据区
	public static String tempData = "";
	private static String TOKEN;

	private static ApiService apiService;
	private static String baseUrl = "http://api.openweathermap.org/";
//	private static final String WE_CHART_URL = "http://test-default-1.oss-cn-shenzhen.aliyuncs.com/201603/APP/38_BipbipMain030301.apk";

	private static ProgressResponseBody.ProgressListener progressListener;

//	/**
//	 * if you need get data process progress, set this interface listener.
//	 *
//	 * @param tempProgressListener
//	 */
//	public static void setProgressListener(ProgressResponseBody.ProgressListener tempProgressListener) {
//		progressListener = tempProgressListener;
//	}

	public static ApiService getApiService(final ProgressResponseBody.ProgressListener tempProgressListener) {
		progressListener = tempProgressListener;
		if (apiService == null) {
			//1.如果你需要在遇到诸如 401 Not Authorised 的时候进行刷新 token，可以使用 Authenticator
			// 这是一个专门设计用于当验证出现错误的时候，进行询问获取处理的拦截器：
			Authenticator mAuthenticator2 = new Authenticator() {
				@Override
				public Request authenticate(Route route, Response response)
						throws IOException {
					TOKEN = tempData;  //不规范写法
					return response.request().newBuilder()
							.addHeader("Authorization", TOKEN)
							.build();
				}
			};

			Interceptor mTokenInterceptor = new Interceptor() {
				@Override
				public Response intercept(Chain chain) throws IOException {
					Request originalRequest = chain.request();
					if (TOKEN == null) { //|| alreadyHasAuthorizationHeader(originalRequest)) {
//						return chain.proceed(originalRequest);

						Response originalResponse = chain.proceed(originalRequest);
						return originalResponse.newBuilder()
								.body(new ProgressResponseBody(originalResponse.body(), progressListener))
								.build();

//						if (null != progressListener) {
//							return chain.proceed(originalRequest);
//						} else {
//							Response originalResponse = chain.proceed(originalRequest);
//							return originalResponse.newBuilder()
//									.body(new ProgressResponseBody(originalResponse.body(), progressListener))
//									.build();
//						}
					}

					Request authorisedRequest = originalRequest.newBuilder()
							.header("Authorization", TOKEN)
							.build();

					if (null == progressListener) {
						return chain.proceed(authorisedRequest);
					} else {
						Response originalResponse = chain.proceed(authorisedRequest);
						return originalResponse.newBuilder()
								.body(new ProgressResponseBody(originalResponse.body(), progressListener))
								.build();
					}
				}
			};

			HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
			loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

			OkHttpClient okHttpClient = new OkHttpClient.Builder()
					.addInterceptor(loggingInterceptor)
					.retryOnConnectionFailure(true)                 //出现错误进行重新的连接？重试几次？错误了有没有回调？
					.connectTimeout(10, TimeUnit.SECONDS)           //设置超时时间 15 秒
					.readTimeout(16, TimeUnit.SECONDS)              //设置超时时间 15 秒
					.addNetworkInterceptor(mTokenInterceptor)       //网络拦截器。
					.authenticator(mAuthenticator2)
					.build();

			Retrofit client = new Retrofit.Builder()
					.baseUrl(baseUrl)
					.client(okHttpClient)
					.addConverterFactory(GsonConverterFactory.create())
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())  //RXjava
					.build();
			apiService = client.create(ApiService.class);
		}

		return apiService;
	}


	/**
	 *
	 */
	public interface ApiService {
		@Streaming
		@GET()
		Observable<ResponseBody> downloadApp(@Url String url);

//		@Streaming
//		@GET(WE_CHART_URL)
//		Observable<ResponseBody> downloadApp();

	}

}
