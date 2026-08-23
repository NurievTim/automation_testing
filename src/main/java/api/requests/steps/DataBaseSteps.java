package api.requests.steps;

import api.dao.AccountDao;
import api.dao.CustomerDao;
import api.dao.TransactionDao;
import api.database.Condition;
import api.database.DBRequest;


public class DataBaseSteps {

    private enum Table {
        CUSTOMERS("customers"),
        ACCOUNTS("accounts"),
        TRANSACTIONS("transactions");

        private final String name;

        Table(String name) {
            this.name = name;
        }
    }

    private enum Column {
        USERNAME("username"),
        ACCOUNT_ID("account_id"),
        ID("id");

        private final String name;

        Column(String name) {
            this.name = name;
        }
    }

    public static AccountDao getAccountByAccountId(int accountId) {
        return DBRequest.builder()
                .requestType(DBRequest.RequestType.SELECT)
                .table(Table.ACCOUNTS.name)
                .where(Condition.equalTo(Column.ID.name, accountId))
                .extractAs(AccountDao.class);
    }

    public static CustomerDao getCustomerByUsername(String username) {
        return DBRequest.builder()
                .requestType(DBRequest.RequestType.SELECT)
                .table(Table.CUSTOMERS.name)
                .where(Condition.equalTo(Column.USERNAME.name, username))
                .extractAs(CustomerDao.class);
    }

    public static TransactionDao getTransactionsByAccountId(int accountId) {
        return DBRequest.builder()
                .requestType(DBRequest.RequestType.SELECT)
                .table(Table.TRANSACTIONS.name)
                .where(Condition.equalTo(Column.ACCOUNT_ID.name, accountId))
                .extractAs(TransactionDao.class);
    }
}
