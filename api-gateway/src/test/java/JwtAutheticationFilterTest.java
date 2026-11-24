import com.arquiweb.gateway.security.JwtAuthenticationFilter;
import com.arquiweb.gateway.security.JwtValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class JwtAutheticationFilterTest {

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private GatewayFilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtValidator);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testFilter_OptionsRequest_PassesThroughWithoutValidation() {
        MockServerHttpRequest request = MockServerHttpRequest.options("/api/events").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, filterChain).block();

        verify(filterChain, times(1)).filter(exchange);
        verifyNoInteractions(jwtValidator);
    }

    @Test
    public void testFilter_NoAuthHeader_AllowsAnonymousConnection() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/events").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, filterChain).block();

        verify(filterChain, times(1)).filter(exchange);
        verifyNoInteractions(jwtValidator);
    }

    @Test
    public void testFilter_InvalidSchema_ReturnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Basic 123456")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, filterChain).block();

        Assert.assertEquals(exchange.getResponse().getStatusCode(), HttpStatus.UNAUTHORIZED);
        verify(filterChain, never()).filter(any());
        verifyNoInteractions(jwtValidator);
    }

    @Test
    public void testFilter_EmptyBearerToken_ReturnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer    ")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, filterChain).block();

        Assert.assertEquals(exchange.getResponse().getStatusCode(), HttpStatus.UNAUTHORIZED);
        verify(filterChain, never()).filter(any());
        verifyNoInteractions(jwtValidator);
    }

    @Test
    public void testFilter_ValidToken_InjectsHeadersAndSanitizesSpoofing() {
        String token = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("X-User-Id", "hacker-id")
                .header("X-User-Role", "ADMIN")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("user-123");
        when(claims.get("role", String.class)).thenReturn("CLIENT");
        when(claims.get("email", String.class)).thenReturn("user@test.com");

        when(jwtValidator.validateAndExtractClaims(token)).thenReturn(claims);

        // Act
        filter.filter(exchange, filterChain).block();

        // Assert
        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain, times(1)).filter(exchangeCaptor.capture());

        HttpHeaders resultHeaders = exchangeCaptor.getValue().getRequest().getHeaders();
        Assert.assertEquals(resultHeaders.getFirst("X-User-Id"), "user-123");
        Assert.assertEquals(resultHeaders.getFirst("X-User-Role"), "CLIENT");
        Assert.assertEquals(resultHeaders.getFirst("X-User-Email"), "user@test.com");
        Assert.assertEquals(resultHeaders.get("X-User-Id").size(), 1);
    }

    @Test
    public void testFilter_JwtException_ReturnsUnauthorized() {
        String token = "expired.or.invalid.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtValidator.validateAndExtractClaims(token))
                .thenThrow(new JwtException("Token expired"));

        filter.filter(exchange, filterChain).block();

        Assert.assertEquals(exchange.getResponse().getStatusCode(), HttpStatus.UNAUTHORIZED);
        verify(filterChain, never()).filter(any());
    }

    @Test
    public void testFilter_IllegalArgumentException_ReturnsUnauthorized() {
        String token = "malformed.token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/reservations")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtValidator.validateAndExtractClaims(token))
                .thenThrow(new IllegalArgumentException("Invalid token argument"));

        filter.filter(exchange, filterChain).block();

        Assert.assertEquals(exchange.getResponse().getStatusCode(), HttpStatus.UNAUTHORIZED);
        verify(filterChain, never()).filter(any());
    }

    @Test
    public void testGetOrder_ReturnsMinusOne() {
        Assert.assertEquals(filter.getOrder(), -1);
    }
}
