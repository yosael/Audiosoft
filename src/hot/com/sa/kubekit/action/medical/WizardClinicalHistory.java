package com.sa.kubekit.action.medical;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.Conversation;

import sun.security.jca.GetInstance;

import com.sa.model.medical.ClinicalHistory;
import com.sa.model.medical.DiagnosticoConsulta;
import com.sa.model.medical.ExamenConsulta;
import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.medical.MedicamentoConsulta;
import com.sa.model.medical.RecomendacionConsulta;
import com.sa.model.medical.ServiceClinicalHistory;
import com.sa.model.medical.id.ServiceClinicalHistoryId;

public abstract class WizardClinicalHistory {

	private Integer typeId;
	private Integer numeroId;

	@Out(required = false)
	protected String linkDiagBack;

	@Out(required = false)
	protected String linkDiagNext;

	@Out(required = false)
	protected String linkEndBack;

	@Out(required = false)
	protected String linkEndNext;

	@In(create = true)
	protected ClienteHome clienteHome;

	@In(create = true)
	protected MedicalAppointmentDAO medicalAppointmentDAO;

	@In(create = true)
	protected GeneralContainer generalContainer;
	
	@In(create = true)
	private PrescriptionHome prescriptionHome;

	@In(create = true)
	private DoctorDAO doctorDAO;

	@In(create = true)
	private PastHistory pastHistory;
	
	@In(create = true)
	private GeneralMedicalDAO generalMedicalDAO;

	@In
	protected EntityManager entityManager;

	// mode = r,w,i
	private String mode;

	private Long consecutive;

	protected void load() {
		// logica de permisos
		System.out.println("entro a superload");
		if (doctorDAO.doctorInSession() != null
				&& doctorDAO.doctorInSession().getId().equals(
						obtainClinicalHistory().getDoctor().getId())) {
			System.out.println("super if");
			setMode("w");
			generalContainer.setMode("w");
		} else {
			System.out.println("super else");
			setMode("r");
			generalContainer.setMode("r");
		}
		System.out.println("super lo demas: generalConteiner:  " + generalContainer.getMode());
		loadMedicalAppointment();
		// cargar los datos generales
		generalContainer.setTreatment(obtainClinicalHistory()
				.getTreatmentDescriptive());
		generalContainer
				.setExams(obtainClinicalHistory().getExamsDescriptive());
		generalContainer.setDiagnostics(new ArrayList<ServiceClinicalHistory>(
				obtainClinicalHistory().getServiceClinicalHistories()));
		loadPastHistories();
	}
	
	
	protected void load2() {
		// logica de permisos
		if (doctorDAO.doctorInSession() != null
				&& doctorDAO.doctorInSession().getId().equals(
						obtainClinicalHistory().getDoctor().getId())) {
			setMode("w");
			generalContainer.setMode("w");
		} else {
			setMode("r");
			generalContainer.setMode("r");
		}
		loadMedicalAppointment2();
		// cargar los datos generales
		generalContainer.setTreatment(obtainClinicalHistory()
				.getTreatmentDescriptive());
		generalContainer
				.setExams(obtainClinicalHistory().getExamsDescriptive());
		generalContainer.setDiagnostics(new ArrayList<ServiceClinicalHistory>(
				obtainClinicalHistory().getServiceClinicalHistories()));
		loadPastHistories();
		
	}

