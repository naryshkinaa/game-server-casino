Game server Example
=========================
Description

Params (located in the file Params) 
 - timeLimitSec - time for user action before Auto Fold (Default 20)
 - startBot - Should start bot for single card game (Default true)
 - botStrategy - bot strategy
 - botConcurrentGames - number of concurrent bots game (Default 2)
 - restPort - REST port of application (Default 8080)
 - socketPort - socket port of application (Default 8081)

Run
 - server:
 - client: from UI folder - npm run build, npm run serve

Notes about implementation
 - I can't implement normal socket bidirection client on UI, so i have done some ugly REST emulation. 
 - socket Index done as soon as possible, only for working application, not as good example 
