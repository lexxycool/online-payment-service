package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int transfer_type_ID;
    private int transfer_status_ID;
    private int account_from;
    private int account_to;
    private BigDecimal amount;

    public Transfer(int transfer_type_ID, int transfer_status_ID, int account_from, int account_to, BigDecimal amount) {
        this.transfer_type_ID = transfer_type_ID;
        this.transfer_status_ID = transfer_status_ID;
        this.account_from = account_from;
        this.account_to = account_to;
        this.amount = amount;
    }



    public int getTransfer_type_ID() {
        return transfer_type_ID;
    }

    public void setTransfer_type_ID(int transfer_type_ID) {
        this.transfer_type_ID = transfer_type_ID;
    }

    public int getTransfer_status_ID() {
        return transfer_status_ID;
    }

    public void setTransfer_status_ID(int transfer_status_ID) {
        this.transfer_status_ID = transfer_status_ID;
    }

    public int getAccount_from() {
        return account_from;
    }

    public void setAccount_from(int account_from) {
        this.account_from = account_from;
    }

    public int getAccount_to() {
        return account_to;
    }

    public void setAccount_to(int account_to) {
        this.account_to = account_to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}