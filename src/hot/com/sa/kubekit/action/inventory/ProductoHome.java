package com.sa.kubekit.action.inventory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.Region;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.UploadEvent;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.UbicacionPrd;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;

@Name("productoHome")
@Scope(ScopeType.CONVERSATION)
public class ProductoHome extends KubeDAO<Producto> {
	private static final long serialVersionUID = 1L;
	private Integer productoId;
	private UbicacionPrd codUbicacion;
	private String nomCoinci = "";

	@In(required = false, create = true)
	private InventarioHome inventarioHome;

	@In(required = false, create = true)
	private CurrencyHome currencyHome;

	@In(required = false, create = true)
	private ClaseProductoHome claseProductoHome;

	@In
	private LoginUser loginUser;
	private DecimalFormat frmtMoney = new DecimalFormat("##########.##");

	private List<Producto> prdsExistencias = new ArrayList<Producto>();
	private List<Producto> prdsExistenciasExl = new ArrayList<Producto>();
	private List<Inventario> productos = new ArrayList<Inventario>();
	private List<Inventario> existencias = new ArrayList<Inventario>();
	private List<CodProducto> currCodigos = new ArrayList<CodProducto>();
	private List<UbicacionPrd> ubicacionesPrd = new ArrayList<UbicacionPrd>();
	private List<Inventario> ubiBodega = new ArrayList<Inventario>();
	private List<Sucursal> listaSucursales = new ArrayList<Sucursal>();

	private String referencia = new String();
	private String codigoBarras = new String();
	private Inventario selectedInv = new Inventario();
	private Empresa empresaSeleccionada;
	private Sucursal sucursalSeleccionada;
	private Sucursal sucursalFlt = new Sucursal() ;
	private List<Sucursal> sucList;
	private Integer totalInventario;
	private String codCatProd;
	private boolean mosExists;
	private boolean imgSize = true;
	private List<Inventario> lsInventarios;
	private String tipoProducto="";
	private String nombreSucursalSelec="";
	

	public void create() {
		setCreatedMessage(createValueExpression(this.sainv_messages
				.get("productoHome_created")));
		setUpdatedMessage(createValueExpression(this.sainv_messages
				.get("productoHome_updated")));
		setDeletedMessage(createValueExpression(this.sainv_messages
				.get("productoHome_deleted")));
	}

