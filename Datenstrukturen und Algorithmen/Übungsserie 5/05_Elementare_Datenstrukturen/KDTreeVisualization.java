import java.awt.Component;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Implements the operations on a KD-Tree and
 * functions to visualize the KD-Tree
 */
public class KDTreeVisualization extends Component{

  LinkedList<Point> points;  // List of points
  TreeNode kdRoot;           // The kd-Tree
  Image img;                 // Image to display points
  int w, h;                  // Width an height of image
  Graphics2D gi;             // Graphics object to draw points
  int n;                     // Number of points
  
  /**
   * Initializes the points.
   * 
   * @param w width of window.
   * @param h height of window.
   * @param n number of points.
   */
  public KDTreeVisualization(int w, int h, int n) {
    
    this.w=w;
    this.h=h;
    this.n=n;
    
    this.kdRoot = null;
  }
  
  /**
   * Initializes the image
   */
  public void init(){
    img = createImage(w, h);
    gi = (Graphics2D)img.getGraphics();
    gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }
  
  /**
   * create and show a set of randomly generated points
   */
  public void initPoints(){
	  points = this.createPoints(n);
	  this.visualizePoints();
  }
  
  /**
   * Initialize points by distributing them randomly.
   */
  public LinkedList<Point> createPoints(int size){
    LinkedList<Point> p = new LinkedList<Point>();
    for(int i=0; i< size; i++) {
      p.add(new Point(Math.round((float)Math.random()*w)-1,Math.round((float)Math.random()*h)-1));
    }
    return p;
  }
  
  /**
   * Searches the nearest neighbor for x points
   * @param x number of points to search
   * @param mode data structure to use (0: list, 1: kd-tree)
   */
  public void searchNN(int x, int mode){
    
	  LinkedList<Point> searchPoints = createPoints(x);
	  Timer t = new Timer();
	  t.reset();
	  Iterator<Point> it = searchPoints.iterator();
	  while(it.hasNext())
	  {
		  Point p = it.next();
		  Point q;
      
		  switch(mode){
		  	case 0: 
		  		q=this.listSearchNN(p);
		  		break;
		  	case 1:
		  		q=this.treeSearchNN(p);
		  }
	  }
	  System.out.printf("Number of points searched: %d, Time: %dms\n", x, t.timeElapsed());
  }
  
  /**
   * starts creation of the kd-Tree
   */
  public void createKDTree(){
	  //similar to the lecture, we create a recursive function "createKDHelperFunc"
	  kdRoot = createKDHelperFunc(points, 0);
  }
  
  private TreeNode createKDHelperFunc(LinkedList<Point> pts, int axis) {
	  //terminate if given point set is empty
	  if (pts.size() == 0) {
		  return null;
	  }
	  //sort points along x or y axis
	  Collections.sort(pts, new PointComparator(axis));
	  //get their median
	  int median = pts.size() / 2;

	  //cycle axis
	  if (axis == 0) {
		  axis = 1;
	  }
	  else {
		  axis = 0;
	  }
	  //split tree in node, left and right part
	  TreeNode node = new TreeNode(pts.get(median));
	  node.left = createKDHelperFunc(new LinkedList<Point>(pts.subList(0, median)), axis);
	  node.right = createKDHelperFunc(new LinkedList<Point>(pts.subList(median+1, pts.size())), axis);
	  return node;
	  
  }
    
  /**
   * searches the nearest neighbor of a point in a 
   * list of points
   * @param p the point for which to search
   * @return the nearest neighbor of p
   */
  private Point listSearchNN(Point p){
	  //position is an iterator for our linked list
	  Iterator<Point> position = points.iterator();
	  
	  //define variables
	  Point candidate = points.getFirst();
	  Point candidate1;
	  double distance = Double.POSITIVE_INFINITY;
	  double distance1;
	  
	  //search point by point for the one with the shortest distance
	  while (position.hasNext()) {
		  candidate1 = position.next();
		  distance1 = p.distanceSq(candidate1);
		  if (distance1 < distance) {
			  distance = distance1;
			  candidate = candidate1;
		  }
	  }
	  //to guess, if result is correct (color nearest point red, to be searched points in orange)
	  /*
	  gi.setColor(Color.ORANGE);
	  gi.fillOval(p.x-2, p.y-2,5,5);
	  gi.setColor(Color.BLACK);
	  gi.setColor(Color.RED);
	  gi.fillOval(candidate.x-2, candidate.y-2,5,5);
	  gi.setColor(Color.BLACK);
	  this.repaint();
	  */
	  return candidate;
  }
  
  /**
   * searches the nearest neighbor of a point in a kd-tree
   * @param p the point for which to search
   * @return the nearest neighbor of p
   */
  private Point treeSearchNN(Point p){
	Point result;
    result = treeSearchNNHelperFunc(p, kdRoot, 0, null);
    
	//to guess, if result is correct (color nearest point red, to be searched points in orange)
	/*
	gi.setColor(Color.ORANGE);
	gi.fillOval(p.x-2, p.y-2,5,5);
	gi.setColor(Color.BLACK);
	gi.setColor(Color.RED);
	gi.fillOval(result.x-2, result.y-2,5,5);
	gi.setColor(Color.BLACK);
	this.repaint();
	*/
    return result;
  }
  
