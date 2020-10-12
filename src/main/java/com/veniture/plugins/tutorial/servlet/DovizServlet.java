package com.veniture.plugins.tutorial.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
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
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.config.ConstantsManager;

import com.atlassian.jira.security.JiraAuthenticationContext;

import java.util.Optional;

@Scanned
public class DovizServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(DovizServlet.class);// The transition ID

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

    private static final String DOVIZ_SCREEN_TEMPLATE = "/templates/doviz.vm";

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
        String jj = null;
        try {
            jj = givenAmount_whenConversion_thenNotNull();
        } catch (CurrencyConverterException e) {
            e.printStackTrace();
        }

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("dolarKuru", jj);

        String action = Optional.ofNullable(req.getParameter("actionType")).orElse("");

        resp.setContentType("text/html;charset=utf-8");

        templateRenderer.render(DOVIZ_SCREEN_TEMPLATE, context, resp.getWriter());
    }



    public String givenAmount_whenConversion_thenNotNull() throws CurrencyConverterException, IOException {
        URL url = new URL("https://api.currencyfreaks.com/latest?apikey=d9b286cad7984661ab465673edd9683b");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        JsonObject jsonObject = new JsonParser().parse(String.valueOf(content)).getAsJsonObject();
        JsonElement rates = jsonObject.get("rates");
        JsonObject jsonRates = rates.getAsJsonObject();
        JsonElement jj = jsonRates.get("TRY");

        logger.info("kur: {}", jj);

        logger.error("errorkur: ");

        in.close();
        con.disconnect();

        return String.valueOf(jj);
    }
}
