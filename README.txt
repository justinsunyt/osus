=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: xxxxxxxx
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Collections: Notes are stored in a collection, specifically a TreeSet. This
     is an appropriate use of collections because each note has no duplicates and
     comes in a specific order denoted by its quarter note. The TreeSet is used often
     to loop through notes in the tick function and check for timing and animations.
     I also use the TreeSet.higher function to get the currentNote to check for clicks
     and the TreeSet.descendingSet function to draw the notes in reverse so the most
     immediate note appears on top of everything else.

  2. File I/O: The game inputs beatmaps in the form of .txt files. Each beatmap file
     contains information such as the beatmap's name, length, difficulty, and all of its
     notes. The game will input whatever beatmap the user has the path to and convert
     the text to in game objects. When the user pauses the game, the current state of the
     game including score and combo and what time they paused at will be stored in a
     save.txt file. When the user quits and reopens the game, the game will read the
     save.txt file and continue where they left off.

  3. Inheritance and Subtyping: There are two types of Notes in osus: the Circle and the
     Slider. They share a lot of core functionalities such getting hits and misses and
     calculating score, but they also have their own unique features, mainly in terms
     of looks and animations. Therefore, I created a Note class that contains all the
     shared functionalities and Circle and Slider classes that extend the Note class and
     contain their own animations functions.

  4. JUnit Testable Component: I tested the game by creating a JUnit test class that
     tests the Note class. I tested the getScore function to make sure that the score
     is calculated correctly based on its potential hit score (which in game is dependent
     on hit accuracy). I tested score, combo, and maxCombo to make sure combo breaks work.


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

  Button: simple button that displays image, used to display start button.
  Circle: extends Note, contains all the animations for a circle note.
  Cursor: displays the cursor image and moves it based on mouse position.
  GameObj: abstract class that contains all the shared functionalities between all game objects.
  Note: abstract class that contains all the shared functionalities between Sliders and Circles.
  Slider: extends Note, contains all the animations for a slider note.
  FileLineIterator: iterates through a file line by line.
  Screen: provides screen size to scale components.
  Sound: plays sounds.


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

  Getting the timing of the notes to be synced up to the song was very challenging.
  I first resorted to using tick timing, but ultimately resorted to measuring a timeDelta.
  Also sorting out pausing and how to sync up the song with the saved data was very challenging.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

  I would refactor more of the GameScreen to extract abstract functionalities to make it easier to test.


========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

  The game was inspried by osu!
  I used the song Harumachi Clover by Will Stetson.