package com.sa.kubekit.action.acct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.AsientoContDet;
import com.sa.model.acct.AsientoContable;
import com.sa.model.acct.CuentaContable;
import com.sa.model.crm.Cliente;
import com.sa.model.medical.ClienteCorporativo;
import com.sa.model.security.ParametroSistema;
import com.sa.model.security.Sucursal;

@Name("asientoContableHome")
@Scope(ScopeType.CONVERSATION)
public class AsientoContableHome extends KubeDAO<AsientoContable>{

	private static final long serialVersionUID = 1L;
	private Integer ascId;
	private String concepto;
	private List<AsientoContable> resultList = new ArrayList<AsientoContable>(); 
	private List<AsientoContDet> ctasActivo = new ArrayList<AsientoContDet>();
	private List<AsientoContDet> ctasPasivo = new ArrayList<AsientoContDet>();
	private Date fltFecha;
	private Double total, totalActivos, totalPasivos;
	private AsientoContDet selAsi;
	
	@In(required=true,create=true)
	@Out(required=true)
	private ConceptoMovHome conceptoMovHome;
	
	@In(required=true,create=true)
	private CuentaContHome cuentaContHome;
	
	@In
	private LoginUser loginUser;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("asicont_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("asicont_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("asicont_deleted")));
		getAsientosList();
	}
	
	public void load(){
		try{
			ctasActivo = new ArrayList<AsientoContDet>();
			ctasPasivo = new ArrayList<AsientoContDet>();
			setInstance(getEntityManager().find(AsientoContable.class, ascId));
			instance.setMonto(moneyDecimal(instance.getMonto()));
			//Cargamos las cuentas de activo y pasivo afectadas en el asiento
			ctasActivo = getEntityManager()
					.createQuery("SELECT d FROM AsientoContDet d " +
							"	WHERE d.asiento = :asi AND d.cuenta.tipoCuenta.codigo = 'AC'")
					.setParameter("asi", instance)
					.getResultList();
			ctasPasivo = getEntityManager()
					.createQuery("SELECT d FROM AsientoContDet d " +
							"	WHERE d.asiento = :asi " +
							"	AND (d.cuenta.tipoCuenta.codigo = 'PV' OR d.cuenta.tipoCuenta.codigo = 'CC' )")
					.setParameter("asi", instance)
					.getResultList();
			actualizarTotal();
			conceptoMovHome.setConcepto(instance.getConcepto().getNombre());
			conceptoMovHome.select(instance.getConcepto());
		}catch (Exception e) {
			clearInstance();
			setInstance(new AsientoContable());
		}
	}
	
	public AsientoContable genAsientoParametrizado(String codCtaD, String codCtaH, 
				Float monto, String concepto, String descripcion, Cliente cli, ClienteCorporativo cliCorp, String tipoMovD, String tipoMovH) {
		String hqlQuery = "SELECT p FROM ParametroSistema p WHERE p.codigo = :cod ";
		//Localizamos las cuentas del debe y del haber
		ParametroSistema paramSis = (ParametroSistema)getEntityManager()
				.createQuery(hqlQuery)
				.setParameter("cod", codCtaD)
				.getSingleResult();
		CuentaContable ctaDebe = (CuentaContable)getEntityManager()
				.createQuery("SELECT c FROM CuentaContable c " +
						"	WHERE c.id = :codCta")
						.setParameter("codCta", paramSis.getValorNum().intValue())
						.getSingleResult();
		paramSis = (ParametroSistema)getEntityManager()
				.createQuery(hqlQuery)
				.setParameter("cod", codCtaH)
				.getSingleResult();
		CuentaContable ctaHaber = (CuentaContable)getEntityManager()
				.createQuery("SELECT c FROM CuentaContable c " +
						"	WHERE c.id = :codCta")
						.setParameter("codCta", paramSis.getValorNum().intValue())
						.getSingleResult();
		//Creamos el encabezado del asiento contable y complementamos el detalle
		AsientoContable asi = new AsientoContable();
		asi.setCliente(cli);
		asi.setCliCorp(cliCorp);
		asi.setSoloLectura(true);
		conceptoMovHome.setConcepto(concepto);
		asi.setComentario(descripcion);
		conceptoMovHome.guardarConcepto();
		asi.setConcepto(conceptoMovHome.getInstance());
		asi.setMonto(new Double(monto));
		
		AsientoContDet detActivo = new AsientoContDet();
		detActivo.setCuenta(ctaDebe);
		detActivo.setMonto(monto);
		detActivo.setTipo(tipoMovD);
		if(ctasActivo == null) 
			ctasActivo = new ArrayList<AsientoContDet>();
		ctasActivo.add(detActivo);
		
		AsientoContDet detPasivo = new AsientoContDet();
		detPasivo.setCuenta(ctaHaber);
		detPasivo.setMonto(monto);
		detPasivo.setTipo(tipoMovH);
		if(ctasPasivo == null) 
			ctasPasivo = new ArrayList<AsientoContDet>();
		ctasPasivo.add(detPasivo);
		
		setTotal(0.0d);
		setTotalActivos(new Double(monto));
		setTotalPasivos(new Double(monto));
		select(asi);
		save();
		return asi;
	}
	
	public void getAsientosList() {
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("otraSuc", loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior())
				.getResultList(); 
		
		if(subSucFlt == null || subSucFlt.size() <= 0) 
			subSucFlt = new ArrayList<Sucursal>();
		
		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior());
				
		Query qr = getEntityManager()
				.createQuery("SELECT a FROM AsientoContable a " +
						"	WHERE (a.sucursal = :suc or a.sucursal IN (:subSuc) ) ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt);
		resultList = qr.getResultList();
	}
	
	public void addCta(String tipoCta) {
		if(tipoCta.equals("AC")) {
			AsientoContDet nwAsi = new AsientoContDet();
			nwAsi.setTipo("CRG");
			ctasActivo.add(nwAsi);
		} else {
			AsientoContDet nwAsi = new AsientoContDet();
			nwAsi.setTipo("ABO");
			ctasPasivo.add(nwAsi);
		}	
	}
	
	public void actualizarTotal() {
		totalActivos = new Double(0);
		totalPasivos = new Double(0);
		total = new Double(0);
		//Calculamos el total de activos y pasivos y el total de ambos, que tiene que ser cero
		for(AsientoContDet tmpAsi : ctasActivo) {
			if(tmpAsi.getMonto() != null) {
				if(tmpAsi.getTipo().equals("CRG"))
					totalActivos += tmpAsi.getMonto();
				else
					totalActivos -= tmpAsi.getMonto();
			}
		}
		
		for(AsientoContDet tmpAsi : ctasPasivo) {
			if(tmpAsi.getMonto() != null) {
				if(tmpAsi.getTipo().equals("ABO"))
					totalPasivos += tmpAsi.getMonto();
				else
					totalPasivos -= tmpAsi.getMonto();
			}
		}
		totalActivos = new Long(Math.round(totalActivos*100))/100.0;
		totalPasivos = new Long(Math.round(totalPasivos*100))/100.0;
		
		total = totalActivos - totalPasivos;
		total = new Long(Math.round(total*100))/100.0;
	}
	
	public void selCuentaAsi(AsientoContDet asiCnt, String tipoCta) {
		cuentaContHome.getCuentasList(tipoCta);
		setSelAsi(asiCnt);
	}
	
	public void setCuentaAsi(CuentaContable cta) {
		selAsi.setCuenta(cta);
		setSelAsi(null);
	}
	
	public void removerCta(AsientoContDet asiCnt, String tipoCta) {
		if(tipoCta.equals("AC"))
			ctasActivo.remove(asiCnt);
		else
			ctasPasivo.remove(asiCnt);
		actualizarTotal();
	}
	
	@Override
	public boolean preSave() {
		if(!validate())
			return false;
		if(instance.getCodigo() == null || instance.getCodigo().trim().equals(""))
			instance.setCodigo("ASCNT");
		
		instance.setSucursal(loginUser.getUser().getSucursal());
		instance.setFechaAsiento(new Date());
		instance.setUsuario(loginUser.getUser());
		//Unificamos las filas de activo y pasivo en una sola lista
		instance.setDetalleAsiento(new ArrayList<AsientoContDet>());
		instance.getDetalleAsiento().addAll(ctasActivo);
		instance.getDetalleAsiento().addAll(ctasPasivo);
		//Si lleva un nuevo concepto, se guarda
		conceptoMovHome.guardarConcepto();
		instance.setConcepto(conceptoMovHome.getInstance());
		return true;
	}
	
	private boolean validate() {
		
		//Validamos que se de el balance entre los montos de las cuentas involucradas
		if(this.total.doubleValue() != 0.00d) {
			FacesMessages.instance().add(
					sainv_messages.get("asicont_error_nobalance"));
			return false;
		} else { //Verificamos que lo que haya de cada lado (ACT y PAS) coincida con el monto del asiento
			if( (instance.getMonto() - this.getTotalActivos()) != 0 ||
					(instance.getMonto() - this.getTotalPasivos()) != 0) {
				FacesMessages.instance().add(
						sainv_messages.get("asicont_error_nobalmnto"));
				return false;
			}
		}
		
		//Validamos que ninguna haya quedado sin cuenta o sin monto
		for(AsientoContDet tmpDet : ctasActivo) {
			if(tmpDet.getMonto() == null || tmpDet.getMonto() <= 0) {
				FacesMessages.instance().add(
						sainv_messages.get("asicont_error_nomntmov"));
				return false;
			}
			
			if(tmpDet.getCuenta() == null) {
				FacesMessages.instance().add(
						sainv_messages.get("asicont_error_noctamov"));
				return false;
			}
		}
		
		for(AsientoContDet tmpDet : ctasPasivo) {
			if(tmpDet.getMonto() == null || tmpDet.getMonto() <= 0) {
				FacesMessages.instance().add(
						sainv_messages.get("asicont_error_nomntmov"));
				return false;
			}
			
			if(tmpDet.getCuenta() == null) {
				FacesMessages.instance().add(
						sainv_messages.get("asicont_error_noctamov"));
				return false;
			}
		}
		

		return true;
	}

	@Override
	public boolean preModify() {
		if(!validate())
			return false;
		
		//Unificamos las filas de activo y pasivo en una sola lista
		instance.setDetalleAsiento(new ArrayList<AsientoContDet>());
		instance.getDetalleAsiento().addAll(ctasActivo);
		instance.getDetalleAsiento().addAll(ctasPasivo);
		//Si lleva un nuevo concepto, se guarda
		conceptoMovHome.guardarConcepto();
		instance.setConcepto(conceptoMovHome.getInstance());
		return true;
	}
	
	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public void posSave() {
		
		//Guardamos el detalle del asiento contable
		for(AsientoContDet tmpDet : instance.getDetalleAsiento()) {
			tmpDet.setAsiento(instance);
			getEntityManager().persist(tmpDet);
		}
		getEntityManager().refresh(instance);
		instance.setCodigo(instance.getCodigo()+instance.getId());
		getEntityManager().merge(instance);
		getEntityManager().flush();
	}

	@Override
	public void posModify() {
		//Guardamos el detalle del asiento contable
		for(AsientoContDet tmpDet : instance.getDetalleAsiento()) {
			if(tmpDet.getId() != null && tmpDet.getId() > 0)
				getEntityManager().merge(tmpDet);
			else {
				tmpDet.setAsiento(instance);
				getEntityManager().persist(tmpDet);
			}
		}
		getEntityManager().refresh(instance);
		instance.setCodigo(instance.getCodigo()+instance.getId());
		getEntityManager().merge(instance);
		getEntityManager().flush();
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<AsientoContable> getResultList() {
		return resultList;
	}

	public void setResultList(List<AsientoContable> resultList) {
		this.resultList = resultList;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public Integer getAscId() {
		return ascId;
	}

	public void setAscId(Integer ascId) {
		this.ascId = ascId;
	}

	public Date getFltFecha() {
		return fltFecha;
	}

	public void setFltFecha(Date fltFecha) {
		this.fltFecha = fltFecha;
	}

	public List<AsientoContDet> getCtasActivo() {
		return ctasActivo;
	}

	public void setCtasActivo(List<AsientoContDet> ctasActivo) {
		this.ctasActivo = ctasActivo;
	}

	public List<AsientoContDet> getCtasPasivo() {
		return ctasPasivo;
	}

	public void setCtasPasivo(List<AsientoContDet> ctasPasivo) {
		this.ctasPasivo = ctasPasivo;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public AsientoContDet getSelAsi() {
		return selAsi;
	}

	public void setSelAsi(AsientoContDet selAsi) {
		this.selAsi = selAsi;
	}

	public Double getTotalActivos() {
		return totalActivos;
	}

	public void setTotalActivos(Double totalActivos) {
		this.totalActivos = totalActivos;
	}

	public Double getTotalPasivos() {
		return totalPasivos;
	}

	public void setTotalPasivos(Double totalPasivos) {
		this.totalPasivos = totalPasivos;
	}

}
