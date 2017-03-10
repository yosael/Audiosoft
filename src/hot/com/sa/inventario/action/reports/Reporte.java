package com.sa.inventario.action.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.model.crm.ChequeDoc;
import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.Producto;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.ReparacionCliente;

@Name("reporte")
@Scope(ScopeType.CONVERSATION)
public class Reporte extends MasterRep implements Serializable{

	private static final long serialVersionUID = 1L;
	private String hql;
	private String hqlCond;
	private String hqlOrder;
	@In
	private EntityManager entityManager;
	
	@In
	private LoginUser loginUser;
	
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	
	private List<Inventario> inventarios = new ArrayList<Inventario>();
	private Inventario inventario;
	private Integer inventarioId;
	
	private List<Movimiento> movimientos = new ArrayList<Movimiento>();
	private List<Item> items = new ArrayList<Item>();
	private List<ReparacionCliente> reparacionesPend = new ArrayList<ReparacionCliente>();
	private List<ReparacionCliente> etapasRetr = new ArrayList<ReparacionCliente>();
	
	private Empresa empresaSeleccionada;
	private Sucursal sucursalSeleccionada;
	private List<Sucursal> sucursales=new ArrayList<Sucursal>();
	
	private Categoria categoriaSeleccionada;
	private Producto productoSeleccionado;
	private List<Producto> productos=new ArrayList<Producto>();
	
	//Creados por ROD para manejar las variables de filtrado de items <= movimento
	private String tipoMovimiento;
	private String razon;
	private String orderBy = "movimiento.fecha";
	private String orderDir = "asc";
	
	public void resetClass() {
		fechaInicio = null;
		fechaFin = null;
		
		inventarios = new ArrayList<Inventario>();
		inventario = null;
		inventarioId = null;
		
		movimientos = new ArrayList<Movimiento>();
		items = new ArrayList<Item>();
		reparacionesPend = new ArrayList<ReparacionCliente>();
		etapasRetr = new ArrayList<ReparacionCliente>();
		
		empresaSeleccionada = null;
		sucursalSeleccionada = null;
		sucursales=new ArrayList<Sucursal>();
		
		categoriaSeleccionada = null;
		productoSeleccionado = null;
		productos=new ArrayList<Producto>();
		tipoMovimiento = "";
		razon = "";
	}
	
	
	public void cargarInventario(){
		inventario = entityManager.find(Inventario.class, inventarioId);
		this.consultarMovimientos();
	}
	
	@SuppressWarnings("unchecked")
	public void consultaInventarios(){
		inventarios = entityManager.createQuery("select i from Inventario i where i.sucursal = :sucursal " +
				"order by i.producto.categoria")
				.setParameter("sucursal", loginUser.getUser().getSucursal())
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public void verReparacionesPendientes(){
		Date date1 = null;
		Date date2 = null;
		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaInicio);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			date1 = cal.getTime();
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(fechaFin);
			cal2.set(Calendar.HOUR_OF_DAY, 23);
			cal2.set(Calendar.MINUTE, 59);
			cal2.set(Calendar.SECOND, 59);
			date2 = cal2.getTime();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		hql = "SELECT r FROM ReparacionCliente r WHERE r.estado = 'PEN' ";
		hqlCond = "";
		hqlCond +=(fechaInicio != null)?" AND r.fechaIngreso >= :fechaInicio ":"";
		hqlCond +=(fechaFin != null)?" AND r.fechaIngreso <= :fechaFin ":"";
		
		if(fechaInicio != null && fechaFin != null)
			reparacionesPend = entityManager.createQuery(hql + hqlCond)
					.setParameter("fechaInicio", date1)
					.setParameter("fechaFin", date2)
					.getResultList();
		else if(fechaInicio != null)
			reparacionesPend = entityManager.createQuery(hql + hqlCond)
					.setParameter("fechaInicio", date1)
					.getResultList();
		else if(fechaFin != null)
			reparacionesPend = entityManager.createQuery(hql + hqlCond)
					.setParameter("fechaFin", date2)
					.getResultList();
		else
			reparacionesPend = entityManager.createQuery(hql + hqlCond)
					.getResultList();
		
	}
	
