package com.egorvaskon.paranoid;


import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.Objects;

public class SecretHeader {

    @ColumnInfo(name = "name")
    private String mName;

    @PrimaryKey
    @ColumnInfo(name = "id")
    private long mId;

    //Won't be used.(only by room)
    @Ignore
    public SecretHeader() {}

    //Won't be used.(only by room)
    public SecretHeader(long id,String name) {
        mName = name;
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        if(name == null)
            mId = 0;
        else mId = name.hashCode();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecretHeader that = (SecretHeader) o;
        return mId == that.getId() && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId());
    }

    public static class DiffCallbacks extends DiffUtil.Callback {

        private List<SecretHeader> mOldData;
        private List<SecretHeader> mNewData;

        public DiffCallbacks(List<SecretHeader> oldData, List<SecretHeader> newData) {
            mOldData = oldData;
            mNewData = newData;
        }

        @Override
        public int getOldListSize() {
            return mOldData.size();
        }

        @Override
        public int getNewListSize() {
            return mNewData.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldData.get(oldItemPosition).getId() == mNewData.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return (mOldData.get(oldItemPosition).getId() == mNewData.get(newItemPosition).getId())
                    && (mOldData.get(oldItemPosition).getName().equals(mNewData.get(newItemPosition).getName()));
        }
    }
}
