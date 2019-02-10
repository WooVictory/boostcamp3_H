package teamh.boostcamp.myapplication.view;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment<B extends ViewDataBinding> extends Fragment implements BaseView{
    /* View 가 모두 Presenter 가 필요하지 않다고 생각함
     * 하지만 Databinding 은 모든 View 가 필요하므로 공통 속성으로 추출 */
    protected B binding;

    public BaseFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Binding 설정
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected abstract int getLayoutId();
}