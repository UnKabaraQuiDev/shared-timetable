package lu.kbra.shared_timetable.server.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.WebSocketSession;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.db.tables.UserTable;
import lu.kbra.shared_timetable.server.utils.SpringUtils;

@Service
public class UserService {

	@Autowired
	private UserTable userTable;

	public UserData authenticate(String name, String password) {
		if (!SpringUtils.validString(name) || !SpringUtils.validString(password)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or password.");
		}

		final UserData ud = userTable
				.byNameAndPassword(name, password)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or invalid credentials."));

		regenToken(ud);

		return ud;
	}

	public UserData create(String name, String password) {
		if (userTable.existsName(name)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already in use.");
		}

		if (!SpringUtils.validString(name) || !SpringUtils.validString(password)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or password.");
		}

		final UserData ud = userTable.createUser(name, password);

		regenToken(ud);

		return ud;
	}

	private void regenToken(UserData ud) {
		ud.regenToken();
		userTable.updateUserData(ud);
	}

	public Optional<UserData> cookies(final HttpServletRequest request) {
		return cookies(request.getCookies());
	}

	public Optional<UserData> cookies(final Cookie[] cookies) {
		for (Cookie cookie : cookies) {
			if ("token".equals(cookie.getName())) {
				return token(cookie.getValue());
			}
		}

		return null;
	}

	public Optional<UserData> token(final String token) {
		return userTable.byToken(token);
	}

	public Optional<UserData> auth(final String auth) {
		if (!auth.startsWith("Token ")) {
			return null;
		}
		final String token = auth.substring("Token ".length());

		return token(token);
	}

	public Optional<UserData> session(WebSocketSession session) {
		return auth(session.getHandshakeHeaders().get("Authorization").get(0));
	}

}
