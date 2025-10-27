package com.cafepos.demo;

// java
import java.util.Scanner;
import java.util.InputMismatchException;
// cafepos
import com.cafepos.domain.*;
import com.cafepos.factory.ProductFactory;
import com.cafepos.decorator.*;
import com.cafepos.catalog.*;
import com.cafepos.common.Money;
import com.cafepos.payment.*;
import com.cafepos.observer.*;
import com.cafepos.pricing.*;

public final class InteractiveCLI {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductFactory factory = new ProductFactory();
    private static final TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
    private static final ReceiptPrinter receiptPrinter = new ReceiptPrinter();

    public static void main(String[] args) {

        System.out.println("\n========================================");
        System.out.println("   Welcome to Cafe POS System");
        System.out.println("========================================\n");

        boolean running = true;

        while (running) {
            try {
                Order order = new Order(OrderIds.next());

                // Register observers
                order.register(new KitchenDisplay());
                order.register(new DeliveryDesk());
                order.register(new CustomerNotifier());

                // Build the order
                boolean orderingComplete = false;
                while (!orderingComplete) {
                    System.out.println("\n--- Order Menu ---");
                    System.out.println("1. Add item to order");
                    System.out.println("2. View current order");
                    System.out.println("3. Proceed to payment");
                    System.out.println("4. Cancel order");
                    System.out.print("Choose an option: ");

                    int choice = getIntInput();

                    switch (choice) {
                        case 1 -> {
                            if (!addItemToOrder(order)) {
                                // User cancelled, go back to main menu
                            }
                        }
                        case 2 -> viewOrder(order);
                        case 3 -> {
                            if (order.items().isEmpty()) {
                                System.out.println("\n[ERROR] Cannot proceed to payment - order is empty!");
                                System.out.println("Please add at least one item to your order.");
                            } else {
                                if (processPayment(order)) {
                                    orderingComplete = true;
                                }
                            }
                        }
                        case 4 -> {
                            System.out.println("\n[INFO] Order cancelled.");
                            orderingComplete = true;
                        }
                        default -> System.out.println("\n[ERROR] Invalid option. Please choose 1-4.");
                    }
                }

                // Ask if user wants to continue
                System.out.print("\nWould you like to place another order? (y/n): ");
                String continueChoice = scanner.nextLine().trim().toLowerCase();
                running = continueChoice.equals("y") || continueChoice.equals("yes");

            } catch (Exception e) {
                System.out.println("\n[ERROR] An unexpected error occurred: " + e.getMessage());
                System.out.println("Please try again.\n");
            }
        }

        System.out.println("\nThank you for using Cafe POS System. Goodbye!\n");
        scanner.close();
    }

    private static boolean addItemToOrder(Order order) {
        try {
            // Choose base product
            System.out.println("\n--- Select Base Product ---");
            System.out.println("1. Espresso ($2.50)");
            System.out.println("2. Latte ($3.20)");
            System.out.println("3. Cappuccino ($3.00)");
            System.out.println("0. Go back");
            System.out.print("Choose a drink: ");

            int drinkChoice = getIntInput();

            if (drinkChoice == 0) {
                return false; // User chose to go back
            }

            String baseCode;
            switch (drinkChoice) {
                case 1 -> baseCode = "ESP";
                case 2 -> baseCode = "LAT";
                case 3 -> baseCode = "CAP";
                default -> {
                    System.out.println("\n[ERROR] Invalid drink choice.");
                    return false;
                }
            }

            // Choose add-ons
            StringBuilder recipe = new StringBuilder(baseCode);
            boolean addingAddons = true;

            while (addingAddons) {
                System.out.println("\n--- Add-ons Menu ---");
                System.out.println("1. Extra Shot (+$0.80)");
                System.out.println("2. Oat Milk (+$0.50)");
                System.out.println("3. Syrup (+$0.40)");
                System.out.println("4. Large Size (+$0.70)");
                System.out.println("5. No more add-ons");
                System.out.println("0. Go back");
                System.out.print("Choose an add-on: ");

                int addonChoice = getIntInput();

                switch (addonChoice) {
                    case 0 -> {
                        return false; // User chose to go back
                    }
                    case 1 -> recipe.append("+SHOT");
                    case 2 -> recipe.append("+OAT");
                    case 3 -> recipe.append("+SYP");
                    case 4 -> recipe.append("+L");
                    case 5 -> addingAddons = false;
                    default -> System.out.println("\n[ERROR] Invalid add-on choice.");
                }
            }

            // Get quantity
            System.out.print("\nEnter quantity (0 to go back): ");
            int quantity = getIntInput();

            if (quantity == 0) {
                return false; // User chose to go back
            }

            if (quantity < 0) {
                System.out.println("\n[ERROR] Quantity must be greater than 0.");
                return false;
            }

            // Create product and add to order
            Product product = factory.create(recipe.toString());
            order.addItem(new LineItem(product, quantity));

            Money itemPrice = (product instanceof Priced p) ? p.price() : product.basePrice();
            Money lineTotal = itemPrice.multiply(quantity);

            System.out.println("\n[SUCCESS] Added: " + product.name() + " x" + quantity);
            System.out.println("Line total: " + lineTotal);
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("\n[ERROR] Failed to add item: " + e.getMessage());
            return false;
        }
    }

