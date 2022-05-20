package me.alien.card.game.util;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Box2I {
    int startX;
    int startY;
    int endX;
    int endY;

    public Box2I(int x, int y, int width, int height) {
        this.startX = x;
        this.startY = y;
        this.endX = x+width;
        this.endY = y+height;
    }

    public Box2I(int x, int y, int size){
        this(x,y,x+size,y+size);
    }

    public boolean contains(int x, int y){
        return (x > startX && x < endX) && (y > startY && y < endY);
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    @Override
    public String toString() {
        return "Box2I{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                '}';
    }

    public boolean contains(Point mousePos) {
        return contains(mousePos.x, mousePos.y);
    }

    public int getWidth(){
        return endX-startX;
    }

    public int getHeight(){
        return endY-startY;
    }
}
