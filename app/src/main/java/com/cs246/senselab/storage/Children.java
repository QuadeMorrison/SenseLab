package com.cs246.senselab.storage;

/**
 * A wrapper interface used to encapsulate the data type of folder children as defined
 * by any API used by the storage package
 *
 * @param <T> Data type of API folder children
 */
public interface Children<T> {
    T getChildren();
    void setChildren(T aChildren);
}

