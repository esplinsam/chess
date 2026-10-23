package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.RegisterRequest;
import service.result.RegisterResult;

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
                "Samuel",
                "password123",
                "samuel@example.com"
        );

        RegisterResult result = userService.register(request);

        assertEquals("Samuel", result.username());
        assertNotNull(result.authToken());

        assertNotNull(userDAO.getUser("Samuel"));
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void registerMissingPassword() {
        RegisterRequest request = new RegisterRequest(
                "Samuel",
                null,
                "samuel@example.com"
        );

        assertThrows(
                BadRequestException.class,
                () -> userService.register(request)
        );
    }

    @Test
    public void registerDuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Samuel",
                "password123",
                "samuel@example.com"
        );

        userService.register(request);

        assertThrows(
                AlreadyTakenException.class,
                () -> userService.register(request)
        );
    }
}