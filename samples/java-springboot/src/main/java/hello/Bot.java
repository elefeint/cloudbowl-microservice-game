package hello;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.IntBinaryOperator;

public class Bot {

	private static Random random = new Random();

	static enum Move {
			FORWARD ("F"),
			TURN_LEFT ("L"),
			TURN_RIGHT ("R"),
			THROW_LEAF ("T");

			private String code;
			private static Map<String, Move> map = new HashMap<>();
			static {
				map.put("F", FORWARD);
				map.put("L", TURN_LEFT);
				map.put("R", TURN_RIGHT);
				map.put("T", THROW_LEAF);
			}

			private Move(String code) {
				this.code = code;
			}

			public String toString() {
				return this.code;
			}

			public Move fromCode(String code) {
				return map.get(code);
			}
	}

	static enum Direction {
		N ((x,s) -> x, (y,steps) -> y - steps, (coord, board) -> coord.y > 0),
		S ((x,s) -> x, (y,steps) -> y + steps, (coord, board) -> coord.y < board.height - 1),
		E ((x,steps) -> x + steps, (y,s) -> y, (coord, board) -> coord.x < board.width - 1),
		W ((x,steps) -> x - steps, (y,s) -> y, (coord, board) -> coord.x > 0);

		IntBinaryOperator changeInX;
		IntBinaryOperator changeInY;
		BiPredicate<Coord, Board> canMovePredicate;

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
			return canMovePredicate.test(coord, board);
		}
	}

	static class Coord {
		int x;
		int y;

		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public String toString() {
			return "[" + x + ", " + y + "]";
		}
	}
	static class Board {
		int width;
		int height;

		Direction[][] bots;

		int northBots, southBots, westBots, eastBots;

		Direction myDirection;
		Coord myCoords;

		public Board(Arena arena, PlayerState selfState) {
			this.width = arena.dims.get(0);
			this.height = arena.dims.get(1);

			this.bots = new Direction[this.height][this.width];

			this.myDirection = Direction.valueOf(selfState.direction);
			this.myCoords = new Coord(selfState.x, selfState.y);

			for (PlayerState state : arena.state.values()) {
				bots[state.y][state.x] = Direction.valueOf(state.direction);

				if (state.x >= selfState.x) {
					eastBots++;
				} else {
					westBots++;
				}

				if (state.y >= selfState.y) {
					southBots++;
				} else {
					northBots++;
				}
			}

			System.out.println("Bot counts: N=" + northBots + ", S=" + southBots
					+ ", W=" + westBots + ", E=" + eastBots);

		}

		public boolean hasBot(Coord coord) {
			if (coord.y < 0 || coord.y > this.height - 1) {
				return false;
			}
			if (coord.x < 0 || coord.x > this.width - 1) {
				return false;
			}
			return bots[coord.y][coord.x] != null;
		}

		public Direction getBotDirection(Coord coord) {
			if (coord.y < 0 || coord.y > this.height - 1) {
				return null;
			}
			if (coord.x < 0 || coord.x > this.width - 1) {
				return null;
			}
			return bots[coord.y][coord.x];
		}

		public boolean hasBot(Direction direction, Coord coord) {
			Direction d = getBotDirection(coord);
			return d == direction;
		}

		public boolean hasThreatBot(Coord coord) {
			for (Direction direction : Direction.values()) {
				if (hasBot(direction, direction.getThreat(coord, 1))) {
					System.out.println("1 step threat at " + coord);
					return true;
				} else if (hasBot(direction, direction.getThreat(coord, 2))) {
					System.out.println("2 step threat at " + coord);
					return true;
				} else if (hasBot(direction, direction.getThreat(coord, 3))) {
					System.out.println("3 step threat at " + coord);
					return true;
				}
			}
			System.out.println("No threat at " + coord);
			return false;

		}

		public Direction getDirectionWithMostTargets() {
			Direction maxDirection;
			int maxBots;

			if (Direction.N.canMove(this.myCoords, this)) {
				maxDirection = Direction.N;
				maxBots = northBots;
			} else {
				maxDirection = Direction.S;
				maxBots = southBots;
			}

			if (eastBots > maxBots && Direction.E.canMove(this.myCoords, this)) {
				maxDirection = Direction.E;
				maxBots = eastBots;
			}
			if (westBots > maxBots && Direction.W.canMove(this.myCoords, this)) {
				maxDirection = Direction.W;
				//maxBots = eastBots;
			}
			System.out.println("Most bots (" + maxBots + ") in direction " + maxDirection);

			return maxDirection;
		}

		public boolean isDirectionPossible(Coord coords, Direction direction) {
			return direction.canMove(coords, this);
		}
	}

	public String move(ArenaUpdate arenaUpdate) {

		String selfUrl = arenaUpdate._links.self.href;

		// delete self from board before creating it
		PlayerState selfState = arenaUpdate.arena.state.remove(selfUrl);

		Board board = new Board(arenaUpdate.arena, selfState);

		Coord selfCoord = new Coord(selfState.x, selfState.y);
		Direction selfDirection = Direction.valueOf(selfState.direction);

		if (selfState.wasHit) {
			// get out of line of fire
			System.out.println("Being attacked, moving randomly");
			if (isForwardPossible(selfState, board)) {
				return "F";
			} else {
				return random.nextInt() % 2 == 0 ? "R" : "L";
			}
		} else if (canThrowLeaf(selfCoord, selfDirection, board)) {
			System.out.println("Can throw leaf from " + selfCoord + " facing " + selfDirection);
			return "T";
		}
		//} else if () {
		// can turn to target

		/*else if (mustRunAway(selfCoord, board)) {
			// TODO: optimize R vs L
			return isForwardPossible(selfDirection, selfCoord, board) ? "F" : "R";
		}*/
	 else {
			Direction dir = getDirectionWithMostTargets(board);

			System.out.println("Going in direction with most targets: " + dir);
			if (board.hasThreatBot(dir.getStep(selfCoord, 1))) {
				dir = Direction.values()[(dir.ordinal() + 1) % 4];
				System.out.println("Best path under attack; picking next clockwise: " + dir);
			}

			if (selfDirection == dir) {
				return "F";
			} else {
				// todo: better logic for not turning 3 times
				return "R";
			}
		}
		/*
		System.out.println(arena);
		String[] commands = new String[] {"F", "R", "L", "T"};
		int i = new Random().nextInt(4);
		return commands[i];

		 */
	}



	boolean isForwardPossible(PlayerState state, Board board) {
		return isForwardPossible(Direction.valueOf(state.direction), new Coord(state.x, state.y),
				board);
	}

	public boolean isForwardPossible(Direction direction, Coord coords, Board board) {

		return board.isDirectionPossible(coords, direction);
	}

	public boolean canThrowLeaf(Coord currentCoord, Direction direction, Board board) {

		return board.hasBot(direction.getStep(currentCoord, 1)) ||
				board.hasBot(direction.getStep(currentCoord,2)) ||
				board.hasBot(direction.getStep(currentCoord,3));
	}

	public boolean mustRunAway(Coord currentCoord, Board board) {
		return board.hasThreatBot(currentCoord);
	}

	static Direction getDirectionWithMostTargets(Board board) {
		return board.getDirectionWithMostTargets();
	}
}
