package team_h.boostcamp.myapplication.data.repository;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import team_h.boostcamp.myapplication.data.remote.deepaffects.AnalyzedEmotionMapper;
import team_h.boostcamp.myapplication.data.remote.deepaffects.DeepAffectApiClient;
import team_h.boostcamp.myapplication.data.remote.deepaffects.request.EmotionAnalyzeRequest;
import team_h.boostcamp.myapplication.model.Diary;
import team_h.boostcamp.myapplication.model.source.local.DiaryDao;


/*
 * Repository 를 data/repository + 가공까지 ! */
public class DiaryRepository implements DiaryRepositoryContract {

    private static DiaryRepository INSTANCE;
    private DeepAffectApiClient deepAffectApiClient;
    private DiaryDao diaryDao;

    private DiaryRepository(@NonNull DeepAffectApiClient deepAffectApiClient,
                            @NonNull DiaryDao diaryDao) {
        this.deepAffectApiClient = deepAffectApiClient;
        this.diaryDao = diaryDao;
    }

    public static DiaryRepository getInstance(@NonNull DeepAffectApiClient deepAffectApiClient,
                                              @NonNull DiaryDao diaryDao) {
        if (INSTANCE == null) {
            synchronized (DiaryRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DiaryRepository(deepAffectApiClient, diaryDao);
                }
            }
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    public Single<List<Diary>> loadMoreDiaryItems(final int idx) {
        return diaryDao.loadMoreDiary(idx).subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Completable deleteRecordItem(@NonNull Diary diary) {
        return diaryDao.deleteDiary(diary).subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Completable insertRecordItem(@NonNull Diary diaryItem) {
        return diaryDao.insertDiary(diaryItem).subscribeOn(Schedulers.io());
    }

    @NonNull
    @Override
    public Single<Integer> analyzeVoiceEmotion(@NonNull EmotionAnalyzeRequest request) {
        return deepAffectApiClient.analyzeVoiceEmotion(request)
                .map(AnalyzedEmotionMapper::parseAnalyzedEmotion)
                .subscribeOn(Schedulers.io());
    }
}
