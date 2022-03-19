package com.vg.market.commodities.query;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ApiHistTimesFunction
{
    TimeSeries("/timeseries", "timeseries");

    private final static Map<String, ApiHistTimesFunction> commoditiesTimes;
    static {
        commoditiesTimes = Arrays.stream(ApiHistTimesFunction.values()).collect(Collectors.toMap(ApiHistTimesFunction::getUrl, Function.identity()));
    }

    String url;
    String value;

    ApiHistTimesFunction(String url, String value) {
        this.url = url;
        this.value = value;
    }

    public static Map<String, ApiHistTimesFunction> getMap()
    {
        return commoditiesTimes;
    }

    public String getUrl()
    {
        return url;
    }

    public String getValue() {
        return value;
    }
}
