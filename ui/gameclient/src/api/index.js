import axios from "axios";

export const API = {
    login: (username, callback) => axios
        .post('connect', {"player": username, "password": null})
        .then(callback),

    startGame: (gameType,callback) => axios
        .post('start-game', {"gameType": gameType})
        .then(callback),

    performAction: (gameId, actionType,callback) => axios
        .post('user-action', {"gameId": gameId, "action": actionType})
        .then(callback),


    getEvents: (callback) => axios
        .get('get-events')
        .then(callback),

    getGameInfo: (gameId, callback) => axios
        .post('get-game-info',{"gameId": gameId})
        .then(callback)
}

