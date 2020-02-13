package com.gamedoora.gamedooraservices.base;

import java.nio.charset.Charset;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BaseRestClient<T> {

    private RestTemplate restTemplate;

    protected T execute(String url, String user, String password, MediaType mediaType, Class klass) {
        ResponseEntity<T> responseEntity = getRestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(user, password, mediaType, null)), klass);
        return responseEntity.getBody();
    }

    protected T execute(String url, String user, String password, String body, MediaType acceptType, MediaType contenType, Class toClass) {
        ResponseEntity<T> responseEntity = getRestTemplate().exchange(url, HttpMethod.POST, new HttpEntity<>(body, createHeaders(user, password, acceptType, contenType)), toClass);
        return responseEntity.getBody();
    }

    HttpHeaders createHeaders(String username, String password, MediaType acceptType, MediaType contenType) {
        return new HttpHeaders() {
            {
                if (StringUtils.isNotBlank(username)) {
                    set("Authorization", generateAuthHeader(username, password));
                }
                if (null != acceptType) {
                    set("Accept", String.format("{0}/{1}", acceptType.getType(), acceptType.getSubtype()));
                }
                if (null != contenType) {
                    set("Content-Type", String.format("{0}/{1}", contenType.getType(), contenType.getSubtype()));
                }
            }
        };
    }

    private String generateAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(@Qualifier("withoutSslVerify") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
