package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.inventory.MovimientoHome;
import com.sa.kubekit.action.inventory.TransferenciaHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.security.SucursalActivaList;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.Transferencia;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.AparatoCliente;
import com.sa.model.workshop.ItemRequisicionEta;
import com.sa.model.workshop.RequisicionEtapaRep;

@Name("requisicionEtaHome")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings("unchecked")
public class RequisicionEtaHome extends KubeDAO<RequisicionEtapaRep> {
	
	private static final long serialVersionUID = 1L;
	
	@In
	private LoginUser loginUser;
	
	@In (create= true, required=false)
	private MovimientoHome movimientoHome;
	
	@In(required=false,create=true)
	@Out(required=false)
	private EtapaRepCliHome etapaRepCliHome;
	
	
	@In (create= true, required=false)
	private TransferenciaHome transferenciaHome;
		
	private List<ItemRequisicionEta> itemsAgregados = new ArrayList<ItemRequisicionEta>();
	private ItemRequisicionEta selectedItem = new ItemRequisicionEta();
	private Integer codigosRestantes;
	private List<CodProducto> currCodigos = new ArrayList<CodProducto>();
	private Map<String, ArrayList<CodProducto>> lstCodsProductos = new HashMap<String, ArrayList<CodProducto>>();
	private List<Object[]> resultList = new ArrayList<Object[]>();
	private List<Sucursal> sucursalesSoli = new ArrayList<Sucursal>();
	private List<Producto> productosSeleccionar = new ArrayList<Producto>();
	private Sucursal sucursalActUs;
	private AparatoCliente aparatoRequision;
	
	private Integer reqId;
	
