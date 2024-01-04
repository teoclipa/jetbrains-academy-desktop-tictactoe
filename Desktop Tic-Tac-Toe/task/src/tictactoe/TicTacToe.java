package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class TicTacToe extends JFrame {

    private final Board board;
    private final JLabel statusLabel;
    private JButton player1Button;
    private JButton player2Button;
    private JButton startResetButton;
    private boolean isPlayer1Turn = true; // True if it's Player 1's turn, false for Player 2
    private boolean isGameInProgress = false;

    public TicTacToe() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tic Tac Toe");
        setResizable(false);
        setSize(450, 450);
        setLocationRelativeTo(null);

        statusLabel = new JLabel("Game is not started");
        statusLabel.setName("LabelStatus");

        board = new Board();

        player1Button = new JButton("Human");
        player1Button.setName("ButtonPlayer1");
        player2Button = new JButton("Human");
        player2Button.setName("ButtonPlayer2");

        player1Button.addActionListener(e -> togglePlayerMode(player1Button));
        player2Button.addActionListener(e -> togglePlayerMode(player2Button));

        startResetButton = new JButton("Start");
        startResetButton.setName("ButtonStartReset");
        startResetButton.addActionListener(e -> startOrResetGame());

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(player1Button);
        toolbar.add(player2Button);
        toolbar.add(startResetButton);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuGame = new JMenu("Game");
        menuGame.setName("MenuGame");

        JMenuItem menuHumanHuman = new JMenuItem("Human vs Human");
        menuHumanHuman.setName("MenuHumanHuman");
        menuHumanHuman.addActionListener(e -> startGame("Human", "Human"));

        JMenuItem menuHumanRobot = new JMenuItem("Human vs Robot");
        menuHumanRobot.setName("MenuHumanRobot");
        menuHumanRobot.addActionListener(e -> startGame("Human", "Robot"));

        JMenuItem menuRobotHuman = new JMenuItem("Robot vs Human");
        menuRobotHuman.setName("MenuRobotHuman");
        menuRobotHuman.addActionListener(e -> startGame("Robot", "Human"));

        JMenuItem menuRobotRobot = new JMenuItem("Robot vs Robot");
        menuRobotRobot.setName("MenuRobotRobot");
        menuRobotRobot.addActionListener(e -> startGame("Robot", "Robot"));

        JMenuItem menuExit = new JMenuItem("Exit");
        menuExit.setName("MenuExit");
        menuExit.addActionListener(e -> System.exit(0));

        menuGame.add(menuHumanHuman);
        menuGame.add(menuHumanRobot);
        menuGame.add(menuRobotHuman);
        menuGame.add(menuRobotRobot);
        menuGame.addSeparator();
        menuGame.add(menuExit);

        menuBar.add(menuGame);
        setJMenuBar(menuBar);

        add(toolbar, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);
        add(board);

        setVisible(true);
    }

    private void startGame(String player1Type, String player2Type) {
        player1Button.setText(player1Type);
        player2Button.setText(player2Type);
        resetGameToInitialState(); // Reset the board and set the game as not started
        isPlayer1Turn = player1Type.equals("Human"); // If the first player is Human, they start, otherwise Robot starts
        isGameInProgress = true;
        startResetButton.setText("Reset");
        updateStatusLabel(); // Make sure to update the status label to show who's turn it is
        disablePlayerButtons(); // Disable player selection buttons
        enableCells(); // Enable all the cells for the game

        if (player1Type.equals("Robot")) {
            makeComputerMove(); // If the first player is Robot, make the computer move
        }
    }

    private void resetGameToInitialState() {
        board.resetBoard();
        for (JButton cell : board.cells) {
            cell.setText(" ");
            cell.setEnabled(false); // Cells should be disabled when the game is not started
        }
        statusLabel.setText("Game is not started");
        isGameInProgress = false;
        startResetButton.setText("Start");
        enablePlayerSelection(); // Player selection should be enabled after reset
    }


    private void togglePlayerMode(JButton playerButton) {
        if (playerButton.getText().equals("Human")) {
            playerButton.setText("Robot");
        } else {
            playerButton.setText("Human");
        }
    }

    private void checkGameState() {
        String result = checkForWinOrDraw();
        if (!result.isEmpty()) {
            if (result.equals("Draw")) {
                statusLabel.setText("Draw");
            } else {
                String winner = isPlayer1Turn ? player1Button.getText() : player2Button.getText();
                statusLabel.setText(MessageFormat.format("The {0} Player ({1}) wins", winner, isPlayer1Turn ? "O" : "X"));
            }
            isGameInProgress = false;
            disableAllCells();
            startResetButton.setText("Reset");
        } else {
            String currentPlayer = isPlayer1Turn ? player1Button.getText() : player2Button.getText();
            statusLabel.setText(MessageFormat.format("The turn of {0} Player ({1})", currentPlayer, isPlayer1Turn ? "X" : "O"));
        }
    }

    private void enablePlayerSelection() {
        player1Button.setEnabled(true);
        player2Button.setEnabled(true);
    }

    private void startOrResetGame() {
        if (isGameInProgress || startResetButton.getText().equals("Reset")) {
            // If the game has just finished, reset the game state
            resetGameToInitialState();
            startResetButton.setText("Start");  // Change button text to "Start"
            enablePlayerSelection();  // Enable player selection for a new game
            statusLabel.setText("Game is not started");
        } else {
            startNewGame();
        }
    }

    private void startNewGame() {
        board.resetBoard();
        isPlayer1Turn = true; // Assuming Player 1 is always "X" and starts the game
        isGameInProgress = true;
        startResetButton.setText("Reset");
        disablePlayerButtons();
        enableCells();
        updateStatusLabel(); // Update the status label right when the game starts

        if (player1Button.getText().equals("Robot")) {
            makeComputerMove();
        }
    }

    private void updateStatusLabel() {
        if (!isGameInProgress) {
            statusLabel.setText("Game is not started");
        } else {
            String player = isPlayer1Turn ? player1Button.getText() : player2Button.getText();
            String mark = isPlayer1Turn ? "X" : "O";
            statusLabel.setText(MessageFormat.format("The turn of {0} Player ({1})", player, mark));
        }
    }

    private void disablePlayerButtons() {
        player1Button.setEnabled(false);
        player2Button.setEnabled(false);
    }

    private void enableCells() {
        for (JButton cell : board.cells) {
            cell.setEnabled(true);
        }
    }

    private void makeComputerMove() {
        List<JButton> freeCells = Arrays.stream(board.cells).filter(button -> button.getText().equals(" ")).toList();
        if (freeCells.size() == 9) return;
        if (!freeCells.isEmpty()) {
            int randomIndex = (int) (Math.random() * freeCells.size());
            JButton randomCell = freeCells.get(randomIndex);
            randomCell.doClick();
        }
    }

    class Board extends JPanel {
        private final JButton[] cells = new JButton[9];

        public Board() {
            setLayout(new GridLayout(3, 3));
            initCells();
        }

        private void initCells() {
            for (int i = 3; i >= 1; i--) {
                for (char c = 'A'; c <= 'C'; c++) {
                    String name = "Button" + c + i;
                    JButton button = new JButton(" ");
                    button.setName(name);
                    button.setFocusPainted(false);
                    button.addActionListener(new CellActionListener());
                    button.setEnabled(false);
                    cells[(c - 'A') + (3 - i) * 3] = button;
                    add(button);
                }
            }
        }

        public void resetBoard() {
            for (JButton cell : cells) {
                cell.setText(" ");
                cell.setEnabled(false);
            }
            isPlayer1Turn = true;
        }
    }

    private class CellActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isGameInProgress) return;
            JButton button = (JButton) e.getSource();
            if (!button.getText().equals(" ")) return;

            button.setText(isPlayer1Turn ? "X" : "O");
            button.setEnabled(false);

            // Toggle the turn before updating the status label and checking the game state
            isPlayer1Turn = !isPlayer1Turn;
            updateStatusLabel(); // Update the status label here to reflect the next player's turn
            checkGameState(); // This will now properly show the result or the next turn

            // Trigger the computer move if it is the computer's turn
            if (isGameInProgress && player1Button.getText().equals("Robot") && isPlayer1Turn) {
                makeComputerMove();
            } else if (isGameInProgress && player2Button.getText().equals("Robot") && !isPlayer1Turn) {
                makeComputerMove();
            }
        }
    }

    private void disableAllCells() {
        for (JButton cell : board.cells) {
            cell.setEnabled(false);
        }
    }

    private String checkForWinOrDraw() {
        int[][] winCombinations = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combo : winCombinations) {
            if (!board.cells[combo[0]].getText().equals(" ") &&
                    board.cells[combo[0]].getText().equals(board.cells[combo[1]].getText()) &&
                    board.cells[combo[1]].getText().equals(board.cells[combo[2]].getText())) {
                return board.cells[combo[0]].getText() + " wins";
            }
        }

        boolean isDraw = Arrays.stream(board.cells).noneMatch(button -> button.getText().equals(" "));
        if (isDraw) {
            return "Draw";
        }

        return "";
    }

}
