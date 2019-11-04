package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_ID")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_ID")
    private Player player;

    private Float score;
    private LocalDateTime finishDate;

    public Score() { }

    public Score(Game game, Player player, Float score, LocalDateTime finishDate) {
        this.game = game;
        this.player = player;
        this.score = score;
        this.finishDate = finishDate;
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }



    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Float getScore() {
        return score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public Map <String, Object> makeScoreDTO(){
        Map<String, Object> dto = new LinkedHashMap<String,Object>();

        dto.put("player", this.getPlayer().getId());
        dto.put("score", this.getScore());
        dto.put("finishDate",this.getFinishDate());

        return dto;
    }

}