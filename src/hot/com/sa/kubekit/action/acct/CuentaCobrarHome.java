package com.sa.kubekit.action.acct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.security.SucursalHome;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.AsientoContable;
import com.sa.model.acct.ConceptoMov;
import com.sa.model.acct.CondicionPago;
import com.sa.model.acct.CuentaCobrar;
import com.sa.model.acct.PagoCuentaPend;
import com.sa.model.medical.ClienteCorporativo;
import com.sa.model.sales.DetVentaProdServ;
import com.sa.model.sales.VentaProdServ;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Name("cuentaCobrarHome")
@Scope(ScopeType.CONVERSATION)
public class CuentaCobrarHome extends KubeDAO<CuentaCobrar>{

	private static final long serialVersionUID = 1L;
	private Integer cxcId;
	private String codComprobante=null;
	private List<CuentaCobrar> resultList = new ArrayList<CuentaCobrar>();
	private List<CondicionPago> condicionesPago = new ArrayList<CondicionPago>();
	private String fechaVencimientoFlt;
	private ClienteCorporativo fltCliCorp;
	private String descPagoClic;
	private Double totalCxcCorp;
	//Registro de pagos para Cxc
	private String descPagoCxc;
	private Float montoPagoCxc;
	private CondicionPago condPagoCxc;
	private String estadoCuentaSelec;
	private List<CuentaCobrar> resultListFac = new ArrayList<CuentaCobrar>();
	private List<CuentaCobrar> resultListQued = new ArrayList<CuentaCobrar>();
	private List<CuentaCobrar> resultListPagadas= new ArrayList<CuentaCobrar>();
	private String busquedaCliente="";
	private String numeroInfo;
	
	protected Date fechaInicio;
	protected Date fechaFin;
	
	private String numFactura;
	private String numQuedan;
	
	//Registro de pagos para Cxc
	
	@In(required=true,create=true)
	@Out(required=true)
	private ConceptoMovHome conceptoMovHome;
	
	@In(required=true,create=true)
	private SucursalHome sucursalHome;
	
	@In(required=false,create=true)
	@Out
	private AsientoContableHome asientoContableHome;
	
