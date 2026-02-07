package fajarcode.serverappinitializr.models.dto.responses.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private Boolean success;
    private String message;
    private Instant timestamp;
    private T data;

    private BaseResponse(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.timestamp = Instant.now();
        this.data = data;
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(true, message, data);
    }

    public static BaseResponse<Void> error(String message) {
        return new BaseResponse<>(false, message, null);
    }

}
