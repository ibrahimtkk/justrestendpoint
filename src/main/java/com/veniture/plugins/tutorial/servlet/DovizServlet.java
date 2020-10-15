package com.veniture.plugins.tutorial.servlet;

import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.veniture.plugins.tutorial.dto.Currency;
import com.veniture.plugins.tutorial.dto.Rates;
import com.veniture.plugins.tutorial.dto.Root;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.config.ConstantsManager;

import com.atlassian.jira.security.JiraAuthenticationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Scanned
public class DovizServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(DovizServlet.class);// The transition ID
    private static final String DOVIZ_SCREEN_TEMPLATE = "/templates/doviz.vm";
    private Rates rates;
    private ArrayList list = new ArrayList();
    private ArrayList currencies = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private List<Float> units = new ArrayList<>();
    Map map = new HashMap();

    @JiraImport
    private TemplateRenderer templateRenderer;
    @JiraImport
    private IssueService issueService;
    @JiraImport
    private ProjectService projectService;
    @JiraImport
    private SearchService searchService;
    @JiraImport
    private JiraAuthenticationContext authenticationContext;
    @JiraImport
    private ConstantsManager constantsManager;



    public DovizServlet(TemplateRenderer templateRenderer, IssueService issueService, ProjectService projectService, SearchService searchService, JiraAuthenticationContext authenticationContext, ConstantsManager constantsManager) {
        this.templateRenderer = templateRenderer;
        this.issueService = issueService;
        this.projectService = projectService;
        this.searchService = searchService;
        this.authenticationContext = authenticationContext;
        this.constantsManager = constantsManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String address = "http://localhost:8089/rest/myrestresource/1.0/api/listmessage";
//
//        URL url = new URL(address);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer content = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
//        Map<String, Object> context = new HashMap();
//        Gson gson = new Gson();
//        RestCurrency restCurrency = gson.fromJson(String.valueOf(content), RestCurrency.class);
//        context.put("dolarKuru", restCurrency.getValue());
//        resp.setContentType("text/html;charset=utf-8");
//        templateRenderer.render(DOVIZ_SCREEN_TEMPLATE, context, resp.getWriter());



        String address = "http://localhost:8089/rest/myrestresource/1.0/api/listmessage";

        URL url = new URL(address);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        String cont = String.valueOf(content);

        cont = cont.replaceAll("\\\\", "");

        cont = cont.substring(10, cont.length()-2);
        cont = cont.toUpperCase();
        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        Rates target2 = gson.fromJson(cont, Rates.class); // deserializes json into target2

        List<String> keyList = new ArrayList();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(cont.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            keyList.add(key);
        }

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        List curList = new ArrayList();

        for (int i=0; i<keyList.toArray().length; i++){
            String name = keyList.get(i);
            try {
                curList.add(new Currency(name, 1/Float.valueOf(String.valueOf(jsonObject.get(name)))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> context = new HashMap();
        RestCurrency restCurrency = gson.fromJson(String.valueOf(content), RestCurrency.class);

        context.put("curList", curList);
        resp.setContentType("text/html;charset=utf-8");
        templateRenderer.render(DOVIZ_SCREEN_TEMPLATE, context, resp.getWriter());
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



class RestCurrency {
    String value;

    public RestCurrency(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}