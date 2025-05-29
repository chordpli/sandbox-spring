package com.pli.sandbox.domain.system;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "System", description = "System related APIs")
@RestController()
@RequestMapping("api/v1/system")
public class SystemController {

    @GetMapping("/health")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Health check successful", content = @Content(mediaType = "application/json", schemaProperties = {
                    @SchemaProperty(name = "status", schema = @Schema(type = "string", example = "OK")),
            })),
    })
    public String health() {
        return "OK";
    }
}
