package edu.toronto.csc301.warehouse;

import java.util.Map;

import edu.toronto.csc301.grid.GridCell;
import edu.toronto.csc301.robot.IGridRobot;

//Interface for the controller. Contains a method which
//starts moving the robots to their destination
//and the getters
public interface IController {
    
	public int startMoving() throws RobotCrashException;
	
	public IWarehouse getWarehouse();
	
	public Map<IGridRobot, GridCell> getRobot2Dest();
	
}
