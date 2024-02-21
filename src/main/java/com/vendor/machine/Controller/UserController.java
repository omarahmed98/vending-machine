package com.vendor.machine.Controller;

import com.vendor.machine.DTO.ApiResponse;
import com.vendor.machine.DTO.RegisterUserDTO;
import com.vendor.machine.DTO.SignInDTO;
import com.vendor.machine.DTO.UpdateUserDTO;
import com.vendor.machine.Entity.User;
import com.vendor.machine.Repository.UserRepository;
import com.vendor.machine.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    @PostMapping("/register")
    @Operation(summary = "register user")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO userDto) {
        // Check if username is already taken
        if (userService.existsByUsername(userDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        userService.register(userDto);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/signin")
    @Operation(summary = "log in user")
    public ResponseEntity<?> signInUser(@RequestBody SignInDTO signInRequest) {
        // Find user by username
        User user = userRepository.findByUsername(signInRequest.getUsername())
                .orElse(null);
        try {
            if (user == null) {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"No username found"),HttpStatus.NOT_FOUND);
            }
            String token = userService.login(signInRequest.getUsername(), signInRequest.getPassword());
            return new ResponseEntity<>(new ApiResponse<>(true,token,"Success"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Internal server error"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Update username and/or role of logged user")
    @PutMapping("")
    public ResponseEntity<?> updateUserData(HttpServletRequest request,@RequestBody UpdateUserDTO updateUserDTO) {
        try {
            int response = userService.updateUser(request, updateUserDTO);
            if(response!=0)
            {
                return new ResponseEntity<>(new ApiResponse<>(true, updateUserDTO.getUsername(),"Success"),HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new ApiResponse<>(false,null,"No rows affected"),HttpStatus.NOT_MODIFIED);
            }
        }
        catch(Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Internal server error"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "delete the user entry using id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        User user = userRepository.findById(id).orElse(null);
        try {
            if(user == null)
                {
                    return new ResponseEntity<>(new ApiResponse<>(false,null,"No users found to delete"),HttpStatus.NOT_FOUND);
                }
            else {
                    userRepository.deleteById(id);
                    return new ResponseEntity<>(new ApiResponse<>(true,null,"Successfully deleted the user"),HttpStatus.OK);
                }
        }
        catch(Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false,null,"Internal server error"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
