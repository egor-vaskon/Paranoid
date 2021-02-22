package com.egorvaskon.paranoid;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.TreeSet;

public class DecodedSecret implements Parcelable {

    private String mName = null;
    private String mSecret = null;
    private TreeSet<Long> mKeys = null;

    public DecodedSecret(){

    }

    public DecodedSecret(String name, String secret, TreeSet<Long> keys) {
        mName = name;
        mSecret = secret;
        mKeys = keys;
    }

    public DecodedSecret(String name, String secret, List<Long> keys) {
        mName = name;
        mSecret = secret;
        mKeys = new TreeSet<>(keys);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public TreeSet<Long> getKeys() {
        return mKeys;
    }

    public void setKeys(TreeSet<Long> keys) {
        mKeys = keys;
    }

    //Parcelable

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mSecret);
        dest.writeInt(mKeys.size());
        for(Long key : mKeys){
            dest.writeLong(key);
        }
    }

    protected DecodedSecret(Parcel in) {
        mName = in.readString();
        mSecret = in.readString();
        int keyCount = in.readInt();
        mKeys = new TreeSet<>();
        for(int i = 0;i<keyCount;i++){
            mKeys.add(in.readLong());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DecodedSecret> CREATOR = new Creator<DecodedSecret>() {
        @Override
        public DecodedSecret createFromParcel(Parcel in) {
            return new DecodedSecret(in);
        }

        @Override
        public DecodedSecret[] newArray(int size) {
            return new DecodedSecret[size];
        }
    };



}
