package com.sa.kubekit.action.workshop;

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
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.sales.VentaItemHome;
import com.sa.kubekit.action.sales.VentaProdServHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.CuentaCobrar;
import com.sa.model.crm.Cliente;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.sales.ComboAparato;
import com.sa.model.sales.CotCmbsItems;
import com.sa.model.sales.CotizacionComboApa;
import com.sa.model.sales.CotizacionCombos;
import com.sa.model.sales.CotizacionPrdSvcAdicionales;
import com.sa.model.sales.DetVentaProdServ;
import com.sa.model.sales.ItemComboApa;
import com.sa.model.sales.Service;
import com.sa.model.sales.VentaProdServ;
import com.sa.model.workshop.AparatoCliente;
import com.sa.model.workshop.ComponenteAparato;
import com.sa.model.workshop.ComponenteDefRep;
import com.sa.model.workshop.CondAparatoRep;
import com.sa.model.workshop.CondicionAparato;
import com.sa.model.workshop.DefCapsulaRep;
import com.sa.model.workshop.DefectoCapsula;
import com.sa.model.workshop.EtapaRepCliente;
import com.sa.model.workshop.EtapaReparacion;
import com.sa.model.workshop.ItemRequisicionEta;
import com.sa.model.workshop.ProcesoTaller;
import com.sa.model.workshop.ReparacionCliente;
import com.sa.model.workshop.RequisicionEtapaRep;
import com.sa.model.workshop.ServicioReparacion;

@Name("reparacionClienteHome")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings("unchecked")
public class ReparacionClienteHome extends KubeDAO<ReparacionCliente>{

	private static final long serialVersionUID = 1L;
	
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private List<AparatoCliente> aparatosCli = new ArrayList<AparatoCliente>();
	private List<ProcesoTaller> procesosTaller = new ArrayList<ProcesoTaller>();
	private List<EtapaReparacion> etapasTaller = new ArrayList<EtapaReparacion>();
	private List<ReparacionCliente> resultList = new ArrayList<ReparacionCliente>();
	private List<CondicionAparato> selCondicionesApa = new ArrayList<CondicionAparato>();
	private List<ComponenteAparato> selComponentesDef = new ArrayList<ComponenteAparato>();
	private List<DefectoCapsula> selDefectosCap = new ArrayList<DefectoCapsula>();
	private List<ServicioReparacion> serviciosRep = new ArrayList<ServicioReparacion>();
	private List<Service> servsCobro = new ArrayList<Service>();
	private Date fecDeHoy;	
	private boolean tipoLista;
	private boolean costoEditable;
	private Double precioEstimado;
	private Double totalServs;
	private Double totalItems;
	private boolean garVtaVigente;
	private boolean garRepVigente;
	private boolean cobrarSiempre;
	private String flagPrsEntrega;
	private String nomCoinci;
	private Integer repCliId;
	private String taller;
	private String nombre;
	private String estado;
	
	
	private String repCliId2="";
	
	private List<ItemRequisicionEta> requisicionesCobro = new ArrayList<ItemRequisicionEta>();
	
	private Float totalAcobrar;
	private Float descuentoCobro;
	
	
	//nuevo agregado el 07/09/2017
	private List<CondAparatoRep> selCondicionesApaN = new ArrayList<CondAparatoRep>();
	private List<ComponenteDefRep> selComponentesDefN = new ArrayList<ComponenteDefRep>();
	private List<DefCapsulaRep> selDefectosCapN = new ArrayList<DefCapsulaRep>();
	
	
	@In
	private LoginUser loginUser;
	
	@In(required = false, create = true)
	@Out(required = false)
	private EtapaRepCliHome etapaRepCliHome;
	
