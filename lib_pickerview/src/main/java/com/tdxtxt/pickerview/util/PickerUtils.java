package com.tdxtxt.pickerview.util;

import android.content.Context;

import com.tdxtxt.pickerview.dataset.OptionDataSet;
import com.tdxtxt.pickerview.picker.OptionPicker;
import com.tdxtxt.pickerview.picker.TimePicker;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/3/27
 *     desc   :
 * </pre>
 */
public class PickerUtils {

    /**
     * 单个滚轮
     * @param title
     * @param data 源数据
     * @param selectWithValue 初始化选中内容
     * @param listener 选择时的回调
     */
    public static void showOneWheel(Context context, String title, List<? extends OptionDataSet> data, String selectWithValue, OnClickItemListener<OptionDataSet> listener){
        if(context == null) return;
        OptionPicker optionPicker = new OptionPicker.Builder(context, 1, new OptionPicker.OnOptionSelectListener(){
            @Override
            public void onOptionSelect(OptionPicker picker, int[] selectedPosition, OptionDataSet[] selectedOptions) {
                if(selectedOptions != null && selectedOptions.length > 0){
                    if(listener != null) listener.onClickItem(selectedOptions[0]);
                }
            }
        }).create();
        optionPicker.setTitle(title);
        if(data != null) optionPicker.setData(data);
        if(selectWithValue != null) optionPicker.setSelectedWithValues(selectWithValue);
        optionPicker.show();
    }

    public static void showOneWheel(Context context, String title, List<? extends OptionDataSet> data, OnClickItemListener<OptionDataSet> listener){
        showOneWheel(context, title, data, null, listener);
    }



    /**
     *
     * @param title
     * @param startMillis 开始选择时间
     * @param endMillis 结束选择时间
     * @param selectWithMillis 初始化选择时间
     * @param listener 选择时的回调
     */
    public static void showTime(Context context, String title, long startMillis, long endMillis, long selectWithMillis, OnClickItemListener<Date> listener){
        if(context == null) return;
        TimePicker timePicker = new TimePicker.Builder(context, TimePicker.TYPE_DATE, new TimePicker.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(TimePicker picker, Date date) {
                if (listener != null) listener.onClickItem(date);
            }
        }).setFormatter(new TimePicker.Formatter() {
            @Override
            public CharSequence format(TimePicker picker, int type, int position, long value) {
                switch (type) {
                    case TimePicker.TYPE_YEAR:
                        return String.format("%d年", value);
                    case TimePicker.TYPE_MONTH:
                        return String.format("%02d月", value);
                    case TimePicker.TYPE_DAY:
                        return String.format("%02d日", value);
                    case TimePicker.TYPE_HOUR:
                        return String.format("%02d时", value);
                    case TimePicker.TYPE_MINUTE:
                        return String.format("%02d分", value);
                    default:
                        return String.valueOf(value);
                }
            }
        }).setRangDate(startMillis, endMillis)
                .setSelectedDate(selectWithMillis)
                .create();
        timePicker.setTitle(title);
        timePicker.show();
    }

    public static void showTime(Context context, String title, long startMillis, long endMillis, OnClickItemListener<Date> listener){
        showTime(context, title, startMillis, endMillis, System.currentTimeMillis(), listener);
    }

}
