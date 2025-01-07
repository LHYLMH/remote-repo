import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gomoku extends JFrame {
    private static final int SIZE = 15; // 棋盘大小
    private static final int CELL_SIZE = 40; // 每个格子的大小
    private static final char EMPTY = '.'; // 空位
    private static final char PLAYER1 = 'X'; // 玩家1
    private static final char PLAYER2 = 'O'; // 玩家2
    private static char[][] board = new char[SIZE][SIZE]; // 棋盘
    private static char currentPlayer = PLAYER1; // 当前玩家
    private JPanel boardPanel; // 棋盘面板

    // 初始化棋盘
    public Gomoku() {
        initBoard();
        initUI();
    }

    // 初始化棋盘数据
    private void initBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    // 初始化UI
    private void initUI() {
        setTitle("五子棋游戏");
        setSize(SIZE * CELL_SIZE, SIZE * CELL_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };

        boardPanel.setPreferredSize(new Dimension(SIZE * CELL_SIZE, SIZE * CELL_SIZE));
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;
                if (x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == EMPTY) {
                    board[x][y] = currentPlayer;
                    boardPanel.repaint();
                    if (checkWin(currentPlayer)) {
                        JOptionPane.showMessageDialog(Gomoku.this, "玩家 " + currentPlayer + " 获胜！");
                        initBoard(); // 重置棋盘
                        boardPanel.repaint();
                    } else {
                        currentPlayer = (currentPlayer == PLAYER1) ? PLAYER2 : PLAYER1;
                    }
                }
            }
        });

        add(boardPanel);
        setVisible(true);
    }

    // 绘制棋盘
    private void drawBoard(Graphics g) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                if (board[i][j] == PLAYER1) {
                    g.drawString("X", i * CELL_SIZE + CELL_SIZE / 2, j * CELL_SIZE + CELL_SIZE / 2);
                } else if (board[i][j] == PLAYER2) {
                    g.drawString("O", i * CELL_SIZE + CELL_SIZE / 2, j * CELL_SIZE + CELL_SIZE / 2);
                }
            }
        }
    }

    // 检查是否有五子连珠
    private boolean checkWin(char player) {
        // 检查行
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j <= SIZE - 5; j++) {
                if (board[i][j] == player && board[i][j + 1] == player &&
                        board[i][j + 2] == player && board[i][j + 3] == player &&
                        board[i][j + 4] == player) {
                    return true;
                }
            }
        }
        // 检查列
        for (int j = 0; j < SIZE; j++) {
            for (int i = 0; i <= SIZE - 5; i++) {
                if (board[i][j] == player && board[i + 1][j] == player &&
                        board[i + 2][j] == player && board[i + 3][j] == player &&
                        board[i + 4][j] == player) {
                    return true;
                }
            }
        }
        // 检查主对角线
        for (int i = 0; i <= SIZE - 5; i++) {
            for (int j = 0; j <= SIZE - 5; j++) {
                if (board[i][j] == player && board[i + 1][j + 1] == player &&
                        board[i + 2][j + 2] == player && board[i + 3][j + 3] == player &&
                        board[i + 4][j + 4] == player) {
                    return true;
                }
            }
        }
        // 检查副对角线
        for (int i = 0; i <= SIZE - 5; i++) {
            for (int j = 4; j < SIZE; j++) {
                if (board[i][j] == player && board[i + 1][j - 1] == player &&
                        board[i + 2][j - 2] == player && board[i + 3][j - 3] == player &&
                        board[i + 4][j - 4] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Gomoku());
    }
}

