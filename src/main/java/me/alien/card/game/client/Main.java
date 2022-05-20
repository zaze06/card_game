package me.alien.card.game.client;

import javax.swing.*;

public class Main extends JFrame{
    public Main(String ip){
        setTitle("card game");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Client client = new Client(ip);

        add(client);
        addKeyListener(client);
        client.addMouseListener(client);
        client.addMouseMotionListener(client);

        setVisible(true);

        client.init();
    }
}
