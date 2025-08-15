package com.crazy.back.clients.swapi.feign;

import feign.Logger;
import feign.Request;
import feign.Response;

import java.io.IOException;

public class FeignLogger extends Logger {
    @Override
    protected void log(String configKey, String format, Object... args) {
        //Empty for now
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        System.out.println("FEIGN REQUEST: " + request.httpMethod() + " " + request.url());
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        System.out.println("FEIGN RESPONSE [" + response.status() + "] " + response.reason());
        return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
    }
}
