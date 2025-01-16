package src;

import src.Controller.GameController;
import src.Model.*;
import src.View.GameView;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // create paths
        List<Path> paths = initializePaths();

        // create players
        Player player1 = new Player("Player 1" ,1);
        Player player2 = new Player("Player 2" ,2);
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // create deck
        Deck deck = new Deck();

        // create board
        Board board = new Board(paths);

        // create view
        GameView view = new GameView();

        // create and start the controller
        GameController controller = new GameController(view, players, deck, board, paths);
        controller.startGame();
    }

    private static List<Path> initializePaths() {
        List<Path> paths = new ArrayList<>();
        paths.add(new Path("Knossos", 1));
        paths.add(new Path("Phaistos", 2));
        paths.add(new Path("Malia", 3));
        paths.add(new Path("Zakros", 4));
        return paths;
    }
}