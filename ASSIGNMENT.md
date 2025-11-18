# Tile / grid game SameGame

Your task is is to design and develop in Java a tile based puzzle (non-action) game [SameGame](https://en.wikipedia.org/wiki/SameGame). Your design should follow the principles taught in the course and utilize the design patterns you have learned about. In particular, you should:

- Consider the main design around the **Model-View-Controller** or **Subject-Observer** patterns, **or a mixture of both**. To demonstrate this, your game should provide two distinct "views" of the game state. One obvious view is the graphical output (you can use a Swing frame), the other one could be a console based text view of the game (e.g. for debugging of the game engine, logging, etc.), or a game "capture view" (this one does not necessarily show anything, but remembers all subsequent game states / views and records them in a list to be later, e.g., replayed or analysed). You should provide at least two different views of the game and show that you can easily register / de-register them in your application through an observer(-like) pattern.

- Consider a game model that keeps track of the set difficulty level (number of colors in use), the current state of the game, accepts controlling events to update the state, checks for end-of-game / winning / loosing conditions, and notifies observers whenever necessary,

- Consider re-pluggable methods for providing different ways of controlling the game. One could be the keyboard, one could be the mouse, yet another could be your mobile phone (with an app that you do not have to write) over a network connection. These methods should be re-pluggable in your design, you could use e.g. the Strategy pattern for this, or any other suitable pattern. At least one default method for providing input should be implemented. Also, the different input methods do not have to be able to work together with each other (but they can).

By no means you are limited to the above requirements, you can extend the idea any way you see fit and apply other patterns. To visualize the game, it is sufficient to use uniformly colored shapes (refer to the circle icon task in the first set of exercises), but you can also use suitable bitmap graphics that you may find on the Internet.

Fulfilling these basic requirements should get you a **grade of 3**, provided of course the principles are applied in the correct way and you end up with a functioning program. Additionally the presentation is required to get a passing grade (see sections below).

**Note:** the purpose of the project **is not** fancy graphics or animations (the existing SameGame implementations go quite far with things like this), in particular (if you are familiar with it) you **should not** use the game loop design pattern and do detailed frame by frame renderings of the screen. In other words, it is perfectly fine and enough to update the game view only once per game state change, and this is usually triggered by user input. What is being judged is the design, use of Java, and familiarity with OOP principles from the course, not the ability of programming graphics.

## Extensions

On top of the basic requirements you can develop the following extensions to shoot for **grade 4**:

- A system for sound notifications using the observer pattern. This should be separate from the graphical / visual game view. The game model under predefined (by you) conditions produces events, for which a dedicated event observer should generate sounds.
 
- A facility for the game to provide the next move suggestion (game helper / cheater) to point the player to the most scoring move to perform next. (So not a fully fledged all move combinations analyzer to maximize the game score.)

- A facility to save and load the list of high scores achieved so far, preferably by using Java serialization (we do not cover this in depth in the course, so it requires a bit of self study (Section 7.5 of the book covers it), but the concept is not too difficult).

- We also expect the code to be properly documented with **JavaDoc** and see attempts of testing your code using **JUnit**.

## Framework

To attempt **grade 5** for the project you should design your code around the principle of a framework, so that you can easily implement other (but similar) games to SameGame with minimal effort. It is up to you how to design the framework for most re-usability, you should, however, document the design of your framework sufficiently in the code and discuss this in the presentation, as well as demonstrate its usability by implementing another game with your framework, namely the [2048 game](https://2048game.com). Alternatively, you can show case your framework by implementing the game [Sokoban](https://en.wikipedia.org/wiki/Sokoban). For this one you may want to use the attached game tile graphics that we extracted from the quoted Wikipedia page:

[sokoban_icons.zip](src/main/resources/sokoban_icons.zip)

## Project code maintenance - version control
As an exercise in code management in real software development and also to facilitate the division of work within your project groups, we ask you to maintain your code through one of the publicly available version control systems, for example GITHub or Bitbucket. To do this, set up an account for each project member (unless you have one already and want to use it), create a **private** (this is important, we do not want the other groups to be peeking into your developments) repository for your project. You should also shortly mention your experiences with version control in the presentation.

## Project code submission

The final project code is to be submitted through the Blackboard submission system, a suitable entry will be created in due time and available in the *Project Assignment* section. When submitting, consider the following:

1. Code should be properly indented and formatted.
2. Code should be documented, explain the role of the parameters to methods and constructors, as well as the general code architecture.
3. Code should not be riddled with debugging statements.
4. If you go for the framework approach, there should be enough documentation explaining how to use your framework (you can also write a short manual for this).

To submit your project prepare a **single ZIP file** with all the (source) files that make up your project. We know that having the code on GITHub or elsewhere should be enough, but this also allows us to see that you are done with it (and also your repository should be private, see above).

***The project material submission is due on June 5th  (end of the examination period minus the holiday day)!***
