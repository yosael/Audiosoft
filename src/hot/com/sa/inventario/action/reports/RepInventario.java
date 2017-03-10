package com.sa.inventario.action.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.model.inventory.Compra;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.Transferencia;
import com.sa.model.security.Sucursal;
import com.sa.model.vta.ComprobanteAsignadoDoc;

@Name("repInventario")
@Scope(ScopeType.CONVERSATION)
public class RepInventario extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hql;
	private String hqlCond;
	private String hqlOrder;
	private String codsServ;
	private final String repCtx = "/inv";
	
	@In
	private EntityManager entityManager;
		
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	private List<Inventario> inventarios = new ArrayList<Inventario>();
	private List<Producto> productos = new ArrayList<Producto>();
	private List<Transferencia> transferencias = new ArrayList<Transferencia>();
	private List<Compra> compras = new ArrayList<Compra>();
	HashMap<String, Object> dtRp = new HashMap<String, Object>();

	private Date fechaInicio;
	private Date fechaFin;
	private String intervaloTiempo;
	private Sucursal sucursal;
	private Sucursal sucursalDest;

	public void resetClass() {
		inventarios = new ArrayList<Inventario>();
		transferencias = new ArrayList<Transferencia>();
		compras = new ArrayList<Compra>();
		productos = new ArrayList<Producto>();
		fechaInicio = null;
		fechaFin = null;
		sucursal = null;
		sucursalDest = null;
		dtRp = new HashMap<String, Object>();
		resetMainClass();
	}
	
	public void existenciasPorSucursal(){
		
		
		hql = "SELECT i FROM Inventario i ";
		hqlCond = " WHERE 1 = 1 ";
		hqlOrder = " ORDER by i.producto.referencia";
		
		List<Map> parametros = new ArrayList<Map>();
		parametros.add(new HashMap<String, Object>(){{put("nomParam","suc");put("valor",sucursal);
							put("condicion"," i.sucursal = :suc ");}});
		inventarios = getFilteredList(entityManager, hql, hqlCond, hqlOrder, parametros);
	}
	
	public void repTransferMensual() {
		transferencias = new ArrayList<Transferencia>();
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT t FROM Transferencia t WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND t.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND t.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND t.fecha BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND t.fecha BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		if(sucursal != null) 
			hql += " AND t.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		if(sucursalDest != null) 
			hql += " AND t.sucursalDestino = :sucDest ";
		else
			hql += " AND (:sucDest IS NULL OR :sucDest = '') ";

		transferencias = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursal)
				.setParameter("sucDest", sucursalDest)
				.getResultList();
		
		//Calculamos total de ventas
		/*setTotDec1(0f);
		for(EtapaRepCliente tmpEt: resultList) 
		if(tmpEt.getReparacionCli().getCosto() != null)
			setTotDec1(getTotDec1()+tmpEt.getReparacionCli().getCosto());
		*/
	
	}
	
	public void repCompraMensual() {
		transferencias = new ArrayList<Transferencia>();
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT c FROM Compra c WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND c.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND c.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND c.fecha BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND c.fecha BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		if(sucursal != null) 
			hql += " AND c.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";

		compras = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursal)
				.getResultList();
		
		//Calculamos total de ventas
		setTotDec1(0f);
		for(Compra tmpCp: compras) 
		if(tmpCp.getSubTotal() != null)
			setTotDec1(getTotDec1()+tmpCp.getSubTotal());
	}
	
	public void repCorr() { 
		List<ComprobanteAsignadoDoc> comprobantes = new ArrayList<ComprobanteAsignadoDoc>();
		 
		hql = "SELECT c FROM ComprobanteAsignadoDoc c WHERE 1 = 1" +
				" AND c.fechaFinalizacion = NULL" +
				" AND c.numActual < c.numFinal";
		
		comprobantes = entityManager.createQuery(hql)
				.getResultList();
		
		System.out.println("Registros: " + comprobantes.size() );		
		
		dtRp.put("comprobantes", comprobantes);
	}	
	
	public void repDetallesTransferencia() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		List<Item> detTransferencias = new ArrayList<Item>();
		String condGnr = "";
		
		if(codsServ != null && !codsServ.trim().equals("")) {
			setValCmb2(codsServ);
			setCodsServ(null);
			setFechaInicio(null);
			setFechaFin(null);
		}	
		
		hql = "SELECT x FROM Transferencia x WHERE 1 = 1 ";
		
		if(fechaInicio != null && fechaFin == null) 
			condGnr += " AND x.entrada.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			condGnr += " AND x.entrada.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			condGnr += " AND x.entrada.fecha BETWEEN :f1 AND :f2 ";
		else
			condGnr = "  AND (:f1 = :f2 OR 1 = 1) ";
		
		if(sucursal != null) {
			condGnr += " AND x.sucursal = :suc ";
		} else
			condGnr += " AND (:suc = :suc OR 1 = 1) ";
		
		Integer numTxn = 0;
		
		if(getValCmb2() != null && !getValCmb2().trim().equals("")) {
			condGnr += " AND x.id = :idtx ";
			numTxn = Integer.parseInt(getValCmb2().replaceAll("TXN", ""));
		} else
			condGnr += " AND (:idtx = :idtx OR 1 = 1) ";
		
		if(sucursalDest != null) {
			condGnr += " AND x.sucursalDestino = :sucd ";
		} else
			condGnr += " AND (:sucd = :sucd OR 1 = 1) ";
		
		
		
		
		List<Transferencia> trans = entityManager.createQuery(hql + condGnr)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursal)
				.setParameter("sucd", sucursalDest)
				.setParameter("idtx", numTxn)
				.getResultList();
		
		//Iteramos sobre las transferencias para ir sacando los items
		for(Transferencia tmpTrans : trans) {
			for(Item tmpItm : tmpTrans.getItems()) {
				tmpItm.setSucursalOri(tmpTrans.getSucursal());
				tmpItm.setSucursalDest(tmpTrans.getSucursalDestino());
								
				boolean agregar = true;
				
				if((getValCmb5() != null && !getValCmb5().trim().equals("")) && 
						!tmpItm.getInventario().getProducto().getReferencia().toUpperCase().contains(getValCmb5().toUpperCase())) 
					agregar = false;
				
				if((getValCmb3() != null && !getValCmb3().trim().equals("")) && 
						!tmpItm.getCodsSerie().toUpperCase().contains(getValCmb3().toUpperCase())) 
					agregar = false;
					
				if(agregar)
					detTransferencias.add(tmpItm);
			}
		}
		
		dtRp.put("lst", detTransferencias);
	}
	
	public void listarMovimientos() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		List<Item> detTransferencias = new ArrayList<Item>();
		String condGnr = "";
		
		if(codsServ != null && !codsServ.trim().equals("")) {
			setValCmb2(codsServ);
			setCodsServ(null);
			setFechaInicio(null);
			setFechaFin(null);
		}	
		
		hql = "SELECT x FROM Transferencia x WHERE 1 = 1 ";
		
		if(fechaInicio != null && fechaFin == null) 
			condGnr += " AND x.entrada.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			condGnr += " AND x.entrada.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			condGnr += " AND x.entrada.fecha BETWEEN :f1 AND :f2 ";
		else
			condGnr = "  AND (:f1 = :f2 OR 1 = 1) ";
		
		if(sucursal != null) {
			condGnr += " AND x.sucursal = :suc ";
		} else
			condGnr += " AND (:suc = :suc OR 1 = 1) ";
		
		Integer numTxn = 0;
		
		if(getValCmb2() != null && !getValCmb2().trim().equals("")) {
			condGnr += " AND x.id = :idtx ";
			numTxn = Integer.parseInt(getValCmb2().replaceAll("TXN", ""));
		} else
			condGnr += " AND (:idtx = :idtx OR 1 = 1) ";
		
		if(sucursalDest != null) {
			condGnr += " AND x.sucursalDestino = :sucd ";
		} else
			condGnr += " AND (:sucd = :sucd OR 1 = 1) ";
		
		
		
		
		List<Transferencia> trans = entityManager.createQuery(hql + condGnr)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursal)
				.setParameter("sucd", sucursalDest)
				.setParameter("idtx", numTxn)
				.getResultList();
		
		//Iteramos sobre las transferencias para ir sacando los items
		for(Transferencia tmpTrans : trans) {
			for(Item tmpItm : tmpTrans.getItems()) {
				tmpItm.setSucursalOri(tmpTrans.getSucursal());
				tmpItm.setSucursalDest(tmpTrans.getSucursalDestino());
								
				boolean agregar = true;
				
				if((getValCmb5() != null && !getValCmb5().trim().equals("")) && 
						!tmpItm.getInventario().getProducto().getReferencia().toUpperCase().contains(getValCmb5().toUpperCase())) 
					agregar = false;
				
				if((getValCmb3() != null && !getValCmb3().trim().equals("")) && 
						!tmpItm.getCodsSerie().toUpperCase().contains(getValCmb3().toUpperCase())) 
					agregar = false;
					
				if(agregar)
					detTransferencias.add(tmpItm);
			}
		}
		
		dtRp.put("lst", detTransferencias);
	}
	
