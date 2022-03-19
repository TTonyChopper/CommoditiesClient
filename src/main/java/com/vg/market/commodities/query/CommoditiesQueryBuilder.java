package com.vg.market.commodities.query;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CommoditiesQueryBuilder
{
    public final static String COMMODITIES_BASE_URL = "commodities-api.com";

    private final String key;

    public CommoditiesQueryBuilder(String key) {
        this.key = key;
    }

    public String getLatest(ApiFunction function, String base, String symbols) {
        return "/api/" + function.getValue()
                +"?access_key=" + key
                +"&base=" + base
                +"&symbols=" + symbols;
    }

    public String getSeries(ApiHistTimesFunction function, String base, String symbols, String endDate) {
        LocalDate endDateParsed = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        LocalDate startDateParsed = endDateParsed.minusDays(365);
        return "/api/" + function.getValue()
                +"?access_key=" + key
                +"&base=" + base
                +"&symbols=" + symbols
                +"&start_date=" + startDateParsed.format(DateTimeFormatter.ISO_DATE)
                +"&end_date=" + endDate;
    }
}
