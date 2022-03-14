<template>
  <div class="header">
    <h2>Game Lobby</h2>
    <label class="nameLabel">Enter your name</label>
    <input v-model="username">
    <button v-on:click="connect" id="connectButton">Connect</button>
<!--    <br>-->
<!--    <br>-->
    <label class="balanceLabel">Balance</label>
    <label class="balanceValue">{{ balance }}</label>
  </div>
  <div v-if="balance != null" class="gamesView">
    <GamesComponent ref="childComponent"/>
  </div>
</template>
<script>
import GamesComponent from './components/Games.vue';
import {API} from './api';
// import { io } from "socket.io-client";

export default {
  name: 'app',
  components: {
    GamesComponent
  },
  data: function () {
    return {
      username: null,
      balance: null,
      socket: null
    }
  },
  mounted: function () {
    this.username = localStorage.getItem("user");
    //here should be socket connection, but i have some problems with implementation, so temp solution is rest ping every second
    // const socket = io.connect("http://localhost:8080")
    // this.socket = socket;
  },
  methods: {
    connect: function () {
      localStorage.setItem("user", this.username);
      API.login(this.username, response => {

        this.balance = "$" + response.data.balance;
        let self = this;
        window.setTimeout(function(){
          if(response.data.activeGames != null) {
            self.$refs.childComponent.restoreGames(response.data.activeGames)
          }
          self.getEvents();
        },100);
      })
    },
    getEvents: function (){
      API.getEvents(response =>  {
        if(response.data.gameResult != null) {
          this.balance= response.data.gameResult[0].balance;
          response.data.gameResult.forEach(
              d => this.$refs.childComponent.gameResultPush(d)
          )
        }
        if(response.data.gameStarted != null) {
          response.data.gameStarted.forEach(
              d => this.$refs.childComponent.gameStartPush(d)
          )
        }
      });
      let self = this;
      window.setTimeout(function(){
        self.getEvents()
      },1000)
    }
  }
}
</script>
<style>
html,body {
  height:calc(100% - 16px);
}
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  width: calc(100% - 16px);
  height: calc(100% - 16px);
}

.nameLabel {
  margin-right: 10px;
}

.balanceLabel {
  margin-left: 20px;
  margin-right: 10px;
}

#connectButton {
  margin-left: 10px;
}

.balanceValue {
  width: 200px;
}
.header {
  height: 15%;
}
.gamesView {
  width: 100%;
  height: 85%;
}
.logo {
  width: 200px;
  height: 40px;
  padding-top: 10px;
}
</style>