package hello;

import java.util.Random;

public class Bot {

	private static Random random = new Random();

	public String move(ArenaUpdate arenaUpdate) {

		// delete self from board before creating it
		String selfUrl = arenaUpdate._links.self.href;
		PlayerState selfState = arenaUpdate.arena.state.remove(selfUrl);

		Board board = new Board(arenaUpdate.arena);
		Coord selfCoord = new Coord(selfState.x, selfState.y);
		Direction selfDirection = Direction.valueOf(selfState.direction);

		BotStats botStats = new BotStats(board, selfCoord);

		if (selfState.wasHit) {
			// get out of line of fire
			System.out.println("Being attacked, moving randomly");
			if (isForwardPossible(selfState, board)) {
				System.out.println(" forward is possible");
				return "F";
			} else {
				String rnd = random.nextInt(2) == 0 ? "R" : "L";
				System.out.println("  random turn: " + rnd);
				return rnd;
			}
		} else if (canThrowLeaf(selfCoord, selfDirection, board)) {
			System.out.println("Can throw leaf from " + selfCoord + " facing " + selfDirection);
			return "T";
		} else {
		//} else if () {
		// can turn to target

		/*else if (mustRunAway(selfCoord, board)) {
			// TODO: optimize R vs L
			return isForwardPossible(selfDirection, selfCoord, board) ? "F" : "R";
		}*/
			Direction turnRightDirection = Direction.values()[(selfDirection.ordinal() + 1) % 4];
			Direction turnLeftDirection = Direction.values()[(selfDirection.ordinal() + 3) % 4];
			Direction turnBackDirection = Direction.values()[(selfDirection.ordinal() + 2) % 4];
			System.out.println("Currently facing " + selfDirection + "; right = " + turnRightDirection +
					"; left = " + turnLeftDirection + "; back = " + turnBackDirection);

			// if we get here, those bots are not a threat because they point away.
			if (canThrowLeaf(selfCoord, turnRightDirection, board)) {
				System.out.println("Turning right to throw leaves");
				return "R";
			}
			if (canThrowLeaf(selfCoord, turnLeftDirection, board)) {
				System.out.println("Turning left to throw leaves");
				return "L";
			}
			// prioritize turning left before turning right to go back
			if (canThrowLeaf(selfCoord, turnBackDirection, board)) {
				System.out.println("Turning right prepare to throw leaves backwards");
				return "R";
			}

			// nothing interesting happening around me; go to a different part of the board
			Direction dir = botStats.getDirectionWithMostTargets();

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


}
