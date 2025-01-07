import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    private GobangGame game;
    private boolean isVsAI = false;
    private JPanel mainPanel; // 主面板，用于切换内容
    private CardLayout cardLayout; // 卡片布局，用于切换面板

    public GameFrame() {
        setTitle("五子棋");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 使用卡片布局
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // 初始面板：输入棋盘大小
        JPanel sizeInputPanel = new JPanel(new BorderLayout());
        JLabel sizeLabel = new JLabel("请输入棋盘大小 :", SwingConstants.CENTER);
        JTextField sizeField = new JTextField(10);
        JButton confirmButton = new JButton("确认");

        JPanel inputPanel = new JPanel();
        inputPanel.add(sizeLabel);
        inputPanel.add(sizeField);
        inputPanel.add(confirmButton);

        sizeInputPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(sizeInputPanel, "sizeInput");

        // 确认按钮事件
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int boardSize = Integer.parseInt(sizeField.getText());
                    if (boardSize < 5 || boardSize > 20) {
                        JOptionPane.showMessageDialog(GameFrame.this, "棋盘大小必须在 5 到 20 之间！");
                        return;
                    }
                    showModeSelection(boardSize); // 显示模式选择面板
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GameFrame.this, "请输入有效的数字！");
                }
            }
        });

        // 设置窗口大小并居中显示
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showModeSelection(int boardSize) {
        // 模式选择面板
        JPanel modeSelectionPanel = new JPanel(new BorderLayout());
        JLabel modeLabel = new JLabel("请选择对战模式：", SwingConstants.CENTER);
        JButton vsAIButton = new JButton("人机对战");
        JButton vsHumanButton = new JButton("人人对战");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(vsAIButton);
        buttonPanel.add(vsHumanButton);

        modeSelectionPanel.add(modeLabel, BorderLayout.CENTER);
        modeSelectionPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 人机对战按钮事件
        vsAIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isVsAI = true;
                startGame(boardSize); // 开始游戏
            }
        });

        // 人人对战按钮事件
        vsHumanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isVsAI = false;
                startGame(boardSize); // 开始游戏
            }
        });

        mainPanel.add(modeSelectionPanel, "modeSelection");
        cardLayout.show(mainPanel, "modeSelection"); // 切换到模式选择面板
    }

    private void startGame(int boardSize) {
        // 创建游戏面板
        game = new GobangGame(boardSize);
        game.setVsAI(isVsAI);

        // 设置窗口大小为棋盘大小
        setSize(boardSize * GobangGame.CELL_SIZE + 16, boardSize * GobangGame.CELL_SIZE + 39);
        setLocationRelativeTo(null); // 窗口居中显示

        // 游戏控制面板
        JPanel gameControlPanel = new JPanel();
        JButton resetButton = new JButton("重新开始");
        JButton undoButton = new JButton("悔棋");
        JButton backButton = new JButton("返回");

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.reset();
                game.setVsAI(isVsAI);
            }
        });

        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.undo();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "sizeInput"); // 返回输入棋盘大小面板
            }
        });

        gameControlPanel.add(resetButton);
        gameControlPanel.add(undoButton);
        gameControlPanel.add(backButton);

        // 主游戏面板
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(game, BorderLayout.CENTER);
        gamePanel.add(gameControlPanel, BorderLayout.SOUTH);

        mainPanel.add(gamePanel, "game");
        cardLayout.show(mainPanel, "game"); // 切换到游戏面板
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame());
    }
}
