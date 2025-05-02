package com.example.CENG453_20242_GROUP15_backend.game;

public class Card {
    public enum Color { RED, YELLOW, GREEN, BLUE, WILD }
    public enum Type {
        NUMBER, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR
    }

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
        return this.color == currentColor ||
                (this.type == Type.NUMBER && topCard.getType() == Type.NUMBER && this.number == topCard.getNumber()) ||
                this.color == Color.WILD ||
                this.type == Type.WILD || this.type == Type.WILD_DRAW_FOUR;
    }

}
