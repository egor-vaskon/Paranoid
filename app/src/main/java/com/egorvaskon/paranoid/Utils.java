package com.egorvaskon.paranoid;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Utils {

    private static final Random sRandom = new Random();

    private Utils() {}

    //[min;max)
    public static int random(int min,int max){
        return sRandom.nextInt(max-min)+min;
    }

    public static void showDialog(@NonNull FragmentManager fm, @NonNull DialogFragment fragment,@NonNull String tag){
        fragment.show(fm,tag);
    }

    @NonNull
    public static <T> ArrayList<T> toArrayList(@NonNull List<T> list){
        if(list instanceof ArrayList)
            return (ArrayList<T>)list;
        else
            return new ArrayList<>(list);
    }

}
