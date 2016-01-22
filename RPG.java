import java.util.Scanner;

public class RPG {
	
	// Game constants
	public static final int VIEW_DISTANCE = 5;
	public static final int INVENTORY_WIDTH = 25;
	
	// data Tutorial
	public static final int TUTORIAL_MESSAGE = 0;
	public static final int TUTORIAL_ACTION = 1;
	public static final int TUTORIAL_DESCRIPTION = 2;
	public static final int TUTORIAL_DONE = 3;
	public static final String[][] TUTORIALS = {
		{ "Now let's see if you can walk.", "w", "walk forward", null },
		{ "Can you also step back?", "s", "walk backward", null },
		{ "Why not glance around?", "a", "turn left", null },
		{ "And also to the right.", "d", "turn right", null },
		{ "Look for help when you remember nothing.", "h", "open help", null },
		{ "Oops. The door is closed. Try open it.", "e", "interact", null },
		{ "There seems to be something inside...", "e", "interact", null },
		{ "Oh! You found something!", "i", "open inventory", null }
	};
	
	// data Item
	public static final int ITEM_NAME = 0;
	public static final int ITEM_TYPE = 1;
	public static final int ITEM_USE = 2;
	public static final int ITEM_UNEQUIP = 3;
	public static final int ITEM_SLOT = 4;
	public static final String[][] ITEMS = {
		// Name, type, use action, unequip action, slot
		// TODO add more items
		{ "Wooden Sword", "equipment", "$player.attack += 5", "$player.attack -= 5", "weapon" },
		{ "Iron Sword", "equipment", "$player.attack += 10", "$player.attack -= 10", "weapon" },
		{ "Sword of Chaos", "equipment", "$player.attack += 15", "$player.attack -= 15", "weapon" },
		{ "Magelight", "equipment", "$player.magic += 5", "$player.magic -= 5", "weapon" },
		{ "Plain Clothes", "equipment", "$player.defense += 3", "$player.defense -= 3", "clothing" },
		{ "Iron Clothing", "equipment", "$player.defense += 5", "$player.defense -= 5", "clothing" },
		{ "Armor of Legends", "equipment", "$player.defense += 8", "$player.defense -= 8", "clothing" },
		{ "Robe of Wonders", "equipment", "$player.magic += 3", "$player.magic -= 3", "clothing" },
		{ "Health Potion", "potion", "$player.health += 20", null, null },
		{ "Minor Poison", "poison", "$enemy.health -= 20", null, null },
		{ "Effective Poison", "poison", "$enemy.health -= 40", null, null },
		{ "Crystal Shard", "misc", null, null, null },
		{ "Skull", "misc", null, null, null }
	};
	
	// data Event
	public static final int EVENT_TRIGGER = 0;
	public static final int EVENT_ACTION = 1;
	public static final String[][] EVENTS = {
		// Trigger, action
		//{ "$player.health > 100", "$player.health = 100" }
	};
	
	// data Description
	public static final String[] DESCRIPTIONS = {
		null, // Initial / supply room

		// TODO add more description	
		"The room smells of dead fish. It looks ancient and torn.\n"
		+ "A strange creature stares at you. Expectingly.",

		"The room is all silent and still. Nothing happens, and nothing moves.\n"
		+ "Though you may wish that there is something here.",
		
		"A little girl about half your size crouches in the middle of the room.\n"
		+ "Her legs are clipped to the floor, and she looks at you desperately.",

		"There's something strange about this room - you can just sense it.\n"
		+ "You feel your spine freeze from the top to the bottom.",
		
		"A grumbling voice slips through the door.",
		
		"Water vapour fills the room.",
		
		"The ground trembles as sounds of cracking and stomping come out of the room.\n"
		+ "A giant monster stands in the middle. The exit is right behind it."
	};
	
	public static final String[] ENDINGS = {
		// Dying
		"You fall to the ground, bleeding. You see nothing and hear nothing.\n"
		+ "After no more than a few minutes, you will cease to exist.\n"
		+ "Your bones and flesh are left to rot in this deep, dark dungeon.\n"
		+ "GAME OVER",
		
		// No girl
		"With the crumbling sound of the monster, you step out of the dungeon.\n"
		+ "The air is fresh, the water is clear, and the sky is blue.\n"
		+ "Though, there's something that never faded away:\n"
		+ "She isn't here. She's gone. Forever.",
		
		"Taking her little white hand and carrying a smile on your face,\n"
		+ "you step out of the dreadful deep dungeon of darkness.\n"
		+ "The girl stares at you, and hold out her delicate arms.\n"
		+ "\"Thank you,\" she whispers, wrapping her arms around you."
	};
	
	/* ------------ Command Language support ------------ */
	
	/*
	 * An object is represented using two String[] (as a String[][]),
	 * one key (property name) array and one value array.
	 */
	
	// Parser data
	public static String[] tokens;
	public static int tokenIndex;
	
	// First dimension
	public static String[] objectNames = new String[32];
	// Second dimension
	public static final int OBJECT_KEYS = 0;
	public static final int OBJECT_VALUES = 1;
	// Global Objects
	public static String[][][] objects = new String[objectNames.length][2][];
	
