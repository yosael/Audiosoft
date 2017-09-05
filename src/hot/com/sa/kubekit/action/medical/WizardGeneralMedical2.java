package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.sales.VentaProdServHome;
import com.sa.model.medical.ClinicalHistory;
import com.sa.model.medical.DiagnosticoConsulta;
import com.sa.model.medical.ExamenConsulta;
import com.sa.model.medical.GeneralInformation;
import com.sa.model.medical.GeneralMedical;
import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.medical.RecomendacionConsulta;
import com.sa.model.medical.id.MedicalAppointmentServiceId;
import com.sa.model.sales.DetVentaProdServ;
import com.sa.model.sales.Service;
import com.sa.model.sales.VentaProdServ;

@Name("wizardGeneralMedical2")
@Scope(ScopeType.CONVERSATION)
//@Scope(ScopeType.SESSION)
public class WizardGeneralMedical2 extends WizardClinicalHistory2 {
	@In(create = true)
	protected MedicalAppointmentDAO2 medicalAppointmentDAO2;

	@In(create = true)
	private GeneralMedicalDAO2 generalMedicalDAO2;
	
	@In(create = true)
	protected ClienteHome2 clienteHome2;
	
	@In(create = true)
	protected AntecedenteHome2 antecedenteHome2;
	
	@In(create = true)
	protected MotivoConsultaHome motivoConsultaHome;
	
	@In(required=false, create=true)
	private PrescriptionHome2 prescriptionHome2;
	
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	
	List<ClinicalHistory> historialAnterior = new ArrayList<ClinicalHistory>();
	
	
	private String paginaAnterior;
	private String observacionVenta;
	private boolean subsecuente=false;
	private String motivoConsultaAnterior="";
	private String resumenPaciente="";

	public WizardGeneralMedical2() {
		// cofiguramos las vistas de navegacion generales
		linkDiagBack = "/medical/clinicalHistory/generalMedical/step2.xhtml";
		linkDiagNext = "/medical/clinicalHistory/generalMedical/stepFinal.xhtml";
		linkEndBack = "/medical/clinicalHistory/generalMedical/stepDiagnostic.xhtml";
		linkEndNext = "/medical/clinicalHistory/generalMedical/stepFinal.xhtml";
	}

	/*public void load() {
		
		if (getConsecutive() != null) {
			if (!generalMedicalDAO2.isManaged()) {
				
				GeneralMedical instance = entityManager.find(
						GeneralMedical.class, getConsecutive());
				if (instance != null) {
					generalMedicalDAO2.select(instance);
					super.load();
				}
			}
			System.out.println("Paso3");
		} else {
			this.init();
		}
		
		prescriptionHome2.setInstance(generalMedicalDAO2.getInstance().getMedicalAppointment().getPrescription());
	}*/
	public void load() {
		//Conversation.instance().end();
		//Conversation.instance().begin();
		//this.generalMedicalDAO2.clean();
		/*super.generalContainer.setMode("w");
		setMode("w");
			setConsecutive(null);
			super.setMode("w");
			quitado 21/12/2016 cuando se puso? */
		
		
			System.out.println("*** Cargo el evento load principal: ");
			
			
			if (getConsecutive() != null) {
				if (!generalMedicalDAO2.isManaged()) {
					
					GeneralMedical instance = entityManager.find(
							GeneralMedical.class, getConsecutive());
					if (instance != null) {
						generalMedicalDAO2.select(instance);
						super.load();
					}
				}
				System.out.println("Paso3");
				
				generarResumen();
				
			} else {
				
				this.init();
				antecedenteHome2.load();//Para carcar la lista de antecedentes disponibles
				motivoConsultaHome.load();
				
				motivoConsultaAnterior();
			}
			
			antecedenteHome2.cargarAntecedentesPaciente(clienteHome2.getInstance());//Para cargar antecedente de pacientes
			
			motivoConsultaHome.cargarMotivosUltimaConsulta(clienteHome2.getInstance().getId());
			
			prescriptionHome2.setInstance(generalMedicalDAO2.getInstance().getMedicalAppointment().getPrescription());
			
		System.out.println("Id ConversationLoad "+ Conversation.instance().getId());
		
		}
	
	
	//Para boton atender cita
	public void cargarNuevaCita()
	{
		this.init();
		prescriptionHome2.setInstance(generalMedicalDAO2.getInstance().getMedicalAppointment().getPrescription());
	}
	
	
	//@Begin//Para boton verHistorial
	public void cargarHistorial()
	{
		if (!generalMedicalDAO2.isManaged()) {
			
			GeneralMedical instance = entityManager.find(
					GeneralMedical.class, getConsecutive());
			if (instance != null) {
				generalMedicalDAO2.select(instance);
				super.load();
			}
		}
		System.out.println("Paso3");
	}
	
