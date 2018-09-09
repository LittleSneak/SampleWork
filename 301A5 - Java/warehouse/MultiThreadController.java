package edu.toronto.csc301.warehouse;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.GridRobot;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

/**
 * A controller which will move robots of a given warehouse to 
 * their destinations given by a map. This will use threads.
 *
 */
public class MultiThreadController implements IController{
	
	//A map of all the robots to their destinations
	private Map<IGridRobot, GridCell> robot2dest;
	//The warehouse the robots are in
	private IWarehouse warehouse;
	//A counter to keep track of how many robots have reached 
	//their destination
	private Counter<IWarehouse> c;
	private IPathPlanner pathplanner;
	
	/**
	 * Constructor for the c lass
	 * 
	 * @param w The warehouse the robots are in
	 * @param map A map of robots to their destination cells
	 */
    public MultiThreadController(IWarehouse w,
    		Map<IGridRobot, GridCell> map, IPathPlanner p){
    	
    	this.robot2dest = map;
    	this.warehouse = w;
    	this.pathplanner = p;
    	if(this.robot2dest == null || this.warehouse == null ||
    			this.pathplanner == null){
    		
    		throw new IllegalArgumentException();
    	}
    	
    	this.c = new Counter<IWarehouse>(this);
    	this.warehouse.subscribe(c);
    }
    
    /**
     * Moves all robots to their destinations and returns the number
     * of total steps taken
     * @throws RobotCrashException 
     */
    public int startMoving() throws RobotCrashException{
    	//holds direction to step in
    	Direction direction;
    	//holds the robot about to move
    	IGridRobot robot;
    	//holds the return of pathplanner
    	Entry<IGridRobot, Direction> toMove;
    	//The task that will be run in a new thread
    	Task runnable;
    	//Pool of threads
    	ExecutorService es = 
    			new ScheduledThreadPoolExecutor(this.robot2dest.size());
    	
    	//Holds the gridcell a robot is about to step to
    	GridCell newCell;
    	//A map of currently moving robots
    	Map<IGridRobot, Direction> movingBots;
    	int steps = 0;
    	
    	//Keep moving robots until the number that reached the destination
    	//is equal to the number of robots we are moving
    	while(this.c.getCount() < this.robot2dest.size()){
    		toMove = 
    				this.pathplanner.nextStep(this.warehouse, this.robot2dest);
    		
    		if(toMove != null){
    			robot = toMove.getKey();
        		direction = toMove.getValue();
        		newCell = GridRobot.oneCellOver(robot.getLocation(),
        				direction);
        		
        		//Check if the robot is about to crash into another robot
        		for(IGridRobot r : this.robot2dest.keySet()){
        			if(newCell.equals(r.getLocation())){
        				throw new RobotCrashException();
        			}
        		}
        		//Check if they are stepping onto where another robot is stepping
        		movingBots = warehouse.getRobotsInMotion();
        		for(IGridRobot r : movingBots.keySet()){
        			if(GridRobot.oneCellOver(r.getLocation(),
        					movingBots.get(r)).equals(newCell)){
        				
        				throw new RobotCrashException();
        			}
        		}
        		
        		//Move the robot in a new thread
        		this.warehouse.addMovingRobot(robot, direction);
        		steps++;
        		runnable = new Task(robot, direction);
        		CompletableFuture.runAsync(runnable, es);
    		}
    	}
    	return steps;
    }
    
    //A getter for the warehouse
    public IWarehouse getWarehouse(){
    	return this.warehouse;
    }
    
    //A getter for the robot2dest
    public Map<IGridRobot, GridCell> getRobot2Dest(){
    	return this.robot2dest;
    }
    
    /**
     * A class for the object that is passed into a new thread to run
     *
     */
    public class Task implements Runnable{
    	
    	private IGridRobot robot;
    	private Direction direction;
        public Task(IGridRobot r, Direction d){
        	this.robot = r;
        	this.direction = d;
        }
    	
		@Override
		public void run() {
			this.robot.step(this.direction);
		}
    }
}
