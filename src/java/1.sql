
-- drop first 
drop table if exists `transaction`;
drop table if exists block;
drop table if exists peer;
drop table if exists alias;
drop table if exists alias_offer ;
drop table if exists asset;
drop table if exists trade ;
drop table if exists ask_order;
drop table if exists bid_order ;
drop table if exists goods;
drop table if exists purchase;
drop table if exists account;
drop table if exists account_asset;
drop table if exists purchase_feedback;
drop table if exists purcharse_public_feedback;
drop table if exists unconfirmed_transaction;
drop table if exists asset_transfer;

-- 区块表
create table block(
	db_id int auto_increment primary key COMMENT "自增长ID",
	id BIGINT not null UNIQUE key COMMENT "主键",
	version int not null COMMENT "版本号",
	`timestamp` int not null COMMENT "时间戳",
	previous_block_id BIGINT COMMENT "前一个区块ID",
	FOREIGN KEY (previous_block_id) REFERENCES block(id) on delete CASCADE,
	total_amount BIGINT not null COMMENT "区块总金额",
	total_fee BIGINT not null COMMENT "区块总费用",
	payload_length int not null COMMENT "区块负载长度",
	generator_public_key BINARY(32) not null COMMENT "区块写入者公钥",
	previous_block_hash BINARY(32) COMMENT "前一个区块的hash值",
	cumulative_difficulty int not null COMMENT "累计难度",
	base_target BIGINT not null COMMENT "基础目标???",
	next_block_id BIGINT , FOREIGN KEY (next_block_id) REFERENCES block(id) on delete set null ,
	-- `index` int not null COMMENT "索引？",
	height int UNIQUE key not null COMMENT "区块高度",
	generation_signature BINARY(64) not null COMMENT "生成签名???",
	block_signature BINARY(64) not null COMMENT "区块签名hash值",
	payload_hash BINARY(32) not null COMMENT "区块负载hash值",
	generator_id BIGINT not null COMMENT "区块写入者的账户ID",
	nonce BIGINT not null COMMENT "生成区块的随机数",
	INDEX(generator_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 交易表
create table `transaction`(
	db_id int auto_increment primary key COMMENT "自增长ID",
	id BIGINT UNIQUE key COMMENT "主键",
	deadline smallint not null COMMENT "交易的截止日期",
	sender_public_key BINARY(32) not null COMMENT "交易发出者的public key",
	recipent_id BIGINT null COMMENT "收款人ID",
	amount BIGINT not null COMMENT "金额",
	fee BIGINT not null COMMENT "费用",
	height int not null COMMENT "高度",
	block_id BIGINT not null COMMENT "交易对应的区块ID",
	FOREIGN key(block_id) REFERENCES block(id) on DELETE CASCADE,
	signature BINARY(64) not null COMMENT "交易签名",
	`timestamp` int not null COMMENT "交易时间戳",
	type TINYINT not null COMMENT "交易类型",
	subtype TINYINT not null COMMENT "交易子类型",
	sender_id BIGINT not null COMMENT "交易发送者账户ID",
	block_timestamp int not null COMMENT "区块的时间戳", 
	full_hash binary(32) not null COMMENT "交易的完整hash值",
	referenced_transaction_full_hash binary(32) UNIQUE key COMMENT "引用的交易完整的hash",
	attachment_bytes TINYBLOB COMMENT "附属内容",
	version TINYINT not null COMMENT "版本",
	has_message TINYINT not null default 0 COMMENT "是否有消息，默认false", 
	has_encrypted_message TINYINT not null default 0 COMMENT "是否有加密消息，默认false",
	has_public_key_announcement TINYINT not null default 0 COMMENT "是否有公钥声明，默认false",
	ec_block_height int default null,
	ec_block_id BIGINT default null,
	has_encrypttoself_message TINYINT not null default 0 COMMENT "是否有加密对自己的消息，默认false",
	INDEX(sender_id),
	INDEX(recipent_id),
	INDEX(`full_hash`),
	INDEX(block_timestamp desc)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 节点表
create table peer(
	address VARCHAR(256) primary key not null
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

update `TRANSACTION` set version = 0;
update `TRANSACTION` set has_message = 1 where type = 1 and subtype = 0;

-- 别名
create table alias(
	db_id int auto_increment primary key COMMENT "自增主键", 
	id BIGINT not null COMMENT "逻辑主键", 
	account_id BIGINT not null COMMENT "账户ID", 
	alias_name varchar(256) not null COMMENT "别名",
	alias_name_lower varchar(256) not null COMMENT "别名小写",
	alias_uri VARCHAR(256) not null COMMENT "别名uri", 
	`timestamp` int not null COMMENT "时间戳", 
	height int not null COMMENT "高度", 
	latest TINYINT not null default 1 COMMENT "最新，默认为true",	
	UNIQUE INDEX(id , height desc),
	index(account_id , height),
	INDEX(alias_name_lower)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 别名提供
create table alias_offer(
	db_id int auto_increment not null primary key,
	id BIGINT not null , 
	price BIGINT not null COMMENT "别名价格",
	buyer_id BIGINT COMMENT "卖家id",
	height int not null COMMENT "高度",
	latest TINYINT default 1 not null COMMENT "默认为最新", 
	UNIQUE INDEX(id , height desc)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 资产

create table asset(
	db_id int auto_increment primary key not null , 
	id BIGINT UNIQUE key not null, 
	account_id BIGINT not null COMMENT "账户ID", 
	name VARCHAR(256) not null COMMENT "名字", 
	description varchar(256) COMMENT "描述", 
	quantity BIGINT not null COMMENT "资产数量", 
	decimals TINYINT not null COMMENT "资产小数位",
	height int not null COMMENT "资产高度",
	INDEX(account_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 交易

create table trade (
	db_id int auto_increment primary key not null , 
	asset_id BIGINT not null , 
	block_id BIGINT not null COMMENT "区块ID", 
	ask_order_id BIGINT not null COMMENT "卖出订单ID", 
	bid_order_id BIGINT not null COMMENT "买入订单ID", 
	ask_order_height int not null COMMENT "卖出订单高度", 
	bid_order_height int not null COMMENT "买入订单高度",
	seller_id BIGINT not null COMMENT "卖出ID",
	buyer_id BIGINT not null COMMENT "买入ID", 
	quantity BIGINT not null COMMENT "交易数量",
	price BIGINT not null COMMENT "价格",
	`timestamp` int not null COMMENT "时间戳",
	height int not null COMMENT "高度",
	unique INDEX(ask_order_id,bid_order_id),
	INDEX(asset_id , height desc),
	INDEX(seller_id,height desc),
	INDEX(buyer_id ,height desc)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 卖单 

CREATE TABLE ask_order(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null,
	account_id BIGINT not null comment "账户ID",
	asset_id BIGINT not null COMMENT "资产ID",
	price BIGINT not null  COMMENT "价格",    
	quantity BIGINT not null COMMENT "数量", 
	creation_height int not null COMMENT "创建高度", 
	height int not null COMMENT "高度", 
	latest TINYINT default 1 COMMENT "是否为最新记录，默认为最新记录", 
	UNIQUE INDEX(id , height desc),
	INDEX(account_id , height),
	INDEX(asset_id,price)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 买单 

CREATE TABLE bid_order(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null,
	account_id BIGINT not null comment "账户ID",
	asset_id BIGINT not null COMMENT "资产ID",
	price BIGINT not null  COMMENT "价格",    
	quantity BIGINT not null COMMENT "数量", 
	creation_height int not null COMMENT "创建高度", 
	height int not null COMMENT "高度", 
	latest TINYINT default 1 COMMENT "是否为最新记录，默认为最新记录", 
	UNIQUE INDEX(id , height desc),
	INDEX(account_id , height),
	INDEX(asset_id,price)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 商品

create table goods(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null , 
	seller_id BIGINT not null COMMENT "卖方ID", 
	`name` varchar(256) not null COMMENT "商品名称", 
	description varchar(256) COMMENT "商品描述", 
	tags varchar(256) COMMENT "商品标签", 
	`timestamp` int not null COMMENT "时间戳", 
	quantity int not null COMMENT "数量", 
	price BIGINT not null COMMENT "价格", 
	delisted TINYINT not null COMMENT "被下架", 
	height int not null COMMENT "高度", 
	latest TINYINT default 1 COMMENT "是否为最新记录，默认为最新记录" ,
	UNIQUE INDEX(id,height desc),
	INDEX(seller_id,`name`),
	INDEX(`TIMESTAMP`,height)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 购买

create table purchase(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null , 
	buyer_id BIGINT not null COMMENT "买方ID",
	goods_id BIGINT not null COMMENT "商品ID",
	seller_id BIGINT not null COMMENT "卖方ID",
	quantity int not null COMMENT "数量",
	price BIGINT not null COMMENT "价格",
	deadline int not null COMMENT "截止日期",
	note varchar(256) COMMENT "日志",
	nonce BINARY(32) COMMENT "随机数",
	`timestamp` int not null COMMENT "时间戳",
	pending TINYINT not null COMMENT "是否pending", 
	goods varchar(256) COMMENT "商品",
	goods_nonce BINARY(32) COMMENT "商品nonce",
	refund_note varchar(256) COMMENT "回款日志",
	refund_nonce BINARY(32) COMMENT "回款nonce",
	has_feedback_notes TINYINT not null default 0 COMMENT "是否有回馈",
	has_public_feedbacks TINYINT not null default 0 COMMENT "是否有公开回馈",
	discount BIGINT not null COMMENT "折扣",
	refund BIGINT not null COMMENT "回款",
	height int not null COMMENT "高度",
	latest TINYINT default 1 COMMENT "是否为最新记录，默认为最新记录" ,
	UNIQUE index(id,height),
	INDEX(buyer_id,height),
	INDEX(seller_id,height),
	INDEX(deadline,height)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 账户信息表

create table account(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null , 
	creation_height int not null COMMENT "账户创建高度",  
	public_key BINARY(32) COMMENT "账户公钥",
	key_height int COMMENT "公钥高度",
	balance bigint not null COMMENT "账户余额",
	unconfirmed_balance BIGINT not null COMMENT "未确认余额",
	forged_balance bigint not null COMMENT "铸造中的金额",
	`name` varchar(256) COMMENT "账户名称",
	description varchar(256) COMMENT "描述",
	current_leasing_height_from int COMMENT "从当前租赁高度",
	current_leasing_height_to int COMMENT "到当前租赁高度",
	current_leasing_id BIGINT null COMMENT "租赁人ID",
	current_lessee_id BIGINT null COMMENT "承租人ID",
	next_leasing_height_from int COMMENT "从下一个租赁高度",
	next_leasing_height_to int COMMENT "到下一个租赁高度",
	next_leasing_id BIGINT null COMMENT "下一个租赁人ID",
	height int not null COMMENT "高度", 
	latest TINYINT default 1 COMMENT "是否为最新记录，默认为最新记录" ,
	UNIQUE KEY (id , height desc)
)ENGINE = INNODB DEFAULT CHARSET=utf8;

-- 账户资产
create table account_asset(
	db_id int auto_increment not null PRIMARY KEY ,
	account_id BIGINT not null ,
	asset_id BIGINT not null COMMENT "账户ID",
	quantity BIGINT not null COMMENT "数量",
	unconfirmed_quantity BIGINT not null COMMENT "未确认数量",
	height int not null COMMENT "高度",
	latest TINYINT default 1 COMMENT "是否为最新记录，默认为最新记录" ,
	UNIQUE INDEX(account_id,asset_id,height)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

-- 购买回馈信息

create table purchase_feedback(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null , 
	feedback_data varchar(256) not null , 
	feedback_nonce BINARY(32) not null,
	height int not null,
	latest TINYINT default 1 COMMENT "是否为最新记录，默认为最新记录",
	UNIQUE INDEX(id,height)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

-- 公开购买回馈信息
create table purcharse_public_feedback(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null , 
	public_feedback varchar(256),
	height int not null,
	latest TINYINT default 1 COMMENT "是否为最新记录，默认为最新记录",
	UNIQUE KEY(id,height)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

-- 未确认交易
create table unconfirmed_transaction(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null UNIQUE KEY, 
	expiration int not null COMMENT "交易过期截止时间", 
	transaction_height int not null COMMENT "交易高度",
	fee_per_byte BIGINT not null COMMENT "交易每个字节的费用",
	`timestamp` int not null COMMENT "时间戳",
	transaction_bytes varchar(256) not null COMMENT "交易字节大小", 
	height int not null COMMENT "交易高度",
	INDEX(TRANSACTION_height asc, fee_per_byte desc , `timestamp` asc)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

-- 资产转移
create table asset_transfer(
	db_id int auto_increment not null PRIMARY KEY ,
	id BIGINT not null UNIQUE KEY, 
	asset_id BIGINT not null COMMENT "资产ID",
	sender_id BIGINT not null COMMENT "发送者ID",
	recipient_id BIGINT not null COMMENT "收款人ID",
	quantity BIGINT not null COMMENT "数量",
	`timestamp` int not null COMMENT "时间戳",
	height int not null COMMENT "高度",
	INDEX(asset_id,height desc),
	INDEX(sender_id,height desc),
	INDEX(recipient_id,height desc)
)ENGINE=INNODB DEFAULT CHARSET=utf8;


