package com.sa.kubekit.action.security;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.security.ConfigImpresora;
import com.sa.model.security.Sucursal;

@Name("configImpresionHome")
@Scope(ScopeType.CONVERSATION)
public class ConfigImpresionHome extends KubeDAO<ConfigImpresora>{

	private static final long serialVersionUID = 1L;
	private Integer cfgprtId;
	private List<ConfigImpresora> resultList = new ArrayList<ConfigImpresora>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("ventaDocHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("ventaDocHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("ventaDocHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ConfigImpresora.class, cfgprtId));
			setValCmb1(instance.getSucursal().getNombre());
		}catch (Exception e) {
			clearInstance();
			setInstance(new ConfigImpresora());
		}
	}
	
	public ConfigImpresora getConfigImpresor(String tpi, Sucursal suc) {
		//Verificamos si la instancia coincide con los criterios
		if(instance != null && 
				instance.getTipoImpresora().equals(tpi) &&
				instance.getSucursal().equals(suc))
			return instance;
		else {
			try {
				ConfigImpresora coinciCfg = (ConfigImpresora)getEntityManager()
						.createQuery("SELECT x FROM ConfigImpresora x " +
								"	WHERE x.tipoImpresora = :tpi AND x.sucursal = :suc ")
								.setParameter("tpi", tpi)
								.setParameter("suc", suc)
								.getSingleResult();
				select(coinciCfg);
				return instance;
			} catch(Exception ex) {}
		}
		return null;
	}
	
	public void cargarConfigsList() {
		resultList = getEntityManager().createQuery("select e from ConfigImpresora e " +
				"	WHERE 1 = 1 order by e.sucursal.nombre ASC, e.nombrePrinter ASC ")
				.getResultList();
	}
	
	public void cargarConfigsActList() {
		resultList = getEntityManager().createQuery("select e from ConfigImpresora e " +
				"	WHERE 1 = 1 AND e.estado = 'ACT' order by e.sucursal.nombre ASC, e.nombrePrinter ASC ")
				.getResultList();
	}
	
	public void seleccionarSucursal(Sucursal suc, boolean isLov) {
		instance.setSucursal(suc);
		if(isLov) setValCmb1(suc.getNombre());
	}
		
	private boolean validar() {
		if(instance.getSucursal() == null) {
			FacesMessages.instance().add(Severity.ERROR, "Debe de seleccionar la sucursal a la que se asociará la configuración.");
			return false;
		}
		
		//Verificamos que no exista una configuracion para la misma sucursal (proximamente validando la caja)
		List<ConfigImpresora> coinci = getEntityManager()
				.createQuery("SELECT x FROM ConfigImpresora x WHERE x.sucursal = :suc AND x.id <> :idc ")
				.setParameter("suc", instance.getSucursal())
				.setParameter("idc", instance.getId())
				.getResultList();
		
		if(coinci != null && coinci.size() > 0) {
			FacesMessages.instance().add(Severity.ERROR, "Ya existe una configuración para el POS seleccionado, seleccione otro.");
			return false;
		}
			
		return true;
	}

	@Override
	public boolean preSave() {
		
		return validar();
	}

	@Override
	public boolean preModify() {

		return validar();
	}

	@Override
	public boolean preDelete() {
		return true;
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

	public Integer getCfgprtId() {
		return cfgprtId;
	}

	public void setCfgprtId(Integer cfgprtId) {
		this.cfgprtId = cfgprtId;
	}

	public List<ConfigImpresora> getResultList() {
		return resultList;
	}

	public void setResultList(List<ConfigImpresora> resultList) {
		this.resultList = resultList;
	}

	
}
