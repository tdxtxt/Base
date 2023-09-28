package com.tdxtxt.pickerview.dataset;

import java.util.List;

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-09-28
 *     desc   :
 * </pre>
 */
public class StringDataSet implements OptionDataSet {
    private String value;
    public StringDataSet(String value){
        this.value = value;
    }

    @Override
    public CharSequence getCharSequence() {
        return value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public List<? extends OptionDataSet> getSubs() {
        return null;
    }
}
