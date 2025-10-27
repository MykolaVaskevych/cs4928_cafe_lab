# InteractiveCLI Manual Test Guide

## Running the CLI
```bash
mvn compile
java -cp target/classes com.cafepos.demo.InteractiveCLI
```

## Test Scenario: Order with Discount

### Steps:
1. Start CLI
2. Choose option `1` (Add item)
3. Select drink `2` (Latte)
4. Add addon `4` (Large Size)
5. Choose `5` (No more add-ons)
6. Enter quantity `2`
7. Choose option `3` (Proceed to payment)
8. Select discount `2` (Loyalty 5%)
9. Choose payment `2` (Card)
10. Enter card: `1234567890123456`
11. Verify receipt shows:
    - Subtotal: 7.80
    - Discount: -0.39
    - Tax (10%): 0.74
    - Total: 8.15

### Expected Output:
✓ All observers notified (Kitchen, Delivery, Customer)
✓ Receipt formatted with PricingService
✓ Discount applied correctly
✓ Tax calculated via TaxPolicy
✓ Payment processed

## New Features Tested:
- [x] TaxPolicy integration (no hardcoded 10%)
- [x] Discount selection menu (5 options)
- [x] PricingService calculates final price
- [x] Formatted receipt with discount line
- [x] All payment strategies work with discounted total
