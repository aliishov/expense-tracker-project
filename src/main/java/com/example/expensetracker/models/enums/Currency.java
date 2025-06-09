package com.example.expensetracker.models.enums;

import lombok.Getter;

@Getter
public enum Currency {
    AZN(1.7),
    EUR(1.86),
    USD(1),
    TRL(0.03),
    AED(0.3);

    final double val;

    Currency(double val) {
        this.val = val;
    }

}
