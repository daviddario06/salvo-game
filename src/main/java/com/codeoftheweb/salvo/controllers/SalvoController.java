package com.codeoftheweb.salvo.controllers;


import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
class SalvoController {

    @Autowired
    public PlayerRepository playerRepository;

    @Autowired
    public GamePlayerRepository gamePlayerRepository;

    @Autowired
    public ScoreRepository scoreRepository;

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findGamePlayer(@PathVariable Long gamePlayerId, Authentication authentication) {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        Map<String, Object> hits;

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        Player player = playerRepository.findPlayerByUserName(authentication.getName());


        List<Map<String, Object>> salvoes;

        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);


        if (gamePlayer == null)
            return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);


        if (player == null)
            return new ResponseEntity<>(makeMap("error", "Player Not Found "), HttpStatus.UNAUTHORIZED);


        if (gamePlayer.getPlayer().getId() == player.getId()) {

            salvoes = gamePlayer
                    .getGame()
                    .getGamePlayers()
                    .stream()
                    .map(gp -> gp.makeGamePlayerSalvoesDTO())
                    .flatMap(x -> x.stream())
                    .collect(toList());

            salvoes = sortedObjectTurn(salvoes);
            hits = gamePlayer.makeHitsDTO();
            String gameState = gamePlayer.getGameState();

            dto.put("id", gamePlayer.getGame().getId());
            dto.put("created", gamePlayer.getGame().getCreationDate());
            dto.put("gameState", gameState);

            dto.put("gamePlayers", gamePlayer.getGame()
                    .getGamePlayers()
                    .stream()
                    .map(gp -> gp.makeGamePlayerDTO())
                    .collect(Collectors.toList()));

            dto.put("ships", gamePlayer.getShips()
                    .stream()
                    .map(sh -> sh.makeShipDTO())
                    .collect(Collectors.toList()));

            dto.put("salvoes", salvoes);
            dto.put("hits", hits);

            addScore(gamePlayer, gameState);

            return new ResponseEntity<Map<String, Object>>(dto, HttpStatus.OK);
        } else
            return new ResponseEntity<>(makeMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
    }

    private void addScore(GamePlayer gamePlayer, String gameState) {

        if(scoreRepository.findAll()
                .stream()
                .filter( score -> score.getGame().getId()==gamePlayer.getGame().getId() )
                .count()<1){

            if (gameState == "WON"){
                Score score_1 = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), (float)1.0, LocalDateTime.now());
                Score score_2 = new Score(gamePlayer.getGame(), gamePlayer.getOpponent().getPlayer(), (float)0.0, LocalDateTime.now());
                scoreRepository.save(score_1);
                scoreRepository.save(score_2);
            }
            if (gameState == "LOST"){
                Score score_1 = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), (float)0.0, LocalDateTime.now());
                Score score_2 = new Score(gamePlayer.getGame(), gamePlayer.getOpponent().getPlayer(), (float)1.0, LocalDateTime.now());
                scoreRepository.save(score_1);
                scoreRepository.save(score_2);
            }
            if (gameState == "TIE"){
                Score score_1 = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), (float)0.5, LocalDateTime.now());
                Score score_2 = new Score(gamePlayer.getGame(), gamePlayer.getOpponent().getPlayer(), (float)0.5, LocalDateTime.now());
                scoreRepository.save(score_1);
                scoreRepository.save(score_2);
            }
        }

    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private List<Map<String, Object>> sortedObjectTurn(List<Map<String, Object>> listObject) {
        return listObject
                .stream()
                .sorted((o1, o2) -> (int) ((long) o1.get("turn") - (long) o2.get("turn")))
                .collect(toList());
    }
}


