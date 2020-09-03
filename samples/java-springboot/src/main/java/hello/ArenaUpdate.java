package hello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Self {
	public String href;
}

class Links {
	public Self self;
}

class PlayerState {
	public PlayerState() {

	}
	public PlayerState(int x, int y, String direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public PlayerState(String playerName, int x, int y, String direction) {
		this(x, y, direction);
		this.playerName = playerName;
	}

	public String playerName;
	public Integer x;
	public Integer y;
	public String direction;
	public Boolean wasHit;
	public Integer score;
}

class Arena {

	public Arena() {}
	public Arena(int width, int height, PlayerState... players) {
		this.dims = Arrays.asList(width, height);
		this.state = new HashMap<>();
		for (PlayerState ps : players) {
			this.state.put(ps.playerName, ps);
		}
	}

	public List<Integer> dims;
	public Map<String, PlayerState> state;
}

class ArenaUpdate {
	public Links _links;
	public Arena arena;
}