package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;


import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.EtapaRepCliente;
import com.sa.model.workshop.ItemRequisicionEta;
import com.sa.model.workshop.ReparacionCliente;
import com.sa.model.workshop.RequisicionEtapaRep;
import com.sa.model.workshop.ServicioReparacion;

@Name("etapaRepCliHome")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings("unchecked")
public class EtapaRepCliHome extends KubeDAO<EtapaRepCliente> {

	private static final long serialVersionUID = 1L;

	private List<Object[]> etapasRepCli = new ArrayList<Object[]>();
	private List<ItemRequisicionEta> itmsRequi = new ArrayList<ItemRequisicionEta>();

	private Integer etaRepId;
	private String accionEta;
	private String descRechazo;
	private String nomCoinci;
	private String comentarioAnterior="";
	private String nombreSiguienteEtapa;
	private boolean btnAceptarContinuar;
	private Integer areaUsuario;
	private Integer idSiguienteEtapa;

	@In
	private LoginUser loginUser;

	@In(required = false, create = true)
	@Out(required = false)
	private ReparacionClienteHome reparacionClienteHome;
	
	public void repsPendientes() {
		// Formamos query para mostrar reparaciones con etapas del area de
		// negocio del usuario logueado
		try{
			
			Sucursal sucursalUser=new Sucursal();
			if(loginUser.getUser().getSucursal().getSucursalSuperior()!=null)
			{
				sucursalUser=loginUser.getUser().getSucursal().getSucursalSuperior();
			}
			else
				sucursalUser=loginUser.getUser().getSucursal();
			
			
			 if(loginUser.getUser().getAreaUsuario() == null && getNomCoinci()==null)
			{
				System.out.println("**** Entro al if numero 3 ");
				
				
					/*etapasRepCli = getEntityManager().createNativeQuery(
							"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
									+ "	cli.nombres || ' ' || cli.apellidos nomCliente, apc.nombre nomProducto,"
									+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
									+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
									+ " FROM aparato_cliente apc, cliente cli, sucursal suc,"
									+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
									+ " etapa_rep_cliente etc "
									+ " WHERE apc.cliente_id = cli.cliente_id " //AND rpc.aprobada = true
									+ " and rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id "
									+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
									+ " and suc.id = rpc.sucursal_id " 
									+ " and etc.estado = 'PEN' "
									+ " ORDER BY rpc.fecha_ingreso ASC ")
					.getResultList();	*/
				
				etapasRepCli = getEntityManager().createNativeQuery(
						"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
								+ "	cli.nombres || ' ' || cli.apellidos nomCliente, apc.nombre nomProducto,"
								+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
								+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
								+ " FROM aparato_cliente apc, cliente cli, sucursal suc,"
								+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
								+ " etapa_rep_cliente etc "
								+ " WHERE apc.cliente_id = cli.cliente_id " //AND rpc.aprobada = true
								+ " and rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id "
								+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
								+ " and suc.id = rpc.sucursal_id " 
								+ " and etc.estado = 'PEN' and (etc.historico is null or etc.historico<>'historico') "
								+ " ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ")
				.getResultList();	
				
					
				
			}
			else if(loginUser.getUser().getAreaUsuario() == null && getNomCoinci()!=null)
			{	
				System.out.println("**** Entro al if numero 4 ");
					etapasRepCli = getEntityManager().createNativeQuery(
							"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
									+ "	cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,"
									+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
									+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
									+ " FROM cliente cli, sucursal suc,"
									+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
									+ " etapa_rep_cliente etc "
									+ " WHERE rpc.repcli_id = cli.cliente_id AND UPPER(cli.nombres || ' ' || cli.apellidos) LIKE :nom" // AND rpc.aprobada = true WHERE rpc.repcli_id = cli.cliente_id AND CONCAT(UPPER(TRIM(cli.nombres)),' ',UPPER(TRIM(cli.apellidos))) LIKE :nom
									+ " and rpc.repcli_id = etc.repcli_id "
									+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
									+ " and suc.id = rpc.sucursal_id and (etc.historico is null or etc.historico<>'historico')" 
									+ " and etc.estado = 'PEN' "
									/*+ " 	and etr.orden = (select MIN(tetr.orden) from etapa_reparacion tetr, etapa_rep_cliente tetc "
									+ "   where tetr.etarep_id = tetc.etarep_id and rpc.repcli_id = tetc.repcli_id "
									+ "	and tetr.areneg_id = ? and tetc.estado = 'PEN') " */
									+ " ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ")
					.setParameter("nom", "%"+getNomCoinci().toUpperCase()+"%")
					//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )
					.getResultList();
					
			}
			//Para taller + busqueda
			else if (getNomCoinci()!=null && loginUser.getUser().getAreaUsuario().getId() == 3){ //  se agrega validdacion de nomCoinci, variable de filtro (buscador de ordenes de trabajo por nombre de cliente)
				
				System.out.println("**** Entro al if numero 1 taller + busqueda");//CONCAT(UPPER(TRIM(c.nombres)),' ',UPPER(TRIM(c.apellidos)))
				etapasRepCli = getEntityManager().createNativeQuery(
									"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
											+ "	cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,"
											+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
											+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
											+ " FROM cliente cli, sucursal suc,"
											+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
											+ " etapa_rep_cliente etc "
											+ " WHERE (rpc.repcli_id = cli.cliente_id and (CONCAT(UPPER(TRIM(cli.nombres)),' ',UPPER(TRIM(cli.apellidos))) LIKE :nom)" // AND rpc.aprobada = true
											+ " and rpc.repcli_id = etc.repcli_id "
											+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
											+ " and suc.id = rpc.sucursal_id and etr.areneg_id = :neg and (etc.historico is null or etc.historico<>'historico')" 
											+ " and etc.estado = 'PEN') || etc.etarep_id=102 "
											/*+ " 	and etr.orden = (select MIN(tetr.orden) from etapa_reparacion tetr, etapa_rep_cliente tetc "
											+ "   where tetr.etarep_id = tetc.etarep_id and rpc.repcli_id = tetc.repcli_id "
											+ "	and tetr.areneg_id = ? and tetc.estado = 'PEN') " */
											+ " ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ")
							.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
							.setParameter("nom", "%"+getNomCoinci().toUpperCase().trim()+"%")
							//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )
							.getResultList();
				//System.out.println("Size del result list de trabajos: " +etapasRepCli.size());
				
				
				
			}  //para el area de negocio de taller
			else if (loginUser.getUser().getAreaUsuario().getId() == 3 && getNomCoinci()==null)
			{	
					System.out.println("**** Entro al if numero 1 taller");
					/*System.out.println("id de reparacion "+instance.getReparacionCli().getId());
					System.out.println("id de proceso taller "+instance.getReparacionCli().getProceso().getId());
					if(instance.getReparacionCli().getProceso().getId().equals(3))
					{*/
					//Integer num=102;
						etapasRepCli = getEntityManager().createNativeQuery(
								"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
										+ "	cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,"
										+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
										+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
										+ " FROM  cliente cli, sucursal suc,"
										+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
										+ " etapa_rep_cliente etc"
										+ " WHERE rpc.cli_id = cli.cliente_id "
										+ " and rpc.repcli_id = etc.repcli_id "
										+ " and etr.prctll_id = prt.prctll_id and ((etr.etarep_id = etc.etarep_id and etr.areneg_id = :neg) or (etr.etarep_id = etc.etarep_id and etc.etarep_id = 102)) "
										+ " and suc.id = rpc.sucursal_id "  
										+ " and etc.estado = 'PEN' and (etc.historico is null or etc.historico<>'historico')"
										+ " ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ")
						.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
						// Al setear la variable del area de negocio como se aprecia abajo da error (desde que se agregó filtro de busqueda por nombre de cliente)
						//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )  
						.getResultList();
					/*}
					else
					{*/
						
						/*etapasRepCli = getEntityManager().createNativeQuery(
								"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
										+ "	cli.nombres || ' ' || cli.apellidos nomCliente, apc.nombre nomProducto,"
										+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
										+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
										+ " FROM aparato_cliente apc, cliente cli, sucursal suc,"
										+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
										+ " etapa_rep_cliente etc"
										+ " WHERE apc.cliente_id = cli.cliente_id "
										+ " and rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id "
										+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
										+ " and suc.id = rpc.sucursal_id and etr.areneg_id = :neg" 
										+ " and etc.estado = 'PEN' "
										+ " ORDER BY rpc.fecha_ingreso ASC ")
						.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
						
						// Al setear la variable del area de negocio como se aprecia abajo da error (desde que se agregó filtro de busqueda por nombre de cliente)
						//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )  
						.getResultList();*/
						
					//}
						
						System.out.println(" *** AREA DE NEGOCIO USUARIO" + loginUser.getUser().getAreaUsuario().getId());
						
						/*List<Object[]> etapasEsperandoAprobacion = getEntityManager().createNativeQuery(
								"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
										+ "	cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,"
										+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
										+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
										+ " FROM  cliente cli, sucursal suc,"
										+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
										+ " etapa_rep_cliente etc"
										+ " WHERE rpc.cli_id = cli.cliente_id "
										+ " and rpc.repcli_id = etc.repcli_id "
										+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = 102 "
										+ " and etc.estado = 'PEN'"
										+ " ORDER BY rpc.fecha_ingreso ASC ")
						.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
						.getResultList();
						*/
					//System.out.println("ETAPAS ESPERANDO APROBACION TAM: "+etapasEsperandoAprobacion.size());
						
					
			}// para audiologa
			else if (loginUser.getUser().getAreaUsuario().getId() == 1 && getNomCoinci()==null)
			{	
					/*System.out.println("**** Entro al if numero 2 audiologa");
					etapasRepCli = getEntityManager().createNativeQuery(
									"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
											+ "	cli.nombres || ' ' || cli.apellidos nomCliente, apc.nombre nomProducto,"
											+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
											+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
											+ " FROM aparato_cliente apc, cliente cli, sucursal suc,"
											+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
											+ " etapa_rep_cliente etc"
											+ " WHERE apc.cliente_id = cli.cliente_id "
											+ " and rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id "
											+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
											+ " and suc.id = rpc.sucursal_id and etr.areneg_id = :neg" 
											+ " and etc.estado = 'PEN' and rpc.sucursal_id=:sucUser "
											+ " ORDER BY rpc.fecha_ingreso ASC ")
							.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
							.setParameter("sucUser",sucursalUser.getId())
							
							// Al setear la variable del area de negocio como se aprecia abajo da error (desde que se agregó filtro de busqueda por nombre de cliente)
							//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )  
							.getResultList();*/
				
				System.out.println("**** Entro al if numero 2 audiologa");
				
				etapasRepCli = getEntityManager().createNativeQuery(
								"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
										+ "	cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,"
										+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
										+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
										+ " FROM  cliente cli, sucursal suc,"
										+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
										+ " etapa_rep_cliente etc"
										+ " WHERE rpc.cli_id = cli.cliente_id "
										+ " and rpc.repcli_id = etc.repcli_id "
										+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
										+ " and suc.id = rpc.sucursal_id and etr.areneg_id = :neg" 
										+ " and etc.estado = 'PEN' and rpc.sucursal_id=:sucUser and (etc.historico is null or etc.historico<>'historico') "
										+ " ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ")
						.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
						.setParameter("sucUser",sucursalUser.getId())
						
						// Al setear la variable del area de negocio como se aprecia abajo da error (desde que se agregó filtro de busqueda por nombre de cliente)
						//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )  
						.getResultList();
						
						//System.out.println(" *** AREA DE NEGOCIO USUARIO" + loginUser.getUser().getAreaUsuario().getId());
						
					
			}
			// para audiologa + busqueda
			else if (loginUser.getUser().getAreaUsuario().getId() == 1 && getNomCoinci()!=null)
			{	
					System.out.println("**** Entro al if numero 2 audiologa + busqueda");
					
					etapasRepCli = getEntityManager().createNativeQuery(
									"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
											+ "	cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,"
											+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
											+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
											+ " FROM cliente cli, sucursal suc,"
											+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
											+ " etapa_rep_cliente etc"
											+ " WHERE rpc.repcli_id = cli.cliente_id AND CONCAT(UPPER(TRIM(cli.nombres)),' ',UPPER(TRIM(cli.apellidos))) LIKE :nom"
											+ " and rpc.repcli_id = etc.repcli_id "
											+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
											+ " and suc.id = rpc.sucursal_id and etr.areneg_id = :neg" 
											+ " and etc.estado = 'PEN' and rpc.sucursal_id=:sucUser and (etc.historico is null or etc.historico<>'historico') "
											+ " ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ")
							.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
							.setParameter("sucUser",sucursalUser.getId())
							.setParameter("nom", "%"+getNomCoinci().toUpperCase()+"%")
							
							// Al setear la variable del area de negocio como se aprecia abajo da error (desde que se agregó filtro de busqueda por nombre de cliente)
							//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )  
							.getResultList();
				
						
						//System.out.println(" *** AREA DE NEGOCIO USUARIO" + loginUser.getUser().getAreaUsuario().getId());
				
						
			}
			/*else if (loginUser.getUser().getAreaUsuario().getId() == 2 && getNomCoinci()==null)
			{	
					
				System.out.println("**** Entro al if numero 2 audiologa");
				etapasRepCli = getEntityManager().createNativeQuery(
								"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
										+ "	cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,"
										+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
										+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
										+ " FROM  cliente cli, sucursal suc,"
										+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
										+ " etapa_rep_cliente etc"
										+ " WHERE rpc.cli_id = cli.cliente_id "
										+ " and rpc.repcli_id = etc.repcli_id "
										+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
										+ " and suc.id = rpc.sucursal_id and etr.areneg_id = :neg" 
										+ " and etc.estado = 'PEN' and rpc.sucursal_id=:sucUser"
										+ " ORDER BY rpc.fecha_ingreso ASC ")
						.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
						.setParameter("sucUser",sucursalUser.getId())
						.getResultList();
						
						//System.out.println(" *** AREA DE NEGOCIO USUARIO" + loginUser.getUser().getAreaUsuario().getId());
			}*/
			
		} 
		catch (Exception e){
			
			/*if(loginUser.getUser().getAreaUsuario() == null && getNomCoinci()==null)
			{
				System.out.println("**** Entro al if numero 3 ");
					etapasRepCli = getEntityManager().createNativeQuery(
							"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
									+ "	cli.nombres || ' ' || cli.apellidos nomCliente, apc.nombre nomProducto,"
									+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
									+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
									+ " FROM aparato_cliente apc, cliente cli, sucursal suc,"
									+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
									+ " etapa_rep_cliente etc "
									+ " WHERE apc.cliente_id = cli.cliente_id " //AND rpc.aprobada = true
									+ " and rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id "
									+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
									+ " and suc.id = rpc.sucursal_id " 
									+ " and etc.estado = 'PEN' "
									+ " ORDER BY rpc.fecha_ingreso ASC ")
					.getResultList();	
					
				
			}
			else 
			{	
				System.out.println("**** Entro al if numero 4 ");
					etapasRepCli = getEntityManager().createNativeQuery(
							"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
									+ "	cli.nombres || ' ' || cli.apellidos nomCliente, apc.nombre nomProducto,"
									+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
									+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
									+ " FROM aparato_cliente apc, cliente cli, sucursal suc,"
									+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
									+ " etapa_rep_cliente etc "
									+ " WHERE apc.cliente_id = cli.cliente_id AND (UPPER(cli.nombres) LIKE :nom OR UPPER(cli.apellidos) LIKE :ape)" // AND rpc.aprobada = true
									+ " and rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id "
									+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
									+ " and suc.id = rpc.sucursal_id " 
									+ " and etc.estado = 'PEN' "
									+ " 	and etr.orden = (select MIN(tetr.orden) from etapa_reparacion tetr, etapa_rep_cliente tetc "
									+ "   where tetr.etarep_id = tetc.etarep_id and rpc.repcli_id = tetc.repcli_id "
									+ "	and tetr.areneg_id = ? and tetc.estado = 'PEN') " 
									+ " ORDER BY rpc.fecha_ingreso ASC ")
					.setParameter("nom", "%"+getNomCoinci().toUpperCase()+"%")
					.setParameter("ape", "%"+getNomCoinci().toUpperCase()+"%")
					//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )
					.getResultList();
			}*/
			
			e.printStackTrace();
		}
		
	}
	
	
	public void verRegistrosHistoricos()
	{
		
		try{
			
			Sucursal sucursalUser=new Sucursal();
			if(loginUser.getUser().getSucursal().getSucursalSuperior()!=null)
			{
				sucursalUser=loginUser.getUser().getSucursal().getSucursalSuperior();
			}
			else
				sucursalUser=loginUser.getUser().getSucursal();
			
			
			 if(loginUser.getUser().getAreaUsuario() == null && getNomCoinci()==null)
			{
				System.out.println("**** Entro al if numero 3 ");
				
				StringBuilder jpql = new StringBuilder();
				
				jpql.append("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, ")
				.append(" cli.nombres || ' ' || cli.apellidos nomCliente, apc.nombre nomProducto,")
				.append(" rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,")
				.append(" prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal")
				.append(" FROM aparato_cliente apc, cliente cli, sucursal suc,")
				.append(" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, ")
				.append(" etapa_rep_cliente etc ")
				.append(" WHERE apc.cliente_id = cli.cliente_id ")
				.append(" and rpc.apacli_id = apc.apacli_id and rpc.repcli_id = etc.repcli_id ")
				.append(" and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id ")
				.append(" and suc.id = rpc.sucursal_id ")
				.append(" and etc.estado = 'PEN' ")
				.append(" ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ");
				
				etapasRepCli = getEntityManager().createNativeQuery(jpql.toString())
				.getResultList();	
					
				
			}
			else if(loginUser.getUser().getAreaUsuario() == null && getNomCoinci()!=null)
			{	
				System.out.println("**** Entro al if numero 4 ");
				
					StringBuilder jpql= new StringBuilder();
					
					jpql.append("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, ")
					.append(" cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,")
					.append(" rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,")
					.append(" prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal")
					.append(" FROM cliente cli, sucursal suc,")
					.append(" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt,")
					.append(" etapa_rep_cliente etc")
					.append(" WHERE rpc.repcli_id = cli.cliente_id AND UPPER(cli.nombres || ' ' || cli.apellidos) LIKE :nom ")
					.append(" and rpc.repcli_id = etc.repcli_id ")
					.append(" and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id ")
					.append(" and suc.id = rpc.sucursal_id ")
					.append(" and etc.estado = 'PEN' ")
					.append(" ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ");
					
					etapasRepCli = getEntityManager().createNativeQuery(jpql.toString())
					.setParameter("nom", "%"+getNomCoinci().toUpperCase()+"%")
					//.setParameter(2, (loginUser.getUser().getAreaUsuario() != null)?loginUser.getUser().getAreaUsuario().getId():0 )
					.getResultList();
			}
			//Para taller + busqueda
			else if (getNomCoinci()!=null && loginUser.getUser().getAreaUsuario().getId() == 3){ //  se agrega validdacion de nomCoinci, variable de filtro (buscador de ordenes de trabajo por nombre de cliente)
				
				System.out.println("**** Entro al if numero 1 taller + busqueda");//CONCAT(UPPER(TRIM(c.nombres)),' ',UPPER(TRIM(c.apellidos)))
				
				StringBuilder jpql = new StringBuilder();
				
				jpql.append("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, ")
				.append(" cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,")
				.append(" rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,")
				.append(" prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal")
				.append(" FROM cliente cli, sucursal suc,")
				.append(" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, ")
				.append(" etapa_rep_cliente etc ")
				.append(" WHERE (rpc.repcli_id = cli.cliente_id and (CONCAT(UPPER(TRIM(cli.nombres)),' ',UPPER(TRIM(cli.apellidos))) LIKE :nom)")
				.append(" and rpc.repcli_id = etc.repcli_id ")
				.append(" and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id ")
				.append(" and suc.id = rpc.sucursal_id and etr.areneg_id = :neg")
				.append(" and etc.estado = 'PEN') || etc.etarep_id=102 ")
				.append(" ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ");
				
				etapasRepCli = getEntityManager().createNativeQuery(jpql.toString())
							.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
							.setParameter("nom", "%"+getNomCoinci().toUpperCase().trim()+"%")
							.getResultList();
				
				
			}  //para el area de negocio de taller
			else if (loginUser.getUser().getAreaUsuario().getId() == 3 && getNomCoinci()==null)
			{	
					System.out.println("**** Entro al if numero 1 taller");
					
					StringBuilder jpql = new StringBuilder();
					
					jpql.append("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, ")
					.append(" cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,")
					.append(" rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,")
					.append(" prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal")
					.append(" FROM  cliente cli, sucursal suc,")
					.append(" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, ")
					.append(" etapa_rep_cliente etc")
					.append(" WHERE rpc.cli_id = cli.cliente_id")
					.append(" and rpc.repcli_id = etc.repcli_id ")
					.append(" and etr.prctll_id = prt.prctll_id and ((etr.etarep_id = etc.etarep_id and etr.areneg_id = :neg) or (etr.etarep_id = etc.etarep_id and etc.etarep_id = 102)) ")
					.append(" and suc.id = rpc.sucursal_id ")
					.append(" and etc.estado = 'PEN'")
					.append(" ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ");
					
						etapasRepCli = getEntityManager().createNativeQuery(jpql.toString())
						.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
						.getResultList();
					
						
						System.out.println(" *** AREA DE NEGOCIO USUARIO" + loginUser.getUser().getAreaUsuario().getId());
						
					
			}// para audiologa
			else if (loginUser.getUser().getAreaUsuario().getId() == 1 && getNomCoinci()==null)
			{	
					
				System.out.println("**** Entro al if numero 2 audiologa");
				
				StringBuilder jpql = new StringBuilder();
				
				jpql.append("SELECT prt.nombre nomProceso, etr.nombre nomEtapa, ")
				.append(" cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,")
				.append(" rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,")
				.append(" prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal")
				.append(" FROM  cliente cli, sucursal suc,")
				.append(" reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, ")
				.append(" etapa_rep_cliente etc")
				.append(" WHERE rpc.cli_id = cli.cliente_id ")
				.append(" and rpc.repcli_id = etc.repcli_id ")
				.append(" and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id ")
				.append(" and suc.id = rpc.sucursal_id and etr.areneg_id = :neg")
				.append(" and etc.estado = 'PEN' and rpc.sucursal_id=:sucUser ")
				.append(" ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ");
				
				etapasRepCli = getEntityManager().createNativeQuery(jpql.toString())
						.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
						.setParameter("sucUser",sucursalUser.getId())
						.getResultList();
						
						System.out.println(" *** AREA DE NEGOCIO USUARIO" + loginUser.getUser().getAreaUsuario().getId());
						
					
			}
			// para audiologa + busqueda
			else if (loginUser.getUser().getAreaUsuario().getId() == 1 && getNomCoinci()!=null)
			{	
				
					System.out.println("**** Entro al if numero 2 audiologa + busqueda");
					etapasRepCli = getEntityManager().createNativeQuery(
									"SELECT prt.nombre nomProceso, etr.nombre nomEtapa, "
											+ "	cli.nombres || ' ' || cli.apellidos nomCliente, cli.telefono1 telefono,"
											+ "	rpc.fecha_ingreso fechaEstFin, etc.fecha_real_fin fechaReaFin, etc.etarepcli_id id,"
											+ "	prt.codigo codProceso, rpc.repcli_id idRep, suc.nombre nomSucursal" 
											+ " FROM cliente cli, sucursal suc,"
											+ " reparacion_cliente rpc, etapa_reparacion etr, proceso_taller prt, "
											+ " etapa_rep_cliente etc"
											+ " WHERE rpc.repcli_id = cli.cliente_id AND CONCAT(UPPER(TRIM(cli.nombres)),' ',UPPER(TRIM(cli.apellidos))) LIKE :nom"
											+ " and rpc.repcli_id = etc.repcli_id "
											+ " and etr.prctll_id = prt.prctll_id and etr.etarep_id = etc.etarep_id "
											+ " and suc.id = rpc.sucursal_id and etr.areneg_id = :neg" 
											+ " and etc.estado = 'PEN' and rpc.sucursal_id=:sucUser "
											+ " ORDER BY rpc.fecha_ingreso,rpc.repcli_id ASC ")
							.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
							.setParameter("sucUser",sucursalUser.getId())
							.setParameter("nom", "%"+getNomCoinci().toUpperCase()+"%")
							.getResultList();
				
						
						System.out.println(" *** AREA DE NEGOCIO USUARIO" + loginUser.getUser().getAreaUsuario().getId());
						
			}
			
		} 
		catch (Exception e){
			
			e.printStackTrace();
		}
		
	}
	
