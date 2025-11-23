package com.example.buggyapp.controller;

import com.example.buggyapp.model.User;
import com.example.buggyapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// DEFECT: Missing CORS configuration or overly permissive CORS
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // DEFECT: Allowing all origins
public class UserController {

    @Autowired
    private UserService userService;

    // DEFECT: Exposing all user data including passwords
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // DEFECT: No authentication/authorization check
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // DEFECT: Returning null instead of 404
        return userService.getUserById(id);
    }

    // DEFECT: Mass assignment vulnerability - accepting raw user object
    @PostMapping
    public User createUser(@RequestBody User user) {
        // DEFECT: No input validation
        // DEFECT: No rate limiting
        return userService.createUser(
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );
    }

    // DEFECT: Path traversal vulnerability
    @GetMapping("/profile/image")
    public ResponseEntity<byte[]> getUserImage(@RequestParam String filename) throws IOException {
        // DEFECT: No path validation - allows directory traversal
        File file = new File("/tmp/images/" + filename);

        // DEFECT: Not checking if file exists or is a directory
        byte[] imageBytes = Files.readAllBytes(file.toPath());

        return ResponseEntity.ok(imageBytes);
    }

    // DEFECT: Command injection vulnerability
    @GetMapping("/export")
    public String exportUsers(@RequestParam String format) throws IOException {
        // DEFECT: Executing system command with user input
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("export_users.sh " + format);

        return "Export completed";
    }

    // DEFECT: Information disclosure - exposing internal paths
    @GetMapping("/logs")
    public String getLogs(@RequestParam String logFile) throws IOException {
        // DEFECT: Path traversal vulnerability
        // DEFECT: Reading arbitrary files from filesystem
        String content = new String(Files.readAllBytes(Paths.get(logFile)));
        return content;
    }

    // DEFECT: XSS vulnerability - not sanitizing output
    @GetMapping("/search")
    public String searchUsers(@RequestParam String query) {
        // DEFECT: Reflecting user input without sanitization
        return "<html><body>Search results for: " + query + "</body></html>";
    }

    // DEFECT: Missing authentication
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        try {
            // DEFECT: No authorization check - any user can delete
            userService.deleteUser(id);
        } catch (Exception e) {
            // DEFECT: Swallowing exception
            e.printStackTrace();
        }
    }

    // DEFECT: Exposing sensitive session data
    @GetMapping("/session")
    public String getSessionInfo(HttpServletRequest request) {
        // DEFECT: Exposing session ID
        return "Session ID: " + request.getSession().getId();
    }

    // DEFECT: Mass assignment vulnerability
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        // DEFECT: Allowing update of any field including sensitive ones
        // DEFECT: No validation that id matches user.id
        return userService.updateUser(
                id,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getActive(),
                user.role,
                user.getCreatedAt()
        );
    }

    // DEFECT: No rate limiting on login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        // DEFECT: Timing attack vulnerability
        // DEFECT: No brute force protection
        if (userService.authenticateUser(username, password)) {
            // DEFECT: Exposing password in response
            return ResponseEntity.ok("Login successful for user: " + username + " with password: " + password);
        }

        // DEFECT: Information disclosure - confirming username exists
        return ResponseEntity.status(401).body("Invalid credentials for user: " + username);
    }

    // DEFECT: Insecure direct object reference
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileId) throws IOException {
        // DEFECT: No access control check
        // DEFECT: Using user input directly in file path
        FileInputStream fis = new FileInputStream("/var/data/user_files/" + fileId);

        // DEFECT: Not closing the stream properly
        byte[] data = fis.readAllBytes();

        return ResponseEntity.ok(data);
    }

    // DEFECT: Verbose error messages
    @GetMapping("/debug/{id}")
    public ResponseEntity<String> debugUser(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);

            // DEFECT: Potential NullPointerException
            String debug = user.toString();

            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            // DEFECT: Exposing stack trace to client
            return ResponseEntity.status(500).body("Error: " + e.getMessage() + "\nStack: " + e.getStackTrace().toString());
        }
    }

    // DEFECT: SQL Injection via service layer
    @GetMapping("/searchByEmail")
    public List<User> searchByEmail(@RequestParam String email) {
        // DEFECT: Passing unsanitized input to repository
        return userService.getAllUsers(); // Simplified for compilation
    }

    // DEFECT: Hardcoded secret key
    @GetMapping("/token")
    public String generateToken(@RequestParam String username) {
        String secretKey = "mySecretKey123"; // DEFECT: Hardcoded secret

        // DEFECT: Weak token generation
        return username + "_" + secretKey + "_" + System.currentTimeMillis();
    }

    // DEFECT: Unvalidated redirect
    @GetMapping("/redirect")
    public void redirect(@RequestParam String url, jakarta.servlet.http.HttpServletResponse response) throws IOException {
        // DEFECT: Open redirect vulnerability
        response.sendRedirect(url);
    }
}
