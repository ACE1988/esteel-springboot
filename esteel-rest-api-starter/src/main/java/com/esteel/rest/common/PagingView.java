package com.esteel.rest.common;


import com.esteel.common.core.Pager;

import java.io.OutputStream;

public interface PagingView<T> {

    String getName(String id);

    void write(Pager<T> pages, OutputStream out);
}