	// Characters
	public static final String[] CHARACTER_PROPERTIES = {
		"Name",
		"Health",
		"Attack",
		"Defense",
		"Magic"
	};
	public static String[] player = {
		"Adventurer", "100", "5", "2", "5"
	};
	public static final String[] FOLLOWER = {
		"Aura", "50", "10", "2", "2"
	};
	public static String[] follower;
	public static final String[][] ENEMIES = {
		{ "Monster", "200", "10", "8", "5" }, // Boss
		{ "Enemy", "50", "5", "2", null },
		{ "Enemy", "100", "5", "5", null }
	};
	public static String[] enemy;

	// Inventory
	public static String[] inventory = new String[32];
	
	// Equipment slots
	public static final String[] SLOTS = {
		"Weapon",
		"Clothing"
	};
	public static String[] slots = new String[SLOTS.length];
	
	/* ------------ Map ------------ */
	
	public static final int SQUARE_TYPE = 0;
	public static final int SQUARE_BLOCKING = 1;
	public static final int SQUARE_VISIBLE = 2;
	public static final int SQUARE_TRANSPARENT = 3;
	public static final int SQUARE_STATE = 4;
	public static final int SQUARE_DATA = 5;
	public static final int SQUARE_MARK = 6;
	public static String[][][] map = new String[75][50][7];
	
	public static final int[][] DIRECTIONS = {
		{ 1, 0, 1, 1, 1, -1 },
		{ 0, 1, -1, 1, 1, 1 },
		{ -1, 0, -1, -1, -1, 1 },
		{ 0, -1, 1, -1, -1, -1 }
	};
	
	public static String[] front, left, right;

	public static int playerX = 2;
	public static int playerY = map[0].length / 2;
	public static int direction = 0;

	/* ------------ Miscellaneous ------------ */
	
	public static boolean endGame = false;
	
	public static boolean isCheatEnabled = false;

	public static Scanner in = new Scanner(System.in);
	
	/* ------------ Game loops ------------ */

	public static void main(String[] args) {
		// Game setup
		createObjects();
		generateMap();
		
		// Enter game
		createPlayer();
		tutorial(0, 5);
		prompt("Now start your adventure.");
		
		// Main game loop
		while (!endGame)
			perform(input("What are you going to do? "
					+ "[w, s, a, d, e, i, m, q, h]"));
	}
	
	public static void tutorial(int id) {
		String[] t = TUTORIALS[id];
		
		// If tutorial done, no need to do it again
		if (t[TUTORIAL_DONE] != null)
			return;
		
		// Mark this tutorial as done
		t[TUTORIAL_DONE] = "true";
		
		// Prompt message
		prompt(t[TUTORIAL_MESSAGE]);
		
		// Show input message ("Press X to X")
		String message = "Press " + t[TUTORIAL_ACTION] + " to "
			+ t[TUTORIAL_DESCRIPTION] + ": ";
		
		// Loop until user enters the action
		while (!input(message).equals(t[TUTORIAL_ACTION]))
			prompt("Nope. Try again.");
		
		// Do what the action does
		perform(t[TUTORIAL_ACTION]);
	}
	
	public static void tutorial(int start, int count) {
		for (int i = start; i < start + count; i++)
			tutorial(i);
	}
	