	private boolean abilitarEdicion;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("requisicionEtaHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("requisicionEtaHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("requisicionEtaHome_deleted")));
	}
	
	public void genResultList() {
		
		Sucursal sucursalUsuario = loginUser.getUser().getSucursal();
		
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("otraSuc", loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior())
				.getResultList(); 
		
		if(subSucFlt == null || subSucFlt.size() <= 0) 
			subSucFlt = new ArrayList<Sucursal>();
		
		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior());
		
		//Iteramos sobre las sucursales para formar el filtro
		StringBuilder fltSuc = new StringBuilder();
		
		for(Sucursal tmpSuc : subSucFlt) {
			fltSuc.append(" rqe.sucreq_id = ");
			fltSuc.append(tmpSuc.getId()).append(" OR ");
		}
		
		
		/*for(Sucursal tmpSuc : subSucFlt) {
			fltSuc.append(" req.sucursalSol.id = ");
			fltSuc.append(tmpSuc.getId()).append(" OR ");
		}*/
		
		fltSuc.append(" 1 = 2 ");
		
		
		
		
		
		/*resultList = getEntityManager().createNativeQuery("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, " +
				"	suc.nombre nomSucursal, rqe.fecha_ingreso fechaIngReq, " +
				"	etc.fecha_est_fin fechaEstFin, rqe.reqeta_id id, " +
				"	prt.codigo codProceso, rpc.repcli_id idRep, rqe.estado estado " +
				" FROM  cliente cli, " +
				" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, " +
				" etapa_rep_cliente etc, requisicion_etapa_rep rqe, sucursal suc " +
				" WHERE rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id " +
						" and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id " +
						" and rqe.etarepcli_id = etc.etarepcli_id and rqe.sucreq_id = suc.id " +
						"  and ( " + fltSuc + " ) and rqe.estado = 'PEN' " +
								"ORDER BY rqe.fecha_ingreso DESC ")
					.getResultList();*/
		
		//Actual resp
			/*	resultList = getEntityManager().createNativeQuery("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, " +
						"	apc.nombre nomProducto, suc.nombre nomSucursal, rqe.fecha_ingreso fechaIngReq, " +
						"	etc.fecha_est_fin fechaEstFin, rqe.reqeta_id id, " +
						"	prt.codigo codProceso, rpc.repcli_id idRep, rqe.estado estado " +
						" FROM aparato_cliente apc, cliente cli, " +
						" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, " +
						" etapa_rep_cliente etc, requisicion_etapa_rep rqe, sucursal suc " +
						" WHERE apc.cliente_id = cli.cliente_id " +
								" and rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id " +
								" and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id " +
								" and rqe.etarepcli_id = etc.etarepcli_id and rqe.sucreq_id = suc.id " +
								"  and ( " + fltSuc + " ) and rqe.estado = 'PEN' " +
										"ORDER BY rqe.fecha_ingreso DESC ")
							.getResultList();
		*/
		
		//Actual comentado el 06/10/2017
		/*resultList = getEntityManager().createNativeQuery("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, " +
				"	rqe.descripcion, suc.nombre nomSucursal, rqe.fecha_ingreso fechaIngReq, " +
				"	etc.fecha_est_fin fechaEstFin, rqe.reqeta_id id, " +
				"	prt.codigo codProceso, rpc.repcli_id idRep, rqe.estado estado " +
				" FROM  cliente cli, " +
				" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, " +
				" etapa_rep_cliente etc, requisicion_etapa_rep rqe, sucursal suc " +
				" WHERE rpc.cli_id = cli.cliente_id " +
						" and rpc.repcli_id = etc.repcli_id " +
						" and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id " +
						" and rqe.etarepcli_id = etc.etarepcli_id and rqe.sucreq_id = suc.id " +
						"  and ( " + fltSuc + " ) and rqe.estado = 'PEN' " +
								"ORDER BY rqe.fecha_ingreso DESC ")
					.getResultList();*/
		
		resultList = getEntityManager().createNativeQuery("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, " +
				"	rqe.descripcion, suc.nombre nomSucursal, rqe.fecha_ingreso fechaIngReq, " +
				"	etc.fecha_est_fin fechaEstFin, rqe.reqeta_id id, " +
				"	prt.codigo codProceso, rpc.repcli_id idRep, rqe.estado estado " +
				" FROM  cliente cli, " +
				" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, " +
				" etapa_rep_cliente etc, requisicion_etapa_rep rqe, sucursal suc " +
				" WHERE rpc.cli_id = cli.cliente_id " +
						" and rpc.repcli_id = etc.repcli_id " +
						" and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id " +
						" and rqe.etarepcli_id = etc.etarepcli_id and rqe.sucreq_id = suc.id " +
						"  and rqe.sucreq_id = "+sucursalUsuario.getId()+" and rqe.estado = 'PEN' " +
								"ORDER BY rqe.fecha_ingreso DESC ")
					.getResultList();
		
		
		
		//Se van a filtrar 
		/*if(instance.getSucursalSol().getSucursalSuperior()!=null)
		{*/
			/*String jpqlQuery="SELECT req.etapaRepCli.etapaRep.procesoTll.nombre,req.id,req.etapaRepCli.reparacionCli.sucursal.nombre," +
					"req.fechaIngreso,req.etapaRepCli.fechaEstFin,req.id,req.etapaRepCli.etapaRep.procesoTll.prcCode,req.etapaRepCli.reparacionCli.id,req.estado FROM RequisicionEtapaRep req where  ( " + fltSuc + " ) and req.estado='PEN' order by req.fechaIngreso ";*/
		//}
			
		//resultList = getEntityManager().createQuery(jpqlQuery).getResultList();	
			
		
		
	}
	
	public void limpiarProductos() {
		itemsAgregados.clear();
	}
	
	public void load(){
		try{
			System.out.println("Entré al load de RequisicionEtaHome");
			//Cargamos la lista de sucursales
			if(reqId == 0)
				throw new Exception();
			itemsAgregados = new ArrayList<ItemRequisicionEta>();
			//sucursalesSoli = getEntityManager().createQuery("SELECT s FROM Sucursal s").getResultList();
			sucursalesSoli = getEntityManager().createQuery("SELECT s FROM Sucursal s where s.bodega=TRUE").getResultList();
			setInstance(getEntityManager().find(RequisicionEtapaRep.class, reqId));
			//Cargamos el listado de productos
			loadItemsReq(instance);
			
			for(Sucursal sucTmp : sucursalesSoli)
			{
				if(sucTmp.getNombre().equals(loginUser.getUser().getSucursal().getNombre()))
					instance.setSucursalSol(loginUser.getUser().getSucursal());
				else if(sucTmp.getNombre().equals(loginUser.getUser().getSucursal().getSucursalSuperior().getNombre()))
					instance.setSucursalSol(loginUser.getUser().getSucursal().getSucursalSuperior());
			}
			
			
			//AparatoCliente aparatoCliente;
			
			if(instance.getEtapaRepCli().getReparacionCli().getAparatoRep()!=null)
			{
				aparatoRequision=instance.getEtapaRepCli().getReparacionCli().getAparatoRep();
			}
			
		}catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("Entré al catch porque no pude cargar la requisición");
			clearInstance();
			itemsAgregados = new ArrayList<ItemRequisicionEta>();
			setInstance(new RequisicionEtapaRep());
			instance.setEtapaRepCli(etapaRepCliHome.getInstance());
			instance.setFechaIngreso(new Date());
			instance.setEstado("COT");
			//sucursalesSoli = getEntityManager().createQuery("SELECT s FROM Sucursal s").getResultList();
			sucursalesSoli = (List<Sucursal>)getEntityManager().createQuery("SELECT s FROM Sucursal s where s.bodega=TRUE").getResultList();
			
			
			for(Sucursal sucTmp : sucursalesSoli)
			{
				if(sucTmp.getNombre().equals(loginUser.getUser().getSucursal().getNombre()))
					instance.setSucursalSol(loginUser.getUser().getSucursal());
				else if(sucTmp.getNombre().equals(loginUser.getUser().getSucursal().getSucursalSuperior().getNombre()))
					instance.setSucursalSol(loginUser.getUser().getSucursal().getSucursalSuperior());
			}
			
			/*if(sucursalesSoli.contains(loginUser.getUser().getSucursal()))
			{
				sucursalActUs=loginUser.getUser().getSucursal();
				System.out.println("Entro 1");
			}
			else if(sucursalesSoli.contains(loginUser.getUser().getSucursal().getSucursalSuperior()))
			{
				sucursalActUs=loginUser.getUser().getSucursal().getSucursalSuperior();
				System.out.println("Entro 2");
			}*/
			
			System.out.println("***nombre sucursal actual "+loginUser.getUser().getSucursal().getNombre());
			System.out.println("***nombre sucursal instance "+instance.getSucursalSol().getNombre());
			
		}
	}
	
	
	public void cargarRequisicion(RequisicionEtapaRep requi)
	{
		try {
			
			
			setInstance(requi);
			itemsAgregados= getEntityManager().createQuery("SELECT i FROM ItemRequisicionEta i where i.reqEtapa.id=:idReq").setParameter("idReq", instance.getId()).getResultList();
			sucursalesSoli = (List<Sucursal>)getEntityManager().createQuery("SELECT s FROM Sucursal s where s.bodega=TRUE").getResultList();
			
			
			List<Transferencia> lsTransferencia = new ArrayList<Transferencia>();
			lsTransferencia = getEntityManager().createQuery("SELECT t FROM Transferencia t where t.requisicion.id=:idRequi and t.estado!='D'").setParameter("idRequi", requi.getId()).getResultList();
			
			if(lsTransferencia!=null && lsTransferencia.size()>0)
			{
				abilitarEdicion = false;
			}
			else
			{
				abilitarEdicion = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			FacesMessages.instance().add(Severity.WARN,"No se pudo cargar la requisicion");
		}
	}
	
	public void editarRequisicion()
	{
		
		if(itemsAgregados.size()<=0)
		{
			FacesMessages.instance().add(Severity.WARN,"Seleccionar un item");
			return;
		}
		
		if(modify())
		{
			for(ItemRequisicionEta item: itemsAgregados)
			{
				if(item.getId()==null)
				{
					item.setReqEtapa(instance);
					getEntityManager().persist(item);
				}
			}
			
			//Nuevo agregado el 03/10/2017
			if(!instance.getSucursalSol().getId().equals(loginUser.getUser().getSucursal().getId()))
			{
				generarTransferencia();
			}
			
			FacesMessages.instance().add(Severity.INFO,"Se actualizo la requisicion");
		}
		else
		{
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al editar la requisicion");
		}
	}
	
	public void quitarItemEditar(ItemRequisicionEta item)
	{
		if(item.getId()!=null)
		{
			itemsAgregados.remove(item);
			getEntityManager().remove(item);
		}
		else
		{
			itemsAgregados.remove(item);
		}
		
		getEntityManager().flush();
	}
	
	public void loadItemsReq(RequisicionEtapaRep req) {
		itemsAgregados = getEntityManager().createQuery("SELECT ir FROM ItemRequisicionEta ir WHERE ir.reqEtapa = :req")
				.setParameter("req", req)
				.getResultList();
	}
	
	public void agregarProducto(Producto prd) {
		ItemRequisicionEta newItem = new ItemRequisicionEta();
		newItem.setProducto(prd);
		newItem.setCantidad(1);
		itemsAgregados.add(newItem);
	}
	
	//agregado el 02/10/2017
	public void agregarProducto(Inventario prd) {
		
		if(prd.getCantidadActual()<=0)
		{
			FacesMessages.instance().add(Severity.WARN,"No hay existencia de este producto");
			return;
		}
		
		ItemRequisicionEta newItem = new ItemRequisicionEta();
		newItem.setProducto(prd.getProducto());
		newItem.setCantidad(1);
		newItem.setInventario(prd);
		itemsAgregados.add(newItem);
	}
	
	public void removerItem(ItemRequisicionEta ir) {
		itemsAgregados.remove(ir);
	}
	
	//Para limitar los items que seleccionan
	public void limitSelectedCodes(CodProducto chkCod) {
		if(chkCod.isTransferido()) {
			ArrayList<CodProducto> codsProds = lstCodsProductos.get(selectedItem.getInventario().getProducto().getReferencia());
			int cntChecked = 0;
			for(CodProducto tmpCod : codsProds) 
				cntChecked+=tmpCod.isTransferido()?1:0;
			
			if(cntChecked > selectedItem.getCantidad())
				chkCod.setTransferido(false);
			else
				codigosRestantes++;
		} else
			codigosRestantes--;
	}
	
	public void setCodigosItm(CodProducto cdp) {
		selectedItem.setCodProducto(cdp);
		selectedItem = null;
	}
	
	public void cargarListaCodigos(ItemRequisicionEta prdItm) {
		//Obtenemos el inventario 
		
		Inventario inv = null;
		
		inv = (Inventario)getEntityManager()
				.createQuery("SELECT x FROM Inventario x " +
						"	WHERE x.producto = :prd AND x.sucursal = :suc ")
						.setParameter("prd", prdItm.getProducto())
						.setParameter("suc", prdItm.getReqEtapa().getSucursalSol())
						.getSingleResult();
		
		selectedItem = prdItm;
		selectedItem.setInventario(inv);
		
		if(inv != null) {
			codigosRestantes = 0;
			ArrayList<CodProducto> codsProds = null;
			//Si aun no se ha cargado la lista para ese item, la cargamos
			if(lstCodsProductos.get(inv.getProducto().getReferencia()) == null) {
				codsProds = (ArrayList<CodProducto>)getEntityManager()
						.createQuery("SELECT c FROM CodProducto c " +
					"	WHERE c.inventario = :inv AND c.estado = 'ACT' ")
					.setParameter("inv", inv)
					.getResultList();
			} else {
				codsProds = lstCodsProductos.get(inv.getProducto().getReferencia());
				//contamos cuantos habian dejado chequeados
				for(CodProducto tmpCod : codsProds)
					if(tmpCod.isTransferido())
						codigosRestantes++;
			}
			
			lstCodsProductos.put(inv.getProducto().getReferencia(), codsProds);
			currCodigos = codsProds;
		}
	}
	
	public boolean approve() {
		
			List<Item> items = new ArrayList<Item>();
			List<Item> itemsSet = null;
		
		for (ItemRequisicionEta item : instance.getItemsRequisicion()) {
			Item itemDescargo = new Item();
			itemDescargo.setCantidad(item.getCantidad());
			
			String numsSeries = "", numsLotes = "";
			ArrayList<CodProducto> codsProds = getLstCodsProductos().get(item.getProducto().getReferencia());
			if(codsProds != null && codsProds.size() > 0)
			for(CodProducto tmpCd: codsProds) {
				if(tmpCd.isTransferido() && tmpCd.getNumSerie() != null && !tmpCd.getNumSerie().equals("")) 
					numsSeries = numsSeries.concat(tmpCd.getNumSerie().trim() + ",");
				if(tmpCd.isTransferido() && tmpCd.getNumLote() != null && !tmpCd.getNumLote().equals("")) 
					numsLotes = numsLotes.concat(tmpCd.getNumLote().trim() + ",");
			}
			
			if(numsSeries != null)
				item.setNumSerie(item.getNumSerie().concat(numsSeries));
			if(numsLotes != null)
				item.setNumLote(item.getNumLote().concat(numsLotes));
			
			
			
			itemDescargo.setCostoUnitario(item.getProducto().getCosto());
			itemDescargo.setPrecioVenta(item.getProducto().getPrcNormal());
			
			//Consultamos el inventario al cual debe de asociarse los items, en base a la sucursal
			List invPrd = getEntityManager().createQuery("SELECT i FROM Inventario i " +
							"	WHERE i.sucursal = :suc AND i.producto = :prd")
							.setParameter("suc", instance.getSucursalSol())
							.setParameter("prd", item.getProducto())
							.getResultList();
			//Verificamos si alcanzan las existencias
			if(((Inventario)invPrd.get(0)).getCantidadActual() < item.getCantidad()) {
				
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("movimientoHome_error_save0"));
				return false;
			} else { //Colocamos la ubicacion actual del producto
				if(((Inventario)invPrd.get(0)).getCodUbicacion() != null)
					item.setUbicacionActual(((Inventario)invPrd.get(0)).getCodUbicacion().getNombre());
				else
					item.setUbicacionActual("-");
				getEntityManager().merge(item);
			}
		
			itemDescargo.setInventario((Inventario)invPrd.get(0));
			itemDescargo.setItemId(new ItemId());
			itemDescargo.getItemId().setInventarioId(itemDescargo.getInventario().getId());
			items.add(itemDescargo);
		}
		
		itemsSet = new ArrayList<Item>(items);
		
		instance.setUsrAprueba(loginUser.getUser());
		instance.setEstado("APR");
		
		instance.setFechaAprobacion(new Date());
		
		if (modify()) { 
			
			/* comentado el 06/10/2017
			//Generamos el movimiento
			movimientoHome.load();
			movimientoHome.getInstance().setObservacion("Movimiento de salida generado automaticamente " +
					"	como aprobacion de la requisicion #" + instance.getId());
			movimientoHome.getInstance().setTipoMovimiento("S");
			movimientoHome.getInstance().setRazon("V");
			movimientoHome.getInstance().setFecha(new Date());
			//movimientoHome.getInstance().setSucursal(instance.getSucursalSol()); //Colocamos que salio de la sucursal de taller
			//Extraemos la sucursal taller
			movimientoHome.getInstance().setSucursal((Sucursal)getEntityManager().createQuery("SELECT s FROM Sucursal s where s.id=101").getSingleResult());
			movimientoHome.getInstance().setItems(itemsSet);
			movimientoHome.setItemsAgregados(items);
			movimientoHome.save();*/
			return true;
		} 
		return false;
	}
	
	
	public boolean aprobarSalidaRequisicion() {
		
		List<Item> items = new ArrayList<Item>();
		List<Item> itemsSet = null;
		
		//Sucursal sucursalTaller = (Sucursal) getEntityManager().createQuery("SELECT s FROM Sucursal s where s.id=101").getSingleResult();
		
		for (ItemRequisicionEta item : instance.getItemsRequisicion()) {
			Item itemDescargo = new Item();
			itemDescargo.setCantidad(item.getCantidad());
			
			item.setDescargada(true); //agregado el 10/10/2017
			
			String numsSeries = "", numsLotes = "";
			ArrayList<CodProducto> codsProds = getLstCodsProductos().get(item.getProducto().getReferencia());
			if(codsProds != null && codsProds.size() > 0)
			for(CodProducto tmpCd: codsProds) {
				if(tmpCd.isTransferido() && tmpCd.getNumSerie() != null && !tmpCd.getNumSerie().equals("")) 
					numsSeries = numsSeries.concat(tmpCd.getNumSerie().trim() + ",");
				if(tmpCd.isTransferido() && tmpCd.getNumLote() != null && !tmpCd.getNumLote().equals("")) 
					numsLotes = numsLotes.concat(tmpCd.getNumLote().trim() + ",");
			}
			
			if(numsSeries != null)
				item.setNumSerie(item.getNumSerie().concat(numsSeries));
			if(numsLotes != null)
				item.setNumLote(item.getNumLote().concat(numsLotes));
			
			
			
			itemDescargo.setCostoUnitario(item.getProducto().getCosto());
			itemDescargo.setPrecioVenta(item.getProducto().getPrcNormal());
			
			//Consultamos el inventario al cual debe de asociarse los items, en base a la sucursal
			List invPrd = getEntityManager().createQuery("SELECT i FROM Inventario i " +
							"	WHERE i.sucursal = :suc AND i.producto = :prd")
							.setParameter("suc",loginUser.getUser().getSucursal())
							//.setParameter("suc", sucursalTaller)
							.setParameter("prd", item.getProducto())
							.getResultList();
			//Verificamos si alcanzan las existencias
			if(((Inventario)invPrd.get(0)).getCantidadActual() < item.getCantidad()) {
				
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("movimientoHome_error_save0"));
				return false;
			} else { //Colocamos la ubicacion actual del producto
				if(((Inventario)invPrd.get(0)).getCodUbicacion() != null)
					item.setUbicacionActual(((Inventario)invPrd.get(0)).getCodUbicacion().getNombre());
				else
					item.setUbicacionActual("-");
				getEntityManager().merge(item);
			}
		
			itemDescargo.setInventario((Inventario)invPrd.get(0));
			itemDescargo.setItemId(new ItemId());
			itemDescargo.getItemId().setInventarioId(itemDescargo.getInventario().getId());
			items.add(itemDescargo);
		}
		
		itemsSet = new ArrayList<Item>(items);
		
		if(instance.getUsrAprueba()==null)
		{
			instance.setUsrAprueba(loginUser.getUser());
			instance.setEstado("APR");
		}
		
		instance.setFechaAprobacion(new Date());
		
		if (modify()) { //Generamos el movimiento
			movimientoHome.load();
			movimientoHome.getInstance().setObservacion("Movimiento de salida generado automaticamente " +
					"	como aprobacion de la requisicion #" + instance.getId());
			movimientoHome.getInstance().setTipoMovimiento("S");
			movimientoHome.getInstance().setRazon("V");
			movimientoHome.getInstance().setFecha(new Date());
			movimientoHome.getInstance().setSucursal(loginUser.getUser().getSucursal()); //Colocamos que salio de la sucursal del usuario que solicita la requisicion
			//Extraemos la sucursal taller
			//movimientoHome.getInstance().setSucursal(sucursalTaller);//Colocamos que salio de la sucursal de taller
			movimientoHome.getInstance().setItems(itemsSet);
			movimientoHome.setItemsAgregados(items);
			movimientoHome.save();
			return true;
		} 
		return false;
	}
	
	public void reingresarItem(ItemRequisicionEta item)
	{
		Item itemIngreso = new Item();
		
		List<Inventario> invPrd = getEntityManager().createQuery("SELECT i FROM Inventario i " +
				"	WHERE i.sucursal = :suc AND i.producto = :prd")
				.setParameter("suc",item.getReqEtapa().getSucursalSol())
				.setParameter("prd", item.getProducto())
				.getResultList();
		
		itemIngreso.setCantidad(item.getCantidad());
		itemIngreso.setInventario(invPrd.get(0));
		itemIngreso.setItemId(new ItemId());
		itemIngreso.getItemId().setInventarioId(itemIngreso.getInventario().getId());
		
		
		Movimiento mov = new Movimiento();
		
		mov.setUsuario(loginUser.getUser());
		mov.setRazon("O");
		mov.setTipoMovimiento("E");
		mov.setFecha(new Date());
		mov.setSucursal(loginUser.getUser().getSucursal());
		
		movimientoHome.setInstance(mov);
		movimientoHome.getItemsAgregados().add(itemIngreso);
		
		item.setDescargada(false);
		getEntityManager().merge(item);
		
		movimientoHome.save();
		
		//modify();
		
		System.out.println("REingreso el item");
	}
	
	
	
	public void reingresarItem(Set<ItemRequisicionEta> items)
	{
		List<Item> itemsIngreso = new ArrayList<Item>();
		
		List<Inventario> invPrd = new ArrayList<Inventario>();
		
		/*for(ItemRequisicionEta itemReq:items)
		{*/
		
		for(Iterator<ItemRequisicionEta> it = items.iterator();it.hasNext();)
		{
		
			ItemRequisicionEta itemReq = it.next();
			
			if(invPrd.size()<=0)
			{
				invPrd = getEntityManager().createQuery("SELECT i FROM Inventario i " +
						"	WHERE i.sucursal = :suc AND i.producto = :prd")
						.setParameter("suc",itemReq.getReqEtapa().getSucursalSol())
						.setParameter("prd", itemReq.getProducto())
						.getResultList();
			}
			
			
			Item itemIngreso = new Item();			
			itemIngreso.setCantidad(itemReq.getCantidad());
			itemIngreso.setInventario(invPrd.get(0));
			itemIngreso.setItemId(new ItemId());
			itemIngreso.getItemId().setInventarioId(itemIngreso.getInventario().getId());
			
			itemsIngreso.add(itemIngreso);
			
			itemReq.setDescargada(false);
			getEntityManager().merge(itemReq);
		
		}
		Movimiento mov = new Movimiento();
		
		mov.setUsuario(loginUser.getUser());
		mov.setRazon("O");
		mov.setTipoMovimiento("E");
		mov.setFecha(new Date());
		mov.setSucursal(loginUser.getUser().getSucursal());
		
		movimientoHome.setInstance(mov);
		movimientoHome.setItemsAgregados(itemsIngreso);
		
		
		movimientoHome.save();
		
		//modify();
		
		//System.out.println("REingreso el item");
	}

	public boolean reject() {
		instance.setEstado("REC");
		return modify();
	}
	
	@Override
	public boolean preSave() {
		//instance.set
		
		/*if(instance.getSucursalSol().getId().equals(loginUser.getUser().getSucursal().getId()))
		{
			System.out.println("Entro a aprobar");
			instance.setEstado("APR");
			instance.setUsrAprueba(loginUser.getUser());
		}*/
		
		return true;
	}
	
	@Override
	public boolean preModify() {
		
		System.out.println("Entro a preModify");
		
		for(ItemRequisicionEta tmpItm : itemsAgregados) {
			//Validamos que para todos los productos de la venta que lleven codigo de lote o de serie, lleven uno
			//Y que lleven el mismo numero de codigos que de items
			ArrayList<CodProducto> codsProds = lstCodsProductos.get(tmpItm.getProducto().getReferencia());
			
			if(tmpItm.getProducto().getCategoria().isTieneNumLote() && (codsProds == null ||
					codsProds.size() < tmpItm.getCantidad())) {
				FacesMessages.instance().add(
						sainv_messages.get("vtaitm_error_itmnolot"));
				return false;
			}
			
			if(tmpItm.getProducto().getCategoria().isTieneNumSerie() && (codsProds == null ||
					codsProds.size() < tmpItm.getCantidad())) {
				FacesMessages.instance().add(
						sainv_messages.get("vtaitm_error_itmnoser"));
				return false;
			}
		}
		
		/*if(instance.getSucursalSol().getId().equals(loginUser.getUser().getSucursal().getId()))
		{
			System.out.println("Entro a aprobar");
			instance.setEstado("APR");
			instance.setUsrAprueba(loginUser.getUser());
		}*/
		
		return true;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void posSave() {
		//Tratamos de guardar todos los items de requisicion
		for(ItemRequisicionEta tmpItm : itemsAgregados) {
			tmpItm.setReqEtapa(instance);
			getEntityManager().persist(tmpItm);
		}
		getEntityManager().refresh(etapaRepCliHome.getInstance());
		
		
		if(!instance.getSucursalSol().getId().equals(loginUser.getUser().getSucursal().getId()))
		{
			generarTransferencia();
		}
	}
	
	public void generarTransferencia()
	{
		Transferencia transf = new Transferencia();
		
		transf.setSucursal(instance.getSucursalSol());
		transf.setFecha(new Date());
		transf.setObservacion("Generado desde requisicion taller");
		
		transf.setSucursalDestino(loginUser.getUser().getSucursal());
		transf.setUsuarioGenera(loginUser.getUser());
		transf.setEstado("P");
		
		/*transf.setDesde(loginUser.getUser().getSucursal().getNombre());
		transf.setHacia(instance.getSucursalSol().getNombre());*/
		
		//getEntityManager().persist(transf); 
		
		transf.setRequisicion(instance);
		
		transferenciaHome.setInstance(transf);
		
		for(ItemRequisicionEta item: itemsAgregados)
		{
			transferenciaHome.agregarProducto(item.getInventario(),item.getCantidad());
		}
		
		transferenciaHome.save();
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
		System.out.println("Entro a postModify");
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public Integer getReqId() {
		return reqId;
	}

	public void setReqId(Integer reqId) {
		this.reqId = reqId;
	}

	public List<ItemRequisicionEta> getItemsAgregados() {
		return itemsAgregados;
	}

	public void setItemsAgregados(List<ItemRequisicionEta> itemsAgregados) {
		this.itemsAgregados = itemsAgregados;
	}

	public List<Sucursal> getSucursalesSoli() {
		return sucursalesSoli;
	}

	public void setSucursalesSoli(List<Sucursal> sucursalesSoli) {
		this.sucursalesSoli = sucursalesSoli;
	}

	public List<Producto> getProductosSeleccionar() {
		return productosSeleccionar;
	}

	public void setProductosSeleccionar(List<Producto> productosSeleccionar) {
		this.productosSeleccionar = productosSeleccionar;
	}

	public List<Object[]> getResultList() {
		return resultList;
	}

	public void setResultList(List<Object[]> resultList) {
		this.resultList = resultList;
	}

	public ItemRequisicionEta getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ItemRequisicionEta selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<CodProducto> getCurrCodigos() {
		return currCodigos;
	}

	public void setCurrCodigos(List<CodProducto> currCodigos) {
		this.currCodigos = currCodigos;
	}

	public Map<String, ArrayList<CodProducto>> getLstCodsProductos() {
		return lstCodsProductos;
	}

	public void setLstCodsProductos(
			Map<String, ArrayList<CodProducto>> lstCodsProductos) {
		this.lstCodsProductos = lstCodsProductos;
	}

	public Integer getCodigosRestantes() {
		return codigosRestantes;
	}

	public void setCodigosRestantes(Integer codigosRestantes) {
		this.codigosRestantes = codigosRestantes;
	}

	public Sucursal getSucursalActUs() {
		return sucursalActUs;
	}

	public void setSucursalActUs(Sucursal sucursalActUs) {
		this.sucursalActUs = sucursalActUs;
	}

	public AparatoCliente getAparatoRequision() {
		return aparatoRequision;
	}

	public void setAparatoRequision(AparatoCliente aparatoRequision) {
		this.aparatoRequision = aparatoRequision;
	}

	public boolean isAbilitarEdicion() {
		return abilitarEdicion;
	}

	public void setAbilitarEdicion(boolean abilitarEdicion) {
		this.abilitarEdicion = abilitarEdicion;
	}

	
	
	

	
}
