package com.example.expensetracker.models.enums;

import lombok.Getter;

@Getter
public enum Currency {
    USD(1),
    EUR(1.1579),
    GBP(1.3584),
    JPY(0.0069),
    CHF(1.2315),
    CAD(0.7366),
    AUD(0.6508),
    NZD(0.6029),
    CNY(0.1392),
    SEK(0.1055),
    NOK(0.1012),
    DKK(0.1552),
    SGD(0.7811),
    HKD(0.1274),
    AED(0.2723),
    TRY(0.0254),
    RUB(0.0127),
    INR(0.0116),
    BRL(0.1795),
    ZAR(0.0557),
    KRW(0.00074),
    AZN(0.5882);

    final double val;

    Currency(double val) {
        this.val = val;
    }

}
