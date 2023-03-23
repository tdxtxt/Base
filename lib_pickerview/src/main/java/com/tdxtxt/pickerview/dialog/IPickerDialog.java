package com.tdxtxt.pickerview.dialog;

import android.content.Context;

import com.tdxtxt.pickerview.picker.BasePicker;

public interface IPickerDialog {

  /**
   * picker create 时回调
   * @see BasePicker#BasePicker(Context)
   */
  void onCreate(BasePicker picker);

  /**
   * 其实可以不提供这个方法，为了方便在{@link BasePicker#show()}
   */
  void showDialog();
}
