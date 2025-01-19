package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.auth.user.UserDTO;
import mindpath.core.service.user.UserEntityService;
import mindpath.core.utility.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(APIRouters.USER_ROUTER)
public class UserController {

    private final UserEntityService userEntityService;

    public UserController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    @GetMapping("/filter")
    public CustomerResponse<List<UserDTO>> filterUsers(
            @RequestParam(value = "fullName", required = false) final String fullName,
            @RequestParam(value = "email", required = false) final String email,
            @RequestParam(value = "role", required = false) final String role,
            @RequestParam(value = "phoneNumber", required = false) final String phoneNumber,
            @RequestParam(value = "isEnabled", required = false) final Boolean isEnabled
    ){
        return new CustomerResponse<>(userEntityService.filterUser(fullName, email, role, phoneNumber, isEnabled), HttpStatus.OK);
    }
}