	public void cargarComentario()
	{
		
	}
	
	public void actualizarComentario()
	{
		getEntityManager().merge(instance);
		getEntityManager().flush();
	}

	public void load() {
		try {
			
			System.out.println("ENTRO A LOAD() ");
			
			setInstance((EtapaRepCliente) getEntityManager()
					.createQuery(
							"select e from EtapaRepCliente e where e.id = :id")
					.setParameter("id", etaRepId).getSingleResult());
			
			reparacionClienteHome.setRepCliId(instance.getReparacionCli().getId());
			reparacionClienteHome.load();
			
			
			System.out.println("Orden"+instance.getEtapaRep().getOrden());
			//System.out.println("ID ETAPA ACTUAL");
			
			System.out.println("Reparacion ID"+instance.getReparacionCli().getId());
			
			
			System.out.println("ID ETAPA CARGADA: "+instance.getId());
			System.out.println("NOMBRE ETAPA CARGADA: "+instance.getEtapaRep().getNombre());
			
			int orderAct=instance.getEtapaRep().getOrden();
			int idEtpAnt=instance.getId()-1;
			
			if(orderAct>1)
			{
				
				comentarioAnterior = (String) getEntityManager().createQuery("SELECT etp.descripcion FROM EtapaRepCliente etp where etp.id=:idEtpAnterior").setParameter("idEtpAnterior", idEtpAnt).getSingleResult();
			}
			 
			
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			clearInstance();
			setInstance(new EtapaRepCliente());

		}
	}

