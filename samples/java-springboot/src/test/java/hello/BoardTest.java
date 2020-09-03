package hello;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class BoardTest {

	@Test
	public void initBoardWithBots() {
		Arena arena = new Arena(3, 6,
				new PlayerState("playerA", 2, 5, "N"), // lower right corner
				new PlayerState("playerB", 1, 3, "W")
				);

		Board board = new Board(arena);
		assertEquals(3, board.width);
		assertEquals(6, board.height);

		assertTrue(board.hasBot(new Coord(2, 5)));
		assertTrue(board.hasBot(new Coord(1, 3)));

		assertFalse(board.hasBot(new Coord(0, 0)));
		assertFalse(board.hasBot(new Coord(0, 5)));
		assertFalse(board.hasBot(new Coord(2, 0)));
	}


}