	public void motivoConsultaAnterior()
	{
		
		System.out.println("ENTRO A MOTIVO CONSULTA ANTERIOR ************");
		historialAnterior=(List<ClinicalHistory>) entityManager.createQuery("SELECT c FROM ClinicalHistory c where c.consecutive=(SELECT MAX(m.consecutive) FROM ClinicalHistory m where m.cliente.id="+clienteHome2.getInstance().getId()+" ) ").getResultList();
		
		
		if(historialAnterior.size()>0)
		{
			System.out.println("ENTRO A ES SUBSECUENTE ************");
			motivoConsultaAnterior = historialAnterior.get(0).getConsultationReason();
			subsecuente=true;
			
		}
	}
	
	
	public void generarResumen()
	{
		System.out.println("Entro a resumen paciente");
		
		
		StringBuilder bl = new StringBuilder();
		//resumenPaciente="";
		
		
		
		if(clienteHome2.getInstance().getGenero()==1)
		{
			bl.append("Masculino. ");
		}
		else
		{
			bl.append("Femenina. ");
		}
		
		bl.append("De ");
		bl.append(clienteHome2.calcularEdad());
		bl.append(" años. ");
		
		
		if(clienteHome2.getInstance().getOcupacion()!=null)
		{
			bl.append(clienteHome2.getInstance().getOcupacion());
			bl.append(". ");
		}
			
		if(generalMedicalDAO2.getInstance().getHeight()!=null)
		{
			bl.append("Estatura ");
			bl.append(generalMedicalDAO2.getInstance().getHeight());
			bl.append("cm. ");
		}
		
		if(generalMedicalDAO2.getInstance().getWeight()!=null)
		{
			bl.append("Peso ");
			bl.append(generalMedicalDAO2.getInstance().getWeight());
			bl.append("kg. ");
		}
		
		if(generalMedicalDAO2.getInstance().getConsultationReason()!=null)
		{
			bl.append("Consulta por ");
			bl.append(generalMedicalDAO2.getInstance().getConsultationReason());
			bl.append(". ");
		}
		
		if(generalMedicalDAO2.getInstance().getObservation()!=null)
		{
			bl.append("Evolucion ");
			bl.append(generalMedicalDAO2.getInstance().getObservation());
			bl.append(". ");
		}
		
		if(generalMedicalDAO2.getInstance().getEnfermedadActual()!=null)
		{
			bl.append("Enfermedad actual ");
			bl.append(generalMedicalDAO2.getInstance().getEnfermedadActual());
			bl.append(". ");
		}
		
		resumenPaciente=bl.toString();
		
		System.out.println(resumenPaciente);
	}
	
	
	public void load2() {
		//Conversation.instance().end();
		//Conversation.instance().begin();
		
		if (getConsecutive() != null) {
			System.out.println("Paso1");
			GeneralMedical instance = entityManager.find(
					GeneralMedical.class, getConsecutive());
			if (instance != null) {
				generalMedicalDAO2.select(instance);
				try {
					super.load2();
				} catch (Exception e) {
					System.err.println("error en super.load(): " + e);
				}
			}
		} else {
			System.out.println("entro a crear el .init()");
			this.init();
		}
		
		antecedenteHome2.cargarAntecedentesPaciente(clienteHome2.getInstance());//Para cargar antecedente de pacientes
		prescriptionHome2.setInstance(generalMedicalDAO2.getInstance().getMedicalAppointment().getPrescription());
		
		System.out.println("Id ConversationLoad2 "+ Conversation.instance().getId());
	}
	
