package com.ketu.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

/**
 * <p/>
 * 功能 :recycleview的基适配器
 *
 * @author ketu 时间 2017/9/12
 * @version 1.0
 */

public abstract class RecycleViewBaseAdapter<T> extends RecyclerView.Adapter<RecycleViewBaseAdapter.BaseViewHolder> {
    /*上下文环境*/
    public Context context;
    /*布局加载器*/
    public LayoutInflater inflater;
    /*数据集*/
    public ArrayList<T> dataList;

    public RecycleViewBaseAdapter(Context context){
         this.context = context;
         inflater = LayoutInflater.from(context);
    }


    /*recyclerview优化的holder*/
    public static abstract class BaseViewHolder<Data> extends RecyclerView.ViewHolder{

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void onRefreshView(Data data,int position);

        public final <T> T findViewById(int resId){

            return (T) itemView.findViewById(resId);

        }

    }

    /**
     * 获取列表数据
     * @return
     */
    public int getItemCount(){
        if (dataList != null){
            return dataList.size();
        }
        return 0;
    }

    /**
     * 设置数据集
     * @param dataList
     */
    public void setDataList(ArrayList<T> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    /**在列表尾追加数据
     * @param dataList
     */
    public void addDataList(ArrayList<T> dataList){
        if (dataList == null){
            dataList = new ArrayList<>();
        }
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }



}
