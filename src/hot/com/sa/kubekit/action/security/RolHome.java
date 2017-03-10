package com.sa.kubekit.action.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.security.Menu;
import com.sa.model.security.Rol;
import com.sa.model.security.RolMenu;
import com.sa.model.security.Usuario;

@Name("rolHome")
@Scope(ScopeType.CONVERSATION)
public class RolHome extends KubeDAO<Rol>{

	private static final long serialVersionUID = 1L;
	private String rolId;
	private List<Menu> menusDisponibles = new ArrayList<Menu>();
	private List<Rol> resultList = new ArrayList<Rol>();
	private boolean check;
	private Short aprSeleccionadas;
	private String nombreMenu;
	private String menuSuperior;
	private List<String> listaMenuSup = new ArrayList<String>();
		
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("rolHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("rolHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("rolHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance((Rol) getEntityManager().createQuery("select r from Rol r where r.codigo like :code")
					.setParameter("code", rolId).getSingleResult());
		}catch (Exception e) {
			clearInstance();
		}
	}
	
	public Rol rolPorCodigo(String codigoRol){
		try{
			return (Rol) getEntityManager()
					.createQuery("SELECT r FROM Rol r WHERE r.codigo = :code")
					.setParameter("code", codigoRol)
					.getSingleResult();	
		}catch( Exception e ){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void cargarListaMenuSuper()
	{
		listaMenuSup=getEntityManager().createQuery("SELECT DISTINCT(m.menuPadre.nombreSpanish) FROM Menu m order by m.menuPadre.nombreSpanish").getResultList();
	}

	@SuppressWarnings("unchecked")
	public void cargarMenusDisponibles(){
		menusDisponibles.clear();
		menusDisponibles = getEntityManager().createQuery("select m from Menu m where " +
				"m.estado like 'ACT' and (m.menuPadre is not null or m.url is not null ) ORDER BY m.menuPadre.orden ASC ")
				.getResultList();
		check = true;
		
		List<Menu> mnusRol = new ArrayList<Menu>();
		for(RolMenu tmpRlMn : instance.getMenus()) {
			tmpRlMn.getMenu().setOpcAsociada(true);
			mnusRol.add(tmpRlMn.getMenu()); //Especificamos los menus que ya tiene asociados el rol
		}
		
		if(mnusRol.size() < menusDisponibles.size()) // Si los menus del rol son menores que los menus disponibles, no se checkean todos
			check = false;
		
		mnusRol.retainAll(menusDisponibles);  //Se quitan de la lista los menus que no estan en los menus disponible, solo se dejan los que tiene el rol
		
		for(Menu menu : mnusRol) 
				menu.setAsociado(true);
		
		cargarListaMenuSuper();//agregado el 27/01/2017
		
	}
	
	public void buscarPorNombre()
	{
		menusDisponibles.clear();
		menusDisponibles = getEntityManager().createQuery("select m from Menu m where " +
				"m.estado like 'ACT' and (m.menuPadre is not null or m.url is not null ) and m.nombreSpanish like :nombre ORDER BY m.menuPadre.orden ASC ")
				.setParameter("nombre", "%"+nombreMenu+"%")
				.getResultList();
		check = true;
		
		List<Menu> mnusRol = new ArrayList<Menu>();
		
		for(RolMenu tmpRlMn : instance.getMenus()) {
			tmpRlMn.getMenu().setOpcAsociada(true);
			mnusRol.add(tmpRlMn.getMenu());
		}
		
		if(mnusRol.size() < menusDisponibles.size())
			check = false;
		
		mnusRol.retainAll(menusDisponibles);
		
		for(Menu menu : mnusRol) 
				menu.setAsociado(true);
	}
	
	public void buscarPorSuperior()
	{
		menusDisponibles.clear();
		menusDisponibles = getEntityManager().createQuery("select m from Menu m where " +
				"m.estado like 'ACT' and (m.menuPadre is not null or m.url is not null ) and m.menuPadre.nombreSpanish like :nombre ORDER BY m.menuPadre.orden ASC ")
				.setParameter("nombre", "%"+menuSuperior+"%")
				.getResultList();
		check = true;
		
		List<Menu> mnusRol = new ArrayList<Menu>();
		for(RolMenu tmpRlMn : instance.getMenus()) {
			tmpRlMn.getMenu().setOpcAsociada(true);
			mnusRol.add(tmpRlMn.getMenu());
		}
		
		if(mnusRol.size() < menusDisponibles.size())
			check = false;
		
		mnusRol.retainAll(menusDisponibles);
		
		for(Menu menu : mnusRol) 
				menu.setAsociado(true);
	}
	
	public void cargarRoles() {
		resultList = getEntityManager().createQuery("SELECT r FROM Rol r").getResultList();
	}
	
	public void changeAll(){
		for(Menu menu : menusDisponibles)
			menu.setAsociado(check);
		
		if(check)
			setAprSeleccionadas( (short)resultList.size());
		else
			setAprSeleccionadas((short)0);
	}
	
	public void checkCheque(Menu mnu){
		
		if(!mnu.isAsociado() && check)
			setCheck(false);
		
		/*else
			setCheck(false);*/
		
		
	}
	
	public void conteoAprSelecc(boolean sel) {
		if(sel)
			setAprSeleccionadas( (short)(getAprSeleccionadas() + 1) );
		else
			setAprSeleccionadas( (short)(getAprSeleccionadas() - 1) );
		
		if(getAprSeleccionadas() == resultList.size())
			check = true;
		else
			check = false;
	}
	
	public String guardarAsociacion(){
		instance.setMenus(new ArrayList<RolMenu>());
		for(Menu menu : menusDisponibles){
			if(menu.isAsociado() && !menu.isOpcAsociada()) {
				RolMenu tmpRlMnu = new RolMenu();
				tmpRlMnu.setMenu(menu);
				tmpRlMnu.setRol(instance);
				getEntityManager().persist(tmpRlMnu);
			} else if(!menu.isAsociado() && menu.isOpcAsociada()) {
				//Buscamos el rol menu asociado y lo borramos
				List<RolMenu> elim = getEntityManager().createQuery("select x from RolMenu x where " +
						" x.rol = :rl AND x.menu = :mn ")
						.setParameter("rl", instance)
						.setParameter("mn", menu)
						.getResultList();
				
				if(!elim.isEmpty())
					getEntityManager().remove(elim.get(0));
			}
		}
		
		getEntityManager().flush();
			return "save";
	}
	
	@Override
	public boolean preSave() {
		return true;
	}

	@Override
	public boolean preModify() {
		return true;
	}

	@Override
	public boolean preDelete() {
		for(RolMenu rlMn : instance.getMenus()) 
			getEntityManager().remove(rlMn);
		
		for(Usuario usr : instance.getUsuarios()) {
			
			getEntityManager().merge(usr);
		}
		
		return true;
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

	public String getRolId() {
		return rolId;
	}

	public void setRolId(String rolId) {
		this.rolId = rolId;
	}

	public List<Menu> getMenusDisponibles() {
		return menusDisponibles;
	}

	public void setMenusDisponibles(List<Menu> menusDisponibles) {
		this.menusDisponibles = menusDisponibles;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public List<Rol> getResultList() {
		return resultList;
	}

	public void setResultList(List<Rol> resultList) {
		this.resultList = resultList;
	}

	public Short getAprSeleccionadas() {
		if(aprSeleccionadas == null)
			return (short)0;
		return aprSeleccionadas;
	}

	public void setAprSeleccionadas(Short aprSeleccionadas) {
		this.aprSeleccionadas = aprSeleccionadas;
	}

	public String getNombreMenu() {
		return nombreMenu;
	}

	public void setNombreMenu(String nombreMenu) {
		this.nombreMenu = nombreMenu;
	}

	public String getMenuSuperior() {
		return menuSuperior;
	}

	public void setMenuSuperior(String menuSuperior) {
		this.menuSuperior = menuSuperior;
	}

	public List<String> getListaMenuSup() {
		return listaMenuSup;
	}

	public void setListaMenuSup(List<String> listaMenuSup) {
		this.listaMenuSup = listaMenuSup;
	}
	
	
	
}
