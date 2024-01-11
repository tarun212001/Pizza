package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;


/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

 public final class DBNinja {
    private static Connection conn;

    // Constants for order types
    public final static String pickup = "Pickup"; 
    public final static String delivery = "Delivery"; 
    public final static String dine_in = "DineIn"; 

    // Constants for pizza sizes
    public final static String size_s = "Small"; 
    public final static String size_m = "Medium"; 
    public final static String size_l = "Large"; 
    public final static String size_xl = "XLarge"; 

    // Constants for crust types
    public final static String crust_thin = "Thin"; 
    public final static String crust_orig = "Original";
    public final static String crust_pan = "Pan"; 
    public final static String crust_gf = "Gluten-Free";

    public final static Map<String, String> sizeMap;
    public final static Map<String, String> crustMap;

    static {
        sizeMap = new HashMap<>();
        sizeMap.put("1", "Small");
        sizeMap.put("2", "Medium");
        sizeMap.put("3", "Large");
        sizeMap.put("4", "XLarge");

        crustMap = new HashMap<>();
        crustMap.put("1", "Thin");
        crustMap.put("2", "Original");
        crustMap.put("3", "Pan");
        crustMap.put("4", "Gluten-Free");
    }

    private static boolean connect_to_db() throws SQLException, IOException {
        try {
            conn = DBConnector.make_connection();
            return true;
        } catch (SQLException | IOException e) {
            return false;
        }
    }

	
	public static void addOrder(Order o) throws SQLException, IOException 
{
    connect_to_db();
    /*
     * add code to add the order to the DB. Remember that we're not just
     * adding the order to the order DB table, but we're also recording
     * the necessary data for the delivery, dinein, and pickup tables
     */
    
    Integer custID = o.getCustID();
    String[] generatedId = { "ID" };

    String insstmnt = "INSERT INTO `order` "
            + "(`order_customerID`, `order_market_price`, `order_retail_price`, `order_type`, `order_status`, `order_Time`) " +
              "VALUES (" + custID + ", 0.00, 0.00, '" + o.getOrderType() + "', false, '" + o.getDate() + "')";

    PreparedStatement ps = conn.prepareStatement(insstmnt, generatedId);

    int result = ps.executeUpdate();

    if (result > 0) {
        try {
            ResultSet resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                o.setOrderID(resultSet.getInt(1));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing the database connection: " + e.getMessage());
                }
            }
        }
    }

    //DO NOT FORGET TO CLOSE YOUR CONNECTION
}

HashMap<Integer, String> pizzaSize = new HashMap();
	
