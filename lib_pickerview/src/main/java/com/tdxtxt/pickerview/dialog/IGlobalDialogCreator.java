package com.tdxtxt.pickerview.dialog;

import android.content.Context;

public interface IGlobalDialogCreator {
  /**
   * 创建IPickerDialog
   */
  IPickerDialog create(Context context);
}