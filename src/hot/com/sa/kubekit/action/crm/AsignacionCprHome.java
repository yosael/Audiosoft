package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.security.Sucursal;
import com.sa.model.vta.ComprobanteAsignadoDoc;
import com.sa.model.vta.ComprobanteImpresion;
   
@Name("asignacionCprHome")  
@Scope(ScopeType.CONVERSATION) 
public class AsignacionCprHome extends KubeDAO<ComprobanteAsignadoDoc> {
 
	private static final long serialVersionUID = 1L;
	private Integer cprasigId;
	private boolean showComp = false;

	private List<ComprobanteAsignadoDoc> resultList = new ArrayList<ComprobanteAsignadoDoc>();
	 
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("asignacionCprHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("asignacionCprHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("asignacionCprHome_deleted")));
	}
		
	@SuppressWarnings("unchecked")
	public void cargarComprobantesAsig(){
		resultList = getEntityManager().createQuery("select c from ComprobanteAsignadoDoc c ")
				.getResultList();
	}
	  
	public void load(){
		try{
			setInstance(getEntityManager().find(ComprobanteAsignadoDoc.class, cprasigId));
		}catch (Exception e) {
			clearInstance(); 
			setInstance(new ComprobanteAsignadoDoc());
			instance.setFechaAsignacion(new Date());
		} 
	}
	
	public ComprobanteAsignadoDoc getSiguienteCorrelativo(ComprobanteImpresion cpi, Sucursal suc) {
		List<ComprobanteAsignadoDoc> res = getEntityManager()
				.createQuery("SELECT x FROM ComprobanteAsignadoDoc x " +
						"	WHERE x.comprobante = :cpi AND x.sucursal = :suc " +
						"	AND x.fechaFinalizacion IS NULL AND x.numActual < x.numFinal ")
						.setParameter("cpi", cpi)
						.setParameter("suc", suc)
						.getResultList();
		
		if(res != null && !res.isEmpty())
			return res.get(0);
			
		return null;
	} 
	
	public void aumentarCorrelativo(ComprobanteAsignadoDoc cad){
		if( cad.getFechaFinalizacion() == null ){
			if( cad.getNumActual() >= cad.getNumFinal() ){
				//
			}else{
				cad.setNumActual( cad.getNumActual() + 1 );
				if( cad.getNumActual() == cad.getNumFinal() ){
					cad.setFechaFinalizacion( new Date() );
				}
				this.setInstance(cad);
				this.update();
			}
		}else{
			
		}
		
	}
	
	private boolean validate() {
		boolean res = true;
		
		return res;
	}
	
	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		instance.setNumActual(instance.getNumInicio() - 1);
		return validate();
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return validate();
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		this.showComp = true;
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
	}

	public Integer getCprasigId() {
		return cprasigId;
	}

	public void setCprasigId(Integer cprasigId) {
		this.cprasigId = cprasigId;
	}

	public List<ComprobanteAsignadoDoc> getResultList() {
		return resultList;
	}

	public void setResultList(List<ComprobanteAsignadoDoc> resultList) {
		this.resultList = resultList;
	}

	public boolean isShowComp() {
		return showComp;
	}

	public void setShowComp(boolean showComp) {
		this.showComp = showComp;
	}
}