	public void cerrarConver(Conversation conver)
	{
		conver.end();
	}
	
	public void mostrarIdC(int idC)
	{
		System.out.println("Id conversation "+idC);
	}
	
	public void metodoPrueba()
	{
		System.out.println("Ingreso a metodo prueba");
	}

	public void init() {
		
		
		super.init();
		
		
		// configuraciones iniciales
		if (clienteHome2.getInstance().getGeneralInformation() == null) {
			GeneralInformation gi = new GeneralInformation(clienteHome2.getInstance());
			/*List<GeneralInformation> giLis = new ArrayList<GeneralInformation>();
			giLis.add(gi);*/
			
			
			//Nuevo agregado el 05/07/2017
			List<GeneralInformation> giLisComprobar = entityManager.createQuery("SELECT g FROM GeneralInformation g where g.cliente.id=:idCliente").setParameter("idCliente", clienteHome2.getInstance().getId()).getResultList();
			
			if(giLisComprobar.size()<=0)
			{
				System.out.println("Lista general information VACIA ");
				
				entityManager.persist(gi);
				clienteHome2.getInstance().setGeneralInformation(gi);
			}
			
			
			System.out.println("REgistro el GeneralInformation *********");
			
		}
		generalMedicalDAO2.getInstance().setCliente(
				medicalAppointmentDAO2.getInstance().getCliente());
		generalMedicalDAO2.getInstance().setMedicalAppointment(
				medicalAppointmentDAO2.getInstance());
		
		
		System.out.println("ENTRO al INIT wizardGeneralMedical2 *******");
		//Nuevo
		//generalMedicalDAO2.getInstance().getMedicalAppointment().set
	}

