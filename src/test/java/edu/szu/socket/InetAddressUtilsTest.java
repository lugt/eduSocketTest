package edu.szu.socket;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
// entry point for all assertThat methods and utility methods (e.g. entry)
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

public class InetAddressUtilsTest {

  public static Logger logger = LoggerFactory.getLogger(InetAddressUtilsTest.class);

  /*****************************************************************************
   *
   * •	使用InetAddress类的方法获取本地机的名称和IP地址。
   *
   *****************************************************************************/

  @Test
  public void getLocalInetAddress() throws UnknownHostException {
    InetAddress one = InetAddressUtils.getLocalInetAddress();
    assertThat(one.getHostAddress()).startsWith("192");
    logger.info("one.getHostAddress = {}", one.getHostAddress());
  }

  /*****************************************************************************
   *
   * •	使用InetAddress类的方法获取网站www.csdn.net的IP地址，如果存在多个IP地址，要求全部返回。
   *
   *****************************************************************************/
  @Test
  public void getCSDNIp() throws Exception{
    InetAddress[] all = InetAddress.getAllByName("www.csdn.net");
    logger.info("Address count : {} ", all.length);
    for(InetAddress csdn : all) {
      logger.info("CSDN IP address: {}", csdn.getHostAddress());
      logger.info("CSDN canonical name: {}", csdn.getCanonicalHostName());
      logger.info("address in bytes:  {} ", csdn.getAddress());
    }
  }

  /******************************************************************************
   *
   * * •	使用URL类下载深圳大学首页http://www.szu.edu.cn，并统计下载得到网页文件的大小
   *
   *****************************************************************************/
  @Test
  public void getSZUHomePage() throws IOException {
    byte[] all = InetAddressUtils.getHttpPage("http://www.szu.edu.cn");
    logger.info("visit http://www.szu.edu.cn, returned : {} bytes", all.length);
    assertThat(all.length).isEqualTo(178);
    all = InetAddressUtils.getHttpPage("https://www.szu.edu.cn/");
    logger.info("visit https://www.szu.edu.cn, returned : {} bytes", all.length);
    assertThat(all.length).isEqualTo(58921);
  }
}