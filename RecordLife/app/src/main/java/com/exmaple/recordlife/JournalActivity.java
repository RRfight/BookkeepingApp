package com.exmaple.recordlife;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.exmaple.recordlife.adapter.ImageMsgAdapter;
import com.exmaple.recordlife.adapter.JournalAdapter;
import com.exmaple.recordlife.listener.OnRecyclerViewClickListener;
import com.exmaple.recordlife.listener.OnRecyclerViewLongClickListener;
import com.exmaple.recordlife.entity.ImageMsg;
import com.exmaple.recordlife.entity.Journal;
import com.exmaple.recordlife.myUtil.AppUtil;

public class JournalActivity extends AppCompatActivity {
    List<Journal> journals = new ArrayList<Journal>();//日志们
    List<ImageMsg> imageMsgs = new ArrayList<ImageMsg>();//图片们
    Journal currentJournal;//记录当前选中的日志
    int position = -1;//记录当前选中的日志位置
    int imagePosition = -1;//记录选中的图片
    RecyclerView recyclerView;
    RecyclerView imageView;
    JournalAdapter adapter;
    ImageMsgAdapter imageAdapter;
    EditText editContent;//编辑日志内容
    TextView textContent;//显示日志内容
    Button addJournal;//添加日志
    Button reverse;//升序/降序排序
    Button editSave;//编辑/保存日志
    Button addImage;//添加图片
    Button findByTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        //隐藏头部
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.hide();

        //获取数据库中的日志
        journals = DataSupport.findAll(Journal.class);

        //获得控件实例
        editContent = (EditText) findViewById(R.id.edit_content);
        addJournal = (Button) findViewById(R.id.add_journal);
        addImage = (Button) findViewById(R.id.add_image);
        editSave = (Button) findViewById(R.id.edit_save);
        reverse = (Button) findViewById(R.id.reverse);
        textContent = (TextView) findViewById(R.id.text_content);
        findByTime = (Button) findViewById(R.id.find_by_time);
        recyclerView = (RecyclerView) findViewById(R.id.journal_item);
        imageView = (RecyclerView) findViewById(R.id.image_item);

        //设置编辑控件不可见，当要编辑或新增日志时才显示出来
        editContent.setVisibility(View.INVISIBLE);

        //设置横向的日志recycleview
        final LinearLayoutManager journalLayoutManager = new LinearLayoutManager(this);
        journalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(journalLayoutManager);
        adapter = new JournalAdapter(journals);
        recyclerView.setAdapter(adapter);
        //设置横向的图片recycleview
        final LinearLayoutManager imageLayoutManager = new LinearLayoutManager(this);
        imageLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        imageView.setLayoutManager(imageLayoutManager);
        imageAdapter = new ImageMsgAdapter(imageMsgs);
        imageView.setAdapter(imageAdapter);

