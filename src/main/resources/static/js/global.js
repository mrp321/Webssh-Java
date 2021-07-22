var global = {
    rsaPublicKey: 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDnxkrdtuM+uOeHZyqAQ0bVV+XSHeRF3ukPrvV+Oofbzp22FjnF7SwzhIGyC4KWWAc3afREsiucRKjuaWmcpuxo0rwVXcrxKNjSeuORvpM8RD4XX72jWPaIa/ft1SoQufH9VGtUcxMn75hWgClqNYjYOnidqxWjgcT/OPw+QaszuwIDAQAB',
    getQueryString: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;
    },
    getSysConfig: function (key) {
        var configValue = null;
        $.ajax({
            url: "/sshapp/sysconfig",
            data: {
                key : key
            },
            type: 'post',
            dataType: 'json',
            async: false,
            success: function (result) {
                console.log("接口返回结果: " + JSON.stringify(result));
                var code = result.code;
                if (code === 0) {
                    configValue = result.data;
                } else {
                    console.log("发生异常: " + result.msg);
                    alert("发生异常: " + result.msg);
                }
            },
            error: function (err) {
                console.log("发生异常: " + err);
                alert("发生异常: " + err);
            }
        });
        return configValue;
    }
}