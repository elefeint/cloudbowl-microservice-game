package hello;

import java.util.Objects;

class Coord {
	int x;
	int y;

	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Coord coord = (Coord) o;
		return x == coord.x &&
				y == coord.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
