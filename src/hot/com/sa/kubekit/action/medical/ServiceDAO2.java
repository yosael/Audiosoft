package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NoInitialContextException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.sales.Service;

@Name("serviceDAO2")
@Scope(ScopeType.CONVERSATION)
public class ServiceDAO2 extends KubeDAO<Service> {

	private static final long serialVersionUID = 1L;

	private Integer serviceId;
	private List<Service> resultList = new ArrayList<Service>();
	private List<Service> resultListExa = new ArrayList<Service>();
	private String nomCoinci="";
	private String filterEstado;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("serviceDAO_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("serviceDAO_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("serviceDAO_deleted")));
	}
	
	public void loadServiciosList() {
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s ORDER BY s.codigo ASC")
				.getResultList();
	}
	
	public void buscadorServicios(){
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE ((UPPER(s.name) LIKE UPPER(:nom) OR " +
						"UPPER(s.codigo) LIKE UPPER(:cod)) AND" +
						" (s.tipoServicio = 'CMB' OR s.tipoServicio = 'TLL'))")
				.setParameter("cod","%"+nomCoinci.toUpperCase()+"%" )
				.setParameter("nom","%"+nomCoinci.toUpperCase()+"%")
				.getResultList();
		
	}
	
	public void buscadorServiciosGral(){
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE ((UPPER(s.name) LIKE UPPER(:nom) OR " +
						"UPPER(s.codigo) LIKE UPPER(:cod)))")
				.setParameter("cod","%"+nomCoinci+"%" )
				.setParameter("nom","%"+nomCoinci+"%")
				.getResultList();
		
	}
	
	public void loadServiciosList(String tipoServ) {
		nomCoinci="";
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps AND s.estado = 'ACT' ORDER BY s.codigo ASC")
				.setParameter("tps", tipoServ)
				.getResultList();
	}
	
	public void loadServiciosListByName(String tipoServ) {
			
			resultList = getEntityManager()
					.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps AND s.estado = 'ACT' AND (UPPER(s.name) like :nom) ORDER BY s.codigo ASC")
					.setParameter("tps", tipoServ)
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.getResultList();
		}
	
	
	//Esta lista debe haber al menos un registro para validar
	public void loadServiciosExa() {
		
		nomCoinci="";
		System.out.println("ENtro a buscar examen");
		
		resultListExa = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps AND s.estado = 'ACT' ORDER BY s.codigo ASC")
				.setParameter("tps", "EXA")
				.getResultList();
		
		System.out.println("Tam" + resultListExa.size());
	}
	
	public void loadServiciosExaByName() {
		
		System.out.println("Var coinci "+nomCoinci);
		
		resultListExa = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps AND s.estado = 'ACT' AND (UPPER(s.name) like :nom)  ORDER BY s.codigo ASC")
				.setParameter("tps", "EXA")
				.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
				.getResultList();
		
		System.out.println("TamDos" + resultListExa.size());
	}
	
	public List<Object[]> findServiciosByName(Object o){
		System.out.println("entré a a findServiciosByName y o.tostring es: "+ o.toString());  
		return getEntityManager()
			.createQuery("SELECT s.codigo, s.name,s FROM Service s WHERE (s.tipoServicio = :tps1 OR s.tipoServicio = :tps2)" +
					" AND s.estado = 'ACT' AND (UPPER(s.name) LIKE UPPER(:nom)) OR (UPPER(s.codigo) LIKE UPPER(:nom)) ORDER BY s.codigo ASC")
			.setParameter("tps1", "EXA")
			.setParameter("tps2", "MED")
			.setParameter("nom","%"+o.toString()+"%")
			.getResultList();
		
	}
	
	public void loadServiciosList(String tipoServ1, String tipoServ2) {
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps1 OR s.tipoServicio = :tps2 AND s.estado = 'ACT' ORDER BY s.codigo ASC")
				.setParameter("tps1", tipoServ1)
				.setParameter("tps2", tipoServ2)
				.getResultList();
	}

	public void loadExa() {
		load();
		instance.setTipoServicio("EXA");
	}
	
	public void load() {
		try {
			setInstance(getEntityManager().find(Service.class, serviceId));
		} catch (Exception e) {
			clearInstance();
			instance = new Service();
		}
	}

	private boolean validar() {
		//Evaluamos el codigo del combo que no se repita
		List<Service> serviciosCoinci = new ArrayList<Service>();
		
		if(!isManaged()) { 
			serviciosCoinci = getEntityManager()
					.createQuery("SELECT s FROM Service s WHERE s.codigo = :cod AND s.estado='ACT'")
					.setParameter("cod", instance.getCodigo())
					.getResultList();
			
		} else {
			serviciosCoinci = getEntityManager()
					.createQuery("SELECT s FROM Service s WHERE s.codigo = :cod AND s.id <> :idSvc AND s.estado='ACT'")
					.setParameter("cod", instance.getCodigo())
					.setParameter("idSvc", instance.getId())
					.getResultList();
		}
		
		if(serviciosCoinci != null && serviciosCoinci.size() > 0) {
			sainv_messages.clear();
			FacesMessages.instance().add(
					sainv_messages.get("serviceDAO_codrep"));
			return false;
		}
		
		return true;
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
	
	public boolean desactivarServicio() {
		instance.setEstado("INA");
		return modify();
	}

	@Override
	public boolean preDelete() {
		/*if (!getInstance().getServiceClinicalHistories().isEmpty()
				|| !getInstance().getMedicalAppointmentServices().isEmpty()) {
			FacesMessages.instance().add(
					sainv_messages.get("serviceDAO_error1"));
			return false;
		}*/
		return true;
	}

	@Override
	public boolean preModify() {
		return validar();
		//return true;
	}

	@Override
	public boolean preSave() {
		instance.setEstado("ACT");
		return validar();
		//return true;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public List<Service> getResultList() {
		return resultList;
	}

	public void setResultList(List<Service> resultList) {
		this.resultList = resultList;
	}

	public List<Service> getResultListExa() {
		return resultListExa;
	}

	public void setResultListExa(List<Service> resultListExa) {
		this.resultListExa = resultListExa;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public String getFilterEstado() {
		return filterEstado;
	}

	public void setFilterEstado(String filterEstado) {
		this.filterEstado = filterEstado;
	}
	
	
}