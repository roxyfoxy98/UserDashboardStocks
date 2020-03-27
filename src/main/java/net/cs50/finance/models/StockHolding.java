package net.cs50.finance.models;

import net.cs50.finance.models.dao.StockHoldingDao;
import net.cs50.finance.models.dao.StockTransactionDao;
import net.cs50.finance.models.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import yahoofinance.YahooFinance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cbay on 5/10/15.
 */

/**
 * Represents a user's ownership stake in a particular stock
 */
@Entity
@Table(name = "stock_holdings")
public class StockHolding extends AbstractEntity {

    private String symbol;
    private int sharesOwned;
    private int ownerId;
    private double investment;
    private double closevalue;
    private double averagePrice;
    public double getClosevalue() {
        return closevalue;
    }

    public void setClosevalue(double closevalue) {
        this.closevalue = closevalue;
    }

    /**
     * The history of past transactions in which this user bought or sold shares from this stock holding
     */
    public List<StockTransaction> transactions;




    public StockHolding() {}

    private StockHolding(String symbol, int ownerId) {
        // make sure symbol is always upper or lowercase (your choice)
        this.symbol = symbol.toUpperCase();
        this.sharesOwned = 0;
        this.closevalue=0;
        this.investment=0;
        this.ownerId = ownerId;
        this.averagePrice=0;
        transactions = new ArrayList<StockTransaction>();
    }




    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    @NotNull
    @Column(name = "owner_id", nullable = false)
    public int getOwnerId(){
        return ownerId;
    }

    protected void setOwnerId(int ownerId){
        this.ownerId = ownerId;
    }

    @NotNull
    @Column(name = "symbol", nullable = false)
    public String getSymbol() {
        return symbol;
    }

    protected void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @NotNull
    @Column(name = "shares_owned", nullable = false)
    public int getSharesOwned() {
        return sharesOwned;
    }

    protected void setSharesOwned(int sharesOwned) {
        this.sharesOwned = sharesOwned;
    }

    @OneToMany(mappedBy = "stockHolding", cascade = CascadeType.PERSIST)
    public List<StockTransaction> getTransactions() {
        return transactions;
    }

