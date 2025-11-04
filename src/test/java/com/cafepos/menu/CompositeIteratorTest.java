package com.cafepos.menu;

import static org.junit.jupiter.api.Assertions.*;

import com.cafepos.common.Money;
import java.util.List;
import org.junit.jupiter.api.Test;

class CompositeIteratorTest {

  @Test
  void depth_first_iteration_collects_all_nodes() {
    Menu root = new Menu("ROOT");
    Menu a = new Menu("A");
    Menu b = new Menu("B");
    root.add(a);
    root.add(b);
    a.add(new MenuItem("x", Money.of(1.0), true));
    b.add(new MenuItem("y", Money.of(2.0), false));

    List<String> names =
        root.allItems().stream().map(MenuComponent::name).toList();

    assertTrue(names.contains("A"));
    assertTrue(names.contains("B"));
    assertTrue(names.contains("x"));
    assertTrue(names.contains("y"));
    assertEquals(4, names.size());
  }

  @Test
  void vegetarian_filter_returns_only_vegetarian_items() {
    Menu root = new Menu("ROOT");
    root.add(new MenuItem("Salad", Money.of(5.0), true));
    root.add(new MenuItem("Burger", Money.of(8.0), false));
    root.add(new MenuItem("Pasta", Money.of(7.0), true));

    List<MenuItem> vegItems = root.vegetarianItems();

    assertEquals(2, vegItems.size());
    assertTrue(vegItems.stream().allMatch(MenuItem::vegetarian));
    assertTrue(vegItems.stream().anyMatch(m -> m.name().equals("Salad")));
    assertTrue(vegItems.stream().anyMatch(m -> m.name().equals("Pasta")));
  }

  @Test
  void nested_menus_traverse_depth_first() {
    Menu root = new Menu("ROOT");
    Menu level1 = new Menu("Level1");
    Menu level2 = new Menu("Level2");

    root.add(level1);
    level1.add(level2);
    level2.add(new MenuItem("Deep", Money.of(3.0), false));

    List<MenuComponent> all = root.allItems();

    assertEquals(3, all.size());
    assertEquals("Level1", all.get(0).name());
    assertEquals("Level2", all.get(1).name());
    assertEquals("Deep", all.get(2).name());
  }

  @Test
  void empty_menu_returns_empty_iterator() {
    Menu empty = new Menu("Empty");

    List<MenuComponent> items = empty.allItems();

    assertTrue(items.isEmpty());
  }

  @Test
  void menu_with_only_items_no_submenus() {
    Menu root = new Menu("ROOT");
    root.add(new MenuItem("Item1", Money.of(1.0), true));
    root.add(new MenuItem("Item2", Money.of(2.0), false));
    root.add(new MenuItem("Item3", Money.of(3.0), true));

    List<MenuItem> vegItems = root.vegetarianItems();

    assertEquals(2, vegItems.size());
  }

  @Test
  void complex_hierarchy_traversal() {
    Menu root = new Menu("CAFÉ");
    Menu drinks = new Menu("Drinks");
    Menu coffee = new Menu("Coffee");
    Menu tea = new Menu("Tea");
    Menu food = new Menu("Food");

    coffee.add(new MenuItem("Espresso", Money.of(2.5), true));
    coffee.add(new MenuItem("Latte", Money.of(3.5), true));
    tea.add(new MenuItem("Green Tea", Money.of(2.0), true));

    drinks.add(coffee);
    drinks.add(tea);

    food.add(new MenuItem("Sandwich", Money.of(5.0), false));
    food.add(new MenuItem("Salad", Money.of(4.5), true));

    root.add(drinks);
    root.add(food);

    List<MenuComponent> all = root.allItems();
    List<MenuItem> veg = root.vegetarianItems();

    // Should include: Drinks, Coffee, Espresso, Latte, Tea, Green Tea, Food, Sandwich, Salad
    assertEquals(9, all.size());
    assertEquals(4, veg.size()); // Espresso, Latte, Green Tea, Salad
  }

  @Test
  void menu_item_name_and_price_accessible() {
    MenuItem item = new MenuItem("Test", Money.of(5.5), true);

    assertEquals("Test", item.name());
    assertEquals(Money.of(5.5), item.price());
    assertTrue(item.vegetarian());
  }

  @Test
  void menu_component_unsupported_operations_throw() {
    MenuItem item = new MenuItem("Test", Money.of(1.0), false);

    assertThrows(
        UnsupportedOperationException.class, () -> item.add(new MenuItem("Other", Money.of(2.0), false)));
    assertThrows(UnsupportedOperationException.class, () -> item.remove(null));
    assertThrows(UnsupportedOperationException.class, () -> item.getChild(0));
  }

  @Test
  void menu_add_remove_operations() {
    Menu menu = new Menu("Test");
    MenuItem item = new MenuItem("Item", Money.of(1.0), true);

    menu.add(item);
    assertEquals(1, menu.allItems().size());

    menu.remove(item);
    assertEquals(0, menu.allItems().size());
  }
}