	public static boolean combat(int enemyID) {
		int health = indexOf(CHARACTER_PROPERTIES, "health");
		int attack = indexOf(CHARACTER_PROPERTIES, "attack");
		int defense = indexOf(CHARACTER_PROPERTIES, "defense");
		int magic = indexOf(CHARACTER_PROPERTIES, "magic");
		
		// Set enemy object
		getObject("enemy")[OBJECT_VALUES] = enemy = ENEMIES[enemyID].clone();
		
		String message = "Combat begins!\n"
				+ "Press a to attack.\n"
				+ "Press i to open inventory.\n"
				+ "Press q to retreat.";
		String choiceMessage = "And you, are going to? [a, i, q";
		if (follower != null) {
			message += "\nPress s to heal follower.\n"
					+ "Press d to taunt (protect follower).";
			choiceMessage += ", s, d";
		}
		choiceMessage += "]";
		prompt(message);
		
		// TODO add attack method: (attacker, defender, type) => boolean
		while (true) {
			// Player attacks
			String choice = input(choiceMessage);
			switch (choice) {
			case "a":
				int damage = asInt(player[attack]) - asInt(enemy[defense]);
				if (damage > 0) {
					int newHealth = asInt(enemy[health]) - damage;
					if (newHealth < 0)
						newHealth = 0;
					prompt("You caused " + damage + " points of damage.");
					if (newHealth <= 0) {
						enemy = null;
						getObject("enemy")[OBJECT_VALUES] = null;
						return true;
					}
					enemy[health] = String.valueOf(newHealth);
				} else {
					prompt("You cannot harm him.");
				}
				break;
			case "s":
				if (follower == null) {
					prompt("You don't currently have a follower!");
					continue;
				}
				int heal = asInt(player[magic]);
				int newHealth = asInt(follower[health]) + heal;
				prompt("You healed the girl " + heal + " points of health.");
				follower[health] = String.valueOf(newHealth);
				break;
			case "d":
				if (follower == null) {
					prompt("You don't currently have a follower!");
					continue;
				}
				prompt("You blocked for the girl.");
				break;
			case "i":
				if (!inventory())
					continue;
				break;
			case "q":
				if (confirm("The enemy will recover once you retreat.\n"
						+ "Are you sure you want to leave?")) {
					getObject("enemy")[OBJECT_VALUES] = enemy = null;
					return false;
				} else
					continue;
			default:
				invalidInput(choice);
				continue;
			}
			
			String[] target = player;
			if (follower != null) {
				// Follower attacks
				int damage = asInt(follower[attack]) - asInt(enemy[defense]);
				if (damage > 0) {
					int newHealth = asInt(enemy[health]) - damage;
					if (newHealth < 0)
						newHealth = 0;
					prompt("The girl caused " + damage + " points of damage.");
					if (newHealth <= 0) {
						getObject("enemy")[OBJECT_VALUES] = enemy = null;
						return true;
					}
					enemy[health] = String.valueOf(newHealth);
				} else {
					prompt("The girl is too weak to attack him.");
				}
				
				// Only when player not blocking for her
				// Half chance to attack the girl
				if (!choice.equals("d") && Math.random() > 0.5)
					target = follower;
			}
			
			// Enemy attacks
			int damage = asInt(enemy[attack]) - asInt(target[defense]);
			if (damage > 0) {
				int newHealth = asInt(target[health]) - damage;
				if (newHealth < 0)
					newHealth = 0;
				
				// Compose message
				prompt("The enemy caused " + damage + " points of damage to "
						+ (target == player ? "you" : "the girl") + ".");
				
				if (newHealth <= 0) {
					if (target == player) {
						// Player killed
						end(0);
						enemy = null;
						return false;
					} else {
						// Girl killed
						prompt("\"Ah!!!\" she screamed, falling to the ground.");
						follower = null;
					}
				}
				target[health] = String.valueOf(newHealth);
			} else {
				prompt("The enemy tried to attack, but caused no damage.");
			}
			
			// Display status
			message = "{$player.name}: {$player.health} ";
			if (follower != null)
				message += "{$follower.name}: {$follower.health} ";
			message += "{$enemy.name}: {$enemy.health}";
			prompt(message);
		}
	}
	
	public static boolean attack(String attacker, String defender, int type) {
		// TODO finish attack logic
		String damage;
		switch (type) {
		case 0:
			damage = "$" + attacker + ".attack - $" + defender + ".defense";
			break;
		default:
			error("Invalid attack type");
			damage = "0";
			break;
		}
		return asInt(execute("$" + defender + ".health -= " + damage)) <= 0;
	}
	
	public static void end(int ending) {
		prompt("------------------------------------------------------------");
		prompt(ENDINGS[ending]);
		prompt("------------------------------------------------------------");
		prompt("                       >  The  End  <                       ");
		endGame = true;
	}
	
	/* ------------ Main Method helpers ------------ */

	public static void createObjects() {
		String[][] playerObj = {
			CHARACTER_PROPERTIES,
			player
		};
		setObject("player", playerObj);
		
		String[] invKeys = new String[inventory.length];
		for (int i = 0; i < invKeys.length; i++) {
			invKeys[i] = "Item " + i;
		}
		String[][] invObj = {
			invKeys,
			inventory
		};
		setObject("inventory", invObj);
		
		String[][] slotsObj = {
			SLOTS,
			slots
		};
		setObject("slots", slotsObj);
		
		String[][] enemyObj = {
			CHARACTER_PROPERTIES,
			enemy
		};
		setObject("enemy", enemyObj);
		
		String[][] followerObj = {
			CHARACTER_PROPERTIES,
			follower
		};
		setObject("follower", followerObj);
	}
	
