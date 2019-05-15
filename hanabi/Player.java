package hanabi;

import java.util.*;
import org.json.simple.JSONObject;


/**
 * Interface for players joining our game. AIPlayers and HumanPlayers will both
 * Implement this.
 */
public interface Player{

  public JSONObject sendJson(ArrayList<Player> players, ArrayList<Integer> centerPile, int informationTokens, boolean rainbow, DiscardPile discard);

  public Hand getHand();

  public int getId();

  public void addCard(Card c);

  public void removeCard(Card c);

  public void removeCard(int index);
  public JSONObject constructJSON(String action, int playerId, String color, int index, int number);
}
