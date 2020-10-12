package com.veniture.plugins.tutorial.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.veniture.plugins.tutorial.dto.Rates;
import com.veniture.plugins.tutorial.dto.Root;
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

@Scanned
public class DovizServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(DovizServlet.class);// The transition ID
    private static final String DOVIZ_SCREEN_TEMPLATE = "/templates/doviz.vm";
    private Rates rates;
    private List<currency> currencies = new ArrayList<>();

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
        try {
            givenAmount_whenConversion_thenNotNull();
        } catch (CurrencyConverterException e) {
            e.printStackTrace();
        }
//
//        currencies.add(new currency("USD" ,1/Float.valueOf(rates.getUSD())));
//        currencies.add(new currency("EUR" ,1/Float.valueOf(rates.getEUR())));

        Map<String, Object> context = new HashMap<String, Object>();

        context.put("dolarKuru", 1/Float.valueOf(rates.getUSD()));
        context.put("euroKuru", 1/Float.valueOf(rates.getEUR()));
//        context.put("currencies", currencies);

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

class currency {
    String name;
    Float unit;

    public currency(String name, Float unit) {
        this.name = name;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getUnit() {
        return unit;
    }

    public void setUnit(Float unit) {
        this.unit = unit;
    }
}