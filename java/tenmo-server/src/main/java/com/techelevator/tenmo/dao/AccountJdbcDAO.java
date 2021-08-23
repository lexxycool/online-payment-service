package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class AccountJdbcDAO implements AccountDAO{
    JdbcTemplate jdbcTemplate;

    public AccountJdbcDAO(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }


    @Override
    public Balance getBalance(String user) {

        Balance balance = new Balance();
        //make call to the database
        String sql = "select balance from accounts join users on accounts.user_id = users.user_id where username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,user);
        if (results.next()){
            balance.setBalance(results.getBigDecimal("balance"));
        }


        return balance;
    }





}
