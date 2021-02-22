package com.egorvaskon.paranoid.room;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public final class GsonTypeConverters {

    private static Gson sGson = new Gson();

    @TypeConverter
    public static String twoDimensionalLongListToJson(@NonNull List<List<Long>> list){
        return sGson.toJson(list);
    }

    @TypeConverter
    public static List<List<Long>> twoDimensionalLongListFromJson(@NonNull String json){
        Type type = new TypeToken<List<List<Long>>>(){}.getType();
        return sGson.fromJson(json,type);
    }

    @TypeConverter
    public static String twoDimensionalByteArrayListToJson(@NonNull List<List<byte[]>> list){
        return sGson.toJson(list);
    }

    @TypeConverter
    public static List<List<byte[]>> twoDimensionalByteArrayListFromJson(@NonNull String json){
        Type type = new TypeToken<List<List<byte[]>>>(){}.getType();
        return sGson.fromJson(json,type);
    }

    @TypeConverter
    public static String longListToJson(@NonNull List<Long> list){
        return sGson.toJson(list);
    }

    @TypeConverter
    public static List<Long> longListFromJson(@NonNull String json){
        Type type = new TypeToken<List<Long>>(){}.getType();
        return sGson.fromJson(json,type);
    }

}
