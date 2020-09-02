package hello;

import java.util.List;
import java.util.Map;

class Self {
	public String href;
}

class Links {
	public Self self;
}

class PlayerState {
	public PlayerState(int x, int y, String direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public Integer x;
	public Integer y;
	public String direction;
	public Boolean wasHit;
	public Integer score;
}

class Arena {
	public List<Integer> dims;
	public Map<String, PlayerState> state;
}

class ArenaUpdate {
	public Links _links;
	public Arena arena;
}