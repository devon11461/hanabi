package hanabi;
import java.util.*;
import org.json.simple.JSONObject;

/**
 * AIPlayers for the users connecting to our client.
 * A view will not be drawn for these users.
 * Still have an ID and hand. 
 *
 */
public class AIPlayer implements Player {
  int id;
  Hand hand;

AIPlayer(int myId){
  id = myId;
  hand = new Hand();
}

/**
 * Generate a full deck of hanabi cards.
 * <p> There is 10 cards per suite, and 5-6 different suites, depending on the
 * game mode we're in.
 *
 * @param rainbow A boolean that represents if we're in rainbow mode or not.
 * @return An arrayList of Cards that contains 10 cards of each suite, in order.
 */
public ArrayList<Card> createDeck(boolean rainbow){
  ArrayList<Card> deck = new ArrayList<>();
  ArrayList<Card> holdSuite = createSuite("red");
  for (Card card : holdSuite){
    deck.add(card);
  }
  holdSuite = createSuite("yellow");
  for (Card card : holdSuite){
    deck.add(card);
  }
  holdSuite = createSuite("blue");
  for (Card card : holdSuite){
    deck.add(card);
  }
  holdSuite = createSuite("white");
  for (Card card : holdSuite){
    deck.add(card);
  }
  holdSuite = createSuite("green");
  for (Card card : holdSuite){
    deck.add(card);
  }
  if (rainbow){
    holdSuite = createSuite("rainbow");
    for (Card card : holdSuite){
      deck.add(card);
    }
  }
  return deck;
}

/**
 * Generate a full 10 cards of a given suite.
 * <p>
 * there will be 3 ones, 2 twos, 2 threes, 2 fours and 1 five.
 *
 * @param color a string representing the color of the suite we're building. This must be yellow, white, blue, green, black, or rainbow. All lowercase
 * @return An arrayList of cards with a length of 10.
 */
public ArrayList<Card> createSuite(String color){
  ArrayList<Card> suite = new ArrayList<>();
  // 3 ones, 2 twos, 2 threes, 2 fours, 1 five
  for (int i = 0; i < 3; i++){
    Card newCard = new Card(color, 1);
    suite.add(newCard);
  }
  for (int i = 0; i < 2; i++){
    Card newTwo = new Card(color, 2);
    Card newThree = new Card(color, 3);
    Card newFour = new Card(color, 4);
    suite.add(newTwo);
    suite.add(newThree);
    suite.add(newFour);
  }
  suite.add(new Card(color, 5));

  return suite;
}

/**
 * This method will get the color of a card given it's index in the center pile or frequencies list.
 *
 * @param i an integer that is mapped down below to a color. i is between 0 and 5, inclusively
 * @return  a string that a all lower-case representation of a color. white, yellow, blue, green, red or rainbow.
 */
public String getColor(int i){
  if (i == 0){
    return "red";
  }
  if (i == 1){
    return "white";
  }
  if (i == 2){
    return "green";
  }
  if (i == 3){
    return "blue";
  }
  if (i == 4){
    return "yellow";
  }
  if (i == 5){
    return "rainbow";
  }
  return "ERROR";
}

/**
 * To compare two cards based off of the number and color.
 *
 * @param card1 the first card that we wish to compare to another card.
 * @param card2 the second card that we wish to compare to antoher card.
 * @return      a boolean, true if the two cards are similar, false otherwise.
 */
public boolean sameCard(Card card1, Card card2){
  return card1.getNumber() == card2.getNumber() && card1.getColor().equals(card2.getColor());
}

/**
 * This method calculates the most probable card in the AIs hand.
 *
 * @param discard an object of type DiscardPile, passed in from the model.
 * @param players an arrayList of players containing all players in the game.
 * @param centerPile an arrayList of integers passed in from the model. Represents the piles of played cards.
 * @param rainbow a boolean that represents if we're in rainbow mode or not. True if we are, false otherwise.
 * @return        Card a card that represents the most probable card in our AIs hand.
*/
public Card calculateMove(DiscardPile discard, ArrayList<Player> players, ArrayList<Integer> centerPile, boolean rainbow){
  //First portion code gathers all seen cards into one arrayList called
  //knownCards.
  ArrayList<Card> deck = this.createDeck(rainbow);
  ArrayList<Card> knownCards = new ArrayList<>();
  for (Card c : discard.getPile()){
    Card hold = new Card(c.getColor(), c.getNumber());
    knownCards.add(hold);
  }
  for (Player player : players ){
    for (Card c : player.getHand().getHand()){
      Card hold2 = new Card(c.getColor(), c.getNumber());
      knownCards.add(hold2);
    }
  }
  // red, white, green, blue, yellow (Remember rainbow)
  int centerSize = 5;
  if (rainbow){
     centerSize = 6;
   }
  for (int num = 0; num < centerSize; num++){
    int value = centerPile.get(num);
    String color = getColor(num);
    for (int i = value; i > 0; i--){
      Card newCard = new Card(color, i);
    }
  }
  // Populate another arraylist with all cards we need to remove, and how many times
  //we must remove them.
 ArrayList<Card> removeTheseAfter = new ArrayList<>();
  for (Card c : deck){
    for (Card knownC : knownCards){
      if (sameCard(c, knownC)){
        removeTheseAfter.add(c);
        knownCards.remove(knownC);
        break;
      }
    }
  }

  //remove all seen cards from the deck.
  for (Card c : removeTheseAfter){
    deck.remove(c);
  }

  //This next portion of code counts how many occurences of each card
  //we see after removing all seen cards from deck.
  ArrayList<Integer> countingFrequencies = new ArrayList<>();
  //must be 30 for rainbow mode.
  int frequencyHolders = 25;
  if (rainbow){
    frequencyHolders = 30;
  }
  for (int i = 0; i < frequencyHolders; i++){
    countingFrequencies.add(0);
  }
  //Iterating the right frequency every time we see a card.
  for (Card c : deck){
    int iter = getArea(c);
    iter += c.getNumber()-1;
    countingFrequencies.set(iter, countingFrequencies.get(iter)+1);
  }
  //Getting the most seen index and mapping it to a card color+value.
  int ourIndex = getMaxIndex(countingFrequencies);
  int ourColorNumber = ourIndex/5;
  int ourNumberValue = (ourIndex % 5) + 1;
  String theFinalColor = getColor(ourColorNumber);
  return (new Card(theFinalColor, ourNumberValue));
}

/**
 * This method takes in the most likely card in our AI's hand and determines if
 * the AI would be better off playing it or discarding it.
 * <p>
 * This method assumes that the card in the AI's hand is this card.
 * This method will also choose to play the card if we have 7 information tokens.
 * @param c a card object that represents the most likely card in our AI's hand, calculated from the function calcuateMove(...)
 * @param centerPile An ArrayList from the model.
 * @param infoTokens an integer from the model, representing how many information tokens we need.
 * @return           A JSONObject containing if we should play or discard the card in question. The object has fields action and position.
 */
@SuppressWarnings("unchecked")
public JSONObject chooseDiscardPlay(Card c, ArrayList<Integer> centerPile, int infoTokens){
  //hacky thing to change later. Issue if we know our cards.
  int handCardIndex = 0;
  for (int i = 0; i < this.getHand().getHand().size(); i++){
    if (this.getHand().getHand().get(i).getColor().equals("empty")){
      handCardIndex = i;
      break;
    }
  }
  JSONObject obj = new JSONObject();
  obj.put("action", "");
  obj.put("position", 0);
  if (infoTokens < 7 && !canPlay(c, centerPile)){
    obj.put("action", "discard");
    obj.put("position", handCardIndex);
  }
  else {
    obj.put("action", "play");
    obj.put("position", handCardIndex);
  }
  return obj;
}

public boolean canPlay(Card c, ArrayList<Integer> centerPile){
  int centerPileIndex = getArea(c)/5;
  int highestCardNumberForColor = centerPile.get(centerPileIndex);
  return (c.getNumber() == highestCardNumberForColor+1);
}

/**
 * This method is a getter for our player's hand.
 *
 * @return A hand object that is our player's hand.
 */
@Override
public Hand getHand(){
  return this.hand;
}

/**
 * This method is responsible for choosing between playing/discard a card and
 * telling information to a team mate.
 * <p>
 * This method will always choose to play a card it knows if it can play it.
 * Likewise, this method will also always choose to discard a card if it knows
 * the card is no longer needed.
 * <p>
 * This method also will default to tellin information if the AI knows nothing about
 * it's own cards and has information tokens left over.
 *
 * @param discardPlay A JSONObject calculated from the method chooseDiscardPlay(...)
 * @param tellInfo    A JSONObject calculated from the method tellInformation(...)
 * @param centerPile  An ArrayList of integers representing the centerpile in the game. Comes straight from the model.
 * @param infoTokens  An integer that represents the amount of info tokens left in the game. Comes from the model.
 * @return            A JSONObject that contains informatin about the move our AI wishes to make.
 */
@SuppressWarnings("unchecked")
public JSONObject chooseMove(JSONObject discardPlay, JSONObject tellInfo, ArrayList<Integer> centerPile, int infoTokens){
  for (Card c : this.getHand().getHand()){
    if (!c.getColor().equals("empty")){
      if (c.getNumber() != 0){
        JSONObject obj = chooseDiscardPlay(c, centerPile, infoTokens);
        if (obj.get("action").equals("play")){
          return obj;
        } else if (centerPile.get(this.getArea(c)/5) <= c.getNumber() && infoTokens < 7){
          obj = new JSONObject();
          obj.put("action", "discard");
          obj.put("position", this.getHand().getHand().indexOf(c));
          return obj;
        }
      } else {
        //known color but not number. Default to play the color. Why tell us
        JSONObject obj = new JSONObject();
        obj.put("action", "play");
        obj.put("position", this.getHand().getHand().indexOf(c));
        return obj;
      }
    } else if (c.getNumber() != 0){
      for (int i : centerPile){
        if (c.getNumber() == i+1){
          JSONObject obj = new JSONObject();
          obj.put("action", "play");
          obj.put("position", this.getHand().getHand().indexOf(c));
          return obj;
        }
      }
    }
  }
  if (infoTokens > 0 && tellInfo != null){
    return tellInfo;
  } else {
    return discardPlay;
  }
}

/**
 * A method that the model will call when it is our AI's turn. It will trigger
 * a series of function calls above.
 *
 * @param players The ArrayList of players from the model.
 * @param centerPile The arrayList of integers from the model.
 * @param informationTokens The integer representing how many information tokens are left in the game from the model.
 * @param rainbow a boolean that represents if we're in rainbow mode or not. True if we are, false otherwise.
 * @param discard A DiscardPile from the model.
 * @return        A JSONObject that contains the move our AI will complete.
 */
@Override
public JSONObject sendJson(ArrayList<Player> players, ArrayList<Integer> centerPile, int informationTokens, boolean rainbow, DiscardPile discard) {
    Card mostLikelyCard = calculateMove(discard, players, centerPile, rainbow);
    JSONObject likelyCardPlayDiscard = chooseDiscardPlay(mostLikelyCard, centerPile, informationTokens);
    JSONObject tellInfoMessage = tellInformation(players, informationTokens, centerPile);
    JSONObject finalJSON = chooseMove(likelyCardPlayDiscard, tellInfoMessage, centerPile, informationTokens);
    return finalJSON;
  }

