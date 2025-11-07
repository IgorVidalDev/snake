package spypunk.snake.model;

import java.awt.Point;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import spypunk.snake.model.Food.Type;

public class Snake {

    private final String name;
    private final String version;
    private final URI projectURI;

    private SnakeInstance snakeInstance;

    private static final boolean SOUND_MUTED = true;
    private static final boolean SOUND_ON = false;

    private boolean muted;

    public enum State {
        RUNNING {
            @Override
            public State togglePause() {
                return PAUSED;
            }
        },
        PAUSED {
            @Override
            public State togglePause() {
                return RUNNING;
            }
        },
        GAME_OVER,
        STOPPED;

        public State togglePause() {
            return this;
        }
    }

    public Snake(final String name, final String version, final URI projectURI) {
        this.name = name;
        this.version = version;
        this.projectURI = projectURI;
        this.snakeInstance = new SnakeInstance();
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public URI getProjectURI() {
        return projectURI;
    }

    public void setSnakeInstance(final SnakeInstance snakeInstance) {
        this.snakeInstance = snakeInstance;
    }

    public void muteSound() {
        this.muted = SOUND_MUTED;
    }

    public void unmuteSound() {
        this.muted = SOUND_ON;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(final boolean muted) {
        this.muted = muted;
    }

    public List<SnakeEvent> getSnakeEvents() {
        return instance().getSnakeEvents();
    }

    private SnakeInstance instance() {
        return snakeInstance;
    }

    public int getScore() {
        return instance().getScore();
    }

    public void setScore(final int score) {
        instance().setScore(score);
    }

    public int getSpeed() {
        return instance().getSpeed();
    }

    public void setSpeed(final int speed) {
        instance().setSpeed(speed);
    }

    public int getCurrentMovementFrame() {
        return instance().getCurrentMovementFrame();
    }

    public void setCurrentMovementFrame(final int currentMoveFrame) {
        instance().setCurrentMovementFrame(currentMoveFrame);
    }

    public LinkedList<Point> getSnakePartLocations() {
        return instance().getSnakePartLocations();
    }

    public Direction getDirection() {
        return instance().getDirection();
    }

    public void setDirection(final Direction direction) {
        instance().setDirection(direction);
    }

    public Optional<Direction> getNextDirection() {
        return instance().getNextDirection();
    }

    public void setNextDirection(final Direction direction) {
        instance().setNextDirection(direction);
    }

    public int getFramesSinceLastFoodPopped() {
        return instance().getFramesSinceLastFoodPopped();
    }

    public void setFramesSinceLastFoodPopped(final int framesSinceLastFoodPopped) {
        instance().setFramesSinceLastFoodPopped(framesSinceLastFoodPopped);
    }

    public Food getFood() {
        return instance().getFood();
    }

    public void setFood(final Food food) {
        instance().setFood(food);
    }

    public Map<Type, Integer> getStatistics() {
        return instance().getStatistics();
    }

    public boolean isFoodPopped() {
        return instance().isFoodPopped();
    }

    public void setFoodPopped(final boolean foodPopped) {
        instance().setFoodPopped(foodPopped);
    }

    public State getState() {
        return instance().getState();
    }

    public void setState(final State state) {
        instance().setState(state);
    }


    
    public int getLives() {
    return instance().getLives();
}

    public void setLives(final int lives) {
    instance().setLives(lives);
}
}
