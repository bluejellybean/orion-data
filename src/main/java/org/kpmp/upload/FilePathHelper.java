package org.kpmp.upload;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;

public class FilePathHelper {

    @Value("${file.base.path}")
    private String basePath;

    @Value("${metadata.file.name}")
    private String metadataFileName;

    public String getPackagePath(String prefix, String suffix) {
        return basePath + File.separator + prefix + "package" + suffix + File.separator;
    }

    public String getMetadataFileName() {
        return metadataFileName;
    }


}