  /**
   * This method maps the color of a card to a specific range of numbers.
   * <p>
   * red cards will be mapped to values 0-4 (depending on their numerical value)
   * <p>
   * white cards will be mapped to values 5-9
   * <p>
   * green cards will be mapped to values 10-14
   * <p>
   * blue cards will be mapped to values 15-19
   * <p>
   * yellow cards will be mapped to values 20-24
   * <p>
   * rainbow cards will be mapped to values 25-29.
   * <p>
   * All values returned will be the start of these intervals (ie 0, 5, 10, 15, 20, 25)
   * @param c A Card that we wish to map a region based on it's color.
   * @return  An integer of either 0, 5, 10, 15, 20, 25, mapped in the way described above.
   */
private int getArea(Card c){
    if (c.getColor().equals("red")){
      return 0;
    }
    if (c.getColor().equals("white")){
      return 5;
    }
    if (c.getColor().equals("green")){
      return 10;
    }
    if (c.getColor().equals("blue")){
      return 15;
    }
    if (c.getColor().equals("yellow")){
      return 20;
    }
    if (c.getColor().equals("rainbow")){
      return 25;
    }
    return -1;
  }

  /**
   * After we calculate which card is the most frequently available card, we return
   * The index that this card is stored at.
   * <p>
   * This mapping is described in the method getArea(Card)'s documentation.
   *
   * @param frequencies An arrayList of integers that contains how many times each card occurs given the above mappings.
   * @return the integer of the index representing the most likely card.
   */
  public int getMaxIndex(ArrayList<Integer> frequencies){
    int max = 0;
    int maxI = 0;
    for (int i = 0; i < frequencies.size(); i++){
      if (frequencies.get(i) > max){
        max = frequencies.get(i);
        maxI = i;
      }
    }
    return maxI;
  }