public static int addPizza(Pizza p) throws SQLException, IOException {
   

    int generatedPizzaId = 0; 

    try {
        double price = getBaseCustPrice(p.getSize(), p.getCrustType());
        double cost = getBaseBusPrice(p.getSize(), p.getCrustType());   
        Double customerPrice = p.getCustPrice() + price;
         Double busPrice = p.getBusPrice()+ cost;
        p.setCustPrice(customerPrice);
        p.setBusPrice(busPrice);
      
        String[] generatedId = {"pizza_ID"};

       



        String insstmnt = "INSERT INTO pizza(pizza_orderID, pizza_size, pizza_crust_type, pizza_market_price, pizza_retail_price, pizza_state) VALUES (" 
                         + p.getOrderID() + ", '" + sizeMap.get(p.getSize()) + "', '" + crustMap.get(p.getCrustType()) + "', " 
                         + p.getBusPrice() + ", " + p.getCustPrice() + ", 'Pending');"; // Assuming 'Pending' as initial state

        connect_to_db();
        PreparedStatement ps = conn.prepareStatement(insstmnt, generatedId);

        int res = ps.executeUpdate();

        if (res > 0) {
            ResultSet resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                generatedPizzaId = resultSet.getInt(1);
                p.setPizzaID(generatedPizzaId); 
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    return generatedPizzaId; 
}

	
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this method will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();
		/*
		 * This method should do 2 two things.
		 * - update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * - connect the topping to the pizza
		 *   What that means will be specific to your yimplementatinon.
		 * 
		 * Ideally, you should't let toppings go negative....but this should be dealt with BEFORE calling this method.
		 * 
		 */
        int value = -1;
        if(isDoubled)
        {
            value = value *2;
        }
		String updateStatement = "update topping set topping_current_inventory = topping_current_inventory +(?) where topping_ID = ? ;";
		
		PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
		
		preparedStatement.setDouble(1, value);
		
		preparedStatement.setInt(2, t.getTopID());
		
		preparedStatement.executeUpdate();
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException {
        connect_to_db();
        try {
            String pizzaQuery = "INSERT INTO pizza_discount (pizza_discount_ID, pizza_discount_name) VALUES (?, ?)";
            PreparedStatement inserts = conn.prepareStatement(pizzaQuery);
            inserts.setInt(1, p.getPizzaID());
            inserts.setString(2, d.getDiscountName());
            inserts.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing the database connection: " + e.getMessage());
                }
            }
        }
    }
    
    public static void updateOrderDetails(Order o) {

		try {
			connect_to_db();
			String updateStatement = "UPDATE `order` SET order_market_price = ?, order_retail_price = ? WHERE order_ID = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
			
			preparedStatement.setDouble(1, o.getCustPrice());
			
			preparedStatement.setDouble(2, o.getBusPrice());
			
			preparedStatement.setInt(3, o.getOrderID());
			
			preparedStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
    public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException {
        connect_to_db();
        try {
            String orderdisQuery = "INSERT INTO order_discount (order_discountID, order_discount_name) VALUES (?, ?)";
            PreparedStatement ins = conn.prepareStatement(orderdisQuery);
            ins.setInt(1, o.getOrderID());
            ins.setString(2, d.getDiscountName());
            ins.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing the database connection: " + e.getMessage());
                }
            }
        }
    }
    

	
    public static Integer addCustomer(Customer c) throws SQLException, IOException {
        connect_to_db();
        if (DBNinja.conn == null) {
            throw new SQLException("Database connection not established.");
        }
        Integer custID = null;
        PreparedStatement ps = null;
        ResultSet pscust = null;
    
        String insquery = "INSERT INTO customer (customer_firstName, customer_lastName, customer_phone, customer_street, customer_city, customer_state, customer_zip) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
        String[] genID = { "customer_ID" };
    
        try {
            ps = DBNinja.conn.prepareStatement(insquery, genID);
            String[] addressParts = c.getAddress().split(",", -1);
            
            if (addressParts.length != 4) {
                throw new IllegalArgumentException("Address format is incorrect. Expected format: Street, City, State, Zip");
            }
    
            ps.setString(1, c.getFName());
            ps.setString(2, c.getLName());
            ps.setString(3, c.getPhone());
            ps.setString(4, addressParts[0].trim()); 
            ps.setString(5, addressParts[1].trim());
            ps.setString(6, addressParts[2].trim()); 
            ps.setString(7, addressParts[3].trim()); 
    
            int result = ps.executeUpdate();
            if (result > 0) {
                pscust = ps.getGeneratedKeys();
                if (pscust.next()) {
                    custID = pscust.getInt(1);
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
    
            e.printStackTrace();
            throw new SQLException("Failed to add customer due to incorrect data format: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pscust != null) {
                    pscust.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        return custID;
    }
    


public static void completeOrder(Order o) throws SQLException, IOException {
    connect_to_db();
    /*
     * Find the specified order in the database and mark that order as complete in the database.
     */
    try {

        String updateOrdStmnt = "UPDATE `order` SET order_status = 1 WHERE order_ID = ?";
        PreparedStatement psordstmnt = conn.prepareStatement(updateOrdStmnt);
        psordstmnt.setInt(1, o.getOrderID());
        psordstmnt.executeUpdate();

        String updatePizStmnt = "UPDATE pizza SET pizza_state = 'Completed' WHERE pizza_orderID = ?";
        PreparedStatement pspizzastmnt = conn.prepareStatement(updatePizStmnt);
        pspizzastmnt.setInt(1, o.getOrderID());
        pspizzastmnt.executeUpdate();

    } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing the database connection: " + e.getMessage());
                }
            }
        }
    }
    
    //DO NOT FORGET TO CLOSE YOUR CONNECTION

    public static ArrayList<Order> getCurrentOrders(int status) throws SQLException, IOException {
        connect_to_db();
        ArrayList<Order> orders = new ArrayList<>();
        PreparedStatement pst = null;
        ResultSet rs = null;
    
        try {
            String query = "SELECT * FROM `order` WHERE order_status = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, status);
    
            rs = pst.executeQuery();
    
            while (rs.next()) {
                int orderID = rs.getInt("order_ID");
                int custID = rs.getInt("order_customerID");
                String orderType = rs.getString("order_type");
                String date = rs.getString("order_Time");
                double custPrice = rs.getDouble("order_retail_price");
                double busPrice = rs.getDouble("order_market_price");
                int isComplete = rs.getInt("order_status");
    
                Order o = new Order(orderID, custID, orderType, date, custPrice, busPrice, isComplete);
                orders.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }
    
        return orders;
    }
    public static void CompleteOrder(Integer orderId) throws SQLException, IOException {
        connect_to_db();
        PreparedStatement pst = null;
    
        try {
            String updateQuery = "UPDATE `order` SET order_status = 1 WHERE order_ID = ?";
            pst = conn.prepareStatement(updateQuery);
            pst.setInt(1, orderId);
    
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }
    }
    


    public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {
        connect_to_db();
        ArrayList<Order> orders = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
    
        try {
            st = conn.createStatement();
            String query;
            if (openOnly) {
                query = "SELECT * FROM `order` WHERE order_status = 0 ORDER BY order_ID";
            } else {
                query = "SELECT * FROM `order` ORDER BY order_ID";
            }
    
            rs = st.executeQuery(query);
    
            while (rs.next()) {
                int orderID = rs.getInt("order_ID");
                int custID = rs.getInt("order_customerID");
                String orderType = rs.getString("order_type");
                String date = rs.getString("order_Time");
                double custPrice = rs.getDouble("order_retail_price");
                double busPrice = rs.getDouble("order_market_price");
                int isComplete = rs.getInt("order_status");
    
                Order o = new Order(orderID, custID, orderType, date, custPrice, busPrice, isComplete);
                orders.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        return orders;
    }
    

	
public static Order getLastOrder() throws SQLException, IOException {
    connect_to_db();
    Order lastOrder = null;
    Statement st = null;
    ResultSet rs = null;

    try {
        st = conn.createStatement();
        String query = "SELECT * FROM `order` ORDER BY order_ID DESC LIMIT 1";
        rs = st.executeQuery(query);

        if (rs.next()) {
            int orderID = rs.getInt("order_ID");
            int custID = rs.getInt("order_customerID");
            String orderType = rs.getString("order_type");
            String date = rs.getString("order_Time"); 
            double custPrice = rs.getDouble("order_retail_price");
            double busPrice = rs.getDouble("order_market_price");
            int isComplete = rs.getInt("order_status");

            lastOrder = new Order(orderID, custID, orderType, date, custPrice, busPrice, isComplete);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
      
    }

    return lastOrder;
}

    
    

    
 


public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException {
    connect_to_db();
    /*
     * Query the database for ALL the orders placed on a specific date
     * and return a list of those orders.
     */
    ArrayList<Order> orders = new ArrayList<>();
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        String query = "SELECT * FROM `order` WHERE DATE(order_Time) = ?";
        pst = conn.prepareStatement(query);
        pst.setString(1, date);

        rs = pst.executeQuery();

        while (rs.next()) {
            int orderID = rs.getInt("order_ID");
            int custID = rs.getInt("order_customerID");
            String orderType = rs.getString("order_type");
            String orderDate = rs.getString("order_Time"); 
            double custPrice = rs.getDouble("order_retail_price");
            double busPrice = rs.getDouble("order_market_price");
            int isComplete = rs.getInt("order_status");

            Order order = new Order(orderID, custID, orderType, orderDate, custPrice, busPrice, isComplete);
            orders.add(order);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return orders;
}

		
public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
    connect_to_db();
    /* 
     * Query the database for all the available discounts and 
     * return them in an ArrayList of discounts.
     */

    ArrayList<Discount> discounts = new ArrayList<>();
    Statement stmt = null;
    ResultSet rs = null;

    try {
        stmt = conn.createStatement();
        String query = "SELECT * FROM discount ORDER BY discount_ID"; 
        rs = stmt.executeQuery(query);

        while (rs.next()) {
            int discountID = rs.getInt("discount_ID"); 
            String discountName = rs.getString("discount_name"); 
            double amount = rs.getDouble("discount_amount"); 
            boolean isPercent = rs.getBoolean("discount_is_percent"); 

            Discount d = new Discount(discountID, discountName, amount, isPercent);
            discounts.add(d);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //DO NOT FORGET TO CLOSE YOUR CONNECTION
    return discounts;
}


public static Discount findDiscountByName(String name) throws SQLException, IOException  {
    connect_to_db();
    /*
     * Query the database for a discount using its name.
     * If found, then return a Discount object for the discount.
     * If it's not found, then return null.
     */

    PreparedStatement pst = null;
    ResultSet rs = null;
    Discount discount = null;

    try {
        String query = "SELECT * FROM discount WHERE discount_name = ?";
        pst = conn.prepareStatement(query);
        pst.setString(1, name);

        rs = pst.executeQuery();

        if (rs.next()) {
            int discountID = rs.getInt("discount_ID"); 
            String discountName = rs.getString("discount_name");
            double amount = rs.getDouble("discount_amount"); 
            boolean isPercent = rs.getBoolean("discount_is_percent"); 

            discount = new Discount(discountID, discountName, amount, isPercent);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return discount;
}



public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
    connect_to_db();
    /*
     * Query the data for all the customers and return an ArrayList of all the customers. 
     * Don't forget to order the data coming from the database appropriately.
     */

    ArrayList<Customer> customers = new ArrayList<>();
    Statement stmt = null;
    ResultSet rs = null;

    try {
        stmt = conn.createStatement();
   
        String query = "SELECT * FROM customer ORDER BY customer_ID";
        rs = stmt.executeQuery(query);

        while (rs.next()) {
            int custID = rs.getInt("customer_ID"); 
            String fName = rs.getString("customer_firstName"); 
            String lName = rs.getString("customer_lastName"); 
            String phone = rs.getString("customer_phone"); 


            Customer customer = new Customer(custID, fName, lName, phone);
            customers.add(customer);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //DO NOT FORGET TO CLOSE YOUR CONNECTION
    return customers;
}


public static Customer findCustomerByPhone(String phoneNumber) throws SQLException, IOException  {
    connect_to_db();
    /*
     * Query the database for a customer using a phone number.
     * If found, then return a Customer object for the customer.
     * If it's not found, then return null.
     */

    PreparedStatement pst = null;
    ResultSet rs = null;
    Customer customer = null;

    try {
        String query = "SELECT * FROM customer WHERE customer_phone = ?";
        pst = conn.prepareStatement(query);
        pst.setString(1, phoneNumber);

        rs = pst.executeQuery();

        if (rs.next()) {
            int custID = rs.getInt("customer_ID"); 
            String fName = rs.getString("customer_firstName"); 
            String lName = rs.getString("customer_lastName"); 
            String phone = rs.getString("customer_phone"); 
            customer = new Customer(custID, fName, lName, phone);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return customer;
}



public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
    connect_to_db();
    /*
     * Query the database for the available toppings and 
     * return an ArrayList of all the available toppings. 
     * Don't forget to order the data coming from the database appropriately.
     */

    ArrayList<Topping> toppings = new ArrayList<>();
    Statement stmt = null;
    ResultSet rs = null;

    try {
        stmt = conn.createStatement();
        String query = "SELECT * FROM topping ORDER BY topping_ID";
        rs = stmt.executeQuery(query);

        while (rs.next()) {
            int topID = rs.getInt("topping_ID");
            String topName = rs.getString("topping_name");
            double perAMT = rs.getDouble("topping_small");
            double medAMT = rs.getDouble("topping_medium");
            double lgAMT = rs.getDouble("topping_large");
            double xLAMT = rs.getDouble("topping_xlarge");
            double custPrice = rs.getDouble("topping_retail_price");
            double busPrice = rs.getDouble("topping_market_price");
            int minINVT = rs.getInt("topping_minimum_inventory");
            int curINVT = rs.getInt("topping_current_inventory");

            Topping topping = new Topping(topID,topName, perAMT, medAMT, lgAMT, xLAMT, custPrice, busPrice, minINVT, curINVT);
            toppings.add(topping);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //DO NOT FORGET TO CLOSE YOUR CONNECTION
    return toppings;
}


public static Topping findToppingByName(String name) throws SQLException, IOException  {
    connect_to_db();
    /*
     * Query the database for the topping using its name.
     * If found, then return a Topping object for the topping.
     * If it's not found, then return null.
     */

    PreparedStatement pst = null;
    ResultSet rs = null;
    Topping topping = null;

    try {
        String query = "SELECT * FROM topping WHERE topping_name = ?";
        pst = conn.prepareStatement(query);
        pst.setString(1, name);

        rs = pst.executeQuery();

        if (rs.next()) {
            String topName = rs.getString("topping_name");
            double perAMT = rs.getDouble("topping_small");
            double medAMT = rs.getDouble("topping_medium");
            double lgAMT = rs.getDouble("topping_large");
            double xLAMT = rs.getDouble("topping_xlarge");
            double custPrice = rs.getDouble("topping_retail_price");
            double busPrice = rs.getDouble("topping_market_price");
            int minINVT = rs.getInt("topping_minimum_inventory");
            int curINVT = rs.getInt("topping_current_inventory");

            topping = new Topping(0, topName, perAMT, medAMT, lgAMT, xLAMT, custPrice, busPrice, minINVT, curINVT);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return topping;
}



public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {
    connect_to_db();
    /*
     * Updates the quantity of the topping in the database by the amount specified.
     */

    PreparedStatement pst = null;

    try {

        String invQuery = "UPDATE topping SET topping_current_inventory = topping_current_inventory + ? WHERE topping_ID = ?";
        pst = conn.prepareStatement(invQuery);

        pst.setDouble(1, quantity);
        pst.setInt(2, t.getTopID());

        pst.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //DO NOT FORGET TO CLOSE YOUR CONNECTION
}

	
public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
    connect_to_db();
    /* 
     * Query the database for the base customer price for that size and crust pizza.
     */

    double basePrice = 0.0;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        String query = "SELECT baseprice_retail_price FROM baseprice WHERE baseprice_size = ? AND baseprice_crust_type = ?";
        pst = conn.prepareStatement(query);

        pst.setString(1, sizeMap.get(size));
        pst.setString(2, crustMap.get(crust));

        rs = pst.executeQuery();

        if (rs.next()) {
            basePrice = rs.getDouble("baseprice_retail_price");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //DO NOT FORGET TO CLOSE YOUR CONNECTION
    return basePrice;
}


public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
    connect_to_db();
    /* 
     * Query the database for the base business price for that size and crust pizza.
     */

    double baseBusPrice = 0.0;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        String query = "SELECT baseprice_market_price FROM baseprice WHERE baseprice_size = ? AND baseprice_crust_type = ?";
        pst = conn.prepareStatement(query);

        pst.setString(1, sizeMap.get(size));
        pst.setString(2, crustMap.get(crust));

        rs = pst.executeQuery();

        if (rs.next()) {
            baseBusPrice = rs.getDouble("baseprice_market_price");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //DO NOT FORGET TO CLOSE YOUR CONNECTION
    return baseBusPrice;
}
public static void updateDeliveryAddress(Integer customerId, String AptNum, String streetName, String cityName,
			String stateName,
			String zip) {
		try {
			String insertStatement = "UPDATE customer SET "
					+ "(customer_street,customer_city,customer_state,customer_zip) "
					+ "VALUES (?, ?, ? , ? ,?) WHERE customer_ID=?";

			connect_to_db();

			PreparedStatement preparedStatement = conn.prepareStatement(insertStatement);

			preparedStatement.setString(1, AptNum);
			preparedStatement.setString(2, streetName);
			preparedStatement.setString(3, cityName);
			preparedStatement.setString(4, stateName);
			preparedStatement.setString(5, zip);
			preparedStatement.setInt(6, customerId);
			preparedStatement.executeUpdate();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("Error closing the database connection: " + e.getMessage());
			}
		}

	}

public static List<Topping> printInventory() throws SQLException, IOException {
    connect_to_db();
    List<Topping> toppings = new ArrayList<>();
    Statement stmt = null;
    ResultSet rs = null;

    try {
        stmt = conn.createStatement();
        String query = "SELECT topping_ID, topping_name, topping_current_inventory FROM topping ORDER BY topping_ID";

        rs = stmt.executeQuery(query);

        while (rs.next()) {
            int topID = rs.getInt("topping_ID");
            String toppingName = rs.getString("topping_name");
            int currentInventory = rs.getInt("topping_current_inventory");

            double defaultPerAMT = 0.0;
            double defaultMedAMT = 0.0;
            double defaultLgAMT = 0.0;
            double defaultXLAMT = 0.0;
            double defaultCustPrice = 0.0;
            double defaultBusPrice = 0.0;
            int defaultMinINVT = 0;

            Topping t = new Topping(topID, toppingName, defaultPerAMT, defaultMedAMT, defaultLgAMT, defaultXLAMT, 
                                    defaultCustPrice, defaultBusPrice, defaultMinINVT, currentInventory);
            toppings.add(t);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }

    return toppings;
}

	
	
public static void printToppingPopReport() throws SQLException, IOException {
    connect_to_db();
    /*
     * Prints the ToppingPopularity report directly using the SQL query 
     * from the view creation instead of querying the view.
     * The result should be readable and sorted as indicated in the prompt.
     */

    Statement stmt = null;
    ResultSet rs = null;

    try {
        stmt = conn.createStatement();
        String query = "SELECT " +
                       "t.topping_name AS 'Topping', " +
                       "COALESCE(COUNT(pt.pizza_topping_name), 0) + COALESCE(SUM(pt.pizza_topping_extras), 0) AS ToppingCount " +
                       "FROM topping t " +
                       "LEFT JOIN pizza_topping pt ON t.topping_name = pt.pizza_topping_name " +
                       "GROUP BY t.topping_name " +
                       "ORDER BY ToppingCount DESC;";

        rs = stmt.executeQuery(query);


        System.out.println("Topping               ToppingCount");

        while (rs.next()) {
            String topping = rs.getString("Topping");
            int toppingCount = rs.getInt("ToppingCount");
            System.out.printf("%-25s %-4d%n", topping, toppingCount);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DO NOT FORGET TO CLOSE YOUR CONNECTION
}

	
public static void printProfitByPizzaReport() throws SQLException, IOException {
    connect_to_db();
    /*
     * Prints the ProfitByPizza report directly using the SQL query 
     * from the view creation instead of querying the view.
     * The result should be readable and sorted as indicated in the prompt.
     */

    Statement stmt = null;
    ResultSet rs = null;

    try {
        stmt = conn.createStatement();
        String query = "SELECT pizza_size AS 'Size', pizza_crust_type AS 'Crust', " +
                       "SUM(pizza_market_price - pizza_retail_price) AS 'Profit', " +
                       "MIN(DATE_FORMAT(order_Time, '%m/%Y')) AS 'OrderMonth' " +
                       "FROM `pizza`, `order` o " +
                       "WHERE pizza.pizza_orderID = o.order_ID " +
                       "GROUP BY pizza_crust_type, pizza_size " +
                       "ORDER BY Profit DESC;";

        rs = stmt.executeQuery(query);


        System.out.printf("%-15s  %-15s  %-10s  %-15s%n", "Pizza Size", "Pizza Crust", "Profit", "Last Order Month");


        while (rs.next()) {
            String size = rs.getString("Size");
            String crust = rs.getString("Crust");
            double profit = rs.getDouble("Profit");
            String lastOrderMonth = rs.getString("OrderMonth");
            System.out.printf("%-15s  %-15s  %-10.2f  %-15s%n", size, crust, profit, lastOrderMonth);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DO NOT FORGET TO CLOSE YOUR CONNECTION
}

	
	
	public static void printProfitByOrderType() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType report directly using the SQL query 
		 * from the view creation instead of querying the view.
		 * The result should be readable and sorted as indicated in the prompt.
		 */
	
		Statement stmt = null;
		ResultSet rs = null;
	
		try {
			stmt = conn.createStatement();
			String query = "SELECT order_type AS 'Customer Type', " +
						   "DATE_FORMAT(order_Time, '%m/%Y') AS 'Order Month', " +
						   "SUM(order_market_price) AS 'Total Order Price', " +
						   "SUM(order_retail_price) AS 'Total Order Cost', " +
						   "SUM(order_market_price - order_retail_price) AS 'Profit' " +
						   "FROM `order` " +
						   "GROUP BY order_type, DATE_FORMAT(order_Time, '%m/%Y') " +
						   "UNION " +
						   "SELECT '' AS 'Customer Type', 'Grand Total' AS 'Order Month', " +
						   "SUM(order_market_price) AS 'Total Order Price', " +
						   "SUM(order_retail_price) AS 'Total Order Cost', " +
						   "SUM(order_market_price - order_retail_price) AS 'Profit' " +
						   "FROM `order`;";
	
			rs = stmt.executeQuery(query);

			System.out.printf("%-15s  %-15s  %-18s  %-18s  %-8s%n", "Customer Type", "Order Month", "Total Order Price",
					"Total Order Cost", "Profit");
			
	

			while (rs.next()) {
				String customerType = rs.getString("Customer Type");
				String orderMonth = rs.getString("Order Month");
				double totalOrderPrice = rs.getDouble("Total Order Price");
				double totalOrderCost = rs.getDouble("Total Order Cost");
				double profit = rs.getDouble("Profit");
				System.out.printf("%-15s  %-15s  %-18.2f  %-18.2f  %-8.2f%n", customerType, orderMonth, totalOrderPrice,
						totalOrderCost, profit);
			}
	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
    
	
	
	
	
	public static String getCustomerName(int custID) throws SQLException, IOException {
		/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. It uses a prepared statement to interact with 
		 * the database.
		 */
	
		connect_to_db();
	
		String customerName = "";
	
		String query = "SELECT customer_firstName, customer_lastName FROM customer WHERE customer_ID = ?;";
	
		try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
			preparedStatement.setInt(1, custID); 
	
			ResultSet resultSet = preparedStatement.executeQuery();
	
			while (resultSet.next()) {
				String firstName = resultSet.getString("customer_firstName");
				String lastName = resultSet.getString("customer_lastName");
				customerName = firstName + " " + lastName;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	
		return customerName;
	}
	

	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


}