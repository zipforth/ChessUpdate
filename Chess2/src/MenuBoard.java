
public class MenuBoard
{
	int Player;
	boolean bot;
	boolean isWhite;
	boolean stopGame=false;
	String whoWon="no one";
	String win;
	String[][] Board = new String[8][8];
	int[] pieceVal={2200,200000,2000,800,8000,4000};
	String[] pieces={"B","K","N","P","Q","R"};
	
	boolean WcanC = true;
	boolean BcanC = true;
	int[] WK = { 7, 4 };// r,c format
	int[] BK = { 0, 4 };
	public MenuBoard()
	{
		Player=0;
		bot=false;
		win="0";
		isWhite=false;
	}
	public MenuBoard(int p,boolean b,boolean i,String w)
	{
		Player=p;
		bot=b;
		isWhite=i;
		win=w;
	}
	public MenuBoard copyBoard()
	{
		MenuBoard b=new MenuBoard(Player,bot,isWhite,win);
		String Hold;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				Hold=this.Board[i][j];
				b.Board[i][j]=Hold;
			}
		}
		b.WcanC=WcanC;
		b.BcanC=BcanC;
		return b;
	}
	public int piecEquation(char num)
	{
		double newInt=365176889.8*Math.pow(Math.E, ((-1485.383126)/(int)num));
		return pieceVal[(int)Math.round(newInt)];
	}
}
