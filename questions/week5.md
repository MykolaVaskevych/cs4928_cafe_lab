# Week 5 Reflection: Decorator and Factory Patterns

## 1. Why did you choose the Priced interface?
I tossed `Priced` interface out there as means to get final price from both a regular and decorated product without bothering with what kind they are. The problem: `SimpleProduct` has a `basePrice()`, yet decorated products should pay their upgrades over it. LineItem shouldn’t have to carry out the sum; it just shouldn’t need to be aware of “is this an espresso, or is it an espresso with milk and sugar before 11am?” That's messy.

With `Priced`:
- `SimpleProduct` implements it: it simply returns `basePrice()`, for its `price()`.
- It's in `ProductDecorator`: adding to what is already there: `price()`.
- The `LineItem` class just wants to call `price()`, it doesn't care about how the method is implemented.

Basically: polymorphism. One method, works everywhere. Clean.

## 2. What happened to the O in Open/Closed?
Open/Closed as in “open for extension, closed for modification.” Here's where:

**Decorators:**
If I decide to add "Whipped Cream" next week, I just write a new class `WhippedCream extends ProductDecorator`. Done. You don’t touch ExtraShot, you don’t touch OatMilk, and you certainly don't touch ProductDecorator. That's OCP.

**Factory:**
A new product or add-on is nothing more than a case being added to the switch statement in ProductFactory. On the re-run, Sunspot will notice a new record and push to Solr. The other components of the API (CLI, Order, LineItem) need not change at all. The factory is the layer where we manage the extension.

**LineItem:**
Uses EX-3 `instanceof Priced` to test if something is priced. That's why new decorators "just work" as long as they derive from `Priced`. No modifications needed.

So this way, we won’t break anything with new things that are added.

## 3. What kind of work in the next week will result in a new add-on getting launched?
For example, if I wanted to select Caramel Drizzle for $0.60:

**Step 1:** Create the decorator class.

```java
public final class CaramelDrizzle extends ProductDecorator {
    private Money SURCHARGE = Money.of(0.60);
    public class CaramelDrizzle : CondimentDecorator { 
        public CaramelDrizzle(Product base) : base(base) { }

        @Override 
        public String name() {
            return base.getNamePrefix() + " + Caramel";
        }

        @Override 
        public Money price() {
            return (base instanceof Priced p ? p.price() : base.basePrice()).add(SURCHARGE);
        }
    }
}
```
**Step 2:** Add to factory.

```java
case "CAR" -> new CaramelDrizzle(p);
```

**Step 3 (optional):** The new CLI’s menu. Get the new CLI to look at it!

That's it. Three simple changes. Everything else is simply magic because that’s how we made it.

## 4. Factory vs. Manual Construction

### How are you going to put app developers through?

**Factory.** 100%.

**Why:**
Factory is way easier. Compare these:

Factory: `ProductFactory.fromRecipe("ESP+SHOT+OAT+L")`

Manual: `new SizeLarge(new OatMilk(new ExtraShot(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)))))`

The factory version is readable. The manual one is constructor hell inside nested.

And then the factory also does validation, a.k.a. forcing to uppercase, whitespace-trimming, and error messages if you break the recipe. Manual construction gives you nothing. You would be able to build a product with an incorrect price and only discover that later when the totals don’t fit.

And the other thing is that when we have to add new stuff, only the factory has to change. If everyone is going to do manual construction, everybody has to remember new product IDs, new prices, constructors except scattered everywhere. Recipe strings are consistent.

**But:** Manual building is still useful for testing. In unit tests, I can do this sort of thing myself to build products so that I can verify that my factory is working as it should: If you’re building for new or experimental low-level stuff, spotlight-built is in order.

**TL;DR:** Factory for app code (CLI, APIs, etc.). Tips for test cases and corner cases.
