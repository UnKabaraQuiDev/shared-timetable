package lu.kbra.shared_timetable.server.db;

import org.springframework.stereotype.Component;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.annotations.base.DB_Base;

@Component
@DB_Base(name = "shared_timetable")
public class STBase extends DataBase {

	public STBase(DataBaseConnector connector) {
		super(connector);
	}

}
