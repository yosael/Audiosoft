package com.sa.kubekit.action.medical;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.UsuarioHome;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.Internal;

@Name("internalDAO")
@Scope(ScopeType.CONVERSATION)
public class InternalDAO extends KubeDAO<Internal> {

	private static final long serialVersionUID = 1L;
	private Integer typeId;
	private String numId;

	@In(create = true)
	private UsuarioHome usuarioHome;

	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("internalDAO_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("internalDAO_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("internalDAO_deleted")));
		usuarioHome.setEnableMessages(false);
	}

	@Override
	public Internal createInstance() {
		Internal internal = super.createInstance();
		internal.setDoctor(false);
		return internal;
	}

	public void load() {
		try {
			Internal internal = (Internal) getEntityManager()
					.createQuery(
							"select s from Internal s where s.personId.numId = :numId and s.personId.identificationTypeId = :typeId")
					.setParameter("numId", getNumId()).setParameter("typeId",
							getTypeId()).getSingleResult();
			setInstance(internal);
			usuarioHome.select(internal);

		} catch (Exception e) {
			e.printStackTrace();
			clearInstance();
		}
	}

	@Override
	public void posDelete() {

	}

	@Override
	public void posModify() {

	}

	@Override
	public void posSave() {

	}

	@Override
	public boolean preDelete() {
		return true;
	}

	@Override
	public boolean preModify() {
		return true;
	}

	@Override
	public boolean preSave() {
		return true;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getNumId() {
		return numId;
	}

	public void setNumId(String numId) {
		this.numId = numId;
	}

}