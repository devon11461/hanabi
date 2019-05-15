package hanabi;
import java.util.*;
import org.json.simple.JSONObject;

/**
 * The board class is the main model for our system. It has many fields.
 * These fields include a DiscardPile, a container of players, the number of
 * fuses and information tokens in the game, the center piles (played cards),
 * Our client's ID, the turn time, the number of Players allowed in the game,
 * And several boolean fields and float
 * for the view to get coordinates to draw things.
 */
public class Board{
  private DiscardPile discardPile;
  private ArrayList<Player> players;
  private int fuse,infoTokens;
  private ArrayList<BoardSubscriber> subs = new ArrayList<>();

  //  **** red, white, green, blue, yellow **** (Order of the indices for colors)
  private ArrayList<Integer> centerPiles;
  private Server server;
  private int playerID;
  private int turnTime;
  private int numberOfPlayers;
  private boolean displayOptions = false;
  private boolean showDiscardPile = false;
  private int playersTurn;
  boolean myTurn = false;
  int playedPosition;
  float playX, playY;
  float discardX, discardY;
  float tellX, tellY;
  float seeDiscardX, seeDiscardY;
  float seeStatsX, seeStatsY;
  float aiX, aiY;
  float tellColorX, tellColorY;
  float tellNumX, tellNumY;
  float tellButtonWidth, tellButtonHeight;
  float buttonWidth, buttonHeight;
  boolean seeStats = false;
  private String message = "Please click a button on the bottom right side!";

  /**
   * This is a constructor of board , it will created a new board with
   * three parameters of Server, number of players, player turn time
   * and player id.
   *
   * @param S is a sever which represents a sever that we created.
   * @param numPlayers is an integer of numbers of players
   * @param returnTime is an integer of each player's turnTime
   * @param iD is an integer of player's id.   *
   */
  Board(Server s, int numPlayers, int returnTime, int iD){
    server = s;
    numberOfPlayers = numPlayers;
    turnTime = returnTime;
    playerID = iD;

    fuse = 3;
    infoTokens= 7;
    players = new ArrayList<>();
    discardPile = new DiscardPile();
    centerPiles = new ArrayList<>();
    for (int i = 0; i < 5; i++){
      centerPiles.add(0);
    }
  }

 /**
  * Purpose: to help get discard pile of DiscardPile type.
  * Return: the discard pile in the board
  * @return DiscardPile that contains all cards int he discard pile.
  */
  public DiscardPile getDiscardPile(){
    return this.discardPile;
  }

  /**
   * This method (toggleSeeStats) is responsible for changing the value of the
   * boolean that indicates if we should draw what the most probable card is
   * for our human users and BoardSubscribers.
   */
  public void toggleSeeStats(){
    System.out.println("Got here " + seeStats);
    this.seeStats = !this.seeStats;
    notifySubscribers();
  }
  /**
   * This is a getter to get all of the players in the game.
   * @return an arraylist of Player, containing all players in the game.
   */
  public ArrayList<Player> getPlayers(){
        return this.players;
  }
 /**
  * getPlayerID()
  * This is a getter for our player's ID.
  * @return an integer that is our client's ID.
  */
  public int getPlayerID(){
    return this.playerID;
  }

 /**
  * This is a getter for the number of players in the game.
  * @return an integer that is the numbe rof players in the game.
  */
  public int getNumberOfPlayers(){
    return this.numberOfPlayers;
  }

/**
  * This is a getter for the number of information tokens in the game
  * @return an integer that is the number of tokens in the game.
  */
  public int getNumTokens(){
    return this.infoTokens;
  }

   /**
    * This is a getter for the number of fuses in the game.
    * @return an integer that is the number of fuses in the game.
    */
  public int getNumFuses(){
    return this.fuse;
  }


   /**
    * This is a getter for the server.
    * @return an object of type Server that we are communicating with.
    */
  public Server getServerName(){
    return this.server;
  }

