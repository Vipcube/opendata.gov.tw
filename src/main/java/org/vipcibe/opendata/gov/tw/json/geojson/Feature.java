package org.vipcibe.opendata.gov.tw.json.geojson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feature {
	@Builder.Default
	private String type = "Feature";
	@Builder.Default
	private Map<String, Object> properties = new LinkedHashMap<>();
	private Geometry geometry;
}
