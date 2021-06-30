package cn.objectspace.webssh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: webssh数据传输
 * @Author: NoCortY
 * @Date: 2020/3/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSSHData {
    //操作
    private String operate;
    private String host;
    //端口号默认为22
    private Integer port = 22;
    private String username;
    private String password;
    private String command = "";

    private int cols = 80;
    private int rows = 24;
    private int width = 640;
    private int height = 480;

    private String sshConnParam;
}
