package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import service.request.LoginRequest;
import service.result.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "username",
                "password123",
                "email@example.com"
        );

        RegisterResult result = userService.register(request);

        assertEquals("username", result.username());
        assertNotNull(result.authToken());

        assertNotNull(userDAO.getUser("username"));
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void registerMissingPassword() {
        RegisterRequest request = new RegisterRequest(
                "username",
                null,
                "email@example.com"
        );

        assertThrows(
                BadRequestException.class,
                () -> userService.register(request)
        );
    }

    @Test
    public void registerDuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "username",
                "password123",
                "email@example.com"
        );

        userService.register(request);

        assertThrows(
                AlreadyTakenException.class,
                () -> userService.register(request)
        );
    }

    @Test
    public void loginTests() {
        assertThrows(
                BadRequestException.class,
                () -> userService.login(
                        new LoginRequest("username", null)
                )
        );

        assertThrows(
                UnauthorizedException.class,
                () -> userService.login(
                        new LoginRequest("Unknown", "password")
                )
        );

        assertThrows(
                UnauthorizedException.class,
                () -> userService.login(
                        new LoginRequest("Samuel", "wrong")
                )
        );
    }

    @Test
    public void logoutTests() {
        assertThrows(
                UnauthorizedException.class,
                () -> userService.logout("bad-token")
        );
    }
}