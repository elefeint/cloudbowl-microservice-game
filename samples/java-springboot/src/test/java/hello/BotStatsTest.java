package hello;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BotStatsTest {

	@Test
	public void botsOnOneSide() {
		Arena arena = new Arena(3, 6,
				new PlayerState("playerA", 2, 5, "N"), // lower right corner
				new PlayerState("playerB", 1, 3, "W")
				);

		Board board = new Board(arena);
		BotStats stats = new BotStats(board, new Coord(0,0));

		assertEquals(2, stats.eastBots);
		assertEquals(0, stats.westBots);
		assertEquals(0, stats.northBots);
		assertEquals(2, stats.southBots);
		assertEquals(Direction.S, stats.getDirectionWithMostTargets());

	}

	@Test
	public void botsOnAllSides() {
		Arena arena = new Arena(3, 6,
				new PlayerState("playerA",2, 5, "N"), // lower right corner
				new PlayerState("playerB",1, 3, "W"));

		Board board = new Board(arena);
		BotStats stats = new BotStats(board, new Coord(2, 4));

		assertEquals(1, stats.eastBots);
		assertEquals(1, stats.westBots);
		assertEquals(1, stats.northBots);
		assertEquals(1, stats.southBots);
		assertEquals(Direction.N, stats.getDirectionWithMostTargets());
	}
}
