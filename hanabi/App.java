package hanabi;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.util.*;
import com.google.gson.*;

/**
 * App is the entry point for our users.
 * To create a game, need tags -q (int), -f/-c, -n 'nsid', -k 'secret', -p (#players)
 * To join a game, need tags -t 'token' -i (# game id) -n 'nsid' -k 'secret' -p (#players) -j
 */
public class App extends Application {
  @Override
  public void start(Stage primaryStage){
    Parameters theArgs = getParameters();
    List<String> allParams = theArgs.getRaw();
    String action = "";
    int timeout = 0;
    String nsid = "";
    String secret = "";
    boolean force = false;
    String token = "";
    int numPlayers = 2;
    int id = 0;
    int playedPosition;
    Server s = new Server();
    boolean ai = false;
    for (int i = 0; i < allParams.size(); i++){
      switch (allParams.get(i)){
        case "-c":
          action = "create";
          force = false;
          break;
        case "-f":
          action = "create";
          force = true;
          break;
        case "-p":
          i+=1;
          numPlayers = Integer.parseInt(allParams.get(i));
          break;
        case "-k":
          i+=1;
          secret = allParams.get(i);
          break;
        case "-n":
          i+=1;
          nsid = allParams.get(i);
          break;
        case "-q":
          i += 1;
          timeout = Integer.parseInt(allParams.get(i));
          break;
        case "-t":
          i += 1;
          token = allParams.get(i);
          break;
        case "-j":
          action = "join";
          break;
        case "-i":
          i += 1;
          id = Integer.parseInt(allParams.get(i));
          break;
        case "-a":
          ai = true;
          break;
      }
    }

    if (action.equals("create")){
      s.sendCreate(nsid, numPlayers, timeout, force, secret);
      s.printRecievedMessage();
    } else {
      s.doJoin(nsid, id, token, secret);
      s.printRecievedMessage();
    }
    final Board model;
    if (!ai){
      HBox root = new HBox();
      primaryStage.setTitle("hanabi");
      primaryStage.setScene(new Scene(root, 800, 800));
      View theView = new View(800, 800);
      root.getChildren().add(theView);
      primaryStage.show();
      model = new Board(s, numPlayers, timeout, 1);
      for (int i = 1; i <= numPlayers; i++){
        model.addPlayer(new HumanPlayer(i));
      }
      Controller controller = new Controller();
      controller.setModel(model);
      theView.setModel(model);
      theView.onDraw();
      theView.setOnMousePressed(controller::handlePressed);
      model.addSubscriber(theView);
    } else {
      model = new Board(s, numPlayers, timeout, 1);
      for (int i = 1; i <= numPlayers; i++){
        model.addPlayer(new AIPlayer(i));
      }
    }


    final int finalNumPlayers = numPlayers;
    Thread one=new Thread(){
      /**
       * A method responsible for dealing with incoming JSON messages from the server.
       * runs on a separate thread.
       */
      public void run(){
        try {
          String theMessage = s.printRecievedMessage();
          model.setMessage("Waiting for players to join!");
          while (!theMessage.contains("game starts")){ // substring(11, 22).equals("game starts")) {
            theMessage = s.printRecievedMessage();
          }
          int numberOfPlayers = calcNumPlayers(theMessage);
          System.out.println(numberOfPlayers);
          if (numberOfPlayers > finalNumPlayers){
            model.correctPlayers(numberOfPlayers - finalNumPlayers);
          }
          handleDrawCards(theMessage, numberOfPlayers, model);
          while (!theMessage.contains("game ends") && !theMessage.contains("game cancelled")){ //substring(11, 20).equals("game ends")){
            theMessage = s.printRecievedMessage();
            if (theMessage.contains("your move")){ //substring(10, 19).equals("your move")) {
              System.out.println("Who's turn is it?: " + model.getPlayersTurn());
              model.setMessage("Your turn! Please click on one of the buttons in the corner!");
              model.sendJSON();
              //yourMove(model);
            } else if (theMessage.contains("notice")){
              handleNotice(model, theMessage);
            } else if (theMessage.contains("reply")){
              handleReply(model, theMessage);
            }
          }
          model.setMessage("Game is over! Thanks for playing! Your score was: " + model.calcScore());
          System.out.println("final score: " + model.calcScore());
        } catch (Exception e){
          System.out.println("Not working");
        }
      }
    };
    one.start();
  }

  public static void main(String[] args){
    launch(args);
  }

  /**
   * A message that gets a message and prints it. Then returns it.
   * @param s Server object that we're recieving messages from.
   * @return String that is the json message from the server.
   */
  public String waitForMessage(Server s){
    return s.printRecievedMessage();
  }

