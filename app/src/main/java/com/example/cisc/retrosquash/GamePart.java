package com.example.cisc.retrosquash;

import android.graphics.Point;

/**
 * Created by keith on 11/21/16.
 */

public abstract class GamePart {
    public static Point screenSize;
    public Point position;
    public Point mySize;
    public float velocityX, velocityY;

    public enum Direction {DIRX, DIRY};

    public void setPosition(Point here) {
        position = here;
    }

    public Point getPosition() {
        return position;
    }

    public void impact(Direction dir) {
        if (dir == Direction.DIRX) {
            velocityX = -velocityX;
        }
        if (dir == Direction.DIRY) {
            velocityY = -velocityY;
        }
    } // End of impact()

    // Constructor
    public GamePart(Point size, Point velocity) {
        mySize = size;
        velocityX = velocity.x;
        velocityY = velocity.y;
    }

    public abstract void DoMove();

    public static void setScreenSize(Point theSize) {
        screenSize = theSize;
    }

} // End abstract class GamePart
