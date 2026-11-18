import com.arquiweb.identity.auth.domain.User;
import com.arquiweb.identity.auth.domain.enums.UserRole;
import com.arquiweb.identity.auth.domain.exceptions.InvalidCredentialsException;
import com.arquiweb.identity.auth.dto.requests.LoginRequest;
import com.arquiweb.identity.auth.dto.requests.RegisterClientRequest;
import com.arquiweb.identity.auth.dto.responses.AuthResponse;
import com.arquiweb.identity.auth.repository.UserRepository;
import com.arquiweb.identity.auth.service.AuthService;
import com.arquiweb.identity.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.identity.infrastructure.util.JwtProvider;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    private AuthService authService;
    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, passwordEncoder, jwtProvider);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testRegisterClient_Successful_ReturnsGeneratedId() {
        RegisterClientRequest request = new RegisterClientRequest(
                "julianfidel06@gmail.com",
                "Julian",
                "Yañez",
                "Password123!",
                "Password123!"
        );

        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password-hash");

        UUID expectedId = UUID.randomUUID();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // Act
        UUID resultId = authService.registerClient(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        Assert.assertEquals(capturedUser.getEmail(), "julianfidel06@gmail.com");
        Assert.assertEquals(resultId, capturedUser.getId());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testRegisterClient_PasswordsMismatch_ThrowsExceptionAndNeverSaves() {
        RegisterClientRequest request = new RegisterClientRequest(
                "nuevo.usuario@test.com",
                "Julian",
                "Yañez",
                "Password123!",
                "OtraPassword456!"
        );

        // Act
        authService.registerClient(request);

        // Assert
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testLogin_Successful_NormalizesEmailAndReturnsAuthResponse() {
        // Arrange
        LoginRequest request = new LoginRequest("  User@Test.COM  ", "Password123!");
        UUID userId = UUID.randomUUID();

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getEmail()).thenReturn("user@test.com");
        when(mockUser.getRole()).thenReturn(UserRole.values()[0]);
        when(mockUser.verifyPassword("Password123!", passwordEncoder)).thenReturn(true);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(mockUser));
        when(jwtProvider.generateToken(mockUser)).thenReturn("jwt.token.generado");
        when(jwtProvider.getExpirationMs()).thenReturn(3600000L);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        Assert.assertNotNull(response);
        Assert.assertEquals(response.accessToken(), "jwt.token.generado");
        Assert.assertEquals(response.userId(), userId);
        Assert.assertEquals(response.email(), "user@test.com");

        verify(userRepository, times(1)).findByEmail("user@test.com");
        verify(jwtProvider, times(1)).generateToken(mockUser);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testLogin_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        LoginRequest request = new LoginRequest("noexiste@test.com", "Password123!");
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        // Act
        authService.login(request);

        // Assert
        verify(jwtProvider, never()).generateToken(any());
    }

    @Test(expectedExceptions = InvalidCredentialsException.class)
    public void testLogin_WrongPassword_ThrowsInvalidCredentialsException() {
        // Arrange
        LoginRequest request = new LoginRequest("user@test.com", "WrongPassword!");

        User mockUser = mock(User.class);
        when(mockUser.verifyPassword("WrongPassword!", passwordEncoder)).thenReturn(false);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(mockUser));

        // Act
        authService.login(request);

        // Assert
        verify(jwtProvider, never()).generateToken(any());
    }
}
