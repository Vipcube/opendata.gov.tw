package org.vipcibe.opendata.gov.tw.json.geojson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureCollection {
	@Builder.Default
	private String type = "FeatureCollection";
	@Builder.Default
	private List<Feature> features = new ArrayList<>();
}
