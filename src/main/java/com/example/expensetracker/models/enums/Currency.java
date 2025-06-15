package com.example.expensetracker.models.enums;

import lombok.Getter;

@Getter
public enum Currency {
    USD(1),
    EUR(0.87),
    GBP(0.74),
    JPY(144.14),
    CHF(0.81),
    CAD(1.36),
    AUD(1.56),
    NZD(1.66),
    CNY(7.17),
    SEK(9.48),
    NOK(9.91),
    DKK(6.46),
    SGD(1.28),
    HKD(7.85),
    AED(3.67),
    TRY(39.41),
    RUB(79.78),
    INR(86.25),
    BRL(5.54),
    ZAR(17.95),
    KRW(1368.75),
    AZN(1.70);

    final double val;

    Currency(double val) {
        this.val = val;
    }

}
