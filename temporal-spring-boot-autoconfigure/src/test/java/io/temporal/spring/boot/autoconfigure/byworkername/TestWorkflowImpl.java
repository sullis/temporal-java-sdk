/*
 * Copyright (C) 2022 Temporal Technologies, Inc. All Rights Reserved.
 *
 * Copyright (C) 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this material except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.temporal.spring.boot.autoconfigure.byworkername;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.NexusOperationOptions;
import io.temporal.workflow.NexusServiceOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.springframework.context.ConfigurableApplicationContext;

@WorkflowImpl(workers = "mainWorker")
public class TestWorkflowImpl implements TestWorkflow {

  // Test auto-wiring of the application context works, this is not indicative of a real-world use
  // case as the workflow implementation should be stateless.
  public TestWorkflowImpl(ConfigurableApplicationContext applicationContext) {}

  @Override
  public String execute(String input) {
    if (input.equals("nexus")) {
      Workflow.newNexusServiceStub(
              TestNexusService.class,
              NexusServiceOptions.newBuilder()
                  .setEndpoint("AutoDiscoveryByWorkerNameTestEndpoint")
                  .setOperationOptions(
                      NexusOperationOptions.newBuilder()
                          .setScheduleToCloseTimeout(Duration.ofSeconds(10))
                          .build())
                  .build())
          .operation(input);
    }
    return Workflow.newActivityStub(
            TestActivity.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(1))
                .validateAndBuildWithDefaults())
        .execute("done");
  }
}
