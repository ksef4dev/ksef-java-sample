package com.github.ksef4dev;

import io.alapierre.ksef.client.AbstractApiClient;
import io.alapierre.ksef.client.ApiClient;
import io.alapierre.ksef.client.ApiException;
import io.alapierre.ksef.client.JsonSerializer;
import io.alapierre.ksef.client.api.InterfejsyInteraktywneFakturaApi;
import io.alapierre.ksef.client.api.InterfejsyInteraktywneSesjaApi;
import io.alapierre.ksef.client.model.rest.auth.AuthorisationChallengeRequest;
import io.alapierre.ksef.client.model.rest.auth.InitSignedResponse;
import io.alapierre.ksef.client.okhttp.OkHttpApiClient;
import io.alapierre.ksef.client.serializer.gson.GsonJsonSerializer;
import io.alapierre.ksef.token.facade.KsefTokenFacade;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 2023.09.15
 */
public class Sample {

    private static final String NIP_FIRMY = "2932110194";
    public static final String token = "E5D99064EBCA79DFD6366BCEA048A1EBCE63510F70EAE9E8A4385879F9761AEE";

    private static final JsonSerializer serializer = new GsonJsonSerializer();
    private static final ApiClient client = new OkHttpApiClient(serializer);
    private static final InterfejsyInteraktywneSesjaApi sesjaApi = new InterfejsyInteraktywneSesjaApi(client);

    public static void main(String[] args) {

        try {

            val signedResponse = loginByToken();

            System.out.println("session token = " + signedResponse.getSessionToken().getToken());

            val invoiceApi = new InterfejsyInteraktywneFakturaApi(client);
            val sessionApi = new InterfejsyInteraktywneSesjaApi(client);

            val sessionToken = signedResponse.getSessionToken().getToken();

            val resp = invoiceApi.invoiceSend(new File("src/main/resources/FA2.xml"), sessionToken);

            System.out.printf("ElementReferenceNumber %s, ReferenceNumber %s, ProcessingCode %d\n",
                    resp.getElementReferenceNumber(),
                    resp.getReferenceNumber(),
                    resp.getProcessingCode());

//            val closeSessionResponse = sessionApi.terminateSession(sessionToken);
//            System.out.println(closeSessionResponse);

        } catch (ApiException ex) {
            System.err.printf("Błąd wywołania API %d (%s) opis błędu %s", ex.getCode(), ex.getMessage(),  ex.getResponseBody());
        } catch (Exception e) {
            System.err.println("błąd: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static InitSignedResponse loginByToken() throws ApiException, ParseException {

        val facade = new KsefTokenFacade(sesjaApi);
        return facade.authByToken(AbstractApiClient.Environment.TEST, NIP_FIRMY, AuthorisationChallengeRequest.IdentifierType.onip, token);
    }

    public static void loadInvoice() throws ParseException, ApiException {

        try {

            val signedResponse = loginByToken();

            System.out.println("session token = " + signedResponse.getSessionToken().getToken());

            val invoiceApi = new InterfejsyInteraktywneFakturaApi(client);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            invoiceApi.getInvoice("2932110194-20231105-651FBA47CECC-F8", signedResponse.getSessionToken().getToken(), out);

            System.out.println(out.toString());

        } catch (ApiException ex) {
            System.out.printf("Błąd wywołania API %d (%s) opis błędu %s", ex.getCode(), ex.getMessage(),  ex.getResponseBody());
            throw ex;
        }
    }

    public static void invoiceStatus(String token, String referenceNumber) throws ApiException {

        val invoiceApi = new InterfejsyInteraktywneFakturaApi(client);
        val resp = invoiceApi.invoiceStatus(token, referenceNumber);

        System.out.println(resp);
    }

}
