package com.springApp.controller;

import com.springApp.dao.UserRepository;
import com.springApp.entities.User;
import com.springApp.security.CurrentUser;
import com.springApp.security.JwtTokenProvider;
import com.springApp.services.JwtAuthenticationResponse;
import com.springApp.services.UserService;
import com.springApp.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("users")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserRepository userRepository;

//    @Autowired
//    MailSender mailSender;


    @CrossOrigin
    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody User newUser) {
        if (userService.find(newUser.getUsername()) != null) {
            logger.error("username Already exist " + newUser.getUsername());
            return new ResponseEntity(
                    new CustomErrorType("user with username " + newUser.getUsername() + "already exist "),
                    HttpStatus.CONFLICT);
        }
        newUser.setRole("USER");

        return new ResponseEntity<User>(userService.save(newUser), HttpStatus.CREATED);
    }

    // this is the login api/service
//    @CrossOrigin
//    @RequestMapping("/login")
//    public Principal user(Principal principal) {
//        logger.info("user logged "+principal);
//        return principal;
//    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody User loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }


    @RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
    public ResponseEntity<?> returnAllUsers(@CurrentUser User user){
        logger.info(user.toString());
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpSession session) {
        session.invalidate();
    }

//    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
//    public ResponseEntity<?> sendEmailService(@RequestBody Object body){
//
//        Gson gson = new Gson();
//        JsonElement jsonElement = gson.toJsonTree(body);
//        JsonObject jsonObject = jsonElement.getAsJsonObject();
//        String from = jsonObject.get("from").getAsString();
//        String to = jsonObject.get("to").getAsString();
//        String mailBody = jsonObject.get("subject").getAsString();
//        String subject = jsonObject.get("body").getAsString();
//
//        mailSender.sendEmail(from,to,subject,mailBody);
//
//        return ResponseEntity.ok("success");
//    }


}
