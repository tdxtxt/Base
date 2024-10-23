package com.tdxtxt.pickerview.util;

import android.content.Context;

import com.tdxtxt.pickerview.R;
import com.tdxtxt.pickerview.dataset.OptionDataSet;
import com.tdxtxt.pickerview.dataset.StringDataSet;
import com.tdxtxt.pickerview.picker.OptionPicker;
import com.tdxtxt.pickerview.picker.TimePicker;

import java.util.ArrayList;
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
     * @param title 标题
     * @param data 源数据
     * @param selectWithValue 初始化选中内容
     * @param listener 选择时的回调
     */
    public static void showOneWheelStr(Context context, String title, List<String> data, String selectWithValue, OnClickItemListener<String> listener){
        if(data == null || data.size() == 0) return;
        List<StringDataSet> list = new ArrayList<>(data.size());
        for(String item : data){
            list.add(new StringDataSet(item));
        }

        if(context == null) return;
        OptionPicker optionPicker = new OptionPicker.Builder(context, 1, new OptionPicker.OnOptionSelectListener(){
            @Override
            public void onOptionSelect(OptionPicker picker, int[] selectedPosition, OptionDataSet[] selectedOptions) {
                if(selectedOptions != null && selectedOptions.length > 0){
                    if(listener != null) listener.onClickItem(selectedOptions[0].getValue());
                }
            }
        }).create();
        optionPicker.setTitle(title);
        optionPicker.setData(list);
        if(selectWithValue != null) optionPicker.setSelectedWithValues(selectWithValue);
        optionPicker.show();
    }
    /**
     * 单个滚轮
     * @param title 标题
     * @param data 源数据
     * @param listener 选择时的回调
     */
    public static void showOneWheelStr(Context context, String title, List<String> data, OnClickItemListener<String> listener){
        showOneWheelStr(context, title, data, null, listener);
    }

    /**
     * 单个滚轮
     * @param title 标题
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

    /**
     * 单个滚轮
     * @param title 标题
     * @param data 源数据
     * @param listener 选择时的回调
     */
    public static void showOneWheel(Context context, String title, List<? extends OptionDataSet> data, OnClickItemListener<OptionDataSet> listener){
        showOneWheel(context, title, data, null, listener);
    }

    /**
     * @param title 标题
     * @param startMillis 开始选择时间
     * @param endMillis 结束选择时间
     * @param selectWithMillis 初始化选择时间
     * @param showType 参考示例：TimePicker.TYPE_DATE 表示仅显示年月日，可以自由组合
     * @param listener 选择时的回调
     */
    public static void showTime(Context context, String title, long startMillis, long endMillis, long selectWithMillis, int showType, OnClickItemListener<Date> listener){
        if(context == null) return;
        TimePicker timePicker = new TimePicker.Builder(context, showType, new TimePicker.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(TimePicker picker, Date... dates) {
                if (listener != null && dates != null && dates.length > 0) listener.onClickItem(dates[0]);
            }
        }).setFormatter(new TimePicker.Formatter() {
                    @Override
                    public CharSequence format(TimePicker picker, int type, int position, long value) {
                        switch (type) {
                            case TimePicker.TYPE_YEAR:
                                return String.format("%d%s", value, context.getString(R.string.年));
                            case TimePicker.TYPE_MONTH:
                                return String.format("%02d%s", value, context.getString(R.string.月));
                            case TimePicker.TYPE_DAY:
                                return String.format("%02d%s", value, context.getString(R.string.日));
                            case TimePicker.TYPE_HOUR:
                                return String.format("%02d%s", value, context.getString(R.string.时));
                            case TimePicker.TYPE_MINUTE:
                                return String.format("%02d%s", value, context.getString(R.string.分));
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

    /**
     * 显示格式： 年  月   日
     * @param title 标题
     * @param startMillis 开始选择时间
     * @param endMillis 结束选择时间
     * @param selectWithMillis 初始化选择时间
     * @param listener 选择时的回调
     */
    public static void showDateTime(Context context, String title, long startMillis, long endMillis, long selectWithMillis, OnClickItemListener<Date> listener){
        showTime(context, title, startMillis, endMillis, selectWithMillis, TimePicker.TYPE_DATE, listener);
    }

    /**
     * 显示格式： 年  月   日
     * @param title 标题
     * @param startMillis 开始选择时间
     * @param endMillis 结束选择时间
     * @param listener 选择时的回调
     */
    public static void showDateTime(Context context, String title, long startMillis, long endMillis, OnClickItemListener<Date> listener){
        showTime(context, title, startMillis, endMillis, System.currentTimeMillis(), TimePicker.TYPE_DATE, listener);
    }

}
