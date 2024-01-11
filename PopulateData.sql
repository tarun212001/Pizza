-- Gayatri Tatineni, Tarun Prathipati
use Pizzeria;
-- -- BasePrice Table

INSERT INTO `baseprice` (
	`baseprice_size`,
    `baseprice_crust_type`,
    `baseprice_retail_price`,
    `baseprice_market_price`
)
VALUES
    (
        "Small",
        "Thin",
        3,
        0.5
    ),
    (
        "Small",
        "Original",
        3,
        0.75
    ),
    (
        "Small",
        "Pan",
        3.5,
        1
    ),
    (
        "Small",
        "Gluten-Free",
        4,
		2
    ),
    (
    "Medium",
    "Thin",
    5,
    1
    ),
    (
    "Medium",
    "Original",
    5,
    1.5
    ),
    (
    "Medium",
    "Pan",
    6,
    2.25
    ),
    (
    "Medium",
    "Gluten-Free",
    6.25,
    3
    ),
    (
    "Large",
    "Thin",
    8,
    1.25
    ),
    (
    "Large",
    "Original",
    8,
    2
    ),
    (
    "Large",
    "Pan",
    9,
    3
    ),
    (
    "Large",
    "Gluten-Free",
    9.5,
    4
    ),
    (
    "XLarge",
    "Thin",
    10,
    2
    ),
    (
    "XLarge",
    "Original",
    10,
    3
    ),
    (
    "XLarge",
    "Pan",
    11.5,
    4.5
    ),
    (
    "XLarge",
    "Gluten-Free",
    12.5,
    6
    )
    ;

-- Discount table

Insert into `discount` (
 `discount_ID`, `discount_name`, `discount_amount`, `discount_is_percent`
)
Values
(1,"Employee",
15,
TRUE
),
(2,"Lunch Special Medium",
1.00,
FALSE
),
(3,"Lunch Special Large",
2.00,
FALSE
),
(4,"Specialty Pizza",
1.50,
FALSE
),
(5,"Happy Hour",
10,
TRUE
),
(6,"Gameday Special",
20,
TRUE
)
;

Insert into `topping` (
`topping_ID`,`topping_name`,`topping_retail_price`, `topping_market_price`,
 `topping_current_inventory`,`topping_minimum_inventory`, `topping_small`, 
 `topping_medium`, `topping_large`, `topping_xlarge`
)
Values
(
1,"Pepperoni",1.25, 0.2, 100, 50, 2, 2.75, 3.5, 4.5),
(2,"Sausage", 1.25, 0.15, 100, 50, 2.5, 3, 3.5, 4.25),
(3,"Ham", 1.5, 0.15, 78, 25, 2, 2.5, 3.25, 4),
(4,"Chicken", 1.75, 0.25, 56, 25, 1.5, 2, 2.25, 3),
(5,"Green Pepper", 0.5,0.02, 85, 25, 1, 1.5, 2, 2.5),
(6,"Onion", 0.5,0.02,79,25,1,1.5,2,2.75),
(7,"Roma Tomato",0.75,0.03,86,10,2,3,3.5,4.5),
(8,"Mushrooms",0.75,0.1,52,50,1.5,2,2.5,3),
(9,"Black Olives",0.6,0.1,39,25,0.75,1,1.5,2),
(10,"Pineapple",1,0.25,15,0,1,1.25,1.75,2),
(11,"Jalapenos",0.5,0.05,64,0,0.5,0.75,1.25,1.75),
(12,"Banana Peppers", 0.5,0.05,36,0,0.6,1,1.3,1.75),
(13,"Regular Cheese", 0.5,0.12,250,50,2,3.5,5,7),
(14,"Four Cheese Blend", 1,0.15,150,25,2,3.5,5,7),
(15,"Feta Cheese", 1.5,0.18,75,0,1.75,3,4,5.5),
(16,"Goat Cheese", 1.5,0.2,54,0,1.6,2.75,4,5.5),
(17,"Bacon",1.5,0.25,89,0,1,1.5,2,3);

-- ORDERS(1) :

