package lu.kbra.shared_timetable.server.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.services.UserService;
import lu.rescue_rush.spring.ws_ext.annotations.AllowAnonymous;

@CrossOrigin
@RestController
@RequestMapping(value = "/user")
public class UserEndpoint {

	@Autowired
	private UserService userService;

	@AllowAnonymous
	@PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
	public UserAuthResponse login(@RequestBody UserAuthRequest auth, HttpServletResponse response) {
		final UserData ud = userService.authenticate(auth.name(), auth.password());

		userService.assignAuth(ud);
		
		response.addCookie(userService.createAuthCookie(ud));

		return new UserAuthResponse(ud.getToken());
	}

	@AllowAnonymous
	@PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
	public UserAuthResponse register(@RequestBody UserAuthRequest auth, HttpServletResponse response) {
		final UserData ud = userService.create(auth.name(), auth.password());

		userService.assignAuth(ud);

		response.addCookie(userService.createAuthCookie(ud));

		return new UserAuthResponse(ud.getToken());
	}

	public record UserAuthRequest(String name, String password) {
	}

	public record UserAuthResponse(String token) {
	}

}
