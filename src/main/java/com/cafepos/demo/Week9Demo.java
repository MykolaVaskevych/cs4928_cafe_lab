package com.cafepos.demo;

import com.cafepos.common.Money;
import com.cafepos.menu.*;
import com.cafepos.state.OrderFSM;

public final class Week9Demo {

  public static void main(String[] args) {
    System.out.println("========================================");
    System.out.println("Week 9 Demo: Composite + Iterator + State");
    System.out.println("========================================\n");

    // Part A & B: Composite + Iterator Pattern Demo
    System.out.println("--- Part A & B: Menu Hierarchy (Composite + Iterator) ---\n");

    Menu root = new Menu("CAFÉ MENU");

    Menu drinks = new Menu(" Drinks ");
    Menu coffee = new Menu("  Coffee ");
    Menu desserts = new Menu(" Desserts ");

    coffee.add(new MenuItem("Espresso", Money.of(2.50), true));
    coffee.add(new MenuItem("Latte (Large)", Money.of(3.90), true));

    drinks.add(coffee);

    desserts.add(new MenuItem("Cheesecake", Money.of(3.50), false));
    desserts.add(new MenuItem("Oat Cookie", Money.of(1.20), true));

    root.add(drinks);
    root.add(desserts);

    System.out.println("Full menu (depth-first traversal):");
    root.print();

    System.out.println("\nVegetarian options (filtered iteration):");
    for (MenuItem mi : root.vegetarianItems()) {
      System.out.println(" * " + mi.name() + " = " + mi.price());
    }

    // Part C: State Pattern Demo
    System.out.println("\n\n--- Part C: Order Lifecycle (State Pattern) ---\n");

    OrderFSM fsm = new OrderFSM();

    System.out.println("Initial status = " + fsm.status());
    System.out.println();

    System.out.println("Attempting to prepare before payment:");
    fsm.prepare();
    System.out.println();

    System.out.println("Processing payment:");
    fsm.pay();
    System.out.println("Status = " + fsm.status());
    System.out.println();

    System.out.println("Kitchen preparing order:");
    fsm.prepare();
    System.out.println();

    System.out.println("Marking order as ready:");
    fsm.markReady();
    System.out.println("Status = " + fsm.status());
    System.out.println();

    System.out.println("Delivering order:");
    fsm.deliver();
    System.out.println("Final status = " + fsm.status());

    System.out.println("\n========================================");
    System.out.println("Summary:");
    System.out.println(" - Composite allows uniform treatment of menus and items");
    System.out.println(" - Iterator enables depth-first traversal and filtering");
    System.out.println(" - State encapsulates lifecycle transitions without conditionals");
    System.out.println("========================================");
  }
}
