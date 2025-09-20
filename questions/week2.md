# Week 2 Lab Questions

## 1. Why aren't prices stored as doubles?
Because doubles suck for money. For an example in Java, try adding 0.1 + 0.2; you do not get 0.3 but rather get something like: 0.30000000000000004. This occurs because computers store decimals in binary form and cannot precisely represent the decimal 0.1.

For our cafe POS, if we used doubles:
- $2.50 * 3 could give us $7.499999999 instead of $7.50
- And then we would be off by a dollar after hundreds of orders
- Accounting is wrong, taxes are wrong

The Money class fixes this:
- Utilizes BigDecimal that takes accurate decimal math into account
- Always rounds to 2 decimals (just like real money)
- You can't accidentally come up with a negative price
- Makes us reflect on money as money, rather than figures

In other words: doubles = rounding errors, Money = correct math.

## 2. What additional value do we gain by making sure that our constraints for quantity > 0 are enforced?
It catches dumb mistakes then and there, rather than down the line.

If I can still do `new LineItem(product, -5)`, the code is valid as well, but the total of the order goes negative. So now I'm debugging to find out why the totals are wrong, looking at the Order class, checking over Money calculations... waste of time.

With the constraint:
- Exception gets thrown immediately after I try to build the bad LineItem
- Clear stack trace to the problem
- Can't produce invalid data = can't have a corrupted order

It's "fail fast" — if you're going to blow up, do it early and with a message instead of silently failing.

It also makes the code self-documenting. I see when the constructor checks quantity > 0, I know that it is a business rule. The domain model models what's true in the domain (can't order negative coffees).

## 3. Did composition (Order has LineItem) seem more natural than inheritance? Why or why not?
Yeah, composition just made so much more sense.

An Order is not a TYPE of LineItem - it POSSESSES LineItems. That's composition.

With composition:
- Order has a `List<LineItem>`
- Can add/remove items dynamically
- Order and LineItem are decoupled; transformation of one doesn't ruin the other
- Sounds good in English: "an order has some line items"

If we were to do inheritance (Order extends LineItem or something), it would look awkward:
- An Order is not a type of LineItem
- How would you even model multiple items? Inheritance is 1-to-1
- Not at all like the real world

And "favor composition over inheritance" from the lectures. Inheritance couples things tightly. Composition allows for their independence and flexibility.

Order is BUILT FROM LineItems, not DERIVED FROM. Composition = right design pattern here.
