/*
 * Copyright © 2016-2017 spypunk <spypunk@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package spypunk.snake.service;

import spypunk.snake.service.HighScoreManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Lists;

import spypunk.snake.constants.SnakeConstants;
import spypunk.snake.guice.SnakeModule.SnakeProvider;
import spypunk.snake.model.Direction;
import spypunk.snake.model.Food;
import spypunk.snake.model.Food.Type;
import spypunk.snake.model.Snake;
import spypunk.snake.model.Snake.State;
import spypunk.snake.model.SnakeEvent;
import spypunk.snake.model.SnakeInstance;
import spypunk.snake.service.HighScoreManager;
@Singleton
public class SnakeServiceImpl implements SnakeService {

    private static final int BONUS_FOOD_RANDOM = 10;

    private static final int BONUS_FOOD_FRAME_LIMIT = 120;

    private static final int DEFAULT_SPEED = 3;

    private final Rectangle gridRectangle = new Rectangle(0, 0, SnakeConstants.WIDTH, SnakeConstants.HEIGHT);

    private final List<Point> gridLocations = createGridLocations();

    private final Random random = new Random();

    private static Snake snake;

    private final HighScoreManager highScoreManager;

    @Inject
    public SnakeServiceImpl(@SnakeProvider final Snake snake, final HighScoreManager highScoreManager) {
        this.snake = snake;
	this.highScoreManager = highScoreManager;
    }

    @Override
    public void start() {
        final List<Point> snakePartLocations = Lists.newArrayList();

        final int x = SnakeConstants.WIDTH / 2;

        snakePartLocations.add(new Point(x, 2));
        snakePartLocations.add(new Point(x, 1));
        snakePartLocations.add(new Point(x, 0));

        snake.setSnakeInstance(new SnakeInstance());
        snake.setLives(3);
        snake.setSpeed(DEFAULT_SPEED);
        snake.setDirection(Direction.DOWN);
        snake.getSnakePartLocations().addAll(snakePartLocations);
        snake.setState(State.RUNNING);

        popNextFood();
    }

    @Override
    public void update() {
        if (!isSnakeRunning()) {
            return;
        }

        handleMovement();
        handleBonusFood();
        handleFoodPopped();
    }

    @Override
    public void pause() {
        snake.setState(snake.getState().togglePause());
    }

    @Override
    public void updateDirection(final Direction direction) {
        if (isSnakeRunning()) {
            snake.setNextDirection(direction);
        }
    }

    @Override
    public void mute() {
        snake.setMuted(!snake.isMuted());
    }

    private void handleFoodPopped() {
        if (snake.isFoodPopped()) {
            snake.setFoodPopped(false);
        } else {
            incrementFramesSinceLastFoodPopped();
        }
    }

    private void incrementFramesSinceLastFoodPopped() {
        snake.setFramesSinceLastFoodPopped(snake.getFramesSinceLastFoodPopped() + 1);
    }

    private void popNextFood() {
        final List<Point> possibleFoodLocations = Lists.newArrayList(gridLocations);

        possibleFoodLocations.removeAll(snake.getSnakePartLocations());

        final int foodIndex = random.nextInt(possibleFoodLocations.size());
        final Point foodLocation = possibleFoodLocations.get(foodIndex);
        final Type foodType = random.nextInt(BONUS_FOOD_RANDOM) == 0 ? Type.BONUS : Type.NORMAL;

        snake.setFood(new Food(foodLocation, foodType));
        snake.setFoodPopped(true);
        snake.setFramesSinceLastFoodPopped(0);
    }

    private static List<Point> createGridLocations() {
        final List<Point> gridLocations = Lists.newArrayList();

        IntStream.range(0, SnakeConstants.WIDTH).forEach(
            x -> IntStream.range(0, SnakeConstants.HEIGHT).forEach(y -> gridLocations.add(new Point(x, y))));

        return gridLocations;
    }

    private void handleMovement() {
    if (!isTimeToHandleMovement()) {
        incrementCurrentMovementFrame();
        return;
    }

    handleDirection();

    Point nextLocation = getNextLocation();

    // Lógica de "Wrap-around" (sem bordas)
    if (!gridRectangle.contains(nextLocation)) {
        nextLocation = getWrappedLocation(nextLocation);
    }

    // Lógica de Colisão (com vidas)
    // A única colisão agora é a auto-colisão
    if (snake.getSnakePartLocations().contains(nextLocation)) {
        handleCollision();
    } else {
        // Movimento normal
        moveSnake(nextLocation);
    }

    resetCurrentMovementFrame();
}
    private void incrementCurrentMovementFrame() {
        snake.setCurrentMovementFrame(snake.getCurrentMovementFrame() + 1);
    }

    private void handleBonusFood() {
        final Food food = snake.getFood();
        final Type foodType = food.getType();

        if (Type.BONUS.equals(foodType) && snake.getFramesSinceLastFoodPopped() == BONUS_FOOD_FRAME_LIMIT) {
            popNextFood();
        }
    }

    private void handleDirection() {
        final Optional<Direction> nextDirection = snake.getNextDirection();

        if (!nextDirection.isPresent()) {
            return;
        }

        snake.setDirection(snake.getDirection().apply(nextDirection.get()));
        snake.setNextDirection(null);
    }

    private void moveSnake(final Point nextLocation) {
        final Deque<Point> snakePartLocations = snake.getSnakePartLocations();

        snakePartLocations.addFirst(nextLocation);

        final Food food = snake.getFood();

        if (food.getLocation().equals(nextLocation)) {
            final Type foodType = food.getType();

            updateScore(foodType);
            updateStatistics(foodType);
            if(foodType==Type.BONUS){
                snake.getSnakeEvents().add(SnakeEvent.FOOD_EATEN_BONUS);

            }
            else{snake.getSnakeEvents().add(SnakeEvent.FOOD_EATEN);}


            popNextFood();
        } else {
            snakePartLocations.removeLast();
        }
    }

    private void updateStatistics(final Type foodType) {
        final Map<Type, Integer> statistics = snake.getStatistics();
        final Integer foodTypeCount = statistics.get(foodType);

        statistics.put(foodType, foodTypeCount + 1);
    }

    private void updateScore(final Type foodType) {
        snake.setScore(snake.getScore() + foodType.getPoints());
    }

    private Point getNextLocation() {
        final Deque<Point> snakePartLocations = snake.getSnakePartLocations();
        final Point snakeHeadPartLocation = snakePartLocations.getFirst();
        final Direction direction = snake.getDirection();

        return direction.apply(snakeHeadPartLocation);
    }

    private Point getWrappedLocation(Point location) {
    int x = location.x;
    int y = location.y;

    if (x < 0) {
        x = SnakeConstants.WIDTH - 1;
    } else if (x >= SnakeConstants.WIDTH) {
        x = 0;
    }

    if (y < 0) {
        y = SnakeConstants.HEIGHT - 1;
    } else if (y >= SnakeConstants.HEIGHT) {
        y = 0;
    }

    return new Point(x, y);
}

/**
 * Reseta a posição da cobra após perder uma vida, mantendo a pontuação.
 */
