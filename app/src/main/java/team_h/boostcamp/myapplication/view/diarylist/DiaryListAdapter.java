package team_h.boostcamp.myapplication.view.diarylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import team_h.boostcamp.myapplication.R;
import team_h.boostcamp.myapplication.databinding.ItemRecordDiaryBinding;
import team_h.boostcamp.myapplication.model.Diary;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.DiaryHolder> {

    private List<Diary> diaryList;
    private Context context;
    private OnRecordItemClickListener onRecordItemClickListener;
    private String[] selectedEmotion;

    DiaryListAdapter(@NonNull Context context) {
        this.context = context;
        this.diaryList = new ArrayList<>();
        this.selectedEmotion = context.getResources().getStringArray(R.array.graph_emojis);
    }

    void setOnRecordItemClickListener(@NonNull OnRecordItemClickListener onRecordItemClickListener) {
        this.onRecordItemClickListener = onRecordItemClickListener;
    }

    @NonNull
    @Override
    public DiaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemRecordDiaryBinding itemRecordDiaryBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_record_diary,
                parent,
                false
        );

        return new DiaryHolder(itemRecordDiaryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryHolder holder, int position) {

        final Diary diary = diaryList.get(position);

        if(onRecordItemClickListener != null) {
            holder.itemRecordDiaryBinding.ivItemDiaryPlay.setOnClickListener(
                    v -> onRecordItemClickListener.onDiaryItemClicked(diary.getRecordFilePath())
            );
        }

        holder.itemRecordDiaryBinding.tvItemDiaryDate.setText(diary.getRecordDate());
        holder.itemRecordDiaryBinding.tvItemDiaryEmotion.setText(selectedEmotion[diary.getSelectedEmotion()]);
        holder.itemRecordDiaryBinding.tvItemDiaryTags.setText(diary.getTags());
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    void addDiaryItem(@NonNull Diary diary) {
        int position = diaryList.size();
        diaryList.add(diary);
        notifyItemInserted(position);
    }

    void addDiaryItems(@NonNull List<Diary> diaries) {
        int from = diaryList.size();
        diaryList.addAll(diaries);
        notifyItemMoved(from, diaryList.size());
    }

    class DiaryHolder extends RecyclerView.ViewHolder {

        ItemRecordDiaryBinding itemRecordDiaryBinding;

        DiaryHolder(@NonNull ItemRecordDiaryBinding itemRecordDiaryBinding) {
            super(itemRecordDiaryBinding.getRoot());
            this.itemRecordDiaryBinding = itemRecordDiaryBinding;
        }
    }
}
