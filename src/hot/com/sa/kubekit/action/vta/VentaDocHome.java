package com.sa.kubekit.action.vta;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.javacc.RichMacroDefinition;

import com.sa.kubekit.action.crm.AsignacionCprHome;
import com.sa.kubekit.action.sales.VentaProdServHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.kubekit.action.util.Numalet;
import com.sa.model.crm.Cliente;
import com.sa.model.sales.DetVentaProdServ;
import com.sa.model.sales.SolicitudImpresion;
import com.sa.model.vta.ClienteDoc;
import com.sa.model.vta.ComprobanteAsignadoDoc;
import com.sa.model.vta.ComprobanteImpresion;
import com.sa.model.vta.DetVentaDoc;
import com.sa.model.vta.VentaDoc;

@Name("ventaDocHome")    
@Scope(ScopeType.CONVERSATION)    
public class VentaDocHome extends KubeDAO<VentaDoc>{ 
 
	private static final long serialVersionUID = 1L;
	private Integer ventaDocId; 
	private List<VentaDoc> resultlist = new ArrayList<VentaDoc>();
	private List<DetVentaDoc> detVentasDoc = new ArrayList<DetVentaDoc>();
	private final float IVA = 0.13F; // 13% IVA
	private final float PERCIBIDO = 0.01F; // 1% IVA PERCIBIDO
	private final float RETENIDO = 0.01F; // 1% IVA RETENIDO
	private float subTotalNs = 0F;
	private float subTotalE = 0F;
	private float subTotalG = 0F; 
	private boolean showComp = false;
	private String nwNombre;
	private String nwApellido;
	private ComprobanteAsignadoDoc comprobanteAsignadoDoc = new ComprobanteAsignadoDoc();
	private ComprobanteImpresion comprobanteSelected = new ComprobanteImpresion();
	private String numFactura;
	private String formaPago;
	private Boolean mostrarModCredito=false;
	private String cerrarModCredito="";
	
	
	@In
	private LoginUser loginUser;	
	
	@In(required=false,create=true) 
	@Out 
	private DetVentaDocHome detVentaDocHome;
	
	@In(required=false,create=true)
	private AsignacionCprHome asignacionCprHome;
	
	@In(required=false,create=true)
	private ClienteDocHome clienteDocHome;
	
	@In(required=false,create=true)
	private VentaProdServHome ventaProdServHome;
	
