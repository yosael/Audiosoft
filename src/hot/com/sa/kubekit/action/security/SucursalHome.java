package com.sa.kubekit.action.security;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import com.sa.kubekit.action.inventory.InventarioHome;
import com.sa.kubekit.action.medical.MedicalAppointmentDAO;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.UbicacionPrd;
import com.sa.model.security.Sucursal;

@Name("sucursalHome")
@Scope(ScopeType.CONVERSATION)
public class SucursalHome extends KubeDAO<Sucursal>{

	private static final long serialVersionUID = 1L;
	private String sucursalId;
	private String estado;
	private String nomUbicacion;
	private List<Sucursal> resultList = new ArrayList<Sucursal>();
	private List<Sucursal> sucursalesSup = new ArrayList<Sucursal>();
	private UbicacionPrd selUbicacion;
	private List<Sucursal> notBodegasSuc = new ArrayList<Sucursal>();
	private List<String> nombresBodegasList = new ArrayList<String>();
	
	
	
	@In
	private LoginUser loginUser;
	
	@In(required=false, create=true)
	private InventarioHome inventarioHome;
	
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("sucursalHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("sucursalHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("sucursalHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance((Sucursal) getEntityManager()
					.createQuery("select s from Sucursal s where s.codigo like :code")
					.setParameter("code", sucursalId)
					.getSingleResult());
			estado = instance.getEstado();
			
		}catch (Exception e) {
			clearInstance();
			setInstance(new Sucursal());
			if(loginUser.getUser().getSucursal()!=null){
				instance.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
			}
		}
		//Cargamos las sucursales superiores
		sucursalesSup = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s.sucursalSuperior = null AND s.empresa = :emp")
				.setParameter("emp", instance.getEmpresa())
				.getResultList();
		//Cargamos las surcursales que no son bodegas
		//System.out.println("Entre a cargarSuc");
		notBodegasSuc = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE (UPPER(s.nombre) NOT LIKE UPPER(:bod)) AND s.empresa = :emp")
						.setParameter("bod", "%BODEGA%")
				.setParameter("emp", instance.getEmpresa())
				.getResultList();
		//System.out.println("tamaño del result list: "+notBodegasSuc.size()); 
	}
	
	public void cargarSucursales() {
		
		//System.out.println("Entro a cargar sucursales");
		
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s ORDER BY s.codigo ASC")
				.getResultList();
		//Cargamos las surcursales que no son bodegas
		//System.out.println("Entre a cargarSuc");
		notBodegasSuc = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE (UPPER(s.nombre) NOT LIKE UPPER(:bod))")
				.setParameter("bod", "%BODEGA%")
				.getResultList();
			//System.out.println("tamaño del result list: "+notBodegasSuc.size()); 
			
			//System.out.println("Sucurusal **** "+loginUser.getUser().getSucursal().getNombre());
			
	}
	
	
	//Nuevo agregado el 22/06/2017
	public void cargarSucursalesPrincipales()
	{
		
		//System.out.println("Entro a cargar sucursales principales");
		
		try {
				
			notBodegasSuc = getEntityManager()
					.createQuery("SELECT s FROM Sucursal s where (s.bodega=true and s.sucursalSuperior=null) or (s.bodega=null or s.bodega=false and s.sucursalSuperior=null) ORDER BY s.codigo ASC")
					.getResultList();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void cargarBodegas()
	{
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s where s.bodega=TRUE ORDER BY s.codigo ASC")
				.getResultList();
	}
	
	public void cargarNombresBodegas()
	{
		nombresBodegasList = getEntityManager()
				.createQuery("SELECT s.nombre FROM Sucursal s where s.bodega=TRUE ORDER BY s.codigo ASC")
				.getResultList();
	}
	
	public void cargarSucursalesAct() {
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s WHERE s.estado = 'ACT' ORDER BY s.codigo ASC ")
				.getResultList();
	}
	
	public void guardarUbicacion(){
		//Buscamos si no existe
		for(UbicacionPrd ubiSuc : instance.getUbicacionesPrd())
			if(ubiSuc.getNombre().toUpperCase().equals(nomUbicacion)) {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("sucursalHome_error_ubiexi"));
				return;
			}
		UbicacionPrd ubi = new UbicacionPrd();
		ubi.setNombre(nomUbicacion);
		ubi.setSucursal(instance);
		try {
			getEntityManager().persist(ubi);
			getEntityManager().refresh(instance);
			FacesMessages.instance().add(Severity.INFO,
					sainv_messages.get("sucursalHome_msg_addedubi"));
		} catch(Exception ex) {
			ex.printStackTrace();
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("sucursalHome_error_addubi"));
		}
	}
	
	
	public void borrarUbicacion(UbicacionPrd ubi) {
		try {
			getEntityManager().remove(ubi);
			getEntityManager().flush();
			getEntityManager().refresh(instance);
			FacesMessages.instance().add(Severity.INFO,
					sainv_messages.get("sucursalHome_msg_delubi"));
		} catch(Exception ex) {
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("sucursalHome_error_delubi"));
		}
	}

	@Override
	public boolean preSave() {
		return true;
	}

	@Override
	public boolean preModify() {
		instance.setEstado(estado);
		return true;
	}

	@Override
	public boolean preDelete() {
		
		if((instance.getUsuarios()==null || instance.getUsuarios().isEmpty()) 
				&& (instance.getMovimientos()==null || instance.getMovimientos().isEmpty()) 
				&& (instance.getPedidos()==null || instance.getPedidos().isEmpty())
				&& (instance.getInventarios()==null || instance.getInventarios().isEmpty())				 
				&& (instance.getTransferenciasRecibidas()==null || instance.getTransferenciasRecibidas().isEmpty())){
			return true;
		}else{
			FacesMessages.instance().add(
					sainv_messages.get("sucursalHome_error_delete1"));
			return false;
		}
		
	}

	@Override
	public void posSave() {
		@SuppressWarnings("unchecked")
		List<Producto> productos = getEntityManager().createQuery("select p from Producto p where p.empresa = :empresa")
				.setParameter("empresa", instance.getEmpresa()).getResultList();
		for(Producto producto : productos){
			Inventario inventario = new Inventario();
			inventario.setCantidadActual(0);
			inventario.setProducto(producto);
			inventario.setSucursal(instance);
			inventarioHome.setInstance(inventario);
			inventarioHome.save();
		}
		getEntityManager().refresh(instance);
		getEntityManager().flush();
	}

	@Override
	public void posModify() {
		// Actualizamos las ubicaciones modificadas
		getEntityManager().refresh(instance);
		for(UbicacionPrd ubiSuc : instance.getUbicacionesPrd())
			if(ubiSuc.getNombre() != null && ubiSuc.getNombre().trim().equals("")) {
				getEntityManager().merge(ubiSuc);
				return;
			}
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public String getSucursalId() {
		return sucursalId;
	}

	public void setSucursalId(String sucursalId) {
		this.sucursalId = sucursalId;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<Sucursal> getResultList() {
		return resultList;
	}

	public void setResultList(List<Sucursal> resultList) {
		this.resultList = resultList;
	}

	public String getNomUbicacion() {
		return nomUbicacion;
	}

	public void setNomUbicacion(String nomUbicacion) {
		this.nomUbicacion = nomUbicacion;
	}

	public UbicacionPrd getSelUbicacion() {
		return selUbicacion;
	}

	public void setSelUbicacion(UbicacionPrd selUbicacion) {
		this.selUbicacion = selUbicacion;
	}

	public List<Sucursal> getSucursalesSup() {
		return sucursalesSup;
	}

	public void setSucursalesSup(List<Sucursal> sucursalesSup) {
		this.sucursalesSup = sucursalesSup;
	}

	public List<Sucursal> getNotBodegasSuc() {
		return notBodegasSuc;
	}

	public void setNotBodegasSuc(List<Sucursal> notBodegasSuc) {
		this.notBodegasSuc = notBodegasSuc;
	}

	public List<String> getNombresBodegasList() {
		return nombresBodegasList;
	}

	public void setNombresBodegasList(List<String> nombresBodegasList) {
		this.nombresBodegasList = nombresBodegasList;
	}
	
	
	
}
