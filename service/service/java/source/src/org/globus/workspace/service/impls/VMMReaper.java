/*
 * Copyright 1999-2008 University of Chicago
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

package org.globus.workspace.service.impls;

import com.google.gson.Gson;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.workspace.Lager;
import org.globus.workspace.persistence.PersistenceAdapter;
import org.globus.workspace.scheduler.defaults.ResourcepoolEntry;
import org.globus.workspace.service.InstanceResource;
import org.globus.workspace.service.WorkspaceHome;
import org.globus.workspace.service.binding.vm.VirtualMachine;
import org.globus.workspace.service.impls.async.*;
import org.globus.workspace.WorkspaceConstants;
import org.globus.workspace.xen.xenssh.Query;
import org.nimbustools.api.services.rm.ManageException;

import java.util.*;

/**
 * Used to query for vm status
 *
 */
public class VMMReaper implements Runnable {

    // -------------------------------------------------------------------------
    // STATIC VARIABLES
    // -------------------------------------------------------------------------

    private static final Log logger =
            LogFactory.getLog(VMMReaper.class.getName());

    protected final VMMRequestFactory reqFactory;
    private final Gson gson = new Gson();

    // -------------------------------------------------------------------------
    // INSTANCE VARIABLES
    // -------------------------------------------------------------------------

    protected final ExecutorService executor;
    protected final WorkspaceHome home;
    protected final Lager lager;
    protected final PersistenceAdapter persistence;

    // -------------------------------------------------------------------------
    // CONSTRUCTORS
    // -------------------------------------------------------------------------

    public VMMReaper(ExecutorService executorService,
                     PersistenceAdapter persistenceImpl,
                     WorkspaceHome whome,
                     Lager lagerImpl) {

        if (executorService == null) {
            throw new IllegalArgumentException("executorService may not be null");
        }
        this.executor = executorService;

        if (persistenceImpl == null) {
            throw new IllegalArgumentException(
                    "persistenceImpl may not be null");
        }
        this.persistence = persistenceImpl;

        if (whome == null) {
            throw new IllegalArgumentException("whome may not be null");
        }
        this.home = whome;

        if (lagerImpl == null) {
            throw new IllegalArgumentException("lagerImpl may not be null");
        }
        this.lager = lagerImpl;
        this.reqFactory = new VMMRequestFactoryImpl(lager);
    }

    
    // -------------------------------------------------------------------------
    // implements Runnable
    // -------------------------------------------------------------------------

    public void run() {
        try {
            this.reaperVMM();
        } catch (Throwable t) {
            logger.error("Problem sweeping resources: " + t.getMessage(), t);
        }
    }


    // -------------------------------------------------------------------------
    // IMPL
    // -------------------------------------------------------------------------

    protected void reaperVMM() throws ManageException {

        if (this.lager.pollLog) {
            logger.trace("Querying VMM for VMs states");
        }

        final List<ResourcepoolEntry> vmms = this.home.vmmReaper();

        if (vmms == null || vmms.size() == 0) {
            return; // *** EARLY RETURN ***
        }

        for (ResourcepoolEntry r: vmms) {
            String hostname = r.getHostname();

            final VMMRequestContext requestContext =
                new VMMRequestContext(0, r.getHostname(), this.lager); //fixme remove id

            // These are the libvirt guest states
            // 1 = running; 2 = idle; 3 = paused; 4 = shutdown; 5 = shut off; 6 = crashed; 7 = dying

            VMMRequest req = reqFactory.query();
            //set context
            VirtualMachine m = new VirtualMachine();

            String state = null;
            try {
//                state = req.execute();
                req.execute();
            } catch (Exception e) {
                //do something
            }

            if (state != null) {
                HashMap<String,Integer> result = gson.fromJson(state, HashMap.class);
            }

        }

        InstanceResource[] ires =  this.home.findAll();

        //29 = CORRUPTED_GENERIC
        for (InstanceResource r: ires) {
            Integer state = r.getState();


            this.persistence.setState(r.getID(),
                                          WorkspaceConstants.STATE_CORRUPTED_GENERIC,
                                          null);
        }

        //TODO compare states with isInconsistent()
    }

    public int getCurrentState() {
        return 0;
    }

    public static boolean isInconsistent(Integer state, Integer queriedState) {
        return state != null && state.equals(queriedState);
    }
}