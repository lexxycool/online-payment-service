package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferClient {
    private int transferTypeID;
    private int transferStatusID;
    private int account_from;
    private int account_to;
    private BigDecimal amount;

    public TransferClient(int transferTypeID, int transferStatusID, int account_from, int account_to, BigDecimal amount) {
        this.transferTypeID = transferTypeID;
        this.transferStatusID = transferStatusID;
        this.account_from = account_from;
        this.account_to = account_to;
        this.amount = amount;
    }


    public int getTransferTypeID() {
        return transferTypeID;
    }

    public void setTransferTypeID(int transferTypeID) {
        this.transferTypeID = transferTypeID;
    }

    public int getTransferStatusID() {
        return transferStatusID;
    }

    public void setTransferStatusID(int transferStatusID) {
        this.transferStatusID = transferStatusID;
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
