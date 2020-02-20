package org.motechproject.tujiokowe.client;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.motechproject.tujiokowe.exception.FtpException;

public class TujiokoweFtpsClient {

    private static final Integer CONNECT_TIMEOUT = 10000;

    private Session session;
    private ChannelSftp sftpChannel;

    public void disconnect() {
        if (session != null && session.isConnected()) {
            if (sftpChannel != null) {
                sftpChannel.exit();
                sftpChannel = null;
            }
            session.disconnect();
            session = null;
        }
    }

    public void connect(String knownHostsFile, String hostname, Integer port, String username, String password) throws FtpException {
        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostsFile);
            session = jsch.getSession(username, hostname, port);
            session.setPassword(password);
            session.connect(CONNECT_TIMEOUT);

            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
        } catch (JSchException e) {
            disconnect();
            throw new FtpException("IOException occurred while connecting: " + e.getMessage(), e);
        }
    }

    public List<String> listFiles(String directory) throws FtpException {
        if (session != null && session.isConnected() && sftpChannel != null) {
            try {
                List<String> filenames = new ArrayList<>();
                Vector files = sftpChannel.ls(directory);
                for (Object f : files) {
                    String fileName = ((LsEntry) f).getFilename();
                    if (!".".equals(fileName) && !"..".equals(fileName)) {
                        filenames.add(fileName);
                    }
                }
                return filenames;
            } catch (SftpException e) {
                throw new FtpException("Could not list files: " + e.getMessage(), e);
            }
        } else {
            throw new FtpException("Could not list files, because there is not active connection");
        }
    }

    public InputStream fetchFile(String location) throws FtpException {
        if (session != null && session.isConnected() && sftpChannel != null) {
            try {
                return sftpChannel.get(location);
            } catch (SftpException e) {
                throw new FtpException("Could not fetch file: " + e.getMessage(), e);
            }
        } else {
            throw new FtpException("Could not fetch file, because there is not active connection");
        }
    }
}