	public void cargarEtapasRep() {
		etapasRepCli = getEntityManager()
				.createQuery(
						"SELECT e FROM EtapaRepCliente e "
								+ "	WHERE e.reparacionCli = :rep ORDER BY e.etapaRep.orden ASC")
				.setParameter("rep", this.reparacionClienteHome.getInstance())
				.getResultList();
		// Cargamos las requisiciones en una sola lista
		itmsRequi = getEntityManager()
				.createQuery(
						"SELECT i FROM ItemRequisicionEta i WHERE i.reqEtapa.etapaRepCli.reparacionCli = :rep")
				.setParameter("rep", reparacionClienteHome.getInstance())
				.getResultList();

		Double totSrv = 0d;
		Double totItm = 0d;

		if (reparacionClienteHome.getInstance().getServiciosRep() != null)
			for (ServicioReparacion tmpServ : reparacionClienteHome
					.getInstance().getServiciosRep())
				totSrv += tmpServ.getServicio().getCosto();

		if (itmsRequi != null)
			for (ItemRequisicionEta tmpIR : itmsRequi)
				totItm += tmpIR.getProducto().getPrcNormal()
						* tmpIR.getCantidad();

		reparacionClienteHome.setTotalServs(totSrv);
		reparacionClienteHome.setTotalItems(totItm);
	}

