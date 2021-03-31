package id.co.learn.ib.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuDto implements Serializable {

    private String label;
    private Integer order;
    private List<MenuDto> children;

}
