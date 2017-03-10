package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.Specialty;
import com.sa.model.sales.Service;

@Name("specialtyDAO")
@Scope(ScopeType.CONVERSATION)
public class SpecialtyDAO extends KubeDAO<Specialty> {

	private static final long serialVersionUID = 1L;

	private Integer specialtyId;
	private List<Service> servicesList = new ArrayList<Service>();

	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("specialtyDAO_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("specialtyDAO_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("specialtyDAO_deleted")));
	}

	public void load() {
		try {
			setInstance(getEntityManager().find(Specialty.class, specialtyId));
			setServicesList(new ArrayList<Service>(getInstance().getServices()));
		} catch (Exception e) {
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
		getInstance().setServices(new HashSet<Service>(servicesList));
		return codeValidation();
	}

	@Override
	public boolean preSave() {
		getInstance().setServices(new HashSet<Service>(servicesList));
		return codeValidation();
	}

	public boolean codeValidation() {
		try {
			Specialty spec = (Specialty) getEntityManager().createQuery(
					"select e from Specialty e where e.code like :code")
					.setParameter("code", getInstance().getCode())
					.getSingleResult();
			if (spec.getId().equals(getInstance().getId()))
				return true;
			FacesMessages.instance().add(
					sainv_messages.get("specialtyDAO_error1"));
		} catch (NoResultException ne) {
			return true;
		} catch (NonUniqueResultException nu) {

		}
		return false;
	}

	public Integer getSpecialtyId() {
		return specialtyId;
	}

	public void setSpecialtyId(Integer specialtyId) {
		this.specialtyId = specialtyId;
	}

	public List<Service> getServicesList() {
		return servicesList;
	}

	public void setServicesList(List<Service> servicesList) {
		this.servicesList = servicesList;
	}

}