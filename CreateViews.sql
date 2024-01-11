-- Gayatri Tatineni, Tarun Prathipati
use Pizzeria;
-- 1st view :

CREATE VIEW ToppingPopularity AS
SELECT
    t.topping_name AS 'Topping',
    COALESCE(COUNT(pt.pizza_topping_name), 0) + COALESCE(SUM(pt.pizza_topping_extras), 0) AS ToppingCount
FROM topping t
LEFT JOIN pizza_topping pt ON t.topping_name = pt.pizza_topping_name
GROUP BY t.topping_name
ORDER BY ToppingCount DESC;

SELECT * FROM ToppingPopularity;


-- 2nd view :
CREATE VIEW ProfitByPizza AS
SELECT pizza_size AS 'Size', pizza_crust_type AS 'Crust' , 
SUM(pizza_market_price-pizza_retail_price) AS 'Profit',
MIN(DATE_FORMAT(order_Time,'%m/%Y')) AS 'OrderMonth' 
FROM `pizza`, `order` o
WHERE pizza.pizza_orderID=o.order_ID
GROUP BY  pizza_crust_type, pizza_size
ORDER BY Profit DESC ;
-- drop view ProfitByPizza
SELECT * FROM ProfitByPizza;


-- 3rd view:
CREATE VIEW ProfitByOrderType AS 
SELECT 
    order_type AS 'customerType',
    DATE_FORMAT(order_Time, '%m/%Y') AS 'OrderMonth',
    SUM(order_market_price) AS 'TotalOrderPrice',
    SUM(order_retail_price) AS 'TotalOrderCost', 
    SUM(order_market_price - order_retail_price) AS Profit 
FROM `order`
GROUP BY order_type, DATE_FORMAT(order_Time, '%m/%Y')
UNION
SELECT 
    '' AS 'customerType',
    'Grand Total' AS 'Order Month',
    SUM(order_market_price) AS 'TotalOrderPrice',
    SUM(order_retail_price) AS 'TotalOrderCost',
    SUM(order_market_price - order_retail_price) AS Profit
FROM `order`;

SELECT * FROM ProfitByOrderType;


