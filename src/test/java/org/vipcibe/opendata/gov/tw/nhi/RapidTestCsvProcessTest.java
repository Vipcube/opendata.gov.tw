package org.vipcibe.opendata.gov.tw.nhi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.vipcibe.opendata.gov.tw.json.geojson.Feature;
import org.vipcibe.opendata.gov.tw.json.geojson.FeatureCollection;
import org.vipcibe.opendata.gov.tw.nhi.model.RapidTestRecord;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
public class RapidTestCsvProcessTest {
	private static HttpClient httpClient;
	private static GeometryFactory geometryFactory;
	private static ObjectMapper objectMapper;
	private final static Path root = Paths.get( "src/test/resources" );
	private final static Path rapidTestCsvPath = root.resolve( "raw/A21030000I-D03001-001.csv" );
	private final static Path rapidTestJsonPath = root.resolve( "json/nhi/rapidTestStock.json" );
	private final static Path resultJsonPath = root.resolve( "../../../docs/rapidTestStock.json" );

	@BeforeAll
	public static void setUp(){
		httpClient = HttpClient.newBuilder()
				.version( HttpClient.Version.HTTP_1_1 )
				.connectTimeout( Duration.ofSeconds( 10 ) )
				.build();

		geometryFactory = new GeometryFactory( new PrecisionModel( 10000 ), 4326 );

		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
	}

	@Test
	@Order( 1 )
	public void downloadRapidTestCSV() throws IOException, InterruptedException {
		String url = "https://data.nhi.gov.tw/resource/Nhi_Fst/Fstdata.csv";
		HttpRequest request = HttpRequest.newBuilder()
				.uri( URI.create(url) )
				.build();

		HttpResponse<String> response = httpClient.send( request, HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals( 200, response.statusCode() );

		String body = response.body();
		Path path = FileUtil.writeString( body, rapidTestCsvPath.toFile(), StandardCharsets.UTF_8 ).toPath();
		Assertions.assertTrue( PathUtil.exists( path, false ) );
	}

	@Test
	@Order( 2 )
	public void transformToJSON() throws JsonProcessingException {
		if ( PathUtil.exists( rapidTestCsvPath, false ) ){
			List<String> contents = FileUtil.readLines( rapidTestCsvPath.toFile(), StandardCharsets.UTF_8 );
			var records = contents.stream()
					.skip( 1 )
					.map( line -> line.split( "," ) )
					.map( splits -> RapidTestRecord.builder()
							.id( splits[0] )
							.name( splits[1] )
							.address( splits[2] )
							.lon( Double.parseDouble( splits[3] ) )
							.lat( Double.parseDouble( splits[4] ) )
							.phone( splits[5] )
							.label( splits[6] )
							.inStock( Integer.parseInt( splits[7] ) )
							.updateTime( splits[8] )
							.build() )
					.collect( Collectors.toList());

			var features = records.stream()
					.map( record -> Feature.builder()
							.properties( Map.of(
									"id", record.getId(),
									"name", record.getName(),
									"address", record.getAddress(),
									"phone", record.getPhone(),
									"label", record.getLabel(),
									"stock", record.getInStock(),
									"updateTime", record.getUpdateTime()
							) )
							.geometry( geometryFactory.createPoint( new Coordinate( record.getLon(), record.getLat() ) ) )
							.build() )
					.collect( Collectors.toList());

			FeatureCollection collection = FeatureCollection.builder()
					.features( features )
					.build();
			FileUtil.writeString( objectMapper.writeValueAsString( collection ), rapidTestJsonPath.toFile(), StandardCharsets.UTF_8 );
		}
	}

	@Test
	@Order( 3 )
	public void moveJSON(){
		if ( PathUtil.exists( rapidTestJsonPath, false ) ){
			PathUtil.copy( rapidTestJsonPath, resultJsonPath, StandardCopyOption.REPLACE_EXISTING );
		}
	}
}