        //在activity中为日志适配器添加监听
        adapter.setItemClickListener(new OnRecyclerViewClickListener() {
            @Override
            public void onItemClickListener(View view) {
                //获得当前选中日志和选中的条目号
                position = recyclerView.getChildAdapterPosition(view);
                currentJournal = adapter.getJorunal(position);
                //显示日志内容
                editContent.setText(currentJournal.getContent());
                textContent.setText(currentJournal.getContent());
                editContent.setVisibility(View.GONE);
                textContent.setVisibility(View.VISIBLE);
                imageMsgs = DataSupport.where("journalid=?", String.valueOf(currentJournal.getId())).find(ImageMsg.class);
                imageAdapter.setImageMsgs(imageMsgs);
                imageAdapter.notifyDataSetChanged();
                addJournal.setText("新增日志");
                editSave.setText("编辑");
            }

            @Override
            public void onItemLongClickListener(View view) {
                position = recyclerView.getChildAdapterPosition(view);
                currentJournal = journals.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(JournalActivity.this);
                dialog.setTitle("删除");
                dialog.setMessage("确定要删除此日志？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        journals.remove(position);
                        adapter.setJournals(journals);
                        adapter.notifyDataSetChanged();
                        //删除日志及图片
                        DataSupport.deleteAll(Journal.class,"id=?",String.valueOf(currentJournal.getId()));
                        DataSupport.deleteAll(ImageMsg.class,"journalid=?",String.valueOf(currentJournal.getId()));

                        editContent.setText("");
                        textContent.setText("");
                        imageAdapter.setImageMsgs(null);
                        imageAdapter.notifyDataSetChanged();
                        position = -1;
                        Toast.makeText(JournalActivity.this, "删除日志成功", Toast.LENGTH_SHORT).show();

                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(JournalActivity.this, "下次别手滑了", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });

        //为图片适配器添加监听
        imageAdapter.setItemLongClickListener(new OnRecyclerViewLongClickListener() {
            @Override
            public void onItemLongClick(View view) {
                imagePosition = imageView.getChildAdapterPosition(view);
                AlertDialog.Builder dialog = new AlertDialog.Builder(JournalActivity.this);
                dialog.setTitle("删除");
                dialog.setMessage("确定要删除此图片？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataSupport.deleteAll(ImageMsg.class,"id=?",String.valueOf(imageMsgs.get(imagePosition).getId()));
                        imageMsgs.remove(imagePosition);
                        imageAdapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(JournalActivity.this, "下次别手滑了", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });

        //新增日志
        addJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addJournal.getText().toString().equals("新增日志")){
                    editContent.setText(null);
                    textContent.setText(null);
                    editContent.setVisibility(View.VISIBLE);
                    textContent.setVisibility(View.GONE);
                    if (position!=-1)
                        position=-1;
                    addJournal.setText("保存日志");
                    imageAdapter.setImageMsgs(null);
                    imageAdapter.notifyDataSetChanged();
                }
                else{
                    String content = editContent.getText().toString();
                    if(content.equals("")){
                        Toast.makeText(JournalActivity.this, "日志内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Journal j = new Journal();
                    j.setContent(content);
                    j.setDate(new Date());
                    editContent.setText(content);
                    textContent.setText(content);
                    editContent.setVisibility(View.GONE);
                    textContent.setVisibility(View.VISIBLE);
                    j.save();
                    addJournal.setText("新增日志");
                    Toast.makeText(JournalActivity.this, "添加新日志成功", Toast.LENGTH_SHORT).show();
                    journals.add(j);
                    adapter.setJournals(journals);
                    adapter.notifyDataSetChanged();
                    textContent.setText("");

                }
            }
        });

        //添加图片
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //没有选择日志先选择
                if (position==-1){
                    if (addJournal.getText().equals("保存日志")){
                        Toast.makeText(JournalActivity.this, "请先保存新日志再添加图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(JournalActivity.this, "请先选择日志", Toast.LENGTH_SHORT).show();
                    return;
                }
                //没有权限的话要先申请
                if (ContextCompat.checkSelfPermission(JournalActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(JournalActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    //打开相册选择图片
                    openAlbum();
                }
            }
        });

        //编辑日志
        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editSave.getText().equals("编辑")){
                    if (position == -1){
                        if (addJournal.getText().equals("保存日志")){
                            Toast.makeText(JournalActivity.this, "请先完成新日志的编写", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(JournalActivity.this, "请先选择日志", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editContent.setVisibility(View.VISIBLE);
                    textContent.setVisibility(View.GONE);
                    editSave.setText("保存");
                }
                else{
                    if (editContent.getText().toString().equals("")){
                        Toast.makeText(JournalActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!editContent.getText().toString().equals(currentJournal.getContent())){
                        currentJournal.setContent(editContent.getText().toString());
                        currentJournal.updateAll("id = ?", String.valueOf(currentJournal.getId()));
                        journals.get(position).setContent(editContent.getText().toString());
                        textContent.setText(editContent.getText().toString());
                    }
                    editContent.setVisibility(View.GONE);
                    textContent.setVisibility(View.VISIBLE);
                    editSave.setText("编辑");
                    Toast.makeText(JournalActivity.this, "保存编辑", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //反转日志排序
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reverse.getText().equals("降序排序")){
                    journalLayoutManager.setReverseLayout(true);
                    reverse.setText("升序排序");
                }
                else{
                    journalLayoutManager.setReverseLayout(false);
                    reverse.setText("降序排序");
                }
            }
        });

        //查找日志，查找出选定日期往后所有日志
        findByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();     //  获取当前时间    —   年、月、日
                int year = c.get(Calendar.YEAR);         //  得到当前年
                int month = c.get(Calendar.MONTH);       //  得到当前月
                final int day = c.get(Calendar.DAY_OF_MONTH);  //  得到当前日

                new DatePickerDialog(JournalActivity.this,new DatePickerDialog.OnDateSetListener() {      //  日期选择对话框
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //  这个方法是得到选择后的 年，月，日，分别对应着三个参数 — year、month、dayOfMonth
                        c.set(year,month,dayOfMonth);
                        c.set(Calendar.HOUR, 0);
                        c.set(Calendar.MINUTE, 0);
                        c.set(Calendar.SECOND, 0);
                        journals = DataSupport.where("date>?",String.valueOf(c.getTimeInMillis())).find(Journal.class);
                        adapter.setJournals(journals);
                        adapter.notifyDataSetChanged();
                    }
                },year,month,day).show();   //  弹出日历对话框时，默认显示 年，月，日
            }
        });
    }
    //打开相册选择图片
    public void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }
    //用户点击弹窗授权窗口上的按钮后调用该方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this, "你拒绝了权限授予", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1://1与startActivityForResult中的1相等
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downerloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        if (imagePath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            image.setImageBitmap(bitmap);
            ImageMsg msg = new ImageMsg();
            msg.setJournalId(currentJournal.getId());
            msg.setImage(AppUtil.imgToBytes(bitmap));
            msg.save();
            imageMsgs.add(msg);
            imageAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(this, "添加图片失败", Toast.LENGTH_SHORT).show();
        }
    }
}
