package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.inventory.MovimientoHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Cliente;
import com.sa.model.inventory.Movimiento;
import com.sa.model.medical.DiagnosticoConsulta;
import com.sa.model.medical.DiagnosticoMed;
import com.sa.model.medical.ExamenConsulta;
import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.medical.Medicamento;
import com.sa.model.medical.MedicamentoConsulta;
import com.sa.model.medical.MedicamentoLaboratorios;
import com.sa.model.medical.Prescription;
import com.sa.model.medical.RecomendacionConsulta;
import com.sa.model.medical.RecomendacionMed;
import com.sa.model.sales.Service;

@Name("prescriptionHome")
@Scope(ScopeType.CONVERSATION)
public class PrescriptionHome extends KubeDAO<Prescription>{

	private static final long serialVersionUID = 1L;
	
	private List<MedicalAppointmentService> serviciosAgregados = new ArrayList<MedicalAppointmentService>();
	private List<MedicamentoConsulta> itemsAgregados = new ArrayList<MedicamentoConsulta>();
	private List<RecomendacionConsulta> recomendacionesAgregadas = new ArrayList<RecomendacionConsulta>();
	private List<DiagnosticoConsulta> diagnosticosAgregados = new ArrayList<DiagnosticoConsulta>();
	private List<ExamenConsulta> examenesAgregados = new ArrayList<ExamenConsulta>();
	
	private List<MedicamentoLaboratorios> medicamentosLaboratorios = new ArrayList<MedicamentoLaboratorios>();
	
	private List<Prescription> prescriptionsPendingList = new ArrayList<Prescription>();
	private Integer prescriptionId;
	private boolean diagnSordera;
	private List<MedicalAppointmentService> serviciosYexamenesEliminados = new ArrayList<MedicalAppointmentService>();
	
	@In(required=false, create=true)
	private ClienteHome clienteHome;
	
	@In(required=false, create=true)
	private MovimientoHome movimientoHome;
	
	@In(required=false, create=true)
	private MedicalAppointmentDAO medicalAppointmentDAO;
	
	@In
	private LoginUser loginUser;
	
	@Override
	@Begin(join=true)
	public void create() {
		super.create();
	}
	
	public void load() {
		try{
			setInstance(getEntityManager().find(Prescription.class, prescriptionId));
			itemsAgregados = new ArrayList<MedicamentoConsulta>(instance.getMedicalAppointment().getClinicalHistory().getMedicamentos());
			examenesAgregados = new ArrayList<ExamenConsulta>(instance.getMedicalAppointment().getClinicalHistory().getExamenes());
			recomendacionesAgregadas = new ArrayList<RecomendacionConsulta>(instance.getMedicalAppointment().getClinicalHistory().getRecomendaciones());
			serviciosAgregados = new ArrayList<MedicalAppointmentService>(medicalAppointmentDAO.getAppointmentItems());
			diagnosticosAgregados = instance.getMedicalAppointment().getClinicalHistory().getDiagnosticos();
			
			//Separamos los servicios que no son examenes
			
			//if(instance.getMedicalAppointment().getClinicalHistory().getExamenes() != null)
			for(MedicalAppointmentService srv : medicalAppointmentDAO.getAppointmentItems()) 
				if(srv.getService().getTipoServicio().equals("EXA") && serviciosAgregados.contains(srv))
					serviciosAgregados.remove(srv);
			
			clienteHome.setInstance(instance.getMedicalAppointment().getCliente());
		}catch (Exception e) {
			
		}
	}
	
	public boolean approve() {
		
		Movimiento movimiento = new Movimiento();
		if (loginUser.getUser() != null) {
			movimiento.setUsuario(loginUser.getUser());
		}else{
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("prescriptionHome_error_save1"));
			return true;
		}
		movimiento.setFecha(new Date());
		movimiento.setObservacion(instance.getObservaciones());
		movimiento.setPrescription(instance);
		movimiento.setRazon("R");
		movimiento.setSucursal(instance.getMedicalAppointment().getDoctor().getUsuario().getSucursal());
		movimiento.setTipoMovimiento("S");
		movimientoHome.setInstance(movimiento);
	
