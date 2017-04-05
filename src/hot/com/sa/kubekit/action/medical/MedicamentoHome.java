package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.Dosificacion;
import com.sa.model.medical.DosificacionMedicamento;
import com.sa.model.medical.IndiceTerapeutico;
import com.sa.model.medical.LaboratorioMed;
import com.sa.model.medical.Medicamento;
import com.sa.model.medical.MedicamentoLaboratorios;
import com.sa.model.medical.Presentacion;
import com.sa.model.medical.PresentacionMedicamento;
import com.sa.model.medical.SustanciaActiva;

@Name("medicamentoHome")
@Scope(ScopeType.CONVERSATION)
public class MedicamentoHome extends KubeDAO<Medicamento>{

	private static final long serialVersionUID = 1L;
	private Integer medmId;
	private List<Medicamento> resultList = new ArrayList<Medicamento>();
	private List<LaboratorioMed> laboratorios = new ArrayList<LaboratorioMed>();
	private List<IndiceTerapeutico> indicesTer = new ArrayList<IndiceTerapeutico>();
	private List<SustanciaActiva> sustanciasAct = new ArrayList<SustanciaActiva>();
	private List<Dosificacion> dosificacionesSel = new ArrayList<Dosificacion>();
	private List<Presentacion> presentacionesSel = new ArrayList<Presentacion>();
	private List<DosificacionMedicamento> dosificacionesList = new ArrayList<DosificacionMedicamento>();
	private List<PresentacionMedicamento> presentacionesList = new ArrayList<PresentacionMedicamento>();
	
	//Nuevo
	private List<MedicamentoLaboratorios> listaMedicamentosLabs = new ArrayList<MedicamentoLaboratorios>();
	
	
	private LaboratorioMed labMed;
	private IndiceTerapeutico indTer;
	private SustanciaActiva susAct;
	private Dosificacion dosif;
	private Presentacion presen;
	private String nomCoinci;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("medicm_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("medicm_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("medicm_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(Medicamento.class, medmId));
			dosificacionesList = instance.getDosificaciones();
			presentacionesList = instance.getPresentaciones();
			listaMedicamentosLabs = instance.getMedicamentosLab();
		}catch (Exception e) {
			clearInstance();
			setInstance(new Medicamento());
		}
		susAct = new SustanciaActiva();
		indTer = new IndiceTerapeutico();
		labMed = new LaboratorioMed();
		dosif = new Dosificacion();
		presen = new Presentacion();
	}
	
	public void getMedicamentosList() {
		resultList = getEntityManager()
				.createQuery("select m from Medicamento m ")
				.getResultList();
	}
	
	public void getMedicamentosByName() {
		resultList = getEntityManager()
				.createQuery("select m from Medicamento m where UPPER(m.nombre) like :nombre ")
				.setParameter("nombre", "%"+nomCoinci.toUpperCase()+"%")
				.getResultList();
		
		System.out.println("ENtro a medicamentos Tamanio *** "+ resultList.size());
	}
	
	public void cargarListaLabs() {
		laboratorios = getEntityManager()
				.createQuery("select l from LaboratorioMed l ")
				.getResultList();
	}
	
	public void cargarListaIndices() {
		indicesTer = getEntityManager()
				.createQuery("select i from IndiceTerapeutico i ")
				.getResultList();
	}
	
	public void cargarSustanciasAct() {
		sustanciasAct = getEntityManager()
				.createQuery("select s from SustanciaActiva s ")
				.getResultList();
	}
	
	public void cargarListaDosif() {
		dosificacionesSel = getEntityManager()
				.createQuery("select d from Dosificacion d ")
				.getResultList();
	}
	
	public void cargarListaPresen() {
		presentacionesSel = getEntityManager()
				.createQuery("select p from Presentacion p ")
				.getResultList();
	}
	
	public void addDosificacion(Dosificacion dos) {
		//Verificamos que no hayan asociado esa dosificacion previamente
		boolean existe = false;
		for(DosificacionMedicamento dosMed : dosificacionesList) {
			if(dosMed.getDosificacion().equals(dos)) {
				existe = true;
				break;
			}
		}
		if(!existe) {
			DosificacionMedicamento newDosMed = new DosificacionMedicamento();
			newDosMed.setDosificacion(dos);
			if(isManaged())
				newDosMed.setMedicamento(instance);
			dosificacionesList.add(newDosMed);
		}
	}
	
	public void addPresentacion(Presentacion pre) {
		//Verificamos que no hayan asociado esa presentacion previamente
		boolean existe = false;
		for(PresentacionMedicamento dosMed : presentacionesList) {
			if(dosMed.getPresentacion().equals(pre)) {
				existe = true;
				break;
			}
		}
		if(!existe) {
			PresentacionMedicamento newPresMed = new PresentacionMedicamento();
			newPresMed.setPresentacion(pre);
			if(isManaged())
				newPresMed.setMedicamento(instance);
			presentacionesList.add(newPresMed);
		}
	}
	
