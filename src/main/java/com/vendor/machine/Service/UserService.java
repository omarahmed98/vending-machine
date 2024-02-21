package com.vendor.machine.Service;
import com.vendor.machine.Securtiy.JwtService;

import jakarta.servlet.http.HttpServletRequest;

import com.vendor.machine.DTO.RegisterUserDTO;
import com.vendor.machine.DTO.UpdateUserDTO;
import com.vendor.machine.Entity.User;
import com.vendor.machine.Mapper.UserMapper;
import com.vendor.machine.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;



@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private String registrationTokenExpirationDate = "7200";
    static String secret = "qVnkSD4Yq1OYNhh5YjgLKIQrA2sAUc9t6/ZOy1ShzC14xZsYW4i6QmmkYv1pSaajJuOk5g+So6lh4lJi+3BeI6AsWufcaoX";

    public boolean existsByUsername(String username)
    {
        Optional<User> user = userRepository.findByUsername(username);
        boolean finder = false;
        if(user.isPresent())
        {
            finder = true;
        }
        return finder;
    }
    public User loadUserByUsername(String username)
    {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void register (RegisterUserDTO userDto)
    {
        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encryptedPassword);

        // Create user entity
        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .deposit(0L)
                .role(userDto.getRole())
                .build();

        // Save user to the database
        userRepository.save(user);
    }
    public String login(String username, String password) {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        String token = JwtService.generateToken(username , Long.parseLong(registrationTokenExpirationDate),secret);
        log.info("token Details{}",token);
        return token;
    }

    public int updateUser(HttpServletRequest request, UpdateUserDTO updateUserDTO) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User fulluser = userRepository.findByUsername(username).orElse(null);
        User user = userMapper.toUser(updateUserDTO);
        user.setId(fulluser.getId());
        user.setPassword(fulluser.getPassword());
        user.setDeposit(fulluser.getDeposit());
        return userRepository
                .updateUserData(user);         
    }
}
