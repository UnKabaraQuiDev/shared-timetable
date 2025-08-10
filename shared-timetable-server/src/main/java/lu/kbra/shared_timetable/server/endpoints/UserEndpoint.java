package lu.kbra.shared_timetable.server.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.services.UserService;

@RestController("user")
public class UserEndpoint {

	@Autowired
	private UserService userService;

	@PostMapping(path = "login", consumes = "application/json", produces = "application/json")
	public UserAuthResponse login(@RequestBody UserAuthRequest auth, HttpServletRequest request) {
		final UserData ud = userService.authenticate(auth.name(), auth.password());

		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(ud, null, AuthorityUtils.createAuthorityList());
		SecurityContextHolder.getContext().setAuthentication(token);

		return new UserAuthResponse(ud.getToken());
	}
	
	@PostMapping(path = "register", consumes = "application/json", produces = "application/json")
	public UserAuthResponse register(@RequestBody UserAuthRequest auth, HttpServletRequest request) {
		final UserData ud = userService.create(auth.name(), auth.password());

		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(ud, null, AuthorityUtils.createAuthorityList());
		SecurityContextHolder.getContext().setAuthentication(token);

		return new UserAuthResponse(ud.getToken());
	}

	public record UserAuthRequest(String name, String password) {
	}

	public record UserAuthResponse(String token) {
	}

}
