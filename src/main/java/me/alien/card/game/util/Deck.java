package me.alien.card.game.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Deck {
    public static final Card[] SPADE = {new Card(0, 0), new Card(0, 1), new Card(0, 2), new Card(0, 3), new Card(0, 4), new Card(0, 5), new Card(0, 6), new Card(0, 7), new Card(0, 8), new Card(0, 9), new Card(0, 10), new Card(0, 11), new Card(0, 12)};//, new Card(0, 13)};
    public static final Card[] CLUB = {new Card(1, 0), new Card(1, 1), new Card(1, 2), new Card(1, 3), new Card(1, 4), new Card(1, 5), new Card(1, 6), new Card(1, 7), new Card(1, 8), new Card(1, 9), new Card(1, 10), new Card(1, 11), new Card(1, 12)};//, new Card(1, 13)};
    public static final Card[] DIAMOND = {new Card(2, 0), new Card(2, 1), new Card(2, 2), new Card(2, 3), new Card(2, 4), new Card(2, 5), new Card(2, 6), new Card(2, 7), new Card(2, 8), new Card(2, 9), new Card(2, 10), new Card(2, 11), new Card(2, 12)};//, new Card(2, 13)};
    public static final Card[] HEART = {new Card(3, 0), new Card(3, 1), new Card(3, 2), new Card(3, 3), new Card(3, 4), new Card(3, 5), new Card(3, 6), new Card(3, 7), new Card(3, 8), new Card(3, 9), new Card(3, 10), new Card(3, 11), new Card(3, 12)};//, new Card(3, 13)};
    public static final Card[] DECK = Arrays.concatenate(SPADE, Arrays.concatenate(CLUB, Arrays.concatenate(DIAMOND, HEART)));

    public static ArrayList<Card> shuffel(){
        ArrayList<Card> left = new ArrayList<>(List.of(DECK));
        ArrayList<Card> out = new ArrayList<>();


        for(int i = 0; i < DECK.length; i++){
            int index = (int) (Math.random()*left.size());
            out.add(left.get(index));
            left.remove(index);
        }

        return out;
    }
}
