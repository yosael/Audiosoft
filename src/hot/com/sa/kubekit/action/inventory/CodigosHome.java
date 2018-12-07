package com.sa.kubekit.action.inventory;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.CodProducto;

@Name("codigosHome")
@Scope(ScopeType.CONVERSATION)
public class CodigosHome extends KubeDAO<CodProducto> {
	
	private static final long serialVersionUID = 1L;

	private List<CodProducto> resultList = new ArrayList<CodProducto>();
	
	private String busqueda;
	private Integer idCodigo;
	
	
	public void buscar(){
		
		System.out.println("Entro a buscar");
		resultList = getEntityManager().createQuery("SELECT c FROM CodProducto c where UPPER(c.numSerie) like :busqueda OR UPPER(c.numLote) like :busqueda")
				.setParameter("busqueda","%"+busqueda.toUpperCase()+"%")
				.getResultList();
		
		
		System.out.println("resultados : r"+resultList.size());
	}
	
	
	public void load(){
		
		System.out.println("Entro a load");
		System.out.println("id cargado: "+idCodigo);
		try {
			setInstance(getEntityManager().find(CodProducto.class, this.idCodigo));
		} catch (Exception e) {
			e.printStackTrace();
			clearInstance();
			instance = new CodProducto();
			
		}
	}
	

	@Override
	public boolean preSave() {
		
		
		
		return true;
	}

	@Override
	public boolean preModify() {
		
		
		if((instance.getNumLote()==null && instance.getNumSerie()==null) || (instance.getNumLote().isEmpty() && instance.getNumSerie().isEmpty()) ){
			FacesMessages.instance().add(Severity.WARN,"Debe ingresar un numero de seria o lote");
			return false;
		}
		
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
	
	
	


	public List<CodProducto> getResultList() {
		return resultList;
	}


	public void setResultList(List<CodProducto> resultList) {
		this.resultList = resultList;
	}


	public String getBusqueda() {
		return busqueda;
	}


	public void setBusqueda(String busqueda) {
		this.busqueda = busqueda;
	}


	public Integer getIdCodigo() {
		return idCodigo;
	}


	public void setIdCodigo(Integer idCodigo) {
		this.idCodigo = idCodigo;
	}


	
	

	
	
	
	
}