	@In(required = false, create = true)
	@Out(required = false)
	private VentaProdServHome ventaProdServHome;
				
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("repCliHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("repCliHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("repCliHome_deleted")));
		
	}
	
	public boolean tieneGarantiaVigente(Date fechaInicio, Integer diasGarantia) {
		if(diasGarantia == null || fechaInicio == null)
			return false;
		
		Calendar perGarIni = new GregorianCalendar();
		Calendar perGarFin = new GregorianCalendar();
		perGarFin.setTime(fechaInicio);
		perGarIni.setTime(fechaInicio);
		perGarFin.add(Calendar.DATE, diasGarantia);
		Calendar currDt = new GregorianCalendar();
		currDt.set(Calendar.HOUR_OF_DAY, 0);
		currDt.set(Calendar.MINUTE, 0);
		currDt.set(Calendar.SECOND, 2);
		perGarIni.set(Calendar.HOUR_OF_DAY, 0);
		perGarIni.set(Calendar.MINUTE, 0);
		perGarIni.set(Calendar.SECOND, 1);
		perGarFin.set(Calendar.HOUR_OF_DAY, 23);
		perGarFin.set(Calendar.MINUTE, 59);
		perGarFin.set(Calendar.SECOND, 59);
		if( currDt.getTimeInMillis() >= perGarIni.getTimeInMillis() && currDt.getTimeInMillis() <= perGarFin.getTimeInMillis() )
			return true;
		else
			return false;
	}
	
	public boolean isNumeric(String cadena)
	{
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	public boolean isNumericFloat(String cadena)
	{
		try {
			Float.parseFloat(cadena);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	
	public void load(){
		try{
			
			if(!getRepCliId2().equals("") && getRepCliId2()!=null)
			{
				//System.out.println("Entro al if ******");
				if(!isNumeric(repCliId2))
				{
					repCliId =Integer.parseInt(repCliId2.substring(3));
					//System.out.println("El caracter es : "+repCliId);
				}
				else
				{
					repCliId=Integer.parseInt(repCliId2);
					//System.out.println("El numero es : "+repCliId);
				}
				
			}
			
			
			//instance.getEtapasReparacion().get(index)
			
			
			servsCobro = new ArrayList<Service>();
			
			procesosTaller = getEntityManager().createQuery("SELECT p from ProcesoTaller p ").getResultList();
			
			
			if(repCliId!=null)
			{
				System.out.println("CARGO la REPARACION ******");
				
				setInstance((ReparacionCliente) getEntityManager().createQuery("SELECT r FROM ReparacionCliente r where r.id = :id")
						.setParameter("id", repCliId).getResultList().get(0));
			
				
				if(instance.getAparatoRep()!=null)
				{
					//Verificamos si aun se tiene la garantia vigente
					garVtaVigente = tieneGarantiaVigente(instance.getAparatoRep().getFechaAdquisicion(), instance.getAparatoRep().getPeriodoGarantia());
				

					/*
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					Integer hoy = Integer.valueOf(sdf.format(new Date()));
					Integer inicial = Integer.valueOf(sdf.format(instance.getAparatoRep().getFechaGarRep())) -1;
					Integer fin = inicial + 1 + instance.getAparatoRep().getPeriodoGarantiaRep();
					
					if (hoy <= fin && hoy >= inicial){
						//esta en garantia
					} else {
						if (hoy > fin)
							// se vencio la garantia
						else
							// todavia no ha entrado en garantia
					}
					*/
					
					Date fa = new Date();		
					if (instance.getAparatoRep().getFechaGarRep()!=null && instance.getAparatoRep().getFechaGarRep().after(fa))
					{
						Calendar calMan = new GregorianCalendar();
						calMan.setTime(instance.getAparatoRep().getFechaGarRep());
						calMan.add(Calendar.DATE, -1);
						//System.out.println("Mira esta es la fecha: "+ calMan.getTime());
						//System.out.println("Mira esta es la fecha de base de datos "+ instance.getAparatoRep().getFechaAdquisicion().getTime());
						garRepVigente = tieneGarantiaVigente(calMan.getTime(), instance.getAparatoRep().getPeriodoGarantiaRep());
					}
					else if (instance.getAparatoRep().getFechaGarRep()!= null) 
					{
						garRepVigente = tieneGarantiaVigente(instance.getAparatoRep().getFechaGarRep(), instance.getAparatoRep().getPeriodoGarantiaRep());
					}
					
					/*System.out.println("TAMANIO Componentes INS " + instance.getCompsDefAparato().size());
					System.out.println("TAMANIO CONDICIONES INS " + instance.getCondsAparatorep().size());
					System.out.println("TAMANIO DEFECTOS INS " + instance.getDefCapsAparato().size());
					System.out.println("Tamanio de los servicios "+instance.getServiciosRep().size());*/
					
					
					//Cargamos lista de comopnentes defectuosos, partes de capsulas defectuosas and so on 
					selComponentesDefN.clear(); selCondicionesApaN.clear(); selDefectosCapN.clear();
					
					
					//nuevo agregado el 08/09/2017
					
					instance.setCompsDefAparato(getEntityManager().createQuery("SELECT c FROM ComponenteDefRep c where c.repCliente.id=:idRep").setParameter("idRep",repCliId).getResultList());
					instance.setCondsAparatorep(getEntityManager().createQuery("SELECT cm FROM CondAparatoRep cm where cm.repCliente.id=:idRep").setParameter("idRep", repCliId).getResultList());
					instance.setDefCapsAparato(getEntityManager().createQuery("SELECT d FROM DefCapsulaRep d where d.repCliente.id=:idRep").setParameter("idRep", repCliId).getResultList());
					
					instance.setServiciosRep(getEntityManager().createQuery("SELECT s FROM ServicioReparacion s where s.reparacion.id=:idRep").setParameter("idRep", repCliId).getResultList());
					
					for(ComponenteDefRep tmpCmp : instance.getCompsDefAparato())
						selComponentesDefN.add(tmpCmp);
							
					for(CondAparatoRep tmpCnd : instance.getCondsAparatorep())
						selCondicionesApaN.add(tmpCnd);
					
					for(DefCapsulaRep tmpDef : instance.getDefCapsAparato())
						selDefectosCapN.add(tmpDef);
					
					serviciosRep.clear();
					serviciosRep.addAll(instance.getServiciosRep());
					
				}
				
				//System.out.println("Tamanio de lista de servicios agregados"+instance.getServiciosRep().size());
				
				List<EtapaRepCliente> etas = getEntityManager()
						.createQuery("SELECT e FROM EtapaRepCliente e WHERE e.reparacionCli = :rep " +
								"	AND e.estado = 'APR' AND e.etapaRep.codEta = 'EVA' ORDER BY e.etapaRep.orden asc ")
						.setParameter("rep", instance)
						.getResultList();
			
				if(etas != null && etas.size() > 0) { //Calculamos el precio tentativo en base a las requisiciones y los servicios
					precioEstimado = 0d;
					if(instance.getServiciosRep() != null) {
						for(ServicioReparacion tmpServ : instance.getServiciosRep()) 
							precioEstimado += tmpServ.getServicio().getCosto();
					}
					
					List<ItemRequisicionEta> lstItemsReq = getEntityManager()
							.createQuery("SELECT i FROM ItemRequisicionEta i WHERE i.reqEtapa.etapaRepCli.reparacionCli = :rep")
							.setParameter("rep", instance)
							.getResultList();
					
					for(ItemRequisicionEta tmpIR : lstItemsReq)  
						precioEstimado += tmpIR.getProducto().getPrcNormal() * tmpIR.getCantidad();
					
				}
				
				//System.out.println("Tamanio de lista de servicios agregados"+instance.getServiciosRep().size());
				if(instance.getServiciosRep().size()>0)
				{
					setServiciosRep(instance.getServiciosRep());
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			clearInstance();
			setInstance(new ReparacionCliente());
		}
		editarCosto();
	}
	
	/*public void load2(){
		try{
			servsCobro = new ArrayList<Service>();
			procesosTaller = getEntityManager().createQuery("select p from ProcesoTaller p ").getResultList();
			setInstance((ReparacionCliente) getEntityManager().createQuery("select r from ReparacionCliente r where r.id = :id")
					.setParameter("id", repCliId).getSingleResult());
			
			//Verificamos si aun se tiene la garantia vigente
			garVtaVigente = tieneGarantiaVigente(instance.getAparatoRep().getFechaAdquisicion(), instance.getAparatoRep().getPeriodoGarantia());
		
			Date fa = new Date();		
			if (instance.getAparatoRep().getFechaGarRep()!=null && instance.getAparatoRep().getFechaGarRep().after(fa)){
				Calendar calMan = new GregorianCalendar();
				calMan.setTime(instance.getAparatoRep().getFechaGarRep());
				calMan.add(Calendar.DATE, -1);
				System.out.println("Mira esta es la fecha: "+ calMan.getTime());
				System.out.println("Mira esta es la fecha de base de datos "+ instance.getAparatoRep().getFechaAdquisicion().getTime());
				garRepVigente = tieneGarantiaVigente(calMan.getTime(), instance.getAparatoRep().getPeriodoGarantiaRep());
			}
			else if (instance.getAparatoRep().getFechaGarRep()!= null) {
				garRepVigente = tieneGarantiaVigente(instance.getAparatoRep().getFechaGarRep(), instance.getAparatoRep().getPeriodoGarantiaRep());
			}
			
			//Cargamos lista de comopnentes defectuosos, partes de capsulas defectuosas and so on 
			selComponentesDef.clear(); selCondicionesApa.clear(); selDefectosCap.clear();
			for(ComponenteDefRep tmpCmp : instance.getCompsDefAparato())
				selComponentesDef.add(tmpCmp.getCmpAparato());
					
			for(CondAparatoRep tmpCnd : instance.getCondsAparatorep())
				selCondicionesApa.add(tmpCnd.getCondAparato());
			
			for(DefCapsulaRep tmpDef : instance.getDefCapsAparato())
				selDefectosCap.add(tmpDef.getDefCapsula());
			serviciosRep.clear();
			serviciosRep.addAll(instance.getServiciosRep());
			
			List<EtapaRepCliente> etas = getEntityManager()
					.createQuery("SELECT e FROM EtapaRepCliente e WHERE e.reparacionCli = :rep " +
							"	AND e.estado = 'APR' AND e.etapaRep.codEta = 'EVA' ORDER BY e.etapaRep.orden asc ")
					.setParameter("rep", instance)
					.getResultList();
			
			if(etas != null && etas.size() > 0) { //Calculamos el precio tentativo en base a las requisiciones y los servicios
				precioEstimado = 0d;
				if(instance.getServiciosRep() != null) {
					for(ServicioReparacion tmpServ : instance.getServiciosRep()) 
						precioEstimado += tmpServ.getServicio().getCosto();
				}
				
				List<ItemRequisicionEta> lstItemsReq = getEntityManager()
						.createQuery("SELECT i FROM ItemRequisicionEta i WHERE i.reqEtapa.etapaRepCli.reparacionCli = :rep")
						.setParameter("rep", instance)
						.getResultList();
				
				for(ItemRequisicionEta tmpIR : lstItemsReq)  
					precioEstimado += tmpIR.getProducto().getPrcNormal() * tmpIR.getCantidad();
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			clearInstance();
			setInstance(new ReparacionCliente());
		}
		editarCosto();
	}*/
	
	public void cargarListaReparaciones() 
	{
		String jpql = " SELECT x FROM ReparacionCliente x WHERE x.fechaIngreso BETWEEN :fch1 AND :fch2 AND (UPPER(x.cliente.nombres) LIKE UPPER(:nom) OR UPPER(x.cliente.apellidos) LIKE UPPER(:nom)) ORDER BY x.id DESC";
		String jpql2="SELECT x FROM ReparacionCliente x WHERE x.fechaIngreso BETWEEN :fch1 AND :fch2 ORDER BY x.id DESC";
		//createQuery(jpql + fltFch + " AND (UPPER(x.cliente.nombres) LIKE UPPER(:nom) OR UPPER(x.cliente.apellidos) LIKE UPPER(:nom)) ORDER BY repcli_id DESC ")
		//String jpqx = "select x from ReparacionCliente x WHERE id >= 2000 ";
		//String jpqy = "select x from ReparacionCliente x WHERE id > 1 ";

		System.out.println("Orden de laboratorio");
		
		/*if(taller!=null && !taller.equals(""))
		{
			String cod; int num=0;
			
			cod=taller.substring(0, 2).toUpperCase();
			
			if(cod.equals("RPR"))
			num=1;
			else if(cod.equals("LMP"))
			num=2;
			else if(cod.equals("MLD"))
			num=3;
			else if(cod.equals("ENS"))
			num=4;
			
			jpql += "and ";
			jpql += "x.proceso.id ="+num+" and id '%" + taller.substring(3, taller.length()-1)+ "%' ";
		}
		
		if(nombre!=null && !nombre.equals(""))
		{
			jpql += "and ";
			jpql += "x.nombreRecibe  like '%" + nombre.toUpperCase() + "%' ";
		}
		
		if(estado!=null && !estado.equals(""))
		{
			jpql += "and ";
			jpql += "x.estado  like '%" + estado+ "%' ";
		}
		
		
		System.out.println(taller +" / "+ nombre +" / "+ estado);
		System.out.println(jpql);

		*/
		
		
	
		//String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
		/*String fltFch="";

		if(getFechaPFlt1() != null && getFechaPFlt2() != null) 
		{
			setFechaPFlt1(truncDate(getFechaPFlt1(), false));
			setFechaPFlt2(truncDate(getFechaPFlt2(), true));
		    fltFch = " x.fechaIngreso BETWEEN :fch1 AND :fch2 ";
		}
		*/
		//System.out.println("NomCoinci: " +getNomCoinci());
		try {
			if (getNomCoinci()==null){
				resultList = getEntityManager().createQuery(jpql2)
						.setParameter("fch1", getFechaPFlt1())
						.setParameter("fch2", getFechaPFlt2())
						.getResultList();

				for(ReparacionCliente rc: resultList){
					rc.setCurrEtaNom(getCurrentEtapaRepCli(rc));
				}
			} else{
				
				resultList = getEntityManager().createQuery(jpql)
						.setParameter("nom", "%"+this.getNomCoinci()+"%")
						.setParameter("fch1", getFechaPFlt1())
						.setParameter("fch2", getFechaPFlt2())
						.getResultList();
				//System.out.println("Size del resultList de Reparaciones: "+resultList.size());
				for(ReparacionCliente rc: resultList){
					rc.setCurrEtaNom(getCurrentEtapaRepCli(rc));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		
		//System.out.println("Normal = "+ jpql);

	}
	
	
	
	public List<ItemRequisicionEta> getPrdsReqEtapa(EtapaRepCliente eta) {
		List<ItemRequisicionEta> items = getEntityManager()
				.createQuery("SELECT i FROM ItemRequisicionEta i WHERE i.reqEtapa.etapaRepCli.reparacionCli = :rep")
				.setParameter("rep", eta.getReparacionCli())
				.getResultList();
		return items;
	}
	
	public void loadProcesosTaller() {
		procesosTaller = getEntityManager()
				.createQuery("SELECT p FROM ProcesoTaller p ")
				.getResultList();
	}
	
	public void loadEtapasTaller() {
		etapasTaller = getEntityManager()
				.createQuery("SELECT e FROM EtapaReparacion e ")
				.getResultList();
	}
		
	public void editarCosto() {
		costoEditable = false;
		if(!isManaged()) { 
			if(this.instance.getProceso() != null && this.instance.getProceso().isRequiereRev() == false)
				costoEditable = true;
			else
				instance.setCosto(null);
		} else {
			if(!instance.getProceso().isRequiereRev() && instance.getCosto() == null)
				costoEditable = true;
			else {
				List<EtapaRepCliente> etapasOrdered = getEntityManager().createQuery("SELECT e FROM EtapaRepCliente e " +
						"	WHERE e.estado = 'APR' and e.reparacionCli = :rep ORDER BY e.etapaRep.orden ASC")
						.setParameter("rep", instance)
						.getResultList();
				if(etapasOrdered != null && etapasOrdered.size() > 0)
					costoEditable = true;
			}
		}
		
	}

	public void cargarListaClientes(){
		//clientes = getEntityManager().createQuery("select c from Cliente c ").getResultList();
		  clientes = getEntityManager().createQuery("select c from Cliente c where id < 2000 ").getResultList();
		  clientes.addAll(getEntityManager().createQuery("select c from Cliente c where id >=2000").getResultList());
		 		  
		tipoLista = true;
	}
	
	public boolean aprobacionCliente() {
		System.out.println("Size del getEtapasReparacion" + instance.getEtapasReparacion().size());
		System.out.println(" Etapa actual: (CurrEtapa().getDescripcion) "+ getCurrentEtapaRepCli(instance) );
		// Verificamos que no existan requisiciones pendientes antes de proceder a autorizar el trabajo de taller. 
		if ( getCurrentEtapaRepCli(instance).equals("Esperando Aprobación")){
			for (EtapaRepCliente tmpEta : instance.getEtapasReparacion()) {
				if (tmpEta.getEtapaRep().getId().equals(102)) {
					for (RequisicionEtapaRep tmpReq : tmpEta.getRequisicionesEtapa()){
						// SI hay requisiciones pendientes
						if (tmpReq.getEstado().equals("PEN")){
							FacesMessages.instance().add(Severity.ERROR,sainv_messages.get("repCliHome_err_req_pen"));
							return false;
						}	
					}
				}
			}
		}
		for (EtapaRepCliente tmpEta : instance.getEtapasReparacion()) {
			
				// SI la etapa es "Esperando Aprobación"
			if (tmpEta.getEtapaRep().getId().equals(102)) {
				tmpEta.setUsuario(loginUser.getUser());//Nuevo el 08/06/2017
				tmpEta.setFechaRealFin(new Date());
				tmpEta.setEstado("APR");
			}
				// SI la etapa es "Reparacion"
			if (tmpEta.getEtapaRep().getId().equals(42)) {
				tmpEta.setFechaInicio(new Date());
				tmpEta.setEstado("PEN");
			}
		}
		instance.setAprobada(true);
		boolean res = modify();
		
		FacesMessages.instance().clear();
		if(res)
			FacesMessages.instance().add(
					sainv_messages.get("repCliHome_scs_apr"));
		else
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("repCliHome_err_apr"));
		return res;
	}
	
	public void entregaCliente() {
		
		//Extraer etapa de entrega de cliente para actualizar la fecha.
		//System.out.println(instance.getCurrEtapa());
		
		if(instance.getProceso().getNombre().equals("Reparacion"))
		{
			System.out.println("Entro a la condicion de reparacion de entrega");
			EtapaRepCliente etapaEntrega =(EtapaRepCliente)getEntityManager().createQuery("Select et from EtapaRepCliente et where et.reparacionCli=:reparacionAct and et.etapaRep.orden=7 ").setParameter("reparacionAct", instance).getSingleResult();
			etapaEntrega.setFechaRealFin(new Date());
			etapaEntrega.setEstado("APR");
			getEntityManager().merge(etapaEntrega);
		}
		else if(instance.getProceso().getNombre().equals("Fabricacion de molde"))
		{ 
			System.out.println("Entro a la condicion de elaboracion de molde de entrega");
			EtapaRepCliente etapaEntrega =(EtapaRepCliente)getEntityManager().createQuery("Select et from EtapaRepCliente et where et.reparacionCli=:reparacionAct and et.etapaRep.orden=5 ").setParameter("reparacionAct", instance).getSingleResult();
			etapaEntrega.setFechaRealFin(new Date());
			etapaEntrega.setEstado("APR");
			getEntityManager().merge(etapaEntrega);
		}
		
		else if(instance.getProceso().getNombre().equals("Limpieza"))
		{ System.out.println("Entro a la limpieza ");
			EtapaRepCliente etapaEntrega =(EtapaRepCliente)getEntityManager().createQuery("Select et from EtapaRepCliente et where et.reparacionCli=:reparacionAct and et.etapaRep.orden=3 ").setParameter("reparacionAct", instance).getSingleResult();
			etapaEntrega.setFechaRealFin(new Date());
			etapaEntrega.setEstado("APR");
			getEntityManager().merge(etapaEntrega);
		}
		
		else if(instance.getProceso().getNombre().equals("Ensamblaje de aparato"))
		{ System.out.println("Entro al ensamblaje ");
			EtapaRepCliente etapaEntrega =(EtapaRepCliente)getEntityManager().createQuery("Select et from EtapaRepCliente et where et.reparacionCli=:reparacionAct and et.etapaRep.orden=5 ").setParameter("reparacionAct", instance).getSingleResult();
			etapaEntrega.setFechaRealFin(new Date());
			etapaEntrega.setEstado("APR");
			getEntityManager().merge(etapaEntrega);
		}
		
		
		
		//Verificar el tipo de reparacion
		
		if(instance.getTipoRep().equals("CRE"))
		{
			if(!validarEntregaCredito())
				return;
		}
		
		if(instance.getTipoRep().equals("PRE"))
		{

			//Actualizar la cotizacion
			List<CotizacionComboApa> cotiRep = getEntityManager().createQuery("SELECT c FROM CotizacionComboApa c where c.id=:idCot").setParameter("idCot", instance.getIdCot()).getResultList();
			
			if(cotiRep.size()>0 && cotiRep!=null)
			{
				cotiRep.get(0).setEstado("TFN");
				getEntityManager().merge(cotiRep.get(0));
				//System.out.println("Actualizo estado de cotizacion luego de trabajo de taller");
			}
			
			
			
		}
		
		//System.out.println("No entro");
		
		
		instance.setEstado("DLV");
		instance.setFechaEntrega(new Date());
		
		if (flagPrsEntrega != null) {
			if (flagPrsEntrega.equals("D"))
				instance.setNombreRecibe(instance.getCliente().getNombres()	+ " " + instance.getCliente().getApellidos());
		} else
			instance.setNombreRecibe(instance.getCliente().getNombres() + " "+ instance.getCliente().getApellidos());
		

		
		modify();
	}
	
	//nuevo el 20/07/2017
	public void prepararEntrega()
	{
		//nuevo 20/07/2017
		setFlagPrsEntrega("D");
		cargarRequisicionesCobro();
		verificarGarantia();
		
		recalcularTotalesCobro();
		
	}
	
	//Metodo para validar la entrega del aparato al cliente, que se ha solicitado a traves de una venta de combo de aparato por credito
	public boolean validarEntregaCredito()
	{
		
		CotizacionComboApa cotizacion = (CotizacionComboApa) getEntityManager().createQuery("Select c From CotizacionComboApa c where c.id=:idCot").setParameter("idCot", instance.getIdCot()).getSingleResult();
		CuentaCobrar cxcAct;
		
		//Si la cotizacion no es binaural
		if(cotizacion.getCotizacionComboBin()==null)
			cxcAct=(CuentaCobrar) getEntityManager().createQuery("Select cx from CuentaCobrar cx where cx.id=:idC").setParameter("idC",cotizacion.getCuentaCobrar().getId()).getSingleResult();
		else
		{
			//Seleccionamos la cotizacion que tiene el id de la cxc
			CotizacionComboApa cotiCxc=(CotizacionComboApa) getEntityManager().createQuery("Select c from CotizacionComboApa c where c.id=:idCotiCxc").setParameter("idCotiCxc", cotizacion.getId()-1).getSingleResult();
			
			cxcAct=(CuentaCobrar) getEntityManager().createQuery("Select cx from CuentaCobrar cx where cx.id=:idC").setParameter("idC",cotiCxc.getCuentaCobrar().getId()).getSingleResult();
		}
		
		if(cxcAct.getRemanente()>0)
		{
			FacesMessages.instance().add(Severity.WARN,"Aun tiene cuotas pendientes");
			return false;
		}
		else
		{
			/*
			//Inventario inv = getEntityManager().createQuery("Select i from Inventario where i.producto")
			Producto produc= (Producto) getEntityManager().createQuery("SELECT p FROM Producto p where p.id=:idProducto").setParameter("idProducto",instance.getAparatoRep().getIdPrd()).getSingleResult();
			//Inventario inve= (Inventario) getEntityManager().createQuery("SELECT i FROM Inventario i where i.id=:idSucursal").setParameter("idSucursal", cotizacion.getSucursal()).getSingleResult();//Sucursal en la que se realizo la cotizacion, debe ser bodega?
			
			//Verificamos si la categoria del producto(aparato) asignado al cliente requiere numero de seria para asi asignarselo
			if(produc.getCategoria().isTieneNumSerie() &&  instance.getAparatoRep().getNumSerie()!=null)//Posiblemente deba cambiarse la comparacion a equals y isEmpty
			{
				
				List<CodProducto> codsProducto = (ArrayList<CodProducto>) getEntityManager()
						.createQuery(
								"SELECT c FROM CodProducto c "
										+ "	WHERE c.inventario.producto = :prd AND c.inventario.sucursal = :sucursalAct AND c.estado = 'ACT' ")
						.setParameter("prd", produc)
						.setParameter("sucursalAct", cotizacion.getSucursal()).getResultList(); //Posiblemente la sucursal tenga que ser una bodega
				
				//Si existen numeros de seria disponibles se asignan al producto
				if(codsProducto.size()>0)
				{
					instance.getAparatoRep().setNumSerie(codsProducto.get(0).getNumSerie());
					codsProducto.get(0).setEstado("USD");
					getEntityManager().merge(codsProducto.get(0));
					
				}
				else
				{
					FacesMessages.instance().add(Severity.WARN,"No existen numeros de serie disponibles para el producto");
					return false;
				}
					
					
			}
			
			//Verificamos si la categoria del producto(aparato) asignado al cliente requiere numero de lote para asi asignarselo
			if(produc.getCategoria().isTieneNumLote() && instance.getAparatoRep().getNumLote()!=null)
			{
				
				List<CodProducto> codsProducto = (ArrayList<CodProducto>) getEntityManager()
						.createQuery(
								"SELECT c FROM CodProducto c "
										+ "	WHERE c.inventario.producto = :prd AND c.inventario.sucursal = :sucursalAct AND c.estado = 'ACT' ")
						.setParameter("prd", produc)
						.setParameter("sucursalAct", loginUser.getUser().getSucursal()).getResultList();
				
				//Si existen numeros de lote disponibles se asignan al producto
				if(codsProducto.size()>0)
				{
					instance.getAparatoRep().setNumLote(codsProducto.get(0).getNumLote());
					codsProducto.get(0).setEstado("USD");
					getEntityManager().merge(codsProducto.get(0));
					
					
					
				}
				else
				{
					FacesMessages.instance().add(Severity.WARN,"No existen numeros de lote disponibles para el producto");
					return false;
				}
					
					
			}
			
			
			//Extrar los combos cotizados en la cotizacion actual para reducir inventario 
			List<CotizacionCombos> combosCotizados = getEntityManager()
					.createQuery("SELECT cm FROM CotizacionCombos cm where cm.cotizacion=:cotizacion")
					.setParameter("cotizacion", cotizacion).getResultList();
			
			//Este es el combo que buscamos, al que pertenece el producto del cliente, y al que se le asigno el numero de seria o lote
			ComboAparato comboSelec=new ComboAparato();
			
			//CombosCotizados en la cotizacion actual
			for(CotizacionCombos comboCot: combosCotizados)
			{

				//Verifiacamos cada items de cada combo para ver en que combo esta nuestro aparato
				for(ItemComboApa itemscom : comboCot.getCombo().getItemsCombo())
				{
					
					//De esta forma encotramos el combo que queremos para luego reducir el inventario de cada producto del combo
					if(itemscom.getProducto().getId()==produc.getId())
					{
						comboSelec=comboCot.getCombo();
					}
					
				}
			}
			
			//Luego de haber encontrado el combo, recorremos los productos del combo para reducirlos en inventario
			for(ItemComboApa itemInve:comboSelec.getItemsCombo())
			{
				Inventario inveProd=(Inventario) getEntityManager()
						.createQuery("Select i From Inventario i where i.producto=:productoCom and i.sucursal=:sucursalCom")
						.setParameter("productoCom",itemInve.getProducto())
						.setParameter("sucursalCom", cotizacion.getSucursal())
						.getSingleResult();
				
				if(inveProd.getCantidadActual()>0)
				{
					
					inveProd.setCantidadActual(inveProd.getCantidadActual()-1);
					
					getEntityManager().merge(inveProd); //Necesario limpiar entityManager??
					
				}
				else
				{
					
					FacesMessages.instance().add(Severity.WARN,"Actualmente no hay existencias en inventario del producto "+ itemInve.getProducto().getNombre());
					return false;
					
				}
				
				
			}*/
	
			
			return true;
			
		}
		
		
	}
	
	
	
	
	public boolean rechazoCliente() {
		
		instance.setAprobada(false);
		
		instance.setEstado("REC");// Comentado el 29/06/2017
		
		System.out.println("Tamanio servicios reparacion: "+getServiciosRep().size());
		/*if(servsCobro != null && servsCobro.size() > 0) 
			cobrarServiciosAparte();*/
		
		
		
		//Eliminar servicios actuales
		/*if(serviciosRep.size()>0)
		{*/
			
			/*for(ServicioReparacion ser:getServiciosRep())
			{
				if(ser.getId()!=null)
				{
					getEntityManager().remove(ser);
					serviciosRep.remove(ser);
					
				}
				else
				{
					serviciosRep.remove(ser);
				}
			}*/
		//}
		
		List<ServicioReparacion> serviciosRepActual = new ArrayList<ServicioReparacion>();
		serviciosRepActual=getEntityManager().createQuery("SELECT s FROM ServicioReparacion s where s.reparacion.id=:idRep").setParameter("idRep", instance.getId()).getResultList();
		
		if(serviciosRepActual.size()>0)
		{
			for(ServicioReparacion servi:serviciosRepActual)
			{
				getEntityManager().remove(servi);
			}
		}
		
		serviciosRep.clear();
		
		
		System.out.println("Servicios cobro tTAM "+getServsCobro().size());
		//Agregamos los nuevos servicios para cobro
		if(servsCobro != null && servsCobro.size() > 0)
		{
			for(Service srC : servsCobro) 
			{
				ServicioReparacion nwSrv = new ServicioReparacion();
				nwSrv.setEstado("");
				nwSrv.setReparacion(instance);
				nwSrv.setServicio(srC);
				getEntityManager().persist(nwSrv);
				
				addServicioRep(srC);
				
				System.out.println("Agregando nuevo servicio..... ");
			}
		}	
		
		
		
		boolean res = modify();
		
		for (EtapaRepCliente tmpEta: instance.getEtapasReparacion())
		{
			
			if(tmpEta.getEtapaRep().getId().equals(102))
			{
				
				System.out.println("Entre a la etapa: " + tmpEta.getEtapaRep().getNombre());
				
				tmpEta.setEstado("REC");
				tmpEta.setFechaInicio(new Date());
				tmpEta.setFechaRealFin(new Date());
				getEntityManager().merge(tmpEta);
				
				System.out.println("Size de las Requisiciones "+ tmpEta.getRequisicionesEtapa().size());
				
				for (RequisicionEtapaRep tmpReq: tmpEta.getRequisicionesEtapa())
				{
					System.out.println("Entre a la requisición: "+ tmpReq.getId());
					tmpReq.setEstado("REC");
					//getEntityManager().merge(tmpReq);
					getEntityManager().remove(tmpReq);
					//getEntityManager().flush();
				}
			}
			else if(tmpEta.getEtapaRep().getId().equals(44) || tmpEta.getEtapaRep().getId().equals(42))
			{
				System.out.println("Entro a ");
				tmpEta.setEstado("REC");
				tmpEta.setFechaInicio(new Date());
				tmpEta.setFechaRealFin(new Date());
				getEntityManager().merge(tmpEta);
			}
			
	
			if(tmpEta.getEtapaRep().getId().equals(103))
			{
				tmpEta.setEstado("PEN");
				tmpEta.setFechaInicio(new Date());
				getEntityManager().merge(tmpEta);
			}
			
			System.out.println("ETAPA ++ "+tmpEta.getEtapaRep().getNombre());
			System.out.println("ETAPA ++ "+tmpEta.getEtapaRep().getId());
		}
		
		getEntityManager().flush();
		
		FacesMessages.instance().clear();
		if(res)
			FacesMessages.instance().add(
					sainv_messages.get("repCliHome_scs_rech"));
		else
			FacesMessages.instance().add(
					sainv_messages.get("repCliHome_err_rech"));
		
		if(getServiciosRep()!=null)
		{
			System.out.println("Ultimo servicio agregado "+getServiciosRep().get(0).getServicio().getName());
			
		}
		
		return res;
	}
	
	public void cargarAparatosCli() {
		aparatosCli = getEntityManager().createQuery("select a from AparatoCliente a where a.cliente = :cli AND a.estado = 'ACT' AND a.activo=true ")
				.setParameter("cli", instance.getCliente())
				.getResultList();
		tipoLista = false;
	}
			
	public void seleccionarCliente(Cliente cliente){
		if(instance.getCliente() != null && !instance.getCliente().equals(cliente))
			instance.setAparatoRep(null);
		instance.setCliente(cliente);
	}
		
	public void seleccionarAparato(AparatoCliente aparatoCli){
		instance.setAparatoRep(aparatoCli);
		
		Date fa = new Date();		
		if (instance.getAparatoRep().getFechaGarRep()!=null && instance.getAparatoRep().getFechaGarRep().after(fa)){
			Calendar calMan = new GregorianCalendar();
			calMan.setTime(instance.getAparatoRep().getFechaGarRep());
			calMan.add(Calendar.DATE, -1);
			garRepVigente = tieneGarantiaVigente(calMan.getTime(), instance.getAparatoRep().getPeriodoGarantiaRep());
		}
		else if (instance.getAparatoRep().getFechaGarRep()!= null) {
			garRepVigente = tieneGarantiaVigente(instance.getAparatoRep().getFechaGarRep(), instance.getAparatoRep().getPeriodoGarantiaRep());
		}		
		garVtaVigente = tieneGarantiaVigente(instance.getAparatoRep().getFechaAdquisicion(), instance.getAparatoRep().getPeriodoGarantia());
	}
	
	public void addComponenteDef(ComponenteAparato componenteApa){
		if(selComponentesDef.indexOf(componenteApa) == -1)
			selComponentesDef.add(componenteApa);
	}
	
	public void addCondicionApa(CondicionAparato condicionApa){
		if(selCondicionesApa.indexOf(condicionApa) == -1)
			selCondicionesApa.add(condicionApa);
	}
	
	public void addDefectoCap(DefectoCapsula defectoCap){
		if(selDefectosCap.indexOf(defectoCap) == -1)
			selDefectosCap.add(defectoCap);
	}
	
	public void delComponenteDef(ComponenteDefRep componenteApa){
		
		if(componenteApa.getId()!=null)
		{
			selComponentesDefN.remove(componenteApa);
			getEntityManager().remove(componenteApa);
		}
		else 
		{
			selComponentesDefN.remove(componenteApa);
		}
	}
	
	public void delCondicionApa(CondAparatoRep condicionApa){
		
		if(condicionApa.getId()!=null)
		{
			selCondicionesApaN.remove(condicionApa);
			getEntityManager().remove(condicionApa);
		}
		else
		{
			selCondicionesApaN.remove(condicionApa);
		}
		
	}
	
	public void delDefectoCap(DefCapsulaRep defectoCap){
		
		if(defectoCap.getId()!=null)
		{
			selDefectosCapN.remove(defectoCap);
			getEntityManager().remove(defectoCap);
		}
		else
		{
			selDefectosCapN.remove(defectoCap);
		}
		
	}
	
	public void addServicioCbr(Service srv) {
		boolean addSrv = true;
		for(Service tmpSrvR : getServsCobro()) {
			if(tmpSrvR.equals(srv)) {
				addSrv = false;
				break;
			}
		}
		if(addSrv)
			servsCobro.add(srv);
	}
	
	public void delServicioCbr(Service srvR) {
		getServsCobro().remove(srvR);
	}
	
	public void delServicioRep(ServicioReparacion srvR) {
		getServiciosRep().remove(srvR);
	}
	
	public void addServicioRep(Service srv) {
		boolean addSrv = true;
		for(ServicioReparacion tmpSrvR : getServiciosRep()) {
			if(tmpSrvR.getServicio().equals(srv)) {
				addSrv = false;
				break;
			}
		} 
		if(addSrv) {
			ServicioReparacion srvRep = new ServicioReparacion();
			srvRep.setReparacion(instance);
			srvRep.setServicio(srv);
			getServiciosRep().add(srvRep);
		}
	}
	
	public String getCurrentEtapaRepCli(ReparacionCliente rep){
		for (EtapaRepCliente tmpEta : rep.getEtapasReparacion()){
			if (tmpEta.getFechaInicio() != null && tmpEta.getFechaRealFin() == null)
				return tmpEta.getEtapaRep().getNombre();
		}
		return "N/A";
	}

	@Override
	public boolean preSave() {
		
		FacesMessages.instance().clear();
		if(instance.getProceso()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Seleccione el tipo de trabajo");
			return false;
		}
		
		if(instance.getFechaIngreso()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Especifique la fecha de ingreso");
			return false;
		}
		
		//Validamos que hayan seleccionado el cliente y el aparato
		if(instance.getCliente() == null) {//|| instance.getAparatoRep() == null
			if(instance.getCliente() == null)
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("repCliHome_misscli"));
			/*if(instance.getAparatoRep() == null)
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("repCliHome_missapa"));*/
			return false;
		} else
			instance.setEstado("PEN");
		
		
		
		
		if(instance.getProceso()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Seleccione el tipo de reparacion");
			return false;
		}
		
		if(!instance.getProceso().getId().equals(3) && instance.getAparatoRep() == null)
		{
			FacesMessages.instance().add(Severity.WARN,"Seleccione o ingrese un aparato");
			return false;
		}
		
		if(instance.getDescripcion()==null || instance.getDescripcion().isEmpty())
		{
			FacesMessages.instance().add(Severity.WARN,"Especifique el motivo del trabajo de taller");
			return false;
		}
		
		
		if(selCondicionesApaN.isEmpty() && (instance.getProceso().getNombre().equals("Reparacion") || instance.getProceso().getNombre().equals("Limpieza") || instance.getProceso().getNombre().equals("Ensamblaje de aparato")))
		{
			
			FacesMessages.instance().add(Severity.WARN,"Debe indicar la condición del aparato");
			return false;
		}
		
		
		
		
		if(instance.getAdelanto()==null)
		{
			instance.setAdelanto(0f);
		}
		else
		{
			if(!isNumericFloat(instance.getAdelanto().toString()))
			{
				FacesMessages.instance().add(Severity.WARN,"El valor del adelanto debe ser numerico");
				return false;
			}
			
		}
		
	
		instance.setTipoRep("NML");
		instance.setAprobada(true);
		
		instance.setUsuarioRecibe(loginUser.getUser()!=null?loginUser.getUser():null);
		
		
		System.out.println("PRESAVE Trabajo taller");
		
		return true; 
	}
	
	private void cobrarServiciosAparte(){
		
		List<DetVentaProdServ> detsVtaSrv = new ArrayList<DetVentaProdServ>();
		
		for(Service srC : servsCobro) {
			ServicioReparacion nwSrv = new ServicioReparacion();
			nwSrv.setEstado("CBR");
			nwSrv.setReparacion(instance);
			nwSrv.setServicio(srC);
			getEntityManager().persist(nwSrv);
			
			DetVentaProdServ dtVta = new DetVentaProdServ();
			dtVta.setCantidad(1);
			StringBuilder bld = new StringBuilder();
			bld.append(srC.getName());
			dtVta.setDetalle(bld.toString());
			dtVta.setEscondido(false);
			dtVta.setMonto(srC.getCosto().floatValue());
			dtVta.setCodExacto(srC.getCodigo());
			dtVta.setCodClasifVta("SRV");
			dtVta.setServicio(srC);
			detsVtaSrv.add(dtVta);
		}
		
		//Guardamos el detalle de la venta por los servicios cobrados
		Double totalReparacion = 0d;
		VentaProdServ vta = new VentaProdServ();
		vta.setCliente(instance.getCliente());
		vta.setDetalle(instance.getDescripcion());
		vta.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
		vta.setEstado("PEN");
		vta.setFechaVenta(new Date());
		vta.setIdDetalle(instance.getId());
		vta.setMonto(0.0f);
		vta.setSucursal(instance.getSucursal());
		vta.setCodTipoVenta(instance.getProceso().getPrcCode()+""+instance.getId());
		vta.setTipoVenta("TLL");
		vta.setUsrEfectua(loginUser.getUser());
		getEntityManager().persist(vta);
		
		for(DetVentaProdServ tmpDt : detsVtaSrv) {
			tmpDt.setVenta(vta);
			totalReparacion += tmpDt.getServicio().getCosto();
			getEntityManager().persist(tmpDt);
		}
		
		vta.setMonto(totalReparacion.floatValue());
		getEntityManager().merge(vta);
		
	}
	
	
	@Override
	public void posSave() 
	{
		
		FacesMessages.instance().clear();
		
		saveCaracteristicasAparato();
		
		//Si se seleccionaron servicios para cobrar, se adicionan a los servicios ya cobrados
		if(servsCobro != null && servsCobro.size() > 0) {
			for(Service srC : servsCobro) {
				ServicioReparacion nwSrv = new ServicioReparacion();
				nwSrv.setReparacion(instance);
				nwSrv.setServicio(srC);
				getEntityManager().persist(nwSrv);
			}
		}
			//cobrarServiciosAparte();
		
		//Luego de guardar la reparacion, generamos todas las etapas
		List<EtapaReparacion> lstEtapas = getEntityManager().createQuery("SELECT e FROM EtapaReparacion e " +
					"	WHERE e.procesoTll = :proceso ORDER BY e.orden ASC")
					.setParameter("proceso", instance.getProceso())
					.getResultList();
		
		EtapaRepCliente etRepCli = null;
		
		Calendar fechaEst = new GregorianCalendar();
		fechaEst.setTime(instance.getFechaIngreso());
		boolean isFirst = true;
		
		for(EtapaReparacion etapaRep: lstEtapas){
			etRepCli = new EtapaRepCliente();
			if(isFirst) { //Seteamos la primera 
				isFirst = false;
				etRepCli.setEstado("PEN");
				Calendar cal = new GregorianCalendar();
			//	cal.add(Calendar.DATE, 1);
				etRepCli.setFechaInicio(cal.getTime());
			}
			etRepCli.setEtapaRep(etapaRep);
			etRepCli.setReparacionCli(instance);
			etRepCli.setUsuario(loginUser.getUser());
			//Calculamos la fecha estimada de finalizacion de cada etapa
			fechaEst.add(Calendar.DATE, etapaRep.getTiempoEstimado());
			etRepCli.setFechaEstFin(fechaEst.getTime());
			etRepCli.setReparacionCli(instance);
			getEntityManager().persist(etRepCli);
		    getEntityManager().flush();
		}
		
		
		//Si existe un adelanto hay que generar la venta
		if(instance.getAdelanto()>0)
		{
			
			VentaProdServ vta = new VentaProdServ();
			
			vta.setCliente(instance.getCliente());
			vta.setDetalle("Adelanto de servicio de "+instance.getProceso().getNombre());
			vta.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
			vta.setEstado("PEN");
			vta.setFechaVenta(new Date());
			vta.setIdDetalle(instance.getId());
			vta.setMonto(instance.getAdelanto());
			vta.setSucursal(instance.getSucursal());
			vta.setCodTipoVenta(instance.getProceso().getPrcCode()+""+instance.getId());
			vta.setTipoVenta("TLL");
			vta.setUsrEfectua(loginUser.getUser());
			getEntityManager().persist(vta);
			
			
			
			DetVentaProdServ det = new DetVentaProdServ();
			
			det.setCantidad(1);
			det.setMonto(instance.getAdelanto());
			det.setDetalle("Adelanto servicio de "+instance.getProceso().getNombre());
			det.setCodClasifVta("SRV");
			det.setCodExacto("RPR"+instance.getId());
			det.setEscondido(false);
			
			det.setVenta(vta);
			
			
			getEntityManager().persist(det);
			
			
			
			FacesMessages.instance().add(Severity.INFO,"Cobro de adelanto registrado");
			
			
		}
		
		
		
		FacesMessages.instance().add(Severity.INFO,"Orden de laboratorio registrada");
	}
	
	private void saveCaracteristicasAparato() {
		
		
		//Comentado el 07/09/2017
		/*if(instance.getId() != null && instance.getId() > 0) {
			for(ComponenteDefRep tmpDf : instance.getCompsDefAparato()) 
				getEntityManager().remove(tmpDf);
			for(CondAparatoRep tmpCn : instance.getCondsAparatorep()) 
				getEntityManager().remove(tmpCn);
			for(DefCapsulaRep tmpDf : instance.getDefCapsAparato()) 
				getEntityManager().remove(tmpDf);
		}*/
		
		
		//Guardamos todos los componentes defectuosos, defectos de capsula y condiciones del aparato
		for(ComponenteDefRep cmpApa : selComponentesDefN) {
			
			if(cmpApa.getId()==null)//nueva condicion el 07/09/2017
			{
				/*ComponenteDefRep tmpCmp = new ComponenteDefRep();
				tmpCmp.setRepCliente(instance);
				tmpCmp.setCmpAparato(cmpApa);*/
				getEntityManager().persist(cmpApa);
			}
		}
				
		for(CondAparatoRep cndApa : selCondicionesApaN) {
			
			if(cndApa.getId()==null)//nueva condicion el 07/09/2017
			{
				/*System.out.println("GUARDO LA CONDICION");
				CondAparatoRep tmpCnd = new CondAparatoRep();
				tmpCnd.setRepCliente(instance);
				tmpCnd.setCondAparato(cndApa);*/
				getEntityManager().persist(cndApa);
			}
		}
				
		for(DefCapsulaRep defCap : selDefectosCapN) {
			
			if(defCap.getId()==null)//nueva condicion el 07/09/2017
			{
				/*DefCapsulaRep tmpDfc = new DefCapsulaRep();
				tmpDfc.setRepCliente(instance);
				tmpDfc.setDefCapsula(defCap);*/
				getEntityManager().persist(defCap);
				
			}
		}
		
		//getEntityManager().flush();
	}
	
	public void addComponenteDefN(ComponenteAparato componenteApa){
		//if(selComponentesDefN.indexOf(componenteApa) == -1)
		
		ComponenteDefRep tmpCmp = new ComponenteDefRep();
		tmpCmp.setRepCliente(instance);
		tmpCmp.setCmpAparato(componenteApa);
		//getEntityManager().persist(tmpCmp);
		
		if(selComponentesDefN.contains(tmpCmp))
		{
			
		}
		else
			selComponentesDefN.add(tmpCmp);
	}
	
	public void addCondicionApaN(CondicionAparato condicionApa){
		//if(selCondicionesApaN.indexOf(condicionApa) == -1)
		
		CondAparatoRep tmpCnd = new CondAparatoRep();
		tmpCnd.setRepCliente(instance);
		tmpCnd.setCondAparato(condicionApa);
		//getEntityManager().persist(tmpCnd);
		
		if(selCondicionesApaN.contains(tmpCnd))
		{
			
		}
		else
			selCondicionesApaN.add(tmpCnd);
	}
	
	public void addDefectoCapN(DefectoCapsula defectoCap){
		//if(selDefectosCapN.indexOf(defectoCap) == -1)
			
			DefCapsulaRep tmpDfc = new DefCapsulaRep();
			tmpDfc.setRepCliente(instance);
			tmpDfc.setDefCapsula(defectoCap);
			//getEntityManager().persist(tmpDfc);
			if(selDefectosCapN.contains(tmpDfc))
			{
				
			}
			else
				selDefectosCapN.add(tmpDfc);
	}

	@Override
	public boolean preModify() {
		return true;
	}

	@Override
	public boolean preDelete() {
		return true;
	}

	@Override
	public void posModify() {
		saveCaracteristicasAparato();
		
		//Si el producto esta siendo entregado, hacemos el cobro
		if(instance.getEstado() != null && instance.getEstado().equals("DLV")) 
		{
			//Cargamos etarepclihome con la instancia etarep_id
			List<EtapaRepCliente> etas = getEntityManager()
					.createQuery("SELECT e FROM EtapaRepCliente e " +
							"	WHERE e.reparacionCli = :rep ORDER BY e.etapaRep.orden DESC ")
					.setParameter("rep", instance)
					.getResultList();
			if(etas != null && etas.size() > 0) 
			{
				etapaRepCliHome.setInstance(etas.get(0)); System.out.println("****Instancia ReparacionCliente "+etas.get(0));
				// Guardamos el detalle de la venta para que aparezca en la
				// pantalla de cobros (incluir requi y costos)
				Double totalReparacion = 0d;
				VentaProdServ vta = new VentaProdServ();
				
				//nuevo el 17/07/2017
				//Verificar si no existe una venta existente
				List<VentaProdServ> vtaExis = getEntityManager().createQuery("SELECT v FROM VentaProdServ v where v.cliente.id=:idCliente and DATE(v.fechaVenta)=:fechaHoy and v.tipoVenta<>'CMB' and v.estado='PEN'").setParameter("idCliente", instance.getCliente().getId()).setParameter("fechaHoy", new Date()).getResultList();
				
				if(vtaExis.size()>0)
				{
					vta = vtaExis.get(0);
				}
				else // si no existe se crea.
				{
				
					vta.setCliente(etapaRepCliHome.getInstance().getReparacionCli().getCliente());
					vta.setDetalle(etapaRepCliHome.getInstance().getReparacionCli().getDescripcion());
					vta.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
					vta.setEstado("PEN");
					vta.setFechaVenta(new Date());
					vta.setIdDetalle(etapaRepCliHome.getInstance().getReparacionCli().getId());
					vta.setMonto(0.0f);
					vta.setSucursal(instance.getSucursal());
					//vta.setSucursal(loginUser.getUser().getSucursal()); // Cambiar para que al guardar reparacion guarde la sucursal
					vta.setCodTipoVenta(instance.getProceso().getPrcCode()+""+instance.getId());
					vta.setTipoVenta("TLL");
					vta.setUsrEfectua(loginUser.getUser());
					getEntityManager().persist(vta);
				}
				
				
				// Requisiciones
				/*List<ItemRequisicionEta> itemsRequis = getEntityManager()
						.createQuery(
								"SELECT i FROM ItemRequisicionEta i WHERE i.reqEtapa.etapaRepCli.reparacionCli = :rep")
						.setParameter("rep", etapaRepCliHome.getInstance().getReparacionCli())
						.getResultList();*/
				
				Float totalReparacionSinDescuentos=0f; // nuevo agregado el 30/06/2017
				
				/*if (itemsRequis != null && itemsRequis.size() > 0)
					for (ItemRequisicionEta tmpItr : itemsRequis) 
					{*/
				Float cantidadDescuento=0f; // nuevo el 20/07/2017
				Float descuentoPorGarantia=0f;
				
				if (requisicionesCobro != null && requisicionesCobro.size() > 0)// nuevo el 20/07/2017
					for (ItemRequisicionEta tmpItr : requisicionesCobro) 
					{
						DetVentaProdServ dtVta = new DetVentaProdServ();
						dtVta.setCantidad(tmpItr.getCantidad());
						StringBuilder bld = new StringBuilder();
						
						//Aqui debemos ir a buscar los numeros de serie utilizados
						dtVta.setNumLote(tmpItr.getNumLote());
						dtVta.setNumSerie(tmpItr.getNumSerie());
						
						bld.append(tmpItr.getProducto().getNombre());
						bld.append(", Modelo "
								+ tmpItr.getProducto().getModelo());
						bld.append(", Marca "
								+ tmpItr.getProducto().getMarca().getNombre());
						dtVta.setDetalle(bld.toString());
						
						if((tmpItr.getGenerado()!=null && tmpItr.getGenerado().equals("GEN")) || !tmpItr.isCobrar())
						{
							dtVta.setMonto(0f);
							//dtVta.setDescuentoCorp(tmpItr.getProducto().getPrcNormal()*tmpItr.getCantidad());
							cantidadDescuento+=(tmpItr.getProducto().getPrcNormal()*tmpItr.getCantidad());
						}
						else
						{
							dtVta.setMonto(tmpItr.getProducto().getPrcNormal()*tmpItr.getCantidad());
							//totalRepa+=tmpItr.getProducto().getPrcNormal()*tmpItr.getCantidad();
						}
						
						if(!tmpItr.isCobrar())
						{
							descuentoPorGarantia+=tmpItr.getProducto().getPrcNormal()*tmpItr.getCantidad();
						}
						
						dtVta.setTipoVenta("TLL");// nuevo 17/07/2017
						dtVta.setVenta(vta);
						dtVta.setCodExacto(tmpItr.getProducto().getReferencia());
						dtVta.setCodClasifVta("PRD");
						dtVta.setCosto(tmpItr.getProducto().getCosto());
						
						totalReparacion += dtVta.getMonto()
								* dtVta.getCantidad();
						
						totalReparacionSinDescuentos += (tmpItr.getProducto().getPrcNormal()*tmpItr.getCantidad());
						
						
						getEntityManager().persist(dtVta);
					}
	
				for (ServicioReparacion srv : getServiciosRep()) {
					
					if(srv.getEstado() == null || !srv.getEstado().equals("CBR")) {
						//totalReparacion += srv.getServicio().getCosto();
						DetVentaProdServ dtVta = new DetVentaProdServ();
						dtVta.setCantidad(1);
						StringBuilder bld = new StringBuilder();
						bld.append(srv.getServicio().getName());
						dtVta.setDetalle(bld.toString());
						dtVta.setEscondido(true);
						
						if((srv.getGenerado()!=null && srv.getGenerado().equals("GEN")) || !srv.isCobrar())
						{
							dtVta.setMonto(0f);
							cantidadDescuento+=(srv.getServicio().getCosto().floatValue());
						}
						else
						{
							dtVta.setMonto(srv.getServicio().getCosto().floatValue());
							//totalRepa+=srv.getServicio().getCosto().floatValue();
						}
						
						if(!srv.isCobrar())
						{
							descuentoPorGarantia+=(srv.getServicio().getCosto().floatValue());
						}
						
						totalReparacion+=dtVta.getMonto();
						
						totalReparacionSinDescuentos+=srv.getServicio().getCosto().floatValue();
						
						dtVta.setTipoVenta("TLL"); // nuevo el 17/07/2017
						dtVta.setVenta(vta);
						dtVta.setCodExacto(srv.getServicio().getCodigo());
						dtVta.setCodClasifVta("SRV");
						dtVta.setServicio(srv.getServicio());
						getEntityManager().persist(dtVta);
					}
				}
				
				//instance.setCosto(totalRepa);
				instance.setCosto(totalReparacion.floatValue());
				
				//nuevo el 20/07/2017
				instance.setIngresosTaller(totalReparacionSinDescuentos);
				instance.setDescuentos_garantia(descuentoPorGarantia);
				instance.setDescuentos(cantidadDescuento);
				
				//vta.setMonto(totalRepa);
	
				// Actualizamos el monto de la venta
				getEntityManager().refresh(vta);
				
				if(instance.getAdelanto()!=null)
				{	
					vta.setMonto(vta.getMonto()+super.moneyDecimal(totalReparacion-instance.getAdelanto()).floatValue()); //Restar el adelanto nuevo agregado el 15/06/2017
					//vta.setMonto(super.moneyDecimal(totalRepa-instance.getAdelanto()).floatValue()); //Restar el adelanto nuevo agregado el 15/06/2017
				}
				else
				{
					vta.setMonto(vta.getMonto()+super.moneyDecimal(totalReparacion).floatValue()); //Restar el adelanto nuevo agregado el 15/06/2017
					//vta.setMonto(super.moneyDecimal(totalRepa).floatValue()); //Restar el adelanto nuevo agregado el 15/06/2017
				}
				
				etapaRepCliHome.getInstance().getReparacionCli().setCosto(vta.getMonto());
				getEntityManager().merge(vta);
				
				
				//Si existe un aparato se ejecuta el siguiente bloque
				if(instance.getAparatoRep()!=null)
				{
						//------En este bloque considerar si la reparacion no debe de aplicarse ningun descuento ya sea que tengo o no
						
						// Verificamos, si hay una garantia de venta o reparacion vigente,
						// se aprueba la venta con descuento del 100 por ciento si no han chequeado lo de cobrar siempre
					
						//Comentado el 21/07/2017
						/*if (!cobrarSiempre && (tieneGarantiaVigente(
								getInstance().getAparatoRep().getFechaGarRep(), 
								getInstance().getAparatoRep().getPeriodoGarantiaRep()) ||
								tieneGarantiaVigente(
										getInstance().getAparatoRep().getFechaAdquisicion(), 
										getInstance().getAparatoRep().getPeriodoGarantia()))) 
						{
							ventaProdServHome.select(vta);
							ventaProdServHome.setLlevaDescuento(true);
							//ventaProdServHome.getInstance().setCantidadDescuento(new Long(Math.round(vta.getMonto()*100))/100.0);
							ventaProdServHome.getInstance().setCantidadDescuento(new Long(Math.round(totalReparacion*100))/100.0);
							ventaProdServHome.getInstance().setTipoDescuento("M");
							ventaProdServHome.getInstance().setDetalle("Cobro con descuento del 100% por cobertura de garantia");
							ventaProdServHome.getInstance().setEstado("PDS");
							ventaProdServHome.getInstance().setUsrDescuento(etapaRepCliHome.getInstance().getUsuario());
							ventaProdServHome.aprobarVta();
						}*/
						
						//---------------------------------------------------------------
						
						
						// Adicionamos una garantia de reparacion si es que no tiene o
						// tiene una no vigente
						System.out.println("tiene garantia negado: "+!tieneGarantiaVigente(instance.getAparatoRep().getFechaGarRep(), instance.getAparatoRep().getPeriodoGarantiaRep()));
						if (!tieneGarantiaVigente(instance.getAparatoRep().getFechaGarRep(), instance.getAparatoRep().getPeriodoGarantiaRep())) 
						{
							Calendar calMan = new GregorianCalendar();
							calMan.add(Calendar.DATE, 1);
							instance.getAparatoRep()
									.setFechaGarRep(calMan.getTime());
							instance.getAparatoRep()
									.setPeriodoGarantiaRep(90);
							getEntityManager()
									.merge(instance.getAparatoRep());
						}
				}
			}
		}
		getEntityManager().flush();
	}
	
	public void cargarRequisicionesCobro()
	{
		requisicionesCobro = getEntityManager()
				.createQuery(
						"SELECT i FROM ItemRequisicionEta i WHERE i.reqEtapa.etapaRepCli.reparacionCli = :rep")
				.setParameter("rep", etapaRepCliHome.getInstance().getReparacionCli())
				.getResultList();
	}
	
	public void verificarGarantia()
	{
		if(garRepVigente || garVtaVigente)
		{
			for(ItemRequisicionEta itm:requisicionesCobro)
			{
				itm.setCobrar(false);
			}
			
			for(ServicioReparacion srv: getServiciosRep())
			{
				srv.setCobrar(false);
			}
		}
		else
		{
			for(ItemRequisicionEta itm:requisicionesCobro)
			{
				itm.setCobrar(true);
			}
			
			for(ServicioReparacion srv: getServiciosRep())
			{
				srv.setCobrar(true);
			}
		}
	}
	
	public void recalcularTotalesCobro()
	{
		totalAcobrar=0f;
		descuentoCobro=0f;
		
		for(ItemRequisicionEta item: requisicionesCobro)
		{
			if(item.isCobrar() && (item.getGenerado()==null || !item.getGenerado().equals("GEN")))
			{
				totalAcobrar+=item.getProducto().getPrcNormal()*item.getCantidad();
			}
			else if(!item.isCobrar() && (item.getGenerado()==null || !item.getGenerado().equals("GEN")))
			{
				descuentoCobro+=item.getProducto().getPrcNormal()*item.getCantidad();
			}
		}
		
		
		for(ServicioReparacion srv: getServiciosRep())
		{
			if(srv.isCobrar() && (srv.getGenerado()==null || !srv.getGenerado().equals("GEN")))
			{
				totalAcobrar+=srv.getServicio().getCosto().floatValue();
			}
			else if(!srv.isCobrar() && (srv.getGenerado()==null || !srv.getGenerado().equals("GEN")))
			{
				descuentoCobro+=srv.getServicio().getCosto().floatValue();
			}
		}
		
	}
	
	
	public void recalcularTotalVenta()
	{
		
		Float totalNuevo=0f;
		
		for (ServicioReparacion srv : getServiciosRep()) {
			
			if(srv.getEstado() == null || !srv.getEstado().equals("CBR")) {
				
				if(srv.getGenerado()!=null && srv.getGenerado().equals("GEN"))							
				{
					
					totalNuevo+=srv.getServicio().getCosto().floatValue();
					
				}
				
			}
		}
	}

	@Override
	public void posDelete() {
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<AparatoCliente> getAparatosCli() {
		return aparatosCli;
	}

	public void setAparatosCli(List<AparatoCliente> aparatosCli) {
		this.aparatosCli = aparatosCli;
	}

	public List<ProcesoTaller> getProcesosTaller() {
		return procesosTaller;
	}

	public void setProcesosTaller(List<ProcesoTaller> procesosTaller) {
		this.procesosTaller = procesosTaller;
	}

	public boolean isTipoLista() {
		return tipoLista;
	}

	public void setTipoLista(boolean tipoLista) {
		this.tipoLista = tipoLista;
	}

	public boolean isCostoEditable() {
		return costoEditable;
	}

	public void setCostoEditable(boolean costoEditable) {
		this.costoEditable = costoEditable;
	}
	
	public Integer getRepCliId() {
		return repCliId;
	}

	public void setRepCliId(Integer repCliId) {
		this.repCliId = repCliId;
	}

	public List<CondicionAparato> getSelCondicionesApa() {
		return selCondicionesApa;
	}

	public void setSelCondicionesApa(List<CondicionAparato> selCondicionesApa) {
		this.selCondicionesApa = selCondicionesApa;
	}

	public List<ComponenteAparato> getSelComponentesDef() {
		return selComponentesDef;
	}

	public void setSelComponentesDef(List<ComponenteAparato> selComponentesDef) {
		this.selComponentesDef = selComponentesDef;
	}

	public List<DefectoCapsula> getSelDefectosCap() {
		return selDefectosCap;
	}

	public void setSelDefectosCap(List<DefectoCapsula> selDefectosCap) {
		this.selDefectosCap = selDefectosCap;
	}

	public List<ServicioReparacion> getServiciosRep() {
		return serviciosRep;
	}

	public void setServiciosRep(List<ServicioReparacion> serviciosRep) {
		this.serviciosRep = serviciosRep;
	}

	public Double getPrecioEstimado() {
		return precioEstimado;
	}

	public void setPrecioEstimado(Double precioEstimado) {
		this.precioEstimado = precioEstimado;
	}

	public Double getTotalServs() {
		return totalServs;
	}

	public void setTotalServs(Double totalServs) {
		this.totalServs = totalServs;
	}

	public Double getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Double totalItems) {
		this.totalItems = totalItems;
	}

	public List<EtapaReparacion> getEtapasTaller() {
		return etapasTaller;
	}

	public void setEtapasTaller(List<EtapaReparacion> etapasTaller) {
		this.etapasTaller = etapasTaller;
	}

	public boolean isGarVtaVigente() {
		return garVtaVigente;
	}

	public void setGarVtaVigente(boolean garVtaVigente) {
		this.garVtaVigente = garVtaVigente;
	}

	public boolean isGarRepVigente() {
		return garRepVigente;
	}

	public void setGarRepVigente(boolean garRepVigente) {
		this.garRepVigente = garRepVigente;
	}

	public String getFlagPrsEntrega() {
		return flagPrsEntrega;
	}

	public void setFlagPrsEntrega(String flagPrsEntrega) {
		this.flagPrsEntrega = flagPrsEntrega;
	}

	public List<Service> getServsCobro() {
		return servsCobro;
	}

	public void setServsCobro(List<Service> servsCobro) {
		this.servsCobro = servsCobro;
	}

	public boolean isCobrarSiempre() {
		return cobrarSiempre;
	}

	public void setCobrarSiempre(boolean cobrarSiempre) {
		this.cobrarSiempre = cobrarSiempre;
	}

	public List<ReparacionCliente> getResultList() {
		return resultList;
	}

	public void setResultList(List<ReparacionCliente> resultList) {
		this.resultList = resultList;
	}

	

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getTaller() {
		return taller;
	}

	public void setTaller(String taller) {
		this.taller = taller;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Date getFecDeHoy() {
		this.fecDeHoy = new Date();
		return fecDeHoy;
	}

	public void setFecDeHoy(Date fecDeHoy) {
		this.fecDeHoy = fecDeHoy;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public String getRepCliId2() {
		return repCliId2;
	}

	public void setRepCliId2(String repCliId2) {
		this.repCliId2 = repCliId2;
	}

	public List<ItemRequisicionEta> getRequisicionesCobro() {
		return requisicionesCobro;
	}

	public void setRequisicionesCobro(List<ItemRequisicionEta> requisicionesCobro) {
		this.requisicionesCobro = requisicionesCobro;
	}

	public Float getTotalAcobrar() {
		return totalAcobrar;
	}

	public void setTotalAcobrar(Float totalAcobrar) {
		this.totalAcobrar = totalAcobrar;
	}

	public Float getDescuentoCobro() {
		return descuentoCobro;
	}

	public void setDescuentoCobro(Float descuentoCobro) {
		this.descuentoCobro = descuentoCobro;
	}

	public List<CondAparatoRep> getSelCondicionesApaN() {
		return selCondicionesApaN;
	}

	public void setSelCondicionesApaN(List<CondAparatoRep> selCondicionesApaN) {
		this.selCondicionesApaN = selCondicionesApaN;
	}

	public List<ComponenteDefRep> getSelComponentesDefN() {
		return selComponentesDefN;
	}

	public void setSelComponentesDefN(List<ComponenteDefRep> selComponentesDefN) {
		this.selComponentesDefN = selComponentesDefN;
	}

	public List<DefCapsulaRep> getSelDefectosCapN() {
		return selDefectosCapN;
	}

	public void setSelDefectosCapN(List<DefCapsulaRep> selDefectosCapN) {
		this.selDefectosCapN = selDefectosCapN;
	}

	
	
	
	

}
