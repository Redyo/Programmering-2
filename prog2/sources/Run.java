package i2;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Run extends JFrame{
	private HashMap<Position, Location> locations = new HashMap<>();
	private HashMap<String, ArrayList<Location>> locationsName = new HashMap<>();
	private HashMap<Integer, ArrayList<Location>> locationsCat = new HashMap<>();
	private ArrayList<Location> markedLocations = new ArrayList<>();

	private Chart chosenMap;
	private boolean unsavedChanges = false;
	
	private String[] newOptions = {"Named","Described"};
	private String[] vehicle={"Bus", "Train", "Subway"};
	
	private JTextField field = new JTextField(8);
	
	private JComboBox<String> options = new JComboBox<>(newOptions);
	private JList<String> list = new JList<>(vehicle);
	private JScrollPane scroll = null;
	private JFileChooser jfc = new JFileChooser(".");
	
	private boolean clicked = false;
	Run(){
		super("i2");
		
		JMenuBar mbar = new JMenuBar();
		JMenu archiveMenu = new JMenu("Archive");
		
		JMenuItem newMap = new JMenuItem("New Map");
		JMenuItem loadPlaces = new JMenuItem("Load places");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem exit = new JMenuItem("Exit");
		
		JButton hideCategory = new JButton("Hide category");
		JButton search = new JButton("Search");
		JButton remove = new JButton("Remove");
		JButton hide = new JButton("Hide");
		JButton whatIsHere = new JButton("What is here?");
		
		JLabel lab = new JLabel("New:");
		JLabel categories = new JLabel("Categories");
		
		setJMenuBar(mbar);
		mbar.add(archiveMenu);
		archiveMenu.add(newMap);
		archiveMenu.add(loadPlaces);
		archiveMenu.add(save);
		archiveMenu.add(exit);
		
		JPanel east = new JPanel();
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
		add(east, BorderLayout.EAST);
		east.add(categories);
		east.add(list);
		east.add(hideCategory);
		categories.setAlignmentX(Component.CENTER_ALIGNMENT);
		list.setAlignmentX(Component.CENTER_ALIGNMENT);
		hideCategory.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel north = new JPanel();
		add(north, BorderLayout.NORTH);
		north.add(lab);
		north.add(options);
		north.add(field);
		north.add(search);
		north.add(remove);
		north.add(hide);
		north.add(whatIsHere);
		
		newMap.addActionListener(new OpenListener());
		loadPlaces.addActionListener(new LoadListener());
		save.addActionListener(new SaveListener());
		exit.addActionListener(new ExitListener());
		options.addActionListener(new ComboBoxListener());
		search.addActionListener(new SearchListener());
		remove.addActionListener(new RemoveListener());
		hide.addActionListener(new HideListener());
		whatIsHere.addActionListener(new WhatListener());
		list.addListSelectionListener(new JListListener());
		hideCategory.addActionListener(new HideCatListener());
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			@Override
	    	public void windowClosing(WindowEvent wev){
		 	    if (unsavedChanges) {
		 			int exitChoice = JOptionPane.showConfirmDialog(Run.this,"There are unsaved changes, "+ "exit anyway?","Exit", JOptionPane.OK_CANCEL_OPTION);
		 			if (exitChoice == JOptionPane.OK_OPTION) {
		 			    System.exit(0);
		 			}		 		
		 	    }else{
		 	    	System.exit(0);
		 	    }
	    	}
		});
		
		setSize(700,500);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}
	
    class OpenListener implements ActionListener{
		public void actionPerformed(ActionEvent ave){
			if (unsavedChanges) {
				int exitChoice = JOptionPane.showConfirmDialog(Run.this,"There are unsaved changes, "+ "exit anyway?","Exit", JOptionPane.OK_CANCEL_OPTION);
	 			if (exitChoice == JOptionPane.OK_OPTION) {
	 				loadMap();
	 			}					
			}else{
				loadMap();
			}
		}
    }
    

    
    class LoadListener implements ActionListener{
		public void actionPerformed(ActionEvent ave){
    		if (chosenMap == null) {
    			JOptionPane.showMessageDialog(null, "Load a map first!");
    		}else if(unsavedChanges){
    			int exitChoice = JOptionPane.showConfirmDialog(Run.this,"There are unsaved changes, "+ "load anyway?","Load", JOptionPane.OK_CANCEL_OPTION);
	 			if (exitChoice == JOptionPane.OK_OPTION) {
	    			load();
	 			}
    		}else{
    			load();
    		}
		}
    }
    
    class SaveListener implements ActionListener{
		public void actionPerformed(ActionEvent ave){
			save();
		}
    }
    
    class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent ese){
			processWindowEvent(new WindowEvent(Run.this, WindowEvent.WINDOW_CLOSING));
		}
    }
    
    class ComboBoxListener implements ActionListener{
    	public void actionPerformed(ActionEvent ave){
    		if (chosenMap == null) {
    			JOptionPane.showMessageDialog(null, "Load a map first!");
    		}else{
    			if(!clicked){
    				clicked = true;
        		chosenMap.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        		chosenMap.addMouseListener(new MapListener());
    			}
 
        		
    		}
    	}
    }
    
    class SearchListener implements ActionListener{
		public void actionPerformed(ActionEvent ave){
			boolean found = false;
			for (Location l: markedLocations){
				l.setMarked(false);
			}
			markedLocations.clear();
			
			String searchText = field.getText();
			ArrayList<Location> list = locationsName.get(searchText);
			
			if(list != null){
				found = true;
			}
			
			if(found){
				for (Location l: list){
					l.setMarked(true);
					markedLocations.add(l);
					if(!(l.isVisible())){
						l.setVisible(true);
					}
					
				}
				repaint();			
			}
			
		}

    }
    
    class RemoveListener implements ActionListener{
		public void actionPerformed(ActionEvent ave){
		
			ArrayList<Location> tbr = new ArrayList<>();
			boolean found = false;
			
			for (Location l: markedLocations){
			    chosenMap.remove(l);
			    found = true;
			    tbr.add(l);
			}
			
			if(found){
				markedLocations.clear();
				for (Location lr: tbr){
					deleteLocation(lr);
				}
				repaint();
			}
			
			if(locations.isEmpty() && locationsName.isEmpty() && markedLocations.isEmpty() && locationsCat.isEmpty()){
				unsavedChanges = false;
			}
			
			
		}
    }
    
    class HideListener implements ActionListener{
		public void actionPerformed(ActionEvent ave){
			for (Location l: markedLocations){
				l.setVisible(false);
				l.setMarked(false);
			}
			markedLocations.clear();
			repaint();
		}
    }
    
    class WhatListener implements ActionListener{
		public void actionPerformed(ActionEvent ave){
    		if (chosenMap == null) {
    			JOptionPane.showMessageDialog(null, "Load a map first!");
    		}else{
        		chosenMap.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        		chosenMap.addMouseListener(new WhatMouseListener());
    		}	
		}
    }
    
    class HideCatListener implements ActionListener{
		public void actionPerformed(ActionEvent ave){
    		int cat = list.getSelectedIndex();
    		ArrayList<Location> list = locationsCat.get(cat);
    		if(list!=null){
    			for (Location l: list) {
        			l.setVisible(false);
        		}
    		}    			    
    		repaint();  
		}
    }
    
	class LocationListener extends MouseAdapter{
    	@Override
    	public void mouseClicked(MouseEvent mev){
    		Location loc = (Location)mev.getSource();
    		if (mev.getButton() == MouseEvent.BUTTON1) {
    			if (loc.getMarked()) {
    				loc.setMarked(false);
    				boolean found = false;
    				int i = 0;
    	    		for(Location l: markedLocations){
    	    			if (loc.getName().equals(l.getName())){
    	    				i = markedLocations.indexOf(l);
    	    				found = true;
    	    			}
    	    		}
    	    		if (found){
    	    			markedLocations.remove(i);
    	    		}
    			}else{
    				loc.setMarked(true);
    				markedLocations.add(loc);
    			}
    		}else if (mev.getButton() == MouseEvent.BUTTON3){
    			if (loc.getFolded()) {
    				loc.setFolded(false);
    			}else{
    				loc.setFolded(true);
    			}
    		}
		}   				
	}
	
	class WhatMouseListener extends MouseAdapter{
    	@Override
    	public void mouseClicked(MouseEvent mev){
			int x = mev.getX();
    		int y = mev.getY();
    		gridCheck(x,y);
    		chosenMap.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		chosenMap.removeMouseListener(this);
    		repaint();
		}   				
	}
	
	void gridCheck(int x, int y){
		int xi = x+30;
		int yi = y+30;
		int xn = x-30;
		int yn = y-30;
		while (xn <= xi) {
			while (yn <= yi) {
				Position p = new Position(xi,yi);
				Location l = locations.get(p);
				if (l != null){
					l.setVisible(true);
					l.setMarked(true);
					markedLocations.add(l);
					repaint();
					return;
				}else{
					yi--;
				}
			}
			yi = y+30; 
			xi--;
		}
	}
	
	void loadMap(){
		int answer = jfc.showOpenDialog(Run.this);
	    if (answer != JFileChooser.APPROVE_OPTION){
	    	return;
	    }
	    File file = jfc.getSelectedFile();
	    String filename = file.getAbsolutePath();
	    chosenMap = new Chart(filename);
	    clearLocations();
	    chosenMap.setLayout(null);
	    scroll = new JScrollPane(chosenMap);
	    if (scroll != null) {
			remove(scroll);
		    add(scroll);
		    pack();
		    validate();
		    repaint();
	    }
	}

    void save(){
		int svar = jfc.showSaveDialog(Run.this);
		if (svar == JFileChooser.APPROVE_OPTION){
		    File f = jfc.getSelectedFile();
		    String file = f.getAbsolutePath();
		    
		    try{
		    	PrintWriter out = new PrintWriter(file);
	    		Collection<Location> v = locations.values();
	    		ArrayList<Location> list = new ArrayList<>(v);

				for (Location l : list) {

					String sc = String.valueOf(l.getClass());
					String[] parts = sc.split("\\.");
					String t = parts[1];
					String n = l.getName();
					String c = l.getCatString();
					Position p = l.getPosition();
					String x = "" + p.getX();
					String y = "" + p.getY();

					switch (t) {
					case "Location":
						t = "Named";
						String text = t + "," + c + "," + x + "," + y + "," + n;
						out.println(text);
						break;
					case "DescribedLocation":
						t = "Described";
						String d = l.getDescription();
						String textDesc = t + "," + c + "," + x + "," + y + "," + n + "," + d;
						out.println(textDesc);
						break;
					}
					unsavedChanges = false;
				} 
				
				out.close();
	    	}catch(FileNotFoundException fnfe){
		        JOptionPane.showMessageDialog(Run.this, "Could not write to file!", "Error", JOptionPane.ERROR_MESSAGE);
		    }
		}
    }
    
    class JListListener implements ListSelectionListener{
    	@Override
    	public void valueChanged(ListSelectionEvent e) {
    		int cat = list.getSelectedIndex();
    		ArrayList<Location> list = locationsCat.get(cat);	
    		if(list!=null){
    			for (Location l: list) {
        			l.setVisible(true);
        		}
    		}    			    
    		repaint();    		   	 	   		    	   		
    	}
    }
    
    class MapListener extends MouseAdapter{
    	@Override
    	public void mouseClicked(MouseEvent mev){
    		clicked = false;
    		int x = mev.getX();
    		int y = mev.getY();
    	    int c = list.getSelectedIndex();
    	    Position p = new Position(x,y);  
    	    String option = (String) options.getSelectedItem();
    	    
    	    Location l = null;
    	    boolean successful = false;

    	    JPanel addForm = new JPanel();
    	    addForm.setLayout(new BoxLayout(addForm, BoxLayout.Y_AXIS));

    	    JPanel nameLine = new JPanel();					
    		JTextField nameField = new JTextField(10);
    		nameLine.add(new JLabel("Name:"));
    		nameLine.add(nameField);
    		addForm.add(nameLine);	
    		
    		
    		
    	    switch(option){
    	    	case "Named":
        			try {
        				JOptionPane.showMessageDialog(null, addForm, "New location",
        				JOptionPane.QUESTION_MESSAGE);
        				String name = nameField.getText();
        				if(name.trim().isEmpty()){
        					Component frame = null;
        					JOptionPane.showMessageDialog(frame,
        					"Wrong input!",
        					"ERROR",
        				    JOptionPane.ERROR_MESSAGE);
        					break;
        				}else{
        					l = new Location(name, c, p);
        					successful = true;
        				}	
        			}catch(NumberFormatException e){
    				    JOptionPane.showMessageDialog(null, "Wrong input!",
    					"ERROR", JOptionPane.ERROR_MESSAGE); 				    
                	}
    		    	break;

    	    	case "Described":
        			JPanel descLine = new JPanel();
    				JTextField descField = new JTextField(10);
    				descLine.add(new JLabel("Description, max 10 chars:"));
    				descLine.add(descField);
    				addForm.add(descLine);
    	    		try {
    	    			JOptionPane.showMessageDialog(null, addForm, "New location",
        				JOptionPane.QUESTION_MESSAGE);
        				String name = nameField.getText();
        				String descName = descField.getText();
        				if(name.trim().isEmpty()){
        					Component frame = null;
    						JOptionPane.showMessageDialog(frame,
        					"Wrong input!",
        					"ERROR",
    						JOptionPane.ERROR_MESSAGE);
    						break;
        				}else{                                            
        					l = new DescribedLocation(name, c, p, descName);
        					successful = true;
        				}        					
                	}catch(NumberFormatException e){
    				    JOptionPane.showMessageDialog(null, "Wrong input!",
    					"ERROR", JOptionPane.ERROR_MESSAGE);  
                	}
        	}
    	    if (successful) {
    			chosenMap.add(l);
    			l.addMouseListener(new LocationListener());	
    			storeLocation(l);
        		unsavedChanges = true;
        		chosenMap.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        		chosenMap.removeMouseListener(this);
        		repaint();
    	    }
    	}
    	
    }
    
    void storeLocation(Location l){
    	Position p = l.getPosition();
    	locations.put(p,l);
    	
    	
    	String name = l.getName();
    	if (locationsName.get(name) != null){
    		ArrayList<Location> list = new ArrayList<>();
    		list.addAll(locationsName.get(name));	
    		list.add(l);
    		locationsName.put(name,list);
    	}else{
    		ArrayList<Location> list = new ArrayList<>();
    		list.add(l);
    		locationsName.put(name,list);
    	}
    	
    	int c = l.getCategory();
    	if (locationsCat.get(c) != null){
    		ArrayList<Location> list = new ArrayList<>();
    		list.addAll(locationsCat.get(c));	
    		list.add(l);
    		locationsCat.put(c,list);
    	}else{
    		ArrayList<Location> list = new ArrayList<>();
    		list.add(l);
    		locationsCat.put(c,list);
    	}
    	
    	repaint();
    }
    
    void deleteLocation(Location l){
    	Position p = l.getPosition();
    	String n = l.getName();
    	int c = l.getCategory();
    	locations.remove(p);
    	markedLocations.remove(l);
    	locationsCat.remove(c);
    	
    	boolean found = false;
    	if (locationsName.get(n) != null){
    		ArrayList<Location> list = new ArrayList<>();
    		list.addAll(locationsName.get(n));
    		for (Location loc: list){
    			if (p == loc.getPosition()) {
    				found = true;				
				}
    		}
			if (found){
				list.remove(l);	
			}
    		locationsName.put(n,list);
    	}
    	
    	found = false;
    	if (locationsCat.get(c) != null){
    		ArrayList<Location> list = new ArrayList<>();
    		list.addAll(locationsCat.get(c));
    		for (Location loc: list){
    			if (l == loc) {
					found = true;			
				}
    		}
			if (found){
				list.remove(l);	
			}
    		locationsCat.put(c,list);
    	}
    	
    }
    
    void clearLocations(){
    	locations.clear();
    	locationsName.clear();
    	locationsCat.clear();
    	markedLocations.clear();
    	
    	unsavedChanges = false;
    	
    	
    }
    
   
    void load() {
    	try{
    		int svar = jfc.showOpenDialog(Run.this);
 		    if (svar == JFileChooser.APPROVE_OPTION){
 				File f = jfc.getSelectedFile();
 				String file = f.getAbsolutePath();
 		    
	    		FileInputStream fs = new FileInputStream(file);
	    		DataInputStream in = new DataInputStream(fs);
	    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    		String line;
	    		while ((line = br.readLine()) != null)   {
	    			String[] parts = line.split(",");
	    			String t = parts[0];
	    			int c = -1;
	    	    	switch (parts[1]) {
		    	        case "Bus":
		    	        	c = 0;
		    	            break;
		    	        case "Buss":
		    	        	c = 0;
		    	            break;
		    	        case "Subway":
		    	        	c = 1;
		    	            break;
		    	        case "Tunnelbana":
		    	        	c = 1;
		    	            break;
		    	        case "Train":
		    	        	c = 2;
		    	            break;
		    	        case "Tåg":
		    	        	c = 2;
		    	            break;
		    	    	case "No category":
		    	    		c = -1;
		    	            break;
		    	    	case "None":
		    	    		c = -1;
		    	            break;
	    	    	}
	    	    	
	    			int x = Integer.parseInt(parts[2]);
	    			int y = Integer.parseInt(parts[3]);
					String n = parts[4];
					Position p = new Position(x,y);
	    			switch(t){
						case "Named":
							Location l = new Location(n,c,p);
							storeLocation(l);
							chosenMap.add(l);
			    			l.addMouseListener(new LocationListener());				    			
			    			break;
						case "Described":
							String d = parts[5];
							Location ld = new DescribedLocation(n,c,p,d);
							storeLocation(ld);
							chosenMap.add(ld);
			    			ld.addMouseListener(new LocationListener());
			    			break;	
	    			}	
	    		}
				in.close();
				fs.close();
				br.close();
				unsavedChanges = false;
				repaint();
 		    }
		}catch(FileNotFoundException fnfe){
			JOptionPane.showMessageDialog(Run.this, "No such file!", "Error", JOptionPane.ERROR_MESSAGE);
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(Run.this, "IO-error occured!", "Error", JOptionPane.ERROR_MESSAGE);
		}
    	unsavedChanges = true;
    }
    
	public static void main(String[] args){
		new Run();
	}
}

