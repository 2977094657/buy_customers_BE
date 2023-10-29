package com.example.explor_gastro.common.httpstatus;

public enum CustomStatusCode {
        SUCCESS(210, "请求成功");

        private final int code;
        private final String message;
        CustomStatusCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
}
