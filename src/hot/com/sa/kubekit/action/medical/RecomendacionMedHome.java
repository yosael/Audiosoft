package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.ContadorRecomendacionAudio;
import com.sa.model.medical.ContadorRecomendacionOto;
import com.sa.model.medical.RecomendacionMed;

@Name("recomendacionMedHome")
@Scope(ScopeType.CONVERSATION)
public class RecomendacionMedHome extends KubeDAO<RecomendacionMed>{

	private static final long serialVersionUID = 1L;
	private Integer recmId;
	private List<RecomendacionMed> resultList = new ArrayList<RecomendacionMed>();
	private String nomCoinci;
	private String codCoinci;
	private boolean buscarAudio=false;
	private boolean buscarOto=false;
	private String opcionBusqueda="";
	private List<RecomendacionMed> listaUltimosOto;
	private List<RecomendacionMed> listaUltimosAudio;
	private boolean cerrarModal=false;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("recomed_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("recomed_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("recomed_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(RecomendacionMed.class, recmId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new RecomendacionMed());
		}
	}
	
	public void iniciarNuevaRecomendacion()
	{
		clearInstance();
		setInstance(new RecomendacionMed());
	}
	
	
	public void obtenerUltimosCodigosOto()
	{
		
		listaUltimosOto = new ArrayList<RecomendacionMed>();
		
		
		listaUltimosOto = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where c.codigo like 'O%' order by r.codigo desc ").setMaxResults(5).getResultList();
		
	}
	
	public void obtenerUltimosCodigosAudio()
	{
		listaUltimosAudio = new ArrayList<RecomendacionMed>();
		
		listaUltimosAudio = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r order by r.codigo desc ").setMaxResults(5).getResultList();
	}
	
	
	public String generarCodigo()
	{
		
		String numeroActual="";
		String codigo="";
		
		
		if(instance.getTipo().equals("A"))
		{
			ContadorRecomendacionAudio contador = new ContadorRecomendacionAudio();
			contador = (ContadorRecomendacionAudio) getEntityManager().createQuery("SELECT c FROM ContadorRecomendacionAudio c").getResultList().get(0);
			numeroActual=contador.getConteo().toString();
			
			contador.setConteo(contador.getConteo()+1);
			getEntityManager().merge(contador);
			
			
		}	
		else if(instance.getTipo().equals("O"))
		{
			ContadorRecomendacionOto contador = new ContadorRecomendacionOto();
			contador = (ContadorRecomendacionOto) getEntityManager().createQuery("SELECT c FROM ContadorRecomendacionOto c").getResultList().get(0);
			numeroActual=contador.getConteo().toString();
			
			contador.setConteo(contador.getConteo()+1);
			getEntityManager().merge(contador);
		}
		
		
		
		if(numeroActual.length()==1)
		{
			codigo=instance.getTipo()+"00"+numeroActual;
		}
		else if(numeroActual.length()==2)
		{
			codigo=instance.getTipo()+"0"+numeroActual;
		}
		else if(numeroActual.length()==3)
		{
			codigo=instance.getTipo()+numeroActual;
		}
		
		return codigo.toUpperCase();
		
	}
	
	public void getRecomenList() {
		resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r Order By r.codigo ASC").getResultList();
	}
	
