package eu.uqasar.web.provider.quality.status;

import java.util.Arrays;
import java.util.List;

import eu.uqasar.model.quality.indicator.Color;

public class ColorProvider {

	public static List<Color> getColors() {
		return Arrays.asList(Color.values());
	}
	
}