	private void loadMedicalAppointment() {
		prescriptionHome.setExamenesAgregados(new ArrayList<ExamenConsulta>());
		prescriptionHome.setRecomendacionesAgregadas(new ArrayList<RecomendacionConsulta>());
		prescriptionHome.setDiagnosticosAgregados(new ArrayList<DiagnosticoConsulta>());
		prescriptionHome.setItemsAgregados(new ArrayList<MedicamentoConsulta>());
		prescriptionHome.setServiciosAgregados(new ArrayList<MedicalAppointmentService>());

		medicalAppointmentDAO.load();
				
		clienteHome.setNumId(medicalAppointmentDAO.getInstance()
				.getCliente().getId().toString());
		clienteHome.load(false);
		ClinicalHistory hist = medicalAppointmentDAO.getInstance().getClinicalHistory();
		
		if(medicalAppointmentDAO.getInstance().getClinicalHistory() != null) {
			//System.out.println("getClinicalHistory no es null: "+ medicalAppointmentDAO.getInstance().getClinicalHistory());
			
			//System.out.println("Tamm Examenes "+medicalAppointmentDAO.getInstance().getClinicalHistory().getExamenes().size());
			
			prescriptionHome.setExamenesAgregados(generalMedicalDAO.getInstance().getMedicalAppointment().getClinicalHistory().getExamenes());
			prescriptionHome.setRecomendacionesAgregadas(generalMedicalDAO.getInstance().getMedicalAppointment().getClinicalHistory().getRecomendaciones());
			prescriptionHome.setItemsAgregados(generalMedicalDAO.getInstance().getMedicalAppointment().getClinicalHistory().getMedicamentos());
			prescriptionHome.setDiagnosticosAgregados(generalMedicalDAO.getInstance().getMedicalAppointment().getClinicalHistory().getDiagnosticos());// comentado el 21/12/2016
			/*prescriptionHome.setExamenesAgregados(medicalAppointmentDAO.getInstance().getClinicalHistory().getExamenes());
			prescriptionHome.setRecomendacionesAgregadas(medicalAppointmentDAO.getInstance().getClinicalHistory().getRecomendaciones());
			prescriptionHome.setItemsAgregados(medicalAppointmentDAO.getInstance().getClinicalHistory().getMedicamentos());
			prescriptionHome.setDiagnosticosAgregados(medicalAppointmentDAO.getInstance().getClinicalHistory().getDiagnosticos());*/
			System.out.println("ENTRO HA WizardClinicalHistory--->loadMedicalAppointment if != null ");
		} 
		
		//Cargamos los servicios y separamos los que son examenes de los que no son examenes
		for(MedicalAppointmentService srvCns : medicalAppointmentDAO.getAppointmentItems()) {
			if(!srvCns.getService().getTipoServicio().equals("EXA")) 
				prescriptionHome.getServiciosAgregados().add(srvCns);
			else if(medicalAppointmentDAO.getInstance().getClinicalHistory() == null) {				
				ExamenConsulta examen = new ExamenConsulta();
				examen.setExamen(srvCns.getService());
				examen.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
				prescriptionHome.getExamenesAgregados().add(examen);
			}
		}		
		
	}
	
	private void loadMedicalAppointment2() {
		prescriptionHome.setExamenesAgregados(new ArrayList<ExamenConsulta>());
		prescriptionHome.setRecomendacionesAgregadas(new ArrayList<RecomendacionConsulta>());
		prescriptionHome.setDiagnosticosAgregados(new ArrayList<DiagnosticoConsulta>());
		prescriptionHome.setItemsAgregados(new ArrayList<MedicamentoConsulta>());
		prescriptionHome.setServiciosAgregados(new ArrayList<MedicalAppointmentService>());

		medicalAppointmentDAO.load2();
				
		ClinicalHistory hist = medicalAppointmentDAO.getInstance().getClinicalHistory();
		
		if(medicalAppointmentDAO.getInstance().getClinicalHistory() != null) {
			System.out.println("getClinicalHistory no es null: "+ medicalAppointmentDAO.getInstance().getClinicalHistory());
			prescriptionHome.setExamenesAgregados(generalMedicalDAO.getInstance().getMedicalAppointment().getClinicalHistory().getExamenes());
			prescriptionHome.setRecomendacionesAgregadas(generalMedicalDAO.getInstance().getMedicalAppointment().getClinicalHistory().getRecomendaciones());
			prescriptionHome.setItemsAgregados(generalMedicalDAO.getInstance().getMedicalAppointment().getClinicalHistory().getMedicamentos());
			prescriptionHome.setDiagnosticosAgregados(generalMedicalDAO.getInstance().getMedicalAppointment().getClinicalHistory().getDiagnosticos());
		}else {
			System.out.println("getClinicalHistory NULL: "+ medicalAppointmentDAO.getInstance().getClinicalHistory());
		} 
		
		//Cargamos los servicios y separamos los que son examenes de los que no son examenes
		for(MedicalAppointmentService srvCns : medicalAppointmentDAO.getAppointmentItems()) {
			if(!srvCns.getService().getTipoServicio().equals("EXA")) 
				prescriptionHome.getServiciosAgregados().add(srvCns);
			else if(medicalAppointmentDAO.getInstance().getClinicalHistory() == null) {				
				ExamenConsulta examen = new ExamenConsulta();
				examen.setExamen(srvCns.getService());
				examen.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
				prescriptionHome.getExamenesAgregados().add(examen);
			}
		}		
		
	}

