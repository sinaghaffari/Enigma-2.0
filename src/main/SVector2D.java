package main;


public class SVector2D {
	public double angle = 0;
	public double x = 0, y = 0;
	public double magnitude = 0;
	public static SVector2D createVectorAlgebraically( double x, double y ) {
		return new SVector2D( x, y, 0 );
		
	}
	public static SVector2D createVectorGeometrically( double angle, double length ) {
		return new SVector2D( angle, length );
	}
	public static SVector2D createZeroVector() {
		return new SVector2D();
	}
	public static SVector2D copyVector( SVector2D vec ) {
		return new SVector2D( vec );
	}
	
	private SVector2D() {
		
	}
	private SVector2D( double angle, double length ) {
		this.angle = angle;
		this.magnitude = length;
		calculatePosition();
	}
	private SVector2D( double x, double y, double zero ) {
		this.x = x;
		this.y = y;
		calculateMagnitude();
		calculateAngles();
	}
	private SVector2D( SVector2D vec ) {
		this.angle = vec.angle;
		this.magnitude = vec.magnitude;
		calculatePosition();
	}
	public void calculateMagnitude() {
		magnitude = Math.sqrt( Math.pow(x, 2) + Math.pow(y, 2) );
	}
	public void calculateAngles() {
		angle = Util.angleOfLineRad(0, 0, x, y);
		if (Double.isNaN(angle)) {
			angle = 0;
		}
	}
	public void calculatePosition( ) {
		x = magnitude * Math.cos(angle);
		y = magnitude * Math.sin(angle);
	}
	public SVector2D add( SVector2D vec ) {
		double x_p_n = this.x + vec.x;
		double y_p_n = this.y + vec.y;
		
		return new SVector2D(x_p_n, y_p_n, 0);
	}
	public SVector2D subtract( SVector2D vec ) {
		double x_p_n = this.x - vec.x;
		double y_p_n = this.y - vec.y;
		
		return new SVector2D(x_p_n, y_p_n, 0);
	}
	public SVector2D multiply( double i ) {
		double x_p_n = this.x * i;
		double y_p_n = this.y * i;
		
		return new SVector2D(x_p_n, y_p_n, 0);
	}
	public SVector2D divide( double i ) {
		double x_p_n = this.x / i;
		double y_p_n = this.y / i;
		
		return new SVector2D(x_p_n, y_p_n, 0);
	}
	public int cross(SVector2D vec) {
		double temp = x*vec.y - vec.x*y;
		if (temp < 0) {
			return -1;
		} else {
			return 1;
		}
	}
	public double dot( SVector2D vec ) {
		return ((this.x * vec.x) + (this.y * vec.y));
	}
	public double angleBetween( SVector2D vec ) {
		return Math.acos(vec.dot(this)/(this.magnitude * vec.magnitude));
	}
	public SVector2D opposite() {
		return new SVector2D(this.x * -1, this.y * -1, 0);
	}
	public SVector2D projectOnto(SVector2D vec) {
		return vec.multiply(this.dot(vec) / Math.pow(vec.magnitude, 2));
	}
	public SVector2D scale(double factor) {
		return new SVector2D(angle, magnitude * factor);
	}
	public SVector2D truncate( double limit ) {
		System.out.println("Limit: " + limit);
		if (magnitude > limit) {
			return SVector2D.createVectorGeometrically(angle, limit);
		} else {
			return this;
		}
	}
	public SVector2D normalize() {
		return SVector2D.createVectorGeometrically(this.angle, 1);
	}
	public void print( ) {
		System.out.println("X          : " + x);
		System.out.println("Y          : " + y);
		
		System.out.println("Angle Deg  : " + Math.toDegrees(angle));
		System.out.println("Angle Rad  : " + angle);
		
		System.out.println("Length     : " + magnitude);
	}
	public void setMagnitude( double magnitude ) {
		this.magnitude = magnitude;
		calculatePosition();
	}
}
