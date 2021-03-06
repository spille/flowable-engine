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
package org.activiti.rest.service.api.repository;

import org.activiti.dmn.api.DecisionTable;

/**
 * @author Yvo Swillens
 */
public class DecisionTableResponse {

  private String id;
  private String category;
  private String name;
  private String key;
  private String description;
  private int version;
  private String resourceName;
  private String deploymentId;
  private String parentDeploymentId;
  private String tenantId;
  private String url;

  public DecisionTableResponse(DecisionTable decisionTable) {
    this.id = decisionTable.getId();
    this.category = decisionTable.getCategory();
    this.name = decisionTable.getName();
    this.key = decisionTable.getKey();
    this.description = decisionTable.getDescription();
    this.version = decisionTable.getVersion();
    this.resourceName = decisionTable.getResourceName();
    this.deploymentId = decisionTable.getDeploymentId();
    this.parentDeploymentId = decisionTable.getParentDeploymentId();
    this.tenantId = decisionTable.getTenantId();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getDeploymentId() {
    return deploymentId;
  }

  public void setDeploymentId(String deploymentId) {
    this.deploymentId = deploymentId;
  }

  public String getParentDeploymentId() {
    return parentDeploymentId;
  }

  public void setParentDeploymentId(String parentDeploymentId) {
    this.parentDeploymentId = parentDeploymentId;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
