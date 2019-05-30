import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;

/*Log for the changing
 * |X|add check/checkmate
 *    Don't forget to test it
 * | |add stalemate, 50 move rule
 * |X|add king movement checks for illegal moves
 * 
 * |X|fix the recursive main call
 * | |add en pas-ent
 * |X|fix the end game message for AI
 * 
 * add more AI:
 * |X|make a material counter P=8 N=20 B=22 R=40 Q=80 K=2000
 * |X|implement for one move
 * |X|implement for 3-ish moves
 * |X|make board heat maps
 * |X|implement board heat maps where counter is
 * |X|Castling read
 * | |Fix the pruning, currently cutting out some incorrect nodes (suiciding pieces)maybe pawn recognition, or weighting
 * 
 * add gui
 * add thread for timer
 * 
 * row 0 starts as black
 */
public class startChess
{
	public static int[][][] PieceMaps = new int[12][8][8];
	static int difficulty;
	public static Scanner kb = new Scanner(System.in);
	public static int al,be;
	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner heatMap = new Scanner(new File("HeatMap.txt"));
		char piece;
		for (int i = 0; i < 12; i++)
		{
			piece = heatMap.nextLine().charAt(0);
			double newInt = 365176889.8 * Math.pow(Math.E, ((-1485.383126) / ((int) piece)));
			int b = (int) Math.round(newInt);
			if (i > 5)
			{
				b += 6;// black is shifted by *2
			}

			for (int r = 0; r < 8; r++)
			{
				String[] abc = heatMap.nextLine().split(" ");
				for (int c = 0; c < 8; c++)
				{
					PieceMaps[b][r][c] = Integer.parseInt(abc[c]);
				}
			}

		}

		
		while (beginMatch())
		{

		}
	}

	public static MenuBoard setMatch()
	{
		MenuBoard a = new MenuBoard();

		System.out.println("Please use c1 c2 format\n" + "for promotion, add rank at end"
				+ "\n0-0 for castle, 0-0-0 for queenside" + "\nPlay against human or AI?(H/A)");

		a.bot = kb.nextLine().contains("A");
		if (a.bot)
		{
			System.out.println("Difficulty (VE/E/M/H/I)");
			String diff=kb.nextLine();
			switch(diff)
			{
			case "VE":
			case "Ve":
			case "vE":
			case "ve":difficulty=0;break;
			case "E":
			case "e":difficulty=1;break;
			case "M":
			case "m":difficulty=3;break;
			case "H":
			case "h":difficulty=5;break;
			case "I":
			case "i":difficulty=9;break;
			}
			System.out.println("Pick a side(b/w)");

			if (kb.nextLine().contains("w"))
			{
				a.Player = 0;
				setBoard(0, a.bot, a);
				printBoard(a.Player, a);
				a.isWhite = true;
			}
			else
			{
				setBoard(1, a.bot, a);
				a.Player = 1;
				a.isWhite = false;
			}
		}
		else
		{
			setBoard(0, false, a);
			printBoard(a.Player, a);
		}
		return a;
	}

	public static boolean beginMatch()
	{
		MenuBoard a = setMatch();
		while (true)
		{
			if (!a.bot)
			{
				a.win = getMoves(kb, a.Player, a.bot, a);
				if (a.stopGame)
				{
					return newGame(kb, a.whoWon,0);
				}
				a.Player++;
			}
			else
			{
				if (!a.stopGame)
				{
					if (a.isWhite)
					{
						System.out.print("Your Move:");
						a.win = getMoves(kb, 0, a.bot, a);
						if (a.stopGame)
						{
							return newGame(kb, a.whoWon,0);
						}
						System.out.print("AI Move:");
						AIMove(0, a);
					}
					else
					{
						System.out.print("\nAI Move:");
						AIMove(1, a);
						if (a.stopGame)
						{
							return newGame(kb, a.whoWon,0);
						}
						System.out.print("Your Move:");
						a.win = getMoves(kb, 1, a.bot, a);

					}
				}
			}

			if (a.win.equals("R"))
			{
				if (a.isWhite)
				{
					System.out.println("Black wins, white resigns.");
					a.whoWon="black";
					
				}
				else
				{
					System.out.println("White wins, black resigns.");
					a.whoWon="white";
				}
				return newGame(kb, a.whoWon,1);

			}
		}
	}

	public static boolean newGame(Scanner kb, String whoWon, int ender)//ender: 0=checkmate, 1=resign, 2=draw, 3=stalemate
	{
		switch(ender)
		{
		case 0:System.out.println("Checkmate, " + whoWon + " wins.");
			break;
		case 1:
			break;
		case 2:System.out.println("Draw, good on you for agreeing!");//needs code for if they don't agree
			break;
		case 3:System.out.println("Stalemate, no one wins.");
			break;
		}
		
		System.out.println("New Game?(y/n)");
		if (kb.nextLine().contains("y"))
		{
			return true;
		}
		else
			return false;
	}

	public static void AIMove(int Player, MenuBoard a)
	{
		String AIColor, notAI;
		if (Player % 2 == 0)
		{
			AIColor = "b";
			notAI = "w";
		}
		else
		{
			AIColor = "w";
			notAI = "b";
		}

		char c[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
		
		// implement a checkmate==min int
		// remove asking for which upgrade in the AI branches
		//unwilling to take pieces, no idea why
		
		al=0;be=0;
		int[] holder=checkStrat(a, difficulty,Integer.MIN_VALUE,Integer.MAX_VALUE,(Player%2==1));
		System.out.println("number clips:"+al+"    number passed:"+be+"    value:"+holder[0]);
		
		//System.out.println(holder[1]+"\t"+holder[0]);
		movePiece(holder[1], a.Player + 1, a);
		//System.out.println(holder[1]);
		printBoard(Player, a);

	}

	public static ArrayList<Integer> AIPieces(Boolean whiteMax, MenuBoard a)
	{
		ArrayList<Integer> myPieces = new ArrayList<Integer>();
		int matVal = 0;
		int i;
		int key = 0;
		for (int r = 0; r < 8; r++)
		{
			for (int col = 0; col < 8; col++)
			{
				if (a.Board[r][col].contains("b")||a.Board[r][col].contains("w"))
				{
					char pieceRank = a.Board[r][col].charAt(2);
					myPieces.add(((r+1)*10) + col);
					
					key = (int) Math.round(365176889.8 * Math.pow(Math.E, ((-1485.383126) / (int) pieceRank)));

					if (a.Board[r][col].contains("w"))
					{
						matVal += PieceMaps[key][r][col];
						matVal += a.piecEquation(pieceRank);
					}
					else
					{
						matVal -= PieceMaps[key + 6][r][col];
						matVal -= a.piecEquation(pieceRank);

					}
				}
			}
		}
		
		myPieces.add(0, matVal);
		return myPieces;
	}

	public static int[] checkStrat(MenuBoard a, int more,int alpha,int beta, boolean whiteMax)
	{//severely broken, doesn't reach 0
		
		int valpos[]=new int[2];
		ArrayList<Integer> source=AIPieces(whiteMax, a);
		
		if(more==0)
		{			
			valpos[0]=source.get(0);
			//System.out.println("this"+valpos[0]);
			return valpos;
		}
		int bestval= whiteMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
		for(int i=1;i<source.size();i++)
		{
			int checker=source.get(i);
			int r,c;
			c=checker%10;
			
			r=((checker-c)/10)-1;
			ArrayList<Integer> dest=getLegal(a,whiteMax,r,c,a.Board[r][c].substring(2, 3));
			for(int j=0;j<dest.size();j++)
			{
				MenuBoard b=a.copyBoard();
				int gimme=checker*100+dest.get(j);
				//System.out.println("this outputs "+gimme);
				//System.out.flush();
				if(movePiece(gimme, b.Player+1, b))
				{
					b.Player++;
					int hold=checkStrat(b,more-1,alpha,beta,!whiteMax)[0];
					if(whiteMax)
					{
						alpha=Math.max(alpha,hold);
						if(beta<=alpha)
						{
							al++;
							be++;
							break;
						}
						if(bestval<=hold)
						{
							bestval=hold;
							valpos[1]=source.get(i)*100+dest.get(j);
							valpos[0]=bestval;
						}
						
					}
					else
					{
						beta=Math.min(beta,hold);
						if(beta<=alpha)
						{
							al++;
							break;
						}
						if(bestval>=hold)
						{
							be++;
							bestval=hold;
							valpos[1]=source.get(i)*100+dest.get(j);
							valpos[0]=bestval;
						}
						
						
					}
				}
			}
			
		}
		//System.out.println("this"+valpos[0]);
		return valpos;
		
	}

	public static String getMoves(Scanner kb, int Player, boolean playBot, MenuBoard a)
	{
		if (!playBot)
			System.out.println("Enter a move in c1c2 format:");
		else
			System.out.print("");
		String c1 = kb.nextLine();
		if (c1.equals("R"))
		{
			return c1;
		}
		int file1 = ((int) c1.charAt(0) - 1) % 8;
		int row1 = 8 - (Integer.parseInt(c1.charAt(1) + ""));
		int file2 = ((int) c1.charAt(2) - 1) % 8;
		int row2 = 8 - (Integer.parseInt(c1.charAt(3) + ""));
		int source=(row1+1)*10 +file1;
		int dest =(row2+1)*10 +file2;
		int move = source*100+dest;
		while (!movePiece(move, Player, a))
		{
			System.out.println("illegal move, try again:");
			c1 = kb.nextLine();
			file1 = ((int) c1.charAt(0) - 1) % 8;
			row1 = 8 - (Integer.parseInt(c1.charAt(1) + ""));
			file2 = ((int) c1.charAt(2) - 1) % 8;
			row2 = 8 - (Integer.parseInt(c1.charAt(3) + ""));
			source=(row1+1)*10 +file1;
			dest =(row2+1)*10 +file2;
		}
		if (playBot || a.stopGame)
		{

		}
		else
			printBoard(((Player + 1) % 2), a);
		return c1;

	}

	public static boolean movePiece(int move, int Player, MenuBoard a)
	{
		int file2  = move%10;
		int row2 =(((move-file2)%100)/10)-1;
		int file1  = ((move-file2-(10*row2+1))%1000)/100;
		int row1 =Math.floorDiv(move, 1000)-1;
		boolean isWhite = (Player % 2 == 0);
		//System.out.println(move+"\t"+row1+" "+file1+"\t"+row2+" "+file2);
		if ((move==8482||move==1412)&&a.Board[row1][file1].contains("K"))
		{
			if (isWhite)
			{
				if (a.WcanC && kingChk("w", a.WK[0], a.WK[1], a).contains(82))
				{
					a.Board[7][4] = "[  ]";
					a.Board[7][2] = "[wK]";
					a.Board[7][3] = "[wR]";
					a.Board[7][0] = "[  ]";
					a.WK[0] = 7;
					a.WK[1] = 2;
					a.WcanC = false;
					return true;
				}
				return false;
			}
			else
			{
				if (a.BcanC && kingChk("b", a.BK[0], a.BK[1], a).contains(12))
				{
					a.Board[0][4] = "[  ]";
					a.Board[0][2] = "[bK]";
					a.Board[0][3] = "[bR]";
					a.Board[0][0] = "[  ]";
					a.BK[0] = 0;
					a.BK[1] = 2;
					a.BcanC = false;
					return true;
				}
				return false;
			}
		}

		if ((move==8486||move==1416)&&a.Board[row1][file1].contains("K"))
		{
			if (isWhite)
			{
				if (a.WcanC && kingChk("w", a.WK[0], a.WK[1], a).contains(86))
				{
					a.Board[7][4] = "[  ]";
					a.Board[7][6] = "[wK]";
					a.Board[7][5] = "[wR]";
					a.Board[7][7] = "[  ]";
					a.WK[0] = 7;
					a.WK[1] = 6;
					a.WcanC = false;
					return true;
				}
				return false;

			}
			else
			{
				if (a.BcanC && kingChk("b", a.BK[0], a.BK[1], a).contains(16))
				{
					a.Board[0][4] = "[  ]";
					a.Board[0][6] = "[bK]";
					a.Board[0][5] = "[bR]";
					a.Board[0][7] = "[  ]";
					a.BK[0] = 0;
					a.BK[1] = 6;
					a.BcanC = false;
					return true;
				}
				return false;

			}
		}

		
		
		String Holder1 = a.Board[row1][file1];
		String Holder2 = a.Board[row2][file2];
		// System.out.println(row1+""+ file1+ " "+row2+""+
		// file2+""+a.Board[row1][file1]);
		if ((isWhite && a.Board[row1][file1].contains("w")) || (!isWhite && a.Board[row1][file1].contains("b")))
		{

			ArrayList<Integer> loc = getLegal(a,isWhite,row1,file1,a.Board[row1][file1].substring(2, 3));
			int pos = (row2+1)*10 + (file2);
			//System.out.println(loc.toString());
			//System.out.println(pos);
			for (int i = 0; i < loc.size(); i++)
			{ 
				if (loc.get(i)==pos)
				{
					String holder = a.Board[row1][file1];
					a.Board[row2][file2] = holder;
					a.Board[row1][file1] = "[  ]";
					if (holder.contains("K"))
					{
						if (holder.contains("w"))
						{
							a.WcanC = false;
							a.WK[0] = row2;
							a.WK[1] = file2;
						}
						else
						{
							a.BcanC = false;
							a.BK[0] = row2;
							a.BK[1] = file2;
						}
					}
					if (a.Board[row2][file2].contains("P") && (row2 == 0 || row2 == 7))
					{
						System.out.println("pick your upgrade:");
						char rankUp = kb.next().toUpperCase().charAt(0);//This kills the program at next kb input
						a.Board[row2][file2] = a.Board[row2][file2].replace('P', rankUp);
					}
					if (isWhite)
					{
						if (getAttacker("w", a.WK[0], a.WK[1], a))
						{
							a.Board[row1][file1] = Holder1;
							a.Board[row2][file2] = Holder2;
							return false;
						}
						if (getAttacker("b", a.BK[0], a.BK[1], a))
						{
							if (getLegal(a, isWhite,a.BK[0], a.BK[1],"K").size() == 0)
							{
								a.stopGame = true;
								a.whoWon = "White";
							}
						}
					}
					else
					{
						if (getAttacker("b", a.BK[0], a.BK[1], a))
						{
							a.Board[row1][file1] = Holder1;
							a.Board[row2][file2] = Holder2;
							
							return false;
						}
						if (getAttacker("w", a.WK[0], a.WK[1], a))
						{
							if (getLegal(a,isWhite, a.WK[0], a.WK[1], "K").size() == 0)
							{
								a.stopGame = true;
								a.whoWon = "Black";
							}
						}
					}

					return true;
				}
			}
			//System.out.println("problem here"+ loc.toString());
		}
		return false;

	}

	public static void setBoard(int Player, boolean isAI, MenuBoard a)
	{
		if (isAI)
			buildBoard(Player, a);
		else
			buildBoard(0, a);
	}

	public static void buildBoard(int Player, MenuBoard a)
	{

		String[] namesw = { "[wR]", "[wN]", "[wB]", "[wQ]", "[wK]", "[wB]", "[wN]", "[wR]" };
		String[] namesb = { "[bR]", "[bN]", "[bB]", "[bQ]", "[bK]", "[bB]", "[bN]", "[bR]" };
		for (int i = 0; i < 8; i++)
		{
			a.Board[7][i] = namesw[i];
			a.Board[6][i] = "[wP]";
			a.Board[0][i] = namesb[i];
			a.Board[1][i] = "[bP]";
		}

		for (int i = 0; i < 8; i++)
		{
			for (int j = 2; j < 6; j++)
			{
				a.Board[j][i] = "[  ]";
			}
		}
		// printa.Board(Player);
	}

	public static void printBoard(int Player, MenuBoard a)
	{
		
		boolean playerWhite = (Player % 2 == 0);
		System.out.println();
		int rank;
		if (playerWhite)
		{
			System.out.println("White to Move:");
			rank = 8;
			for (String[] c : a.Board)
			{
				for (String b : c)
				{
					System.out.print(b);
				}

				System.out.print(" |" + rank--);
				System.out.println();
			}
			System.out.println(" a   b   c   d   e   f   g   h  ");
		}
		else
		{
			System.out.println("Black to Move:");
			rank = 1;
			for (int i = 7; i >= 0; i--)
			{
				for (int j = 7; j >= 0; j--)
				{
					System.out.print(a.Board[i][j]);
				}
				System.out.print(" |" + rank++);
				System.out.println();
			}
			System.out.println(" h   g   f   e   d   c   b   a  ");
		}

	}

	public static boolean getAttacker(String color, int r, int c, MenuBoard a)
	{// these work, add pawn and king
		String othercolor;
		if (color.equals("b"))
			othercolor = "w";
		else
			othercolor = "b";
		ArrayList<Integer> bishop = bishopChk(color, r, c, a);
		int row;
		int col;
		for (int i = 0; i < bishop.size(); i++)
		{
			col = bishop.get(i)%10;
			row = ((bishop.get(i)-col)/10)-1;
			
			if (a.Board[row][col].contains(othercolor + "B") || a.Board[row][col].contains(othercolor + "Q"))
			{
				return true;
			}
		}
		ArrayList<Integer> knight = knightChk(color, r, c, a);
		for (int i = 0; i < knight.size(); i++)
		{
			col = knight.get(i)%10;
			row = ((knight.get(i)-col)/10)-1;
			if (a.Board[row][col].contains(othercolor + "N"))
			{
				return true;
			}
		}
		ArrayList<Integer> rook = rookChk(color, r, c, a);
		for (int i = 0; i < rook.size(); i++)
		{
			col = rook.get(i)%10;
			row = ((rook.get(i)-col)/10)-1;
			if (a.Board[row][col].contains(othercolor + "R") || a.Board[row][col].contains(othercolor + "Q"))
			{
				return true;
			}
		}
		ArrayList<Integer> pawn = pawnChk(color, r, c, a);
		for (int i = 0; i < pawn.size(); i++)
		{
			
			col = pawn.get(i)%10;
			row = ((pawn.get(i)-col)/10)-1;
			if (a.Board[row][col].contains(othercolor + "P"))
			{
				return true;
			}
		}
		ArrayList<Integer> king = pawnChk(color, r, c, a);
		for (int i = 0; i < king.size(); i++)
		{
			col = king.get(i)%10;
			row = ((king.get(i)-col)/10)-1;
			if (a.Board[row][col].contains(othercolor + "K"))
			{
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Integer> getLegal(MenuBoard a,boolean isWhite,int r,int c,String type)
	{
		String color= isWhite ?"w":"b";
		switch (type) {
		case "P":
			return pawnChk(color, r, c, a);

		case "N":
			return knightChk(color, r, c, a);

		case "B":
			return bishopChk(color, r, c, a);

		case "R":
			return rookChk(color, r, c, a);

		case "Q":
			return queenChk(color, r, c, a);

		case "K":
			return kingChk(color, r, c, a);

		case " ":
			return null;

		}
		return null;
	}

	public static ArrayList<Integer> pawnChk(String color, int r, int c, MenuBoard a)
	{// no ent-pas-ent, otherwise good I think
		ArrayList<Integer> moveset = new ArrayList<Integer>();

		if (color.contains("b"))
		{
			if (r < 7)
			{
				if (a.Board[r + 1][c].equals("[  ]"))
				{
					if (r == 1)
					{
						if (a.Board[r + 2][c].equals("[  ]"))
						{
							moveset.add(((r + 3)*10) +c);
						}

					}
					moveset.add((r + 2)*10 + c);
				}
				if (c < 6)
					if (a.Board[r + 1][c + 1].contains("w"))
					{
						moveset.add(((r + 2)*10) + (c + 1));
					}
				if (c > 0)
					if (a.Board[r + 1][c - 1].contains("w"))
					{
						moveset.add((r + 2)*10 + (c - 1));
					}
			}
		}
		else
		{
			if (r > 0)
			{
				if (a.Board[r - 1][c].equals("[  ]"))
				{
					if (r == 6)
					{
						if (a.Board[r - 2][c].equals("[  ]"))
						{
							moveset.add((r - 1)*10 + c);
						}

					}
					moveset.add((r)*10 + c);
				}
				if (c < 6)
					if (a.Board[r - 1][c + 1].contains("b"))
					{
						moveset.add((r)*10 + (c + 1));
					}
				if (c > 0)
					if (a.Board[r - 1][c - 1].contains("b"))
					{
						moveset.add((r)*10 + (c - 1));
					}
			}
		}
		return moveset;
	}

	public static ArrayList<Integer> knightChk(String color, int r, int c, MenuBoard a)
	{// should be good

		ArrayList<Integer> moveset = new ArrayList<Integer>();
		for (int r2 = -2; r2 < 3; r2++)
		{

			if (Math.abs(r2) == 1)
			{
				if ((c - 2 >= 0) && ((r + r2) >= 0 && (r + r2) < 8))
				{
					if (!a.Board[r + r2][c - 2].contains(color))
					{
						moveset.add((r + r2+1)*10 + (c - 2));
					}
				}
				if ((c + 2 < 8) && ((r + r2) >= 0 && (r + r2) < 8))
				{
					if (!a.Board[r + r2][c + 2].contains(color))
					{
						moveset.add((r + r2+1)*10 + (c + 2));
					}
				}
			}
			if (Math.abs(r2) == 2)
			{
				if ((c - 1 >= 0) && ((r + r2) >= 0 && (r + r2) < 8))
				{
					if (!a.Board[r + r2][c - 1].contains(color))
					{
						moveset.add((r + r2+1)*10 + (c - 1));
					}
				}
				if ((c + 1 < 8) && ((r + r2) >= 0 && (r + r2) < 8))
				{
					if (!a.Board[r + r2][c + 1].contains(color))
					{
						moveset.add((r + r2+1)*10 + (c + 1));
					}
				}
			}
		}
		return moveset;
	}

	public static ArrayList<Integer> bishopChk(String color, int r, int c, MenuBoard a)
	{
		ArrayList<Integer> moveset = new ArrayList<Integer>();
		boolean nw = true;
		boolean ne = true;
		boolean sw = true;
		boolean se = true;
		boolean continues = true;
		int i = 0;
		while (continues)
		{
			i++;
			if (!(nw || ne || sw || se))
			{
				continues = false;
			}
			if (nw)
			{
				if (r - i >= 0 && c - i >= 0)
				{
					if (!(a.Board[r - i][c - i].contains(color)))
					{
						moveset.add((r - i+1)*10 + (c - i));
						if (!(a.Board[r - i][c - i].equals("[  ]")))
						{
							nw = false;
						}
					}
					else
						nw = false;
				}
				else
					nw = false;

			}
			if (ne)
			{
				if (r - i >= 0 && c + i < 8)
				{
					if (!(a.Board[r - i][c + i].contains(color)))
					{
						moveset.add((r - i+1)*10 + (c + i));
						if (!(a.Board[r - i][c + i].equals("[  ]")))
						{
							ne = false;
						}
					}
					else
						ne = false;
				}
				else
					ne = false;
			}
			if (sw)
			{
				if (r + i < 8 && c - i >= 0)
				{
					if (!(a.Board[r + i][c - i].contains(color)))
					{
						moveset.add((r + i+1)*10 + (c - i));
						if (!(a.Board[r + i][c - i].equals("[  ]")))
						{
							sw = false;
						}
					}
					else
						sw = false;
				}
				else
					sw = false;
			}
			if (se)
			{
				if (r + i < 8 && c + i < 8)
				{
					if (!(a.Board[r + i][c + i].contains(color)))
					{
						moveset.add((r + i+1)*10 + (c + i));
						if (!(a.Board[r + i][c + i].equals("[  ]")))
						{
							se = false;
						}
					}
					else
						se = false;
				}
				else
					se = false;
			}
		}
		return moveset;
	}

	public static ArrayList<Integer> rookChk(String color, int r, int c, MenuBoard a)
	{
		ArrayList<Integer> moveset = new ArrayList<Integer>();
		boolean n = true;
		boolean s = true;
		boolean e = true;
		boolean w = true;
		boolean continues = true;
		int i = 0;
		while (continues)
		{
			i++;
			if (!(n || e || s || w))
			{
				continues = false;
			}
			if (n)
			{
				if (r - i >= 0)
				{
					if (!a.Board[r - i][c].contains(color))
					{
						moveset.add((r - i+1)*10 + (c));
						if (!a.Board[r - i][c].contains("[  ]"))
						{
							n = false;
						}
					}
					else
						n = false;
				}
				else
					n = false;

			}
			if (e)
			{
				if (c + i < 7)
				{
					if (!a.Board[r][c + i].contains(color))
					{
						moveset.add((r+1)*10 + (c + i));
						if (!a.Board[r][c + i].contains("[  ]"))
						{
							e = false;
						}
					}
					else
						e = false;
				}
				else
					e = false;
			}
			if (w)
			{
				if (c - i >= 0)
				{
					if (!a.Board[r][c - i].contains(color))
					{
						moveset.add((r+1)*10 + (c - i));
						if (!a.Board[r][c - i].contains("[  ]"))
						{
							w = false;
						}
					}
					else
						w = false;
				}
				else
					w = false;
			}
			if (s)
			{
				if (r + i < 7)
				{
					if (!a.Board[r + i][c].contains(color))
					{
						moveset.add((r + i+1)*10 + (c));
						if (!a.Board[r + i][c].contains("[  ]"))
						{
							s = false;
						}
					}
					else
						s = false;
				}
				else
					s = false;
			}
		}
		return moveset;
	}

	public static ArrayList<Integer> queenChk(String color, int r, int c, MenuBoard a)
	{
		ArrayList<Integer> moveset = rookChk(color, r, c, a);
		ArrayList<Integer> movesetTwo = bishopChk(color, r, c, a);
		for (int i = 0; i < movesetTwo.size(); i++)
		{
			moveset.add(movesetTwo.get(i));
		}
		return moveset;
	}

	public static ArrayList<Integer> kingChk(String color, int r, int c, MenuBoard a)
	{
		String holder, holder2 = "";

		ArrayList<Integer> moveset = new ArrayList<Integer>();
		for (int i = -1; i < 2; i++)
		{
			for (int j = -1; j < 2; j++)
			{
				if ((r + i < 8 && r + i >= 0) && (c + j < 8 && c + j >= 0))
				{
					if ((!a.Board[r + i][c + j].contains(color)))
					{
						holder = a.Board[r][c];
						holder2 = a.Board[r + i][c + j];
						a.Board[r][c] = "[  ]";
						a.Board[r + i][c + j] = holder;
						if (!getAttacker(color, (r + i), (c + j), a))// fix w/
																		// pawns
						{
							moveset.add((r + i+1)*10 + (c + j));
						}
						a.Board[r][c] = holder;
						a.Board[r + i][c + j] = holder2;

					}
				}
			}
		}

		if (color.contains("w") && a.WcanC)
		{
			if (a.Board[7][1].equals("[  ]") && a.Board[7][2].equals("[  ]") && a.Board[7][3].equals("[  ]"))
			{
				if (!(getAttacker("w", 7, 1, a) || getAttacker("w", 7, 2, a) || getAttacker("w", 7, 3, a)
						|| getAttacker("w", 7, 4, a)))
				{
					moveset.add(82);
				}
			}
			if (a.Board[7][5].equals("[  ]") && a.Board[7][6].equals("[  ]"))
			{
				if (!(getAttacker("w", 7, 5, a) || getAttacker("w", 7, 6, a) || getAttacker("w", 7, 4, a)))
				{
					moveset.add(86);
				}
			}
		}
		else
			if (a.BcanC)
			{
				if (a.Board[0][1].equals("[  ]") && a.Board[0][2].equals("[  ]") && a.Board[0][3].equals("[  ]"))
				{
					if (!(getAttacker("b", 0, 1, a) || getAttacker("b", 0, 2, a) || getAttacker("b", 0, 3, a)
							|| getAttacker("b", 0, 4, a)))
					{
						moveset.add(12);
					}
				}
				if (a.Board[0][5].equals("[  ]") && a.Board[0][6].equals("[  ]"))
				{
					if (!(getAttacker("b", 0, 5, a) || getAttacker("b", 0, 6, a) || getAttacker("b", 7, 0, a)))
					{
						moveset.add(16);
					}
				}

			}

		return moveset;
	}
}