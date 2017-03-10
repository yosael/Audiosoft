package com.sa.kubekit.action.util;

import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Duration;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.Renderer;

@Name("emailService")
@AutoCreate
public class EmailService {

	@In(create = true)
	private Renderer renderer;

	@Asynchronous
	public void sendMessage(@Duration long delay, String template,
			Map<String, Object> items) {
		try {
			for (java.util.Map.Entry<String, Object> item : items.entrySet()) {
				Contexts.getEventContext().set(item.getKey(), item.getValue());
			}
			renderer.render(template);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}