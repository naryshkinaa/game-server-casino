Game server Example
=========================
##Description

This is implementation of Game Server for card games. (see TASK.md)

##Features
- Implementing 2 games with rules as described.
- UI client is ready and work.
- Multi-tabling is done. UI support up to 4 concurrent games. Server has no limit for concurrent games
- Player has limitations for making action (20 sec). After this it makes auto Fold
- Player can reconnect to server and running games (it can be done to any state: waiting opponent or performing action). Can be test this by refresh page and click connect button again
- Bot (for single game) is implemented with different strategies. It start by default and can be configured (see Params)
- Persist player balance during restarting. 

##Limitations
- I can't implement normal socket client on UI, so i have done some ugly REST emulation with ping test every second, but normally all UserNotifications should be delivered by socket


##Params 
[located in the file Params] 
 - timeLimitSec - time for user action before Auto Fold (Default 20)
 - startBot - Should start bot for single card game (Default true)
 - botStrategy - bot strategy (Base (play 8 and greater) and Agro (play 3 and greater))
 - botConcurrentGames - number of concurrent bots game (Default 2)
 - restPort - REST port of application (Default 8080)
 - socketPort - socket port of application (Default 8081)

##Run
 - server: standard Idea start App
  - client: from ui/gameclient folder 
   - npm install 
   - npm run serve
   - http://localhost:3000 


![plot](./screen.png)