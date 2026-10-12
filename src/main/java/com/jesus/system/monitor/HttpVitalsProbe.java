package com.jesus.system.monitor;

import com.jesus.system.config.WatchdogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Shared HTTP vitals probe (Java 17 HttpClient) used by the monitors.
 * Replaces the duplicated legacy HttpURLConnection checks.
 */
@Component
public class HttpVitalsProbe {

    private static final Logger log = LoggerFactory.getLogger(HttpVitalsProbe.class);

    private final WatchdogProperties properties;
    private final HttpClient httpClient;

    public HttpVitalsProbe(WatchdogProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.getHttpTimeout())
                .build();
    }

    /**
     * @return true if the endpoint answers with a non-5xx status within the configured timeout
     */
    public boolean isAlive(String endpoint) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                    .timeout(properties.getHttpTimeout())
                    .GET()
                    .build();
            int status = httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
            return status >= 200 && status < 500;
        } catch (IOException e) {
            log.debug("HTTP vitals probe failed for {}: {}", endpoint, e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (IllegalArgumentException e) {
            log.error("Invalid health endpoint URI '{}': {}", endpoint, e.getMessage());
            return false;
        }
    }
}
