package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.crm.ClienteHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Cliente;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Producto;
import com.sa.model.workshop.AparatoCliente;
import com.sa.model.workshop.PiezaAparatoCliente;

@Name("aparatoClienteHome")
@Scope(ScopeType.CONVERSATION)
public class AparatoClienteHome extends KubeDAO<AparatoCliente>{

	private static final long serialVersionUID = 1L;
	private Integer apaCliId;
	private List<AparatoCliente> resultList = new ArrayList<AparatoCliente>();
	private List<Item> items = new ArrayList<Item>();
	private String descripcion;
	private Cliente clienteFlt;
	private boolean tieneGarantia;
	private boolean tieneGarantiaRep;
	private boolean cerrarModal;
	private Producto productoAsociado;
	
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("aparcli_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("aparcli_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("aparcli_deleted")));
	}
		
	@In(required=false,create=true)
	@Out
	private ClienteHome clienteCRMHome;
	
	@In
	private LoginUser loginUser;
			
	public void load(){
		try{
			setInstance(getEntityManager().find(AparatoCliente.class, apaCliId));
			if(instance.getPeriodoGarantia() != null && instance.getPeriodoGarantia() > 0)
				setTieneGarantia(true);
			if(instance.getPeriodoGarantiaRep() != null && instance.getPeriodoGarantiaRep() > 0)
				setTieneGarantiaRep(true);
			
			if(!instance.isCustomApa() && !instance.isHechoMedida()) {
				//Agregamos el aparato principal a la lista
				PiezaAparatoCliente prpalPz = new PiezaAparatoCliente();
				prpalPz.setIdPrd(instance.getIdPrd());
				prpalPz.setMarca(instance.getMarca());
				prpalPz.setModelo(instance.getModelo());
				prpalPz.setNombre(instance.getNombre());
				prpalPz.setNumLote(instance.getNumLote());
				prpalPz.setNumSerie(instance.getNumSerie());
				instance.getPiezasApa().add(prpalPz);
				prpalPz.setPrincipal(true);
				if (items.size()==0)
					for(PiezaAparatoCliente tmpPz : instance.getPiezasApa()) {
						Item itm = new Item();
						//Tratamos de encontrar el producto
						Inventario inv = (Inventario)getEntityManager()
								.createQuery("SELECT i FROM Inventario i WHERE i.sucursal = :suc AND i.producto = (SELECT p FROM Producto p WHERE p.id = :idPrd)")
								.setParameter("suc", loginUser.getUser().getSucursal())
								.setParameter("idPrd", tmpPz.getIdPrd())
								.getSingleResult();
						itm.setInventario(inv);
						if(inv.getProducto().getCategoria().isTieneNumSerie() ||
								inv.getProducto().getCategoria().isTieneNumLote()) {
							CodProducto codPrd = new CodProducto();
							if(inv.getProducto().getCategoria().isTieneNumSerie())
								codPrd.setNumSerie(tmpPz.getNumSerie());
							if(inv.getProducto().getCategoria().isTieneNumLote())
								codPrd.setNumLote(tmpPz.getNumLote());
							itm.setCodProducto(codPrd);
						}
						if(tmpPz.isPrincipal())
							itm.setPrincipal(true);
						items.add(itm);
					}
			}
		}catch (Exception e) {
			clearInstance();
			setInstance(new AparatoCliente());
			instance.setCliente(clienteCRMHome.getInstance());
		}
		
	}
	
	public void agregarProductoAsociado(Producto producto)
	{
		setProductoAsociado(producto);
		instance.setNombre(producto.getNombre());
		instance.setMarca(producto.getMarca().getNombre());
		instance.setModelo(producto.getModelo());
		
		
	}
	
	
	//método que actualiza el Estado de un aparato
	public void setActivo(AparatoCliente apa){
		
		System.out.println("Estado aparato Activo="+apa.isActivo());
		
		getEntityManager().merge(apa);
		getEntityManager().flush();
		FacesMessages.instance().add(
				sainv_messages.get("aparcli_activo"));
	}
	
	public void loadClienteApas(Cliente cli) {
		clienteCRMHome.select(cli);
		getApasCotsCliList();
	}
	
	public void getApasCotsCliList() {
		if(clienteCRMHome.isManaged()) {
			resultList = getEntityManager().createQuery("SELECT a FROM AparatoCliente a WHERE a.cliente = :cli AND a.estado = 'ACT' ")
					.setParameter("cli", clienteCRMHome.getInstance())
					.getResultList();
		}
	}
	
	public void setApaPrpal(Item itm) {
		if(itm.isPrincipal()) {
			for(Item tmpItm : items) {
				if(!tmpItm.equals(itm))
					tmpItm.setPrincipal(false);
			}
		}
	}
	
	public void cargarListaClientes(){
		clienteCRMHome.cargarListaClientes();
	}
	
	public void removerItem(Item itm) {
		items.remove(itm);
	}
	
	public void agregarItem(Inventario inv) {
		Item itm = new Item();
		itm.setInventario(inv);
		if(itm.getInventario().getProducto().getCategoria().isTieneNumLote() ||
				itm.getInventario().getProducto().getCategoria().isTieneNumSerie()) {
			CodProducto newCod = new CodProducto();
			itm.setCodProducto(newCod);
		}
		System.out.println("Agregado en linea 143");
		items.add(itm);
		if(items.size() == 1)
			itm.setPrincipal(true);
	}
	
	public void cargarCliente(Cliente cliente)
	{
		instance.setCliente(cliente);
		instance.setCustomApa(false);
	}

	@Override
	public boolean preSave() {
		cerrarModal=false;
		// Verificamos que todos los items que tienen numero de serie o lote lo tengan lleno.
				for (Item item : getItems()) {
					if (item.getInventario().getProducto() != null) {
						System.out.println("Producto: " + item.getInventario().getProducto().getNombre());
						if (item.getInventario().getProducto().getCategoria()
								.isTieneNumLote()) {
							if (item.getCodProducto().getNumLote().isEmpty()) {
								System.out.println("Entré al if, yo debería estar explotando en tu cara NumLote");
								FacesMessages.instance().add(
										sainv_messages.get("aparcli_error_itmnolot"));
								return false;
							}
						} else if (item.getInventario().getProducto().getCategoria().isTieneNumSerie()){
							if (item.getCodProducto().getNumSerie().isEmpty()) {
								System.out.println("Entré al if, yo debería estar explotando en tu cara NumSerie");
								FacesMessages.instance().add(
										sainv_messages.get("aparcli_error_itmnoser"));
								return false;
							}
						}
						}
					}
		return guardarAparato();
	}
	
	public void actualizarDesdeCheckExterno()
	{
		if(instance.isCustomApa())
			productoAsociado=null;
			
	}
	
	public void registrarNuevoAparato()
	{
		cerrarModal=false;
		if(!instance.isCustomApa() && productoAsociado==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe asociar un producto/item al aparato");
			return;
		}
		
		
		
		if(!instance.isCustomApa() && productoAsociado!=null)
		{
			items.clear();
			
			System.out.println("Entro a item para pieza");
			Item item = new Item();
			item.setCantidad(1);
			item.setCostoUnitario(productoAsociado.getCosto()!=null?productoAsociado.getCosto():0f);
			
			
			item.setInventario(productoAsociado.getInventarios().get(0));
			
			item.setCodProducto(new CodProducto());
			item.getCodProducto().setNumSerie(instance.getNumSerie()!=null?instance.getNumSerie():null);
			item.getCodProducto().setEstado("ACT");
			item.getCodProducto().setNumLote(null);
			
			item.setPrincipal(true);
			
			System.out.println("Serie en codigo "+item.getCodProducto().getNumSerie());
			
			this.items.add(item);
			
			instance.setIdPrd(productoAsociado.getId()!=null?productoAsociado.getId():null);
		}
		
		instance.setActivo(true);
		
		save();
	}
	
	private boolean guardarAparato() {
		Item apaPrpal = null;
		//Validamos que hayan seleccionado el cliente y el combo
		if(instance.getCliente() == null) {
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("aparcli_error_nocli"));
			
			return false;
		}
		
		//nuevo
		if(instance.getMarca()==null || instance.getMarca()=="")
		{System.out.println("marca vacia");
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar la marca");
			return false;
		}
		if(instance.getModelo()==null || instance.getModelo()=="")
		{System.out.println("modelo vacio");
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar el modelo");
			return false;
		}
		
		/*if(instance.getNumSerie()==null || instance.getNumSerie()=="")
		{System.out.println("num serie vacia");
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar numero de serie");
			return false;
		}*/
		
		
		System.out.println("No entrp a los validaciones");
		if(instance.getNombre()==null || instance.getNombre()=="")
		{
			instance.setNombre(instance.getModelo()+" "+instance.getMarca());
		}
		
		
		if(!instance.isCustomApa() && !instance.isHechoMedida()) {
		
			if(items == null || items.size() <= 0) {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("aparcli_error_noitm"));
				return false;
			}
			
			//Validamos que todas lleven producto
			for(Item tmpItm : getItems()) {
				//Vamos buscando el aparato principal
				if(tmpItm.isPrincipal())
					apaPrpal = tmpItm;
			}
			
			if(apaPrpal == null) {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("aparcli_error_noprpal"));
				return false;
			}
			
			
			//Seteamos los datos del aparato del cliente a guardar
			
			instance.setIdPrd(apaPrpal.getInventario().getProducto().getId());
			if(apaPrpal.getCodProducto() != null) {
				instance.setNumSerie(apaPrpal.getCodProducto().getNumSerie());
				instance.setNumLote(apaPrpal.getCodProducto().getNumLote());
			}
			instance.setMarca(apaPrpal.getInventario().getProducto().getMarca().getNombre());
			instance.setModelo(apaPrpal.getInventario().getProducto().getModelo());
			instance.setNombre(apaPrpal.getInventario().getProducto().getNombre());
		} else {
			instance.setIdPrd(null);
		}
		
		instance.setFechaAdquisicion(new Date());
		instance.setEstado("ACT");
		if(!instance.isCustomApa())
			instance.setCustomApa(false);
		
		cerrarModal=true;
		
		return true;
	}
	
	@Override
	public boolean preModify() {
		// Verificamos que todos los items que tienen numero de serie o lote lo tengan lleno.
		for (Item item : getItems()) {
			if (item.getInventario().getProducto() != null) {
				System.out.println("Producto: " + item.getInventario().getProducto().getNombre());
				if (item.getInventario().getProducto().getCategoria().isTieneNumLote() && item.getCodProducto().getNumLote().isEmpty()) {
					System.out.println("Entré al if, yo debería estar explotando en tu cara NumLote");
					FacesMessages.instance().add(sainv_messages.get("aparcli_error_itmnolot"));
					return false;
				} else if (item.getInventario().getProducto().getCategoria().isTieneNumSerie() && item.getCodProducto().getNumSerie().isEmpty())		{
					System.out.println("Entré al if, yo debería estar explotando en tu cara NumSerie");
					FacesMessages.instance().add(sainv_messages.get("aparcli_error_itmnoser"));
					return false;
				}
			}
		}
		return guardarAparato();
	}

	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public void posSave() {
		getEntityManager().refresh(instance);
		if(!instance.isCustomApa() && !instance.isHechoMedida()) {
			//Guardamos los demas accesorios del combo y el detalle de los costos en un solo texto
			for(Item tmpItm : getItems()) {
				
				if(!tmpItm.isPrincipal()) {
					PiezaAparatoCliente piezaApa = new PiezaAparatoCliente();
					piezaApa.setAparatoCliente(instance);
					piezaApa.setIdPrd(tmpItm.getInventario().getProducto().getId());
					if (tmpItm.getInventario().getProducto().getMarca() != null)
						piezaApa.setMarca(tmpItm.getInventario().getProducto().getMarca().getNombre());
					piezaApa.setModelo(tmpItm.getInventario().getProducto().getModelo());
					piezaApa.setNombre(tmpItm.getInventario().getProducto().getNombre());
					if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie())
						piezaApa.setNumSerie(tmpItm.getCodProducto().getNumSerie());
					if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote())
						piezaApa.setNumLote(tmpItm.getCodProducto().getNumLote());
					getEntityManager().persist(piezaApa);
				}
			}
		}
		getEntityManager().flush();
	}

	@Override
	public void posModify() {
		getEntityManager().refresh(instance);
		for(PiezaAparatoCliente tmpPiz : instance.getPiezasApa())
			getEntityManager().remove(tmpPiz);
		
		if(!instance.isCustomApa() && !instance.isHechoMedida()) {		
			//Guardamos los demas accesorios del combo y el detalle de los costos en un solo texto
			for(Item tmpItm : getItems()) {
				if(!tmpItm.isPrincipal()) {
					PiezaAparatoCliente piezaApa = new PiezaAparatoCliente();
					piezaApa.setAparatoCliente(instance);
					piezaApa.setIdPrd(tmpItm.getInventario().getProducto().getId());
					if (tmpItm.getInventario().getProducto().getMarca() != null)
						piezaApa.setMarca(tmpItm.getInventario().getProducto().getMarca().getNombre());
					piezaApa.setModelo(tmpItm.getInventario().getProducto().getModelo());
					piezaApa.setNombre(tmpItm.getInventario().getProducto().getNombre());
					if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie())
						piezaApa.setNumSerie(tmpItm.getCodProducto().getNumSerie());
					if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote())
						piezaApa.setNumLote(tmpItm.getCodProducto().getNumLote());
					getEntityManager().persist(piezaApa);
				
				}
			}
		}
		getEntityManager().flush();
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public Integer getApaCliId() {
		return apaCliId;
	}

	public void setApaCliId(Integer apaCliId) {
		this.apaCliId = apaCliId;
	}

	public List<AparatoCliente> getResultList() {
		return resultList;
	}

	public void setResultList(List<AparatoCliente> resultList) {
		this.resultList = resultList;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Cliente getClienteFlt() {
		return clienteFlt;
	}

	public void setClienteFlt(Cliente clienteFlt) {
		this.clienteFlt = clienteFlt;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public boolean isTieneGarantia() {
		return tieneGarantia;
	}

	public void setTieneGarantia(boolean tieneGarantia) {
		this.tieneGarantia = tieneGarantia;
	}

	public boolean isTieneGarantiaRep() {
		return tieneGarantiaRep;
	}

	public void setTieneGarantiaRep(boolean tieneGarantiaRep) {
		this.tieneGarantiaRep = tieneGarantiaRep;
	}

	public boolean isCerrarModal() {
		return cerrarModal;
	}

	public void setCerrarModal(boolean cerrarModal) {
		this.cerrarModal = cerrarModal;
	}

	public Producto getProductoAsociado() {
		return productoAsociado;
	}

	public void setProductoAsociado(Producto productoAsociado) {
		this.productoAsociado = productoAsociado;
	}


	
	
	

}
