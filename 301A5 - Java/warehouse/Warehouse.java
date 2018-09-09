package edu.toronto.csc301.warehouse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.GridRobot;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class Warehouse implements IWarehouse, IGridRobot.StepListener {
	
	//The grid for the warehouse
	private IGrid<Rack> grid;
	//A set of listeners to notify when we get changes
	private Set<Consumer<IWarehouse>> listeners;
	//A set of all the robots in the warehouse
	private Set<IGridRobot> robots;
	//A map of all moving robots in the warehouse
	private Map<IGridRobot, Direction> movingBots;
	private Lock lock;
	
	public Warehouse(IGrid<Rack> grid){
		if(grid == null){
			throw new NullPointerException();
		}
		this.grid = grid;
		this.listeners = new HashSet<Consumer<IWarehouse>>();
		this.robots = new HashSet<IGridRobot>();
		this.movingBots = new HashMap<IGridRobot, Direction>();
		this.lock = new ReentrantLock();
    }
	
	//Add robot to map of moving robots
	@Override
	public void onStepStart(IGridRobot robot, Direction direction) {
		this.lock.lock();
		this.movingBots.put(robot, direction);
		this.lock.unlock();
		this.updateAll();
	}

	//Remove robot from map of moving robots
	@Override
	public void onStepEnd(IGridRobot robot, Direction direction) {
		this.lock.lock();
		this.movingBots.remove(robot);
		this.lock.unlock();
		this.updateAll();
		
	}

	//Returns the grid for the warehouse
	@Override
	public IGrid<Rack> getFloorPlan() {
		return this.grid;
	}


	//Adds a robot to the hashset of robots
	@Override
	public IGridRobot addRobot(GridCell initialLocation) {
		//Check if robot is outside grid
		if(!this.grid.hasCell(initialLocation)){
			throw new IllegalArgumentException();
		}
		//Check if there's already a robot at initialLocation
		Iterator<IGridRobot> it = this.getRobots();
		GridCell c;
		while(it.hasNext()){
			c = (GridCell) it.next().getLocation();
			if(c.x == initialLocation.x && c.y == initialLocation.y){
				throw new IllegalArgumentException();
			}
		}
		//Add the new robot
		GridRobot robo = new GridRobot(initialLocation);
		robo.startListening(this);
		this.robots.add(robo);
		this.updateAll();
		return robo;
    }
	
	//Returns an iterator for the hashset of robots
	@Override
	public Iterator<IGridRobot> getRobots() {
		return this.robots.iterator();
    }


	//Returns the map of moving robots
	@Override
	public Map<IGridRobot, Direction> getRobotsInMotion() {
		this.lock.lock();
		Map<IGridRobot, Direction> retMap = 
				new HashMap<IGridRobot, Direction>();
		
		for (IGridRobot r : this.movingBots.keySet()){
			retMap.put(r, this.movingBots.get(r));
		}
		this.lock.unlock();
		return retMap;
    }


	//Adds the observer
	@Override
	public void subscribe(Consumer<IWarehouse> observer) {
		this.listeners.add(observer);
    }

	//Removes the observer
	@Override
	public void unsubscribe(Consumer<IWarehouse> observer) {
		this.listeners.remove(observer);
    }
	
	//Function for notifying observers
	public void updateAll(){
		for (Consumer<IWarehouse> c : this.listeners){
			c.accept(this);
		}
	}
	
	public Lock getLock(){
		return this.lock;
	}
	
	/**
	 * Adds a given robot and direction to the map of moving robots
	 */
	public void addMovingRobot(IGridRobot r, Direction d){
		this.lock.lock();
		this.movingBots.put(r, d);
		this.lock.unlock();
	}
}
