package com.shewell.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class FtpUtil {

    private final FTPClient ftpClient;

    @Value("${ftp.host:127.0.0.1}")
    private String host;

    @Value("${ftp.port:21}")
    private int port;

    @Value("${ftp.username:anonymous}")
    private String username;

    @Value("${ftp.password:}")
    private String password;

    @Value("${ftp.base-path:/}")
    private String basePath;

    @Value("${ftp.url-prefix:http://127.0.0.1}")
    private String urlPrefix;

    private boolean connect() {
        try {
            ftpClient.connect(host, port);
            if (!ftpClient.login(username, password)) {
                log.error("FTP登录失败");
                return false;
            }
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return false;
            }
            return true;
        } catch (IOException e) {
            log.error("FTP连接失败: {}", e.getMessage());
            return false;
        }
    }

    public String upload(byte[] data, String dir, String fileName) {
        if (!connect()) return null;
        try {
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fullDir = basePath + dir + "/" + dateDir;
            makeDirs(fullDir);
            ftpClient.changeWorkingDirectory(fullDir);
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            boolean ok = ftpClient.storeFile(fileName, in);
            in.close();
            if (ok) {
                return urlPrefix + "/" + dir + "/" + dateDir + "/" + fileName;
            }
            return null;
        } catch (IOException e) {
            log.error("FTP上传失败: {}", e.getMessage());
            return null;
        } finally {
            disconnect();
        }
    }

    private void makeDirs(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String d : path.split("/")) {
            if (d.isEmpty()) continue;
            sb.append("/").append(d);
            try {
                if (!ftpClient.changeWorkingDirectory(sb.toString())) {
                    ftpClient.makeDirectory(sb.toString());
                    ftpClient.changeWorkingDirectory(sb.toString());
                }
            } catch (Exception ignored) {}
        }
    }

    private void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ignored) {}
    }
}
