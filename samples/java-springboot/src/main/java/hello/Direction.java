package hello;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.IntBinaryOperator;

enum Direction {
	N ((x,s) -> x, (y,steps) -> y - steps, (coord, board) -> coord.y > 0),
	E ((x,steps) -> x + steps, (y,s) -> y, (coord, board) -> coord.x < board.width - 1),
	S ((x,s) -> x, (y,steps) -> y + steps, (coord, board) -> coord.y < board.height - 1),
	W ((x,steps) -> x - steps, (y,s) -> y, (coord, board) -> coord.x > 0);

	private IntBinaryOperator changeInX;
	private IntBinaryOperator changeInY;
	private BiPredicate<Coord, Board> canMovePredicate;

	static Map<Direction,Direction> opposites = new HashMap<>();
	static {
		opposites.put(N, S);
		opposites.put(S, N);
		opposites.put(W, E);
		opposites.put(E, W);
	}

	private Direction(IntBinaryOperator changeInX, IntBinaryOperator changeInY, BiPredicate<Coord, Board> canMove) {
		this.changeInX = changeInX;
		this.changeInY = changeInY;

		this.canMovePredicate = canMove;
	}

	public Coord getStep(Coord current, int stepsInDirection) {
		return new Coord(
				changeInX.applyAsInt(current.x, stepsInDirection),
				changeInY.applyAsInt(current.y, stepsInDirection));
	}

	public Coord getThreat(Coord current, int stepsInDirection) {
		return opposites.get(this).getStep(current, stepsInDirection);
	}

	public boolean canMove(Coord coord, Board board) {
		return canMovePredicate.test(coord, board) && !board.hasBot(getStep(coord, 1));
	}
}