  /**
   * A method that takes in a message from the server, the number of players in
   * the game and the model our client is using. It then adds cards to each player's
   * hand.
   * @param s String s that is the message containing all players' cards.
   * @param numPlayers int the number of players in the game.
   * @param model Board object that is the model we're connected to.
   */
  public void handleDrawCards(String s, int numPlayers, Board model){
    System.out.println(numPlayers);
    int i = 33;
    int finalN = 0;
    int walker = 33;
    for (int player = 1; player <= numPlayers; player++){
      i = walker;
      walker++;
      while (!s.substring(walker, walker+1).equals("]")){
        walker++;
      }
      walker += 1;
      finalN = walker;
      String array = s.substring(i, finalN);
      if (s.substring(i, finalN).length() == 3){
        i++;
        array = s.substring(i, finalN);
      }
      System.out.println(s.substring(i, finalN) + " length: " + s.substring(i, finalN).length());
      initializePlayerHand(array, player, model);
    }
    model.setMessage("Wait for your turn!");
    model.notifySubscribers();
  }

  /**
   * Helper function for handleDrawCards(...). Takes one player's cards and adds them
   * to the player's hand.
   * @param s String s containing a message of which cards are in the player's hand.
   * @param player int Id of the player.
   * @param model Board object that our client is using.
   */
  private void initializePlayerHand(String s, int player, Board model){
    int i = 0;
    String color = "";
    if (s.length() == 2){
      System.out.println("Got here!");
      model.setId(player);
      int howMany = model.getNumberOfPlayers();
      if (howMany < 4){
        howMany = 5;
      } else {
        howMany = 4;
      }
      for (int count = 0; count < howMany; count++){
        model.addCard(player, new Card("empty", 0));
      }
    } else{
      while (i < s.length()){
        if (!getColor(s.substring(i, i+1)).equals("")){
          model.addCard(player, new Card(getColor(s.substring(i, i+1)), Integer.parseInt(s.substring(i+1, i+2))));
        }
        i++;
      }
    }
  }

  /**
   * A method responsible for mapping a given letter to a color.
   * @param s String object that is one letter. Values are w, y, b, g, or r.
   * @return String s that is a lower-case color. Empty string if argument is unacceptable.
   */
  public String getColor(String s){
    switch (s){
      case "y":
        return "yellow";
      case "b":
        return "blue";
      case "g":
        return "green";
      case "r":
        return "red";
      case "w":
        return "white";
      default:
        return "";
    }
  }

  /**
   * A method responsible for activating a human player's controller.
   * @param model Board object that is the model our client is using.
   */
  public void yourMove(Board model){
    model.myTurn = true;
  }

  /**
   * A method that is called whenever a player in our game makes a poor move.
   * decrements fuses left in the game.
   * @param model Board object that is our client's model.
   */
  public void handleBurned(Board model){
    model.loseAFuse();
  }

  /**
   * A method responsible for handling an incoming notice json message from the server.
   * @param model Board object that is our client's model.
   * @param message String that is a string representation of a JSON message.
   */
  public void handleNotice(Board model, String message){
    if (message.contains("played")){
      int index = Integer.parseInt(message.substring(message.indexOf("position")+10, message.indexOf("position")+11));
      System.out.println(index + " = their played index");
      System.out.println("Players turn: " + model.getPlayersTurn());
      if (model.canPlay(model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(index-1))) {
          System.out.println("We got that we can play the card.");
      } else {
        model.loseAFuse();
        Card holdCard = model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(index-1);
        model.addToDiscardPile(new Card(holdCard.getColor(), holdCard.getNumber()));
      }
      model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(index-1).setColor(getColor(message.substring(message.indexOf("card")+7, message.indexOf("card")+8)));
      model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(index-1).setNumber(Integer.parseInt(message.substring(message.indexOf("card")+8, message.indexOf("card")+9)));
      model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(index-1).setKnowsFalse();
      System.out.println(Integer.parseInt(message.substring(message.indexOf("card")+8, message.indexOf("card")+9)));

      model.setPlayersTurn();
    }
    else if (message.contains("inform")){
      int playerId = Integer.parseInt(message.substring(message.indexOf("player")+8, message.indexOf("player")+9));
      Player p = model.getPlayerById(playerId);
      for (Card c : p.getHand().getHand()){
        if (message.contains("rank")){
          int theRank = Integer.parseInt(message.substring(message.indexOf("rank")+6, message.indexOf("rank")+7));
          if (c.getNumber() == theRank){
            c.setKnowNumber();
          }
        } else {
          String color = getColor(message.substring(message.indexOf("suit")+6, message.indexOf("suit")+7));
          if (c.getColor().equals(color)){
            c.setKnowColor();
          }
        }
      }
      model.setPlayersTurn();
    }
  }

