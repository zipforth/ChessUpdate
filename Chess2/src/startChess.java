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
 * 
 * add more AI:
 * |X|make a material counter P=8 N=20 B=22 R=40 Q=80 K=2000
 * |X|implement for one move
 * | |implement for 3-ish moves
 * |X|make board heat maps
 * |X|implement board heat maps where counter is
 * 
 * add gui
 * add thread for timer
 * row 0 starts as black
 */
public class startChess
{
	public static int[][][] PieceMaps = new int[12][8][8];
	static int difficulty;
	
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

		Scanner kb = new Scanner(System.in);
		while (beginMatch(kb))
		{

		}
	}

	public static MenuBoard setMatch(Scanner kb)
	{
		MenuBoard a = new MenuBoard();

		System.out.println("Please use c1 c2 format\n" + "for promotion, add rank at end"
				+ "\n0-0 for castle, 0-0-0 for queenside" + "\nPlay against human or AI?(H/A)");

		a.bot = kb.nextLine().contains("A");
		if (a.bot)
		{
			System.out.println("Difficulty (VE/E/M/H/I");
			String diff=kb.nextLine();
			switch(diff)
			{
			case "[Vv][Ee]":difficulty=0;break;
			case "[Ee]":difficulty=1;break;
			case "[Mm]":difficulty=2;break;
			case "[Hh]":difficulty=3;break;
			case "[Ii]":difficulty=8;break;
			}
			System.out.println(difficulty+" Pick a side(b/w):");

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

	public static boolean beginMatch(Scanner kb)
	{
		MenuBoard a = setMatch(kb);
		while (true)
		{
			if (!a.bot)
			{
				a.win = getMoves(kb, a.Player, a.bot, a);
				if (a.stopGame)
				{
					return newGame(kb, a.whoWon);
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
							return newGame(kb, a.whoWon);
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
							return newGame(kb, a.whoWon);
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
					System.out.println("Black wins, white resigns");
				}
				else
					System.out.println("White wins, black resigns");
				return newGame(kb, a.whoWon);

			}
		}
	}

	public static boolean newGame(Scanner kb, String whoWon)
	{
		System.out.println("Checkmate, " + whoWon + " wins");
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
		// can look one move ahead
		// implement minmax tree, with each move looked at now as a seperate
		// tree
		// from there, take each board in the tree and find average
		// best average tree is the pick
		// the average should be found recursively for most efficiency
		// implement a checkmate==min int
		//need pruning badly, can't run 4
		String holder[]=checkStrat(AIColor, notAI, c, a, 4).split(":");

		String bestMove = holder[1];
		movePiece(bestMove, a.Player + 1, a);
		System.out.println(bestMove);
		printBoard(Player, a);

	}

	public static ArrayList<String> AIPieces(String AIColor, MenuBoard a)
	{
		ArrayList<String> myPieces = new ArrayList<String>();
		int matVal = 0;
		int i;
		int key = 0;
		for (int r = 0; r < 8; r++)
		{
			for (int col = 0; col < 8; col++)
			{
				if (a.Board[r][col].contains(AIColor))
				{
					char pieceRank = a.Board[r][col].charAt(2);
					myPieces.add(r + "" + col);
					matVal += a.piecEquation(pieceRank);
					key = (int) Math.round(365176889.8 * Math.pow(Math.E, ((-1485.383126) / (int) pieceRank)));

					if (AIColor.equals("w"))
					{

						matVal += PieceMaps[key][r][col];
					}
					else
					{

						matVal += PieceMaps[key + 6][r][col];

					}
				}
			}
		}

		myPieces.add(0, matVal + "");
		return myPieces;
	}

	public static String checkStrat(String AIColor, String notAI, char[] c, MenuBoard a, int more)
	{
		ArrayList<String> myPieces = AIPieces(AIColor, a);
		int valMe = Integer.parseInt(myPieces.get(0));
		int valThem = Integer.parseInt(AIPieces(notAI, a).get(0));
		ArrayList<String> legalMoves = new ArrayList<String>();
		int menext;
		int themnext;
		int ratioToBeat = Integer.MIN_VALUE;
		int beater;
		int nextUpMe, nextUpThem;
		String bestMove = "";
		String move = "";
		int c1, c2, r1, r2;

		for (int i = 1; i < myPieces.size(); i++)
		{
			legalMoves.clear();
			r1 = Integer.parseInt(myPieces.get(i).charAt(0) + "");
			c1 = Integer.parseInt(myPieces.get(i).charAt(1) + "");
			legalMoves = getLegal(a.Board[r1][c1].charAt(2) + "", AIColor, r1, c1, a);
			if (legalMoves.size() > 0)
				for (int i2 = 0; i2 < legalMoves.size(); i2++)
				{
					if (!legalMoves.get(i2).contains("-"))
					{
						MenuBoard b = a.copyBoard();
						
						// needs rules for castling and parsing
						// enemy section doesn't give anything useful
						r2 = Integer.parseInt(legalMoves.get(i2).charAt(0) + "");
						c2 = Integer.parseInt(legalMoves.get(i2).charAt(1) + "");
						move = c[c1] + "" + (8 - r1) + " " + c[c2] + "" + (8 - r2);
						b.Player=b.Player+1;
						movePiece(move, b.Player, b);

						menext = Integer.parseInt(AIPieces(AIColor, b).get(0));
						themnext = Integer.parseInt(AIPieces(notAI, b).get(0));
						
						if (more!=0)
						{
							MenuBoard forRecursion=b.copyBoard();
							String enemy[] = checkStrat(notAI, AIColor, c, forRecursion, more-1).split(":");

							nextUpMe = menext;
							nextUpThem =themnext;
							beater = (nextUpMe-nextUpThem)-(Integer.parseInt(enemy[0]));
							//System.out.println(beater+" "+move+" | "+enemy[1]);
						}
						else
						{
							nextUpMe = menext;
							nextUpThem = themnext;
							beater = (nextUpMe-nextUpThem);
						}
						
						if (beater >= ratioToBeat)
						{
							ratioToBeat = beater;
								bestMove = move;
						}
						nextUpMe = 0;
						nextUpThem = 0;
					}
				}
		}
		return ratioToBeat + ":" + bestMove;
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
		while (!movePiece(c1, Player, a))
		{
			System.out.println("illegal move, try again:");
			c1 = kb.nextLine();
		}
		if (playBot || a.stopGame)
		{

		}
		else
			printBoard(((Player + 1) % 2), a);
		return c1;

	}

	public static boolean movePiece(String c1, int Player, MenuBoard a)
	{

		boolean isWhite = (Player % 2 == 0);
		if (c1.equals("0-0-0") || c1.equals("O-O-O"))
		{
			if (isWhite)
			{
				if (a.WcanC && kingChk("w", a.WK[0], a.WK[1], a).contains("0-0-0"))
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
				if (a.BcanC && kingChk("b", a.BK[0], a.BK[1], a).contains("0-0-0"))
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

		if (c1.equals("0-0") || c1.equals("O-O"))
		{
			if (isWhite)
			{
				if (a.WcanC && kingChk("w", a.WK[0], a.WK[1], a).contains("0-0"))
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
				if (a.BcanC && kingChk("b", a.BK[0], a.BK[1], a).contains("0-0"))
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

		c1 = c1.replaceAll("\\s+", "");

		int file1 = ((int) c1.charAt(0) - 1) % 8;
		int row1 = 8 - (Integer.parseInt(c1.charAt(1) + ""));
		int file2 = ((int) c1.charAt(2) - 1) % 8;
		int row2 = 8 - (Integer.parseInt(c1.charAt(3) + ""));
		String Holder1 = a.Board[row1][file1];
		String Holder2 = a.Board[row2][file2];
		// System.out.println(row1+""+ file1+ " "+row2+""+
		// file2+""+a.Board[row1][file1]);
		if ((isWhite && a.Board[row1][file1].contains("w")) || (!isWhite && a.Board[row1][file1].contains("b")))
		{

			ArrayList<String> loc = getLegal(a.Board[row1][file1].substring(2, 3), a.Board[row1][file1].substring(1, 2),
					row1, file1, a);
			String pos = (row2) + "" + (file2);

			for (int i = 0; i < loc.size(); i++)
			{
				if (loc.get(i).equals(pos))
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
						char rankUp = c1.charAt(4);
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
							if (getLegal("K", "b", a.BK[0], a.BK[1], a).size() == 0)
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
							if (getLegal("K", "w", a.WK[0], a.WK[1], a).size() == 0)
							{
								a.stopGame = true;
								a.whoWon = "Black";
							}
						}
					}

					return true;
				}
			}
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
		ArrayList<String> bishop = bishopChk(color, r, c, a);
		int row;
		int col;
		for (int i = 0; i < bishop.size(); i++)
		{
			row = Integer.parseInt(bishop.get(i).charAt(0) + "");
			col = Integer.parseInt(bishop.get(i).charAt(1) + "");
			if (a.Board[row][col].contains(othercolor + "B") || a.Board[row][col].contains(othercolor + "Q"))
			{
				return true;
			}
		}
		ArrayList<String> knight = knightChk(color, r, c, a);
		for (int i = 0; i < knight.size(); i++)
		{
			row = Integer.parseInt(knight.get(i).charAt(0) + "");
			col = Integer.parseInt(knight.get(i).charAt(1) + "");
			if (a.Board[row][col].contains(othercolor + "N"))
			{
				return true;
			}
		}
		ArrayList<String> rook = rookChk(color, r, c, a);
		for (int i = 0; i < rook.size(); i++)
		{
			row = Integer.parseInt(rook.get(i).charAt(0) + "");
			col = Integer.parseInt(rook.get(i).charAt(1) + "");
			if (a.Board[row][col].contains(othercolor + "R") || a.Board[row][col].contains(othercolor + "Q"))
			{
				return true;
			}
		}
		ArrayList<String> pawn = pawnChk(color, r, c, a);
		for (int i = 0; i < pawn.size(); i++)
		{
			row = Integer.parseInt(pawn.get(i).charAt(0) + "");
			col = Integer.parseInt(pawn.get(i).charAt(1) + "");
			if (a.Board[row][col].contains(othercolor + "P"))
			{
				return true;
			}
		}
		ArrayList<String> king = pawnChk(color, r, c, a);
		for (int i = 0; i < king.size(); i++)
		{
			row = Integer.parseInt(king.get(i).charAt(0) + "");
			col = Integer.parseInt(king.get(i).charAt(1) + "");
			if (a.Board[row][col].contains(othercolor + "K"))
			{
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> getLegal(String type, String color, int r, int c, MenuBoard a)
	{
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

	public static ArrayList<String> pawnChk(String color, int r, int c, MenuBoard a)
	{// no ent-pas-ent, otherwise good I think
		ArrayList<String> moveset = new ArrayList<String>();

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
							moveset.add((r + 2) + "" + c);
						}

					}
					moveset.add((r + 1) + "" + c);
				}
				if (c < 6)
					if (a.Board[r + 1][c + 1].contains("w"))
					{
						moveset.add((r + 1) + "" + (c + 1));
					}
				if (c > 0)
					if (a.Board[r + 1][c - 1].contains("w"))
					{
						moveset.add((r + 1) + "" + (c - 1));
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
							moveset.add((r - 2) + "" + c);
						}

					}
					moveset.add((r - 1) + "" + c);
				}
				if (c < 6)
					if (a.Board[r - 1][c + 1].contains("b"))
					{
						moveset.add((r - 1) + "" + (c + 1));
					}
				if (c > 0)
					if (a.Board[r - 1][c - 1].contains("b"))
					{
						moveset.add((r - 1) + "" + (c - 1));
					}
			}
		}
		return moveset;
	}

	public static ArrayList<String> knightChk(String color, int r, int c, MenuBoard a)
	{// should be good

		ArrayList<String> moveset = new ArrayList<String>();
		for (int r2 = -2; r2 < 3; r2++)
		{

			if (Math.abs(r2) == 1)
			{
				if ((c - 2 >= 0) && ((r + r2) >= 0 && (r + r2) < 8))
				{
					if (!a.Board[r + r2][c - 2].contains(color))
					{
						moveset.add((r + r2) + "" + (c - 2));
					}
				}
				if ((c + 2 < 8) && ((r + r2) >= 0 && (r + r2) < 8))
				{
					if (!a.Board[r + r2][c + 2].contains(color))
					{
						moveset.add((r + r2) + "" + (c + 2));
					}
				}
			}
			if (Math.abs(r2) == 2)
			{
				if ((c - 1 >= 0) && ((r + r2) >= 0 && (r + r2) < 8))
				{
					if (!a.Board[r + r2][c - 1].contains(color))
					{
						moveset.add((r + r2) + "" + (c - 1));
					}
				}
				if ((c + 1 < 8) && ((r + r2) >= 0 && (r + r2) < 8))
				{
					if (!a.Board[r + r2][c + 1].contains(color))
					{
						moveset.add((r + r2) + "" + (c + 1));
					}
				}
			}
		}
		return moveset;
	}

	public static ArrayList<String> bishopChk(String color, int r, int c, MenuBoard a)
	{
		ArrayList<String> moveset = new ArrayList<String>();
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
						moveset.add((r - i) + "" + (c - i));
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
						moveset.add((r - i) + "" + (c + i));
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
						moveset.add((r + i) + "" + (c - i));
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
						moveset.add((r + i) + "" + (c + i));
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

	public static ArrayList<String> rookChk(String color, int r, int c, MenuBoard a)
	{
		ArrayList<String> moveset = new ArrayList<String>();
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
				if (r - i > 0)
				{
					if (!a.Board[r - i][c].contains(color))
					{
						moveset.add((r - i) + "" + (c));
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
						moveset.add((r) + "" + (c + i));
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
				if (c - i > 0)
				{
					if (!a.Board[r][c - i].contains(color))
					{
						moveset.add((r) + "" + (c - i));
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
						moveset.add((r + i) + "" + (c));
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

	public static ArrayList<String> queenChk(String color, int r, int c, MenuBoard a)
	{
		ArrayList<String> moveset = rookChk(color, r, c, a);
		ArrayList<String> movesetTwo = bishopChk(color, r, c, a);
		for (int i = 0; i < movesetTwo.size(); i++)
		{
			moveset.add(movesetTwo.get(i));
		}
		return moveset;
	}

	public static ArrayList<String> kingChk(String color, int r, int c, MenuBoard a)
	{
		String holder, holder2 = "";

		ArrayList<String> moveset = new ArrayList<String>();
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
							moveset.add((r + i) + "" + (c + j));
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
					moveset.add("0-0-0");
				}
			}
			if (a.Board[7][5].equals("[  ]") && a.Board[7][6].equals("[  ]"))
			{
				if (!(getAttacker("w", 7, 5, a) || getAttacker("w", 7, 6, a) || getAttacker("w", 7, 4, a)))
				{
					moveset.add("0-0");
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
						moveset.add("0-0-0");
					}
				}
				if (a.Board[0][5].equals("[  ]") && a.Board[0][6].equals("[  ]"))
				{
					if (!(getAttacker("b", 0, 5, a) || getAttacker("b", 0, 6, a) || getAttacker("b", 7, 0, a)))
					{
						moveset.add("0-0");
					}
				}

			}

		return moveset;
	}
}