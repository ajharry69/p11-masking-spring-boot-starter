package com.github.ajharry69.log.mask;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "p11.masking")
public class P11MaskingProperties {
    @Builder.Default
    private boolean enabled = true;
    private List<String> fields;
    @Builder.Default
    private MaskingStyle maskStyle = MaskingStyle.FULL;
    @Builder.Default
    private String maskCharacter = "*";

    public enum MaskingStyle {FULL, PARTIAL, LAST4}

}