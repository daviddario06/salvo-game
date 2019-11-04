package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;


    @ElementCollection
    @Column(name = "shipLocation")
    private List<String> locations;

    public Ship() {
    }

    public Ship(String type, GamePlayer gamePlayer, List<String> locations) {
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }



    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }



    public List<String> getLocations() {
        return locations;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public Map<String, Object> makeShipDTO(){
        Map<String, Object> dto = new LinkedHashMap<String,Object>();
        dto.put("type",this.getType());
        dto.put("locations",this.getLocations());
        return dto;
    }

}
