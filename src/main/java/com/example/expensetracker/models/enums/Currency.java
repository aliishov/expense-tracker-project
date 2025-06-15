package com.example.expensetracker.models.enums;

import lombok.Getter;

@Getter
public enum Currency {
    USD(1),
    EUR(1),
    GBP(1),
    JPY(1),
    CHF(1),
    CAD(1),
    AUD(1),
    NZD(1),
    CNY(1),
    SEK(1),
    NOK(1),
    DKK(1),
    SGD(1),
    HKD(1),
    AED(1),
    TRY(1),
    RUB(1),
    INR(1),
    BRL(1),
    ZAR(1),
    KRW(1),
    AZN(1);

    final double val;

    Currency(double val) {
        this.val = val;
    }

}
