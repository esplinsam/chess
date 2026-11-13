package client.dto;

public record GameSummary (
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName
) {

}