package net.cs50.finance.controllers;

import net.cs50.finance.models.*;
import net.cs50.finance.models.dao.StockHoldingDao;
import net.cs50.finance.models.dao.StockTransactionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

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
    public String quote(String symbol, Model model) throws StockLookupException {

        // Implement quote lookup
        yahoofinance.Stock mystock = Stock.lookupStock(symbol);
        Float stockPrice = Float.parseFloat(mystock.getQuote().getPrice().toString());
        model.addAttribute("stock_price", stockPrice);

        String myName = mystock.getName();
        model.addAttribute("stock_desc", myName);

        // pass data to template
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");

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
    public String buy(String symbol, int numberOfShares, HttpServletRequest request, Model model) throws IOException {

        // implement buyshares(user, symbol, #shares)
        User user = getUserFromSession(request);

        StockHolding holding = null;
        try {
             holding = StockHolding.buyShares(user, symbol, numberOfShares);
        } catch (StockLookupException | IOException e) {
            e.printStackTrace();
        }

        stockHoldingDao.save(holding);
        // save user with UserDao
        userDao.save(user);

        List<StockTransaction> transactions= stockTransactionDao.findBySymbolAndUserId(symbol.toUpperCase(),user.getUid());
        holding=StockHolding.updateaverageprice(user,symbol,transactions);
        stockHoldingDao.save(holding);


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
    public String sell(String symbol, int numberOfShares, HttpServletRequest request, Model model) {

        // Implement sell action
        // implement sellShares(user, symbol, #shares)
        User user = getUserFromSession(request);

        StockHolding holding = null;
        try {
            holding = StockHolding.sellShares(user, symbol, numberOfShares);
        } catch (StockLookupException | IOException e) {
            e.printStackTrace();
        }

        stockHoldingDao.save(holding);
        userDao.save(user);

        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");

        return "transaction_confirm";
    }

}
