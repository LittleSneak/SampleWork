package edu.toronto.csc301.warehouse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.grid.IGrid;
import edu.toronto.csc301.robot.GridRobot;
import edu.toronto.csc301.robot.IGridRobot;
import edu.toronto.csc301.robot.IGridRobot.Direction;

public class PathPlanner implements IPathPlanner{

	/**
	 * Returns an entry of a robot to direction which represents the direction the given
	 * robot should step.
	 * 
	 * @param warehouse The warehouse the robots are in
	 * @param robot2dest A map of all robots to their destination gridcells
	 */
	public Entry<IGridRobot, Direction> nextStep(IWarehouse warehouse, Map<IGridRobot, GridCell> robot2dest) {
		//Get the robot
		IGridRobot robo = null;
		IGridRobot adjRobo;
		//The direction to step in
		Direction d = null;
		//The entry being returned
		Entry<IGridRobot, Direction> retEntry;
		Map<IGridRobot, Direction> robotsInMotion = warehouse.getRobotsInMotion();
		GridCell dest;
		Direction d2 = null;
		
		//Find a robot that has not reached its destination and is
		//not moving
		warehouse.getLock().lock();
		for(IGridRobot robot : robot2dest.keySet()){
			//Nonmoving robot that has not reached its destination
			if(!robot.getLocation().equals(robot2dest.get(robot)) 
					&& !robotsInMotion.containsKey(robot)){
				dest = robot2dest.get(robot);
				d = breadthFirstSearch(robot, warehouse, dest, robot2dest);
				//If there is no robot where we want to step, then we can just move this robot
				adjRobo = isThereARobot(warehouse, 
						GridRobot.oneCellOver(robot.getLocation(), d),
						robotsInMotion);
				
				if(adjRobo != null){
					d2 = breadthFirstSearch(adjRobo, warehouse, 
							robot2dest.get(adjRobo), robot2dest);
				}
				if(adjRobo == null){
					robo = robot;
					break;
				}
				//Trying to move to a robot that has reached its destination
				else if(robot2dest.get(adjRobo).equals(adjRobo.getLocation())&&
						!robotsInMotion.containsKey(adjRobo)){
					robo = adjRobo;
					d = getRandomDirection(warehouse, adjRobo,
							robotsInMotion, d);
					break;
				}
				//Trying to move to a robot that is trying to move to  where we are
				else if((Math.abs((d.ordinal() - d2.ordinal())) == 2 
								&& !robotsInMotion.containsKey(adjRobo))){
					
					d = getRandomDirection(warehouse, robot, robotsInMotion,
							d2);
					robo = robot;
					break;
				}
			}
		}
		warehouse.getLock().unlock();
		//All robots are either in motion or reached destination
		if (robo == null){
			return null;
		}
		retEntry = new HashMap.SimpleEntry<IGridRobot, Direction>(robo, d);
		return retEntry;
	}
	