	@In
	private LoginUser loginUser;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("ctxcb_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("ctxcb_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("ctxcb_deleted")));
	} 
	
	public void resetFltCorp() {
		setFltCliCorp(null);
		setDescPagoClic("");
		
	}
	
	
	public void load(){
		try{
			
		
				setInstance(new CuentaCobrar());
				setInstance(getEntityManager().find(CuentaCobrar.class, cxcId));
				System.out.println("entro por id");
			
			
			conceptoMovHome.select(instance.getConcepto());
			conceptoMovHome.setConcepto(instance.getConcepto().getNombre());
			//instance.setMonto(moneyDecimal(instance.getMonto()).floatValue());
			
			
			
		}catch (Exception e) {
			clearInstance();
			setInstance(new CuentaCobrar());
			instance.setDiasPlazo(3);
			if(loginUser.getUser().getSucursal() != null)
				instance.setSucursal(loginUser.getUser().getSucursal());
		} finally {
			//Cargamos las condiciones de pago
			condicionesPago = getEntityManager()
					.createQuery("SELECT c FROM CondicionPago c")
					.getResultList();
			sucursalHome.cargarSucursales();
		}
	}
	
	public void load2(){
		try{
			
				setInstance(new CuentaCobrar());
				setInstance((CuentaCobrar)getEntityManager().createQuery("SELECT c FROM CuentaCobrar c where c.comprobante=:cp").setParameter("cp", codComprobante).getSingleResult());
				System.out.println("entro por comprobante");
			
			
			conceptoMovHome.select(instance.getConcepto());
			conceptoMovHome.setConcepto(instance.getConcepto().getNombre());
			//instance.setMonto(moneyDecimal(instance.getMonto()).floatValue());
			
			
			
		}catch (Exception e) {
			clearInstance();
			setInstance(new CuentaCobrar());
			instance.setDiasPlazo(3);
			if(loginUser.getUser().getSucursal() != null)
				instance.setSucursal(loginUser.getUser().getSucursal());
		} finally {
			//Cargamos las condiciones de pago
			condicionesPago = getEntityManager()
					.createQuery("SELECT c FROM CondicionPago c")
					.getResultList();
			sucursalHome.cargarSucursales();
		}
	}
	
	public void buscarCxcCliente(String resultSelect)
	{
		
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("otraSuc", loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior())
				.getResultList(); 
		
		if(subSucFlt == null || subSucFlt.size() <= 0) 
			subSucFlt = new ArrayList<Sucursal>();
		
		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior());
		
		if(resultSelect.equals("ACT"))
		{
			
			resultList = getEntityManager()
					.createQuery("SELECT c FROM CuentaCobrar c " +
							"	WHERE 1 = 1 " +
							"	AND (c.sucursal = :suc or c.sucursal IN (:subSuc) ) " +
							"	AND (c.estado IS NULL OR c.estado = 'ACT') " +
							"	AND CONCAT(UPPER(TRIM(c.cliente.nombres)),' ',UPPER(TRIM(c.cliente.apellidos))) like :busqueda )" +
							" ORDER BY c.fechaVencimiento DESC")
					.setParameter("suc", loginUser.getUser().getSucursal())
					.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt)
					.setParameter("busqueda", "%"+busquedaCliente.toUpperCase().trim()+"%")
					.getResultList();
			setFechaVencimientoFlt("");
			
		}
		else if(resultSelect.equals("FAC"))
		{
			
			resultListFac = getEntityManager()
					.createQuery("SELECT c FROM CuentaCobrar c " +
							"	WHERE 1 = 1 " +
							"	AND (c.sucursal = :suc or c.sucursal IN (:subSuc) ) " +
							"	AND (c.estado IS NULL OR c.estado = 'FAC') " +
							"	AND CONCAT(UPPER(TRIM(c.cliente.nombres)),' ',UPPER(TRIM(c.cliente.apellidos))) like :busqueda ) " +
							" ORDER BY c.fechaVencimiento DESC")
					.setParameter("suc", loginUser.getUser().getSucursal())
					.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt)
					.setParameter("busqueda", "%"+busquedaCliente.toUpperCase().trim()+"%")
					.getResultList();
			setFechaVencimientoFlt("");
			
		}
		else if(resultSelect.equals("QDN"))
		{
			
			resultListQued = getEntityManager()
					.createQuery("SELECT c FROM CuentaCobrar c " +
							"	WHERE 1 = 1 " +
							"	AND (c.sucursal = :suc or c.sucursal IN (:subSuc) ) " +
							"	AND (c.estado IS NULL OR c.estado = 'QDN') " +
							"	AND CONCAT(UPPER(TRIM(c.cliente.nombres)),' ',UPPER(TRIM(c.cliente.apellidos))) like :busqueda ) " +
							" ORDER BY c.fechaVencimiento DESC")
					.setParameter("suc", loginUser.getUser().getSucursal())
					.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt)
					.setParameter("busqueda", "%"+busquedaCliente.toUpperCase().trim()+"%")
					.getResultList();
			setFechaVencimientoFlt("");
			
		}
		else if(resultSelect.equals("PGD"))
		{
			
			resultListPagadas = getEntityManager()
					.createQuery("SELECT c FROM CuentaCobrar c " +
							"	WHERE 1 = 1 " +
							"	AND (c.sucursal = :suc or c.sucursal IN (:subSuc) ) " +
							"	AND (c.estado IS NULL OR c.estado = 'PGD') " +
							"	AND CONCAT(UPPER(TRIM(c.cliente.nombres)),' ',UPPER(TRIM(c.cliente.apellidos))) like :busqueda )  " +
							" ORDER BY c.fechaVencimiento DESC")
					.setParameter("suc", loginUser.getUser().getSucursal())
					.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt)
					.setParameter("busqueda", "%"+busquedaCliente.toUpperCase()+"%")
					.getResultList();
			setFechaVencimientoFlt("");
			
		}
		
		
		
	}
	
	public void getCuentasCobrarList() {
		
		if(getFechaPFlt1()==null && getFechaPFlt2()==null)
			super.setRangoUlt30dias();
		
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("otraSuc", loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior())
				.getResultList(); 
		
		if(subSucFlt == null || subSucFlt.size() <= 0) 
			subSucFlt = new ArrayList<Sucursal>();
		
		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior());
		
		System.out.println("FEcha 1 "+ getFechaPFlt1());
		System.out.println("FEcha 2 "+ getFechaPFlt2());
		
		resultList = getEntityManager()
				.createQuery("SELECT c FROM CuentaCobrar c " +
						"	WHERE 1 = 1 " +
						"	AND (c.sucursal = :suc or c.sucursal IN (:subSuc) ) " +
						"	AND (c.estado IS NULL OR c.estado = 'ACT') AND c.fechaIngreso BETWEEN :fch1 AND :fch2 " +
						" ORDER BY c.fechaVencimiento DESC")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt)
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.getResultList();
		setFechaVencimientoFlt("");
	}
	
	public void getCuentasCobrarListFac() {
		
		if(getFechaPFlt1()==null && getFechaPFlt2()==null)
			super.setRangoUlt30dias();
		
		System.out.println("Cargo los fatos de factura");
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("otraSuc", loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior())
				.getResultList(); 
		
		if(subSucFlt == null || subSucFlt.size() <= 0) 
			subSucFlt = new ArrayList<Sucursal>();
		
		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior());
		
		resultListFac = getEntityManager()
				.createQuery("SELECT c FROM CuentaCobrar c " +
						"	WHERE 1 = 1 " +
						"	AND (c.sucursal = :suc or c.sucursal IN (:subSuc) ) " +
						"	AND (c.estado IS NULL OR c.estado = 'FAC') AND c.fechaIngreso BETWEEN :fch1 AND :fch2 " +
						" ORDER BY c.fechaVencimiento DESC")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt)
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.getResultList();
		setFechaVencimientoFlt("");
	}
	
	
	public void getCuentasCobrarListQued(){
		
		if(getFechaPFlt1()==null && getFechaPFlt2()==null)
			super.setRangoUlt30dias();
		
		System.out.println("Cargo los fatos de quedan");
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("otraSuc", loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior())
				.getResultList(); 
		
		if(subSucFlt == null || subSucFlt.size() <= 0) 
			subSucFlt = new ArrayList<Sucursal>();
		
		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior());
		
		resultListQued = getEntityManager()
				.createQuery("SELECT c FROM CuentaCobrar c " +
						"	WHERE 1 = 1 " +
						"	AND (c.sucursal = :suc or c.sucursal IN (:subSuc) ) " +
						"	AND (c.estado IS NULL OR c.estado = 'QDN') AND c.fechaIngreso BETWEEN :fch1 AND :fch2 " +
						" ORDER BY c.fechaVencimiento DESC")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt)
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.getResultList();
		setFechaVencimientoFlt("");
	}
	
