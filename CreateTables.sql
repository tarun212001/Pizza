-- Gayatri Tatineni, Tarun Prathipati

DROP SCHEMA IF EXISTS Pizzeria;
CREATE SCHEMA Pizzeria;
use Pizzeria;
-- -- Customer:
CREATE TABLE `customer` (
  `customer_ID` INT NOT NULL AUTO_INCREMENT,
  `customer_firstName` varchar(15) ,
  `customer_lastName` varchar(15) ,
  `customer_phone` varchar(15) ,
  `customer_apt` varchar(15) ,
  `customer_street` varchar(40) NOT NULL,
  `customer_city` varchar(15) NOT NULL,
  `customer_state` varchar(5) NOT NULL,
  `customer_zip` int NOT NULL,
  PRIMARY KEY (`customer_ID`)
) AUTO_INCREMENT=1 ;


-- -- Topping:

CREATE TABLE `topping` (
`topping_ID` int NOT NULL,
  `topping_name` varchar(25) NOT NULL,
  `topping_market_price` decimal(5,2) NOT NULL,
  `topping_retail_price` decimal(5,2) NOT NULL,
  `topping_current_inventory` int NOT NULL,
  `topping_minimum_inventory` int NOT NULL,
  `topping_small` decimal(5,2) NOT NULL,
  `topping_medium` decimal(5,2) NOT NULL,
  `topping_large` decimal(5,2) NOT NULL,
  `topping_xlarge` decimal(5,2) NOT NULL,
  PRIMARY KEY (`topping_name`)
) AUTO_INCREMENT=1 ;

-- -- Order:
CREATE TABLE `order` (
  `order_ID` int NOT NULL AUTO_INCREMENT,
  `order_customerID` int NOT NULL,
  `order_market_price` decimal(5,2) NOT NULL,
  `order_retail_price` decimal(5,2) NOT NULL,
  `order_type` varchar(15) NOT NULL,
  `order_status` boolean NOT NULL,
  `order_Time` datetime NOT NULL ,
  PRIMARY KEY (`order_ID`),
  CONSTRAINT `order_customerIDFK1` FOREIGN KEY (`order_customerID`) REFERENCES `customer` (`customer_ID`)
) AUTO_INCREMENT=1;


-- -- BasePrice:

CREATE TABLE `baseprice` (
  `baseprice_size` varchar(20) NOT NULL,
  `baseprice_crust_type` varchar(20) NOT NULL,
  `baseprice_market_price` decimal(5,2) NOT NULL,
  `baseprice_retail_price` decimal(5,2) NOT NULL,
  PRIMARY KEY (`baseprice_crust_type`,`baseprice_size`)
) ;
-- -- Pizza:

CREATE TABLE `pizza` (
  `pizza_ID` int NOT NULL AUTO_INCREMENT,
  `pizza_orderID` int NOT NULL,
  `pizza_size` varchar(20) NOT NULL,
  `pizza_crust_type` varchar(20) NOT NULL,
  `pizza_market_price` decimal(5,2) NOT NULL,
  `pizza_retail_price` decimal(5,2) NOT NULL,
  `pizza_state` varchar(15) NOT NULL,
  PRIMARY KEY (`pizza_ID`),
  CONSTRAINT `pizza_orderIDFK3` FOREIGN KEY (`pizza_orderID`) REFERENCES `order` (`order_ID`),
  CONSTRAINT `pizza_sizeFK2` FOREIGN KEY (`pizza_crust_type`, `pizza_size`) REFERENCES `baseprice` (`baseprice_crust_type`, `baseprice_size`)
) AUTO_INCREMENT=1;

-- -- PizzaTopping:

CREATE TABLE `pizza_topping` (
  `pizza_toppingID` int NOT NULL,
  `pizza_topping_name` varchar(25) NOT NULL,
  `pizza_topping_extras` tinyint(1) NOT NULL,
   PRIMARY KEY (`pizza_toppingID`, `pizza_topping_name`),
   CONSTRAINT `pizza_toppingFK2` FOREIGN KEY (`pizza_toppingID`) REFERENCES `pizza` (`pizza_ID`),
   CONSTRAINT `pizza_toppingFK1` FOREIGN KEY (`pizza_topping_name`) REFERENCES `topping` (`topping_name`)
) ;

-- -- Discount:

CREATE TABLE `discount` (
  `discount_ID` int NOT NULL,
  `discount_name` varchar(25) NOT NULL,
  `discount_amount` decimal(5,2),
  `discount_is_percent` tinyint(1) NOT NULL,
  PRIMARY KEY (`discount_name`)
)  ;



-- -- PizzaDiscount:

CREATE TABLE `pizza_discount` (
  `pizza_discount_ID` int NOT NULL,
  `pizza_discount_name` varchar(25) NOT NULL,
  PRIMARY KEY (`pizza_discount_name`,`pizza_discount_ID`),
  CONSTRAINT `pizza_discount_nameFK2` FOREIGN KEY (`pizza_discount_name`) REFERENCES `discount` (`discount_name`),
  CONSTRAINT `pizza_discount_IDFK1` FOREIGN KEY (`pizza_discount_ID`) REFERENCES `pizza` (`pizza_ID`)
) ;



-- -- DineIn:

CREATE TABLE `dinein` (
  `dinein_orderID` int NOT NULL,
  `dinein_table_num` int NOT NULL,
  PRIMARY KEY (`dinein_orderID`),
  CONSTRAINT `dinein_orderIDFK1` FOREIGN KEY (`dinein_orderID`) REFERENCES `order` (`order_ID`)
) ;

		-- -- Delivery
CREATE TABLE `delivery` (
  `delivery_orderID` int NOT NULL,
  `delivery_order_customerID` int NOT NULL,
  `delivery_street` varchar(40) NOT NULL,
  `delivery_city` varchar(15) NOT NULL,
  `delivery_state` varchar(5) NOT NULL,
  `delivery_zip` int NOT NULL,
  PRIMARY KEY (`delivery_orderID`),
  CONSTRAINT `delivery_orderIDFK1` FOREIGN KEY (`delivery_orderID`) REFERENCES `order` (`order_ID`),
  CONSTRAINT `delivery_order_customerIDFK2` FOREIGN KEY (`delivery_order_customerID`) REFERENCES `order` (`order_customerID`)
) ;

		-- -- PickUp:

CREATE TABLE `pickup` (
  `pickup_order_ID` int NOT NULL,
  `pickup_order_customerID` int NOT NULL,
  `pickup_status` varchar(20) NOT NULL,
  PRIMARY KEY (`pickup_order_ID`),
  CONSTRAINT `pickup_order_IDFK1` FOREIGN KEY (`pickup_order_ID`) REFERENCES `order` (`order_ID`),
  CONSTRAINT `pickup_order_customerIDFK2` FOREIGN KEY (`pickup_order_customerID`) REFERENCES `order` (`order_customerID`)
) ;




-- -- OrderDiscount:

CREATE TABLE `order_discount` (
  `order_discountID` int NOT NULL,
  `order_discount_name` varchar(25) NOT NULL,
  PRIMARY KEY (`order_discountID`,`order_discount_name`),
  CONSTRAINT `order_discount_nameFK2` FOREIGN KEY (`order_discount_name`) REFERENCES `discount` (`discount_name`),
  CONSTRAINT `order_discountIDDFK1` FOREIGN KEY (`order_discountID`) REFERENCES `order` (`order_ID`)
);






