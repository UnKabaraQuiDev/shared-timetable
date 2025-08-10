package lu.kbra.shared_timetable.server.db;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lu.kbra.shared_timetable.server.utils.SpringUtils;
import lu.pcy113.pclib.db.AbstractDBTable;
import lu.pcy113.pclib.db.AbstractDBView;
import lu.pcy113.pclib.db.DataBase;

@Service
public class TableProxyService {

	private static final Logger LOGGER = Logger.getLogger(TableProxyService.class.getName());

	public static TableProxyService INSTANCE;

	private final Map<Class<?>, DataBase> databases = new HashMap<>();
	private final Map<Class<?>, AbstractDBTable<?>> tables = new HashMap<>();
	private final Map<Class<?>, AbstractDBView<?>> views = new HashMap<>();

	public TableProxyService(ApplicationContext context) {
		for (DataBase db : context.getBeansOfType(DataBase.class).values()) {
			Class<?> clazz = SpringUtils.getProxiedClass(db);
			if (!STBase.class.isAssignableFrom(clazz)) {
				LOGGER.warning("Base `" + clazz.getSimpleName() + "` is NOT of type STBase. This is deprecated/discouraged.");
			}
			this.databases.put(clazz, db);
		}

		for (AbstractDBTable<?> table : context.getBeansOfType(AbstractDBTable.class).values()) {
			final Class<?> clazz = SpringUtils.getProxiedClass(table);

			this.tables.put(clazz, table);
		}

		for (AbstractDBView<?> view : context.getBeansOfType(AbstractDBView.class).values()) {
			final Class<?> clazz = SpringUtils.getProxiedClass(view);

			this.views.put(clazz, view);
		}

		INSTANCE = this;
	}

	@PostConstruct
	public void insertDefaults() throws Throwable {
	}

	@SuppressWarnings("unchecked")
	public <T extends DataBase> T database(Class<T> clazz) {
		return (T) databases.get(clazz);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractDBTable<?>> T table(Class<T> clazz) {
		return (T) tables.get(clazz);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractDBView<?>> T view(Class<T> clazz) {
		return (T) views.get(clazz);
	}

}
