package lu.kbra.shared_timetable.client.utils;

import java.awt.Color;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ColorDeserializer extends JsonDeserializer<Color> {

	@Override
	public Color deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);

		if (node.isTextual()) {
			// Hex string
			String hex = node.asText();
			if (hex.startsWith("#"))
				hex = hex.substring(1);
			return Color.decode("0x" + hex);
		} else if (node.isArray() && node.size() >= 3) {
			// RGB array [r, g, b]
			int r = node.get(0).asInt();
			int g = node.get(1).asInt();
			int b = node.get(2).asInt();
			return new Color(r, g, b);
		} else if (node.isObject()) {
			// RGB object {red:..., green:..., blue:...}
			int r = node.has("red") ? node.get("red").asInt() : 0;
			int g = node.has("green") ? node.get("green").asInt() : 0;
			int b = node.has("blue") ? node.get("blue").asInt() : 0;
			return new Color(r, g, b);
		}

		throw new IllegalArgumentException("Cannot deserialize Color from: " + node.toString());
	}
}