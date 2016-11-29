package com.example.cisc.retrosquash;

import android.graphics.Point;

/**
 * Created by keith on 11/28/16.
 */

public class Ball extends GamePart {
    public Ball(Point size, Point velocity){
        super(size, velocity);
    }

    public void DoMove() {
        int newX;
        int newY;

        newX = (int) (position.x + velocityX);
        // if out of bounds
        if( (newX - (mySize.x/2) < 0 ) ||
                (newX + (mySize.x/2) > screenSize.x) ) {
            velocityX *= -1; // Flip velocity for bounce
            newX = (int) (position.x + velocityX); // Guaranteed to be in bounds
        }
        position.x = newX;

        newY = (int) (position.y + velocityY);
        // if out of bounds
        if( (newY - (mySize.y/2) < 0 ) ||
                (newY + (mySize.y/2) > screenSize.y) ) {
            velocityY *= -1; // Flip velocity for bounce
            newY = (int) (position.y + velocityY); // Guaranteed to be in bounds
        }
        position.y = newY;
    }
} // End class Ball
