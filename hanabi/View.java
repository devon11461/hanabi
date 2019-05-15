package hanabi;

import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;


/**
 * View is something that all HumanPlayers will see on their screen and interact
 * with. Designed using javafx, and as such javafx will be a dependency this project
 * has. Implements boardsubscriber. 
 */
public class View extends Pane implements BoardSubscriber {
  Canvas theCanvas;
  GraphicsContext gc;
  Board model;
  private float CARD_WIDTH = 50;
  private float CARD_HEIGHT = 100;

  /**
   * The constructor
   * @param width  the width that we want the view to be.
   * @param height the height that we want our view to be.
   */
  View(float width, float height){
    this.theCanvas = new Canvas(width, height);
    setStyle("-fx-background-color: mediumseagreen");
    this.gc = theCanvas.getGraphicsContext2D();
    this.getChildren().add(theCanvas);
    onDraw();
  }

  /**
   * This is responsible for giving our a view a model it has to draw.
   *
   * @param board  an Object of type Board that represents the model we're going to draw.
   */
  public void setModel(Board board){
    model = board;
  }


  /**
   * This method is responsible for drawing the model it represents.
   * It will be called whenever the model changes, and will (slightly) change
   * the display
   *
   */
  public void onDraw(){
    //Drawing a black square right now!
      gc.clearRect(0, 0, this.getWidth(), this.getHeight());

      if (model!=null){
          if (this.model.getPlayers().size() == 2){
            drawTwoGame();
          } else if (this.model.getPlayers().size() == 3){
            drawThreeGame();
          } else if (this.model.getPlayers().size() == 4){
            drawFourGame();
          } else if (this.model.getPlayers().size() == 5){
            drawFiveGame();
          }
          if (model.getDisplayOptions()){
            gc.setStroke(Color.BLACK);
            float topLeftX = (float) ((this.getWidth()/2)-(2*CARD_WIDTH + 20));
            float topLeftY = (float) (this.getHeight()-240);
            gc.strokeRect(topLeftX, topLeftY, 4*CARD_WIDTH, 2*CARD_WIDTH);
            gc.setFill(Color.DARKMAGENTA);
            gc.fillRect(topLeftX + 10, topLeftY+10, 3.5*CARD_WIDTH+5, CARD_WIDTH-10);
            gc.fillRect(topLeftX + 10, topLeftY+55, 3.5*CARD_WIDTH+5, CARD_WIDTH-10);
            this.model.tellButtonWidth = (float)3.5*CARD_WIDTH+5;
            this.model.tellButtonHeight = CARD_WIDTH-10;
            this.model.tellNumX = topLeftX + 10;
            this.model.tellNumY = topLeftY + 10;
            this.model.tellColorX = topLeftX + 10;
            this.model.tellColorY = topLeftY + 55;
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("TimesRoman", (CARD_WIDTH-10) / 2));
            gc.fillText("Number",((topLeftX+10 + (3.5*CARD_WIDTH+5 + topLeftX+10)) / 2 - CARD_WIDTH + 10), (topLeftY+10 + (topLeftY+10+CARD_WIDTH-10)) / 2);
            gc.fillText("Color",((topLeftX+10 + (3.5*CARD_WIDTH+5 + topLeftX+10)) / 2 - CARD_WIDTH + 20), (topLeftY+55 + (topLeftY+55+CARD_WIDTH-10)) / 2);
          }

          makeButtons();

          drawMessage();

          drawDiscardPile();

          drawCenterPiles();

          drawStats();

          this.gc.setFill(Color.WHITE);
          gc.setFont(new Font("TimesRoman", (float)0.035*this.getHeight()));
          gc.fillText("Fuses: " + model.getNumFuses(), 10, 700);
          gc.fillText("Info Tokens: " + model.getNumTokens(), 10, 730);
      }
  }

  /**
   * This method will be called when we're drawing cards. It will
   * immediately be called for each card in each hand. It is responsible
   * for selecting the right color to draw the card in.
   *
   * @param color  the color of the card, as a lower-case string. Will be either yellow, white, red, blue, green or empty.
   */
  public void setColor(String color){
    switch(color){
      case "yellow":
        this.gc.setFill(Color.YELLOW);
        break;
      case "red":
        this.gc.setFill(Color.RED);
        break;
      case "white":
        this.gc.setFill(Color.WHITE);
        break;
      case "blue":
        this.gc.setFill(Color.BLUE);
        break;
      case "green":
        this.gc.setFill(Color.GREEN);
        break;
      case "empty":
        this.gc.setFill(Color.BLACK);
        break;
      default:
        return;


    }
  }

  /**
   * Draw the discard pile stored in the model. Only will be used when
   * the Draw Discard Pile button is pressed.
   * <p>
   *
   */
  public void drawDiscardPile(){
    if (model.getSeeDiscard()){
      String constructString = "Discard pile: ";
      for (Card c : model.getDiscardPile().getPile()){
        constructString += c.toString();
      }
      this.gc.setFill(Color.WHITE);
      gc.setFont(new Font("TimesRoman", (float)0.015*this.getHeight()));
      gc.fillText(constructString, 200, 240);
    }
  }

  public void drawStats(){
    if (model.seeStats){
      for (Player p : model.getPlayers()){
        if (p.getId() == model.getPlayerID()){
          AIPlayer holdPlayer = new AIPlayer(model.getPlayerID());
          Card c = holdPlayer.calculateMove(model.getDiscardPile(), model.getPlayers(), model.getPile(), false); //variable if we had rainbow mode.
          String statsMessage = "Most probable card in your hand: " + c.toString();
          this.gc.setFill(Color.WHITE);
          gc.setFont(new Font("TimesRoman", (float)0.015*this.getHeight()));
          gc.fillText(statsMessage, 200, 260);
        }
      }
    }
  }

  /**
   * A method responsible for drawing a message that indicates what our user should do.
   *
   */
  public void drawMessage(){
    this.gc.setFill(Color.WHITE);
    gc.setFont(new Font("TimesRoman", (float)0.02*this.getHeight()));
    gc.fillText(model.getMessage(), 200, 200);
    gc.fillText("Or click anywhere else to go back!", 200, 220);
  }

  public void drawCenterCard(String color, float x, int index){
    int cardNumber = model.getPile().get(index);
    this.setColor(color);
    gc.fillRect(x, 300, CARD_WIDTH, CARD_HEIGHT);
    gc.setFill(Color.BLACK);
    gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
    gc.fillText("" + cardNumber, x-5+(CARD_WIDTH/2), 300+(CARD_HEIGHT)/2);
    gc.setFill(Color.WHITE);
    gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3.2)));
    gc.fillText("" + cardNumber, x-5+(CARD_WIDTH/2), 300+(CARD_HEIGHT)/2);
  }

  /**
   * A method responsible for drawing the 5-6 center piles in the game.
   * Incomplete. Needs to consider rainbow mode.
   *
   */
  public void drawCenterPiles(){
    float x = 200;
    drawCenterCard("red", x, 0);
    x += CARD_WIDTH+20;
    drawCenterCard("white", x, 1);
    x += CARD_WIDTH+20;
    drawCenterCard("green", x, 2);
    x += CARD_WIDTH+20;
    drawCenterCard("blue", x, 3);
    x += CARD_WIDTH+20;
    drawCenterCard("yellow", x, 4);
    //x += CARD_WIDTH+20;
    //drawCenterCard("rainbow", x, 5);

  }
  /**
   *  Draw the 6 buttons that our users must click on. These buttons will
   * be how each player starts their turn. The buttons are can be interacted
   * with while clicking.
   */
  public void makeButtons(){
    gc.setFill(Color.DARKMAGENTA);
    gc.fillRect(0.8*this.getWidth(), 0.68*this.getHeight(), (0.2*this.getWidth()), (0.04)*this.getHeight());
    gc.setFill(Color.WHITE);
    gc.setFont(new Font("TimesRoman", (float)0.02*this.getHeight()));
    gc.fillText("Play", (0.8*this.getWidth()+(0.015*this.getWidth())), 0.7*this.getHeight());
    this.model.playX = (float)(0.8*this.getWidth());
    this.model.playY = (float)(0.68*this.getHeight());

    gc.setFill(Color.DARKMAGENTA);
    gc.fillRect(0.8*this.getWidth(), 0.73*this.getHeight(), (0.2*this.getWidth()), (0.04*this.getHeight()));
    gc.setFill(Color.WHITE);
    gc.setFont(new Font("TimesRoman", (float)0.02*this.getHeight()));
    gc.fillText("Discard", (0.8*this.getWidth()+(0.015*this.getWidth())), 0.75*this.getHeight());
    this.model.discardX = (float)(0.8*this.getWidth());
    this.model.discardY = (float)(0.73*this.getHeight());

    gc.setFill(Color.DARKMAGENTA);
    gc.fillRect(0.8*this.getWidth(), 0.78*this.getHeight(), (0.2*this.getWidth()), (0.04*this.getHeight()));
    gc.setFill(Color.WHITE);
    gc.setFont(new Font("TimesRoman", (float)0.02*this.getHeight()));
    gc.fillText("AI Mode", (0.8*this.getWidth()+(0.015*this.getWidth())), 0.8*this.getHeight());
    this.model.aiX = (float)(0.8*this.getWidth());
    this.model.aiY = (float)(0.78*this.getHeight());

    gc.setFill(Color.DARKMAGENTA);
    gc.fillRect(0.8*this.getWidth(), 0.83*this.getHeight(), (0.2*this.getWidth()), (0.04*this.getHeight()));
    gc.setFill(Color.WHITE);
    gc.setFont(new Font("TimesRoman", (float)0.02*this.getHeight()));
    gc.fillText("Tell Info", (0.8*this.getWidth()+(0.015*this.getWidth())), 0.85*this.getHeight());
    this.model.tellX = (float)(0.8*this.getWidth());
    this.model.tellY = (float)(0.83*this.getHeight());

    gc.setFill(Color.DARKMAGENTA);
    gc.fillRect(0.8*this.getWidth(), 0.88*this.getHeight(), (0.2*this.getWidth()),(0.04*this.getHeight()));
    gc.setFill(Color.WHITE);
    gc.setFont(new Font("TimesRoman", (float)0.02*this.getHeight()));
    gc.fillText("See Discard", (0.8*this.getWidth()+(0.015*this.getWidth())), 0.9*this.getHeight());
    this.model.seeDiscardX = (float)(0.8*this.getWidth());
    this.model.seeDiscardY = (float)(0.88*this.getHeight());

    gc.setFill(Color.DARKMAGENTA);
    gc.fillRect(0.8*this.getWidth(), 0.93*this.getHeight(), (0.2*this.getWidth()), (0.04*this.getHeight()));
    gc.setFill(Color.WHITE);
    gc.setFont(new Font("TimesRoman", (float)0.02*this.getHeight()));
    gc.fillText("See stats",(0.8*this.getWidth()+(0.015*this.getWidth())), 0.95*this.getHeight());
    this.model.seeStatsX = (float)(0.8*this.getWidth());
    this.model.seeStatsY = (float)(0.93*this.getHeight());

    model.buttonWidth = (float) (0.2*this.getWidth());
    model.buttonHeight = (float)(0.04*this.getHeight());
  }

  /**
   *  Layout the cards for a 2-player game. One player will be at the top of
   *  the screen, and
   *  our client will be at the bottom..
   */
  public void drawTwoGame(){
    float ourPlayer =  (float) ((this.getWidth()/2 ) - ((this.model.getPlayers().get(0).getHand().getHand().size()*CARD_WIDTH)) + 60);
    float otherCard  =   (float)((this.getWidth()/2 ) - (this.model.getPlayers().get(0).getHand().getHand().size()*CARD_WIDTH) + 60);
    for(Player p : model.getPlayers()){
      for (Card c: p.getHand().getHand()){
        if (p.getId()==model.getPlayerID()){
          this.setColor(c.getColor());
          gc.fillRect(ourPlayer, this.getHeight()-130, CARD_WIDTH, CARD_HEIGHT);
          if (c.getNumber() != 0){
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
            gc.fillText(""+c.getNumber(), ourPlayer+20, (this.getHeight()-130)+(CARD_HEIGHT/2));
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3.2)));
            gc.fillText(""+c.getNumber(), ourPlayer+20, (this.getHeight()-130)+(CARD_HEIGHT/2));
          }
          c.setCoordinates(ourPlayer, (float)this.getHeight()-130);
          p.getHand().setDimension(CARD_WIDTH, CARD_HEIGHT);
          ourPlayer+=(CARD_WIDTH+20);
        }
        else{
          this.setColor(c.getColor());
          gc.fillRect(otherCard, 20, CARD_WIDTH, CARD_HEIGHT);
          gc.setFill(Color.BLACK);
          gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
          gc.fillText(""+c.getNumber(), otherCard+20, 20+(CARD_HEIGHT/2));
          gc.setFill(Color.WHITE);
          gc.setFont(new Font("TimesRoman", CARD_HEIGHT/3.2));
          gc.fillText(""+c.getNumber(), otherCard+20, 20+(CARD_HEIGHT/2));
          c.setCoordinates(otherCard, 20);
          if (c.getKnowColor()){
            gc.setFont(new Font("TimesRoman", (10)));
            gc.setFill (Color.WHITE);
            gc.fillText("Know color", otherCard, 20+CARD_HEIGHT+10);
          }
          if (c.getKnowNumber()){
            gc.setFont(new Font("TimesRoman", (10)));
            gc.setFill (Color.WHITE);
            gc.fillText("Know #", otherCard, 20+CARD_HEIGHT+20);
          }
          p.getHand().setDimension(CARD_WIDTH, CARD_HEIGHT);
          otherCard+=(CARD_WIDTH+20);
        }

      }
    }
  }

  /**
   *  Layout the cards for a 3-player game. 2 players will be at the top of
   *  the screen, and
   *  our client will be at the bottom.
   */
  public void drawThreeGame(){
    float ourPlayer =  (float) ((this.getWidth()/2 ) - ((this.model.getPlayers().get(0).getHand().getHand().size()*CARD_WIDTH)) + 60);
    float otherCard  =  30;
    for(Player p : model.getPlayers()){
      for (Card c: p.getHand().getHand()){
        if (p.getId()==model.getPlayerID()){
          this.setColor(c.getColor());
          gc.fillRect(ourPlayer, this.getHeight()-130, CARD_WIDTH, CARD_HEIGHT);
          if (c.getNumber() != 0){
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
            gc.fillText(""+c.getNumber(), ourPlayer+20, (this.getHeight()-130)+(CARD_HEIGHT/2));
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3.2)));
            gc.fillText(""+c.getNumber(), ourPlayer+20, (this.getHeight()-130)+(CARD_HEIGHT/2));
          }
          c.setCoordinates(ourPlayer, (float)this.getHeight()-130);
          p.getHand().setDimension(CARD_WIDTH, CARD_HEIGHT);
          ourPlayer+=(CARD_WIDTH+20);
        }
        else{
          this.setColor(c.getColor());
          gc.fillRect(otherCard, 20, CARD_WIDTH, CARD_HEIGHT);
          gc.setFill(Color.BLACK);
          gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
          gc.fillText(""+c.getNumber(), otherCard+20, 20+(CARD_HEIGHT/2));
          gc.setFill(Color.WHITE);
          gc.setFont(new Font("TimesRoman", CARD_HEIGHT/3.2));
          gc.fillText(""+c.getNumber(), otherCard+20, 20+(CARD_HEIGHT/2));
          c.setCoordinates(otherCard, 20);
          if (c.getKnowColor()){
            gc.setFont(new Font("TimesRoman", (10)));
            gc.setFill (Color.WHITE);
            gc.fillText("Know color", otherCard, 20+CARD_HEIGHT+10);
          }
          if (c.getKnowNumber()){
            gc.setFont(new Font("TimesRoman", (10)));
            gc.setFill (Color.WHITE);
            gc.fillText("Know #", otherCard, 20+CARD_HEIGHT+20);
          }
          p.getHand().setDimension(CARD_WIDTH, CARD_HEIGHT);
          otherCard+=(CARD_WIDTH+20);
        }

      }
      if (p.getId() != model.getPlayerID()){
      otherCard += 50;
      }
    }
  }

  /**
   *  Layout the cards for a 4-player game. One player will be at the top of
   *  the screen, one player will be on the right side of the screen, one on
   * the left and our client will be at the bottom.
   */
  public void drawFourGame(){
    float ourPlayer =  (float) ((this.getWidth()/2 ) - ((this.model.getPlayers().get(0).getHand().getHand().size()*CARD_WIDTH)) + 60);
    float topPlayer =  (float) ((this.getWidth()/2 ) - ((this.model.getPlayers().get(0).getHand().getHand().size()*CARD_WIDTH)) + 60);
    float leftPlayer = 150;
    float rightPlayer = 150;
    float sideWidth = CARD_HEIGHT;
    float sideHeight = CARD_WIDTH;
    int countDrawn = 0;
    int cardsDrawn = 0;
    for (Player p : model.getPlayers()){
      for (Card c: p.getHand().getHand()){
        if (p.getId() == model.getPlayerID()){
            this.setColor(c.getColor());
            gc.fillRect(ourPlayer, this.getHeight()-130, CARD_WIDTH, CARD_HEIGHT);
            if (c.getNumber() != 0){
              gc.setFill(Color.BLACK);
              gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
              gc.fillText(""+c.getNumber(), ourPlayer+20, (this.getHeight()-130)+(CARD_HEIGHT/2));
              gc.setFill(Color.WHITE);
              gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3.2)));
              gc.fillText(""+c.getNumber(), ourPlayer+20, (this.getHeight()-130)+(CARD_HEIGHT/2));
            }
            c.setCoordinates(ourPlayer, (float)this.getHeight()-130);
            p.getHand().setDimension(CARD_WIDTH, CARD_HEIGHT);
            ourPlayer+=(CARD_WIDTH+20);
          }
          else if (countDrawn == 0){
            this.setColor(c.getColor());
            gc.fillRect(topPlayer, 20, CARD_WIDTH, CARD_HEIGHT);
            if (c.getNumber() != 0){
              gc.setFill(Color.BLACK);
              gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
              gc.fillText(""+c.getNumber(), topPlayer+20, 20+(CARD_HEIGHT/2));
              gc.setFill(Color.WHITE);
              gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3.2)));
              gc.fillText(""+c.getNumber(), topPlayer+20, 20+(CARD_HEIGHT/2));
            }
            c.setCoordinates(topPlayer, 20);
            if (c.getKnowColor()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know color", topPlayer, 20+CARD_HEIGHT+10);
            }
            if (c.getKnowNumber()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know #", topPlayer, 20+CARD_HEIGHT+20);
            }
            topPlayer+=(CARD_WIDTH+20);
            cardsDrawn += 1;
            p.getHand().setDimension(CARD_WIDTH, CARD_HEIGHT);
            if (cardsDrawn == p.getHand().getHand().size()){
              cardsDrawn = 0;
              countDrawn++;
            }
          } else if (countDrawn == 1){
            this.setColor(c.getColor());
            gc.fillRect(20, leftPlayer, sideWidth, sideHeight);
            if (c.getNumber() != 0){
              gc.setFill(Color.BLACK);
              gc.setFont(new Font("TimesRoman", (sideHeight/2)));
              gc.fillText(""+c.getNumber(), (20+sideWidth)/2, (leftPlayer+(sideHeight/2)));
              gc.setFill(Color.WHITE);
              gc.setFont(new Font("TimesRoman", (sideHeight/2.3)));
              gc.fillText(""+c.getNumber(), (20+sideWidth)/2, (leftPlayer+(sideHeight/2)));
            }
            if (c.getKnowColor()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know color", leftPlayer, 20+sideWidth+10);
            }
            if (c.getKnowNumber()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know #", leftPlayer, 20+sideHeight+20);
            }
            c.setCoordinates(20, leftPlayer);
            leftPlayer += sideHeight + 20;
            cardsDrawn += 1;
            p.getHand().setDimension(CARD_HEIGHT, CARD_WIDTH);
            if (cardsDrawn == p.getHand().getHand().size()){
              cardsDrawn = 0;
              countDrawn++;
            }
          }
          else if (countDrawn == 2){
            this.setColor(c.getColor());
            gc.fillRect(this.getWidth()-150, rightPlayer, sideWidth, sideHeight);
            if (c.getNumber() != 0){
              gc.setFill(Color.BLACK);
              gc.setFont(new Font("TimesRoman", (sideHeight/2)));
              gc.fillText(""+c.getNumber(), ((this.getWidth()-150)+(sideWidth/2)), (rightPlayer+(sideHeight/2)));
              gc.setFill(Color.WHITE);
              gc.setFont(new Font("TimesRoman", (sideHeight/2.3)));
              gc.fillText(""+c.getNumber(), ((this.getWidth()-150)+(sideWidth/2)), (rightPlayer+(sideHeight/2)));
            }
            if (c.getKnowColor()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know color", rightPlayer, 20+sideHeight+10);
            }
            if (c.getKnowNumber()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know #", rightPlayer, 20+sideHeight+20);
            }
            c.setCoordinates((float)this.getWidth()-150, rightPlayer);
            rightPlayer += sideHeight + 20;
            cardsDrawn += 1;
            p.getHand().setDimension(CARD_HEIGHT, CARD_WIDTH);
          }
        }
      }
  }

  /**
   *  Layout the cards for a 5-player game. 2 players will be at the top of
   *  the screen, one on the right side, one on the left side, and
   *  our client will be at the bottom.
   */
  public void drawFiveGame(){
    float ourPlayer =  (float) ((this.getWidth()/2 ) - ((this.model.getPlayers().get(0).getHand().getHand().size()*CARD_WIDTH)) + 60);
    float topPlayer =  40;
    float leftPlayer = 180;
    float rightPlayer = 180;
    float sideWidth = CARD_HEIGHT;
    float sideHeight = CARD_WIDTH;
    int countDrawn = 0;
    int cardsDrawn = 0;
    for (Player p : model.getPlayers()){
      for (Card c: p.getHand().getHand()){
        if (p.getId() == model.getPlayerID()){
            this.setColor(c.getColor());
            gc.fillRect(ourPlayer, this.getHeight()-130, CARD_WIDTH, CARD_HEIGHT);
            if (c.getNumber() != 0){
              gc.setFill(Color.BLACK);
              gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
              gc.fillText(""+c.getNumber(), ourPlayer+20, (this.getHeight()-130)+(CARD_HEIGHT/2));
              gc.setFill(Color.WHITE);
              gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3.2)));
              gc.fillText(""+c.getNumber(), ourPlayer+20, (this.getHeight()-130)+(CARD_HEIGHT/2));
            }
            c.setCoordinates(ourPlayer, (float)this.getHeight()-130);
            p.getHand().setDimension(CARD_WIDTH, CARD_HEIGHT);
            ourPlayer+=(CARD_WIDTH+20);
          }
          else if (countDrawn < 2){
            this.setColor(c.getColor());
            gc.fillRect(topPlayer, 10, CARD_WIDTH, CARD_HEIGHT);
            if (c.getNumber() != 0){
              gc.setFill(Color.BLACK);
              gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3)));
              gc.fillText(""+c.getNumber(), topPlayer+20, 10+(CARD_HEIGHT/2));
              gc.setFill(Color.WHITE);
              gc.setFont(new Font("TimesRoman", (CARD_HEIGHT/3.2)));
              gc.fillText(""+c.getNumber(), topPlayer+20, 10+(CARD_HEIGHT/2));
            }
            c.setCoordinates(topPlayer, 10);
            if (c.getKnowColor()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know color", topPlayer, 20+CARD_HEIGHT+10);
            }
            if (c.getKnowNumber()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know #", topPlayer, 20+CARD_HEIGHT+20);
            }
            topPlayer+=(CARD_WIDTH+20);
            cardsDrawn += 1;
            p.getHand().setDimension(CARD_WIDTH, CARD_HEIGHT);
            if (cardsDrawn == p.getHand().getHand().size()){
              cardsDrawn = 0;
              topPlayer += 50;
              countDrawn++;
            }
          } else if (countDrawn == 2){
            this.setColor(c.getColor());
            gc.fillRect(20, leftPlayer, sideWidth, sideHeight);
            if (c.getNumber() != 0){
              gc.setFill(Color.BLACK);
              gc.setFont(new Font("TimesRoman", (sideHeight/2)));
              gc.fillText(""+c.getNumber(), (20+sideWidth)/2, (leftPlayer+(sideHeight/2)));
              gc.setFill(Color.WHITE);
              gc.setFont(new Font("TimesRoman", (sideHeight/2.3)));
              gc.fillText(""+c.getNumber(), (20+sideWidth)/2, (leftPlayer+(sideHeight/2)));
            }
            if (c.getKnowColor()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know color", leftPlayer, 20+sideHeight+10);
            }
            if (c.getKnowNumber()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know #", leftPlayer, 20+sideHeight+20);
            }
            c.setCoordinates(20, leftPlayer);
            leftPlayer += sideHeight + 20;
            cardsDrawn += 1;
            p.getHand().setDimension(CARD_HEIGHT, CARD_WIDTH);
            if (cardsDrawn == p.getHand().getHand().size()){
              cardsDrawn = 0;
              countDrawn++;
            }
          }
          else if (countDrawn == 3){
            this.setColor(c.getColor());
            gc.fillRect(this.getWidth()-115, rightPlayer, sideWidth, sideHeight);
            if (c.getNumber() != 0){
              gc.setFill(Color.BLACK);
              gc.setFont(new Font("TimesRoman", (sideHeight/2)));
              gc.fillText(""+c.getNumber(), ((this.getWidth()-115)+(sideWidth/2)), (rightPlayer+(sideHeight/2)));
              gc.setFill(Color.WHITE);
              gc.setFont(new Font("TimesRoman", (sideHeight/2.3)));
              gc.fillText(""+c.getNumber(), ((this.getWidth()-115)+(sideWidth/2)), (rightPlayer+(sideHeight/2)));
            }
            if (c.getKnowColor()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know color", rightPlayer, 20+sideHeight+10);
            }
            if (c.getKnowNumber()){
              gc.setFont(new Font("TimesRoman", (10)));
              gc.setFill (Color.WHITE);
              gc.fillText("Know #", rightPlayer, 20+sideHeight+20);
            }
            c.setCoordinates((float)this.getWidth()-115, rightPlayer);
            rightPlayer += sideHeight + 20;
            cardsDrawn += 1;
            p.getHand().setDimension(CARD_HEIGHT, CARD_WIDTH);
          }
        }
      }
  }

  /**
   *  This method will be called whenever we receive a message from the server
   * or from user input that changes the state of the game. It will cause
   * any subscribers to redraw themselves.
   */
  public void modelChanged(){
    this.onDraw();
  }
}
