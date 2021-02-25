package com.egorvaskon.paranoid;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Entity(tableName = "secrets")
public class Secret {

    public static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    @ColumnInfo(name = "name")
    private String mName;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId = -1;

    @ColumnInfo(name = "keys")
    private List<Long> mKeys = new ArrayList<>();

    @ColumnInfo(name = "hash")
    private byte[] mHash;

    @ColumnInfo(name = "data")
    private byte[] mEncodedData = null;

    public Secret(){}

    public Secret(@NonNull String name,@NonNull List<Long> keys){
        mName = name;
        mKeys = keys;
    }

    public Secret(long id,@NonNull String name,@NonNull List<Long> keys){
        mId = id;
        mName = name;
        mKeys = keys;
    }

    public void addKey(@NonNull Long key){
        mKeys.add(key);
    }

    public void setKey(int stage,@NonNull Long key){
        mKeys.set(stage,key);
    }

    public void removeKey(int stage){
        mKeys.remove(stage);
    }

    public Completable encode(@NonNull final byte[] data,@NonNull List<String> correctAnswers){
        return Completable.fromAction(() -> {
            if(data.length < 1 || correctAnswers.size() != mKeys.size())
                throw new IllegalArgumentException("Data cannot be empty.");

            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            mHash = msgDigest.digest(data);

            byte[] buff = new byte[data.length];
            System.arraycopy(data,0,buff,0,data.length);

            for(int i = 0;i<mKeys.size();i++){
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] keyBytes = digest.digest(correctAnswers.get(i).getBytes(StandardCharsets.UTF_8));

                SecretKey sKey = new SecretKeySpec(keyBytes,0,keyBytes.length,ALGORITHM);

                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE,sKey);

                buff = cipher.doFinal(buff);
            }

            mEncodedData = buff;
        }).subscribeOn(Schedulers.io());
    }

    public Single<byte[]> decode(@NonNull List<String> answers){
        return Single.fromCallable(() -> {
            if(answers.size() != mKeys.size()){
                throw new IllegalArgumentException();
            }

            byte[] buff = new byte[mEncodedData.length];
            System.arraycopy(mEncodedData,0,buff,0,mEncodedData.length);

            for(int i = mKeys.size()-1;i>-1;i--){
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] keyBytes = digest.digest(answers.get(i).getBytes(StandardCharsets.UTF_8));

                SecretKey sKey = new SecretKeySpec(keyBytes,0,keyBytes.length,ALGORITHM);

                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE,sKey);

                buff = cipher.doFinal(buff);
            }

            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            byte[] newHash = msgDigest.digest(buff);

            if(mHash.length != newHash.length)
                throw new Exception();

            for(int i = 0;i<mHash.length;i++){
                if(mHash[i] != newHash[i])
                    throw new Exception();
            }

            return buff;
        });
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public byte[] getEncodedData() {
        return mEncodedData;
    }

    public void setEncodedData(byte[] encodedData) {
        mEncodedData = encodedData;
    }

    public byte[] getHash() {
        return mHash;
    }

    public void setHash(byte[] hash) {
        mHash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Secret secret = (Secret) o;
        return getId() == secret.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public List<Long> getKeys() {
        return mKeys;
    }

    public void setKeys(List<Long> keys) {
        mKeys = keys;
    }
}