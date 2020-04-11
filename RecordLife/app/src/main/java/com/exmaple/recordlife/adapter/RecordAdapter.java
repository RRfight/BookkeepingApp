package com.exmaple.recordlife.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exmaple.recordlife.BillActivity;
import com.exmaple.recordlife.R;
import com.exmaple.recordlife.entity.Record;
import com.exmaple.recordlife.entity.RecordType;
import com.exmaple.recordlife.listener.OnRecyclerViewClickListener;
import com.exmaple.recordlife.myUtil.AppUtil;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RR on 2019/11/18.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder>{
    private List<Record> mRecord;
    //配置监听器
    private OnRecyclerViewClickListener listener;

    public void setItemClickListener(OnRecyclerViewClickListener itemClickListener) {
        listener = itemClickListener;
    }

    public Record getRecord(int position){
        return mRecord.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View recordView;
        TextView recordDate;
        TextView recordDesc;
        TextView recordCost;
        public ViewHolder(View view){
            super(view);
            recordDate = (TextView) view.findViewById(R.id.record_date);
            recordDesc = (TextView) view.findViewById(R.id.record_desc);
            recordCost = (TextView) view.findViewById(R.id.record_cost);
        }
    }

    public RecordAdapter(List<Record> records){
        mRecord = records;
    }
    public void setmRecord(List<Record> records){
        mRecord = records;
    }
    public void addOneRecord(Record record){
        if (BillActivity.typePosition == -1&& mRecord.size()==0)
            return;
        mRecord.add(record);
        notifyDataSetChanged();
        //更新统计
        AppUtil.doStatisticsMsg(mRecord);
    }
    public void changeOneRecord(Record record){
        for (int i = 0;i<mRecord.size();i++){
            if (mRecord.get(i).getId()==record.getId()){
                //若该条账单修改完还是原来的类型  或者  当前是显示全部，只要修改该信息
                if (mRecord.get(i).getTypeId()==record.getTypeId()||BillActivity.typePosition==-1)
                    mRecord.set(i,record);
                //如果账单的类型修改完跟现在不一样并且不是显示全部，就把他移除出去
                else if (mRecord.get(i).getTypeId()!=record.getTypeId()&&BillActivity.typePosition!=-1)
                    mRecord.remove(i);
                notifyDataSetChanged();
                //更新统计
                AppUtil.doStatisticsMsg(mRecord);
                break;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_items,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecordAdapter.ViewHolder holder, int position) {
        Record record = mRecord.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        holder.recordDate.setText(sdf.format(record.getDate()));
        holder.recordDesc.setText(record.getDesc());
        holder.recordCost.setText(String.valueOf(record.getCost()));
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    listener.onItemClickListener(v);
                    notifyDataSetChanged();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClickListener(v);
                    notifyDataSetChanged();
                    return false;
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        try {
            return mRecord.size();
        }catch (NullPointerException e){
            return 0;
        }
    }
}
