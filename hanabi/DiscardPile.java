package hanabi;

import java.util.ArrayList;

/**
 * Similar to the hand class. The only field that we need to maintain in
 * this class is a record of all the cards in the discard pile. Container
 * is an ArrayList of Cards titled pile.
 */
public class DiscardPile{
  private ArrayList<Card> pile;

  DiscardPile(){
    pile = new ArrayList<>();
  }

  /**
   * This method returns the discard pile. A getter.
   * @return an ArrayList containing all discarded cards.
   */
  public ArrayList<Card> getPile(){
    return this.pile;
  }

  /**
   * receiveCard(Card) is a method responsible for adding a card to the discard
   * pile.
   * @param c Card object that we wish to add to the discard pile.
   */
  public void recieveCard(Card c){
    this.pile.add(c);
  }
}
