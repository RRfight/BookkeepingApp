package com.exmaple.recordlife.myUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.exmaple.recordlife.BillActivity;
import com.exmaple.recordlife.entity.ImageMsg;
import com.exmaple.recordlife.entity.Record;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by RR on 2019/11/22.
 */

public class AppUtil {
    //将图片转换为字节
    public static byte[] imgToBytes(Bitmap bitmap){
        //将图片转化为位图
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos= new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为70%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            //将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }catch (Exception e){
        }finally {
            try {
                bitmap.recycle();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }
    //将字节转换为位图
    public static Bitmap bytesToImg(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    public static List<Bitmap> getBitmaps(int id){
        List<ImageMsg> imageMsgs = DataSupport.select("image").where("journalid=?", String.valueOf(id)).find(ImageMsg.class);
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        for(ImageMsg msg:imageMsgs){
            bitmaps.add(bytesToImg(msg.getImage()));
        }
        return bitmaps;
    }
    //今天
    public static Date today(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        return c.getTime();
    }
    //月初
    public static Date monthBegin(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DATE,1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    //月末
    public static Date monthEnd() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        c.set(Calendar.DATE, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }
    //统计消费数，总消费和月消费
    public static void doStatisticsMsg(List<Record> records){
        int count = 0;
        float sum = 0,monthSum = 0;
        String[] msg = new String[3];
        for (Record r : records){
            count++;
            sum =sum + r.getCost();
            if (r.getDate().getTime()>=monthBegin().getTime()&&r.getDate().getTime()<=monthEnd().getTime())
                monthSum += r.getCost();
        }
        BillActivity.statistics.setText("记录:"+count+"  总花费:"+sum+"  月花费:"+monthSum);
    }
    public static boolean listEqual(List<Record> r1,List<Record>r2){
        for (int i = 0;i<r1.size();i++){
            if (r1.get(i).getId()!=r2.get(i).getId()){
                return false;
            }
        }
        return true;
    }
}