	public String calcTiempoRespuesta(EtapaRepCliente e){
		long diff = 0;
		if (e.getFechaRealFin()!=null)
			diff = (e.getFechaRealFin().getTime()-e.getFechaInicio().getTime())/3600000;
		else 
			diff = ((new Date()).getTime()-e.getFechaInicio().getTime())/3600000;
		if (diff < 12)
			return "g";
		else if (diff < 24)
			return "y";
		else  if (diff < 48)
			return "o";
		else if (diff >48)
			return "r";
		else return "na";
	}
	
	public String calcTiempoRespuesta(ReparacionCliente rep){
		long diff = 0;
		for (EtapaRepCliente e : rep.getEtapasReparacion()){
			if (e.getFechaInicio()!=null && e.getFechaRealFin()==null){
				
				diff = ((new Date()).getTime()-e.getFechaInicio().getTime())/3600000;
				if (diff < 12)
					return "g";
				else if (diff < 24)
					return "y";
				else  if (diff < 48)
					return "o";
				else if (diff >48)
					return "r";
				else return "na";	
			}
		}
		return "na";
	
	}
	
	public String calcTiempoRespuesta(Integer idEtaRepCiente){
		List <EtapaRepCliente> etaRepClis = new ArrayList<EtapaRepCliente>();
		etaRepClis.add( (EtapaRepCliente) getEntityManager().createQuery("SELECT e FROM EtapaRepCliente e WHERE id=:id")
				.setParameter("id", idEtaRepCiente)
				.getSingleResult());
		EtapaRepCliente e = etaRepClis.get(0);
			if (e.getFechaInicio()!=null){
				long diff = 0;
				if (e.getFechaRealFin()!=null)
					diff = (e.getFechaRealFin().getTime()-e.getFechaInicio().getTime())/3600000;
				else 
					diff = ((new Date()).getTime()-e.getFechaInicio().getTime())/3600000;
				
					if (diff < 12)
						return "g";
					else if (diff < 24)
						return "y";
					else  if (diff < 48)
						return "o";
					else if (diff >48)
						return "r";
					else return "na";	
			}
		return "na";
	}
	
	
	public void getResultList() {
		etapasRepCli = getEntityManager()
				.createNativeQuery(
						"SELECT * FROM etapa_rep_cliente e "
								+ "	WHERE e.reparacionCli = :rep ORDER BY e.etapaRep.orden ASC")
				.setParameter("rep", this.reparacionClienteHome.getInstance())
				.getResultList();
	}

