package lu.kbra.shared_timetable.server.db.datas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lu.kbra.shared_timetable.server.Permission;
import lu.kbra.shared_timetable.server.db.typ.ListType;
import lu.kbra.shared_timetable.server.utils.SpringUtils;
import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.Nullable;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.autobuild.column.Unique;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.rescue_rush.spring.ws_ext.UserID;

public class UserData implements UserDetails, UserID, DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private long id;

	@Column(length = 24)
	@Unique
	private String username;

	@Column(length = 64)
	private String password;

	@Column(length = 64)
	@Nullable
	@Unique(1)
	private String token;

	@Column(type = ListType.class)
	private List<Permission> permissions = new ArrayList<>();

	public UserData() {
	}

	public UserData(long id) {
		this.id = id;
	}

	public UserData(long id, String username, String password, List<Permission> permissions) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.permissions = permissions;
	}

	public UserData(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return permissions.stream().map(permission -> (GrantedAuthority) () -> permission.name().toLowerCase(Locale.ROOT)).toList();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void regenToken() {
		setToken(UserData.hash(username + password + System.currentTimeMillis()));
	}

	public static String hash(String pw) {
		return SpringUtils.hash(pw);
	}

	@Override
	public String toString() {
		return "UserData [id=" + id + ", username=" + username + ", password=" + password + ", token=" + token + ", permissions="
				+ permissions + "]";
	}

}
