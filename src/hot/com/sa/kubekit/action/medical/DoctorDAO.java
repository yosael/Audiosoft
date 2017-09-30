package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jboss.seam.Instance;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.security.UsuarioHome;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.Doctor;
import com.sa.model.medical.Internal;
import com.sa.model.medical.Specialty;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;



@Name("doctorDAO")
@Scope(ScopeType.CONVERSATION)
public class DoctorDAO extends KubeDAO<Doctor> {

	private static final long serialVersionUID = 1L;
	private Integer numId;
	private List<Specialty> specialtyList = new ArrayList<Specialty>(0);
	private List<Doctor> resultList = new ArrayList<Doctor>();
	private String searchSuc;
	private Sucursal searchSucOb;
	private Specialty specialtySelec;
	private List<Specialty> specialties=new ArrayList<Specialty>();

	@In(create = true)
	private UsuarioHome usuarioHome;

	@In(required = true, create = false)
	private LoginUser loginUser;

	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("doctorDAO_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("doctorDAO_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("doctorDAO_deleted")));
		
		//System.out.println("ENTRO AL METODO CREATE DEL DOCTOR: TIMEOUT: "+Conversation.instance().getTimeout());
	}
	
	public Doctor doctorInSession() {
		Usuario user = loginUser.loadUser();
		if (user.getDoctorReg() != null) {
			numId = user.getDoctorReg().getId();
			load();
			if (isManaged())
				return getInstance();
		}
		return null;
	}
	
	
	//Muestra la lista de especialidades de cada doctor
	public List<Specialty> loadSpecialtiesDc(Doctor doctor)
	{
		List<Specialty> lista;
		
		lista=new ArrayList<Specialty>(doctor.getSpecialties());
		
		return lista;
	}
	
	
	//Carga la lista de especialidades en el selectOneMenu
	public void loadSpecialties()
	{
		
		specialties=getEntityManager().createQuery("Select s from Specialty s").getResultList();
		//System.out.println("Numero de especialidades "+ specialties.size());
	}
	
	
	public void load() {
		
		try {
			Doctor doctor = (Doctor) getEntityManager()
					.createQuery("select s from Doctor s where s.id = :numId")
					.setParameter("numId", getNumId()).getSingleResult();
			setInstance(doctor);
			specialtyList = new ArrayList<Specialty>(doctor.getSpecialties());
			if(instance.getUsuario().getSucursal()!=null){
				usuarioHome.setEmpresaSeleccionada(instance.getUsuario().getSucursal().getEmpresa());
				usuarioHome.setSucursales(new ArrayList<Sucursal>(usuarioHome.getEmpresaSeleccionada().getSucursales()));
			}
			usuarioHome.setEstado(instance.getUsuario().getEstado());
			
		} catch (Exception e) {
			e.printStackTrace();
			clearInstance();
			setInstance(new Doctor());
			//instance.setDoctor(true);
			/*if(loginUser.getUser()!=null && loginUser.getUser().getSucursal()!=null){
				instance.setSucursal(loginUser.getUser().getSucursal());
			}*/
		}
	}
	
	
	public void searchDocBySuc()
	{
		
		resultList = getEntityManager()
				.createQuery("SELECT  e from Doctor e where  UPPER(e.usuario.sucursal.sucursalSuperior.nombre) like :searchSuc  OR UPPER(e.usuario.sucursal.nombre) like :searchSuc  and UPPER(e.usuario.sucursal.nombre) NOT LIKE UPPER(:bod)  order by e.nombres")
				.setParameter("searchSuc", "%"+searchSuc.toUpperCase()+"%")
				.setParameter("bod", "%BODEGA%")
				.getResultList();
		//System.out.println("Entroo a buscar");
	}
	
	
	//Muestra los doctores por sucursal seleccionada
	public void searchDocBySucSl()
	{
		
		
		try {
			
			if((getSearchSucOb().getBodega()==false || getSearchSucOb().getBodega()==null)  && getSearchSucOb().getSucursalSuperior()==null)
			{
				resultList = getEntityManager()
						.createQuery("SELECT  e from Doctor e where  e.usuario.sucursal.sucursalSuperior = :searchSucOb  order by e.nombres")// Si la sucursal tiene sucursal superior. El usuario tiene sucursal superior y hay que compararlo con esa
						.setParameter("searchSucOb",searchSucOb)
						.getResultList();
			}
			else if(getSearchSucOb().getBodega()==true && getSearchSucOb().getSucursalSuperior()==null)// Para evitar que se filtren las bodegas que tienen sucursal superior
			{
				
				resultList = getEntityManager()
						.createQuery("SELECT  e from Doctor e where  e.usuario.sucursal = :searchSucOb  order by e.nombres")
						.setParameter("searchSucOb",searchSucOb)
						.getResultList();
				
			}
			
		} catch (Exception e) {
			
			resultList = getEntityManager()
					.createQuery("select e from Doctor e order by e.nombres")
					.getResultList();
			
			
		}
		
		
	}
	
	public void searchDocBySucSlFromDoc()
	{
		
		try {
			
		
			if(loginUser.getUser().getSucursal().getSucursalSuperior()!=null)
			{
				setSearchSucOb(loginUser.getUser().getSucursal().getSucursalSuperior());
				
				resultList = getEntityManager()
						.createQuery("SELECT  e from Doctor e where  e.usuario.sucursal.sucursalSuperior = :searchSucOb  order by e.nombres")
						.setParameter("searchSucOb",searchSucOb)
						.getResultList();
			}
			else
			{
				setSearchSucOb(loginUser.getUser().getSucursal());
				
				resultList = getEntityManager()
						.createQuery("SELECT  e from Doctor e where  e.usuario.sucursal = :searchSucOb  order by e.nombres")
						.setParameter("searchSucOb",searchSucOb)
						.getResultList();
			}
		
		
		
		
		
		/*	
			if(getSearchSucOb().getNombre().equals("MIRAMONTE"))
			{
				
			}
			else
			{
				
				
				
			}*/
			
		} catch (Exception e) {
			
			resultList = getEntityManager()
					.createQuery("select e from Doctor e order by e.nombres")
					.getResultList();
			
		}
		
		
		loadSpecialties();
		
	}
	
	
	//Busca los doctores por especialidad
	public void searchDocBySpecial()
	{
		
		if(specialtySelec==null)
		{
			resultList = getEntityManager()
					.createQuery("select e from Doctor e order by e.nombres")
					.getResultList();
		}
		else
		{
		
			List<Doctor> resultTemp;
			resultTemp =getEntityManager()
					.createQuery("select doc from Doctor doc")
					.getResultList();
			//System.out.println("**** por especialidad "+ resultTemp.size());
			resultList.clear();
			for(Doctor doc:resultTemp)
			{
				for(Specialty sp: doc.getSpecialties())
				{
					if(sp==specialtySelec)
					{
						resultList.add(doc);
						System.out.println("numero doctor "+ doc.getApellidos());
					}
						
					
					
				}
			}
		}
	
	}
	
	
	//Muestra a lista de doctores por defecto y ejecuta el metodo que carga las especialidades en el selectonemenu
	public void listaDoctores() {
		
		
		resultList = getEntityManager()
				.createQuery("select e from Doctor e order by e.nombres")
				.getResultList();
		
		
		loadSpecialties();
		
	}

	@Override
	public void posDelete() {
		numId=null;
	}

	@Override
	public void posModify() {
		numId=null;
	}

	@Override
	public void posSave() {
		numId=null;
	}

	@Override
	public boolean preDelete() {
		if (!getInstance().getMedicalAppointments().isEmpty()) {
			FacesMessages.instance().add(sainv_messages.get("doctorDAO_error2"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		getInstance()
				.setSpecialties(new HashSet<Specialty>(getSpecialtyList()));
		return true;
	}

	@Override
	public boolean preSave() {
		/*SHA1 sha = new SHA1();
		String claveUsr = sha.getSalt()+getInstance().getNombreUsuario();
		try {
			getInstance().setPass(sha.getHash(claveUsr));
		} catch(NoSuchAlgorithmException ex) {
			getInstance().setPass("");
		}*/
		getInstance().setSpecialties(new HashSet<Specialty>(getSpecialtyList()));
		return true;
	}

	public void select(Internal internal) {
		Doctor doctor = getEntityManager().find(Doctor.class,
				internal.getId());
		inspect("***valor = " + doctor);
		super.select(doctor);
	}

	public Integer getNumId() {
		return numId;
	}

	public void setNumId(Integer numId) {
		this.numId = numId;
	}

	public List<Specialty> getSpecialtyList() {
		return specialtyList;
	}

	public void setSpecialtyList(List<Specialty> specialtyList) {
		this.specialtyList = specialtyList;
	}

	public List<Doctor> getResultList() {
		return resultList;
	}

	public void setResultList(List<Doctor> resultList) {
		this.resultList = resultList;
	}
	
	
	public String getSearchSuc() {
		return searchSuc;
	}

	public void setSearchSuc(String searchSuc) {
		this.searchSuc = searchSuc;
	}

	public Sucursal getSearchSucOb() {
		return searchSucOb;
	}

	public void setSearchSucOb(Sucursal searchSucOb) {
		this.searchSucOb = searchSucOb;
	}

	public Specialty getSpecialtySelec() {
		return specialtySelec;
	}

	public void setSpecialtySelec(Specialty specialtySelec) {
		this.specialtySelec = specialtySelec;
	}

	public List<Specialty> getSpecialties() {
		return specialties;
	}

	public void setSpecialties(List<Specialty> specialties) {
		this.specialties = specialties;
	}
	
	

}