	protected void init() {
		System.out.println("entro al init()");
		setMode("w");
		generalContainer.setMode("w");
		
		obtainClinicalHistory().setDoctor(doctorDAO.doctorInSession());
		loadMedicalAppointment();
		loadPastHistories();
	}

	// metodo para el registro de diagnosticos

	public void createDiagnostics() {
		if (generalContainer.getDiagnostics() == null) {
			generalContainer
					.setDiagnostics(new ArrayList<ServiceClinicalHistory>());
			for (MedicalAppointmentService serv : medicalAppointmentDAO
					.getInstance().getMedicalAppointmentServices()) {
				ServiceClinicalHistory servClinicalHistory = new ServiceClinicalHistory();
				servClinicalHistory.setService(serv.getService());
				servClinicalHistory.setMedicalAppointmentService(serv);
				servClinicalHistory.setClinicalHistory(obtainClinicalHistory());
				generalContainer.getDiagnostics().add(servClinicalHistory);
			}
		}
	}

	public void saveDiagnostics() {
		for (ServiceClinicalHistory diag : generalContainer.getDiagnostics()) {
			try{
				diag.getServiceClinicalHistoryId().getClinicalHistory();
				//System.out.println("Fecha contenida "+  diag.getClinicalHistory().getCreationDate());
			}catch (Exception e) {
				System.out.println("Fecha contenida "+  diag.getClinicalHistory().getCreationDate());
				System.out.println("EL ID DE DIAG ES NULO");
			}
			if (!entityManager.contains(diag)) {
				diag.setServiceClinicalHistoryId(new ServiceClinicalHistoryId(
						diag.getService().getId(), obtainClinicalHistory()
								.getConsecutive()));
				entityManager.persist(diag);
			}
		}
		entityManager.flush();
	}

	public String stepDiagnostic() {
		obtainClinicalHistory().setTreatmentDescriptive(
				generalContainer.getTreatment());
		obtainClinicalHistory()
				.setExamsDescriptive(generalContainer.getExams());
		
		return "next";
	}

	public String stepFinal() {
		if (medicalAppointmentDAO.getInstance().getStatus() != 1) {
			medicalAppointmentDAO.getInstance().setStatus(1);
			medicalAppointmentDAO.setEnableMessages(false);
			medicalAppointmentDAO.modify();
		}
		return null;
	}

	public void loadPastHistories() {
		System.out.println("Entro a loadPastHistories()");
		/*pastHistory.setPatient(medicalAppointmentDAO.getInstance()
				.getPatientPlan().getPatient());*/
		if (obtainClinicalHistory().getConsecutive() != null)
			System.out.println("Entro al if de loadPastHistories()");
			pastHistory.setNoShowConsecutive(obtainClinicalHistory().getConsecutive());
			pastHistory.load();
	}

	public abstract ClinicalHistory obtainClinicalHistory();

	// metodos de acceso

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Long getConsecutive() {
		return consecutive;
	}

	public void setConsecutive(Long consecutive) {
		this.consecutive = consecutive;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}