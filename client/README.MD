# Game of Three java Client
# Notes :
- Java version 11 
- way of presentation is through console but it's decoupled from the logic
- built on top of Spring boot, used websocket for communication with other player
- Reacts upon events received from the subscribed topics
- players can play both automatically without inputs but only after
doing registration and start the game.

# Run steps:
- build with maven : mvnw install
- use the generated Jar in target folder and copy it with the number of clients
you want to try
- then run jars : java -jar client-X.X.X.jar
- you will be prompted to start new game from main menu
- register annonymously with nickname and play mode (auto / manual)
- wait other player to join (run another copy of client jar)
- then start play
