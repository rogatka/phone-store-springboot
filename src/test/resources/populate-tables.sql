insert into users(id, first_name, last_name)
VALUES(1, 'Michael','Cordon'),
(2, 'John','Gordon'),
(3, 'Chael','Drew');

insert into accounts(id, user_id, amount)
VALUES(1, 1, 24500.57),
(2, 2, 112000.45);

insert into phones(id, model, description, phone_count, price) values
(1,'Samsung S20', 'Camera 120 Mpx', 1000, 699.99),
(2,'Apple Iphone X20', 'Camera 20 Mpx', 5000, 1699.99),
(3,'Xiaomi Mi50', 'Camera 200 Mpx', 15000, 399.99);

insert into orders(id, account_id, status) values
(1, 1, 'NOT_STARTED'),
(2, 1, 'PROCESSING'),
(3, 1, 'READY'),
(4, 2, 'NOT_STARTED'),
(5, 2, 'READY');

insert into order_cards(id, order_id, phone_id, item_count) values
(1, 1, 1, 2),
(2, 1, 3, 3),
(3, 2, 1, 1),
(4, 2, 2, 2),
(5, 3, 2, 1),
(6, 4, 1, 5),
(7, 4, 2, 3),
(8, 5, 3, 6),
(9, 5, 2, 4);

insert into order_status_history(id,order_id, order_status, time_stamp) values
(1, 1, 'NOT_STARTED', '2020-05-20 14:44:11'),
(2, 2, 'NOT_STARTED', '2020-05-20 10:43:11'),
(3, 2, 'PROCESSING', '2020-05-20 17:27:31'),
(4, 3, 'NOT_STARTED', '2020-05-10 12:12:41'),
(5, 3, 'PROCESSING', '2020-05-11 19:36:11'),
(6, 3, 'READY', '2020-05-21 10:16:41'),
(7, 4, 'NOT_STARTED', '2020-05-21 12:34:51'),
(8, 5, 'NOT_STARTED', '2020-05-21 16:24:11'),
(9, 5, 'PROCESSING', '2020-05-21 20:13:53'),
(10, 5, 'READY', '2020-05-22 10:17:43');
