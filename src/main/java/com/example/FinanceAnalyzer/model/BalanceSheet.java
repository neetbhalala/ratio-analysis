package com.example.FinanceAnalyzer.model;

import lombok.Data;

import java.util.Map;

@Data
public class BalanceSheet {

    // Assets
    private Map<String, Double> nonCurrentAssets;
    private Map<String, Double> currentAssets;
    private Double totalNonCurrentAssets;
    private Double totalCurrentAssets;
    private Double totalAssets;

    // Equity
    private Map<String, Double> equity;
    private Double totalEquity;

    // Liabilities
    private Map<String, Double> nonCurrentLiabilities;
    private Map<String, Double> currentLiabilities;
    private Double totalNonCurrentLiabilities;
    private Double totalCurrentLiabilities;
    private Double totalLiabilities;

    // Total Equity and Liabilities
    private Double totalEquityAndLiabilities;

}