	/*public boolean aprobarEtapa() {
		
		//if(instance.getEtapaRep().getId().equals((41)) || instance.getEtapaRep().getId().equals((47)) || instance.getEtapaRep().getId().equals((53)) && instance.getEstado().equals("PEN"))
		//if(reparacionClienteHome.getServiciosRep().size()<=0 && (instance.getEtapaRep().getProcesoTll().getNombre().equals("Diagnostico") || instance.getEtapaRep().getProcesoTll().getNombre().equals("Limpieza")))
			if(reparacionClienteHome.getServiciosRep().size()<=0 && (instance.getEtapaRep().getId().equals((41)) || instance.getEtapaRep().getId().equals((47)) || instance.getEtapaRep().getId().equals((53)) && instance.getEstado().equals("PEN")))	
		{
			FacesMessages.instance().add(Severity.WARN,"Debe agregar al menos un servicio...");//para validar que agrege al menos un servicio. sebe ser solo en diagnos
			return false;
		}
			
		setAccionEta("APR");
		
		
		return this.modify();
	}*/
	
	public String aprobarEtapa() {
		
		//if(instance.getEtapaRep().getId().equals((41)) || instance.getEtapaRep().getId().equals((47)) || instance.getEtapaRep().getId().equals((53)) && instance.getEstado().equals("PEN"))
		//if(reparacionClienteHome.getServiciosRep().size()<=0 && (instance.getEtapaRep().getProcesoTll().getNombre().equals("Diagnostico") || instance.getEtapaRep().getProcesoTll().getNombre().equals("Limpieza")))
		if(reparacionClienteHome.getServiciosRep().size()<=0 && (instance.getEtapaRep().getId().equals((41)) || instance.getEtapaRep().getId().equals((47)) || instance.getEtapaRep().getId().equals((53)) && instance.getEstado().equals("PEN")))	
		{
			FacesMessages.instance().add(Severity.WARN,"Debe agregar al menos un servicio...");//para validar que agrege al menos un servicio. sebe ser solo en diagnos
			return "";
		}
			
		setAccionEta("APR");
		
		if(this.modify())
			return "/taller/etasReparacion/list.xhtml";
		else
			return "";
		
	}
	
	public boolean aprobarEtapaContinuar()
	{
		
		if(aprobarEtapa().equals(""))
			return false;
		
		//load();
		
		etaRepId = idSiguienteEtapa;
		
		return true; 
	}

	public boolean rechazarEtapa() {
		setAccionEta("REC");
		return this.modify();
	}
	
