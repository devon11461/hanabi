package hanabi;

import java.util.ArrayList;

/**
 * Each instance of player will have an instance of hand. This class
 * just holds the list of cards that a player has in their hand. The length of
 * this list, after the player is initialized, should not be less than 4
 * nor greater than 5, depending on the amount of players in the game
 * (see hanabi rules for more detail).
 * Object of type hand will also record the height and width of the cards
 * in the hand. This will be used for hit detection.
 * These fields (cardWidth, cardHeight) will not be initialized or used for
 * AIPlayers.
 */
public class Hand {
  private ArrayList<Card> cards;
  private float cardWidth, cardHeight;

  Hand(){
    cards = new ArrayList<>();
  }

  /**
   * method addCard(Card) is a method that adds a card to the hand.
   * @param c Card object that will be added to the hand.
   */
  public void addCard(Card c){
    cards.add(c);
  }

  /**
   * removeCard(Card) is a method responsible for removing a given card
   * from a hand.
   * @param c Card object that will be removed from a hand.
   */
  public void removeCard(Card c){
    cards.remove(c);
  }

  /**
   * checkHit(float, float) is a method responsible for doing hit detection
   * on a plyaer's hand.
   * @param x float x-coordinate from a mouse click.
   * @param y float y-coordinate from a mouse click.
   * @return boolean true if we clicked a card in this hand, false otherwise.
   */
  public boolean checkHit(float x, float y){
      for (Card c: this.cards){
        if (c.xCorner<x && x<c.xCorner+cardWidth &&  c.yCorner<y && y<c.yCorner+cardHeight){
          return true;
        }
      }
    return false;
  }

  /**
   * getHand() is a method responsible for returning a container of cards
   * in the hand.
   * @return ArrayList of cards that is all the cards in this hand.
   */
  public ArrayList<Card> getHand(){
    return this.cards;
  }

  /**
   * setDimension(float, float) is a method that sets the width and height
   * of the cards in the hand. All cards in the hand will be drawn with the same
   * width and height. Used for hit detection.
   * @param width Float the width of each card in the hand.
   * @param height Float the height of each card in the hand.
   */
  public void setDimension(float width, float height){
    cardWidth=width;
    cardHeight=height;

  }

 /**
  * purpose: getter the width of the card
  * @return float the width of each card in the hand.
 */
  public float getWidth(){
    return this.cardWidth;
  }

  /**
   * purpose: getter the height of the card
   * @return float the height of each card in the hand.
  */
  public float getHeight(){
    return this.cardHeight;
  }
}