	// metodos step1
	public String step1() {
		try {
			if (clienteHome2.getInstance().getGeneralInformation().getId() == null) {
				entityManager.persist(clienteHome2.getInstance()
						.getGeneralInformation());
			} else {
				entityManager.merge(clienteHome2.getInstance()
						.getGeneralInformation());
			}
			if (clienteHome2.modify()) {
				return "next";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// metodos step2
	public String step2() {
		// validamos datos del paso dos
		return step1();
	}

	// metodos step3
	public String step3() {
		// validamos datos del paso tres
		return "next"; 
	}
	
	public String allSteps(){
		
		FacesMessages.instance().clear();
		
		if(prescriptionHome2.getExamenesAgregados().isEmpty() && prescriptionHome2.getServiciosAgregados().isEmpty()){
			FacesMessages.instance().add("No hay ningun examen o servicio agregado");
			
			return "";
		}
		else
		{
		
				try {
					if (clienteHome2.getInstance().getGeneralInformation().getId() == null) {
						entityManager.persist(clienteHome2.getInstance()
								.getGeneralInformation());
					} else {
						entityManager.merge(clienteHome2.getInstance()
								.getGeneralInformation());
					}
					if (clienteHome2.modify()) {
						//return "next";
					}
				} catch (Exception e) {
					System.out.println("catch allsteps: " + e);
					e.printStackTrace();
				}
				createDiagnostics();
				//Verificamos si se diagnostico sordera
						if(prescriptionHome2.isDiagnSordera())
							clienteHome2.getInstance().setDiagnosSordera(new Date());
						
						super.stepFinal();
						
						generalMedicalDAO2.setMedicamentos(prescriptionHome2.getItemsAgregados());
						
						if (!generalMedicalDAO2.isManaged()) {
							if (generalMedicalDAO2.save()){
								
								//Guardamos los diagnosticos, recomendaciones, examenes, servicios adicionales
								for(RecomendacionConsulta recCon : prescriptionHome2.getRecomendacionesAgregadas()) {
									recCon.setConsulta(generalMedicalDAO2.getInstance());
									recCon.setNomRecomendacion(recCon.getRecomendacion().getNombre());
									
									System.out.println("** consulta "+generalMedicalDAO2.getInstance().getMedicalAppointment().getId());
									System.out.println("** historial "+generalMedicalDAO2.getInstance().getObservation());
									
									entityManager.persist(recCon);
								}
								
								for(DiagnosticoConsulta digCon : prescriptionHome2.getDiagnosticosAgregados()) {
									digCon.setConsulta(generalMedicalDAO2.getInstance());
									digCon.setNomDiagnostico(digCon.getDiagnostico().getNombre());
									entityManager.persist(digCon);
								}
								
								/*for(MedicamentoLaboratorios medLabs : prescriptionHome2.getM) {
									digCon.setConsulta(generalMedicalDAO2.getInstance());
									digCon.setNomDiagnostico(digCon.getDiagnostico().getNombre());
									entityManager.persist(digCon);
								}*/
								
								System.out.println("TAMANIO EXAMENES AGREGADOS: "+prescriptionHome2.getExamenesAgregados().size());
								//Examenes
								for(ExamenConsulta exaCon : prescriptionHome2.getExamenesAgregados()) {
									
									exaCon.setConsulta(generalMedicalDAO2.getInstance());
									exaCon.setNomExamen(exaCon.getExamen().getName());
									entityManager.persist(exaCon);
									System.out.println("PERSISTIO NUEVO EXAMEN ******");
								}
								
								System.out.println("TAMANIO SERVICIOS AGREGADOS: "+prescriptionHome2.getServiciosAgregados().size());
								for(MedicalAppointmentService srv : prescriptionHome2.getServiciosAgregados()) {
									if(medicalAppointmentDAO2.getAppointmentItems().contains(srv)) 
										medicalAppointmentDAO2.getAppointmentItems().remove(srv);
									else {
										//srv.setServiceClinicalHistory();
										MedicalAppointmentServiceId id = new MedicalAppointmentServiceId(
												medicalAppointmentDAO2.getInstance().getId(), srv.getService().getId());
										MedicalAppointmentService med = new MedicalAppointmentService();
										med.setMedicalAppointmentServiceId(id);
										med.setService(srv.getService());
										med.setMedicalAppointment(medicalAppointmentDAO2.getInstance());
										entityManager.persist(med);
									}
								}
								
								
								//Nuevo agregado el 09/06/2017
								//prescriptionHome2.verificarServiciosExamenesEliminados();
								/*if(prescriptionHome2.getServiciosYexamenesEliminados().size()>0)
								{
									//medicalAppointmentDAO2.getInstance().getMedicalAppointmentServices().removeAll(prescriptionHome2.getServiciosYexamenesEliminados());
									
									
									for(MedicalAppointmentService srv:prescriptionHome2.getServiciosYexamenesEliminados())
									{
										System.out.println("SERVICIO A ELIMINAR: "+srv.getService().getName());
										medicalAppointmentDAO2.getInstance().getMedicalAppointmentServices().remove(srv);
										entityManager.remove(srv);
									}
									entityManager.flush();
									System.out.println("ELIMINO LOS SERVICIOS DESDE EL WIZARD **********");
								}
								*/
								
								//Guardar aqui los nuevos motivos de consulta
								
								saveDiagnostics();
								
								
								prescriptionHome2.getInstance().setMedicalAppointment(generalMedicalDAO2.getInstance().getMedicalAppointment());
								prescriptionHome2.save();
								FacesMessages.instance().add(sainv_messages
										.get("history_created"));
								//Generamos un detalle de la venta
								Double totalReparacion = 0d;
								VentaProdServ vta = new VentaProdServ();
								
								
								//nuevo el 17/07/2017
								//Verificar si no existe una venta existente
								List<VentaProdServ> vtaExis = entityManager.createQuery("SELECT v FROM VentaProdServ v where v.cliente.id=:idCliente and DATE(v.fechaVenta)=:fechaHoy and v.tipoVenta<>'CMB' and v.estado='PEN'").setParameter("idCliente", clienteHome2.getInstance().getId()).setParameter("fechaHoy", new Date()).getResultList();
								
								if(vtaExis.size()>0)
								{
									vta = vtaExis.get(0);
									vta.setObservacion(observacionVenta);
								}
								else
								{
									vta.setCliente(clienteHome2.getInstance());
									vta.setDetalle("Servicios medicos - " + medicalAppointmentDAO2.getInstance().getComment());
									vta.setEmpresa(medicalAppointmentDAO2.getLoginUser().getUser().getSucursal().getEmpresa());
									
									vta.setEstado("PEN");
									vta.setFechaVenta(new Date());
									vta.setIdDetalle(medicalAppointmentDAO2.getInstance().getId());
									vta.setMonto(0.0f);
									//vta.setSucursal(medicalAppointmentDAO2.getLoginUser().getUser().getSucursal()); //Cambiar para que al gaurdar reparacion guarde la sucursal
									vta.setSucursal(medicalAppointmentDAO2.getInstance().getSucursal());//Se cambio a que la venta sera tomada en cuenta segun la sucursal especificada en la cita, esta sera espeficiada por la persona que la prog
									System.out.println("*******************1111 sucursal "+ medicalAppointmentDAO2.getInstance().getSucursal());
									vta.setTipoVenta("CST");
									vta.setUsrEfectua(medicalAppointmentDAO2.getLoginUser().getUser());
									
									//Agregado el 09/02/2017
									vta.setObservacion(observacionVenta);
									entityManager.persist(vta);
								
								}
								
								//Servicios registrados 
								entityManager.flush();
								entityManager.refresh(medicalAppointmentDAO2.getInstance());
								entityManager.refresh(generalMedicalDAO2.getInstance());
								List<Service> serviciosCobrados = new ArrayList<Service>();
								
								for(MedicalAppointmentService tmpSrv: medicalAppointmentDAO2.getInstance().getMedicalAppointmentServices()) {
									
									System.out.println("Nombre Servicio: "+tmpSrv.getService().getName());
									
									serviciosCobrados.add(tmpSrv.getService());
									DetVentaProdServ dtVta = new DetVentaProdServ();
									dtVta.setCantidad(1);
									StringBuilder bld = new StringBuilder();
									bld.append(tmpSrv.getService().getName());
									dtVta.setCodClasifVta("SRV");
									dtVta.setCodExacto(tmpSrv.getService().getCodigo());
									dtVta.setServicio(tmpSrv.getService());
									dtVta.setEscondido(false);
									dtVta.setDetalle(bld.toString());
									dtVta.setMonto(tmpSrv.getService().getCosto().floatValue());
									dtVta.setVenta(vta);
									dtVta.setTipoVenta("CST"); // nuevo el 17/07/2017
									totalReparacion += dtVta.getMonto()*dtVta.getCantidad();
									entityManager.persist(dtVta);
								}
								//Y los examenes tambien se adjuntan al cobro
								for(ExamenConsulta tmpSrv: generalMedicalDAO2.getInstance().getExamenes()) {
									System.out.println("Nombre examen "+tmpSrv.getExamen().getName());
									if(!serviciosCobrados.contains(tmpSrv.getExamen())) {
										DetVentaProdServ dtVta = new DetVentaProdServ();
										dtVta.setCantidad(1);
										StringBuilder bld = new StringBuilder();
										bld.append(tmpSrv.getExamen().getName());
										dtVta.setCodClasifVta("SRV");
										dtVta.setCodExacto(tmpSrv.getExamen().getCodigo());
										dtVta.setServicio(tmpSrv.getExamen());
										dtVta.setEscondido(false);
										dtVta.setDetalle(bld.toString());
										dtVta.setMonto(tmpSrv.getExamen().getCosto().floatValue());
										dtVta.setVenta(vta);
										dtVta.setTipoVenta("CST"); // nuevo el 17/07/2017
										
										totalReparacion += dtVta.getMonto()*dtVta.getCantidad();
										entityManager.persist(dtVta);
									}
								}
								
								//Actualizamos el monto de la venta
								entityManager.refresh(vta);
								vta.setMonto(vta.getMonto()+(new VentaProdServHome().moneyDecimal(totalReparacion).floatValue()));
								entityManager.merge(vta);
								
								motivoConsultaHome.persistirMotivosLista();
								antecedenteHome2.persistirAntecedentesLista();
								
							}
						} else if (generalMedicalDAO2.modify()){
							saveDiagnostics();
							prescriptionHome2.modify();
							FacesMessages.instance().add(sainv_messages
									.get("history_modified"));
						}
						
		
				return "exito";
		}
	}
	
	@Override
	public String stepFinal() {
		//Verificamos si se diagnostico sordera
		if(prescriptionHome2.isDiagnSordera())
			clienteHome2.getInstance().setDiagnosSordera(new Date());
		
		super.stepFinal();
		
		generalMedicalDAO2.setMedicamentos(prescriptionHome2.getItemsAgregados());
		
		if (!generalMedicalDAO2.isManaged()) {
			if (generalMedicalDAO2.save()){
				
				//Guardamos los diagnosticos, recomendaciones, examenes, servicios adicionales
				for(RecomendacionConsulta recCon : prescriptionHome2.getRecomendacionesAgregadas()) {
					recCon.setConsulta(generalMedicalDAO2.getInstance());
					System.out.println("** consulta "+generalMedicalDAO2.getInstance().getMedicalAppointment().getId());
					System.out.println("** historial "+generalMedicalDAO2.getInstance().getObservation());
					
					recCon.setNomRecomendacion(recCon.getRecomendacion().getNombre());
					entityManager.persist(recCon);
				}
				
				for(DiagnosticoConsulta digCon : prescriptionHome2.getDiagnosticosAgregados()) {
					digCon.setConsulta(generalMedicalDAO2.getInstance());
					digCon.setNomDiagnostico(digCon.getDiagnostico().getNombre());
					entityManager.persist(digCon);
				}
				
				for(ExamenConsulta exaCon : prescriptionHome2.getExamenesAgregados()) {
					exaCon.setConsulta(generalMedicalDAO2.getInstance());
					exaCon.setNomExamen(exaCon.getExamen().getName());
					entityManager.persist(exaCon);
				}
				
				for(MedicalAppointmentService srv : prescriptionHome2.getServiciosAgregados()) {
					if(medicalAppointmentDAO2.getAppointmentItems().contains(srv)) 
						medicalAppointmentDAO2.getAppointmentItems().remove(srv);
					else {
						//srv.setServiceClinicalHistory();
						MedicalAppointmentServiceId id = new MedicalAppointmentServiceId(
								medicalAppointmentDAO2.getInstance().getId(), srv.getService().getId());
						MedicalAppointmentService med = new MedicalAppointmentService();
						med.setMedicalAppointmentServiceId(id);
						med.setService(srv.getService());
						med.setMedicalAppointment(medicalAppointmentDAO2.getInstance());
						entityManager.persist(med);
					}
				}
				
				saveDiagnostics();
				prescriptionHome2.getInstance().setMedicalAppointment(generalMedicalDAO2.getInstance().getMedicalAppointment());
				prescriptionHome2.save();
				FacesMessages.instance().add(sainv_messages
						.get("history_created"));
				//Generamos un detalle de la venta
				Double totalReparacion = 0d;
				VentaProdServ vta = new VentaProdServ();
				vta.setCliente(clienteHome2.getInstance());
				vta.setDetalle("Servicios medicos - " + medicalAppointmentDAO2.getInstance().getComment());
				vta.setEmpresa(medicalAppointmentDAO2.getLoginUser().getUser().getSucursal().getEmpresa());
			
				
				vta.setEstado("PEN");
				vta.setFechaVenta(new Date());
				vta.setIdDetalle(medicalAppointmentDAO2.getInstance().getId());
				vta.setMonto(0.0f);
				//vta.setSucursal(medicalAppointmentDAO2.getLoginUser().getUser().getSucursal()); //Cambiar para que al gaurdar reparacion guarde la sucursal
				vta.setSucursal(medicalAppointmentDAO2.getInstance().getSucursal());//Se cambio a que la venta sera tomada en cuenta segun la sucursal especificada en la cita, esta sera espeficiada por la persona que la prog
				System.out.println("*******************2222 sucursal "+ medicalAppointmentDAO2.getInstance().getSucursal());
				vta.setTipoVenta("CST");
				vta.setUsrEfectua(medicalAppointmentDAO2.getLoginUser().getUser());
				//Agregado el 09/02/2017
				vta.setObservacion(observacionVenta);
				
				entityManager.persist(vta);
				//Servicios registrados 
				entityManager.flush();
				entityManager.refresh(medicalAppointmentDAO2.getInstance());
				entityManager.refresh(generalMedicalDAO2.getInstance());
				List<Service> serviciosCobrados = new ArrayList<Service>();
				for(MedicalAppointmentService tmpSrv: medicalAppointmentDAO2.getInstance().getMedicalAppointmentServices()) {
					serviciosCobrados.add(tmpSrv.getService());
					DetVentaProdServ dtVta = new DetVentaProdServ();
					dtVta.setCantidad(1);
					StringBuilder bld = new StringBuilder();
					bld.append(tmpSrv.getService().getName());
					dtVta.setCodClasifVta("SRV");
					dtVta.setCodExacto(tmpSrv.getService().getCodigo());
					dtVta.setServicio(tmpSrv.getService());
					dtVta.setEscondido(false);
					dtVta.setDetalle(bld.toString());
					dtVta.setMonto(tmpSrv.getService().getCosto().floatValue());
					dtVta.setVenta(vta);
					totalReparacion += dtVta.getMonto()*dtVta.getCantidad();
					entityManager.persist(dtVta);
				}
				//Y los examenes tambien se adjuntan al cobro
				for(ExamenConsulta tmpSrv: generalMedicalDAO2.getInstance().getExamenes()) {
					if(!serviciosCobrados.contains(tmpSrv.getExamen())) {
						DetVentaProdServ dtVta = new DetVentaProdServ();
						dtVta.setCantidad(1);
						StringBuilder bld = new StringBuilder();
						bld.append(tmpSrv.getExamen().getName());
						dtVta.setCodClasifVta("SRV");
						dtVta.setCodExacto(tmpSrv.getExamen().getCodigo());
						dtVta.setServicio(tmpSrv.getExamen());
						dtVta.setEscondido(false);
						dtVta.setDetalle(bld.toString());
						dtVta.setMonto(tmpSrv.getExamen().getCosto().floatValue());
						dtVta.setVenta(vta);
						totalReparacion += dtVta.getMonto()*dtVta.getCantidad();
						entityManager.persist(dtVta);
					}
				}
				//Actualizamos el monto de la venta
				entityManager.refresh(vta);
				vta.setMonto(new VentaProdServHome().moneyDecimal(totalReparacion).floatValue());
				entityManager.merge(vta);
			}
		} else if (generalMedicalDAO2.modify()){
			saveDiagnostics();
			prescriptionHome2.modify();
			FacesMessages.instance().add(sainv_messages
					.get("history_modified"));
		}
		return "exito";
	}
	
	

	@Override
	public ClinicalHistory obtainClinicalHistory() {
		return generalMedicalDAO2.getInstance();
	}

	public String getPaginaAnterior() {
		return paginaAnterior;
	}

	public void setPaginaAnterior(String paginaAnterior) {
		this.paginaAnterior = paginaAnterior;
	}

	public String getObservacionVenta() {
		return observacionVenta;
	}

	public void setObservacionVenta(String observacionVenta) {
		this.observacionVenta = observacionVenta;
	}

	public boolean isSubsecuente() {
		return subsecuente;
	}

	public void setSubsecuente(boolean subsecuente) {
		this.subsecuente = subsecuente;
	}

	public List<ClinicalHistory> getHistorialAnterior() {
		return historialAnterior;
	}

	public void setHistorialAnterior(List<ClinicalHistory> historialAnterior) {
		this.historialAnterior = historialAnterior;
	}

	public String getMotivoConsultaAnterior() {
		return motivoConsultaAnterior;
	}

	public void setMotivoConsultaAnterior(String motivoConsultaAnterior) {
		this.motivoConsultaAnterior = motivoConsultaAnterior;
	}

	public String getResumenPaciente() {
		return resumenPaciente;
	}

	public void setResumenPaciente(String resumenPaciente) {
		this.resumenPaciente = resumenPaciente;
	}

	

	
	
	
	
}