package cn.objectspace.webssh.pojo.sftp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSFTPData
 *
 * @author qchen
 * @date 2021/7/21 9:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSFTPData {
    /**
     * 主机
     */
    private String host = "127.0.0.1";
    /**
     * 端口
     */
    private int port = 22;
    /**
     * 用户名
     */
    private String username = "root";
    /**
     * 密码
     */
    private String password = "root";
}
