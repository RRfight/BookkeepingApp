package com.exmaple.recordlife;

import android.app.DatePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.exmaple.recordlife.entity.Journal;
import com.exmaple.recordlife.entity.Record;
import com.exmaple.recordlife.entity.RecordType;
import com.exmaple.recordlife.myUtil.AppUtil;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddRecordActivity extends AppCompatActivity {
    Spinner spinner;
    EditText cost;
    Button date;
    EditText desc;
    Button submit;

    Date selectDate = AppUtil.today();
    List<RecordType> all = new ArrayList<RecordType>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        //隐藏头部
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.hide();

        spinner = (Spinner) findViewById(R.id.spinner);
        cost = (EditText) findViewById(R.id.cost);
        date = (Button) findViewById(R.id.date);
        desc = (EditText) findViewById(R.id.desc);
        submit = (Button) findViewById(R.id.submit);
        final Record record = (Record)getIntent().getSerializableExtra("record");

        //设置spinner
        all = DataSupport.findAll(RecordType.class);
        String[] types = new String[all.size()];
        for (int i = 0;i<types.length;i++){
            types[i] = all.get(i).getType();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,types);
        spinner.setAdapter(adapter);
        if (getIntent().getIntExtra("type",-1)!=-1);
            spinner.setSelection(getIntent().getIntExtra("type",-1));
        //设置选择按钮
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        date.setText(sdf.format(selectDate));
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(AddRecordActivity.this,new DatePickerDialog.OnDateSetListener() {      //  日期选择对话框
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //  这个方法是得到选择后的 年，月，日，分别对应着三个参数 — year、month、dayOfMonth
                        c.set(year,month,dayOfMonth);
                        selectDate = c.getTime();
                        date.setText(sdf.format(selectDate));
                    }
                },year,month,day).show();   //  弹出日历对话框时，默认显示 年，月，日
            }
        });

        if (record!=null){
            cost.setText(String.valueOf(record.getCost()));
            desc.setText(record.getDesc());
            selectDate = record.getDate();
            date.setText(sdf.format(selectDate));
            int position;
            for (position=0;position<types.length;position++){
                if (record.getTypeId()==all.get(position).getId())
                    break;
            }
            spinner.setSelection(position,true);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float costNum=0;
                try {
                    costNum = Float.parseFloat(cost.getText().toString());
                    if (costNum == 0){
                        Toast.makeText(AddRecordActivity.this, "金额不能为0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }catch (Exception e){
                    Toast.makeText(AddRecordActivity.this, "金额要为数字哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (record == null){
                    Record r = new Record();
                    r.setDate(selectDate);
                    r.setTypeId(all.get(spinner.getSelectedItemPosition()).getId());
                    r.setCost(costNum);
                    r.setDesc(desc.getText().toString());
                    r.save();
                    BillActivity.recordAdapter.addOneRecord(r);
                    Toast.makeText(AddRecordActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                }else{
                    record.setDate(selectDate);
                    record.setTypeId(all.get(spinner.getSelectedItemPosition()).getId());
                    record.setCost(costNum);
                    record.setDesc(desc.getText().toString());
                    record.updateAll("id=?",String.valueOf(record.getId()));
                    BillActivity.recordAdapter.changeOneRecord(record);
                    Toast.makeText(AddRecordActivity.this, "修改完成", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

}