	@In(required=false,create=true)
	private ComprobanteImpresionHome comprobanteImpresionHome;
	
	
	@Override  
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("ventaDocHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("ventaDocHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("ventaDocHome_deleted")));
		
		//setNewInstance(newInstance);
	}
		
	@SuppressWarnings("unchecked")  
	public void cargarListaVentas(){
		
		String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
		if(getFechaPFlt1() != null && getFechaPFlt2() != null) {
			setFechaPFlt1(truncDate(getFechaPFlt1(), false));
			setFechaPFlt2(truncDate(getFechaPFlt2(), true));
			fltFch = " AND v.fecha BETWEEN :fch1 AND :fch2 ";
		}
				
		resultlist = getEntityManager()
			.createQuery("SELECT v FROM VentaDoc v WHERE 1 = 1 " +
				fltFch + " ORDER BY v.fecha DESC")
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.getResultList();
	}
	
	public void load(){ 
		try{
			setInstance(getEntityManager().find(VentaDoc.class, ventaDocId));
			 
			this.detVentasDoc = getEntityManager().
					createQuery("SELECT i FROM DetVentaDoc i WHERE 1=1" +
							" AND i.venta.id = :V1")
					.setParameter("V1",instance.getId())
					.getResultList();
			
			if( detVentasDoc == null ){
				detVentasDoc = new ArrayList<DetVentaDoc>(); 
			}
			
			/*if( instance.getDetVentas() != null ){
				this.setDetVentasDoc(instance.getDetVentas());
			}*/
			
			actualizarSubtotal();

		}catch (Exception e) {
			 
			clearInstance(); 
			setInstance(new VentaDoc());
			instance.setFecha( new Date() );
		} 
	} 
	
	public boolean agregarCompr(ComprobanteImpresion compr){		
		instance.setComprobante( compr );
		comprobanteAsignadoDoc = asignacionCprHome.getSiguienteCorrelativo( compr, loginUser.getUser().getSucursal() );
		if( comprobanteAsignadoDoc != null ){
			instance.setSerie( comprobanteAsignadoDoc.getSerie() );
			Long numActual = comprobanteAsignadoDoc.getNumActual().longValue() + 1;
			instance.setCorrelativo( numActual );
		}else{
			FacesMessages.instance().add(Severity.WARN, "La sucursal ya no dispone de comprobantes.");
			return false;
		}
		
		return true;
	}
	
	public void agregarDetVenta(){
		DetVentaDoc detVentaDoc = new DetVentaDoc();  
		detVentaDoc.setVenta(instance);
		detVentaDoc.setCantidad(1);
		detVentaDoc.setPrecioUnitario( new Float(0) );
		detVentaDoc.setDetalle("");
		detVentaDoc.setTipo("G");
		detVentaDoc.setTotal( detVentaDoc.getCantidad() * detVentaDoc.getPrecioUnitario() );
		this.detVentasDoc.add(detVentaDoc);
		actualizarSubtotal();
	}	
			
	public void genCompr() {
		/*setShowComp(true);
		//Creando una solicitud de impresion
		SolicitudImpresion solicitudImpresion = new SolicitudImpresion();
		solicitudImpresion.setUsuario( this.loginUser.getUser() );
		solicitudImpresion.setVentaDoc(instance);
		solicitudImpresion.setFecha(  new Date()  );
		solicitudImpresion.setTipDoc(getInstance().getComprobante().getCodigo());
		//Guardando la solicitud de impresion
		if( instance.getId() != null ){
			getEntityManager().merge(solicitudImpresion);
			getEntityManager().flush();
		}*/
	}
	
	
	public void imprimirFactura(ClienteDoc clientedoc)
	{
		
		if(!validarDatosFactura())
			return;
		
		//ClienteDoc clientedoc2 = new ClienteDoc();
		
		System.out.println("Entro a imprimir factura");
		 
		//Primero actualizar el cliente
		System.out.println("Nombreee "+clientedoc.getNombre());
		
		clienteDocHome.select(clientedoc);
		//clienteDocHome.setInstance(clientedoc);
		clienteDocHome.update();
		
		/*getEntityManager().merge(clientedoc);
		getEntityManager().flush();
		getEntityManager().refresh(clientedoc);*/
		
		//clientedoc2=clientedoc;
		
		//getEntityManager().getTransaction().begin();
		
		System.out.println("Actualizo la informacion del cliente");
		
		System.out.println("Nombree segunda vez" + clientedoc.getNombre());
		System.out.println("Apellido "+ clientedoc.getApellido());
		System.out.println("Id cliente "+ clientedoc.getId());
		
		//Obtener los datos de el nuevo registro del cliente
		
		
		setInstance(new VentaDoc());
		
		System.out.println("Comproante "+ comprobanteImpresionHome.getInstance().getCodigo());
		
		instance.setCliente(clientedoc);
		instance.setCorrelativo(Long.parseLong(numFactura));
		instance.setSucursal(ventaProdServHome.getInstance().getSucursal());
		instance.setFecha(new Date());
		instance.setComprobante(comprobanteSelected);
		instance.setDescuento(0f);
		//instance.setFormaPago(formaPago);
		instance.setFormaPago("EFE");//Pensar en que forma dinamica realizarlo	
		instance.setPercibido(0f);//??
		instance.setRetenido(0f);//??
		instance.setUsuario(loginUser.getUser());		
		instance.setEstado("APL");
		
		instance.setTotal(ventaProdServHome.getInstance().getMonto());
		
		save();
		
		
		System.out.println("Guardo la ventaDoc ***************");
		
		for(DetVentaProdServ detV: ventaProdServHome.getInstance().getDetVenta())
		{
			DetVentaDoc detDoc = new DetVentaDoc();
			
			detDoc.setDetalle(detV.getDetalle());
			detDoc.setCantidad(detV.getCantidad());
			detDoc.setPrecioUnitario(detV.getMonto());
			//detDoc.setProducto(detV.getProducto().get);
			detDoc.setTipo("G");//Gravada(G), Exenta (E), J?
			detDoc.setTotal(detV.getMonto());
			detDoc.setVenta(instance);
			
			detVentaDocHome.setInstance(detDoc);
			detVentaDocHome.save();
		}
		
		
		crearSolicitudImpresion();
		
		System.out.println("Se creo la solicitud de impresion ************");
		
		FacesMessages.instance().add("Solicitud de impresion creada");
		
	}
	
	public boolean validarDatosFactura()
	{
		//ClienteDoc clientedoc = new ClienteDoc();
		
		System.out.println("Entro a validar factura");
		
		if(numFactura==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el numero de factura");
			return false;
		}
		
		if(clienteDocHome.getInstance().getTelefono1()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese al menos un telefono");
			return false;
		}
		
		if(comprobanteSelected.getCodigo()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Seleccionar empresa y comprobante");
			return false;
		}
		
		
		
		
		return true;
	}
	
	public boolean validarDatosCreditoF()
	{
		cerrarModCredito="";
		ClienteDoc clientedoc = new ClienteDoc();
		
		clientedoc=clienteDocHome.getInstance();
		
		if(numFactura==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el numero de factura");
			return false;
		}
		
		if(clientedoc.getDui()==null && clientedoc.getNit()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingresel el dui o el nit");
			return false;
		}
		
		if(comprobanteSelected.getCodigo()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Seleccionar empresa y comprobante");
			return false;
		}
		
		if(clienteDocHome.getInstance().getGiro()==null) //preguntar si es obligatorio
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el giro");
			return false;
		}
		
		
		
		return true;
	}
	
	
	public void imprimirCreditoFiscal(ClienteDoc clientedoc)
	{
		
		if(!validarDatosCreditoF())
			return;
		
		//ClienteDoc clientedoc2 = new ClienteDoc();
		
		System.out.println("Entro a imprimir credito");
		 
		//Primero actualizar el cliente
		//System.out.println("Nombreee "+clientedoc.getNombre());
		
		clienteDocHome.select(clientedoc);
		//clienteDocHome.setInstance(clientedoc);
		clienteDocHome.update();
		
		/*getEntityManager().merge(clientedoc);
		getEntityManager().flush();
		getEntityManager().refresh(clientedoc);*/
		
		//clientedoc2=clientedoc;
		
		//getEntityManager().getTransaction().begin();
		
		System.out.println("Actualizo la informacion del cliente credito");
		
		System.out.println("Nombree segunda vez" + clientedoc.getNombre());
		System.out.println("Apellido "+ clientedoc.getApellido());
		System.out.println("Id cliente "+ clientedoc.getId());
		
		//Obtener los datos de el nuevo registro del cliente
		
		
		setInstance(new VentaDoc());
		
		//System.out.println("Comproante "+ comprobanteImpresionHome.getInstance().getCodigo());
		
		instance.setCliente(clientedoc);
		instance.setCorrelativo(Long.parseLong(numFactura));
		instance.setSucursal(ventaProdServHome.getInstance().getSucursal());
		instance.setFecha(new Date());
		instance.setComprobante(comprobanteSelected);
		instance.setDescuento(0f);
		//instance.setFormaPago(formaPago);
		instance.setFormaPago("EFE");//Pensar en que forma dinamica realizarlo	
		instance.setPercibido(0f);//??
		instance.setRetenido(0f);//??
		instance.setUsuario(loginUser.getUser());		
		instance.setEstado("APL");
		
		instance.setTotal(ventaProdServHome.getInstance().getMonto());
		
		save();
		
		
		System.out.println("Guardo la ventaDoc ***************");
		
		for(DetVentaProdServ detV: ventaProdServHome.getInstance().getDetVenta())
		{
			DetVentaDoc detDoc = new DetVentaDoc();
			
			detDoc.setDetalle(detV.getDetalle());
			detDoc.setCantidad(detV.getCantidad());
			detDoc.setPrecioUnitario(detV.getMonto());
			//detDoc.setProducto(detV.getProducto().get);
			detDoc.setTipo("G");//Gravada(G), Exenta (E), J?
			detDoc.setTotal(detV.getMonto());
			detDoc.setVenta(instance);
			
			detVentaDocHome.setInstance(detDoc);
			detVentaDocHome.save();
		}
		
		
		crearSolicitudImpresion();
		
		System.out.println("Se creo la solicitud de impresion ************");
		
		FacesMessages.instance().add("Solicitud de impresion creada");
		
		//clienteDocHome.setMostrarModFactura(false);
		
		cerrarModCredito="#{rich:component('modCreditoFiscal')}.hide();";
		
	}
	
	public void crearSolicitudImpresion()
	{
		System.out.println("************* Entro al metodo solicitud de impresion ************");
		
		
		
		SolicitudImpresion soli = new SolicitudImpresion();

		soli.setFecha(new Date());
		soli.setUsuario(loginUser.getUser());
		soli.setVentaDoc(instance);

		// Guardando la solicitud de impresion
		if (instance.getId() != null) {
			getEntityManager().merge(soli);
			getEntityManager().flush();
		}
	}
	
	public String getCantidadLetras(Float cantidad) {
		return getCantidadLetras(cantidad.doubleValue());
	}
	
	public String getCantidadLetras(Double cantidad) {
		Numalet numlt = new Numalet();
		return numlt.convertNumToLetters(cantidad, true) + " DOLARES";
	}
		
	public void removerDetVenta(DetVentaDoc detVentaDoc){
		detVentasDoc.remove(detVentaDoc);
		actualizarSubtotal();
	}
	
	private Float gravarPercepcion(Float precio) {
		Float gravable = precio;
		if(!instance.getComprobante().getTipo().equals("CCF"))
			gravable = precio / (1 + IVA);
		return gravable * PERCIBIDO;
	}	
	
	private Float gravarRetencion(Float precio) {
		Float gravable = precio;
		if(!instance.getComprobante().getTipo().equals("CCF"))
			gravable = precio / (1 + IVA);
		return gravable * RETENIDO;
	}	
	
	private Float gravarIva(Float precio) {
		Float gravable = precio;
		if(!instance.getComprobante().getTipo().equals("CCF"))
			gravable = precio / (1 + IVA);
		return ((gravable) * IVA);
	}
	
	private void actualizarTotal() {
		Float gravable = instance.getTotal();
		if(!instance.getComprobante().getTipo().equals("CCF"))
			gravable = instance.getTotal() / (1 + IVA);
		instance.setTotal( gravable + instance.getIva() + instance.getPercibido() - instance.getRetenido() );
	}	
	
	public void actualizarSubtotal(){
		instance.setTotal( new Float(0) );
		this.setSubTotalE( new Float(0) );
		this.setSubTotalG( new Float(0) );
		this.setSubTotalNs( new Float(0) );
		instance.setPercibido(0f);
		instance.setRetenido(0f);
				 
		for(DetVentaDoc detVtaItem: detVentasDoc){
			
			if(detVtaItem.getCantidad() != null && detVtaItem.getPrecioUnitario() != null ){
				detVtaItem.setTotal( detVtaItem.getCantidad() * detVtaItem.getPrecioUnitario() );
				instance.setTotal( instance.getTotal() + detVtaItem.getTotal() );
				
				if( detVtaItem.getTipo().equals("E") )
					this.setSubTotalE( this.getSubTotalE() + detVtaItem.getTotal() );
				else {
					if( detVtaItem.getTipo().equals("G") ) 
						this.setSubTotalG( this.getSubTotalG() + detVtaItem.getTotal() );
					else if( detVtaItem.getTipo().equals("J") )
							this.setSubTotalNs( this.getSubTotalNs() + detVtaItem.getTotal());
				}
			}
		}
		
		if (instance.getCliente() != null //instance.getComprobante().getTipo().equals("CCF") &&
				&& ((subTotalG / (1 + IVA)) >= 100 || instance.getCliente().isOmisionMinimoRet()) 
				&& !instance.getCliente().isExento()) {
			
			int tipoContri = instance.getComprobante().getEmpresaDoc().getTipoContribuyente();
			if (instance.getCliente().getTipoContribuyente() != null && instance.getCliente().getTipoContribuyente() < tipoContri 
					&& tipoContri == 3)
				instance.setPercibido(gravarPercepcion(subTotalG));
			if (instance.getCliente().getTipoContribuyente() != null && 
					instance.getCliente().getTipoContribuyente() > tipoContri
					&& instance.getCliente().getTipoContribuyente() == 3 )
				instance.setRetenido(gravarRetencion(subTotalG));
		}
		
		instance.setIva( gravarIva(subTotalG) );
		actualizarTotal();
	}	

	@Override
	public boolean preSave() {
		if(instance.getCliente() == null) {
			ClienteDoc clt = new ClienteDoc();
			clt.setNombre(nwNombre);
			clt.setApellido(nwApellido);
			clienteDocHome.select(clt);
			clienteDocHome.save();
			clt = clienteDocHome.getInstance();
			instance.setCliente(clt);
		}
		instance.setSucursal(loginUser.getUser().getSucursal());
		instance.setEstado("APL");
		// TODO Auto-generated method stub
		/*if( comprobanteAsignadoDoc == null ){ comentado el 23/01/2017
			FacesMessages.instance().add(Severity.WARN, "No se dispone de comprobantes para realizar la venta.");
		}*/
		
		return true;  
	}
	
	public boolean anularVenta(){
		System.out.println("Anulando la Venta");
		try{
			instance.setEstado("ANU");
			this.modify();
			FacesMessages.instance().add(Severity.INFO, "La venta se ha anulado correctamente.");
			return true;
		}catch(Exception e){
			FacesMessages.instance().add(Severity.ERROR, "La venta no se ha podido ser anulada.");
			return false;
		}
	}	
	
	public void seleccionarCliente(ClienteDoc clt) {
		getInstance().setCliente(clt);
		actualizarSubtotal();
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
		
		/*if( comprobanteAsignadoDoc != null ){ comentado el 23/01/2017
			System.out.println("No se aumenta el correlativo");
			asignacionCprHome.aumentarCorrelativo( comprobanteAsignadoDoc );
		}else{
			System.out.println("No se aumenta el correlativo");
		} */
		
		/*for(DetVentaDoc detVtaItem: detVentasDoc){ comentado el 23/01/2017
			detVentaDocHome.setInstance(detVtaItem);
			detVentaDocHome.save();
		}
		
		List<VentaDoc> vd = getEntityManager().
				createQuery("SELECT i FROM VentaDoc i WHERE 1=1 ORDER BY i.id DESC").getResultList();
		if(vd!=null && !vd.isEmpty()){
			this.ventaDocId = (int) vd.get(0).getId();
		}*/
		
		/*setShowComp(true);
		//Creando una solicitud de impresion
		SolicitudImpresion solicitudImpresion = new SolicitudImpresion();
		solicitudImpresion.setUsuario( this.loginUser.getUser() );
		solicitudImpresion.setVentaDoc(instance);
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

	public Integer getVentaDocId() {
		return ventaDocId;
	}

	public void setVentaDocId(Integer ventaDocId) {
		this.ventaDocId = ventaDocId;
	}

	public List<VentaDoc> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List<VentaDoc> resultlist) {
		this.resultlist = resultlist;
	}

	public List<DetVentaDoc> getDetVentasDoc() {
		return detVentasDoc;
	}

	public void setDetVentasDoc(List<DetVentaDoc> detVentasDoc) {
		this.detVentasDoc = detVentasDoc;
	}

	public float getSubTotalNs() {
		return subTotalNs;
	}

	public void setSubTotalNs(float subTotalNs) {
		this.subTotalNs = subTotalNs;
	}

	public float getSubTotalE() {
		return subTotalE;
	}

	public void setSubTotalE(float subTotalE) {
		this.subTotalE = subTotalE;
	}

	public float getSubTotalG() {
		return subTotalG;
	}

	public void setSubTotalG(float subTotalG) {
		this.subTotalG = subTotalG;
	}

	public boolean isShowComp() {
		return showComp;
	}

	public void setShowComp(boolean showComp) {
		this.showComp = showComp;
	}

	public ComprobanteAsignadoDoc getComprobanteAsignadoDoc() {
		return comprobanteAsignadoDoc;
	}

	public void setComprobanteAsignadoDoc(ComprobanteAsignadoDoc comprobanteAsignadoDoc) {
		this.comprobanteAsignadoDoc = comprobanteAsignadoDoc;
	}

	public String getNwNombre() {
		return nwNombre;
	}

	public void setNwNombre(String nwNombre) {
		this.nwNombre = nwNombre;
	}

	public String getNwApellido() {
		return nwApellido;
	}

	public void setNwApellido(String nwApellido) {
		this.nwApellido = nwApellido;
	}

	public ComprobanteImpresion getComprobanteSelected() {
		return comprobanteSelected;
	}

	public void setComprobanteSelected(ComprobanteImpresion comprobanteSelected) {
		this.comprobanteSelected = comprobanteSelected;
	}

	
	public String getNumFactura() {
		return numFactura;
	}

	public void setNumFactura(String numFactura) {
		this.numFactura = numFactura;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public Boolean getMostrarModCredito() {
		return mostrarModCredito;
	}

	public void setMostrarModCredito(Boolean mostrarModCredito) {
		this.mostrarModCredito = mostrarModCredito;
	}

	public String getCerrarModCredito() {
		return cerrarModCredito;
	}

	public void setCerrarModCredito(String cerrarModCredito) {
		this.cerrarModCredito = cerrarModCredito;
	}
	
	
	
	
	
	
}
