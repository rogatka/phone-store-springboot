-- drop database if exists phoneStoreDb;
-- create database phoneStoreDb;

-- drop user 'mysql'@'localhost';
-- CREATE USER 'mysql'@'localhost' identified by 'mysql';
-- GRANT ALL PRIVILEGES ON phonestoredb.* To 'mysql'@'localhost';

-- use phoneStoreDb;

create table phones(
id bigint auto_increment not null,
model varchar(30) not null check (model != ''),
description varchar(200) default '',
phone_count bigint not null check (phone_count >= 0),
price decimal(20,2) not null check(price >= 0),
primary key (id));

create table users(
id bigint auto_increment not null,
first_name varchar(50) not null check (first_name != ''),
last_name varchar(50) not null check (last_name != ''),
primary key (id));

create table accounts(
id bigint auto_increment not null,
user_id bigint unique not null,
amount decimal(20,2) not null check (amount >= 0),
primary key (id),
constraint fk_account_user foreign key (user_id) references users(id));

create table orders(
id bigint auto_increment not null,
account_id bigint not null,
status VARCHAR(30) default null,
total_sum decimal(20,2) default null check (total_sum >= 0),
primary key (id),
constraint fk_order_account foreign key (account_id) references accounts(id) on delete cascade);

create table order_cards(
id bigint auto_increment not null,
order_id bigint,
phone_id bigint,
item_count bigint not null check (item_count > 0),
primary key (id),
constraint fk_card_order foreign key (order_id) references orders(id) on delete cascade,
constraint fk_card_phone foreign key (phone_id) references phones(id) on delete set null);

create table order_status_history(
id bigint auto_increment not null,
order_id bigint not null,
order_status varchar(30) not null,
time_stamp timestamp not null,
primary key (id),
constraint fk_history_order foreign key (order_id) references orders(id) on delete cascade);
