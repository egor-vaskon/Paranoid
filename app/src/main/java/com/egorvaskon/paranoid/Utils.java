package com.egorvaskon.paranoid;

import android.app.Activity;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    public static void addOrReplaceFragment(@NonNull FragmentActivity activity,@NonNull Fragment fragment, @IdRes int container){
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if(fm.findFragmentById(container) != null)
            transaction.replace(container,fragment);
        else
            transaction.add(container,fragment);

        transaction.commit();
    }

    public static boolean equals(CharSequence s1,CharSequence s2){
        return ((s1 == null && s2 == null) || (s1 != null && s2 != null && s1.toString().equals(s1.toString())));
    }

}