  /**
   * A method responsible for adding a card to our AI's hand.
   *
   * @param c A card that we wish to add to our AI's hand.
   */
  public void addCard(Card c){
    this.hand.addCard(c);
  }

  /**
   * A method responsible for removing a card from our AI's hand.
   *
   * @param c A card that we wish to remove from our AI's hand.
   */
  public void removeCard(Card c){
    this.hand.removeCard(c);
  }

  /**
   * A method that can get our us AI's ID.
   *
   * @return an integer that is our AIPlayer's id.
   */
  @Override
  public int getId(){
    return this.id;
  }

  /**
   * This method is responsible for determining if we can immediately
   * tell a team mate information. This will return null if we can not.
   *
   * @param players An arrayList of players stored in our model. All players in the game.
   * @param informationTokens An integer representing how many information tokens the team has.
   * @param center An arrayList of integers that represents the cards that have been successfully played.
   * @return A JSONObject that contains information about who and what we're telling a player. Will be null if we can't tell anything.
  */
  @SuppressWarnings("unchecked")
  public JSONObject tellInformation(ArrayList<Player> players, int informationTokens, ArrayList<Integer> center){
    if (informationTokens == 0){
      return null;
    }
    for (int i = 0; i < players.size(); i++){
      if (players.get(i).getId() != this.id){
        for (Card c : players.get(i).getHand().getHand()){
            if (this.canPlay(c, center) && !c.getKnowNumber()){
              JSONObject obj = new JSONObject();
              obj.put("action", "inform");
              obj.put("player", players.get(i).getId());
              obj.put("rank", c.getNumber());
              return obj;
            }
        }
      }
    }
    return null;
  }

