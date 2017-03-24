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

import com.sa.kubekit.action.inventory.CompraHome;
import com.sa.kubekit.action.inventory.MovimientoHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Compra;
import com.sa.model.inventory.DetalleReparacionExterna;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.Proveedor;
import com.sa.model.inventory.ReparacionExterna;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.AparatoCliente;
import com.sa.model.workshop.PiezaAparatoCliente;
import com.sa.model.workshop.ReparacionCliente;

@Name("reparacionExternaHome")
@Scope(ScopeType.CONVERSATION)
public class ReparacionExternaHome extends KubeDAO<ReparacionExterna> {
	
	
	private static final long serialVersionUID = 1L;
	private List<ReparacionExterna> resultList;
	private int idReparacionExterna;
	private List<DetalleReparacionExterna> detalleReparacion= new ArrayList<DetalleReparacionExterna>();
	private int indiceDetalle;
	private int contadorDetalle=0;
	private List<CodProducto> listaCodigos;
	private DetalleReparacionExterna nuevoDetalle;
	private List<DetalleReparacionExterna> listaItemsIngreso = new ArrayList<DetalleReparacionExterna>();
	private String numFacturaCompra="";
	private List<Item> listaItemsMovimiento = new ArrayList<Item>();
	private String siguienteEstado="";
	private boolean cambioLocal=false;
	private CodProducto nuevoCodigo;
	private CodProducto codigoSelectedEntrega;

	@In
	private LoginUser loginUser;
	
	@In(required = false, create = true)
	@Out
	private DetalleReparacionExternaHome detalleReparacionExternaHome;
	
	@In(required = false, create = true)
	@Out
	private CompraHome compraHome;
	
	@In(required = false, create = true)
	@Out
	private MovimientoHome movimientoHome;
	
	public void load()
	{
		
		try{
			
			setInstance((ReparacionExterna) getEntityManager().createQuery("select r from ReparacionExterna r where r.idReparacionExterna=:idReparacion")
					.setParameter("idReparacion", idReparacionExterna).getSingleResult());
			setDetalleReparacion(instance.getDetalleReparacion());
			
			System.out.println("Estado reparacion externa"+instance.getEstado());
			System.out.println("Curtomer number"+instance.getCustomerNumber());
			
			/*if(instance.getEstado().equals("Generada") && getDetalleReparacion().size()>0)
			{
				System.out.println("Entro a condicion detalle");
				for(DetalleReparacionExterna det:instance.getDetalleReparacion())
				{
					System.out.println("Entro al for");
					if(det.getPiezaReparacion()==null)//El movimiento tomara los productos del inventario donde se este agregando el item.
					{
						System.out.println("Entro a la primera condicion");
						movimientoHome.agregarProducto(cargarInventarioEnvio(det.getAparato())); //Si la pieza es null, significa que lo que se qiere reparar es el aparato
					}
					else 
					{
						System.out.println("Entro a la segunda condicion");
						movimientoHome.agregarProducto(cargarInventarioEnvio(det.getPiezaReparacion())); // Si la pieza no es null entonces lo que se va a reparar es la pieza.
					}
				}
			}*/
			
			System.out.println("Curtomer number2"+instance.getCustomerNumber());
			
		}catch (Exception e) {
			
			System.out.println("Entro a catch load");
			System.out.println(e.getLocalizedMessage());
			System.out.println(e.getCause());
			
			//clearInstance();
			setInstance(new ReparacionExterna());
			
			//Extraer empresa audiomed
			Empresa empresa = new Empresa();
			empresa = (Empresa) getEntityManager().createQuery("SELECT e FROM Empresa e where e.id=:idEmpresa").setParameter("idEmpresa", 2).getSingleResult();
			instance.setEmpresa(empresa);
		}
	}
	
	public void cargarReparaciones()
	{
		resultList = new ArrayList<ReparacionExterna>();
		resultList = getEntityManager().createQuery("SELECT r FROM ReparacionExterna r").getResultList();
	}
	
