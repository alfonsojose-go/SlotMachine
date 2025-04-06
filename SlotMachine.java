import javax.swing.*;
import java.awt.*;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class SlotMachine extends JFrame {
    private JLabel[][] reels;
    private JPanel balancePanel;
    private JLabel betLabel;
    private JButton increaseBetButton;
    private JButton decreaseBetButton;
    private int balance = 300;
    private int currentBet = 10;
    private final String[] symbols = {"🐚", "🐋", "🐟", "🐬", "🐢", "🐙", "🦀", "🌊", "💎", "⭐"};
    private final Random random = new Random();
    private ImageIcon[] numberImages;
    private ImageIcon backgroundImage;
    private JButton coinSlotButton;
    private JLabel messageLabel;  // Add message label field

    // Custom styled button for bet controls
    private class StyledButton extends JButton {
        public StyledButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Dialog", Font.BOLD, 28));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw metallic background
            GradientPaint gradient;
            if (getModel().isPressed()) {
                gradient = new GradientPaint(
                    0, 0, new Color(60, 60, 60),
                    0, getHeight(), new Color(40, 40, 40)
                );
            } else if (getModel().isRollover()) {
                gradient = new GradientPaint(
                    0, 0, new Color(100, 180, 255),
                    0, getHeight(), new Color(0, 100, 200)
                );
            } else {
                gradient = new GradientPaint(
                    0, 0, new Color(100, 100, 100),
                    0, getHeight(), new Color(60, 60, 60)
                );
            }
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            // Add shine effect
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()/2-2, 15, 15);

            // Draw border
            g2d.setColor(new Color(30, 30, 30));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);

            // Draw text with shadow
            String text = getText();
            FontMetrics fm = g2d.getFontMetrics();
            
            // Draw text shadow
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(text, (getWidth() - fm.stringWidth(text)) / 2 + 1, (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + 1);
            
            // Draw text
            g2d.setColor(getForeground());
            g2d.setFont(getFont());
            g2d.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);

            g2d.dispose();
        }
    }

    // Custom coin slot button class
    private class CoinSlotButton extends JButton {
        private final Color goldLight = new Color(255, 215, 0);    // Bright gold
        private final Color goldDark = new Color(184, 134, 11);    // Darker gold
        private final Color goldShadow = new Color(139, 69, 19);   // Dark brown

        public CoinSlotButton() {
            super("INSERT COIN");
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setPreferredSize(new Dimension(200, 80));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw metallic gold background
            GradientPaint gradient;
            if (getModel().isPressed()) {
                gradient = new GradientPaint(
                    0, 0, goldDark,
                    getWidth(), getHeight(), goldShadow
                );
            } else if (getModel().isRollover()) {
                gradient = new GradientPaint(
                    0, 0, goldLight,
                    getWidth(), getHeight(), goldDark
                );
            } else {
                gradient = new GradientPaint(
                    0, 0, goldLight.brighter(),
                    getWidth(), getHeight(), goldDark
                );
            }
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            // Add metallic shine effect
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() / 2 - 5, 20, 20);

            // Draw coin slot
            g2d.setColor(goldShadow);
            int slotWidth = 80;
            int slotHeight = 10;
            int x = (getWidth() - slotWidth) / 2;
            int y = (getHeight() - slotHeight) / 2;
            g2d.fillRoundRect(x, y, slotWidth, slotHeight, 8, 8);

            // Add "INSERT COIN" text with gold gradient and shadow
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics fm = g2d.getFontMetrics();
            
            // Draw text shadow
            g2d.setColor(goldShadow);
            String text = "INSERT COIN";
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            g2d.drawString(text, textX + 1, y - 15 + 1);

            // Draw text with gold gradient
            GradientPaint textGradient = new GradientPaint(
                textX, y - 25, goldLight,
                textX, y - 15, goldDark
            );
            g2d.setPaint(textGradient);
            g2d.drawString(text, textX, y - 15);

            g2d.dispose();
        }
    }

    // Custom title label with glow effect
    private class GlowingTitle extends JLabel {
        private final Color glowColor = new Color(0, 200, 255);  // Aqua blue glow
        private final int glowSize = 4;

        public GlowingTitle(String text) {
            super(text, SwingConstants.CENTER);
            setForeground(Color.WHITE);
            setFont(new Font("Serif", Font.BOLD, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Get the shape of the text
            FontMetrics fm = g2d.getFontMetrics(getFont());
            String text = getText();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            // Draw outer glow
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 50));
            for (int i = glowSize; i > 0; i--) {
                g2d.drawString(text, textX - i, textY);
                g2d.drawString(text, textX + i, textY);
                g2d.drawString(text, textX, textY - i);
                g2d.drawString(text, textX, textY + i);
            }

            // Draw inner glow
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 100));
            for (int i = glowSize/2; i > 0; i--) {
                g2d.drawString(text, textX - i, textY);
                g2d.drawString(text, textX + i, textY);
                g2d.drawString(text, textX, textY - i);
                g2d.drawString(text, textX, textY + i);
            }

            // Draw text shadow
            g2d.setColor(new Color(0, 0, 0, 128));
            g2d.drawString(text, textX + 2, textY + 2);

            // Draw main text
            g2d.setColor(getForeground());
            g2d.drawString(text, textX, textY);

            g2d.dispose();
        }
    }

    // Custom symbol cell with fancy styling and animation
    private class SymbolCell extends JLabel {
        private float yOffset = 0;  // For spin animation
        private boolean isSpinning = false;
        private float spinSpeed = 0;
        private boolean isWinningSymbol = false;
        private Timer blinkTimer = null;
        private float blinkPhase = 0;  // For smooth blinking

        public SymbolCell(String text) {
            super(text, SwingConstants.CENTER);
            setOpaque(false);
            setFont(new Font("Dialog", Font.PLAIN, 60));
            setPreferredSize(new Dimension(130, 130));
            setForeground(new Color(50, 50, 50));  // Dark gray for better emoji colors
        }

        public void startSpinning(float speed) {
            stopBlinking();  // Stop any existing blink animation
            isSpinning = true;
            spinSpeed = speed;
            yOffset = 0;
        }

        public void stopSpinning(String finalSymbol) {
            isSpinning = false;
            setText(finalSymbol);
        }

        public void startBlinking() {
            isWinningSymbol = true;
            if (blinkTimer != null) {
                blinkTimer.stop();
            }
            blinkPhase = 0;
            blinkTimer = new Timer(16, _ -> {  // 60 FPS for smooth animation
                blinkPhase = (blinkPhase + 0.05f) % ((float)(2 * Math.PI));  // Smooth cycle
                repaint();
            });
            blinkTimer.start();
        }

        public void stopBlinking() {
            isWinningSymbol = false;
            if (blinkTimer != null) {
                blinkTimer.stop();
                blinkTimer = null;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Calculate background colors based on winning state
            Color bgColor1, bgColor2, borderColor1, borderColor2;
            if (isWinningSymbol && blinkTimer != null) {
                float intensity = (float) (Math.sin(blinkPhase) + 1) / 2;  // Range 0 to 1
                
                // Interpolate between normal and gold colors
                bgColor1 = interpolateColor(new Color(240, 240, 240), new Color(255, 223, 0), intensity);
                bgColor2 = interpolateColor(new Color(200, 200, 200), new Color(218, 165, 32), intensity);
                borderColor1 = interpolateColor(new Color(200, 200, 200), new Color(255, 215, 0), intensity);
                borderColor2 = interpolateColor(new Color(100, 100, 100), new Color(184, 134, 11), intensity);
            } else {
                bgColor1 = new Color(240, 240, 240);
                bgColor2 = new Color(200, 200, 200);
                borderColor1 = new Color(200, 200, 200);
                borderColor2 = new Color(100, 100, 100);
            }
            
            // Create metal-like background with current colors
            GradientPaint bgGradient = new GradientPaint(
                0, 0, bgColor1,
                getWidth(), getHeight(), bgColor2
            );
            g2d.setPaint(bgGradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            // Add metallic effect
            g2d.setPaint(new GradientPaint(
                0, 0, new Color(255, 255, 255, 100),
                0, getHeight(), new Color(0, 0, 0, 30)
            ));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            // Draw chrome-like border with current colors
            GradientPaint borderGradient = new GradientPaint(
                0, 0, borderColor1,
                0, getHeight(), borderColor2
            );
            g2d.setPaint(borderGradient);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);

            if (isSpinning) {
                // Spinning animation code remains unchanged
                yOffset = (yOffset + spinSpeed) % getHeight();
                String currentText = getText();
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.setColor(getForeground());
                g2d.drawString(currentText, 
                    (getWidth() - fm.stringWidth(currentText)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - getHeight() + yOffset);
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.drawString(currentText,
                    (getWidth() - fm.stringWidth(currentText)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + yOffset);
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.drawString(currentText,
                    (getWidth() - fm.stringWidth(currentText)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + getHeight() + yOffset);
                
                repaint();
            } else {
                // Draw static symbol
                String text = getText();
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                // Draw text with shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawString(text, textX + 1, textY + 1);
                g2d.setColor(getForeground());
                g2d.drawString(text, textX, textY);
            }

            g2d.dispose();
        }

        private Color interpolateColor(Color c1, Color c2, float ratio) {
            int r = (int)(c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
            int g = (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
            int b = (int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
            return new Color(r, g, b);
        }
    }

    public SlotMachine() {
        setTitle("Secret of the Mermaid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setMinimumSize(new Dimension(1200, 850));  

        // Load background image
        try {
            BufferedImage background = ImageIO.read(new File("slotMachineImages/scales.jpg"));
            backgroundImage = new ImageIcon(background.getScaledInstance(1200, 850, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create main panel with background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Scale the background image to fit the panel
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 200, 30, 200));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setOpaque(true);

        // Create title label with centered text and glow effect
        JLabel titleLabel = new GlowingTitle("Secret of the Mermaid");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setPreferredSize(new Dimension(500, 70));  
        titleLabel.setMaximumSize(new Dimension(500, 70));  
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Create reels panel with fixed size and rounded corners
        JPanel reelsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create rich magenta gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 0, 255).darker(),
                    0, getHeight(), new Color(128, 0, 128)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Add sparkle effect
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight()/2, 25, 25);
                
                g2d.dispose();
            }
        };
        reelsPanel.setLayout(new GridLayout(3, 3, 20, 20));  
        reelsPanel.setPreferredSize(new Dimension(500, 400));
        reelsPanel.setMaximumSize(new Dimension(500, 400));
        reelsPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        reelsPanel.setOpaque(false);

        // Create a container for the reels with shadow effect
        JPanel reelsContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Darker shadow for better depth
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth() - 8, getHeight() - 8, 20, 20);
                
                g2d.dispose();
            }
        };
        reelsContainer.setPreferredSize(new Dimension(550, 450));  
        reelsContainer.setMaximumSize(new Dimension(550, 450));
        reelsContainer.setOpaque(false);
        reelsContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  
        reelsContainer.add(reelsPanel, BorderLayout.CENTER);

        reels = new JLabel[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                reels[row][col] = new SymbolCell(symbols[0]);
                reelsPanel.add(reels[row][col]);
            }
        }

        // Create controls panel with fixed width
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setMaximumSize(new Dimension(500, 300));  
        controlsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlsPanel.setOpaque(false);  

        // Create separate panels for bet controls and spin button
        JPanel betPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        betPanel.setPreferredSize(new Dimension(500, 70));  
        betPanel.setMaximumSize(new Dimension(500, 70));  
        betPanel.setOpaque(false);  
        
        // Bet controls with consistent sizing
        decreaseBetButton = new StyledButton("-");
        decreaseBetButton.setPreferredSize(new Dimension(70, 50));
        decreaseBetButton.setForeground(new Color(255, 215, 0));    

        betLabel = new JLabel("BET: $" + currentBet);
        betLabel.setPreferredSize(new Dimension(200, 50));  // Wider for numbers
        betLabel.setFont(new Font("Arial Black", Font.BOLD, 28));  // Bolder font
        betLabel.setHorizontalAlignment(SwingConstants.CENTER);
        betLabel.setForeground(new Color(255, 223, 0));  // Brighter gold

        increaseBetButton = new StyledButton("+");
        increaseBetButton.setPreferredSize(new Dimension(70, 50));
        increaseBetButton.setForeground(new Color(255, 215, 0));    

        // Add bet controls to bet panel
        betPanel.add(decreaseBetButton);
        betPanel.add(betLabel);
        betPanel.add(increaseBetButton);

        // Load number images
        numberImages = new ImageIcon[10];
        try {
            for (int i = 0; i < 10; i++) {
                BufferedImage img = ImageIO.read(new File("slotMachineImages/num" + i + ".png"));
                // Scale the image to a reasonable size
                Image scaledImg = img.getScaledInstance(30, 40, Image.SCALE_SMOOTH);
                numberImages[i] = new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading number images!");
            System.exit(1);
        }

        // Create balance panel
        balancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        balancePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balancePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));  // Reduced bottom padding
        balancePanel.setOpaque(false);
        
        // Create message label
        messageLabel = new JLabel(" ");  // Start with empty space
        messageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        updateBalance(); 

        // Create coin slot button panel with label
        JPanel coinSlotPanel = new JPanel();
        coinSlotPanel.setLayout(new BoxLayout(coinSlotPanel, BoxLayout.Y_AXIS));
        coinSlotPanel.setOpaque(false);
        coinSlotPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create coin slot button
        coinSlotButton = new CoinSlotButton();
        coinSlotButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create "Insert Here" label
        JLabel insertHereLabel = new JLabel("INSERT HERE");
        insertHereLabel.setFont(new Font("Arial Black", Font.BOLD, 18));
        insertHereLabel.setForeground(new Color(255, 223, 0));  // Match gold color
        insertHereLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components to coin slot panel
        coinSlotPanel.add(coinSlotButton);
        coinSlotPanel.add(Box.createVerticalStrut(5));  // Small gap
        coinSlotPanel.add(insertHereLabel);

        // Add components to control panel with more spacing
        controlsPanel.add(betPanel);
        controlsPanel.add(Box.createVerticalStrut(30));
        controlsPanel.add(coinSlotPanel);  // Add panel instead of just button

        // Add all components to main panel with more spacing
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(balancePanel);
        mainPanel.add(messageLabel);  // Add message label after balance
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(reelsContainer);  
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(controlsPanel);

        // Add action listeners
        coinSlotButton.addActionListener(_ -> spin());
        increaseBetButton.addActionListener(_ -> changeBet(10));
        decreaseBetButton.addActionListener(_ -> changeBet(-10));

        // Add main panel to frame
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
        // Auto-clear message after 3 seconds
        Timer timer = new Timer(3000, _ -> {
            messageLabel.setText(" ");
            messageLabel.setForeground(Color.WHITE);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void spin() {
        if (currentBet > balance) {
            showMessage("Insufficient funds!", Color.RED);
            return;
        }

        // Reset winning highlights
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ((SymbolCell)reels[i][j]).stopBlinking();
            }
        }

        balance -= currentBet;
        updateBalance();

        // Disable spin button during animation
        coinSlotButton.setEnabled(false);

        // Create timers for rolling animation
        Timer[] columnTimers = new Timer[3];
        final String[][] finalSymbols = new String[3][3];
        
        // Pre-determine final symbols
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 3; row++) {
                finalSymbols[row][col] = symbols[random.nextInt(symbols.length)];
            }
        }

        // Create rolling animation for each column with delays
        for (int i = 0; i < 3; i++) {
            final int col = i;
            AtomicInteger ticks = new AtomicInteger(0);
            final float baseSpeed = 30f;  // Base spinning speed
            
            columnTimers[col] = new Timer(16, _ -> {  // ~60 FPS
                // Calculate current speed (starts fast, slows down)
                float progress = Math.min(1.0f, ticks.get() / 60.0f);
                float currentSpeed = baseSpeed * (1.0f - progress * progress);
                
                // Update symbols in current column
                for (int row = 0; row < 3; row++) {
                    SymbolCell cell = (SymbolCell)reels[row][col];
                    if (ticks.get() < 40 + (col * 20)) {
                        // Still spinning
                        cell.startSpinning(currentSpeed);
                        cell.setText(symbols[random.nextInt(symbols.length)]);
                    } else {
                        // Stop on final symbol
                        cell.stopSpinning(finalSymbols[row][col]);
                    }
                }

                // Stop current column after its duration
                if (ticks.incrementAndGet() >= 50 + (col * 20)) {
                    columnTimers[col].stop();
                    
                    // If this is the last column, check for wins
                    if (col == 2) {
                        coinSlotButton.setEnabled(true);
                        highlightWinningLines();
                    }
                }
            });
            
            // Start each column timer with a delay
            Timer startDelay = new Timer(col * 400, _ -> columnTimers[col].start());
            startDelay.setRepeats(false);
            startDelay.start();
        }
    }

    private void highlightWinningLines() {
        int winAmount = 0;
        boolean[][] winningCells = new boolean[3][3];

        // Stop any existing blink animations
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                ((SymbolCell)reels[row][col]).stopBlinking();
            }
        }

        // Check rows
        for (int row = 0; row < 3; row++) {
            int rowWin = checkLine(
                reels[row][0].getText(),
                reels[row][1].getText(),
                reels[row][2].getText()
            );
            if (rowWin > 0) {
                winAmount += rowWin;
                winningCells[row][0] = true;
                winningCells[row][1] = true;
                winningCells[row][2] = true;
            }
        }

        // Check diagonals
        int mainDiagWin = checkLine(
            reels[0][0].getText(),
            reels[1][1].getText(),
            reels[2][2].getText()
        );
        if (mainDiagWin > 0) {
            winAmount += mainDiagWin;
            winningCells[0][0] = true;
            winningCells[1][1] = true;
            winningCells[2][2] = true;
        }

        int otherDiagWin = checkLine(
            reels[0][2].getText(),
            reels[1][1].getText(),
            reels[2][0].getText()
        );
        if (otherDiagWin > 0) {
            winAmount += otherDiagWin;
            winningCells[0][2] = true;
            winningCells[1][1] = true;
            winningCells[2][0] = true;
        }

        // Start blinking animation for winning cells
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (winningCells[row][col]) {
                    ((SymbolCell)reels[row][col]).startBlinking();
                }
            }
        }

        // Update balance and show message
        if (winAmount > 0) {
            balance += winAmount;
            updateBalance();
            showMessage("You won $" + winAmount + "!", new Color(255, 223, 0));
        } else {
            showMessage("Try again!", Color.WHITE);
        }
    }

    private int checkLine(String symbol1, String symbol2, String symbol3) {
        if (symbol1.equals(symbol2) && symbol2.equals(symbol3)) {
            // All three match
            if (symbol1.equals("💎")) {
                return currentBet * 8;  // Diamond jackpot
            } else if (symbol1.equals("🐢")) {
                return currentBet * 7;   // Sea turtle special
            } else if (symbol1.equals("⭐")) {
                return currentBet * 5;   // Triple stars
            } else if (symbol1.equals("🌊")) {
                return currentBet * 4;   // Triple waves
            } else {
                return currentBet * 3;   // Any other three of a kind
            }
        } else if (symbol1.equals(symbol2) || symbol2.equals(symbol3) || symbol1.equals(symbol3)) {
            // Two matching symbols - only pay if they're special symbols
            if (symbol1.equals("💎") || symbol2.equals("💎") || symbol3.equals("💎") ||
                symbol1.equals("🐢") || symbol2.equals("🐢") || symbol3.equals("🐢") ||
                symbol1.equals("⭐") || symbol2.equals("⭐") || symbol3.equals("⭐") ||
                symbol1.equals("🌊") || symbol2.equals("🌊") || symbol3.equals("🌊")) {
                return currentBet;   // Special symbols match
            }
        }
        return 0;
    }

    private void changeBet(int amount) {
        int newBet = currentBet + amount;
        if (newBet >= 10 && newBet <= 100) {
            currentBet = newBet;
            betLabel.setText("BET: $" + currentBet);
        }
    }

    private void updateBalance() {
        balancePanel.removeAll();
        
        // Add "BALANCE: $" label with enhanced styling
        JLabel balanceText = new JLabel("BALANCE: $");
        balanceText.setFont(new Font("Arial Black", Font.BOLD, 32));  // Larger, bolder font
        balanceText.setForeground(new Color(255, 223, 0));  // Brighter gold
        balancePanel.add(balanceText);

        // Convert balance to string and add each digit
        String balanceStr = String.valueOf(balance);
        for (int i = 0; i < balanceStr.length(); i++) {
            int digit = Character.getNumericValue(balanceStr.charAt(i));
            JLabel digitLabel = new JLabel(numberImages[digit]);
            balancePanel.add(digitLabel);
        }

        balancePanel.revalidate();
        balancePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SlotMachine game = new SlotMachine();
            game.setVisible(true);
        });
    }
}