-- Customer table:
INSERT INTO `customer` (`customer_firstName`,`customer_lastName`,`customer_phone`,`customer_street`,`customer_city`,`customer_state`,`customer_zip`) VALUES('dinein','customer1','NA','NA','NA','NA',12345); 


-- Order Table
INSERT INTO `order`
(`order_customerID`,`order_market_price`,`order_retail_price`,`order_type`,`order_status`,`order_Time`)
VALUES
(1, 20.75, 3.68,"DineIn","1",'2023-03-05 12:03:00');

-- Pizza Table
INSERT INTO `pizza`
(`pizza_ID`,`pizza_orderID`,`pizza_size`,`pizza_crust_type`,`pizza_market_price`,`pizza_retail_price`,`pizza_state`)
VALUES
(1001, 1, "Large", "Thin",  20.75, 3.68, "Completed");

-- DineIn
INSERT INTO `dinein`
(`dinein_orderID`,`dinein_table_num`)
VALUES
(1,21); 

-- PizzaTopping Table
INSERT INTO `pizza_topping`
(`pizza_toppingID`,`pizza_topping_name`,`pizza_topping_extras`)
VALUES
(1001, "Regular Cheese", TRUE), (1001, "Pepperoni", FALSE), (1001, "Sausage", FALSE);

-- OrderDiscount Table:
INSERT INTO `order_discount`(`order_discountID`,`order_discount_name`)
VALUES
(1,"Lunch Special Large");

-- ORDERS(2) :

-- Customer table:
INSERT INTO `customer`(`customer_firstName`,`customer_lastName`,`customer_phone`,`customer_street`,`customer_city`,`customer_state`,`customer_zip`) VALUES('dinein','customer2','NA','NA','NA','NA',12345);


-- Order Table
INSERT INTO `order`
(`order_customerID`,`order_market_price`,`order_retail_price`,`order_type`,`order_status`,`order_Time`)
VALUES
( 2, 19.78 ,4.63 ,"DineIn","1",'2023-04-03 12:05:00');

-- Pizza Table
INSERT INTO `pizza`
(`pizza_ID`,`pizza_orderID`,`pizza_size`,`pizza_crust_type`,`pizza_market_price`,`pizza_retail_price`,`pizza_state`)
VALUES
(1002, 2, "Medium", "Pan", 12.85, 3.23 , "Completed"),
(1003, 2, "Small", "Original", 6.93, 1.40, "Completed");

-- DineIn
INSERT INTO `dinein`
(`dinein_orderID`,`dinein_table_num`)
VALUES
(2,4); 

-- PizzaTopping Table
INSERT INTO `pizza_topping`
(`pizza_toppingID`,`pizza_topping_name`,`pizza_topping_extras`)
VALUES
(1002, "Feta Cheese", FALSE), 
(1002, "Black Olives", FALSE), 
(1002, "Roma Tomato", FALSE),
(1002, "Mushrooms", FALSE), 
(1002, "Banana Peppers", FALSE),
(1003, "Banana Peppers", FALSE), 
(1003, "Chicken", False), 
(1003, "Regular Cheese", FALSE);

-- PizzaDiscount Table:
INSERT INTO `pizza_discount`(`pizza_discount_ID`,`pizza_discount_name`)
VALUES
(1002,"Lunch Special Large"),
(1002,"Specialty Pizza");

-- ORDERS(3) :
INSERT INTO `customer` (`customer_firstName`,`customer_lastName`,`customer_phone`,`customer_street`,`customer_city`,`customer_state`,`customer_zip`)
VALUES
('Andrew', 'Wilkes-Krier', 8642545861,'115 Party Blvd','Anderson','SC',29621);

-- Order Table
INSERT INTO `order`
(`order_customerID`,`order_market_price`,`order_retail_price`,`order_type`,`order_status`,`order_Time`)
VALUES
( 3, 89.28 , 19.8 ,"Pickup","1" ,'2023-03-03 21:30:00' );

