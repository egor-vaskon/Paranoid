package com.egorvaskon.paranoid.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egorvaskon.paranoid.DisposableManager;
import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.Utils;
import com.egorvaskon.paranoid.ui.activity.EditSecretActivity;
import com.egorvaskon.paranoid.ui.adapter.BaseRecyclerViewAdapterWithSelectableItems;
import com.egorvaskon.paranoid.ui.adapter.ItemTouchHelperCallbackImpl;
import com.egorvaskon.paranoid.ui.adapter.SecretsAdapter;
import com.egorvaskon.paranoid.ui.adapter.view_holder.SecretViewHolder;
import com.egorvaskon.paranoid.ui.viewmodel.KeysViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.QuizViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.SecretsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SecretsFragment extends Fragment {

    private static final String TAG = "SecretsFragment";

    private SecretsAdapter mSecretsAdapter;
    private RecyclerView mRecyclerView;

    private final DisposableManager mDisposableManager = new DisposableManager();

    private KeysViewModel mKeysViewModel;
    private SecretsViewModel mSecretsViewModel;

    private long mSelectedSecretId;
    private QuizViewModel mDecodingQuizViewModel;

    private static final String DECODING_QUIZ = "decoding_quiz_view_model";
    private static final String DECODING_QUIZ_DIALOG_TAG = "decoding_quiz_dialog_tag";

    public SecretsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_secrets,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(view instanceof RecyclerView){
            mRecyclerView = (RecyclerView) view;
            mSecretsAdapter = new SecretsAdapter(view.getContext(),true,false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());

            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelperCallbackImpl(mRecyclerView));

            touchHelper.attachToRecyclerView(mRecyclerView);

            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),layoutManager.getOrientation()));

            mRecyclerView.setAdapter(mSecretsAdapter);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() != null){
            ViewModelProvider vmProvider = new ViewModelProvider(this,
                    new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()));

            mSecretsViewModel = vmProvider.get(SecretsViewModel.class);
            mSecretsViewModel.getSecretsLiveData().observe(getViewLifecycleOwner(),secrets -> {
                if(mSecretsAdapter != null)
                    mSecretsAdapter.update(secrets);
            });

            mKeysViewModel = vmProvider.get(KeysViewModel.class);

            mDisposableManager.pushDisposable(mSecretsAdapter
                    .getEventStream()
                    .filter(e -> e.code == BaseRecyclerViewAdapterWithSelectableItems.Message.REMOVE_ITEM)
                    .subscribe(e -> mSecretsViewModel.deleteSecret(e.itemId)));

            mDisposableManager.pushDisposable(mSecretsAdapter
                    .getEventStream()
                    .filter(e -> e.code == BaseRecyclerViewAdapterWithSelectableItems.Message.ITEM_CLICK)
                    .subscribe(e -> onItemClick(e.itemId)));
        }
    }

    private void onItemClick(long itemId){
        mSelectedSecretId = itemId;

        mDisposableManager.pushDisposable(getQuestions(itemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(questions -> startQuiz(itemId,questions)));
    }

    private void startQuiz(long itemId,@NonNull ArrayList<String> questions){
        mDecodingQuizViewModel = new ViewModelProvider(requireActivity()).get(DECODING_QUIZ,QuizViewModel.class);

        QuizDialog quizDialog = QuizDialog.newInstance(DECODING_QUIZ,questions);
        quizDialog.show(requireFragmentManager(),DECODING_QUIZ_DIALOG_TAG);

        mDecodingQuizViewModel.getAnswers().observe(requireActivity(),answers -> {
            if(answers != null)
                onAnswersAvailable(itemId,Utils.toArrayList(answers));
        });
    }

    private void onAnswersAvailable(long itemId,@NonNull ArrayList<String> answers){
        mDecodingQuizViewModel.reset();

        Intent intent = new Intent(requireActivity(), EditSecretActivity.class);
        intent.putExtra(EditSecretActivity.EXTRA_SECRET_ID,itemId);
        intent.putStringArrayListExtra(EditSecretActivity.EXTRA_ANSWERS,answers);

        startActivity(intent);
    }

    private Single<ArrayList<String>> getQuestions(long secretId){
        return mSecretsViewModel.getSecret(secretId)
                .flatMap(secret -> mKeysViewModel.getQuestions(secret.getKeys()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mKeysViewModel = null;
        mSecretsViewModel = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mDisposableManager.dispose();
        mRecyclerView = null;
        mSecretsAdapter = null;
    }
}
