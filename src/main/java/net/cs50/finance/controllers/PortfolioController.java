package net.cs50.finance.controllers;

import net.cs50.finance.models.Stock;
import net.cs50.finance.models.StockHolding;
import net.cs50.finance.models.StockLookupException;
import net.cs50.finance.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class PortfolioController extends AbstractFinanceController {

    @RequestMapping(value = "/portfolio")
    public String portfolio(HttpServletRequest request, Model model){

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

            // make a hashmap to store the row
            HashMap<String, String> row = new HashMap<String, String>();

            // get symbol
            String symbol = holding.getSymbol();

            // get sharesOwned
            int shareNum = holding.getSharesOwned();
            double investment=Math.round(holding.getInvestment()*100.00)/100.00;

            // use symbol to lookup stock(returns symbol,name,price)
            try {
                myLookup = Stock.lookupStock(symbol);
            }
            catch (StockLookupException e) {
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
            if(holding.getSharesOwned()==0)
            {
                profit = Math.round((holding.getClosevalue()-investment) * 100.00) / 100.00;
               // System.out.println(holding.getClosevalue());

            }
           else {  profit = Math.round((displayValue-investment) * 100.00) / 100.00;}


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

        // get user cash and add to array
        double cash = user.getCash();

        // round cash
        double displayCash = Math.round((cash) * 100.000) / 100.000;;
        String cashString = String.valueOf(displayCash);

        // pass array to template
        model.addAttribute("stockParts", stockParts);
        model.addAttribute("cash", cashString);

        model.addAttribute("title", "Portfolio");
        model.addAttribute("portfolioNavClass", "active");

        return "portfolio";
    }

}