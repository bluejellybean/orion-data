package org.kpmp.packages;

public enum PackageKeys {

	CLASS("_class"), CREATED_AT("createdAt"), DATA_GENERATORS("dataGenerators"), DESCRIPTION("description"),
	DISPLAY_NAME("displayName"), EMAIL("email"), FILES("files"), FILE_NAME("fileName"), FIRST_NAME("firstName"),
	ID("_id"), LAST_NAME("lastName"), PACKAGE_TYPE("packageType"), PROTOCOL("protocol"),
	REGENERATE_ZIP("regenerateZip"), SHIBID("shibId"), SIZE("size"), SUBJECT_ID("subjectId"), SUBMITTER("submitter"),
	SUBMITTER_EMAIL("submitterEmail"), SUBMITTER_FIRST_NAME("submitterFirstName"), SUBMITTER_ID("$oid"),
	SUBMITTER_ID_OBJECT("$id"), SUBMITTER_LAST_NAME("submitterLastName"),
	TIS_INTERNAL_EXPERIMENT_ID("tisInternalExperimentID"), TIS_NAME("tisName"), VERSION("version"),
	LARGE_FILES_CHECKED("largeFilesChecked");

	private String key;

	private PackageKeys(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}
}
