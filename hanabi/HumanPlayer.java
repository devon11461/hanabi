package hanabi;
import org.json.simple.JSONObject;

import java.util.*;
import org.json.simple.JSONObject;

/**
 * HumanPlayers will be instances of human players connecting to the server.
 * All human players must have a view drawn, a hand and an ID. 
 */
public class HumanPlayer implements Player {
  private int playerID;
  private Hand hand;

  /**
   * The constructor
   * @param id  the id of the player
   */
  HumanPlayer(int id){
    this.playerID = id;
    this.hand = new Hand();
  }

  /**
   * getHand() getter for returning the players hand.
   * @return Hand object that is the hand of the player.
   */
  public Hand getHand(){
    return this.hand;
  }

  /**
   * getId() getter for returning the players id.
   * @return int object that is the id of the player.
   */
  public int getId(){
    return this.playerID;
  }


  /**
   * addCard(Card) is a method that adds a card to the user's hand.
   * @param c Card object that will be added to the user's hand.
   */
  public void addCard(Card c){
    this.hand.addCard(c);
  }


  /**
   * removeCard(Card) is a method that removes a card from the user's hand.
   * @param c Card object that will be removed from the user's hand.
   */
  public void removeCard(Card c){
    this.hand.removeCard(c);
  }

  /**
   * removeCard(int) is a method that removes a card from the user's hand given the index of the card.
   * @param index  int that is the index of the card we wish to remove.
   */
  public void removeCard(int index){
    this.hand.getHand().remove(index);
  }

 /**
  * Method that creates a JSONObject that contains information about the move we
  * intend to do.
  * @param action String that is "play", "tell", "discard".
  * @param playerId id of the player we wish to tell information. Will not be used
  *                 by play or discard actions.
  * @param color String that contains information about telling another player of a color.
  *              value must be "yellow", "blue", "red", "green", "white", or "rainbow"
  * @param index int the index of the card that we wish to play or discard. Only used by
  *              play or discard actions.
  * @param number int that contains information about tellin gantoher player of an integer.
  * @return JSONObject that contains information about playing, discarding or telling information.
  */
  public JSONObject constructJSON(String action, int playerId, String color, int index, int number){
    switch (action){
      case "play":
        return this.constructPlayJSON(index);
      case "tell":
        return (this.constructTellJSON(playerId, number, color));
      case "discard":
        return (this.constructDiscardJSON(index));
      default:
        return null;
    }
  }

  /**
   * A method responsible for constructing an object containing information
   * about playing a card at a certain index.
   * @param index Integer that is the index of the card we want to play.
   * @return JSONObject that contains fields action and position.
   */
  @SuppressWarnings("unchecked")
  public JSONObject constructPlayJSON(int index){
    JSONObject obj = new JSONObject();
    obj.put("action", "play");
    obj.put("position", index);
    return obj;
  }

  /**
   * constructTellJSON(int, int, String) a method that constructs a JSONObject
   * given details about telling information. One of paramaters number or color must be
   * 0 or "empty", respsectively. If neither is empty, assumes telling about a rank.
   * @param playerId Int the id of the player we wish to tell info to.
   * @param number int the rank that we wish to tell a player about.
   * @param color Strin the color that we wish to tell a player about.
   * @return JSONObject containing information about telling information about
   */
  @SuppressWarnings("unchecked")
  public JSONObject constructTellJSON(int playerId, int number, String color){
    JSONObject obj = new JSONObject();
    obj.put("action", "inform");
    if (number != 0){
      obj.put("rank", number);
    } else {
      obj.put("suite", color);
    }
    obj.put("player", playerId);
    return obj;
    }

    /**
     * constructDiscardJSON(int) a method that constructs a message about
     * sending a discard message.
     * @param index Integer that is the index of the card we wish to discard.
     * @return JSONObject containing information about discarding a card.
     */
    @SuppressWarnings("unchecked")
    public JSONObject constructDiscardJSON(int index){
      JSONObject obj = new JSONObject();
      obj.put("action", "discard");
      obj.put("position", index);
      return obj;
    }

    /**
     * sendJSON(ArrayList, ArrayList, int, boolean, discardPile)
     * This method returns null for HumanPlayers. It is our way to determine if we
     * are a human or not.
     * @param players ArrayList of players containing all players in the game.
     * @param centerPile ArrayList of integers that contains the piles of played cards in the game.
     * @param informationTokens integer that is the number of info. tokens remaining in the game.
     * @param rainbow boolean that is true if we're in rainbow mode, false otherwise.
     * @param discard DiscardPile object from the model.
     */
    public JSONObject sendJson(ArrayList<Player> players, ArrayList<Integer> centerPile, int informationTokens, boolean rainbow, DiscardPile discard){
        return null;
    }
  }
