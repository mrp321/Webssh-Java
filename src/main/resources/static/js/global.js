var global = {
    rsaPublicKey: 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDnxkrdtuM+uOeHZyqAQ0bVV+XSHeRF3ukPrvV+Oofbzp22FjnF7SwzhIGyC4KWWAc3afREsiucRKjuaWmcpuxo0rwVXcrxKNjSeuORvpM8RD4XX72jWPaIa/ft1SoQufH9VGtUcxMn75hWgClqNYjYOnidqxWjgcT/OPw+QaszuwIDAQAB',
    getQueryString: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;
    }
}