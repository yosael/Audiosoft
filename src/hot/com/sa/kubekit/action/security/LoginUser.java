package com.sa.kubekit.action.security;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jasypt.digest.StandardStringDigester;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.security.Identity;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.model.security.Rol;
import com.sa.model.security.Sistema;
import com.sa.model.security.Usuario;

@Name("loginUser")
@Scope(ScopeType.SESSION)
@AutoCreate
public class LoginUser {

	@In
	private Identity identity;
	@In
	private EntityManager entityManager;
	@In
	private LocaleSelector localeSelector;

	@In(required = true)
	private KubeBundle kubekit_messages;

	@In(create = true)
	private UsuarioHome usuarioHome;
		
	private Usuario user;
	private Sistema sistema;
	private Boolean trabajosTaller=false;
	
	public void selecLanguage(String language){
		localeSelector.setLanguage(language);
		localeSelector.select();
	}
	

	public boolean authenticate() {

		try {
			user = null;
			loadUser();
			 
			StandardStringDigester digester = new StandardStringDigester();
			digester.setAlgorithm("SHA-1");   // optionally set the algorithm
			digester.setIterations(50000);  // increase security by performing 50000 hashing iterations
			
			System.out.println(digester.digest(identity.getCredentials().getPassword()) + ">>>>>");
			//System.out.println( user.getPass() + ">>>>>");
			if (digester.matches(identity.getCredentials().getPassword(), user.getPass())) {
				
				// Si la validacion de clave y usuario son exitosas se procede
				// con la validacion de que la empresa y la sucursal esten activas
				
				if(user.getNumeroIntentos()==5){
					FacesMessages.instance().add(
							kubekit_messages.get("login_user_blocked"));
					return false;
				}
				
				if(user.getEstado().equals("INA")){
					FacesMessages.instance().add(
							kubekit_messages.get("login_user_disabled"));
					return false;
				}
				
				if(user.getRoles().isEmpty()){
					FacesMessages.instance().add(
							kubekit_messages.get("login_user_no_rol"));
					return false;
				}
				
				boolean sistemasActivos = false;
				for(Rol rol : user.getRoles()){
					if(rol.getSistema().getEstado().equals("ACT")){
						sistemasActivos=true;
						sistema = rol.getSistema();
						break;
					}
				}
				if(!sistemasActivos){
					FacesMessages.instance().add(
							kubekit_messages.get("login_user_no_system"));
					return false;
				}
				
				try{
					if(user.getSucursal().getEstado().equals("INA")){
						FacesMessages.instance().add(
								kubekit_messages.get("login_branch_disabled"));
						return false;
					}
					
					if(user.getSucursal().getEmpresa().getEstado().equals("INA")){
						FacesMessages.instance().add(
								kubekit_messages.get("login_company_disabled"));
						return false;
					}
				}catch (Exception e) {
				}
				usuarioHome.getInstance().setFechaUltimoAcceso(new Date());
				usuarioHome.getInstance().setNumeroIntentos(0);
				usuarioHome.update();
				FacesMessages.instance().clear();
				
				//notificacionUserHome.notificacionesInicio();
				return true;
				
			} else {
				System.out.println("### = Linea 118");
				usuarioHome.getInstance().setNumeroIntentos(usuarioHome.getInstance().getNumeroIntentos()+1);
				usuarioHome.update();
				FacesMessages.instance().clear();
				if(usuarioHome.getInstance().getNumeroIntentos() >= 5)
					FacesMessages.instance().add(kubekit_messages.get("login_password_err_locked"));
				else
					FacesMessages.instance().add(kubekit_messages.get("login_password_error"));
			
				return false;
			}

		} catch (NoResultException ne) {
			System.out.println("### = nre");
			ne.printStackTrace();
		} catch (Exception e) {
			System.out.println("### = exception");
			e.printStackTrace();
		}
		return false;
	}

	public String startSession() {
		
		if (identity.login() != null) {
			
			for(Rol rol : user.getRoles()){
				if(rol.getSistema().getEstado().equals("ACT")){
					identity.addRole(rol.getNombre());
				}
			}
			return "login";
		}
		return null;
	}

	public String endSession() {
		identity.logout();
		user = null;
		return "end";
	}	

	public Usuario loadUser() {
		try{
			user = (Usuario) entityManager.createQuery(
					"select u from Usuario u where u.nombreUsuario like :name")
					.setParameter("name", identity.getCredentials().getUsername())
					.getSingleResult();
			usuarioHome.setEnableMessages(false);
			usuarioHome.select(user);
			
			return user;
		}catch (Exception e) {
			FacesMessages.instance().add(
					kubekit_messages.get("login_user_not_found"));
			return null;
		}
				
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}


	public Sistema getSistema() {
		return sistema;
	}


	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}


	public Boolean getTrabajosTaller() {
		return trabajosTaller;
	}


	public void setTrabajosTaller(Boolean trabajosTaller) {
		this.trabajosTaller = trabajosTaller;
	}
	
	
	
}