	public void addNewDosificacion() {
		if(dosif != null && dosif.getNombre() != null && !dosif.getNombre().trim().equals("")) {
			//VErificamos que no exista ese nombre de dosificacion
			List<LaboratorioMed> coincidencias = getEntityManager()
					.createQuery("SELECT d FROM Dosificacion d WHERE UPPER(d.nombre) = UPPER(:nomLab)")
					.setParameter("nomLab", dosif.getNombre())
					.getResultList();
			if(coincidencias == null || coincidencias.size() <= 0) { 
				getEntityManager().persist(dosif);
				dosif = new Dosificacion();
				cargarListaDosif();
				dosif.setNombre("");
			} else {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("medicm_error_dosexis"));
			}
		}
	}
	
	public void addNewPresentacion() {
		if(presen != null && presen.getNombre() != null && !presen.getNombre().trim().equals("")) {
			//VErificamos que no exista ese nombre de dosificacion
			List<LaboratorioMed> coincidencias = getEntityManager()
					.createQuery("SELECT p FROM Presentacion p WHERE UPPER(p.nombre) = UPPER(:nomLab)")
					.setParameter("nomLab", presen.getNombre())
					.getResultList();
			if(coincidencias == null || coincidencias.size() <= 0) { 
				getEntityManager().persist(presen);
				presen = new Presentacion();
				cargarListaPresen();
				presen.setNombre("");
			} else {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("medicm_error_presexis"));
			}
		}
	}
	
	public void addLaboratorio() {
		if(labMed != null && labMed.getNombre() != null && !labMed.getNombre().trim().equals("")) {
			//VErificamos que no exista ese nombre de laboratorio
			List<LaboratorioMed> coincidencias = getEntityManager()
					.createQuery("SELECT l FROM LaboratorioMed l WHERE UPPER(l.nombre) = UPPER(:nomLab)")
					.setParameter("nomLab", labMed.getNombre())
					.getResultList();
			if(coincidencias == null || coincidencias.size() <= 0) { 
				getEntityManager().persist(labMed);
				labMed = new LaboratorioMed();
				cargarListaLabs();
				labMed.setNombre("");
			} else {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("medicm_error_labexis"));
			}
		}
	}
	
	public void addIndiceTer() {
		if(indTer != null && indTer.getNombre() != null && !indTer.getNombre().trim().equals("")) {
			//VErificamos que no exista ese nombre de laboratorio
			List<IndiceTerapeutico> coincidencias = getEntityManager()
					.createQuery("SELECT i FROM IndiceTerapeutico i WHERE UPPER(i.nombre) = UPPER(:nomInd)")
					.setParameter("nomInd", indTer.getNombre())
					.getResultList();
			if(coincidencias == null || coincidencias.size() <= 0) { 
				getEntityManager().persist(indTer);
				indTer = new IndiceTerapeutico();
				cargarListaIndices();
				indTer.setNombre("");
			} else {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("medicm_error_indexis"));
			}
		}
	}
	
	public void addSustanciaAct() {
		if(susAct != null && susAct.getNombre() != null && !susAct.getNombre().trim().equals("")) {
			//VErificamos que no exista ese nombre de laboratorio
			List<SustanciaActiva> coincidencias = getEntityManager()
					.createQuery("SELECT s FROM SustanciaActiva s WHERE UPPER(s.nombre) = UPPER(:nomSus)")
					.setParameter("nomSus", susAct.getNombre())
					.getResultList();
			if(coincidencias == null || coincidencias.size() <= 0) { 
				getEntityManager().persist(susAct);
				susAct = new SustanciaActiva();
				cargarSustanciasAct();
				susAct.setNombre("");
			} else {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("medicm_error_suaexis"));
			}
		}
	}
	
	public void remDosifMedic(DosificacionMedicamento dos) {
		dosificacionesList.remove(dos);
	}
	
	public void remPresenMedic(PresentacionMedicamento pre) {
		presentacionesList.remove(pre);
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
		//Borramos lo asociado de las dosificaciones y presentaciones
		if(instance.getDosificaciones() != null)
			for(DosificacionMedicamento dosMed : instance.getDosificaciones()) 
				getEntityManager().remove(dosMed);
		
		if(instance.getPresentaciones() != null)
			for(PresentacionMedicamento preMed : instance.getPresentaciones()) 
				getEntityManager().remove(preMed);
		
		return true;
		
	}

	@Override
	public void posSave() {
		saveDetailMed();
	}
	
	private void saveDetailMed() {
		getEntityManager().refresh(instance);
		if(instance.getDosificaciones() != null)
			for(DosificacionMedicamento dosMed : instance.getDosificaciones()) 
				getEntityManager().remove(dosMed);
		
		persistirMedicamentosLab();
		
		if(instance.getPresentaciones() != null)
			for(PresentacionMedicamento preMed : instance.getPresentaciones()) 
				getEntityManager().remove(preMed);
		
		getEntityManager().flush();
		// Guardamos las dosificaciones y presentaciones de la lista
		for(DosificacionMedicamento dosMed : dosificacionesList) {
			DosificacionMedicamento newDos = new DosificacionMedicamento();  
			newDos.setMedicamento(instance);
			newDos.setDosificacion(dosMed.getDosificacion());
			getEntityManager().persist(newDos);
		}	
		
		
		for(PresentacionMedicamento preMed : presentacionesList) {
			PresentacionMedicamento newPre = new PresentacionMedicamento();  
			newPre.setMedicamento(instance);
			newPre.setPresentacion(preMed.getPresentacion());
			getEntityManager().persist(newPre);
		}	
	}
	
	
	//Nuevo el 04/04/2017
	public void agregarLaboratorio(LaboratorioMed laboratorio)
	{
		MedicamentoLaboratorios medicamentosLab = new MedicamentoLaboratorios();
		medicamentosLab.setLaboratorio(laboratorio);
		listaMedicamentosLabs.add(medicamentosLab);
		
	}
	
	public void persistirMedicamentosLab()
	{
		for(MedicamentoLaboratorios medLabs: listaMedicamentosLabs)
		{
			
			medLabs.setMedicamento(instance);
			
			if(medLabs.getId()!=null)
			{System.out.println("Guardo el medicamento lab");
				getEntityManager().persist(medLabs);
				
			}
		}
	}
	
	public void quitarMedicamentosLab(MedicamentoLaboratorios medLabs)
	{
		if(medLabs.getId()!=null)
		{
			listaMedicamentosLabs.remove(medLabs);
			getEntityManager().remove(medLabs);
		}
		else
		{
			listaMedicamentosLabs.remove(medLabs);
		}
	}

	@Override
	public void posModify() {
		
		System.out.println("Entro al possave");
		saveDetailMed();
		
		System.out.println("Paso del metodo");
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<Medicamento> getResultList() {
		return resultList;
	}

	public void setResultList(List<Medicamento> resultList) {
		this.resultList = resultList;
	}

	public Integer getMedmId() {
		return medmId;
	}

	public void setMedmId(Integer medmId) {
		this.medmId = medmId;
	}

	public List<LaboratorioMed> getLaboratorios() {
		return laboratorios;
	}

	public void setLaboratorios(List<LaboratorioMed> laboratorios) {
		this.laboratorios = laboratorios;
	}

	public List<IndiceTerapeutico> getIndicesTer() {
		return indicesTer;
	}

	public void setIndicesTer(List<IndiceTerapeutico> indicesTer) {
		this.indicesTer = indicesTer;
	}

	public List<SustanciaActiva> getSustanciasAct() {
		return sustanciasAct;
	}

	public void setSustanciasAct(List<SustanciaActiva> sustanciasAct) {
		this.sustanciasAct = sustanciasAct;
	}

	public SustanciaActiva getSusAct() {
		return susAct;
	}

	public void setSusAct(SustanciaActiva susAct) {
		this.susAct = susAct;
	}

	public IndiceTerapeutico getIndTer() {
		return indTer;
	}

	public void setIndTer(IndiceTerapeutico indTer) {
		this.indTer = indTer;
	}

	public LaboratorioMed getLabMed() {
		return labMed;
	}

	public void setLabMed(LaboratorioMed labMed) {
		this.labMed = labMed;
	}

	public List<DosificacionMedicamento> getDosificacionesList() {
		return dosificacionesList;
	}

	public void setDosificacionesList(
			List<DosificacionMedicamento> dosificacionesList) {
		this.dosificacionesList = dosificacionesList;
	}

	public List<PresentacionMedicamento> getPresentacionesList() {
		return presentacionesList;
	}

	public void setPresentacionesList(
			List<PresentacionMedicamento> presentacionesList) {
		this.presentacionesList = presentacionesList;
	}

	public Dosificacion getDosif() {
		return dosif;
	}

	public void setDosif(Dosificacion dosif) {
		this.dosif = dosif;
	}

	public Presentacion getPresen() {
		return presen;
	}

	public void setPresen(Presentacion presen) {
		this.presen = presen;
	}

	public List<Dosificacion> getDosificacionesSel() {
		return dosificacionesSel;
	}

	public void setDosificacionesSel(List<Dosificacion> dosificacionesSel) {
		this.dosificacionesSel = dosificacionesSel;
	}

	public List<Presentacion> getPresentacionesSel() {
		return presentacionesSel;
	}

	public void setPresentacionesSel(List<Presentacion> presentacionesSel) {
		this.presentacionesSel = presentacionesSel;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public List<MedicamentoLaboratorios> getListaMedicamentosLabs() {
		return listaMedicamentosLabs;
	}

	public void setListaMedicamentosLabs(
			List<MedicamentoLaboratorios> listaMedicamentosLabs) {
		this.listaMedicamentosLabs = listaMedicamentosLabs;
	}
	
	
	
	
}
