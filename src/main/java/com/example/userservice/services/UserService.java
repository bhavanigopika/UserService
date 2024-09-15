package com.example.userservice.services;

//import com.example.userservice.dtos.SendEmailDTO;
import com.example.userservice.dtos.SendEmailDTO;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    //we directly use the attribute of bCryptPasswordEncoder because of spring security
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private KafkaTemplate<String, String> kafkaTemplate;//accept key-value pair
    private ObjectMapper objectMapper;
    /*private static final String SECRET_KEY = "dfsf342gg4fgd34fg34433ffdgbh";*/


    //Add the dependency of bCryptPasswordEncoder, so create the method of BCryptPasswordEncoder in the BCryptConfiguration class.
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder,
                       UserRepository userRepository,
                       TokenRepository tokenRepository,
                       KafkaTemplate<String, String> kafkaTemplate,
                       ObjectMapper objectMapper) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    public User signUp(String fullName, String email, String password) throws JsonProcessingException {
        User user = new User();
        user.setName(fullName);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        /*
        After signup, I want to publish this event to the message queue  We are going to use the Kafka message queue
         */
        SendEmailDTO sendEmailDto = new SendEmailDTO();
        sendEmailDto.setFrom("krishengineerxyz@gmail.com");
        sendEmailDto.setTo("vidhuengineerxyz@gmail.com");
        sendEmailDto.setSubject("Welcome to Scaler");
        sendEmailDto.setBody("Welcome, we are very happy and excited to welcome you in the platform");

        //kafkaTemplate.send(event_category(topic), actual request(which have all the details of what we used during sending emails))
        kafkaTemplate.send("send_Email", objectMapper.writeValueAsString(sendEmailDto));
        return user;

        //kafka queue - [{send_Email, {"to" : "" , from : "" , "body}]

    }

    public Token login(String email, String password){
        //Before issuing the token, we will validate the email and return the valid user(i.e) optionalUser
        Optional<User> optionalUser = userRepository.findByEmail(email);

        //if the user is not present
        if(optionalUser.isEmpty()){
            //we can throw the exception by creating userNotFoundException or throw RunTimeException or return null
            return null;
        }
        //if user exists
        User user = optionalUser.get();

        /*Then validate the password, once user exist
        BCrypt gives a new hash every time even for the same string(password), so can't do bCryptPasswordEncoder.encode(password) == optionalUser.get().getHashedPassword()
        So, we use verify or matches method
        bCryptPasswordEncoder.matches(login password, saved password of user during signup)*/
        //if bCryptPasswordEncoder is not match
        if(!bCryptPasswordEncoder.matches(password, user.getHashedPassword())){
            //if password is invalid, then we can throw the password invalid exception or return null
            return null;
        }

        /*once password also valid, then create the token
        Now, create the token – we need tokenValue, user, expirtyAt…User we already know, now set the tokenValue, expirtyAt…
        For business logic, we need how much time you need a token for
        expiryAt = today + 30 days*/
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryAtAfterThirtyDays = currentDate.plusDays(30);

        //we have to store the expiryAt in Date object...so convert localDate object to Date object
        LocalDateTime currentDateTime = expiryAtAfterThirtyDays.atStartOfDay();
        ZonedDateTime zonedDateTime = currentDateTime.atZone(ZoneId.systemDefault());
        Date expiryAtAfterThriyDaysDate = Date.from(zonedDateTime.toInstant());

        Token token = new Token();
        //set the expiryAt, user
        token.setExpiryAt(expiryAtAfterThriyDaysDate);
        token.setUser(user);

        //set the token value
        //JWT -> A, B, C -> https://github.com/jwtk/jjwt -> see below
       /*String jwtToken = Jwts.builder()
                .setSubject("user@example.com")
                .claim("role", "admin")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
        token.setTokenValue(jwtToken);*/

        //But, now let set the random token value here
        token.setTokenValue(RandomStringUtils.randomAlphanumeric(128));

        //finally save the token in database
        Token savedToken = tokenRepository.save(token);
        return savedToken;
    }

    public void logout(String tokenValue){
        //For logout, the tokenValue should delete
        //Here, first check the conditon, whether the tokenValue is false(i.e) token is valid
        Optional<Token> optionalToken = tokenRepository.findByTokenValueAndDeletedEquals(tokenValue, false);

        if(optionalToken.isEmpty()){
            //return token doesn't exist exception
            return;
        }
        //if optionalToken is exist
        Token token = optionalToken.get();
        //make the isDeleted = true (i.e) token becomes invalid -> softDelete
        token.setDeleted(true);//soft-delete
        tokenRepository.save(token);
        return;

    }

    /*public UserDTO validateToken(String tokenValue) {
    }*/

    //To validate the token, from service, it completely sends the user, but controller,
    // it response back to the client as UserDTO...So, in UserDTO we write some conditons
    public User validateToken(String tokenValue){
        //Goal: Check in the database, whether the tokenValue is exist, deleted = true (still token is valid), expiryTime is not after 30 days(take currentDate (i.e) new Date())
        Optional<Token> optionalToken = tokenRepository.findByTokenValueAndDeletedEqualsAndExpiryAtGreaterThan(tokenValue, false, new Date());

        if(optionalToken.isEmpty()){
            //token is invalid
            return null;
        }
        Token token = optionalToken.get();//Get the token
        User user = token.getUser();//get the user from token(which is valid user)
        return user;


    }


}
