package cn.objectspace.webssh.util;

import cn.hutool.core.util.StrUtil;
import cn.objectspace.webssh.pojo.sftp.SFTPConnectInfo;
import cn.objectspace.webssh.pojo.sftp.WebSFTPData;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

/**
 * SFTPUtil
 *
 * @author qchen
 * @date 2021/7/21 9:20
 */
@Slf4j
public class SFTPUtil {
    private static final ThreadLocal<WebSFTPData> WEB_SFTP_DATA_TL = new ThreadLocal<>();

    /**
     * 连接ftp/sftp服务器
     *
     * @param webSFTPData SFTP连接信息
     * @return
     */
    public static SFTPConnectInfo getConnect(WebSFTPData webSFTPData) throws Exception {
        if (webSFTPData == null) {
            throw new RuntimeException("[WebSFTPData]不能为空");
        }
        String host = webSFTPData.getHost();
        if (StrUtil.isNotBlank(host)) {
            throw new RuntimeException("[host]不能为空");
        }
        int port = webSFTPData.getPort();
        String username = webSFTPData.getUsername();
        if (StrUtil.isNotBlank(username)) {
            throw new RuntimeException("[username]不能为空");
        }
        String password = webSFTPData.getPassword();
        if (StrUtil.isNotBlank(password)) {
            throw new RuntimeException("[password]不能为空");
        }
        WEB_SFTP_DATA_TL.set(webSFTPData);
        Session session = null;
        Channel channel = null;
        ChannelSftp sftp = null;// sftp操作类

        JSch jsch = new JSch();

        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no"); // 不验证 HostKey
        session.setConfig(config);
        try {
            session.connect();
        } catch (Exception e) {
            if (session.isConnected()) {
                session.disconnect();
            }
            log.error("连接服务器失败,请检查主机[" + host + "],端口[" + port
                    + "],用户名[" + username + "],端口[" + port
                    + "]是否正确,以上信息正确的情况下请检查网络连接是否正常或者请求被防火墙拒绝.");
        }
        channel = session.openChannel("sftp");
        try {
            channel.connect();
        } catch (Exception e) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
            log.error("连接服务器失败,请检查主机[" + host + "],端口[" + port
                    + "],用户名[" + username + "],密码是否正确,以上信息正确的情况下请检查网络连接是否正常或者请求被防火墙拒绝.");
        }
        sftp = (ChannelSftp) channel;
        return new SFTPConnectInfo(session, channel, sftp);
    }

    /**
     * 断开连接
     */
    public static void disConn(Session session, Channel channel, ChannelSftp sftp) throws Exception {
        if (null != sftp) {
            sftp.disconnect();
            sftp.exit();
            sftp = null;
        }
        if (null != channel) {
            channel.disconnect();
            channel = null;
        }
        if (null != session) {
            session.disconnect();
            session = null;
        }
        WEB_SFTP_DATA_TL.remove();
    }

    /**
     * 上传文件
     *
     * @param directory  上传的目录-相对于SFPT设置的用户访问目录，
     *                   为空则在SFTP设置的根目录进行创建文件（除设置了服务器全磁盘访问）
     * @param uploadFile 要上传的文件全路径
     */
    public static void upload(String directory, String uploadFile) throws Exception {

        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        try {
            try {
                sftp.cd(directory); //进入目录
            } catch (SftpException sException) {
                if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) { //指定上传路径不存在
                    sftp.mkdir(directory);//创建目录
                    sftp.cd(directory); //进入目录
                }
            }

            File file = new File(uploadFile);
            InputStream in = new FileInputStream(file);
            sftp.put(in, file.getName());
            in.close();

        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            disConn(session, channel, sftp);
        }
    }

    /**
     * 下载文件
     *
     * @param directory    下载目录 根据SFTP设置的根目录来进行传入
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public static void download(String directory, String downloadFile, String saveFile) throws Exception {
        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        try {

            sftp.cd(directory); //进入目录
            File file = new File(saveFile);
            boolean bFile;
            bFile = false;
            bFile = file.exists();
            if (!bFile) {
                bFile = file.mkdirs();//创建目录
            }
            OutputStream out = new FileOutputStream(new File(saveFile, downloadFile));

            sftp.get(downloadFile, out);

            out.flush();
            out.close();

        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            disConn(session, channel, sftp);
        }
    }


    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public static void delete(String directory, String deleteFile) throws Exception {
        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        try {
            sftp.cd(directory); //进入的目录应该是要删除的目录的上一级
            sftp.rm(deleteFile);//删除目录
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            disConn(session, channel, sftp);
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @return list 文件名列表
     * @throws Exception
     */
    public static List<String> listFiles(String directory) throws Exception {
        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        Vector fileList = null;
        List<String> fileNameList = new ArrayList<String>();
        fileList = sftp.ls(directory); //返回目录下所有文件名称
        disConn(session, channel, sftp);

        Iterator it = fileList.iterator();

        while (it.hasNext()) {

            String fileName = ((ChannelSftp.LsEntry) it.next()).getFilename();
            if (".".equals(fileName) || "..".equals(fileName)) {
                continue;
            }
            fileNameList.add(fileName);

        }

        return fileNameList;
    }

    /**
     * 删除目录下所有文件
     *
     * @param directory 要删除文件所在目录
     */
    public static void deleteAllFile(String directory) throws Exception {
        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        try {
            List<String> files = listFiles(directory);//返回目录下所有文件名称
            sftp.cd(directory); //进入目录

            for (String deleteFile : files) {
                sftp.rm(deleteFile);//循环一次删除目录下的文件
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            disConn(session, channel, sftp);
        }

    }

    /**
     * 删除目录 (删除的目录必须为空)
     *
     * @param deleteDir 要删除的目录
     */
    public static void deleteDir(String deleteDir) throws Exception {
        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        try {

            sftp.rmdir(deleteDir);

        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            disConn(session, channel, sftp);
        }
    }

    /**
     * 创建目录
     *
     * @param directory 要创建的目录 位置
     * @param dir       要创建的目录
     */
    public static void creatDir(String directory, String dir) throws Exception {
        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        try {
            sftp.cd(directory);
            sftp.mkdir(dir);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            disConn(session, channel, sftp);
        }
    }

    /**
     * 更改文件名
     *
     * @param directory 文件所在目录
     * @param oldFileNm 原文件名
     * @param newFileNm 新文件名
     * @throws Exception
     */
    public static void rename(String directory, String oldFileNm, String newFileNm) throws Exception {
        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        try {
            sftp.cd(directory);
            sftp.rename(oldFileNm, newFileNm);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            disConn(session, channel, sftp);
        }
    }

    /**
     * 进入目录
     *
     * @param directory
     * @throws Exception
     */
    public static void cd(String directory) throws Exception {

        SFTPConnectInfo s = getConnect(WEB_SFTP_DATA_TL.get());//建立连接
        Session session = s.getSession();
        Channel channel = s.getChannel();
        ChannelSftp sftp = s.getSftp();// sftp操作类
        try {
            sftp.cd(directory); //目录要一级一级进
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            disConn(session, channel, sftp);
        }
    }
}
