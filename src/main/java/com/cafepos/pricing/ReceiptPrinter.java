package com.cafepos.pricing;

public final class ReceiptPrinter {

  public String format(String recipe, int qty, PricingResult pr, int taxPercent) {
    StringBuilder receipt = new StringBuilder();
    receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
    receipt.append(formatPricing(pr, taxPercent));
    return receipt.toString();
  }

  public String formatPricing(PricingResult pr, int taxPercent) {
    StringBuilder pricing = new StringBuilder();
    pricing.append("Subtotal: ").append(pr.subtotal()).append("\n");
    if (pr.discount().asBigDecimal().signum() > 0) {
      pricing.append("Discount: -").append(pr.discount()).append("\n");
    }
    pricing.append("Tax (").append(taxPercent).append("%): ").append(pr.tax()).append("\n");
    pricing.append("Total: ").append(pr.total());
    return pricing.toString();
  }
}
