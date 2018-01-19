package com.newtally.core.dto;

public class ResponseDto {
    
    private Integer response_code;
    private String response_message;
    private Object response_data;
    public ResponseDto() {
        
    }
    
    public ResponseDto(Integer response_code, String response_message, Object response_data) {
        super();
        this.response_code = response_code;
        this.response_message = response_message;
        this.response_data = response_data;
    }
    
    public Integer getResponse_code() {
        return response_code;
    }
    public void setResponse_code(Integer response_code) {
        this.response_code = response_code;
    }
    public String getResponse_message() {
        return response_message;
    }
    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }
    public Object getResponse_data() {
        return response_data;
    }
    public void setResponse_data(Object response_data) {
        this.response_data = response_data;
    }
}
