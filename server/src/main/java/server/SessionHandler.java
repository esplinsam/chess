package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;
import service.request.LoginRequest;
import service.result.LoginResult;

public class SessionHandler {
    private final UserService userService;
    private final Gson gson;

    public SessionHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    public void login(Context context) {
        try {
            LoginRequest request = gson.fromJson(
                    context.body(),
                    LoginRequest.class
            );

            LoginResult result =
                    userService.login(request);

            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(result));

        } catch (JsonSyntaxException
                 | BadRequestException exception) {

            sendError(context, 400, "bad request");

        } catch (UnauthorizedException exception) {

            sendError(context, 401, "unauthorized");

        } catch (DataAccessException exception) {

            sendError(
                    context,
                    500,
                    exception.getMessage()
            );
        }
    }

    private void sendError(
            Context context,
            int status,
            String description
    ) {
        context.status(status);
        context.contentType("application/json");
        context.result(gson.toJson(
                new ErrorResponse(
                        "Error: " + description
                )
        ));
    }

    public void logout(Context context) {
        try {
            String authToken =
                    context.header("authorization");

            userService.logout(authToken);

            context.status(200);
            context.contentType("application/json");
            context.result("{}");

        } catch (UnauthorizedException exception) {

            sendError(
                    context,
                    401,
                    "unauthorized"
            );

        } catch (DataAccessException exception) {

            sendError(
                    context,
                    500,
                    exception.getMessage()
            );
        }
    }


}