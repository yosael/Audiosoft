package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Ocupacion;
import com.sa.model.medical.MotivoConsulta;

@Name("ocupacionHome")
@Scope(ScopeType.CONVERSATION)
public class OcupacionHome extends KubeDAO<Ocupacion> {

	private static final long serialVersionUID = 1L;
	
	
	private List<Ocupacion> ocupaciones = new ArrayList<Ocupacion>();
	private Integer idOcupacion;
	
	
	public void load()
	{
		ocupaciones = getEntityManager().createQuery("FROM Ocupacion c").getResultList();
	}
	

	public void cargarOcupacion()
	{
		try {
			
			setInstance(getEntityManager().find(Ocupacion.class, idOcupacion));
			
		} catch (Exception e) {
			
			clearInstance();
			instance = new Ocupacion();
			
		}
	}
	

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		
		
		if(instance.getNombre()==null || instance.getNombre().isEmpty())
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el nombre de la ocupacion");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}


	public List<Ocupacion> getOcupaciones() {
		return ocupaciones;
	}


	public void setOcupaciones(List<Ocupacion> ocupaciones) {
		this.ocupaciones = ocupaciones;
	}


	public Integer getIdOcupacion() {
		return idOcupacion;
	}


	public void setIdOcupacion(Integer idOcupacion) {
		this.idOcupacion = idOcupacion;
	}
	
	
	
	

}
