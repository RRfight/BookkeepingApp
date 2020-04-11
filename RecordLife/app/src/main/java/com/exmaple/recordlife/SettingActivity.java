package com.exmaple.recordlife;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText question;
    private EditText answer;
    private Switch onOff;
    private Button saveSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //隐藏头部
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.hide();

        //获得控件
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        question = (EditText) findViewById(R.id.question);
        answer = (EditText) findViewById(R.id.answer);
        onOff = (Switch) findViewById(R.id.verify_switch);
        saveSetting = (Button) findViewById(R.id.save_setting);
        //获得之前保存的设置
        onOff.setChecked(pref.getBoolean("needVerify",false));
        question.setText(pref.getString("question",null));
        answer.setText(pref.getString("answer",null));

        //保存配置
        saveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = pref.edit();
                editor.putString("question",question.getText().toString());
                editor.putString("answer",answer.getText().toString());
                editor.putBoolean("needVerify",onOff.isChecked());
                editor.apply();
                Toast.makeText(SettingActivity.this, "保存配置成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