  /**
    * This is a getter for the length of each turn in the game.
    * @return an integer that is equal to the turn time in a game.
    */
  public int getTurnTime(){
    return this.turnTime;
  }

  /**
    * TThis is a getter for all views subscribed to this model.
    * @return An ArrayList that contains all views subscribed to this model.
    */
  public ArrayList<BoardSubscriber> getSubscribers(){
    return this.subs;
  }

  /**
    * This is a setter for our client's game ID.
    * @param id this is a variable that is sent in when the server sends us a list of cards. Our id is the position of the empty array of cards in the JSON. Between 1 and 5.
    */
  public void setId(int id){
    this.playerID = id;
    notifySubscribers();
  }

  /**
    * This is a getter for the center piles.
    * Order of indices reflects colors: Red, White, Green, Blue, Yellow (0,..,5)
    * @return An ArrayList containing the top value for each color.
    */
  public ArrayList<Integer> getPile(){
    return this.centerPiles;
  }

  /**
    * This method allows us to add a player to this model's list of players.
    * It also causes the view to redraw itself.
    * @param p a player that we wish to add to the model.
    */
  public void addPlayer(Player p){
    this.players.add(p);
    notifySubscribers();
  }

   /**
    * A method that is meant to detect if we clicked on the play button. will only
    * ever be called when it is our turn.
    * @param x the x coordinate from a mouse click.
    * @param y the y coordinate from a mouse click.
    * @return boolean true if we hit the play button, false otherwise.
    */
  public boolean hitPlay(float x, float y){
    return (x > this.playX && x < this.playX + buttonWidth && y > this.playY && y < this.playY+buttonHeight);
  }

  /**
   * A method that is meant to detect if we clicked on the play button. will only
   * ever be called when it is our turn.
   * @param x the x coordinate from a mouse click.
   * @param y the y coordinate from a mouse click.
   * @return boolean true if we hit the discard button, false otherwise.
   */
  public boolean hitDiscard(float x, float y){
    return (x > this.discardX && x < this.discardX + buttonWidth && y > this.discardY && y < this.discardY+buttonHeight);
  }

  /**
   * A method that is meant to detect if we clicked on the tell button. will only
   * ever be called when it is our turn.
   * @param x the x coordinate from a mouse click.
   * @param y the y coordinate from a mouse click.
   * @return boolean true if we hit the tell info button, false otherwise.
   */
  public boolean hitTell(float x, float y){
    return (x > this.tellX && x < this.tellX + buttonWidth && y > this.tellY && y < this.tellY+buttonHeight);
  }

  /**
   * A method that is meant to detect if we clicked on the seeStats button. will only
   * ever be called when it is our turn.
   * @param x the x coordinate from a mouse click.
   * @param y the y coordinate from a mouse click.
   * @return boolean true if we hit the see stats button, false otherwise.
   */
  public boolean hitStats(float x, float y){
    return (x > this.seeStatsX && x < this.seeStatsX + buttonWidth && y > this.seeStatsY && y < this.seeStatsY+buttonHeight);
  }

  /**
   * A method that is meant to detect if we clicked on the seeDiscard button. will only
   * ever be called when it is our turn.
   * @param x the x coordinate from a mouse click.
   * @param y the y coordinate from a mouse click.
   * @return boolean true if we hit the see discard pile button, false otherwise.
   */
  public boolean hitSeeDiscard(float x, float y){
    return (x > this.seeDiscardX && x < this.seeDiscardX + buttonWidth && y > this.seeDiscardY && y < this.seeDiscardY+buttonHeight);
  }

  /**
   * A method that is meant to detect if we clicked on the AIMode button. will only
   * ever be called when it is our turn.
   * @param x the x coordinate from a mouse click.
   * @param y the y coordinate from a mouse click.
   * @return boolean true if we hit the Ai mode button, false otherwise.
   */
  public boolean hitAI(float x, float y){
    return (x > this.aiX && x < this.aiX + buttonWidth && y > this.aiY && y < this.aiY+buttonHeight);
  }

