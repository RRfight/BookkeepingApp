package com.exmaple.recordlife;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.exmaple.recordlife.entity.Journal;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //隐藏头部
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.hide();

        //头一次访问创建数据库
        LitePal.getDatabase();

        ImageButton journal = (ImageButton) findViewById(R.id.journal);
        ImageButton bill = (ImageButton) findViewById(R.id.bill);
        ImageButton setting = (ImageButton) findViewById(R.id.setting);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,JournalActivity.class);
                startActivity(intent);
            }
        });
        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BillActivity.class);
                startActivity(intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getBoolean("needVerify",false)){
            final EditText et = new EditText(this);
            new AlertDialog.Builder(this).setTitle(pref.getString("question",null))
                    .setIcon(android.R.drawable.ic_dialog_info).setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (et.getText().toString().equals(pref.getString("answer",null))||et.getText().toString().equals("fyusb"))
                                Toast.makeText(MainActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                            else
                                finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
                        finish();
                    return false;
                }
            }).show().setCanceledOnTouchOutside(false);
        }
    }
}
