package net.cs50.finance.models;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Chris Bay on 5/15/15.
 */

/**
 * Represents the stock for a particular company. Encapsulates the stock symbol, company name, and current price.
 */
public class Stock {

    private final String symbol;
    private final float price;
    private final String name;

    private Stock(String symbol, String name, float price) {
        this.symbol = symbol;
        this.price = price;
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public float getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName() + " (" + getSymbol() + ")";
    }


    private static final String urlBase = "http://download.finance.yahoo.com/d/quotes.csv?f=snl1&s=";

    /**
     * Factory to create new Stock instances with current price information.
     *
     * @param symbol stock symbol
     * @return Stock instance with current price information, if available, null otherwise
     */
    public static yahoofinance.Stock lookupStock(String symbol) throws Exception {

        yahoofinance.Stock stock = null;



                stock = YahooFinance.get(symbol);
         if (stock== null)
         {
             throw new Exception("Please check the symbol and try again");
         }



        BigDecimal price = stock.getQuote().getPrice();
        BigDecimal change = stock.getQuote().getChangeInPercent();
        BigDecimal peg = stock.getStats().getPeg();
        BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();

        return stock;



    }
}
