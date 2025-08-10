package lu.kbra.shared_timetable.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.kbra.shared_timetable.server.db.tables.UserTable;
import lu.kbra.shared_timetable.server.endpoints.UserData;

@Service
public class UserService {

	@Autowired
	private UserTable userTable;

	public UserData authenticate(String name, String password) {
		return null;
	}
	
}
