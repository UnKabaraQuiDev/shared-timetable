package lu.kbra.shared_timetable.server.configs.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.server.db.type.ListType;
import lu.pcy113.pclib.db.utils.BaseDataBaseEntryUtils;

@Component
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SpringDataBaseEntryUtils extends BaseDataBaseEntryUtils {

	public SpringDataBaseEntryUtils() {
		// java types -----
		typeMap.put(List.class, col -> new ListType());
		typeMap.put(ArrayList.class, col -> new ListType());
		typeMap.put(LinkedList.class, col -> new ListType());

		// native types -----
		typeMap.put(ListType.class, col -> new ListType());

	}

}
