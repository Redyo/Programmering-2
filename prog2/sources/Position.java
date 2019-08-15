package i2;

class Position {

	private int x;
	private int y;
	private int xy;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
		String calc = "" + x + y;
		xy = Integer.parseInt(calc);
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}
	
	public int getXY(){
		return xy;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Position other = (Position) obj;
		if (xy != other.xy){
			return false;
		} 
		return true;
	}
	
	@Override
	public int hashCode(){
		return xy;
	}
	
	@Override
    public String toString(){
    	return "X: " + x + ", Y: " + y;
    }

}