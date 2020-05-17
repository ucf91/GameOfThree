# Game of Three server
# Notes:
- Java version 11
- built on top spring boot, used Websockets for communications
- DDD architecture
- communications are based on event driven architecture since the used websocket solution is based on events
and topics with subscriptions by clients
- storage is inMemory with java collections
- server can handle any number of clients (parallel / sequentially)
- if for instance 3 clients want to start new game, the server synchronize them to make first
two clients starts the game and the third one will wait for fourth client to join him in another game.
- integration tests is doable but I ran out of time.

# run steps:
mvnw spring-boot:run
