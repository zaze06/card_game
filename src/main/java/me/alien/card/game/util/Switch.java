package me.alien.card.game.util;

public class Switch {
    boolean value;

    public boolean read(){
        boolean out = value;
        value = false;
        return out;
    }

    public void set(boolean value){
        this.value = value;
    }

    public void flip(){
        value = !value;
    }
}