-- Pizza Table
INSERT INTO `pizza`
(`pizza_ID`,`pizza_orderID`,`pizza_size`,`pizza_crust_type`,`pizza_market_price`,`pizza_retail_price`,`pizza_state`)
VALUES
(1004, 3, "Large", "Original", 14.88, 3.30, "Completed"),
(1005, 3, "Large", "Original", 14.88, 3.30, "Completed"),
(1006, 3, "Large", "Original", 14.88, 3.30, "Completed"),
(1007, 3, "Large", "Original", 14.88, 3.30, "Completed"),
(1008, 3, "Large", "Original", 14.88, 3.30, "Completed"),
(1009, 3, "Large", "Original", 14.88, 3.30, "Completed");

-- PickUp Table:
INSERT INTO `pickup`
(`pickup_order_ID`,`pickup_order_customerID`,`pickup_status`)
VALUES
(3,3, "Picked up");

-- PizzaTopping Table
INSERT INTO `pizza_topping`
(`pizza_toppingID`,`pizza_topping_name`,`pizza_topping_extras`)
VALUES
(1004, "Regular Cheese", FALSE), (1004, "Pepperoni", FALSE),
(1005, "Regular Cheese", FALSE), (1005, "Pepperoni", FALSE),
(1006, "Regular Cheese", FALSE), (1006, "Pepperoni", FALSE),
(1007, "Regular Cheese", FALSE), (1007, "Pepperoni", FALSE),
(1008, "Regular Cheese", FALSE), (1008, "Pepperoni", FALSE),
(1009, "Regular Cheese", FALSE), (1009, "Pepperoni", FALSE);

-- ORDERS(4) :

-- Order Table
INSERT INTO `order`
(`order_customerID`,`order_market_price`,`order_retail_price`,`order_type`,`order_status`,`order_Time`)
VALUES
(3,86.19, 23.62, "Delivery","1",'2023-04-20 19:11:00');

-- Pizza Table
INSERT INTO `pizza`
(`pizza_ID`,`pizza_orderID`,`pizza_size`,`pizza_crust_type`,`pizza_market_price`,`pizza_retail_price`,`pizza_state`)
VALUES
(1010, 4, "XLarge", "Original", 27.94, 9.19, "Completed"),
(1011, 4, "XLarge", "Original", 31.50, 6.25, "Completed"),
(1012, 4, "XLarge", "Original", 26.75, 8.18, "Completed");

-- PizzaTopping Table
INSERT INTO `pizza_topping`
(`pizza_toppingID`,`pizza_topping_name`,`pizza_topping_extras`)
VALUES
(1010, "Pepperoni", FALSE), (1010, "Sausage", FALSE), (1010, "Four Cheese Blend", FALSE),
(1011, "Ham", TRUE), (1011, "Pineapple", TRUE), (1011, "Four Cheese Blend", FALSE),
(1012, "Chicken", FALSE), (1012, "Bacon", FALSE), (1012, "Four Cheese Blend", FALSE);

-- PizzaDiscount Table:
INSERT INTO `pizza_discount`(`pizza_discount_ID`,`pizza_discount_name`)
VALUES
(1011,"Specialty Pizza");


-- OrderDiscount Table:
INSERT INTO `order_discount`(`order_discountID`,`order_discount_name`)
VALUES
(4,"Gameday Special");

-- Delivery Table:

INSERT INTO `delivery`
(`delivery_orderID`,`delivery_order_customerID`,`delivery_street`,`delivery_city`,`delivery_state`,`delivery_zip`)
VALUES
(4,3,'115 Party Blvd','Anderson','SC',29621);


-- ORDERS(5) :
INSERT INTO `customer` (`customer_firstName`,`customer_lastName`,`customer_phone`,`customer_street`,`customer_city`,`customer_state`,`customer_zip`)
VALUES
('Matt', 'Engers', 8644749953,'NA','NA','NA',12345);

-- Order Table
INSERT INTO `order`
(`order_customerID`,`order_market_price`,`order_retail_price`,`order_type`,`order_status`,`order_Time`)
VALUES
(4, 27.45, 7.88,"Pickup", "1",'2023-03-02 17:30:00');

-- Pizza Table
INSERT INTO `pizza`
(`pizza_ID`,`pizza_orderID`,`pizza_size`,`pizza_crust_type`,`pizza_market_price`,`pizza_retail_price`,`pizza_state`)
VALUES
(1013, 5, "XLarge", "Gluten-Free", 27.45, 7.88, "Completed");

