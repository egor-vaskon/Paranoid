package com.egorvaskon.paranoid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.SecretHeader;
import com.egorvaskon.paranoid.ui.adapter.view_holder.SecretViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SecretsAdapter extends BaseRecyclerViewAdapterWithSelectableItems<SecretViewHolder> {

    private List<SecretHeader> mSecrets = new ArrayList<>();

    private boolean mDeletionEnabled;
    private boolean mSelectionEnabled;

    public SecretsAdapter(@NonNull Context context,boolean deletionEnabled,boolean selectionEnabled){
        super(context);

        mDeletionEnabled = deletionEnabled;
        mSelectionEnabled = selectionEnabled;
    }

    @Override
    public int getItemCount() {
        return mSecrets.size();
    }

    @Override
    public long getItemId(int position) {
        return mSecrets.get(position).getId();
    }

    @Override
    public int getItemPosition(long id) {
        return getRecyclerView().findViewHolderForItemId(id).getAdapterPosition();
    }

    @Override
    public void bind(@NonNull SecretViewHolder holder,int position,boolean isSelected) {
        holder.bind(mSecrets.get(position));
    }

    @NonNull
    @Override
    public SecretViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SecretViewHolder(
                LayoutInflater.from(getContext()).inflate(R.layout.secret,parent,false),
                getAdapterContext(),
                mDeletionEnabled,
                mSelectionEnabled);
    }

    public List<SecretHeader> getSecrets() {
        return mSecrets;
    }

    public void update(@NonNull List<SecretHeader> secrets){
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new SecretHeader.DiffCallbacks(mSecrets,secrets),false);

        diff.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {}

            @Override
            public void onRemoved(int position, int count) {
                for(int i = 0;i<count;i++){
                    deselect(getItemId(position));
                }
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) { }

            @Override
            public void onChanged(int position, int count, @Nullable Object payload) { }
        });

        diff.dispatchUpdatesTo(this);
        mSecrets = secrets;
    }


}
