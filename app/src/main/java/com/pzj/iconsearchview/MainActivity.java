package com.pzj.iconsearchview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.pzj.iconsearchview.widget.IconSearchView;

public class MainActivity extends AppCompatActivity {

    private IconSearchView mSearchView;

    private int mIconTagNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        this.mSearchView = (IconSearchView) findViewById(R.id.search_view);
        this.mSearchView.setOnIconRemoveListener(new IconSearchView.OnIconRemoveListener() {
            @Override
            public void onIconRemoved(View v, String tag) {
                Toast.makeText(MainActivity.this, "移除了：" + tag, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 添加一个图像
     *
     * @param view
     */
    public void addView(View view) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher_round);
        this.mSearchView.addIconView(imageView, mIconTagNum++ + "");
    }
}
