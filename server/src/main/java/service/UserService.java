package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import service.request.LoginRequest;
import service.result.LoginResult;

import org.mindrot.jbcrypt.BCrypt;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request)
            throws BadRequestException,
            AlreadyTakenException,
            DataAccessException {

        if (request == null
                || request.username() == null
                || request.password() == null
                || request.email() == null) {
            throw new BadRequestException(
                    "Missing registration information"
            );
        }

        UserData existingUser = userDAO.getUser(request.username());

        if (existingUser != null) {
            throw new AlreadyTakenException(
                    "Username is already taken"
            );
        }

        UserData userData = new UserData(
                request.username(),
                request.password(),
                request.email()
        );

        String passwordHash = BCrypt.hashpw(
                userData.password(),
                BCrypt.gensalt()
        );

        UserData storedUser = new UserData(
                userData.username(),
                passwordHash,
                userData.email()
        );

        userDAO.createUserData(storedUser);

        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(
                authToken,
                request.username()
        );

        authDAO.createAuth(authData);

        return new RegisterResult(
                request.username(),
                authToken
        );
    }

    public LoginResult login(LoginRequest request)
            throws BadRequestException,
            UnauthorizedException,
            DataAccessException {

        if (request == null
                || request.username() == null
                || request.password() == null) {
            throw new BadRequestException(
                    "Missing login information"
            );
        }

        UserData user =
                userDAO.getUser(request.username());

        if (user == null ||
                !BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException(
                    "Error: incorrect username or password"
            );
        }

        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(
                authToken,
                user.username()
        );

        authDAO.createAuth(authData);

        return new LoginResult(
                user.username(),
                authToken
        );
    }

    public void logout(String authToken)
            throws UnauthorizedException,
            DataAccessException {

        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            throw new UnauthorizedException(
                    "Invalid authentication token"
            );
        }

        authDAO.deleteAuth(authToken);
    }

}