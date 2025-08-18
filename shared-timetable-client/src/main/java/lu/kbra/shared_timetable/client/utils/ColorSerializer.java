package lu.kbra.shared_timetable.client.utils;

import java.awt.Color;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ColorSerializer extends JsonSerializer<Color> {

	@Override
	public void serialize(Color color, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (color == null) {
			gen.writeNull();
			return;
		}
		// Serialize as hex string, e.g., "#FF00AA"
		String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
		gen.writeString(hex);
	}

}
