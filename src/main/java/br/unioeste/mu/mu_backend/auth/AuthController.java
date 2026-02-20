package br.unioeste.mu.mu_backend.auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final LoginAttemptLimiter loginAttemptLimiter;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          LoginAttemptLimiter loginAttemptLimiter) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.loginAttemptLimiter = loginAttemptLimiter;
    }

    @Operation(summary = "Authenticate user and generate JWT", security = {})
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        String username = req.getUsername();
        loginAttemptLimiter.checkAllowed(ipAddress, username);

        Authentication authentication;
        try {
            authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, req.getPassword())
            );
        } catch (AuthenticationException ex) {
            loginAttemptLimiter.registerFailure(ipAddress, username);
            throw ex;
        }

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        String token = jwtService.generateToken(username, role);
        loginAttemptLimiter.registerSuccess(ipAddress, username);

        return new LoginResponse(token);
    }
}
