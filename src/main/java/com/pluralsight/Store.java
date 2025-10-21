package com.pluralsight;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Store {

    private static final HashMap<String, Integer> productsWithQuantity = new HashMap<>();

    public static void main(String[] args) {

        // Create lists for inventory and the shopping cart
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        // Load inventory from the data file (pipe-delimited: id|name|price)
        loadInventory("products.csv", inventory);

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
        for (Product product : inventory) {
            System.out.println(product);
        }

        System.out.println("\nEnter the sku/id of the product you want to add to cart or ('X' to return): ");
        String userInputSku = scanner.nextLine().trim().toLowerCase();

        if (userInputSku.equalsIgnoreCase("x")) {
            System.out.println("Returning to main menu");
        } else {

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
        // TODO:
        //   • list each product in the cart
        //   • compute the total cost
        //   • ask the user whether to check out (C) or return (X)
        //   • if C, call checkOut(cart, totalAmount, scanner)

        System.out.println("\nYour cart");
        System.out.println("---------");

        double totalPrice = 0;

        for (Product product : cart) {
            productsWithQuantity.put(product.getProductName(),
                    productsWithQuantity.getOrDefault(product.getProductName(), 0) + 1);
            totalPrice += product.getPrice();
        }

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
        // TODO: implement steps listed above
        System.out.println("Total amount owed: $" + totalAmount);

        System.out.println("Proceed with purchase? (Y/N)");
        String purchase = scanner.nextLine().trim();

        if (purchase.equalsIgnoreCase("y")) {
            System.out.print("Enter payment amount: $");
            double customerPayment = scanner.nextDouble();
            scanner.nextLine();

            double customerChange = customerPayment - totalAmount;

            if (customerChange < 0) {
                System.out.println("Sorry that is not enough, returning your payment");
            } else {
                System.out.println("\nOrder Date: " + LocalDate.now());
                System.out.println("\nItems purchased:");

                for (String s : productsWithQuantity.keySet()) {
                    System.out.println(productsWithQuantity.get(s) + "x " + s);
                }

                System.out.println("\nSales Total: " + totalAmount);
                System.out.printf("Amount Paid: $%.2f", customerPayment);
                System.out.printf("\nChange Given: %.2f\n\n", customerChange);

                try {
                    //Make Folder
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmm");
                    String formattedCurrentDate = LocalDate.now().format(dateFormat);
                    String formattedCurrentTime = LocalTime.now().format(timeFormat);
                    String formattedCustomerPayment = String.format("%.2f", customerPayment);
                    String formattedCustomerChange = String.format("%.2f", customerChange);

                    //Manually saving it to the same file for testing
                    String fileName = formattedCurrentDate + formattedCurrentTime + ".txt";
                    BufferedWriter myWriter = new BufferedWriter(new FileWriter("202510191041.txt"));

                    myWriter.write("Order Date: " + LocalDate.now() + "\n");
                    myWriter.write("Items purchased:\n\n");

                    for (String s : productsWithQuantity.keySet()) {
                        myWriter.write(productsWithQuantity.get(s) + "x " + s + "\n");
                    }

                    myWriter.write("\nSales Total: " + totalAmount + "\n");
                    myWriter.write("Amount Paid: " + formattedCustomerPayment + "\n");
                    myWriter.write("Change Given: $" + formattedCustomerChange + "\n");

                    myWriter.close();
                } catch (Exception exception) {
                    System.out.println("Error occurred while writing the file");
                    System.err.println(exception.getMessage());
                }

                cart.clear();
                productsWithQuantity.clear();
                System.out.println("Thank you for your purchase!");
            }
        }
    }

    /**
     * Searches a list for a product by its id.
     *
     * @return the matching Product, or null if not found
     */
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        // TODO: loop over the list and compare ids

        for (Product product : inventory) {
            if (product.getSku().equalsIgnoreCase(id)) {
                return product;
            }
        }

        return null;
    }
}

 