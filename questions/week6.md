# Week 6 Reflection: Name of the method ought to be determined by what it does

## Refactoring Code Smells

### What Odors Did You Take Away, and Where?

#### 1. God Class & Long Method

The entire `OrderManagerGod.process()` method was trying to do too much - 60+ lines in charge of everything, from creating products to printing receipts. Product creation, pricing, discounting, tax calculation, payment processing, and formatting are not a single method responsibility. That's insane.

I divided it into a bunch of smaller classes, each dealing with only one thing: `DiscountPolicy` for the discounts, `TaxPolicy` for handling taxes, `PricingService` for coordinating pricing, `ReceiptPrinter` for formatting, and the obligatory god class you tie everything together in – your preferred flavor of `CheckoutService`.

#### 2. Global/Static State

`OrderManagerGod` had `public static int TAX_PERCENT = 10` and `public static String LAST_DISCOUNT_CODE = null` just sitting there as global state that can be mutated. This is terrible because it means that it isn't thread-safe, and I can't write any tests around that code. This also creates all of these implicit dependencies everywhere in my system.

I got rid of them completely. This way `FixedRate` is injected with the given tax rate via its constructor into `FixedRateTaxPolicy`. No longer would global variables mess everything up.

#### 3. Primitive Obsession

Everything in the original code was based on strings — from discount codes (“LOYAL5” and “COUPON1”) to payment types (“CASH” and “CARD”). With raw strings, of course, there is no type safety, and you have to do all parsing manually with if-else chains.

Common types I entered under "proper" OOP in your list: `DiscountPolicy` is the interface with its concrete instances (`NoDiscount`, `LoyaltyPercentDiscount`, `FixedCouponDiscount`). Way cleaner, and the compiler is actually doing a lot of work catching errors now.

#### 4. Feature Envy

All of this discount logic, tax math & receipt formatting were just squeezed into `OrderManagerGod`, although all these are logically their own classes.

Fixed by moving:

- Push logic down into `DiscountPolicy` implementations
- Tax calculation into `TaxPolicy`
- Receipt formatting into `ReceiptPrinter`

And now each class has the actual data that it operates on.

#### 5. Shotgun Surgery Risk

Do you want another kind of discount? You could have edited the huge `process()` method and hope to not break anything. Same with changing tax rules. It takes just one change and ripples through the entire thing. And what you've done now is to just implement `DiscountPolicy` for a new discount type. There’s nothing that needs to be changed in existing code. Way safer.

#### 6. Duplicated Logic

Inline BigDecimal math was all over - multiply here, divide there, subtract over yonder. All kinds of magic numbers: fives, hundreds, tens. Easy to flub and tough to sustain.

I encapsulated all of the math operations in my policy classes using appropriately named and validated methods. No more mystery math in the air.

### What refactoring(s) did you perform and why?

#### Extract Class

Exposed the responsibilities out of the God Class and assigned them their own homes:

- Discount logic became `DiscountPolicy` interface with 3 implementations
- Tax logic became `TaxPolicy` with `FixedRateTaxPolicy`
- Formatting became `ReceiptPrinter`

This is SRP in essence (a.k.a. God Classes) - each class has one responsibility.

#### Extract Method

Long `process()` got extracted into smaller ones that you can actually test independently:

- Discount calculation extraction away from `DiscountPolicy`. `discountOf()`
- Tax calculation factored out to `TaxPolicy`. `taxOn()`
- Receipt format is now taken out to a separate class, `ReceiptPrinter`. `format()`

#### Replace Conditional with Polymorphism

Instead of having a bunch of if-else statements for kinds of discount codes, I leverage polymorphism.

Before: `if (discountCode.equals("LOYAL5"))... else if (discountCode.equals("COUPON1"))...`

After: Now just inject the right `DiscountPolicy` and invoke `discountOf()`. The correct behavior happens automatically.

#### Introduce Parameter Object

Instead of passing subsidy, discount, tax, and total as four separate Money, I wrapped them all into a `PricingResult` record. Cleaner, less spamming of parameters.

#### Constructor Injection (Dependency Injection)

Become rid of global state by making the dependencies explicit through constructors.

- `PricingService` takes a `DiscountPolicy` and a `TaxPolicy`.
- `CheckoutService` receives the instances of `ProductFactory`, `PricingService`, `ReceiptPrinter`, and tax percent

Makes dependencies explicit and testable. No more hidden global state "gotchas".

#### Remove Global State

