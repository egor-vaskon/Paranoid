package com.egorvaskon.paranoid.ui.adapter.view_holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.egorvaskon.paranoid.Key;
import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.ui.adapter.BaseRecyclerViewAdapterWithSelectableItems;

public class KeyViewHolder extends BaseRecyclerViewAdapterWithSelectableItems.BaseViewHolder {

    private final TextView mName;
    private final TextView mQuestion;
    private final CheckBox mCheckBox;
    private final ImageButton mDeleteButton;

    public KeyViewHolder(@NonNull View itemView, @NonNull BaseRecyclerViewAdapterWithSelectableItems.AdapterContext adapterContext,
                         boolean selectionEnabled,
                         boolean deletionEnabled){
        super(itemView,adapterContext);

        itemView.setOnClickListener(view -> sendMessage(BaseRecyclerViewAdapterWithSelectableItems.Message.ITEM_CLICK));

        mName = itemView.findViewById(R.id.key_name);
        mQuestion = itemView.findViewById(R.id.key_question);

        mDeleteButton = itemView.findViewById(R.id.delete_key);
        if(deletionEnabled){
            mDeleteButton.setOnClickListener(v -> remove());
        }
        else {
            mDeleteButton.setEnabled(false);
            mDeleteButton.setVisibility(View.GONE);
        }

        mCheckBox = itemView.findViewById(R.id.key_checkbox);
        if(selectionEnabled){
            mCheckBox.setVisibility(View.VISIBLE);
            mCheckBox.setOnCheckedChangeListener((btn,checked) -> {
                if(checked)
                    select();
                else
                    deselect();
            });
        }
        else{
            mCheckBox.setVisibility(View.GONE);
            mCheckBox.setEnabled(false);
        }
    }

    @Override
    protected void onSelected() {
        super.onSelected();

        mCheckBox.setChecked(true);
    }

    @Override
    protected void onDeselected() {
        super.onDeselected();

        mCheckBox.setChecked(false);
    }

    public void bind(@NonNull Key key){
        mName.setText(key.getName());
        mQuestion.setText(key.getName().equals(key.getQuestion()) ? "" : key.getQuestion());
    }

}