	public void getRecomenListByName() {
		
		resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.nombre) like :nom  order by r.codigo")
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.getResultList();
		System.out.println("Tamanio recomendacion ****** "+resultList.size());
	}
	
	public void getRecomenListByNameCod() {
		
		
		//String oto="O";
		//String audio="";
		
		if(opcionBusqueda.equals("2") && nomCoinci==null)
		{
			resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.codigo) like :cod  order by r.codigo")
					.setParameter("cod","O%")
					.getResultList();
			
		}
		else if(opcionBusqueda.equals("1") && nomCoinci==null)
		{
			resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.codigo) like :cod  order by r.codigo")
					.setParameter("cod","A%")
					.getResultList();
			
		}
		else if(opcionBusqueda.equals("") && nomCoinci!=null)
		{
			resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.nombre) like :nom  order by r.codigo")
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.getResultList();
		}
		else if(opcionBusqueda.equals("2") && nomCoinci!=null)
		{
			resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.codigo) like :cod and UPPER(r.nombre) like :nom order by r.codigo")
					.setParameter("cod","O%")
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.getResultList();
			
		}
		else if(opcionBusqueda.equals("1") && nomCoinci!=null)
		{
			resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.codigo) like :cod and UPPER(r.nombre) like :nom  order by r.codigo")
					.setParameter("cod","A%")
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.getResultList();
		}
			
		/*else if(codCoinci!=null && nomCoinci!=null)
		{
			resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.nombre) like :nom and UPPER(r.codigo) like :cod order by r.codigo")
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.setParameter("cod",this.nomCoinci.toUpperCase()+"%")
					.getResultList();
		}*/
		
		
		
		/*resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.nombre) like :nom  order by r.codigo")
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.getResultList();*/
		
		System.out.println("Tamanio recomendacion ****** "+resultList.size());
	}

	@Override
	public boolean preSave() {
		
		
		if(instance.getTipo()==null || instance.getTipo().equals(""))
		{
			
			FacesMessages.instance().add(Severity.WARN,"Seleccionar el tipo de recomendacion");
			return false;
		}
		
		
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<RecomendacionMed> coinList = getEntityManager()
				.createQuery("SELECT r FROM RecomendacionMed r WHERE UPPER(r.nombre) = UPPER(:rec)")
				.setParameter("rec", instance.getNombre())
				.getResultList();
		
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("recomed_name_dupl"));
			return false;
		}
		
		/*List<RecomendacionMed> coinList2 = getEntityManager()
				.createQuery("SELECT r FROM RecomendacionMed r WHERE UPPER(r.codigo) = UPPER(:rec)")
				.setParameter("rec", instance.getCodigo())
				.getResultList();
		
		if(coinList2 != null && coinList2.size() > 0) {
			FacesMessages.instance().add(Severity.WARN,"El codigo ya existe");
			return false;
		}*/
		
		instance.setCodigo(generarCodigo());
		
		
		
		return true;
	}

	@Override
	public boolean preModify() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<RecomendacionMed> coinList = getEntityManager()
				.createQuery("SELECT r FROM RecomendacionMed r " +
						"	WHERE UPPER(r.nombre) = UPPER(:rec) AND r.id <> :idR")
				.setParameter("rec", instance.getNombre())
				.setParameter("idR", instance.getId())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("recomed_name_dupl"));
			return false;
		}
		
		List<RecomendacionMed> coinList2 = getEntityManager()
				.createQuery("SELECT r FROM RecomendacionMed r WHERE UPPER(r.codigo) = UPPER(:rec)")
				.setParameter("rec", instance.getCodigo())
				.getResultList();
		
		
		if(recmId!=null && recmId!=instance.getId())
		{
			if(coinList2 != null && coinList2.size() > 0) {
				FacesMessages.instance().add(Severity.WARN,"El codigo ya existe");
				return false;
			}
			
		}
		
		return true;
	}
	
	
	public void agregarRecomendacionModal()
	{
		
		System.out.println("Entro a recomendacion modal");
		cerrarModal=false;
		
		if(save())
		{
			cerrarModal=true;
			System.out.println("Guardo con exito");
		}
		else
		{
			return;
		}
		
	}

	@Override
	public boolean preDelete() {
		return false;
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

	public List<RecomendacionMed> getResultList() {
		return resultList;
	}

	public void setResultList(List<RecomendacionMed> resultList) {
		this.resultList = resultList;
	}

	public Integer getRecmId() {
		return recmId;
	}

	public void setRecmId(Integer recmId) {
		this.recmId = recmId;
	}
	

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public String getCodCoinci() {
		return codCoinci;
	}

	public void setCodCoinci(String codCoinci) {
		this.codCoinci = codCoinci;
	}

	public boolean isBuscarAudio() {
		return buscarAudio;
	}

	public void setBuscarAudio(boolean buscarAudio) {
		this.buscarAudio = buscarAudio;
	}

	public boolean isBuscarOto() {
		return buscarOto;
	}

	public void setBuscarOto(boolean buscarOto) {
		this.buscarOto = buscarOto;
	}

	public String getOpcionBusqueda() {
		return opcionBusqueda;
	}

	public void setOpcionBusqueda(String opcionBusqueda) {
		this.opcionBusqueda = opcionBusqueda;
	}

	public List<RecomendacionMed> getListaUltimosOto() {
		return listaUltimosOto;
	}

	public void setListaUltimosOto(List<RecomendacionMed> listaUltimosOto) {
		this.listaUltimosOto = listaUltimosOto;
	}

	public List<RecomendacionMed> getListaUltimosAudio() {
		return listaUltimosAudio;
	}

	public void setListaUltimosAudio(List<RecomendacionMed> listaUltimosAudio) {
		this.listaUltimosAudio = listaUltimosAudio;
	}

	public boolean isCerrarModal() {
		return cerrarModal;
	}

	public void setCerrarModal(boolean cerrarModal) {
		this.cerrarModal = cerrarModal;
	}
	
	
	
	

}
