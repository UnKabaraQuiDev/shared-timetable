package lu.kbra.shared_timetable.server.utils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.pcy113.pclib.builder.SQLBuilder;
import lu.pcy113.pclib.datastructure.pair.Pair;
import lu.pcy113.pclib.db.autobuild.column.type.ColumnType;
import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SinglePreparedQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;
import lu.pcy113.pclib.db.utils.SimpleTransformingQuery;

public class QueryBuilder<T extends SQLQueryable<V>, V extends DataBaseEntry> {

	private final T table;
	private Node root;
	private final List<Object> params = new ArrayList<>();
	private final List<String> paramColumns = new ArrayList<>();
	private int limit = SQLBuilder.ENTRY_LIMIT;
	private int offset = 0;
	private final List<Pair<String, String>> orderBy = new ArrayList<>();

	private QueryBuilder(T table) {
		this.table = table;
	}

	protected String getPreparedQuerySQL_(SQLQueryable<V> table) {
		final StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table.getQualifiedName());
		if (root != null) {
			sql.append(" WHERE ").append(root.toSQL());
		}
		if (!orderBy.isEmpty()) {
			sql.append(" ORDER BY ").append(orderBy.stream().map(p -> p.getKey() + " " + p.getValue()).collect(Collectors.joining(", ")));
		}
		if (limit > 0) {
			sql.append(" LIMIT ").append(limit);
		}
		if (offset > 0) {
			sql.append(" OFFSET ").append(offset);
		}
		return sql.toString();
	}

	protected void updateQuerySQL_(PreparedStatement stmt) throws SQLException {
		DataBaseEntryUtils dbEntryUtils = table.getDbEntryUtils();
		Class<? extends SQLQueryable<V>> tableClass = table.getTargetClass();
		Class<? extends DataBaseEntry> entryType = dbEntryUtils.getEntryType(tableClass);

		for (int i = 0; i < params.size(); i++) {
			Field field = dbEntryUtils.getFieldFor(entryType, paramColumns.get(i));
			ColumnType columnType = dbEntryUtils.getTypeFor(field);
			columnType.store(stmt, i + 1, params.get(i));
		}
	}

	public PreparedQuery<V> list() {
		return new PreparedQuery<V>() {

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				return getPreparedQuerySQL_(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				updateQuerySQL_(stmt);
			}

		};
	}

	public <B> TransformingQuery<V, B> transform(Function<List<V>, B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return new TransformingQuery<V, B>() {

			@Override
			public B transform(List<V> data) throws SQLException {
				return transformer.apply(data);
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				return getPreparedQuerySQL_(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				updateQuerySQL_(stmt);
			}

		};
	}

	public <B> RawTransformingQuery<V, B> rawTransform(Function<ResultSet, B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return new RawTransformingQuery<V, B>() {

			@Override
			public B transform(ResultSet rs) throws SQLException {
				return transformer.apply(rs);
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				return getPreparedQuerySQL_(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				updateQuerySQL_(stmt);
			}

		};
	}

	public TransformingQuery<V, Optional<V>> firstOptional() {
		return new TransformingQuery<V, Optional<V>>() {

			@Override
			public Optional<V> transform(List<V> data) throws SQLException {
				return Optional.ofNullable(SimpleTransformingQuery.<V, V>transform(data, Query.Type.FIRST_NULL));
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				return getPreparedQuerySQL_(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				updateQuerySQL_(stmt);
			}

		};
	}

	public SinglePreparedQuery<V> firstNull() {
		return new SinglePreparedQuery<V>() {

			@Override
			public V transform(List<V> data) throws SQLException {
				return SimpleTransformingQuery.<V, V>transform(data, Query.Type.FIRST_NULL);
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				return getPreparedQuerySQL_(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				updateQuerySQL_(stmt);
			}

		};
	}

	public SinglePreparedQuery<V> firstThrow() {
		return new SinglePreparedQuery<V>() {

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				return getPreparedQuerySQL_(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				updateQuerySQL_(stmt);
			}

		};
	}

	public static <T extends SQLQueryable<V>, V extends DataBaseEntry> QueryBuilder<T, V> select(T table) {
		return new QueryBuilder<>(table);
	}

	public QueryBuilder<T, V> where(String stmt) {
		root = new StmtConditionNode(stmt);
		paramColumns.clear();
		params.clear();
		return this;
	}

	public QueryBuilder<T, V> and(String stmt) {
		addCondition("AND", new StmtConditionNode(stmt));
		return this;
	}

	public QueryBuilder<T, V> or(String stmt) {
		addCondition("OR", new StmtConditionNode(stmt));
		return this;
	}

	public QueryBuilder<T, V> where(String column, String operator, Object value) {
		root = new ConditionNode(column, operator, value);
		paramColumns.clear();
		params.clear();
		paramColumns.add(column);
		params.add(value);
		return this;
	}

	public QueryBuilder<T, V> and(String column, String operator, Object value) {
		addCondition("AND", new ConditionNode(column, operator, value));
		paramColumns.add(column);
		params.add(value);
		return this;
	}

	public QueryBuilder<T, V> or(String column, String operator, Object value) {
		addCondition("OR", new ConditionNode(column, operator, value));
		paramColumns.add(column);
		params.add(value);
		return this;
	}

	public QueryBuilder<T, V> limit(int limit) {
		if (limit < 0) {
			throw new IllegalArgumentException("Limit cannot be negative.");
		}
		this.limit = limit;
		return this;
	}

	public QueryBuilder<T, V> offset(int offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset cannot be negative.");
		}
		this.offset = offset;
		return this;
	}

	public QueryBuilder<T, V> orderByAsc(String column) {
		orderBy.add(new Pair<>(column, "ASC"));
		return this;
	}

	public QueryBuilder<T, V> orderByDesc(String column) {
		orderBy.add(new Pair<>(column, "DESC"));
		return this;
	}

	private void addCondition(String op, Node newCondition) {
		if (root == null) {
			root = newCondition;
			return;
		}
		if (op.equals("AND")) {
			// AND has higher precedence: attach to the right if possible
			if (root instanceof BinaryOpNode && ((BinaryOpNode) root).op.equals("OR")) {
				BinaryOpNode orNode = (BinaryOpNode) root;
				orNode.right = new BinaryOpNode("AND", orNode.right, newCondition);
			} else {
				root = new BinaryOpNode("AND", root, newCondition);
			}
		} else if (op.equals("OR")) {
			// OR has lower precedence: always wrap current root
			root = new BinaryOpNode("OR", root, newCondition);
		}
	}

	private interface Node {
		String toSQL();
	}

	private static class ConditionNode implements Node {
		String column, op;
		Object value;

		ConditionNode(String column, String op, Object value) {
			this.column = column;
			this.op = op;
			this.value = value;
		}

		@Override
		public String toSQL() {
			return column + " " + op + " ?";
		}
	}

	private static class StmtConditionNode extends ConditionNode {
		String stmt;

		public StmtConditionNode(String stmt) {
			super(null, null, null);
			this.stmt = stmt;
		}

		@Override
		public String toSQL() {
			return stmt;
		}
	}

	private static class BinaryOpNode implements Node {
		String op;
		Node left, right;

		BinaryOpNode(String op, Node left, Node right) {
			this.op = op;
			this.left = left;
			this.right = right;
		}

		@Override
		public String toSQL() {
			return "(" + left.toSQL() + " " + op + " " + right.toSQL() + ")";
		}
	}

}
