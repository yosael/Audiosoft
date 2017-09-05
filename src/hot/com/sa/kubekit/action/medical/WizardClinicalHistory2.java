package com.sa.kubekit.action.medical;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;

import com.sa.model.medical.ClinicalHistory;
import com.sa.model.medical.DiagnosticoConsulta;
import com.sa.model.medical.ExamenConsulta;
import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.medical.MedicamentoConsulta;
import com.sa.model.medical.RecomendacionConsulta;
import com.sa.model.medical.ServiceClinicalHistory;
import com.sa.model.medical.id.ServiceClinicalHistoryId;

public abstract class WizardClinicalHistory2 {

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
	protected ClienteHome2 clienteHome2;

	@In(create = true)
	protected MedicalAppointmentDAO2 medicalAppointmentDAO2;

	@In(create = true)
	protected GeneralContainer2 generalContainer2;
	
	@In(create = true)
	private PrescriptionHome2 prescriptionHome2;

	@In(create = true)
	private DoctorDAO doctorDAO;

	@In(create = true)
	private PastHistory pastHistory;
	
	@In(create = true)
	private GeneralMedicalDAO2 generalMedicalDAO2;

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
			generalContainer2.setMode("w");
		} else {
			System.out.println("super else");
			setMode("r");
			generalContainer2.setMode("r");
		}
		System.out.println("super lo demas: generalConteiner:  " + generalContainer2.getMode());
		loadMedicalAppointment();
		// cargar los datos generales
		generalContainer2.setTreatment(obtainClinicalHistory()
				.getTreatmentDescriptive());
		generalContainer2
				.setExams(obtainClinicalHistory().getExamsDescriptive());
		generalContainer2.setDiagnostics(new ArrayList<ServiceClinicalHistory>(
				obtainClinicalHistory().getServiceClinicalHistories()));
		loadPastHistories();
	}
	
	
	protected void load2() {
		// logica de permisos
		if (doctorDAO.doctorInSession() != null
				&& doctorDAO.doctorInSession().getId().equals(
						obtainClinicalHistory().getDoctor().getId())) {
			setMode("w");
			generalContainer2.setMode("w");
		} else {
			setMode("r");
			generalContainer2.setMode("r");
		}
		loadMedicalAppointment2();
		// cargar los datos generales
		generalContainer2.setTreatment(obtainClinicalHistory()
				.getTreatmentDescriptive());
		generalContainer2
				.setExams(obtainClinicalHistory().getExamsDescriptive());
		generalContainer2.setDiagnostics(new ArrayList<ServiceClinicalHistory>(
				obtainClinicalHistory().getServiceClinicalHistories()));
		loadPastHistories();
		
	}

	private void loadMedicalAppointment() {
		prescriptionHome2.setExamenesAgregados(new ArrayList<ExamenConsulta>());
		prescriptionHome2.setRecomendacionesAgregadas(new ArrayList<RecomendacionConsulta>());
		prescriptionHome2.setDiagnosticosAgregados(new ArrayList<DiagnosticoConsulta>());
		prescriptionHome2.setItemsAgregados(new ArrayList<MedicamentoConsulta>());
		prescriptionHome2.setServiciosAgregados(new ArrayList<MedicalAppointmentService>());

		medicalAppointmentDAO2.load();
				
		clienteHome2.setNumId(medicalAppointmentDAO2.getInstance()
				.getCliente().getId().toString());
		clienteHome2.load(false);
		ClinicalHistory hist = medicalAppointmentDAO2.getInstance().getClinicalHistory();
		
		if(medicalAppointmentDAO2.getInstance().getClinicalHistory() != null) {
			//System.out.println("getClinicalHistory no es null: "+ medicalAppointmentDAO2.getInstance().getClinicalHistory());
			
			//System.out.println("Tamm Examenes "+medicalAppointmentDAO2.getInstance().getClinicalHistory().getExamenes().size());
			
			prescriptionHome2.setExamenesAgregados(generalMedicalDAO2.getInstance().getMedicalAppointment().getClinicalHistory().getExamenes());
			prescriptionHome2.setRecomendacionesAgregadas(generalMedicalDAO2.getInstance().getMedicalAppointment().getClinicalHistory().getRecomendaciones());
			prescriptionHome2.setItemsAgregados(generalMedicalDAO2.getInstance().getMedicalAppointment().getClinicalHistory().getMedicamentos());
			prescriptionHome2.setDiagnosticosAgregados(generalMedicalDAO2.getInstance().getMedicalAppointment().getClinicalHistory().getDiagnosticos());// comentado el 21/12/2016
			/*prescriptionHome2.setExamenesAgregados(medicalAppointmentDAO2.getInstance().getClinicalHistory().getExamenes());
			prescriptionHome2.setRecomendacionesAgregadas(medicalAppointmentDAO2.getInstance().getClinicalHistory().getRecomendaciones());
			prescriptionHome2.setItemsAgregados(medicalAppointmentDAO2.getInstance().getClinicalHistory().getMedicamentos());
			prescriptionHome2.setDiagnosticosAgregados(medicalAppointmentDAO2.getInstance().getClinicalHistory().getDiagnosticos());*/
			System.out.println("ENTRO HA WizardClinicalHistory--->loadMedicalAppointment if != null ");
		} 
		
		//Cargamos los servicios y separamos los que son examenes de los que no son examenes
		for(MedicalAppointmentService srvCns : medicalAppointmentDAO2.getAppointmentItems()) {
			if(!srvCns.getService().getTipoServicio().equals("EXA")) 
				prescriptionHome2.getServiciosAgregados().add(srvCns);
			else if(medicalAppointmentDAO2.getInstance().getClinicalHistory() == null) {				
				ExamenConsulta examen = new ExamenConsulta();
				examen.setExamen(srvCns.getService());
				examen.setConsulta(medicalAppointmentDAO2.getInstance().getClinicalHistory());
				prescriptionHome2.getExamenesAgregados().add(examen);
			}
		}		
		
	}
	
	private void loadMedicalAppointment2() {
		prescriptionHome2.setExamenesAgregados(new ArrayList<ExamenConsulta>());
		prescriptionHome2.setRecomendacionesAgregadas(new ArrayList<RecomendacionConsulta>());
		prescriptionHome2.setDiagnosticosAgregados(new ArrayList<DiagnosticoConsulta>());
		prescriptionHome2.setItemsAgregados(new ArrayList<MedicamentoConsulta>());
		prescriptionHome2.setServiciosAgregados(new ArrayList<MedicalAppointmentService>());

		medicalAppointmentDAO2.load2();
				
		ClinicalHistory hist = medicalAppointmentDAO2.getInstance().getClinicalHistory();
		
		if(medicalAppointmentDAO2.getInstance().getClinicalHistory() != null) {
			System.out.println("getClinicalHistory no es null: "+ medicalAppointmentDAO2.getInstance().getClinicalHistory());
			prescriptionHome2.setExamenesAgregados(generalMedicalDAO2.getInstance().getMedicalAppointment().getClinicalHistory().getExamenes());
			prescriptionHome2.setRecomendacionesAgregadas(generalMedicalDAO2.getInstance().getMedicalAppointment().getClinicalHistory().getRecomendaciones());
			prescriptionHome2.setItemsAgregados(generalMedicalDAO2.getInstance().getMedicalAppointment().getClinicalHistory().getMedicamentos());
			prescriptionHome2.setDiagnosticosAgregados(generalMedicalDAO2.getInstance().getMedicalAppointment().getClinicalHistory().getDiagnosticos());
		}else {
			System.out.println("getClinicalHistory NULL: "+ medicalAppointmentDAO2.getInstance().getClinicalHistory());
		} 
		
		//Cargamos los servicios y separamos los que son examenes de los que no son examenes
		for(MedicalAppointmentService srvCns : medicalAppointmentDAO2.getAppointmentItems()) {
			if(!srvCns.getService().getTipoServicio().equals("EXA")) 
				prescriptionHome2.getServiciosAgregados().add(srvCns);
			else if(medicalAppointmentDAO2.getInstance().getClinicalHistory() == null) {				
				ExamenConsulta examen = new ExamenConsulta();
				examen.setExamen(srvCns.getService());
				examen.setConsulta(medicalAppointmentDAO2.getInstance().getClinicalHistory());
				prescriptionHome2.getExamenesAgregados().add(examen);
			}
		}		
		
	}

	protected void init() {
		System.out.println("entro al init()");
		setMode("w");
		generalContainer2.setMode("w");
		
		obtainClinicalHistory().setDoctor(doctorDAO.doctorInSession());
		loadMedicalAppointment();
		loadPastHistories();
	}

	// metodo para el registro de diagnosticos

	public void createDiagnostics() {
		
		if (generalContainer2.getDiagnostics() == null) 
		{
			generalContainer2.setDiagnostics(new ArrayList<ServiceClinicalHistory>());
			
			for (MedicalAppointmentService serv : medicalAppointmentDAO2.getInstance().getMedicalAppointmentServices()) 
			{
				ServiceClinicalHistory servClinicalHistory = new ServiceClinicalHistory();
				servClinicalHistory.setService(serv.getService());
				servClinicalHistory.setMedicalAppointmentService(serv);
				servClinicalHistory.setClinicalHistory(obtainClinicalHistory());
				generalContainer2.getDiagnostics().add(servClinicalHistory);
			}
		}
	}

	public void saveDiagnostics() {
		for (ServiceClinicalHistory diag : generalContainer2.getDiagnostics()) {
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
				generalContainer2.getTreatment());
		obtainClinicalHistory()
				.setExamsDescriptive(generalContainer2.getExams());
		
		return "next";
	}

	public String stepFinal() {
		if (medicalAppointmentDAO2.getInstance().getStatus() != 1) {
			medicalAppointmentDAO2.getInstance().setStatus(1);
			medicalAppointmentDAO2.setEnableMessages(false);
			medicalAppointmentDAO2.modify();
		}
		return null;
	}

	public void loadPastHistories() {
		System.out.println("Entro a loadPastHistories()");
		/*pastHistory.setPatient(medicalAppointmentDAO2.getInstance()
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