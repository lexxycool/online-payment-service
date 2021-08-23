TRUNCATE  users, accounts, transfer_statuses, transfer_types, transfers CASCADE;

INSERT INTO users (user_id, username, password_hash)
VALUES (1001, 'user', 'password'),
        (1002, 'Manning', 'colts'),
        (1003, 'Brady', 'patriots');


INSERT INTO accounts (account_id, user_id, balance)
values (2001, 1001, 350),
        (2002, 1002, 999),
        (2003, 1003, 750);


INSERT INTO transfer_statuses (transfer_status_id, transfer_status_desc)
VALUES (1, 'Pending'),
       (2, 'Approved'),
       (3, 'Rejected');


INSERT INTO transfer_types (transfer_type_id, transfer_type_desc)
VALUES (1, 'Request'),
       (2, 'Send');

INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (3001, 2, 2, 2003, 2002, 50),
        (3002, 1, 1, 2002, 2003, 100),
        (3003, 1, 3, 2002, 2003, 150),
        (3004, 2, 2, 2003, 2002, 200);
