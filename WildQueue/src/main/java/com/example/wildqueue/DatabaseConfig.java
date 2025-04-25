package com.example.wildqueue;

public class DatabaseConfig {
	public static final String HOST = "gateway01.ap-southeast-1.prod.aws.tidbcloud.com";
	public static final String PORT = "4000";
	public static final String USERNAME = "Yb6TTiydzfUS3EJ.root";
	public static final String PASSWORD = "uQm1CEVIsEqWZWNJ";
	public static final String DATABASE = "test";
	public static final String CERT_PATH = "E:/PreFiJava/src/main/java/com/example/prefijava/isrgrootx1.pem";

	public static final String JDBC_URL = String.format(
			"jdbc:mysql://%s:%s/%s?useSSL=true&requireSSL=true&verifyServerCertificate=true&trustCertificate=file:%s",
			HOST, PORT, DATABASE, CERT_PATH
	);
}
