
/*Jason Zhang
 * Period 5
 * 10/30/2018
 * */
import java.util.*;

/**
 * This class is the main class of the "World of Zuul" application. "World of
 * Zuul" is a very simple, text based adventure game. Users can walk around some
 * scenery. That's all. It should really be extended to make it more
 * interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * method.
 * 
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates and executes the
 * commands that the parser returns.
 * 
 * @author Michael Kolling and David J. Barnes
 * @version 1.0 (February 2002)
 */

class Game {
	private Parser parser;
	private Room currentRoom;
	Room outside, pub, lab, office, science, math, history, gym, cafe, library, mh1, mh2, mh3, mh4, ah1, ah2, bh1, bh2,
			counsellor, attendance, bathroom;
	ArrayList<Item> inventory = new ArrayList<Item>();
	//all the items
	ArrayList<String> itemsNeeded = new ArrayList<String>() {
		{
			add("Map");
			add("Juice");
			add("Banana");
			add("Balls");
			add("Computer");
			
		}
	};

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		createRooms();
		parser = new Parser();
	}

	public static void main(String[] args) {
		Game myGame = new Game();
		myGame.play();
	}

	/**
	 * Create all the rooms and link their exits together.
	 */
	private void createRooms() {

		// create the rooms
		outside = new Room("outside the main entrance of the university");
		pub = new Room("in the campus pub");
		lab = new Room("in a computing lab");
		office = new Room("in the admin office");
		science = new Room("in the science classroom");
		math = new Room("in the math classroom");
		history = new Room("in the history classroom");
		gym = new Room("in the campus gym");
		cafe = new Room("in the campus cafeteria");
		mh2 = new Room("in Main-Hall 2");
		library = new Room("in the library");
		bathroom = new Room("in the bathroom");
		mh3 = new Room("in Main-Hall 3");
		mh1 = new Room("in Main-Hall 1");
		attendance = new Room("in the attendence office");
		ah1 = new Room("in A-hall 1");
		ah2 = new Room("in A-hall 2");
		bh1 = new Room("in B-hall 1");
		bh2 = new Room("in B-hall 2");
		counsellor = new Room("in the counsellor's office");
		mh4 = new Room("in Main-Hall 4");

		// initialise room exits
		outside.setExit("west", mh4);
		mh4.setExit("north", ah1);
		ah1.setExit("west", history);
		history.setExit("east", ah1);
		ah1.setExit("east", pub);
		pub.setExit("west", ah1);
		ah1.setExit("north", ah2);
		ah1.setExit("south", mh4);
		ah2.setExit("west", math);
		ah2.setExit("south", ah1);
		math.setExit("east", ah2);
		ah2.setExit("east", office);
		office.setExit("west", ah2);
		ah2.setExit("north", attendance);
		attendance.setExit("south", ah2);
		mh4.setExit("west", mh3);
		mh3.setExit("west", mh2);
		mh2.setExit("west", mh1);
		mh1.setExit("west", counsellor);
		counsellor.setExit("east", mh1);
		mh1.setExit("east", mh2);
		mh2.setExit("east", mh3);
		mh3.setExit("east", mh4);
		mh4.setExit("east", outside);
		mh2.setExit("north", science);
		science.setExit("south", mh2);
		mh1.setExit("south", cafe);
		cafe.setExit("north", mh1);
		mh3.setExit("south", bh1);
		bh1.setExit("north", mh3);
		bh1.setExit("west", lab);
		lab.setExit("east", bh1);
		bh1.setExit("east", library);
		library.setExit("west", bh1);
		bh1.setExit("south", bh2);
		bh2.setExit("north", bh1);
		bh2.setExit("west", gym);
		gym.setExit("east", bh2);
		bh2.setExit("east", bathroom);
		bathroom.setExit("west", bh2);

		currentRoom = outside; // start game outside

		lab.setItem(new Item("Computer"));
		science.setItem(new Item("Beakers"));
		history.setItem(new Item("Map"));
		cafe.setItem(new Item("Banana"));
		cafe.setItem(new Item("Apple"));
		cafe.setItem(new Item("Chicken"));
		gym.setItem(new Item("Balls"));
		gym.setItem(new Item("Cones"));
		library.setItem(new Item("Books"));
		pub.setItem(new Item("Juice"));
		math.setItem(new Item("Math worksheets"));

	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		printWelcome();

		// Enter the main command loop. Here we repeatedly read commands and
		// execute them until the game is over.

		boolean finished = false;
		while (!finished) {
			Command command = parser.getCommand();
			finished = processCommand(command);
		}
		System.out.println("Thank you for playing.  Good bye.");
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println();
		System.out.println("Welcome to Adventure!");
		System.out.println("Adventure is a new, incredibly boring adventure game.");
		System.out.println("Type 'help' if you need help.");
		System.out.println();
		System.out.println(currentRoom.getLongDescription());
	}

	/**
	 * Given a command, process (that is: execute) the command. If this command ends
	 * the game, true is returned, otherwise false is returned.
	 */
	private boolean processCommand(Command command) {
		boolean wantToQuit = false;

		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}
		//all the comands
		String commandWord = command.getCommandWord();
		if (commandWord.equals("help")) {
			printHelp();
		} else if (commandWord.equals("go")) {
			wantToQuit = goRoom(command);
		} else if (commandWord.equals("quit")) {
			wantToQuit = quit(command);
		} else if (commandWord.equals("inventory")) {
			printInventory();
		} else if (commandWord.equals("get")) {
			getItem(command);
		} else if (commandWord.equals("drop")) {
			dropItem(command);
		}
		return wantToQuit;
	}

	private void dropItem(Command command) {
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know what to drop...
			System.out.println("Drop what?");
			return;
		}

		String item = command.getSecondWord();

		// Try to leave current room.
		Item newItem = null;
		int index = 0;
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).getDescription().equals(item)) {
				newItem = inventory.get(i);
				index = i;
			}
		}
		if (newItem == null)
			System.out.println("That item is not in your inventory!");
		else {
			inventory.remove(index);
			currentRoom.setItem(new Item(item));
			System.out.println("Dropped:" + item);
		}
	}

	private void printInventory() {
		String output = "";
		for (int i = 0; i < inventory.size(); i++) {
			output += inventory.get(i).getDescription() + " ";
		}
		System.out.println("You are carrying: ");
		System.out.println(output);
	}
	// implementations of user commands:

	/**
	 * Print out some help information. Here we print some stupid, cryptic message
	 * and a list of the command words.
	 */
	private void printHelp() {
		System.out.println("You are lost. You are alone. You wander");
		System.out.println("around at the university.");
		System.out.println();
		System.out.println("Your command words are:");
		parser.showCommands();
	}

	private void getItem(Command command) {
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know what to pick up...
			System.out.println("Get what?");
			return;
		}

		String item = command.getSecondWord();

		// Try to leave current room.
		Item newItem = currentRoom.getItem(item);

		if (newItem == null)
			System.out.println("That item is not here!");
		else {
			inventory.add(newItem);
			currentRoom.removeItem(item);
			System.out.println("Picked up:" + item);
		}
	}

	/**
	 * Try to go to one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private boolean goRoom(Command command) {
		int numberOfItems = 0;
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return false;
		}

		String direction = command.getSecondWord();

		// Try to leave current room.
		Room nextRoom = currentRoom.getExit(direction);

		if (nextRoom == null)
			System.out.println("There is no door!");
		else {
			currentRoom = nextRoom;
			System.out.println(currentRoom.getLongDescription());
			if (currentRoom == office) {
				for (int i = 0; i < 5; i++) {
					for (int j = 0; j < 5; j++) {
						if ((inventory.get(i).getDescription()).equals(itemsNeeded.get(j))) {
							numberOfItems++;
						}

					}
			if (numberOfItems == 5) {
				System.out.println("You win!");
				return true;
			}
		}
		}
		}
		return false;
		
	}

	/**
	 * "Quit" was entered. Check the rest of the command to see whether we really
	 * quit the game. Return true, if this command quits the game, false otherwise.
	 */
	private boolean quit(Command command) {
		if (command.hasSecondWord()) {
			System.out.println("Quit what?");
			return false;
		} else
			return true; // signal that we want to quit
	}
}
