package cn.objectspace.webssh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBody<T> {
    // 状态码
    private int code;
    // 请求信息
    private String msg;
    // 返回数据
    private T data;
}
