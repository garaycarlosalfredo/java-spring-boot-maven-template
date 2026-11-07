package com.example.template.items.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.template.exception.ApiExceptionHandler;
import com.example.template.exception.ResourceNotFoundException;
import com.example.template.items.entity.Item;
import com.example.template.items.service.ItemService;
import com.example.template.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@WebMvcTest(controllers = ItemController.class, properties = {
        "app.security.username=test-user",
        "app.security.password=test-password"
})
@Import(SecurityConfig.class)
class ItemControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService service;

    @Test
    @WithMockUser
    void keepsRootUnmapped() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isNotFound());
    }

    @Nested
    class OperationalEndpointAccess {
        @Test
        void permitsHealthPathsWithoutAuthentication() throws Exception {
            mockMvc.perform(get("/actuator/health/liveness"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void protectsOtherManagementPathsWithProblemDetails() throws Exception {
            mockMvc.perform(get("/actuator/env"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
        }

        @Test
        void protectsItemPathsWithProblemDetails() throws Exception {
            mockMvc.perform(get("/api/v1/items"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
            verifyNoInteractions(service);
        }
    }

    @Nested
    @WithMockUser
    class CreateItem {
        @BeforeEach
        void configureOperation() {
            when(service.create(anyString(), nullable(String.class), any(BigDecimal.class))).thenAnswer(invocation -> {
                var item = mock(Item.class);
                when(item.id()).thenReturn(41L);
                when(item.sku()).thenReturn(900L);
                when(item.name()).thenReturn(invocation.getArgument(0));
                when(item.description()).thenReturn(invocation.getArgument(1));
                when(item.price()).thenReturn(invocation.getArgument(2));
                return item;
            });
        }

        @Test
        void acceptsItemJson() throws Exception {
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {"name":"Notebook","description":"Example item","price":12.50}
                            """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/api/v1/items/41"))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(41))
                    .andExpect(jsonPath("$.sku").value(900))
                    .andExpect(jsonPath("$.name").value("Notebook"))
                    .andExpect(jsonPath("$.price").value(12.50));
        }

        @Test
        void rejectsMalformedJson() throws Exception {
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.instance").value("/api/v1/items"));
        }

        @Test
        void rejectsIncompatibleFieldType() throws Exception {
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {"name":{},"description":"Example item"}
                            """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.instance").value("/api/v1/items"));
        }

        @Test
        void rejectsMissingRequestBody() throws Exception {
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.instance").value("/api/v1/items"));
        }

        @Test
        void rejectsUnsupportedMediaType() throws Exception {
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("Example item"))
                    .andExpect(status().isUnsupportedMediaType());
        }

        @ParameterizedTest
        @CsvSource({
                "'{}', name",
                "'{\"name\":null,\"price\":1}', name",
                "'{\"name\":\"   \",\"price\":1}', name",
                "'{\"name\":\"Notebook\"}', price",
                "'{\"name\":\"Notebook\",\"price\":null}', price",
                "'{\"name\":\"Notebook\",\"price\":-0.01}', price",
                "'{\"name\":\"Notebook\",\"price\":1.001}', price",
                "'{\"name\":\"Notebook\",\"price\":10000000000}', price"
        })
        void rejectsInvalidFields(String json, String field) throws Exception {
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("Request validation failed"))
                    .andExpect(jsonPath("$.errors[?(@.field == '%s')]", field).isNotEmpty());
            verifyNoInteractions(service);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "0.01", "9999999999.99"})
        void acceptsPriceBoundariesAndOptionalDescription(String price) throws Exception {
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Notebook\",\"price\":" + price + "}"))
                    .andExpect(status().isCreated());
        }

        @Test
        void acceptsMaximumLengthsAfterNameNormalization() throws Exception {
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"  " + "a".repeat(100) + "  \",\"description\":\""
                            + "b".repeat(1000) + "\",\"price\":1}"))
                    .andExpect(status().isCreated());
        }

        @ParameterizedTest
        @ValueSource(strings = {"name", "description"})
        void rejectsExcessiveLength(String field) throws Exception {
            String name = "a".repeat(field.equals("name") ? 101 : 1);
            String description = "b".repeat(field.equals("description") ? 1001 : 1);
            mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"" + name + "\",\"description\":\"" + description + "\",\"price\":1}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].field").value(field));
        }
    }

    @Nested
    @WithMockUser
    class ListItems {
        @Test
        void returnsItemsAsJson() throws Exception {
            var item = mock(Item.class);
            when(item.id()).thenReturn(1L);
            when(item.sku()).thenReturn(2L);
            when(item.name()).thenReturn("Notebook");
            when(item.description()).thenReturn("Example");
            when(item.price()).thenReturn(new BigDecimal("12.50"));
            when(service.list()).thenReturn(List.of(item));

            mockMvc.perform(get("/api/v1/items"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].sku").value(2))
                    .andExpect(jsonPath("$[0].name").value("Notebook"))
                    .andExpect(jsonPath("$[0].description").value("Example"))
                    .andExpect(jsonPath("$[0].price").value(12.50));
            verify(service).list();
        }

        @Test
        void returnsEmptyJsonArrayWhenNoItemsExist() throws Exception {
            when(service.list()).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/items"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("[]"));
            verify(service).list();
        }
    }

    @Nested
    @WithMockUser
    class GetItem {
        @Test
        void returnsItemAsJson() throws Exception {
            var item = mock(Item.class);
            when(item.id()).thenReturn(1L);
            when(item.sku()).thenReturn(2L);
            when(item.name()).thenReturn("Notebook");
            when(item.description()).thenReturn("Example");
            when(item.price()).thenReturn(new BigDecimal("12.50"));
            when(service.get(1L)).thenReturn(item);

            mockMvc.perform(get("/api/v1/items/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.sku").value(2))
                    .andExpect(jsonPath("$.name").value("Notebook"))
                    .andExpect(jsonPath("$.description").value("Example"))
                    .andExpect(jsonPath("$.price").value(12.50));
            verify(service).get(1L);
        }

        @Test
        void returnsProblemDetailWhenItemIsMissing() throws Exception {
            when(service.get(999L)).thenThrow(new ResourceNotFoundException("Item", 999L));

            mockMvc.perform(get("/api/v1/items/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value("Item not found"));
        }

        @Test
        void rejectsNonNumericIdWithoutCallingService() throws Exception {
            mockMvc.perform(get("/api/v1/items/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
            verifyNoInteractions(service);
        }
    }

    @Nested
    @WithMockUser
    class UpdateItem {
        private static final String VALID_BODY = """
                {"name":"Notebook","description":"Updated","price":15.00}
                """;

        @Test
        void replacesItemAndReturnsUpdatedJson() throws Exception {
            var item = mock(Item.class);
            when(item.id()).thenReturn(1L);
            when(item.sku()).thenReturn(2L);
            when(item.name()).thenReturn("Notebook");
            when(item.description()).thenReturn("Updated");
            when(item.price()).thenReturn(new BigDecimal("15.00"));
            when(service.update(eq(1L), eq("Notebook"), eq("Updated"), any(BigDecimal.class))).thenReturn(item);

            mockMvc.perform(put("/api/v1/items/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.sku").value(2))
                    .andExpect(jsonPath("$.name").value("Notebook"))
                    .andExpect(jsonPath("$.description").value("Updated"))
                    .andExpect(jsonPath("$.price").value(15.00));
            verify(service).update(1L, "Notebook", "Updated", new BigDecimal("15.00"));
        }

        @Test
        void clearsDescriptionWhenBodyOmitsIt() throws Exception {
            var item = mock(Item.class);
            when(item.id()).thenReturn(1L);
            when(item.sku()).thenReturn(2L);
            when(item.name()).thenReturn("Notebook");
            when(item.description()).thenReturn(null);
            when(item.price()).thenReturn(new BigDecimal("15.00"));
            when(service.update(eq(1L), eq("Notebook"), isNull(), any(BigDecimal.class))).thenReturn(item);

            mockMvc.perform(put("/api/v1/items/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {"name":"Notebook","price":15.00}
                            """))
                    .andExpect(status().isOk())
                    .andExpect(content().json("""
                            {"id":1,"sku":2,"name":"Notebook","description":null,"price":15.00}
                            """));
            verify(service).update(1L, "Notebook", null, new BigDecimal("15.00"));
        }

        @Test
        void returnsProblemDetailWhenItemIsMissing() throws Exception {
            when(service.update(eq(999L), anyString(), nullable(String.class), any(BigDecimal.class)))
                    .thenThrow(new ResourceNotFoundException("Item", 999L));

            mockMvc.perform(put("/api/v1/items/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value("Item not found"));
        }

        @ParameterizedTest
        @CsvSource({
                "'{}', name",
                "'{\"name\":null,\"price\":1}', name",
                "'{\"name\":\"   \",\"price\":1}', name",
                "'{\"name\":\"Notebook\"}', price",
                "'{\"name\":\"Notebook\",\"price\":null}', price",
                "'{\"name\":\"Notebook\",\"price\":-0.01}', price",
                "'{\"name\":\"Notebook\",\"price\":1.001}', price",
                "'{\"name\":\"Notebook\",\"price\":10000000000}', price"
        })
        void rejectsInvalidFieldsWithoutCallingService(String json, String field) throws Exception {
            mockMvc.perform(put("/api/v1/items/1")
                    .contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("Request validation failed"))
                    .andExpect(jsonPath("$.errors[?(@.field == '%s')]", field).isNotEmpty());
            verifyNoInteractions(service);
        }

        @Test
        void rejectsMalformedJson() throws Exception {
            mockMvc.perform(put("/api/v1/items/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.instance").value("/api/v1/items/1"));
        }

        @Test
        void rejectsMissingRequestBody() throws Exception {
            mockMvc.perform(put("/api/v1/items/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.instance").value("/api/v1/items/1"));
        }

        @Test
        void rejectsNonNumericIdWithoutCallingService() throws Exception {
            mockMvc.perform(put("/api/v1/items/abc")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(VALID_BODY))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
            verifyNoInteractions(service);
        }

        @Test
        void rejectsUnsupportedMediaType() throws Exception {
            mockMvc.perform(put("/api/v1/items/1")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("Updated"))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

    @Nested
    @WithMockUser
    class DeleteItem {
        @Test
        void deletesItemAndConfirmsDeletedId() throws Exception {
            mockMvc.perform(delete("/api/v1/items/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("{\"id\":1}", true));
            verify(service).delete(1L);
        }

        @Test
        void returnsProblemDetailWhenItemIsMissing() throws Exception {
            doThrow(new ResourceNotFoundException("Item", 999L)).when(service).delete(999L);

            mockMvc.perform(delete("/api/v1/items/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value("Item not found"));
        }

        @Test
        void rejectsNonNumericIdWithoutCallingService() throws Exception {
            mockMvc.perform(delete("/api/v1/items/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
            verifyNoInteractions(service);
        }
    }

    @Nested
    class UnauthenticatedRequests {
        @Test
        void rejectsCreate() throws Exception {
            expectUnauthorized(mockMvc.perform(post("/api/v1/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Notebook\",\"price\":12.50}")));
        }

        @Test
        void rejectsList() throws Exception {
            expectUnauthorized(mockMvc.perform(get("/api/v1/items")));
        }

        @Test
        void rejectsGetById() throws Exception {
            expectUnauthorized(mockMvc.perform(get("/api/v1/items/1")));
        }

        @Test
        void rejectsUpdate() throws Exception {
            expectUnauthorized(mockMvc.perform(put("/api/v1/items/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Notebook\",\"price\":15.00}")));
        }

        @Test
        void rejectsDelete() throws Exception {
            expectUnauthorized(mockMvc.perform(delete("/api/v1/items/1")));
        }

        @Test
        void rejectsUnmappedRoot() throws Exception {
            expectUnauthorized(mockMvc.perform(get("/")));
        }

        private void expectUnauthorized(ResultActions result) throws Exception {
            result.andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.title").value("Unauthorized"))
                    .andExpect(jsonPath("$.detail").value("Authentication required"))
                    .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, containsString("Basic")));
            verifyNoInteractions(service);
        }
    }

    @Nested
    class BasicCredentials {
        @Test
        void acceptsTheConfiguredUser() throws Exception {
            when(service.list()).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/items").with(httpBasic("test-user", "test-password")))
                    .andExpect(status().isOk());
            verify(service).list();
        }

        @Test
        void rejectsWrongPassword() throws Exception {
            mockMvc.perform(get("/api/v1/items").with(httpBasic("test-user", "wrong-password")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.detail").value("Authentication required"))
                    .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, containsString("Basic")));
            verifyNoInteractions(service);
        }

        @Test
        void rejectsUnknownUsername() throws Exception {
            mockMvc.perform(get("/api/v1/items").with(httpBasic("someone-else", "test-password")))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("Authentication required"));
            verifyNoInteractions(service);
        }
    }

    @Nested
    @WithMockUser
    class UnexpectedFailure {
        @Test
        void returnsGenericProblemWithoutLeakingDetailsWhenServiceThrows() throws Exception {
            when(service.list()).thenThrow(new IllegalStateException("Database credentials: secret"));

            mockMvc.perform(get("/api/v1/items"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.title").value("Internal Server Error"))
                    .andExpect(jsonPath("$.detail").value("Unexpected error"))
                    .andExpect(content().string(not(containsString("Database credentials"))))
                    .andExpect(content().string(not(containsString("IllegalStateException"))));
        }

        @Test
        void logsUnexpectedExceptionAtErrorLevelWithStack() throws Exception {
            var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ApiExceptionHandler.class);
            var appender = new ListAppender<ILoggingEvent>();
            appender.start();
            logger.addAppender(appender);
            try {
                when(service.list()).thenThrow(new IllegalStateException("trace me"));

                mockMvc.perform(get("/api/v1/items"))
                        .andExpect(status().isInternalServerError());

                var event = appender.list.stream()
                        .filter(entry -> entry.getLevel() == Level.ERROR)
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("No error-level log recorded for the unexpected exception"));
                assertNotNull(event.getThrowableProxy());
                assertEquals("trace me", event.getThrowableProxy().getMessage());
            } finally {
                logger.detachAppender(appender);
                appender.stop();
            }
        }
    }
}
