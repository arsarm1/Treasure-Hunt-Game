/* Map.java
 * Authors: Armen Arslanian
 *          Anderson Giang
 * COMP 282
 * December 13, 2015
 * Professor Mike Barnes
 */
import java.util.Scanner;
import java.io.*;
import java.util.HashMap;
import java.awt.*;
import java.util.Iterator;
import java.util.ArrayList;
import SimulationFramework.*;
import java.util.Collection;
import java.sql.*;
/**<p>Map of all WayPoints.</p>
 * @author Armen Arslanian
 * @author Anderson Giang
 * @version 1.0
 * @since 12/13/2015
 */
public class Map {
   /**Hold the number of gold WayPoints*/
   private int numOfGold = 0;
	/**Hold the number of treasure maps*/
   private int numOfMaps = 0;
	/**Hold the number of cities*/
   private int numOfCities = 0;
   /**HashMap of all WayPoints from text file*/
   private HashMap<Point, WayPoint> myMap = new HashMap<Point,WayPoint>();
   /**ArrayList of neighbors from text file*/
   private ArrayList<Point> neighbors = new ArrayList<Point>();
   /**For GUI components
    * @see SimFrame from SimulationFramework
    */
   private AnimatePanel animatePanel;
   /**For GUI components
    * @see SimFrame from SimulationFramework
    */
   private SimFrame simFrame;
   /**<p>Constructor that creates a Map with the Waypoints.</p> 
    * @param simFrame simulation frame
    */
   public Map(SimFrame simFrame) {
      this.simFrame = simFrame;
      animatePanel = simFrame.getAnimatePanel();
      readWayPoints();
   }
   /**<p>This method reads WayPoints from waypoint.txt file.
    * It parses information needed from WayPoints.
    * Then, it creates the WayPoints and puts them in a HashMap.
    * 16 values for 1 WayPoint. Check if there is a neighbor Point, 
    * once there is, it will always be the default value of [8],
    * which will always be the neighbor point x and the index + 1 will be
    * the neighbor point y and we will be incrementing our i by two to get
    * index 10 for the next x neighbor point and so on....
    * Method was modified to read from SQLite database for Project 3.</p>
    */
    /*      	          x    y     H    C    G   MAPX&Y  N
    * example: WayPoint  40   300   68   67   0   0   0   4   (60   300)   (20   300)   (60   320)   (40   320)
    *       	  Index   0    1     2    3    4   5   6   7    8     9      10    11     12   13      14    15
    */
   public void readWayPoints() {
      Connection c = null;
      Statement stmt = null;
      String sql;
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:ArslanianGiang.db");
         stmt = c.createStatement();
         sql = "DROP TABLE IF EXISTS Player";
         stmt.executeUpdate(sql);
         sql = "DROP TABLE IF EXISTS Treasure";
         stmt.executeUpdate(sql);
         sql = "DROP TABLE IF EXISTS Map";
         stmt.executeUpdate(sql);
         sql = "DROP TABLE IF EXISTS City";
         stmt.executeUpdate(sql);
         System.out.println("Creating the tables.");
          //Create the table for the Player.
         sql = "CREATE TABLE Player " +
                    "(id String PRIMARY KEY, " +
                    "place String NOT NULL, " +
                    "wealth DOUBLE NOT NULL)"; 
         stmt.executeUpdate(sql);
         //Create the table for Treasures.
         sql = "CREATE TABLE Treasure " +
            		"(place String PRIMARY KEY, " +
            		"gold INT NOT NULL)";    
         stmt.executeUpdate(sql);  
         //Create the table for the Maps.
         sql = "CREATE TABLE Map " +
            		"(place String PRIMARY KEY, " +
            		"treasurePlace String NOT NULL)";
         stmt.executeUpdate(sql);
         sql = "CREATE TABLE City " +
            		"(place String PRIMARY KEY, " +
            		"cost INT NOT NULL)";
         stmt.executeUpdate(sql); 
         //Insert the players into the Player table.
         sql = "INSERT INTO Player VALUES" +
            		"('1', '(20,20)', 1000)";
         stmt.executeUpdate(sql);
         sql = "INSERT INTO Player VALUES" +
            		"('2', '(500,500)', 1000)";
         stmt.executeUpdate(sql);
         sql = "INSERT INTO Player VALUES" +
            		"('3', '(20,480)', 1000)";
         stmt.executeUpdate(sql);
         sql = "INSERT INTO Player VALUES" +
            		"('4', '(500,20)', 1000)";
         stmt.executeUpdate(sql);
         System.out.println("Tables have been successfully created. \nDisplaying WayPoints might take several minutes.");
         Scanner input = null;
         try { 
            input = new Scanner(new File("waypoint.txt"));        
         }
         catch(FileNotFoundException e) {
            System.out.println("Could not find waypoint.txt. \nPlease make sure it is in the correct directory.");
            return;
         }
         while (input.hasNextLine()) {
         //regular expression for java in spaces.
            String[] token = input.nextLine().trim().split("\\s+");
         //creating an ArrayList of neighbor points. Initialize it here because every time we go to a new line         
            ArrayList<Point> listOfNPoints = new ArrayList<Point>();
            int x = Integer.parseInt(token[0]);
            int y = Integer.parseInt(token[1]);
            int height = Integer.parseInt(token[2]);
            int cityCost = Integer.parseInt(token[3]);
            int gold = Integer.parseInt(token[4]);
            int mapX = Integer.parseInt(token[5]);
            int mapY = Integer.parseInt(token[6]);
            int countOfNeighbors = Integer.parseInt(token[7]);
            if (token.length > 7) {
               for (int i = 8; i < token.length - 1; i+=2) {
                  int neighborX = Integer.parseInt(token[i]);
                  int neighborY = Integer.parseInt(token[i+1]);
               //Create point object to add it into the ArrayList of neighbor points
                  Point myPoint = new Point(neighborX, neighborY);
                  listOfNPoints.add(myPoint);
               }
            }       
            String place = String.format("'(%d,%d)'", x, y);
            String treasurePlace = String.format("'(%d,%d)'", mapX, mapY);
            //count how many gold WayPoints
            if (gold > 0) {
               numOfGold++;
            }
            //count how many treasure maps
            if (mapX != 0 && mapY != 0) {
               numOfMaps++;
            }
            //count how many cities
            if (cityCost > 0) {
               numOfCities++;
            }
            sql = "INSERT INTO Treasure VALUES" +
               	"(" + place + ", " + gold + ")";   
            stmt.executeUpdate(sql);
            sql = "INSERT INTO Map VALUES" +
               	"(" + place + ", " + treasurePlace + ")";   
            stmt.executeUpdate(sql);
            sql = "INSERT INTO City VALUES" +
               	"(" + place + ", " + cityCost + ")";   
            stmt.executeUpdate(sql);
            /* create WayPoint object with all of the variable parameters,
             * how come we didn't include the count of neighbors in the WayPoint constructor?
             */
            WayPoint wp = new WayPoint(x, y, height, cityCost, gold, mapX, mapY, countOfNeighbors, listOfNPoints);
            Point myPoint = new Point(x, y);
            myMap.put(myPoint, wp);
            animatePanel.addPermanentDrawable(wp);
            //connectors to see potential paths
            for (int i = 0; i < wp.getNeighbors().size(); i++) {
               Connector c1 = new Connector(wp.getPoint(),wp.getNeighbors().get(i), Color.black);
               animatePanel.addPermanentDrawable(c1);
            }
         }//end of while loop
         input.close(); //close file   
      } 
      catch (Exception e) {
         System.err.println(e.getClass().getName() + " : " + e.getMessage());
         System.exit(0);
      }
      System.out.println("Opened database successfully");
   }
   
	/**<p>This method finds the closest WayPoint to the given point.
	 *    Calculates the two dimensional distance between the given point
	 *    and ALL of the WayPoints in the HashMap.
	 *    Therefore, chooses closest WayPoint by shortest distance.</p>
	 * @param p given point (will find closest WayPoint to p)
	 * @return WayPoint (closest one).
	 */
   public WayPoint findClosestWayPoint(Point p) {
      Iterator<WayPoint> iterator = myMap.values().iterator();
   	//This is the WayPoint in the iterator.
      WayPoint closest_waypoint = iterator.next();	   
      Point point = closest_waypoint.getPoint();
   	//compute distance formulas and updating variables if necessary
      double closest_distance = twoDimDistance(point, p);
      while (iterator.hasNext()) {		
         //This is the second WayPoint in the iterator.
         WayPoint wp = iterator.next();
         Point nextPoint = wp.getPoint();
         double distance = twoDimDistance(nextPoint, p);
         if (distance < closest_distance) {
            closest_distance = distance;
            closest_waypoint = wp;
         }
      }
      return closest_waypoint;
   }//end of findClosestWayPoint
	/**<p>Helper Method.
	 *    Calculates the two dimensional distance between two points.</p>
	 * @param a given point a
	 * @param b given point b
	 * @return 2D distance between point a and b.
	 */
   public double twoDimDistance(Point a,Point b) {
      double x = Math.pow(a.getX() - b.getX(), 2);
      double y = Math.pow(a.getY() - b.getY(), 2);
      return Math.sqrt(x + y);
   }
	/**<p>Helper Method.
	 * Calculates the three dimensional distance between two WayPoints.</p>
	 * @param a given point a
	 * @param b given point b
	 * @return 3D distance between point a and b
	 */
   public double threeDimDistance(WayPoint a, WayPoint b) {
      double x = Math.pow(a.getPoint().getX() - b.getPoint().getX(), 2);
      double y = Math.pow(a.getPoint().getY() - b.getPoint().getY(), 2);
      double z = Math.pow(a.getHeight() - b.getHeight(), 2);
      return Math.sqrt(x + y + z);
   } 
	/**<p>This method returns a WayPoint from the HashMap with a unique point value.</p>
	 * @param p unique point value
	 * @return WayPoint
	 */
   public WayPoint getWayPoint(Point p) {
      WayPoint wp = myMap.get(p);
      return wp;
   }
   /**<p>This method is useful for debugging.</p>
    * @return String of map size, number of cities, number of gold WayPoints, and number of treasure maps.
    */
   public String mapInfo() {
      return "Create HashMap " + myMap.size() + " City " + numOfCities + " Gold " + numOfGold + " Map " + numOfMaps;
   }
}//end Map class