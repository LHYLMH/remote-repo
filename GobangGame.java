import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

public class GobangGame extends JPanel {
    private int boardSize; // 棋盘大小
    static final int CELL_SIZE = 40;
    private int[][] board; // 0: 空, 1: 黑子, 2: 白子
    private int currentPlayer = 1; // 1 = 黑子, 2 = 白子
    private Stack<Move> moveHistory = new Stack<>();
    private boolean isVsAI = false; // 是否对战AI
    private int robot_x, robot_y; // AI 下棋的位置

    private static class Move {
        int x, y, player;
        Move(int x, int y, int player) {
            this.x = x;
            this.y = y;
            this.player = player;
        }
    }

    public GobangGame(int size) {
        this.boardSize = size;
        this.board = new int[boardSize][boardSize];
        this.setPreferredSize(new Dimension(boardSize * CELL_SIZE, boardSize * CELL_SIZE));
        this.setBackground(new Color(204, 153, 51)); // 背景色 #cc9933
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    private void handleMouseClick(int x, int y) {
        int row = y / CELL_SIZE; // 注意行与列的计算
        int col = x / CELL_SIZE;

        if (row < boardSize && col < boardSize && board[row][col] == 0) {
            // 玩家下棋
            board[row][col] = currentPlayer;
            moveHistory.push(new Move(row, col, currentPlayer));

            // 检查是否胜利
            if (checkWinner(row, col)) {
                repaint();  // 先绘制最后一颗棋子
                JOptionPane.showMessageDialog(this, (currentPlayer == 1 ? "黑方" : "白方") + " 胜利!");
                reset(); // 重置游戏
                return; // 结束游戏
            }

            // 如果是对战AI，切换到AI下棋
            if (isVsAI && currentPlayer == 1) {
                currentPlayer = 2; // 切换到白棋（AI）
                machine(); // AI下棋
                makeMove(robot_x, robot_y);
                if (checkWinner(robot_x, robot_y)) {
                    JOptionPane.showMessageDialog(this, "白方 胜利!");
                    reset(); // 重置游戏
                }
                currentPlayer = 1; // 切换回黑棋（玩家）
            } else {
                currentPlayer = (currentPlayer == 1) ? 2 : 1; // 切换玩家
            }
            repaint();
        }
    }

    private void makeMove(int row, int col) {
        board[row][col] = currentPlayer;
        moveHistory.push(new Move(row, col, currentPlayer));
        repaint();
    }

    private boolean checkWinner(int row, int col) {
        int player = board[row][col];
        return checkDirection(row, col, 1, 0, player) || // 水平
                checkDirection(row, col, 0, 1, player) || // 垂直
                checkDirection(row, col, 1, 1, player) || // 斜线（正对角线）
                checkDirection(row, col, 1, -1, player);  // 斜线（反对角线）
    }

    private boolean checkDirection(int row, int col, int dx, int dy, int player) {
        int count = 1;

        for (int i = 1; i < 5; i++) {
            int r = row + i * dx, c = col + i * dy;
            if (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == player) {
                count++;
            } else {
                break;
            }
        }

        for (int i = 1; i < 5; i++) {
            int r = row - i * dx, c = col - i * dy;
            if (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board[r][c] == player) {
                count++;
            } else {
                break;
            }
        }

        return count >= 5; // 判断是否有五子连珠
    }

    private void machine() {
        int[][] ts = new int[boardSize][boardSize]; // 记录每个点的得分

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                ts[i][j] = 0;
            }
        }

        int wn; // 白色个数
        int bn; // 黑色个数

        // 横向
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize - 4; j++) {
                wn = 0;
                bn = 0;
                for (int k = j; k < j + 5; k++) {
                    if (board[i][k] == 1) {
                        wn++;
                    } else if (board[i][k] == 2) {
                        bn++;
                    }
                }
                for (int k = j; k < j + 5; k++) {
                    if (board[i][k] == 0) {
                        ts[i][k] += score(wn, bn);
                    }
                }
            }
        }

        // 纵向
        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize - 4; i++) {
                wn = 0;
                bn = 0;
                for (int k = i; k < i + 5; k++) {
                    if (board[k][j] == 1) {
                        wn++;
                    } else if (board[k][j] == 2) {
                        bn++;
                    }
                }
                for (int k = i; k < i + 5; k++) {
                    if (board[k][j] == 0) {
                        ts[k][j] += score(wn, bn);
                    }
                }
            }
        }

        // 左上 右下
        for (int i = 0; i < boardSize - 4; i++) {
            for (int j = 0; j < boardSize - 4; j++) {
                wn = 0;
                bn = 0;
                for (int ki = i, kj = j; ki < i + 5; ki++, kj++) {
                    if (board[ki][kj] == 1) {
                        wn++;
                    } else if (board[ki][kj] == 2) {
                        bn++;
                    }
                }
                for (int ki = i, kj = j; ki < i + 5; ki++, kj++) {
                    if (board[ki][kj] == 0) {
                        ts[ki][kj] += score(wn, bn);
                    }
                }
            }
        }

        // 右上 左下
        for (int i = 4; i < boardSize; i++) {
            for (int j = 0; j < boardSize - 4; j++) {
                wn = 0;
                bn = 0;
                for (int ki = i, kj = j; kj < j + 5; ki--, kj++) {
                    if (board[ki][kj] == 1) {
                        wn++;
                    } else if (board[ki][kj] == 2) {
                        bn++;
                    }
                }
                for (int ki = i, kj = j; kj < j + 5; ki--, kj++) {
                    if (board[ki][kj] == 0) {
                        ts[ki][kj] += score(wn, bn);
                    }
                }
            }
        }

        Vector<Integer> vv = new Vector<>();
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (ts[i][j] > max) {
                    max = ts[i][j];
                }
            }
        }
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (ts[i][j] == max) {
                    vv.add(i);
                    vv.add(j);
                }
            }
        }
        Random random = new Random();
        int r = random.nextInt(vv.size() / 2);
        robot_x = vv.get(r * 2);
        robot_y = vv.get(r * 2 + 1);
        vv.clear();
    }

    private int score(int w, int b) {
        if (w > 0 && b > 0) {
            return 0;
        }
        if (w == 0 && b == 0) {
            return 7;
        }
        if (w == 1) {
            return 35;
        }
        if (w == 2) {
            return 800;
        }
        if (w == 3) {
            return 15000;
        }
        if (w == 4) {
            return 800000;
        }
        if (b == 1) {
            return 15;
        }
        if (b == 2) {
            return 400;
        }
        if (b == 3) {
            return 1800;
        }
        if (b == 4) {
            return 100000;
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();

        // 绘制棋盘网格
        for (int i = 0; i < boardSize; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, height); // 垂直线
            g.drawLine(0, i * CELL_SIZE, width, i * CELL_SIZE); // 水平线
        }

        // 绘制棋子
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == 1) {
                    g.setColor(Color.BLACK);
                    g.fillOval(j * CELL_SIZE + 5, i * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                } else if (board[i][j] == 2) {
                    g.setColor(Color.WHITE);
                    g.fillOval(j * CELL_SIZE + 5, i * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                }
            }
        }
    }


    public void reset() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = 0;
            }
        }
        moveHistory.clear();
        currentPlayer = 1;
        repaint();
    }

    public void undo() {
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.pop();
            board[lastMove.x][lastMove.y] = 0;
            currentPlayer = lastMove.player;
            repaint();
        }
    }

    public void setVsAI(boolean isVsAI) {
        this.isVsAI = isVsAI;
    }
}
