package com.tdxtxt.baselib.adapter.recycle.bean;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class BaseListBody<T> {
    public int pageNum;
    public int pageSize;
    public int totalPage;
    public int totalCount;
    public List<T> list;

    public boolean isNextPage(){
        return pageNum < totalPage;
    }

    public boolean isEmpty(){
        return 0 == totalCount;
    }
}
