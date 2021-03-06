/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.rest.content.service.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activiti.content.api.ContentItem;
import org.activiti.content.api.ContentService;
import org.activiti.content.engine.ContentEngine;
import org.activiti.content.engine.ContentEngineConfiguration;
import org.activiti.content.engine.impl.test.AbstractContentTestCase;
import org.activiti.content.engine.test.ContentTestHelper;
import org.activiti.rest.content.ContentRestUrlBuilder;
import org.activiti.rest.content.conf.ApplicationConfiguration;
import org.activiti.rest.content.util.TestServerUtil;
import org.activiti.rest.content.util.TestServerUtil.TestServer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.eclipse.jetty.server.Server;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import junit.framework.AssertionFailedError;

public abstract class BaseSpringContentRestTestCase extends AbstractContentTestCase {

  private static Logger log = LoggerFactory.getLogger(BaseSpringContentRestTestCase.class);

  protected static String SERVER_URL_PREFIX;
  protected static ContentRestUrlBuilder URL_BUILDER;

  protected static Server server;
  protected static ApplicationContext appContext;
  protected ObjectMapper objectMapper = new ObjectMapper();

  protected static ContentEngine contentEngine;

  protected Throwable exception;

  protected static ContentEngineConfiguration contentEngineConfiguration;
  protected static ContentService contentService;

  protected static CloseableHttpClient client;
  protected static LinkedList<CloseableHttpResponse> httpResponses = new LinkedList<CloseableHttpResponse>();

  static {

    TestServer testServer = TestServerUtil.createAndStartServer(ApplicationConfiguration.class);
    server = testServer.getServer();
    appContext = testServer.getApplicationContext();
    SERVER_URL_PREFIX = testServer.getServerUrlPrefix();
    URL_BUILDER = ContentRestUrlBuilder.usingBaseUrl(SERVER_URL_PREFIX);

    // Lookup services
    contentEngine = appContext.getBean(ContentEngine.class);
    contentEngineConfiguration = contentEngine.getContentEngineConfiguration();
    contentService = contentEngine.getContentService();

    // Create http client for all tests
    CredentialsProvider provider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("kermit", "kermit");
    provider.setCredentials(AuthScope.ANY, credentials);
    client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

    // Clean shutdown
    Runtime.getRuntime().addShutdownHook(new Thread() {

      @Override
      public void run() {

        if (client != null) {
          try {
            client.close();
          } catch (IOException e) {
            log.error("Could not close http client", e);
          }
        }

        if (server != null && server.isRunning()) {
          try {
            server.stop();
          } catch (Exception e) {
            log.error("Error stopping server", e);
          }
        }
      }
    });
  }

  @Override
  public void runBare() throws Throwable {
    try {
      super.runBare();
      
    } catch (AssertionFailedError e) {
      log.error(EMPTY_LINE);
      log.error("ASSERTION FAILED: {}", e, e);
      exception = e;
      throw e;

    } catch (Throwable e) {
      log.error(EMPTY_LINE);
      log.error("EXCEPTION: {}", e, e);
      exception = e;
      throw e;

    } finally {
      ContentTestHelper.assertAndEnsureCleanDb(contentEngine);
      contentEngineConfiguration.getClock().reset();
      closeHttpConnections();
    }
  }

  /**
   * IMPORTANT: calling method is responsible for calling close() on returned {@link HttpResponse} to free the connection.
   */
  public CloseableHttpResponse executeRequest(HttpUriRequest request, int expectedStatusCode) {
    return internalExecuteRequest(request, expectedStatusCode, true);
  }

  /**
   * IMPORTANT: calling method is responsible for calling close() on returned {@link HttpResponse} to free the connection.
   */
  public CloseableHttpResponse executeBinaryRequest(HttpUriRequest request, int expectedStatusCode) {
    return internalExecuteRequest(request, expectedStatusCode, false);
  }

