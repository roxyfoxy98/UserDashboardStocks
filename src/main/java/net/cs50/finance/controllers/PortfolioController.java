package net.cs50.finance.controllers;

import net.cs50.finance.WebApplicationConfig;
import net.cs50.finance.models.Stock;
import net.cs50.finance.models.StockHolding;
import net.cs50.finance.models.StockLookupException;
import net.cs50.finance.models.User;
import net.cs50.finance.models.dao.HibernateUtil;
import net.cs50.finance.models.dao.StockHoldingDao;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.management.Query;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static org.hibernate.jpa.AvailableSettings.PERSISTENCE_UNIT_NAME;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class PortfolioController extends AbstractFinanceController {

    @Autowired
    EntityManagerFactory emFactory;
    @Autowired
    StockHoldingDao holdingdao;

    @Autowired
    SessionFactory sessionFactory;


    @RequestMapping(value = "/portfolio")
    public String portfolio(HttpServletRequest request, Model model) {

        // Implement portfolio display
        // get session id
        yahoofinance.Stock myLookup = null;

        User user = getUserFromSession(request);

        // get portfolio
        Map<String, StockHolding> portfolio = user.getPortfolio();

        // make storage for individual parts of row
        HashMap<String, HashMap<String, String>> stockParts = new HashMap<String, HashMap<String, String>>();

        // Iterate over portfolio
        for (StockHolding holding : portfolio.values()) {
            if (holding.getSharesOwned() != 0) {
                // make a hashmap to store the row
                HashMap<String, String> row = new HashMap<String, String>();

                // get symbol
                String symbol = holding.getSymbol();

                // get sharesOwned
                int shareNum = holding.getSharesOwned();
                double investment = Math.round(holding.getInvestment() * 100.00) / 100.00;

                // use symbol to lookup stock(returns symbol,name,price)
                try {
                    myLookup = Stock.lookupStock(symbol);
                } catch (StockLookupException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double profit;
                // get price
                double price = Double.parseDouble(myLookup.getQuote().getPrice().toString());

                // get totalValue
                double totalValue = price * shareNum;

                // use Stock.toString() to concatenate name and symbol
                String nameString = myLookup.toString();

                // round price, total value to display 2 decimals
                double displayPrice = Math.round(price * 100.00) / 100.00;
                double displayValue = Math.round(totalValue * 100.00) / 100.00;
                if (holding.getSharesOwned() == 0) {
                    profit = Math.round((holding.getClosevalue() - investment) * 100.00) / 100.00;
                    // System.out.println(holding.getClosevalue());

                } else {
                    profit = Math.round((displayValue - investment) * 100.00) / 100.00;
                }


                // put stockParts into HashMap
                row.put("nameString", nameString);
                row.put("symbol", symbol);
                row.put("shareNum", String.valueOf(shareNum));
                row.put("price", String.valueOf(displayPrice));
                row.put("totalValue", String.valueOf(displayValue));
                row.put("totalInvestment", String.valueOf(investment));
                row.put("ProfitLoss", String.valueOf(profit));

                stockParts.put(symbol, row);
            }
        }
        // get user cash and add to array
        double cash = user.getCash();

        // round cash
        double displayCash = Math.round((cash) * 100.000) / 100.000;
        ;
        String cashString = String.valueOf(displayCash);

        // pass array to template
        model.addAttribute("stockParts", stockParts);
        model.addAttribute("cash", cashString);

        model.addAttribute("title", "Portfolio");
        model.addAttribute("portfolioNavClass", "active");

        return "portfolio";
    }


    @RequestMapping(value = "/portfolio/sellstock", method = RequestMethod.POST, params = "action=sellstocks")
    public String sellSymbolStocks(HttpServletRequest request, Model model, @RequestParam String idsymbol) throws Exception {

        System.out.println(idsymbol);
        User user = getUserFromSession(request);
        int id = user.getUid();
        System.out.println(id);

        StockHolding holdingtest = holdingdao.findBySymbolAndOwnerId(idsymbol, id);
        System.out.println(holdingtest);
        int numberOfShares = holdingtest.getSharesOwned();
        try {
            holdingtest = StockHolding.sellShares(user, idsymbol, numberOfShares);
        } catch (Exception ex2) {
            throw new Exception("Please insert a valid symbol for the portfolio");

        }

        holdingdao.save(holdingtest);

//todo: refactor

        yahoofinance.Stock myLookup = null;

        Map<String, StockHolding> portfolio = user.getPortfolio();

        // make storage for individual parts of row
        HashMap<String, HashMap<String, String>> stockParts = new HashMap<String, HashMap<String, String>>();

        // Iterate over portfolio
        for (StockHolding holding : portfolio.values()) {
            if (holding.getSharesOwned() != 0) {
                // make a hashmap to store the row
                HashMap<String, String> row = new HashMap<String, String>();

                // get symbol
                String symbol = holding.getSymbol();

                // get sharesOwned
                int shareNum = holding.getSharesOwned();
                double investment = Math.round(holding.getInvestment() * 100.00) / 100.00;

                // use symbol to lookup stock(returns symbol,name,price)
                try {
                    myLookup = Stock.lookupStock(symbol);
                } catch (StockLookupException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                double profit;
                // get price
                double price = Double.parseDouble(myLookup.getQuote().getPrice().toString());

                // get totalValue
                double totalValue = price * shareNum;

                // use Stock.toString() to concatenate name and symbol
                String nameString = myLookup.toString();

                // round price, total value to display 2 decimals
                double displayPrice = Math.round(price * 100.00) / 100.00;
                double displayValue = Math.round(totalValue * 100.00) / 100.00;
                if (holding.getSharesOwned() == 0) {
                    profit = Math.round((holding.getClosevalue() - investment) * 100.00) / 100.00;
                    // System.out.println(holding.getClosevalue());

                } else {
                    profit = Math.round((displayValue - investment) * 100.00) / 100.00;
                }


                // put stockParts into HashMap
                row.put("nameString", nameString);
                row.put("symbol", symbol);
                row.put("shareNum", String.valueOf(shareNum));
                row.put("price", String.valueOf(displayPrice));
                row.put("totalValue", String.valueOf(displayValue));
                row.put("totalInvestment", String.valueOf(investment));
                row.put("ProfitLoss", String.valueOf(profit));

                stockParts.put(symbol, row);
            }
        }

        // get user cash and add to array
        double cash = user.getCash();

        // round cash
        double displayCash = Math.round((cash) * 100.000) / 100.000;
        ;
        String cashString = String.valueOf(displayCash);

        // pass array to template
        model.addAttribute("stockParts", stockParts);
        model.addAttribute("cash", cashString);

        model.addAttribute("title", "Portfolio");
        model.addAttribute("portfolioNavClass", "active");

        return "portfolio";
    }


}