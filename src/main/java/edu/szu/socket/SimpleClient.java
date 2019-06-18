package edu.szu.socket;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA;
import edu.szu.SHA3Digest;
import jdk.internal.util.xml.impl.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleClient {


  private static Logger logger = LoggerFactory.getLogger(SimpleServer.class);
  private static final int MAX_INPUT = 2000000;
  private static final int PORT = 8998;

  public static void main (String[] args) {
    try {
      new Thread(new Handler(new Socket(InetAddressUtils.getLocalInetAddress(), PORT))).start();
      return;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static class Handler implements Runnable {
    final Socket socket;

    Handler(Socket s) {
      socket = s;
    }

    public void run() {
      try {
        byte[] output = sendFiles();
        OutputStream out = socket.getOutputStream();
        out.write(output);
        out.flush();

        InputStream ins = socket.getInputStream();
        byte[] input = InetAddressUtils.getFromInputStream(ins);
        process(input);

        //out.close();
        //ins.close();
        while (true) {
          input = InetAddressUtils.getFromInputStream(ins);
          process(input);
          Thread.sleep(100);
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    private byte[] process(byte[] input) {
      logger.info("Server response : {}", new String(input, StandardCharsets.UTF_8));
      return null;
    }

    /**
     * 客户端处理文件传输，
     * MSB = Big Endian
     * 文件协议 : 56 (CMD),
     * [0,0,0,26] (size of filename),
     * [0,0,0,200] (size of file content) ,
     * file name,
     * file content
     *
     * @return
     */
    private byte[] sendFiles() throws IOException {
      String fileName = "/Users/xc5/z2z.sh";
      String fileCanonName = "z2z.sh";
      byte[] fileCanonNameBytes = fileCanonName.getBytes(StandardCharsets.UTF_8);
      byte[] fileContent = Files.readAllBytes(Paths.get(fileName));
      byte[] result = new byte[fileCanonNameBytes.length + fileContent.length + 9];

      putInt(result, 1, fileCanonNameBytes.length);
      putInt(result, 5, fileContent.length);
      System.arraycopy(fileCanonNameBytes, 0, result,
          9, fileCanonNameBytes.length);
      System.arraycopy(fileContent, 0, result,
          9 + fileCanonNameBytes.length, fileContent.length);
      result[0] = 56;

      logger.info("sending files : {} as {}", fileName, fileCanonName);
      logger.info("digest : {}", SHA3Digest.StringDigest(256,
          SHA3Digest.bytesToHexString(fileContent)));
      logger.info("total len = 9 + {} + {} = {}",
          fileCanonNameBytes.length, fileContent.length, result.length);

      return result;
    }

    private void putInt(byte[] cmd, int i, int length) {
      cmd[i] = (byte) (length >> 24);
      cmd[i + 1] = (byte) (length >> 16);
      cmd[i + 2] = (byte) (length >> 8);
      cmd[i + 3] = (byte) (length >> 0);
    }
  }
}
