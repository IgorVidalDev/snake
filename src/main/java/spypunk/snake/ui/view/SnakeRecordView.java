 package spypunk.snake.ui.view; 

import static spypunk.snake.ui.constants.SnakeUIConstants.CELL_SIZE;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.Timer;

import spypunk.snake.model.Snake;
import spypunk.snake.service.HighScoreManager;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.util.SwingUtils.Text;

public class SnakeRecordView extends AbstractSnakeView {

    private final Rectangle highScoreRectangle = new Rectangle(0, 0, 15 * CELL_SIZE, 2 * CELL_SIZE);

    private final HighScoreManager highScoreManager;
    private int displayedHighScore;
    private boolean showRecord = true;
    private boolean recordBlinking = false;

    public SnakeRecordView(final FontCache fontCache,
                           final ImageCache imageCache,
                           final Snake snake,
                           final HighScoreManager highScoreManager) {
        super(fontCache, imageCache, snake);
        this.highScoreManager = highScoreManager;
        this.displayedHighScore = highScoreManager.getHighScore();
        initializeComponent(highScoreRectangle.width, highScoreRectangle.height);
    }

    @Override
    protected void doPaint(final Graphics2D graphics) {
        final int currentScore = snake.getScore();

        if (currentScore > displayedHighScore) {
            displayedHighScore = currentScore;

            if (!recordBlinking) {
                recordBlinking = true;
                Timer blinkTimer = new Timer(300, e -> showRecord = !showRecord);
                blinkTimer.setRepeats(true);
                blinkTimer.start();

                new Timer(3000, e -> {
                    blinkTimer.stop();
                    showRecord = true;
                    recordBlinking = false;
                    ((Timer) e.getSource()).stop();
                }).start();
            }
        }

        if (showRecord) {
            final Text highScoreText = new Text("Recorde: " + displayedHighScore, fontCache.getScoreFont());
            SwingUtils.renderCenteredText(graphics, highScoreRectangle, highScoreText);
        }
    }
}
    