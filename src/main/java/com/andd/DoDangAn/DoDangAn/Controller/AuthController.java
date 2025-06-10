package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.Util.JwtUtil;
import com.andd.DoDangAn.DoDangAn.models.User;
import com.andd.DoDangAn.DoDangAn.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            if (error.equals("PleaseLogin")) {
                model.addAttribute("error", "Vui lòng đăng nhập để tiếp tục!");
            } else if (error.equals("TokenExpired")) {
                model.addAttribute("error", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
            } else if (error.equals("InvalidToken")) {
                model.addAttribute("error", "Token không hợp lệ, vui lòng đăng nhập lại!");
            } else {
                model.addAttribute("error", "Lỗi xác thực, vui lòng thử lại!");
            }
        }
        return "login";
    }

    @PostMapping("/user/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            Model model, HttpSession session
    ) {
        try {
            if (isValidUser(username, password)) {
                Optional<User> userOptional = userRepository.findByUsername(username);
                String fullName = userOptional.isPresent() ? userOptional.get().getFullname() : username;
                if (fullName == null) fullName = username;

                String jwt = jwtUtil.generateToken(username, fullName);
                logger.info("User {} logged in successfully", username);


                Cookie jwtCookie = new Cookie("jwt", jwt);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge((int) (jwtUtil.getExpiration() / 1000));
                response.addCookie(jwtCookie);

                User user = userOptional.get();
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> "ADMIN".equals(role.getName()));

                if (isAdmin) {
                    session.setAttribute("loggedInUser", user); // Lưu thông tin người dùng vào session
                    model.addAttribute("loggedInUser", user); // Truyền thông tin người dùng vào model
                    logger.info("Chuyển hướng người dùng {} đến trang admin", username);
                    return "redirect:/admin/home";
                } else {
                    session.setAttribute("loggedInUser", user); // Lưu thông tin người dùng vào session
                    model.addAttribute("loggedInUser", user); // Truyền thông tin người dùng vào model
                    logger.info("Chuyển hướng người dùng {} đến trang user", username);
                    return "redirect:/user/home";
                }
            } else {
                Optional<User> userOptional = userRepository.findByUsername(username);
                if (!userOptional.isPresent()) {
                    logger.warn("Login failed: User {} not found", username);
                    model.addAttribute("error", "Tài khoản không tồn tại!");
                } else {
                    logger.warn("Login failed: Incorrect password for user {}", username);
                    model.addAttribute("error", "Mật khẩu không đúng!");
                }
                return "login";
            }
        } catch (Exception e) {
            logger.error("Error during login for user {}: {}", username, e.getMessage());
            model.addAttribute("error", "Lỗi hệ thống, vui lòng thử lại sau!");
            return "login";
        }
    }

    private boolean isValidUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            return false;
        }
        User user = userOptional.get();
        if (user.getPassword() == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }
}