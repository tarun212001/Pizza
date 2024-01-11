package cpsc4620;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the methods for each of the menu options.
 * 
 * This file should not need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove methods as you see necessary. But you MUST have all of the menu methods (including exit!)
 * 
 * Simply removing menu methods because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 */

 public class Menu {

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws SQLException, IOException {

        System.out.println("Welcome to Pizzas-R-Us!");

        int menu_option = 0;

        while (menu_option != 9) {
            PrintMenu();
            String option = reader.readLine().trim();
            if (option.isEmpty()) {
                System.out.println("Please enter a valid choice.");
                continue;
            }

            try {
                menu_option = Integer.parseInt(option);

                if (menu_option < 1 || menu_option > 9) {
                    System.out.println("Invalid choice. Please enter a valid number between 1 and 9.");
                    continue;
                }

                switch (menu_option) {
                    case 1:
                        EnterOrder();
                        break;
                    case 2:
                        viewCustomers();
                        break;
                    case 3:
                        EnterCustomer();
                        break;
                    case 4:
                        ViewOrders();
                        break;
                    case 5:
                        MarkOrderAsComplete();
                        break;
                    case 6:
                        ViewInventoryLevels();
                        break;
                    case 7:
                        AddInventory();
                        break;
                    case 8:
                        PrintReports();
                        break;

                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a valid number.");
            } catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
		}
        }
    }

	public static void EnterOrder() throws SQLException, IOException {
		try {
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			String customerChoice = reader.readLine();
			if (customerChoice.isEmpty()) {
				System.out.println("Incorrect entry, not an option");
				return;
			}
	
			int customerId;
			if ("y".equalsIgnoreCase(customerChoice)) {
				System.out.println("Here's a list of the current customers: ");
				ArrayList<Customer> customers = DBNinja.getCustomerList();
				customers.forEach(customer -> System.out.println("CustID=" + customer.getCustID() + " | " + "Name=" + customer.getFName() + " " + customer.getLName() + " , Phone= " + customer.getPhone()));
				System.out.println("Which customer is this order for? Enter ID Number:");
	
				final int enteredCustomerId = Integer.parseInt(reader.readLine());
	
				boolean customerExists = customers.stream().anyMatch(c -> c.getCustID() == enteredCustomerId);
				if (!customerExists) {
					System.out.println("Customer ID not found. Please enter a new customer.");
					customerId = EnterCustomer();
				} else {
					customerId = enteredCustomerId;
				}
			} else {
				customerId = EnterCustomer();
			}
	
			System.out.println("Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the number of your choice:");
			String orderTypeInput = reader.readLine();
			String orderType = "";
			switch (orderTypeInput) {
				case "1":
					orderType = DBNinja.dine_in;
					break;
				case "2":
					orderType = DBNinja.pickup;
					break;
				case "3":
					orderType = DBNinja.delivery;
					break;
				default:
					System.out.println("ERROR: I don't understand your input for: Is this order an existing customer?");
					return;
			}
	
			Date date = Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String strDate = dateFormat.format(date);
			Order order = new Order(0, customerId, orderType, strDate, 0.0, 0.0, 0);
			DBNinja.addOrder(order);
			int orderId = order.getOrderID();
	
			switch (orderType) {
				case DBNinja.dine_in:
					System.out.println("What is the table number for this order?");
					int tableNumber = Integer.parseInt(reader.readLine());
	
					break;
				case DBNinja.pickup:
	
					break;
				case DBNinja.delivery:
					System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
					String Apt = reader.readLine();
					System.out.println("What is the Street for this order? (e.g., Smile Street)");
					String street = reader.readLine();
					System.out.println("What is the City for this order? (e.g., Greenville)");
					String city = reader.readLine();
					System.out.println("What is the State for this order? (e.g., SC)");
					String state = reader.readLine();
					System.out.println("What is the Zip Code for this order? (e.g., 20605)");
					String zip = reader.readLine();
				DBNinja.updateDeliveryAddress(null, Apt, street, city, state, zip);
					break;
			} 
			double totalOrderCost = 0.00;
			double totalOrderPrice = 0.00;
	
			// Building pizzas for the order
			boolean addMorePizza;
			do {
				System.out.println("Let's build a pizza!");
				Pizza pizza = buildPizza(reader, orderId);
	
				if (pizza == null) {
					System.out.println("Invalid input, Returning to menu...");
					return;
				}
				DBNinja.addPizza(pizza);
				System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
				addMorePizza = !"-1".equals(reader.readLine());
				totalOrderCost += pizza.getBusPrice();
				totalOrderPrice += pizza.getCustPrice();
			} while (addMorePizza);
	
			order.setBusPrice(totalOrderCost);
	
			order.setCustPrice(totalOrderPrice);
	
			System.out.println("Do you want to add discounts to this order? Enter y/n?");
			boolean addDiscounts = "y".equalsIgnoreCase(reader.readLine());
			while (addDiscounts) {
				ArrayList<Discount> discounts = DBNinja.getDiscountList();
				for (Discount discount : discounts) {
					System.out.println(discount);
				}
				System.out.println("Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				int discountId = Integer.parseInt(reader.readLine());
				if (discountId == -1) {
					break;
				} else {
					Discount selectedDiscount = getDiscountById(discounts, discountId);
					if (selectedDiscount != null) {
						DBNinja.useOrderDiscount(order, selectedDiscount);
					}
				}
				System.out.println("Do you want to add more discounts to this order? Enter y/n?");
				addDiscounts = "y".equalsIgnoreCase(reader.readLine());
			}
			DBNinja.updateOrderDetails(order);
	
			System.out.println("Finished adding order...Returning to menu...");
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid numerical value.");
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO Exception: " + e.getMessage());
		}
	}
	
	private static Discount getDiscountById(ArrayList<Discount> discounts, int id) {
		for (Discount d : discounts) {
			if (d.getDiscountID() == id) {
				return d;
			}
		}
		return null;
	}
	
	

	

		 /* User Input Prompts...
		System.out.println("Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the number of your choice:");
		System.out.println("Is this order for an existing customer? Answer y/n: ");
		System.out.println("Here's a list of the current customers: ");
		System.out.println("Which customer is this order for? Enter ID Number:");
		System.out.println("ERROR: I don't understand your input for: Is this order an existing customer?");
		System.out.println("What is the table number for this order?");
		System.out.println("Let's build a pizza!");
		System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
		System.out.println("Do you want to add discounts to this order? Enter y/n?");
		System.out.println("Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
		System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
		System.out.println("What is the Street for this order? (e.g., Smile Street)");
		System.out.println("What is the City for this order? (e.g., Greenville)");
		System.out.println("What is the State for this order? (e.g., SC)");
		System.out.println("What is the Zip Code for this order? (e.g., 20605)");
		
		
		System.out.println("Finished adding order...Returning to menu...");
	}
	*/
	
	public static void viewCustomers ()  throws SQLException, IOException{
		try {
			/*
			 * Simply print out all of the customers from the database.
			 */
	
			ArrayList<Customer> customers;
			customers = DBNinja.getCustomerList();
			// System.out.println("CustomerID | Customer First Name | Customer Last Name | Customer Phone Number");
			// System.out.println("-------------------------------------------------------------");
			for (Customer customer : customers) {
				System.out.println("CustID=" + customer.getCustID() + " | " + "Name=" + customer.getFName() + " " + customer.getLName() + " , Phone= " + customer.getPhone());
			}
		} catch (SQLException | IOException e) {
			System.err.println("Error occurred while displaying the customers: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static Integer EnterCustomer() throws SQLException, IOException {
		try {


            System.out.println("What is this customer's name (First Name <space> Last Name): ");
            Integer newCustID;
            String name = reader.readLine().trim();

            if (name.isEmpty()) {
                System.out.println("Incorrect entry, not an option");
                return null;
            }

            int spaceIndex = name.indexOf(' ');
            if (spaceIndex == -1 || spaceIndex == 0 || spaceIndex == name.length() - 1) {
                throw new IllegalArgumentException("Invalid name format. Please enter both first and last names.");
            }

            String firstName = name.substring(0, spaceIndex);
            String lastName = name.substring(spaceIndex + 1);

            System.out.println("What is this customer's phone number (##########) (No dash/space): ");
            String phone = reader.readLine().trim();

            if (phone.isEmpty()) {
                System.out.println("Incorrect entry, not an option");
                return null;
            }

            if (!phone.matches("\\d{10}")) {
                throw new IllegalArgumentException("Invalid phone number format. Please enter 10 digits without dash or space.");
            }

            String fullAddress = "street" + ", " + "city" + ", " + "state" + ", " + "29631";
            Customer cust = new Customer(0, firstName, lastName, phone);
            cust.setAddress(fullAddress);

            newCustID = DBNinja.addCustomer(cust);
            return newCustID;

        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return null;
        } catch (SQLException | IOException e) {
            System.err.println("An error occurred during the customer adding process: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
	
	

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException {
		try {
			//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Would you like to:\n(a) display all orders [open or closed]\n(b) display all open orders\n(c) display all completed [closed] orders\n(d) display orders since a specific date");
		ArrayList<Order> ord = null;
		String choice = reader.readLine();

			switch (choice) {
				case "a":
					ord = DBNinja.getOrders(false); // Get all orders
					break;
				case  "b":
					ord = DBNinja.getOrders(true); // Get open orders
					break;
				case "c":
					ord = DBNinja.getOrders(false); // Get all orders and filter for completed ones
					ord.removeIf(o -> o.getIsComplete() == 0);
					break;
				case "d":
					System.out.println("What is the date you want to restrict by? (FORMAT= YYYY-MM-DD)");
					String orddate = reader.readLine();
					ord = DBNinja.getOrdersByDate(orddate);
					break;
				default:
					System.out.println("Invalid choice, returning to menu");
					return;
			}
	
			for (Order order : ord) {
				System.out.println(order.toSimplePrint());
			}
	

			System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
			int specificOrderID = Integer.parseInt(reader.readLine());
			Map<Integer, Order> ordMap = ord.stream().collect(Collectors.toMap(Order::getOrderID, Function.identity()));
			Order specOrder = ordMap.get(specificOrderID);
	
			if (specOrder != null) {
				System.out.println(specOrder.toString());
			} else {
				System.out.println("Order not found or incorrect entry, returning to menu.");
			}
	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	


	

	
	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException {
		/*
		 * All orders that are created through java (part 3, not the orders from part 2) should start as incomplete
		 * 
		 * When this method is called, you should print all of the "open" orders and allow the user to choose which of the incomplete orders they wish to mark as complete
		 */
	try {
		ArrayList<Order> ord = DBNinja.getCurrentOrders(0);
	
		if (ord.isEmpty()) {
			System.out.println("There are no open orders currently... returning to menu...");
			return;
		}
	
		for (Order order : ord) {
			System.out.println(order.toSimplePrint());
		}
	
		// User Input Prompts...
		System.out.println("Which order would you like to mark as complete? Enter the OrderID: ");
		
		
			//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			Integer ordID = Integer.parseInt(reader.readLine());

			DBNinja.CompleteOrder(ordID);
			
			System.out.println("Order with ID " + ordID + " marked as complete.");
		} catch (NumberFormatException e) {
			System.out.println("Incorrect entry, not an option");
		}catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	

	public static void ViewInventoryLevels() throws SQLException, IOException {
		/*
		 * Print the inventory. Display the topping ID, name, and current inventory
		 */
		try {
		List<Topping> top = DBNinja.printInventory();
		
		if (!top.isEmpty()) {
			System.out.println(" Here's the Toppings List         ");
			System.out.println(" Id   | Topping              | Current Inventory Level ");
			System.out.println("-------------------------------------------");
			
			for (Topping topping : top) {
				System.out.printf(" %-4s | %-20s | %4s %n",
						topping.getTopID(), topping.getTopName(), topping.getCurINVT());
			}
		} else {
			System.out.println("Inventory is empty.");
		}
	} catch (SQLException | IOException e) {
		System.err.println("Error occurred while displaying the inventory: " + e.getMessage());
		e.printStackTrace();
	}
}
 
	


	public static void AddInventory() throws SQLException, IOException {
		/*
		 * This should print the current inventory and then ask the user which topping (by ID) they want to add more to and how much to add
		 */
		
		try {
		ViewInventoryLevels();
		
		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
	
			System.out.println("Which topping do you want to add inventory to? Enter the number: ");
			Integer topID = Integer.parseInt(reader.readLine());
			
			System.out.println("How many units would you like to add? ");
			Double extras = Double.parseDouble(reader.readLine());
			
			Topping t1 = new Topping(topID, "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0);
			

			DBNinja.addToInventory(t1, extras);
			
			System.out.println("Inventory updated successfully.");
		} catch (NumberFormatException e) {
			System.out.println("Incorrect input format. Please enter a valid number.");
		} catch (SQLException | IOException e){
			System.out.println("An error occurred while updating the inventory: " + e.getMessage());
		}
	}
	

	// A method that builds a pizza. Used in our add new order method
	public static Pizza buildPizza(BufferedReader reader, int orderID) throws SQLException, IOException {
		try {
		System.out.println("What size is the pizza?");
		System.out.println("1." + DBNinja.size_s);
		System.out.println("2." + DBNinja.size_m);
		System.out.println("3." + DBNinja.size_l);
		System.out.println("4." + DBNinja.size_xl);
		System.out.print("Enter the corresponding number: ");
		String sizeChoice = reader.readLine();
		if (sizeChoice.isEmpty()) {
			System.out.println("Returning to menu...");
			return null; 
		}
		System.out.println("What crust for this pizza?");
		System.out.println("1." + DBNinja.crust_thin);
		System.out.println("2." + DBNinja.crust_orig);
		System.out.println("3." + DBNinja.crust_pan);
		System.out.println("4." + DBNinja.crust_gf);
		System.out.print("Enter the corresponding number: ");
		String crustChoice = reader.readLine();
	if (crustChoice.isEmpty()) {
			System.out.println("Returning to menu...");
			return null; 
		}

		Pizza pizza = new Pizza(0, sizeChoice, crustChoice, orderID, "Pending", "", 0.0, 0.0);

		boolean moreToppings = true;
while (moreToppings) {
    System.out.println("Available Toppings:");
    List<Topping> toppings = DBNinja.getToppingList();
    for (Topping t : toppings) {
        System.out.println(t.getTopID() + ": " + t.getTopName());
    }
    System.out.print("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings: ");
    String input = reader.readLine().trim();
    
    if (input.equals("-1")) {
        moreToppings = false; 
    } else if (input.isEmpty()) {
        System.out.println("Returning to menu...");
        return null; 
    } else {
        try {
            int topId = Integer.parseInt(input);
            Topping selectedTopping = getToppingById(toppings, topId);
            if (selectedTopping != null) {
                System.out.print("Do you want to add extra topping? Enter y/n: ");
                boolean isExtra = "y".equalsIgnoreCase(reader.readLine());
				isExtra = input.isEmpty();
				if (isExtra) {
					System.out.println("Incorrect entry, not an option");
				
				}
                pizza.addToppings(selectedTopping, isExtra);
                DBNinja.useTopping(pizza, selectedTopping, isExtra);
            } else {
                System.out.println("We don't have enough of that topping to add it...");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numerical value.");

        }
    }
}
DBNinja.addPizza(pizza);

System.out.print("Do you want to add discounts to this Pizza? Enter y/n: ");
String input = reader.readLine().trim();
boolean addDiscounts = "y".equalsIgnoreCase(input);

if (addDiscounts) {
    while (addDiscounts) {
        ArrayList<Discount> discounts = DBNinja.getDiscountList();
        for (Discount d : discounts) {
            System.out.println(d.getDiscountID() + ": " + d.getDiscountName());
        }
        System.out.print("Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
        int discId = Integer.parseInt(reader.readLine());
        if (discId == -1) {
            addDiscounts = false;
            continue;
        }

        Discount selectedDiscount = getDiscountById(discounts, discId);
        if (selectedDiscount != null) {
            pizza.addDiscounts(selectedDiscount);
            // System.out.println("Test Pizza Discount cost price:"+pizza.getCustPrice()+" cost:"+pizza.getBusPrice());

            DBNinja.usePizzaDiscount(pizza, selectedDiscount);
            System.out.print("Do you want to add more discounts to this Pizza? Enter y/n: ");
            input = reader.readLine().trim();
            addDiscounts = "y".equalsIgnoreCase(input);
        }
    }
} else {
    System.out.println("Returning to menu");

}

return pizza;

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numerical value.");
            return null;
        } catch (SQLException | IOException e) {
            System.err.println("An error occurred while building the pizza: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

	private static Topping getToppingById(List<Topping> toppings, int id) {
		for (Topping t : toppings) {
			if (t.getTopID() == id) {
				return t;
			}
		}
		return null;
	}
	
	
	
	

		/* User Input Prompts...
		System.out.println("What size is the pizza?");
		System.out.println("1."+DBNinja.size_s);
		System.out.println("2."+DBNinja.size_m);
		System.out.println("3."+DBNinja.size_l);
		System.out.println("4."+DBNinja.size_xl);
		System.out.println("Enter the corresponding number: ");
		System.out.println("What crust for this pizza?");
		System.out.println("1."+DBNinja.crust_thin);
		System.out.println("2."+DBNinja.crust_orig);
		System.out.println("3."+DBNinja.crust_pan);
		System.out.println("4."+DBNinja.crust_gf);
		System.out.println("Enter the corresponding number: ");
		System.out.println("Available Toppings:");
		System.out.println("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings: ");
		System.out.println("Do you want to add extra topping? Enter y/n");
		System.out.println("We don't have enough of that topping to add it...");
		System.out.println("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings: ");
		System.out.println("Do you want to add discounts to this Pizza? Enter y/n?");
		System.out.println("Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
		System.out.println("Do you want to add more discounts to this Pizza? Enter y/n?");
	*/ 
		
		
	
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		/*
		 * This method asks the use which report they want to see and calls the DBNinja method to print the appropriate report.
		 * 
		 */

		// User Input Prompts...
		try{
			//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Which report do you wish to print? Enter\n(a) ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");
		String option = reader.readLine();

		switch (option) {
			case "a":
				DBNinja.printToppingPopReport();

				break;
			case "b":
				DBNinja.printProfitByPizzaReport();
				break;
			case "c":
				DBNinja.printProfitByOrderType();
				break;
				default:
        System.out.println("I don't understand that input... returning to menu...");
        break;
		}
	}
		catch (SQLException | IOException e) {
            System.err.println("An error occurred" + e.getMessage());
            e.printStackTrace();
        }
	
	

	}

	//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
	// DO NOT EDIT ANYTHING BELOW HERE, THIS IS NEEDED TESTING.
	// IF YOU EDIT SOMETHING BELOW, IT BREAKS THE AUTOGRADER WHICH MEANS YOUR GRADE WILL BE A 0 (zero)!!

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	/*
	 * autograder controls....do not modiify!
	 */

	public final static String autograder_seed = "6f1b7ea9aac470402d48f7916ea6a010";

	
	private static void autograder_compilation_check() {

		try {
			Order o = null;
			Pizza p = null;
			Topping t = null;
			Discount d = null;
			Customer c = null;
			ArrayList<Order> alo = null;
			ArrayList<Discount> ald = null;
			ArrayList<Customer> alc = null;
			ArrayList<Topping> alt = null;
			double v = 0.0;
			String s = "";

			DBNinja.addOrder(o);
			DBNinja.addPizza(p);
			DBNinja.useTopping(p, t, false);
			DBNinja.usePizzaDiscount(p, d);
			DBNinja.useOrderDiscount(o, d);
			DBNinja.addCustomer(c);
			DBNinja.completeOrder(o);
			alo = DBNinja.getOrders(false);
			o = DBNinja.getLastOrder();
			alo = DBNinja.getOrdersByDate("01/01/1999");
			ald = DBNinja.getDiscountList();
			d = DBNinja.findDiscountByName("Discount");
			alc = DBNinja.getCustomerList();
			c = DBNinja.findCustomerByPhone("0000000000");
			alt = DBNinja.getToppingList();
			t = DBNinja.findToppingByName("Topping");
			DBNinja.addToInventory(t, 1000.0);
			v = DBNinja.getBaseCustPrice("size", "crust");
			v = DBNinja.getBaseBusPrice("size", "crust");
			DBNinja.printInventory();
			DBNinja.printToppingPopReport();
			DBNinja.printProfitByPizzaReport();
			DBNinja.printProfitByOrderType();
			s = DBNinja.getCustomerName(0);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}


}


