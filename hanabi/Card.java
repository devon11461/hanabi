package hanabi;

/**
 * Card is a class that contains information about cards that each player will have .
 */
public class Card{
  /**
   * Stuff to hold the value of each card. Important to note that
   *  xCorner and yCorner will tell us information about where
   *  each card is drawn on the GUI.
   */
  private String color;
  private int number;
  private boolean knowNumber = false;
  private boolean knowColor = false;
   float xCorner, yCorner;

  Card(String givenColor, int givenNumber){
    if (givenNumber < 0 || givenNumber > 5){
       //needs to throw exception TODO.
    }
    else if (givenColor.equals("red") || givenColor.equals("green") || givenColor.equals("blue") || givenColor.equals("yellow") || givenColor.equals("white") || givenColor.equals("rainbow") || givenColor.equals("empty")){
      this.color = givenColor;
      this.number = givenNumber;
    }
  }

  /**
   * setKnowNumber() a method responsible for setting the boolean field knowNumber to true.
   * Will be called when someone gives our client information.
   */
  public void setKnowNumber(){
    this.knowNumber = true;
  }

  /**
   * setKnowColor() a method responsible for setting the boolean field knowColor to true.
   * Will be called when someone gives our client information.
   */
  public void setKnowColor(){
    this.knowColor = true;
  }

  /**
   * getknowNumber() a method that gets the value stored in knowNumber.
   * @return boolean the value of knowNumber.
   */
  public boolean getKnowNumber(){
    return this.knowNumber;
  }

  /**
   * getKnowColor() a method that gets the value stored in knowColor.
   * @return boolean the value of knowColor.
   */
  public boolean getKnowColor(){
    return this.knowColor;
  }

  /**
   * setCoordinates(float, float) a method that will be called when drawing.
   * The values of the fields, xCorner and yCorner, will be used for hit detection.
   * @param x x-coordinate of a mouse click.
   * @param y y-coordinate of a mouse click.
   */
  public void setCoordinates(float x, float y){
    this.xCorner = x;
    this.yCorner = y;
  }

  /**
   * getColor() a method responsible for getting the color of our card.
   * @return String color, "yellow", "red", "white", "blue", "empty", "yellow", or "rainbow"
   */
  public String getColor(){
    return this.color;
  }

  /**
   * setColor() a method responsible for setting the color of our card.
   * Called when we replace a card or get information about a card.
   * @param color String that reprsents a suit. "yellow", "red", "white", "blue", "empty", "yellow", or "rainbow"
   */
  public void setColor(String color){
    this.color = color;
  }

  /**
   * getColor() a method responsible for getting the rank of our card.
   * @return Integer number, values between 0 and 5 inclusive.  (0 if unknown number)
   */
  public int getNumber(){
    return this.number;
  }

  /**
   * setNumber() a method responsible for setting the rank of our card.
   * Called when we replace a card or get information about a card.
   * @param n Integer number, values between 0 and 5 inclusive.  (0 if unknown number)
   */
  public void setNumber(int n){
    this.number = n;
  }

  /**
   * setKnowsFalse() a method responsible for changing the values of knowColor
   * and knownumber to false. This will be called whenever our client draws a new card.
   */
  public void setKnowsFalse(){
    this.knowColor = false;
    this.knowNumber = false;
  }

  /**
   * A method responsible for converting the values of our card to readable values.
   * @return String that respresents the suit and rank of our card.
   */
  public String toString(){
    return ("" + color.substring(0, 1) + number + " ");
  }
}
