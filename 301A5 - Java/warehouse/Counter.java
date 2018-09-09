package edu.toronto.csc301.warehouse;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import edu.toronto.csc301.robot.IGridRobot;

/**
 * A consumer which the multithreadcontroller will use.
 * Contains a counter which represents the number of robots that have
 * reached their destination. This updates everytime the warehouse is updated.
 * The warehouse is updated everytime a robot finishes moving or starts moving.
 *
 * @param <T> 
 */
public class Counter<T> implements Consumer<T>{

	private MultiThreadController controller;
	private int counter;
	private Lock lock;
	
	public Counter(MultiThreadController m){
		this.controller = m;
		this.counter = 0;
		this.lock = new ReentrantLock();
	}
	
	@Override
	public void accept(T warehouse) {
		this.lock.lock();
		this.counter = 0;
		for(IGridRobot robot : this.controller.getRobot2Dest().keySet()){
    		if(robot.getLocation().equals(this.controller.getRobot2Dest().get(robot))){
    			this.counter++;
    		}
    	}
		this.lock.unlock();
	}
	
	public int getCount(){
		this.lock.lock();
		int retCount = this.counter;
		this.lock.unlock();
		return retCount;
	}
    
}