    private static void viewOrder(Order order) {
        System.out.println("\n========================================");
        System.out.println("         Current Order #" + order.id());
        System.out.println("========================================");

        if (order.items().isEmpty()) {
            System.out.println("Order is empty.");
        } else {
            for (LineItem item : order.items()) {
                System.out.printf(" - %s x%d = %s%n",
                    item.product().name(),
                    item.quantity(),
                    item.lineTotal());
            }
            System.out.println("----------------------------------------");
            System.out.println("Subtotal: " + order.subtotal());
            System.out.println("Tax (" + taxPolicy.getPercent() + "%): " + order.taxAtPercent(taxPolicy.getPercent()));
            System.out.println("Total: " + order.totalWithTax(taxPolicy.getPercent()));
        }
        System.out.println("========================================");
    }

    private static boolean processPayment(Order order) {
        try {
            // Show final order
            viewOrder(order);

            if (order.subtotal().equals(Money.zero())) {
                System.out.println("\n[ERROR] Cannot process payment - order total is zero!");
                return false;
            }

            // Apply discount
            DiscountPolicy discountPolicy = selectDiscountPolicy();
            PricingService pricingService = new PricingService(discountPolicy, taxPolicy);
            PricingResult pricingResult = pricingService.price(order.subtotal());

            // Show receipt with discount applied
            System.out.println("\n========================================");
            System.out.println("         Final Receipt");
            System.out.println("========================================");
            System.out.println("Order #" + order.id());
            for (LineItem item : order.items()) {
                System.out.printf(" - %s x%d = %s%n",
                    item.product().name(),
                    item.quantity(),
                    item.lineTotal());
            }
            System.out.println("----------------------------------------");
            System.out.println("Subtotal: " + pricingResult.subtotal());
            if (pricingResult.discount().asBigDecimal().signum() > 0) {
                System.out.println("Discount: -" + pricingResult.discount());
            }
            System.out.println("Tax (" + taxPolicy.getPercent() + "%): " + pricingResult.tax());
            System.out.println("Total: " + pricingResult.total());
            System.out.println("========================================");

            // Choose payment method
            System.out.println("\n--- Payment Method ---");
            System.out.println("1. Cash");
            System.out.println("2. Card");
            System.out.println("3. Wallet");
            System.out.println("0. Go back");
            System.out.print("Choose payment method: ");

            int paymentChoice = getIntInput();

            if (paymentChoice == 0) {
                return false; // User chose to go back
            }

            Money totalAmount = pricingResult.total();
            PaymentStrategy paymentStrategy;
            switch (paymentChoice) {
                case 1 -> {
                    if (!processCashPayment(totalAmount)) {
                        return false; // User cancelled or payment failed
                    }
                    paymentStrategy = new CashPayment();
                }
                case 2 -> {
                    System.out.print("Enter card number (16 digits, 0 to go back): ");
                    String cardNumber = scanner.nextLine().trim();
                    if (cardNumber.equals("0")) {
                        return false; // User chose to go back
                    }
                    if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
                        System.out.println("\n[ERROR] Invalid card number. Must be 16 digits.");
                        return false;
                    }
                    paymentStrategy = new CardPayment(cardNumber);
                }
                case 3 -> {
                    System.out.print("Enter wallet ID (0 to go back): ");
                    String walletId = scanner.nextLine().trim();
                    if (walletId.equals("0")) {
                        return false; // User chose to go back
                    }
                    if (walletId.isEmpty()) {
                        System.out.println("\n[ERROR] Wallet ID cannot be empty.");
                        return false;
                    }
                    paymentStrategy = new WalletPayment(walletId);
                }
                default -> {
                    System.out.println("\n[ERROR] Invalid payment method.");
                    return false;
                }
            }

            // Process payment
            System.out.println("\nProcessing payment...");
            order.pay(paymentStrategy);

            // Mark order ready
            System.out.println("\nPreparing order...");
            Thread.sleep(1000); // Simulate preparation time
            order.markReady();

            System.out.println("\n========================================");
            System.out.println("  Payment successful! Order complete.");
            System.out.println("========================================");
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("\n[ERROR] Payment failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean processCashPayment(Money totalAmount) {
        System.out.println("\n========================================");
        System.out.println("         Cash Payment");
        System.out.println("========================================");
        System.out.println("Total to pay: $" + totalAmount);
        System.out.println("Available denominations: 1, 5, 10, 20, 50, 100");
        System.out.println("========================================\n");

        Money totalPaid = Money.zero();

        while (totalPaid.compareTo(totalAmount) < 0) {
            // Calculate remaining amount
            Money remaining = totalAmount.subtract(totalPaid);

            System.out.println("\nRemaining to pay: $" + remaining);
            System.out.println("Total paid so far: $" + totalPaid);
            System.out.println("\n--- Insert Cash ---");
            System.out.println("Enter denomination (1, 5, 10, 20, 50, 100)");
            System.out.print("Amount (0 to cancel): ");

            int denomination = getIntInput();

            if (denomination == 0) {
                System.out.println("\n[INFO] Payment cancelled.");
                return false; // User cancelled
            }

            // Validate denomination
            if (denomination != 1 && denomination != 5 && denomination != 10 &&
                denomination != 20 && denomination != 50 && denomination != 100) {
                System.out.println("\n[ERROR] Invalid denomination. Please use: 1, 5, 10, 20, 50, or 100");
                continue;
            }

            // Add to total paid
            Money payment = Money.of(denomination);
            totalPaid = totalPaid.add(payment);

            System.out.println("\n[INFO] Payment received: $" + payment);
        }

        // Check if we need to give change
        if (totalPaid.compareTo(totalAmount) > 0) {
            // Overpayment - calculate change
            Money change = totalPaid.subtract(totalAmount);
            System.out.println("\n[SUCCESS] Payment complete!");
            System.out.println("Total paid: $" + totalPaid);
            System.out.println("Change: $" + change);
        } else {
            // Exact payment
            System.out.println("\n[SUCCESS] Payment complete!");
            System.out.println("Total paid: $" + totalPaid);
            System.out.println("No change required.");
        }

        return true;
    }

    private static DiscountPolicy selectDiscountPolicy() {
        System.out.println("\n--- Apply Discount? ---");
        System.out.println("1. No discount");
        System.out.println("2. Loyalty discount (5%)");
        System.out.println("3. Loyalty discount (10%)");
        System.out.println("4. Fixed coupon ($1.00)");
        System.out.println("5. Fixed coupon ($2.00)");
        System.out.print("Choose discount: ");

        int choice = getIntInput();

        return switch (choice) {
            case 1 -> new NoDiscount();
            case 2 -> new LoyaltyPercentDiscount(5);
            case 3 -> new LoyaltyPercentDiscount(10);
            case 4 -> new FixedCouponDiscount(Money.of(1.00));
            case 5 -> new FixedCouponDiscount(Money.of(2.00));
            default -> {
                System.out.println("\n[INFO] Invalid choice, applying no discount.");
                yield new NoDiscount();
            }
        };
    }

    private static int getIntInput() {
        try {
            int value = scanner.nextInt();
            scanner.nextLine(); // consume newline
            return value;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // clear invalid input
            System.out.println("\n[ERROR] Invalid input. Please enter a number.");
            return -1;
        }
    }
}
