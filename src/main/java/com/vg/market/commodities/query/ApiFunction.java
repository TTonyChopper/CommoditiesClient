package com.vg.market.commodities.query;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ApiFunction
{
    Intraday("/latest", "latest");

    private final static Map<String, ApiFunction> commodities;
    static {
        commodities = Arrays.stream(ApiFunction.values()).collect(Collectors.toMap(ApiFunction::getUrl, Function.identity()));
    }

    String url;
    String value;

    ApiFunction(String url, String value) {
        this.url = url;
        this.value = value;
    }

    public static Map<String, ApiFunction> getMap()
    {
        return commodities;
    }

    public String getUrl()
    {
        return url;
    }

    public String getValue() {
        return value;
    }
}