		return true;
	}
	
	public boolean reject() {
		instance.setEstado(2);
		return modify();
	}
	
	public void clearItems(){
		this.itemsAgregados = new ArrayList<MedicamentoConsulta>();
	}
	
	@SuppressWarnings("unchecked")
	public void cargarPrescriptionsFromPatient(Cliente cliente){
		prescriptionsPendingList = getEntityManager().createQuery("select p from Prescription p " +
				"where p.estado = 0 and p.medicalAppointment.cliente = :cliente")
				.setParameter("cliente", cliente)
				.getResultList();
		if(prescriptionsPendingList.isEmpty()){
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("prescriptionHome_no_prescriptions"));
		}
	}
	
	public void agregarMedicamento(Medicamento medicm) {
		
		for(MedicamentoConsulta tmpMed : itemsAgregados)
			if(tmpMed.getMedicamento().equals(medicm)){
				
				/*FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("prescriptionHome_error_additem"));*/
				itemsAgregados.remove(tmpMed);
				medicm.setAsociado(false);
				
				return;
			}
		
		//Revisar porque toman el indice cero, siempre estaria agarrando solo el primero de la lista????? 04/04/2017: Solo es para mostrarlo en el select, luego se selecciona
		MedicamentoConsulta item = new MedicamentoConsulta();
		item.setCantidad((short) 1);
		item.setMedicamento(medicm);
		item.setSelDosif(medicm.getDosificaciones().get(0));
		item.setSelPresen(medicm.getPresentaciones().get(0));
		item.setSelLab(medicm.getMedicamentosLab().get(0));
		
		itemsAgregados.add(item);
	}
	
	public void agregarRecomendacion(RecomendacionMed rec) {
		
		for(RecomendacionConsulta tmpRec : recomendacionesAgregadas)
		{
			if(tmpRec.getRecomendacion().equals(rec)){
				
				recomendacionesAgregadas.remove(tmpRec);
				rec.setAsociado(false);
				/*FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("prescriptionHome_error_addrec"));*/
				
				
				return;
			}
			
		}
		
		RecomendacionConsulta recons = new RecomendacionConsulta();
		recons.setRecomendacion(rec);
		recomendacionesAgregadas.add(recons);
	}
	
	public void agregarDiagnostico(DiagnosticoMed dig) {
		
		for(DiagnosticoConsulta tmpDig : diagnosticosAgregados)
			if(tmpDig.getDiagnostico().equals(dig)){
				removerDiagnostico(tmpDig);
				dig.setAsociado(false);
				return;
			}
		
		DiagnosticoConsulta diagn = new DiagnosticoConsulta();
		diagn.setDiagnostico(dig);
		diagnosticosAgregados.add(diagn);
	}
	
	//List<HashMap<K, V>> examenes = new ArrayList<String,ExamenConsulta>();
	
	public void agregarExamen(Service exa) {
		
		
		int indice=0;
		for(ExamenConsulta tmpExa : examenesAgregados)
		{
			System.out.println("Ïndicen examenes "+ examenesAgregados.get(indice));
			if(tmpExa.getExamen().equals(exa)){
				
				System.out.println("Entro a eliminar examen");
				
				//ExamenConsulta exam = new ExamenConsulta();
				//exam.setExamen(exa);
				//examenesAgregados.re
				
				//removerServicioExam(exa);
				examenesAgregados.remove(indice);
				exa.setAsociado(false);
				/*FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("prescriptionHome_error_addexa"));*/
				
				return;
						
				
			}
			indice++;
		}
		
		ExamenConsulta exacon = new ExamenConsulta();
		exacon.setExamen(exa);
		examenesAgregados.add(exacon);
		
	}
	
	public void agregarServicio(Service srv) {
		
		int indice=0;
		for(MedicalAppointmentService tmpSrv : serviciosAgregados)
		{
			if(tmpSrv.getService().equals(srv)){
				
				serviciosAgregados.remove(indice);
				srv.setAsociado(false);
				/*FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("prescriptionHome_error_addsrv"));*/
				
				return;
			}
			
			indice++;
		}
		MedicalAppointmentService medicap = new MedicalAppointmentService();
		medicap.setService(srv);
		serviciosAgregados.add(medicap);
	}
	
	public void removerItem(MedicamentoConsulta medicm){
		/*for(MedicamentoConsulta tmpMed : itemsAgregados)
			if(tmpMed.getMedicamento().equals(medicm))
				itemsAgregados.remove(medicm);*/
		
		if(itemsAgregados.contains(medicm))
			itemsAgregados.remove(medicm);
		
		System.out.println("Remover medicamento");
	}
	
	public void removerExamen(ExamenConsulta exc) {
		
		
		List<MedicalAppointmentService> mService = new ArrayList<MedicalAppointmentService>();
		mService = getEntityManager().createQuery("SELECT s FROM MedicalAppointmentService s where s.medicalAppointmentServiceId.medicalAppointmentId="+medicalAppointmentDAO.getInstance().getId()+" and s.medicalAppointmentServiceId.serviceId="+exc.getExamen().getId()+" ").getResultList();
		
		//if(exc.getExamen().getId()!=null)
		if(mService.size()>0)
		{
			System.out.println("SERVICIO DE EXAMEN DIFERENTE DE NULL");
			
			//Remover de la lista
			
			examenesAgregados.remove(exc);
			//serviciosAgregados.re
			
			//remover de la db el MedicalAppointmentService que se ha registrador
			
			//List<MedicalAppointmentService> paraRemover = medicalAppointmentDAO.getInstance().getMedicalAppointmentServices(); 
		
			//medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().
			
			/*for(MedicalAppointmentService mService:paraRemover)
			{
				if(mService.getService().equals(exc.getExamen()))
				{*/
					//getEntityManager().remove(mService.getServiceClinicalHistory());
					System.out.println("ENTRO AL IF DENTRO DEL FOR PARA REMOVER DESDE LA DB");
					medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().remove(mService.get(0));
					//serviciosYexamenesEliminados.add(mService);
					serviciosAgregados.remove(mService.get(0));
					getEntityManager().remove(mService.get(0));
					getEntityManager().flush();
					
			/*	}
			}*/
			
			
		}
		else
		{
			System.out.println("NO entro a eliminar a la db");
			examenesAgregados.remove(exc);
		}
		
	}
	
	public void verificarServiciosExamenesEliminados()
	{
		
		if(serviciosYexamenesEliminados.size()>0)
		{
			medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().removeAll(serviciosYexamenesEliminados);
			System.out.println("Elimino servicios y/o examenes");
		}
	}
	
	public void removerServicioExam(Service srv)
	{
		examenesAgregados.remove(srv);
	}
	
	public void removerServicioS(MedicalAppointmentService srv)//Nuevo, agregado el 07/06/2017
	{
		if(srv.getMedicalAppointmentServiceId()!=null)
		{
			//getEntityManager().getTransaction().begin();
			
			medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().remove(srv);
			//serviciosYexamenesEliminados.add(srv);
			getEntityManager().remove(srv);
			serviciosAgregados.remove(srv);
			getEntityManager().flush();
			//getEntityManager().getTransaction().commit();
			//getEntityManager().close();
			
		}
		else
		{
			serviciosAgregados.remove(srv);
		}
	}
	
	public void removerServicioConsul(Service srv)
	{
		
	}
	
	public void removerDiagnostico(DiagnosticoConsulta diagn) {
		diagn.getDiagnostico().setAsociado(false);
		diagnosticosAgregados.remove(diagn);
	}
	
	public void removerRecomendacion(RecomendacionConsulta recm) {
		recomendacionesAgregadas.remove(recm);
	}
	
	public void removerServicio(MedicalAppointmentService srv) {
		
		serviciosAgregados.remove(srv);
	}
	
	public void setDiagnPrpal(DiagnosticoConsulta diagn) {
		for(DiagnosticoConsulta tmpDign : diagnosticosAgregados) 
			tmpDign.setPrincipal(false);
		diagn.setPrincipal(true);
	}
	
	@Override
	public boolean preSave() {
		/*
		if(this.itemsAgregados.isEmpty())
			return false;
		*/
		//instance.getMedicalAppointment().getClinicalHistory().setMedicamentos(itemsAgregados);
		return true;
	}

	@Override
	public boolean preModify() {
		/*
		if(this.itemsAgregados.isEmpty())
			return false;
		*/
		//instance.getMedicalAppointment().getClinicalHistory().setMedicamentos(itemsAgregados);
		return true;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void posSave() {
		
		getEntityManager().flush();
		getEntityManager().refresh(instance);
		FacesMessages.instance().clear();
	}

	@Override
	public void posModify() {
		List<MedicamentoConsulta> itemsRemover = new ArrayList<MedicamentoConsulta>(instance.getMedicalAppointment().getClinicalHistory().getMedicamentos());
		itemsRemover.removeAll(itemsAgregados);
		
		/*for(MedicamentoConsulta item : itemsRemover){
			itemPrescriptionHome.setInstance(item);
			itemPrescriptionHome.delete();
		}
		
		for(MedicamentoConsulta item: itemsAgregados){
			item.getItemPrescriptionId().setPrescriptionId(instance.getId());
			item.setPrescription(instance);
			itemPrescriptionHome.setInstance(item);
			itemPrescriptionHome.save();
		}*/
		
		getEntityManager().flush();
		getEntityManager().refresh(instance);
		FacesMessages.instance().clear();
	}

	@Override
	public void posDelete() {
	}

	public List<MedicamentoConsulta> getItemsAgregados() {
		return itemsAgregados;
	}

	public void setItemsAgregados(List<MedicamentoConsulta> itemsAgregados) {
		this.itemsAgregados = itemsAgregados;
	}

	public List<Prescription> getPrescriptionsPendingList() {
		return prescriptionsPendingList;
	}

	public void setPrescriptionsPendingList(List<Prescription> prescriptionsPendingList) {
		this.prescriptionsPendingList = prescriptionsPendingList;
	}

	public Integer getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(Integer prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public List<RecomendacionConsulta> getRecomendacionesAgregadas() {
		return recomendacionesAgregadas;
	}

	public void setRecomendacionesAgregadas(
			List<RecomendacionConsulta> recomendacionesAgregadas) {
		this.recomendacionesAgregadas = recomendacionesAgregadas;
	}

	public List<ExamenConsulta> getExamenesAgregados() {
		return examenesAgregados;
	}

	public void setExamenesAgregados(List<ExamenConsulta> examenesAgregados) {
		this.examenesAgregados = examenesAgregados;
	}

	public List<MedicalAppointmentService> getServiciosAgregados() {
		return serviciosAgregados;
	}

	public void setServiciosAgregados(
			List<MedicalAppointmentService> serviciosAgregados) {
		this.serviciosAgregados = serviciosAgregados;
	}

	public List<DiagnosticoConsulta> getDiagnosticosAgregados() {
		return diagnosticosAgregados;
	}

	public void setDiagnosticosAgregados(
			List<DiagnosticoConsulta> diagnosticosAgregados) {
		this.diagnosticosAgregados = diagnosticosAgregados;
	}

	public boolean isDiagnSordera() {
		return diagnSordera;
	}

	public void setDiagnSordera(boolean diagnSordera) {
		this.diagnSordera = diagnSordera;
	}

	public List<MedicalAppointmentService> getServiciosYexamenesEliminados() {
		return serviciosYexamenesEliminados;
	}

	public void setServiciosYexamenesEliminados(
			List<MedicalAppointmentService> serviciosYexamenesEliminados) {
		this.serviciosYexamenesEliminados = serviciosYexamenesEliminados;
	}

	
	
}
