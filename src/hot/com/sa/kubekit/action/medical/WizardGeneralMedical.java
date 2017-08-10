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

@Name("wizardGeneralMedical")
@Scope(ScopeType.CONVERSATION)
//@Scope(ScopeType.SESSION)
public class WizardGeneralMedical extends WizardClinicalHistory {
	@In(create = true)
	protected MedicalAppointmentDAO medicalAppointmentDAO;

	@In(create = true)
	private GeneralMedicalDAO generalMedicalDAO;
	
	@In(create = true)
	protected ClienteHome clienteHome;
	
	@In(create = true)
	protected AntecedenteHome antecedenteHome;
	
	@In(create = true)
	protected MotivoConsultaHome motivoConsultaHome;
	
	@In(required=false, create=true)
	private PrescriptionHome prescriptionHome;
	
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	
	List<ClinicalHistory> historialAnterior = new ArrayList<ClinicalHistory>();
	
	
	private String paginaAnterior;
	private String observacionVenta;
	private boolean subsecuente=false;
	private String motivoConsultaAnterior="";
	private String resumenPaciente="";

	public WizardGeneralMedical() {
		// cofiguramos las vistas de navegacion generales
		linkDiagBack = "/medical/clinicalHistory/generalMedical/step2.xhtml";
		linkDiagNext = "/medical/clinicalHistory/generalMedical/stepFinal.xhtml";
		linkEndBack = "/medical/clinicalHistory/generalMedical/stepDiagnostic.xhtml";
		linkEndNext = "/medical/clinicalHistory/generalMedical/stepFinal.xhtml";
	}

	/*public void load() {
		
		if (getConsecutive() != null) {
			if (!generalMedicalDAO.isManaged()) {
				
				GeneralMedical instance = entityManager.find(
						GeneralMedical.class, getConsecutive());
				if (instance != null) {
					generalMedicalDAO.select(instance);
					super.load();
				}
			}
			System.out.println("Paso3");
		} else {
			this.init();
		}
		
		prescriptionHome.setInstance(generalMedicalDAO.getInstance().getMedicalAppointment().getPrescription());
	}*/
	public void load() {
		//Conversation.instance().end();
		//Conversation.instance().begin();
		//this.generalMedicalDAO.clean();
		/*super.generalContainer.setMode("w");
		setMode("w");
			setConsecutive(null);
			super.setMode("w");
			quitado 21/12/2016 cuando se puso? */
		
		
			System.out.println("*** Cargo el evento load principal: ");
			
			
			if (getConsecutive() != null) {
				if (!generalMedicalDAO.isManaged()) {
					
					GeneralMedical instance = entityManager.find(
							GeneralMedical.class, getConsecutive());
					if (instance != null) {
						generalMedicalDAO.select(instance);
						super.load();
					}
				}
				System.out.println("Paso3");
				
				generarResumen();
				
			} else {
				
				this.init();
				antecedenteHome.load();//Para carcar la lista de antecedentes disponibles
				motivoConsultaHome.load();
				
				motivoConsultaAnterior();
			}
			
			antecedenteHome.cargarAntecedentesPaciente(clienteHome.getInstance());//Para cargar antecedente de pacientes
			
			motivoConsultaHome.cargarMotivosUltimaConsulta(clienteHome.getInstance().getId());
			
			prescriptionHome.setInstance(generalMedicalDAO.getInstance().getMedicalAppointment().getPrescription());
			
		System.out.println("Id ConversationLoad "+ Conversation.instance().getId());
		
		}
	
	
	//Para boton atender cita
	public void cargarNuevaCita()
	{
		this.init();
		prescriptionHome.setInstance(generalMedicalDAO.getInstance().getMedicalAppointment().getPrescription());
	}
	
	
	//@Begin//Para boton verHistorial
	public void cargarHistorial()
	{
		if (!generalMedicalDAO.isManaged()) {
			
			GeneralMedical instance = entityManager.find(
					GeneralMedical.class, getConsecutive());
			if (instance != null) {
				generalMedicalDAO.select(instance);
				super.load();
			}
		}
		System.out.println("Paso3");
	}
	
