package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.kubekit.action.util.Numalet;
import com.sa.model.crm.ChequeDoc;
import com.sa.model.crm.ProveedorDoc;
import com.sa.model.sales.SolicitudImpresion;
   
@Name("chequeDocHome")  
@Scope(ScopeType.CONVERSATION) 
public class ChequeDocHome extends KubeDAO<ChequeDoc> {
 
	private static final long serialVersionUID = 1L;
	private Integer cheqDocId;
	private boolean showComp = false;

	private List<ChequeDoc> chequesDoc = new ArrayList<ChequeDoc>();
	
	@In
	private LoginUser loginUser;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("chequeDocHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("chequeDocHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("chequeDocHome_deleted")));
	}
		
	@SuppressWarnings("unchecked")
	public void cargarListaCheques(){
		chequesDoc = getEntityManager().createQuery("select c from ChequeDoc c ")
				.getResultList();
	}
	  
	public void load(){
		try{
			setInstance(getEntityManager().find(ChequeDoc.class, cheqDocId));
		}catch (Exception e) {
			clearInstance(); 
			setInstance(new ChequeDoc());
			instance.setLugar("SAN SALVADOR");
		} 
	}  
	
	public boolean anularCheque(){
		System.out.println("Anulando la Venta");
		try{
			instance.setEstado("ANU");
			this.modify();
			FacesMessages.instance().add(Severity.INFO, "El cheque se ha anulado correctamente.");
			return true;
		}catch(Exception e){
			FacesMessages.instance().add(Severity.ERROR, "El cheque no se ha podido ser anulado.");
			return false;
		}
		
	}	
	
	public void genCompr() {
		/*setShowComp(true);
		//Creando una solicitud de impresion
		SolicitudImpresion solicitudImpresion = new SolicitudImpresion();
		solicitudImpresion.setUsuario( this.loginUser.getUser() );
		solicitudImpresion.setChequeDoc(instance);
		solicitudImpresion.setFecha(  new Date()  );
		solicitudImpresion.setTipDoc(getInstance().getComprobante().getCodigo());
		//Guardando la solicitud de impresion
		if( instance.getId() != null ){
			getEntityManager().merge(solicitudImpresion);
			getEntityManager().flush();
		}*/
	}
	
	public void setProv( ProveedorDoc proveedor ){
		instance.setProveedor(proveedor);
		instance.setOrdenDe(proveedor.getRazonSocial());
	}
	
	public void convertNumToLetter(){
		Numalet numalet = new Numalet();
		instance.setMontoLetras( numalet.convertNumToLetters( instance.getMonto().toString(), false ) + " DOLARES" );
		
	}

	@Override
	public boolean preSave() {
		instance.setEstado("APL");
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
		return true;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		List<ChequeDoc> vd = getEntityManager().
				createQuery("SELECT i FROM ChequeDoc i WHERE 1=1 ORDER BY i.id DESC").getResultList();
		if(vd!=null && !vd.isEmpty())
			this.cheqDocId = (int) vd.get(0).getId();
		
		/*setShowComp(true);
		//Creando una solicitud de impresion
		SolicitudImpresion solicitudImpresion = new SolicitudImpresion();
		solicitudImpresion.setUsuario( this.loginUser.getUser() );
		solicitudImpresion.setChequeDoc(instance);
		solicitudImpresion.setFecha(  new Date()  );
		solicitudImpresion.setTipDoc(getInstance().getComprobante().getCodigo());
		//Guardando la solicitud de impresion
		if( instance.getId() != null ){
			getEntityManager().merge(solicitudImpresion);
			getEntityManager().flush();
		}*/
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
	}

	public Integer getCheqDocId() {
		return cheqDocId;
	}

	public void setCheqDocId(Integer cheqDocId) {
		this.cheqDocId = cheqDocId;
	}

	public List<ChequeDoc> getChequesDoc() {
		return chequesDoc;
	}

	public void setChequesDoc(List<ChequeDoc> chequesDoc) {
		this.chequesDoc = chequesDoc;
	}

	public boolean isShowComp() {
		return showComp;
	}

	public void setShowComp(boolean showComp) {
		this.showComp = showComp;
	}
}
