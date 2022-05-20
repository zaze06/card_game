package me.alien.card.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.alien.card.game.server.Server;
import me.alien.card.game.util.Version;

import javax.swing.*;

public class Main extends JFrame {

    public static final Version VERSION = new Version("1-BETA");
    public static final Gson GSON = new GsonBuilder().create();

    public static void main(String[] args) {
        if(args[0].equalsIgnoreCase("-server")){
            new Server();
        }
        else{
            new me.alien.card.game.client.Main(args[0]);
        }
    }
}
