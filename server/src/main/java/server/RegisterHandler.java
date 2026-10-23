package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UserService;
import service.request.RegisterRequest;
import service.result.RegisterResult;

public class RegisterHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context context) {
        try {
            RegisterRequest request = gson.fromJson(
                    context.body(),
                    RegisterRequest.class
            );

            RegisterResult result =
                    userService.register(request);

            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(result));

        } catch (JsonSyntaxException
                 | BadRequestException exception) {

            sendError(context, 400, "bad request");

        } catch (AlreadyTakenException exception) {

            sendError(context, 403, "already taken");

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
}