import com.arquiweb.gateway.security.JwtValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtValidatorTest {
    private static final String TEST_BASE64_SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdmFsaWRhdGlvbi0yNTYtYml0cw==";
    private static final String OTHER_BASE64_SECRET = "YW5vdGhlci1zZWNyZXQta2V5LWZvci1pbnZhbGlkLXNpZ25hdHVyZS10ZXN0";

    private JwtValidator jwtValidator;
    private SecretKey signingKey;

    @BeforeMethod
    public void setUp() {
        jwtValidator = new JwtValidator(TEST_BASE64_SECRET);
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_BASE64_SECRET));
    }

    @Test
    public void testValidateAndExtractClaims_ValidToken_ReturnsClaims() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600_000);

        String validToken = Jwts.builder()
                .subject("user-123")
                .claim("role", "CLIENT")
                .claim("email", "user@test.com")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();

        Claims claims = jwtValidator.validateAndExtractClaims(validToken);

        Assert.assertNotNull(claims);
        Assert.assertEquals(claims.getSubject(), "user-123");
        Assert.assertEquals(claims.get("role", String.class), "CLIENT");
        Assert.assertEquals(claims.get("email", String.class), "user@test.com");
    }

    @Test(expectedExceptions = ExpiredJwtException.class)
    public void testValidateAndExtractClaims_ExpiredToken_ThrowsExpiredJwtException() {
        Date pastIssued = new Date(System.currentTimeMillis() - 7200_000);
        Date pastExpiration = new Date(System.currentTimeMillis() - 3600_000);

        String expiredToken = Jwts.builder()
                .subject("user-123")
                .issuedAt(pastIssued)
                .expiration(pastExpiration)
                .signWith(signingKey)
                .compact();

        jwtValidator.validateAndExtractClaims(expiredToken);
    }

    @Test(expectedExceptions = SignatureException.class)
    public void testValidateAndExtractClaims_InvalidSignature_ThrowsSignatureException() {
        SecretKey wrongKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(OTHER_BASE64_SECRET));

        String forgedToken = Jwts.builder()
                .subject("hacker-999")
                .claim("role", "ADMIN")
                .signWith(wrongKey)
                .compact();

        jwtValidator.validateAndExtractClaims(forgedToken);
    }

    @Test(expectedExceptions = MalformedJwtException.class)
    public void testValidateAndExtractClaims_MalformedToken_ThrowsMalformedJwtException() {
        jwtValidator.validateAndExtractClaims("esto.no.es.un.jwt.valido");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValidateAndExtractClaims_EmptyToken_ThrowsIllegalArgumentException() {
        jwtValidator.validateAndExtractClaims("");
    }
}