	public static void generateMap() {
		// Fill map with wall
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				map[i][j][SQUARE_TYPE] = "wall";
			}
		}
		
		// Generate rooms and paths
		// Horizontal position (starts from middle)
		int y = map[0].length / 2;
		int nextRoomDis = 0;
		int roomNum = 0;
		for (int x = 1; x < map.length - 5; x++) {
			if (nextRoomDis == 0) {
				// Add door
				map[x][y][SQUARE_TYPE] = "door";
				if (roomNum < DESCRIPTIONS.length)
					map[x][y][SQUARE_DATA] = String.valueOf(roomNum);
				
				// Generate room
				x += 2;
				generateRoom(x, y, roomNum);
				
				// Generate room sideways
				if (roomNum != 0 && Math.random() > 0.8) {
					int dir = Math.random() > 0.5 ? 1 : -1;
					
					// Add door
					map[x][y + 2 * dir][SQUARE_TYPE] = "door";
					
					int i = x;
					int dis = (int) (Math.random() * 3 + 3) * dir; // 3 - 5
					int offset = 3 * dir;
					
					for (int j = y + offset; j != y + dis + offset; j += dir) {
						map[i][j][SQUARE_TYPE] = null;
						if (Math.random() > 0.5) {
							i += (int) (Math.random() * 3) - 1; // -1 to 1
							map[i][j][SQUARE_TYPE] = null;
						}
					}
					map[i][y + dis + offset][SQUARE_TYPE] = "door";
					generateRoom(i, y + dis + offset + 2 * dir, 0);
				}
				
				roomNum++;
				x += 2;
				
				// Add door
				if (isInMap(x, y))
					map[x][y][SQUARE_TYPE] = "door";
				nextRoomDis = (int) (Math.random() * 5 + 1); // 1 - 5
			}
			else {
				// Generate path
				map[x][y][SQUARE_TYPE] = null;
				if (Math.random() > 0.5) {
					// Move sideways
					y += (int) (Math.random() * 3) - 1; // -1 to 1
					map[x][y][SQUARE_TYPE] = null;
				}
				nextRoomDis--;
			}
		}
		
		// Prevent player from opening initial door
		map[1][map[0].length / 2][SQUARE_STATE] = "locked";
		
		// Initialize each square on map
		for (String[][] line : map) {
			for (String[] square : line) {
				if (square[SQUARE_TYPE] == null)
					continue;
				square[SQUARE_BLOCKING] = "true";
				square[SQUARE_VISIBLE] = "true";
				square[SQUARE_TRANSPARENT] = "false";
	
				initializeSquare(square);
			}
		}
	}
	
	public static void generateRoom(int x, int y, int roomNum) {
		for (int i = x - 1; i < x + 2; i++) {
			for (int j = y - 1; j < y + 2; j++) {
				if (isInMap(i, j))
					map[i][j][SQUARE_TYPE] = null;
			}
		}

		// Spawn random item(s)
		int count = (int) (Math.random() * 3); // 0, 1, 2
		for (int c = 0; c < count; c++) {
			int i = x - 1 + (int) (Math.random() * 3);
			int j = y - 1 + (int) (Math.random() * 3);
			if (isInMap(i, j)) {
				map[i][j][SQUARE_TYPE] = "chest";
				map[i][j][SQUARE_DATA] = String.valueOf(
						(int) (Math.random() * ITEMS.length));
			}
		}
		
		// Spawn room-specific stuff
		String[] center = map[x][y];
		switch (roomNum) {
		case 1:
			center[SQUARE_TYPE] = "enemy";
			center[SQUARE_DATA] = "1";
			break;
		case 3:
			center[SQUARE_TYPE] = "girl";
			break;
		case 4:
			center[SQUARE_TYPE] = "trap";
			break;
		case 5:
			center[SQUARE_TYPE] = "enemy";
			center[SQUARE_DATA] = "2";
			break;
		case 7:
			center[SQUARE_TYPE] = "enemy";
			center[SQUARE_DATA] = "0";
			break;
		}
	}
	
	public static void createPlayer() {
		prompt("\"Where am I?\" You wake up with a buzzing head.\n"
				+ "\"Aren't I supposed to be on a mission to rescue a little girl?\"");
		String playerName = input("What's your name, adventurer?");
		if (!playerName.isEmpty())
			setProperty("player", "name", playerName);
		prompt("Oh yeah! {$player.name}, the great adventurer!\n"
				+ "Unfortunately, you find yourself trapped inside a deep dark dungeon.\n"
				+ "There's nothing to help you beside yourself... and probably the girl.\n"
				+ "But first, you must find her before she could help you.");
		switch (input("Now, you like your adventure to be:\n"
				+ "1: Easy, 2: Normal, 3: Hard")) {
		case "1":
			setProperty("player", "health", "200");
			prompt("Enjoying the easy swift, huh?");
			break;
		case "2":
			// No setProperty since it's default value
			prompt("Everything's just the same. Fair for everyone.");
			break;
		case "3":
			setProperty("player", "health", "80");
			prompt("You are one of the GREATEST adventurers in the world!");
			break;
		default:
			setProperty("player", "health", "50");
			prompt("Oops. You didn't choose.\n"
					+ "You're going to experience the hardest one of all.");
			break;
		}
	}

	public static void perform(String action) {
		switch (action) {
		case "w":
			if (move(1))
				peek();
			break;
		case "s":
			if (move(-1))
				peek();
			break;
		case "a":
			direction = (direction + 1) % 4;
			updatePositionSquares();
			peek();
			break;
		case "d":
			direction = (direction + 3) % 4;
			updatePositionSquares();
			peek();
			break;
		case "e":
			interact();
			break;
		case "m":
			mark();
			break;
		case "i":
			inventory();
			break;
		case "q":
			if (confirm("You really want to abort this mission?\n"
					+ "That's not really a brave act!"))
				endGame = true;
			break;
		case "h":
			prompt("You can use these keys in the game:\n"
					+ "w: walk forward\n"
					+ "s: walk backward\n"
					+ "a: turn left\n"
					+ "d: turn right\n"
					+ "e: interact\n"
					+ "i: inventory\n"
					+ "m: mark\n"
					+ "q: quit\n"
					+ "h: open help");
			break;
		default:
			invalidInput(action);
			break;
		}
		checkEvents();
	}
	
	public static void invalidInput(String action) {
		if (action.equals("jesus")) {
			if (isCheatEnabled = !isCheatEnabled) {
				prompt("What do you mean you don't understand?...");
			} else {
				prompt("Fine. Let's do this again.");
			}
			return;
		}
		
		if (!isCheatEnabled) {
			prompt("What are you doing???");
		} else {
			prompt(execute(action));
		}
	}
	
	/* ------------ Action methods ------------ */

	public static boolean move(int distance) {
		int newX = playerX + DIRECTIONS[direction][0] * distance;
		int newY = playerY + DIRECTIONS[direction][1] * distance;
		
		// Find target square
		String[] square = map[newX][newY];

		// Collision
		if (square[SQUARE_TYPE] != null) {
			// If blocked, don't move
			if (isTrue(square[SQUARE_BLOCKING])) {
				prompt("You are blocked by the " + square[SQUARE_TYPE] + ".");
				return false;
			}
			
			// Trigger collision event
			collideSquare(square);
		}
		
		// Move to new position
		playerX = newX;
		playerY = newY;
		updatePositionSquares();
		
		return true;
	}
	
	public static void peek() {
		final int[] dir = DIRECTIONS[direction];

		// Front
		boolean somethingAhead = false;
		for (int dis = 1; dis <= VIEW_DISTANCE; dis++) {
			String[] square = map[playerX + dir[0] * dis][playerY + dir[1] * dis];
			if (square[SQUARE_TYPE] != null && isTrue(square[SQUARE_VISIBLE])) {
				somethingAhead = true;
				
				String type = square[SQUARE_TYPE];
				if (dis > 1) {
					prompt("You see a " + type + " " + dis + "m ahead. ");
				} else {
					String message = "There's a " + type + " right in front of you. ";

					// Display state and mark
					if (square[SQUARE_STATE] != null)
						message += "It's " + square[SQUARE_STATE] + ". ";
					if (square[SQUARE_MARK] != null)
						message += "\nYou have written: \"" + square[SQUARE_MARK] + "\".";
					prompt(message);
				}
				if (!isTrue(square[SQUARE_TRANSPARENT]))
					break;
			}
		}
		if (!somethingAhead)
			prompt("It's too far to see what's ahead.");
		
		if (left[SQUARE_TYPE] != null && isTrue(left[SQUARE_VISIBLE])) {
			prompt("To your front left, there's a " + left[SQUARE_TYPE] + ".");
		}

		if (right[SQUARE_TYPE] != null && isTrue(right[SQUARE_VISIBLE])) {
			prompt("To your front right, there's a " + right[SQUARE_TYPE] + ".");
		}

		// Trigger peek event
		if (front[SQUARE_TYPE] != null && isTrue(front[SQUARE_VISIBLE]))
			peekSquare(front);
	}
	
	public static boolean interact() {
		if (front[SQUARE_TYPE] == null) {
			prompt("There's nothing to interact in front of you.");
			return false;
		}

		return interactSquare(front);
	}
	
	public static void mark() {
		if (front[SQUARE_TYPE] == null) {
			prompt("There's nothing you can write on.");
			return;
		}
		String message = input("What do you want to write?");
		if (front[SQUARE_MARK] == null)
			front[SQUARE_MARK] = "";
		front[SQUARE_MARK] += message;
	}
	
	public static void checkEvents() {
		for (String[] t : EVENTS) {
			String result = execute(t[EVENT_TRIGGER]);
			if (!isTrue(result))
				continue;
			execute(t[EVENT_ACTION]);
		}
	}
	
	/* ------------ Square Event methods ------------ */
	
	public static void initializeSquare(String[] s) {
		switch (s[SQUARE_TYPE]) {
		case "door":
			if (s[SQUARE_STATE] != null) // Locked door
				break;
			s[SQUARE_STATE] = "closed";
			break;
		case "chest":
			s[SQUARE_BLOCKING] = "false";
			s[SQUARE_TRANSPARENT] = "true";
			break;
		case "enemy":
			s[SQUARE_TRANSPARENT] = "false";
			break;
		case "trap":
			s[SQUARE_BLOCKING] = "false";
			s[SQUARE_VISIBLE] = "false";
			break;
		}
	}
	
	public static void collideSquare(String[] s) {
		switch (s[SQUARE_TYPE]) {
		case "trap":
			prompt("You stepped on a trap, which caused 20 points of damage.");
			int health = indexOf(CHARACTER_PROPERTIES, "health");
			int h = asInt(player[health]) - 20;
			player[health] = String.valueOf(h);
			if (h < 0) {
				end(0);
			}
			prompt("Your health is now {$player.health}.");
			break;
		}
	}
	
	public static void peekSquare(String[] s) {
		switch (s[SQUARE_TYPE]) {
		case "door":
			tutorial(5);
			break;
		case "chest":
			tutorial(6, 2); // E, I
			break;
		case "enemy":
			if ("dead".equals(s[SQUARE_STATE]))
				break;
			// Enter combat mode
			int enemyID = asInt(s[SQUARE_DATA]);
			if (enemyID != 0) {
				// Ordinary enemy
				prompt("The enemy sees you. He raises his weapon.");
				if (combat(enemyID)) {
					prompt("Victory!");
					s[SQUARE_STATE] = "dead";
					s[SQUARE_BLOCKING] = "false";
				}
			} else {
				// Boss fight
				prompt("The monster stares at you. Greedily.\n"
						+ "\"{$player.name}, I've been waiting for you,\" it growls.");
				if (combat(enemyID)) {
					if (follower != null)
						end(2);
					else
						end(1);
				}
			}
			break;
		case "girl":
			prompt("\"Please, help me!\" she stares with a pair of watery eyes.");
			if (confirm("Release her?")
					|| confirm("\"Please? I'm all here alone,\" begs the girl.\n"
							+ "Break the chains?")) {
				getObject("follower")[OBJECT_VALUES] = follower = FOLLOWER.clone();
				s[SQUARE_TYPE] = null;
				
				prompt("\"Yeah!\" she hurrahs merrily. \"What's your name, adventurer?\"\n"
						+ "\"{$player.name},\" you reply. \"Now let's hurry.\"\n"
						+ "\"Thanks, {$player.name},\" she says. \"I'm {$follower.name}, by the way.\"");
			} else {
				prompt("\"I guess I'm too much of a trouble after all,\" she sighs.");
			}
			break;
		}
	}
	
	public static boolean interactSquare(String[] s) {
		switch (s[SQUARE_TYPE]) {
		case "door":
			switch (s[SQUARE_STATE]) {
			case "closed":
				s[SQUARE_STATE] = "open";
				s[SQUARE_BLOCKING] = "false";
				s[SQUARE_TRANSPARENT] = "true";
				prompt("You opened the door.");
				if (s[SQUARE_DATA] != null) {
					// Display room description
					int id = asInt(s[SQUARE_DATA]);
					prompt(DESCRIPTIONS[id]);
					
					// Remove data to prevent description again
					s[SQUARE_DATA] = null;
				}
				break;
			case "open":
				s[SQUARE_STATE] = "closed";
				s[SQUARE_BLOCKING] = "true";
				s[SQUARE_TRANSPARENT] = "false";
				prompt("You closed the door.");
				break;
			case "locked":
				prompt("The door is locked.");
				break;
			}
			break;
		case "chest":
			if ("empty".equals(s[SQUARE_STATE])) {
				prompt("There's nothing inside.");
				break;
			}
			
			String name = ITEMS[asInt(s[SQUARE_DATA])][ITEM_NAME];
			prompt("You found " + name + " in the chest.");
			addToList(inventory, s[SQUARE_DATA]);
			s[SQUARE_STATE] = "empty";
			break;
		default:
			prompt("The " + s[SQUARE_TYPE] + " doesn't respond.");
			return false;
		}
		return true;
	}
	
	/* ------------ Inventory methods ------------ */
	
	public static boolean inventory() {
		String display = displayInventory(slots, "Equipment", 'a') + "\n\n"
			+ displayInventory(inventory, "Inventory", '0')
			+ "\nEnter a letter or digit to use / unequip item. Nothing to cancel.";
		
		String choice = input(display);
		
		if (choice.length() == 0)
			return false;
		
		if (choice.length() > 1) {
			prompt("Enter only one letter or digit instead.");
			return false;
		}
		
		char c = choice.charAt(0);
		boolean result;
		if (Character.isDigit(c))
			result = useInventoryItem(c - '0');
		else if (Character.isLetter(c))
			result = unequipItem(c - 'a');
		else {
			prompt("Enter a letter or digit instead.");
			return false;
		}
		
		if (!result) {
			prompt("This item does not exist.");
		}
		return result;
	}
	
	public static String displayInventory(String[] inv, String title, char start) {
		String output = "";
		
		String titleLine = "= " + title + " =";
		while (titleLine.length() < INVENTORY_WIDTH)
			titleLine += "=";
		output += " " + titleLine + "\n";
		
		for (int i = 0; i < inv.length; i++) {
			String item = inv[i];
			if (item == null)
				continue;
			int id = asInt(item);
			String itemLine = " | " + (char) (start + i) + ". " + ITEMS[id][ITEM_NAME];
			while (itemLine.length() < titleLine.length())
				itemLine += " ";
			output += itemLine + "|\n";
		}
		output += " ";
		for (int i = 0; i < titleLine.length(); i++)
			output += "=";
		return output;
	}
	
	public static boolean useInventoryItem(int inventoryID) {
		String id = inventory[inventoryID];
		if (id == null) {
			// Item does not exist
			return false;
		}
		int itemID = asInt(id);
		String[] item = ITEMS[itemID];
		
		switch (item[ITEM_TYPE]) {
		case "equipment":
			int slotIndex = indexOf(SLOTS, item[ITEM_SLOT]);
			if (slots[slotIndex] != null) {
				// Unequip first
				unequipItem(slotIndex);
			}
			// Remove from inventory
			inventory[inventoryID] = null;
			// Execute use command
			execute(item[ITEM_USE]);
			// Add to slots
			slots[slotIndex] = id;
			
			prompt(item[ITEM_NAME] + " equipped.");
			break;
		case "poison":
			if (enemy == null) {
				prompt("There's no enemy to harm right now.");
				break;
			}
		case "potion":
			execute(item[ITEM_USE]);
			inventory[inventoryID] = null;

			prompt(item[ITEM_NAME] + " used.");
			break;
		case "misc":
			prompt("This item cannot be used.");
			break;
		}
		return true;
	}
	
	public static boolean unequipItem(int slotID) {
		// Check if valid slot id
		if (slotID < 0 || slotID >= slots.length)
			return false;
		
		// Check if there is an item equipped
		String id = slots[slotID];
		if (id == null)
			return false;
		
		int itemID = asInt(id);
		String[] item = ITEMS[itemID];
		// Execute unequip command
		execute(item[ITEM_UNEQUIP]);
		// Add to inventory
		addToList(inventory, id);
		// Remove from slot
		slots[slotID] = null;
		
		prompt(item[ITEM_NAME] + " unequipped.");
		return true;
	}
	
	/* ------------ Command Language methods ------------ */
	
	public static String execute(String commands) {
		// Separate each statement
		String[] cmds = commands.split(";", 0);
		
		String result = null;
		for (String command : cmds) {
			command = command.trim();
			if (command.isEmpty())
				continue;
			tokens = command.split(" ");
			tokenIndex = 0;
			result = statement();
		}
		return result;
	}
	
	public static String statement() {
		switch (tokens[tokenIndex]) {
		case "if": {
			tokenIndex++;
			String condition = assignment();
			if (isTrue(condition))
				return statement();
			else
				return null;
		}
		default:
			return assignment();
		}
	}
	
	public static String assignment() {
		if (tokenIndex == tokens.length - 1 || tokens[tokenIndex + 1].indexOf('=') < 0
				|| tokens[tokenIndex + 1].indexOf("==") >= 0)
			return comparison();
		
		if (tokens[tokenIndex].charAt(0) != '$') {
			error("Assignment requires object as operand");
			return null;
		}
		
		String target = null;
		String property = null;
		
		int dotIndex = tokens[tokenIndex].indexOf('.');
		if (dotIndex < 0) {
			// No dot
			target = tokens[tokenIndex].substring(1);
		} else {
			// Remove the $ sign
			target = tokens[tokenIndex].substring(1, dotIndex);
			property = tokens[tokenIndex].substring(dotIndex + 1);
		}
		
		// Search for object id
		String[][] object = getObject(target);
		// Check for non-existing object
		if (object == null) {
			error("Object " + target + " does not exist.");
			return null;
		}
		
		// Search for object property
		int propertyID = property == null
				? -1 : indexOf(object[OBJECT_KEYS], property);
		
		String[] values = object[OBJECT_VALUES];
		
		tokenIndex++;
		String op = tokens[tokenIndex];
		
		tokenIndex++;
		String data = comparison();
		
		switch (op) {
		case "=":
			return values[propertyID] = data;
		case "+=":
			if (property != null)
			{
				int value = asInt(values[propertyID]);
				value += asInt(data);
				return values[propertyID] = String.valueOf(value);
			} else {
				addToList(object[OBJECT_VALUES], data);
				return data;
			}
		case "-=":
			if (property != null) {
				int value = asInt(values[propertyID]);
				value -= asInt(data);
				return values[propertyID] = String.valueOf(value);
			} else {
				removeFromList(object[OBJECT_VALUES], data);
				return data;
			}
		default:
			error("Operator " + op + " does not exist.");
			return null;
		}
	}
	
	public static String comparison() {
		String v1 = polynomial();
	
		if (tokenIndex == tokens.length)
			return v1;
		
		switch (tokens[tokenIndex]) {
		case "==":
			tokenIndex++;
			boolean equal = v1 != null && v1.equals(assignment());
			return String.valueOf(equal);
		case ">":
			tokenIndex++;
			boolean greater = asInt(v1) > asInt(assignment());
			return String.valueOf(greater);
		case "<":
			tokenIndex++;
			boolean smaller = asInt(v1) < asInt(assignment());
			return String.valueOf(smaller);
		default:
			return v1;
		}
	}

	public static String polynomial() {
		String value = term();
		
		while (tokenIndex < tokens.length) {
			switch (tokens[tokenIndex]) {
			case "+":
				tokenIndex++;
				value = String.valueOf(asInt(value) + asInt(term()));
				break;
			case "-":
				tokenIndex++;
				value = String.valueOf(asInt(value) - asInt(term()));
				break;
			default:
				return value;
			}
		}
		return value;
	}
	
	public static String term() {
		String value = factor();
		
		while (tokenIndex < tokens.length) {
			switch (tokens[tokenIndex]) {
			case "*":
				tokenIndex++;
				value = String.valueOf(asInt(value) * asInt(factor()));
				break;
			case "/":
				tokenIndex++;
				value = String.valueOf(asInt(value) / asInt(factor()));
				break;
			default:
				return value;
			}
		}
		return value;
	}
	
	public static String factor() {
		String token = tokens[tokenIndex];
		tokenIndex++;
		
		if (token.charAt(0) != '$')
			return token;

		String target = null;
		String property = null;
		
		int dotIndex = token.indexOf('.');
		if (dotIndex < 0) {
			// No dot
			target = token.substring(1);
			String[][] object = getObject(target);
			
			if (object == null) {
				return null;
			}

			String[] keys = object[OBJECT_KEYS];
			String[] values = object[OBJECT_VALUES];

			// Print every properties
			String message = "Object " + target;
			for (int j = 0; j < keys.length; j++) {
				message += "\n  " + keys[j] + " = " + values[j];
			}
			return message;
		} else {
			// Remove the $ sign
			target = token.substring(1, dotIndex);
			property = token.substring(dotIndex + 1);
			
			String[][] obj = getObject(target);
			return getProperty(obj, property);
		}
		
	}
	
	public static boolean isTrue(String value) {
		return value != null && !"0".equals(value) && !"false".equalsIgnoreCase(value);
	}
	
	// Safer method to use instead of Integer.parseInt()
	// Since Exceptions cannot be used, we make one without instead
	public static int asInt(String s) {
		int value = 0;
		int negative = 1;
		if (s.charAt(0) == '-') {
			negative = -1;
			s = s.substring(1);
		}
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (!Character.isDigit(ch))
				return 0;
			value *= 10;
			value += (ch - '0');
		}
		return negative * value;
	}
	
	public static void addToList(String[] list, String data) {
		for (int i = 0; i < list.length; i++) {
			if (list[i] == null) {
				list[i] = data;
				break;
			}
		}
	}
	
	public static void removeFromList(String[] list, String data) {
		for (int i = 0; i < list.length; i++) {
			if (data.equalsIgnoreCase(list[i])) {
				list[i] = null;
				break;
			}
		}
	}
	
	public static String[][] getObject(String name) {
		int i = indexOf(objectNames, name);
		if (i < 0)
			return null;
		return objects[i];
	}
	
	public static void setObject(String name, String[][] obj) {
		int i = indexOf(objectNames, name);
		if (i < 0) {
			// Find next empty name to place new object
			for (i = 0; i < objectNames.length; i++)
				if (objectNames[i] == null)
					break;
			// Set name
			objectNames[i] = name;
		}
		objects[i] = obj;
	}
	
	public static String getProperty(String[][] obj, String prop) {
		int i = indexOf(obj[OBJECT_KEYS], prop);
		if (i < 0)
			return null;
		return obj[OBJECT_VALUES][i];
	}
	
	public static String getProperty(String obj, String prop) {
		return getProperty(getObject(obj), prop);
	}

	public static void setProperty(String[][] obj, String prop, String val) {
		int i = indexOf(obj[OBJECT_KEYS], prop);
		if (i < 0)
			return;
		obj[OBJECT_VALUES][i] = val;
	}
	
	public static void setProperty(String obj, String prop, String val) {
		setProperty(getObject(obj), prop, val);
	}
	
	/* ------------ Helper methods ------------ */

	public static void updatePositionSquares() {
		int[] dir = DIRECTIONS[direction];
		front = map[playerX + dir[0]][playerY + dir[1]];
		left = map[playerX + dir[2]][playerY + dir[3]];
		right = map[playerX + dir[4]][playerY + dir[5]];
	}
	
	public static boolean isInMap(int x, int y) {
		return x >= 0 && x < map.length && y >= 0 && y < map[x].length;
	}

	public static int indexOf(String[] array, String value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null && array[i].equalsIgnoreCase(value))
				return i;
		}
		return -1;
	}
	
	public static int indexOf(String[][] array, int col, String value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null && array[i][col] != null
					&& array[i][col].equalsIgnoreCase(value))
				return i;
		}
		return -1;
	}
	
	/* ------------ I/O methods ------------ */
	
	public static void prompt(String message) {
		// Translate message
		int start, end;
		while ((start = message.indexOf('{')) >= 0) {
			end = message.indexOf('}', start + 1);
			String command = message.substring(start + 1, end);
			message = message.substring(0, start)
					+ execute(command)
					+ message.substring(end + 1);
		}
		
		System.out.println(message);
	}

	public static void error(String message) {
		System.err.println(message);
	}

	public static String input(String message) {
		prompt(message);
		return in.nextLine();
	}
	
	public static String input() {
		return in.nextLine();
	}
	
	public static boolean confirm(String message) {
		while (true) {
			prompt(message + " [y, n]");
			switch (in.nextLine().toLowerCase()) {
			case "y":
				return true;
			case "n":
				return false;
			default:
				continue;
			}
		}
	}
	
}
