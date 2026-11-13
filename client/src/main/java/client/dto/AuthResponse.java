package client.dto;

public record AuthResponse (
    String username,
    String authToken
) {

}