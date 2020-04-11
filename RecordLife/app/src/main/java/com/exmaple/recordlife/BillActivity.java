package com.exmaple.recordlife;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.exmaple.recordlife.adapter.RecordAdapter;
import com.exmaple.recordlife.adapter.RecordTypeAdapter;
import com.exmaple.recordlife.entity.Record;
import com.exmaple.recordlife.entity.RecordType;
import com.exmaple.recordlife.listener.OnRecyclerViewClickListener;
import com.exmaple.recordlife.myUtil.AppUtil;

import org.litepal.crud.ClusterQuery;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BillActivity extends AppCompatActivity {
    List<RecordType> types = new ArrayList<RecordType>();
    List<Record> records = DataSupport.findAll(Record.class);
    Button allRecord;
    public static int typePosition=-1;
    RecordType currentType;
    int recordPosition=-1;
    Record record;
    ImageButton addType;
    ImageButton addRecord;
    ImageButton orderByCost;
    ImageButton orderByDate;
    public static TextView statistics;
    RecyclerView typeView;
    RecyclerView recordView;
    RecordTypeAdapter typeAdapter;
    public static RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        //隐藏头部
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.hide();

        addType = (ImageButton) findViewById(R.id.add_item);
        allRecord = (Button) findViewById(R.id.all_reocrds);
        addRecord = (ImageButton) findViewById(R.id.add_record);
        orderByCost = (ImageButton) findViewById(R.id.order_by_cost);
        orderByDate = (ImageButton) findViewById(R.id.order_by_date);
        statistics = (TextView) findViewById(R.id.statistics);
        typeView = (RecyclerView) findViewById(R.id.type_item);
        recordView = (RecyclerView) findViewById(R.id.record_item);
        //获得所有类型,放在recycleview中
        types = DataSupport.findAll(RecordType.class);
        typeAdapter = new RecordTypeAdapter(types);
        LinearLayoutManager typeManager = new LinearLayoutManager(this);
        typeManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typeView.setLayoutManager(typeManager);
        typeView.setAdapter(typeAdapter);
        //recordview
        recordAdapter = new RecordAdapter(records);
        LinearLayoutManager recordManager = new LinearLayoutManager(this);
        recordManager.setOrientation(LinearLayoutManager.VERTICAL);
        recordView.setLayoutManager(recordManager);
        recordView.setAdapter(recordAdapter);
        //更新统计
        AppUtil.doStatisticsMsg(records);

        //给typeview添加监听器
        typeAdapter.setItemClickListener(new OnRecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View view) {
                typePosition = typeView.getChildAdapterPosition(view);
                currentType = types.get(typePosition);
                records = DataSupport.where("typeid=?",String.valueOf(currentType.getId())).find(Record.class);
                recordAdapter.setmRecord(records);
                recordAdapter.notifyDataSetChanged();
                //更新统计
                AppUtil.doStatisticsMsg(records);
            }
            @Override
            public void onItemLongClickListener(View view) {
                typePosition = typeView.getChildAdapterPosition(view);
                currentType = types.get(typePosition);
                AlertDialog.Builder dialog = new AlertDialog.Builder(BillActivity.this);
                dialog.setTitle("删除");
                dialog.setMessage("确定要删除类型并清空对应消费记录?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        types.remove(typePosition);
                        typeAdapter.setmRecordType(types);
                        typeAdapter.notifyDataSetChanged();
                        //删除类型和相应记录
                        DataSupport.deleteAll(RecordType.class,"id=?",String.valueOf(currentType.getId()));
                        DataSupport.deleteAll(Record.class,"typeid=?",String.valueOf(currentType.getId()));

                        recordAdapter.setmRecord(null);
                        recordAdapter.notifyDataSetChanged();
                        typePosition = -1;
                        statistics.setText("记录:0  总花费:0  月花费:0");
                        Toast.makeText(BillActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BillActivity.this, "下次别手滑了", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
        //给recordView添加监听器
        recordAdapter.setItemClickListener(new OnRecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View view) {
                recordPosition =recordView.getChildAdapterPosition(view);
                record = records.get(recordPosition);
                Intent intent = new Intent(BillActivity.this,AddRecordActivity.class);
                intent.putExtra("record",record);
                startActivity(intent);
            }

            @Override
            public void onItemLongClickListener(View view) {
                recordPosition =recordView.getChildAdapterPosition(view);
                record = records.get(recordPosition);
                AlertDialog.Builder dialog = new AlertDialog.Builder(BillActivity.this);
                dialog.setTitle("删除");
                dialog.setMessage("确定要删除此消费记录?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        records.remove(recordPosition);
                        recordAdapter.setmRecord(records);
                        recordAdapter.notifyDataSetChanged();

                        DataSupport.deleteAll(Record.class,"id=?",String.valueOf(record.getId()));
                        //更新统计
                        AppUtil.doStatisticsMsg(records);

                        Toast.makeText(BillActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BillActivity.this, "下次别手滑了", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
        //查看全部账单
        allRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                records = DataSupport.findAll(Record.class);
                recordAdapter.setmRecord(records);
                if (typePosition!=-1)
                    for (int i = 0;i<typeAdapter.isClicks.size();i++)
                    typeAdapter.isClicks.set(i,false);
                recordAdapter.notifyDataSetChanged();
                typePosition = -1;
                //更新统计
                AppUtil.doStatisticsMsg(records);
            }
        });

        //添加消费类型弹窗
        addType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(BillActivity.this);
                dialog.setTitle("添加新的消费类型");
                dialog.setIcon(android.R.drawable.ic_dialog_info);
                final EditText et = new EditText(BillActivity.this);
                dialog.setView(et);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText().toString().equals(""))
                            Toast.makeText(BillActivity.this, "类型名不能为空", Toast.LENGTH_SHORT).show();
                        else {
                            RecordType type = new RecordType();
                            type.setType(et.getText().toString());
                            type.save();
                            types.add(type);
                            typeAdapter.setmRecordType(types);
                            typeAdapter.notifyDataSetChanged();
                        }
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BillActivity.this, "下次别手滑了", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });

        //添加消费记录
        addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (types == null||types.size()==0){
                    Toast.makeText(BillActivity.this, "请先添加消费类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(BillActivity.this,AddRecordActivity.class);
                intent.putExtra("type",typePosition);
                startActivity(intent);
                }
        });


        //按金额排序
        orderByCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (records==null||records.size()==0){
                    Toast.makeText(BillActivity.this, "没有记录可以排序", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (typePosition == -1){
                    if (AppUtil.listEqual(records,DataSupport.order("cost asc").find(Record.class)))
                        records = DataSupport.order("cost desc").find(Record.class);
                    else
                        records = DataSupport.order("cost asc").find(Record.class);
                }
                //未排序或降序时按升序排，升序时按降序排
                else if (AppUtil.listEqual(records,DataSupport.where("typeid=?",String.valueOf(currentType.getId())).order("cost asc").find(Record.class)))
                    records = DataSupport.where("typeid=?",String.valueOf(currentType.getId())).order("cost desc").find(Record.class);
                else
                    records = DataSupport.where("typeid=?",String.valueOf(currentType.getId())).order("cost asc").find(Record.class);
                recordAdapter.setmRecord(records);
                recordAdapter.notifyDataSetChanged();
            }
        });
        //按日期排序
        orderByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (records==null||records.size()==0){
                    Toast.makeText(BillActivity.this, "没有记录可以排序", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (typePosition == -1){
                    if (AppUtil.listEqual(records,DataSupport.order("date asc").find(Record.class)))
                        records = DataSupport.order("date desc").find(Record.class);
                    else
                        records = DataSupport.order("date asc").find(Record.class);
                }
                //未排序或降序时按升序排，升序时按降序排
                else if (AppUtil.listEqual(records,DataSupport.where("typeid=?",String.valueOf(currentType.getId())).order("date asc").find(Record.class)))
                    records = DataSupport.where("typeid=?",String.valueOf(currentType.getId())).order("date desc").find(Record.class);
                else
                    records = DataSupport.where("typeid=?",String.valueOf(currentType.getId())).order("date asc").find(Record.class);
                recordAdapter.setmRecord(records);
                recordAdapter.notifyDataSetChanged();
            }
        });
    }
}
