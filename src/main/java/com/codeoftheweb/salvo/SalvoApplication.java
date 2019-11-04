package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import com.codeoftheweb.salvo.repositories.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);

    }

    @Bean
    public CommandLineRunner initData(PlayerRepository repository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository
            , SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
        return (args) -> {

            Player bauer = new Player("j.bauer@ctu.gov", "123");
            Player obrian = new Player("c.obrian@ctu.gov", passwordEncoder().encode("456"));
            Player almeida = new Player("t.almeida@ctu.gov", passwordEncoder().encode("789"));
            Player palmer = new Player("d.palmer@whitehouse.gov", passwordEncoder().encode("147"));
            bauer.setPassword(passwordEncoder().encode("123"));

            repository.save(bauer);
            repository.save(obrian);
            repository.save(almeida);
            repository.save(palmer);

            Game game_1 = new Game(LocalDateTime.parse("2017-02-17T15:20:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Game game_2 = new Game(LocalDateTime.parse("2017-02-17T16:20:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Game game_3 = new Game(LocalDateTime.parse("2017-02-17T17:20:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Game game_4 = new Game(LocalDateTime.parse("2017-02-17T18:20:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Game game_5 = new Game(LocalDateTime.parse("2017-02-17T19:20:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Game game_6 = new Game(LocalDateTime.parse("2017-02-17T20:20:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            gameRepository.save(game_1);
            gameRepository.save(game_2);
            gameRepository.save(game_3);
            gameRepository.save(game_4);
            gameRepository.save(game_5);
            gameRepository.save(game_6);

            GamePlayer gamePlayer_1 = new GamePlayer(game_1, bauer, LocalDateTime.parse("2017-02-17T15:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_2 = new GamePlayer(game_1, obrian, LocalDateTime.parse("2017-02-17T15:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_3 = new GamePlayer(game_2, bauer, LocalDateTime.parse("2017-02-17T16:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_4 = new GamePlayer(game_2, obrian, LocalDateTime.parse("2017-02-17T16:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_5 = new GamePlayer(game_3, obrian, LocalDateTime.parse("2017-02-17T17:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_6 = new GamePlayer(game_3, almeida, LocalDateTime.parse("2017-02-17T17:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_7 = new GamePlayer(game_4, bauer, LocalDateTime.parse("2017-02-17T18:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_8 = new GamePlayer(game_4, obrian, LocalDateTime.parse("2017-02-17T18:20:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_9 = new GamePlayer(game_5, almeida, LocalDateTime.parse("2017-02-17T19:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_10 = new GamePlayer(game_5, bauer, LocalDateTime.parse("2017-02-17T19:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            GamePlayer gamePlayer_11 = new GamePlayer(game_6, palmer, LocalDateTime.parse("2017-02-17T20:25:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            gamePlayerRepository.save(gamePlayer_1);
            gamePlayerRepository.save(gamePlayer_2);
            gamePlayerRepository.save(gamePlayer_3);
            gamePlayerRepository.save(gamePlayer_4);
            gamePlayerRepository.save(gamePlayer_5);
            gamePlayerRepository.save(gamePlayer_6);
            gamePlayerRepository.save(gamePlayer_7);
            gamePlayerRepository.save(gamePlayer_8);
            gamePlayerRepository.save(gamePlayer_9);
            gamePlayerRepository.save(gamePlayer_10);
            gamePlayerRepository.save(gamePlayer_11);

            Ship ship_1 = new Ship("Destroyer", gamePlayer_1, Arrays.asList(new String[]{"H2", "H3", "H4"}));
            Ship ship_2 = new Ship("Submarine", gamePlayer_1, Arrays.asList(new String[]{"E1", "F1", "G1"}));
            Ship ship_3 = new Ship("Patrol Boat", gamePlayer_1, Arrays.asList(new String[]{"B4", "B5"}));
            Ship ship_4 = new Ship("Destroyer", gamePlayer_2, Arrays.asList(new String[]{"B5", "C5", "D5"}));
            Ship ship_5 = new Ship("Patrol Boat", gamePlayer_2, Arrays.asList(new String[]{"F1", "F2"}));
            Ship ship_6 = new Ship("Destroyer", gamePlayer_3, Arrays.asList(new String[]{"B5", "C5", "D5"}));
            Ship ship_7 = new Ship("Patrol Boat", gamePlayer_3, Arrays.asList(new String[]{"C6", "C7"}));
            Ship ship_8 = new Ship("Submarine", gamePlayer_4, Arrays.asList(new String[]{"A2", "A3", "A4"}));
            Ship ship_9 = new Ship("Patrol Boat", gamePlayer_4, Arrays.asList(new String[]{"G6", "H6"}));

            shipRepository.save(ship_1);
            shipRepository.save(ship_2);
            shipRepository.save(ship_3);
            shipRepository.save(ship_4);
            shipRepository.save(ship_5);
            shipRepository.save(ship_6);
            shipRepository.save(ship_7);
            shipRepository.save(ship_8);
            shipRepository.save(ship_9);

            Salvo salvo_1 = new Salvo(gamePlayer_1, (long) 1, Arrays.asList(new String[]{"B5", "C5", "F1"}));
            Salvo salvo_2 = new Salvo(gamePlayer_2, (long) 1, Arrays.asList(new String[]{"B4", "B5", "B6"}));
            Salvo salvo_3 = new Salvo(gamePlayer_1, (long) 2, Arrays.asList(new String[]{"F2", "D5"}));
            Salvo salvo_4 = new Salvo(gamePlayer_2, (long) 2, Arrays.asList(new String[]{"E1", "H3", "A2"}));
            Salvo salvo_5 = new Salvo(gamePlayer_3, (long) 1, Arrays.asList(new String[]{"A2", "A4", "G6"}));
            Salvo salvo_6 = new Salvo(gamePlayer_4, (long) 1, Arrays.asList(new String[]{"B5", "D5", "C7"}));
            Salvo salvo_7 = new Salvo(gamePlayer_3, (long) 2, Arrays.asList(new String[]{"A3", "H6"}));
            Salvo salvo_8 = new Salvo(gamePlayer_4, (long) 2, Arrays.asList(new String[]{"C5", "C6"}));

            salvoRepository.save(salvo_1);
            salvoRepository.save(salvo_2);
            salvoRepository.save((salvo_3));
            salvoRepository.save((salvo_4));
            salvoRepository.save((salvo_5));
            salvoRepository.save((salvo_6));
            salvoRepository.save((salvo_7));
            salvoRepository.save((salvo_8));


            Score score_1 = new Score(game_1, bauer, (float) 1, LocalDateTime.parse("2017-02-17T15:30:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Score score_2 = new Score(game_1, obrian, (float) 0.0, LocalDateTime.parse("2017-02-17T15:30:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Score score_3 = new Score(game_2, bauer, (float) 0.5, LocalDateTime.parse("2017-02-17T15:30:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Score score_4 = new Score(game_2, obrian, (float) 0.5, LocalDateTime.parse("2017-02-17T15:30:15", DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            scoreRepository.save(score_1);
            scoreRepository.save(score_2);
            scoreRepository.save(score_3);
            scoreRepository.save(score_4);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}


@Configuration
class WebSecurityConfiguration<PersonRepository> extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName -> {
            Player player = playerRepository.findPlayerByUserName(inputName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/web/**").permitAll()
                .antMatchers("/api/games").permitAll()
                .antMatchers("/api/game_view/*").hasAuthority("USER")
                .antMatchers("/api/game/**").hasAuthority("USER")
                .and()
                .formLogin();

        http.formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();
        http.headers().frameOptions().disable();
        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}