package hello;

public class BotStats {
	private Board board;
	private Coord selfCoord;

	int northBots, southBots, westBots, eastBots;

	// TODO: obvs this is terrible
	public BotStats(Board board, Coord selfCoord) {
		this.board = board;
		this.selfCoord = selfCoord;

		for (int y = 0; y < board.height; y++) {
			for (int x = 0; x < board.width; x++) {
				if (board.hasBot(new Coord(x, y))) {

					if (x >= selfCoord.x) {
						eastBots++;
					} else {
						westBots++;
					}

					if (y >= selfCoord.y) {
						southBots++;
					} else {
						northBots++;
					}

				}

			}
		}

		System.out.println("Bot counts: N=" + northBots + ", S=" + southBots
				+ ", W=" + westBots + ", E=" + eastBots);
	}

	public Direction getDirectionWithMostTargets() {
		Direction maxDirection;
		int maxBots;

		if (Direction.N.canMove(this.selfCoord, this.board)) {
			maxDirection = Direction.N;
			maxBots = northBots;
		} else {
			maxDirection = Direction.S;
			maxBots = southBots;
		}

		if (eastBots > maxBots && Direction.E.canMove(this.selfCoord, this.board)) {
			maxDirection = Direction.E;
			maxBots = eastBots;
		}
		if (westBots > maxBots && Direction.W.canMove(this.selfCoord, this.board)) {
			maxDirection = Direction.W;
			//maxBots = eastBots;
		}
		System.out.println("Most bots (" + maxBots + ") in direction " + maxDirection);

		return maxDirection;
	}
}
