<template>
  <div class="tableRoot">
    <div class="tableInner" v-if="currentState===GameState.SELECT_GAME">
      <div class="buttonWrapper">
        <button v-on:click='selectGame("SINGLE_CARD_GAME")' class="selectGameButton"><b>Single Card Game</b></button>
      </div>
      <div class="buttonWrapper">
        <button  v-on:click='selectGame("DOUBLE_CARD_GAME")' class="selectGameButton"><b>Double Card Game</b></button>
      </div>
    </div>
    <div class="tableInner" v-if="currentState===GameState.WAITING_OPPONENT">
      <div class="waitingLabel">
        Waiting Opponent ...
      </div>
    </div>
    <div class="tableInner" v-if="currentState===GameState.RUNNING">
      <div class="cardWrapper">
        {{ card1 }} {{ card2 }}
      </div>
      <div class="cardWrapper">
        <button :disabled="actionDone" v-on:click='action("FOLD")' class="selectGameButton"><b>Fold</b></button>
      </div>
      <div class="cardWrapper">
        <button :disabled="actionDone" v-on:click='action("PLAY")' class="selectGameButton"><b>Play</b></button>
      </div>
      <div class="result">{{result}}</div>
    </div>
  </div>

</template>
<script>

import {API} from '../api';
import {GameState} from '../logic/gameState';

export default {
  name: 'GameTable',
  data : function () {
    return {
      GameState : GameState,
      currentState: GameState.SELECT_GAME,
      actionDone: false,
      gameId: null,
      result: null,
      card1: null,
      card2: null
    }
  },
  methods: {
    selectGame: function (gameType) {
      API.startGame(gameType, response => {
        this.currentState = GameState.WAITING_OPPONENT;
        this.gameId = response.data.gameId;
        console.log(this.gameId)
      })
    },
    toNewGame: function(){
      this.currentState = GameState.SELECT_GAME;
      this.gameId = null;
      this.actionDone = false;
      this.result = null;
    },
    action: function (actionType) {
      this.actionDone = true;
      API.performAction(this.gameId, actionType, () => {
      })
    },
    gameResultPush: function(data) {
      if(data.gameId === this.gameId){
        this.result = data.message
        var self = this;
        window.setTimeout(function (){
         self.toNewGame();
        },3000)

      }
    },
    gameStartPush: function(data) {
      console.log(data)
      if(data.gameId === this.gameId){
        this.currentState = GameState.RUNNING;
        this.card1 = data.hand.cards[0].rank
        if(data.hand.cards.length == 2) this.card2 = data.hand.cards[1].rank
      }
    },
    restoreGame: function(game) {
      this.gameId = game;
      this.currentState = GameState.RESTORING;
      API.getGameInfo(game, response =>  {
        if(!response.data.exist) {
          this.toNewGame();
        }
        else {
          if(response.data.hand != null){
            this.currentState = GameState.RUNNING;
            this.card1 = response.data.hand.cards[0].rank;
            this.actionDone = response.data.actionDone;
          }
          else {
            this.currentState = GameState.WAITING_OPPONENT;
          }
        }
      })
    }
  }
}
</script>
<style>

.tableRoot {
  height: calc(100% - 0px);
  width: 100%;
  display: flex;
  border-radius: 10px;
}
.selectGameButton {

  width: 200px;
  height: 100%;
  border-radius: 10px;
  border: none;
}
.buttonWrapper {
  width: 100%;
  height: 20%;
  margin-top: 8%;
}
.waitingLabel {
  width: 100%;
  height: 20%;
  margin-top: 17%;
  font-size: 20px;
  color:white;
}
.cardWrapper {
  width: 100%;
  height: 20%;
  margin-top: 4%;
  font-size: 40px;
  color:white;
}
.tableInner {
  width: 100%;
  height: 100%;
  /*display: flex;*/
}
.tableRoot {
  background-image: url("../../public/table.jpeg");
  background-color: rgba(255,255,255,0.1);
  background-blend-mode: lighten;
}
.result {
  width: 100%;
  color: white;
}
</style>