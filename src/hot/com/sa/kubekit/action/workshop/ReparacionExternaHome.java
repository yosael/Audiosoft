package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.DetalleReparacionExterna;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.ReparacionExterna;
import com.sa.model.security.Empresa;

@Name("reparacionExternaHome")
@Scope(ScopeType.CONVERSATION)
public class ReparacionExternaHome extends KubeDAO<ReparacionExterna> {
	
	
	private static final long serialVersionUID = 1L;
	private List<ReparacionExterna> resultList;
	private int idReparacionExterna;
	private List<DetalleReparacionExterna> detalleReparacion;
	
	public void load()
	{
		
		try{
			setInstance((ReparacionExterna) getEntityManager().createQuery("select r from ReparacionExterna r where r.idReparacionExterna=:idReparacion")
					.setParameter("idReparacion", idReparacionExterna).getSingleResult());
			setDetalleReparacion(instance.getDetalleReparacion());
			
			
			
		}catch (Exception e) {
			clearInstance();
			
			//Extraer empresa audiomed
			Empresa empresa = new Empresa();
			 empresa = (Empresa) getEntityManager().createQuery("SELECT e FROM ReparacionExterna e where e.id=:idEmpresa").setParameter("idEmpresa", 2).getSingleResult();
			 instance.setEmpresa(empresa);
		}
	}
	
	public void cargarReparaciones()
	{
		resultList = new ArrayList<ReparacionExterna>();
		
		resultList = getEntityManager().createQuery("SELECT r FROM ReparacionExterna r").getResultList();
	}
	
	public void agregarAparato(Producto aparato)
	{
		DetalleReparacionExterna detalle = new DetalleReparacionExterna();
		
		
		detalle.setAparato(aparato);
		detalle.setReparacionExterna(instance);
	}
	
	public void agregarPieza(Producto pieza)
	{
		
	}
	

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return false;
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

	public List<DetalleReparacionExterna> getDetalleReparacion() {
		return detalleReparacion;
	}

	public void setDetalleReparacion(
			List<DetalleReparacionExterna> detalleReparacion) {
		this.detalleReparacion = detalleReparacion;
	}
	
	

}
