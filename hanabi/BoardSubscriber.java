package hanabi;

/**
 * Interface for our publish/subscribe design pattern. Our view will implement this. 
 */
public interface BoardSubscriber {
  public void modelChanged();
}
