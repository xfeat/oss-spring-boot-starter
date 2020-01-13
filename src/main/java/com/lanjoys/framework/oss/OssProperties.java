package com.lanjoys.framework.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.lanjoys.framework.oss.OssProperties.PREFIX;

@Data
@ConfigurationProperties(prefix = PREFIX)
public class OssProperties {
    public static final String PREFIX = "oss";
    private String endpoint;
    private String bucketName;
    private String accessKeyId;
    private String secretAccessKey;
    private String folder;
    private String accessPrefix;
}
