package hanabi;
import java.util.*;
import org.json.simple.JSONObject;

/**
 * This class Main is strictly for early stages of testing with the command line.
 * It is not a part of the main project.
 */
public class Main {
  public static void main(String[] args){
    String action = "";
    int timeout = 0;
    String nsid = "";
    String secret = "";
    boolean force = false;
    String token = "";
    int numPlayers = 2;
    int id = 0;
    Server s = new Server();
    boolean ai = false;
    for (int i = 0; i < args.length; i++){
      switch (args[i]){
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
          numPlayers = Integer.parseInt(args[i]);
          break;
        case "-k":
          i+=1;
          secret = args[i];
          break;
        case "-n":
          i+=1;
          nsid = args[i];
          break;
        case "-q":
          i += 1;
          timeout = Integer.parseInt(args[i]);
          break;
        case "-t":
          i += 1;
          token = args[i];
          break;
        case "-j":
          action = "join";
          break;
        case "-i":
          i += 1;
          id = Integer.parseInt(args[i]);
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
    // testfx value = new testfx();
    // Board model = new Board(s, numPlayers, timeout, 1);
    // value.setModel(model);
    // value.theView.onDraw();
    // model.addPlayer(new HumanPlayer(1));
    // testfx.main(args);
  }
}
