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

package org.activiti.content.engine.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.activiti.content.api.ContentItem;
import org.activiti.content.api.ContentItemQuery;
import org.activiti.content.engine.impl.interceptor.CommandContext;
import org.activiti.content.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.Page;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public class ContentItemQueryImpl extends AbstractQuery<ContentItemQuery, ContentItem> implements ContentItemQuery, Serializable {

  private static final long serialVersionUID = 1L;
  protected String id;
  protected Set<String> ids;
  protected String mimeType;
  protected String mimeTypeLike;
  protected String taskId;
  protected String taskIdLike;
  protected String processInstanceId;
  protected String processInstanceIdLike;
  protected String contentStoreId;
  protected String contentStoreIdLike;
  protected String contentStoreName;
  protected String contentStoreNameLike;
  protected String field;
  protected String fieldLike;
  protected Boolean contentAvailable;
  protected Long contentSize;
  protected Long minContentSize;
  protected Long maxContentSize;
  protected Date createdDate;
  protected Date createdDateBefore;
  protected Date createdDateAfter;
  protected String createdBy;
  protected String createdByLike;
  protected Date lastModifiedDate;
  protected Date lastModifiedDateBefore;
  protected Date lastModifiedDateAfter;
  protected String lastModifiedBy;
  protected String lastModifiedByLike;
  protected String tenantId;
  protected String tenantIdLike;
  protected boolean withoutTenantId;

  public ContentItemQueryImpl() {
  }

  public ContentItemQueryImpl(CommandContext commandContext) {
    super(commandContext);
  }

  public ContentItemQueryImpl(CommandExecutor commandExecutor) {
    super(commandExecutor);
  }

  public ContentItemQueryImpl id(String id) {
    this.id = id;
    return this;
  }
  
  public ContentItemQueryImpl ids(Set<String> ids) {
    this.ids = ids;
    return this;
  }
  
  public ContentItemQueryImpl mimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }
  
  public ContentItemQueryImpl mimeTypeLike(String mimeTypeLike) {
    this.mimeTypeLike = mimeTypeLike;
    return this;
  }

  public ContentItemQueryImpl taskId(String taskId) {
    this.taskId = taskId;
    return this;
  }
  
  public ContentItemQueryImpl taskIdLike(String taskIdLike) {
    this.taskIdLike = taskIdLike;
    return this;
  }
  
  public ContentItemQueryImpl processInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
    return this;
  }

  public ContentItemQueryImpl processInstanceIdLike(String processInstanceIdLike) {
    this.processInstanceIdLike = processInstanceIdLike;
    return this;
  }
  
  public ContentItemQueryImpl field(String field) {
    this.field = field;
    return this;
  }

  public ContentItemQueryImpl fieldLike(String fieldLike) {
    this.fieldLike = fieldLike;
    return this;
  }
  
  public ContentItemQueryImpl contentStoreId(String contentStoreId) {
    this.contentStoreId = contentStoreId;
    return this;
  }

  public ContentItemQueryImpl contentStoreIdLike(String contentStoreIdLike) {
    this.contentStoreIdLike = contentStoreIdLike;
    return this;
  }
  
  public ContentItemQueryImpl contentStoreName(String contentStoreName) {
    this.contentStoreName = contentStoreName;
    return this;
  }

  public ContentItemQueryImpl contentStoreNameLike(String contentStoreNameLike) {
    this.contentStoreNameLike = contentStoreNameLike;
    return this;
  }
  
  public ContentItemQueryImpl contentAvailable(Boolean contentAvailable) {
    this.contentAvailable = contentAvailable;
    return this;
  }
  
  public ContentItemQueryImpl contentSize(Long contentSize) {
    this.contentSize = contentSize;
    return this;
  }
  
  public ContentItemQueryImpl minContentSize(Long minContentSize) {
    this.minContentSize = minContentSize;
    return this;
  }
  
  public ContentItemQueryImpl maxContentSize(Long maxContentSize) {
    this.maxContentSize = maxContentSize;
    return this;
  }
  
  public ContentItemQueryImpl createdDate(Date createdDate) {
    this.createdDate = createdDate;
    return this;
  }
  
  public ContentItemQueryImpl createdDateBefore(Date createdDateBefore) {
    this.createdDateBefore = createdDateBefore;
    return this;
  }
  
  public ContentItemQueryImpl createdDateAfter(Date createdDateAfter) {
    this.createdDateAfter = createdDateAfter;
    return this;
  }
  
  public ContentItemQueryImpl createdBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public ContentItemQueryImpl createdByLike(String createdByLike) {
    this.createdByLike = createdByLike;
    return this;
  }
  
  public ContentItemQueryImpl lastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    return this;
  }
  
  public ContentItemQueryImpl lastModifiedDateBefore(Date lastModifiedDateBefore) {
    this.lastModifiedDateBefore = lastModifiedDateBefore;
    return this;
  }
  
  public ContentItemQueryImpl lastModifiedDateAfter(Date lastModifiedDateAfter) {
    this.lastModifiedDateAfter = lastModifiedDateAfter;
    return this;
  }
  
  public ContentItemQueryImpl lastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
    return this;
  }

  public ContentItemQueryImpl lastModifiedByLike(String lastModifiedByLike) {
    this.lastModifiedByLike = lastModifiedByLike;
    return this;
  }

  public ContentItemQueryImpl tenantId(String tenantId) {
    if (tenantId == null) {
      throw new ActivitiIllegalArgumentException("deploymentTenantId is null");
    }
    this.tenantId = tenantId;
    return this;
  }

  public ContentItemQueryImpl tenantIdLike(String tenantIdLike) {
    if (tenantIdLike == null) {
      throw new ActivitiIllegalArgumentException("deploymentTenantIdLike is null");
    }
    this.tenantIdLike = tenantIdLike;
    return this;
  }

  public ContentItemQueryImpl withoutTenantId() {
    this.withoutTenantId = true;
    return this;
  }

  // sorting ////////////////////////////////////////////////////////

  public ContentItemQuery orderByCreatedDate() {
    return orderBy(ContentItemQueryProperty.CREATED_DATE);
  }

  public ContentItemQuery orderByTenantId() {
    return orderBy(ContentItemQueryProperty.TENANT_ID);
  }

  // results ////////////////////////////////////////////////////////

  @Override
  public long executeCount(CommandContext commandContext) {
    checkQueryOk();
    return commandContext.getContentItemEntityManager().findContentItemCountByQueryCriteria(this);
  }

  @Override
  public List<ContentItem> executeList(CommandContext commandContext, Page page) {
    checkQueryOk();
    return commandContext.getContentItemEntityManager().findContentItemsByQueryCriteria(this, page);
  }

  // getters ////////////////////////////////////////////////////////

  public String getId() {
    return id;
  }

  public Set<String> getIds() {
    return ids;
  }

  public String getTaskId() {
    return taskId;
  }

  public String getTaskIdLike() {
    return taskIdLike;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public String getProcessInstanceIdLike() {
    return processInstanceIdLike;
  }

  public String getContentStoreId() {
    return contentStoreId;
  }

  public String getContentStoreIdLike() {
    return contentStoreIdLike;
  }

  public String getContentStoreName() {
    return contentStoreName;
  }

  public String getContentStoreNameLike() {
    return contentStoreNameLike;
  }

  public String getMimeType() {
    return mimeType;
  }

  public String getMimeTypeLike() {
    return mimeTypeLike;
  }

  public String getField() {
    return field;
  }

  public String getFieldLike() {
    return fieldLike;
  }

  public Boolean getContentAvailable() {
    return contentAvailable;
  }

  public Long getContentSize() {
    return contentSize;
  }

  public Long getMinContentSize() {
    return minContentSize;
  }

  public Long getMaxContentSize() {
    return maxContentSize;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public Date getCreatedDateBefore() {
    return createdDateBefore;
  }

  public Date getCreatedDateAfter() {
    return createdDateAfter;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedByLike() {
    return createdByLike;
  }

  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public Date getLastModifiedDateBefore() {
    return lastModifiedDateBefore;
  }

  public Date getLastModifiedDateAfter() {
    return lastModifiedDateAfter;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public String getLastModifiedByLike() {
    return lastModifiedByLike;
  }

  public String getTenantId() {
    return tenantId;
  }

  public String getTenantIdLike() {
    return tenantIdLike;
  }

  public boolean isWithoutTenantId() {
    return withoutTenantId;
  }
}
