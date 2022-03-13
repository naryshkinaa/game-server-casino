<template>
    <div class="board">
      <div id="first" class="table">
        <GameTable ref="child1"/>
      </div>
      <div id="second" class="table">
        <GameTable ref="child2"/>
      </div>
      <div style='clear:both'></div>
      <div id="third" class="table">
        <GameTable ref="child3"/>
      </div>
      <div id="fourth" class="table">
        <GameTable ref="child4"/>
      </div>
    </div>
</template>
<script>


import GameTable from "@/components/GameTable";

export default {
  name: 'GamesComponent',
  components: {GameTable},
  methods: {
    gameResultPush: function(data) {
      this.$refs.child1.gameResultPush(data);
      this.$refs.child2.gameResultPush(data);
      this.$refs.child3.gameResultPush(data);
      this.$refs.child4.gameResultPush(data);
    },
    gameStartPush: function(data) {
      this.$refs.child1.gameStartPush(data);
      this.$refs.child2.gameStartPush(data);
      this.$refs.child3.gameStartPush(data);
      this.$refs.child4.gameStartPush(data);
    },
    restoreGames: function (games){
      if(games[0] != null) this.$refs.child1.restoreGame(games[0]);
      if(games[1] != null) this.$refs.child1.restoreGame(games[1]);
      if(games[2] != null) this.$refs.child1.restoreGame(games[2]);
      if(games[3] != null) this.$refs.child1.restoreGame(games[3]);
    }
  }
}
</script>
<style>

.table {
  /*width: 45%;*/
  /*height: 45%;*/
  width: calc(50% - 14px);
  height: calc(50% - 14px);
  margin:6px;
  border:1px solid black;
  float: left;
  border-radius: 10px;
}
.board {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

</style>