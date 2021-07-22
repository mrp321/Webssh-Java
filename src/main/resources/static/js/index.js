/**
 * 提交SSH登录参数
 */
function submit() {
    var host = $("#host").val();
    if (host == null || host === '') {
        alert("host 不能为空");
        return;
    }
    var port = $("#port").val();
    if (port == null || port === '') {
        alert("port 不能为空");
        return;
    }
    var username = $("#username").val();
    if (username == null || username === '') {
        alert("username 不能为空");
        return;
    }
    var password = $("#password").val();
    if (password == null || password === '') {
        alert("password 不能为空");
        return;
    }
    var passwordEnc = rsaEnc(password);
    if (passwordEnc == null || passwordEnc === '') {
        alert("password 加密异常");
        return;
    }
    $.ajax({
        url: "/sshapp/loginSSH",
        data: {
            host: host,
            port: port,
            username: username,
            password: passwordEnc
        },
        type: 'post',
        dataType: 'json',
        async: true,
        success: function (result) {
            console.log("接口返回结果: " + JSON.stringify(result));
            var code = result.code;
            if (code === 0) {
                location.href = '/sshapp/websshpage?params=' + result.data;
            } else {
                alert("发生异常: " + result.msg);
            }
        },
        error: function (err) {
            alert("发生异常: " + err);
        }
    });
}

/**
 * 登录密码RSA方式加密
 * @param data
 * @returns {*}
 */
function rsaEnc(data) {
    var encrypt = new JSEncrypt();
    encrypt.setPublicKey('-----BEGIN PUBLIC KEY-----' + global.rsaPublicKey + '-----END PUBLIC KEY-----');
    return encrypt.encrypt(data);
}