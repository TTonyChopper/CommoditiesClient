package com.vg.market;

import com.vg.market.commodities.query.ApiFunction;
import com.vg.market.commodities.query.ApiHistTimesFunction;
import com.vg.market.commodities.query.CommoditiesQueryBuilder;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CommoditiesVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger("SampleLogger");

    private CommoditiesQueryBuilder queryBuilder;

    private HttpServer httpServer;

    public static final int API_HISTORICAL_DAYS_LIMIT = 365;
    public static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new CommoditiesVerticle());
    }

    private String getYesterdayDate() {
        return LocalDate.now().minusDays(1).format(API_DATE_FORMATTER);
    }

    public String getYesterdayDate(LocalDate date) {
        return date.minusDays(1).format(DateTimeFormatter.ISO_DATE);
    }

    private LocalDate getTodayDate() {
        return LocalDate.now();
    }

    public LocalDate getApiHistLimitDate(LocalDate endDate) {
        return endDate.minusDays(API_HISTORICAL_DAYS_LIMIT);
    }

    public LocalDate getMinimalStartDate(String endDate) {
        LocalDate parsed = LocalDate.parse(endDate, API_DATE_FORMATTER);
        LocalDate now = LocalDate.now();
        LocalDate absLimit = getApiHistLimitDate(now);
        return absLimit;
    }

    @Override
    public void start() {
        WebClient client = WebClient.create(vertx);
        final Router router = Router.router(vertx);

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(ar -> {
            if (ar.failed()) {
                // Failed to retrieve the configuration
            } else {
                JsonObject config = ar.result();
                queryBuilder = new CommoditiesQueryBuilder(config.getString("apikey"));
            }
        });

        ApiFunction.getMap().forEach((k, v)->{
            router.route("/raw/rates" + k).handler(ctx -> {

                String from = ctx.request().getParam("from");
                from = (from == null) ? "XAU" : from;
                String tos = ctx.request().getParam("tos");
                tos = (tos == null) ? "USD,EUR,AUD" : tos;

                client
                        .get(443, CommoditiesQueryBuilder.COMMODITIES_BASE_URL, queryBuilder.getLatest(v, from, tos))
                        .ssl(true)
                        .send()
                        .onSuccess(response -> {
                            log.debug("Received response with status code" + response.statusCode());
                            ctx.response().putHeader("content-type", "text/json")
                                    .send(response.body());
                        })
                        .onFailure(err ->
                                log.debug("Something went wrong " + err.getMessage()));
            });
        });

        ApiHistTimesFunction.getMap().forEach((k, v)->{
            router.route("/raw/hist" + k).handler(ctx -> {

                String from = ctx.request().getParam("from");
                from = (from == null) ? "XAU" : from;
                String to = ctx.request().getParam("to");
                to = (to == null) ? "USD" : to;
                String endDate = ctx.request().getParam("endDate");
                endDate = (endDate == null) ? getYesterdayDate() : endDate;

                String startDate = getMinimalStartDate(endDate).format(API_DATE_FORMATTER);

                client
                        .get(443, CommoditiesQueryBuilder.COMMODITIES_BASE_URL, queryBuilder.getSeries(v, from, to, startDate, endDate))
                        .ssl(true)
                        .send()
                        .onSuccess(response -> {
                            log.debug("Received response with status code" + response.statusCode());
                            ctx.response().putHeader("content-type", "text/json")
                                    .send(response.body());
                        })
                        .onFailure(err ->
                                log.debug("Something went wrong " + err.getMessage()));
            });
        });

        router.route("/alive").handler(ctx -> {
            System.out.println("Hello");
            ctx.response().putHeader("content-type", "text/plain")
                    .send("Hello");
        });

        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router)
                .listen(9094)
                .onFailure(h->System.out.println("HTTP server failed on port 9094"));

    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
        httpServer.close();
    }
}
