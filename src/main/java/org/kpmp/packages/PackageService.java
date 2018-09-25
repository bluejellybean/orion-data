package org.kpmp.packages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.kpmp.UniversalIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final MessageFormat zipPackage = new MessageFormat("Service|{0}|{1}");

	private PackageRepository packageRepository;
	private UniversalIdGenerator universalIdGenerator;
	private PackageFileHandler packageFileHandler;
	private PackageZipService packageZipper;
	private FilePathHelper filePathHelper;

	@Autowired
	public PackageService(PackageRepository packageRepository, UniversalIdGenerator universalIdGenerator,
			PackageFileHandler packageFileHandler, PackageZipService packageZipper, FilePathHelper filePathHelper) {
		this.packageRepository = packageRepository;
		this.filePathHelper = filePathHelper;
		this.universalIdGenerator = universalIdGenerator;
		this.packageFileHandler = packageFileHandler;
		this.packageZipper = packageZipper;

	}

	public List<Package> findAllPackages() {
		List<Package> packages = packageRepository.findAll(new Sort(Sort.Direction.DESC, "createdAt"));
		for (Package packageToCheck : packages) {
			String zipFileName = filePathHelper.getZipFileName(packageToCheck.getPackageId());
			if (new File(zipFileName).exists()) {
				packageToCheck.setIsDownloadable(true);
			} else {
				packageToCheck.setIsDownloadable(false);
			}
		}
		return packages;
	}

	public Path getPackageFile(String packageId) {
		String packagePath = filePathHelper.getPackagePath(packageId);
		Path filePath = Paths.get(packagePath, packageId + ".zip");
		if (!filePath.toFile().exists()) {
			throw new RuntimeException("The file was not found: " + filePath.getFileName().toString());
		}
		return filePath;
	}

	public Package savePackageInformation(Package packageInfo) {
		packageInfo.setPackageId(universalIdGenerator.generateUniversalId());
		packageInfo.setCreatedAt(new Date());
		Package savedPackage = packageRepository.save(packageInfo);
		return savedPackage;
	}

	public Package findPackage(String packageId) {
		return packageRepository.findByPackageId(packageId);
	}

	public void saveFile(MultipartFile file, String packageId, String filename, long fileSize, boolean isInitialChunk)
			throws Exception {

		if (filename.equalsIgnoreCase("metadata.json")) {
			filename = filename.replace(".", "_user.");
		}

		if (isInitialChunk) {
			updatePackageInfo(packageId, filename, fileSize);
		}
		packageFileHandler.saveMultipartFile(file, packageId, filename, !isInitialChunk);
	}

	public void createZipFile(String packageId) {

		Package packageInfo = packageRepository.findByPackageId(packageId);

		new Thread() {
			public void run() {
				try {
					packageZipper.createZipFile(packageInfo);
				} catch (IOException e) {
					e.printStackTrace();
				}
				log.info(zipPackage.format(new Object[] { "createZipFile", packageId }));
			}
		}.start();
	}

	private void updatePackageInfo(String packageId, String filename, long fileSize) throws Exception {
		Package packageInformation = packageRepository.findByPackageId(packageId);
		if (packageContainsFileWithSameName(filename, packageInformation)) {
			throw new Exception("Cannot add multiple files with the same name to a package");
		}
		List<Attachment> attachments = packageInformation.getAttachments();
		Attachment attachment = createAttachment(filename, fileSize);
		attachments.add(attachment);
		packageInformation.setAttachments(attachments);
		packageRepository.save(packageInformation);
	}

	private boolean packageContainsFileWithSameName(String filename, Package packageInformation) {
		List<Attachment> attachments = packageInformation.getAttachments();
		for (Attachment attachment : attachments) {
			if (filename == attachment.getFileName()) {
				return true;
			}
		}
		return false;
	}

	private Attachment createAttachment(String filename, long fileSize) {
		Attachment attachment = new Attachment();
		attachment.setFileName(filename);
		attachment.setSize(fileSize);
		attachment.setId(universalIdGenerator.generateUniversalId());
		return attachment;
	}

}
