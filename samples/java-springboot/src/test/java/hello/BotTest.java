package hello;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import hello.Bot.Board;
import hello.Bot.Coord;
import hello.Bot.Direction;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class BotTest {


	final static Board board3by3;

	static {
		Arena arena = new Arena();
		arena.dims = new ArrayList<>(2);
		arena.dims.add(3); // narrow
		arena.dims.add(3); // tall
		arena.state = new HashMap<>();

		board3by3 = new Board(arena, new PlayerState(0, 0, "W"));;

	}
	Bot bot = new Bot();

	@Test
	public void isForwardPossible_middleOfBoard() {



		assertTrue(bot.isForwardPossible(new PlayerState(1, 1, "N"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(1, 1, "S"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(1, 1, "E"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(1, 1, "W"), board3by3));

	}

	@Test
	public void isForwardPossible_middleOfEdge() {

		// top row, middle column
		assertFalse(bot.isForwardPossible(new PlayerState(1, 0, "N"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(1, 0, "S"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(1, 0, "E"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(1, 0, "W"), board3by3));

		// middle row, first column
		assertTrue(bot.isForwardPossible(new PlayerState(0, 1, "N"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(0, 1, "S"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(0, 1, "E"), board3by3));
		assertFalse(bot.isForwardPossible(new PlayerState(0, 1, "W"), board3by3));

		// middle row, last column
		assertTrue(bot.isForwardPossible(new PlayerState(2, 1, "N"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(2, 1, "S"), board3by3));
		assertFalse(bot.isForwardPossible(new PlayerState(2, 1, "E"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(2, 1, "W"), board3by3));

		// bottom row, middle column
		assertTrue(bot.isForwardPossible(new PlayerState(1, 2, "N"), board3by3));
		assertFalse(bot.isForwardPossible(new PlayerState(1, 2, "S"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(1, 2, "E"), board3by3));
		assertTrue(bot.isForwardPossible(new PlayerState(1, 2, "W"), board3by3));
	}

	@Test
	public void initBoardWithBots() {
		Arena arena = new Arena();
		arena.dims = new ArrayList<>(2);
		arena.dims.add(3); // narrow
		arena.dims.add(6); // tall
		arena.state = new HashMap<>();

		// unused
		PlayerState selfState = new PlayerState(0, 0, "N");

		PlayerState stateA = new PlayerState(2, 5, "N"); // lower right corner
		PlayerState stateB = new PlayerState(1, 3, "W");
		arena.state.put("playerA", stateA);
		arena.state.put("playerB", stateB);


		Board board = new Board(arena, selfState);
		assertEquals(3, board.width);
		assertEquals(6, board.height);
		assertEquals(2, board.eastBots);
		assertEquals(0, board.westBots);
		assertEquals(0, board.northBots);
		assertEquals(2, board.southBots);
		assertEquals(Direction.S, board.getDirectionWithMostTargets());

		assertTrue(board.hasBot(new Coord(2, 5)));
		assertTrue(board.hasBot(new Coord(1, 3)));

		assertFalse(board.hasBot(new Coord(0, 0)));
		assertFalse(board.hasBot(new Coord(0, 5)));
		assertFalse(board.hasBot(new Coord(2, 0)));

	}

	@Test
	public void direction() {
		Direction north = Direction.N;
		Coord current = new Coord(10, 10);
		assertEquals("[10, 7]", north.getStep(current, 3).toString());
	}

	@Test
	public void canThrowLeaf() {

		/*
		    _ _ _
		    _ _ _
		    _ _ _
		    _ O> _
		    _ _ X
		    _ _ <O
		 */
		Arena arena = new Arena();
		arena.dims = new ArrayList<>(2);
		arena.dims.add(3); // narrow
		arena.dims.add(6); // tall
		arena.state = new HashMap<>();
		PlayerState stateA = new PlayerState(2, 5, "N"); // lower right corner
		PlayerState stateB = new PlayerState(1, 3, "W");
		arena.state.put("playerA", stateA);
		arena.state.put("playerB", stateB);

		PlayerState selfState = new PlayerState(2, 4, "N"); // unused
		Board board = new Board(arena, selfState);
		assertEquals(1, board.eastBots);
		assertEquals(1, board.westBots);
		assertEquals(1, board.northBots);
		assertEquals(1, board.southBots);
		assertEquals(Direction.N, board.getDirectionWithMostTargets());

		/*
				X _ _
		    _ _ _
		    _ _ _
		    _ O> _
		    _ _ _
		    _ _ ^
		 */
		assertFalse(bot.canThrowLeaf(new Coord(0, 0), Direction.N, board));
		assertFalse(bot.canThrowLeaf(new Coord(0, 0), Direction.W, board));
		assertFalse(bot.canThrowLeaf(new Coord(0, 0), Direction.E, board));
		assertFalse(bot.canThrowLeaf(new Coord(0, 0), Direction.S, board));

		/*
				_ _ _
		    _ _ _
		    _ _ _
		    X O>_
		    _ _ _
		    _ _^
		 */
		assertFalse(bot.canThrowLeaf(new Coord(0, 3), Direction.N, board));
		assertFalse(bot.canThrowLeaf(new Coord(0, 3), Direction.W, board));
		assertTrue(bot.canThrowLeaf(new Coord(0, 3), Direction.E, board));
		assertFalse(bot.canThrowLeaf(new Coord(0, 3), Direction.S, board));

		/*
				_ X _
		    _ _ _
		    _ _ _
		    _ O>_
		    _ _ _
		    _ _ ^
		 */
		Coord coord3 = new Coord(1, 0);
		assertFalse(bot.canThrowLeaf(coord3, Direction.N, board));
		assertFalse(bot.canThrowLeaf(coord3, Direction.W, board));
		assertFalse(bot.canThrowLeaf(coord3, Direction.E, board));
		assertTrue(bot.canThrowLeaf(coord3, Direction.S, board));


		/*
			_ _ _
			_ _ _
			_ _ X
			_ O>_
			_ _ _
			_ _ ^
	 */
		Coord coord4 = new Coord(2, 2);
		assertFalse(bot.canThrowLeaf(coord4, Direction.N, board));
		assertFalse(bot.canThrowLeaf(coord4, Direction.W, board));
		assertFalse(bot.canThrowLeaf(coord4, Direction.E, board));
		assertTrue(bot.canThrowLeaf(coord4, Direction.S, board));

		/*
			_ _ _
			_ _ X
			_ _ _
			_ O>_
			_ _ _
			_ _ ^
	 */
		Coord coord5 = new Coord(2, 1);
		assertFalse(bot.canThrowLeaf(coord5, Direction.N, board));
		assertFalse(bot.canThrowLeaf(coord5, Direction.W, board));
		assertFalse(bot.canThrowLeaf(coord5, Direction.E, board));
		assertFalse(bot.canThrowLeaf(coord5, Direction.S, board));
	}

	@Test
	public void hasThreatBot() {
		/*
		    _ _ _
		    _ _ _
		    _ _ _
		    _ > _
		    _ _ _
		    _ _ ^
		 */
		Arena arena = new Arena();
		arena.dims = new ArrayList<>(2);
		arena.dims.add(3); // narrow
		arena.dims.add(6); // tall
		arena.state = new HashMap<>();
		PlayerState stateA = new PlayerState(2, 5, "N"); // lower right corner
		PlayerState stateB = new PlayerState(1, 3, "W");
		arena.state.put("playerA", stateA);
		arena.state.put("playerB", stateB);

		PlayerState selfState = new PlayerState(0, 0, "N"); // unused
		Board board = new Board(arena, selfState);

		/*
				X _ _
		    _ _ _
		    _ _ _
		    _ O> _
		    _ _ _
		    _ _ ^
		 */
		assertFalse(board.hasThreatBot(new Coord(0, 0)));

		/*
				_ _ _
		    _ _ _
		    _ _ _
		    X O>_
		    _ _ _
		    _ _ ^
		 */
		assertTrue(board.hasThreatBot(new Coord(0, 3)));

		/*
				_ X _
		    _ _ _
		    _ _ _
		    _ O>_
		    _ _ _
		    _ _ ^
		 */
		Coord coord3 = new Coord(1, 0);
		// there is bot but facing other direction
		assertFalse(board.hasThreatBot(coord3));



		/*
			_ _ _
			_ _ _
			_ _ X
			_ > _
			_ _ _
			_ _ ^
	 */
		Coord coord4 = new Coord(2, 2);
		assertTrue(board.hasThreatBot(coord4));


		/*
			_ _ _
			_ _ X
			_ _ _
			_ O>_
			_ _ _
			_ _ <O
	 */
		Coord coord5 = new Coord(2, 1);
		assertFalse(board.hasThreatBot(coord5));

	}
}