	/**
	 *  Returns whether or not a given gridcell in a warehouse has an
	 *  obstruction. An obstruction is a wall or non-moving robot
	 * 
	 * @param w The warehouse that is being checked
	 * @param c The gridcell that is checked to see if it's obstructed
	 * @return True if there is an obstruction
	 */
	public boolean isObstructed(IWarehouse w, GridCell c, 
			Map<IGridRobot, GridCell> robot2dest){
		
		IGridRobot r;
		if(!w.getFloorPlan().hasCell(c)){
			return true;
		}
		Iterator<IGridRobot> it = w.getRobots();
		while(it.hasNext()){
			r = it.next();
			if(r.getLocation().equals(c) && !robot2dest.containsKey(r)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Return true if there is a robot on the given gridcell of the given
	 * warehouse and false otherwise.
	 * 
	 * @param w The warehouse being checked
	 * @param gc The gridcell being checked
	 * @param movingBots A map where a key is a robot and the value is the direction
	 *                   the robot is moving in
	 * @return true or false depending on whether there is a robot at
	 *         the gridcell
	 */
	public IGridRobot isThereARobot(IWarehouse w, GridCell gc,
			Map<IGridRobot, Direction> movingBots){
		
		Iterator<IGridRobot> it = w.getRobots();
		IGridRobot robot;
		//Check if any robots are on the gridcell we want
		while(it.hasNext()){
			robot = it.next();
			if(robot.getLocation().equals(gc)){
				return robot;
			}
		}
		//Check if any robots are stepping onto the gridcell we want
		for(IGridRobot r : movingBots.keySet()){
			if(GridRobot.oneCellOver(r.getLocation(),
					movingBots.get(r)).equals(gc)){
				
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Returns a direction that the robot can move to
	 * 
	 * @param w The warehouse
	 * @param r The robot that will be moved
	 * @param robotsInMotion A map which represents moving robots
	 * @param dir The direction an adjacent robot is moving to
	 * @return A direction that a robot can step in
	 */
	public Direction getRandomDirection(IWarehouse w, IGridRobot r,
			Map<IGridRobot, Direction> robotsInMotion, Direction dir){
		IGrid g = w.getFloorPlan();
		GridCell gc;
		Iterator<IGridRobot> it;
		boolean passed = true;
		
		for(Direction d : Direction.values()){
			gc = GridRobot.oneCellOver(r.getLocation(), d);
			//Only check if there's a robot if the cell is on the grid
			if(g.hasCell(gc)){
				it = w.getRobots();
			    //Check if any robots are in the direction
			    while(it.hasNext()){
				    if(it.next().getLocation().equals(gc)){
					    passed = false;
				    }
			    }
			    for(IGridRobot robot : robotsInMotion.keySet()){
			    	if(GridRobot.oneCellOver(robot.getLocation(), 
			    			robotsInMotion.get(robot)).equals(gc)){
			    		passed = false;
			    	}
			    }
			    if(passed && d != dir){
			    	return d;
			    }
			    passed = true;
			}
			
		}
		return dir;
	}
	
	/**
	 * Returns the direction a given robot should move which is calculated by BFS
	 * 
	 * @param r The robot that is being moved
	 * @param warehouse The warehouse the robot is in
	 * @param dest The destination cell for the robot
	 * @return The Direction the robot should move
	 */
	public Direction breadthFirstSearch(IGridRobot r, IWarehouse warehouse,
			GridCell dest, Map<IGridRobot, GridCell> robot2dest){
		
		Queue<GridCell> q = new LinkedList<GridCell>();
		GridCell start = r.getLocation();
		Map<GridCell, GridCell> map = new HashMap<GridCell, GridCell>();
		GridCell exploring;
		GridCell holder;
		Direction d = null;
		
		q.add(start);
		while(!q.isEmpty()){
			exploring = q.remove();
			//Explore north if not obstructed
			if(!isObstructed(warehouse, GridCell.at(exploring.x,
					exploring.y + 1), robot2dest)){
				
				holder = GridCell.at(exploring.x, exploring.y + 1);
				if(!map.containsKey(holder)){
					q.add(holder);
				    map.put(holder, exploring);
				}
			}
			//Explore south if not obstructed
			if(!isObstructed(warehouse, GridCell.at(exploring.x,
					exploring.y - 1), robot2dest)){
				
				holder = GridCell.at(exploring.x, exploring.y - 1);
				if(!map.containsKey(holder)){
					q.add(holder);
				    map.put(holder, exploring);
				}
			}
			//Explore east if not obstructed
			if(!isObstructed(warehouse, GridCell.at(exploring.x + 1,
					exploring.y), robot2dest)){
				
				holder = GridCell.at(exploring.x + 1, exploring.y);
				if(!map.containsKey(holder)){
					q.add(holder);
				    map.put(holder, exploring);
				}
			}
			//Explore west if not obstructed
			if(!isObstructed(warehouse, GridCell.at(exploring.x - 1,
					exploring.y), robot2dest)){
				
				holder = GridCell.at(exploring.x - 1, exploring.y);
				if(!map.containsKey(holder)){
					q.add(holder);
				    map.put(holder, exploring);
				}
			}
		}
		
		//Work backwards from destination to find where to step
		holder = dest;
		while(!(start.equals(map.get(holder)))){
			holder = map.get(holder);
		}
		if(holder.x == start.x && holder.y == start.y + 1){
			d = Direction.NORTH;
		}
		else if(holder.x == start.x && holder.y == start.y - 1){
			d = Direction.SOUTH;
		}
		else if(holder.x == start.x + 1 && holder.y == start.y){
			d = Direction.EAST;
		}
		else{
			d = Direction.WEST;
		}
		return d;
	}
}