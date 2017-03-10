package com.sa.kubekit.action.acct;

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

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.CajaChicaDet;
import com.sa.model.acct.CajaChicaMov;

@Name("cajaChicaHome")
@Scope(ScopeType.CONVERSATION)
public class CajaChicaHome extends KubeDAO<CajaChicaMov>{

	private static final long serialVersionUID = 1L;
	private Integer cjcId;
	private List<CajaChicaMov> resultList = new ArrayList<CajaChicaMov>();
	private Date fechaConsulta;
	private Double monto;
	private String tipoMov; //CRG Cargo , ABO Abono
	private String comentario;
	private boolean cajaActiva = true;
	
	@In(required=true,create=true)
	@Out(required=true)
	private ConceptoMovHome conceptoMovHome;
	
	@In
	private LoginUser loginUser;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("concmv_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("concmv_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("concmv_deleted")));
	}
	
	public void load(){
		try{
			setCajaActiva(true);
			
			setInstance(getEntityManager().find(CajaChicaMov.class, cjcId));
			//Verificamos si la caja que estan cargando esta disponible para hacer movimientos o no
			if(!instance.getEstado().equals("ABI"))
				setCajaActiva(false);
			
			Calendar cal = new GregorianCalendar();
			cal.setTime(instance.getFecha());
			
			if( ! (cal.getTimeInMillis() >= truncDate(new Date(), false).getTime() && cal.getTimeInMillis() <= truncDate(new Date(), true).getTime() ))
				setCajaActiva(false);
			
		}catch (Exception e) {
			clearInstance();
			setInstance(new CajaChicaMov());
		}
	}
	
	public void getCajaChicaMovs(boolean consultaHistorico) {
		
		String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
		String sucFlt = " AND (:suc IS NULL OR 1 = 1) ";
		if(getFechaPFlt1() != null && getFechaPFlt2() != null) {
			setFechaPFlt1(truncDate(getFechaPFlt1(), false));
			setFechaPFlt2(truncDate(getFechaPFlt2(), true));
			fltFch = " AND c.fecha BETWEEN :fch1 AND :fch2 ";
		}
		
		if(!loginUser.getUser().isAccionEspecial())
			sucFlt = " AND c.sucursal = :suc ";
		
		resultList = getEntityManager()
				.createQuery("SELECT c FROM CajaChicaMov c " +
						"	WHERE 1 = 1 " + fltFch + sucFlt +
						"	ORDER BY c.fecha DESC")
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter("suc", loginUser.getUser().getSucursal())
				.getResultList();
		
		if(!consultaHistorico) {
			//Verificamos si para el dia actual hay un registro
			Date fechaHoy = new Date();
			
			Long existe = null;
			try {
				existe = (Long)getEntityManager()
					.createQuery("SELECT SUM(1) FROM CajaChicaMov c " +
							"	WHERE c.sucursal = :suc AND c.fecha BETWEEN :fch1 AND :fch2 " + 
							"	")
					.setParameter("fch1", truncDate(fechaHoy, false))
					.setParameter("fch2", truncDate(fechaHoy, true))
					.setParameter("suc", loginUser.getUser().getSucursal())
					.getSingleResult();
			} catch (Exception ex) {}
			
			if(existe == null || existe <= 0) {
				CajaChicaMov cjaHoy = new CajaChicaMov();
				cjaHoy.setFecha(new Date());
				cjaHoy.setMonto(0d);
				cjaHoy.setEstado("ABI");
				if(loginUser.getUser().getSucursal() != null)
					cjaHoy.setSucursal(loginUser.getUser().getSucursal());
				select(cjaHoy);
				save();
				getEntityManager().refresh(instance);
				resultList.add(0, instance);
			}
		}
	}
	
	public void guardarMovCaja() { 
		CajaChicaDet detCaja = new CajaChicaDet();
		conceptoMovHome.guardarConcepto();
		detCaja.setConcepto(conceptoMovHome.getInstance());
		detCaja.setFecha(new Date());
		detCaja.setMonto(monto);
		detCaja.setTipo(tipoMov);
		detCaja.setComentario(comentario);
		detCaja.setMovPrincipal(instance);
		getEntityManager().persist(detCaja);
		//Actualizamos el monto del encabezado
		if(tipoMov.equals("CRG")) {
			instance.setMonto(instance.getMonto() + getMonto());
			instance.setIngresos(instance.getIngresos() + getMonto());
		} else if(tipoMov.equals("ABO")) {
			instance.setMonto(instance.getMonto() - getMonto());
			instance.setGastos(instance.getGastos() + getMonto());
		}
		
		instance.setMonto(new Long(Math.round(instance.getMonto()*100))/100.0);
		modify();
		getEntityManager().refresh(instance);
		getEntityManager().flush();
		comentario = "";
		monto = 0.00d;
		tipoMov = "ABO";
		conceptoMovHome.setConcepto("");
		conceptoMovHome.clean();
		FacesMessages.instance().add(sainv_messages.get("cjachi_msg_movadded"));
	}
	
	public void borrarMovCaja(CajaChicaDet det) {
		if(det.getTipo().equals("CRG")) {
			instance.setMonto(instance.getMonto() - det.getMonto());
			instance.setIngresos(instance.getIngresos() - getMonto());
		} else if(det.getTipo().equals("ABO")) {
			instance.setMonto(instance.getMonto() + det.getMonto());
			instance.setGastos(instance.getGastos() - getMonto());
		}
		modify();
		getEntityManager().remove(det);
		getEntityManager().refresh(instance);
		getEntityManager().flush();
		FacesMessages.instance().add(
				sainv_messages.get("cjachi_msg_movdel"));
	}
	
	public boolean cerrarCajaChica() {
		//Actualizamos el estado de la caja chica para cerrar el dia de hoy
		instance.setEstado("CER");
		modify();
		getEntityManager().refresh(instance);
		getEntityManager().flush();
		FacesMessages.instance().add(
				sainv_messages.get("cjachi_msg_cjacerr"));
		return true;
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

	public List<CajaChicaMov> getResultList() {
		return resultList;
	}

	public void setResultList(List<CajaChicaMov> resultList) {
		this.resultList = resultList;
	}
	
	public Integer getCjcId() {
		return cjcId;
	}

	public void setCjcId(Integer cjcId) {
		this.cjcId = cjcId;
	}

	public Date getFechaConsulta() {
		return fechaConsulta;
	}

	public void setFechaConsulta(Date fechaConsulta) {
		this.fechaConsulta = fechaConsulta;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public String getTipoMov() {
		return tipoMov;
	}

	public void setTipoMov(String tipoMov) {
		this.tipoMov = tipoMov;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public boolean isCajaActiva() {
		return cajaActiva;
	}

	public void setCajaActiva(boolean cajaActiva) {
		this.cajaActiva = cajaActiva;
	}
	
	
}
