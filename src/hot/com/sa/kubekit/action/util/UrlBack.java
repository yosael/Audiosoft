package com.sa.kubekit.action.util;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("urlBack")
@Scope(ScopeType.PAGE)
public class UrlBack {
	private String back;

	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}

}