	// Método que convierte la imagen a un array de byte, luego asigna el
	// resultado al objeto instance
	public void loadImage(UploadEvent e) {
		String path = e.getUploadItem().getFileName();
		System.out.println("Path var: "+path);
		File file = new File(e.getUploadItem().getFile().getPath());
		int length = e.getUploadItem().getFileName().length();
		// validamos que la imagen no exceda los 100 KB
		if (file.length() > 100000) {
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("productoHome_error_image"));
			setImgSize(false);
		}  else {
			setImgSize(true);
			byte[] bFile = new byte[(int) file.length()];
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				// convert file into array of bytes
				fileInputStream.read(bFile);
				fileInputStream.close();
				instance.setImage(bFile);
				System.out.println(instance.getImage().length);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void getProductosExisLst() {

		// Calculamos el total en base a todos los inventarios
		getEntityManager().clear();
		prdsExistencias = getEntityManager()
				.createQuery(
						"select p from Producto p where (UPPER(p.referencia) like UPPER(:ref) or UPPER(p.nombre) like UPPER(:ref) or UPPER(p.categoria.codigo) like UPPER(:ref) or UPPER(p.modelo) like UPPER(:ref)) order by p.referencia,p.categoria.codigo,p.nombre ")
				.setParameter("ref", "%" + this.getNomCoinci() + "%")
				.getResultList();

		totalInventario = 0;
		for (Producto tmpPrd : prdsExistencias) {
			Integer conteo = 0;
			for (Inventario tmpInv : tmpPrd.getInventarios()) {
				if (sucursalFlt != null) {System.out.println("Sucursal no nula");//linea de prueba
					if (tmpInv.getSucursal().getId()
							.equals(sucursalFlt.getId())) {
						conteo += tmpInv.getCantidadActual();
						break;
					}
				} else {
					conteo += tmpInv.getCantidadActual();
				}
			}
			tmpPrd.setTotalPrds(conteo);
		}
	}

	public void limpiarValores() {
		codCatProd = "";
		mosExists = false;
	}

	public void verificarExistenciasProducto() {
		System.out.println("Entrando al metodo");
		// Calculamos el total en base a todos los inventarios
		String hql = "SELECT p FROM Producto p WHERE 1 = 1";
		if (codCatProd != null) {
			if (codCatProd.length() > 0) {
				hql += " AND p.categoria.codigo LIKE '" + codCatProd + "%'";
			}
		}

		hql += " ORDER BY p.referencia";
		System.out.println(hql);
		prdsExistencias = getEntityManager().createQuery(hql).getResultList();

		totalInventario = 0;
		for (Producto tmpPrd : prdsExistencias) {
			Integer conteo = 0;
			for (Inventario tmpInv : tmpPrd.getInventarios()) {
				if (sucursalFlt != null) {
					if (tmpInv.getSucursal().getId()
							.equals(sucursalFlt.getId())) {
						conteo += tmpInv.getCantidadActual();
						break;
					}
				} else {
					conteo += tmpInv.getCantidadActual();
				}
			}
			tmpPrd.setTotalPrds(conteo);
		}

		prdsExistenciasExl = new ArrayList<Producto>();
		System.out.println("Mostrar Existencias: " + mosExists);
		if (mosExists = true) {
			for (Producto tmpPrd : prdsExistencias) {
				if (tmpPrd.getTotalPrds() > 0) {
					prdsExistenciasExl.add(tmpPrd);
				}
			}
		} else {
			prdsExistenciasExl = prdsExistencias;
		}
		System.out.println("Existencias: " + prdsExistenciasExl.size());
	}

	public void actuUbicacion(UbicacionPrd ubi) {
		System.out.println("Llegue a actu y los valores son Ubi: "
				+ ubi.getNombre() + " Sucursal: "
				+ this.sucursalSeleccionada.getNombre() + " Id: "
				+ instance.getId());
		try {
			getEntityManager().clear();
			getEntityManager().joinTransaction();
			getEntityManager()
					.createQuery(
							"UPDATE Inventario i SET i.codUbicacion = :ubi WHERE (i.producto.id = :id AND i.sucursal = :suc) ")
					.setParameter("ubi", ubi)
					.setParameter("id", instance.getId())
					.setParameter("suc", this.sucursalSeleccionada)
					.executeUpdate();
			getEntityManager().flush();
			load();
		} catch (NullPointerException n) {
			//
		}
	}

	public void cargarListaProductos() {
		String filtro1 = "%";

		if (!this.referencia.isEmpty()) {
			filtro1 = this.referencia;
		}
		if (this.sucursalSeleccionada != null)
			this.productos = getEntityManager()
					.createQuery(
							"select i from Inventario i where i.sucursal = :sucursal and i.producto.referencia like :ref order by i.producto.nombre")
					.setParameter("sucursal", this.sucursalSeleccionada)
					.setParameter("ref", filtro1).getResultList();
		else
			this.productos = getEntityManager()
					.createQuery(
							"select i from Inventario i where i.sucursal = :sucursal and i.producto.referencia like :ref order by i.producto.nombre")
					.setParameter("sucursal",
							this.loginUser.getUser().getSucursal())
					.setParameter("ref", filtro1).getResultList();
	}

	public List<Object[]> coinciPrdSrv(Object o) {
		List<Object[]> res = new ArrayList<Object[]>();
		List<Object[]> prds = getEntityManager()
				.createQuery(
						"select i.producto.referencia,i.producto.nombre, i.producto.marca.nombre, i.cantidadActual, i "
								+ "	from Inventario i where "
								+ "  i.sucursal = :sucursal "
								+ "	AND ((UPPER(i.producto.referencia) like UPPER(:ref)) "
								+ "	OR UPPER(i.producto.nombre) like UPPER(:ref)) "
								+ "	order by i.producto.referencia ASC ")
				.setParameter("sucursal",
						this.loginUser.getUser().getSucursal())
				.setParameter("ref", "%" + o.toString() + "%")
				.setMaxResults(30).getResultList();
		res.addAll(prds);
		return res;
	}

	public List<Object[]> coinciPrdSuc(Object o) {

		List<Object[]> res = new ArrayList<Object[]>();
		List<Object[]> prds = getEntityManager()
				.createQuery(
						"select i.producto.referencia,i.producto.nombre, i.producto.marca.nombre, i.cantidadActual, i "
								+ "	from Inventario i where "
								+ "  i.sucursal = :sucursal "
								+ "	AND ((UPPER(i.producto.referencia) like UPPER(:ref)) "
								+ "	OR UPPER(i.producto.nombre) like UPPER(:ref)) "
								+ "	order by i.producto.referencia ASC ")
				.setParameter("sucursal", sucursalSeleccionada)
				.setParameter("ref", "%" + o.toString() + "%")
				.setMaxResults(30).getResultList();

		res.addAll(prds);
		return res;
	}

	public void buscadorProductos() {
		if (this.sucursalSeleccionada != null)
			this.productos = getEntityManager()
					.createQuery(
							"select i from Inventario i where (i.sucursal = :sucursal) and"
									+ " (UPPER(i.producto.referencia) like UPPER(:cod) or UPPER(i.producto.nombre) like UPPER(:nom)) order by i.producto.referencia,i.producto.nombre")
					.setParameter("cod", "%" + nomCoinci + "%")
					.setParameter("nom", "%" + nomCoinci + "%")
					.setParameter("sucursal", this.sucursalSeleccionada)
					.getResultList();
		else
			this.productos = getEntityManager()
					.createQuery(
							"select i from Inventario i where (i.sucursal = :sucursal) AND"
									+ " (UPPER(i.producto.referencia) like UPPER(:cod) or UPPER(i.producto.nombre) like UPPER(:nom)) order by i.producto.referencia,i.producto.nombre")
					.setParameter("sucursal",
							this.loginUser.getUser().getSucursal())
					.setParameter("cod", "%" + nomCoinci + "%")
					.setParameter("nom", "%" + nomCoinci + "%").getResultList();
	}

	public void cargarListaProductos(Sucursal sucFiltro) {
		setSucursalSeleccionada(sucFiltro);
		cargarListaProductos();
	}

	public void setUbicacionPrd(UbicacionPrd ubi) {
		this.codUbicacion = ubi;
	}

	public UbicacionPrd getUbicacionPrd() {
		return this.codUbicacion;
	}

	public void cargarListaUbicaciones() {

		if (sucursalSeleccionada == null) {

		} else {
			System.out.println(this.sucursalSeleccionada.getNombre());
			this.ubicacionesPrd = getEntityManager()
					.createQuery(
							"SELECT u FROM UbicacionPrd u WHERE u.sucursal = :suc")
					.setParameter("suc", this.sucursalSeleccionada)
					.getResultList();
			getEntityManager().clear();
		}
	}

	public void cargarUbiPrd() {
		this.ubiBodega.clear();
		if (sucursalSeleccionada == null) {
			this.ubiBodega = getEntityManager()
					.createQuery(
							"SELECT i FROM Inventario i where (i.sucursal = :suc AND i.producto.id = :prod)")
					.setParameter("suc", this.loginUser.getUser().getSucursal())
					.setParameter("prod", instance.getId()).getResultList();
		} else {
			this.ubiBodega = getEntityManager()
					.createQuery(
							"SELECT u FROM Inventario i JOIN UbicacionPrd u ON i.codUbicacion = u  "
									+ "where (i.sucursal = :suc AND i.producto.id = :prod)")
					.setParameter("suc", this.sucursalSeleccionada)
					.setParameter("prod", instance.getId()).getResultList();
		}
	}

	public void cargarListaUbiPrd() {
		this.ubiBodega.clear();
		this.ubiBodega = getEntityManager()
				.createQuery(
						"SELECT i FROM Inventario i where (i.producto.id = :prod)")
				.setParameter("prod", instance.getId()).getResultList();
	}

	public void cargarListaProdsTaller(Sucursal sucFiltro) {
		this.productos = getEntityManager()
				.createQuery(
						"select i from Inventario i where i.sucursal = :sucursal order by i.producto.nombre ")
				.setParameter("sucursal", sucFiltro).getResultList();
	}

	public void cargarListaProdsCat(Categoria cat, Sucursal suc) {
		Sucursal sucFiltro = this.loginUser.getUser().getSucursal();
		if (suc != null) {
			sucFiltro = suc;
		}
		this.productos = getEntityManager()
				.createQuery(
						"select i from Inventario i where   i.sucursal = :sucursal AND i.producto.categoria = :cat order by i.producto.nombre ")
				.setParameter("sucursal", sucFiltro).setParameter("cat", cat)
				.getResultList();
	}

	public void load() {
		try {
			setInstance((Producto) getEntityManager().find(Producto.class,
					this.productoId));

			if (this.loginUser.getUser().getSucursal() != null) {
				List<Inventario> lstInvs = getEntityManager()
						.createQuery(
								"SELECT i FROM Inventario i \tWHERE i.sucursal = :suc AND i.producto = :prd")
						.setParameter("suc",
								this.loginUser.getUser().getSucursal())
						.setParameter("prd", this.instance).getResultList();
				if ((lstInvs != null) && (!lstInvs.isEmpty()))
					this.codUbicacion = ((Inventario) lstInvs.get(0))
							.getCodUbicacion();
			}
		} catch (Exception e) {
			clearInstance();
			setInstance(new Producto());
			if ((this.loginUser.getUser() != null)
					&& (this.loginUser.getUser().getSucursal() != null))
				instance.setEmpresa(this.loginUser.getUser().getSucursal()
						.getEmpresa());
		} finally {
			this.currencyHome.getMonedasList();
			this.claseProductoHome.getClasesProductoList();
		}

		if (this.instance != null)
			actualizarMontos();

		cargarListaUbiPrd();
	}

	public void loadConsolidadoExis() {
		List<Producto> prdElim = new ArrayList<Producto>();
		String hql = "SELECT p FROM Producto p  WHERE 1=1 ORDER BY p.tipo ASC,p.referencia,p.nombre ";
		prdsExistencias = getEntityManager().createQuery(hql).getResultList();
		// Calculamos el total en base a todos los inventarios
		totalInventario = 0;
		for (Producto tmpPrd : prdsExistencias) {
			Integer conteo = 0;
			for (Inventario tmpInv : tmpPrd.getInventarios()) 
			{
				
				if(nombreSucursalSelec!=null && !nombreSucursalSelec.equals("")) 
				{
					if (tmpInv.getSucursal().getNombre().equals(nombreSucursalSelec)) 
					{
						conteo += tmpInv.getCantidadActual();
						break;
					}
				} else {
					conteo += tmpInv.getCantidadActual();
				}
				
			}
			if (conteo > 0) 
			{
				tmpPrd.setTotalPrds(conteo);
				totalInventario += conteo;
				
			} else {
				prdElim.add(tmpPrd);
			}
			
		}
		prdsExistencias.removeAll(prdElim);
		System.out.println("Entro a liad exis");
		
		System.out.println("SYYys"+prdsExistencias.size());
	}
	
	public void loadConsolidadoInexis()//pro
	{
		lsInventarios = new ArrayList<Inventario>();
		int cero=0;
		
		//System.out.println("sucursal"+sucursalFlt.getNombre());
		
		String jpql = "SELECT i FROM Inventario i where i.cantidadActual='"+cero+"'  group by i.producto.referencia,i.id,i.producto.nombre,i.producto.marca,i.producto.modelo,i.producto.categoria.nombre order by i.producto.referencia";
		lsInventarios=getEntityManager().createQuery(jpql).getResultList();
		
		System.out.println("tam "+lsInventarios.size());
	}
	
	public void verificarSucursal()
	{
		if(nombreSucursalSelec==null)
			setNombreSucursalSelec("");
		
		System.out.println("suc selected *****"+nombreSucursalSelec);
	}
	
	public void cargarExcel()
	{
		System.out.println("Entro a cargar excel");
		List<Producto> prdElim = new ArrayList<Producto>();
		
		//System.out.println("Tipo seleccionado"+tipoProducto);
		//System.out.println("sucursal selected"+sucursalFlt.getNombre());
		
		/*String jpql="";
		
		if(tipoProducto.equals("A"))
		{
			 jpql = "SELECT p FROM Producto p  WHERE 1=1 and p.tipo='A' ORDER BY p.referencia,p.tipo ASC ";
			 System.out.println("Entro a A");
		}
		else if(tipoProducto.equals("B"))
		{
			jpql = "SELECT p FROM Producto p  WHERE 1=1 and p.tipo='B' ORDER BY p.referencia,p.tipo ASC ";
			System.out.println("Entro a b");
		}
		else if(tipoProducto.equals("C"))
			jpql = "SELECT p FROM Producto p  WHERE 1=1 and p.tipo='C' ORDER BY p.referencia,p.tipo ASC ";
		else
			jpql = "SELECT p FROM Producto p  WHERE 1=1 ORDER BY p.referencia,p.tipo ASC ";*/
		
		
		if(tipoProducto!=null)
		{
		
			if(!tipoProducto.equals(""))
			{
				System.out.println("disdd");
				prdsExistenciasExl = getEntityManager()
						.createQuery(
								"select p from Producto p where p.tipo=:tipo order by p.referencia ")
						.setParameter("tipo",tipoProducto)
						.getResultList();
			}
			else
			{
				prdsExistenciasExl = getEntityManager()
						.createQuery(
								"select p from Producto p order by p.referencia ")
						.getResultList();
			}
		}
		else
		{
			prdsExistenciasExl = getEntityManager()
					.createQuery(
							"select p from Producto p  order by p.referencia ")
					.getResultList();
		}
		
		
		//prdsExistenciasExl = getEntityManager().createQuery(jpql).getResultList();
		// Calculamos el total en base a todos los inventarios
		totalInventario = 0;
		for (Producto tmpPrd : prdsExistenciasExl) {
			Integer conteo = 0;
			for (Inventario tmpInv : tmpPrd.getInventarios()) {
				/*if (sucursalFlt != null) {
					if (tmpInv.getSucursal().getId()
							.equals(sucursalFlt.getId())) {
						conteo += tmpInv.getCantidadActual();
						break;
					}
				} else {*/
					conteo += tmpInv.getCantidadActual();
				//}
			}
			if (conteo > 0) {
				tmpPrd.setTotalPrds(conteo);
				totalInventario += conteo;
			} else {
				prdElim.add(tmpPrd);
			}
		}
		prdsExistenciasExl.removeAll(prdElim);
		
		System.out.println("Tam cargar"+prdsExistenciasExl.size());
		
		
	}
	
	public void loadConsolidadoExisExcel() throws IOException
	{
		
		//cargarExcel();
		
		loadConsolidadoExis();
		
		System.out.println(prdsExistenciasExl.size());
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader(
				"Content-Disposition",
				"attachment;filename=Existencias-"
						+ sdf.format(cal.getTime()) + ".xls");
		
		
		HSSFWorkbook libro = new HSSFWorkbook();
		HSSFSheet hoja = libro.createSheet();
		CreationHelper ch = libro.getCreationHelper();

		HSSFRow fila;
		HSSFCell celda;

		// definicion de estilos para las celdas
		HSSFFont headfont = libro.createFont(), headfont2 = libro
				.createFont(),headfontW = libro.createFont(), headfont3 = libro.createFont();
		headfont.setFontName("Arial");
		headfont.setFontHeightInPoints((short) 8);
		headfont2.setFontName("Arial");
		headfont2.setFontHeightInPoints((short) 10);
		headfont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		headfontW.setFontName("Arial");
		headfontW.setFontHeightInPoints((short) 10);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 8);
		//headfont3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		

					HSSFCellStyle stAling = libro.createCellStyle(), stDate = libro
							.createCellStyle(), stAlingRight = libro.createCellStyle(), stTitles = libro.createCellStyle(),stTitlesD = libro
							.createCellStyle(),stTitlesI = libro.createCellStyle(), stTotals = libro.createCellStyle(), stList = libro
							.createCellStyle(), stFinal = libro.createCellStyle(), stPorcent = libro
							.createCellStyle();

					// Para Formatos de dolar y porcentaje
					DataFormat estFormato = libro.createDataFormat();

					stAling.setFont(headfont);
					stAling.setWrapText(true);
					stAling.setAlignment(stAling.ALIGN_RIGHT);
					stAling.setDataFormat(estFormato.getFormat("$#,#0.00"));

					stDate.setDataFormat(ch.createDataFormat().getFormat("dd/mm/yy"));
					stDate.setFont(headfont3);

					stTitles.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stTitles.setAlignment(stTitles.ALIGN_CENTER);
					stTitles.setFont(headfontW);
					stTitles.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					//stTitlesD.setFillBackgroundColor(HSSFColor.RED.index);
					stTitles.setFillForegroundColor(HSSFColor.GREEN.index);
					
					stTitlesD.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stTitlesD.setAlignment(stTitles.ALIGN_CENTER);
					stTitlesD.setFont(headfontW);
					stTitlesD.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					//stTitlesD.setFillBackgroundColor(HSSFColor.RED.index);
					stTitlesD.setFillForegroundColor(HSSFColor.RED.index);
					
					stTitlesI.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stTitlesI.setAlignment(stTitles.ALIGN_CENTER);
					stTitlesI.setFont(headfontW);
					stTitlesI.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					//stTitlesD.setFillBackgroundColor(HSSFColor.RED.index);
					stTitlesI.setFillForegroundColor(HSSFColor.BLUE.index);

					stList.setAlignment(stList.ALIGN_CENTER);
					//stList.setVerticalAlignment(stList.VERTICAL_TOP);
					stList.setWrapText(true);
					stList.setFont(headfont3);

					stFinal.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stFinal.setAlignment(stTitles.ALIGN_RIGHT);
					stFinal.setFont(headfont3);
					stFinal.setDataFormat(estFormato.getFormat("$#,#0.00"));

					// Estilo para porcentaje
					stPorcent.setFont(headfont);
					stPorcent.setWrapText(true);
					stPorcent.setAlignment(stAlingRight.ALIGN_RIGHT);
					stPorcent.setDataFormat(estFormato.getFormat("#0.#00%"));
					
					
					// agregando la lista de productos, srv, combos.
					fila = hoja.createRow(1);
					
					celda = fila.createCell(0);
					celda.setCellValue("Codigo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Nombre");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Marca");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Modelo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Tipo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(5);
					celda.setCellValue("Categoria");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(6);
					celda.setCellValue("Cod Cat");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(7);
					celda.setCellValue("Cantidad");
					celda.setCellStyle(stTitles);
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT r.estado,r.fechaEntrega,r.fechaIngreso,cond.condAparato.nombre,CONCAT(r.cliente.nombres,' ',r.cliente.apellidos),
					 * r.aparatoRep.marca,r.aparatoRep.modelo,r.aparatoRep.numSerie,r.id,r.costo,r,r.proceso.nombre FROM ReparacionCliente r,
					 * CondAparatoRep cond where r.id=cond.repCliente.id";*/
					System.out.println("Entro al metodo");
					int contFila=2;//,contCelda=0;
					
					System.out.println("size "+prdsExistenciasExl.size());
					int total=0;
					 //prdsExistenciasExl
					for(Producto prod: prdsExistencias)
					{
						System.out.println("for");
						fila = hoja.createRow(contFila);
						System.out.println("Fila "+contFila);
						
						celda=fila.createCell(0);//Codigo 
						celda.setCellValue(prod.getReferencia());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						 
						celda=fila.createCell(1); //Nombre
						celda.setCellValue(prod.getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2); //Marca
						celda.setCellValue(prod.getMarca().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //Modulo
						celda.setCellValue(prod.getModelo());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						celda=fila.createCell(4); //Tipo 
						celda.setCellValue(prod.getTipo());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(4);
						
						celda=fila.createCell(5); //Categoria 
						celda.setCellValue(prod.getCategoria().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(5);
						
						celda=fila.createCell(6); //Cod cat 
						celda.setCellValue(prod.getCategoria().getCodigo());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(6);
						
						celda=fila.createCell(7); //Cantidad 
						celda.setCellValue(prod.getTotalPrds());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(7);
						
						contFila++;
						total+=prod.getTotalPrds();
						
					}
					
					fila = hoja.createRow(contFila);
					
					celda = fila.createCell(6);
					celda.setCellValue("Total: ");
					celda.setCellStyle(stList);
					hoja.autoSizeColumn(6);
					
					celda = fila.createCell(7);
					celda.setCellValue(total);
					celda.setCellStyle(stList);
					hoja.autoSizeColumn(7);
					
					
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
		
	}
	
	public void findByType()
	{
		
	System.out.println("Entro a tipe");
				
		getEntityManager().clear();
		
		if(tipoProducto!=null)
		{
		
			if(!tipoProducto.equals(""))
			{
				System.out.println("disdd");
				prdsExistencias = getEntityManager()
						.createQuery(
								"select p from Producto p where p.tipo=:tipo order by p.referencia ")
						.setParameter("tipo",tipoProducto)
						.getResultList();
			}
			else
			{
				prdsExistencias = getEntityManager()
						.createQuery(
								"select p from Producto p order by p.referencia ")
						.getResultList();
			}
		}
		else
		{
			prdsExistencias = getEntityManager()
					.createQuery(
							"select p from Producto p  order by p.referencia ")
					.getResultList();
		}

		totalInventario = 0;
		for (Producto tmpPrd : prdsExistencias) {
			Integer conteo = 0;
			for (Inventario tmpInv : tmpPrd.getInventarios()) {
				/*if (sucursalFlt != null) {
					if (tmpInv.getSucursal().getId()
							.equals(sucursalFlt.getId())) {
						conteo += tmpInv.getCantidadActual();
						break;
					}
				} else {*/
					conteo += tmpInv.getCantidadActual();
				//}
			}
			tmpPrd.setTotalPrds(conteo);
		}
		
		System.out.println(prdsExistencias.size());
		
	}

	public void loadSucursalesRep() {
		List<Inventario> prdElim = new ArrayList<Inventario>();
		String hql = "SELECT s FROM Sucursal s WHERE 	1=1 ";
		
		System.out.println("entro al metodo");
		if (!nombreSucursalSelec.equals(""))
		{
			hql += " AND s.nombre = :suc ";
			System.out.println("sucursal seleccionada"+nombreSucursalSelec);
			
			sucList = getEntityManager().createQuery(hql)
					.setParameter("suc", nombreSucursalSelec).getResultList();
		}
		else
		{
			hql += " AND :suc IS NULL ";
			System.out.println("sucursal vacia");
			
			sucList = getEntityManager().createQuery(hql)
					.setParameter("suc", sucursalFlt).getResultList();
		}

		

		// Calculamos el total por sucursal
		for (Sucursal tmpS : sucList) {
			Integer totSuc = 0;
			for (Inventario tmpInv : tmpS.getInventarios()) {
				totSuc += tmpInv.getCantidadActual();
				if (tmpInv.getCantidadActual() <= 0)
					prdElim.add(tmpInv);
			}

			tmpS.getInventarios().removeAll(prdElim);
			prdElim = new ArrayList<Inventario>();
			tmpS.setTotalInvs(totSuc);
		}
	}
	
	public void cargarSucursales() {
		listaSucursales = getEntityManager()
				.createQuery("SELECT s FROM Sucursal s ORDER BY s.codigo ASC")
				.getResultList();
		//Cargamos las surcursales que no son bodegas
		//System.out.println("Entre a cargarSuc");
		
			//System.out.println("tamaño del result list: "+notBodegasSuc.size()); 
			
			//System.out.println("Sucurusal **** "+loginUser.getUser().getSucursal().getNombre());
			
		
		setSucursalFlt(listaSucursales.get(0));
		System.out.println("Cargo la primera sucursal");
	}

	public void loadExistencias() {
		try {
			setInstance(getEntityManager()
					.find(Producto.class, this.productoId));

			if (this.loginUser.getUser().getSucursal() != null) {
				List<Inventario> lstInvs = getEntityManager()
						.createQuery(
								"SELECT i FROM Inventario i "
										+ "	WHERE i.sucursal = :suc AND i.producto = :prd")
						.setParameter("suc",
								this.loginUser.getUser().getSucursal())
						.setParameter("prd", this.instance).getResultList();
				if ((lstInvs != null) && (!lstInvs.isEmpty())) {
					this.codUbicacion = lstInvs.get(0).getCodUbicacion();
				}
				this.existencias = getEntityManager()
						.createQuery(
								"SELECT i FROM Inventario i "
										+ "	WHERE i.producto = :prd AND i.sucursal.empresa = :emp")
						.setParameter("prd", this.instance)
						.setParameter(
								"emp",
								this.loginUser.getUser().getSucursal()
										.getEmpresa()).getResultList();
			} else {
				this.existencias = getEntityManager()
						.createQuery(
								"SELECT i FROM Inventario i "
										+ "	WHERE i.producto = :prd")
						.setParameter("prd", this.instance).getResultList();
			}
		} catch (Exception e) {
			clearInstance();
			setInstance(new Producto());
			if ((this.loginUser.getUser() != null)
					&& (this.loginUser.getUser().getSucursal() != null))
				instance.setEmpresa(this.loginUser.getUser().getSucursal()
						.getEmpresa());
		}
	}

	public void cargarListaCodigos(Inventario inv) {
		System.out.println("Entre a CargarListaCodigos "
				+ inv.getItems().toArray().toString());
		this.selectedInv = inv;
		this.currCodigos = ((ArrayList) getEntityManager()
				.createQuery(
						"SELECT c FROM CodProducto c WHERE c.inventario = :inv AND c.estado = 'ACT' ")
				.setParameter("inv", inv).getResultList());
	}

	public void actualizarMontoPrd(Producto prd) {
		setInstance(prd);
		actualizarMontos();
	}

	public void actualizarMontos() {
		if ((instance.getCosto() != null) && (instance.getMoneda() != null) && (instance.getClaseProducto() != null)) 
		{
			Float tmpTotal = Float.valueOf(instance.getCosto().floatValue());
			tmpTotal = Float
					.valueOf(tmpTotal.floatValue()
							+ (instance.getPercentImport() != null ? instance
									.getPercentImport().shortValue()
									* instance.getCosto().floatValue() / 100.0F
									: 0.0F));
			tmpTotal = Float.valueOf(tmpTotal.floatValue()
					+ (instance.getPercentTaxes() != null ? instance
							.getPercentTaxes().shortValue()
							* tmpTotal.floatValue() / 100.0F : 0.0F));

			instance.setPrcNormal(Float.valueOf(0.0F));
			instance.setPrcMinimo(Float.valueOf(0.0F));
			instance.setPrcOferta(Float.valueOf(0.0F));

			instance.setPrcNormal(Float.valueOf((tmpTotal.floatValue() + (instance
					.getClaseProducto().getGananciaNormal() != null ? instance
					.getClaseProducto().getGananciaNormal().floatValue()
					* tmpTotal.floatValue() / 100.0F : 0.0F))
					* instance.getMoneda().getMaxVal().floatValue()));
			instance.setPrcMinimo(Float.valueOf((tmpTotal.floatValue() + (instance
					.getClaseProducto().getGananciaMinima() != null ? instance
					.getClaseProducto().getGananciaMinima().floatValue()
					* tmpTotal.floatValue() / 100.0F : 0.0F))
					* instance.getMoneda().getMaxVal().floatValue()));
			instance.setPrcOferta(Float.valueOf((tmpTotal.floatValue() + (instance
					.getClaseProducto().getGananciaOferta() != null ? instance
					.getClaseProducto().getGananciaOferta().floatValue()
					* tmpTotal.floatValue() / 100.0F : 0.0F))
					* instance.getMoneda().getMaxVal().floatValue()));

			instance.setPrcNormal(Float.valueOf(this.frmtMoney.format(instance
					.getPrcNormal())));
			instance.setPrcMinimo(Float.valueOf(this.frmtMoney.format(instance
					.getPrcMinimo())));
			instance.setPrcOferta(Float.valueOf(this.frmtMoney.format(instance
					.getPrcOferta())));
		}
	}

	public boolean preSave() {
		if (instance.getEmpresa() == null) {
			FacesMessages.instance().add(
					this.sainv_messages.get("productoHome_error_save1"),
					new Object[0]);
			return false;
		}
		return true;
	}

	public boolean preModify() {
		if (instance.getEmpresa() == null) {
			FacesMessages.instance().add(
					this.sainv_messages.get("productoHome_error_save1"),
					new Object[0]);
			return false;
		}
		return true;
	}

	public boolean preDelete() {
		try {
			if ((instance.getInventarios() == null)
					|| (instance.getInventarios().isEmpty())) {
				return true;
			}
			FacesMessages.instance().add(
					this.sainv_messages.get("productoHome_error_delete1"));
			return false;
		} catch (Exception e) {
		}
		return true;
	}

	public boolean isImgSize() {
		return imgSize;
	}

	public void setImgSize(boolean imgSize) {
		this.imgSize = imgSize;
	}

	public void posSave() {
		List<Sucursal> sucursales = getEntityManager()
				.createQuery(
						"select s from Sucursal s where s.empresa = :empresa")
				.setParameter("empresa", instance.getEmpresa()).getResultList();

		for (Sucursal sucursal : sucursales) {
			Inventario inventario = new Inventario();
			inventario.setCantidadActual(Integer.valueOf(0));
			inventario.setProducto(instance);
			inventario.setSucursal(sucursal);
			this.inventarioHome.setInstance(inventario);
			this.inventarioHome.save();
		}
		getEntityManager().refresh(this.instance);
		getEntityManager().flush();
	}

	public void posModify() {
		if ((this.loginUser.getUser().getSucursal() != null)
				&& (this.codUbicacion != null)) {
			List<Inventario> lstInvs = getEntityManager()
					.createQuery(
							"SELECT i FROM Inventario i \tWHERE i.sucursal = :suc AND i.producto = :prd")
					.setParameter("suc", this.loginUser.getUser().getSucursal())
					.setParameter("prd", this.instance).getResultList();
			if ((lstInvs != null) && (!lstInvs.isEmpty())) {
				Inventario invSucursal = (Inventario) lstInvs.get(0);
				invSucursal.setCodUbicacion(this.codUbicacion);
				getEntityManager().merge(invSucursal);
				getEntityManager().flush();
			}
		}
	}

	public void posDelete() {
	}

	public Integer getProductoId() {
		return this.productoId;
	}

	public void setProductoId(Integer productoId) {
		this.productoId = productoId;
	}

	public List<Inventario> getProductos() {
		return this.productos;
	}

	public void setProductos(List<Inventario> productos) {
		this.productos = productos;
	}

	public String getReferencia() {
		return this.referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getCodigoBarras() {
		return this.codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public Empresa getEmpresaSeleccionada() {
		return this.empresaSeleccionada;
	}

	public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
		this.empresaSeleccionada = empresaSeleccionada;
	}

	public Sucursal getSucursalSeleccionada() {
		return this.sucursalSeleccionada;
	}

	public void setSucursalSeleccionada(Sucursal sucursalSeleccionada) {
		this.sucursalSeleccionada = sucursalSeleccionada;
	}

	public UbicacionPrd getCodUbicacion() {
		return this.codUbicacion;
	}

	public void setCodUbicacion(UbicacionPrd codUbicacion) {
		this.codUbicacion = codUbicacion;
	}

	public List<Inventario> getExistencias() {
		return this.existencias;
	}

	public void setExistencias(List<Inventario> existencias) {
		this.existencias = existencias;
	}

	public List<CodProducto> getCurrCodigos() {
		return this.currCodigos;
	}

	public void setCurrCodigos(List<CodProducto> currCodigos) {
		this.currCodigos = currCodigos;
	}

	public Inventario getSelectedInv() {
		return this.selectedInv;
	}

	public void setSelectedInv(Inventario selectedInv) {
		this.selectedInv = selectedInv;
	}

	public List<UbicacionPrd> getUbicacionesPrd() {
		return this.ubicacionesPrd;
	}

	public void setUbicacionesPrd(List<UbicacionPrd> ubicacionesPrd) {
		this.ubicacionesPrd = ubicacionesPrd;
	}

	public List<Producto> getPrdsExistencias() {
		return prdsExistencias;
	}

	public void setPrdsExistencias(List<Producto> prdsExistencias) {
		this.prdsExistencias = prdsExistencias;
	}

	public Sucursal getSucursalFlt() {
		return sucursalFlt;
	}

	public void setSucursalFlt(Sucursal sucursalFlt) {
		this.sucursalFlt = sucursalFlt;
	}

	public Integer getTotalInventario() {
		return totalInventario;
	}

	public void setTotalInventario(Integer totalInventario) {
		this.totalInventario = totalInventario;
	}

	public List<Sucursal> getSucList() {
		return sucList;
	}

	public void setSucList(List<Sucursal> sucList) {
		this.sucList = sucList;
	}

	public String getCodCatProd() {
		return codCatProd;
	}

	public void setCodCatProd(String codCatProd) {
		this.codCatProd = codCatProd;
	}

	public boolean isMosExists() {
		return mosExists;
	}

	public void setMosExists(boolean mosExists) {
		this.mosExists = mosExists;
	}

	public List<Producto> getPrdsExistenciasExl() {
		return prdsExistenciasExl;
	}

	public void setPrdsExistenciasExl(List<Producto> prdsExistenciasExl) {
		this.prdsExistenciasExl = prdsExistenciasExl;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public List<Inventario> getUbiBodega() {
		return ubiBodega;
	}

	public void setUbiBodega(List<Inventario> ubiBodega) {
		this.ubiBodega = ubiBodega;
	}

	public List<Inventario> getLsInventarios() {
		return lsInventarios;
	}

	public void setLsInventarios(List<Inventario> lsInventarios) {
		this.lsInventarios = lsInventarios;
	}

	public List<Sucursal> getListaSucursales() {
		return listaSucursales;
	}

	public void setListaSucursales(List<Sucursal> listaSucursales) {
		this.listaSucursales = listaSucursales;
	}

	public String getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(String tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public String getNombreSucursalSelec() {
		return nombreSucursalSelec;
	}

	public void setNombreSucursalSelec(String nombreSucursalSelec) {
		this.nombreSucursalSelec = nombreSucursalSelec;
	}

	
	
	
	
	
}