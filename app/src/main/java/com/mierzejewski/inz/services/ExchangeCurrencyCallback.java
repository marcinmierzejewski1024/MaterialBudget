package com.mierzejewski.inz.services;

import java.util.List;

/**
 * Created by dom on 10/04/15.
 */
public interface ExchangeCurrencyCallback
{
    public void onSuccess(List<Object> data);
    public void onError(String data);
}