	public boolean noAprobarEtapa()
	{
		setAccionEta("NAP");
		return this.modify();
	}

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean preModify() {
		
		
		
		if(!accionEta.equals("NAP"))
		{
			System.out.println("Entro a diferente de NAP");
			
			//Validar si se ingreso al menos un servicio o descripcion adicional(componente defectuoso, defecto de capsula)
			if(instance.getEtapaRep().getId().equals((41)) || instance.getEtapaRep().getId().equals((47)) || instance.getEtapaRep().getId().equals((53)) || instance.getEtapaRep().getId().equals((58)) && instance.getEstado().equals("PEN"))
			{
				// && reparacionClienteHome.getSelDefectosCap().isEmpty() && reparacionClienteHome.getSelComponentesDef().isEmpty()
				if(reparacionClienteHome.getServiciosRep().isEmpty())
				{
					FacesMessages.instance().add(Severity.WARN,"Debe agregar al menos un servicio");
					return false;
				}
				
			}
			
			//Verificamos si la etapa elaboracion de molde o ensamblaje tiene requisiciones pendientes
			if(instance.getEtapaRep().getNombre().equals("Elaboracion de molde") || instance.getEtapaRep().getNombre().equals("Ensamblaje"))
			{
				for(RequisicionEtapaRep requi: instance.getRequisicionesEtapa())
				{
					if(requi.getEstado().equals("PEN"))
					{
						FacesMessages.instance().add(Severity.ERROR,sainv_messages.get("repCliHome_err_req_pen"));
						return false;
					}
				}
			}
		}
		
		
		// Verificamos si se trata de una aprobacion o de una eliminacion
		if (accionEta.equals("APR")) {
			// Verificamos que las cotizaciones se ingresen como requisiciones
			for (RequisicionEtapaRep tmpReq : instance.getRequisicionesEtapa()) {
				if (tmpReq.getEstado().equals("PEN"))
					FacesMessages.instance().add(
							Severity.ERROR,
							sainv_messages
									.get("etapaRepCliHome_error_reqnotapr"));

			}
			instance.setEstado("APR");
			instance.setFechaRealFin(new Date());
			instance.setUsuario(loginUser.getUser());
		}
		else if(accionEta.equals("NAP"))
		{
			System.out.println("Entro a NAP");
			// Verificamos que las cotizaciones se ingresen como requisiciones
			for (RequisicionEtapaRep tmpReq : instance.getRequisicionesEtapa()) {
				if (tmpReq.getEstado().equals("PEN"))
					FacesMessages.instance().add(
							Severity.ERROR,
							sainv_messages
									.get("etapaRepCliHome_error_reqnotapr"));

			}
			instance.setEstado("PEN");
			instance.setFechaRealFin(new Date());
			instance.setUsuario(loginUser.getUser());
			
		}
		else {
			
			System.out.println("Entro al else DescRechazo");
			
			descRechazo = instance.getDescripcion();
			instance.setEstado(null);
			instance.setFechaRealFin(null);
			instance.setUsuario(null);
		}
		return true;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void posModify() {
		
		if (accionEta.equals("APR")) 
		{
			// Colocamos la siguiente etapa como pendiente
			List<EtapaRepCliente> siguienteEtapa = getEntityManager()
					.createQuery(
							"SELECT er FROM EtapaRepCliente er "
									+ "	WHERE er.reparacionCli = :rep AND (er.estado is null or er.estado <> 'APR' and er.estado <> 'NAP' and er.estado <> 'REC') "
									+ "	AND er <> :currEta ORDER BY er.etapaRep.orden ASC")
					.setParameter("rep", instance.getReparacionCli())
					.setParameter("currEta", instance).getResultList();
			
			
			
			if(siguienteEtapa != null && siguienteEtapa.size() > 0) 
			{
				EtapaRepCliente tmpEta = (EtapaRepCliente) siguienteEtapa.get(0);
				
					//tmpEta.getEtapaRep().getOrden()==7) solo estaba esta linea en la condicion hasta el 07/02/2017
					if((tmpEta.getEtapaRep().getProcesoTll().getNombre().equals("Reparacion") && tmpEta.getEtapaRep().getOrden()==7) || (tmpEta.getEtapaRep().getProcesoTll().getNombre().equals("Fabricacion de molde") && tmpEta.getEtapaRep().getOrden()==5) || (tmpEta.getEtapaRep().getProcesoTll().getNombre().equals("Limpieza") && tmpEta.getEtapaRep().getOrden()==3) || (tmpEta.getEtapaRep().getProcesoTll().getNombre().equals("Ensamblaje de aparato") && tmpEta.getEtapaRep().getOrden()==5))
					{			System.out.println("****Entro a condicion de reparacion y molde");
								tmpEta.setEstado("PEN");
						
								Calendar cal = new GregorianCalendar();
								cal.add(Calendar.DATE, 1);
								tmpEta.setFechaInicio(cal.getTime());
								getEntityManager().merge(tmpEta);
			
								for (RequisicionEtapaRep tmpReq : instance
										.getRequisicionesEtapa()) {
									// Si hay cotizaciones ingresadas, las convertimos a
									// requisiciones
									if (tmpReq.getEstado().equals("COT")) {
										tmpReq.setEstado("PEN");
										tmpReq.setEtapaRepCli(tmpEta);
										getEntityManager().merge(tmpReq);
									}
								}
								getEntityManager().flush();
								// SI se esta aprobando la evaluacion se setea el flag para que
								// el cliente decida si se aprueba o no
								if (instance.getEtapaRep().getCodEta().equals("EVA"))
									reparacionClienteHome.getInstance().setAprobada(false);
								else
									reparacionClienteHome.getInstance().setAprobada(true);
								
								
								reparacionClienteHome.getInstance().setEstado("FIN");
								reparacionClienteHome.getInstance().setFechaFin(new Date());
						
						
						
					}
					else
					{ 
						
						//System.out.println("Entro al else de comparacion etapa Reparacion y molde");
						tmpEta.setEstado("PEN");
						
								Calendar cal = new GregorianCalendar();
								cal.add(Calendar.DATE, 1);
								tmpEta.setFechaInicio(cal.getTime());
								getEntityManager().merge(tmpEta);
			
								for (RequisicionEtapaRep tmpReq : instance
										.getRequisicionesEtapa()) {
									// Si hay cotizaciones ingresadas, las convertimos a
									// requisiciones
									if (tmpReq.getEstado().equals("COT")) {
										tmpReq.setEstado("PEN");
										tmpReq.setEtapaRepCli(tmpEta);
										getEntityManager().merge(tmpReq);
									}
								}
								getEntityManager().flush();
								// SI se esta aprobando la evaluacion se setea el flag para que
								// el cliente decida si se aprueba o no
								if (instance.getEtapaRep().getCodEta().equals("EVA"))
									reparacionClienteHome.getInstance().setAprobada(false);///Se necesita???
								else
									reparacionClienteHome.getInstance().setAprobada(true);
					}

			} 
			else //if(siguienteEtapa == null || siguienteEtapa.size() == 0 || siguienteEtapa.get(0).getEtapaRep().getOrden()==7) 
			{ 			// Si ya no hay ninguna otra etapa que este pendiente,
						// significa que el trabajo ha sido finalizado,
						// actualizamos el estado
				reparacionClienteHome.getInstance().setEstado("FIN");
				reparacionClienteHome.getInstance().setFechaFin(new Date());
				
				//System.out.println("Entro a tamanuo ceroo ");
			/*
				// Adicionamos una garantia de reparacion si es que no tiene o
				// tiene una no vigente
				if (!reparacionClienteHome.tieneGarantiaVigente(
						reparacionClienteHome.getInstance().getAparatoRep()
								.getFechaGarRep(), reparacionClienteHome
								.getInstance().getAparatoRep()
								.getPeriodoGarantiaRep())) {
					Calendar calMan = new GregorianCalendar();
					calMan.add(Calendar.DATE, 1);
					reparacionClienteHome.getInstance().getAparatoRep()
							.setFechaGarRep(calMan.getTime());
					reparacionClienteHome.getInstance().getAparatoRep()
							.setPeriodoGarantiaRep(90);
					getEntityManager()
							.merge(reparacionClienteHome.getInstance()
									.getAparatoRep());
				}

				*/
				
				
				
				
				
				
				/*
				// Guardamos el detalle de la venta para que aparezca en la
				// pantalla de cobros (incluir requi y costos)
				Double totalReparacion = 0d;
				VentaProdServ vta = new VentaProdServ();
				vta.setCliente(instance.getReparacionCli().getCliente());
				vta.setDetalle(instance.getReparacionCli().getDescripcion());
				vta.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
				vta.setEstado("PEN");
				vta.setFechaVenta(new Date());
				vta.setIdDetalle(instance.getReparacionCli().getId());
				vta.setMonto(0.0f);
				vta.setSucursal(loginUser.getUser().getSucursal()); // Cambiar
																	// para que
																	// al
																	// gaurdar
																	// reparacion
																	// guarde la
																	// sucursal
				vta.setTipoVenta("TLL");
				vta.setUsrEfectua(loginUser.getUser());
				getEntityManager().persist(vta);
				// Requisiciones
				List<ItemRequisicionEta> itemsRequis = getEntityManager()
						.createQuery(
								"SELECT i FROM ItemRequisicionEta i WHERE i.reqEtapa.etapaRepCli.reparacionCli = :rep")
						.setParameter("rep", instance.getReparacionCli())
						.getResultList();
				if (itemsRequis != null && itemsRequis.size() > 0)
					for (ItemRequisicionEta tmpItr : itemsRequis) {
						DetVentaProdServ dtVta = new DetVentaProdServ();
						dtVta.setCantidad(tmpItr.getCantidad());
						StringBuilder bld = new StringBuilder();
						bld.append(tmpItr.getProducto().getNombre());
						bld.append(", Modelo "
								+ tmpItr.getProducto().getModelo());
						bld.append(", Marca "
								+ tmpItr.getProducto().getMarca().getNombre());
						dtVta.setDetalle(bld.toString());
						dtVta.setMonto(tmpItr.getProducto().getPrcNormal());
						dtVta.setVenta(vta);
						dtVta.setCodClasifVta(tmpItr.getProducto()
								.getReferencia());
						dtVta.setCodClasifVta("PRQ");
						totalReparacion += dtVta.getMonto()
								* dtVta.getCantidad();
						getEntityManager().persist(dtVta);
					}

				for (ServicioReparacion srv : reparacionClienteHome
						.getInstance().getServiciosRep()) {
					if(srv.getEstado() == null || !srv.getEstado().equals("CBR")) {
						totalReparacion += srv.getServicio().getCosto();
						DetVentaProdServ dtVta = new DetVentaProdServ();
						dtVta.setCantidad(1);
						StringBuilder bld = new StringBuilder();
						bld.append(srv.getServicio().getName());
						dtVta.setDetalle(bld.toString());
						dtVta.setEscondido(true);
						dtVta.setMonto(srv.getServicio().getCosto().floatValue());
						dtVta.setVenta(vta);
						dtVta.setCodClasifVta(srv.getServicio().getCodigo());
						dtVta.setServicio(srv.getServicio());
						getEntityManager().persist(dtVta);
					}
				}

				// Actualizamos el monto de la venta
				getEntityManager().refresh(vta);
				vta.setMonto(super.moneyDecimal(totalReparacion).floatValue());
				instance.getReparacionCli().setCosto(vta.getMonto());
				getEntityManager().merge(vta);
				
				
				// Verificamos, si hay una garantia de venta o reparacion vigente,
				// se aprueba la venta con descuento del 100 por ciento
				if (reparacionClienteHome.tieneGarantiaVigente(
						reparacionClienteHome.getInstance().getAparatoRep().getFechaGarRep(), 
						reparacionClienteHome.getInstance().getAparatoRep().getPeriodoGarantiaRep()) ||
						reparacionClienteHome.tieneGarantiaVigente(
								reparacionClienteHome.getInstance().getAparatoRep().getFechaAdquisicion(), 
								reparacionClienteHome.getInstance().getAparatoRep().getPeriodoGarantia())) {
					ventaProdServHome.select(vta);
					ventaProdServHome.setLlevaDescuento(true);
					ventaProdServHome.getInstance().setCantidadDescuento(new Long(Math.round(vta.getMonto()*100))/100.0);
					ventaProdServHome.getInstance().setTipoDescuento("M");
					ventaProdServHome.getInstance().setDetalle("Cobro con descuento del 100% por cobertura de garantÃ­a");
					ventaProdServHome.getInstance().setEstado("PDS");
					ventaProdServHome.getInstance().setUsrDescuento(instance.getUsuario());
					ventaProdServHome.aprobarVta();
				}
				*/

			}
			// Actualizamos por si tiene precios nuevos o algo asi
			/*for (ServicioReparacion tmpSrvR : reparacionClienteHome.getInstance().getServiciosRep())
				getEntityManager().remove(tmpSrvR);*/

			//getEntityManager().flush();
			for (ServicioReparacion tmpSrvR : reparacionClienteHome.getServiciosRep()) //Agregado el 07/02/2017
			{
				//tmpSrvR.setId(null);
				if(tmpSrvR.getId()==null)
				{
					getEntityManager().persist(tmpSrvR);
					
				}
			}

			FacesMessages.instance().clear();
			FacesMessages.instance().add(
					sainv_messages.get("etarepcli_etapa_fin"));
		} 
		
		//Codigo para no aprobar una orden de laboratorio que no tiene reparacion
		else if(accionEta.equals("NAP"))
		{
			//System.out.println("Entro a NAP Pos Modify");
			
			// Consultamos todas las etapas pendientes
			List<EtapaRepCliente> siguienteEtapa = getEntityManager()
					.createQuery(
							"SELECT er FROM EtapaRepCliente er "
									+ "	WHERE er.reparacionCli = :rep AND (er.estado is null or er.estado <> 'APR') "
									+ "	AND er <> :currEta ORDER BY er.etapaRep.orden ASC")
					.setParameter("rep", instance.getReparacionCli())
					.setParameter("currEta", instance).getResultList();
			
			//Actualizamos el estado de la etapa actual a aprobado (seria diagnostico). Ya que el diagnostico si debe realizarse
			getInstance().setEstado("APR");
			getEntityManager().merge(instance);
			
			//actualizamos los demas estado a No aprobados(NAP) excepto 'Entrega'
			if (siguienteEtapa != null && siguienteEtapa.size() > 0) 
			{
				
				for(EtapaRepCliente etpas: siguienteEtapa)
				{
					if(!etpas.getEtapaRep().getNombre().equals("Pendiente de entrega"))
					{
						
						etpas.setEstado("NAP");
						getEntityManager().merge(etpas);
						
					}
					else
					{
						etpas.setEstado("PEN");
						getEntityManager().merge(etpas);
						break;
					}
					
					
				}
					
				
				/*
				EtapaRepCliente tmpEta = (EtapaRepCliente) siguienteEtapa
						.get(0);
				tmpEta.setEstado("PEN");
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.DATE, 1);
				tmpEta.setFechaInicio(cal.getTime());
				getEntityManager().merge(tmpEta);*/
				
				

				/*for (RequisicionEtapaRep tmpReq : instance
						.getRequisicionesEtapa()) {
					// Si hay cotizaciones ingresadas, las convertimos a
					// requisiciones
					if (tmpReq.getEstado().equals("COT")) {
						tmpReq.setEstado("PEN");
						tmpReq.setEtapaRepCli(tmpEta);
						getEntityManager().merge(tmpReq);
					}
				}*/
			
			
				getEntityManager().flush();
				// SI se esta aprobando la evaluacion se setea el flag para que
				// el cliente decida si se aprueba o no
				
				
				/*if (instance.getEtapaRep().getCodEta().equals("EVA"))
					reparacionClienteHome.getInstance().setAprobada(false);
				else
					reparacionClienteHome.getInstance().setAprobada(true);*/
				reparacionClienteHome.getInstance().setEstado("FIN");
				reparacionClienteHome.getInstance().setFechaFin(new Date());
			} 
			
			else 
			{ // Si ya no hay ninguna otra etapa que este pendiente,
						// significa que el trabajo ha sido finalizado,
						// actualizamos el estado
				reparacionClienteHome.getInstance().setEstado("FIN");
				reparacionClienteHome.getInstance().setFechaFin(new Date());
			}
			
			// Actualizamos por si tiene precios nuevos o algo asi  /////comentado el 07/02/2017
			/*for (ServicioReparacion tmpSrvR : reparacionClienteHome
					.getInstance().getServiciosRep())
				getEntityManager().remove(tmpSrvR);

			getEntityManager().flush();
			for (ServicioReparacion tmpSrvR : reparacionClienteHome
					.getServiciosRep()) {
				tmpSrvR.setId(null);
				getEntityManager().persist(tmpSrvR);
			}*/
			
			for (ServicioReparacion tmpSrvR : reparacionClienteHome.getServiciosRep()) //Agregado el 07/02/2017
			{
				//tmpSrvR.setId(null);
				if(tmpSrvR.getId()==null)
					getEntityManager().persist(tmpSrvR);
			}

			FacesMessages.instance().clear();
			FacesMessages.instance().add(
					sainv_messages.get("etarepcli_etapa_fin"));
			
		}
		
		
		else {
			
			//System.out.println("Entro al else en posModify");
			
			String tmpDesc = "";
			// Limpiamos la fecha de finalizacion de la etapa y los comentarios,
			// y los borramos
			List<EtapaRepCliente> etapasPasadas = getEntityManager()
					.createQuery(
							"SELECT er FROM EtapaRepCliente er WHERE er.reparacionCli = :rep "
									+ "	ORDER BY er.etapaRep.orden DESC")
					.setParameter("rep", instance.getReparacionCli())
					.getResultList();
			for (EtapaRepCliente tmpEt : etapasPasadas) {
				tmpDesc = tmpEt.getDescripcion();
				tmpEt.setFechaRealFin(null);
				tmpEt.setDescripcion(null);
				tmpEt.setUsuario(null);
				tmpEt.setEstado(null);
				
				if (tmpEt.getEtapaRep().equals(
						instance.getEtapaRep().getLoopBack())) {
					tmpDesc = descRechazo + " - " + tmpDesc;
					tmpEt.setDescripcion(tmpDesc);
					tmpEt.setEstado("PEN");
					getEntityManager().merge(tmpEt);
					getEntityManager().flush();
					break;
				}
				
				getEntityManager().merge(tmpEt);
			}
			
			getEntityManager().flush();
			
			FacesMessages.instance().clear();
			FacesMessages.instance().add(
					sainv_messages.get("etarepcli_etapa_rec"));
		}
		// Ya sea que aprobamos o no, guardamos las condiciones del aparato que fueron cambiadas
		reparacionClienteHome.modify();
		
		// Reseteamos la lista de las reparaciones pendientes para el usuario
		repsPendientes();
	}
	
	
	public void siguienteEtapaUsuario()
	{
		// Colocamos la siguiente etapa como pendiente
		List<EtapaRepCliente> etapas = getEntityManager()
				.createQuery(
						"SELECT er FROM EtapaRepCliente er "
								+ "	WHERE er.reparacionCli = :rep AND (er.estado is null or er.estado <> 'APR' and er.estado <> 'NAP' and er.estado <> 'REC') "
								+ "	AND er <> :currEta ORDER BY er.etapaRep.orden ASC")
				.setParameter("rep", instance.getReparacionCli())
				.setParameter("currEta", instance).getResultList();
		
		
		nombreSiguienteEtapa = etapas.get(0).getEtapaRep().getNombre();
		idSiguienteEtapa = etapas.get(0).getId();
		
		//System.out.println("Area usuario "+ loginUser.getUser().getAreaUsuario().getId());
		
		System.out.println("Nombre "+nombreSiguienteEtapa);
		System.out.println("ID siguiente etapa "+etapas.get(0).getId());
		//System.out.println("Area negocio usuario "+loginUser.getUser().getAreaUsuario().getId());
		//System.out.println("Area negocio etapa "+etapas.get(0).getEtapaRep().getAreaEncargada().getId());
		
		if(etapas!=null && etapas.size()>0)
		{
			if(loginUser.getUser().getAreaUsuario()==null || (etapas.get(0).getEtapaRep().getAreaEncargada().getId().equals(loginUser.getUser().getAreaUsuario().getId())))
			{
		
				btnAceptarContinuar = true;
				System.out.println("Boton aceptar true");
				etaRepId = etapas.get(0).getId();
				
				System.out.println("ID ETAPA boton "+etaRepId);
				
				return;
			}
		}
		
		
		btnAceptarContinuar = false;
	}
	

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub

	}

