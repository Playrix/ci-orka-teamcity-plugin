package com.macstadium.orka;

import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import jetbrains.buildServer.log.Loggers;

public class SSHUtil {
  private static final Logger LOG = Logger.getInstance(Loggers.CLOUD_CATEGORY_ROOT + OrkaConstants.TYPE);

  public boolean waitForSSH(String host, int sshPort, int retries, int secondsBetweenRetries)
      throws IOException, InterruptedException {
    LOG.debug(String.format("Waiting for SSH on %s:%d (max %d attempts, %ds interval)",
        host, sshPort, retries, secondsBetweenRetries));

    // Socket connect timeout in milliseconds (10 seconds)
    final int CONNECT_TIMEOUT_MS = 10_000;

    for (int attempt = 1; attempt <= retries; attempt++) {
      try (Socket s = new Socket()) {
        s.connect(new InetSocketAddress(host, sshPort), CONNECT_TIMEOUT_MS);
        LOG.info(String.format("SSH ready on %s:%d (attempt %d/%d)", host, sshPort, attempt, retries));
        return true;
      } catch (IOException ex) {
        if (attempt == retries) {
          LOG.warnAndDebugDetails(String.format("SSH not available on %s:%d after %d attempts",
              host, sshPort, retries), ex);
          throw ex;
        }
        LOG.debug(String.format("SSH check %d/%d failed: %s", attempt, retries, ex.getMessage()));
        Thread.sleep(TimeUnit.SECONDS.toMillis(secondsBetweenRetries));
      }
    }

    return false;
  }
}