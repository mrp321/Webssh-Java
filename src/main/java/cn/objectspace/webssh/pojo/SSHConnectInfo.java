package cn.objectspace.webssh.pojo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

/**
* @Description: ssh连接信息
* @Author: NoCortY
* @Date: 2020/3/8
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSHConnectInfo {
    private WebSocketSession webSocketSession;
    private JSch jSch;
    private Channel channel;

}
