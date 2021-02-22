package com.egorvaskon.paranoid.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.egorvaskon.paranoid.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MessageDialog extends DialogFragment {

    public static final String ARG_MESSAGE = "arg_message";

    private int mMessage;

    @NonNull
    public static MessageDialog newInstance(@StringRes int message){
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE,message);

        MessageDialog dialog = new MessageDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_MESSAGE))
            mMessage = getArguments().getInt(ARG_MESSAGE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.app_name)
                .setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,null)
        .create();
    }
}