  public JSONObject constructJSON(String action, int playerId, String color, int index, int number){
    return null;
  }

  public void removeCard(int index){
    this.hand.getHand().remove(index);
  }


  /*
  A bunch of tests for this class. More to come.
  */
  public static void main(String[] args){

      AIPlayer p1 = new AIPlayer(1);
      AIPlayer p2 = new AIPlayer(2);
      AIPlayer p3 = new AIPlayer(3);
      AIPlayer p4 = new AIPlayer(4);
      ArrayList<Card> testDeck = p4.createDeck(false);
      assert(testDeck.size() == 50);
      DiscardPile p = new DiscardPile();
      ArrayList<Integer> center = new ArrayList<>();
      for (int i = 0; i < 6; i++){
        center.add(0);
      }
      p1.addCard(new Card("yellow", 1));
      p2.addCard(new Card("red", 1));
      p3.addCard(new Card("white", 1));
      p1.addCard(new Card("green", 1));
      p2.addCard(new Card("blue", 1));
      ArrayList<Player> players = new ArrayList<>();
      players.add(p1);
      players.add(p2);
      players.add(p3);
      System.out.println(p4.calculateMove(p, players, center, true).getColor()); //should say 'rainbow'
      assert(p4.sameCard(p4.calculateMove(p, players, center, true), new Card("rainbow", 1)));

      System.out.println(p4.calculateMove(p, players, center, false).getColor()); //should say 'red'
      System.out.println(p4.calculateMove(p, players, center, false).getNumber()); //should say '1'
      assert(p4.sameCard(p4.calculateMove(p, players, center, false), new Card("red", 1)));

      p4.addCard(new Card("empty", 0));
      JSONObject thePlayDiscardMessage = p4.chooseDiscardPlay(p4.calculateMove(p, players, center, false), center, 6);
      System.out.println(thePlayDiscardMessage.get("action") + " " + thePlayDiscardMessage.get("position"));
      assert(thePlayDiscardMessage.get("action").equals("play"));
      assert((int)thePlayDiscardMessage.get("position") == 0);
      center.set(0, 1);
      thePlayDiscardMessage = p4.chooseDiscardPlay(p4.calculateMove(p, players, center, false), center, 6);
      System.out.println(thePlayDiscardMessage.get("action") + " " + thePlayDiscardMessage.get("position"));
      assert(thePlayDiscardMessage.get("action").equals("discard"));
      assert((int)thePlayDiscardMessage.get("position") == 0);

      JSONObject tellInformation = p4.tellInformation(players, 5, center);
      System.out.println("Our Answer: " + tellInformation.get("rank") + ", the right answer: 1");
      System.out.println("Our Answer: " + tellInformation.get("player") + ", the right answer: 1");

      //BIG test

       p1 = new AIPlayer(1);
       p2 = new AIPlayer(2);
       p3 = new AIPlayer(3);

      p = new DiscardPile();
      center = new ArrayList<>();
      for (int i = 0; i < 6; i++){
        center.add(0);
      }

      center.set(4, 1);
      p1.addCard(new Card("yellow", 2));
      p1.addCard(new Card("blue", 1));
      p1.addCard(new Card("green", 1));
      p2.addCard(new Card("yellow", 2));
      p2.addCard(new Card("green", 1));
      p2.addCard(new Card("white", 5));
      p3.addCard(new Card("empty", 0));
      p3.addCard(new Card("empty", 0));
      p3.addCard(new Card("empty", 0));
      players = new ArrayList<>();
      players.add(p1);
      players.add(p2);
      players.add(p3);
      JSONObject theMessage = p3.sendJson(players, center, 5, false, p);
      System.out.println("Our answer: " + theMessage.get("action") + ", The right answer: inform");
      System.out.println("Our answer for whom to inform: " + theMessage.get("player") + ", the right answer: 1");
      System.out.println("Our answer for the rank we're telling them about: " + theMessage.get("rank") + ", the right answer: 2");
  }

}
