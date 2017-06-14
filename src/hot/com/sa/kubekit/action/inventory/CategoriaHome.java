package com.sa.kubekit.action.inventory;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Categoria;

@Name("categoriaHome")
@Scope(ScopeType.CONVERSATION)
public class CategoriaHome extends KubeDAO<Categoria>{

	private static final long serialVersionUID = 1L;
	private Integer categoriaId;
	private List<Categoria> resultList = new ArrayList<Categoria>();
	private String nomCoinci;
	private Boolean filterEstado;
	
	
	@In
	private LoginUser loginUser;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("categoriaHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("categoriaHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("categoriaHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(Categoria.class, categoriaId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new Categoria());
			if(loginUser.getUser()!=null && loginUser.getUser().getSucursal()!=null){
				instance.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
			}
		}
	}
	
	public void loadCatList() {
		resultList = getEntityManager().createQuery("select e from Categoria e " +
				"	WHERE 1 = 1 and  e.activo=true order by e.nombre ")
				.getResultList();
	}
	
	public void cargarCategoriasPadre()
	{
		System.out.println("Entro a categorias metodo");
		resultList = getEntityManager().createQuery("select DISTINCT(e.categoriaPadre) from Categoria e" +
				"	WHERE 1=1 order by e.categoriaPadre.codigo,e.categoriaPadre.nombre ")
				.getResultList();
		
		System.out.println("Categorias "+resultList.size());
		
	}
	
	public void buscadorCategorias(){
		System.out.println("Valor nomCoinci "+ nomCoinci);
		resultList = getEntityManager().createQuery("select e from Categoria e " +
				"	WHERE (UPPER(e.nombre) LIKE UPPER(:nom) OR UPPER(e.codigo) LIKE UPPER(:cod)) and e.activo=true order by e.codigo,e.nombre ")
				.setParameter("cod","%"+nomCoinci+"%" )
				.setParameter("nom","%"+nomCoinci+"%")
				.getResultList();	
		System.out.println("Tamaño de resultList "+resultList.size()); 
	}
	
	
	public void loadCatPadreList(Categoria cat) {
		if(cat != null)
		resultList = getEntityManager().createQuery("select e from Categoria e " +
				"	WHERE 1=1 AND e <> :cat order by e.codigo,e.nombre ")
				.setParameter("cat", cat)
				.getResultList();
		else
			resultList = getEntityManager().createQuery("select e from Categoria e " +
					"	WHERE 1=1 order by e.codigo,e.nombre ")
					.getResultList();
	}
	
	public void loadCatTallerList() {
		resultList = getEntityManager().createQuery("select e from Categoria e " +
				"	WHERE e.deTaller = true order by e.codigo ASC ")
				.getResultList();
	}
	
	private boolean validar() {
		if(instance.getEmpresa()==null){
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("categoriaHome_error_save1"));
			return false;
		}
		if(instance.getCategoriaPadre()!=null && instance.getCategoriaPadre().getId()==instance.getId()){
			instance.setCategoriaPadre(null);
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("categoriaHome_error_save2"));
			return false;
		}
		
		//Evaluamos el codigo del combo que no se repita
		/*List<Categoria> catsCoinci = new ArrayList<Categoria>();
		if(!isManaged()) { 
			catsCoinci = getEntityManager()
					.createQuery("SELECT c FROM Categoria c WHERE UPPER(c.codigo) = UPPER(:cod)")
					.setParameter("cod", instance.getCodigo())
					.getResultList();
			
		} else {
			catsCoinci = getEntityManager()
					.createQuery("SELECT c FROM Categoria c WHERE UPPER(c.codigo) = UPPER(:cod) AND c.id <> :idCat")
					.setParameter("cod", instance.getCodigo())
					.setParameter("idCat", instance.getId())
					.getResultList();
		}*/
		
		//Nuevo 13/12/2016. Se podra ingresar un mismo codigo siempre q la categoria este inactiva
		List<Categoria> catsCoinci = new ArrayList<Categoria>();
		if(!isManaged()) { 
			catsCoinci = getEntityManager()
					.createQuery("SELECT c FROM Categoria c WHERE UPPER(c.codigo) = UPPER(:cod) AND c.activo=true")
					.setParameter("cod", instance.getCodigo())
					.getResultList();
			
		} else {
			catsCoinci = getEntityManager()
					.createQuery("SELECT c FROM Categoria c WHERE UPPER(c.codigo) = UPPER(:cod) AND c.id <> :idCat and c.activo=true")
					.setParameter("cod", instance.getCodigo())
					.setParameter("idCat", instance.getId())
					.getResultList();
		}
		
		if(catsCoinci != null && catsCoinci.size() > 0) {
			sainv_messages.clear();
			FacesMessages.instance().add(
					sainv_messages.get("categoriaHome_codrep"));
			FacesMessages.instance().add("La categoria debe estar inactiva para reutilzar el codigo");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean preSave() {
		
		return validar();
	}

	@Override
	public boolean preModify() {

		return validar();
	}

	@Override
	public boolean preDelete() {
		if((instance.getProductos()==null || instance.getProductos().isEmpty()) && 
				(instance.getProductos()==null || instance.getSubCategorias().isEmpty())){
			return true;
		}else{
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("categoriaHome_error_delete1"));
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

	public Integer getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Integer categoriaId) {
		this.categoriaId = categoriaId;
	}

	public List<Categoria> getResultList() {
		return resultList;
	}

	public void setResultList(List<Categoria> resultList) {
		this.resultList = resultList;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public Boolean getFilterEstado() {
		return filterEstado;
	}

	public void setFilterEstado(Boolean filterEstado) {
		this.filterEstado = filterEstado;
	}
	
	

	
}
