package com.sa.kubekit.action.sales;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.sales.Iva;

@Name("ivaHome")
@Scope(ScopeType.EVENT)
public class IvaHome extends KubeDAO<Iva> {
	
	
	private static final long serialVersionUID = 1L;
	
	public void load()
	{
		List<Iva> ivas = new ArrayList<Iva>();
		
		
		try {
			
			if(ivas!=null && ivas.size()>0)
			{
				ivas = getEntityManager().createQuery("SELECT i FROM Iva i").getResultList();
				setInstance(ivas.get(0));
			}
			else
			{
				setInstance(new Iva());
			}
			
		} catch (Exception e) {
			
			setInstance(new Iva());
		}
			 
		
		
	}

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		
		if(instance.getPorcentaje()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el porcentaje");
			return false;
		}
		
		instance.setFechaModificacion(new Date());
		
		
		return true;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		
		if(instance.getPorcentaje()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el porcentaje");
			return false;
		}
		
		instance.setFechaModificacion(new Date());
		
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
	
	

}
