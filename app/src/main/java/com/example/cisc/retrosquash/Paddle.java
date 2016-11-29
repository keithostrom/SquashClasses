package com.example.cisc.retrosquash;

import android.graphics.Point;

/**
 * Created by keith on 11/28/16.
 */

public class Paddle extends GamePart {
    public Paddle(Point size, Point velocity){
        super(size, velocity);
    }

    @Override
    public void DoMove() { // Paddle only moves in X Axis
        int newX;
        newX = (int) (position.x + velocityX);
        velocityX = velocityX * (float) 0.75; // Fun behavior
        if( (newX + (mySize.x/2) < screenSize.x) &&
                (newX - (mySize.x/2) > 0 )   ) { // Within bounds
            position.x = newX;
        }
    }
}
