package pathfinding;

import entities.EntityEnemy;

public class slowZombie extends EntityEnemy{

	public slowZombie(int spawnX, int spawnY) {
		super(spawnX, spawnY);
		speed = 1.5;
		health = 200;
	}
	
	public slowZombie(double pointX, double pointY){
		super(pointX,pointY);
	}

}
