package com.veniture.plugins.tutorial.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.veniture.plugins.tutorial.dto.Currency;
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
import java.util.ArrayList;
import java.util.List;

/**
 * A resource of message.
 */
@Path("/api")
public class ListRestResource {

    private final Logger logger = LoggerFactory.getLogger(ListRestResource.class);// The transition ID
    private Rates rates;
    private List currencyList = new ArrayList();
    private String jsonString;

//    @GET
//    @Path("/listmessage")
//    @AnonymousAllowed
//    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    public Response getMessage() throws IOException, CurrencyConverterException {
//        logger.error("errör");
//        givenAmount_whenConversion_thenNotNull();
//        return Response.ok(new ListRestResourceModel( currencyList.get(0) )).build();
////        return Response.ok(new MyRestResourceModel("jj")).build();
//    }

    @GET
    @Path("/listmessage")
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage() throws IOException, CurrencyConverterException {
        givenAmount_whenConversion_thenNotNull();
        return Response.ok(new MyRestResourceModel( jsonString )).build();
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

        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        jsonString = mapper.writeValueAsString(rates);

        in.close();
        con.disconnect();
    }
}