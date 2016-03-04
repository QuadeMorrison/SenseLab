package com.cs246.senselab.storage;

import android.widget.ListAdapter;

import java.util.List;

/**
 * Convert Children object into a ListAdapter for android. Provides access to important data
 * that the children contain.
 */
public interface ChildrenAdapter {
    void append(Children children);
    ListAdapter getDataAdapter();
    List<String> getIdList();
    List<String> getNameList();
    void clear();
}
