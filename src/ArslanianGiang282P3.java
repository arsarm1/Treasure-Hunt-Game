/* ArslanianGiang282P3.java
 * Authors: Armen Arslanian
 *          Anderson Giang
 * COMP 282
 * December 13, 2015
 * Professor Mike Barnes
 */
/*
ArslanianGiang282P3.java

Mike Barnes
8/12/2015
*/
import java.awt.*;
import java.util.Comparator;
import java.awt.event.*;  
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Stack;
import SimulationFramework.*;
import java.util.HashSet;
import java.sql.*;
// CLASSPATH = ... /282projects/SimulationFrameworkV4
// PATH = ... /282projects/SimulationFrameworkV3/SimulationFramework

/**
ArslanianGiang282P3 is the simulation's main class (simulation app) that is a
subclass of SimFrame.  
<p> 

282 Simulation Framework applications must have a subclass of SimFrame
that also has a main method.  The simulation app can make the
appropriate author and usage "help" dialogs, override
setSimModel() and simulateAlgorithm() abstract methods
inherited from SimFrame.  They should also add any specific model
semantics and actions.

<p>

The simulated algorithm is defined in simulateAlgorithm().

<p>
ArslanianGiang282P3 UML class diagram (More detailed UML can be found ArslanianGiang282P3\doc\UML\detailedUML.pdf)
<p>
<img src="ArslanianGiang282P3.png" alt="UML" height="500" width="900">

@since 8/12/2013
@version 3.0
@author G. M. Barnes
@author Armen Arslanian
@author Anderson Giang
*/
public class ArslanianGiang282P3 extends SimFrame   {
   /**eliminate warning @ serialVersionUID*/
   private static final long serialVersionUID = 42L;
   // GUI components for application's menu
   /**the simulation application*/
   private ArslanianGiang282P3 app;
   // application variables;
   /**the actors "bots" of the simulation*/
   private ArrayList <Player> bot = new ArrayList<Player>();
   /**Map of WayPoints*/
   private Map map;
   /**Player who will move along the path.*/
   private Player charlie;
   /**For storing the current WayPoint.*/
   private WayPoint current_waypoint;
   /**For storing the starting WayPoint.*/
   private WayPoint initial_waypoint;
   /**For storing the destination WayPoint.*/
   private WayPoint dest_waypoint;
   /**Player 1 object.*/
   private Player player1;
   /**Player 2 object.*/
   private Player player2;
   /**Player 3 object.*/
   private Player player3;
   /**Player 4 object.*/
   private Player player4;
   /**For moving along the calculated path.*/
   private WayPoint target;
   /**If a WayPoint has treasure.*/
   ArrayList<Point> listOfNPoints = new ArrayList<Point>();
   /**Treasure WayPoint.*/
   private WayPoint treasure = new WayPoint(0, 0, 0, 0, 0, 0, 0, 0, listOfNPoints);
   /**For SQLite connection.*/
   Connection c = null;
   /**For SQLite statements.*/
   Statement stmt = null;
   /**For storing SQLite results.*/
   ResultSet rs;
   /* Main Method
    */
   public static void main(String args[]) {
      ArslanianGiang282P3 app = new ArslanianGiang282P3("ArslanianGiang282P3", "terrain282.png");
      app.start();  // start is inherited from SimFrame
   }
   /**
   Make the application:  create the MenuBar, "help" dialogs, 
   */
   public ArslanianGiang282P3(String frameTitle, String imageFile) {
      super(frameTitle, imageFile);
      // create menus
      JMenuBar menuBar = new JMenuBar();
      // set About and Usage menu items and listeners.
      aboutMenu = new JMenu("About");
      aboutMenu.setMnemonic('A');
      aboutMenu.setToolTipText(
         "Display informatiion about this program");
      // create a menu item and the dialog it invoke 
      usageItem = new JMenuItem("usage");
      authorItem = new JMenuItem("author");
      usageItem.addActionListener( // anonymous inner class event handler
            new ActionListener() {        
               public void actionPerformed(ActionEvent event) {
                  JOptionPane.showMessageDialog( ArslanianGiang282P3.this, 
                     "Sit back and watch the the players find their path to the destinations \n" +
                     "You can speed up or slow down the Bot with the horizontal bar located at the bottom of the window" +
                     "The rest of the buttons are self-explanatory - Start, Stop, Clear, and Reset",
                     "Usage",   // dialog window's title
                     JOptionPane.PLAIN_MESSAGE);
               }
            }
         );
      // create a menu item and the dialog it invokes
      authorItem.addActionListener(
            new ActionListener() {          
               public void actionPerformed(ActionEvent event) {
                  JOptionPane.showMessageDialog( ArslanianGiang282P3.this, 
                     "Armen Arslanian \n" +
                     "armen.arslanian.526@my.csun.edu \n" +
                     "Anderson Giang \n" +
                     "anderson.giang.820@my.csun.edu \n" +
                     "COMP 282",
                     "author",  // dialog window's title
                     JOptionPane.INFORMATION_MESSAGE,
                     //  author's picture 
                     new ImageIcon("author.png"));
               }
            }
         );
      // add menu items to menu 
      aboutMenu.add(usageItem);   // add menu item to menu
      aboutMenu.add(authorItem);
      menuBar.add(aboutMenu);
      setJMenuBar(menuBar);
      validate();  // resize layout managers
      // construct the application specific variables
   }
   /** 
   Set up the actors (Bots), wayPoints (Markers), and possible traveral
   paths (Connectors) for the simulation.
   */
   public void setSimModel() {
   	// set any initial visual Markers or Connectors
   	// get any required user mouse clicks for positional information.
   	// initialize any algorithm halting conditions (ie, number of steps/moves).
      map = new Map(this);
      makePlayers();
   }//end of setSimModel
   /**<p>This is a helper method.
    *    It creates the players.</p>
    */
   public void makePlayers() {
      player1 = new Player("1", 20, 20, 500, 500,0, 0, Color.red);
      bot.add(player1);
      animatePanel.addBot(player1);
      player2 = new Player("2", 500, 500, 20, 20,0,0 ,Color.pink);
      bot.add(player2);
      animatePanel.addBot(player2);
      player3 = new Player("3", 20, 480, 500, 20,0,0 ,Color.orange);
      bot.add(player3);
      animatePanel.addBot(player3);
      player4 = new Player("4", 500, 20, 20, 480,0,0, Color.blue);
      bot.add(player4);
      animatePanel.addBot(player4); 
   }
   /**<p>This method performs <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a>.
    * Calculates the shortest path by adding the neighbor cost. 
    * and the heuristic cost to the destination from the neighbor.
    * Makes use of a minimum priority queue to have the lowest cost always at the head of the queue.
    * It's helper method is movePath.</p>
    * @param start starting WayPoint
    * @param end ending WayPoint
    * @param player the player that will determine a path
    */
   public void aStar(WayPoint start, WayPoint end, Player player) {
      animatePanel.clearTemporaryDrawables();
      /*if (charlie.hasTreasure()) { //1
         statusReport("Start (" + target.getPoint().getX() + ", " + target.getPoint().getY() + "), "
            	+ "Stop (" + treasure.getPoint().getX() + ", " + treasure.getPoint().getY() + "), Player " 
                + charlie.getStrength() + " $ " + charlie.getWealth());
      }*/
      //open list minimum priority queue
      PriorityQueue<WayPoint> open_list = new PriorityQueue<WayPoint>(10, 
            new Comparator<WayPoint>() {
               public int compare(WayPoint a,WayPoint b) {
                  return (int) a.getF() - (int)  b.getF();		   
               }
            });
      //closed list HashSet
      HashSet<Point> closed_list = new HashSet<Point>();
      current_waypoint = start;
      current_waypoint.setF(map.threeDimDistance(current_waypoint, end));
      current_waypoint.setParent(null);
      /*if (charlie.hasTreasure() == false && current_waypoint.getColor().equals(Color.yellow)) { //2
         statusReport("Start (" + current_waypoint.getPoint().getX() + ", " + current_waypoint.getPoint().getY() + "), "
            		+ "Stop (" + dest_waypoint.getPoint().getX() + ", " + dest_waypoint.getPoint().getY() + "), Player " 
                  + charlie.getStrength() + " $ " + charlie.getWealth());
      }*/
      open_list.add(current_waypoint);
      while (!open_list.isEmpty() && !current_waypoint.equals(end)) {	   
         current_waypoint = open_list.poll(); //least cost
         if (current_waypoint.equals(end)) {
            break;
         }
         else {
            closed_list.add(current_waypoint.getPoint());
            Marker gray = new Marker(current_waypoint.getPoint(), Color.gray, 2);
            animatePanel.addTemporaryDrawable(gray);
            checkStateToWait();
            for (int i = 0; i < current_waypoint.getNeighbors().size(); i++) { //add neighbors
               WayPoint mark = map.getWayPoint(current_waypoint.getNeighbors().get(i));
               if ((!open_list.contains(mark)) && (!closed_list.contains(mark.getPoint()))) { //do not add the ones already in the lists
                  mark.setParent(current_waypoint);
                  mark.setG(mark.getParent().getG() + map.threeDimDistance(current_waypoint, mark));
                  mark.setH(map.threeDimDistance(mark, end));
                  mark.setF(mark.getG() + mark.getH());
                  open_list.add(mark);
                  Marker white = new Marker(mark.getPoint(), Color.white, 3);
                  animatePanel.addTemporaryDrawable(white);
                  checkStateToWait();
               }
            }
         }
      }
      if (current_waypoint.equals(end)) {
         findPath(start, player);
      }
      else {
         setStatus("Path does not exist.");
         statusReport("No path (" + initial_waypoint.getPoint().getX() + ", " + initial_waypoint.getPoint().getY() 
                           + ") to (" + dest_waypoint.getPoint().getX() + ", " + dest_waypoint.getPoint().getY() + ")");
         setSimRunning(false);
         animatePanel.setComponentState(false,false,false,false,true);
      }
   }
   /**<p>This method chooses the correct player
    *    It also knows when the game is over.</p>
    */
   public void choosePlayer() {
      for (int i = 0; i < bot.size(); i++) {
         if (bot.get(i).isPlaying() == false) {
            bot.remove(i);
         }			   
      }
      //Game Over!!!
      if (bot.isEmpty()) {
         String id1 = ""; String id2 = ""; String id3 = ""; String id4 = "";
         double wealth1 = 0; double wealth2 = 0; double wealth3 = 0; double wealth4 = 0;
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Player ORDER BY wealth DESC;");
            rs.next();
            id1 = rs.getString("id");
            rs.next();
            id2 = rs.getString("id");
            rs.next();
            id3 = rs.getString("id");
            rs.next();
            id4 = rs.getString("id");
         } 
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         statusReport("Game Over. Players finished " + id1 + " " + id2 + " " + id3 + " " + id4);
         statusReport("Results:");
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Player where id = " + id1 + ";");
            while (rs.next()) {
               wealth1 = rs.getDouble("wealth");
            }
            rs.close();
            rs = stmt.executeQuery("SELECT * FROM Player where id = " + id2 + ";");
            while (rs.next()) {
               wealth2 = rs.getDouble("wealth");
            }
            rs.close();
            rs = stmt.executeQuery("SELECT * FROM Player where id = " + id3 + ";");
            while (rs.next()) {
               wealth3 = rs.getDouble("wealth");
            }
            rs.close();
            rs = stmt.executeQuery("SELECT * FROM Player where id = " + id4 + ";");
            while (rs.next()) {
               wealth4 = rs.getDouble("wealth");
            }
            rs.close();
         } 
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         statusReport("Player " + id1 + " $ " + (int)wealth1);
         statusReport("Player " + id2 + " $ " + (int)wealth2);
         statusReport("Player " + id3 + " $ " + (int)wealth3);
         statusReport("Player " + id4 + " $ " + (int)wealth4);
         setStatus("Done");
         setSimRunning(false);
         animatePanel.setComponentState(false,false,false,false,true);
      } 
      if (!bot.isEmpty()) {
         Random r = new Random();
         //charlie will be either player 1 to 4
         charlie = bot.get(r.nextInt(bot.size()));
         //if player 1-4 is still playing move to the next point and perform the necessary updates
      }
      if (charlie.isPlaying() == true) {
         performUpdates(charlie.getPath());
      }
   }
   /**<p>This method is essential.
    *    SQLite Queries to get information about players and the map.</p>
    * @param path the current player's path.
    */
   public void performUpdates(Stack<WayPoint> path) {
      double distance;
      WayPoint prev = map.getWayPoint(charlie.getPoint());
      WayPoint target = path.pop();
      charlie.move(target.getPoint());
      checkStateToWait();
      distance = map.threeDimDistance(prev, target);	   
      prev = target;
      charlie.setStrength(charlie.getStrength()-(int)distance); //casting distance
      String waypoint = String.format("'(%d,%d)'", target.getPoint().x,target.getPoint().y);
      //done with treasure map
      if (charlie.getTreasurePlace().equals(charlie.getPoint()) && charlie.hasTreasure()) {
         charlie.setTreasure(false);
         aStar(map.getWayPoint(charlie.getTreasurePlace()), map.getWayPoint(charlie.getGoal()), charlie);
         statusReport("Path (" + charlie.getTreasurePlace().getX() + ", " + charlie.getTreasurePlace().getY() + ") to (" 
                  + charlie.getGoal().getX() + ", " + charlie.getGoal().getY() + ") length " + charlie.getPathSize());
      }
      //essential variables for queries
      String treasurePlace = null;
      int cityCost = 0;
      int wealth = 0;
      int gold = 0;
      //update player's place in the database
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
         stmt = c.createStatement();
         c.setAutoCommit(false);
         stmt.executeUpdate("UPDATE Player set place = " + waypoint + " WHERE id = " + charlie.getId());
         c.commit();
      }
      catch (Exception e) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      }
      //positve wealth players have a contest
      //most player take a third of the other players' money
      //other players loose money
      boolean check = false;
      String richPlayerId = null;
      double richPlayerWealth = 0;
      ArrayList<String> poorPlayerIds = new ArrayList<String>();
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
         stmt = c.createStatement();
         rs = stmt.executeQuery("SELECT place FROM Player GROUP BY place HAVING(COUNT(place) > 1);");
         check = rs.next();
         rs.close();
      }
      catch (Exception e) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      }
      //if there are players at the same place
      if (check) {
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Player WHERE place IN (SELECT place FROM Player GROUP BY place HAVING COUNT(*) > 1);");
            while (rs.next()) {
               if ( ((rs.getDouble("wealth")) > 0) && ((rs.getDouble("wealth")) > richPlayerWealth) ) {
                  richPlayerId = rs.getString("id");
                  richPlayerWealth = rs.getDouble("wealth");
               }
            }
            rs.close();
         }
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Player WHERE place IN (SELECT place FROM Player GROUP BY place HAVING COUNT(*) > 1);");
            while (rs.next()) {
               if (!((rs.getString("id")).equals(richPlayerId)) && ((rs.getDouble("wealth")) > 0) ) {
                  richPlayerWealth += ((rs.getDouble("wealth")) / 3);
                  poorPlayerIds.add(rs.getString("id"));
               }
            }
            rs.close();
         }
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         //most wealthy player gains a third of poorer player's money who are in the same location
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            c.setAutoCommit(false);
            stmt.executeUpdate("UPDATE Player set wealth = " + (int)richPlayerWealth + " WHERE id = " + richPlayerId);
            c.commit();
         }
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         //positive poor player lose a third of their money
         for (String poor : poorPlayerIds) {
            try {
               Class.forName("org.sqlite.JDBC");
               c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
               stmt = c.createStatement();   
               rs = stmt.executeQuery("SELECT wealth FROM Player WHERE id = " + poor + ";");
               while (rs.next()) {
                  wealth = (int)rs.getDouble("wealth");
               }
               rs.close();
            } 
            catch (Exception e) {
               System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            }
            wealth = wealth - (wealth / 3);
            try {
               Class.forName("org.sqlite.JDBC");
               c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
               stmt = c.createStatement();
               c.setAutoCommit(false);
               stmt.executeUpdate("UPDATE Player set wealth = " + wealth + " WHERE id = " + poor + ";");
               c.commit();
            } 
            catch (Exception e) {
               System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            }
         }
         //display
         String display = "";
         double poorWealth = 0;
         for (String poor : poorPlayerIds) {
            try {
               Class.forName("org.sqlite.JDBC");
               c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
               stmt = c.createStatement();
               rs = stmt.executeQuery("SELECT wealth FROM Player WHERE id = " + poor + ";");
               while (rs.next()) {
                  poorWealth = rs.getDouble("wealth");
               }
               display += " " + poor + " $ " + poorWealth;
            } 
            catch (Exception e) {
               System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            }
         }
         statusReport("Contest! Player " + richPlayerId + " $ " + (int)richPlayerWealth + " wins against player(s)" + display);
      }
      //queries and statusReports
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
         stmt = c.createStatement();
         //query for cityCost
         rs = stmt.executeQuery("SELECT cost FROM CITY WHERE PLACE = " + waypoint + ";");
         while (rs.next()) {
            cityCost = rs.getInt("cost");
         }
         rs.close();
         //query for wealth
         rs = stmt.executeQuery("SELECT wealth FROM Player WHERE id = " + charlie.getId() + ";");
         while (rs.next()) {
            wealth = rs.getInt("wealth");
         }
         rs.close();
         //query for gold
         rs = stmt.executeQuery("SELECT gold FROM Treasure WHERE place = " + waypoint + ";");
         while (rs.next()) {
            gold = rs.getInt("gold");
         }
         rs.close();
         //query for map
         rs = stmt.executeQuery("SELECT treasurePlace FROM Map WHERE place = " + waypoint + ";");
         while (rs.next()) {
            treasurePlace = rs.getString("treasurePlace");
         }
         rs.close();
      }
      catch (Exception e) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      }
      //pay city cost
      if (wealth > cityCost) {
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            c.setAutoCommit(false);
            stmt.executeUpdate("UPDATE Player set wealth = " + (wealth - cityCost) + " WHERE id = " + charlie.getId());  
            c.commit();
         }
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
      } 
      //if we are at a city
      if (cityCost > 0) {
         charlie.setStrength(charlie.getStrength()+ target.getCityCost());
         statusReport("Player " + charlie.getId() + " City (" + target.getPoint().getX() + ", " + target.getPoint().getY() + "), "
                        + "$ " + cityCost + ", " + charlie.getStrength() + " $ " + wealth);
      }
      //if there is gold
      if (gold > 0) {
         statusReport("Player " + charlie.getId() + " Gold (" + target.getPoint().getX() + ", " + target.getPoint().getY() + "), "
                        + "$ " + gold + ", " + charlie.getStrength() + " $ " + wealth);
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            c.setAutoCommit(false);
            stmt.executeUpdate("UPDATE Player set wealth = " + (wealth+gold) + " WHERE id = " + charlie.getId());
            c.commit();
            c.setAutoCommit(false);
            stmt.executeUpdate("UPDATE Treasure set gold = " + 0 + " WHERE place = " + waypoint);
            c.commit();
         }
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
      }
      //if there is a treasure map
      if (!treasurePlace.equals("(0,0)") && charlie.hasTreasure() == false) {
         statusReport("Player " + charlie.getId() + " Map (" + target.getPoint().getX() + ", " + target.getPoint().getY() + ") "
                  + "Treasure " + treasurePlace + " " +  
                  + charlie.getStrength() + " $ " + wealth);
         charlie.setTreasure(true);
         Point pointTreasure = new Point(target.getMapX(), target.getMapY());
         treasure = map.getWayPoint(pointTreasure);
         charlie.setTreasurePlace(pointTreasure);
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            c.setAutoCommit(false);
            stmt.executeUpdate("UPDATE Map set treasurePlace = " + "'(0,0)'" + " WHERE place = " + waypoint);
            c.commit();
         }
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         aStar(target, map.getWayPoint(charlie.getTreasurePlace()), charlie);
         statusReport("Path (" + target.getPoint().getX() + ", " + target.getPoint().getY() + ") to " 
                  + treasurePlace + " length " + charlie.getPathSize());
      }
      //if a given player reached their destination
      if (charlie.getPoint().equals(charlie.getGoal()) && charlie.hasTreasure() == false) {
         //update player's wealth in the database
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            c.setAutoCommit(false);
            stmt.executeUpdate("UPDATE Map set treasurePlace = " + "'(0,0)'" + " WHERE place = " + waypoint);
            c.commit();
         } 
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         wealth += charlie.getStrength(); //add strength to wealth
         statusReport("Player " + charlie.getId() + " Success, goal (" + charlie.getGoal().getX() + ", " + charlie.getGoal().getY()
            + ") " + charlie.getStrength() + " $ " + wealth);
         charlie.setPlaying(false);
      }
      choosePlayer();
   }
   /**<p>This method find a path for the selected player.</p>
    * @param begin starting WayPoint
    * @param player Player
    */   
   public void findPath(WayPoint begin, Player player) {     
      Stack<WayPoint> pathP1 = new Stack<WayPoint>();
      Stack<WayPoint> pathP2 = new Stack<WayPoint>();
      Stack<WayPoint> pathP3 = new Stack<WayPoint>();
      Stack<WayPoint> pathP4 = new Stack<WayPoint>();
      //player 1
      if (player.getId().equals("1")) {
         pathP1.push(current_waypoint);
         while (!current_waypoint.equals(begin)) {
            current_waypoint = current_waypoint.getParent();
            pathP1.push(current_waypoint);
         } 
         player1.setPath(pathP1);
      }
      //player 2
      if (player.getId().equals("2")) {
         pathP2.push(current_waypoint);
         while (!current_waypoint.equals(begin)) {
            current_waypoint = current_waypoint.getParent();
            pathP2.push(current_waypoint);
         } 
         player2.setPath(pathP2);
      }
      //player 3
      if (player.getId().equals("3")) {
         pathP3.push(current_waypoint);
         while (!current_waypoint.equals(begin)) {
            current_waypoint = current_waypoint.getParent();
            pathP3.push(current_waypoint);
         } 
         player3.setPath(pathP3);
      }
      //player 4
      if (player.getId().equals("4")) {
         pathP4.push(current_waypoint);
         while (!current_waypoint.equals(begin)) {
            current_waypoint = current_waypoint.getParent();
            pathP4.push(current_waypoint);
         } 
         player4.setPath(pathP4);
      }  
   }
   /**<p>This method calls choosePlayer.</p>
    */
   public void movePath() {
      choosePlayer();
   }
   /**
   Simulate the algorithm.
   */
   public synchronized void simulateAlgorithm() {
   	// Declare and set any local control variables.
   	// Or set up the initial algorithm state:
   	// declare and set any algorithm specific varibles
      while (runnable()) {
      	// put your algorithm code here.
      	// ...
      	// The following statement must be at end of any
      	// overridden abstact simulateAlgorithm() method
         statusReport(map.mapInfo());
         double wealth1 = 0; double wealth2 = 0; double wealth3 = 0; double wealth4 = 0;
         try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Player where id = " + player1.getId() + ";");
            while (rs.next()) {
               wealth1 = rs.getDouble("wealth");
            }
            rs.close();
            rs = stmt.executeQuery("SELECT * FROM Player where id = " + player2.getId() + ";");
            while (rs.next()) {
               wealth2 = rs.getDouble("wealth");
            }
            rs.close();
            rs = stmt.executeQuery("SELECT * FROM Player where id = " + player3.getId() + ";");
            while (rs.next()) {
               wealth3 = rs.getDouble("wealth");
            }
            rs.close();
            rs = stmt.executeQuery("SELECT * FROM Player where id = " + player4.getId() + ";");
            while (rs.next()) {
               wealth4 = rs.getDouble("wealth");
            }
            rs.close();
         } 
         catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         //player 1
         WayPoint begin1 = map.getWayPoint(player1.getBegin());
         Point p1 = new Point(500,500);
         WayPoint goal1 = map.getWayPoint(p1);
         goal1.setColor(Color.green);
         goal1.setSize(5);
         statusReport("Player " + player1.getId() + " Start (" + begin1.getPoint().getX() + ", " + begin1.getPoint().getY() + "), "
            	+ "Stop (" + goal1.getPoint().getX() + ", " + goal1.getPoint().getY() + "), Player " 
                + player1.getStrength() + " $ " + wealth1);
         aStar(begin1,goal1,player1);
         //player 2
         WayPoint begin2 = map.getWayPoint(player2.getBegin());
         Point p2 = new Point(20,20);
         WayPoint goal2 = map.getWayPoint(p2);
         goal2.setColor(Color.green);
         goal2.setSize(5);
         statusReport("Player " + player2.getId() + " Start (" + begin2.getPoint().getX() + ", " + begin2.getPoint().getY() + "), "
            	+ "Stop (" + goal2.getPoint().getX() + ", " + goal2.getPoint().getY() + "), Player " 
                + player1.getStrength() + " $ " + wealth2);
         aStar(begin2,goal2, player2);
         //player 3
         WayPoint begin3 = map.getWayPoint(player3.getBegin());
         Point p3 = new Point(500,20);
         WayPoint goal3 = map.getWayPoint(p3);
         goal3.setColor(Color.green);
         goal3.setSize(5);
         statusReport("Player " + player3.getId() + " Start (" + begin3.getPoint().getX() + ", " + begin3.getPoint().getY() + "), "
            	+ "Stop (" + goal3.getPoint().getX() + ", " + goal3.getPoint().getY() + "), Player " 
                + player1.getStrength() + " $ " + wealth3);
         aStar(begin3,goal3, player3);
         //player 4
         WayPoint begin4 = map.getWayPoint(player4.getBegin());
         Point p4 = new Point(20,480);
         WayPoint goal4 = map.getWayPoint(p4);
         goal4.setColor(Color.green);
         goal4.setSize(5);
         statusReport("Player " + player4.getId() + " Start (" + begin4.getPoint().getX() + ", " + begin4.getPoint().getY() + "), "
            	+ "Stop (" + goal4.getPoint().getX() + ", " + goal4.getPoint().getY() + "), Player " 
                + player1.getStrength() + " $ " + wealth4);
         aStar(begin4,goal4, player4);	  
         movePath();
         setSimRunning(false);
      }
   }
}