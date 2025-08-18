package lu.kbra.shared_timetable.server.db.tables;

import jakarta.annotation.PostConstruct;
import lu.pcy113.pclib.db.AbstractDBTable;
import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;

public class STTable<T extends DataBaseEntry> extends DataBaseTable<T> {

	public STTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBTable<T>> tableClass) {
		super(dataBase, dbEntryUtils, tableClass);
	}

	public STTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public STTable(DataBase dataBase) {
		super(dataBase);
	}

	@PostConstruct
	public void init() {
		super.create().run();
	}

}