	@SuppressWarnings("unchecked")
	public void verEtapasRetrasadas(){
		Date date1 = null;
		Date date2 = null;
		Date dateToday = null;
		try{
			
			Calendar cal3 = Calendar.getInstance();
			cal3.set(Calendar.HOUR_OF_DAY, 23);
			cal3.set(Calendar.MINUTE, 59);
			cal3.set(Calendar.SECOND, 59);
			dateToday = cal3.getTime();
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaInicio);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			date1 = cal.getTime();
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(fechaFin);
			cal2.set(Calendar.HOUR_OF_DAY, 23);
			cal2.set(Calendar.MINUTE, 59);
			cal2.set(Calendar.SECOND, 59);
			date2 = cal2.getTime();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		hql = "SELECT e FROM EtapaRepCliente e WHERE e.reparacionCli.estado = 'PEN'  ";
		hqlCond = "";
		hqlCond += " AND e.fechaEstFin < :fechaHoy ";
		hqlCond +=(fechaInicio != null)?" AND e.reparacionCli.fechaIngreso >= :fechaInicio ":"";
		hqlCond +=(fechaFin != null)?" AND e.reparacionCli.fechaIngreso <= :fechaFin ":"";
		hqlOrder = " ORDER BY e.reparacionCli.fechaIngreso ASC, e.etapaRep.orden ASC ";
		
		if(fechaInicio != null && fechaFin != null)
			etapasRetr = entityManager.createQuery(hql + hqlCond + hqlOrder)
					.setParameter("fechaHoy", dateToday)
					.setParameter("fechaInicio", date1)
					.setParameter("fechaFin", date2)
					.getResultList();
		else if(fechaInicio != null)
			etapasRetr = entityManager.createQuery(hql + hqlCond + hqlOrder)
					.setParameter("fechaHoy", dateToday)
					.setParameter("fechaInicio", date1)
					.getResultList();
		else if(fechaFin != null)
			etapasRetr = entityManager.createQuery(hql + hqlCond + hqlOrder)
					.setParameter("fechaHoy", dateToday)
					.setParameter("fechaFin", date2)
					.getResultList();
		else
			etapasRetr = entityManager.createQuery(hql + hqlCond + hqlOrder)
					.setParameter("fechaHoy", dateToday)
					.getResultList();
		
	}
	
