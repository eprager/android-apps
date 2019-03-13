package com.emmaprager.stockwatch;

public class Stock {
    private String symbol;
    private String company;
    private double price;
    private double price_change;
    private double price_change_percent;

    Stock(String symbol, String company, double price, double price_change, double price_change_percent) {
        this.symbol = symbol;
        this.company = company;
        this.price = price;
        this.price_change = price_change;
        this.price_change_percent = price_change_percent;
    }

    Stock() {
        symbol = "XXX";
        company = "Company, Inc.";
        price = 100;
        price_change = 1;
        price_change_percent = 1;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompany() {
        return company;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceChange() {
        return price_change;
    }

    public double getPriceChangePercent() {
        return price_change_percent;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPriceChange(double price_change) {
        this.price_change = price_change;
    }

    public void setPriceChangePercent(double price_change_percent) {
        this.price_change_percent = price_change_percent;
    }

}
