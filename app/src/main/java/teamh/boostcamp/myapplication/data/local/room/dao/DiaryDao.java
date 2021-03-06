package teamh.boostcamp.myapplication.data.local.room.dao;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import teamh.boostcamp.myapplication.data.local.room.entity.DiaryEntity;
import teamh.boostcamp.myapplication.data.model.Diary;
import teamh.boostcamp.myapplication.data.model.Emotion;

@Dao
public interface DiaryDao {

    @Query("SELECT * FROM diaries WHERE recordDate < :recordDate ORDER BY recordDate DESC LIMIT :pageSize")
    Single<List<DiaryEntity>> loadDiaryList(@NonNull Date recordDate,
                                            final int pageSize);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull DiaryEntity... diaryEntities);

    @Query("DELETE FROM diaries")
    void deleteAllDiaries();

    @Query("Select * FROM diaries WHERE recordDate > :startDate AND recordDate < :endDate AND selectedEmotion = :emotion ORDER BY recordDate LIMIT :limitCount")
    Single<List<Diary>> selectDiaryListByEmotionAndDate(Emotion emotion, Date startDate, Date endDate, int limitCount);

    @Query("SELECT * FROM diaries ORDER BY recordDate DESC LIMIT 1")
    Single<DiaryEntity> loadRecentInsertedDiary();

    @Query("DELETE FROM diaries WHERE id=:id")
    void delete(String id);

    @Query("SELECT * FROM diaries")
    Observable<List<Diary>> loadAll();

    @Query("SELECT * FROM diaries WHERE id=:id")
    Single<DiaryEntity> selectDiaryById(String id);

    @Query("SELECT * FROM diaries WHERE id NOT IN (:diaryEntityIdList)")
    Maybe<List<DiaryEntity>> loadNotBackupDiaryList(@NonNull List<String> diaryEntityIdList);

    @Query("SELECT * FROM diaries")
    Single<List<DiaryEntity>> loadAllDiaryEntities();

    @Update
    void updateDiaries(DiaryEntity ...diaryEntityList);

    //    @Query("Select * FROM diaries WHERE recordDate > :startDate AND recordDate < :endDate AND selectedEmotion = :emotion ORDER BY recordDate LIMIT :limitCount")
//    Maybe<List<Diary>> selectDiaryListByEmotionAndDate1(Emotion emotion, Date startDate, Date endDate, int limitCount);
}
