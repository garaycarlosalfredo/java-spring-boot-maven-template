package com.example.template.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import com.jayway.jsonpath.JsonPath;

import tools.jackson.databind.json.JsonMapper;

class ProblemDetailSecurityHandlerTests {

    // Mirrors the Boot auto-configured mapper, which registers the ProblemDetail mixin.
    private final ProblemDetailSecurityHandler handler = new ProblemDetailSecurityHandler(
            JsonMapper.builder()
                    .addMixIn(ProblemDetail.class, ProblemDetailJacksonMixin.class)
                    .build());

    @Test
    void writesUnauthorizedProblemWithBasicChallenge() throws Exception {
        var request = new MockHttpServletRequest("GET", "/api/v1/items");
        var response = new MockHttpServletResponse();

        handler.commence(request, response, new InsufficientAuthenticationException("internal reason"));

        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, response.getContentType());
        assertEquals("Basic realm=\"api\"", response.getHeader(HttpHeaders.WWW_AUTHENTICATE));
        String body = response.getContentAsString();
        assertEquals("Unauthorized", JsonPath.read(body, "$.title"));
        assertEquals(401, (int) JsonPath.read(body, "$.status"));
        assertEquals("Authentication required", JsonPath.read(body, "$.detail"));
        assertEquals("/api/v1/items", JsonPath.read(body, "$.instance"));
        assertFalse(body.contains("internal reason"));
    }

    @Test
    void writesForbiddenProblemWithoutChallenge() throws Exception {
        var request = new MockHttpServletRequest("DELETE", "/api/v1/items/7");
        var response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("internal reason"));

        assertEquals(403, response.getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, response.getContentType());
        assertNull(response.getHeader(HttpHeaders.WWW_AUTHENTICATE));
        String body = response.getContentAsString();
        assertEquals("Forbidden", JsonPath.read(body, "$.title"));
        assertEquals(403, (int) JsonPath.read(body, "$.status"));
        assertEquals("Access denied", JsonPath.read(body, "$.detail"));
        assertEquals("/api/v1/items/7", JsonPath.read(body, "$.instance"));
        assertFalse(body.contains("internal reason"));
        assertTrue(body.contains("Access denied"));
    }
}
