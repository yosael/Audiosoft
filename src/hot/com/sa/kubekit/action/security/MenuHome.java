package com.sa.kubekit.action.security;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.security.Menu;
import com.sa.model.security.RolMenu;

@Name("menuHome")
@Scope(ScopeType.CONVERSATION)
public class MenuHome extends KubeDAO<Menu>{

	private static final long serialVersionUID = 1L;
	private Integer menuId;
	private String estado;
	private List<Menu> menusDisponibles = new ArrayList<Menu>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("menuHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("menuHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("menuHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(Menu.class, menuId));
			estado = instance.getEstado();
			if(instance.getSistema()!=null){
				this.cargarMenus();
			}
		}catch (Exception e) {
			clearInstance();
		}
	}
	
	//Obtiene los Menus superiores del sistema seleccionado
	@SuppressWarnings("unchecked")
	public void cargarMenus(){
		menusDisponibles = getEntityManager().createQuery("select m from Menu m where " +
							"m.sistema.id = :sistema and m.menuPadre is null")
							.setParameter("sistema", instance.getSistema().getId())
							.getResultList();
		if(instance.getMenuPadre()==null){
			menusDisponibles.remove(instance);
		}
	}

	@Override
	public boolean preSave() {
		if(instance.getMenuPadre()!=null && instance.getMenuPadre().getId()==instance.getId()){
			instance.setMenuPadre(null);
			FacesMessages.instance().add(
					sainv_messages.get("menuHome_error_save1"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		if(instance.getMenuPadre()!=null && instance.getMenuPadre().getId()==instance.getId()){
			instance.setMenuPadre(null);
			FacesMessages.instance().add(
					sainv_messages.get("menuHome_error_save1"));
			return false;
		}
		instance.setEstado(estado);
		return true;
	}

	@Override
	public boolean preDelete() {
		if(instance.getSubMenus()==null || instance.getSubMenus().isEmpty()){
			instance.setRoles(new ArrayList<RolMenu>());
			this.modify();
			FacesMessages.instance().clear();
			return true;
		}else{
			FacesMessages.instance().add(
					sainv_messages.get("menuHome_error_delete1"));
			return false;
		}
		
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<Menu> getMenusDisponibles() {
		return menusDisponibles;
	}

	public void setMenusDisponibles(List<Menu> menusDisponibles) {
		this.menusDisponibles = menusDisponibles;
	}

}
