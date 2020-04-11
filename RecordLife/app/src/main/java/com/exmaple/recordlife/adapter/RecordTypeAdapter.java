package com.exmaple.recordlife.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exmaple.recordlife.R;
import com.exmaple.recordlife.entity.Journal;
import com.exmaple.recordlife.entity.Record;
import com.exmaple.recordlife.entity.RecordType;
import com.exmaple.recordlife.listener.OnRecyclerViewClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RR on 2019/11/18.
 */

public class RecordTypeAdapter extends RecyclerView.Adapter<RecordTypeAdapter.ViewHolder>{
    private List<RecordType> mRecordType;
    //配置监听器
    private OnRecyclerViewClickListener listener;
    //记录item的选中情况
    public List<Boolean> isClicks;

    public void setItemClickListener(OnRecyclerViewClickListener itemClickListener) {
        listener = itemClickListener;
    }

    public RecordType getRecordType(int position){
        return mRecordType.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View recordTypeView;
        TextView typeName;
        public ViewHolder(View view){
            super(view);
            typeName = (TextView) view.findViewById(R.id.type_name);
        }
    }

    public RecordTypeAdapter(List<RecordType> recordTypes){
        mRecordType = recordTypes;
        isClicks = new ArrayList<>();
        for (int i = 0;i<mRecordType.size()+3;i++){
            isClicks.add(false);
        }
    }
    public void setmRecordType(List<RecordType> recordTypes){
        mRecordType = recordTypes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_items,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecordTypeAdapter.ViewHolder holder, int position) {
        RecordType type = mRecordType.get(position);
        holder.typeName.setText(type.getType());
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    listener.onItemClickListener(v);
                    for (int i = 0; i < isClicks.size(); i++) {
                        isClicks.set(i, false);
                    }
                    isClicks.set(position, true);
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
        //6、判断改变属性
        if (isClicks.get(position)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#aaaaaa"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFAFAFA"));
        }
    }
    @Override
    public int getItemCount() {
        try {
            return mRecordType.size();
        }catch (NullPointerException e){
            return 0;
        }
    }
}
