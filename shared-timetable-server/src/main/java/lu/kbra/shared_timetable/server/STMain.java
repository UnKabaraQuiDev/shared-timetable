package lu.kbra.shared_timetable.server;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class STMain {

	public static final File CONFIG_DIR = new File(System.getProperty("user.dir"), "/.config/shared-timetable-server/");

	public static void main(String[] args) {
		if (!CONFIG_DIR.exists()) {
			CONFIG_DIR.mkdirs();
		}
		
		SpringApplication.run(STMain.class, args);
	}

}
