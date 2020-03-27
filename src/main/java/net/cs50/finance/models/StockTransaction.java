package net.cs50.finance.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Chris Bay on 5/17/15.
 */


/**
 * Represents the record of an event in which a user bought or sold some number of shares of a particular stock.
 */
@Entity
@Table(name = "transactions")
public class StockTransaction extends AbstractEntity {

    public enum TransactionType {
        BUY("buy"), SELL("sell");
        private String type;

        TransactionType(String type){
            this.type = type;
        }
    };

    private TransactionType type;
    private int shares;
    private float price;
    private Date transactionTime;
    private String symbol;
    private int userId;
    private StockHolding stockHolding;

/*public StockTransaction(){
    this.transactionTime = new Date();
     this.type= TransactionType.BUY;
     this.symbol="";

}*/
public StockTransaction() throws StockLookupException {
  }


    public StockTransaction(StockHolding stockHolding, int shares, TransactionType type) throws StockLookupException {
        this.shares = shares;
        this.stockHolding = stockHolding;
        this.transactionTime = new Date();
        this.symbol = stockHolding.getSymbol();
        this.type = type;
        this.userId = stockHolding.getOwnerId();
        this.price =Float.parseFloat(Stock.lookupStock(symbol).getQuote().getPrice().toString());    }

    @Override
    public String toString() {
        return "StockTransaction{" +
                "type=" + type +
                ", shares=" + shares +
                ", price=" + price +
                ", transactionTime=" + transactionTime +
                ", symbol='" + symbol + '\'' +
                ", userId=" + userId +
                ", stockHolding=" + stockHolding +
                '}';
    }

    @ManyToOne
    public StockHolding getStockHolding() {
        return stockHolding;
    }

    protected void setStockHolding(StockHolding stockHolding) {
        this.stockHolding = stockHolding;
    }

    @NotNull
    @Column(name = "shares", nullable = false)
    public int getShares() {
        return shares;
    }

    protected void setShares(int shares) {
        this.shares = shares;
    }

    @NotNull
    @Column(name = "price", nullable = false)
    public float getPrice() {
        return price;
    }

    protected void setPrice(float price) {
        this.price = price;
    }


    @Column(name = "transaction_time", nullable = false)
    public Date getTransationTime() {
        return transactionTime;
    }

    protected void setTransationTime(Date transationTime) {
        this.transactionTime = transationTime;
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
    @Column(name = "user_id", nullable = false)
    public int getUserId() {
        return userId;
    }

    protected void setUserId(int userId) {
        this.userId = userId;
    }

    @NotNull
    @Column(name = "type", nullable = false)
    public TransactionType getType() {
        return this.type;
    }

    protected void setType(TransactionType type) {
        this.type = type;
    }

}
