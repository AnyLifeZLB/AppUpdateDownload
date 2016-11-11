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
 *
 * Created by Anylife.zlb@gmail.com on 2016/7/11.
 */
public class HttpCall {

	private static ApiService apiService;
	private static String baseUrl = "http://api.openweathermap.org/";

	private static ProgressResponseBody.ProgressListener progressListener;


	public static ApiService getApiService(final ProgressResponseBody.ProgressListener tempProgressListener) {
		progressListener = tempProgressListener;
		if (apiService == null) {

			Authenticator mAuthenticator2 = new Authenticator() {
				@Override
				public Request authenticate(Route route, Response response)
						throws IOException {
//					TOKEN = YourRefreshToken;
					return response.request().newBuilder()
//							.addHeader("Authorization", TOKEN)
							.build();
				}
			};

			Interceptor mTokenInterceptor = new Interceptor() {
				@Override
				public Response intercept(Chain chain) throws IOException {
					Request originalRequest = chain.request();

						Response originalResponse = chain.proceed(originalRequest);
						return originalResponse.newBuilder()
								.body(new ProgressResponseBody(originalResponse.body(), progressListener))
								.build();

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
//					.authenticator(mAuthenticator2)
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
	}

}
