package com.amlogic.cvdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.gridlayout.widget.GridLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.amlogic.cvdemo.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private GridLayout gridLayout;
    private HashMap<String, String> mSupportedModels = new HashMap();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getBaseContext();
        gridLayout = findViewById(R.id.gridLayout);
        initSupportedList();
        // 可以在这里定义按钮的数量

        createModelsList(mSupportedModels);
    }

    private void initSupportedList() {
        mSupportedModels.put(mContext.getString(R.string.semantic_segmentation), AMLSemanticSegmentationActivity.class.getName());
        mSupportedModels.put(mContext.getString(R.string.image_edit), AMLImageEditActivity.class.getName());
        mSupportedModels.put(mContext.getString(R.string.super_resolution), AMLSuperResolutionActivity.class.getName());
    }

    private void createModelsList(HashMap<String, String> map) {

        Set<String> keySet = map.keySet();
        for (String key : keySet)  {
            Button button = new Button(this);
            button.setText(key);
            button.setLayoutParams(new GridLayout.LayoutParams());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 获取目标Activity的Class对象
                    try {
                        Class<?> targetActivity = Class.forName(map.get(key));
                        Log.d(TAG, "main activity onclick target = " + map.get(key));
                        Intent intent = new Intent(MainActivity.this, targetActivity);
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            // 创建 LayoutParams
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            params.rowSpec = GridLayout.spec(0); // 设置行
//            params.columnSpec = GridLayout.spec(2); // 设置列
            params.setGravity(Gravity.CENTER); // 设置居中

            // 将 LayoutParams 应用到按钮
            button.setLayoutParams(params);
            button.setGravity(Gravity.CENTER);

            gridLayout.addView(button);
            gridLayout.setForegroundGravity(Gravity.CENTER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    //获得Canny边缘
    public native void getEdge(Object bitmap);
}