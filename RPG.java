import java.util.Scanner;

public class RPG {
	
	// type Tutorial
	public static final int TUTORIAL_COMMAND = 0;
	public static final int TUTORIAL_MESSAGE = 1;
	public static final int TUTORIAL_ACTION = 2;
	public static final int TUTORIAL_DESCRIPTION = 3;
	public static final String[][] TUTORIALS = {
		// Command, message, action key, action description
		{ "", "First, learn to walk.", "w", "walk forward" },
		{ "add sword", "Oh! You found a wooden sword!", "i", "open inventory" }
	};
	
	// type Item
	public static final int ITEM_NAME = 0;
	public static final int ITEM_TYPE = 1;
	public static final int ITEM_EFFECT = 2;
	public static final int ITEM_SLOT = 3;
	public static final String[][] ITEMS = {
		// Name, type, effect, slot
		{ "Sword", "equipment", "increase player attack 5", "weapon" },
		{ "T-shirt", "equipment", "increase player defense 3", "clothing" },
		{ "Health Potion", "potion", "increase player health 20", null }
	};
	
	// map Property
	public static final String[] PROPERTIES = {
		"Health",
		"Attack",
		"Defense"
	};
	public static int[] playerProperties = new int[PROPERTIES.length];
	public static int[] followerProperties;
	public static int[] enemyProperties;

	// type InventoryItem
	public static final int INVENTORY_ID = 0;
	public static final int INVENTORY_DATA = 1;
	public static int[][] playerInventory = new int[32][];
	
	// map Slot
	public static final String[] SLOTS = {
		"weapon",
		"clothing"
	};
	public static int[][] playerSlots = new int[SLOTS.length][];
	
	public static boolean isCheatEnabled = false;

	public static int[][][] map = new int[50][50][2];
	
	public static Scanner in = new Scanner(System.in);

	public static void main(String[] args) {
		initialize();
		
//		tutorial();

		// Main game loop
		String action;
		while (!(action = input("What to do next?")).equals("q")) {
			perform(action);
		}
		
		prompt("Quitting game");
	}
	
	public static void initialize() {
		
	}
	
	public static void tutorial() {
		for (String[] t : TUTORIALS) {
			// Prepare tutorial (execute command)
			execute(t[TUTORIAL_COMMAND]);
			// Prompt message
			prompt(t[TUTORIAL_MESSAGE]);
			// Show input message ("Press X to X")
			String message = "Press " + t[TUTORIAL_ACTION] + " to "
				+ t[TUTORIAL_DESCRIPTION] + ".";
			// Loop until user enters the key
			while (!input(message).equalsIgnoreCase(t[TUTORIAL_ACTION]))
				prompt("Nope. Try again.");
			// Do what the key does
			perform(t[TUTORIAL_ACTION]);
		}
		// End of tutorials
		prompt("Now start your adventure.");
	}
	
	public static void perform(String action) {
		switch (action) {
		case "w":
			move(0, 1);
			break;
		case "a":
			move(-1, 0);
			break;
		case "s":
			move(0, -1);
			break;
		case "d":
			move(1, 0);
			break;
		case "i":
			inventory();
			break;
		case "jesus":
			if (isCheatEnabled = !isCheatEnabled) {
				prompt("What do you mean you don't understand?...");
			} else {
				prompt("Okay. Fine.");
			}
			break;
		default:
			if (!isCheatEnabled) {
				prompt("What are you doing???");
				break;
			}
			execute(action);
		}
	}
	
	public static void execute(String commands) {
		String[] cmds = commands.split(",");
		for (String command : cmds) {
			String[] args = command.trim().split(" ");
			
			switch (args[0]) {
			case "set":
			{
				// E.g. set player health 10
				int[] properties = getProperties(args[1]);
				int i = indexOf(PROPERTIES, args[2]);
				properties[i] = Integer.parseInt(args[3]);
				break;
			}
			case "increase":
			{
				// E.g. increase player health 10
				int[] properties = getProperties(args[1]);
				int i = indexOf(PROPERTIES, args[2]);
				properties[i] += Integer.parseInt(args[3]);
				break;
			}
			case "decrease":
			{
				// E.g. decrease player health 10
				int[] properties = getProperties(args[1]);
				int i = indexOf(PROPERTIES, args[2]);
				properties[i] -= Integer.parseInt(args[3]);
				break;
			}
			case "add":
				int id = indexOf(ITEMS, ITEM_NAME, args[1]);
				if (id < 0) {
					error("Item " + args[1] + " not found.");
					break;
				}
				int[] item = { id, 0 };
				addToInventory(item);
				break;
			case "remove":
				removeFromInventory(indexOf(ITEMS, ITEM_NAME, args[1]));
				break;
			}
		}
	}

	public static void combat() {
		
	}
	
	public static void inventory() {
		String choice;
		while (true) {
			displayInventory(playerInventory, "Inventory");
			
			choice = input("Enter id to use/equip item, q to exit inventory:");
			if (choice.equals("q"))
				break;
			
			useInventoryItem(Integer.parseInt(choice));
			prompt("Item used.");
		}
		
		updatePlayerProperties();
	}
	
	public static void chest() {
		
	}
	
	public static void displayInventory(int[][] inventory, String title) {
		prompt(" = " + title + " ====== ");
		for (int i = 0; i < inventory.length; i++) {
			int[] item = inventory[i];
			if (item == null)
				continue;
			prompt(" | " + i + ". " + ITEMS[item[INVENTORY_ID]][ITEM_NAME]);
		}
		prompt(" ================== ");
	}
	
	public static void useInventoryItem(int id) {
		String[] item = ITEMS[playerInventory[id][INVENTORY_ID]];
		switch (item[ITEM_TYPE]) {
		case "equipment":
			int slot = indexOf(SLOTS, item[ITEM_SLOT]);
//			if (playerSlots[slot] != null) {
//				// Unequip item
//				addToInventory(playerSlots[slot]);
//			}
			// Equip item
			playerSlots[slot] = playerInventory[id];
//			removeFromInventory(id);
			break;
		case "potion":
			execute(item[ITEM_EFFECT]);
			break;
		case "misc":
			prompt("This item cannot be used.");
			break;
		}
	}
	
	public static void addToInventory(int[] item) {
		for (int i = 0; i < playerInventory.length; i++) {
			if (playerInventory[i] == null) {
				playerInventory[i] = item;
				break;
			}
		}
	}
	
	public static void removeFromInventory(int id) {
		// Remove from inventory
		for (int i = 0; i < playerInventory.length; i++) {
			if (playerInventory[i][INVENTORY_ID] == id) {
				playerInventory[i] = null;
				break;
			}
		}
		for (int i = 0; i < playerSlots.length; i++) {
			if (playerSlots[i][INVENTORY_ID] == id) {
				playerSlots[i] = null;
				break;
			}
		}
	}
	
	public static void updatePlayerProperties() {
		
	}
	
	public static int indexOf(String[] array, String value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equalsIgnoreCase(value))
				return i;
		}
		return -1;
	}
	
	public static int indexOf(String[][] array, int col, String value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i][col].equalsIgnoreCase(value))
				return i;
		}
		return -1;
	}
	
	public static int[] getProperties(String character) {
		switch (character) {
		case "player":
			return playerProperties;
		case "follower":
			return followerProperties;
		case "enemy":
			return enemyProperties;
		}
		return null;
	}
	
	public static void prompt(String message) {
		System.out.println(message);
	}

	public static void error(String message) {
		System.err.println(message);
	}

	public static String input(String message) {
		System.out.println(message);
		return in.nextLine();
	}
	
	public static void move(int dx, int dy) {
		
	}

}
