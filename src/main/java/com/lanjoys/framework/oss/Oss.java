package com.lanjoys.framework.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Oss {
    public static final Pattern imageBase64Pattern = Pattern.compile("^data:image/(.*?);base64,(.*)");
    static OssProperties ossProperties;
    static OSSClient client;

    private static String upload2Oss(String folder, String fileName, byte[] data) {
        if (StringUtils.isBlank(folder) || data == null) {
            throw new RuntimeException("未设置folder或data为null");
        }

        String moduleFolder = createFolderIfNecessary(folder);
        String releaseFileName = createFileNameIfNecessary(fileName);
        client.putObject(new PutObjectRequest(ossProperties.getBucketName(), moduleFolder + releaseFileName, new ByteArrayInputStream(data)));
        return ossProperties.getAccessPrefix() + folder + "/" + fileName;
    }

    private static String createFileNameIfNecessary(String filename) {
        return StringUtils.isNotBlank(filename) ? filename : UUID.randomUUID().toString();
    }

    private static String createFolderIfNecessary(String folder) {
        String moduleFolder = ossProperties.getFolder() + folder + "/";
        if (!client.doesObjectExist(ossProperties.getBucketName(), moduleFolder)) {
            client.putObject(ossProperties.getBucketName(), moduleFolder, new ByteArrayInputStream(new byte[0]));
        }
        return moduleFolder;
    }

    private static String upload2Oss(String folder, String fileName, InputStream inputStream) {
        if (StringUtils.isBlank(folder) || inputStream == null) {
            throw new RuntimeException("未设置folder或inputstream为null");
        }

        String moduleFolder = createFolderIfNecessary(folder);

        String releaseFileName = createFileNameIfNecessary(fileName);
        client.putObject(new PutObjectRequest(ossProperties.getBucketName(), moduleFolder + releaseFileName, inputStream));
        return ossProperties.getAccessPrefix() + folder + "/" + fileName;
    }


    public static String upload(String folder, String imageBase64OrUrl) {
        if (StringUtils.startsWithIgnoreCase(imageBase64OrUrl, "http")) {
            return imageBase64OrUrl;
        }

        Matcher matcher = imageBase64Pattern.matcher(imageBase64OrUrl);
        if (!matcher.find()) {
            throw new RuntimeException("不是合法的图片");
        }

        return upload2Oss(
                folder,
                UUID.randomUUID() + "." + matcher.group(1),
                Base64Utils.decodeFromString(matcher.group(2))
        );
    }

    public static String upload(String folder, String fileName, InputStream inputStream) {
        return upload2Oss(
                folder,
                UUID.randomUUID() + "." + Files.getFileExtension(fileName),
                inputStream
        );
    }

    public static String upload(String folder, MultipartFile file) throws IOException {
        return upload2Oss(
                folder,
                UUID.randomUUID() + "." + Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())),
                file.getInputStream()
        );
    }

}