private void resetSnakeAfterLifeLost() {
    final List<Point> snakePartLocations = Lists.newArrayList();

    final int x = SnakeConstants.WIDTH / 2;

    snakePartLocations.add(new Point(x, 2));
    snakePartLocations.add(new Point(x, 1));
    snakePartLocations.add(new Point(x, 0));

    snake.getSnakePartLocations().clear();
    snake.getSnakePartLocations().addAll(snakePartLocations);
    snake.setDirection(Direction.DOWN);
    snake.setNextDirection(null);
    snake.setCurrentMovementFrame(0);

    popNextFood();
}


private void handleCollision() {
    snake.setLives(snake.getLives() - 1);

    if (snake.getLives() > 0) {
        // Ainda tem vidas, reseta a cobra
        resetSnakeAfterLifeLost();
    } else {
        // Sem vidas, fim de jogo
        snake.setState(State.GAME_OVER);
        snake.getSnakeEvents().add(SnakeEvent.GAME_OVER);

        // Salva o high score (lógica que estava em handleMovement)
        // Agora isso vai funcionar porque injetamos o highScoreManager
        if (snake.getScore() > highScoreManager.getHighScore()) {
            highScoreManager.saveHighScore(snake.getScore());
        }
    }
}

    private boolean isTimeToHandleMovement() {
        return snake.getCurrentMovementFrame() == snake.getSpeed();
    }

    public static boolean isSnakeRunning() {
        return State.RUNNING.equals(snake.getState());
    }

    private void resetCurrentMovementFrame() {
        snake.setCurrentMovementFrame(0);
    }
    public static State getState(){
        return snake.getState();
    }
}