package lu.kbra.shared_timetable.server.db.tables;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.server.db.datas.UserData;
import lu.kbra.shared_timetable.server.utils.QueryBuilder;
import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;

@Component
public class UserTable extends STTable<UserData> {

	public UserTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	// @Cacheable("user.token")
	public Optional<UserData> byToken(String token) {
		Logger.getLogger(UserTable.class.getName()).info("Querying user by token: " + token);
		return super.query(QueryBuilder.select(this).where("token", "=", token).firstOptional()).run();
	}

	@Cacheable(value = "user.name-pass", key = "#name + '-' + lu.kbra.shared_timetable.server.db.datas.UserData.hash(#password)")
	public Optional<UserData> byNameAndPassword(String name, String password) {
		return super.query(
				QueryBuilder.select(this).where("username", "=", name).and("password", "=", UserData.hash(password)).firstOptional()).run();
	}

	@Cacheable("user.name")
	public Optional<UserData> byName(String name) {
		return Optional.ofNullable(super.loadUnique(new UserData(name, null)).run());
	}

	@Cacheable("user.id")
	public Optional<UserData> byId(long id) {
		return Optional.ofNullable(super.load(new UserData(id)).run());
	}

	@Cacheable(value = "user.name-pass", key = "#ud.username + '-' + lu.kbra.shared_timetable.server.db.datas.UserData.hash(#ud.password)")
	public UserData createUser(String name, String password) {
		return super.insertAndReload(new UserData(name, UserData.hash(password))).run();
	}

	public boolean existsName(String name) {
		return super.existsUnique(new UserData(name, null)).run();
	}

	@Caching(
			evict =
			{
					@CacheEvict(value = "user.name-pass", key = "#name + '-' + #password"),
					@CacheEvict(value = "user.token", key = "#ud.token"),
					@CacheEvict(value = "user.name", key = "#ud.username"),
					@CacheEvict(value = "user.id", key = "#ud.id") })
	public UserData updateUserData(UserData ud) {
		return super.update(ud).run();
	}

}
