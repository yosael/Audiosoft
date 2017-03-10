package com.sa.kubekit.action.acct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.CuentaContable;
import com.sa.model.security.ParametroSistema;
import com.sa.model.security.Sistema;

@Name("paramContHome")
@Scope(ScopeType.CONVERSATION)
public class ParamContHome extends KubeDAO<ParametroSistema>{

	private static final long serialVersionUID = 1L;
	private Integer prctId;
	private List<ParametroSistema> resultList = new ArrayList<ParametroSistema>();
	private ParametroSistema selParam = new ParametroSistema();
	
	List<Map<String, Object>> paramsCnt;
	List<String> paramsOtr;
	
	@In
	private LoginUser loginUser;
	
	@In(required=false,create=true)
	private CuentaContHome cuentaContHome;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("paramact_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("paramact_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("paramact_deleted")));
				
		
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ParametroSistema.class, prctId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ParametroSistema());
		}
	}
	
	public void initParamList() {
		paramsCnt = new ArrayList<Map<String, Object>>();
		Map<String, Object> mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCSVRVM"); mpParam.put("nam", "Cuenta de ventas de servicios medicos");
		mpParam.put("dsc", "Cuenta donde se depositan los ingresos por servicios medicos");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCSVAMP"); mpParam.put("nam", "Cuenta de ventas de aparatos o piezas");
		mpParam.put("dsc", "Cuenta donde se depositan los ingresos por venta de aparatos o piezas");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCSVREP"); mpParam.put("nam", "Cuenta de ventas de reparaciones de aparatos");
		mpParam.put("dsc", "Cuenta donde se depositan los ingresos por hacer reparaciones a aparatos");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCCMPVR"); mpParam.put("nam", "Cuenta de compra de insumos");
		mpParam.put("dsc", "Cuenta de donde se saca dinero para hacer las compras de insumos");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CXCVSVMD"); mpParam.put("nam", "Cuenta por cobrar de servicios medicos");
		mpParam.put("dsc", "Cuenta por cobrar de servicios medicos que no han sido cobrados");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CXCVAPMD"); mpParam.put("nam", "Cuenta por cobrar de aparatos y piezas");
		mpParam.put("dsc", "Cuenta por cobrar donde se registra lo pendiente de venta de aparatos medicos y piezas");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CXCVAREP"); mpParam.put("nam", "Cuenta por cobrar de reparaciones");
		mpParam.put("dsc", "Cuenta por cobrar donde se registra lo pendiente de cobrar de las reparaciones");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCMVCCH"); mpParam.put("nam", "Cuenta de caja chica");
		mpParam.put("dsc", "Cuenta donde se maneja la caja chica");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCAPCNT"); mpParam.put("nam", "Cuenta de Capital Contable");
		mpParam.put("dsc", "Cuenta donde se incrementa o decrementa el capital contable de la empresa");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCCSERV"); mpParam.put("nam", "Cuenta de CC para ventas de servicio");
		mpParam.put("dsc", "Cuenta donde se incrementa o decrementa el capital contable de la empresa para servicios");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCCVTAIT"); mpParam.put("nam", "Cuenta de CC para ventas de aparatos");
		mpParam.put("dsc", "Cuenta donde se incrementa o decrementa el capital contable de la empresa para venta de aparatos o piezas");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
		mpParam = new HashMap<String, Object>();
		mpParam.put("cod", "CTCCVTAIT"); mpParam.put("nam", "Cuenta del Activo para compras");
		mpParam.put("dsc", "Cuenta para sacar dinero para realizar compras de piezas, aparatos o insumos");
		mpParam.put("sys", loginUser.getSistema());
		paramsCnt.add(mpParam);
	}
	
	public void getParametrosContables() {
		if(paramsCnt == null || paramsCnt.size() <= 0)
			initParamList();
		resultList = new ArrayList<ParametroSistema>();		
		for(Map<String, Object> tmpPrm : paramsCnt) { 
			ParametroSistema prmCnt = null;
			try {
				prmCnt = (ParametroSistema)getEntityManager()
						.createQuery("SELECT p FROM ParametroSistema p " +
								"	WHERE p.codigo = :codparam AND p.sistema = :sis ")
						.setParameter("codparam", tmpPrm.get("cod"))
						.setParameter("sis", tmpPrm.get("sys"))
						.getSingleResult();
				CuentaContable cnt = (CuentaContable)getEntityManager()
						.createQuery("SELECT c FROM CuentaContable c " +
								"	WHERE c.id = :idcta ")
						.setParameter("idcta", prmCnt.getValorNum().intValue())
						.getSingleResult();
				prmCnt.setSelObj(cnt);
			} catch(NoResultException ex) {
				prmCnt = new ParametroSistema();
				prmCnt.setCodigo(tmpPrm.get("cod").toString());
				prmCnt.setNombre(tmpPrm.get("nam").toString());
				prmCnt.setDescripcion(tmpPrm.get("dsc").toString());
				prmCnt.setSistema((Sistema)tmpPrm.get("sys"));
				
			}
			resultList.add(prmCnt);
		}
	}
	
	public void selCuentaParam(ParametroSistema par) {
		setSelParam(par);
		cuentaContHome.getCuentasList();
	}
	
	public void setCuentaParam(CuentaContable cta) {
		selParam.setValorNum(cta.getId().floatValue());
		selParam.setSelObj(cta);
	}
	
	public boolean actualizarParam() {
		for(ParametroSistema tmpPrm : resultList) {
			if(tmpPrm.getId() != null)
				getEntityManager().merge(tmpPrm);
			else {
				select(tmpPrm);
				save();
				//getEntityManager().persist(tmpPrm);
			}
		}
		return true;
	}

	@Override
	public boolean preSave() {
		//Verificamos que ninguna cuenta venga vacia
		/*for(ParametroSistema tmpPm : resultList) {
			if(tmpPm.getValorNum() == null) {
				FacesMessages.instance().add(
						sainv_messages.get("paramact_error_noctapar"));
				return false;
			}
		}*/
		return true;
	}

	@Override
	public boolean preModify() {
		return true;
	}

	@Override
	public boolean preDelete() {
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

	public List<ParametroSistema> getResultList() {
		return resultList;
	}

	public void setResultList(List<ParametroSistema> resultList) {
		this.resultList = resultList;
	}

	public Integer getPrctId() {
		return prctId;
	}

	public void setPrctId(Integer prctId) {
		this.prctId = prctId;
	}

	public ParametroSistema getSelParam() {
		return selParam;
	}

	public void setSelParam(ParametroSistema selParam) {
		this.selParam = selParam;
	}
	
}
