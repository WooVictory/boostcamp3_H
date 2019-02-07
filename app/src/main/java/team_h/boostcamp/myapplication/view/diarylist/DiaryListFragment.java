package team_h.boostcamp.myapplication.view.diarylist;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tedpark.tedpermission.rx2.TedRx2Permission;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import team_h.boostcamp.myapplication.R;
import team_h.boostcamp.myapplication.data.remote.deepaffects.DeepAffectApiClient;
import team_h.boostcamp.myapplication.data.repository.DiaryRepository;
import team_h.boostcamp.myapplication.databinding.FragmentDiaryListBinding;
import team_h.boostcamp.myapplication.model.Diary;
import team_h.boostcamp.myapplication.model.source.local.AppDatabase;
import team_h.boostcamp.myapplication.utils.KeyPadUtil;
import team_h.boostcamp.myapplication.view.BaseFragment;

public class DiaryListFragment extends BaseFragment<FragmentDiaryListBinding> implements DiaryContract.View {

    private Context context;
    private DiaryPresenterImpl presenter;

    private HashTagListAdapter hashTagListAdapter;
    private DiaryListAdapter diaryListAdapter;

    private CompositeDisposable compositeDisposable;

    public DiaryListFragment() {/*Required empty public constructor*/}

    public static DiaryListFragment newInstance() {
        return new DiaryListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initPresenter();
        initView();

        binding.setPresenter(presenter);

        presenter.loadMoreDiaryItems();

        compositeDisposable = new CompositeDisposable();

        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_diary_list;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onViewDetached();
        context = null;
        presenter = null;
        compositeDisposable.clear();
    }

    @Override
    public void showNoRecordFileMessage() {
        showToastMessage(R.string.item_record_no_voice_file);
    }

    @Override
    public void showEmotionNotSelectedMessage() { showToastMessage(R.string.item_record_no_emotion_selected); }

    @Override
    public void showRecordNotFinishedMessage() { showToastMessage(R.string.item_record_now_recording); }

    @Override
    public void showEmotionAnalyzeFailMessage() { showToastMessage(R.string.item_record_emotion_analyze_fail); }

    @Override
    public void showDiaryItemSaveFail() {
        showToastMessage(R.string.item_record_save_fail);
    }

    @Override
    public void showDiaryItemSaved() {
        showToastMessage(R.string.item_record_save_success);
    }

    @Override
    public void closeHashTagKeyPad() {
        KeyPadUtil.closeKeyPad(context, binding.etItemRecordInput);
    }

    @Override
    public void showMoreDiaryItems(@NonNull List<Diary> diaryList) {
        diaryListAdapter.addDiaryItems(diaryList);
    }

    private void showToastMessage(int stringId) {
        Toast.makeText(context, getString(stringId), Toast.LENGTH_SHORT).show();
    }

    private void initPresenter() {
        presenter = new DiaryPresenterImpl(this,
                DiaryRepository.getInstance(DeepAffectApiClient.getInstance(),
                        AppDatabase.getInstance(context).diaryDao()),
                new DiaryRecorderImpl());
    }

    /* View 초기화 */
    private void initView() {
        // Tag RecyclerView, Adapter 설정
        hashTagListAdapter = new HashTagListAdapter(context);
        hashTagListAdapter.setItemClickListener(position -> hashTagListAdapter.removeItem(position));

        binding.recyclerViewItemRecordTags.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        binding.recyclerViewItemRecordTags.setAdapter(hashTagListAdapter);

        // DiaryList 설정
        diaryListAdapter = new DiaryListAdapter(context);
        diaryListAdapter.setOnRecordItemClickListener(filePath -> { /*재생 구현*/ });

        binding.recyclerViewMainList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        binding.recyclerViewMainList.setAdapter(diaryListAdapter);

        // 무한 스크롤
        binding.recyclerViewMainList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1)) {
                    presenter.loadMoreDiaryItems();
                }
            }
        });

        // 해시태그 스페이스 기준으로 입력
        binding.etItemRecordInput.setFilters(new InputFilter[]{
                (source, start, end, dest, dStart, dEnd) -> {
                    for (int i = start; i < end; ++i) {
                        if (Character.isSpaceChar(source.charAt(i)) && binding.etItemRecordInput.getText() != null) {
                            hashTagListAdapter.addItem("#" + binding.etItemRecordInput.getText().toString().trim());
                            binding.etItemRecordInput.setText("");
                            return null;
                        }
                    }
                    return null;
                }
        });

        // 녹음 버튼 이벤트
        binding.buttonRecordItemRecord.setOnClickListener(v ->
                compositeDisposable.add(TedRx2Permission.with(context)
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                        .setRationaleMessage(getString(R.string.item_record_permission_msg))
                        .setRationaleTitle(getString(R.string.item_record_permission_title))
                        .request()
                        .subscribe(tedPermissionResult -> {
                            if (tedPermissionResult.isGranted()) {
                                // KeyPad 가 열려있으면 닫아주기
                                KeyPadUtil.closeKeyPad(context, binding.etItemRecordInput);
                                // 버튼 클릭 이벤트 처리
                                presenter.recordDiaryItem();
                            } else {
                                // 요구한 권한이 하나라도 없으면 토스트 메시지
                                showToastMessage(R.string.item_record_permission_denied);
                            }
                        }, error -> {
                            // 에러 출력
                            Log.e("Test", error.getMessage());
                        })
                ));

        // 저장 버튼 클릭
        binding.buttonItemRecordDone.setOnClickListener(v -> presenter.saveDiaryItem(hashTagListAdapter.getItemList()));
    }
}
