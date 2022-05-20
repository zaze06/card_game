package me.alien.card.game.client;

import me.alien.card.game.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import static me.alien.card.game.Main.GSON;
import static me.alien.card.game.Main.VERSION;

public class Client extends JComponent implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
    private Switch mousePressed = new Switch();
    Timer displayTimer;
    Timer gameTimer;

    int availableCardsPile = 0;
    Box2I availablePile;

    Card topPlayed;
    Card[] hidden = new Card[3];
    Box2I[] hiddenBox = new Box2I[3];
    Card[] showed = new Card[3];
    Box2I[] shownBox = new Box2I[3];

    ArrayList<Card> hand = new ArrayList<>();
    ArrayList<Box2I> handBox = new ArrayList<>();
    Point mousePos = new Point(-1, -1);

    int oldSize = 0;

    Socket socket;
    PrintWriter out;

    final ArrayList<DataPacket> dataIn = new ArrayList<>();
    DataInThread dataInThread = new DataInThread();

    public Client(String hostname){

        try{
            socket = new Socket(hostname, 4067);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            br.readLine();
            out.println(GSON.toJson(new DataPacket(VERSION.toString(), Type.VERSION)));
            out.println(GSON.toJson(new DataPacket("ready", Type.INFO)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dataInThread.start();

        displayTimer = new Timer(100, this);
        displayTimer.start();
        gameTimer = new Timer(100, e -> {
            try {
                DataPacket data = dataIn.get(0);
                if(data.getType() == Type.ERROR){
                    System.out.print(data.getData(String.class));
                    System.exit(1);
                }

                if(data.getType() == Type.NEW_GAME){
                    init();
                }
                else if (data.getType() == Type.CARD_ARRAY_HAND) {
                    hand = new ArrayList<>(data.getData(ArrayList.class));
                    for (int i = 0; i < hand.size(); i++) {
                        int xPos = (getWidth() / 2 + (30 * i)) - (30 * hand.size() / 2) + (i * 3);
                        handBox.add(i, new Box2I(xPos, getHeight() - 40, 30, 40));
                    }
                } else if (data.getType() == Type.CARD_ARRAY_HIDDEN) {
                    ArrayList<Card> cards = data.getData(ArrayList.class);
                    System.arraycopy(cards.toArray(new Card[]{}), 0, hidden, 0, 3);
                    for (int j = 0; j < 3; j++) {
                        int xOff = switch (j) {
                            case 0 -> (30 + 3 + 15) * -1;
                            case 2 -> (3 + 15);
                            default -> -15;
                        };
                        if (hidden[j] == null) continue;
                        hiddenBox[j] = new Box2I(getWidth() / 2 + xOff + 6, getHeight() - 88 - 5, 30, 40);
                    }
                } else if (data.getType() == Type.CARD_ARRAY_SHOWED) {
                    ArrayList<Card> cards = data.getData(ArrayList.class);
                    System.arraycopy(cards.toArray(new Card[]{}), 0, showed, 0, 3);
                    for (int j = 0; j < 3; j++) {
                        int xOff = switch (j) {
                            case 0 -> (30 + 3 + 15) * -1;
                            case 2 -> (3 + 15);
                            default -> -15;
                        };
                        if (showed[j] == null) continue;
                        shownBox[j] = new Box2I(getWidth() / 2 + xOff + 6, getHeight() - 88 - 5, 30, 40);
                    }
                }

                for (int i = 0; i < 3; i++) {
                    if (Arrays.equals(showed, new Card[]{null, null, null}) && hand.isEmpty()) {
                        if (hiddenBox[i].contains(mousePos) && mousePressed.read()) {
                            out.print(GSON.toJson(new DataPacket(hidden[i], Type.CARD_HIDDEN)));
                            break;
                        }
                    }
                }
                for (int i = 0; i < 3; i++) {
                    if (hand.isEmpty()) {
                        if (shownBox[i].contains(mousePos) && mousePressed.read()) {
                            out.print(GSON.toJson(new DataPacket(showed[i], Type.CARD_SHOWED)));
                            break;
                        }
                    }
                    for (int j = 0; j < 3; j++) {
                        int xOff = switch (j) {
                            case 0 -> (30 + 3 + 15) * -1;
                            case 2 -> (3 + 15);
                            default -> -15;
                        };
                        if (hidden[j] == null) continue;
                        hiddenBox[j] = new Box2I(getWidth() / 2 + xOff + 6, getHeight() - 88 - 5, 30, 40);
                    }
                }
                for (int i = 0; i < hand.size(); i++) {
                    if (handBox.isEmpty()) break;
                    if (handBox.get(i).contains(mousePos) && mousePressed.read()) {
                        out.print(GSON.toJson(new DataPacket(hand.get(i), Type.CARD_HAND)));
                        break;
                    }
                }
                if (availablePile.contains(mousePos) && mousePressed.read()) {
                    if (availableCardsPile > 0) {
                        out.print(GSON.toJson(new DataPacket("", Type.AVALIBLE_PILE)));
                    }
                }

                try {

                } catch (IndexOutOfBoundsException ignored) {
                }
                if (availableCardsPile == 0) {
                    availablePile = new Box2I(-2, -2, 0);
                }
            }catch (Exception ignored){}
        });
        gameTimer.start();
    }

    public void init(){
        for(int i = 0; i < 3; i++){
            int xOff = switch (i) {
                case 0 -> (30 + 3 + 15)*-1;
                case 2 -> (3 + 15);
                default -> -15;
            };
            hiddenBox[i] = new Box2I(getWidth()/2+xOff+6, getHeight()-88-5,30,40);
        }
        for(int i = 0; i < 3; i++){
            int xOff = switch (i) {
                case 0 -> (30 + 3 + 15)*-1;
                case 2 -> (3 + 15);
                default -> -15;
            };

            shownBox[i] = new Box2I(getWidth()/2+xOff+6, getHeight()-88-5,30,40);
        }

        for(int i = 0; i < hand.size(); i++) {
            int xPos = (getWidth() / 2 + (30 * i)) - (30 * hand.size() / 2) + (i * 3);
            handBox.add(i, new Box2I(xPos+2, getHeight()-46, 30,40));
        }

        availablePile = new Box2I(getWidth()/2+10,getHeight()/2-22,30,40);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        doDraw(g2d);
    }

    private void doDraw(Graphics2D g2d) {
        /*for(int i = 0; i < 13; i++){
            Deck.HEART[i].draw(g2d, i*30+2+(i*5), 0+2);
        }
        for(int i = 0; i < 13; i++){
            Deck.DIAMOND[i].draw(g2d, i*30+2+(i*5), 40+2+5);
        }
        for(int i = 0; i < 13; i++){
            Deck.SPADE[i].draw(g2d, i*30+2+(i*5), 80+2+10);
        }
        for(int i = 0; i < 13; i++){
            Deck.CLUB[i].draw(g2d, i*30+2+(i*5), 120+2+15);
        }
        int i = 13;
        Card.drawCover(g2d,i*30+2+(i*5),2);
        Card.drawCover(g2d,i*30+2+(i*5),40+2+5);
        Card.drawCover(g2d,i*30+2+(i*5),80+2+10);
        Card.drawCover(g2d,i*30+2+(i*5),120+2+15);*/

        //for(int i = 0; i < getWidth(); i += 30){
        //    g2d.drawLine(i,0, i, getHeight());
        //}

        try {
            topPlayed.draw(g2d, getWidth() / 2 - 35, getHeight() / 2 - 20);
        }catch (IndexOutOfBoundsException | NullPointerException ignored){}

        if(availableCardsPile > 0){
            Box2I box = availablePile;
            if(box.contains(mousePos)){
                g2d.setColor(Color.ORANGE);
                g2d.fillRect(box.getStartX()-2,box.getStartY()-3,box.getWidth()+5,box.getHeight()+5);
            }
        }
        if(availableCardsPile > 40){
            Card.drawCover(g2d, getWidth()/2+13, getHeight()/2-20);
        }
        if(availableCardsPile > 30){
            Card.drawCover(g2d, getWidth()/2+11, getHeight()/2-20);
        }
        if(availableCardsPile > 20){
            Card.drawCover(g2d, getWidth()/2+9, getHeight()/2-20);
        }
        if(availableCardsPile > 10){
            Card.drawCover(g2d, getWidth()/2+7, getHeight()/2-20);
        }
        if(availableCardsPile > 0){
            Card.drawCover(g2d, getWidth()/2+5, getHeight()/2-20);
        }

        for(int i = 0; i < 3; i++){
            if(hidden[i] != null){
                if(Arrays.equals(hidden, new Card[]{null, null, null})){
                    Box2I box = hiddenBox[i];
                    if(box.contains(mousePos)){
                        g2d.setColor(Color.ORANGE);
                        g2d.fillRect(box.getStartX()-3,box.getStartY()-3,box.getWidth()+5,box.getHeight()+5);
                    }
                }
                int xOff = switch (i) {
                    case 0 -> (30 + 3 + 15)*-1;
                    case 2 -> (3 + 15);
                    default -> -15;
                };
                if(hidden[i] == null) continue;
                Card.drawCover(g2d, getWidth()/2 + xOff, getHeight()-88);
            }
        }

        for(int i = 0; i < 3; i++){
            if(showed[i] != null){
                Box2I box = shownBox[i];
                if(box.contains(mousePos)){
                    g2d.setColor(Color.orange);
                    g2d.fillRect(box.getStartX()-3,box.getStartY()-3,box.getWidth()+5,box.getHeight()+5);
                }
                int xOff = switch (i) {
                    case 0 -> (30 + 3 + 15)*-1;
                    case 2 -> (3 + 15);
                    default -> -15;
                };
                if(showed[i] == null) continue;
                showed[i].draw(g2d, getWidth()/2 + xOff, getHeight()-88);
            }
        }

        for(int i = 0; i < hand.size(); i++){
            int xPos = ((getWidth()/2+(30*i))-(30*hand.size()/2)+(i*3))-3;
            Box2I box = handBox.get(i);
            if(box.contains(mousePos)){
                g2d.setColor(Color.orange);
                g2d.fillRect(box.getStartX()-5,box.getStartY()-2,box.getWidth()+5,box.getHeight()+5);
            }
            if(hand.get(i) == null) continue;
            hand.get(i).draw(g2d, xPos, getHeight()-40);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed.set(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed.set(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        mousePos = new Point(-1, -1);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
    }

    private class DataInThread extends Thread{
        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    synchronized (dataIn) {
                        String data = br.readLine();
                        if (data == null) continue;
                        System.out.print("received: " + data);
                        dataIn.add(GSON.fromJson(data, DataPacket.class));
                        out.println(GSON.toJson(new DataPacket("ready", Type.INFO)));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
