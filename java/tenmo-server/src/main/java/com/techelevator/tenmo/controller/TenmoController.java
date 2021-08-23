package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferUserDAO;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.loading.PrivateClassLoader;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    @Autowired
    AccountDAO dao;
    @Autowired
    JdbcUserDao userDao;
    @Autowired
    TransferUserDAO transferDao;
    @Autowired
    JdbcTemplate jdbcTemplate;


    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public Balance getBalance(Principal principal) {
        System.out.println(principal.getName());
        return dao.getBalance(principal.getName());
    }


    @RequestMapping(path = "/userlist", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        List<User> users = userDao.findAll();

        return users;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public void updateBalance(@RequestBody Transfer transfer) {
        int accountID = transferDao.getAccountID(transfer.getAccount_from());
        BigDecimal balance = transferDao.getSenderAccountBalance(accountID);

        // System.out.println(transfer.getAccount_from() + " " + transfer.getAccount_to() +" " + transfer.getTransfer_status_ID() + " " + transfer.getTransfer_type_ID() + " " + transfer.getAmount());
        boolean isPossible = transferDao.isThereEnoughMoney(balance, transfer.getAmount());
        if(isPossible || transfer.getTransfer_type_ID() == 1) {
            //do the transfer
            transfer.setAccount_from(accountID);
            transfer.setAccount_to(transferDao.getAccountID(transfer.getAccount_to()));
            transferDao.transferMoney(transfer.getTransfer_type_ID(), transfer.getTransfer_status_ID(),
                    transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount());
            if(transfer.getTransfer_type_ID() == 2) {
                System.out.println(userDao.updateSenderBalance(transfer.getAccount_from(), transfer.getAmount()));
                System.out.println(userDao.updateReceiverBalance(transfer.getAccount_to(), transfer.getAmount()));
            }

        }else {
            //send message that transfer failed
            System.out.println("Invalid Transfer");
        }

    }
    @RequestMapping(path = "/history",method = RequestMethod.GET)
    public String history(Principal principal){

       int userID =  transferDao.getUserID(principal.getName());
       int accountID = transferDao.getAccountID(userID);
        String sql = "select transfer_id, transfer_type_id, amount, username, account_to from transfers join accounts on (account_from = account_id) join users on (accounts.user_id = users.user_id) where account_to = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountID);
        String result = "-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To                 Amount\n" +
                "-------------------------------------------";
        while(results.next()){
            int transferID = results.getInt("transfer_id");
            BigDecimal amount = results.getBigDecimal("amount");
            String username = results.getString("username");
            int type = results.getInt("transfer_type_id");
            if(type == 2) {
                result += "\n" + transferID + "      " + "From: " + username + "                $ " + amount;
            }

        }

        sql = "select transfer_id, transfer_type_id, amount, username, account_from from transfers join accounts on (account_to = account_id) join users on (accounts.user_id = users.user_id) where account_from = ?";

        results = jdbcTemplate.queryForRowSet(sql, accountID);
        while(results.next()){
            int transferID = results.getInt("transfer_id");
            BigDecimal amount = results.getBigDecimal("amount");
            String username = results.getString("username");
            int type = results.getInt("transfer_type_id");
            if(type == 2) {
                result += "\n" + transferID + "      " + "To: " + username + "                $ " + amount;
            }
        }
        return result;
    }

    @RequestMapping(path = "/details/{id}",method = RequestMethod.GET)
    public String transactionDetails(Principal principal, @PathVariable int id){

        int userID =  transferDao.getUserID(principal.getName());
        int accountID = transferDao.getAccountID(userID);
        String sql = "select transfer_type_id, transfer_status_id , username, account_to, amount from transfers join accounts on (account_from = account_id) join users on (accounts.user_id = users.user_id) where transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);
        String result = "";
        if (results.next()) {
            result = "--------------------------------------------\n" +
                    "Transfer Details\n" +
                    "--------------------------------------------";
            String from = results.getString("username");
            sql = "select transfer_type_id, transfer_status_id , username, account_from, amount from transfers join accounts on (account_to = account_id) join users on (accounts.user_id = users.user_id) where transfer_id = ?";
            results = jdbcTemplate.queryForRowSet(sql,id);
            if(results.next()){

                String to = results.getString("username");
                int type = results.getInt("transfer_type_id");
                String typeString = "";
                if (type == 2){
                    typeString = "Send";
                }else if(type == 1) {
                    typeString = "Request";
                }
                int status = results.getInt("transfer_status_id");
                String statusString = "";
                if (status == 2){
                    statusString = "Approved";
                }else if(status == 1) {
                    statusString = "Pending";
                }
                BigDecimal amount = results.getBigDecimal("amount");
                result += "\nId: " + id;
                result += "\nFrom: " + from;
                result += "\nTo: " + to;
                result += "\nType: " + typeString;
                result += "\nStatus: " + statusString;
                result += "\nAmount: " + amount.toString();
            }

        }
        return result;
    }



    @RequestMapping(path = "/requests",method = RequestMethod.GET)
    public String requests(Principal principal){

        int userID =  transferDao.getUserID(principal.getName());
        int accountID = transferDao.getAccountID(userID);
//        String sql = "select transfer_id, transfer_type_id, amount, username, account_to from transfers join accounts on (account_from = account_id) join users on (accounts.user_id = users.user_id) where account_to = ?";
//
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountID);
        String result = "-------------------------------------------\n" +
                "Pending      Transfers\n" +
                "ID            To                 Amount\n" +
                "-------------------------------------------";
//        while(results.next()){
//            int transferID = results.getInt("transfer_id");
//            BigDecimal amount = results.getBigDecimal("amount");
//            String username = results.getString("username");
//            int type = results.getInt("transfer_type_id");
//            if(type == 1) {
//                result += "\n" + transferID + "      " + "From: " + username + "                $ " + amount;
//            }
//
//        }

        String sql = "select transfer_id, transfer_type_id, transfer_status_id, amount, username, account_to from transfers join accounts on (account_from = account_id) join users on (accounts.user_id = users.user_id) where account_to = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountID);
        while(results.next()){
            int transferID = results.getInt("transfer_id");
            BigDecimal amount = results.getBigDecimal("amount");
            String username = results.getString("username");
            int type = results.getInt("transfer_type_id");
            int status = results.getInt("transfer_status_id");
            if(type == 1 && status == 1) {
                result += "\n" + transferID + "      " + "To: " + username + "                $ " + amount;
            }
        }
        return result;
    }

    @RequestMapping(path="approve", method = RequestMethod.POST)
    public String approved(@RequestBody Integer id) {
        String sql = "select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount from transfers where transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()) {
            BigDecimal amount = results.getBigDecimal("amount");
            int account_from = results.getInt("account_to");
            int accountID = transferDao.getAccountID(account_from);
            int account_to = results.getInt("account_from");
            BigDecimal balance = transferDao.getSenderAccountBalance(account_from);
            System.out.println(balance+ " " + amount);
            boolean isPossible = transferDao.isThereEnoughMoney(balance, amount);
            if(isPossible) {
                String sql1 = "update transfers set transfer_type_id = 2, transfer_status_id = 2 where transfer_id = ?";
                jdbcTemplate.update(sql1, id);
                userDao.updateSenderBalance(account_from, amount);
                userDao.updateReceiverBalance(account_to, amount);
                return "Transfer Successful";
            }else {
                return "Invalid Transfer: Insufficient Funds";
            }
        }
        return "";
    }
    @RequestMapping(path="reject", method = RequestMethod.POST)
    public String rejected(@RequestBody Integer id) {
        String sql = "select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount from transfers where transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            String sql1 = "update transfers set transfer_status_id = 3 where transfer_id = ?";
            jdbcTemplate.update(sql1, id);
            BigDecimal amount = results.getBigDecimal("amount");
        }
        return "Transfer Rejected";
    }

    @RequestMapping(path="pending", method = RequestMethod.POST)
    public boolean pending(@RequestBody Integer id) {
        String sql = "select transfer_id from transfers where transfer_type_id = 1";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        List<Integer> pendingList = new ArrayList<>();
        while(result.next()) {
            int transferId = result.getInt("transfer_id");
            pendingList.add(transferId);

        }
        if(pendingList.contains(id)) {
            return true;
        }
       return false;
    }

//    @RequestMapping(path="/check-fund", method = RequestMethod.GET)
//    public boolean balance(Principal principal, BigDecimal amount) {
//            int accountID = transferDao.getAccountID(principal.getName());
//            BigDecimal balance = transferDao.getSenderAccountBalance(accountID);
//            return transferDao.isThereEnoughMoney(balance, amount);
//
//    }

}
