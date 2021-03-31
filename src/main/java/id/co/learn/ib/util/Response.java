package id.co.learn.ib.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-24
*/ 
@Data
@NoArgsConstructor
public class Response {

    private Object data;
    private String message;
    private Boolean result;

    public Response(Object data, String message, Boolean result){
        this.data = data;
        this.message = message;
        this.result = result;
    }
    
}
