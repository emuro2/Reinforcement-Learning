

/*
 * @author Erik Muro
 * 
 * 
 * Is the JFrame display of the map and utilities of each square 
 * 
 */




import java.io.IOException;

import javax.swing.JFrame;




public class Board {

	
		static JFrame f;
		static Display d;
	
			
		/**
		 * @param args
		 * @throws IOException 
		 */
		public static void main(String[] args) throws InterruptedException, IOException {
			// Display the board to the screen.
			f = new JFrame("Grid World");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			d = new Display();	
		
			f.add(d);
			f.setSize(350,375);			
			f.setVisible(true);
			

					
			
			d.repaint();
				
			
			
		}

}