  protected CloseableHttpResponse internalExecuteRequest(HttpUriRequest request, int expectedStatusCode, boolean addJsonContentType) {
    CloseableHttpResponse response = null;
    try {
      if (addJsonContentType && request.getFirstHeader(HttpHeaders.CONTENT_TYPE) == null) {
        // Revert to default content-type
        request.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
      }
      response = client.execute(request);
      Assert.assertNotNull(response.getStatusLine());

      int responseStatusCode = response.getStatusLine().getStatusCode();
      if (expectedStatusCode != responseStatusCode) {
        log.info("Wrong status code : " + responseStatusCode + ", but should be " + expectedStatusCode);
        log.info("Response body: " + IOUtils.toString(response.getEntity().getContent()));
      }

      Assert.assertEquals(expectedStatusCode, responseStatusCode);
      httpResponses.add(response);
      return response;

    } catch (ClientProtocolException e) {
      Assert.fail(e.getMessage());
    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
    return null;
  }

  public void closeResponse(CloseableHttpResponse response) {
    if (response != null) {
      try {
        response.close();
      } catch (IOException e) {
        fail("Could not close http connection");
      }
    }
  }

  protected void closeHttpConnections() {
    for (CloseableHttpResponse response : httpResponses) {
      if (response != null) {
        try {
          response.close();
        } catch (IOException e) {
          log.error("Could not close http connection", e);
        }
      }
    }
    httpResponses.clear();
  }

  protected String encode(String string) {
    if (string != null) {
      try {
        return URLEncoder.encode(string, "UTF-8");
      } catch (UnsupportedEncodingException uee) {
        throw new IllegalStateException("JVM does not support UTF-8 encoding.", uee);
      }
    }
    return null;
  }

  /**
   * Checks if the returned "data" array (child-node of root-json node returned by invoking a GET on the given url) contains entries with the given ID's.
   */
  protected void assertResultsPresentInDataResponse(String url, String... expectedResourceIds) throws JsonProcessingException, IOException {
    int numberOfResultsExpected = expectedResourceIds.length;

    // Do the actual call
    CloseableHttpResponse response = executeRequest(new HttpGet(SERVER_URL_PREFIX + url), HttpStatus.SC_OK);

    // Check status and size
    JsonNode dataNode = objectMapper.readTree(response.getEntity().getContent()).get("data");
    closeResponse(response);
    assertEquals(numberOfResultsExpected, dataNode.size());

    // Check presence of ID's
    List<String> toBeFound = new ArrayList<String>(Arrays.asList(expectedResourceIds));
    Iterator<JsonNode> it = dataNode.iterator();
    while (it.hasNext()) {
      String id = it.next().get("id").textValue();
      toBeFound.remove(id);
    }
    assertTrue("Not all expected ids have been found in result, missing: " + StringUtils.join(toBeFound, ", "), toBeFound.isEmpty());
  }

  protected void assertResultsPresentInPostDataResponseWithStatusCheck(String url, ObjectNode body, int expectedStatusCode, String... expectedResourceIds) throws JsonProcessingException, IOException {
    int numberOfResultsExpected = 0;
    if (expectedResourceIds != null) {
      numberOfResultsExpected = expectedResourceIds.length;
    }

    // Do the actual call
    HttpPost post = new HttpPost(SERVER_URL_PREFIX + url);
    post.setEntity(new StringEntity(body.toString()));
    CloseableHttpResponse response = executeRequest(post, expectedStatusCode);

    if (expectedStatusCode == HttpStatus.SC_OK) {
      // Check status and size
      JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());
      JsonNode dataNode = rootNode.get("data");
      assertEquals(numberOfResultsExpected, dataNode.size());

      // Check presence of ID's
      if (expectedResourceIds != null) {
        List<String> toBeFound = new ArrayList<String>(Arrays.asList(expectedResourceIds));
        Iterator<JsonNode> it = dataNode.iterator();
        while (it.hasNext()) {
          String id = it.next().get("id").textValue();
          toBeFound.remove(id);
        }
        assertTrue("Not all entries have been found in result, missing: " + StringUtils.join(toBeFound, ", "), toBeFound.isEmpty());
      }
    }

    closeResponse(response);
  }
  
  /**
   * Extract a date from the given string. Assertion fails when invalid date has been provided.
   */
  protected Date getDateFromISOString(String isoString) {
    DateTimeFormatter dateFormat = ISODateTimeFormat.dateTime();
    try {
      return dateFormat.parseDateTime(isoString).toDate();
    } catch (IllegalArgumentException iae) {
      fail("Illegal date provided: " + isoString);
      return null;
    }
  }
  
  protected String createContentItem(String name, String mimeType, String taskId, String processInstanceId,
      String tenantId, String createdBy, String lastModifiedBy) {
    
    ContentItem contentItem = fillAndCreateContentItem(name, mimeType, taskId, processInstanceId, tenantId, createdBy, lastModifiedBy);
    contentService.saveContentItem(contentItem);
    return contentItem.getId();
  }
  
  protected String createContentItem(String name, String mimeType, String taskId, String processInstanceId,
      String tenantId, String createdBy, String lastModifiedBy, InputStream fileStream) {
    
    ContentItem contentItem = fillAndCreateContentItem(name, mimeType, taskId, processInstanceId, tenantId, createdBy, lastModifiedBy);
    contentService.saveContentItem(contentItem, fileStream);
    return contentItem.getId();
  }
  
  protected ContentItem fillAndCreateContentItem(String name, String mimeType, String taskId, String processInstanceId,
      String tenantId, String createdBy, String lastModifiedBy) {
    
    ContentItem contentItem = contentService.newContentItem();
    contentItem.setName(name);
    contentItem.setMimeType(mimeType);
    contentItem.setTaskId(taskId);
    contentItem.setProcessInstanceId(processInstanceId);
    contentItem.setTenantId(tenantId);
    contentItem.setCreatedBy(createdBy);
    contentItem.setLastModifiedBy(lastModifiedBy);
    
    return contentItem;
  }
}
