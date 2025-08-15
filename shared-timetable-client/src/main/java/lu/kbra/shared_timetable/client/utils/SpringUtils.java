package lu.kbra.shared_timetable.client.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.io.ClassPathResource;

import lu.pcy113.pclib.PCUtils;

public class SpringUtils {

	public static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

}
