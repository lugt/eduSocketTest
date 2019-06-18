package edu.szu.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class InetAddressUtils {
  private static Logger logger = LoggerFactory.getLogger(InetAddressUtils.class);

  public static InetAddress getLocalInetAddress() throws UnknownHostException {
    return InetAddress.getLocalHost();
  }

  public static byte[] getHttpPage(String s) throws IOException {
    HttpURLConnection httpConn;
    URL url = new URL(s);
    httpConn = (HttpURLConnection) url.openConnection();
    httpConn.setRequestMethod("GET");
    httpConn.setRequestProperty("Content-Type", "text/html");
    httpConn.setConnectTimeout(10000);
    httpConn.setReadTimeout(10000);
    return readOutcome(httpConn);
  }

  public static byte[] readOutcome(HttpURLConnection httpConn) throws IOException {
    int status = httpConn.getResponseCode();
    InputStream inputStream = httpConn.getInputStream();
    byte[] result = getFromInputStream(inputStream);
    if (status != HttpURLConnection.HTTP_OK) {
      logger.error("response not HTTP 200, error msg: {}", new String(result, StandardCharsets.UTF_8));
    }
    return result;
  }

  public static byte[] getFromInputStream(InputStream inputStream) throws IOException {
    byte[] result = new byte[2000000];
    byte[] all = new byte[1000000];
    int resCursor = 0;
    int count = 0;
    //while () {
    while((count = inputStream.read(all)) <= 0){
      // nothing.
    }
    System.arraycopy(all, 0, result, resCursor, count);
    resCursor += count;
    //}
    //inputStream.close();
    return Arrays.copyOf(result, resCursor);
  }
}
