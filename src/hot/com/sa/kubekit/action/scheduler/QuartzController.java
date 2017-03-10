package com.sa.kubekit.action.scheduler;
import java.io.Serializable;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.async.QuartzTriggerHandle;
/**
* @author Oscar
*/
@Name("quartzController")
@AutoCreate
public class QuartzController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@In
	DummyControl dummyControl;
	
	private QuartzTriggerHandle quartzTriggerHandleDoJob;
	
	public void scheduleTimer() {
		//Job 1
		//quartzTriggerHandleDoJob = dummyControl.doJob(new Date(), "0 15 10 ? * MON");
		//Job N
	}
}