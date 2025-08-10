package lu.kbra.shared_timetable.server.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ClassPathResource;

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
	
}
