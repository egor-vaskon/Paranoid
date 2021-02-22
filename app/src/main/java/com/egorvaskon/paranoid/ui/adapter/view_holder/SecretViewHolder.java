package com.egorvaskon.paranoid.ui.adapter.view_holder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.SecretHeader;
import com.egorvaskon.paranoid.ui.adapter.BaseRecyclerViewAdapterWithSelectableItems;

public class SecretViewHolder extends BaseRecyclerViewAdapterWithSelectableItems.BaseViewHolder {

    private TextView mName;
    private ImageButton mDeleteButton;
    private CheckBox mCheckBox;

    @SuppressLint("ClickableViewAccessibility")
    public SecretViewHolder(@NonNull View itemView,
                            @NonNull BaseRecyclerViewAdapterWithSelectableItems.AdapterContext adapterContext,
                            boolean deletable,
                            boolean selectable) {
        super(itemView, adapterContext);

        itemView.setOnClickListener(view -> sendMessage(BaseRecyclerViewAdapterWithSelectableItems.Message.ITEM_CLICK));

        mName = itemView.findViewById(R.id.secret_name);
        mDeleteButton = itemView.findViewById(R.id.delete_secret);
        mCheckBox = itemView.findViewById(R.id.secret_checkbox);

        if(deletable)
            mDeleteButton.setOnClickListener(v -> remove());
        else{
            mDeleteButton.setEnabled(false);
            mDeleteButton.setVisibility(View.GONE);
        }

        if(selectable){
            mCheckBox.setOnCheckedChangeListener((btn,checked) -> {
                if(checked)
                    select();
                else
                    deselect();
            });

            mCheckBox.setEnabled(true);
            mCheckBox.setVisibility(View.VISIBLE);
        }
        else{
            mCheckBox.setEnabled(false);
            mCheckBox.setVisibility(View.GONE);
            mCheckBox.setFocusable(false);
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

    public void bind(@NonNull SecretHeader secret){
        mName.setText(secret.getName());
    }
}
