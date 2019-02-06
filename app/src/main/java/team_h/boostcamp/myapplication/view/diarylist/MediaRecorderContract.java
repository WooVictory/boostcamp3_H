package team_h.boostcamp.myapplication.view.diarylist;

import androidx.annotation.NonNull;

public interface MediaRecorderContract {

    /* 녹음 시작 */
    void startRecord();

    /* 녹음 종료 */
    void finishRecord();

    /* 자원 해제 */
    void releaseRecorder();

    /* 파일 경로 반환*/
    @NonNull
    String getFilePath();

    /* 녹음 준비 */
    void prepareRecord();
}