  private Point treeSearchNNHelperFunc(Point p, TreeNode node, int axis, Point candidate) {
	  //search k-d tree for nearest point like in the lecture
	  double candidate_distance = Double.POSITIVE_INFINITY;
	  
	  //skip this first time, since we don't yet have a candidate
	  if (candidate != null) {
		  candidate_distance = p.distanceSq(candidate);
	  }
	  
	  PointComparator comp = new PointComparator(axis);
	  
	  //cycle axis
	  if (axis == 0) {
		  axis = 1;
	  }
	  else {
		  axis = 0;
	  }
	  
	  //get distance in x or y between current node and to be searched point
	  double axis_distance = comp.signedDistance(node.position, p);
	  
	  //split "branches" into one including p and one which doesn't
	  TreeNode include, exclude;
	  
	  if (axis_distance >= 0.0) {
		  include = node.left;
		  exclude = node.right;
	  }
	  else {
		  include = node.right;
		  exclude = node.left;
	  }
	  
	  //first, we traverse the k--d tree until we reach a "leaf"
	  if (include != null) {
		  candidate = treeSearchNNHelperFunc(p, include, axis, candidate);
		  candidate_distance = p.distanceSq(candidate);
	  }
	  
	  //traverse tree back from leaf to root
	  //now get the distance between current node and point, and adapt the candidate for nearest point if necessary
	  double current_node_distance = p.distanceSq(node.position);
	  if (current_node_distance < candidate_distance) {
		  candidate = node.position;
		  candidate_distance = current_node_distance;
	  }
	  
	  //check, if there could be possible closer points in the other tree part
	  if (exclude != null) {
		  double minimum_distance_to_exclude = axis_distance * axis_distance;
		  if (minimum_distance_to_exclude < candidate_distance) {
			  candidate = treeSearchNNHelperFunc(p, exclude, axis, candidate);
		  }
	  }
	  
	  return candidate;
  }
  
  /**
   * Visualizes the points in the list
   */
  public void visualizePoints(){
    gi.clearRect(0, 0, w, h);
    
    Iterator<Point> it = points.iterator();
    while(it.hasNext())
    {
      Point p = it.next();
      gi.fillOval(p.x-2, p.y-2,5,5);
    }
    this.repaint();
  }
  
  /**
   * Visualizes the order of the points in the list
   *
   */
  public void visualizeList(){
    gi.clearRect(0, 0, w, h);
    
    Point old= new Point(0,0);
    Iterator<Point> it = points.iterator();
    if(it.hasNext()){
      old=it.next();
      gi.setColor(Color.RED);
      gi.fillOval(old.x-2, old.y-2,5,5);
    }
    while(it.hasNext())
    {
      Point p = it.next();
      gi.setColor(Color.BLACK);
      gi.fillOval(p.x-2, p.y-2,5,5);
      gi.setColor(Color.BLUE);
      gi.drawLine(old.x, old.y,p.x,p.y);
      gi.setColor(Color.BLACK);
      old = p;
    }
    this.repaint();
  }
  
  /**
   * starter for the kd-tree visualization
   */
  public void visualizeTree(){
	  gi.clearRect(0, 0, w, h);
      visualize(this.kdRoot, 0, 0, w, 0, h);
      this.repaint();
  }
  
  /**
   * Visualizes the kd-tree
   * @param n TreeNode
   * @param depth depth in the tree
   * @param left left border of the sub-image
   * @param right right border of the sub-image
   * @param top top border of the sub-image
   * @param bottom bottom border of the sub-image
   */
  private void visualize(TreeNode n, int depth, int left, int right, int top, int bottom){
	  if(n != null){
		  int axis = depth%2;
		  if(axis == 0){
			  gi.setColor(Color.BLUE);
			  gi.drawLine(n.position.x, top, n.position.x, bottom);
			  gi.setColor(Color.BLACK);
			  gi.fillOval(n.position.x-2, n.position.y-2, 5, 5);
			  visualize(n.left, depth+1, left, n.position.x, top, bottom);
			  visualize(n.right, depth+1, n.position.x, right, top, bottom);
		  }else {
			  gi.setColor(Color.RED);
			  gi.drawLine(left, n.position.y, right, n.position.y);
			  gi.setColor(Color.BLACK);
			  gi.fillOval(n.position.x-2, n.position.y-2, 5, 5);
			  visualize(n.left, depth+1, left,right , top,n.position.y);
			  visualize(n.right, depth+1, left ,right , n.position.y, bottom);
		  }
	  }
  }
  
  
  /**
   * Paint the image
   */
    public void paint(Graphics g)
    { 
      g.drawImage(img, 0, 0, null);
    }
    
  public Dimension getPreferredSize() {
    return new Dimension(w, h);
  }

  /**
   * Node in the kd-Tree
   */
  private class TreeNode
  {
    private TreeNode left, right;    // Pointers to left and right child
    private Point position;          // Position of the Point
    
    TreeNode(Point point)
    {
      this.position = point;
    }
  }
}