	public void agregarAparato(Producto aparato)
	{
		
		/*DetalleReparacionExterna detalle = new DetalleReparacionExterna();
		
		detalle.setAparato(aparato);
		detalle.setReparacionExterna(instance);
		detalle.setEstado("GEN");
		detalle.setFechaModificacion(new Date());
		
		detalleReparacion.add(detalle);*/
		
		nuevoDetalle.setAparato(aparato);
		System.out.println("Asigno el aparato");
	}
	
	public void agregarPieza(Producto pieza)
	{
		
		/*detalleReparacion.get(indiceDetalle).setPiezaReparacion(pieza);
		detalleReparacion.get(indiceDetalle).setFechaModificacion(new Date());
		
		for(DetalleReparacionExterna det: detalleReparacion)
		{
			
			if(detalleSelected.equals(det))
			{
				det.setPiezaReparacion(pieza);
				det.setFechaModificacion(new Date());
			}
		}*/
		
		nuevoDetalle.setPiezaReparacion(pieza);
	}
	
	
	public void seleccionarProveedor(Proveedor proveedor)
	{	
		instance.setProveedor(proveedor);
	}
	
	public void cargarCodigosProducto(Producto aparato)
	{
		
		
		System.out.println("Nombre producto"+aparato.getNombre());
		listaCodigos = new ArrayList<CodProducto>();
		
		//listaCodigos = getEntityManager().createQuery("SELECT c FROM CodProducto c where ")
		Inventario inv = (Inventario) getEntityManager()
				.createQuery(
						"SELECT i FROM Inventario i WHERE i.sucursal = :suc AND i.producto = :prd")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("prd", aparato).getSingleResult();

		// Sacamos el inventario del producto
		listaCodigos = (ArrayList<CodProducto>) getEntityManager()
				.createQuery(
						"SELECT c FROM CodProducto c "
								+ "	WHERE c.inventario.producto = :prd AND c.inventario = :inv AND c.estado = 'ACT' ")
				.setParameter("prd", aparato)
				.setParameter("inv", inv).getResultList();
		
		
	}
	
	public void seleccionarCodigo(CodProducto codigo)
	{
		nuevoDetalle.setCodigo(codigo);
	}
	
	public void seleccionarCodigoNuevo(CodProducto codigo)
	{
		nuevoCodigo=codigo;
		
	}
	
	public void agregarDetalle() //NOTA: La validacion que realiza el movimiento es que no se debe repetir el items 2 veeces, es posible que esta validacion no sea requerida para la reparacion
	{
		nuevoDetalle.setFechaModificacion(new Date());
		nuevoDetalle.setEstado("Generada");
		detalleReparacion.add(nuevoDetalle);
		
		/*if(nuevoDetalle.getPiezaReparacion()==null)//El movimiento tomara los productos del inventario donde se este agregando el item.
		{
			movimientoHome.agregarProducto(cargarInventarioEnvio(nuevoDetalle.getAparato())); //Si la pieza es null, significa que lo que se qiere reparar es el aparato
		}
		else 
		{
			movimientoHome.agregarProducto(cargarInventarioEnvio(nuevoDetalle.getPiezaReparacion())); // Si la pieza no es null entonces lo que se va a reparar es la pieza.
		}*/
	}
	
	public void modificarDetalleIngresada()///
	{
		for(DetalleReparacionExterna det: detalleReparacion)
		{
			
			if(det.getIdDetalleRep()==null)//Si el detalle no esta registrado se registra
			{
				
				det.setReparacionExterna(instance);
				det.setEstado("Generada");
				detalleReparacionExternaHome.setInstance(det);
				detalleReparacionExternaHome.save();
				System.out.println("Entro a registrar nuevo detalle");
				
			}
			
			/*if(instance.getEstado().equals("Ingresado"))
			{
				det.setEstado("Enviado");
				det.setFechaModificacion(new Date());
				detalleReparacionExternaHome.modify();
				System.out.println("Entro a actualizar el detalle a enviado");
			}*/

		}
	}
	

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		
		System.out.println("Presave");
		
		
		if(instance.getProveedor()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingresar el proveedor");
			return false;
		}
		
