package com.sa.kubekit.action.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jasypt.digest.StandardStringDigester;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.security.AreaNegocio;
import com.sa.model.security.Empresa;
import com.sa.model.security.Rol;
import com.sa.model.security.Sistema;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Name("usuarioHome")
@Scope(ScopeType.CONVERSATION)
public class UsuarioHome extends KubeDAO<Usuario>{

	private static final long serialVersionUID = 1L;
	private String usuarioId;
	private Empresa empresaSeleccionada;
	private List<Sucursal> sucursales = new ArrayList<Sucursal>();
	private List<AreaNegocio> areasNegocio = new ArrayList<AreaNegocio>();
	private List<Usuario> resultList = new ArrayList<Usuario>();
	private Sistema sistemaSeleccionado;
	private boolean check;
	
	@In
	private LoginUser loginUser;
	
	@In(required=false,create=true)
	@Out(required=false)
	private RolList rolList;
	
	private String estado;
	private String pass=new String();
	private String confirmacion= new String();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("usuarioHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("usuarioHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("usuarioHome_deleted")));
	}
	
	public void load(){
		try{
			//Cargamos la lista de areas de negocio
			areasNegocio = getEntityManager().createQuery("SELECT a FROM AreaNegocio a").getResultList();
			setInstance((Usuario) getEntityManager().createQuery("select u from Usuario u where u.nombreUsuario like :user")
					.setParameter("user", usuarioId).getSingleResult());
			
			if(instance.getSucursal()!=null){
				empresaSeleccionada = instance.getSucursal().getEmpresa();
				sucursales = new ArrayList<Sucursal>(empresaSeleccionada.getSucursales());
			} 			
			estado = instance.getEstado();
			
		}catch (Exception e) {
			clearInstance();
			setInstance(new Usuario());
			if(loginUser.getUser()!=null && loginUser.getUser().getSucursal()!=null){
				instance.setSucursal(loginUser.getUser().getSucursal());
			}
		}
	}

	public void cargarSucursales(){
		sucursales = new ArrayList<Sucursal>(empresaSeleccionada.getSucursales());
	}
	
	public void cargarListaUsuarios() {
		resultList = getEntityManager()
				.createQuery("SELECT u FROM Usuario u WHERE u.estado = 'ACT'")
				.getResultList();
	}
	
	public void getUsersAuthDisc(){
		resultList = getEntityManager().createQuery("SELECT u FROM Usuario u WHERE u.estado='ACT' and u.autorizDesc=true")
				.getResultList();
	}
	
	public void resetCount(){
		instance.setNumeroIntentos(0);
		modify();
		FacesMessages.instance().clear();
		FacesMessages.instance().add(sainv_messages.get("usuarioHome_modify_reset"));
	}
	
	
	public void cargarRolesDisponibles(){
		check = true;
		for(Rol rol : rolList.getResultList()){
			if(rol.getUsuarios().contains(instance)){
				rol.setAsociado(true);
			}else
				check = false;
			
		}
		
	}
	
	public String guardarAsociacion(){
		instance.setRoles(new HashSet<Rol>());
		for(Rol rol : rolList.getResultList()){
			if(rol.isAsociado()){
				instance.getRoles().add(rol);
			}
		}
		if(modify()){
			getEntityManager().refresh(instance);
			for(Rol rol : rolList.getResultList()){
				getEntityManager().refresh(rol);
			}
			return "save";
		}else{
			return "error";
		}
	}
	
	public void changeAll(){
		for(Rol rol : rolList.getResultList()){
			rol.setAsociado(check);
		}
	}
	
	/**
	 * Sirve para verificar que no seleccionen mas de un rol de un mismo sistema
	 */
	public void checkRoles(Rol chkRol) {
		/* al cliente necesita asociar mas de un rol a un usuario
		if(chkRol.isAsociado()) 
			for(Rol tmpRol : rolList.getResultList()) 
				if(!tmpRol.equals(chkRol)) 
					if(chkRol.getSistema().equals(tmpRol.getSistema()) &&
							chkRol.isAsociado() && tmpRol.isAsociado()) {
						chkRol.setAsociado(false);
						break;
					}
		*/
	}
	
	private void changePass(){
		try {
			StandardStringDigester digester = new StandardStringDigester();
			digester.setAlgorithm("SHA-1");   // optionally set the algorithm
			digester.setIterations(50000);  // increase security by performing 50000 hashing iterations
			instance.setPass(digester.digest(this.pass));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean preSave() {
		if(!pass.equals(confirmacion)){
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("usuarioHome_error_save1"));
			return false;
		}
		this.changePass();
		return true;
	}

	@Override
	public boolean preModify() {
		if(!this.pass.isEmpty()){
			if(!pass.equals(confirmacion)){
				FacesMessages.instance().add(Severity.ERROR,
						sainv_messages.get("usuarioHome_error_save1"));
				return false;
			}
			this.changePass();
		}
		instance.setEstado(estado);
		return true;
	}

	@Override
	public boolean preDelete() {
		if((instance.getMovimientos()==null || instance.getMovimientos().isEmpty()) 
				&& (instance.getPedidos()==null || instance.getPedidos().isEmpty()) 
				&& (instance.getTransferencias()==null || instance.getTransferencias().isEmpty())){
			instance.setRoles(new HashSet<Rol>());
			estado = instance.getEstado();
			if(modify()){
				FacesMessages.instance().clear();
				return true;
			}else{
				return false;
			}
		}else{
			FacesMessages.instance().add(
					sainv_messages.get("usuarioHome_error_delete1"));
			return false;
		}
	}

	@Override
	public void posSave() {
	}

	@Override
	public void posModify() {
	}

	@Override
	public void posDelete() {
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Empresa getEmpresaSeleccionada() {
		return empresaSeleccionada;
	}

	public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
		this.empresaSeleccionada = empresaSeleccionada;
	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getConfirmacion() {
		return confirmacion;
	}

	public void setConfirmacion(String confirmacion) {
		this.confirmacion = confirmacion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Sistema getSistemaSeleccionado() {
		return sistemaSeleccionado;
	}

	public void setSistemaSeleccionado(Sistema sistemaSeleccionado) {
		this.sistemaSeleccionado = sistemaSeleccionado;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public List<AreaNegocio> getAreasNegocio() {
		return areasNegocio;
	}

	public void setAreasNegocio(List<AreaNegocio> areasNegocio) {
		this.areasNegocio = areasNegocio;
	}

	public List<Usuario> getResultList() {
		return resultList;
	}

	public void setResultList(List<Usuario> resultList) {
		this.resultList = resultList;
	}

	
}
