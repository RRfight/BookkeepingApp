package com.exmaple.recordlife.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.exmaple.recordlife.R;
import com.exmaple.recordlife.entity.ImageMsg;
import com.exmaple.recordlife.listener.OnRecyclerViewLongClickListener;
import com.exmaple.recordlife.myUtil.AppUtil;

import java.util.List;

/**
 * Created by RR on 2019/11/18.
 */

public class ImageMsgAdapter extends RecyclerView.Adapter<ImageMsgAdapter.ViewHolder>{
    private List<ImageMsg> mImageMsgs;
    //配置监听器
    private OnRecyclerViewLongClickListener listener;

    public void setItemLongClickListener(OnRecyclerViewLongClickListener itemLongClickListener) {
        listener = itemLongClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View imageView;
        ImageView image;
        public ViewHolder(View view){
            super(view);
            image = (ImageView) view.findViewById(R.id.journal_image);
        }
    }

    public ImageMsgAdapter(List<ImageMsg> imageMsgs){
        mImageMsgs = imageMsgs;
    }
    public void setImageMsgs(List<ImageMsg> imageMsgs){
        mImageMsgs = imageMsgs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_items,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ImageMsgAdapter.ViewHolder holder, int position) {
        ImageMsg msg = mImageMsgs.get(position);
        holder.image.setImageBitmap(AppUtil.bytesToImg(msg.getImage()));
        if (listener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(v);
                    return false;
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        //捕获list为空时调用方法抛出的空指针异常
        try {
            return mImageMsgs.size();
        }catch (NullPointerException e){
            return 0;
        }
    }
}
