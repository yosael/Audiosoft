package com.sa.kubekit.action.medical;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeSearcher;
import com.sa.kubekit.action.util.UtilString;
import com.sa.model.sales.Service;

@Name("serviceSearch")
@Scope(ScopeType.CONVERSATION)
public class ServiceSearch extends KubeSearcher<Service> {

	private String text;
	private UtilString utilString = new UtilString();

	@SuppressWarnings("unchecked")
	@Override
	protected void searchImpl() {
		String jpql = "select m from Service m WHERE 1 = 1 AND m.estado = 'ACT' and (m.eliminado is null or m.eliminado<>'ELIM') ";
		String order = " order by m.name ASC";
		if (text != null && !text.isEmpty()) {
			jpql += " AND  "
					+ utilString.addOrToQuery(text.toUpperCase(), "m", "name")
					+ "  ";
		}
		setResultList(entityManager.createQuery(jpql + order).getResultList());
	}

	@Override
	protected boolean validateParams() {
		return true;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}