package edu.toronto.csc301.robot;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.toronto.csc301.grid.GridCell;

public class GridRobot implements IGridRobot {

	
	// =========================== Static Helper(s) ===========================
	
	
	public static GridCell oneCellOver(GridCell location, Direction direction){
		switch (direction) {
		case NORTH:
			return GridCell.at(location.x, location.y + 1);
		case EAST:
			return GridCell.at(location.x + 1, location.y);
		case SOUTH:
			return GridCell.at(location.x, location.y - 1);
		case WEST:
			return GridCell.at(location.x - 1, location.y);
		default:
			return null;
		}
	}
	
	
	// ========================================================================
	
	
	
	private GridCell location;
	private long delayInMilliseconds;
	private Set<StepListener> stepListeners;
	private Lock lock;
	
	
	public GridRobot(GridCell initialLocation, long delayInMilliseconds) {
		Objects.requireNonNull(initialLocation);
		this.location = initialLocation;
		this.delayInMilliseconds = delayInMilliseconds;
		this.stepListeners = new HashSet<StepListener>();
		this.lock = new ReentrantLock();
	}
	
	public GridRobot(GridCell initialLocation) {
		this(initialLocation, 500);   // Default delay is half a second
	}
	
	
	
	
	@Override
	public GridCell getLocation() {
		this.lock.lock();
		GridCell retLocation = location;
		this.lock.unlock();
		return retLocation;
	}


	@Override
	public void step(Direction direction) {
		for(StepListener listener : stepListeners){
			listener.onStepStart(this, direction);
		}
		
		// Simulate a the time it takes for a robot to move by sleeping 
		try {
			Thread.sleep(delayInMilliseconds);
		} catch (InterruptedException e) { }
        this.lock.lock();
		location = GridRobot.oneCellOver(location, direction);
		this.lock.unlock();
		for(StepListener listener : stepListeners){
			listener.onStepEnd(this, direction);
		}
	}
	


	@Override
	public void startListening(StepListener listener) {
		stepListeners.add(listener);
	}

	@Override
	public void stopListening(StepListener listener) {
		stepListeners.remove(listener);
	}

}
