package i2;

import javax.swing.*;
import java.awt.*;

class Chart extends JPanel{
    private ImageIcon picture;

    public Chart(String pic){
		picture = new ImageIcon(pic);
		int w = picture.getIconWidth();
		int h = picture.getIconHeight();
		setPreferredSize(new Dimension(w,h));
		setLayout(null);
    }

    protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(picture.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}