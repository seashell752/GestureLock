package com.gusturelock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gusturelock.LockPatternView.Cell;
import com.gusturelock.LockPatternView.DisplayMode;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*
 * Author: Ruils 心怀产品梦的安卓码农 
 * Blog: http://blog.csdn.net/ruils
 * QQ: 5452781
 * Email: 5452781@qq.com
 */

public class LockSetupActivity extends Activity implements
        LockPatternView.OnPatternListener, OnClickListener {

    private static final String TAG = "LockSetupActivity";
    private LockPatternView lockPatternView;
    private Button leftButton;
    private Button rightButton;

    private static final int STEP_1 = 1; // 开始
    private static final int STEP_2 = 2; // 第一次设置手势完成
    private static final int STEP_3 = 3; // 按下继续按钮
    private static final int STEP_4 = 4; // 第二次设置手势完成
    // private static final int SETP_5 = 4; // 按确认按钮

    private int step;

    private List<Cell> choosePattern;

    private boolean confirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setup);
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        leftButton = (Button) findViewById(R.id.left_btn);
        rightButton = (Button) findViewById(R.id.right_btn);

        step = STEP_1;
        updateView();
    }

    private void updateView() {
        switch (step) {
        case STEP_1:
            leftButton.setText(R.string.cancel);
            rightButton.setText("");
            rightButton.setEnabled(false);
            choosePattern = null;
            confirm = false;
            lockPatternView.clearPattern();
            lockPatternView.enableInput();
            break;
        case STEP_2:
            leftButton.setText(R.string.try_again);
            rightButton.setText(R.string.goon);
            rightButton.setEnabled(true);
            lockPatternView.disableInput();
            break;
        case STEP_3:
            leftButton.setText(R.string.cancel);
            rightButton.setText("");
            rightButton.setEnabled(false);
            lockPatternView.clearPattern();
            lockPatternView.enableInput();
            break;
        case STEP_4:
            leftButton.setText(R.string.cancel);
            if (confirm) {
                rightButton.setText(R.string.confirm);
                rightButton.setEnabled(true);
                lockPatternView.disableInput();
            } else {
                rightButton.setText("");
                lockPatternView.setDisplayMode(DisplayMode.Wrong);
                lockPatternView.enableInput();
                rightButton.setEnabled(false);
            }

            break;

        default:
            break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.left_btn:
            if (step == STEP_1 || step == STEP_3 || step == STEP_4) {
                finish();
            } else if (step == STEP_2) {
                step = STEP_1;
                updateView();
            }
            break;

        case R.id.right_btn:
            if (step == STEP_2) {
                step = STEP_3;
                updateView();
            } else if (step == STEP_4) {

                SharedPreferences preferences = getSharedPreferences(
                        MainActivity.LOCK, MODE_PRIVATE);
                preferences
                        .edit()
                        .putString(MainActivity.LOCK_KEY,
                                LockPatternView.patternToString(choosePattern))
                        .commit();

                Intent intent = new Intent(this, LockActivity.class);
                startActivity(intent);
                finish();
            }

            break;

        default:
            break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPatternStart() {
        Log.d(TAG, "onPatternStart");
    }

    @Override
    public void onPatternCleared() {
        Log.d(TAG, "onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) {
        Log.d(TAG, "onPatternCellAdded");
    }

    @Override
    public void onPatternDetected(List<Cell> pattern) {
        Log.d(TAG, "onPatternDetected");

        if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
            Toast.makeText(this,
                    R.string.lockpattern_recording_incorrect_too_short,
                    Toast.LENGTH_LONG).show();
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            return;
        }

        if (choosePattern == null) {
            choosePattern = new ArrayList<Cell>(pattern);
 //           Log.d(TAG, "choosePattern = "+choosePattern.toString());
//            Log.d(TAG, "choosePattern.size() = "+choosePattern.size());
            Log.d(TAG, "choosePattern = "+Arrays.toString(choosePattern.toArray()));
         
            step = STEP_2;
            updateView();
            return;
        }
//[(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]
//[(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]    
        
        Log.d(TAG, "choosePattern = "+Arrays.toString(choosePattern.toArray()));
        Log.d(TAG, "pattern = "+Arrays.toString(pattern.toArray()));
        
        if (choosePattern.equals(pattern)) {
//            Log.d(TAG, "pattern = "+pattern.toString());
//            Log.d(TAG, "pattern.size() = "+pattern.size());
            Log.d(TAG, "pattern = "+Arrays.toString(pattern.toArray()));
           
            confirm = true;
        } else {
            confirm = false;
        }
      
        step = STEP_4;
        updateView();

    }

}
