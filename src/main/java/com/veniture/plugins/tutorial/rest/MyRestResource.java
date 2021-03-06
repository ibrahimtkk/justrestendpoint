package com.veniture.plugins.tutorial.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.veniture.plugins.tutorial.dto.Rates;
import com.veniture.plugins.tutorial.dto.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A resource of message.
 */
@Path("/message")
public class MyRestResource {

    private final Logger logger = LoggerFactory.getLogger(MyRestResource.class);// The transition ID
    private Rates rates;

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage() throws IOException, CurrencyConverterException {
        logger.error("errör");
        givenAmount_whenConversion_thenNotNull();
        return Response.ok(new MyRestResourceModel( String.valueOf(1/Float.valueOf(rates.getUSD())) )).build();
//        return Response.ok(new MyRestResourceModel(jj)).build();
    }

    public void givenAmount_whenConversion_thenNotNull() throws CurrencyConverterException, IOException {
        URL url = new URL("https://api.currencyfreaks.com/latest?apikey=d9b286cad7984661ab465673edd9683b&base=TRY");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        Gson gson = new Gson();
        Root root = gson.fromJson(String.valueOf(content), Root.class);

        this.rates = root.getRates();
        logger.error("errorkur: ");

        in.close();
        con.disconnect();
    }
}