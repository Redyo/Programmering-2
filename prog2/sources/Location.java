package i2;

import javax.swing.*;
import java.awt.*;

class Location extends JComponent {
	private boolean folded = false;
	private boolean marked = false;

	private String name;
	private int category;
	private String catString = "";
	
	private int x;
	private int y;
	
	private Position position;

	public Location(String name, int category, Position position){
		this.name = name;
		this.category = category;
		this.position = position;
		x = position.getX();
		y = position.getY();
		setBounds(x-15,y-30,30,30);
		setPreferredSize(new Dimension(30,30));
		setMaximumSize(new Dimension(30,30));
		setMinimumSize(new Dimension(30,30));
    	switch (category) {
	        case 0:
	        	catString = "Bus";
	            break;
	        case 1:
	        	catString = "Subway";
	            break;
	        case 2:
	        	catString = "Train";
	            break;
	    	case -1:
	    		catString = "No category";
	            break;
    	}
	}
	
    protected void paintComponent(Graphics g){
		super.paintComponent(g);
        if (folded) {
        	setBounds(x-40,y-40,80,40);
        	g.setColor(Color.WHITE);
        	g.fillRect(0, 0, 80, 40);
        	g.setColor(Color.BLACK);
        	String nameCut = name.substring(0, Math.min(name.length(), 10));
        	g.drawString(nameCut, 5, 15);
        	String desc = getDescription();
        	if(!desc.equals("")) {
            	g.drawString(desc, 5, 30);
        	}
            if (marked) {
            	g.setColor(Color.RED);
            	g.drawRect(0, 0, 79, 39);
            }
        }else{
        	setBounds(x-15,y-30,30,30);
    		switch (category) {
            case 0:
				g.setColor(Color.RED);
                break;
            case 1:
            	g.setColor(Color.BLUE);
                break;
            case 2:
            	g.setColor(Color.GREEN);
                break;
        	case -1:
        		g.setColor(Color.BLACK);
                break;
    		}
            int[] xPoints ={0,15,30}, yPoints = {0,30,0};
            int nPoint = 3;
            g.fillPolygon(xPoints, yPoints, nPoint);
            if (marked) {
            	g.setColor(Color.RED);
            	g.drawRect(0, 0, 29, 29);
            }
        }
    }
    
    public String getName(){
    	return name;
    }

    public int getCategory(){
    	return category;
    }
    
    public String getCatString(){
    	return catString;
    }

    public Position getPosition(){
    	return position;
    }

    public void setFolded(boolean b){
    	folded = b;
    	repaint();
    }

    public void setMarked(boolean b){
		marked = b;
		repaint();
    }

    public boolean getFolded(){
    	return folded;
    }

    public boolean getMarked(){
    	return marked;
    }
    
    public String toString(){
    	return "Name: " + name + " , Category: " + catString; 
    }
    
    public String getDescription(){
        Location x = new DescribedLocation("", 0, position, "");
        DescribedLocation y = (DescribedLocation) x;
        return y.getDescription();
     }
}

class DescribedLocation extends Location {
	private String description;
	public DescribedLocation(String name, int category, Position position, String description){
		super(name, category, position);
		this.description = description;
	}

	public String getDescription(){
    	return description;
    }
	
}

