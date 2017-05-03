package com.sa.kubekit.action.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Inventario;
import com.sa.model.security.AreaNegocio;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Name("notificacionUserHome")
@Scope(ScopeType.CONVERSATION)
public class NotificacionUserHome extends KubeDAO<Usuario>{

	private static final long serialVersionUID = 1L;
	private boolean shofNotif = false;
	public Integer conteo1;
	private HashMap<String, Object> dtRp = new HashMap<String, Object>();
	List<Object[]> etapasRepCli = new ArrayList<Object[]>();
	private boolean trabajosTaller=false;
	
	@In
	private LoginUser loginUser;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("notifuser_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("notifuser_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("notifuser_deleted")));
	}
	
	public void notificacionesInicio() {
		if(loginUser.getUser() != null && loginUser.getUser().isNotificacionInv()) {
			//Sacamos la lista de items que estan abajo del limite de existencias
			String hql = "SELECT x FROM Inventario x WHERE 1 = 1 " +
					"	AND x.producto.cantidadMinima > x.cantidadActual " +
					"	AND x.sucursal = :suc  ORDER BY x.producto.categoria.codigo ASC, " +
					"	x.producto.referencia ASC ";
				
			List<Inventario> inventarios = getEntityManager().createQuery(hql)
					.setParameter("suc", loginUser.getUser().getSucursal())
					.getResultList();
			if(inventarios == null)
				inventarios = new ArrayList<Inventario>();
			
			dtRp.put("prdExisLim", inventarios);
		}
		
		//System.out.println("ID del usuario"+loginUser.getUser().getId());
		
		if(loginUser.getUser().getAreaUsuario()!=null)
		{
		
			Integer idArea;
			//idArea = (Integer) getEntityManager().createQuery("SELECT a.areaUsuario.id FROM Usuario a where a.id=:idUs").setParameter("idUs", loginUser.getUser().getId()).getSingleResult();
			idArea=loginUser.getUser().getAreaUsuario().getId();
			//System.out.println("Area de negocio"+loginUser.getUser().getAreaUsuario().getNombre());
			//System.out.println("hola"+loginUser.getUser().getAreaUsuario().getNombre()+"hola");
			//System.out.println("Id Area negocio"+idArea);
			
			if(idArea==3)
			{
				//System.out.println("Entro a taller");
				try {
					
						//List<Object[]> etapasRepCli = new ArrayList<Object[]>();
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
										+ " and etc.estado = 'PEN' "
										+ " ORDER BY rpc.fecha_ingreso ASC ")
						.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
						.getResultList();
						
						if(etapasRepCli.size()>0)
						{
							FacesMessages.instance().add(Severity.WARN,"Tiene "+etapasRepCli.size()+" trabajos de taller pendientes");
							FacesMessages.instance().clear();
							trabajosTaller=true;
							//System.out.println("Entro al tamanio de las tareas");
						}
					
				} catch (Exception e) {
					System.out.println("Error al cargar");
					System.out.println(e.getMessage());
				}
				
			}
			/*else if(idArea==1)
			{//System.out.println("Entro a audiologia");
				try {
					
					Sucursal sucursalUser=new Sucursal();
					if(loginUser.getUser().getSucursal().getSucursalSuperior()!=null)
					{
						sucursalUser=loginUser.getUser().getSucursal().getSucursalSuperior();
					}
					else
						sucursalUser=loginUser.getUser().getSucursal();
					
					//List<Object[]> etapasRepCli = new ArrayList<Object[]>();
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
									+ " and etc.estado = 'PEN' and rpc.sucursal_id=:sucUser "
									+ " ORDER BY rpc.fecha_ingreso ASC ")
					.setParameter("neg", loginUser.getUser().getAreaUsuario().getId())
					.setParameter("sucUser",sucursalUser.getId())
					.getResultList();
					
					if(etapasRepCli.size()>0)
					{
						FacesMessages.instance().add(Severity.WARN,"Tiene "+etapasRepCli.size()+" trabajos de taller pendientes");
						FacesMessages.instance().clear();
						trabajosTaller=true;
						//System.out.println("Entro al tamanio de las tareas");
					}
					
				
				} catch (Exception e) {
					System.out.println("Error al cargar");
					System.out.println(e.getMessage());
				}
				
				
			}*/
		}
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

	
	public boolean isShofNotif() {
		return shofNotif;
	}

	public void setShofNotif(boolean shofNotif) {
		this.shofNotif = shofNotif;
	}

	public Integer getConteo1() {
		return conteo1;
	}

	public void setConteo1(Integer conteo1) {
		this.conteo1 = conteo1;
	}

	public HashMap<String, Object> getDtRp() {
		return dtRp;
	}

	public void setDtRp(HashMap<String, Object> dtRp) {
		this.dtRp = dtRp;
	}

	public List<Object[]> getEtapasRepCli() {
		return etapasRepCli;
	}

	public void setEtapasRepCli(List<Object[]> etapasRepCli) {
		this.etapasRepCli = etapasRepCli;
	}

	public boolean isTrabajosTaller() {
		return trabajosTaller;
	}

	public void setTrabajosTaller(boolean trabajosTaller) {
		this.trabajosTaller = trabajosTaller;
	}

	

	
	
}
