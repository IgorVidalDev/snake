package spypunk.snake.ui.view;

import static spypunk.snake.ui.constants.SnakeUIConstants.CELL_SIZE;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import spypunk.snake.model.Snake;
import spypunk.snake.ui.cache.ImageCache;
import spypunk.snake.ui.font.cache.FontCache;
import spypunk.snake.ui.util.SwingUtils;
import spypunk.snake.ui.util.SwingUtils.Text;

public class SnakeLivesView extends AbstractSnakeView {

    // Define a área onde o texto  será renderizado.
    private final Rectangle livesRectangle = new Rectangle(0, 0, 8 * CELL_SIZE, 2 * CELL_SIZE);

    public SnakeLivesView(final FontCache fontCache,
                          final ImageCache imageCache,
                          final Snake snake) {
        super(fontCache, imageCache, snake);
        // Inicializa o tamanho do componente
        initializeComponent(livesRectangle.width, livesRectangle.height);
    }

    @Override
    protected void doPaint(final Graphics2D graphics) {
        // Obtém o valor atual de vidas do modelo
        final String livesTextContent = "Vidas: " + snake.getLives();
        
        // Cria o objeto de texto
        final Text livesText = new Text(livesTextContent, fontCache.getScoreFont());
        
        // Desenha o texto centralizado no retângulo definido
        SwingUtils.renderCenteredText(graphics, livesRectangle, livesText);
    }
}