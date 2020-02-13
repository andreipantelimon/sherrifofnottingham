
# Sheriff of Nottingham

Minimal implementation for the board game Sheriff of Nottingham as part of the homework for Object Oriented Programming course.

Sheriff of Nottingham is a game where 3 types of players are trying to get as many coins as possible before the end of the round, declaring bags of cards and, possibly, trying to bribe the sheriff to ignore illegal cards as they are the most profitable.

The types of players are as follows:
1. Basic player which is the honest one who almost never lies, doesn't accept bribe and inspects everyone.
2. Greedy player who, at times, lies and gets the most bribe of them all.
3. Bribe player, which gives bribe and inspects only the left and right players.

Implemented by: Andrei Pantelimon 325CA

## Demo / How it works
This is how the input looks like:

You give the program a number of rounds, a number of players, what type are your players, a number of cards and the cards themselves.

> Note: The numbers that are the cards are id's that are referenced in a table on the site of the homework.


![Input](https://i.imgur.com/SgSKbau.png)          ![Output](https://i.imgur.com/83BJBsm.png)

And it will magically print the scores of each player as shown in the second photo.

## Compile and usage
Compiling the program: 

` javac -cp ".:FileIO.jar" -g com/tema1/main/Main.java`
> Note: You need to have the .jar file of the API inside the src folder.

Running it:
`java -cp ".:FileIO.jar" com/tema1/main/Main INPUT_FILE.in dummy > OUTPUT_FILE.out`

## Tech framework and API

Project is made with IntelliJ Idea Community Edition in Java 12 with using only the [FileIO API](http://elf.cs.pub.ro/poo/laboratoare/tutorial-io) from the OOP team at the university.

## Code example

Below is showed how a basic player is legitimate and declares his bag with only legal cards.

![Code example](https://i.imgur.com/9cm9MrZ.png)

It makes an array of only the legal cards, sorts it, gets the most frequent card and the count and adds it to the bag.

## Flow and Design

Explaining in short terms: 
1. Every players get his own class : Merchant for the basic, Greedy for the greedy player and Bribe for the bribe player.
2. The game runs:
2.1. Every player is declared as sheriff when his turn comes.
2.2. Merchants declares their bags.
2.3. Sheriff goes on with the inspection.
2.4. Items that passed are moved on the stall.
3. The profit and the King's and Queen's bonus are calculated and added on the score.
4. The final scoreboard is printed.

## References
- [Sheriff of Nottingham](http://elf.cs.pub.ro/poo/teme/tema) - Homework description with necessary information 
- [FileIO API](http://elf.cs.pub.ro/poo/laboratoare/tutorial-io) - Download and tutorial on how to use it
