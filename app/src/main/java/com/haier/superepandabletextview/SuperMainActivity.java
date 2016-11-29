package com.haier.superepandabletextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Harry.Kong on 2016/11/26.
 */
public class SuperMainActivity extends AppCompatActivity {

    RecyclerView main_rv;
    MainAdapter adapter;

    ArrayList<DataBean> models;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_epandable_text_view);
        models=new ArrayList<>();
        String[] arrays=getResources().getStringArray(R.array.news);
        for (String array : arrays) {
            DataBean bean=new DataBean();
            bean.setText(array);
            models.add(bean);
        }
        main_rv= (RecyclerView) findViewById(R.id.main_rv);
        main_rv.setHasFixedSize(true);
        main_rv.setLayoutManager(new LinearLayoutManager(this));
        adapter=new MainAdapter(this, models);
        main_rv.setAdapter(adapter);
    }
}