public void repExistenciasBajoLimite() {
		
		hql = "SELECT x FROM Inventario x WHERE 1 = 1 " + 
				" AND x.producto.activo = true" +
				"	AND x.producto.cantidadMinima > x.cantidadActual AND x.sucursal = :suc ";
		
		if(getValCmb1() != null && !getValCmb1().trim().equals(""))
			hql += " AND UPPER(x.producto.categoria.codigo) LIKE UPPER(:codcat) ";
		else
			hql += " AND (:codcat = 'dum' OR 1 = 1) ";
		
		if(getValCmb2() != null && !getValCmb2().trim().equals(""))
			hql += " AND UPPER(x.producto.referencia) LIKE UPPER(:codprd) ";
		else
			hql += " AND (:codprd = 'dum' OR 1 = 1) ";
		
			hql += " ORDER BY x.producto.categoria.codigo ASC, x.producto.referencia ASC ";
			
		inventarios = entityManager.createQuery(hql)
				.setParameter("suc", sucursal)
				.setParameter("codcat", getValCmb1().concat("%"))
				.setParameter("codprd", getValCmb2().concat("%"))
				.getResultList();
	}


	public void repCostoInventario() {
		Long cantItemsGbl = 0l;
		Double totalInvGbl = 0d;
		List<HashMap<String, Object>> listaInventarios = new ArrayList<HashMap<String, Object>>();
		//Sacamos una lista de todas las sucursales, filtrando si escogieron sucursal
		hql = "SELECT c FROM Sucursal c WHERE 1 = 1  ";
		
		if(sucursal != null) 
			hql += " AND c = :suc ";
		else
			hql += " AND (:suc IS NULL OR 1 = 1) ";
		
		List<Sucursal> sucursalesInv = entityManager.createQuery(hql)
				.setParameter("suc", sucursal)
				.getResultList();
		
		for(Sucursal tmpSuc : sucursalesInv) {
			hql = "SELECT x FROM Inventario x WHERE x.sucursal = :suc ";
			
			if(isFlag1()) 
				hql += " AND x.cantidadActual > 0 ";
			
			hql += " ORDER BY x.producto.referencia ASC ";
			List<Inventario> lstInv = new ArrayList<Inventario>();
			if(isFlag2()) {
				lstInv = entityManager.createQuery(hql)
						.setParameter("suc", tmpSuc)
						.getResultList(); 
			}
			//Consultamos cuantos productos hay en total y cuanto es el costo total con SUMs
			Long cantItems = 0l;
			Double totalInv = 0d;
			
			hql = "SELECT SUM(cantidadActual) FROM Inventario x WHERE x.sucursal = :suc ";
			cantItems = (Long)entityManager.createQuery(hql)
					.setParameter("suc", tmpSuc)
					.getSingleResult();
			
			hql = "SELECT SUM(x.cantidadActual * x.producto.costo) FROM Inventario x WHERE x.sucursal = :suc ";
			totalInv = (Double)entityManager.createQuery(hql)
					.setParameter("suc", tmpSuc)
					.getSingleResult();
			
			HashMap<String, Object> mpInv = new HashMap<String, Object>();
			mpInv.put("lst", lstInv);
			mpInv.put("qnt", cantItems);
			mpInv.put("mnt", totalInv);
			mpInv.put("suc", tmpSuc);
			
			listaInventarios.add(mpInv);
			
			cantItemsGbl += cantItems;
			totalInvGbl += totalInv;
		}
		
		dtRp.put("lst", listaInventarios);
		dtRp.put("mnt", totalInvGbl);
		dtRp.put("qnt", cantItemsGbl);
	}

