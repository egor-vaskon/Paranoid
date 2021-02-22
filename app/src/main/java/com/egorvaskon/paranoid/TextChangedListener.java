package com.egorvaskon.paranoid;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

public abstract class TextChangedListener implements TextWatcher {

    public abstract void onTextChanged(@NonNull CharSequence text);

    @Override
    public final void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public final void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public final void afterTextChanged(Editable editable) {
        onTextChanged(editable);
    }
}
