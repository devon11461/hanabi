package hanabi;

import java.net.Socket;
import org.json.simple.*;
import java.io.*;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;
import java.time.Instant;
import java.math.*;
import com.google.gson.*;
import java.util.*;
import javax.*;

/**
 * Server is a class responsible for communicating with the server our client is
 * communicating with. Contains  everything needed to talk to a server (printwriter, input and output stream, socket) ,
 * and a model to interact with.
 *
 */
public class Server{
  Socket ourConnection;
  Board model;
  PrintWriter pw;
  InputStream is;
  InputStreamReader isr;
  BufferedReader br;

  @SuppressWarnings("unchecked")
  Server(){
    try {
      ourConnection = new Socket("GPU2.USASK.CA", 10219);
      //System.out.println(message);
      /**
       *Send a create-a-game message
       */
      OutputStream os = ourConnection.getOutputStream();
      //OutputStreamWriter osw = new OutputStreamWriter(os);
      pw = new PrintWriter(os);
      //sendCreate("drp882", 2, 60, true, "6ba5a2868964dd7e52be54d935c743e0");
      //pw.flush();

      is = ourConnection.getInputStream();
      isr = new InputStreamReader(is);
      br = new BufferedReader(isr);

      // String theMessage = "";
      // char theChar;
      // while ((theChar = (char)br.read()) != '}'){
      //   theMessage += theChar;
      // }
      // theMessage += '}';
      // System.out.println(theMessage);
      // ourConnection.close();

    }

    catch (Exception e){
      System.out.println("We did not connect");
    }
  }

  /**
   *  setBoard(Board) sets a model to the server to interact with.
   * @param theModel Board object that we wish to interact with.
   */
  public void setBoard(Board theModel){
    this.model = theModel;
  }

  /**
   * sendJSON(String) a method that was used for testing, prints out json messages.
   * @param message String that is the players message.
   */
  public void sendJSON(String message){
    System.out.println(message);
  }

  public static void main(String[] args){
    Server hold = new Server();
  }

  /**
   * computeHash(string) a method given to us for this course.
   * computes the md5hash of a json message.
   * @param msg String that is a json message we wish to hash.
   */
  private static String computeHash(String msg){
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(msg.getBytes());
      return new BigInteger(1, md.digest()).toString(16);
    } catch (Exception e){
      System.out.println("MD5 failed");
      return "";
    }
  }

  /**
   * CreateJson intenal class only seen and used by Server. java. this class
   * contains information aboutcreating jsons.
   */
  class CreateJson{
    String cmd;
    String nsid;
    int players;
    int timeout;
    boolean force;
    int timestamp;
    String md5hash;
    CreateJson(String command, String nsid, int players, int timeout, boolean force, int timestamp, String hash){
      this.cmd = command;
      this.nsid = nsid;
      this.players = players;
      this.timeout = timeout;
      this.force = force;
      this.timestamp = timestamp;
      this.md5hash = hash;
    }

    /**
     * changeHash(String) a method responsible for putting the md5hash into our secret.
     * @param newHash the hash of our old JSOn message.
     */
    public void changeHash(String newHash){
      this.md5hash = newHash;
    }
  }

  class PlayDiscardJson{
    String action;
    int position;
    PlayDiscardJson(String action, int position){
      this.action = action;
      this.position = position;
    }
  }

  class InformRankJson{
    String action;
    int player;
    int rank;

    InformRankJson(int player, int rank){
      this.action = "inform";
      this.player = player;
      this.rank = rank;
    }
  }

  class InformSuiteJson{
    String action;
    int player;
    String suit;

    InformSuiteJson(int player, String suite){
      this.action = "inform";
      this.player = player;
      this.suit = suite;
    }
  }

  class JoinJson{
    String cmd;
    String nsid;
    int gameId;
    String token;
    int timestamp;
    String md5hash;

    JoinJson(String nsid, int id, String token, int timestamp, String secret){
      this.cmd = "join";
      this.nsid = nsid;
      this.gameId = id;
      this.token = token;
      this.timestamp = timestamp;
      this.md5hash = secret;
    }

    public void changeHash(String newSecret){
      this.md5hash = newSecret;
    }
  }


  /**
   * doPlayDiscardAction(String, int)
   * method responsible for sending a message containing information about
   * discarding or playing a card to the server.
   * @param action String that contains 'discard' or 'play'.
   * @param position int that contains information about playing a certain card in our hand (1-5).
   */
  public void doPlayDiscardAction(String action, int position){
    PlayDiscardJson move = new PlayDiscardJson(action, position);
    Gson gson = new Gson();
    System.out.println(gson.toJson(move));
    gson.toJson(move, this.pw);
    pw.flush();
  }

  /**
   * doInformRankAction(int, int)
   * method responsible for sending a message containing information about
   * telling another player about a rank in their hand.
   * @param player int that is the player's id that we wish to tell info to.
   * @param rank int that contains the position of the card in the players hand (1-5).
   */
  public void doInformRankAction(int player, int rank){
    InformRankJson move = new InformRankJson(player, rank);
    Gson gson = new Gson();
    System.out.println(gson.toJson(move));
    gson.toJson(move, this.pw);
    pw.flush();
  }

  /**
   * doInformSuiteAction(int, String)
   * method responsible for sending a message containing information about
   * telling another player about a suit in their hand.
   * @param player int that is the player's id that we wish to tell info to.
   * @param suite String that contains the color that we wish to tell the player about.
   */
  public void doInformSuiteAction(int player, String suite){
    InformSuiteJson move = new InformSuiteJson(player, suite.substring(0, 1));
    Gson gson = new Gson();
    System.out.println(gson.toJson(move));
    gson.toJson(move, this.pw);
    pw.flush();
  }


  /**
   * sendCreate(...) method responsible for sending a create-game request to the server.
   * @param nsid String that is the user's NSID.
   * @param players int the number of players in the game.
   * @param time int the timeout time.
   * @param force boolean if we're forcing a create. True if we are, false otherwise.
   * @param secret String containing our given secret.
   */
  public void sendCreate(String nsid, int players, int time, boolean force, String secret){
    CreateJson theClassJSON = new CreateJson("create", nsid, players, time, force, (int) Instant.now().getEpochSecond(), secret);
    Gson gson = new Gson();
    String message = gson.toJson(theClassJSON);
    theClassJSON.changeHash(computeHash(message));
    gson.toJson(theClassJSON, this.pw);
    pw.flush();

  }

  /**
   * doJoin(...) method responsible for sending a join-game request to the server.
   * @param nsid String that is the user's NSID.
   * @param gameId int the gameId of the game we're joining
   * @param token String secret token given by the game creator.
   * @param secret String containing our given secret.
   */
  public void doJoin(String nsid, int gameId, String token, String secret){
    int time = (int) Instant.now().getEpochSecond();
    JoinJson theClassJSON = new JoinJson(nsid, gameId, token, time, secret);
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);
    Gson gson = gsonBuilder.create();
    String message = gson.toJson(theClassJSON);
    theClassJSON.changeHash(computeHash(message));
    gson.toJson(theClassJSON, this.pw);
    pw.flush();
  }

  /**
   * printRecievedMessage() a method used for testing. Prints the incoming JSON message.
   * @return String the recieved JSON message. Returns after printing to std. out.
   */
  public String printRecievedMessage(){
    try{
    String theMessage = "";
    char theChar;
    while ((theChar = (char)br.read()) != '}'){
      theMessage += theChar;
    }
    theMessage += '}';
    System.out.println(theMessage);
    return theMessage;
  } catch (Exception e){
    //
  }
  return "";
  }


}
