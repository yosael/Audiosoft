package com.sa.kubekit.action.sales;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Producto;
import com.sa.model.sales.CodigoDescuentoCorp;
import com.sa.model.sales.DetDescuentoCorp;
import com.sa.model.sales.Service;

@Name("codigoDescuentoCorpHome")
@Scope(ScopeType.CONVERSATION)
public class CodigoDescuentoCorpHome extends KubeDAO<CodigoDescuentoCorp> {
	
	
	private static final long serialVersionUID = 1L;
	
	
	List<CodigoDescuentoCorp> listaCodigos;
	Integer codId;
	List<DetDescuentoCorp> listaDetalle =  new ArrayList<DetDescuentoCorp>();

	
	public void load()
	{
		try {
			
			setInstance(getEntityManager().find(CodigoDescuentoCorp.class, codId));
			setListaDetalle(instance.getDetDescuentos());
			
		} catch (Exception e) {
			clearInstance();
			setInstance(new CodigoDescuentoCorp());
		}
	}
	
	
	public void obtenerListaCodigos()
	{
		listaCodigos = new ArrayList<CodigoDescuentoCorp>();
		listaCodigos = getEntityManager().createQuery("SELECT c FROM CodigoDescuentoCorp c").getResultList();
	}
	
	public void agregarProducto(Producto producto)
	{
		
			
		DetDescuentoCorp detDesc = new DetDescuentoCorp();
		detDesc.setCodigo(instance);
		detDesc.setProducto(producto);
		
		for(DetDescuentoCorp d:listaDetalle)
		{
			if(d.getProducto()!=null)
			{
				if(d.getProducto().equals(producto))
				{
					FacesMessages.instance().add(Severity.INFO,"El producto ya fue agregado");
					return;
				}
			}
		}
		
		listaDetalle.add(detDesc);
		
		if(instance.getCodigo()!=null)
		{
			instance.setCodigo(instance.getCodigo());
		}
	}
	
	public void agregarServicio(Service servicio)
	{
		
		
		DetDescuentoCorp detDesc = new DetDescuentoCorp();
		detDesc.setCodigo(instance);
		detDesc.setService(servicio);
		
		for(DetDescuentoCorp d:listaDetalle)
		{
			if(d.getService()!=null)
			{
				if(d.getService().equals(servicio))
				{
					FacesMessages.instance().add(Severity.INFO,"El servicio ya fue agregado");
					return;
				}
			}
		}
		
		listaDetalle.add(detDesc);
		
		if(instance.getCodigo()!=null)
		{
			instance.setCodigo(instance.getCodigo());
		}
	}
	

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		
		System.out.println("Codigo preingresado");
		
		/*if(instance.getClienteCorp()==null)
		{
			FacesMessages.instance().add(Severity.INFO,"Debe seleccionar el cliente corporativo");
			return false;
		}*/
		
		if(instance.getCodigo()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el codigo del descuento");
			return false;
		}
		
		if(instance.getEstado()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el estado");
			return false;
		}
		
		if(instance.getFechaEmision()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese la fecha de emision");
			return false;
		}
		
		if(instance.getFechaFinalizacion()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese la fecha de finalizacion");
			return false;
		}
		
		
		if(listaDetalle.size()<=0)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe agregar al menos un detalle");
			return false;
		}
		
		List<CodigoDescuentoCorp> listValidar = new ArrayList<CodigoDescuentoCorp>();
		listValidar = getEntityManager().createQuery("SELECT cd FROM CodigoDescuentoCorp cd where cd.codigo=:cod").setParameter("cod", instance.getCodigo()).getResultList();
		
		if(listValidar!=null && listValidar.size()>0)
		{
			FacesMessages.instance().add(Severity.WARN,"El codigo ya existe");
			return false;
		}
		
		instance.setDetDescuentos(listaDetalle);
		
		return true;
	}
	
	public void removerItem(DetDescuentoCorp det)
	{
		if(det.getIdDetCod()==null)
		{
			listaDetalle.remove(det);
			System.out.println("Elimino en memoria");
		}
		else
		{
			
			listaDetalle.remove(det);
			getEntityManager().remove(getEntityManager().find(DetDescuentoCorp.class, det.getIdDetCod()));
			getEntityManager().refresh(instance);
			//getEntityManager().flush();
			
			System.out.println("Elimino desde la base");
		}
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		
		System.out.println("Entro a modificar");
		if(instance.getCodigo()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el codigo del descuento");
			return false;
		}
		
		if(instance.getEstado()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el estado");
			return false;
		}
		
		if(instance.getFechaEmision()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese la fecha de emision");
			return false;
		}
		
		if(instance.getFechaFinalizacion()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese la fecha de finalizacion");
			return false;
		}
		
		
		if(listaDetalle.size()<=0)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe agregar al menos un detalle");
			return false;
		}
		
		/*System.out.println("codigo"+codId);
		System.out.println("codigo instancia"+instance.getCodigo());
		if(!codId.toString().equals(instance.getCodigo()))
		{
			System.out.println("Entro a validar");
			
			List<CodigoDescuentoCorp> listValidar = new ArrayList<CodigoDescuentoCorp>();
			listValidar = getEntityManager().createQuery("SELECT cd FROM CodigoDescuentoCorp cd where cd.codigo=:cod").setParameter("cod", instance.getCodigo()).getResultList();
			
			if(listValidar!=null && listValidar.size()>0)
			{
				FacesMessages.instance().add(Severity.WARN,"El codigo ya existe");
				return false;
			}
			
			
		}*/
		
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
		
		System.out.println("codigo ingresado");
		
		boolean entro=false;
		for(DetDescuentoCorp det: listaDetalle)
		{
			if(det.getIdDetCod()==null)
			{
				getEntityManager().persist(det);
				entro=true;
			}
		}
		
		if(entro)
			getEntityManager().flush();
		
		FacesMessages.instance().clear();
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		boolean entro=false;
		for(DetDescuentoCorp det: listaDetalle)
		{
			if(det.getIdDetCod()==null)
			{
				getEntityManager().persist(det);
				entro=true;
			}
		}
		
		if(entro)
			getEntityManager().flush();
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<CodigoDescuentoCorp> getListaCodigos() {
		return listaCodigos;
	}

	public void setListaCodigos(List<CodigoDescuentoCorp> listaCodigos) {
		this.listaCodigos = listaCodigos;
	}


	public Integer getCodId() {
		return codId;
	}


	public void setCodId(Integer codId) {
		this.codId = codId;
	}


	public List<DetDescuentoCorp> getListaDetalle() {
		return listaDetalle;
	}


	public void setListaDetalle(List<DetDescuentoCorp> listaDetalle) {
		this.listaDetalle = listaDetalle;
	}
	
	
	
	

}