	public List<Object[]> getEtapasRepCli() {
		return etapasRepCli;
	}

	public void setEtapasRepCli(List<Object[]> etapasRepCli) {
		this.etapasRepCli = etapasRepCli;
	}

	public Integer getEtaRepId() {
		return etaRepId;
	}

	public void setEtaRepId(Integer etaRepId) {
		this.etaRepId = etaRepId;
	}

	public String getAccionEta() {
		return accionEta;
	}

	public void setAccionEta(String accionEta) {
		this.accionEta = accionEta;
	}

	public List<ItemRequisicionEta> getItmsRequi() {
		return itmsRequi;
	}

	public void setItmsRequi(List<ItemRequisicionEta> itmsRequi) {
		this.itmsRequi = itmsRequi;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public String getComentarioAnterior() {
		return comentarioAnterior;
	}

	public void setComentarioAnterior(String comentarioAnterior) {
		this.comentarioAnterior = comentarioAnterior;
	}


	public String getNombreSiguienteEtapa() {
		return nombreSiguienteEtapa;
	}


	public void setNombreSiguienteEtapa(String nombreSiguienteEtapa) {
		this.nombreSiguienteEtapa = nombreSiguienteEtapa;
	}


	public boolean isBtnAceptarContinuar() {
		return btnAceptarContinuar;
	}


	public void setBtnAceptarContinuar(boolean btnAceptarContinuar) {
		this.btnAceptarContinuar = btnAceptarContinuar;
	}


	public Integer getIdSiguienteEtapa() {
		return idSiguienteEtapa;
	}


	public void setIdSiguienteEtapa(Integer idSiguienteEtapa) {
		this.idSiguienteEtapa = idSiguienteEtapa;
	}
	
	
	
	

}
