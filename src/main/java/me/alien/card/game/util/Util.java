package me.alien.card.game.util;

public class Util {
    public static boolean equal(Object... obj){
        Object val = obj[0];
        for(Object obj2 : obj){
            if(!obj.equals(val)){
                return false;
            }
        }
        return true;
    }
}