  /**
   * Method responsible for handling an incoming reply message from the server.
   * @param model Board object that is our client's model.
   * @param message Incoming JSON message as a string from the server.
   */
  public void handleReply(Board model, String message){
    if (message.contains("burned")){
      model.addToDiscardPile(new Card(getColor(message.substring(message.indexOf("card")+7, message.indexOf("card")+8)), Integer.parseInt(message.substring(message.indexOf("card")+8, message.indexOf("card")+9))));
      model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(model.playedPosition).setColor("empty");
      model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(model.playedPosition).setNumber(0);
      model.setPlayersTurn();
      model.loseAFuse();
    } else if (message.contains("built")){
      String color = getColor(message.substring(message.indexOf("card")+7, message.indexOf("card")+8));
      int pileIndex = model.mapColor(color);
      model.getPile().set(pileIndex, model.getPile().get(pileIndex)+1);
      model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(model.playedPosition).setColor("empty");
      model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(model.playedPosition).setNumber(0);
      model.getPlayerById(model.getPlayersTurn()+1).getHand().getHand().get(model.playedPosition).setKnowsFalse();
      model.setPlayersTurn();

    } else if (message.contains("invalid")){
      model.gainAToken();
    } else if (message.contains("inform")){
      if (message.contains("rank")){
        int rank = Integer.parseInt(message.substring(message.indexOf("rank")+6, message.indexOf("rank")+7));
        String arrayMessage = message.substring(message.indexOf("info", 18)+7, message.length()-1);
        int index = -1;
        for (int i = 0; i < arrayMessage.length(); i++){
          if (arrayMessage.substring(i, i+1).equals("f")){
            index++;
          } if (arrayMessage.substring(i, i+1).equals("t")){
            index++;
            for (Player p : model.getPlayers()){
              if (p.getId() == model.getPlayerID()){
                p.getHand().getHand().get(index).setKnowNumber();
                p.getHand().getHand().get(index).setNumber(rank);
              }
            }
          }
        }
      }
      else if (message.contains("suit")){
        String color = getColor(message.substring(message.indexOf("suit")+7, message.indexOf("suit")+8));
        String arrayMessage = message.substring(message.indexOf("info", 18)+7, message.length()-1);
        int index = -1;
        System.out.println(arrayMessage);
        for (int i = 0; i < arrayMessage.length(); i++){
          if (arrayMessage.substring(i, i+1).equals("f")){
            index++;
          } if (arrayMessage.substring(i, i+1).equals("t")){
            index++;
            for (Player p : model.getPlayers()){
              if (p.getId() == model.getPlayerID()){
                p.getHand().getHand().get(index).setKnowColor();
                p.getHand().getHand().get(index).setColor(color);
              }
            }
          }
        }
      }
      model.loseAToken();
      model.setPlayersTurn();
    } else if (message.contains("discarded")){
      int position = Integer.parseInt(message.substring(message.indexOf("position")+10, message.indexOf("position")+11));
      model.gainAToken();
      for (Player p : model.getPlayers()){
        if (p.getId() == model.getPlayersTurn()+1){
          Card c = p.getHand().getHand().get(position-1);
          model.addToDiscardPile(new Card(c.getColor(), c.getNumber()));
          if (!message.contains("NONE")){
            String newColor = getColor(message.substring(message.indexOf("card", 30)+7, message.indexOf("card", 30)+8));
            int newNumber = Integer.parseInt(message.substring(message.indexOf("card", 30)+8, message.indexOf("card", 30)+9));
            System.out.println("newNumber = " + newNumber + " newColor = " + newColor);
            p.getHand().getHand().get(position-1).setColor(newColor);
            p.getHand().getHand().get(position-1).setNumber(newNumber);
            p.getHand().getHand().get(position-1).setKnowsFalse();
          } else {
            p.getHand().getHand().get(position-1).setColor("empty");
            p.getHand().getHand().get(position-1).setNumber(0);
            p.getHand().getHand().get(position-1).setKnowsFalse();
          }
        }
      }
      model.setPlayersTurn();
    } else if (message.contains("accepted")){
      if (message.contains("card")){
        String color = getColor(message.substring(message.indexOf("card")+7, message.indexOf("card")+8));
        int number =  Integer.parseInt(message.substring(message.indexOf("card")+8, message.indexOf("card")+9));
        model.addToDiscardPile(new Card(color, number));
        model.gainAToken();
      }
        model.setPlayersTurn();
      }
    }

    /**
     * calcNumPlayers(String) is a method that calculates how many players are in the game
     * given the dealing cards message from the server.
     * @param message String with the cards being dealt to players.
     * @return integer that is the number of players in the game. 
     */
    public int calcNumPlayers(String message){
      int total = -1;
      for (int i = 0; i < message.length(); i++){
        if (message.charAt(i) == '['){
          total++;
        }
      }
      return total;
    }
  }
