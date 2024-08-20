package lk.ijse.SpringProject.controller;

import lk.ijse.SpringProject.dto.AuthDto;
import lk.ijse.SpringProject.dto.ResponseDto;
import lk.ijse.SpringProject.dto.UserDto;
import lk.ijse.SpringProject.service.impl.UserServiceimpl;
import lk.ijse.SpringProject.util.JwtUtil;
import lk.ijse.SpringProject.util.Varlist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceimpl userService;
    private final ResponseDto responseDTO;

    //constructor injection
    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserServiceimpl userService,ResponseDto responseDTO) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.responseDTO = responseDTO;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResponseDto> authenticate(@RequestBody UserDto userDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDto(Varlist.Unauthorized, "Invalid Credentials", e.getMessage()));
        }

        UserDto loadedUser = userService.loadUserDetailsByUsername(userDTO.getEmail());
        if (loadedUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDto(Varlist.Conflict, "Authorization Failure! Please Try Again", null));
        }

        String token = jwtUtil.generateToken(loadedUser);
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDto(Varlist.Conflict, "Authorization Failure! Please Try Again", null));
        }

        AuthDto authDTO = new AuthDto();
        authDTO.setEmail(loadedUser.getEmail());
        authDTO.setToken(token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(Varlist.Created, "Success", authDTO));
    }
    @PostMapping(value = "/register")
    public ResponseEntity<ResponseDto> registerUser(@RequestBody UserDto userDTO) {
        try {
            int res = userService.saveUser(userDTO);
            switch (res) {
                case Varlist.Created -> {
                    String token = jwtUtil.generateToken(userDTO);
                    AuthDto authDTO = new AuthDto();
                    authDTO.setEmail(userDTO.getEmail());
                    authDTO.setToken(token);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDto(Varlist.Created, "Success", authDTO));
                }
                case Varlist.Not_Acceptable -> {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponseDto(Varlist.Not_Acceptable, "Email Already Used", null));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponseDto(Varlist.Bad_Gateway, "Error", null));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(Varlist.Internal_Server_Error, e.getMessage(), null));
        }
    }
}