public void repTransferenciasSoli() {
	setFechaInicio(fechaInicio!=null?truncDate(fechaInicio, false):null);
	setFechaFin(fechaFin!=null?truncDate(fechaFin, true):null);
	
	hql = "SELECT x FROM Transferencia x WHERE 1 = 1  ";
	if(fechaInicio != null && fechaFin == null) 
		hql += " AND x.fecha >= :f1 AND :f2 IS NULL ";
	else if(fechaInicio == null && fechaFin != null)
		hql += " AND x.fecha <= :f2 AND :f1 IS NULL ";
	else if(fechaInicio != null && fechaFin != null)
		hql += " AND x.fecha BETWEEN :f1 AND :f2 ";
	else {
		hql += "  AND x.fecha BETWEEN :f1 AND :f2 ";
		
	}

	if(sucursal != null) 
		hql += " AND x.sucursal = :suc ";
	else
		hql += " AND (:suc IS NULL OR :suc = '') ";
	
	if(getValCmb1() == null || getValCmb1().equals(""))
		hql+=" AND (:est IS NULL OR :est = '') ";
	else 
		hql+=" AND x.estado = :est ";
	
	hql += " ORDER BY x.fecha ASC ";

	transferencias = entityManager.createQuery(hql)
			.setParameter("f1", fechaInicio)
			.setParameter("f2", fechaFin)
			.setParameter("tpref", getValCmb1())
			.setParameter("suc", sucursal)
			.getResultList();
}

	public void repProductoPedido() {
		setFechaInicio(fechaInicio!=null?truncDate(fechaInicio, false):null);
		setFechaFin(fechaFin!=null?truncDate(fechaFin, true):null);
		
		hql = "SELECT DISTINCT x.inventario.producto FROM ItemPedido x WHERE 1 = 1   ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.pedido.fechaInicio >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.pedido.fechaInicio <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.pedido.fechaInicio BETWEEN :f1 AND :f2 ";
		else 
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		
		if(sucursal != null) 
			hql += " AND x.pedido.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		productos = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursal)
				.getResultList();
		//Iteramos por cada producto y vamos sacando cuanto va de cada uno
		
		hql = "SELECT SUM(x.cantidad) FROM ItemPedido x WHERE 1 = 1 AND x.inventario.producto = :prd ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.pedido.fechaInicio >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.pedido.fechaInicio <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.pedido.fechaInicio BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND x.pedido.fechaInicio BETWEEN :f1 AND :f2 ";
		}
	
		if(sucursal != null) 
			hql += " AND x.pedido.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		for(Producto tmpPrd : productos) {
			Long cant = (Long)entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursal)
				.setParameter("prd", tmpPrd)
				.getSingleResult();
			if(cant != null)
				tmpPrd.setTotalPrds(cant.intValue());
			else
				tmpPrd.setTotalPrds(0);
		}
	}

	public String getRepCtx() {
		return repCtx;
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

	public String getIntervaloTiempo() {
		return intervaloTiempo;
	}

	public void setIntervaloTiempo(String intervaloTiempo) {
		this.intervaloTiempo = intervaloTiempo;
	}

	public List<Inventario> getInventarios() {
		return inventarios;
	}

	public void setInventarios(List<Inventario> inventarios) {
		this.inventarios = inventarios;
	}

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public List<Transferencia> getTransferencias() {
		return transferencias;
	}

	public void setTransferencias(List<Transferencia> transferencias) {
		this.transferencias = transferencias;
	}

	public List<Compra> getCompras() {
		return compras;
	}

	public void setCompras(List<Compra> compras) {
		this.compras = compras;
	}

	public Sucursal getSucursalDest() {
		return sucursalDest;
	}

	public void setSucursalDest(Sucursal sucursalDest) {
		this.sucursalDest = sucursalDest;
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	public HashMap<String, Object> getDtRp() {
		return dtRp;
	}

	public void setDtRp(HashMap<String, Object> dtRp) {
		this.dtRp = dtRp;
	}

	public String getCodsServ() {
		return codsServ;
	}

	public void setCodsServ(String codsServ) {
		this.codsServ = codsServ;
	}
	
}
