package de.uniks.stpmon.k.world;

import de.uniks.stpmon.k.utils.Direction;

import java.awt.image.BufferedImage;

public record CharacterSet(String name, BufferedImage image) {

    public static final int TRAINER_HEIGHT = 32;
    public static final int TRAINER_WIDTH = 16;

    public static final int PREVIEW_ROW = 0;
    public static final int IDLE_ROW = 1;
    public static final int MOVE_ROW = 2;
    public static final int SPRITES_PER_COLUMN = 6;

    public BufferedImage getPreview(Direction direction) {
        return image.getSubimage(direction.ordinal() * TRAINER_WIDTH,
                PREVIEW_ROW * TRAINER_HEIGHT,
                TRAINER_WIDTH, TRAINER_HEIGHT);
    }

    public int getSpriteX(int index, Direction direction) {
        return direction.ordinal() * TRAINER_WIDTH * SPRITES_PER_COLUMN +
                Math.min(index, SPRITES_PER_COLUMN) * TRAINER_WIDTH;
    }

    public int getSpriteY(boolean isMoving) {
        return (isMoving ? MOVE_ROW : IDLE_ROW) * TRAINER_HEIGHT;
    }

    public void fillSpriteData(float[] data, int index, Direction direction, boolean isMoving) {
        float x = getSpriteX(index, direction);
        float y = getSpriteY(isMoving);
        data[0] = x / image.getWidth();
        data[1] = y / image.getHeight();
        data[2] = (x + TRAINER_WIDTH) / image.getWidth();
        data[3] = (y + TRAINER_HEIGHT) / image.getHeight();
    }

}
