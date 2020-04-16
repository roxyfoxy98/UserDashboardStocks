package net.cs50.finance.controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import net.cs50.finance.models.*;
import net.cs50.finance.models.dao.StockHoldingDao;
import net.cs50.finance.models.dao.StockTransactionDao;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import com.google.gson.Gson;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static net.cs50.finance.models.Stock.lookupStock;
import static net.cs50.finance.models.StockHolding.buyShares;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class StockController extends AbstractFinanceController {

    @Autowired
    StockHoldingDao stockHoldingDao;

    @Autowired
    StockTransactionDao stockTransactionDao;

    @RequestMapping(value = "/quote", method = RequestMethod.GET)
    public String quoteForm(Model model) {

        // pass data to template
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");
        return "quote_form";
    }

    @RequestMapping(value = "/quote", method = RequestMethod.POST)
    public String quote(String symbol, Model model) throws Exception {


        // Implement quote lookup
        yahoofinance.Stock mystock = Stock.lookupStock(symbol);
        Calendar start=Calendar.getInstance();
        Calendar end=Calendar.getInstance();

        start.add(Calendar.MONTH,-1);
        end.add(Calendar.MONTH,1);

       yahoofinance.Stock history= YahooFinance.get(symbol, start, end, Interval.DAILY);
       System.out.println(history.getHistory(start,end));

       List<HistoricalQuote> historicalQuoteList= history.getHistory(start,end, Interval.DAILY);
       List <HistoricalQuoteDTO> listforjson=new ArrayList<>();
       for (HistoricalQuote hq : historicalQuoteList)
       { SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
           String dateStr = dateFormat.format(hq.getDate().getTime());
            HistoricalQuoteDTO hqdto= new HistoricalQuoteDTO(dateStr,hq.getOpen()+"", hq.getHigh()+"",hq.getLow()+"", hq.getClose()+"");
           listforjson.add(hqdto);


       }

        String jsonlistforhtml = new Gson().toJson(listforjson);

        System.out.println(listforjson);
        if (mystock==null)
        {
            throw new Exception( "Please check the Quote Symbol and try again");
        }
        Float stockPrice = Float.parseFloat(mystock.getQuote().getPrice().toString());
        model.addAttribute("stock_price", stockPrice);

        String myName = mystock.getName();
        model.addAttribute("stock_desc", myName);
        // pass data to template
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");
        model.addAttribute("history",listforjson);
        return "quote_display";
    }

    @RequestMapping(value = "/buy", method = RequestMethod.GET)
    public String buyForm(Model model) {

        model.addAttribute("title", "Buy");
        model.addAttribute("action", "/buy");
        model.addAttribute("buyNavClass", "active");
        return "transaction_form";
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public String buy(String symbol, String numberOfShares, HttpServletRequest request, Model model) throws Exception {

        // implement buyshares(user, symbol, #shares)
        User user = getUserFromSession(request);
        if (symbol=="" || symbol==" ")
        {
            throw new Exception("Please insert a valid symbol");
        }
        StockHolding holding = null;

        try {
            Integer.parseInt(numberOfShares);
        } catch (Exception exc1) {
            throw new Exception ("Please insert a NUMBER of shares");
        };


            try {

                holding = StockHolding.buyShares(user, symbol, Integer.parseInt(numberOfShares));
            } catch (StockLookupException | IOException e) {
                e.printStackTrace();
            }

            stockHoldingDao.save(holding);
            // save user with UserDao
            userDao.save(user);




            model.addAttribute("title", "Buy");
            model.addAttribute("action", "/buy");
            model.addAttribute("buyNavClass", "active");

            return "transaction_confirm";
        }


    @RequestMapping(value = "/sell", method = RequestMethod.GET)
    public String sellForm(Model model) {
        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");
        return "transaction_form";
    }

    @RequestMapping(value = "/sell", method = RequestMethod.POST)
    public String sell(String symbol, String numberOfShares, HttpServletRequest request, Model model) throws Exception {

        // Implement sell action
        // implement sellShares(user, symbol, #shares)

        if (symbol=="" || symbol==" ")
        {
            throw new Exception("Please insert a valid symbol");
        }
        User user = getUserFromSession(request);
        int id= user.getUid();

        StockHolding holdingtest=stockHoldingDao.findBySymbolAndOwnerId(symbol,id);
if (holdingtest==null)
{
    throw new Exception("Please check the symbol to be suitable for your portfolio");
}

        try {
            Integer.parseInt(numberOfShares);
        } catch (Exception exc1) {
            throw new Exception ("Please insert a NUMBER of shares");
        };

        StockHolding holding=null;

        try {
            holding = StockHolding.sellShares(user, symbol, Integer.parseInt(numberOfShares));
        } catch (Exception ex2) { throw new Exception("Please insert a valid symbol for the portfolio");


        }



        stockHoldingDao.save(holding);
        userDao.save(user);



        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");

        return "transaction_confirm";
    }

}
