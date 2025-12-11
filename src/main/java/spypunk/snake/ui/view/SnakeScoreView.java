package spypunk.snake.ui.view;

import static spypunk.snake.ui.constants.SnakeUIConstants.CELL_SIZE;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import spypunk.snake.model.Snake;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.util.SwingUtils.Text;

public class SnakeScoreView extends AbstractSnakeView {

    private final Rectangle scoreRectangle = new Rectangle(0, 0, 13  * CELL_SIZE, 2 * CELL_SIZE);

    public SnakeScoreView(final FontCache fontCache,
                          final ImageCache imageCache,
                          final Snake snake) {
        super(fontCache, imageCache, snake);
        // Inicializa com o tamanho exato necessário
        initializeComponent(scoreRectangle.width, scoreRectangle.height);
    }

    @Override
    protected void doPaint(final Graphics2D graphics) {
        final int currentScore = snake.getScore();
        final Text scoreText = new Text("Pontos: " + currentScore, fontCache.getScoreFont());
        SwingUtils.renderCenteredText(graphics, scoreRectangle, scoreText);
    }
}