package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context context) {
        try {
            clearService.clear();

            context.status(200);
            context.contentType("application/json");
            context.result("{}");
        } catch (DataAccessException exception) {
            ErrorResponse response = new ErrorResponse(
                    "Error: " + exception.getMessage()
            );

            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(response));
        }
    }
}