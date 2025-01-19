package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.auth.login.LogInDTO;
import mindpath.core.domain.auth.login.LogInResponseDTO;
import mindpath.core.domain.auth.register.RegisterStudentDTO;
import mindpath.core.domain.auth.register.RegisterUserDTO;
import mindpath.core.service.auth.AuthService;
import mindpath.core.service.firstbasestorage.AzureStorageService;
import mindpath.core.utility.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping(APIRouters.AUTH_ROUTER)
public class AuthController {

    private final AuthService authService;
    private final AzureStorageService azureStorageService;

    public AuthController(AuthService authService, AzureStorageService azureStorageService) {
        this.authService = authService;
        this.azureStorageService = azureStorageService;
    }

    @Operation(summary = "Register a new teacher or super teacher", description = "Registers a new user based on the role provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public CustomerResponse<String> registerTeachers(
            @Parameter(description = "Role of the user to register", required = true) @RequestParam String roleName,
            @RequestBody @Valid RegisterUserDTO registerUserDTO) {
        return new CustomerResponse<>(authService.registerTeachers(roleName, registerUserDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Register a new student", description = "Registers a new student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register/student")
    public CustomerResponse<String> registerStudents(
            @RequestBody @Valid RegisterStudentDTO registerStudentDTO
    ) {
        return new CustomerResponse<>(authService.registerStudents(registerStudentDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "User login", description = "Logs in a user and returns authentication details including access and refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = LogInResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid login credentials"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public CustomerResponse<LogInResponseDTO> logIn(
            @Parameter(description = "Login details", required = true) @NotNull @Valid @RequestBody final LogInDTO logInDTO) {
        return  new CustomerResponse<>(authService.logIn(logInDTO),HttpStatus.OK);
    }

    @Operation(summary = "Validate token", description = "Validates a given token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "400", description = "Invalid token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/validate/{token}")
    public CustomerResponse<String> validateToken(
            @Parameter(description = "Token to validate", required = true) @PathVariable("token") @NotNull final String token) {
        return  new CustomerResponse<>(authService.validateToken(token),HttpStatus.OK);
    }

    @Operation(summary = "Confirm account", description = "Confirms a user's account based on the confirmation token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid confirmation token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/confirm")
    public String activateAccount(
            @Parameter(description = "Confirmation token", required = true) @RequestParam("token") final String confirmationToken) {
        return authService.confirmAccount(confirmationToken);
    }

    @GetMapping("/forgot-password")
    public CustomerResponse<String> processForgotPassword(
            @Parameter(description = "Email address", required = true) @RequestParam("email") final String email) {
        return new CustomerResponse<>(authService.processForgotPassword(email),HttpStatus.OK);
    }

    @PutMapping("/reset-password")
    public CustomerResponse<String> resetPassword(
            @Parameter(description = "Token", required = true) @RequestParam("token") final String token,
            @Parameter(description = "Email address", required = true) @RequestParam("email") final String email,
            @Parameter(description = "New password", required = true) @RequestParam("password") final String password) {
        return new CustomerResponse<>(authService.resetPassword(token, email, password.replace(" ", "+")),HttpStatus.OK);
    }

}
