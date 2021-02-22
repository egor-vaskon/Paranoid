package com.egorvaskon.paranoid;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.Objects;

@Entity(tableName = "keys")
public class Key {

    private static final String TAG = "Key";

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "question")
    private String mQuestion;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "refCount")
    private int mRefCount;

    public Key() {}

    @Ignore
    public Key(String name) {
        mName = name;
        //mId = mName.hashCode();
        mQuestion = "";
    }

    @Ignore
    public Key(String name, @NonNull String question) {
        mName = name;
        mQuestion = question;
        //mId = mName.hashCode();
    }

    @NonNull
    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(@NonNull String question) {
        mQuestion = question;
    }

    public String getName() {
        return mName;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getRefCount() {
        return mRefCount;
    }

    public void setRefCount(int refCount) {
        mRefCount = refCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return getName().equals(key.getName()) && getQuestion().equals(key.getQuestion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(),getQuestion());
    }

    public static class DiffCallbacks extends DiffUtil.Callback {

        private List<Key> mOldData;
        private List<Key> mNewData;

        public DiffCallbacks(List<Key> oldData, List<Key> newData) {
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
            return (mOldData.get(oldItemPosition).equals(mNewData.get(newItemPosition)));
        }
    }

}
