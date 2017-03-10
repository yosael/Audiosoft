package com.sa.kubekit.action.i18n;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;

@Name("bundleCreator")
@Scope(ScopeType.APPLICATION)
@Startup
public class BundleCreator {

	@Create
	public void init() {
		Contexts.getApplicationContext().set("kubekit_messages",
				new KubeBundle("kubekit_messages"));
		Contexts.getApplicationContext().set("menu_messages",
				new KubeBundle("menu_messages"));
		Contexts.getApplicationContext().set("sainv_messages",
				new KubeBundle("sainv_messages"));
		Contexts.getApplicationContext().set("sainv_view_messages",
				new KubeBundle("sainv_view_messages"));
	}
}