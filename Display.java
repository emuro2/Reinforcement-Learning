
/*
 * @author Erik Muro
 * emuro2
 * 
 * 
 * Is the Value Iteration learned policy.
 * As well, the Q-learning algorithm learned policy
 * 
 */



import java.awt.*;
import java.io.IOException;

import javax.swing.*;



public class Display extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  final int GRIDx = 6;
	public final int GRIDy = 6;
	public int xpos;
	public int ypos;
	 boolean[][] blocked = new boolean[6][6];
	private int[][] box=new int [6][6];

	public float [][][] final_utilities = new float [2][6][6];
	public float [][] rootMeans = new float [6][6];
	int array_size = 2;
	private int [][] directions = new int [6][6];
	
	int Qlearn = 0;
	int direction;
	int count = 0;
	double discount_factor = 0.99;
	
	Display() throws IOException
	{	
		int input = System.in.read() - '0';
		
		
		//value iteration
		if(input == 0)
		{
			value_iteration();
		}
		else if(input == 9)
		{
			value_iteration();
			for(int i = 0; i < GRIDx; i++ )
			{
				for(int j = 0; j< GRIDy; j++)
				{
					rootMeans[i][j] = final_utilities[count][i][j];
					System.out.print(rootMeans[i][j]+ "     " );
				}
				System.out.println();
			}
			final_utilities = new float [2][6][6];
			count = 0;
			reinforcement_learning();
			
			
		}
		//Reinforcement learning
		else
		{
			reinforcement_learning();
		}
	}


	private void value_iteration() 
	{
		System.out.println("Value Iteration");
		init();
		calculate();
		updateDirections();
		printGraph();
	}


	//initializes the board, with utilities =0
	public void init()
	{
		xpos = 0;
		ypos = 5;
		for(int i=0; i<GRIDx;i++)
		{
			for(int j=0; j<GRIDy; j++)
			{
				final_utilities[0][i][j] =0;
				final_utilities[1][i][j] =0;
				
				directions[i][j] = 0;
				//walls
				if(i == 1 && j ==1 )
				{
					blocked[i][j]= true;
					box[i][j]=-900;
				}
				else if (i == 1 && j ==2 )
				{
					blocked[i][j]=true;	
					box[i][j]=-900;
				}
				else if (i == 1 && j ==3 )
				{
					blocked[i][j]=true;
					box[i][j]=-900;
				}
				else if (i == 3 && j ==0 )
				{
					blocked[i][j]=true;
					box[i][j]=-900;
				}
				else if (i == 3 && j ==1 )
				{
					blocked[i][j]=true;
					box[i][j]=-900;
				}
				//not a wall
				else
				{	
					blocked[i][j] = false;
					box[i][j]=0;
				}
			}
		}
		
		//positives
		box[5][0] = 1; 		
		box[5][5] = 10;
	
	
		//negatives
		box[5][4] = -1;		
		box[3][5] = -1;	
		box[2][5] = -1;		
		box[3][4] = -1;
		
		
		
		
	}



	private void printGraph() 
	{
	
		for(int i=0; i<GRIDx;i++)
		{
			System.out.println("-------------"+ array_size);
			for(int j=0; j<GRIDy; j++)
			{
				for(int k = 0; k < array_size; k++)
				{
					System.out.print("    "+final_utilities[k][i][j]);
				}
				System.out.println("new number: "+j);
			}
			System.out.println(" "+i);
			System.out.println();
		}
		
	}

	//updates where each state should go next
	private void updateDirections() {
		
		
		for(int i=0; i<GRIDx;i++)
		{
			for(int j=0; j<GRIDy; j++)
			{
				if(!blocked[i][j])
					directions[i][j] = max(i,j,count); 
			}
		}
		
	}
	

	//helper func for updating directions
	private int max(int i , int j, int c)
	{
		float currBest=-999;
		
		String direct = null;
		
		//up
		if( valid(i, j-1) && (currBest < final_utilities[c][i][j-1] ) )
		{
			currBest = final_utilities[c][i][j-1];
			direct = "u"; 
		}
		//right
		if( valid(i+1, j) && (currBest < final_utilities[c][i+1][j] ) )
		{
			currBest = final_utilities[c][i+1][j];
			direct = "r"; 
		}
		//down
		if( valid(i, j+1) && (currBest < final_utilities[c][i][j+1] ) )
		{
			currBest = final_utilities[c][i][j+1];
			direct = "d"; 
		}
		//left
		if( valid(i-1, j) && (currBest < final_utilities[c][i-1][j] ) )
		{
			currBest = final_utilities[c][i-1][j];
			direct = "l"; 
		}
		if(direct == null)
			System.out.println(currBest+"  "+i+", "+j+"   up : "+ valid(i, j-1)+" down : "+ valid(i, j+1) +" right : "+ valid(i+1, j) +" left : "+ valid(i-1, j)  );
		
	
		if(direct.equals("u"))
			return 0;
		else if(direct.equals("r"))
			return 1;
		else if(direct.equals("d"))
			return 2;
		else 
			return 3;
		
	}


	//value iteration
	public void calculate()
	{
		if( count !=0 && checkConvergence(count) )
		{
			return;
		}

		count++;

		for(int k = count; k < array_size; k++)
		{
			for(int i=0; i<GRIDx;i++)
			{
				for(int j=0; j<GRIDy; j++)
				{
					if(box[i][j]>0 )
						final_utilities[k][i][j] = box[i][j];
					else if(box[i][j]== -1)
						final_utilities[k][i][j] = -1;
					else if(!blocked[i][j])
						final_utilities[k][i][j] =  (float)(reward(i, j) + ( discount_factor * maxNeighbor(i, j, k-1) ) );			
				}
			}
			
		}

		calculate();

		return;
		
	}
	
	
	//rewards for the non-terminal states is -0.04, else terminal reward
	private double reward(int i, int j) 
	{
		if(box[i][j] == 0)
			return -0.04;
		else
			return box[i][j];

	}
	

	//find max neighbor
	private double maxNeighbor(int i , int j, int k) 
	{
		float currBest = -900;
		int c =0;
		
		//up
		if( valid(i, j-1))
		{
			currBest = Math.max(currBest,final_utilities[k][i][j-1]);
			c++;
		}
		//right
		if( valid(i+1, j))
		{
			currBest = Math.max(currBest,final_utilities[k][i+1][j]);
			c++;
		}
		//down
		if( valid(i, j+1))
		{
			currBest = Math.max(currBest,final_utilities[k][i][j+1]);
			c++;
		}
		//left
		if( valid(i-1, j))
		{
			currBest = Math.max(currBest,final_utilities[k][i-1][j]);
			c++;
		}
		
		//error check
		if(currBest == -900)
			System.out.println("Something went wrong with maxNeighbor");
	
		currBest = (float)(currBest*.8);
		
		return currBest;
	}

	
	//checks if coordinates i and j are valid
	private boolean valid(int i, int j) 
	{
		//out of bounds
		if(i < 0 || i >= GRIDx)
			return false;
		else if(j < 0 || j >= GRIDy)
			return false;
		
		//wall
		else if(blocked[i][j])
			return false;
		
		//valid
		else
			return true;
	}


	//check the final_utilites to see if they are converging
	public boolean checkConvergence(int pos)
	{
		

		if( Math.abs( (float)Math.abs(final_utilities[count][0][0]) - (float)Math.abs(final_utilities[count-1][0][0]) ) < (.0001))
		{
			System.out.println(count);
			return true;
		}
		
		//resize final utilities for the next round
		float [][][] utilities = new float [array_size+1][6][6];
		for(int k = 0; k < array_size; k++)
		{
			for(int i=0; i<GRIDx;i++)
			{
				for(int j=0; j<GRIDy; j++)
				{
					utilities[k][i][j] = final_utilities[k][i][j];
				}
			}
		}
		
		final_utilities = utilities;
		array_size++;
		
		return false;
	}

	
	//paint board
	public void paint(Graphics g){
		
		
		// 7X6 Board
			int x=((getWidth()) / GRIDx) ;
			int y=( (getHeight())/GRIDy );

		
		
			//Black Background squares
			for(int i=0; i<GRIDx;i++)
			{
				for (int j=0; j<GRIDy; j++)
				{

					
					if(box[i][j]> 0)
					{

						g.setColor(Color.GREEN);
						g.fill3DRect(i * x, j * y, x, y, true);

						Font p1= new Font("Algerian",Font.BOLD,getWidth()/35);
						g.setFont(p1);
					
						g.setColor(Color.BLACK);
						g.drawString("+1", i*x+(x/3), j*y+(y/2));
						
						//utilities
						Font p2= new Font("Algerian",Font.BOLD,getWidth()/35);
						g.setFont(p2);
						g.setColor(Color.BLACK);
						g.drawString(""+final_utilities[count][i][j], i*x, j*y+y);	
					}
					else if(box[i][j]==-1)
					{
						g.setColor(Color.ORANGE);
						g.fill3DRect(i * x, j * y, x, y, true);
						Font p1= new Font("Algerian",Font.BOLD,getWidth()/35);
						g.setFont(p1);
					
						g.setColor(Color.BLACK);
						g.drawString("-1", i*x+(x/3), j*y+(y/2));
						
						//utilities
						Font p2= new Font("Algerian",Font.BOLD,getWidth()/35);
						g.setFont(p2);
						g.setColor(Color.BLACK);
						g.drawString(""+final_utilities[count][i][j], i*x, j*y+y);	
						
					}
					
					else if(blocked[i][j]==false)
					{
						g.setColor(Color.WHITE);
						g.fill3DRect(i * x, j * y, x, y, true);
						 if (j == 5 && i == 0)
						 {
								
							Font p1= new Font("Algerian",Font.BOLD,getWidth()/35);
							g.setFont(p1);
						
							g.setColor(Color.BLACK);
							g.drawString("Start", i*x+(x/3), j*y+(y/2));
								
						 }

						 //utilities
						Font p1= new Font("Algerian",Font.BOLD,getWidth()/35);
						g.setFont(p1);
						g.setColor(Color.BLACK);
						g.drawString(""+final_utilities[count][i][j], i*x, j*y+y);						
						
					}
					else if (blocked[i][j]== true)
					{
						g.setColor(Color.LIGHT_GRAY);
						g.fill3DRect(i * x, j * y, x, y, true);
						Font p1= new Font("Algerian",Font.BOLD,getWidth()/35);
						g.setFont(p1);
					
						g.setColor(Color.BLACK);
						g.drawString("Wall", i*x+(x/3), j*y+(y/2));						
					}
					
					//directions
					//up
					if(directions[i][j]==0 && blocked[i][j]== false)
					{
						Font p1= new Font("Algerian",Font.BOLD,getWidth()/45);
						g.setFont(p1);
						g.setColor(Color.BLUE);
						g.drawString("^", i*x+(x/2)-2, j*y+(y/5));	
						g.drawString("|", i*x+(x/2), j*y+(y/4));	
					}
					//right
					else if(directions[i][j]==1 && blocked[i][j]== false)
					{

						Font p1= new Font("Algerian",Font.BOLD,getWidth()/45);
						g.setFont(p1);
						g.setColor(Color.BLUE);
						g.drawString("->", i*x+(x/2), j*y+(y/5));	
					}
					//down
					else if(directions[i][j]==2 && blocked[i][j]== false)
					{
						Font p1= new Font("Algerian",Font.BOLD,getWidth()/45);
						g.setFont(p1);
						g.setColor(Color.BLUE);
						g.drawString("|", i*x+(x/2), j*y+(y/5));	
						g.drawString("_", i*x+(x/2), j*y+(y/4));	
	
					}
					//left
					else if(directions[i][j]==3 && blocked[i][j]== false)
					{

						Font p1= new Font("Algerian",Font.BOLD,getWidth()/45);
						g.setFont(p1);
						g.setColor(Color.BLUE);
						g.drawString("<-", i*x+(x/2), j*y+(y/5));	
					}
				}
			}

			


		
	}
	


	
	
	
	private void reinforcement_learning() 
	{
		System.out.println("Reinforcement Learning");
		init();
		calculateQ();
		updateDirections();
	}


	//Q-learning calculations
	private void calculateQ() 
	{
		
		if(box[xpos][ypos]> 0)
		{
			final_utilities[0][xpos][ypos] = box[xpos][ypos];
			xpos = 0;
			ypos = 5;
		}
		else if(box[xpos][ypos]== -1)
		{
			final_utilities[0][xpos][ypos] = -1;
			xpos = 0;
			ypos = 5;
		}
		if(Qlearn >=9000 )
		{
			return;
		}
		
		Qlearn++;
		
		//update utilities	
		//if not at a wall, update the utility
		if(!blocked[xpos][ypos])
		{
			double r = reward(xpos,ypos)+ discount_factor*(maxQ(xpos, ypos) );
		
			final_utilities[0][xpos][ypos] = (float) (final_utilities[0][xpos][ypos]+ alpha(Qlearn)*(r)   );
		}
		
		direction = max(xpos, ypos, 0);
		
		//black box
		int dir = blackbox(direction);
		
		//up
		if(dir == 0 && valid(xpos, ypos-1))
		{
			ypos--;
		}
		//right
		else if(dir == 1 && valid(xpos+1, ypos))
		{
			xpos++;
		}
		//down
		else if(dir == 2 && valid(xpos, ypos+1))
		{
			ypos++;
		}
		//left
		else if(dir == 3 && valid(xpos-1, ypos))
		{
			xpos--;
		}
		
		direction = dir;
	
		if(Qlearn < 50 || Qlearn%50 == 0)
			root_means();
		
		calculateQ();
		
	}
	
	




	private void root_means() 
	{
		double sum = 0;
		
		for(int i = 0; i < 6; i++)
		{
			for(int j = 0; j< 6;j++)
			{
				sum += ( Math.pow( final_utilities[count][i][j]- rootMeans[i][j] , 2)  );
			}
		}
		
		sum = sum /32;
		
		sum = Math.sqrt(sum);
		
		System.out.println("i: "+Qlearn+" = "+sum);
		
	}





	private int blackbox(int dir) {
		
		int ran = (int)(Math.random()*10);
		//System.out.println(ran);
		if(ran < 8)
			return dir;
		else if(ran ==8)
			return (dir+3)%4;
		else 
			return (dir+1)%4;

	}



	private float maxQ(int x, int y) 
	{
		float currBest = -900;
	
		
		//up
		if( valid(x, y-1))
		{
			currBest = Math.max(currBest,final_utilities[0][x][y-1]-final_utilities[0][x][y]);		
		}
		//right
		if( valid(x+1, y))
		{
			currBest = Math.max(currBest,final_utilities[0][x+1][y]-final_utilities[0][x][y]);
		}
		//down
		if( valid(x, y+1))
		{
			currBest = Math.max(currBest,final_utilities[0][x][y+1]-final_utilities[0][x][y]);
		}
		//left
		if( valid(x-1, y))
		{
			currBest = Math.max(currBest,final_utilities[0][x-1][y]-final_utilities[0][x][y]);
		}
		
		
		
		//error check
		if(currBest == -900)
			System.out.println("Something went wrong with maxNeighbor");
		
		return currBest;
	}



	private double alpha(int t) 
	{		
		double ret = (59 + t); 
		ret = 60/ret;
		return ret;
	}
	
	
	
}









