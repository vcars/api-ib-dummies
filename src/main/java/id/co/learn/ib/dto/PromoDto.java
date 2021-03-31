package id.co.learn.ib.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromoDto implements Serializable {

    private Long id;
    private String title;
    private String content;
    private String featuredImage;

}