Deleted the static fields entirely. Tax rate is now passed to the constructor. Removed discounter tracking as we no longer need it. Structures the code in a predictable and safe way.

### How Do Your New Design Meet SOLID Principles?

#### Single Responsibility Principle

Every class has exactly one responsibility:

- `DiscountPolicy`: calculates discounts
- `TaxPolicy`: calculates tax
- `ReceiptPrinter`: formats receipts
- `PricingService`: wraps pricing (discount plus tax)
- `CheckoutService`: orchestrates the entire checkout flow

One class, one reason to disrupt. Clean.

#### Open/Closed Principle

Do you want to add another discount type? Just implement `DiscountPolicy`. Keep your hands off of `PricingService` and `CheckoutService` altogether. They are closed for modification but open for extension. Same for tax policies -- can implement `ProgressiveTaxPolicy` or `RegionalTaxPolicy` without having to change any existing code.

#### Liskov Substitution Principle

Any `DiscountPolicy` can be used in place of any other `DiscountPolicy` without causing problems. The indirect benefit from this pattern is that `PricingService` doesn't have to know (or care) whether it's receiving a `LoyaltyPercentDiscount` or a `FixedCouponDiscount`, as they both conform to the interface and "work"!

#### Interface Segregation Principle

Interfaces are small and focused:

- `DiscountPolicy`: one method (`discountOf`)
- `TaxPolicy`: two functions (`taxOn`, `getPercent`)

No bloated interfaces that make you implement things you don’t want.

#### Dependency Inversion Principle

High-level classes, such as `PricingService`, depend only on abstractions (i.e., the interfaces `DiscountPolicy` and `TaxPolicy`), not on concrete objects themselves. The "nitty gritty" details (which include 5% loyalty discount, a 10% tax rate) are buried somewhere in the implementations. Dependencies here move from the actual to the abstract, that is the correct direction.

### What would it take to add a new discount type? For instance, let’s say I want to add an around-the-calendar discount - say 15% off in the month of December

#### Step 1: Defining the New Class

```java
package com.cafepos.pricing;

import com.cafepos.common.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

public class SeasonalDiscount implements DiscountPolicy {
  private final int percent;
  private final Month applicableMonth;

  public SeasonalDiscount(int percent, Month applicableMonth) {
    if (percent < 0) {
      throw new IllegalArgumentException("percent must be positive");
    }
    this.percent = percent;
    this.applicableMonth = applicableMonth;
  }

  @Override
  public Money discountOf(Money subtotal) {
    if (LocalDate.now().getMonth() == applicableMonth) {
      var d = subtotal.asBigDecimal()
               .multiply(BigDecimal.valueOf(percent))
               .divide(BigDecimal.valueOf(100));
      return Money.of(d);
    }
    return Money.zero();
  }
}
```

#### Step 2: Use It

```java
var pricing = new PricingService(
  new SeasonalDiscount(15, Month.DECEMBER),
  new FixedRateTaxPolicy(10)
);
var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);
```

#### Step 3: Write Tests

```java
@Test
void seasonal_discount_applies_in_december() {
  DiscountPolicy d = new SeasonalDiscount(15, Month.DECEMBER);
  // test it works in December
}

@Test
void seasonal_discount_does_not_apply_in_january() {
  DiscountPolicy d = new SeasonalDiscount(15, Month.DECEMBER);
  // test it for other months than December
}
```

#### What Shouldn’t Change?

- `PricingService` - already compatible with any `DiscountPolicy`
- `CheckoutService` - no changes
- `ReceiptPrinter` - no changes
- `TaxPolicy` - unaffected
- Existing tests - still pass

That's the Open/Closed Principle in action. Well, add new feature without modifying the old code.

### Summary: Before and After

#### Before (Smelly)

- One big God Class that does everything
- Global mutable state everywhere
- String-based conditionals everywhere
- Impossible to test properly
- Difficult to interpret and modify

#### After (Clean)

- 8 classes with focused roles for each job
- No global state: Everything injected
- Polymorphic behavior through interfaces
- Testable (one-by-one tests of all classes)
- Easy to customize (new policies can be easily added without changing existing code)
- Follows all SOLID principles

#### The Flow Now

```
CheckoutService (orchestrates everything)
    ↓
ProductFactory (creates products)
    ↓
PricingService (does some pricing/discount/tax policies)
    ↓
ReceiptPrinter (formats output)
```

All dependencies injected. No globals. No God Class. Actually maintainable.
