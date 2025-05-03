package com.example.CENG453_20242_GROUP15_backend.game;

public class PlayCardRequest {
    private int cardIndex; // index of the card in the human player's hand
    private Card.Color chosenColor; // needed for Wild/Wild Draw Four

    public PlayCardRequest() {}

    public PlayCardRequest(int cardIndex, Card.Color chosenColor) {
        this.cardIndex = cardIndex;
        this.chosenColor = chosenColor;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public Card.Color getChosenColor() {
        return chosenColor;
    }

    public void setChosenColor(Card.Color chosenColor) {
        this.chosenColor = chosenColor;
    }
}
