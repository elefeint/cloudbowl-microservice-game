package hello;

public class Board {
	int width;
	int height;

	Direction[][] bots;

	public Board(Arena arena) {
		this.width = arena.dims.get(0);
		this.height = arena.dims.get(1);

		this.bots = new Direction[this.height][this.width];

		for (PlayerState state : arena.state.values()) {
			bots[state.y][state.x] = Direction.valueOf(state.direction);
		}

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

	public boolean isDirectionPossible(Coord coords, Direction direction) {
		return direction.canMove(coords, this);
	}
}


