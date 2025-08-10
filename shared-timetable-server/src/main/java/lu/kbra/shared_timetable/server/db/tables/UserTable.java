package lu.kbra.shared_timetable.server.db.tables;

import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.utils.QueryBuilder;
import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;

@Component
public class UserTable extends DataBaseTable<UserData> {

	public UserTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public UserData getUser(String name, String password) {
		return super.query(QueryBuilder.select(this).where("username", "=", name).and("password", "=", UserData.hash(password))).run().stream().findFirst().orElse(null);
	}

	public UserData getUser(String name) {
		return super.loadUnique(new UserData(name, null)).run();
	}

	public UserData getUser(long id) {
		return super.load(new UserData(id)).run();
	}

	public UserData createUser(String name, String password) {
		return super.insertAndReload(new UserData(name, UserData.hash(password))).run();
	}

}
