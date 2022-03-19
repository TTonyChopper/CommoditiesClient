package com.vg.market.commodities.query;

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

    public String getSeries(ApiHistTimesFunction function, String base, String symbols, String startDate, String endDate) {
        return "/api/" + function.getValue()
                +"?access_key=" + key
                +"&base=" + base
                +"&symbols=" + symbols
                +"&start_date=" + startDate
                +"&end_date=" + endDate;
    }
}
