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
package org.activiti.engine.impl.util;

import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.event.ActivitiEngineEventType;
import org.activiti.engine.delegate.event.ActivitiEventDispatcher;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.IdentityLinkType;

/**
 * @author Joram Barrez
 * @author Marcus Klimstra
 */
public class TaskHelper {
  
  public static void completeTask(TaskEntity taskEntity, Map<String, Object> variables, 
    Map<String, Object> transientVariables, boolean localScope, CommandContext commandContext) {
    // Task complete logic
    
    if (taskEntity.getDelegationState() != null && taskEntity.getDelegationState().equals(DelegationState.PENDING)) {
      throw new ActivitiException("A delegated task cannot be completed, but should be resolved instead.");
    }

    if (variables != null) {
      if (localScope) {
        taskEntity.setVariablesLocal(variables);
      } else if (taskEntity.getExecutionId() != null) {
        taskEntity.setExecutionVariables(variables);
      } else {
        taskEntity.setVariables(variables);
      }
    }
    
    if (transientVariables != null) {
      if (localScope) {
        taskEntity.setTransientVariablesLocal(transientVariables);
      } else {
        taskEntity.setTransientVariables(transientVariables);
      }
    }

    commandContext.getProcessEngineConfiguration().getListenerNotificationHelper().executeTaskListeners(taskEntity, TaskListener.EVENTNAME_COMPLETE);
    if (Authentication.getAuthenticatedUserId() != null && taskEntity.getProcessInstanceId() != null) {
      ExecutionEntity processInstanceEntity = commandContext.getExecutionEntityManager().findById(taskEntity.getProcessInstanceId());
      commandContext.getIdentityLinkEntityManager().involveUser(processInstanceEntity, Authentication.getAuthenticatedUserId(),IdentityLinkType.PARTICIPANT);
    }

    ActivitiEventDispatcher eventDispatcher = Context.getProcessEngineConfiguration().getEventDispatcher();
    if (eventDispatcher.isEnabled()) {
      if (variables != null) {
        eventDispatcher.dispatchEvent(ActivitiEventBuilder.createEntityWithVariablesEvent(ActivitiEngineEventType.TASK_COMPLETED, taskEntity, variables, localScope));
      } else {
        eventDispatcher.dispatchEvent(ActivitiEventBuilder.createEntityEvent(ActivitiEngineEventType.TASK_COMPLETED, taskEntity));
      }
    }

    commandContext.getTaskEntityManager().deleteTask(taskEntity, null, false, false);

    // Continue process (if not a standalone task)
    if (taskEntity.getExecutionId() != null) {
      ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(taskEntity.getExecutionId());
      commandContext.getAgenda().planTriggerExecutionOperation(executionEntity);
    }
  }

}
