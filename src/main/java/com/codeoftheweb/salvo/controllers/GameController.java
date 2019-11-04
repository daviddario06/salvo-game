package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    public GameRepository gameRepository;

    @Autowired
    public PlayerRepository playerRepository;

    @Autowired
    public GamePlayerRepository gamePlayerRepository;



    @Autowired
    public SalvoRepository salvoRepository;


    @RequestMapping("/games")
    public Map<String, Object> getAll(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        if (!isGuest(authentication)) {
            dto.put("player", playerRepository.findPlayerByUserName(authentication.getName()).makePlayerDTO());
        } else
            dto.put("player", "Guest");

        dto.put("games", gameRepository.findAll()
                .stream()
                .map(game -> game.makeGameDTO())
                .collect(Collectors.toList()));

        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {

        Player player = playerRepository.findPlayerByUserName(authentication.getName());

        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);


        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "No Such Player "), HttpStatus.UNAUTHORIZED);
        }

        Game game = new Game(LocalDateTime.now());
        gameRepository.save(game);

        GamePlayer gamePlayer = new GamePlayer(game, player, LocalDateTime.now());
        gamePlayerRepository.save(gamePlayer);

        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.OK);
    }


    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {

        Game game = gameRepository.findById(gameId).orElse(null);
        Player player = playerRepository.findPlayerByUserName(authentication.getName());

        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);


        if (game == null)
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);

        if (player == null)
            return new ResponseEntity<>(makeMap("error", "Player Not Found "), HttpStatus.UNAUTHORIZED);


        if (game.getGamePlayers().size() == 2)
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);

        GamePlayer gamePlayer = new GamePlayer(game, player, LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        gamePlayerRepository.save(gamePlayer);
        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.OK);

    }




    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvoes(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {

        Player player = playerRepository.findPlayerByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);

        if (gamePlayer == null)
            return new ResponseEntity<>(makeMap("error", "Player not found"), HttpStatus.UNAUTHORIZED);

        if (player == null)
            return new ResponseEntity<>(makeMap("error", "Player Not Found "), HttpStatus.UNAUTHORIZED);

        if (!(gamePlayer.getPlayer().getId() == player.getId()))
            return new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);

        if (gamePlayer.getSalvoes().isEmpty()){
            salvo.setTurn((long)1);
            salvo.setGamePlayer(gamePlayer);
            salvoRepository.save(salvo);
            return new ResponseEntity<>(makeMap("OK","Salvo Created"),HttpStatus.CREATED);
        }

        GamePlayer opponent = gamePlayer.getOpponent();

        if (opponent != null){
            if (gamePlayer.getSalvoes().size() <= opponent.getSalvoes().size()){
                salvo.setTurn((long) (gamePlayer.getSalvoes().size()+1));
                salvo.setGamePlayer(gamePlayer);
            }
            else return new ResponseEntity<>(makeMap("error","wait for turn"), HttpStatus.FORBIDDEN);
        }
        else return new ResponseEntity<>(makeMap("error","No such opponent"), HttpStatus.FORBIDDEN);

        salvoRepository.save(salvo);
        return new ResponseEntity<>(makeMap("OK","Success"),HttpStatus.CREATED);

    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
