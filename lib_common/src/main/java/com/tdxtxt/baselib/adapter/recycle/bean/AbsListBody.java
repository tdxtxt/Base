package com.tdxtxt.baselib.adapter.recycle.bean;

import java.util.List;

public interface AbsListBody<T> {
    List<T> getDataList();
    boolean hasNextPage();
    boolean isEmpty();
}
