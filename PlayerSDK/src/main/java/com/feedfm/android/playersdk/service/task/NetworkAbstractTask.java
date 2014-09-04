package com.feedfm.android.playersdk.service.task;

import com.feedfm.android.playersdk.service.TaskQueueManager;
import com.feedfm.android.playersdk.service.webservice.Webservice;

/**
 * Created by mharkins on 9/2/14.
 */
public abstract class NetworkAbstractTask<Params, Progress, Result> extends PlayerAbstractTask<Params, Progress, Result> {
    protected Webservice mWebservice;

    public NetworkAbstractTask(TaskQueueManager queueManager, Webservice mWebservice) {
        super(queueManager);

        this.mWebservice = mWebservice;
    }
}
