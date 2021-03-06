package teamh.boostcamp.myapplication.data.remote.apis.deepaffects;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import teamh.boostcamp.myapplication.BuildConfig;
import teamh.boostcamp.myapplication.data.remote.apis.deepaffects.request.EmotionAnalyzeRequest;
import teamh.boostcamp.myapplication.data.remote.apis.deepaffects.response.EmotionAnalysisResponse;

/*
 * Url 에 맞는 Client 생성 */
public class DeepAffectApiClient {

    private static final String BASE_URL = "https://proxy.api.deepaffects.com/audio/generic/api/v2/";

    private static DeepAffectApiClient INSTANCE;

    private final Retrofit retrofit;

    private DeepAffectApiClient() {
        // Log 를 확인하기 위한 okHttp 설정
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttp Object 생성
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // retrofit client 생성
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    @NonNull
    public static DeepAffectApiClient getInstance() {
        if (INSTANCE == null) {
            synchronized (DeepAffectApiClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DeepAffectApiClient();
                }
            }
        }
        return INSTANCE;
    }

    @NonNull
    public Single<List<EmotionAnalysisResponse>> analyzeVoiceEmotion(@NonNull EmotionAnalyzeRequest request) {
        return retrofit.create(EmotionAnalysisAPI.class)
                .analyzeRecordEmotion(BuildConfig.deep_affects_apikey, request.getRequest())
                .subscribeOn(Schedulers.io());
    }
}
