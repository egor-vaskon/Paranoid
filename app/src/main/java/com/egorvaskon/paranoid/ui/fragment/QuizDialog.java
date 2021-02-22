package com.egorvaskon.paranoid.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.TextChangedListener;
import com.egorvaskon.paranoid.ui.viewmodel.EditSecretViewModel;
import com.egorvaskon.paranoid.ui.viewmodel.QuizViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class QuizDialog extends DialogFragment {

    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_QUIZ_VIEW_MODEL_NAME = "quiz_view_model_id";

    private ArrayList<String> mAnswers = new ArrayList<>();
    private ArrayList<String> mQuestions;

    private String mQuizViewModelName = null;

    private int mCurrentQuestion;

    private EditSecretViewModel mSharedViewModel;
    private QuizViewModel mQuizViewModel;

    @NonNull
    public static QuizDialog newInstance(@NonNull String viewModelName){
        Bundle args = new Bundle();
        args.putString(ARG_QUIZ_VIEW_MODEL_NAME,viewModelName);

        QuizDialog dialog = new QuizDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    public static QuizDialog newInstance(@NonNull String viewModelName,@NonNull ArrayList<String> questions){
        Bundle args = new Bundle();
        args.putString(ARG_QUIZ_VIEW_MODEL_NAME,viewModelName);
        args.putStringArrayList(ARG_QUESTIONS,questions);

        QuizDialog dialog = new QuizDialog();
        dialog.setArguments(args);

        return dialog;
    }

    public QuizDialog(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments() != null && getArguments().containsKey(ARG_QUESTIONS))
            mQuestions = getArguments().getStringArrayList(ARG_QUESTIONS);

        if(getArguments() != null && getArguments().containsKey(ARG_QUIZ_VIEW_MODEL_NAME))
            mQuizViewModelName = getArguments().getString(ARG_QUIZ_VIEW_MODEL_NAME);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Activity activity = requireActivity();

        TextInputLayout rootLayout = (TextInputLayout) LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.quiz_dialog,null,false);

        TextInputEditText answerView = rootLayout.findViewById(R.id.user_answer);

        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setTitle(mQuestions.get(0))
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel,(dI,c) -> dI.cancel())
                .setPositiveButton(R.string.next,null)
                .setView(rootLayout)
                .create();

        dialog.setOnShowListener(dI -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> onQuestionAnswered(rootLayout,answerView)));

        answerView.addTextChangedListener(new TextChangedListener() {
            @Override
            public void onTextChanged(@NonNull CharSequence text) {
                if(mCurrentQuestion >= 0)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!text.toString().isEmpty());
            }
        });

        return dialog;
    }

    private void onQuestionAnswered(@NonNull TextInputLayout layout,@NonNull TextInputEditText answerView){
        AlertDialog aDialog = (AlertDialog)getDialog();

        if(aDialog != null && getActivity() != null){
            if(mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size()
                    && answerView.getText() != null && !answerView.getText().toString().isEmpty()){
                mAnswers.add(answerView.getText().toString());
            }

            if(mCurrentQuestion+1 >= mQuestions.size()-1){
                aDialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.finish);
            }

            if(mCurrentQuestion == mQuestions.size()-1){
                mQuizViewModel.setAnswers(mAnswers);
                aDialog.dismiss();
            }

            if(mCurrentQuestion < mQuestions.size()-1){
                aDialog.setTitle(mQuestions.get(mCurrentQuestion+1));
                answerView.setText(null);
            }

            mCurrentQuestion++;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCurrentQuestion = 0;
        mAnswers.clear();

        mSharedViewModel = new ViewModelProvider(requireActivity())
                .get(EditSecretViewModel.class);

        mQuizViewModel = new ViewModelProvider(requireActivity())
                .get(mQuizViewModelName,QuizViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mSharedViewModel = null;
    }
}
