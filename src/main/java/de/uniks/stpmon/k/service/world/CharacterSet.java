package de.uniks.stpmon.k.service.world;

import de.uniks.stpmon.k.models.map.TrainerSprite;
import de.uniks.stpmon.k.utils.Direction;

import java.awt.image.BufferedImage;

public class CharacterSet {
    public static final int TRAINER_HEIGHT = 32;
    public static final int TRAINER_WIDTH = 16;
    public static final int IDLE_ROW = 1;
    public static final int MOVE_ROW = 2;
    public static final int SPRITES_PER_COLUMN = 6;
    private final String name;
    private final BufferedImage image;

    public CharacterSet(String name, BufferedImage image) {
        this.name = name;
        this.image = image;
    }

    public int getSpriteX(int index, Direction direction) {
        return direction.ordinal() * TRAINER_WIDTH * SPRITES_PER_COLUMN +
                Math.min(index, SPRITES_PER_COLUMN) * TRAINER_WIDTH;
    }

    public int getSpriteY(boolean isMoving) {
        return (isMoving ? MOVE_ROW : IDLE_ROW) * TRAINER_HEIGHT;
    }

    public TrainerSprite getSprite(int index, Direction direction, boolean isMoving) {
        float x = getSpriteX(index, direction);
        float y = getSpriteY(isMoving);
        return new TrainerSprite(x / image.getWidth(), y / image.getHeight(),
                (x + TRAINER_WIDTH) / image.getWidth(), (y + TRAINER_HEIGHT) / image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

}
