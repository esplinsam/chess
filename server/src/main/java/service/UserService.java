package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.request.RegisterRequest;
import service.result.RegisterResult;

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

        userDAO.createUserData(userData);

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
}