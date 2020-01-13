package com.lanjoys.framework.oss;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;

@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    public OssAutoConfiguration(OssProperties ossProperties) {
        OSSClient client = new OSSClient(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getSecretAccessKey());
        //bucket
        if (!client.doesBucketExist(ossProperties.getBucketName())) {
            client.createBucket(ossProperties.getBucketName());
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(ossProperties.getBucketName());
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
            client.createBucket(createBucketRequest);
        }

        //folder
        if (!client.doesObjectExist(ossProperties.getBucketName(), ossProperties.getFolder())) {
            client.putObject(ossProperties.getBucketName(), ossProperties.getFolder(), new ByteArrayInputStream(new byte[0]));
        }

        Oss.client = client;
        Oss.ossProperties = ossProperties;
    }

}
