package team.themoment.everygsm.server.v2.global.thirdparty.feign.datagsm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DataGSM OpenAPI 공통 응답 래퍼. 실제 페이로드는 {@code data} 필드에 담겨 온다.
 */
@Getter
@NoArgsConstructor
public class DatagsmApiResponse<T> {

    private String status;
    private int code;
    private String message;
    private T data;
}
