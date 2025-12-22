package team.themoment.everygsm.server.v2.global.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import lombok.SneakyThrows;

@OpenAPIDefinition(info = @Info(title = "Hello, GSM 2025", description = "광주소프트웨어마이스터고등학교 입학지원 시스템", version = "v1"))
@Configuration
public class SwaggerConfig {

    private void addResponseBodyWrapperSchemaExample(Operation operation, boolean hideDataField) {
        final Content content = operation.getResponses().get("200").getContent();
        if (content != null) {
            content.keySet().forEach(mediaTypeKey -> {
                final MediaType mediaType = content.get(mediaTypeKey);
                Schema<?> originalSchema = mediaType.getSchema();
                mediaType.schema(wrapSchema(originalSchema, hideDataField));
            });
        }
    }

    @SneakyThrows
    private Schema<?> wrapSchema(Schema<?> originalSchema, boolean hideDataField) {
        final Schema<?> wrapperSchema = new Schema<>();

        wrapperSchema.addProperty("status", new Schema<>().type("string").example("OK"));
        wrapperSchema.addProperty("code", new Schema<>().type("integer").example(200));
        wrapperSchema.addProperty("message", new Schema<>().type("string").example("OK"));
        if (!hideDataField)
            wrapperSchema.addProperty("data", originalSchema);

        return wrapperSchema;
    }
}
