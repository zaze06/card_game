package me.alien.card.game.util;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;

public class Card {
    int color;
    int value;

    private static final String[] DISPLAY = {"3", "4", "5", "6", "7", "8","9", "J", "Q", "K", "A", "10", "2"};
    private static final String[] ICONS = {"♠", "♣", "♦", "♥"};

    public Card(int color, int value) {
        this.color = color;
        this.value = value;
    }

    public int getColorValue() {
        return color;
    }

    public Color getColor(){
        if(color == 0){
            return Color.BLACK;
        } else if (color == 1) {
            return Color.BLACK;
        } else if (color == 2) {
            return Color.RED;
        } else if (color == 3) {
            return Color.RED;
        }
        return null;
    }

    public String getColorIcon(){
        return ICONS[color];
    }

    public int getValue() {
        return value;
    }

    public String getValueChar(){
        return DISPLAY[value];
    }

    public void draw(Graphics2D g2d, int x, int y){
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y,30,40);

        g2d.setColor(getColor());
        g2d.drawRect(x, y, 30, 40);

        g2d.setFont(new Font("Sans-Serif", Font.PLAIN, 11));
        g2d.drawString(getValueChar(), x+3,y+12);
        g2d.drawString(getColorIcon(), x+20,y+12);

        g2d.setFont(new Font("Sans-Serif", Font.PLAIN, -11));
        g2d.drawString(getValueChar(), x+28,y+30);
        g2d.drawString(getColorIcon(), x+12,y+30);
    }

    public static void drawCover(Graphics2D g2d, int x, int y){
        /*g2d.drawLine(x+10,y+0,x+0,y+10);
        g2d.drawLine(x+10,y+40,x+0,y+30);
        g2d.drawLine(x+20,y+0,x+30,y+10);
        g2d.drawLine(x+20,y+40,x+30,y+30);*/

        g2d.setColor(new ColorUIResource(44, 81, 96));
        g2d.fillRect(x, y,30,40);

        g2d.setColor(Color.BLACK);

        for(int x1 = x; x1 < x+30; x1 += 10) {
            for (int y1 = y; y1 < y + 40; y1 += 10) {
                g2d.drawLine(x1 + 10, y1 + 0, x1 + 0, y1 + 10);
            }
        }

        for(int x1 = x; x1 < x+30; x1 += 10) {
            for (int y1 = y; y1 < y + 40; y1 += 10) {
                g2d.drawLine(x1 + 0, y1 + 0, x1 + 10, y1 + 10);
            }
        }

        g2d.drawRect(x,y,30,40);
    }

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", value=" + value +
                '}';
    }

    public String card(){
        return getColorIcon()+" "+getValueChar();
    }

    public static String getValueChar(int value){
        return DISPLAY[value];
    }
}
