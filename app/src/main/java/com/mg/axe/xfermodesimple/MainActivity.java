package com.mg.axe.xfermodesimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {

    private CustomHeadView one, two, three;

    private ScrollView slHead, slGGK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);

        slHead = findViewById(R.id.slHead);
        slGGK = findViewById(R.id.slGGK);

        one.drawHead(R.drawable.head_s, R.drawable.head_d);
        two.drawHead(R.drawable.wbx, R.drawable.head_d);
        three.drawHead(R.drawable.love, R.drawable.head_d);
    }


    public void showHead(View view) {
        slHead.setVisibility(View.VISIBLE);
        slGGK.setVisibility(View.GONE);
    }

    public void showGGK(View view) {
        slHead.setVisibility(View.GONE);
        slGGK.setVisibility(View.VISIBLE);
    }
}
