package com.example.seizuredetectionapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPHelpers {
	public static final String MYURL = "http://104.237.129.207:8080/";

	// HEY!
	// Change me to false when we have the wristband working and attached
	// -John
	private static final boolean useDummyData = true;
	public static final boolean Debug() {return useDummyData; }

	private static HttpURLConnection setupConn(String path, String params, String method) {
		// Create a http request to the server
		HttpURLConnection conn = null;
		try {
			URL url = new URL(MYURL + "/" + path + params);
			conn = (HttpURLConnection) url.openConnection();
			if (method.equals("POST")) {
				conn.setDoOutput(true);
			}
			conn.setDoInput(true);
			conn.setRequestMethod(method);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	private static HttpURLConnection setupPost() {
		return setupConn("", "", "POST");
	}

	private static HttpURLConnection setupGet() {
		return setupConn("", "", "GET");
	}

	private static HttpURLConnection setupPost(String path) {
		return setupConn(path, "", "POST");
	}

	private static HttpURLConnection setupGet(String path) {
		return setupConn(path, "", "GET");
	}

	private static HttpURLConnection setupPost(String path, String params) {
		return setupConn(path, params, "POST");
	}

	private static HttpURLConnection setupGet(String path, String params) {
		return setupConn(path, params, "GET");
	}

	private static String getResponse(HttpURLConnection connection) {
		// Retrieve the response from the connection
		String response = "";

		try {
			// Get input stream
			InputStream inputStream = connection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			// Read input into string
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				response += line;
			}
			// Close the connection
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return response;
	}

	public static String createUser() {
		HttpURLConnection connection = null;
		try {
			connection = setupGet();
			return getResponse(connection);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static String getString(String type) {
		return getFactoid(type, "");
	}

	public static String getFactoid(String type, String params) {
		HttpURLConnection connection = null;
		try {
			connection = setupGet(type, params);
			return getResponse(connection);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static String postFactoid(String type, String params) {
		return postFactoid(type, params, null);
	}

	public static String postFactoid(String type, String params, String data) {
		HttpURLConnection connection = null;
		try {
			connection = setupPost(type, params);
			// Write key as parameters
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			// Write json into post if it's there
			if (data != null) {
				wr.writeBytes(data);
			}
			wr.flush();
			wr.close();
			return getResponse(connection);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static String getEDA(String userkey) {
		return getFactoid("eda", "?key=" + userkey);
	}

	public static String postEDA(String userkey, float eda) {
		return postFactoid("eda", "?key=" + userkey,
				"{\"reading\":" + eda + ", \"timestamp\":\"" + System.currentTimeMillis() / 1000 + "\"}");
	}

	public static String getHR(String userkey) {
		return getFactoid("hr", "?key=" + userkey);
	}

	public static String postHR(String userkey, int hr) {
		return postFactoid("hr", "?key=" + userkey,
				"{\"reading\":" + hr + ", \"timestamp\":\"" + System.currentTimeMillis() / 1000 + "\"}");
	}

	public static String getAcceleration(String userkey) {
		return getFactoid("acc", "?key=" + userkey);
	}

	public static String postAcceleration(String userkey, float x, float y, float z) {
		return postFactoid("acc", "?key=" + userkey, "{\"reading\":{" + "\"x\":" + x + ", \"y\":" + y + ", \"z\":" + z
				+ "}, \"timestamp\":\"" + System.currentTimeMillis() / 1000 + "\"}");
	}

	public static String getGyro(String userkey) {
		return getFactoid("gyro", "?key=" + userkey);
	}

	public static String postGyro(String userkey, float x, float y, float z) {
		return postFactoid("gyro", "?key=" + userkey, "{\"reading\":{" + "\"x\":" + x + ", \"y\":" + y + ", \"z\":" + z
				+ "}, \"timestamp\":\"" + System.currentTimeMillis() / 1000 + "\"}");
	}

	public static String getSnapshots(String userkey) {
		return getFactoid("snapshots", "?key=" + userkey);
	}

	public static String postSnapshot(String userkey) {
		return postFactoid("snapshots", "?key=" + userkey);
	}

	public static String snapshot(String userkey) {
		return postFactoid("snapshots", "?key=" + userkey);
	}

	public static void main(String[] args) {
		// Create a user
		String userkey = createUser();
		// Post an EDA factoid
		String edaPost = postEDA(userkey, 1.0f);
		// Post an HR factoid
		String hrPost = postHR(userkey, 100);
		// Post a snapshot
		String snapshotPost = postSnapshot(userkey);
		// Post acceleration
		String accPost = postAcceleration(userkey, 1.0f, 1.0f, 1.0f);
		// Post gyro
		String gyroPost = postGyro(userkey, 1.0f, 1.0f, 1.0f);
		// Get an EDA factoid
		String eda = getEDA(userkey);
		// Get an HR factoid
		String hr = getHR(userkey);
		// Get a snapshot
		String snapshot = getSnapshots(userkey);
		// Get acceleration
		String acc = getAcceleration(userkey);
		// Get gyro
		String gyro = getGyro(userkey);


		// Print the responses
		System.out.println("User key: " + userkey);
		System.out.println("EDA Post: " + edaPost);
		System.out.println("HR Post: " + hrPost);
		System.out.println("Acc Post: " + accPost);
		System.out.println("Gyro Post: " + gyroPost);
		System.out.println("Snapshot Post: " + snapshotPost);
		System.out.println("EDA: " + eda);
		System.out.println("HR: " + hr);
		System.out.println("Snapshot: " + snapshot);
		System.out.println("Acceleration: " + acc);
		System.out.println("Gyro: " + gyro);
	}
}
