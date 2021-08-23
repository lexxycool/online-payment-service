package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.controller.TenmoController;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class TransferUserDAO implements TransferDAO{
    JdbcTemplate jdbcTemplate;
    TenmoController tenmoController = new TenmoController();


    public TransferUserDAO(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    //getting the sender's account balance
   @Override
    public BigDecimal getSenderAccountBalance(int account_id) {
        BigDecimal balance = new BigDecimal("0.00");
        String sql = "select balance from accounts join users on accounts.user_id = " +
                "users.user_id where account_id = ? ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, account_id);
        if(result.next()) {
            balance = (result.getBigDecimal("balance"));
        }
        return balance;
    }


    public boolean isThereEnoughMoney(BigDecimal balance, BigDecimal transferAmount) {

        if(balance.compareTo(transferAmount) >= 0) {
            return true;
        }else {
            return false;
        }

    }

    public void transferMoney(int type, int status, int account_from, int account_to, BigDecimal amount) {
        String sql = "insert into transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount )" +
                "values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, type, status, account_from, account_to, amount);
    }

    public int getAccountID(int  user_id) {
        int id = 0;
        String sql = "select account_id from accounts " +
                "where user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, user_id);
        if(result.next()) {
            id = result.getInt("account_id");
        }
        return id;
    }

    public int getUserID(String username){
        int id = 0;
        String sql = "select user_id from users where username = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
        if(result.next()){
            id = result.getInt("user_id");
        }
        return id;
    }
    public List<Transfer> getAllTransfers(){
        String sql = "select transfer_type_id, transfer_status_id, account_from, account_to, amount from transfers";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        List<Transfer> transfers = new ArrayList<>();
        while (result.next()){
            BigDecimal amount = result.getBigDecimal("amount");
           int account_from = result.getInt("account_from");
            int type_id = result.getInt("transfer_type_id");
            int account_to = result.getInt("account_to");
            int status_id = result.getInt("transfer_status_id");
            Transfer transfer = new Transfer(type_id, status_id, account_from, account_to, amount);
            transfers.add(transfer);
        }
        return transfers;
    }



    //if the sender account balance is greater than the amount to transfer
    //get the amount you want to transfer
  /*  @Override
    public BigDecimal transferMoney(int senderAccount_id, int receiverAccount_id, BigDecimal amount) {
        BigDecimal amountToTransfer = null;
        String sql = "select amount from transfers where account_from = ? ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, senderAccount_id);
        if(result.next()) {
            amountToTransfer.add(result.getBigDecimal("amount"));

       }
        return amountToTransfer;
    }*/


    //update receiver acct by adding the amount to the account balance
    @Override
    public void updateReceiverAcct(int receiverAccount_id, BigDecimal amount) {

    }


    //update sender acct by subtracting the amount from the account balance
    @Override
    public void updateSenderAcct(int senderAccount_id, BigDecimal amount) {

    }

/*    @Override
    public List<Transfer> receivedTransfers(int accountID) {
        String sql = "select transfer_id, amount, username, account_to from transfers join accounts on (account_from = account_id) join users on (accounts.user_id = users.user_id) where account_to = ?";
        List<Transfer> transfers = new ArrayList<>();

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountID);
        if(results.next()){
            int transferID = results.getInt("transfer_id");
            int transfer_type_id = results.getInt("transfer_type_id");
            int transfer_status_id = results.getInt("transfer_status_id");
            int account_

        }
        return null;
    }*/


}
