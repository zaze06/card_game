package me.alien.card.game.util;

public class DataPacket {
    Object data;
    Type type;

    public DataPacket(Object data, Type type) {
        this.data = data;
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public <T> T getData(Class<T> clazz){
        return clazz.cast(data);
    }

    public Type getType() {
        return type;
    }
}
