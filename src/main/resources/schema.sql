--Category Table ---
CREATE TABLE test.category (
  category_id SERIAL PRIMARY KEY,
  category_name VARCHAR(100)  NOT NULL
);
 
 
-- Books Table ----
CREATE TABLE IF NOT EXISTS test.book (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES test.category(category_id)
);

 
 
-- Role Table ----
CREATE TABLE test.role (
  role_id SERIAL PRIMARY KEY,
  role VARCHAR(50)  NOT NULL
);
 

INSERT INTO test.role VALUES (1, 'Customer');
INSERT INTO test.role VALUES ( 2,'Admin');
 
-- User Table ----
CREATE  TABLE test.user (
  user_id SERIAL PRIMARY KEY,
  first_name VARCHAR(200)  NOT NULL,
  last_name VARCHAR(200)  NOT NULL,
   role_id BIGINT ,
  mail_id VARCHAR(200) NOT NULL,

  FOREIGN KEY (role_id) REFERENCES test.role(role_id)
);
 


 
-- order Table ----
CREATE TABLE test.order (
    order_id SERIAL PRIMARY KEY,
    user_id BIGINT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10, 2),
	FOREIGN KEY (user_id) REFERENCES test.User(user_id)
);
 
-- order_detail Table ----
CREATE TABLE test.order_detail (
    order_detail_id SERIAL PRIMARY KEY,
    order_id BIGINT,
    book_id BIGINT,
    quantity BIGINT,
	FOREIGN KEY (book_id) REFERENCES test.book(id),
	FOREIGN KEY (order_id) REFERENCES test.order(order_id)
);

--Shopping cart Table ----

CREATE TABLE test.shopping_cart (
    cart_id SERIAL PRIMARY KEY,
    user_id BIGINT
);


CREATE SEQUENCE test.cart_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

CREATE TABLE test.cart_item (
    cart_item_id SERIAL PRIMARY KEY,
    cart_id BIGINT REFERENCES test.shopping_cart(cart_id),
    book_id BIGINT REFERENCES test.book(id),
    quantity BIGINT,
    price FLOAT
);

