package client;

import chess.*;

import java.util.Scanner;

public class ClientMain {
    State state;

    public enum State {
        QUIT,
        SIGNEDOUT,
        SIGNEDIN
    }

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }

    public void run(Scanner scanner) {
        System.out.println("Welcome to Chess. Type Help to get started.");
        while (!state.equals(State.QUIT)) {
            String input = scanner.nextLine();
            if (state == State.SIGNEDOUT) {
                handlePreLogin(input);
            } else {
                handlePostLogin(input);
            }
        }
    }

    public static void handlePreLogin(String input) {}

    public static void handlePostLogin(String input) {}
}
