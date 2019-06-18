package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.UniversalIdGenerator;
import org.kpmp.logging.LoggingService;
import org.kpmp.users.User;
import org.kpmp.users.UserRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class CustomPackageRepositoryTest {

	@Mock
	private PackageRepository packageRepository;
	@Mock
	private MongoTemplate mongoTemplate;
	@Mock
	private UniversalIdGenerator universalIdGenerator;
	@Mock
	private UserRepository userRepo;
	@Mock
	private JsonWriterSettingsConstructor jsonWriterSettings;
	private CustomPackageRepository repo;
	@Mock
	private LoggingService logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		repo = new CustomPackageRepository(packageRepository, mongoTemplate, universalIdGenerator, userRepo,
				jsonWriterSettings, logger);
	}

	@After
	public void tearDown() throws Exception {
		repo = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSaveDynamicForm_happyPath() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		when(packageMetadata.toString()).thenReturn("{}");
		when(universalIdGenerator.generateUniversalId()).thenReturn("123").thenReturn("456");
		when(packageMetadata.getString("submitterEmail")).thenReturn("emailAddress");
		User user = mock(User.class);
		when(user.getId()).thenReturn("5c2f9e01cb5e710049f33121");
		when(userRepo.findByEmail("emailAddress")).thenReturn(user);
		JSONArray files = mock(JSONArray.class);
		when(files.length()).thenReturn(1);
		JSONObject file = mock(JSONObject.class);
		when(files.getJSONObject(0)).thenReturn(file);
		when(packageMetadata.getJSONArray("files")).thenReturn(files);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(mongoTemplate.getCollection("packages")).thenReturn(mongoCollection);

		String packageId = repo.saveDynamicForm(packageMetadata, "userId");

		ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
		verify(mongoCollection).insertOne(documentCaptor.capture());
		assertEquals("123", packageId);
		verify(file).put("_id", "456");
		verify(packageMetadata).remove("submitterEmail");
		verify(packageMetadata).remove("submitterFirstName");
		verify(packageMetadata).remove("submitterLastName");
		verify(packageMetadata).remove("submitter");
		Document actualDocument = documentCaptor.getValue();
		assertEquals("123", actualDocument.get("_id"));
		assertNotNull(actualDocument.get("createdAt"));
		assertEquals(false, actualDocument.get("regenerateZip"));
		DBRef submitter = (DBRef) actualDocument.get("submitter");
		assertEquals("users", submitter.getCollectionName());
		ObjectId objectId = (ObjectId) submitter.getId();
		assertEquals(new ObjectId("5c2f9e01cb5e710049f33121"), objectId);
		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		verify(logger).logInfoMessage(classCaptor.capture(), userIdCaptor.capture(), packageIdCaptor.capture(),
				uriCaptor.capture(), messageCaptor.capture());
		assertEquals(CustomPackageRepository.class, classCaptor.getValue());
		assertEquals("userId", userIdCaptor.getValue());
		assertEquals(packageId, packageIdCaptor.getValue());
		assertEquals("CustomPackageRepository.saveDynamicForm", uriCaptor.getValue());
		assertEquals(true, messageCaptor.getValue().startsWith("Timing|start|"));
		assertEquals(true, messageCaptor.getValue().endsWith("|emailAddress|123|1 files"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSaveDynamicForm_whenNewUser() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		JSONObject mockSubmitter = mock(JSONObject.class);
		when(packageMetadata.toString()).thenReturn("{}");
		when(universalIdGenerator.generateUniversalId()).thenReturn("123").thenReturn("456");
		when(packageMetadata.getString("submitterEmail")).thenReturn("emailAddress");
		when(packageMetadata.getJSONObject("submitter")).thenReturn(mockSubmitter);
		when(mockSubmitter.getString("displayName")).thenReturn("displayName");
		when(mockSubmitter.getString("email")).thenReturn("emailAddress2");
		when(mockSubmitter.getString("firstName")).thenReturn("firstName");
		when(mockSubmitter.getString("lastName")).thenReturn("lastName");
		when(mockSubmitter.has("displayName")).thenReturn(true);
		User user = mock(User.class);
		when(user.getId()).thenReturn("5c2f9e01cb5e710049f33121");
		when(userRepo.save(any(User.class))).thenReturn(user);
		when(userRepo.findByEmail("emailAddress")).thenReturn(null);
		JSONArray files = mock(JSONArray.class);
		when(files.length()).thenReturn(1);
		JSONObject file = mock(JSONObject.class);
		when(files.getJSONObject(0)).thenReturn(file);
		when(packageMetadata.getJSONArray("files")).thenReturn(files);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(mongoTemplate.getCollection("packages")).thenReturn(mongoCollection);

		String packageId = repo.saveDynamicForm(packageMetadata, "userId");

		ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
		verify(mongoCollection).insertOne(documentCaptor.capture());
		assertEquals("123", packageId);
		verify(file).put("_id", "456");
		verify(packageMetadata).remove("submitterEmail");
		verify(packageMetadata).remove("submitterFirstName");
		verify(packageMetadata).remove("submitterLastName");
		verify(packageMetadata).remove("submitter");
		Document actualDocument = documentCaptor.getValue();
		assertEquals("123", actualDocument.get("_id"));
		assertNotNull(actualDocument.get("createdAt"));
		assertEquals(false, actualDocument.get("regenerateZip"));
		DBRef submitter = (DBRef) actualDocument.get("submitter");
		assertEquals("users", submitter.getCollectionName());
		ObjectId objectId = (ObjectId) submitter.getId();
		assertEquals(new ObjectId("5c2f9e01cb5e710049f33121"), objectId);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepo).save(userCaptor.capture());
		User actualUser = userCaptor.getValue();
		assertEquals("displayName", actualUser.getDisplayName());
		assertEquals("emailAddress2", actualUser.getEmail());
		assertEquals("firstName", actualUser.getFirstName());
		assertEquals("lastName", actualUser.getLastName());
		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		verify(logger).logInfoMessage(classCaptor.capture(), userIdCaptor.capture(), packageIdCaptor.capture(),
				uriCaptor.capture(), messageCaptor.capture());
		assertEquals(CustomPackageRepository.class, classCaptor.getValue());
		assertEquals("userId", userIdCaptor.getValue());
		assertEquals(packageId, packageIdCaptor.getValue());
		assertEquals("CustomPackageRepository.saveDynamicForm", uriCaptor.getValue());
		assertEquals(true, messageCaptor.getValue().startsWith("Timing|start|"));
		assertEquals(true, messageCaptor.getValue().endsWith("|emailAddress|123|1 files"));
	}

	@Test
	public void testSave() {
		Package expectedPackage = mock(Package.class);
		Package packageInfo = expectedPackage;
		when(packageRepository.save(packageInfo)).thenReturn(expectedPackage);

		Package savedPackage = repo.save(packageInfo);

		verify(packageRepository).save(packageInfo);
		assertEquals(expectedPackage, savedPackage);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetJSONByPackageId() throws Exception {
		MongoDatabase db = mock(MongoDatabase.class);
		when(mongoTemplate.getDb()).thenReturn(db);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(db.getCollection("packages")).thenReturn(mongoCollection);
		FindIterable<Document> result = mock(FindIterable.class);
		when(mongoCollection.find(any(BasicDBObject.class))).thenReturn(result);
		Document document = mock(Document.class);
		JsonWriterSettings jsonWriterSettingsReturn = mock(JsonWriterSettings.class);
		when(jsonWriterSettings.getSettings()).thenReturn(jsonWriterSettingsReturn);
		when(document.toJson(any(JsonWriterSettings.class), any(DocumentCodec.class))).thenReturn(
				"{ \"_id\": \"123\", \"key\": \"value\", \"submitter\": { $id: { $oid: '123' }}, \"regenerateZip\": true, \"createdAt\": { $date: 123567 } }");
		when(result.first()).thenReturn(document);
		User user = mock(User.class);
		when(user.generateJSONForApp()).thenReturn("{user: information, exists: here}");
		when(userRepo.findById("123")).thenReturn(Optional.of(user));

		String packageJson = repo.getJSONByPackageId("123");

		assertEquals(
				"{\"regenerateZip\":true,\"createdAt\":{\"$date\":123567},\"submitter\":{\"exists\":\"here\",\"user\":\"information\"},\"_id\":\"123\",\"key\":\"value\"}",
				packageJson);
		ArgumentCaptor<BasicDBObject> queryCaptor = ArgumentCaptor.forClass(BasicDBObject.class);
		verify(mongoCollection).find(queryCaptor.capture());
		assertEquals("123", queryCaptor.getValue().get("_id"));
		ArgumentCaptor<JsonWriterSettings> jsonWriterCaptor = ArgumentCaptor.forClass(JsonWriterSettings.class);
		ArgumentCaptor<DocumentCodec> codecCaptor = ArgumentCaptor.forClass(DocumentCodec.class);
		verify(document).toJson(jsonWriterCaptor.capture(), codecCaptor.capture());
		assertEquals(jsonWriterSettingsReturn, jsonWriterCaptor.getValue());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testFindAll() throws Exception {
		Document firstResult = mock(Document.class);
		List<Document> results = Arrays.asList(firstResult);
		when(mongoTemplate.find(any(Query.class), any(Class.class), any(String.class))).thenReturn(results);
		when(firstResult.toJson(any(JsonWriterSettings.class), any(DocumentCodec.class))).thenReturn(
				"{ \"_id\": \"123\", \"key\": \"value\", \"submitter\": { $id: { $oid: '123' }}, \"regenerateZip\": true, \"createdAt\": { $date: 123567 } }");
		JsonWriterSettings jsonWriterSettingsReturn = mock(JsonWriterSettings.class);
		when(jsonWriterSettings.getSettings()).thenReturn(jsonWriterSettingsReturn);

		List<JSONObject> allJsons = repo.findAll();

		assertEquals(1, allJsons.size());

		ArgumentCaptor<JsonWriterSettings> jsonWriterCaptor = ArgumentCaptor.forClass(JsonWriterSettings.class);
		ArgumentCaptor<DocumentCodec> codecCaptor = ArgumentCaptor.forClass(DocumentCodec.class);
		verify(firstResult).toJson(jsonWriterCaptor.capture(), codecCaptor.capture());
		assertEquals(jsonWriterSettingsReturn, jsonWriterCaptor.getValue());
		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		ArgumentCaptor<Class> entityCaptor = ArgumentCaptor.forClass(Class.class);
		ArgumentCaptor<String> collectionCaptor = ArgumentCaptor.forClass(String.class);
		verify(mongoTemplate).find(queryCaptor.capture(), entityCaptor.capture(), collectionCaptor.capture());
		assertEquals("packages", collectionCaptor.getValue());
		assertEquals(Document.class, entityCaptor.getValue());
	}

	@Test
	public void testUpdateField() throws Exception {
		repo.updateField("id", "thisField", "a value");

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
		ArgumentCaptor<String> collectionNameCaptor = ArgumentCaptor.forClass(String.class);
		verify(mongoTemplate).updateFirst(queryCaptor.capture(), updateCaptor.capture(),
				collectionNameCaptor.capture());
		assertEquals("packages", collectionNameCaptor.getValue());
		Query actualQuery = queryCaptor.getValue();
		Document queryObject = actualQuery.getQueryObject();
		assertEquals("id", queryObject.get(PackageKeys.ID.getKey()));
		Update updater = updateCaptor.getValue();
		Document updateObject = updater.getUpdateObject();
		Object actualDocumnet = updateObject.get("$set");
		Document expectedDocument = new Document();
		expectedDocument.append("thisField", "a value");
		assertEquals(expectedDocument, actualDocumnet);
	}

}
