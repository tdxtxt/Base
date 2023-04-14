package com.tdxtxt.linkedview;


import java.util.List;

/**
 * Created by wuzongbo on 2018/1/2.
 */

public interface IPickerData {

    String getObjId();

    String getDisplayName();

    boolean isSelectedItem();

    void setSelectItem(boolean selected);

    boolean isCheckedItem();

    void setCheckedItem(boolean checked);

    List<? extends IPickerData> nodes();
}
