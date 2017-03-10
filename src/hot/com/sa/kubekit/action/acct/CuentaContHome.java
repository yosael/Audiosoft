package com.sa.kubekit.action.acct;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.CuentaContable;
import com.sa.model.acct.TipoCuenta;

@SuppressWarnings("unchecked")
@Name("cuentaContHome")
@Scope(ScopeType.CONVERSATION)
public class CuentaContHome extends KubeDAO<CuentaContable>{

	private static final long serialVersionUID = 1L;
	private Integer ctaContId;
	private TreeNode<CuentaContable> rootNode = null;
	private List<CuentaContable> resultList = new ArrayList<CuentaContable>();
	private List<CuentaContable> cuentasPadre = new ArrayList<CuentaContable>();
	private List<TipoCuenta> tiposCuenta = new ArrayList<TipoCuenta>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("cuentac_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("cuentac_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("cuentac_deleted")));
				
	}
	
	private void cargarArbolCuentas(CuentaContable cuentaPrincipal) {
        try {
        	//Sacamos primero los 3 nodos principales de activos, pasivos y cc
        	List<CuentaContable> cuentasMain = new ArrayList<CuentaContable>(3);
        	List<CuentaContable> cuentasMainHijas;
        	List<TipoCuenta> nodosMain = getEntityManager()
        			.createQuery("SELECT t FROM TipoCuenta t ORDER BY t.codigo")
        			.getResultList();
        	for(TipoCuenta tpC : nodosMain) {
        		if(cuentaPrincipal == null) {
	        		CuentaContable tmpC = new CuentaContable();
	        		cuentasMainHijas = new ArrayList<CuentaContable>();
	        		tmpC.setNombre(tpC.getNombre());
	        		for(CuentaContable dumC : tpC.getCuentasHijas())
	        			if(dumC.getCuentaPadre() == null)
	        				cuentasMainHijas.add(dumC);
	        		tmpC.setCuentasHijas(cuentasMainHijas);
	        		cuentasMain.add(tmpC);
        		} else if(tpC.getCuentasHijas().contains(cuentaPrincipal)) {
        			CuentaContable tmpC = new CuentaContable();
        			cuentasMainHijas = new ArrayList<CuentaContable>();
	        		tmpC.setNombre(tpC.getNombre());
	        		cuentasMainHijas.add(cuentaPrincipal);
	        		tmpC.setCuentasHijas(cuentasMainHijas);
	        		cuentasMain.add(tmpC);
        		}
        	}
            rootNode = new TreeNodeImpl<CuentaContable>();
            agregarSubCuentas(rootNode, cuentasMain);
        } catch (Exception e) {
            
        } 
    }
	
	private void agregarSubCuentas(TreeNode<CuentaContable> node, List<CuentaContable> cuentasHijas) {
        int counter = 1;
        
        for(CuentaContable tmpCta : cuentasHijas) {
            TreeNodeImpl<CuentaContable> nodeImpl = new TreeNodeImpl<CuentaContable>();
            nodeImpl.setData(tmpCta);
            node.addChild(new Integer(counter), nodeImpl);
            if(tmpCta.getCuentasHijas() != null && tmpCta.getCuentasHijas().size() > 0) {
            	tmpCta.setTieneHijos(true);
            	agregarSubCuentas(nodeImpl, tmpCta.getCuentasHijas());
            } else
            	tmpCta.setTieneHijos(false);
            counter++;
        }
    }
	
	public void load(){
		try{
			setInstance(getEntityManager().find(CuentaContable.class, ctaContId));
			if(instance.getCuentaPadre() != null)
				instance.setTipoCuenta(instance.getCuentaPadre().getTipoCuenta());
			if(instance.getCuentasHijas() != null && instance.getCuentasHijas().size() > 0) {
				List<CuentaContable> cuentasPadres = new ArrayList<CuentaContable>();
				cuentasPadres.add(instance);
				cargarArbolCuentas(instance);
			}
		}catch (Exception e) {
			clearInstance();
			setInstance(new CuentaContable());
		} finally { 
			tiposCuenta = getEntityManager().createQuery("SELECT t FROM TipoCuenta t").getResultList();
		}
	}
	
	public void getCuentasList() {
		resultList = getEntityManager().createQuery("SELECT c FROM CuentaContable c").getResultList();
		cargarArbolCuentas(null);
	}
	
	public void getCuentasList(String tipoCuenta) {
		if(tipoCuenta == null || tipoCuenta.trim().equals(""))
			tipoCuenta = null;
		
		String tipoCuenta2 = tipoCuenta;
		if(tipoCuenta.equals("PV"))
			tipoCuenta2 = "CC";
		
		resultList = getEntityManager()
				.createQuery("SELECT c FROM CuentaContable c " +
						"	WHERE ( c.tipoCuenta.codigo = :cod OR c.tipoCuenta.codigo = :cod2) " +
						"		AND size(c.cuentasHijas) = 0 ")
				.setParameter("cod", tipoCuenta)
				.setParameter("cod2", tipoCuenta2)
				.getResultList();
	}
	
	public void cargarCuentasPadre() {
		if(isManaged()) { //Buscamos cuentas que no esten abajo de esta cuenta en nivel
			cuentasPadre = getEntityManager().createQuery("SELECT c FROM CuentaContable c " +
							"	WHERE c.cuentaPadre <> :cnt OR c.cuentaPadre IS NULL ")
							.setParameter("cnt", instance)
							.getResultList();
		} else {
			cuentasPadre = getEntityManager().createQuery("SELECT c FROM CuentaContable c").getResultList();
		}
	}
	
	//Seteamos la cuenta padre y le ponemos el mismo tipo de cuenta que su padre
	public void setCtaPadre(CuentaContable ctaPadre) {
		instance.setCuentaPadre(ctaPadre);
		instance.setTipoCuenta(ctaPadre.getTipoCuenta());
	}

	@Override
	public boolean preSave() {
		//Verificamos que se escoja una cuenta papa del mismo nivel o que no sea su hija
		if(!cuentaPapaCorrecta(instance)) {
			FacesMessages.instance().add(
					sainv_messages.get("cuentac_error_ctapdrno"));
			return false;
		}
		if(!cuentaEsUnica()) {
			FacesMessages.instance().add(
					sainv_messages.get("cuentac_error_ctaexis"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		//Verificamos que se escoja una cuenta papa del mismo nivel o que no sea su hija
		if(!cuentaPapaCorrecta(instance)) {
			FacesMessages.instance().add(
					sainv_messages.get("cuentac_error_ctapdrno"));
			return false;
		}
		if(!cuentaEsUnica()) {
			FacesMessages.instance().add(
					sainv_messages.get("cuentac_error_ctaexis"));
			return false;
		}
		
		return true;
	}
	
	private boolean cuentaPapaCorrecta(CuentaContable cnt){
		boolean res = true;
		//Recursivamente nos aseguramos que la cuenta papa no se encuentre abajo de la cuenta de la instancia
		if(cnt.getCuentasHijas() != null && cnt.getCuentasHijas().size() > 0){
			for(CuentaContable tmpCnt : cnt.getCuentasHijas()) {
				if(tmpCnt.equals(cnt.getCuentaPadre())) {
					res = false;
					break;
				} else if(tmpCnt.getCuentasHijas() != null && tmpCnt.getCuentasHijas().size() > 0) {
					res = cuentaPapaCorrecta(tmpCnt);
					if(!res)
						break;
				}
			}
		} 
		return res;
	}
	
	private boolean cuentaEsUnica(){
		boolean res = false;
		CuentaContable cuentaComp = null;
		if(isManaged())
			cuentaComp = instance;

		//Verificamos que el codigo o nombre de la cuenta no se repita
		List tmpList = getEntityManager().createQuery("SELECT c FROM CuentaContable c " +
						"	WHERE c <> :cta AND (UPPER(c.nombre) = UPPER(:nomC) OR UPPER(c.codCuenta) = UPPER(:codC))")
						.setParameter("nomC", instance.getNombre())
						.setParameter("codC", instance.getCodCuenta())
						.setParameter("cta", cuentaComp)
						.getResultList();
		if(tmpList == null || tmpList.size() <= 0)
			res = true;
		
		return res;
	}

	@Override
	public boolean preDelete() {
		//Eliminamos todas las cuentas hijas
		eliminarCuentasHijas(instance);
		return true;
	}
	
	private void eliminarCuentasHijas(CuentaContable cnt) {
		if(cnt.getCuentasHijas() != null && cnt.getCuentasHijas().size() > 0)
			for(CuentaContable tmpC : cnt.getCuentasHijas()) {
				if(tmpC.getCuentasHijas() != null && tmpC.getCuentasHijas().size() > 0)
					eliminarCuentasHijas(tmpC);
				getEntityManager().remove(tmpC);
			}
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posModify() {
		//Si modificaron el padre de la cuenta, todos los hijos de esta cuenta deben de pertenecer al mismo tipo de cuenta
		getEntityManager().refresh(instance);
		cambiarTipoCuenta(instance);
		getEntityManager().flush();
		getEntityManager().refresh(instance);
		resultList = null;
		rootNode = null;
	}
	
	private void cambiarTipoCuenta(CuentaContable cnt) {
		if(cnt.getCuentasHijas() != null && cnt.getCuentasHijas().size() > 0)
			for(CuentaContable tmpC : cnt.getCuentasHijas()) {
				tmpC.setTipoCuenta(cnt.getTipoCuenta());
				getEntityManager().merge(tmpC);
				getEntityManager().refresh(tmpC);
				if(tmpC.getCuentasHijas() != null && tmpC.getCuentasHijas().size() > 0)
					cambiarTipoCuenta(tmpC);
			}
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<CuentaContable> getResultList() {
		return resultList;
	}

	public void setResultList(List<CuentaContable> resultList) {
		this.resultList = resultList;
	}

	public List<CuentaContable> getCuentasPadre() {
		return cuentasPadre;
	}

	public void setCuentasPadre(List<CuentaContable> cuentasPadre) {
		this.cuentasPadre = cuentasPadre;
	}

	public Integer getCtaContId() {
		return ctaContId;
	}

	public void setCtaContId(Integer ctaContId) {
		this.ctaContId = ctaContId;
	}

	public TreeNode<CuentaContable> getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode<CuentaContable> rootNode) {
		this.rootNode = rootNode;
	}

	public List<TipoCuenta> getTiposCuenta() {
		return tiposCuenta;
	}

	public void setTiposCuenta(List<TipoCuenta> tiposCuenta) {
		this.tiposCuenta = tiposCuenta;
	}
	
	
}
