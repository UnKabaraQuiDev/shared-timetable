package lu.kbra.shared_timetable.server.utils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import lu.pcy113.pclib.db.autobuild.column.type.ColumnType;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;

public class QueryBuilder<T extends SQLQueryable<V>, V extends DataBaseEntry> implements SQLQuery.PreparedQuery<V> {

	private final T table;
	private final List<String> columns = new ArrayList<>();
	private final List<String> conditions = new ArrayList<>();
	private final List<Object> params = new ArrayList<>();

	private QueryBuilder(T table) {
		this.table = table;
	}

	public static <T extends SQLQueryable<V>, V extends DataBaseEntry> QueryBuilder<T, V> select(T table) {
		return new QueryBuilder<>(table);
	}

	public QueryBuilder<T, V> where(String column, String operator, Object value) {
		conditions.clear();
		addCondition(column, operator, value);
		return this;
	}

	public QueryBuilder<T, V> and(String column, String operator, Object value) {
		addCondition(column, operator, value);
		return this;
	}

	private void addCondition(String column, String operator, Object value) {
		conditions.add(column + " " + operator + " ?");
		columns.add(column);
		params.add(value);
	}

	@Override
	public String getPreparedQuerySQL(SQLQueryable<V> table) {
		String tableName = table.getQualifiedName();
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);
		if (!conditions.isEmpty()) {
			final StringJoiner joiner = new StringJoiner(" AND ");
			for (String cond : conditions) {
				joiner.add(cond);
			}
			sql.append(" WHERE ").append(joiner);
		}
		return sql.toString();
	}

	@Override
	public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
		final DataBaseEntryUtils dbEntryUtils = table.getDbEntryUtils();
		final Class<? extends SQLQueryable<V>> tableClass = table.getTargetClass();
		final Class<? extends DataBaseEntry> entryType = dbEntryUtils.getEntryType(tableClass);

		for (int i = 0; i < params.size(); i++) {
			final Field field = dbEntryUtils.getFieldFor(entryType, columns.get(i));
			final ColumnType columnType = dbEntryUtils.getTypeFor(field);

			columnType.store(stmt, i + 1, params.get(i));
		}
	}
}
