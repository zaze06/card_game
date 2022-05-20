package me.alien.card.game.server;

import me.alien.card.game.util.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static me.alien.card.game.Main.GSON;
import static me.alien.card.game.Main.VERSION;

public class Server implements ActionListener {
    ServerSocket socket;

    ArrayList<Client> clients = new ArrayList<>();
    AcceptClientThread acceptClientThread = new AcceptClientThread();
    Timer gameLoop;

    ArrayList<Card> playedCards = new ArrayList<>();
    ArrayList<Card> player1Hand = new ArrayList<>();
    ArrayList<Card> player2Hand = new ArrayList<>();
    Card[] player1Hidden = new Card[3];
    Card[] player2Hidden = new Card[3];
    Card[] player1Shown = new Card[3];
    Card[] player2Shown = new Card[3];


    public Server(){
        try {
            socket = new ServerSocket(4067);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        acceptClientThread.start();

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(clients.size() >= 2){

            if(playedCards.size() > 0) {

                if (playedCards.get(playedCards.size() - 1).getValueChar().equalsIgnoreCase("10")) {
                    for (Card card : playedCards) {
                        System.out.print(card.card() + ", ");
                    }
                    System.out.print("\n");
                    playedCards = new ArrayList<>();
                } else if (playedCards.get(playedCards.size() - 1).getValueChar().equalsIgnoreCase("A") &&
                        playedCards.get(playedCards.size() - 2).getValueChar().equalsIgnoreCase("A")) {
                    for (Card card : playedCards) {
                        System.out.print(card.card() + ", ");
                    }
                    System.out.print("\n");
                    playedCards = new ArrayList<>();
                } else if (Util.equal(
                        playedCards.get(playedCards.size() - 1).getValueChar(),
                        playedCards.get(playedCards.size() - 2).getValueChar(),
                        playedCards.get(playedCards.size() - 3).getValueChar(),
                        playedCards.get(playedCards.size() - 4).getValueChar())) {
                    for (Card card : playedCards) {
                        System.out.print(card.card() + ", ");
                    }
                    System.out.print("\n");
                    playedCards = new ArrayList<>();
                }
            }
        }
    }

    private class AcceptClientThread extends Thread{
        @Override
        public void run() {
            while (true){
                try {
                    Socket tmpSocket = socket.accept();
                    System.out.print("new client, stating handshake");
                    BufferedReader br = new BufferedReader(new InputStreamReader(tmpSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(tmpSocket.getOutputStream(), true);

                    out.println("start");

                    String data = br.readLine();
                    if(data == null){
                        out.println(GSON.toJson(new DataPacket("NullPointerException", Type.ERROR)));
                        socket.close();
                        continue;
                    }
                    DataPacket dataPacket = GSON.fromJson(data, DataPacket.class);
                    if(dataPacket.getType() != Type.VERSION) {
                        continue;
                    }
                    if (!VERSION.equals(new Version(dataPacket.getData(String.class)))) {
                        out.println(GSON.toJson(new DataPacket("Incompatible version, server version: " + VERSION, Type.ERROR)));
                        continue;
                    }
                    clients.add(new Client(tmpSocket));

                    if(clients.size() >= 2){
                        Client client1 = clients.get(0);
                        Client client2 = clients.get(1);
                        client1.send(GSON.toJson(new DataPacket("new game", Type.NEW_GAME)));
                        client2.send(GSON.toJson(new DataPacket("new game", Type.NEW_GAME)));

                        ArrayList<Card> deck = Deck.shuffel();
                        for(int i = 0; i < 3; i++){
                            player1Hidden[i] = deck.get(deck.size()-1);
                            deck.remove(deck.size()-1);
                            player2Hidden[i] = deck.get(deck.size()-1);
                            deck.remove(deck.size()-1);
                        }
                        for(int i = 0; i < 3; i++){
                            player1Shown[i] = deck.get(deck.size()-1);
                            deck.remove(deck.size()-1);
                            player2Shown[i] = deck.get(deck.size()-1);
                            deck.remove(deck.size()-1);
                        }
                        for(int i = 0; i < 3; i++){
                            player1Hand.add(deck.get(deck.size()-1));
                            deck.remove(deck.size()-1);
                            player2Hand.add(deck.get(deck.size()-1));
                            deck.remove(deck.size()-1);
                        }

                        client1.send(GSON.toJson(new DataPacket(player1Hidden, Type.CARD_ARRAY_HIDDEN)));
                        client2.send(GSON.toJson(new DataPacket(player2Hidden, Type.CARD_ARRAY_HIDDEN)));
                        client1.send(GSON.toJson(new DataPacket(player1Shown, Type.CARD_ARRAY_SHOWED)));
                        client2.send(GSON.toJson(new DataPacket(player2Shown, Type.CARD_ARRAY_SHOWED)));
                        client1.send(GSON.toJson(new DataPacket(player1Hand, Type.CARD_ARRAY_HAND)));
                        client2.send(GSON.toJson(new DataPacket(player2Hand, Type.CARD_ARRAY_HAND)));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