INSERT INTO `pizza_topping`
(`pizza_toppingID`,`pizza_topping_name`,`pizza_topping_extras`)
VALUES
(1013, "Green Pepper", FALSE), (1013, "Onion", FALSE), (1013, "Roma Tomato", FALSE),
(1013, "Mushrooms",  FALSE), (1013, "Black Olives", FALSE), (1013, "Goat Cheese", FALSE);

-- PizzaDiscount Table:
INSERT INTO `pizza_discount`(`pizza_discount_ID`,`pizza_discount_name`)
VALUES
(1013,"Specialty Pizza");

-- PickUp Table:
INSERT INTO `pickup`
(`pickup_order_ID`,`pickup_order_customerID`,`pickup_status`)
VALUES
(5,4,"Picked up");


-- ORDERS(6) :
INSERT INTO `customer` (`customer_firstName`,`customer_lastName`,`customer_phone`,`customer_street`,`customer_city`,`customer_state`,`customer_zip`)
VALUES
('Frank', 'Turner', 8642328944,'6745 Wessex St','Anderson','SC',29621);


-- Order Table
INSERT INTO `order`
(`order_customerID`,`order_market_price`,`order_retail_price`,`order_type`,`order_status`,`order_Time`)
VALUES
(5,25.81 ,4.24,"Delivery",  1,'2023-03-02 18:17:00' );

-- Pizza Table
INSERT INTO `pizza`
(`pizza_ID`,`pizza_orderID`,`pizza_size`,`pizza_crust_type`,`pizza_market_price`,`pizza_retail_price`,`pizza_state`)
VALUES
(1014, 6, "Large", "Thin", 25.81, 4.24, "Completed");

-- PizzaTopping Table
INSERT INTO `pizza_topping`
(`pizza_toppingID`,`pizza_topping_name`,`pizza_topping_extras`)
VALUES
(1014, "Green Pepper", FALSE), (1014, "Onion", FALSE), (1014, "Chicken", FALSE),
(1014, "Mushrooms",  FALSE), (1014, "Four Cheese Blend", TRUE);

-- Delivery Table:

INSERT INTO `delivery`
(`delivery_orderID`,`delivery_order_customerID`,`delivery_street`,`delivery_city`,`delivery_state`,`delivery_zip`)
VALUES
(6,5,'6745 Wessex St','Anderson','SC',29621);


-- ORDERS(7) :
INSERT INTO `customer` (`customer_firstName`,`customer_lastName`,`customer_phone`,`customer_street`,`customer_city`,`customer_state`,`customer_zip`)
VALUES
('Milo', 'Auckerman', 8648785679,'8879 Suburban Home','Anderson','SC',29621);

-- Order Table
INSERT INTO `order`
(`order_customerID`,`order_market_price`,`order_retail_price`,`order_type`,`order_status`,`order_Time`)
VALUES
(6,37.25, 6, "Delivery",  1,'2023-04-13 20:32:00'  );

-- Pizza Table
INSERT INTO `pizza`
(`pizza_ID`,`pizza_orderID`,`pizza_size`,`pizza_crust_type`,`pizza_market_price`,`pizza_retail_price`,`pizza_state`)
VALUES
(1015, 7, "Large", "Thin", 18.00, 2.75, "Completed"),
(1016, 7, "Large", "Thin", 19.25, 3.25, "Completed");

-- PizzaTopping Table
INSERT INTO `pizza_topping`
(`pizza_toppingID`,`pizza_topping_name`,`pizza_topping_extras`)
VALUES
(1015, "Four Cheese Blend", TRUE),
(1016, "Regular Cheese",  FALSE), (1016, "Pepperoni", TRUE);

-- OrderDiscount Table:
INSERT INTO `order_discount`(`order_discountID`,`order_discount_name`)
VALUES
(7,"Employee");

-- Delivery Table:

INSERT INTO `delivery`
(`delivery_orderID`,`delivery_order_customerID`,`delivery_street`,`delivery_city`,`delivery_state`,`delivery_zip`)
VALUES
(7,6,'8879 Suburban Home','Anderson','SC',29621);