  /**
   * A method that is meant to detect if we clicked on the Tell Color button. will only
   * ever be called when it is our turn.
   * @param x the x coordinate from a mouse click.
   * @param y the y coordinate from a mouse click.
   * @return boolean true if we hit the tell color button, false otherwise.
   */
  public boolean hitTellColor(float x, float y){
    return (x > this.tellColorX && x < this.tellColorX + tellButtonWidth && y > this.tellColorY && y < this.tellColorY+tellButtonHeight);
  }

  /**
   * A method that is meant to detect if we clicked on the TellNum button. will only
   * ever be called when it is our turn.
   * @param x the x coordinate from a mouse click.
   * @param y the y coordinate from a mouse click.
   * @return boolean true if we hit the tell number button, false otherwise.
   */
  public boolean hitTellNum(float x, float y){
    return (x > this.tellNumX && x < this.tellNumX + tellButtonWidth && y > this.tellNumY && y < this.tellNumY+tellButtonHeight);
  }


  /**
   * A method that will be called whenever we need to add a card to any players hand.
   * <p>
   * @param playerId an integer representing the player id to get the player's hand that we're adding the card to.
   * @param c a Card that we are adding to player (playerId)'s hand.
   */
  public void addCard (int playerId, Card c){
    for (Player p : players){
      if (p.getId() == playerId){
        p.addCard(c);
        notifySubscribers();
        return;
      }
    }
    return;
  }

  /**
   * A method that will be called whenever we need to remove a card to any players hand.
   * <p>
   * @param playerId an integer representing the player id to get the player's hand that we're removing the card from.
   * @param c a Card that we are removing from player (playerId)'s hand.
   */
  public void removeCard(int playerId, Card c){
    for (Player p : players){
      if (p.getId() == playerId){
        p.removeCard(c);
        notifySubscribers();
        return;
      }
    }
  }

  /**
   * A method that will be called whenever we need to check if we clicked on a card
   * in our own hand.
   * <p>
   * @param x The x-coordinate of a mouse click.
   * @param y The y-coordinate of a mouse click.
   * @return an integer between 0 and 4 if we hit one of our own cards, or -1 if we hit no cards.
   */
  public int getMyHitIndex(float x, float y){
    for (Player p : this.players){
      if (p.getId() == this.playerID){
        for (int i = 0; i < p.getHand().getHand().size(); i++){
          if (p.getHand().getHand().get(i).xCorner < x && x < p.getHand().getHand().get(i).xCorner+p.getHand().getWidth()){
            if (p.getHand().getHand().get(i).yCorner < y && y < p.getHand().getHand().get(i).yCorner+p.getHand().getHeight()){
              return i;
            }
          }
        }
      }
    }
    return -1;
  }

  /**
   * A method that will be called whenever we need to determine which
   * player we clicked on (determined by the card in the hand we clicked on)
   * <p>
   * @param x The x-coordinate of a mouse click.
   * @param y The y-coordinate of a mouse click.
   * @return an integer between 1 and numPlayers indicating which player we interacted with. If we didn't click on another player's card we return -1.
   */
  public int getHitPlayer(float x, float y){
    for (Player p : this.players){
      if (p.getHand().checkHit(x, y)) {
          return p.getId();
      }
    }
    return -1;
  }

