package edu.szu.socket;

import edu.szu.SHA3Digest;
import jdk.management.resource.internal.inst.StaticInstrumentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleServer {

  private static final int MAX_INPUT = 2000000;
  private static final int PORT = 8998;
  private static Logger logger = LoggerFactory.getLogger(SimpleServer.class);

  public static void main(String[] args) {
    Server one = new Server();
    new Thread(one).start();
  }

  static class Server implements Runnable {
    public void run() {
      try {
        ServerSocket ss = new ServerSocket(PORT);
        while (!Thread.interrupted())
          new Thread(new Handler(ss.accept())).start();
        // or, single-threaded, or a thread pool
      } catch (IOException ex) { }
    }
  }

  static class Handler implements Runnable {
    final Socket socket;
    Handler(Socket s) { socket = s; }
    public void run() {
      try {
        InputStream ins = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        byte[] input = InetAddressUtils.getFromInputStream(ins);
        byte[] output = process(input);
        out.write(output);
        out.close();
        ins.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /**
     * 处理文件传输，
     * MSB = Big Endian
     * 文件协议 : 56 (CMD),
     *          [0,0,0,26] (size of filename),
     *          [0,0,0,200] (size of file content) ,
     *          file name,
     *          file content
     * @param cmd
     * @return
     */
    private byte[] process(byte[] cmd) throws IOException {
      if(cmd.length <= 9){
        return "1 error".getBytes();
      }
      if(cmd[0] == 56){
        // read the file;
        int fileNameSize = readInt(cmd, 1);
        int fileContentSize = readInt(cmd, 5);
        if(cmd.length != fileNameSize + fileContentSize + 9) {
          logger.info("error because the \n " +
              "filename size = {} \n " +
              "filecontent size = {}, \n" +
              "and 9 byte prefix \n" +
              "does not meet cmd with size {}", fileNameSize, fileContentSize, cmd.length);
          return "2 error".getBytes();
        }
        byte[] name = new byte[fileNameSize];
        byte[] content = new byte[fileContentSize];
        System.arraycopy(cmd, 9, name, 0, fileNameSize);
        System.arraycopy(cmd, 9 + fileNameSize, content, 0, fileContentSize);
        String fn = new String(name, StandardCharsets.UTF_8);
        String tmpfn = fn + "." + System.currentTimeMillis();
        logger.info("file name : {}, len = {}", fn, fileNameSize);
        Files.write(Paths.get(tmpfn), content);
        logger.info("file content saved : {}, size = {} ", tmpfn, fileContentSize);
        logger.info("digest : {}", SHA3Digest.StringDigest(256,
            SHA3Digest.bytesToHexString(content)));
        return ("0 ok, got " + SHA3Digest.StringDigest(256,
            SHA3Digest.bytesToHexString(content))).getBytes();
      } else {
        return "4 error".getBytes();
      }
    }

    private int readInt(byte[] cmd, int i) {
      return (((int)(char) cmd[i]) << 24) +
          (((int)(char) cmd[i + 1]) << 16) +
          (((int)(char) cmd[i + 2]) << 8) +
          (int)(char) cmd[i + 3];
    }
  }
}
