package com.sa.kubekit.action.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class KubeBundle extends HashMap<String, String> {

	private static final long serialVersionUID = 1L;

	private String nameResource;

	public KubeBundle() {
	}

	public KubeBundle(String nameResource) {
		this.nameResource = nameResource;
	}

	public String get(String key) {
		return getMessageFromResourceBundle(key);
	}

	public String getNameResource() {
		return nameResource;
	}

	public void setNameResource(String nameResource) {
		this.nameResource = nameResource;
	}

	private String getMessageFromResourceBundle(String key) {
		ResourceBundle bundle = null;
		String message = "";
		Locale locale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		try {
			bundle = ResourceBundle.getBundle(getNameResource(), locale,
					getCurrentLoader(getNameResource()));
		} catch (MissingResourceException e) {
			// bundle with this name not found;
		}
		if (bundle == null)
			return null;
		try {
			message = bundle.getString(key);
		} catch (Exception e) {
		}
		return message;
	}

	public static ClassLoader getCurrentLoader(Object fallbackClass) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
			loader = fallbackClass.getClass().getClassLoader();
		return loader;
	}

}