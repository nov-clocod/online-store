package com.pluralsight;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Store {

    //Storing product and quantity
    private static final HashMap<String, Integer> productsWithQuantity = new HashMap<>();

    public static void main(String[] args) {

        // Create lists for inventory and the shopping cart
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        // Load inventory from the data file (pipe-delimited: id|name|price)
        loadInventory("products.csv", inventory);

        //Make Folder if it doesn't exist
        File receiptsFolder = new File("receiptsFolder");

        if (!receiptsFolder.exists()) {
            if (receiptsFolder.mkdir()){
                System.out.println("Receipts Folder Created!");
            }
        }

        // Main menu loop
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to the Online Store!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");
            System.out.println("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> displayProducts(inventory, cart, scanner);
                case 2 -> displayCart(cart, scanner);
                case 3 -> System.out.println("\nThank you for shopping with us!");
                default -> System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    /**
     * Reads product data from a file and populates the inventory list.
     * File format (pipe-delimited):
     * id|name|price
     * <p>
     * Example line:
     * A17|Wireless Mouse|19.99
     */
    public static void loadInventory(String fileName, ArrayList<Product> inventory) {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = myReader.readLine()) != null) {
                String[] section = line.split("\\|");
                String sku = section[0];
                String productName = section[1];
                double price = Double.parseDouble(section[2]);

                inventory.add(new Product(sku, productName, price));
            }

            myReader.close();

        } catch (Exception exception) {
            System.out.println("Error reading the file");
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Displays all products and lets the user add one to the cart.
     * Typing X returns to the main menu.
     */
    public static void displayProducts(ArrayList<Product> inventory,
                                       ArrayList<Product> cart,
                                       Scanner scanner) {

        System.out.println("\nProducts");
        System.out.println("--------");

        //Displays all products
        for (Product product : inventory) {
            System.out.println(product);
        }

        System.out.println("\nEnter the sku/id of the product you want to add to cart or ('X' to return): ");
        String userInputSku = scanner.nextLine().trim().toLowerCase();

        if (userInputSku.equalsIgnoreCase("x")) {
            System.out.println("Returning to main menu");
        } else {

            //Finds product with user input and store to a variable for reusability or returns null
            Product productFound = findProductById(userInputSku, inventory);

            if (productFound != null) {
                cart.add(productFound);
                System.out.println("Added \""+ productFound.getProductName() + "\" to your cart!");
            } else {
                System.out.println("We don't have that product that you entered: " + userInputSku);
            }

        }
    }

    /**
     * Shows the contents of the cart, calculates the total,
     * and offers the option to check out.
     */
    public static void displayCart(ArrayList<Product> cart, Scanner scanner) {

        System.out.println("\nYour cart");
        System.out.println("---------");

        //Declare total price in cart
        double totalPrice = 0;

        //Loops cart and saves the product into hashmap for product name and updates quantity
        for (Product product : cart) {
            productsWithQuantity.put(product.getProductName(),
                    productsWithQuantity.getOrDefault(product.getProductName(), 0) + 1);
            totalPrice += product.getPrice();
        }

        //Displays the products from hashmap
        for (String s : productsWithQuantity.keySet()) {
            System.out.println(productsWithQuantity.get(s) + "x " + s);
        }

        if (totalPrice == 0) {
            System.out.println("Your cart is empty, Returning to home screen");
            return;
        }

        System.out.println("Total Amount: $" + totalPrice);

        System.out.println("\nPress 'C' to checkout, Press 'X' to return to menu");
        String checkoutOrReturn = scanner.nextLine().trim();

        if (checkoutOrReturn.equalsIgnoreCase("c")) {
            checkOut(cart, totalPrice, scanner);
        }
    }

    /**
     * Handles the checkout process:
     * 1. Confirm that the user wants to buy.
     * 2. Accept payment and calculate change.
     * 3. Display a simple receipt.
     * 4. Clear the cart.
     */
    public static void checkOut(ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {

        System.out.println("Total amount owed: $" + totalAmount);

        System.out.println("Proceed with purchase? (Y/N)");
        String purchase = scanner.nextLine().trim();

        //Catches invalid inputs or errors
        try {
            if (purchase.equalsIgnoreCase("y")) {
                System.out.print("Enter payment amount: $");
                double customerPayment = scanner.nextDouble();
                scanner.nextLine();

                double customerChange = customerPayment - totalAmount;

                //Checks if customer paid enough
                if (customerChange < 0) {
                    System.out.println("Sorry that is not enough, returning your payment and back to the main menu");
                } else {
                    System.out.println("\nOrder Date: " + LocalDate.now());
                    System.out.println("\nItems purchased:");

                    for (String s : productsWithQuantity.keySet()) {
                        System.out.println(productsWithQuantity.get(s) + "x " + s);
                    }

                    System.out.println("\nSales Total: " + totalAmount);
                    System.out.printf("Amount Paid: $%.2f", customerPayment);
                    System.out.printf("\nChange Given: %.2f\n\n", customerChange);

                    //Date and time formatters
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmm");
                    String formattedCurrentDate = LocalDate.now().format(dateFormat);
                    String formattedCurrentTime = LocalTime.now().format(timeFormat);

                    //Payment and change formatting
                    String formattedCustomerPayment = String.format("%.2f", customerPayment);
                    String formattedCustomerChange = String.format("%.2f", customerChange);

                    /*Manually saving it to the same file for testing
                    Can convert to dynamically, just need to replace the hardcoded
                    date + time.txt section to fileName variable*/
                    String fileName = formattedCurrentDate + formattedCurrentTime + ".txt";
                    BufferedWriter myWriter = new BufferedWriter(new FileWriter("receiptsFolder/202510191041.txt"));

                    //Writes the sales information to the file
                    myWriter.write("Order Date: " + LocalDate.now() + "\n");
                    myWriter.write("Items purchased:\n\n");

                    for (String s : productsWithQuantity.keySet()) {
                        myWriter.write(productsWithQuantity.get(s) + "x " + s + "\n");
                    }

                    myWriter.write("\nSales Total: " + totalAmount + "\n");
                    myWriter.write("Amount Paid: " + formattedCustomerPayment + "\n");
                    myWriter.write("Change Given: $" + formattedCustomerChange + "\n");

                    //Close writer, clear cart, and hashmap
                    myWriter.close();
                    cart.clear();
                    productsWithQuantity.clear();

                    System.out.println("Thank you for your purchase!");
                }

            } else {
                System.out.println("Returning to main menu");
            }
        } catch (Exception exception) {
            scanner.nextLine();
            System.out.println("Error occurred check your inputs or file");
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Searches a list for a product by its id.
     *
     * @return the matching Product, or null if not found
     */
    public static Product findProductById(String id, ArrayList<Product> inventory) {

        for (Product product : inventory) {
            if (product.getSku().equalsIgnoreCase(id)) {
                return product;
            }
        }

        return null;
    }
}

 