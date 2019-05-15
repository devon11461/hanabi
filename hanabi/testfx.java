package hanabi;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

/**
 * This class testfx is strictly for early stages of testing with our GUI.
 * It is not a part of the main project.
 */
public class testfx extends Application {

  @Override
  public void start(Stage primaryStage){
    HBox root = new HBox();
    primaryStage.setTitle("hanabi");
    primaryStage.setScene(new Scene(root, 800, 800));
    View theView = new View(800, 800);
    root.getChildren().add(theView);
    primaryStage.show();

    Board model = new Board(new Server(),3, 60, 1 );
    model.addToDiscardPile(new Card ("white", 1));
    model.addToDiscardPile(new Card ("blue", 1));
    model.addToDiscardPile(new Card ("green", 2));

    Controller controller = new Controller();
    controller.setModel(model);
    HumanPlayer player1= new HumanPlayer(1);
    AIPlayer player2=new AIPlayer(2);
    AIPlayer player3=new AIPlayer(3);
    AIPlayer player4 = new AIPlayer(4);
    AIPlayer player5 = new AIPlayer(5);

    player1.addCard(new Card("empty",2));
    player1.addCard(new Card("blue",0));
    player1.addCard(new Card("empty",2));
    player1.addCard(new Card("blue",0));
    player1.addCard(new Card("empty",2));

    player2.addCard(new Card("white",3));
    player2.addCard(new Card("blue",1));
    player2.addCard(new Card("red",5));
    player2.addCard(new Card("blue",1));
    player2.addCard(new Card("red",5));

    player3.addCard(new Card("yellow",3));
    player3.addCard(new Card("blue",1));
    player3.addCard(new Card("red",5));
    player3.addCard(new Card("blue",1));
    player3.addCard(new Card("red",5));

    player4.addCard(new Card("yellow",3));
    player4.addCard(new Card("blue",1));
    player4.addCard(new Card("red",5));
    player4.addCard(new Card("blue",1));
    player4.addCard(new Card("red",5));

    player5.addCard(new Card("yellow",3));
    player5.addCard(new Card("blue",1));
    player5.addCard(new Card("red",5));
    player5.addCard(new Card("blue",1));
    player5.addCard(new Card("red",5));

    model.addPlayer(player1);
    model.addPlayer(player2);
    model.addPlayer(player3);
    model.addPlayer(player4);
    model.addPlayer(player5);
    theView.setModel(model);
    theView.onDraw();
    theView.setOnMousePressed(controller::handlePressed);

    model.addSubscriber(theView);

    model.sendJSON();
    //test for the view and controller. Clicking on one of the play, discard
    //or tell information buttons changes our state. Can assure that we can't
    //click on these buttons again and change the state again.
  }

  public static void main(String[] args){
    launch(args);
  }
}
