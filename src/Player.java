/* Player.java
 * Authors: Armen Arslanian
 *          Anderson Giang
 * COMP 282
 * December 13, 2015
 * Professor Mike Barnes
 */
import java.awt.*;
import SimulationFramework.*;
import java.util.Stack;
/**<p>Player inherits Bot so it moves along the path.</p>
 * @author Armen Arslanian
 * @author Anderson Giang
 * @version 1.0
 * @since 12/13/2015
 */
public class Player extends Bot {
   /**Player's strength. Player starts with a strength of 2000*/
   private int strength = 2000;
   /**Player's wealth. Player starts with a wealth of 1000*/
   private int wealth = 1000;   
   /**If there is treasure or not.*/
   private boolean treasure = false;
   /**Player's id*/
   private String id;
   /**Player's location*/
   private String place;
   /**Player's goal location*/
   private Point goal;
   /**Player's beginning location*/
   private Point begin;
   /**Player's treasure location*/
   private Point treasurePlace;
   /**Player's path*/
   private Stack<WayPoint> myPath = new Stack<>();
   /**Player's path*/
   private boolean isPlaying = true;
   /**<p>Constructor that creates a Player</p>
    * @param label name of player
    * @param x start x-coordinate of player
    * @param y start y-coordinate of player
    * @param goalX goal x-coordinate of player
    * @param goalY goal y-coordinate of player
    * @param treasureX treasure x-coordinate of player
    * @param treasureY treasure y-coordinate of player
    * @param colorValue color of player
    */
   public Player(String label, int x, int y, int goalX, int goalY, int treasureX, int treasureY, Color colorValue) {
      super(label, x, y,colorValue); //use suitable constructor from Bot class
      id = label;
      goal = new Point(goalX, goalY);
      begin = new Point(x, y);
      treasurePlace = new Point(treasureX,treasureY);
   }
   /**<p>Get Method to return path length player.</p>
    * @return length of player.
    */
   public int getPathSize() {
      return myPath.size();
   }
   /**<p>Get Method to return strength of a player.</p>
    * @return Strength of player.
    */
   public int getStrength() {
      return strength;
   }
   /**<p>Set Method to set player's strength.
    * Distance that the player moves is subtracted from player's strength.
    * Player can buy strength (increase strength) up to the city's cost.</p>
    * @param strength int variable.
    */
   public void setStrength(int strength) {
      this.strength = strength;
   }
   /**<p>Get Method to return if there is treasure on the WayPoint.</p>
    * @return If there is treasure or not.
    */
   public boolean hasTreasure() {
      return treasure;
   }
   /**<p>Set Method to set if the player has treasure or not.
    * True means there is treasure.</p>
    * @param treasure boolean variable
    */
   public void setTreasure(boolean treasure) {
      this.treasure = treasure;
   }
   /**<p>Set Method to set if the player is still playing or not.
    * True means they are still playing.</p>
    * @param isPlaying boolean variable
    */
   public void setPlaying(boolean isPlaying) {
      this.isPlaying = isPlaying;
   }
   /**<p>Get Method to return player is still playing.</p>
    * @return If player is still playing.
    */
   public boolean isPlaying() {
      return isPlaying;
   }
   /**<p>Get Method to return player's id.</p>
    * @return id.
    */
   public String getId() {
      return id;
   }
   /**<p>Set Method to set the path of the player.</p>
    * @param myPath Stack<WayPoint> variable
    */
   public void setPath(Stack<WayPoint> myPath) {
      this.myPath = myPath;
   }
   /**<p>Set Method to set the the treasure of the player.</p>
    * @param treasurePlace Point variable
    */
   public void setTreasurePlace(Point treasurePlace) {
      this.treasurePlace = treasurePlace;
   }
   /**<p>Get Method to return player's treasure location.</p>
    * @return treasurePlace.
    */
   public Point getTreasurePlace() {
      return treasurePlace;
   }
   /**<p>Get Method to return player's path.</p>
    * @return myPath in a stack.
    */
   public Stack<WayPoint> getPath() {
      return myPath;
   }
   /**<p>Get Method to return player's goal.</p>
    * @return goal location.
    */
   public Point getGoal() {
      return goal;
   }
   /**<p>Get Method to return player's beginning location.</p>
    * @return beginning location.
    */
   public Point getBegin() {
      return begin;
   }
   /**<p>Calls moveTo from Bot class.
    * This is how the player will move to the next WayPoint.</p>
    * @param pt point variable
    */
   public void move(Point pt) { 
      moveTo(pt);
   }
}//end Player class










