package com.example.CENG453_20242_GROUP15_backend.game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private Integer id;
    private String name;
    private List<Card> hand = new ArrayList<>();
    private boolean isCPU;
    private boolean hasDeclaredUno = false;

    public Player(Integer id, String name, boolean isCPU) {
        this.id = id;
        this.name = name;
        this.isCPU = isCPU;
    }
    public Player(String name, boolean isCPU) {
        this.name = name;
        this.isCPU = isCPU;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public boolean isCPU() {
        return isCPU;
    }

    public void setCPU(boolean isCPU) {
        this.isCPU = isCPU;
    }

    public boolean hasDeclaredUno() {
        return hasDeclaredUno;
    }

    public void setDeclaredUno(boolean hasDeclaredUno) {
        this.hasDeclaredUno = hasDeclaredUno;
    }

    public List<Card> getPlayableCards(Card topCard, Card.Color currentColor) {
        List<Card> playable = new ArrayList<>();
        for (Card c : hand) {
            if (c.isPlayableOn(topCard, currentColor)) {
                playable.add(c);
            }
        }
        return playable;
    }

}
