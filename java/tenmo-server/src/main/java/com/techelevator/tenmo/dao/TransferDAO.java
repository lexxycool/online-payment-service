package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {


    BigDecimal getSenderAccountBalance(int account_id);
    void updateReceiverAcct(int receiverAccount_id, BigDecimal amount);
    void updateSenderAcct(int senderAccount_id, BigDecimal amount);



}
