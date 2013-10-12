package raycasting;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import main.Game;
import main.SVector2D;
import main.Util;
import map.Chunk;
import map.ChunkManager;
import map.Map;

import entities.Entity;
import entities.EntityManager;

public class RayCaster {
	public static RayCollisionProfile castRay (SVector2D position, double angle, boolean doesDamage, Entity[] ignoreArray, Rectangle2D[] ignoredWalls) {
		ArrayList<Entity> ignoreList = new ArrayList<Entity>();
		ArrayList<Rectangle2D> ignoreWalls = new ArrayList<Rectangle2D>();
		for (int a = 0; a < ignoreArray.length; a++) {
			ignoreList.add(ignoreArray[a]);
		}
		for (int a = 0; a < ignoredWalls.length; a++) {
			ignoreWalls.add(ignoredWalls[a]);
		}
		ArrayList<Point2D.Double> collisionPoints = new ArrayList<Point2D.Double>();
		ArrayList<Rectangle2D.Double> collisionRects = new ArrayList<Rectangle2D.Double>();
		ArrayList<SVector2D> collisionFace = new ArrayList<SVector2D>();
		SVector2D direction = SVector2D.createVectorGeometrically(angle, 1);
		ArrayList<Chunk> chunkList = intersectChunks(position, direction, angle);
		
		Set<Rectangle2D.Double> wallSet = new HashSet<Rectangle2D.Double>();
		Set<Entity> entitySet = new HashSet<Entity>();
		for (Chunk c : chunkList) {
			wallSet.addAll(c.wallList);
			entitySet.addAll(c.entityList);
		}
		for(Iterator<Rectangle2D.Double> i = wallSet.iterator(); i.hasNext();) {
			Rectangle2D.Double rec = i.next();
			if (!ignoreWalls.contains(rec)) {
				Point2D initialPosition = new Point2D.Double(position.x, position.y);
				double m = Math.tan(angle);
				double b = initialPosition.getY() - m * initialPosition.getX();
				int outcode = rec.outcode(initialPosition);
				double cx = Double.NaN, cy = Double.NaN;
				/*
				 * W      = 1
				 * NW     = 3
				 * N      = 2
				 * NE	  = 6
				 * E      = 4
				 * SE     = 12
				 * S      = 8
				 * SW     = 9
				 * INSIDE = 0
				 */
				SVector2D face = SVector2D.createVectorAlgebraically(100, 100);
				if (outcode == 0) {
					return null;
				} else if (outcode == 1) {
					if (direction.x > 0) {
						double collisionY = m * rec.getMinX() + b;
						if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
							cx = rec.getMinX();
							cy = collisionY;
							face = SVector2D.createVectorAlgebraically(-1, 0);
						}
					}
				} else if (outcode == 3) {
					if (direction.x > 0 && direction.y > 0) {
						double collisionY = m * rec.getMinX() + b;
						if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
							cx = rec.getMinX();
							cy = collisionY;
							face = SVector2D.createVectorAlgebraically(-1, 0);
						} else {
							double collisionX = (rec.getMinY() - b) / m;
							if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
								cx = collisionX;
								cy = rec.getMinY();
								face = SVector2D.createVectorAlgebraically(0, -1);
							}
						}
					}
				} else if (outcode == 2) {
					if (direction.y > 0) {
						double collisionX = (rec.getMinY() - b) / m;
						if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
							cx = collisionX;
							cy = rec.getMinY();
							face = SVector2D.createVectorAlgebraically(0, -1);
						}
					}
				} else if (outcode == 6) {
					if (direction.x < 0 && direction.y > 0) {
						double collisionY = m * rec.getMaxX() + b;
						if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
							cx = rec.getMaxX();
							cy = collisionY;
							face = SVector2D.createVectorAlgebraically(1, 0);
						} else {
							double collisionX = (rec.getMinY() - b) / m;
							if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
								cx = collisionX;
								cy = rec.getMinY();
								face = SVector2D.createVectorAlgebraically(0, -1);
							}
						}
					}
				} else if (outcode == 4) {
					if (direction.x < 0) {
						double collisionY = m * rec.getMaxX() + b;
						if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
							cx = rec.getMaxX();
							cy = collisionY;
							face = SVector2D.createVectorAlgebraically(1, 0);
						}
					}
				} else if (outcode == 12) {
					if (direction.x < 0 && direction.y < 0) {
						double collisionY = m * rec.getMaxX() + b;
						if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
							cx = rec.getMaxX();
							cy = collisionY;
							face = SVector2D.createVectorAlgebraically(1, 0);
						}
						double collisionX = (rec.getMaxY() - b) / m;
						if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
							cx = collisionX;
							cy = rec.getMaxY();
							face = SVector2D.createVectorAlgebraically(0, 1);
						}

					}
				} else if (outcode == 8) {
					if (direction.y < 0) {
						double collisionX = (rec.getMaxY() - b) / m;
						if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
							cx = collisionX;
							cy = rec.getMaxY();
							face = SVector2D.createVectorAlgebraically(0, 1);
						}
					}
				} else if (outcode == 9) {
					if (direction.x > 0 && direction.y < 0) {
						double collisionY = m * rec.getMinX() + b;
						if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
							cx = rec.getMinX();
							cy = collisionY;
							face = SVector2D.createVectorAlgebraically(-1, 0);
						} else {
							double collisionX = (rec.getMaxY() - b) / m;
							if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
								cx = collisionX;
								cy = rec.getMaxY();
								face = SVector2D.createVectorAlgebraically(0, 1);
							}
						}
					}
				}
				if (cx != Double.NaN && cy != Double.NaN) {
					collisionPoints.add(new Point2D.Double(cx, cy));
					collisionRects.add(rec);
					collisionFace.add(face);
				}
			}
		}
		int closestID = 0;
		double closestIDDistance = Double.POSITIVE_INFINITY;
		for (int a = 0; a < collisionPoints.size(); a++) {
			double currentIDDistance = Util.distance(collisionPoints.get(a).getX(), collisionPoints.get(a).getY(), position.x, position.y);
			if (closestIDDistance > currentIDDistance) {
				closestID = a;
				closestIDDistance = currentIDDistance;
			}
		}
		Line2D bulletToWallLine = new Line2D.Double(position.x, position.y, collisionPoints.get(closestID).getX(), collisionPoints.get(closestID).getY());
		double cx = collisionPoints.get(closestID).getX();
		double cy = collisionPoints.get(closestID).getY();
		collisionPoints.clear();
		ArrayList<EntityCollisionProfile> entityCollisions = new ArrayList<EntityCollisionProfile>();
		for (Iterator<Entity> i = entitySet.iterator(); i.hasNext();) {
			Entity entity = i.next();
			if (!ignoreList.contains(entity)) { // is the entity an enemy
				boolean lineCollision = false;
				lineCollision = bulletToWallLine.intersects(entity.boundingBox);
				double x1 = bulletToWallLine.getX1() - entity.position.x;
				double y1 = bulletToWallLine.getY1() - entity.position.y;
				double x2 = bulletToWallLine.getX2() - entity.position.x;
				double y2 = bulletToWallLine.getY2() - entity.position.y;
				double dx = x2 - x1;
				double dy = y2 - y1;
				double dr = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
				double D = (x1 * y2) - (x2 * y1);
				double r = entity.boundingBox.getWidth() / 2.0;
				double delta = Math.pow(r, 2) * Math.pow(dr, 2) - Math.pow(D, 2);
				if (delta >= 0) {
					double ix1 = ((D * dy + sgn(dy) * dx * Math.sqrt(delta)) / (Math.pow(dr, 2))) + entity.position.x;
					double ix2 = ((D * dy - sgn(dy) * dx * Math.sqrt(delta)) / (Math.pow(dr, 2))) + entity.position.x;
					double iy1 = ((-D * dx + Math.abs(dy) * Math.sqrt(delta)) / (Math.pow(dr, 2))) + entity.position.y;
					double iy2 = ((-D * dx - Math.abs(dy) * Math.sqrt(delta)) / (Math.pow(dr, 2))) + entity.position.y;
					double distance1 = Util.distance(position.x, position.y, ix1, iy1);
					double distance2 = Util.distance(position.x, position.y, ix2, iy2);
					if (lineCollision) {
						if (distance1 > distance2) {
							entityCollisions.add(new EntityCollisionProfile(SVector2D.createVectorAlgebraically(ix2, iy2), SVector2D.createVectorAlgebraically(ix1, iy1), entity, distance2));
						} else {
							entityCollisions.add(new EntityCollisionProfile(SVector2D.createVectorAlgebraically(ix1, iy1), SVector2D.createVectorAlgebraically(ix2, iy2), entity, distance1));
						}
					}
				}
			}
		}
		Collections.sort(entityCollisions);
		if (entityCollisions.size() != 0) {
			return new RayCollisionProfile(SVector2D.createVectorAlgebraically(cx, cy), entityCollisions.get(0).positionClosest, entityCollisions, collisionRects.get(closestID), collisionFace.get(closestID));
		} else {
			return new RayCollisionProfile(SVector2D.createVectorAlgebraically(cx, cy), SVector2D.createVectorAlgebraically(cx, cy), entityCollisions, collisionRects.get(closestID), collisionFace.get(closestID));
		}
	}
	public static byte sgn( double x ) {
		if (x < 0) {
			return -1;
		} else {
			return 1;
		}
	}
	private static ArrayList<Chunk> intersectChunks(SVector2D position, SVector2D direction, double angle) {
		ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
		for (Chunk c : ChunkManager.chunkList) {
			Rectangle2D rec = c.bounds;
			Point2D initialPosition = new Point2D.Double(position.x, position.y);
			double m = Math.tan(angle);
			double b = initialPosition.getY() - m * initialPosition.getX();
			int outcode = rec.outcode(initialPosition);
			/*
			 * W      = 1
			 * NW     = 3
			 * N      = 2
			 * NE	  = 6
			 * E      = 4
			 * SE     = 12
			 * S      = 8
			 * SW     = 9
			 * INSIDE = 0
			 */
			if (outcode == 0) {
				chunkList.add(c);
			} else if (outcode == 1) {
				if (direction.x > 0) {
					double collisionY = m * rec.getMinX() + b;
					if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
						chunkList.add(c);
					}
				}
			} else if (outcode == 3) {
				if (direction.x > 0 && direction.y > 0) {
					double collisionY = m * rec.getMinX() + b;
					if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
						chunkList.add(c);
					} else {
						double collisionX = (rec.getMinY() - b) / m;
						if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
							chunkList.add(c);
						}
					}
				}
			} else if (outcode == 2) {
				if (direction.y > 0) {
					double collisionX = (rec.getMinY() - b) / m;
					if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
						chunkList.add(c);
					}
				}
			} else if (outcode == 6) {
				if (direction.x < 0 && direction.y > 0) {
					double collisionY = m * rec.getMaxX() + b;
					if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
						chunkList.add(c);
					} else {
						double collisionX = (rec.getMinY() - b) / m;
						if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
							chunkList.add(c);
						}
					}
				}
			} else if (outcode == 4) {
				if (direction.x < 0) {
					double collisionY = m * rec.getMaxX() + b;
					if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
						chunkList.add(c);
					}
				}
			} else if (outcode == 12) {
				if (direction.x < 0 && direction.y < 0) {
					double collisionY = m * rec.getMaxX() + b;
					if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
						chunkList.add(c);
					}
					double collisionX = (rec.getMaxY() - b) / m;
					if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
						chunkList.add(c);
					}

				}
			} else if (outcode == 8) {
				if (direction.y < 0) {
					double collisionX = (rec.getMaxY() - b) / m;
					if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
						chunkList.add(c);
					}
				}
			} else if (outcode == 9) {
				if (direction.x > 0 && direction.y < 0) {
					double collisionY = m * rec.getMinX() + b;
					if (collisionY >= rec.getMinY() && collisionY <= rec.getMaxY()) {
						chunkList.add(c);
					} else {
						double collisionX = (rec.getMaxY() - b) / m;
						if (collisionX >= rec.getMinX() && collisionX <= rec.getMaxX()) {
							chunkList.add(c);
						}
					}
				}
			}
		}
		return chunkList;
	}
}
