package com.mierzejewski.inzynierka.model;

import java.io.Serializable;

/**
 * Created by dom on 07/04/15.
 */
public enum ExpenseIncomeType implements Serializable
{

    EXPENSE(1),INCOME(2);


    int a;

    ExpenseIncomeType(int a)
    {
        this.a = a;
    }
}
