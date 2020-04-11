package net.cs50.finance.models;

import net.cs50.finance.models.util.PasswordHash;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cbay on 5/10/15.
 */

/**
 * Represents a user on our site
 */
@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    private String userName;
    private String hash;
    private double investment;

    /**
     * A collection of all the StockHoldings this user owns. The keys are stock symbols, ie "YHOO"
     */
    private Map<String, StockHolding> portfolio;

    // add cash to user class
    private double cash;

    public User(String userName, String password) {
        this.hash = PasswordHash.getHash(password);
        this.userName = userName;
        this.portfolio = new HashMap<String, StockHolding>();
        this.cash = 10000;
    }

    // empty constructor so Spring can do its magic
    public User() {}

    @NotNull
    @Column(name = "username", unique = true, nullable = false)
    public String getUserName() {
        return userName;
    }

    protected void setUserName(String userName){
        this.userName = userName;
    }

    @NotNull
    @Column(name = "hash")
    public String getHash() {
        return hash;
    }

    protected void setHash(String hash) {
        this.hash = hash;
    }

    public double getInvestment() {
        return investment;
    }

    public void setInvestment(double investment) {
        this.investment = investment;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    public Map<String, StockHolding> getPortfolio() {
        return portfolio;
    }

    private void setPortfolio(Map<String, StockHolding> portfolio) {
        this.portfolio = portfolio;
    }


    @Column(name = "cash")
    public double getCash() { return cash; }

    protected void setCash(double cash) { this.cash = cash; }

    void addHolding (StockHolding holding) throws IllegalArgumentException {

        // Ensure a holding for the symbol doesn't already exist
        if (portfolio.containsKey(holding.getSymbol())) {
            throw new IllegalArgumentException("A holding for symbol " + holding.getSymbol()
                    + " already exits for user " + getUid());
        }

        portfolio.put(holding.getSymbol(), holding);
    }

  public   void deleteholding (StockHolding holding)
    {
        if (portfolio.containsKey(holding.getSymbol()))
        {
            portfolio.replace(holding.getSymbol(),null);
            portfolio.remove(holding.getSymbol());
        }
    }
}