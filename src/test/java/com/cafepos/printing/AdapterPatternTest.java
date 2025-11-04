package com.cafepos.printing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import vendor.legacy.LegacyThermalPrinter;

class AdapterPatternTest {

  static class FakeLegacyPrinter extends LegacyThermalPrinter {
    int lastLen = -1;
    byte[] lastPayload = null;

    @Override
    public void legacyPrint(byte[] payload) {
      this.lastLen = payload.length;
      this.lastPayload = payload;
    }
  }

  @Test
  void adapter_convertsTextToBytes() {
    FakeLegacyPrinter fake = new FakeLegacyPrinter();
    Printer printer = new LegacyPrinterAdapter(fake);

    printer.print("ABC");

    assertTrue(fake.lastLen >= 3);
    assertEquals(3, fake.lastLen);
  }

  @Test
  void adapter_handlesEmptyString() {
    FakeLegacyPrinter fake = new FakeLegacyPrinter();
    Printer printer = new LegacyPrinterAdapter(fake);

    printer.print("");

    assertEquals(0, fake.lastLen);
  }

  @Test
  void adapter_handlesMultilineText() {
    FakeLegacyPrinter fake = new FakeLegacyPrinter();
    Printer printer = new LegacyPrinterAdapter(fake);

    String receipt = "Order #123\nItem: Latte\nTotal: 5.00";
    printer.print(receipt);

    assertTrue(fake.lastLen > 0);
    assertEquals(receipt.length(), fake.lastLen);
  }

  @Test
  void adapter_preservesTextContent() {
    FakeLegacyPrinter fake = new FakeLegacyPrinter();
    Printer printer = new LegacyPrinterAdapter(fake);

    String text = "Test Receipt";
    printer.print(text);

    assertNotNull(fake.lastPayload);
    String decoded = new String(fake.lastPayload, java.nio.charset.StandardCharsets.UTF_8);
    assertEquals(text, decoded);
  }

  @Test
  void adapter_handlesSpecialCharacters() {
    FakeLegacyPrinter fake = new FakeLegacyPrinter();
    Printer printer = new LegacyPrinterAdapter(fake);

    String text = "€ÜØÄ";
    printer.print(text);

    assertTrue(fake.lastLen > 0);
    String decoded = new String(fake.lastPayload, java.nio.charset.StandardCharsets.UTF_8);
    assertEquals(text, decoded);
  }
}
