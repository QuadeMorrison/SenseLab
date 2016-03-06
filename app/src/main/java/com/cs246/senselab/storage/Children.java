package com.cs246.senselab.storage;

import java.util.List;

/**
 * A wrapper interface used to encapsulate the data type of folder children as defined
 * by any API used by the storage package
 *
 * @param <T> Data type of API folder children
 */
public interface Children<T> {
    T getChildren();
    void setChildren(T aChildren);
    List<String> getNames();
    List<String> getIds();
}

