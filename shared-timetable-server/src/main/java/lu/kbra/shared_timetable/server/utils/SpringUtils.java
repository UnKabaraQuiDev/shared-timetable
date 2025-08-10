package lu.kbra.shared_timetable.server.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.pcy113.pclib.PCUtils;

public class SpringUtils {

	public static boolean extractFile(String inJarPath, File configDir, String configFileName) {
		try {
			ClassPathResource resource = new ClassPathResource(inJarPath);

			Path targetPath = Path.of(new File(configDir, configFileName).getPath());

			Files.createDirectories(targetPath.getParent());

			if (Files.exists(targetPath)) {
				return false;
			}

			try (InputStream inputStream = resource.getInputStream()) {
				Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
			}

			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String hash(String str) {
		return PCUtils.hashString(str, "SHA-256");
	}

	public static void notFound(boolean b, String string) {
		if (b) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, string);
		}
	}

	public static void notFound(boolean b, String string, Runnable else_) {
		if (b) {
			else_.run();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, string);
		}
	}

	public static void badRequest(boolean b, String string) {
		if (b) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, string);
		}
	}

	public static void badRequest(boolean b, String string, Runnable else_) {
		if (b) {
			else_.run();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, string);
		}
	}

	public static void internalServerError(boolean b, String string) {
		if (b) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, string);
		}
	}

	public static void internalServerError(boolean b, String string, Runnable else_) {
		if (b) {
			else_.run();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, string);
		}
	}

	public static void forbidden(String string) {
		throw new ResponseStatusException(HttpStatus.FORBIDDEN, string);
	}

	public static void forbidden(boolean b, String string) {
		if (b) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, string);
		}
	}

	public static void forbidden(boolean b, String string, Runnable else_) {
		if (b) {
			else_.run();
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, string);
		}
	}

	public static void unauthorized(boolean b, String string) {
		if (b) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, string);
		}
	}

	public static void unauthorized(boolean b, String string, Runnable else_) {
		if (b) {
			else_.run();
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, string);
		}
	}

	public static boolean validString(String s) {
		return s != null && !s.isEmpty() && !s.isBlank();
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> getProxiedInterface(Class<?> clazz, Class<T> class1) {
		for (Class<?> iface : clazz.getInterfaces()) {
			if (class1.isAssignableFrom(iface) && iface != class1) {
				return (Class<? extends T>) iface;
			}
		}

		return null;
	}

	public static <T> Class<?> getProxiedClass(T type) {
		final Class<?> clazz = AopUtils.getTargetClass(type);
		if (clazz == Class.class)
			return null;
		return clazz;
	}

	public static boolean isContextUser() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return false;
		}
		return true;
	}

	public static boolean isAnonymous() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.isAuthenticated() && auth instanceof AnonymousAuthenticationToken;
	}

	public static UserData getContextUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return null;
		}
		return (UserData) auth.getPrincipal();
	}

	public static Authentication setContextUser(UserData user) {
		final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null,
				AuthorityUtils.createAuthorityList("ANONYMOUS"));
		SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
	}
}