	public void motivoConsultaAnterior()
	{
		
		System.out.println("ENTRO A MOTIVO CONSULTA ANTERIOR ************");
		historialAnterior=(List<ClinicalHistory>) entityManager.createQuery("SELECT c FROM ClinicalHistory c where c.consecutive=(SELECT MAX(m.consecutive) FROM ClinicalHistory m where m.cliente.id="+clienteHome.getInstance().getId()+" ) ").getResultList();
		
		
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
		
		
		
		if(clienteHome.getInstance().getGenero()==1)
		{
			bl.append("Masculino. ");
		}
		else
		{
			bl.append("Femenina. ");
		}
		
		bl.append("De ");
		bl.append(clienteHome.calcularEdad());
		bl.append(" años. ");
		
		
		if(clienteHome.getInstance().getOcupacion()!=null)
		{
			bl.append(clienteHome.getInstance().getOcupacion());
			bl.append(". ");
		}
			
		if(generalMedicalDAO.getInstance().getHeight()!=null)
		{
			bl.append("Estatura ");
			bl.append(generalMedicalDAO.getInstance().getHeight());
			bl.append("cm. ");
		}
		
		if(generalMedicalDAO.getInstance().getWeight()!=null)
		{
			bl.append("Peso ");
			bl.append(generalMedicalDAO.getInstance().getWeight());
			bl.append("kg. ");
		}
		
		if(generalMedicalDAO.getInstance().getConsultationReason()!=null)
		{
			bl.append("Consulta por ");
			bl.append(generalMedicalDAO.getInstance().getConsultationReason());
			bl.append(". ");
		}
		
		if(generalMedicalDAO.getInstance().getObservation()!=null)
		{
			bl.append("Evolucion ");
			bl.append(generalMedicalDAO.getInstance().getObservation());
			bl.append(". ");
		}
		
		if(generalMedicalDAO.getInstance().getEnfermedadActual()!=null)
		{
			bl.append("Enfermedad actual ");
			bl.append(generalMedicalDAO.getInstance().getEnfermedadActual());
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
				generalMedicalDAO.select(instance);
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
		
		antecedenteHome.cargarAntecedentesPaciente(clienteHome.getInstance());//Para cargar antecedente de pacientes
		prescriptionHome.setInstance(generalMedicalDAO.getInstance().getMedicalAppointment().getPrescription());
		
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
		if (clienteHome.getInstance().getGeneralInformation() == null) {
			GeneralInformation gi = new GeneralInformation(clienteHome.getInstance());
			/*List<GeneralInformation> giLis = new ArrayList<GeneralInformation>();
			giLis.add(gi);*/
			
			
			//Nuevo agregado el 05/07/2017
			List<GeneralInformation> giLisComprobar = entityManager.createQuery("SELECT g FROM GeneralInformation g where g.cliente.id=:idCliente").setParameter("idCliente", clienteHome.getInstance().getId()).getResultList();
			
			if(giLisComprobar.size()<=0)
			{
				System.out.println("Lista general information VACIA ");
				
				entityManager.persist(gi);
				clienteHome.getInstance().setGeneralInformation(gi);
			}
			
			
			System.out.println("REgistro el GeneralInformation *********");
			
		}
		generalMedicalDAO.getInstance().setCliente(
				medicalAppointmentDAO.getInstance().getCliente());
		generalMedicalDAO.getInstance().setMedicalAppointment(
				medicalAppointmentDAO.getInstance());
		
		
		System.out.println("ENTRO al INIT WizardGeneralMedical *******");
		//Nuevo
		//generalMedicalDAO.getInstance().getMedicalAppointment().set
	}

	// metodos step1
	public String step1() {
		try {
			if (clienteHome.getInstance().getGeneralInformation().getId() == null) {
				entityManager.persist(clienteHome.getInstance()
						.getGeneralInformation());
			} else {
				entityManager.merge(clienteHome.getInstance()
						.getGeneralInformation());
			}
			if (clienteHome.modify()) {
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
		
		if(prescriptionHome.getExamenesAgregados().isEmpty() && prescriptionHome.getServiciosAgregados().isEmpty()){
			FacesMessages.instance().add("No hay ningun examen o servicio agregado");
			
			return "";
		}
		else
		{
		
				try {
					if (clienteHome.getInstance().getGeneralInformation().getId() == null) {
						entityManager.persist(clienteHome.getInstance()
								.getGeneralInformation());
					} else {
						entityManager.merge(clienteHome.getInstance()
								.getGeneralInformation());
					}
					if (clienteHome.modify()) {
						//return "next";
					}
				} catch (Exception e) {
					System.out.println("catch allsteps: " + e);
					e.printStackTrace();
				}
				createDiagnostics();
				//Verificamos si se diagnostico sordera
						if(prescriptionHome.isDiagnSordera())
							clienteHome.getInstance().setDiagnosSordera(new Date());
						
						super.stepFinal();
						
						generalMedicalDAO.setMedicamentos(prescriptionHome.getItemsAgregados());
						
						if (!generalMedicalDAO.isManaged()) {
							if (generalMedicalDAO.save()){
								
								//Guardamos los diagnosticos, recomendaciones, examenes, servicios adicionales
								for(RecomendacionConsulta recCon : prescriptionHome.getRecomendacionesAgregadas()) {
									recCon.setConsulta(generalMedicalDAO.getInstance());
									recCon.setNomRecomendacion(recCon.getRecomendacion().getNombre());
									
									System.out.println("** consulta "+generalMedicalDAO.getInstance().getMedicalAppointment().getId());
									System.out.println("** historial "+generalMedicalDAO.getInstance().getObservation());
									
									entityManager.persist(recCon);
								}
								
								for(DiagnosticoConsulta digCon : prescriptionHome.getDiagnosticosAgregados()) {
									digCon.setConsulta(generalMedicalDAO.getInstance());
									digCon.setNomDiagnostico(digCon.getDiagnostico().getNombre());
									entityManager.persist(digCon);
								}
								
								/*for(MedicamentoLaboratorios medLabs : prescriptionHome.getM) {
									digCon.setConsulta(generalMedicalDAO.getInstance());
									digCon.setNomDiagnostico(digCon.getDiagnostico().getNombre());
									entityManager.persist(digCon);
								}*/
								
								System.out.println("TAMANIO EXAMENES AGREGADOS: "+prescriptionHome.getExamenesAgregados().size());
								//Examenes
								for(ExamenConsulta exaCon : prescriptionHome.getExamenesAgregados()) {
									
									exaCon.setConsulta(generalMedicalDAO.getInstance());
									exaCon.setNomExamen(exaCon.getExamen().getName());
									entityManager.persist(exaCon);
									System.out.println("PERSISTIO NUEVO EXAMEN ******");
								}
								
								System.out.println("TAMANIO SERVICIOS AGREGADOS: "+prescriptionHome.getServiciosAgregados().size());
								for(MedicalAppointmentService srv : prescriptionHome.getServiciosAgregados()) {
									if(medicalAppointmentDAO.getAppointmentItems().contains(srv)) 
										medicalAppointmentDAO.getAppointmentItems().remove(srv);
									else {
										//srv.setServiceClinicalHistory();
										MedicalAppointmentServiceId id = new MedicalAppointmentServiceId(
												medicalAppointmentDAO.getInstance().getId(), srv.getService().getId());
										MedicalAppointmentService med = new MedicalAppointmentService();
										med.setMedicalAppointmentServiceId(id);
										med.setService(srv.getService());
										med.setMedicalAppointment(medicalAppointmentDAO.getInstance());
										entityManager.persist(med);
									}
								}
								
								
								//Nuevo agregado el 09/06/2017
								//prescriptionHome.verificarServiciosExamenesEliminados();
								/*if(prescriptionHome.getServiciosYexamenesEliminados().size()>0)
								{
									//medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().removeAll(prescriptionHome.getServiciosYexamenesEliminados());
									
									
									for(MedicalAppointmentService srv:prescriptionHome.getServiciosYexamenesEliminados())
									{
										System.out.println("SERVICIO A ELIMINAR: "+srv.getService().getName());
										medicalAppointmentDAO.getInstance().getMedicalAppointmentServices().remove(srv);
										entityManager.remove(srv);
									}
									entityManager.flush();
									System.out.println("ELIMINO LOS SERVICIOS DESDE EL WIZARD **********");
								}
								*/
								
								//Guardar aqui los nuevos motivos de consulta
								
								saveDiagnostics();
								
								
								prescriptionHome.getInstance().setMedicalAppointment(generalMedicalDAO.getInstance().getMedicalAppointment());
								prescriptionHome.save();
								FacesMessages.instance().add(sainv_messages
										.get("history_created"));
								//Generamos un detalle de la venta
								Double totalReparacion = 0d;
								VentaProdServ vta = new VentaProdServ();
								
								
								//nuevo el 17/07/2017
								//Verificar si no existe una venta existente
								List<VentaProdServ> vtaExis = entityManager.createQuery("SELECT v FROM VentaProdServ v where v.cliente.id=:idCliente and DATE(v.fechaVenta)=:fechaHoy and v.tipoVenta<>'CMB' and v.estado='PEN'").setParameter("idCliente", clienteHome.getInstance().getId()).setParameter("fechaHoy", new Date()).getResultList();
								
								if(vtaExis.size()>0)
								{
									vta = vtaExis.get(0);
									vta.setObservacion(observacionVenta);
								}
								else
								{
									vta.setCliente(clienteHome.getInstance());
									vta.setDetalle("Servicios medicos - " + medicalAppointmentDAO.getInstance().getComment());
									vta.setEmpresa(medicalAppointmentDAO.getLoginUser().getUser().getSucursal().getEmpresa());
									
									vta.setEstado("PEN");
									vta.setFechaVenta(new Date());
									vta.setIdDetalle(medicalAppointmentDAO.getInstance().getId());
									vta.setMonto(0.0f);
									//vta.setSucursal(medicalAppointmentDAO.getLoginUser().getUser().getSucursal()); //Cambiar para que al gaurdar reparacion guarde la sucursal
									vta.setSucursal(medicalAppointmentDAO.getInstance().getSucursal());//Se cambio a que la venta sera tomada en cuenta segun la sucursal especificada en la cita, esta sera espeficiada por la persona que la prog
									System.out.println("*******************1111 sucursal "+ medicalAppointmentDAO.getInstance().getSucursal());
									vta.setTipoVenta("CST");
									vta.setUsrEfectua(medicalAppointmentDAO.getLoginUser().getUser());
									
									//Agregado el 09/02/2017
									vta.setObservacion(observacionVenta);
									entityManager.persist(vta);
								
								}
								
								//Servicios registrados 
								entityManager.flush();
								entityManager.refresh(medicalAppointmentDAO.getInstance());
								entityManager.refresh(generalMedicalDAO.getInstance());
								List<Service> serviciosCobrados = new ArrayList<Service>();
								
								for(MedicalAppointmentService tmpSrv: medicalAppointmentDAO.getInstance().getMedicalAppointmentServices()) {
									
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
								for(ExamenConsulta tmpSrv: generalMedicalDAO.getInstance().getExamenes()) {
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
								antecedenteHome.persistirAntecedentesLista();
								
							}
						} else if (generalMedicalDAO.modify()){
							saveDiagnostics();
							prescriptionHome.modify();
							FacesMessages.instance().add(sainv_messages
									.get("history_modified"));
						}
						
		
				return "exito";
		}
	}
	
	@Override
	public String stepFinal() {
		//Verificamos si se diagnostico sordera
		if(prescriptionHome.isDiagnSordera())
			clienteHome.getInstance().setDiagnosSordera(new Date());
		
		super.stepFinal();
		
		generalMedicalDAO.setMedicamentos(prescriptionHome.getItemsAgregados());
		
		if (!generalMedicalDAO.isManaged()) {
			if (generalMedicalDAO.save()){
				
				//Guardamos los diagnosticos, recomendaciones, examenes, servicios adicionales
				for(RecomendacionConsulta recCon : prescriptionHome.getRecomendacionesAgregadas()) {
					recCon.setConsulta(generalMedicalDAO.getInstance());
					System.out.println("** consulta "+generalMedicalDAO.getInstance().getMedicalAppointment().getId());
					System.out.println("** historial "+generalMedicalDAO.getInstance().getObservation());
					
					recCon.setNomRecomendacion(recCon.getRecomendacion().getNombre());
					entityManager.persist(recCon);
				}
				
				for(DiagnosticoConsulta digCon : prescriptionHome.getDiagnosticosAgregados()) {
					digCon.setConsulta(generalMedicalDAO.getInstance());
					digCon.setNomDiagnostico(digCon.getDiagnostico().getNombre());
					entityManager.persist(digCon);
				}
				
				for(ExamenConsulta exaCon : prescriptionHome.getExamenesAgregados()) {
					exaCon.setConsulta(generalMedicalDAO.getInstance());
					exaCon.setNomExamen(exaCon.getExamen().getName());
					entityManager.persist(exaCon);
				}
				
				for(MedicalAppointmentService srv : prescriptionHome.getServiciosAgregados()) {
					if(medicalAppointmentDAO.getAppointmentItems().contains(srv)) 
						medicalAppointmentDAO.getAppointmentItems().remove(srv);
					else {
						//srv.setServiceClinicalHistory();
						MedicalAppointmentServiceId id = new MedicalAppointmentServiceId(
								medicalAppointmentDAO.getInstance().getId(), srv.getService().getId());
						MedicalAppointmentService med = new MedicalAppointmentService();
						med.setMedicalAppointmentServiceId(id);
						med.setService(srv.getService());
						med.setMedicalAppointment(medicalAppointmentDAO.getInstance());
						entityManager.persist(med);
					}
				}
				
				saveDiagnostics();
				prescriptionHome.getInstance().setMedicalAppointment(generalMedicalDAO.getInstance().getMedicalAppointment());
				prescriptionHome.save();
				FacesMessages.instance().add(sainv_messages
						.get("history_created"));
				//Generamos un detalle de la venta
				Double totalReparacion = 0d;
				VentaProdServ vta = new VentaProdServ();
				vta.setCliente(clienteHome.getInstance());
				vta.setDetalle("Servicios medicos - " + medicalAppointmentDAO.getInstance().getComment());
				vta.setEmpresa(medicalAppointmentDAO.getLoginUser().getUser().getSucursal().getEmpresa());
			
				
				vta.setEstado("PEN");
				vta.setFechaVenta(new Date());
				vta.setIdDetalle(medicalAppointmentDAO.getInstance().getId());
				vta.setMonto(0.0f);
				//vta.setSucursal(medicalAppointmentDAO.getLoginUser().getUser().getSucursal()); //Cambiar para que al gaurdar reparacion guarde la sucursal
				vta.setSucursal(medicalAppointmentDAO.getInstance().getSucursal());//Se cambio a que la venta sera tomada en cuenta segun la sucursal especificada en la cita, esta sera espeficiada por la persona que la prog
				System.out.println("*******************2222 sucursal "+ medicalAppointmentDAO.getInstance().getSucursal());
				vta.setTipoVenta("CST");
				vta.setUsrEfectua(medicalAppointmentDAO.getLoginUser().getUser());
				//Agregado el 09/02/2017
				vta.setObservacion(observacionVenta);
				
				entityManager.persist(vta);
				//Servicios registrados 
				entityManager.flush();
				entityManager.refresh(medicalAppointmentDAO.getInstance());
				entityManager.refresh(generalMedicalDAO.getInstance());
				List<Service> serviciosCobrados = new ArrayList<Service>();
				for(MedicalAppointmentService tmpSrv: medicalAppointmentDAO.getInstance().getMedicalAppointmentServices()) {
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
				for(ExamenConsulta tmpSrv: generalMedicalDAO.getInstance().getExamenes()) {
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
		} else if (generalMedicalDAO.modify()){
			saveDiagnostics();
			prescriptionHome.modify();
			FacesMessages.instance().add(sainv_messages
					.get("history_modified"));
		}
		return "exito";
	}
	
	

	@Override
	public ClinicalHistory obtainClinicalHistory() {
		return generalMedicalDAO.getInstance();
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