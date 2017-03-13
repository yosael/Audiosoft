package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.DetalleReparacionExterna;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.Proveedor;
import com.sa.model.inventory.ReparacionExterna;
import com.sa.model.security.Empresa;

@Name("reparacionExternaHome")
@Scope(ScopeType.CONVERSATION)
public class ReparacionExternaHome extends KubeDAO<ReparacionExterna> {
	
	
	private static final long serialVersionUID = 1L;
	private List<ReparacionExterna> resultList;
	private int idReparacionExterna;
	private List<DetalleReparacionExterna> detalleReparacion= new ArrayList<DetalleReparacionExterna>();
	private int indiceDetalle;
	private int contadorDetalle=0;
	private List<CodProducto> listaCodigos;

	@In
	private LoginUser loginUser;
	
	
	public void load()
	{
		
		try{
			
			setInstance((ReparacionExterna) getEntityManager().createQuery("select r from ReparacionExterna r where r.idReparacionExterna=:idReparacion")
					.setParameter("idReparacion", idReparacionExterna).getSingleResult());
			setDetalleReparacion(instance.getDetalleReparacion());
			
			
			
		}catch (Exception e) {
			
			//clearInstance();
			setInstance(new ReparacionExterna());
			
			//Extraer empresa audiomed
			Empresa empresa = new Empresa();
			empresa = (Empresa) getEntityManager().createQuery("SELECT e FROM Empresa e where e.id=:idEmpresa").setParameter("idEmpresa", 2).getSingleResult();
			instance.setEmpresa(empresa);
		}
	}
	
	public void cargarReparaciones()
	{
		resultList = new ArrayList<ReparacionExterna>();
		resultList = getEntityManager().createQuery("SELECT r FROM ReparacionExterna r").getResultList();
	}
	
	public void agregarAparato(Producto aparato)
	{
		
		DetalleReparacionExterna detalle = new DetalleReparacionExterna();
		
		detalle.setAparato(aparato);
		detalle.setReparacionExterna(instance);
		detalle.setEstado("GEN");
		detalle.setFechaModificacion(new Date());
		
		detalleReparacion.add(detalle);
	}
	
	public void agregarPieza(Producto pieza)
	{
		
		detalleReparacion.get(indiceDetalle).setPiezaReparacion(pieza);
		detalleReparacion.get(indiceDetalle).setFechaModificacion(new Date());
	}
	
	public void aumentarContador()
	{
		contadorDetalle++;
		System.out.println("Contador"+contadorDetalle);
	}
	
	public void seleccionarProveedor(Proveedor proveedor)
	{
		
		instance.setProveedor(proveedor);
	}
	
	public void cargarCodigosProducto(Producto aparato,int numero)
	{
		setIndiceDetalle(numero);
		listaCodigos = new ArrayList<CodProducto>();
		
		//listaCodigos = getEntityManager().createQuery("SELECT c FROM CodProducto c where ")
		Inventario inv = (Inventario) getEntityManager()
				.createQuery(
						"SELECT i FROM Inventario i WHERE i.sucursal = :suc AND i.producto = :prd")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("prd", aparato).getSingleResult();

		// Sacamos el inventario del producto
		listaCodigos = (ArrayList<CodProducto>) getEntityManager()
				.createQuery(
						"SELECT c FROM CodProducto c "
								+ "	WHERE c.inventario.producto = :prd AND c.inventario = :inv AND c.estado = 'ACT' ")
				.setParameter("prd", aparato)
				.setParameter("inv", inv).getResultList();
		
		
	}
	
	public void seleccionarCodigo(CodProducto codigo)
	{
		detalleReparacion.get(indiceDetalle).setCodigo(codigo);
		
	}

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		
		System.out.println("Presave");
		
		instance.setFechaCreacion(new Date());
		instance.setFechaModificacion(new Date());
		
		
		instance.setDetalleReparacion(detalleReparacion);
		
		return true;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
				
		//getEnti
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<DetalleReparacionExterna> getDetalleReparacion() {
		return detalleReparacion;
	}

	public void setDetalleReparacion(
			List<DetalleReparacionExterna> detalleReparacion) {
		this.detalleReparacion = detalleReparacion;
	}

	public int getIndiceDetalle() {
		return indiceDetalle;
	}

	public void setIndiceDetalle(int indiceDetalle) {
		this.indiceDetalle = indiceDetalle;
	}

	public int getContadorDetalle() {
		return contadorDetalle;
	}

	public void setContadorDetalle(int contadorDetalle) {
		this.contadorDetalle = contadorDetalle;
		this.contadorDetalle++;
		System.out.println("Contador detalle"+contadorDetalle);
	}

	public List<ReparacionExterna> getResultList() {
		return resultList;
	}

	public void setResultList(List<ReparacionExterna> resultList) {
		this.resultList = resultList;
	}

	public int getIdReparacionExterna() {
		return idReparacionExterna;
	}

	public void setIdReparacionExterna(int idReparacionExterna) {
		this.idReparacionExterna = idReparacionExterna;
	}
	
	

}
