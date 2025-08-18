package lu.kbra.shared_timetable.server.db.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.springframework.core.convert.support.DefaultConversionService;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.db.autobuild.column.type.ColumnType;
import lu.pcy113.pclib.db.autobuild.column.type.ColumnType.FixedColumnType;

public class ListType implements FixedColumnType {

	@Override
	public String getTypeName() {
		return "JSON";
	}

	@Override
	public Object encode(Object value) {
		if (value instanceof JSONArray) {
			return ((JSONArray) value).toString();
		} else if (value instanceof List<?>) {
			return new JSONArray((List<?>) value).toString();

			// TODO: @Bananenkaer wtf is this?
			/*
			 * List<?> list = (List<?>) value; JSONArray array = new JSONArray(); ObjectMapper om = new
			 * ObjectMapper(); for(Object obj : list) { if(obj instanceof Jsonable) { JSONObject s = new
			 * JSONObject(((Jsonable) obj).asJson(om)); System.out.println(s); array.put(s); } else {
			 * System.out.println("hm"); array.put(obj); } }
			 * 
			 * String array2 = new JSONArray(list).toString(); System.err.println(array2); return array2;
			 */
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Object decode(Object value, Type type) {
		if (value == null)
			return null;

		if (type == JSONArray.class) {
			return new JSONArray((String) value);
		} else if (type instanceof ParameterizedType parameterizedType) {
			final Type rawType = parameterizedType.getRawType();

			if (rawType instanceof Class<?> rawClass && List.class.isAssignableFrom(rawClass)) {
				final Type elementType = parameterizedType.getActualTypeArguments()[0];

				if (!(elementType instanceof Class<?> elementClass)) {
					throw new IllegalArgumentException("Unsupported element type: " + elementType);
				}

				final List<Object> list;
				if (rawClass.equals(List.class)) {
					list = new ArrayList<>();
				} else {
					list = (List<Object>) PCUtils.newInstance(rawClass);
				}

				final JSONArray array = new JSONArray((String) value);
				array.forEach(item -> {
					if (elementClass.isAssignableFrom(item.getClass())) {
						list.add(elementClass.cast(item));
					} else {
						list.add(DefaultConversionService.getSharedInstance().convert(item, elementClass));
					}
				});

				return list;
			}
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
		stmt.setString(index, (String) value);
	}

	@Override
	public String getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}

}
