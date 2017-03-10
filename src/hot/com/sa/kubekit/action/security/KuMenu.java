package com.sa.kubekit.action.security;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.security.Identity;
import org.richfaces.component.html.HtmlDropDownMenu;
import org.richfaces.component.html.HtmlMenuItem;
import org.richfaces.component.html.HtmlToolBarGroup;

import com.sa.model.security.Menu;
import com.sa.model.security.Rol;
import com.sa.model.security.RolMenu;
import com.sa.model.security.Usuario;

//se deben agregar las librerias de richfaces para que funcione

@Name("kuMenu")
@Scope(ScopeType.EVENT)
@AutoCreate
public class KuMenu {

	private HtmlToolBarGroup kubeToolBar = new HtmlToolBarGroup();
	private List<Menu> menusUsuario = new ArrayList<Menu>();
	
	@In
	private LocaleSelector localeSelector;

	@Create
	public void buildMenu() {
		Usuario usuario = loadUser();
		if (usuario == null){
			return;
		}
		this.menusUsuario = new ArrayList<Menu>();
		for(Rol rol : usuario.getRoles()){
			for(RolMenu menu : rol.getMenus())
				this.menusUsuario.add(menu.getMenu());
		}
		//System.out.println("creando el menu");
		kubeToolBar = new HtmlToolBarGroup();
		
		List<Menu> parents = ((MenuParentList) Component
				.getInstance(MenuParentList.class)).getResultList();

		boolean continuar = false;
		
		for (Menu mod : parents) {
			if(mod.isVisible() && mod.getEstado().equals("ACT")){
				for(Menu menu : this.menusUsuario){
					if(menu.getMenuPadre().getId()==mod.getId()){
						continuar = true;
						break;
					}
				}
				if(continuar){
					//System.out.println("ESTE ES UN MENU PADRE: " + mod.getNombre());
					HtmlDropDownMenu menuModule = createMenuModule(mod);
					if (menuModule != null && menuModule.getChildCount() > 0) {
						kubeToolBar.getChildren().add(menuModule);
					}
				}
				continuar = false;
			}
		}
	}

	public Usuario loadUser() {
		try {
			EntityManager entityManager = (EntityManager) Component
					.getInstance("entityManager");
			Usuario user = (Usuario) entityManager.createQuery(
					"select u from Usuario u where u.nombreUsuario like :name")
					.setParameter("name",
							Identity.instance().getCredentials().getUsername())
					.getSingleResult();
			return user;
		} catch (Exception e) {
			return null;
		}
	}

	// carga los permisos que tengan vistas
	private HtmlDropDownMenu createMenuModule(Menu mod) {
		//System.out.println("Creando menu --");
		HtmlDropDownMenu menuParent = new HtmlDropDownMenu();
		menuParent.setDirection("bottom-right"); 

		HtmlPanelGroup group = new HtmlPanelGroup();

		HtmlOutputText text = new HtmlOutputText();
		if(localeSelector.getLanguage().equals("es")){
			text.setValue(mod.getNombreSpanish());
		}else{
			text.setValue(mod.getNombreEnglish());
		}
		
		//System.out.println("ASIGNO EL TEXTO");
		group.getChildren().add(text);
		menuParent.getFacets().put("label", group);

		List<HtmlMenuItem> menus = new ArrayList<HtmlMenuItem>(0);
		
		if (!mod.getSubMenus().isEmpty()) {
			for (Menu modRecursive : mod.getSubMenus()) {
				//System.out.println("VA A CREAR EL MENU HIJO: "+ modRecursive.getNombre());
				if(this.menusUsuario.contains(modRecursive)){
					if(modRecursive.isVisible() && modRecursive.getEstado().equals("ACT")){
						menus.add(createMenuItem(modRecursive));
						menuParent.getChildren().addAll(menus);
					}
				}
			}
		} else {
			menuList(mod, true);
			menuParent.getChildren().addAll(menus);
		}

		return menuParent;
	}

	private void menuList(Menu module, boolean mode) {
		if (mode) {
			if (!module.getSubMenus().isEmpty()) {
				for (Menu child : module.getSubMenus()) {
					menuList(child, true);
				}
			}
		}
	}

	private HtmlMenuItem createMenuItem(Menu menu) {
		HtmlMenuItem item = new HtmlMenuItem();
		item.setSubmitMode("ajax");
		//item.setOnclick("document.getElementById('fMenu:opm"+menu.getId()+"').click()");
		
		org.jboss.seam.ui.component.html.HtmlLink link = new org.jboss.seam.ui.component.html.HtmlLink();
		link.setId("opm" + menu.getId());
		
		if(localeSelector.getLanguage().equals("es")){
			link.setValue(menu.getNombreSpanish());
		}else{
			link.setValue(menu.getNombreEnglish());
		}
		
		link.setView(menu.getUrl());
		item.getChildren().add(link);

		return item;
	}

	public HtmlToolBarGroup getKubeToolBar() {
		return kubeToolBar;
	}

	public void setKubeToolBar(HtmlToolBarGroup kubeToolBar) {
		this.kubeToolBar = kubeToolBar;
	}

}