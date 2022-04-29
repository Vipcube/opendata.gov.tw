package org.vipcibe.opendata.gov.tw.nhi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RapidTestRecord {
	private String id;
	private String name;
	private String address;
	private double lat;
	private double lon;
	private String phone;
	private String label;
	private int inStock;
	private String updateTime;
}
