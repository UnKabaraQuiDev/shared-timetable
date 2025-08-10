package lu.kbra.shared_timetable.server.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lu.kbra.shared_timetable.server.services.UserService;

@RestController("user")
public class UserEndpoint {

	@Autowired
	private UserService userService;
	
	@PostMapping(path = "auth", consumes = "application/json", produces = "application/json")
	public UserAuthResponse auth(@RequestBody UserAuthRequest auth, HttpServletRequest request) {
		final UserData userData = userService.authenticate(auth.name(), auth.password());
		
		request.getSession().setAttribute("user", auth.name());
	}

	public record UserAuthRequest(String name, String password) {
	}
	
	public record UserAuthResponse(String token) {
	}
	
}
