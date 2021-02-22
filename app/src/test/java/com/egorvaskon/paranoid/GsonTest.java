package com.egorvaskon.paranoid;

import com.google.gson.Gson;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class GsonTest {

    @Test
    public void gson_test(){
        Gson gson = new Gson();

        List<String> list = new ArrayList<>();
        list.add("hhh");
        list.add("jjj");

        String json = gson.toJson(list);

        int i = 1;
    }

}
