package cn.objectspace.webssh.controller;

import cn.objectspace.webssh.pojo.RespBody;
import cn.objectspace.webssh.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sshapp")
@Slf4j
public class RouterController {
    private static final String RAW_WEB_SSH_PAGE_PARAM_URL = "host=%s&port=%s&username=%s&password=%s";

    @Autowired
    private Environment env;

    @RequestMapping("/")
    public String indexpage() {
        return "index";
    }

    @RequestMapping("/loginSSH")
    @ResponseBody
    public RespBody<String> websshpage(HttpServletRequest request) {
        Map<String, String> reqParams = this.getRequestParams(request);
        log.info("登录至SSH, 请求参数为: {}", reqParams);
        // SSH登录参数串
        String paramUrl = String.format(RAW_WEB_SSH_PAGE_PARAM_URL, reqParams.get("host"), reqParams.get("port"), reqParams.get("username"), SecurityUtil.rsaDec(reqParams.get("password")));
        String paramUrlEnc = null;
        String msg = "调用成功";
        boolean succ = false;
        try {
            paramUrlEnc = SecurityUtil.rsaEnc(paramUrl);
            succ = true;
        } catch (Exception e) {
            msg = e.getMessage();
        }
        RespBody<String> respBody = new RespBody<>(succ ? 0 : -1, msg, paramUrlEnc);
        return respBody;
    }

    @RequestMapping("/websshpage")
    public String websshpage() {
        return "webssh";
    }

    @RequestMapping("/sysconfig")
    @ResponseBody
    public RespBody<String> sysconfig(String key) {
        return new RespBody<>(0, "调用成功", env.getProperty(key));
    }

    private String getRequestParam(HttpServletRequest request, String param) {
        return getRequestParams(request).get(param);
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> requestParams = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            requestParams.put(paramName, paramValue);
        }
        return requestParams;
    }
}
