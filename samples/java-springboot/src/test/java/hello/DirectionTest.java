package hello;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class DirectionTest {

	@Test
	public void direction() {
		Direction north = Direction.N;
		Coord current = new Coord(10, 10);
		assertEquals("[10, 7]", north.getStep(current, 3).toString());
	}

	@Test
	public void getStepGoesOutOfField() {
		Coord negativeY = Direction.N.getStep(new Coord(0, 0), 1);
		assertEquals(0, negativeY.x);
		// invalid for this game, but correct as far as typical coordinate grid goes.
		assertEquals(-1, negativeY.y);
	}

	@Test
	public void getStepMovesSingleStep() {
		assertEquals(new Coord(2, 3), Direction.N.getStep(new Coord(2, 4), 1));
		assertEquals(new Coord(2, 5), Direction.S.getStep(new Coord(2, 4), 1));
		assertEquals(new Coord(1, 4), Direction.W.getStep(new Coord(2, 4), 1));
		assertEquals(new Coord(3, 4), Direction.E.getStep(new Coord(2, 4), 1));
	}

	@Test
	public void getStepMovesMultipleSteps() {
		assertEquals(new Coord(2, 1), Direction.N.getStep(new Coord(2, 4), 3));
		assertEquals(new Coord(2, 7), Direction.S.getStep(new Coord(2, 4), 3));
		assertEquals(new Coord(-1, 4), Direction.W.getStep(new Coord(2, 4), 3));
		assertEquals(new Coord(5, 4), Direction.E.getStep(new Coord(2, 4), 3));
	}

	@Test
	public void getThreat() {
		assertEquals(new Coord(10, 15), Direction.N.getThreat(new Coord(10, 10), 5));
		assertEquals(new Coord(10, 5), Direction.S.getThreat(new Coord(10, 10), 5));
		assertEquals(new Coord(15, 10), Direction.W.getThreat(new Coord(10, 10), 5));
		assertEquals(new Coord(5, 10), Direction.E.getThreat(new Coord(10, 10), 5));

	}

	@Test
	public void canMoveReturnsTrueWhenNoWallsEncountered() {
		Arena arena = new Arena(3, 3);

		Board board = new Board(arena);

		assertTrue(Direction.N.canMove(new Coord(1, 2), board));
		assertTrue(Direction.S.canMove(new Coord(1, 0), board));
		assertTrue(Direction.W.canMove(new Coord(2, 1), board));
		assertTrue(Direction.E.canMove(new Coord(0, 1), board));
	}

	@Test
	public void canMoveReturnsFalseWhenRunningIntoWallsWalls() {
		Arena arena = new Arena(3, 3);

		Board board = new Board(arena);

		assertFalse(Direction.N.canMove(new Coord(1, 0), board));
		assertFalse(Direction.S.canMove(new Coord(1, 2), board));
		assertFalse(Direction.W.canMove(new Coord(0, 1), board));
		assertFalse(Direction.E.canMove(new Coord(2, 1), board));
	}

	@Test
	public void canMoveReturnsFalseWhenRunningIntoOtherBots() {
		/*
		    _ _ _
		    _ X _
		    _ _ _
		 */
		Arena arena = new Arena(3, 3,
				new PlayerState("playerA", 1, 1, "N"));

		Board board = new Board(arena);

		assertFalse(Direction.N.canMove(new Coord(1, 2), board));
		assertFalse(Direction.S.canMove(new Coord(1, 0), board));
		assertFalse(Direction.W.canMove(new Coord(2, 1), board));
		assertFalse(Direction.E.canMove(new Coord(0, 1), board));
	}

}