		if(instance.getCustomerNumber()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingresar el numero de cliente");
			return false;
		}
		
		List<ReparacionExterna> reparacionExistente = new ArrayList<ReparacionExterna>();
		reparacionExistente = getEntityManager().createQuery("SELECT r FROM ReparacionExterna r Where r.estado='Generada'").getResultList();
		
		if(reparacionExistente!=null && reparacionExistente.size()>0)
		{
			FacesMessages.instance().add(Severity.WARN,"Ya existe una reparacion generada");
			FacesMessages.instance().add(Severity.WARN,"Favor enviar o cambiar estado de la generada");
			return false;
		}
		
		
		instance.setFechaCreacion(new Date());
		instance.setFechaModificacion(new Date());
		
		
		//instance.setDetalleReparacion(detalleReparacion);
		
		return true;
	}
	
	public void cargarNuevoDetalle()
	{
		nuevoDetalle = new DetalleReparacionExterna();
	}
	
	public void removerDetalle(DetalleReparacionExterna detalle)
	{
		if(detalle.getIdDetalleRep()==null)
		{
			detalleReparacion.remove(detalle);
			System.out.println("Elimino de la memoria");
		}
		else
		{
			detalleReparacion.remove(detalle);
			getEntityManager().remove(detalle);
			System.out.println("Elimino de la db");
		}
	}
	
	
	public void enviarReparacion()
	{
		instance.setEstado("Enviada");
		instance.setFechaEnvio(new Date());
		System.out.println("Entro a enviar a reparacion");
		
		modify();
		
		modificarDetalles();
		
		//Crear movimiento de salida y reducir inventarios. 
		/*Movimiento movimiento = new Movimiento();
		
		movimiento.setObservacion("Envio a reparacion externa");
		movimiento.setTipoMovimiento("S");
		movimiento.setRazon("RE");
		movimiento.setSucursal(loginUser.getUser().getSucursal());
		movimiento.setFecha(new Date());
		movimiento.setUsuario(loginUser.getUser());
		
		movimientoHome.setInstance(movimiento);
		movimientoHome.save();*/
		
		salidaCodigo();
		
		
		System.out.println("Finalizo envio");
		
		
	}
	
	public void agregarDetallesMovimiento(Producto producto)
	{
		
		movimientoHome.agregarProducto(cargarInventarioEnvio(producto)); //Si la pieza es null, significa que lo que se qiere reparar es el aparato
	}
	
	public void agregarMovimiento(String tipo)
	{
		//Crear movimiento de salida y reducir inventarios. 
		Movimiento movimiento = new Movimiento();
		
		movimiento.setObservacion("Reparacion externa");
		movimiento.setTipoMovimiento(tipo);//E,S
		movimiento.setRazon("RE");//Razon: Reparacion
		movimiento.setSucursal(loginUser.getUser().getSucursal());
		movimiento.setFecha(new Date());
		movimiento.setUsuario(loginUser.getUser());
		
		movimientoHome.setInstance(movimiento);
		movimientoHome.save();
	}
	
	public void modificarDetalles()//Actualizar el estado de los detalles cuando se envia
	{
		for(DetalleReparacionExterna det: detalleReparacion)
		{
			
			if(det.getIdDetalleRep()==null)//Si el detalle no esta registrado se registra
			{
				
				det.setReparacionExterna(instance);
				det.setEstado("Enviado");
				detalleReparacionExternaHome.setInstance(det);
				detalleReparacionExternaHome.save();
				System.out.println("Entro a registrar nuevo detalle");
				
			}
			
			if(instance.getEstado().equals("Enviada"))//Revisar si peligra q ingrese dos vees
			{
				det.setEstado("Enviado");
				det.setFechaModificacion(new Date());
				detalleReparacionExternaHome.modify();
				System.out.println("Entro a actualizar el detalle a enviado");
			}

		}
		
		System.out.println("Entro a modificar detalles");
	}
	
	public void agregarCompra(DetalleReparacionExterna item)
	{
		System.out.println("id aparatp"+item.getAparato().getId());
		if(listaItemsIngreso.contains(item))
		{
			FacesMessages.instance().add(Severity.WARN,"El item ya fue seleccionado");
			return;
		}
		
		listaItemsIngreso.add(item);
	}
	
	public void desecharItem(DetalleReparacionExterna item)
	{
		item.setEstado("Desechado");
		
		detalleReparacionExternaHome.setInstance(item);
		detalleReparacionExternaHome.modify();
	}
	
	
	public void finalizarReingreso()
	{
		
		if(numFacturaCompra.equals(""))
		{
			FacesMessages.instance().add(Severity.WARN,"Especifique identificador para ingreso-compra");
			return;
		}
		
		
		//->Llenar la lista tipo items para ingresar compra
		
		for(DetalleReparacionExterna item: listaItemsIngreso)
		{
			
			Item itemCompra = new Item();
			itemCompra.setCantidad(1);
			itemCompra.setItemId(new ItemId());
			
			if(item.getPiezaReparacion()==null)
			{
				
				itemCompra.setCostoUnitario(item.getAparato().getCosto());
				itemCompra.setInventario(cargarInventarioProducto(item.getAparato()));
				itemCompra.getItemId().setInventarioId(cargarInventarioProducto(item.getAparato()).getId());
				
				//->Validar si requiere ingreso de nuevo codigo
				if(item.getNuevoCodigo().getNumSerie()!=null && !item.getNuevoCodigo().getNumSerie().equals("")) 
				{
					System.out.println("entro al codigo nuevo");
					itemCompra.setCodProducto(item.getNuevoCodigo());
					System.out.println("Nuevo codigo"+itemCompra.getCodProducto().getNumSerie());
					
					compraHome.cargarListaCodigosNuevo(itemCompra,item.getNuevoCodigo());
				}
				else
				{
					System.out.println("Serie actual"+item.getCodigo().getNumSerie());
					System.out.println("Inventario"+item.getCodigo().getInventario().getId());
					System.out.println("Movimiento"+item.getCodigo().getMovimiento().getId());
					itemCompra.setCodProducto(item.getCodigo());
					//itemCompra.setMovimiento(item.getCodigo().getMovimiento());
					
					
					//compraHome.cargarListaCodigos(itemCompra);
					compraHome.cargarListaCodigosNuevo(itemCompra, item.getCodigo());
					
				}
			
				if(item.getReparacionCliente()!=null)
				{
					if(item.getReparacionCliente().getEstadoRepExterna().equals("ENV"))
					{
						modificarEstadoReparacionCli(item.getReparacionCliente(),"RCA");//El item devuelto en la reparacion sera un aparato
					}
				}
			}
			else
			{
				if(item.getReparacionCliente()!=null)
				{
					if(item.getReparacionCliente().getEstadoRepExterna().equals("ENV"))
					{
						modificarEstadoReparacionCli(item.getReparacionCliente(),"RCP");//El item devuelto en la reparacion sera una pieza
					}
				}
				
				itemCompra.setCostoUnitario(item.getPiezaReparacion().getCosto());
				itemCompra.setInventario(cargarInventarioProducto(item.getPiezaReparacion()));
				itemCompra.getItemId().setInventarioId(cargarInventarioProducto(item.getPiezaReparacion()).getId());
			}
			
			
			
			
			
			listaItemsMovimiento.add(itemCompra);
			
		}
		
		compraHome.setInstance(new Compra());
		compraHome.getInstance().setFecha(new Date());
		compraHome.getInstance().setNumeroFactura(numFacturaCompra);
		compraHome.getInstance().setSucursal(obtenerSucursalPrincipal());
		compraHome.getInstance().setFormaPago("Efectivo");
		compraHome.getInstance().setTipoMovimiento("E");
		compraHome.getInstance().setRazon("C");
		compraHome.setItemsAgregados(listaItemsMovimiento);
		compraHome.actualizarSubtotal();
		
		
		if(!compraHome.save())
		{
			System.out.println("No se pudo guardar la compra");
			return;
		}
		else
		{
			System.out.println("Se guardo la compra");
		}
		
		
		/*int enviados=0;
		for(DetalleReparacionExterna det: detalleReparacion)
		{
			if(det.getEstado().equals("Enviado"))
				enviados++;
			else if(det.getEstado().equals("Re-ingresado"))
				reingresados++;
			
		}
		
		int reingresados=0;*/
		for(DetalleReparacionExterna item: listaItemsIngreso)
		{
			item.setEstado("Re-ingresado");
			item.setFechaRecibido(new Date());
			item.setFechaModificacion(new Date());
			
			if(item.getNuevoCodigo()!=null)
			{
				item.setIdNuevoCodigo(item.getNuevoCodigo().getId());
			}
			
			detalleReparacionExternaHome.setInstance(item);
			detalleReparacionExternaHome.modify();
			
		}
		
		//modify();
		
		
		/*if(enviados<reingresados)
		{
			instance.setEstado("Recibiendo");
		}
		else
		{
			instance.setEstado("Recibido");
		}*/
		
		instance.setEstado(verificarEstadoPostEnvio());
		
		modify();
		
		
		listaItemsIngreso.clear();
		
		//->Ingresar primero la compra
		
		//->Registrar los items
		
		
	}
	
	public String verificarEstadoPostEnvio()
	{
		
		boolean enviados=false;
		boolean recibidos=false;
		
		List<DetalleReparacionExterna> detalle=getEntityManager().createQuery("SELECT d FROM DetalleReparacionExterna d where d.reparacionExterna.idReparacionExterna=:idRep").setParameter("idRep", instance.getIdReparacionExterna()).getResultList();
		for(DetalleReparacionExterna det:detalle)
		{
			
			if(det.getEstado().equals("Enviado"))
				enviados=true;
			else if(det.getEstado().equals("Re-ingresado") || det.getEstado().equals("Desechado"))
				recibidos=true;
			
		}
		
		
		if(enviados==false && recibidos==true)
			return "Recibido";
		else
			return "Recibiendo";
			
		
	}
	
	public void salidaCodigo()
	{
		
		for(DetalleReparacionExterna det: getDetalleReparacion())
		{
			
			if(det.getPiezaReparacion()==null)
			{
				System.out.println("Entro a cambiar codigo*********");
				det.getCodigo().setEstado("INA");
				getEntityManager().merge(det.getCodigo());
				getEntityManager().flush();
			}
			
		}
		
		System.out.println("Finalizo salida codigo");
	}
	
	public Sucursal obtenerSucursalPrincipal()
	{
		return (Sucursal)getEntityManager().createQuery("SELECT s FROM Sucursal s where s.id=103").getSingleResult();
	}
	
	
	public Inventario cargarInventarioProducto(Producto producto)
	{
		Inventario inv = (Inventario) getEntityManager()
				.createQuery(
						"SELECT i FROM Inventario i WHERE i.sucursal = :suc AND i.producto = :prd")
				.setParameter("suc", obtenerSucursalPrincipal())
				.setParameter("prd", producto).getSingleResult();
		
		return inv;
	}
	
	
	public Inventario cargarInventarioEnvio(Producto producto) //Al existir la sucursal en el campo del detalle, sera necesario enviar como parametro la sucursal. 
	{
		
		System.out.println("Sucursal Usuario"+loginUser.getUser().getSucursal().getNombre());
		
		Inventario inv = (Inventario) getEntityManager()
				.createQuery(
						"SELECT i FROM Inventario i WHERE i.sucursal = :suc AND i.producto = :prd")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("prd", producto).getSingleResult();
		
		return inv;
	}
	
	public ReparacionExterna cargarReparacionGenerada()
	{
		List<ReparacionExterna> result = new ArrayList<ReparacionExterna>();
		result = getEntityManager().createQuery("SELECT r FROM ReparacionExterna r where r.estado='Generada'").getResultList();
		if(result.size()>0 && result!=null)
			return result.get(0);
		else
			return null;
	}
	
	public void cargarReparacionDesdeTaller(ReparacionCliente reparacion)
	{
		

		nuevoDetalle = new DetalleReparacionExterna();
		
		Producto aparato = cargarAparato(reparacion.getAparatoRep().getIdPrd());
		CodProducto codigo = cargarCodigo(reparacion.getAparatoRep().getNumSerie());
		
		nuevoDetalle.setReparacionCliente(reparacion);
		nuevoDetalle.setAparato(cargarAparato(reparacion.getAparatoRep().getIdPrd()));
		nuevoDetalle.setCodigo(codigo);
		
				
		
	}
	
	public void guardarDetalleTaller()
	{
		
		nuevoDetalle.setFechaModificacion(new Date());
		nuevoDetalle.setEstado("Generada");
		nuevoDetalle.setReparacionExterna(cargarReparacionGenerada());
		
		
	}
	
	public Producto cargarAparato(int  idProducto)
	{
		Producto producto = new Producto();
		try {
			
			producto = (Producto) getEntityManager().createQuery("SELECT p FROM Producto p where p.id=:idProducto").setParameter("idProducto", idProducto).getSingleResult();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Ocurrio un problema");
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema, intente mas tarde");
			return producto;
		}
		
		return producto;
	}
	
	public CodProducto cargarCodigo(String numero)
	{
		CodProducto codigo = new CodProducto();
		try {
			
			codigo = (CodProducto) getEntityManager().createQuery("SELECT p FROM CodProducto p where p.numSerie=:numero").setParameter("numero", numero).getSingleResult();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Ocurrio un problema");
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema, intente mas tarde");
			return codigo;
		}
		
		return codigo;
	}
	
	
	public void desabilitarAparatoCliente(AparatoCliente aparato)
	{
		aparato.setEstado("INA");
		aparato.setActivo(false);
		
		getEntityManager().merge(aparato);
	}
	
	public AparatoCliente registrarNuevoAparato(AparatoCliente aparato,CodProducto codigo)
	{
		AparatoCliente apaCli = new AparatoCliente();
		apaCli.setCliente(aparato.getCliente());
		apaCli.setFechaAdquisicion(new Date());
		apaCli.setLadoAparato(aparato.getLadoAparato());
		apaCli.setMarca(aparato.getMarca());
		apaCli.setModelo(aparato.getModelo());
		apaCli.setNombre(aparato.getNombre());
		apaCli.setNumSerie(codigo.getNumSerie());
		apaCli.setActivo(true);
		apaCli.setEstado("ACT");
		
		getEntityManager().persist(apaCli);
		
		codigo.setEstado("USD");
		getEntityManager().merge(codigo);
		
		return apaCli;
	}
	
	public void agregarReparacionExternaTaller()//NOTA:Agregar nuevo campo en reparacionCliente que indique si se resolvio o no localmente la reparacion del item pendiente
	{
		
		if(cambioLocal)
		{
			
			if(nuevoDetalle.getPiezaReparacion()==null)
			{
				//Se debe abilitar la opcion para seleccionar nuevo numero de serie
				//Verificar existencia de aparato y # serie en bodega taller
				agregarDetallesMovimiento(nuevoDetalle.getAparato());
				agregarMovimiento("S");
				//Desabilitar aparato viejo del cliente
				desabilitarAparatoCliente(nuevoDetalle.getReparacionCliente().getAparatoRep());
				//Registrar aparato nuevo.
				AparatoCliente nuevoAparato = new AparatoCliente();
				nuevoAparato = registrarNuevoAparato(nuevoDetalle.getReparacionCliente().getAparatoRep(), nuevoCodigo);
				//Sustituir aparato nuevo en reparacion
				sustituirAparato(nuevoDetalle.getReparacionCliente(),nuevoAparato);
				//Registrar detalle de reparacion externa
				
				nuevoDetalle.setFechaModificacion(new Date());
				nuevoDetalle.setEstado("Generada");
				nuevoDetalle.setReparacionExterna(cargarReparacionGenerada());
				detalleReparacionExternaHome.setInstance(nuevoDetalle);
				detalleReparacionExternaHome.save();
				
				//Indicar en detalle de la reparacion que el problema fue resuelto
				
			}
			else
			{
				//Verificar existencia de pieza en bodega de taller
				//Reducir inventario de pieza
				agregarDetallesMovimiento(nuevoDetalle.getPiezaReparacion());
				agregarMovimiento("S");
				
				//Registrar envio de reparacion externa
				nuevoDetalle.setFechaModificacion(new Date());
				nuevoDetalle.setEstado("Generada");
				nuevoDetalle.setReparacionExterna(cargarReparacionGenerada());
				detalleReparacionExternaHome.setInstance(nuevoDetalle);
				detalleReparacionExternaHome.save();
			}
			
		}
		else
		{
						
			//Dejar estado de reparacion en espera
			//Registrar detalle de reparacion externa .....Poner try catch
			
			modificarEstadoReparacionCli(nuevoDetalle.getReparacionCliente(),"ENV");
			
			nuevoDetalle.setFechaModificacion(new Date());
			nuevoDetalle.setEstado("Generada");
			nuevoDetalle.setReparacionExterna(cargarReparacionGenerada());
			detalleReparacionExternaHome.setInstance(nuevoDetalle);
			detalleReparacionExternaHome.save();
			
		}
		
		
	}
	
	public void mostrarValorRadio()
	{
		System.out.println("VAlor *****"+cambioLocal);
	}
	
	public void modificarEstadoReparacionCli(ReparacionCliente reparacion,String estado)
	{
		reparacion.setEstadoRepExterna(estado);
		getEntityManager().merge(reparacion);
	}
	
	public void sustituirAparato(ReparacionCliente reparacion,AparatoCliente aparato)
	{
		reparacion.setAparatoRep(aparato);
		getEntityManager().merge(reparacion);
	}
	
	
	public void cargarReparacionRecibida(DetalleReparacionExterna detalleRep)
	{
		//Verificar si es pieza o aparato
		
		/*Producto pieza = new Producto();
		pieza= nuevoDetalle.getPiezaReparacion();
		*/
		
		if(detalleRep.getPiezaReparacion()==null)
		{
			
			//Sustituir aparato y reducir inventario de item y #Serie;
			
			//Registrar aparato cliente
			AparatoCliente aparatoNuevo = new AparatoCliente();
			CodProducto codigoAparato = new CodProducto();
			if(detalleRep.getIdNuevoCodigo()==null)
			{
				codigoAparato=detalleRep.getCodigo();
			}
			else
			{
				codigoAparato=buscarCodById(detalleRep.getIdNuevoCodigo());
			}
			detalleRep.setPiezaReparacion(null);
			aparatoNuevo=registrarNuevoAparato(detalleRep.getReparacionCliente().getAparatoRep(),codigoAparato);
			
			//Desabilitar aparato viejo
			desabilitarAparatoCliente(detalleRep.getReparacionCliente().getAparatoRep());
			
			//Sustituir nuevo aparato en reparacion
			sustituirAparato(detalleRep.getReparacionCliente(),aparatoNuevo);
			
			//Crear el modivmiento y reducir inventario
			agregarDetallesMovimiento(detalleRep.getAparato());
			
			modificarEstadoReparacionCli(detalleRep.getReparacionCliente(), "FIN");
			
			agregarMovimiento("S");
			
		}
		else
		{
			//Crear el modivmiento y reducir inventario
			agregarDetallesMovimiento(detalleRep.getPiezaReparacion());
			
			modificarEstadoReparacionCli(detalleRep.getReparacionCliente(), "FIN");
			
			agregarMovimiento("S");
			
			
		}
	}
	
	public CodProducto buscarCodById(int idCodigo)
	{
		return (CodProducto) getEntityManager().createQuery("SELECT c FROM CodProducto c where c.id=:idCodigo").setParameter("idCodigo", idCodigo).getSingleResult();
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
		for(DetalleReparacionExterna det: detalleReparacion)
		{
			det.setReparacionExterna(instance);
			
			detalleReparacionExternaHome.setInstance(det);
			detalleReparacionExternaHome.save();

		}
		
		System.out.println("Entro a possave");
	}	

	@Override
	public void posModify() {
		
		
		if(instance.getEstado().equals("Generada"))
		{
			modificarDetalleIngresada();
			System.out.println("Entro a modificar detalles");
		}
		
		System.out.println("Entro a posModify");
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<DetalleReparacionExterna> getDetalleReparacion() {
		return detalleReparacion;
	}

	public void setDetalleReparacion(
			List<DetalleReparacionExterna> detalleReparacion) {
		this.detalleReparacion = detalleReparacion;
	}

	public int getIndiceDetalle() {
		return indiceDetalle;
	}

	public void setIndiceDetalle(int indiceDetalle) {
		System.out.println("Indice Actual"+indiceDetalle);
		this.indiceDetalle = indiceDetalle;
	}

	public int getContadorDetalle() {
		return contadorDetalle;
	}

	public void setContadorDetalle(int contadorDetalle) {
		this.contadorDetalle = contadorDetalle;
		this.contadorDetalle++;
		System.out.println("Contador detalle"+this.contadorDetalle);
		
	}

	public List<ReparacionExterna> getResultList() {
		return resultList;
	}

	public void setResultList(List<ReparacionExterna> resultList) {
		this.resultList = resultList;
	}

	public int getIdReparacionExterna() {
		return idReparacionExterna;
	}

	public void setIdReparacionExterna(int idReparacionExterna) {
		this.idReparacionExterna = idReparacionExterna;
	}

	public DetalleReparacionExterna getNuevoDetalle() {
		return nuevoDetalle;
	}

	public void setNuevoDetalle(DetalleReparacionExterna nuevoDetalle) {
		this.nuevoDetalle = nuevoDetalle;
	}

	public List<CodProducto> getListaCodigos() {
		return listaCodigos;
	}

	public void setListaCodigos(List<CodProducto> listaCodigos) {
		this.listaCodigos = listaCodigos;
	}

	public String getNumFacturaCompra() {
		return numFacturaCompra;
	}

	public void setNumFacturaCompra(String numFacturaCompra) {
		this.numFacturaCompra = numFacturaCompra;
	}

	public List<DetalleReparacionExterna> getListaItemsIngreso() {
		return listaItemsIngreso;
	}

	public void setListaItemsIngreso(
			List<DetalleReparacionExterna> listaItemsIngreso) {
		this.listaItemsIngreso = listaItemsIngreso;
	}

	public String getSiguienteEstado() {
		return siguienteEstado;
	}

	public void setSiguienteEstado(String siguienteEstado) {
		this.siguienteEstado = siguienteEstado;
	}

	public boolean isCambioLocal() {
		return cambioLocal;
	}

	public void setCambioLocal(boolean cambioLocal) {
		this.cambioLocal = cambioLocal;
	}

	public CodProducto getNuevoCodigo() {
		return nuevoCodigo;
	}

	public void setNuevoCodigo(CodProducto nuevoCodigo) {
		this.nuevoCodigo = nuevoCodigo;
	}

	
	
	
	

}