  /**
   * A method that will be called whenever we need to determine which card we clicked
   * on. This method will only be called if we confirm that we clicked on another player's
   * card (after the getHitPlayer(x, y) is called and returned successfully). This means that we should never return null.
   * <p>
   * @param x The x-coordinate of a mouse click.
   * @param y The y-coordinate of a mouse click.
   * @return A Card c, which contains the values of the card we clicked on. Should never return null.
   */
  public Card getHitCard(float x, float y){
    for (Player p : this.players){
      if (p.getId() != this.playerID){
        for (Card c : p.getHand().getHand()){
          if (c.xCorner < x && x < c.xCorner + p.getHand().getWidth()){
            if (c.yCorner < y && y < c.yCorner + p.getHand().getHeight()){
              return c;
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * A method that will be called whenever we need to determine which
   * player we clicked on (determined by the card in the hand we clicked on)
   * <p>
   * @param option a boolean to determine if we should display the buttons a user must click on to tell another user about a number of color.
   * true if we should, false otherwise.
   */
  public void setDisplayOptions(boolean option){
    this.displayOptions = option;
    notifySubscribers();
  }

  /**
   * A getter method that will return the value of displayOptions.
   * @return a boolean, true if we should display the buttons tell Number and tell Color, false otherwise.
   */
  public boolean getDisplayOptions(){
    return this.displayOptions;
  }

  /**
   * A method that will add board subscribers to our list of subscribers. Will allow
   * us to notify the subscribed view.
   * @param s a BoardSubscriber that we will add to our ArrayList of subscribers.
   */
  public void addSubscriber(BoardSubscriber s){
    subs.add(s);
  }

  /**
   * A method that will notify our subsribers that our model has changed.
   * <p> Will occur whenver we add or remove anything but a subscriber from the model.
   */
  public void notifySubscribers(){
    for (BoardSubscriber s : subs){
      s.modelChanged();
    }
  }

  /**
   * A method that constructs a json message given information for a move.
   * @param action String that contains 'play', 'discard', or 'tell'.
   * @param playerId int player id that we wish to tell info to.
   * @param color String color of the info we wish to tell.
   * @param index index position of the card we wish to play/discard.
   * @param number rank of the card we wish to tell info about.
   * @return JSONObject that contains info about the action we wish to do.
   */
  public JSONObject constructJSON(String action, int playerId, String color, int index, int number){
    for (Player p : players){
      if (p.getId() == this.playerID){
        return p.constructJSON(action, playerId, color, index, number);
      }
    }
    return null;
  }

  /**
   * A getter method that will return the value of getMessage()
   * @return A string that contains the message we wish to tell our client.
   */
  public String getMessage(){
    return message;
  }

  /**
   * A setter method that will return the value of message. Causes all subscribed
   * views to redraw.
   * @param newMessage String that we wish our model's message to be changed to.
   */
  public void setMessage(String newMessage){
    message = newMessage;
    notifySubscribers();
  }

  /**
   * A getter method that will change the boolean value of showDiscardPile.
   * Will cause all subscribed views to redraw.
   */
  public void toggleDiscardPile(){
    this.showDiscardPile = !this.showDiscardPile;
    notifySubscribers();
  }

  /**
   * A getter method that will return the value of showDiscardPile
   * @return Boolean, true if we should show the discard pile, false otherwise.
   */
  public boolean getSeeDiscard(){
    return this.showDiscardPile;
  }

  /**
   * addToDiscardPile(Card) is a method that takes in a card and adds it
   * to the cards discard pile.
   * @param c Card that we wish to add to the discard pile.
   */
  public void addToDiscardPile(Card c){
    this.discardPile.recieveCard(c);
    notifySubscribers();
  }

  /**
   * A method that will cause our board to decrement a fuse.
   * This method will be called whenveer the server we're connected to
   * sends us a 'burned' fuse reply.
   * It will cause all subscribed views to redraw.
   */
  public void loseAFuse(){
    this.fuse--;
    notifySubscribers();
  }

  /**
   * An decrementer for the number of information tokens our model has stored.
   * This method will be called whenever one of our teammates (or us) tells another information.
   * It will cause all subscribed views to redraw.
   */
  public void loseAToken(){
    this.infoTokens--;
    notifySubscribers();
  }

  /**
   * An incrementer for the number of information tokens our model has stored.
   * This method will be called whenever one of our teammates (or us) discards a card.
   * It will cause all subscribed views to redraw.
   */
  public void gainAToken(){
    this.infoTokens++;
    notifySubscribers();
  }

  /**
   * This method will be called whenver the server notifies us that it is our turn.
   * If our player is an AI, it will start the AI's process. If our player is a human,
   * it will activate the controller.
   */
  public void sendJSON(){
    for (Player p : players){
      if (p.getId() == this.playerID){
        JSONObject theMessage = p.sendJson(players, centerPiles, infoTokens, false, discardPile);
        if (theMessage != null){
          String action = theMessage.get("action").toString();
          if (action.equals("inform")){
            this.sendInformRankJson((int)theMessage.get("player"), (int)theMessage.get("rank"));
          } else if (action.equals("play")){
            this.sendPlayJson((int)theMessage.get("position"));
          } else if (action.equals("discard")){
            this.sendDiscardJson((int)theMessage.get("position"));
          }
        }
        else {
          myTurn = true;
        }
      }
    }
  }

  /**
   * This method is responsible for sending a json message to our server
   * when our human users click on 'AI Mode'. It's the second
   * method called in a method chain when the AI mode button
   * is pressed. This method essentially allows an AI
   * to complete one move.
   * @param obj a JSONObject that contains information about the move our AI has
   *            determined is best.
   */
  public void sendJSONAIMode(JSONObject obj){
    String action = obj.get("action").toString();
    if (action.equals("inform")){
      this.sendInformRankJson((int)obj.get("player"), (int)obj.get("rank"));
    } else if (action.equals("play")){
      this.sendPlayJson((int)obj.get("position"));
    } else if (action.equals("discard")){
      this.sendDiscardJson((int)obj.get("position"));
    }
  }

    /**
     * sendInformRankJson(int, int) is a method responsible for telling the Server field
     * to send a json containing information about informing a player about a rank.
     * @param player the ID of the player we wish to tell Information
     * @param rank the rank that we wish to tell Player player about.
     */
    public void sendInformRankJson(int player, int rank){
      for (Player p : players){
        if (p.getId() == player){
          for (Card c : p.getHand().getHand()){
            if (c.getNumber() == rank){
              c.setKnowNumber();
            }
          }
        }
      }
      this.infoTokens--;
      notifySubscribers();
      this.server.doInformRankAction(player, rank);
    }

    /**
     * sendInformSuiteJson(int, String) is a method responsible for telling the Server field
     * to send a json containing information about informing a player about a suit.
     * @param player the ID of the player we wish to tell Information
     * @param suite the color that we wish to tell Player player about.
     */
    public void sendInformSuiteJson(int player, String suite){
      for (Player p : players){
        if (p.getId() == player){
          for (Card c : p.getHand().getHand()){
            if (c.getColor().equals(suite)){
              c.setKnowColor();
            }
          }
        }
      }
      this.infoTokens--;
      notifySubscribers();
      this.server.doInformSuiteAction(player, suite);
    }

    /**
     * sendPlayJson(int) is a method responsible for telling the Server field
     * to send a json containing information about playing a certain position in our hand.
     * @param position the index of the card in our hand that we wish to inform the server about.
     */
    public void sendPlayJson(int position){
      this.server.doPlayDiscardAction("play", position+1);
    }

    /**
     * sendDiscardJson(int) is a method responsible for telling the Server field
     * to send a json containing information about discarding a certain position in our hand.
     * @param position the index of the card in our hand that we wish to inform the server about.
     */
    public void sendDiscardJson(int position){
      Player p = this.getPlayerById(getPlayerID());
      Card c = p.getHand().getHand().get(position);
      c.setColor("empty");
      c.setNumber(0);
      this.server.doPlayDiscardAction("discard", position+1);
    }

    /**
     * setPlayersTurn() is a method responsible for maintaining which playerID's turn it is.
     * Causes view to redraw. Every time this method is called, the value of playersTurn
     * is incremented by 1. If the value of the number stored in this field is grater than or equal to
     * the total players in the game, this value is reset to 0. The Player Id that corresponds
     * to this number is playersTurn+1. We keep this value one lower because we are worrying about
     * the index of the player.
     */
    public void setPlayersTurn(){
      this.playersTurn++;
      if (this.playersTurn >= this.numberOfPlayers){
        this.playersTurn = 0;
      }
      notifySubscribers();
    }

    /**
     * sgetPlayersTurn() This method gets who's turn it is , as an index.
     * @return Integer that represents who's turn it is (as an index, which will be one lower than the player's ID).
     */
    public int getPlayersTurn(){
      return this.playersTurn;
    }

    /**
     * A method responsible for determining if a card can be played.
     * @param c Card that we wish to see can be immediately played.
     * @return a boolean value, true if the card can currently be played and false otherwise.
     */
    public boolean canPlay(Card c){
      String color = c.getColor();
      int pileIndex = mapColor(color);
      System.out.println(color);
      if (color.equals("empty")){
        return false;
      }
      if (centerPiles.get(pileIndex) == c.getNumber()-1){
        System.out.println("got here");
        centerPiles.set(pileIndex, centerPiles.get(pileIndex)+1);
        notifySubscribers();
        return true;
      } else {
        return false;
      }
    }

    /**
     * A method responsible for determining an integer value for the color given.
     * This method will be used when we check the center values. The reason for this is
     * because we need index values for the centerPiles values.
     * @param color String that is a color. The value of this string must be "red", "white", "green", "blue", "yellow" or "rainbow".
     * @return an integer value that maps to an index in centerPiles. If none of the above colors are given as inputs, this  method returns -1.
     */
    public int mapColor(String color){
      if (color.equals("red")){
        return 0;
      }
      if (color.equals("white")){
        return 1;
      }
      if (color.equals("green")){
        return 2;
      }
      if (color.equals("blue")){
        return 3;
      }
      if (color.equals("yellow")){
        return 4;
      }
      if (color.equals("rainbow")){
        return 5;
      }
      return -1;
    }

    /**
     * A method responsible for getting a specific player by their PlayerID.
     * @param id Integer that is a players ID.
     * @return a Player p with an ID id. Null if no such player exists (Should never happen).
     */
    public Player getPlayerById(int id){
      for (Player p : this.players){
        if (p.getId() == id){
          return p;
        }
      }
      return null;
    }

    /**
     * A method responsible for removing a card from a players hand at a given index.
     * Causes view to redraw.
     * The player's card that is in the given index will be removed.
     * @param playerId Integer that is the players ID. greater than or equal to 1, less than or equal to 5.
     * @param index the index value (0-5 or 0-4, depending on how many players are in the game) for the card we wish to remove.
     */
    public void removeCard(int playerId, int index){
      for (Player p : this.players){
        if (p.getId() == playerId){
          p.removeCard(index);
          notifySubscribers();
        }
      }
    }

    /**
     * calcScore() a method responsible for calculating the score. Will be used
     * when the game ends to calculate the team's score.
     * @return Integer value of the summation of total cards played and accepted.
     */
    public int calcScore(){
      int total = 0;
      for (int i : centerPiles){
        total += i;
      }
      return total;
    }

    /**
     * doAIMove is the method that is directly called when we click on the
     * AI Mode button. This method temeporarily creates an AIPlayer with the same
     * ID as our human user and has it calculate the move that it would perform at
     * the current stage of the game.
     * This method then sets the user's turn to false and indicates that the user must wait
     * for their turn again.
     */
    public void doAIMove(){
      AIPlayer aiPlayer = new AIPlayer(getPlayerID());
      JSONObject obj = aiPlayer.sendJson(players, centerPiles, infoTokens, false, discardPile);
      this.sendJSONAIMode(obj);
      this.myTurn = false;
      this.setMessage("Please wait for your turn!");
    }

    /**
     * correctPlayers(int) adds players to the game depending on how any
     * extra players we need to know in the join message. Only gets called
     * when the game we're joining/creating has 3+ players.
     * @param diff integer that is the difference between how many players are in the game and 2.
     */
    public void correctPlayers(int diff){
      int maxId = this.players.size();
      for (int i = 0; i < diff; i++){
        this.addPlayer(new HumanPlayer(maxId+i+1));
      }
      this.numberOfPlayers += diff;
    }
  }
