/*
 * Copyright 1999-2010 University of Chicago
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.globus.workspace.remoting.admin;

public class VMTranslation {

    private String id;
    private String groupId;
    private String callerIdentity;
    private String state;

    public VMTranslation() {}

    public VMTranslation(String id, String groupId, String callerIdentity, String state) {
        this.id = id;
        this.groupId = groupId;
        this.callerIdentity = callerIdentity;
        this.state = state;
    }

    public String getId() {
        return id;
    }
    public String getGroupId() {
        return groupId;
    }
    public String getCallerIdentity() {
        return callerIdentity;
    }
    public String getState() {
        return state;
    }

}
