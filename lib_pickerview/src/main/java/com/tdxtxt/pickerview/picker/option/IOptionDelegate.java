package com.tdxtxt.pickerview.picker.option;


import com.tdxtxt.pickerview.dataset.OptionDataSet;
import com.tdxtxt.pickerview.picker.OptionPicker;

import java.util.List;

/**
 * Created by fuchaoyang on 2018/7/6.<br/>
 * description：
 */

public interface IOptionDelegate {
  //void init(int hierarchy, List<PickerView> pickerViews, int[] selectedPosition);
  void init(OptionPicker.Delegate delegate);

  void setData(List<? extends OptionDataSet>... options);

  void setSelectedWithValues(String... values);

  OptionDataSet[] getSelectedOptions();

  void reset();
}
