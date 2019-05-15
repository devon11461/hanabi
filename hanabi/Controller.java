package hanabi;
import org.json.simple.JSONObject;


import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The construct
 * Has a model that we'll 'poke' to change.
 * Several variables for maintaining game state. These include the state,
 * the card that was chosen (so we can change it's values after we get a reply from
 * the server), and the hitPlayerId (so we can see the last player we interacted with
 * when we tell information).
*/
public class Controller{
    private Gamestate state = Gamestate.READY;
    private Board model;
    private Card chosenCard;
    private int hitPlayerId = -1;

    /**
    * This is responsible for giving our a view a model it has to draw
     *@param board   An object of type board that represents the model we are going to interact with
    */
    public void setModel(Board board){
      model = board;
    }

    Controller(){

    }

    /**
     * This method responsible for handle mouse event what our user do in different case.
     * if user click some place that we set, it will send Json message to user.
     * if user not click the right place, system wiil prompt meeage to user to click again
     * <p> It's important to note that, from any state, if we click anywhere that is not
     * described below, we will be sent to the READY state. It's also important to note that
     * most actions (besides see stats/discard pile) will only be doable when it's our turn. 
     * @param event a mouse event.
     */
    public void handlePressed(MouseEvent event) {

      switch (state){
        /**
         * Case 1 : Ready. We will be in this state whenever it is our turn.
         * From this state, we can click on any button in the bottom right corner of
         * the screen.
         */
        case READY:
          if (model.myTurn) {
            if (model.hitDiscard((float)event.getX(), (float)event.getY()) && model.getNumTokens() < 7){
              model.setMessage("Click on one of your cards to discard!");
              this.state = Gamestate.DISCARD;
              break;
            }
            if (model.hitTell((float)event.getX(), (float)event.getY()) && model.getNumTokens() > 0){
              model.setMessage("Click on one of your team mates cards to tell them information!");
              this.state = Gamestate.TELL;
              break;
            }
            if (model.hitPlay((float)event.getX(), (float)event.getY())){
              model.setMessage("Click on one of your cards to play!");
              this.state=Gamestate.PLAY;
              break;
            }
            if (model.hitAI((float)event.getX(), (float)event.getY())){
              model.doAIMove();
              break;
            }
          } if (model.hitSeeDiscard((float)event.getX(), (float)event.getY())){
              model.toggleDiscardPile();
            }
            else if (model.hitStats((float) event.getX(), (float)event.getY())){
              model.toggleSeeStats();
            }
           break;

           /**
            * Case 2 : Ready. We will be in this state we can play a card.
            * This state will look for clicks on our own player's cards.
            */
        case PLAY:
          if (model.myTurn){

            for (Player p : model.getPlayers()){
              if (p.getId() == model.getPlayerID()){
                if (p.getHand().checkHit((float)event.getX(), (float)event.getY())) {
                  JSONObject theObj = model.constructJSON("play", 0, "", model.getMyHitIndex((float)event.getX(), (float)event.getY()), 0);
                  // model.sendHumanJSON(theObj);
                  model.sendPlayJson(model.getMyHitIndex((float)event.getX(), (float)event.getY()));
                  System.out.println("Playing a card: JSONObject action = " + theObj.get("action") + " the index : " + theObj.get("position"));
                  model.playedPosition = model.getMyHitIndex((float)event.getX(), (float)event.getY());
                  // model.setPlayersTurn();
                  model.setMessage("Wait for your turn!");
                  model.myTurn = false;
                  this.state = Gamestate.READY;
                  break;
                } else {
                  model.setMessage("Please click a button on the bottom right side!");
                  this.state = Gamestate.READY;
                  break;
                }
              }
            }
          } break;

          /**
           * Case 3 : DISCARD. We will be in this state when we click on the discard
           * button from the READY state.
           * From this state, we can click on any card in our hand to send it to the server
           * as a discard action.
           */
        case DISCARD:
        if (model.myTurn){

        // System.out.println(" You have click DISCARD button, click your card will show index of them");
          for (Player p : model.getPlayers()){
            if (p.getId() == model.getPlayerID()){
              if (p.getHand().checkHit((float)event.getX(), (float)event.getY())){
                JSONObject theObj = model.constructJSON("discard", 0, "", model.getMyHitIndex((float)event.getX(), (float)event.getY()), 0);
                model.sendDiscardJson(model.getMyHitIndex((float)event.getX(), (float)event.getY()));
                System.out.println("Playing a card: JSONObject action = " + theObj.get("action") + " the index : " + theObj.get("position"));
                model.setMessage("Wait for your turn!");
                model.myTurn = false;
                this.state = Gamestate.READY;
                break;
              } else {
                model.setMessage("Please click a button on the bottom right side!");
                this.state = Gamestate.READY;
                break;
              }
            }
          }
        }
          break;
          /**
           * Case 4 : TELL. We will be in this state after we click on the TELL INFO button
           * in the bottom right corner from the READY state.
           * From this state, we can click on any one of our team mates cards to
           * cause a prompt to appear indicating that we can tell information
           * about a number or color.
           */
        case TELL:
        if (model.myTurn){
            hitPlayerId = model.getHitPlayer((float) event.getX(), (float) event.getY());
            if (hitPlayerId != -1){
              if (hitPlayerId != model.getPlayerID()){
                state = Gamestate.INFORMATION;
                chosenCard = model.getHitCard((float) event.getX(), (float) event.getY());
                model.setDisplayOptions(true);
                model.setMessage("Click on one of the options at the center of the screen!");
                break;
              } else {
                model.setMessage("Please click a button on the bottom right side!");
                state = Gamestate.READY;
              }
            } else {
              state = Gamestate.READY;
              model.setMessage("Please click a button on the bottom right side!");
            }
          }
            break;

            /**
             * Case 5 : INFORMATION. We will be in this state after we click on one of our
             * teammates cards from the TELL state.
             * From this state, we can click on any one of the two options (tell number or color)
             * that have appeared at the center of the screen. Clicking on either
             * of these options will complete a turn.
             */
        case INFORMATION:
        if (model.myTurn){
          if (model.hitTellNum((float) event.getX(), (float) event.getY())) {
            // JSONObject theObj = model.constructJSON("tell", hitPlayerId, "", 0, chosenCard.getNumber());
            model.sendInformRankJson(hitPlayerId, chosenCard.getNumber());
            // model.sendHumanJSON(theObj);
            //System.out.println("Telling a card: JSONObject action = " + theObj.get("action") + " the player : " + theObj.get("player") + "the info: " + theObj.get("rank"));
            state = Gamestate.READY;
            hitPlayerId = -1;
            model.setDisplayOptions(false);
            model.myTurn = false;
            model.setMessage("Wait for your turn!");
            break;
          } else if (model.hitTellColor((float) event.getX(), (float) event.getY())) {
            // JSONObject theObj = model.constructJSON("tell", hitPlayerId, chosenCard.getColor(), 0, 0);
            model.sendInformSuiteJson(hitPlayerId, chosenCard.getColor());
            // model.sendHumanJSON(theObj);
          //  System.out.println("Telling a card: JSONObject action = " + theObj.get("action") + " the player : " + theObj.get("player") + "the info: " + theObj.get("suite"));
            state = Gamestate.READY;
            model.setDisplayOptions(false);
            model.myTurn = false;
            model.setMessage("Wait for your turn!");
            break;
          } else {
            state = Gamestate.READY;
            model.setMessage("Please click a button on the bottom right side!");
            model.setDisplayOptions(false);
            break;
          }
        }
        }
      }

 }
