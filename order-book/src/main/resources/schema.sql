drop table if exists order_schema;
drop table if exists trade_schema;
drop table if exists wallet_schema;

create table order_schema (                                                                                                                                                                                                                                                                    
    price decimal(38,2),
    size integer not null,
    creation_date datetime(6),
    id bigint not null auto_increment,
    wallet_id bigint not null,
    state enum ('CANCELLED','CLOSED','CREATING','TRADING'),
    type enum ('PURCHASE','SALE'),
    primary key (id)
) engine=InnoDB;

create table trade_schema (
    change_money decimal(38,2),
    price decimal(38,2),
    size integer not null,
    buyer_wallet_id bigint not null,
    creation_date datetime(6),
    id bigint not null auto_increment,
    purchase_order_id bigint not null,
    sale_order_id bigint not null,
    seller_wallet_id bigint not null,
    type enum ('PURCHASE','SALE'),
    primary key (id)
) engine=InnoDB;

create table wallet_schema (
    amount_of_money decimal(38,2),
    amount_of_vibranium integer not null,
    id bigint not null auto_increment,
    primary key (id)
) engine=InnoDB;