    protected void setTransactions(List<StockTransaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Instance method for buying shares of a holding
     *
     * @param numberOfShares
     * @throws IllegalArgumentException if numberOfShares < 0
     * @throws StockLookupException     if unable to lookup stock info
     */
    private void buyShares(int numberOfShares) throws StockLookupException {

        if (numberOfShares < 0) {
            throw new IllegalArgumentException("Can not purchase a negative number of shares.");
        }

        setSharesOwned(sharesOwned + numberOfShares);

        StockTransaction transaction = new StockTransaction(this, numberOfShares, StockTransaction.TransactionType.BUY);
        this.transactions.add(transaction);
        yahoofinance.Stock mystock = Stock.lookupStock(symbol);
        Double stockPrice = Double.parseDouble(mystock.getQuote().getPrice().toString());

        this.setInvestment(this.getInvestment()+stockPrice*numberOfShares);


      // double  sum=0;



    }

    public double getInvestment() {
        return investment;
    }

    public void setInvestment(double investment) {
        this.investment = investment;
    }

    /**
     * Instance method for selling shares of a holding
     *
     * @param numberOfShares
     * @throws IllegalArgumentException if numberOfShares greater than shares owned
     * @throws StockLookupException     if unable to lookup stock info
     */
    private void sellShares(int numberOfShares) throws StockLookupException, IOException {
        yahoofinance.Stock stock= YahooFinance.get(symbol);

        if (numberOfShares > sharesOwned) {
            throw new IllegalArgumentException("Number to sell exceeds shares owned for stock" + symbol);
        }
        if (sharesOwned==numberOfShares)
        {
            this.setClosevalue(numberOfShares*(Double.parseDouble(stock.getQuote().getPrice().toString())));
        }
        setSharesOwned(sharesOwned - numberOfShares);
        StockTransaction transaction = new StockTransaction(this, numberOfShares, StockTransaction.TransactionType.SELL);
        this.transactions.add(transaction);


    }

    /**
     * Static method for buying shares of a StockHolding. Creates a new holding if the user did not already have one,
     * otherwise simply updates sharesOwned on the existing holding
     *
     * @param user              user to buy the stock
     * @param symbol            symbol of the stock to buy
     * @param numberOfShares    number of shares to buy
     * @return                  the holding for the user and symbol
     * @throws IllegalArgumentException
     */
    public static StockHolding buyShares(User user, String symbol, int numberOfShares) throws StockLookupException, IOException {
        // make sure symbol matches case convention
        symbol = symbol.toUpperCase();

        // Get existing holding
        Map<String, StockHolding> userPortfolio = user.getPortfolio();
        StockHolding holding;

        // Create new holding, if user has never owned the stock before
        if (!userPortfolio.containsKey(symbol)) {
            holding = new StockHolding(symbol, user.getUid());
            user.addHolding(holding);
            holding = userPortfolio.get(symbol);
        } else {// Conduct buy
            holding = userPortfolio.get(symbol);
        }
        holding.buyShares(numberOfShares);

        // update user cash on buy
        // subtract price from cash
        yahoofinance.Stock mystock = Stock.lookupStock(symbol);
        Double stockPrice = Double.parseDouble(mystock.getQuote().getPrice().toString());

        double totalPrice = stockPrice * numberOfShares;

        // get amount of cash from user
        Double cash = user.getCash();

        // if purchase price is greater than amount of user cash, throw exception
        if (totalPrice > cash) {
            throw new IllegalArgumentException("Purchase price is greater than the amount of cash available.");
        }
        Double newCash = cash - totalPrice;
        user.setCash(newCash);
        // holding.setInvestment(holding.getInvestment()+totalPrice);




            return holding;
        }


        /**
         * Static method for selling shares of a StockHolding.
         *
         * @param user              owner of the holding
         * @param symbol            symbol of the holding to sell
         * @return the given holding for symbol and user, or null if user has never owned any of symbol's stock
         * @throws IllegalArgumentException
         */
        public static StockHolding updateaverageprice (User user, String symbol, List<StockTransaction> transactions) throws IOException {

            symbol = symbol.toUpperCase();

            // Get existing holding
            Map<String, StockHolding> userPortfolio = user.getPortfolio();
            StockHolding holding;
            holding = userPortfolio.get(symbol);


            if (holding.getAveragePrice() != 0) {
                double price = 0;
                System.out.println(transactions.size());
                for (int i=0; i<transactions.size();i++
               ) {
                    System.out.println(transactions.get(i));
                   price = price + transactions.get(i).getPrice() * transactions.get(i).getShares();

               }
                System.out.println(holding.getSharesOwned());
               holding.setAveragePrice((price) / holding.getSharesOwned());
           }

        else {
                yahoofinance.Stock stock=YahooFinance.get(symbol);
               double price= Double.parseDouble(stock.getQuote().getPrice().toString());
        holding.setAveragePrice(price);
    }
return holding;
}


        public static StockHolding sellShares (User user, String symbol,int numberOfShares) throws
        StockLookupException, IOException {

            // make sure symbol matches case convention, symbol toupperCase
            symbol = symbol.toUpperCase();

            // Get existing holding
            Map<String, StockHolding> userPortfolio = user.getPortfolio();
            StockHolding holding;

            if (!userPortfolio.containsKey(symbol)) {
                return null;
            }

            // Conduct sale
            holding = userPortfolio.get(symbol);
            holding.sellShares(numberOfShares);
            yahoofinance.Stock stock = YahooFinance.get(symbol);

            holding = userPortfolio.get(symbol);

            double cashfromselling = numberOfShares * (Double.parseDouble(stock.getQuote().getPrice().toString()));
          //  System.out.println(cashfromselling);
            user.setCash(user.getCash() + cashfromselling);
            System.out.println("Noul investment e "+ (holding.getInvestment()-(holding.getAveragePrice()*numberOfShares)));
            holding.setInvestment(holding.getInvestment()-(holding.getAveragePrice()*numberOfShares));

            return holding;
        }
    }