public void getCuentasCobrarListPagadas(){
	
	if(getFechaPFlt1()==null && getFechaPFlt2()==null)
		super.setRangoUlt30dias();
		
		System.out.println("Cargo los datos pagados");
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("otraSuc", loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior())
				.getResultList(); 
		
		if(subSucFlt == null || subSucFlt.size() <= 0) 
			subSucFlt = new ArrayList<Sucursal>();
		
		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior());
		
		resultListPagadas = getEntityManager()
				.createQuery("SELECT c FROM CuentaCobrar c " +
						"	WHERE 1 = 1 " +
						"	AND (c.sucursal = :suc or c.sucursal IN (:subSuc) ) " +
						"	AND (c.estado IS NULL OR c.estado = 'PGD') AND c.fechaIngreso BETWEEN :fch1 AND :fch2  " +
						" ORDER BY c.fechaVencimiento DESC")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("subSuc", subSucFlt==null?new ArrayList<Sucursal>():subSucFlt)
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.getResultList();
		setFechaVencimientoFlt("");
	}
	
	
	
	public boolean verifSelCxcCorp() {
		//VErificamos que al menos hayan seleccionado una CxC para hacer el pago corporativo
		for(CuentaCobrar tmpCxc : resultList) 
			if(tmpCxc.isCxcSel())
				return true;
		
		return false;
	}
	
	public boolean verifSelCxcCorpFac() {
		//VErificamos que al menos hayan seleccionado una CxC para hacer el pago corporativo
		for(CuentaCobrar tmpCxc : resultListFac) 
			if(tmpCxc.isCxcSel())
				return true;
		
		return false;
	}
	
	public boolean verifSelCxcCorpQued() {
		//VErificamos que al menos hayan seleccionado una CxC para hacer el pago corporativo
		for(CuentaCobrar tmpCxc : resultListQued) 
			if(tmpCxc.isCxcSel())
				return true;
		
		return false;
	}
	
	public void addPagoCxc() {
		
		
		//Llenamos un objeto de pgo
		PagoCuentaPend pagoCxc = new PagoCuentaPend();
		pagoCxc.setComentario(descPagoCxc);
		
		System.out.println("Remanente actual: "+ moneyDecimal(instance.getRemanente()));
		System.out.println("Pago a realizar: "+ montoPagoCxc);
		
		Double remanenteMoney=moneyDecimal(instance.getRemanente()); 
		Double pagoMoney=moneyDecimal(montoPagoCxc.doubleValue());
		
		System.out.println("Remanente actual2: "+ remanenteMoney);
		System.out.println("Pago a realizar2: "+ pagoMoney);
		
		if(remanenteMoney <= pagoMoney) {
			
			pagoCxc.setMonto(instance.getRemanente().floatValue());
			pagoCxc.setRemanente(0f);
			
			if(instance.getId_venta()!=null)
			{
				VentaProdServ vtaActual = (VentaProdServ)getEntityManager().createQuery("SELECT v FROM VentaProdServ v where v.id=:idV").setParameter("idV", instance.getId_venta()).getSingleResult();
				vtaActual.setEstado("ABF"); //En que parte se actualiza la venta???
				getEntityManager().merge(vtaActual);
				getEntityManager().flush();
				
			}
			
			System.out.println("Cuenta saldada");
			
		} else {
			System.out.println("Cuenta no saldada");
			pagoCxc.setMonto(montoPagoCxc.floatValue());
			pagoCxc.setRemanente(instance.getRemanente().floatValue() - pagoCxc.getMonto().floatValue());
		}
		pagoCxc.setCuentaCobrar(instance);
		pagoCxc.setFechaIngreso(new Date());
		pagoCxc.setSucursal(loginUser.getUser().getSucursal());
		pagoCxc.setCondicionPago(condPagoCxc);
		/*CondicionPago cnd = new CondicionPago(); 
		cnd=(CondicionPago)condPagoCxc;*/
		//Registramos el pago
		getEntityManager().persist(pagoCxc);
		//Generamos un asiento contable por el pago efectuado
		AsientoContable asi = asientoContableHome.genAsientoParametrizado("CXCVSVMD", "CTCAPCNT", pagoCxc.getMonto(), 
				"PAGO DE CxC", pagoCxc.getComentario() + " - # comprobante CxC: " + instance.getComprobante(), instance.getCliente(), fltCliCorp,  "ABO", "CRG");
		pagoCxc.setAsiento(asi);

		if(pagoCxc.getCondicionPago().getNombre().equals("EFECTIVO") || pagoCxc.getCondicionPago().getNombre().equals("TRANSFERENCIA"))
		{
			pagoCxc.setEstado("Aprobado");
		}
		else
		{
			pagoCxc.setEstado("Ingresado");
		}
		
		

		getEntityManager().merge(pagoCxc);
		//Verificamos el remanente si es cero
		if(pagoCxc.getRemanente() <= 0) {
			
			
			instance.setRemanente(0f);
			instance.setEstado("PGD");
			instance.setFechaFinalizacion(new Date());
			
			FacesMessages.instance().add(
					sainv_messages.get("ctxcb_cxcpagadas"));
			
		}
		instance.setRemanente(pagoCxc.getRemanente());
		
		getEntityManager().merge(instance);
		getEntityManager().flush();
		getEntityManager().refresh(instance);
		setDescPagoCxc(null);
		setMontoPagoCxc(null);
		setCondPagoCxc(null);
		
		//Insertar nueva venta para el pago realizado
		//System.out.println("tipo de pago: "+condPagoCxc.getNombre());
		if(pagoCxc.getCondicionPago().getNombre().equals("EFECTIVO"))
		{
			
		
			//VentaProdServ vtaActual = (VentaProdServ)getEntityManager().createQuery("SELECT v FROM VentaProdServ v where v.id=:idV").setParameter("idV", instance.getId_venta()).getSingleResult();
			VentaProdServ vtaAbono = new VentaProdServ();
			vtaAbono.setFechaVenta(new Date());
			vtaAbono.setTipoVenta("ABN");
			vtaAbono.setMonto(pagoCxc.getMonto());
			vtaAbono.setDetalle("Abono, comprobante: "+instance.getComprobante());
			vtaAbono.setIdDetalle(0);
			vtaAbono.setCliente(instance.getCliente());
			vtaAbono.setEstado("APR");
			vtaAbono.setEmpresa(null);
			vtaAbono.setUsrEfectua(loginUser.getUser());
			vtaAbono.setSucursal(loginUser.getUser().getSucursal());
			vtaAbono.setTipoDescuento(null);
			vtaAbono.setCliCorp(null);
			vtaAbono.setCodTipoVenta(null);
			getEntityManager().persist(vtaAbono);
			
			DetVentaProdServ detalleAbn = new DetVentaProdServ();
			detalleAbn.setCantidad(1);
			detalleAbn.setMonto(pagoCxc.getMonto());
			detalleAbn.setDetalle("Abono a venta");
			detalleAbn.setVenta(vtaAbono);
			detalleAbn.setCodClasifVta("ABN");
			detalleAbn.setCosto(pagoCxc.getMonto());
			detalleAbn.setCodExacto(instance.getComprobante());
			
			getEntityManager().persist(detalleAbn);
			
			getEntityManager().flush();
			getEntityManager().refresh(instance);
			getEntityManager().refresh(vtaAbono);
			getEntityManager().refresh(detalleAbn);
			
		}
		
		
		sainv_messages.clear();
		/*FacesMessages.instance().add(
				sainv_messages.get("ctxcb_cxcpagadas"));*/
		FacesMessages.instance().add("El pago ha sido agregado");
	}
	
	
	public void aprobarPago(PagoCuentaPend pago)
	{
		//CuentaCobrar cuentaActual = (CuentaCobrar)getEntityManager().createQuery("SELECT v FROM CuentaCobrar v where v.id=:idC").setParameter("idV", pago.getCuentaCobrar().getId()).getSingleResult();
		
		if(instance.getId_venta()!=null)
		{
			VentaProdServ vtaActual = (VentaProdServ)getEntityManager().createQuery("SELECT v FROM VentaProdServ v where v.id=:idV").setParameter("idV", instance.getId_venta()).getSingleResult();
			pago.setEstado("Aprobado");
			getEntityManager().merge(pago);
			VentaProdServ vtaAbono = new VentaProdServ();
			
			vtaAbono.setFechaVenta(new Date());
			vtaAbono.setTipoVenta("ABN");
			vtaAbono.setMonto(pago.getMonto());
			vtaAbono.setDetalle("Abono, comprobante: "+instance.getComprobante());
			vtaAbono.setIdDetalle(0);
			vtaAbono.setCliente(instance.getCliente());
			vtaAbono.setEstado("APR");
			vtaAbono.setEmpresa(vtaActual.getEmpresa());
			vtaAbono.setUsrEfectua(loginUser.getUser());
			vtaAbono.setSucursal(loginUser.getUser().getSucursal());
			vtaAbono.setTipoDescuento(vtaActual.getTipoDescuento());
			vtaAbono.setCliCorp(vtaActual.getCliCorp());
			vtaAbono.setCodTipoVenta(vtaActual.getCodTipoVenta());
			getEntityManager().persist(vtaAbono);
			
			DetVentaProdServ detalleAbn = new DetVentaProdServ();
			detalleAbn.setCantidad(1);
			detalleAbn.setMonto(pago.getMonto());
			detalleAbn.setDetalle("Abono a venta");
			detalleAbn.setVenta(vtaAbono);
			detalleAbn.setCodClasifVta("ABN");
			detalleAbn.setCosto(pago.getMonto());
			
			getEntityManager().persist(detalleAbn);
			
			getEntityManager().flush();
			getEntityManager().refresh(instance);
			getEntityManager().refresh(vtaAbono);
			getEntityManager().refresh(detalleAbn);
			getEntityManager().refresh(pago);
		
		}
		else
		{
			
			//VentaProdServ vtaActual = (VentaProdServ)getEntityManager().createQuery("SELECT v FROM VentaProdServ v where v.id=:idV").setParameter("idV", instance.getId_venta()).getSingleResult();
			pago.setEstado("Aprobado");
			getEntityManager().merge(pago);
			VentaProdServ vtaAbono = new VentaProdServ();
			
			vtaAbono.setFechaVenta(new Date());
			vtaAbono.setTipoVenta("ABN");
			vtaAbono.setMonto(pago.getMonto());
			vtaAbono.setDetalle("Abono, comprobante: "+instance.getComprobante());
			vtaAbono.setIdDetalle(0);
			vtaAbono.setCliente(instance.getCliente());
			vtaAbono.setEstado("APR");
			vtaAbono.setEmpresa(null);
			vtaAbono.setUsrEfectua(loginUser.getUser());
			vtaAbono.setSucursal(loginUser.getUser().getSucursal());
			vtaAbono.setTipoDescuento(null);
			vtaAbono.setCliCorp(null);
			vtaAbono.setCodTipoVenta(null);
			getEntityManager().persist(vtaAbono);
			
			
			DetVentaProdServ detalleAbn = new DetVentaProdServ();
			detalleAbn.setCantidad(1);
			detalleAbn.setMonto(pago.getMonto());
			detalleAbn.setDetalle("Abono a venta");
			detalleAbn.setVenta(vtaAbono);
			detalleAbn.setCodClasifVta("ABN");
			detalleAbn.setCosto(pago.getMonto());
			
			getEntityManager().persist(detalleAbn);
			
			getEntityManager().flush();
			getEntityManager().refresh(instance);
			getEntityManager().refresh(vtaAbono);
			getEntityManager().refresh(detalleAbn);
			getEntityManager().refresh(pago);
			
			
			
		}
		
		
		
		
	}
	
	public void calcFechaVencimiento() {
		if(instance.getFechaIngreso() != null && instance.getDiasPlazo() != null) {
			Calendar fechaVen = new GregorianCalendar();
			fechaVen.setTime(instance.getFechaIngreso());
			fechaVen.add(Calendar.DATE, instance.getDiasPlazo());
			instance.setFechaVencimiento(fechaVen.getTime());
		}
	}
	
	

	
	@Override
	public boolean preSave() {
		guardarConcepto();
		java.util.Calendar cal = new java.util.GregorianCalendar();
		cal.setTime(instance.getFechaIngreso());
		cal.add(java.util.Calendar.DATE, instance.getDiasPlazo());
		instance.setFechaVencimiento(cal.getTime());
		//instance.setRemanente(instance.getMonto());
		instance.setEstado("ACT");
		return true;
	}

	@Override
	public boolean preModify() {
		guardarConcepto();
		java.util.Calendar cal = new java.util.GregorianCalendar();
		cal.setTime(instance.getFechaIngreso());
		cal.add(java.util.Calendar.DATE, instance.getDiasPlazo());
		instance.setFechaVencimiento(cal.getTime());
		return true;
	}
	
	public boolean actualizarOPagarSelCxc()
	{
		boolean res = true;
		Double totalCancelar = 0d;
		
		
		System.out.println("Entro a actualizar");
		if(getEstadoCuentaSelec().equals("PGD"))
		{
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultList) { //NOTA IMPORTANTE: la cuenta por cobrar tiene asignado un idVenta el que se puede utlizar para seleccioanr la venta y actualizar su estado de 'Pendiente con abono' a 'Pagada'
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("PGD");
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario()+ "\n" + " - " + descPagoClic);
					//getEntityManager().merge(tmpCxc);
					setInstance(tmpCxc);
					update();
					//getEntityManager().refresh(tmpCxc);
					//getEntityManager().flush();
					
					System.out.println("Entro al for pgd "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
			}
			
			System.out.println("Entro a pagar "+getEstadoCuentaSelec());
			
			//Aqui hay que generar una venta del cliente corporativo.
			
			
			//DetVentaProdServ det = new DetVentaProdServ();
			//det.setCantidad(1);
			//det.set
			
			
			//Generamos un asiento contable de las cuentas por cobrar saldadas
			asientoContableHome.genAsientoParametrizado("CXCVSVMD", "CTCAPCNT", totalCancelar.floatValue(), 
					"PAGO DE CxC CLIENTE CORPORATIVO", descPagoClic, null, fltCliCorp,  "ABO", "CRG");
			sainv_messages.clear();
			FacesMessages.instance().add(
					sainv_messages.get("ctxcb_cxcpagadas"));
			resultList.clear();
			
		}
		else if(getEstadoCuentaSelec().equals("FAC"))
		{
			
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultList) {
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					//tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("FAC");
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario()+ "\n" + " - " + descPagoClic);
					tmpCxc.setNumFactura(numFactura);
					//getEntityManager().merge(tmpCxc);
					//getEntityManager().refresh(tmpCxc);
					setInstance(tmpCxc);
					update();
					
					System.out.println("Entro al for fac "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
			}
			System.out.println("Entro a factura "+getEstadoCuentaSelec());
			
			resultList.clear();
			
		}
		else if(getEstadoCuentaSelec().equals("QDN"))
		{
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultList) {
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					//tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("QDN");
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario() + "\n" + " - " + descPagoClic);
					tmpCxc.setNumQuedan(numQuedan);
					//getEntityManager().merge(tmpCxc);
					//getEntityManager().refresh(tmpCxc);
					setInstance(tmpCxc);
					update();
					
					System.out.println("Entro al for qdn "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
			}
			
			System.out.println("Entro a quedan "+getEstadoCuentaSelec());
			
			
		}
		
		
		
		setNumeroInfo("");
		
		setTotalCxcCorp(0d);
		
		return res;
		
	}
	
	public boolean pagarSelCxc() {
		boolean res = true;
		Double totalCancelar = 0d;
		
		if(getEstadoCuentaSelec().equals("PGD")) //Ahora este estado tambien representa la pagina origen desde donde se realiza el pago, puede ser: list,listFac o listQued
		{
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultList) {
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					
					System.out.println("Factura" + tmpCxc.getNumFactura());
					System.out.println("Quedan" + tmpCxc.getNumQuedan());
					
					tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("PGD");//El estado ahora sera pagada.
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario() + "\n" + " - " + descPagoClic);
					//getEntityManager().merge(tmpCxc);
					//getEntityManager().refresh(tmpCxc);
					setInstance(tmpCxc);
					update();
					
					System.out.println("Entro al for pgd "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
				
			}
			
			System.out.println("Entro a pagada "+getEstadoCuentaSelec());
			
			
			//Generamos un asiento contable de las cuentas por cobrar saldadas
			asientoContableHome.genAsientoParametrizado("CXCVSVMD", "CTCAPCNT", totalCancelar.floatValue(), 
					"PAGO DE CxC CLIENTE CORPORATIVO", descPagoClic, null, fltCliCorp,  "ABO", "CRG");
			sainv_messages.clear();
			FacesMessages.instance().add(
					sainv_messages.get("ctxcb_cxcpagadas"));
			
		}
		else if(getEstadoCuentaSelec().equals("FAC")) //desde donde se paga . pagina Factura
		{
			
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultListFac) {
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("PGD");
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario() + "\n" + " - " + descPagoClic);
					tmpCxc.setNumFactura(numeroInfo);
					//getEntityManager().merge(tmpCxc);
					//getEntityManager().refresh(tmpCxc);
					setInstance(tmpCxc);
					update();
					
					System.out.println("Entro al for pgd "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
			}
			
			System.out.println("Entro a pagar por factura "+getEstadoCuentaSelec());
			
			resultListFac.clear();
			
		}
		else if(getEstadoCuentaSelec().equals("FACQDN")) //Cuando el estado se cambia de factura a quedan. Desde la vista listFac
		{
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultListFac) {
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					//tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("QDN");
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario() + "\n" + " - " + descPagoClic);
					tmpCxc.setNumQuedan(numQuedan);
					//getEntityManager().merge(tmpCxc);
					//getEntityManager().refresh(tmpCxc);
					setInstance(tmpCxc);
					update();
					
					System.out.println("Entro al for qdn "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
			}
			
			System.out.println("Entro a actualizar a quedan desde factura "+getEstadoCuentaSelec());
			
			resultListFac.clear();
			
		}
		
		else if(getEstadoCuentaSelec().equals("QDNFAC")) //Cuando el estado se cambia de quedan a factura. Desde la vista listQued
		{
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultListQued) {
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					//tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("FAC");
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario() + "\n" + " - " + descPagoClic);
					tmpCxc.setNumFactura(numeroInfo);
					//getEntityManager().merge(tmpCxc);
					//getEntityManager().refresh(tmpCxc);
					setInstance(tmpCxc);
					update();
					
					System.out.println("Entro al for FAC "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
			}
			
			System.out.println("Entro a actualizar a factura desde queda "+getEstadoCuentaSelec());
			
			resultListQued.clear();
			
		}
		
		else if( getEstadoCuentaSelec().equals("QDN"))
		{
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultListQued) {
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("PGD");
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario() + "\n" + " - " + descPagoClic);
					//tmpCxc.setNumQuedan(numQuedan);
					//getEntityManager().merge(tmpCxc);
					//getEntityManager().refresh(tmpCxc);
					setInstance(tmpCxc);
					update();
					System.out.println("Entro al for pgd "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
			}
			
			System.out.println("Entro a pagar desde quedan "+getEstadoCuentaSelec());
			
			//Generamos un asiento contable de las cuentas por cobrar saldadas
			asientoContableHome.genAsientoParametrizado("CXCVSVMD", "CTCAPCNT", totalCancelar.floatValue(), 
					"PAGO DE CxC CLIENTE CORPORATIVO", descPagoClic, null, fltCliCorp,  "ABO", "CRG");
			sainv_messages.clear();
			FacesMessages.instance().add(
					sainv_messages.get("ctxcb_cxcpagadas"));
			
			resultListQued.clear();
			
		}
		
		/*else if( getEstadoCuentaSelec().equals("QDNP"))
		{
			
			//Iteramos sobre la lista de CxC las que fueron seleccionadas
			setInstance(null);
			for(CuentaCobrar tmpCxc : resultList) {
				if(tmpCxc.isCxcSel()) {
					totalCancelar += tmpCxc.getMonto();
					tmpCxc.setRemanente(0f);
					tmpCxc.setEstado("QDN");
					tmpCxc.setFechaFinalizacion(new Date());
					tmpCxc.setComentario(tmpCxc.getComentario() + "\n" + " - " + descPagoClic);
					tmpCxc.setNumQuedan(numQuedan);
					//getEntityManager().merge(tmpCxc);
					//getEntityManager().refresh(tmpCxc);
					setInstance(tmpCxc);
					update();
					System.out.println("Entro al for pgd "+ tmpCxc.getEstado());
				}
				
				tmpCxc.setCxcSel(false);
			}
			
			System.out.println("Entro a pagar desde quedan "+getEstadoCuentaSelec());
			
			//Generamos un asiento contable de las cuentas por cobrar saldadas
			asientoContableHome.genAsientoParametrizado("CXCVSVMD", "CTCAPCNT", totalCancelar.floatValue(), 
					"PAGO DE CxC CLIENTE CORPORATIVO", descPagoClic, null, fltCliCorp,  "ABO", "CRG");
			sainv_messages.clear();
			FacesMessages.instance().add(
					sainv_messages.get("ctxcb_cxcpagadas"));
			
			resultListQued.clear();
			
		}*/
		
		//setNumeroInfo("");
	
		setTotalCxcCorp(0d);
		
		return res;
	}
	
	private void guardarConcepto() {
		
		if(!conceptoMovHome.isManaged() ||
			!conceptoMovHome.getInstance().getNombre().toUpperCase().equals(conceptoMovHome.getConcepto().toUpperCase())) {
			ConceptoMov concMov = new ConceptoMov();
			concMov.setNombre(conceptoMovHome.getConcepto());
			conceptoMovHome.select(concMov);
			conceptoMovHome.save();
		} 
		instance.setConcepto(conceptoMovHome.getInstance());
	}
	
	public void updTotalCxcCorp() {
		
		totalCxcCorp = 0d;
		for(CuentaCobrar tmpCxc : resultList) 
			if(tmpCxc.isCxcSel()) {
				if(tmpCxc.getRemanente() == null)
					totalCxcCorp += tmpCxc.getMonto();
				else
					totalCxcCorp += tmpCxc.getRemanente();
			}
	}
	
	public void updTotalCxcCorpFac() {
		totalCxcCorp = 0d;
		for(CuentaCobrar tmpCxc : resultListFac) 
			if(tmpCxc.isCxcSel()) {
				if(tmpCxc.getRemanente() == null)
					totalCxcCorp += tmpCxc.getMonto();
				else
					totalCxcCorp += tmpCxc.getRemanente();
			}
	}
	
	public void updTotalCxcCorpQued() {
		totalCxcCorp = 0d;
		for(CuentaCobrar tmpCxc : resultListQued) 
			if(tmpCxc.isCxcSel()) {
				if(tmpCxc.getRemanente() == null)
					totalCxcCorp += tmpCxc.getMonto();
				else
					totalCxcCorp += tmpCxc.getRemanente();
			}
	}

	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public void posSave() {
		//Generamos el asiento contable correspondiente a la cuenta por cobrar
	/*	asientoContableHome.genAsientoParametrizado("CXCVSVMD", "CTCAPCNT", instance.getMonto(), 
				instance.getConcepto().getNombre(), instance.getComentario(), instance.getCliente(), null, "CRG", "ABO");
		instance.setAsiento(asientoContableHome.getInstance());
		getEntityManager().merge(instance);
		//getEntityManager().flush();
		getEntityManager().refresh(instance);*/
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<CuentaCobrar> getResultList() {
		return resultList;
	}

	public void setResultList(List<CuentaCobrar> resultList) {
		this.resultList = resultList;
	}

	public Integer getCxcId() {
		return cxcId;
	}

	public void setCxcId(Integer cxcId) {
		this.cxcId = cxcId;
	}

	public List<CondicionPago> getCondicionesPago() {
		return condicionesPago;
	}

	public void setCondicionesPago(List<CondicionPago> condicionesPago) {
		this.condicionesPago = condicionesPago;
	}
	
	public String getFechaVencimientoFlt() {
		return fechaVencimientoFlt;
	}
	
	public void setFechaVencimientoFlt(String f) {
		fechaVencimientoFlt = f;
	}

	public ClienteCorporativo getFltCliCorp() {
		return fltCliCorp;
	}

	public void setFltCliCorp(ClienteCorporativo fltCliCorp) {
		this.fltCliCorp = fltCliCorp;
	}

	public String getDescPagoClic() {
		return descPagoClic;
	}

	public void setDescPagoClic(String descPagoClic) {
		this.descPagoClic = descPagoClic;
	}

	public Double getTotalCxcCorp() {
		return totalCxcCorp;
	}

	public void setTotalCxcCorp(Double totalCxcCorp) {
		this.totalCxcCorp = totalCxcCorp;
	}

	public String getDescPagoCxc() {
		return descPagoCxc;
	}

	public void setDescPagoCxc(String descPagoCxc) {
		this.descPagoCxc = descPagoCxc;
	}

	

	public Float getMontoPagoCxc() {
		return montoPagoCxc;
	}

	public void setMontoPagoCxc(Float montoPagoCxc) {
		this.montoPagoCxc = montoPagoCxc;
	}

	public CondicionPago getCondPagoCxc() {
		return condPagoCxc;
	}

	public void setCondPagoCxc(CondicionPago condPagoCxc) {
		this.condPagoCxc = condPagoCxc;
	}

	public String getCodComprobante() {
		return codComprobante;
	}

	public void setCodComprobante(String codComprobante) {
		this.codComprobante = codComprobante;
	}

	public String getEstadoCuentaSelec() {
		return estadoCuentaSelec;
	}

	public void setEstadoCuentaSelec(String estadoCuentaSelec) {
		this.estadoCuentaSelec = estadoCuentaSelec;
	}

	public List<CuentaCobrar> getResultListFac() {
		return resultListFac;
	}

	public void setResultListFac(List<CuentaCobrar> resultListFac) {
		this.resultListFac = resultListFac;
	}

	public List<CuentaCobrar> getResultListQued() {
		return resultListQued;
	}

	public void setResultListQued(List<CuentaCobrar> resultListQued) {
		this.resultListQued = resultListQued;
	}

	public List<CuentaCobrar> getResultListPagadas() {
		return resultListPagadas;
	}

	public void setResultListPagadas(List<CuentaCobrar> resultListPagadas) {
		this.resultListPagadas = resultListPagadas;
	}

	public String getBusquedaCliente() {
		return busquedaCliente;
	}

	public void setBusquedaCliente(String busquedaCliente) {
		this.busquedaCliente = busquedaCliente;
	}

	public String getNumeroInfo() {
		return numeroInfo;
	}

	public void setNumeroInfo(String numeroInfo) {
		this.numeroInfo = numeroInfo;
	}

	public String getNumFactura() {
		return numFactura;
	}

	public void setNumFactura(String numFactura) {
		this.numFactura = numFactura;
	}

	public String getNumQuedan() {
		return numQuedan;
	}

	public void setNumQuedan(String numQuedan) {
		this.numQuedan = numQuedan;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	
	
	
	
}
