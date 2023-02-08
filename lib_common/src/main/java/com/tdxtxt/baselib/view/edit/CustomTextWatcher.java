package com.tdxtxt.baselib.view.edit;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.tdxtxt.baselib.R;

public abstract class CustomTextWatcher implements TextWatcher {
    private String lastStr;
    private EditText editText;

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public abstract void onTextChanged(EditText editText, String context);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(TextUtils.isEmpty(lastStr)){
            onTextChanged(editText, editable.toString());
        }else if(!lastStr.equals(editable.toString())){
            onTextChanged(editText, editable.toString());
            lastStr = editable.toString();
        }
    }

    public static void createImpl(CustomTextWatcher lisenter, EditText... ets){
        if(ets == null) return;
        for (EditText et : ets) {
            Object oldLisenter = et.getTag(R.id.tag_watcher_listener);
            if(oldLisenter instanceof TextWatcher){
                et.removeTextChangedListener((TextWatcher) oldLisenter);
            }

            lisenter.setEditText(et);
            et.addTextChangedListener(lisenter);
            et.setTag(R.id.tag_watcher_listener, lisenter);
        }
    }
}
