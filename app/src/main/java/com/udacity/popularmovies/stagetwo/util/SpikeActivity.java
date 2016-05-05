package com.udacity.popularmovies.stagetwo.util;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.view.BaseActivity;
import com.udacity.popularmovies.stagetwo.view.MovieGalleryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kunaljaggi on 4/30/16.
 */
public class SpikeActivity extends BaseActivity {

    private ListView lv1;
    private ListView lv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_spike);

        lv1 = (ListView) findViewById(R.id.listView1);
        lv2 = (ListView) findViewById(R.id.listView2);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).
        List<String> your_array_list = new ArrayList<String>();
        your_array_list.add("foo");
        your_array_list.add("bar");
        your_array_list.add("fido");
        your_array_list.add("badsdar");
        your_array_list.add("fadsoo");
        your_array_list.add("adfs");
        your_array_list.add("sfgdfg");
        your_array_list.add("wewrwerw");
        your_array_list.add("xxxxx");
        your_array_list.add("ddddd");
        your_array_list.add("ccccc");
        your_array_list.add("bbbbb");
        your_array_list.add("aaaaaa");
        your_array_list.add("qqqqqq");
        your_array_list.add("xxxxeeeeeeex");
        your_array_list.add("rrrr");

        List<String> your_array_list2 = new ArrayList<String>();
        your_array_list2.add("One");
        your_array_list2.add("Two");
        your_array_list2.add("Three");
        your_array_list2.add("Four");
        your_array_list2.add("Five");
        your_array_list2.add("Six");
        your_array_list2.add("Seven");
        your_array_list2.add("8");
        your_array_list2.add("9");
        your_array_list2.add("10");
        your_array_list2.add("11");
        your_array_list2.add("12");
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list );

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list );

lv1.setAdapter(arrayAdapter1);
        lv2.setAdapter(arrayAdapter2);

        lv1.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        lv2.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        Common.setListViewHeightBasedOnChildren(lv1);
        Common.setListViewHeightBasedOnChildren(lv2);

    }
}
