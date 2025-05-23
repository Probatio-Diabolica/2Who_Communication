# 2Who Communication
> 2Who Communication is a lightweight and modular messaging system consisting of two key components:

>*    [2Who_Server](Server.md):- A centralized server responsible for managing communication, routing messages, and maintaining user sessions.
  - To run it :  run ```java -jar server.jar <portnumber>```
  - Note :: If no port number is being provided, then it'll use 2005 as a default port 

>*    [2Who_Chat](Chat.md): An intuitive application that connects to the server to enable real-time messaging between users.
  - Before running the client, make sure you run the server.
  - after the server has been started, fill the same port as of the server in the client to connect
  - in address, provide you local IP or just type in ```localhost``` it should work
