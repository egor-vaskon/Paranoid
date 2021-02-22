package com.egorvaskon.paranoid.ui.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.egorvaskon.paranoid.Key;
import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.ui.viewmodel.KeysViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class EditKeyDialogFragment extends DialogFragment {

    private static final String ARG_ID = "arg_id";
    private static final String ARG_NAME = "arg_name";
    private static final String ARG_QUESTION = "arg_question";

    private long mKeyId;
    private boolean mCreate = true;

    private boolean mHasUserEditedQuestion = false;
    private boolean mIgnoreQuestionEditedEvent = false;

    //If you want to edit existing key.
    @NonNull
    public static EditKeyDialogFragment newInstance(long id,@NonNull String name,@NonNull String question){
        Bundle args = new Bundle();
        args.putLong(ARG_ID,id);
        args.putString(ARG_NAME,name);
        args.putString(ARG_QUESTION,question);

        EditKeyDialogFragment fragment = new EditKeyDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    //Create new key.
    public EditKeyDialogFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments() != null){
            if(getArguments().containsKey(ARG_ID)){
                mCreate = false;
                mKeyId = getArguments().getLong(ARG_ID);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_question_dialog,null,false);

        TextInputEditText name = view.findViewById(R.id.new_key_name);
        TextInputEditText question = view.findViewById(R.id.new_question);

        int titleRes = R.string.create_question;
        if(!mCreate)
            titleRes = R.string.edit_question;

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(titleRes)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(android.R.string.ok,(dInterface,i) -> {
                    if(name.getText() != null && question.getText() != null){
                        ViewModelProvider vmProvider = new ViewModelProvider(requireActivity(),
                                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()));

                        Key key = new Key(name.getText().toString(),question.getText().toString());
                        if(!mCreate)
                            key.setId(mKeyId);

                        vmProvider.get(KeysViewModel.class).addKey(key);
                    }
                })
                .setView(view)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false));

        boolean[] flags = new boolean[2];
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(getDialog() != null && getDialog().isShowing()){
                    flags[0] = !editable.toString().isEmpty();

                    if(flags[0] && (question.getText() == null
                            || question.getText().toString().isEmpty()
                            || !mHasUserEditedQuestion)){
                        flags[1] = true;
                        mIgnoreQuestionEditedEvent = true;
                        question.setText(editable);
                    }

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(flags[0] && flags[1]);
                }
            }
        });

        question.setOnEditorActionListener((view1, actionId, event) -> {
            mHasUserEditedQuestion = true;
            return false;
        });

        question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(getDialog() != null && getDialog().isShowing()){
                    flags[1] = !editable.toString().isEmpty();

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(flags[0] && flags[1]);

                    if(!mIgnoreQuestionEditedEvent)
                        mHasUserEditedQuestion = true;

                    mIgnoreQuestionEditedEvent = false;
                }
            }
        });

        if(!mCreate){
            name.setText(getArguments().getString(ARG_NAME,""));
            question.setText(getArguments().getString(ARG_QUESTION,""));
        }

        return dialog;
    }
}
