package org.vipcibe.opendata.gov.tw.nhi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
public class RapidTestCsvProcess {
	private static HttpClient httpClient;
	private final static Path root = Paths.get( "src/test/resources" );
	private final static Path rapidTestCsvPath = root.resolve( "raw/A21030000I-D03001-001.csv" );

	@BeforeAll
	public static void setUp(){
		httpClient = HttpClient.newBuilder()
				.version( HttpClient.Version.HTTP_1_1 )
				.connectTimeout( Duration.ofSeconds( 10 ) )
				.build();
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
	public void transformRapidTestCSV(){
		Assertions.assertTrue( PathUtil.exists( rapidTestCsvPath, false ) );
	}
}
