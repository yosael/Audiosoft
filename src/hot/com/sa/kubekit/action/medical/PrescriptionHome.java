package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

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
import com.sa.model.medical.ExamImagenoConsulta;
import com.sa.model.medical.ExamImagenologia;
import com.sa.model.medical.ExamenAudioConsulta;
import com.sa.model.medical.ExamenAudiologia;
import com.sa.model.medical.ExamenConsulta;
import com.sa.model.medical.ExamenLabConsulta;
import com.sa.model.medical.ExamenLaboratorio;
import com.sa.model.medical.ExamenOtoConsulta;
import com.sa.model.medical.ExamenOtoneurologia;
import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.medical.Medicamento;
import com.sa.model.medical.MedicamentoConsulta;
import com.sa.model.medical.MedicamentoLaboratorios;
import com.sa.model.medical.Prescription;
import com.sa.model.medical.RecomendacionConsulta;
import com.sa.model.medical.RecomendacionMed;
import com.sa.model.medical.ServiceClinicalHistory;
import com.sa.model.medical.id.MedicalAppointmentServiceId;
import com.sa.model.sales.Service;
import com.sa.model.workshop.ServicioReparacion;

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
	
	private Float totalServicios;
	private Float totalExamenes;
	private Float totalCobroConsulta;
	
	
	private List<ExamenAudioConsulta> lstExamenesAudioConsulta = new ArrayList<ExamenAudioConsulta>();
	private List<ExamenOtoConsulta> lstExamenesOtoConsulta = new ArrayList<ExamenOtoConsulta>();
	private List<ExamenLabConsulta> lstExamenesLabConsulta = new ArrayList<ExamenLabConsulta>();
	private List<ExamImagenoConsulta> lstExamImagenoConsulta = new ArrayList<ExamImagenoConsulta>();
	
	
	//nuevo
	
	private MedicamentoConsulta medicamentoSeleccionado =  new MedicamentoConsulta();
	
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
		
		try{
			
			for(MedicamentoConsulta tmpMed : itemsAgregados)
				if(tmpMed.getMedicamento().equals(medicm)){
					
					/*FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("prescriptionHome_error_additem"));*/
					itemsAgregados.remove(tmpMed);
					medicm.setAsociado(false);
					
					if(tmpMed.getId()!=null){
						getEntityManager().remove(tmpMed);
					}
					
					return;
				}
			
			//Revisar porque toman el indice cero, siempre estaria agarrando solo el primero de la lista????? 04/04/2017: Solo es para mostrarlo en el select, luego se selecciona
			MedicamentoConsulta item = new MedicamentoConsulta();
			item.setCantidad((short) 1);
			item.setMedicamento(medicm);
			
			if(medicm.getDosificaciones()!=null && medicm.getDosificaciones().size()>0)
				item.setSelDosif(medicm.getDosificaciones().get(0));
			else
			{
				FacesMessages.instance().add(Severity.WARN,"El medicamento no tiene dosificacion");
				return;
			}
			
			if(medicm.getPresentaciones()!=null && medicm.getPresentaciones().size()>0)
				item.setSelPresen(medicm.getPresentaciones().get(0));
			else
			{
				FacesMessages.instance().add(Severity.WARN,"El medicamento no tiene presentacion");
				return;
			}
			
			if(medicm.getMedicamentosLab()!=null && medicm.getMedicamentosLab().size()>0)
				item.setSelLab(medicm.getMedicamentosLab().get(0));
			else
			{
				FacesMessages.instance().add(Severity.WARN,"El medicamento no tiene laboratorio");
				return;
			}
			
			item.setObservacion("");
			
			itemsAgregados.add(item);
		}
		catch(NullPointerException e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un inconveniente al agregar el medicamento");
			e.printStackTrace();
		}
		catch(Exception e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un inconveniente al agregar el medicamento");
			e.printStackTrace();
		}
	}
	
	public void agregarRecomendacion(RecomendacionMed rec) {
		
		try{
			for(RecomendacionConsulta tmpRec : recomendacionesAgregadas)
			{
				if(tmpRec.getRecomendacion().equals(rec)){
					
					recomendacionesAgregadas.remove(tmpRec);
					rec.setAsociado(false);
					
					if(tmpRec.getId()!=null){
						try{
							getEntityManager().remove(tmpRec);
						}
						catch(PersistenceException e){
							e.printStackTrace();
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
					/*FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("prescriptionHome_error_addrec"));*/
					
					
					return;
				}
				
			}
			
			RecomendacionConsulta recons = new RecomendacionConsulta();
			recons.setRecomendacion(rec);
			recons.setNomRecomendacion(rec.getNombre());
			recons.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
			getEntityManager().persist(recons);
			recomendacionesAgregadas.add(recons);
			
			System.out.println("Agrego la recomendacion");
		}
		catch(PersistenceException e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un inconveniente al agregar la recomendacion. Intente con otra");
			e.printStackTrace();
		}
		catch(Exception e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un inconveniente al agregar la recomendacion. Intente con otra");
			e.printStackTrace();
		}
	}
	
	public void agregarDiagnostico(DiagnosticoMed dig) {
		
		try{
			for(DiagnosticoConsulta tmpDig : diagnosticosAgregados)
				if(tmpDig.getDiagnostico().equals(dig)){
					//diagnosticosAgregados.remove(dig);//agregado el 27/01/2018 
					
						try{
							removerDiagnostico(tmpDig);
						}
						catch(PersistenceException e){
							e.printStackTrace();
						}
						catch(Exception e){
							e.printStackTrace();
						}
						
					dig.setAsociado(false);
					return;
				}
			
			DiagnosticoConsulta diagn = new DiagnosticoConsulta();
			diagn.setDiagnostico(dig);
			diagn.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
			diagn.setPrincipal(false);
			diagn.setNomDiagnostico(dig.getNombre());
			getEntityManager().persist(diagn);
			diagnosticosAgregados.add(diagn);
		}
		catch(PersistenceException e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al agregar el diagnostico");
			e.printStackTrace();
		}
		catch(Exception e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al agregar el diagnostico");
			e.printStackTrace();
		}
	}
	
	
	public void agregarExamenAudiologia(ExamenAudiologia exam)
	{
		try{
			for(ExamenAudioConsulta examen:lstExamenesAudioConsulta)
			{
				if(examen.getExamen().getIdExamen()==exam.getIdExamen())
				{
					System.out.println("Es igual");
					removerExamenAudio(examen);
					exam.setAsociado(false);
					return;
				}
			}
			
			ExamenAudioConsulta examenAudio = new ExamenAudioConsulta();
			examenAudio.setExamen(exam);
			examenAudio.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
			getEntityManager().persist(examenAudio);
			lstExamenesAudioConsulta.add(examenAudio);
		}
		catch(PersistenceException e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al agregar el examen. Intenta con otro");
			e.printStackTrace();
		}
		catch(Exception e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al agregar el examen. Intenta con otro");
			e.printStackTrace();
		}
	}
	
	public void agregarExamenLaboratorio(ExamenLaboratorio exam)
	{
		try{
			for(ExamenLabConsulta examen:lstExamenesLabConsulta)
			{
				if(examen.getExamenLab().getIdExamenLab()==exam.getIdExamenLab())
				{
					removerExamenLab(examen);
					exam.setAsociado(false);
					
					if(examen.getId()!=null){
						getEntityManager().remove(examen);
					}
						
					return;
				}
			}
			
			ExamenLabConsulta examenLab = new ExamenLabConsulta();
			examenLab.setExamenLab(exam);
			examenLab.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
			getEntityManager().persist(examenLab);
			lstExamenesLabConsulta.add(examenLab);
		}
		catch(PersistenceException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void agregarExamenImageno(ExamImagenologia exam)
	{
		try{
			for(ExamImagenoConsulta examen:lstExamImagenoConsulta)
			{
				if(examen.getExamen().getIdExamImageno()==exam.getIdExamImageno())
				{
					removerExamenImageno(examen);
					exam.setAsociado(false);
					
					return;
				}
			}
			
			ExamImagenoConsulta examenImageno = new ExamImagenoConsulta();
			examenImageno.setExamen(exam);
			examenImageno.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
			getEntityManager().persist(examenImageno);
			lstExamImagenoConsulta.add(examenImageno);
		}
		catch(PersistenceException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void agregarExamenOtoneurologia(ExamenOtoneurologia exam)
	{
		try{
			for(ExamenOtoConsulta examen:lstExamenesOtoConsulta)
			{
				if(examen.getExamen().getIdExamenOto()==exam.getIdExamenOto())
				{
					removerExamenOtoneuro(examen);
					exam.setAsociado(false);
					return;
				}
			}
			
			ExamenOtoConsulta examenOtoneuro = new ExamenOtoConsulta();
			examenOtoneuro.setExamen(exam);
			examenOtoneuro.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
			
			getEntityManager().persist(examenOtoneuro);
			lstExamenesOtoConsulta.add(examenOtoneuro);
		}
		catch(PersistenceException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//List<HashMap<K, V>> examenes = new ArrayList<String,ExamenConsulta>();
	
	public void agregarExamen(Service exa) {
		
		try{
			int indice=0;
			for(ExamenConsulta tmpExa : examenesAgregados)
			{
				System.out.println("�ndicen examenes "+ examenesAgregados.get(indice));
				if(tmpExa.getExamen().equals(exa)){
					
					System.out.println("Entro a eliminar examen");
					
					//ExamenConsulta exam = new ExamenConsulta();
					//exam.setExamen(exa);
					//examenesAgregados.re
					
					//removerServicioExam(exa);
					//examenesAgregados.remove(indice); // comentado el 31/10/2017
					exa.setAsociado(false);
					removerExamenN(tmpExa);//nuevo agregado el 31/10/2017
					/*FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("prescriptionHome_error_addexa"));*/
					
					return;
							
					
				}
				indice++;
			}
			
			ExamenConsulta exacon = new ExamenConsulta();
			exacon.setExamen(exa);
			exacon.setComentario("");
			exacon.setAsociado(true);
			exacon.setConsulta(medicalAppointmentDAO.getInstance().getClinicalHistory());
			exacon.setNomExamen(exa.getName());
			
			//Agregado el 04/02/2018
			MedicalAppointmentServiceId medicap = new MedicalAppointmentServiceId(medicalAppointmentDAO.getInstance().getId(), exa.getId());
			MedicalAppointmentService med = new MedicalAppointmentService();
			med.setMedicalAppointmentServiceId(medicap);
			med.setService(exa);
			med.setMedicalAppointment(medicalAppointmentDAO.getInstance());
			getEntityManager().persist(med);
			/////////////////
			
			
			getEntityManager().persist(exacon);
			
			examenesAgregados.add(exacon);
		}
		catch(PersistenceException e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al agregar el examen. Intenta con otro");
			e.printStackTrace();
			
		}
		catch(Exception e){
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al agregar el examen. Intenta con otro");
			e.printStackTrace();
		}
		
	}
	
	public void agregarServicio(Service srv) {
		
		try{
			int indice=0;
			for(MedicalAppointmentService tmpSrv : serviciosAgregados)
			{
				if(tmpSrv.getService().equals(srv)){
					
					serviciosAgregados.remove(indice);
					srv.setAsociado(false);
					removerServicioS(tmpSrv);
					
					/*FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("prescriptionHome_error_addsrv"));*/
					
					return;
				}
				
				indice++;
			}
			//MedicalAppointmentService medicap = new MedicalAppointmentService();
			//medicap.setService(srv);
			
			MedicalAppointmentServiceId medicap = new MedicalAppointmentServiceId(medicalAppointmentDAO.getInstance().getId(), srv.getId());
			MedicalAppointmentService med = new MedicalAppointmentService();
			med.setMedicalAppointmentServiceId(medicap);
			med.setService(srv);
			med.setMedicalAppointment(medicalAppointmentDAO.getInstance());
			getEntityManager().persist(med);
			
			serviciosAgregados.add(med);
		}
		catch(PersistenceException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void removerItem(MedicamentoConsulta medicm){
		/*for(MedicamentoConsulta tmpMed : itemsAgregados)
			if(tmpMed.getMedicamento().equals(medicm))
				itemsAgregados.remove(medicm);*/
		
		//if(itemsAgregados.contains(medicm)) comentado el 31/10/2017
		
		itemsAgregados.remove(medicm);
		
		if(medicm.getMedicamento()!=null)
			medicm.getMedicamento().setAsociado(false);
		
		
		if(medicm.getId()!=null)
		{
			getEntityManager().remove(medicm);
		}
		
		//System.out.println("Remover medicamento");
	}
	
	public void removerExamen(ExamenConsulta exc) throws PersistenceException, Exception {
		
		
		List<MedicalAppointmentService> mService = new ArrayList<MedicalAppointmentService>();
		mService = getEntityManager().createQuery("SELECT s FROM MedicalAppointmentService s where s.medicalAppointmentServiceId.medicalAppointmentId="+medicalAppointmentDAO.getInstance().getId()+" and s.medicalAppointmentServiceId.serviceId="+exc.getExamen().getId()+" ").getResultList();
		
		exc.getExamen().setAsociado(false);
		
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
				try{	
					
					System.out.println("Servicio a eliminar: "+ mService.get(0).getService().getName());
					
					medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().remove(mService.get(0));
					//serviciosYexamenesEliminados.add(mService);
					serviciosAgregados.remove(mService.get(0));
					getEntityManager().remove(mService.get(0));
					getEntityManager().flush();
				}
				catch(PersistenceException e){
					FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el examen");
					e.printStackTrace();
				}
				catch(Exception e){
					FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el examen");
					e.printStackTrace();
				}
					
			/*	}
			}*/
			
			
		}
		else
		{
			System.out.println("NO entro a eliminar a la db");
			examenesAgregados.remove(exc);
		}
		
	}
	
	//Este metodo elimina los examenes del listado. Timpanometria,Audiometria, etc
	public void removerExamenN(ExamenConsulta exc) throws EntityNotFoundException,PersistenceException,Exception //Agregado el 31/10/2017
	{
		System.out.println("Lista examenesAgregados antes: "+examenesAgregados.size());
		examenesAgregados.remove(exc);
		System.out.println("Lista examenesAgregados dps: "+examenesAgregados.size());
		
		exc.setAsociado(false);
		
		if(exc.getId()!=null)
		{
			
			System.out.println("Elimino solo el examen de la db");
			try {
				getEntityManager().remove(exc);
			} catch (Exception e) {
				FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el examen");
				e.printStackTrace();
				
			}
			
		}
		
		List<MedicalAppointmentService> mService = new ArrayList<MedicalAppointmentService>();
		mService = getEntityManager().createQuery("SELECT s FROM MedicalAppointmentService s where s.medicalAppointmentServiceId.medicalAppointmentId="+medicalAppointmentDAO.getInstance().getId()+" and s.medicalAppointmentServiceId.serviceId="+exc.getExamen().getId()+" ").getResultList();
		
		if(mService.size()>0)
		{
			System.out.println("ENTRO AL IF DENTRO DEL FOR PARA REMOVER DESDE LA DB MEdicalAppontmentService");
			
			try{
				//if(serviciosAgregados.contains(mService.get(0)))
					//System.out.println("La lista serviciosAgregados contiene a "+mService.get(0));
				
				//serviciosAgregados.remove(mService.get(0));
				//System.out.println("Servicios agregados size antes: "+serviciosAgregados.size());
				
				//System.out.println("Tamaninio Servicio antes: "+medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().size());
				medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().remove(mService.get(0));//agregado el 03/02/2018
				medicalAppointmentDAO.getAppointmentItems().remove(exc.getExamen());//agregado el 03/02/2017
				try{
					ServiceClinicalHistory schElim = null;
					for(ServiceClinicalHistory sch:medicalAppointmentDAO.getInstance().getClinicalHistory().getServiceClinicalHistories()){
						if(sch.getService().getId()==exc.getExamen().getId()){
							schElim = sch;
						}
					}
					getEntityManager().remove(schElim);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				getEntityManager().remove(mService.get(0)); //comentado el 27/01/2017
				getEntityManager().refresh(medicalAppointmentDAO.getInstance());// agregado el 04/02/2018
				
			}
			catch(Exception e){
				FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el examen");
				e.printStackTrace();
			}
			
			try{
				getEntityManager().flush();
			}
			catch(EntityNotFoundException e){
				FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el examen");
				e.printStackTrace();
			}
			catch(PersistenceException e){
				FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el examen");
				e.printStackTrace();
			}
			catch(Exception e){
				FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el examen");
				e.printStackTrace();
			}
			
			System.out.println("Tamaninio Servicio dps: "+medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().size());
			System.out.println("Servicios agregados size dps: "+serviciosAgregados.size());
			
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
		
		try {
			
			if(srv.getMedicalAppointmentServiceId()!=null)
			{
				//getEntityManager().getTransaction().begin();
				
				//int ind=medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().indexOf(srv);
				
				System.out.println("Entro a removerServicioExam: entro al if");
				medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().remove(srv);
				medicalAppointmentDAO.getAppointmentItems().remove(srv);//agregado el 03/02/2017
				//medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().remove(ind);
				//serviciosYexamenesEliminados.add(srv);
				//medicalAppointmentDAO.getInstance().getClinicalHistory().getServiceClinicalHistories();
				medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().remove(srv);
				
				//agregado el 03/02/2018
				try{
					ServiceClinicalHistory schElim = null;
					for(ServiceClinicalHistory sch:medicalAppointmentDAO.getInstance().getClinicalHistory().getServiceClinicalHistories()){
						if(sch.getService().getId()==srv.getService().getId()){
							schElim = sch;
						}
					}
					getEntityManager().remove(schElim);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				System.out.println("lista serviciosAgrgados antes: "+serviciosAgregados.size());
				serviciosAgregados.remove(srv);
				getEntityManager().remove(srv); //comentado el 31/10/2017
				
				getEntityManager().refresh(medicalAppointmentDAO.getInstance());// agregado el 04/02/2018
				
				try{
					getEntityManager().flush();
				}
				catch(EntityNotFoundException e){
					FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el servicio");
					e.printStackTrace();
				}
				catch(PersistenceException e){
					FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el servicio");
					e.printStackTrace();
				}
				catch(Exception e){
					FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema al remover el servicio");
					e.printStackTrace();
				}
				//getEntityManager().getTransaction().commit();
				//getEntityManager().close();
				System.out.println("lista serviciosAgrgados dps: "+serviciosAgregados.size());
				
			}
			else
			{
				serviciosAgregados.remove(srv);
			}
			
		} catch (Exception e) {
			System.out.println("Entro al catch ");
			FacesMessages.instance().add(Severity.WARN,"No se pudo remover el examen. Intente haciendo un descuento para que sea invalido el cobro");
		}	
	}
	
	
	public Float calcularTotalCobro()
	{
		Float total=0F;
		totalExamenes=0F;
		totalServicios=0F;
		
		for(ExamenConsulta ex: examenesAgregados)
		{
			total+=ex.getExamen().getCosto().floatValue();
			totalExamenes+=ex.getExamen().getCosto().floatValue();
		}
		
		for(MedicalAppointmentService mService: serviciosAgregados)
		{
			total+=mService.getService().getCosto().floatValue();
			totalServicios+=mService.getService().getCosto().floatValue();
		}
		
		return total;
	}
	
	public void calcularTotalCobroC()
	{
		totalCobroConsulta=0F;
		totalExamenes=0F;
		totalServicios=0F;
		
		for(ExamenConsulta ex: examenesAgregados)
		{
			totalCobroConsulta+=ex.getExamen().getCosto().floatValue();
			totalExamenes+=ex.getExamen().getCosto().floatValue();
		}
		
		for(MedicalAppointmentService mService: serviciosAgregados)
		{
			totalCobroConsulta+=mService.getService().getCosto().floatValue();
			totalServicios+=mService.getService().getCosto().floatValue();
		}
		
		
	}
	
	public void removerServicioConsul(Service srv)
	{
		
	}
	
	public void removerDiagnostico(DiagnosticoConsulta diagn) throws PersistenceException,Exception {
		
		try{
			diagn.getDiagnostico().setAsociado(false);
			diagnosticosAgregados.remove(diagn);
			if(diagn.getId()!=null)
			{
				getEntityManager().remove(diagn);
				//getEntityManager().flush();
			}
		}
		catch(PersistenceException e){
			throw new PersistenceException(e);
		}
		catch(Exception e){
			throw new Exception(e);
		}
	}
	
	public void removerRecomendacion(RecomendacionConsulta recm) {
		recomendacionesAgregadas.remove(recm);
		
		if(recm.getId()!=null)
		{
			getEntityManager().remove(recm);
		}
	}
	
	public void removerServicio(MedicalAppointmentService srv) {
		
		serviciosAgregados.remove(srv);
	}
	
	public void removerExamenAudio(ExamenAudioConsulta examenConsulta)
	{
		examenConsulta.getExamen().setAsociado(false);
		lstExamenesAudioConsulta.remove(examenConsulta);
		
		if(examenConsulta.getId()!=null)
		{
			getEntityManager().remove(examenConsulta);
		}
	}
	
	public void removerExamenLab(ExamenLabConsulta examenConsulta)
	{
		examenConsulta.getExamenLab().setAsociado(false);
		lstExamenesLabConsulta.remove(examenConsulta);
		
		if(examenConsulta.getId()!=null)
		{
			getEntityManager().remove(examenConsulta);
		}
	}
	
	public void removerExamenImageno(ExamImagenoConsulta examenConsulta) throws PersistenceException,Exception
	{
		try{
			examenConsulta.getExamen().setAsociado(false);
			lstExamImagenoConsulta.remove(examenConsulta);
			
			if(examenConsulta.getId()!=null)
			{
				getEntityManager().remove(examenConsulta);
			}
		}
		catch(PersistenceException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void removerExamenOtoneuro(ExamenOtoConsulta examenConsulta)
	{
		examenConsulta.getExamen().setAsociado(false);
		lstExamenesOtoConsulta.remove(examenConsulta);
		
		if(examenConsulta.getId()!=null)
		{
			getEntityManager().remove(examenConsulta);
		}
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
	
	public void seleccionarInfoMedicamento(MedicamentoConsulta md){
		this.medicamentoSeleccionado = md;
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

	public Float getTotalServicios() {
		return totalServicios;
	}

	public void setTotalServicios(Float totalServicios) {
		this.totalServicios = totalServicios;
	}

	public Float getTotalExamenes() {
		return totalExamenes;
	}

	public void setTotalExamenes(Float totalExamenes) {
		this.totalExamenes = totalExamenes;
	}

	public Float getTotalCobroConsulta() {
		return totalCobroConsulta;
	}

	public void setTotalCobroConsulta(Float totalCobroConsulta) {
		this.totalCobroConsulta = totalCobroConsulta;
	}

	public List<ExamenAudioConsulta> getLstExamenesAudioConsulta() {
		return lstExamenesAudioConsulta;
	}

	public void setLstExamenesAudioConsulta(
			List<ExamenAudioConsulta> lstExamenesAudioConsulta) {
		this.lstExamenesAudioConsulta = lstExamenesAudioConsulta;
	}

	public List<ExamenOtoConsulta> getLstExamenesOtoConsulta() {
		return lstExamenesOtoConsulta;
	}

	public void setLstExamenesOtoConsulta(
			List<ExamenOtoConsulta> lstExamenesOtoConsulta) {
		this.lstExamenesOtoConsulta = lstExamenesOtoConsulta;
	}

	public List<ExamenLabConsulta> getLstExamenesLabConsulta() {
		return lstExamenesLabConsulta;
	}

	public void setLstExamenesLabConsulta(
			List<ExamenLabConsulta> lstExamenesLabConsulta) {
		this.lstExamenesLabConsulta = lstExamenesLabConsulta;
	}

	public List<ExamImagenoConsulta> getLstExamImagenoConsulta() {
		return lstExamImagenoConsulta;
	}

	public void setLstExamImagenoConsulta(
			List<ExamImagenoConsulta> lstExamImagenoConsulta) {
		this.lstExamImagenoConsulta = lstExamImagenoConsulta;
	}

	public MedicamentoConsulta getMedicamentoSeleccionado() {
		return medicamentoSeleccionado;
	}

	public void setMedicamentoSeleccionado(MedicamentoConsulta medicamentoSeleccionado) {
		this.medicamentoSeleccionado = medicamentoSeleccionado;
	}

	
	
	
	
}
