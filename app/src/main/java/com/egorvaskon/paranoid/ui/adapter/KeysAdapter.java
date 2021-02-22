package com.egorvaskon.paranoid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import com.egorvaskon.paranoid.Key;
import com.egorvaskon.paranoid.R;
import com.egorvaskon.paranoid.ui.adapter.view_holder.KeyViewHolder;

import java.util.ArrayList;
import java.util.List;

public class KeysAdapter extends BaseRecyclerViewAdapterWithSelectableItems<KeyViewHolder>{

    private List<Key> mKeys = new ArrayList<>();

    private boolean mDeletionEnabled;
    private boolean mSelectionEnabled;

    public KeysAdapter(@NonNull Context context,boolean deletionEnabled,boolean selectionEnabled){
        super(context);

        mDeletionEnabled = deletionEnabled;
        mSelectionEnabled = selectionEnabled;
    }

    @Override
    public int getItemCount() {
        return mKeys.size();
    }

    @Override
    public long getItemId(int position) {
        return mKeys.get(position).getId();
    }

    @Override
    public int getItemPosition(long id) {
        return getRecyclerView().findViewHolderForItemId(id).getAdapterPosition();
    }

    public void update(@NonNull List<Key> keys){
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new Key.DiffCallbacks(mKeys,keys),false);

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
        mKeys = keys;
    }

    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.key,parent,false),
                getAdapterContext(),
                mSelectionEnabled,mDeletionEnabled);
    }

    public List<Key> getKeys() {
        return mKeys;
    }

    @Override
    public void bind(@NonNull KeyViewHolder holder, int position, boolean isSelected) {
        holder.bind(mKeys.get(position));
    }

}
