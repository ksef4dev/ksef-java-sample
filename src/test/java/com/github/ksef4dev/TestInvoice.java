package com.github.ksef4dev;

import io.alapierre.ksef.client.ApiException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2023.11.06
 */
@Disabled
public class TestInvoice {

    @Test
    void testLoadInvoice() throws ParseException, ApiException {
        Sample.loadInvoice();
    }

    @Test
    void testInvoiceStatus() throws ApiException {
        Sample.invoiceStatus("...............", "....................");
    }

}
