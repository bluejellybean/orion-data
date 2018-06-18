package org.kpmp.upload;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class FileHandler {

	FilePathHelper filePathHelper;

	@Autowired
	public FileHandler(FilePathHelper filePathHelper) {
		this.filePathHelper = filePathHelper;
	}

	public File saveMultipartFile(MultipartFile file, int packageId, String filename, boolean shouldAppend)
			throws IOException {
		String filePath = filePathHelper.getPackagePath("", Integer.toString(packageId));
		File packageDirectory = new File(filePath);
		if (!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}
		File fileToSave = new File(filePath + filename);

		if (shouldAppend) {
			FileUtils.writeByteArrayToFile(fileToSave, file.getBytes(), true);
		} else {
			FileUtils.writeByteArrayToFile(fileToSave, file.getBytes());
		}
		return fileToSave;
	}

}
