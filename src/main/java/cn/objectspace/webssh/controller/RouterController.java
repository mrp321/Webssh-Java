package cn.objectspace.webssh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RouterController {
    private static final String RAW_WEB_SSH_PAGE_PARAM_URL = "?host=%s&port=%s&username=%s&password=%s";

    @RequestMapping("/")
    public String indexpage() {
        return "index";
    }

    @RequestMapping("/loginSSH")
    public String websshpage(HttpServletRequest request) {
        Map<String, String> reqParams = this.getRequestParams(request);
//        // 添加重定向地址参数
//        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
//        redirectAttributes.addAllAttributes(reqParams);

        String paramUrl = String.format(RAW_WEB_SSH_PAGE_PARAM_URL, reqParams.get("host"), reqParams.get("port"), reqParams.get("username"), reqParams.get("password"));
        return "redirect:websshpage" + paramUrl;
    }

    @RequestMapping("/websshpage")
    public String websshpage() {
        return "webssh";
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