	private String str1;
	private String str2;
	private Double totalMontoCheques;
	List<ChequeDoc> chequesDoc = new ArrayList<ChequeDoc>();	
	
	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}

	public Double getTotalMontoCheques() {
		return totalMontoCheques;
	}

	public void setTotalMontoCheques(Double totalMontoCheques) {
		this.totalMontoCheques = totalMontoCheques;
	}

	public List<ChequeDoc> getChequesDoc() {
		return chequesDoc;
	}

	public void setChequesDoc(List<ChequeDoc> chequesDoc) {
		this.chequesDoc = chequesDoc;
	}

	@SuppressWarnings("unchecked")
	public void chequesEmitidos(){

		if(fechaInicio == null){
			fechaInicio = new Date();
		}
		
		if(fechaFin == null){
			fechaFin = new Date();
		}					
		
		hql = "SELECT e FROM ChequeDoc e WHERE 1 = 1 AND (e.fecha >= :F1 AND e.fecha <= :F2)";
		
		
		if( str2 == null || str2.equals("") ){
			hql += " AND :P1 = :P1";
		}else{ 
			hql += " AND e.comprobante.empresaDoc.nombre = :P1";
		}
		
		if( str1 == null || str1.equals("") ){
			hql += " AND :P2 = :P2";
		}else{
			hql += " AND e.proveedor.razonSocial = :P2";
		}	
		
		chequesDoc = new ArrayList<ChequeDoc>();
		chequesDoc = entityManager.createQuery(hql)
				.setParameter("F1", fechaInicio)
				.setParameter("F2", fechaFin)
				.setParameter("P1", str2)
				.setParameter("P2", str1)
				.getResultList(); 
		
		System.out.println("Obteniendo listado de cheques");
		
		hql = "SELECT SUM(e.monto) FROM ChequeDoc e WHERE 1 = 1 AND (e.fecha >= :F1 AND e.fecha <= :F2)";
		
		if( str2 == null || str2.equals("") ){
			hql += " AND :P1 = :P1";
		}else{ 
			hql += " AND e.comprobante.empresaDoc.nombre = :P1";
		}
		
		if( str1 == null || str1.equals("") ){
			hql += " AND :P2 = :P2";
		}else{
			hql += " AND e.proveedor.razonSocial = :P2";
		}		
		
		totalMontoCheques = 0.0;
		totalMontoCheques = (Double) entityManager.createQuery(hql)
				.setParameter("F1", fechaInicio)
				.setParameter("F2", fechaFin)
				.setParameter("P1", str2)
				.setParameter("P2", str1)
				.getSingleResult();
	}	
	
	@SuppressWarnings("unchecked")
	public void consultarMovimientos(){
		Date date1 = null;
		Date date2 = null;
		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaInicio);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			date1 = cal.getTime();
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(fechaFin);
			cal2.set(Calendar.HOUR_OF_DAY, 23);
			cal2.set(Calendar.MINUTE, 59);
			cal2.set(Calendar.SECOND, 59);
			date2 = cal2.getTime();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		 
		if(fechaInicio==null && fechaFin==null){
			items = entityManager.createQuery("select distinct(i) from Item i " +
					"where i.itemId.inventarioId = :inventarioId")
					.setParameter("inventarioId", inventarioId)
					.getResultList();
		}else if(fechaInicio!=null && fechaFin==null){
			items = entityManager.createQuery("select distinct(i) from Item i " +
					"where i.itemId.inventarioId = :inventarioId and i.movimiento.fecha >= :fechaInicio")
					.setParameter("inventarioId", inventarioId)
					.setParameter("fechaInicio", date1)
					.getResultList();
		}else if(fechaInicio==null && fechaFin!=null){
			items = entityManager.createQuery("select distinct(i) from Item i " +
					"where i.itemId.inventarioId = :inventarioId and i.movimiento.fecha <= :fechaFin")
					.setParameter("inventarioId", inventarioId)
					.setParameter("fechaFin", date2)
					.getResultList();
		}else{
			System.out.println("ENTRO AL ULTIMO ELSE");
			items = entityManager.createQuery("select distinct(i) from Item i " +
					"where i.itemId.inventarioId = :inventarioId and i.movimiento.fecha between :fecha1 and :fecha2")
					.setParameter("inventarioId", inventarioId)
					.setParameter("fecha1", date1)
					.setParameter("fecha2", date2)
					.getResultList();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void listarMovimientos(){
		String condGnr = "";
		
		if(fechaInicio != null && fechaFin == null) 
			condGnr += " AND x.movimiento.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			condGnr += " AND x.movimiento.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			condGnr += " AND x.movimiento.fecha BETWEEN :f1 AND :f2 ";
		else
			condGnr = "  AND (:f1 = :f2 OR 1 = 1) ";
		
		if(sucursalSeleccionada != null) {
			condGnr += " AND x.movimiento.sucursal = :suc ";
		} else
			condGnr += " AND (:suc = :suc OR 1 = 1) ";
		
		if(tipoMovimiento != null) {
			condGnr += " AND x.movimiento.tipoMovimiento = :tipm ";
		} else
			condGnr += " AND (:tipm = :tipm OR 1 = 1) ";
		
		if(razon != null && !razon.trim().equals("")) {
			condGnr += " AND x.movimiento.razon = :razn ";
		} else
			condGnr += " AND (:razn = :razn OR 1 = 1) ";
		
		if(categoriaSeleccionada != null) {
			condGnr += " AND x.inventario.producto.categoria = :cat ";
		} else
			condGnr += " AND (:cat = :cat OR 1 = 1) ";
		
		if(productoSeleccionado != null) {
			condGnr += " AND x.inventario.producto = :prd ";
		} else
			condGnr += " AND (:prd = :prd OR 1 = 1) ";
		
		
		String sQuery = "select x from Item x where 1 = 1 ";
		
		items = entityManager.createQuery(sQuery + condGnr)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursalSeleccionada)
				.setParameter("tipm", tipoMovimiento)
				.setParameter("razn", razon)
				.setParameter("cat", categoriaSeleccionada)
				.setParameter("prd", productoSeleccionado)
				.getResultList();
		System.out.println("");
	}
	
	public void cargarProductos(){
		if(categoriaSeleccionada!=null){
			System.out.print("Categoria :" + categoriaSeleccionada.getNombre());
			productos = new ArrayList<Producto>(categoriaSeleccionada.getProductos());
		}
		this.listarMovimientos();
	}
	
	@SuppressWarnings("unchecked")
	public void cargarSucursales(){
		if(empresaSeleccionada==null){
			if(loginUser.getUser().getSucursal()!=null){
				this.empresaSeleccionada=loginUser.getUser().getSucursal().getEmpresa();
				this.sucursalSeleccionada=loginUser.getUser().getSucursal();
			}
		}
		
		if(empresaSeleccionada!=null){
			sucursales=entityManager.createQuery("select s from Sucursal s where s.empresa = :empresa")
												.setParameter("empresa", empresaSeleccionada)
												.getResultList();
		}
		
	}
	
	public String obtenerTipoMovimiento(Item item){
		if(item.getMovimiento().getTipoMovimiento().equals("E")){
			return sainv_messages.get("report_movement_type_e");
		}else{
			return sainv_messages.get("report_movement_type_s");
		}
	}
	
	public String obtenerTipoTransaccion(Item item){
		if(item.getMovimiento().getRazon().equals("O")){
			return sainv_messages.get("report_movement_reason_o");
		}else if(item.getMovimiento().getRazon().equals("T")){
			return sainv_messages.get("report_movement_reason_t");
		}else if(item.getMovimiento().getRazon().equals("P")){
			return sainv_messages.get("report_movement_reason_p");
		}else{
			return sainv_messages.get("report_movement_reason_c");
		}
	}
	
	public Integer pdfColsNumber(){
		Integer cols=7;
		if(this.tipoMovimiento==null)
			cols+=1;
		if(loginUser.getUser().getSucursal()==null)
			cols+=1;
		return cols;
	}
	
	public List<Inventario> getInventarios() {
		return inventarios;
	}

	public void setInventarios(List<Inventario> inventarios) {
		this.inventarios = inventarios;
	}

	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}

	public Integer getInventarioId() {
		return inventarioId;
	}

	public void setInventarioId(Integer inventarioId) {
		this.inventarioId = inventarioId;
	}

	public List<Movimiento> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<Movimiento> movimientos) {
		this.movimientos = movimientos;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<ReparacionCliente> getReparacionesPend() {
		return reparacionesPend;
	}

	public void setReparacionesPend(List<ReparacionCliente> reparacionesPend) {
		this.reparacionesPend = reparacionesPend;
	}

	public List<ReparacionCliente> getEtapasRetr() {
		return etapasRetr;
	}

	public void setEtapasRetr(List<ReparacionCliente> etapasRetr) {
		this.etapasRetr = etapasRetr;
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public String getRazon() {
		return razon;
	}

	public void setRazon(String razon) {
		this.razon = razon;
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	public Categoria getCategoriaSeleccionada() {
		return categoriaSeleccionada;
	}

	public void setCategoriaSeleccionada(Categoria categoriaSeleccionada) {
		this.categoriaSeleccionada = categoriaSeleccionada;
	}

	public Producto getProductoSeleccionado() {
		return productoSeleccionado;
	}

	public void setProductoSeleccionado(Producto productoSeleccionado) {
		this.productoSeleccionado = productoSeleccionado;
	}

	public Empresa getEmpresaSeleccionada() {
		return empresaSeleccionada;
	}

	public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
		this.empresaSeleccionada = empresaSeleccionada;
	}

	public Sucursal getSucursalSeleccionada() {
		return sucursalSeleccionada;
	}

	public void setSucursalSeleccionada(Sucursal sucursalSeleccionada) {
		this.sucursalSeleccionada = sucursalSeleccionada;
	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}
	
	public String getInvPDF(){
		return "/sainv/sainv/reportes/inventarios/invPDF.sa?docId=1";
	}
	
	public String getDetailPDF(){
		return "/sainv/sainv/reportes/inventarios/detailInvPDF.sa?docId=1";
	}
	
	public String getMovPDF(){
		return "/sainv/sainv/reportes/movimientos/movPDF.sa?docId=1";
	}
	
	public String getCurrRepsPDF(){
		return "/sainv/sainv/reportes/taller/currRepsPDF.sa?docId=1";
	}
	
	public String getretrEtasPDF(){
		return "/sainv/sainv/reportes/taller/retrEtasPDF.sa?docId=1";
	}
	
	public String getRepChequesPDF(){
		return "/sainv/sainv/reportes/crm/repChequesPDF.sa?docId=1";
	}	
	
}
