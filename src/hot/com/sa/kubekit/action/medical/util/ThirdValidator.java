package com.sa.kubekit.action.medical.util;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.model.medical.id.ThirdId;

@Name("thirdValidator")
@Scope(ScopeType.CONVERSATION)
public class ThirdValidator {

	@In
	private EntityManager entityManager;

	public boolean validate(ThirdId id) {
		try {
			entityManager
					.createQuery(
							"select p from Third p where p.thirdId.numId = :numId and p.thirdId.identificationTypeId = :idTypeId")
					.setParameter("numId", id.getNumId()).setParameter(
							"idTypeId", id.getIdentificationTypeId())
					.getSingleResult();
			return false;
		} catch (NoResultException nre) {
			return true;
		}

	}
}
