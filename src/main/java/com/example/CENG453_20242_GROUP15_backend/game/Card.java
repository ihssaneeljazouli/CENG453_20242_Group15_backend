package com.example.CENG453_20242_GROUP15_backend.game;

public class Card {
    public enum Color { RED, YELLOW, GREEN, BLUE, WILD }
    public enum Type {NUMBER, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR}

    private Color color;
    private Type type;
    private int number; // -1 if type not number

    public Card(Color color, Type type, int number) {
        this.color = color;
        this.type = type;
        this.number = number;
    }

    public Type getType(){
        return this.type;
    }
    public void setType(Type type){
        this.type= type;
    }
    public Color getColor(){
        return this.color;
    }
    public void setColor(Color color){
        this.color= color;
    }

    public int getNumber(){
        return this.number;
    }
    public void setNumber(int number){
        this.number= number;
    }

     public boolean isPlayableOn(Card topCard, Color currentColor) {
        // Always playable
        if (this.type == Type.WILD || this.type == Type.WILD_DRAW_FOUR) {
            return true;
        }
        // Color match
        if (this.color == currentColor) {
            return true;
        }
        // Number card: match number
        if (this.type == Type.NUMBER && topCard.getType() == Type.NUMBER && this.number == topCard.getNumber()) {
            return true;
        }
        // Action card: match same type (SKIP, REVERSE, DRAW_TWO)
        if (this.type == topCard.getType() && this.type != Type.NUMBER) {
            return true;
        }
        return false;
    }


}
