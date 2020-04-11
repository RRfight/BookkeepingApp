package com.exmaple.recordlife.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exmaple.recordlife.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.exmaple.recordlife.entity.Journal;
import com.exmaple.recordlife.listener.OnRecyclerViewClickListener;

/**
 * Created by RR on 2019/11/18.
 */

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder>{
    private List<Journal> mJournal;
    //配置监听器
    private OnRecyclerViewClickListener listener;
    //记录item的选中情况
    private List<Boolean> isClicks;
    //在adapter中置入适配器
    public void setItemClickListener(OnRecyclerViewClickListener itemClickListener) {
        listener = itemClickListener;
    }

    public Journal getJorunal(int position){
        return mJournal.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View journalView;
        TextView date;
        public ViewHolder(View view){
            super(view);
            date = (TextView) view.findViewById(R.id.journal_date);
        }
    }

    public JournalAdapter(List<Journal> journals){
        mJournal = journals;
        isClicks = new ArrayList<>();
        for (int i = 0;i<mJournal.size()+2;i++){
            isClicks.add(false);
        }
    }
    public void setJournals(List<Journal> journals){
        mJournal = journals;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_items,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final JournalAdapter.ViewHolder holder, int position) {
        Journal journal = mJournal.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        holder.date.setText((position + 1) + "、" + sdf.format(journal.getDate()));
        //触发监听之后adapter中要进行的操作
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
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }
    @Override
    public int getItemCount() {
        try {
            return mJournal.size();
        }catch (NullPointerException e){
            return 0;
        }
    }
}
