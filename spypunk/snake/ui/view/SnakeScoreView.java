/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.ui.view;

import static spypunk.snake.ui.constants.SnakeUIConstants.CELL_SIZE;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.Timer;

import spypunk.snake.service.HighScoreManager;
import spypunk.snake.model.Snake;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.util.SwingUtils.Text;

public class SnakeScoreView extends AbstractSnakeView {

    private final Rectangle scoreRectangle = new Rectangle(50, 0, 12 * CELL_SIZE, 2 * CELL_SIZE);
    private final Rectangle highScoreRectangle = new Rectangle(300, 0, 15 * CELL_SIZE, 2 * CELL_SIZE);

    private final HighScoreManager highScoreManager;

    private int displayedHighScore;
    private boolean showRecord = true;
    private boolean recordBlinking = false;

    public SnakeScoreView(final FontCache fontCache,
                          final ImageCache imageCache,
                          final Snake snake,
                          final HighScoreManager highScoreManager) {
        super(fontCache, imageCache, snake);
        this.highScoreManager = highScoreManager;
        this.displayedHighScore = highScoreManager.getHighScore();
        initializeComponent(scoreRectangle.width + highScoreRectangle.width,
                            Math.max(scoreRectangle.height, highScoreRectangle.height));
    }

    @Override
    protected void doPaint(final Graphics2D graphics) {
        final int currentScore = snake.getScore();

        final Text scoreText = new Text("Pontos: " + currentScore, fontCache.getScoreFont());
        SwingUtils.renderCenteredText(graphics, scoreRectangle, scoreText);

        // Atualiza displayedHighScore se score atual ultrapassou o record antigo
        if (currentScore > displayedHighScore) {
            displayedHighScore = currentScore;

            // efeito de piscar por 3 segundos
            if (!recordBlinking) {
                recordBlinking = true;

                Timer blinkTimer = new Timer(300, e -> showRecord = !showRecord);
                blinkTimer.setRepeats(true);
                blinkTimer.start();

                // parar de piscar após 3 segundos
                new Timer(3000, e -> {
                    blinkTimer.stop();
                    showRecord = true;
                    recordBlinking = false;
                    ((Timer) e.getSource()).stop();
                }).start();
            }
        }

        // Record (pisca quando bate novo)
        if (showRecord) {
            final Text highScoreText = new Text("Recorde: " + displayedHighScore, fontCache.getScoreFont());
            SwingUtils.renderCenteredText(graphics, highScoreRectangle, highScoreText);
        }
    }
}
