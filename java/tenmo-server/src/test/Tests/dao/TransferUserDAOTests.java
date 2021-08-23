//package dao;
//
//import com.techelevator.tenmo.controller.TenmoController;
//import com.techelevator.tenmo.dao.TransferDAO;
//import com.techelevator.tenmo.dao.TransferUserDAO;
//import com.techelevator.tenmo.model.Transfer;
//import org.junit.Before;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.sql.DataSource;
//import java.math.BigDecimal;
//
//public class TransferUserDAOTests extends DaoTests{
//
//    private JdbcTemplate jdbcTemplate;
//
//
//    private Transfer transfer;
//
//    private static final Transfer transfer_1 = new Transfer(2, 2, 2003, 2002, new BigDecimal("300"));
//    private static final Transfer transfer_2 = new Transfer(2, 2, 2003, 2002, new BigDecimal("500"));
//    private static final Transfer transfer_3 = new Transfer(1, 1, 2003, 2002, new BigDecimal("200"));
//    private static final Transfer transfer_4 = new Transfer(1, 1, 2003, 2002, new BigDecimal("100"));
//    private static final Transfer transfer_5 = new Transfer(1, 1, 2003, 2001, new BigDecimal("100"));
//
//    @Before
//    public void setup() {
//        jdbcTemplate = new JdbcTemplate(dataSource);
//
//        TransferUserDAO transfer = new TransferUserDAO();
//
//        transfer.transferMoney(1, 1, 2002, 2003, new BigDecimal("100"));
//
//
//    }
//
//
//
//}

package dao;
import com.techelevator.tenmo.dao.AccountJdbcDAO;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferUserDAO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransferUserDAOTests extends DaoTests {

    private static final Transfer TRANSFER_1= new Transfer( 2, 2, 2003, 2002, new BigDecimal("50"));
    private static final Transfer TRANSFER_2= new Transfer( 1, 1, 2002, 2003, new BigDecimal("100"));
    private static final Transfer TRANSFER_3= new Transfer( 1, 3, 2002, 2003, new BigDecimal("150"));
    private static final Transfer TRANSFER_4= new Transfer( 2, 2, 2003, 2002, new BigDecimal("200"));
    private TransferUserDAO sut;

    private Transfer testTransfer;

    @Before
    public void setup() {
        sut = new TransferUserDAO(dataSource);
        testTransfer = new Transfer(2,2,2001, 2002, new BigDecimal("500"));
    }

    @Test
    public void getAllTransfers_returns_all_transfers() {
        List<Transfer> actual = sut.getAllTransfers();
        List<Transfer> expected = new ArrayList<>();
        expected.add(TRANSFER_1);
        expected.add(TRANSFER_2);
        expected.add(TRANSFER_3);
        expected.add(TRANSFER_4);

        Assert.assertEquals(actual.size(),expected.size());
        assertTransfersMatch(expected.get(0),TRANSFER_1);
    }




    private void assertTransfersMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getAccount_from(), actual.getAccount_from());
        Assert.assertEquals(expected.getAccount_to(), actual.getAccount_to());
        Assert.assertEquals(expected.getAmount().compareTo(actual.getAmount()), 0);
        Assert.assertEquals(expected.getTransfer_status_ID(), actual.getTransfer_status_ID());
        Assert.assertEquals(expected.getTransfer_type_ID(), actual.getTransfer_type_ID(), 0.001);
    }

}