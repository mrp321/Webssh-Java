package cn.objectspace.webssh.pojo.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SFTPConnectInfo
 *
 * @author qchen
 * @date 2021/7/21 9:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SFTPConnectInfo {
    private Session session;//会话
    private Channel channel;//连接通道
    private ChannelSftp sftp;// sftp操作类
}