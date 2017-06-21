package com.sa.kubekit.action.sales;

import java.awt.Font;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.acct.AsientoContableHome;
import com.sa.kubekit.action.acct.ConceptoMovHome;
import com.sa.kubekit.action.acct.CuentaCobrarHome;
import com.sa.kubekit.action.inventory.ItemHome;
import com.sa.kubekit.action.inventory.MovimientoHome;
import com.sa.kubekit.action.inventory.ProductoList;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.AsientoContDet;
import com.sa.model.acct.AsientoContable;
import com.sa.model.acct.CondicionPago;
import com.sa.model.acct.CuentaCobrar;
import com.sa.model.acct.PagoCuentaPend;
import com.sa.model.crm.Cliente;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.medical.ClienteCorporativo;
import com.sa.model.sales.CodigoDescuentoCorp;
import com.sa.model.sales.DetDescuentoCorp;
import com.sa.model.sales.DetVentaProdServ;
import com.sa.model.sales.SolicitudImpresion;
import com.sa.model.sales.TasaTarjetaCred;
import com.sa.model.sales.VentaProdServ;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion.User;

@Name("ventaProdServHome")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings("unchecked")
public class VentaProdServHome extends KubeDAO<VentaProdServ> {

	private static final long serialVersionUID = 1L;
	private Integer vtaPrsId;
	private List<VentaProdServ> resultList = new ArrayList<VentaProdServ>();
	private List<VentaProdServ> resultList2 = new ArrayList<VentaProdServ>();
	private List<VentaProdServ> resultListDesc = new ArrayList<VentaProdServ>();
	private List<Object[]> prodServList = new ArrayList<Object[]>();
	private List<Object[]> prodServList2 = new ArrayList<Object[]>();
	private List<Object[]> montoDiario = new ArrayList<Object[]>();
	private Object mtDiario;
	private Object mt = new Object();
	private Double totalRestante;
	private Double totalRango;
	private Double totalDescuento;
	private Double totalCostos;
	private Double totalMonto;
	private boolean cuentaCobrar;
	private Double abonoCxc;
	private ClienteCorporativo cliCorp;
	private boolean llevaDescuento;
	private boolean cobroDesc;
	private Double totalConDesc;
	private TasaTarjetaCred formaPago;
	private Double totalCrgs;
	private float total;
	private Float desEfectivo;
	private Float desPorcentaje;
	private String estadoFilter;
	private Float numCero=0f;
	private List<CuentaCobrar> listaCuentasPendientes;
	private Float totalCxcP;
	private boolean cupon=false;
	private String cuponCod="";

	private CuentaCobrar infoCxc;

	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("vtacomb_created2")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("vtacomb_updated2")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("vtacomb_deleted2")));
	}

	@In(required = false, create = true)
	@Out
	private AsientoContableHome asientoContableHome;

	@In(required = false, create = true)
	@Out
	private ConceptoMovHome conceptoMovHome;

	@In(required = false, create = true)
	@Out
	private CuentaCobrarHome cuentaCobrarHome;

	@In(required = false, create = true)
	@Out
	private TasaTarjetaCredHome tasaTarjetaCredHome;

	@In
	private LoginUser loginUser;

	@In(required = false, create = true)
	@Out
	private MovimientoHome movimientoHome;

	@In(required = false, create = true)
	@Out
	private ItemHome itemHome;

	public void load(Integer vtaId) {
		setVtaPrsId(vtaId);
		load();

	}
	
	
	public void cargarCuentasPorCobrar()
	{
		listaCuentasPendientes = new ArrayList<CuentaCobrar>();
		
		try {
			listaCuentasPendientes=getEntityManager().createQuery("SELECT c FROM CuentaCobrar c where c.cliente.id=:idCliente and c.estado='ACT' ").setParameter("idCliente", instance.getCliente().getId()).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

	public void load() {
		try {
			setInstance(getEntityManager().find(VentaProdServ.class, vtaPrsId));
			// Calculamos total de costos
			totalCostos = new Double(0d);
			for (DetVentaProdServ tmpDt : instance.getDetVenta())
				totalCostos += tmpDt.getMonto() * tmpDt.getCantidad();

			totalCostos = new Long(Math.round(totalCostos * 100)) / 100.0;
			
			tasaTarjetaCredHome.getTasasCmb();
			
			/*TasaTarjetaCred pagoEfe = new TasaTarjetaCred();
			
			pagoEfe.setNombre("Efectivo");
			pagoEfe.setPorcentaje(0f);
			pagoEfe.setPagos((short) -1);
			pagoEfe.setId(Integer.MAX_VALUE - 1);
			
			formaPago = pagoEfe;*/ //comentado el 31/01/2017
			formaPago= tasaTarjetaCredHome.getResultList().get(0);
			
			System.out.println("Forma pago id "+formaPago.getId());
			System.out.println("pagos ********* "+formaPago.getPagos());
			
			totalCrgs = instance.getMonto().doubleValue();
			// Adicionamos la forma de pago en efectivo que no tiene cargos
			// adicionales
			//tasaTarjetaCredHome.getResultList().add(0, pagoEfe); comentaro el 31/01/2017 *******

			//if (instance.getEstado().equals("PDS")) { comentado el 06/02/2017
			
				// Calculamos el total con descuento
				
				if(instance.getCantidadDescuento()!=null)
				{
					totalConDesc = instance.getMonto().doubleValue();
					if (instance.getTipoDescuento().equals("M"))
						totalConDesc -= instance.getCantidadDescuento();
					else if (instance.getTipoDescuento().equals("P"))
						totalConDesc -= instance.getCantidadDescuento() / 100
								* totalConDesc;
				}
			//} else
				//instance.setTipoDescuento(null);
			
			cargarCuentasPorCobrar();

		} catch (Exception e) {
			e.printStackTrace();
			clearInstance();
			setInstance(new VentaProdServ());
		}
	}

	public void cargarInfoVentaCxc() {
		try {
			setInstance(getEntityManager().find(VentaProdServ.class, vtaPrsId));
			// Calculamos total de costos
			totalCostos = new Double(0d);
			for (DetVentaProdServ tmpDt : instance.getDetVenta())
				totalCostos += tmpDt.getMonto() * tmpDt.getCantidad();

			totalCostos = new Long(Math.round(totalCostos * 100)) / 100.0;
			tasaTarjetaCredHome.getTasasCmb();
			TasaTarjetaCred pagoEfe = new TasaTarjetaCred();
			pagoEfe.setNombre("Efectivo");
			pagoEfe.setPorcentaje(0f);
			pagoEfe.setPagos((short) -1);
			pagoEfe.setId(Integer.MAX_VALUE - 1);
			formaPago = pagoEfe;
			totalCrgs = instance.getMonto().doubleValue();
			// Adicionamos la forma de pago en efectivo que no tiene cargos
			// adicionales
			tasaTarjetaCredHome.getResultList().add(0, pagoEfe);

			infoCxc = new CuentaCobrar();
			infoCxc = (CuentaCobrar) getEntityManager()
					.createQuery(
							"Select c FROM CuentaCobrar c where c.id_venta=:idventa")
					.setParameter("idventa", vtaPrsId).getSingleResult();

			/*
			 * if(instance.getEstado().equals("PDS")) { //Calculamos el total
			 * con descuento totalConDesc = instance.getMonto().doubleValue();
			 * if(instance.getTipoDescuento().equals("M")) totalConDesc -=
			 * instance.getCantidadDescuento(); else
			 * if(instance.getTipoDescuento().equals("P")) totalConDesc -=
			 * instance.getCantidadDescuento()/100*totalConDesc; } else
			 * instance.setTipoDescuento(null);
			 */

		} catch (Exception e) {
			e.printStackTrace();
			clearInstance();
			setInstance(new VentaProdServ());
		}
	}

	// Genera y Descarga el archivo Excel
	public void exportarExcel() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader(
				"Content-Disposition",
				"attachment;filename=cobros_aprobados-"
						+ sdf.format(cal.getTime()) + ".xls");

		try {
			
			List<Sucursal> subSucFlt = getEntityManager()
					.createQuery(
							"SELECT s FROM Sucursal s WHERE (s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc) ")
					.setParameter("suc", loginUser.getUser().getSucursal())
					.setParameter(
							"otraSuc",
							loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
									.getUser().getSucursal() : loginUser.getUser()
									.getSucursal().getSucursalSuperior())
					.getResultList();

			if (subSucFlt == null || subSucFlt.size() <= 0)
				subSucFlt = new ArrayList<Sucursal>();

			subSucFlt.add(loginUser.getUser().getSucursal());
			subSucFlt
					.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
							.getUser().getSucursal() : loginUser.getUser()
							.getSucursal().getSucursalSuperior());

			String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
			if (getFechaPFlt1() != null && getFechaPFlt2() != null) {
				setFechaPFlt1(truncDate(getFechaPFlt1(), false));
				setFechaPFlt2(truncDate(getFechaPFlt2(), true));
				fltFch = " AND ps.fechaVenta BETWEEN :fch1 AND :fch2 ";
			}
			
			
			prodServList = getEntityManager()
					.createQuery(
							"SELECT det.codClasifVta, det.codExacto, det.detalle, SUM(det.cantidad) AS cantidad, SUM(det.monto*det.cantidad) AS  monto FROM DetVentaProdServ det WHERE det.venta IN "
									+ "(SELECT ps.id FROM VentaProdServ ps WHERE (ps.sucursal = :suc or ps.sucursal IN (:subSuc) ) AND ps.estado <> 'PEN' and ps.estado<>'PDS' "
									+ fltFch
									+ ") GROUP BY codClasifVta, det.codExacto, det.detalle ORDER BY det.codClasifVta, det.codExacto")
					.setParameter("suc", loginUser.getUser().getSucursal())
					.setParameter("fch1", getFechaPFlt1())
					.setParameter("fch2", getFechaPFlt2())
					.setParameter(
							"subSuc",
							subSucFlt == null ? new ArrayList<Sucursal>()
									: subSucFlt).getResultList();
			
			HSSFWorkbook libro = new HSSFWorkbook();
			HSSFSheet hoja = libro.createSheet();
			CreationHelper ch = libro.getCreationHelper();

			HSSFRow fila;
			HSSFCell celda;

			// definicion de estilos para las celdas
			HSSFFont headfont = libro.createFont(), headfont2 = libro
					.createFont(), headfont3 = libro.createFont();
			headfont.setFontName("Arial");
			headfont.setFontHeightInPoints((short) 8);
			headfont2.setFontName("Arial");
			headfont2.setFontHeightInPoints((short) 10);
			headfont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			headfont3.setFontName("Arial");
			headfont3.setFontHeightInPoints((short) 7);
			headfont3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFCellStyle stAling = libro.createCellStyle(), stDate = libro
					.createCellStyle(), stAlingRight = libro.createCellStyle(), stTitles = libro
					.createCellStyle(), stTotals = libro.createCellStyle(), stList = libro
					.createCellStyle(), stFinal = libro.createCellStyle(), stPorcent = libro
					.createCellStyle();

			// Para Formatos de dolar y porcentaje
			DataFormat estFormato = libro.createDataFormat();

			stAling.setFont(headfont);
			stAling.setWrapText(true);
			stAling.setAlignment(stAling.ALIGN_RIGHT);
			stAling.setDataFormat(estFormato.getFormat("$#,#0.00"));

			stDate.setDataFormat(ch.createDataFormat().getFormat("dd/mm/yy"));
			stDate.setFont(headfont2);

			stAlingRight.setFont(headfont);
			stAlingRight.setWrapText(true);
			stAlingRight.setAlignment(stAlingRight.ALIGN_RIGHT);
			stAlingRight.setDataFormat(estFormato.getFormat("$#,#0.00"));

			stTitles.setVerticalAlignment(stTitles.VERTICAL_CENTER);
			stTitles.setAlignment(stTitles.ALIGN_CENTER);
			stTitles.setFont(headfont2);

			stTotals.setAlignment(stTitles.ALIGN_RIGHT);
			stTotals.setFont(headfont2);
			stTotals.setDataFormat(estFormato.getFormat("$#,#0.00"));

			stList.setAlignment(stList.ALIGN_CENTER);
			stList.setVerticalAlignment(stList.VERTICAL_TOP);
			stList.setWrapText(true);
			stList.setFont(headfont3);

			stFinal.setVerticalAlignment(stTitles.VERTICAL_CENTER);
			stFinal.setAlignment(stTitles.ALIGN_RIGHT);
			stFinal.setFont(headfont2);
			stFinal.setDataFormat(estFormato.getFormat("$#,#0.00"));

			// Estilo para porcentaje
			stPorcent.setFont(headfont);
			stPorcent.setWrapText(true);
			stPorcent.setAlignment(stAlingRight.ALIGN_RIGHT);
			stPorcent.setDataFormat(estFormato.getFormat("#0.#00%"));

			// Filtrado para obtener la fecha acorde al formato
			fila = hoja.createRow(0);

			celda = fila.createCell(0);
			celda.setCellValue("RANGO DE FECHAS: ");
			celda.setCellStyle(stTitles);
			celda = fila.createCell(1);
			celda.setCellValue(getFechaPFlt1());
			celda.setCellStyle(stDate);
			celda = fila.createCell(2);
			celda.setCellValue(getFechaPFlt2());
			celda.setCellStyle(stDate);

			int cont = 3;

			// crea la lista hacia la derecha de los CODIGOS de srv, cmb y prd
			// efectuados en el rango.
			for (Object[] item : prodServList) {
				celda = fila.createCell(cont);
				celda.setCellValue((String) item[1]);
				celda.setCellStyle(stList);
				cont++;
			}
			// agregando la lista de productos, srv, combos.
			fila = hoja.createRow(1);
			celda = fila.createCell(0);
			celda.setCellValue("CLIENTE");
			celda.setCellStyle(stTitles);
			celda = fila.createCell(1);
			celda.setCellValue("TIPO DE VENTA");
			celda.setCellStyle(stTitles);
			celda = fila.createCell(2);
			celda.setCellValue("SUCURSAL");
			celda.setCellStyle(stTitles);

			int cli = 2, contCelda, totalCantidad = 0;
			cont = 3;
			// crea la lista hacia la derecha de srv, cmb y prd efectuados en el
			// rango.
			for (Object[] item : prodServList) {
				celda = fila.createCell(cont);
				hoja.setColumnWidth(cont, 5000);
				celda.setCellValue((String) item[2]);
				celda.setCellStyle(stList);
				cont++;
			}

			celda = fila.createCell(cont);
			celda.setCellValue("TOTAL PRODUCTOS");
			celda.setCellStyle(stTitles);
			celda = fila.createCell(cont + 1);
			celda.setCellValue("MONTO");
			celda.setCellStyle(stTitles);
			celda = fila.createCell(cont + 2);
			celda.setCellValue("DESCUENTO");
			celda.setCellStyle(stTitles);
			celda = fila.createCell(cont + 3);
			celda.setCellValue("DESC %");
			celda.setCellStyle(stTitles);

			hoja.getRow(1).setHeight((short) 650);

			// crea la lista de clientes
			for (VentaProdServ rl : resultList) {// recorre cada venta
				fila = hoja.createRow(cli);
				fila.createCell(0).setCellValue(
						rl.getCliente().getNombreCompleto());
				if (rl.getTipoVenta().equals("CMB")) {
					fila.createCell(1).setCellValue("Combo");
				} else if (rl.getTipoVenta().equals("CST")) {
					fila.createCell(1).setCellValue("Servicio Medico");
				} else if (rl.getTipoVenta().equals("TLL")) {
					fila.createCell(1).setCellValue("Servicio de Taller");
				} else if (rl.getTipoVenta().equals("ITM")) {
					fila.createCell(1).setCellValue("Producto");
				} else if (rl.getTipoVenta().equals("EXA")) {
					fila.createCell(1).setCellValue("Examen");
				}
				fila.createCell(2).setCellValue(rl.getSucursal().getNombre());

				for (DetVentaProdServ dt : rl.getDetVenta()) { // recorre cada
																// detalle desde
																// la celda 4 en
																// excel
					contCelda = 3;
					for (Object[] item : prodServList) { // recorre los items
						if (dt.getDetalle().equals(item[2])) {
							celda = fila.createCell(contCelda);
							celda.setCellValue(Math.rint(dt.getMonto() * 100) / 100);
							celda.setCellStyle(stAling);
						}
						contCelda++;
					}
				}
				// totales monto
				celda = fila.createCell(cont + 1);
				celda.setCellValue(Math.rint(rl.getRestante() * 100) / 100); // Formatos
																				// faltantes
				celda.setCellStyle(stAlingRight);

				double montoDescuento = 0;
				// totales descuento
				if (rl.getCantidadDescuento() != null
						&& rl.getTipoDescuento() != null)
					if (rl.getTipoDescuento().equals("M")) {
						celda = fila.createCell(cont + 2);
						celda.setCellValue(Double.parseDouble(rl
								.getCantidadDescuento().toString()));

						celda.setCellStyle(stAlingRight);
					} else {

						montoDescuento = (rl.getMonto())
								* (rl.getCantidadDescuento() / 100);
						celda = fila.createCell(cont + 2);
						celda.setCellValue(montoDescuento);
						celda.setCellStyle(stAlingRight);

						celda = fila.createCell(cont + 3);
						celda.setCellValue((Double.parseDouble(rl
								.getCantidadDescuento().toString())) / 100);
						celda.setCellStyle(stPorcent);

					}
				cli++;
			}

			fila = hoja.createRow(cli);
			celda = fila.createCell(2);
			celda.setCellValue("TOTALES");
			celda.setCellStyle(stTitles);
			cont = 3;
			for (Object[] item : prodServList) { // crea la lista hacia la
													// derecha de srv, cmb y prd
													// efectuados en el rango.
				celda = fila.createCell(cont);
				celda.setCellValue(Math.rint((Double) item[4] * 100) / 100);
				celda.setCellStyle(stTotals);
				cont++;
			}
			celda = fila.createCell(cont);
			celda.setCellValue(Math.rint(totalRango * 100) / 100);
			celda.setCellStyle(stFinal);
			celda = fila.createCell(cont + 1);
			celda.setCellValue(Math.rint(totalRestante * 100) / 100);
			celda.setCellStyle(stFinal);
			celda = fila.createCell(cont + 2);
			celda.setCellValue(Math.rint(totalDescuento * 100) / 100);
			celda.setCellStyle(stFinal);

			fila = hoja.createRow(cli + 1);
			celda = fila.createCell(2);
			celda.setCellValue("CANTIDAD");
			celda.setCellStyle(stTitles);
			cont = 3;

			for (Object[] item : prodServList) { // crea la lista de cantidades
													// hacia la derecha de srv,
													// cmb y prd efectuados en
													// el rango.
				celda = fila.createCell(cont);
				totalCantidad += Integer.parseInt(item[3].toString());
				celda.setCellValue(Integer.parseInt(item[3].toString()));
				// celda.setCellStyle(stTotals);
				cont++;
			}

			celda = fila.createCell(cont);
			celda.setCellValue(totalCantidad);
			// celda.setCellStyle(stTotals);

			for (int i = 0; i < 3; i++) {
				hoja.autoSizeColumn(i);
			}
			for (int i = cont; i <= cont + 2; i++) {
				hoja.autoSizeColumn(i);
			}

			hoja.createFreezePane(3, 0);

			OutputStream os = response.getOutputStream();
			libro.write(os);
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		FacesContext.getCurrentInstance().responseComplete();
	}

	public void getVentasGeneral() {

		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery(
						"SELECT s FROM Sucursal s WHERE (s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc) ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter(
						"otraSuc",
						loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
								.getUser().getSucursal() : loginUser.getUser()
								.getSucursal().getSucursalSuperior())
				.getResultList();

		if (subSucFlt == null || subSucFlt.size() <= 0)
			subSucFlt = new ArrayList<Sucursal>();

		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt
				.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
						.getUser().getSucursal() : loginUser.getUser()
						.getSucursal().getSucursalSuperior());

		String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
		if (getFechaPFlt1() != null && getFechaPFlt2() != null) {
			setFechaPFlt1(truncDate(getFechaPFlt1(), false));
			setFechaPFlt2(truncDate(getFechaPFlt2(), true));
			fltFch = " AND v.fechaVenta BETWEEN :fch1 AND :fch2 ";
		}

		resultList = getEntityManager()
				.createQuery(
						"SELECT v FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) "
								+ fltFch + " ORDER BY v.fechaVenta DESC ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();
		System.out.println("sucursal: "
				+ loginUser.getUser().getSucursal().getNombre());

		// cambio, todos los resultados de estados se desean en cobros.
		/*
		 * resultList = getEntityManager() .createQuery(
		 * "SELECT v FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) "
		 * + fltFch + " AND v.estado <> 'PDS' ORDER BY v.fechaVenta DESC ")
		 * .setParameter("suc", loginUser.getUser().getSucursal())
		 * .setParameter("fch1", getFechaPFlt1()) .setParameter("fch2",
		 * getFechaPFlt2()) .setParameter("subSuc", subSucFlt==null?new
		 * ArrayList<Sucursal>():subSucFlt) .getResultList();
		 */

	}

	public void getVentasAprovadas() {

		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery(
						"SELECT s FROM Sucursal s WHERE (s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc) ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter(
						"otraSuc",
						loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
								.getUser().getSucursal() : loginUser.getUser()
								.getSucursal().getSucursalSuperior())
				.getResultList();

		if (subSucFlt == null || subSucFlt.size() <= 0)
			subSucFlt = new ArrayList<Sucursal>();

		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt
				.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
						.getUser().getSucursal() : loginUser.getUser()
						.getSucursal().getSucursalSuperior());

		String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
		if (getFechaPFlt1() != null && getFechaPFlt2() != null) {
			setFechaPFlt1(truncDate(getFechaPFlt1(), false));
			setFechaPFlt2(truncDate(getFechaPFlt2(), true));
			fltFch = " AND v.fechaVenta BETWEEN :fch1 AND :fch2 ";
		}
		// filtrado por la sucursal del usuario login
		resultList = getEntityManager()
				.createQuery(
						"SELECT v FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) AND  v.estado = 'APR'"
								+ fltFch + " ORDER BY v.fechaVenta DESC ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();

		resultList2 = getEntityManager()
				.createQuery(
						"SELECT v FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) AND  v.estado = 'ABN'"
								+ fltFch + " ORDER BY v.fechaVenta DESC ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();

		List<VentaProdServ> resultList3 = getEntityManager()
				.createQuery(
						"SELECT v FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) AND  v.estado = 'ABF'"
								+ fltFch + " ORDER BY v.fechaVenta DESC ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();

		for (VentaProdServ res2 : resultList2) {
			resultList.add(res2);
		}

		for (VentaProdServ res3 : resultList3) {
			resultList.add(res3);
		}

		// Asignando los pagos de las ventas pendientes
		// List<CuentaCobrar> listaCuentas =
		// getEntityManager().createQuery("SELECT c FROM CuentaCobrar c ")

		// suma la cantidad de montos por ventas aprobadas
		totalMonto = (Double) getEntityManager()
				.createQuery(
						"SELECT SUM(v.monto) FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) AND v.estado = 'APR' "
								+ fltFch + "")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getSingleResult();
		if (totalMonto == null)
			totalMonto = 0.0;
		// suma la cantidad de descuentos efectuados en el rango
		totalDescuento = (Double) getEntityManager()
				.createQuery(
						"SELECT SUM(CASE "
								+ "WHEN v.tipoDescuento='P' THEN (v.cantidadDescuento/100*v.monto)  "
								+ "WHEN v.tipoDescuento='M' THEN (v.cantidadDescuento) "
								+ "WHEN v.tipoDescuento='P' AND v.totalDescuentoCorp!=null THEN (v.cantidadDescuento/100*v.monto+v.totalDescuentoCorp)  "
								+ "WHEN v.tipoDescuento='M' AND v.totalDescuentoCorp!=null THEN (v.cantidadDescuento+v.totalDescuentoCorp)"
								+ "WHEN v.tipoDescuento=null AND v.totalDescuentoCorp!=null THEN (v.totalDescuentoCorp)"
								+ "END) "
								+ "FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) AND v.estado = 'APR' "
								+ fltFch + "")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getSingleResult();
		if (totalDescuento == null)
			totalDescuento = 0.0;
		// suma la cantidad de productos y servicios vendidos sin descuento
		totalRango = (Double) getEntityManager()
				.createQuery(
						"SELECT SUM(det.monto*det.cantidad) FROM DetVentaProdServ det WHERE det.venta IN "
								+ "(SELECT v.id FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) AND v.estado = 'APR' "
								+ fltFch + ")")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getSingleResult();
		if (totalRango == null)
			totalRango = 0.0;

		totalRestante = totalMonto - totalDescuento;

	}

	public void getServProdList() {
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery(
						"SELECT s FROM Sucursal s WHERE (s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc) ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter(
						"otraSuc",
						loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
								.getUser().getSucursal() : loginUser.getUser()
								.getSucursal().getSucursalSuperior())
				.getResultList();

		if (subSucFlt == null || subSucFlt.size() <= 0)
			subSucFlt = new ArrayList<Sucursal>();

		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt
				.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
						.getUser().getSucursal() : loginUser.getUser()
						.getSucursal().getSucursalSuperior());

		String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
		if (getFechaPFlt1() != null && getFechaPFlt2() != null) {
			setFechaPFlt1(truncDate(getFechaPFlt1(), false));
			setFechaPFlt2(truncDate(getFechaPFlt2(), true));
			fltFch = " AND ps.fechaVenta BETWEEN :fch1 AND :fch2 ";
		}
		
		
		prodServList = getEntityManager()
				.createQuery(
						"SELECT det.codClasifVta, det.detalle, SUM(det.cantidad) AS cantidad, SUM(CASE WHEN det.venta.monto>0 THEN (det.monto*det.cantidad) END) AS  monto FROM DetVentaProdServ det WHERE det.venta IN "
								+ "(SELECT ps.id FROM VentaProdServ ps WHERE (ps.sucursal = :suc or ps.sucursal IN (:subSuc) ) AND ps.estado <> 'PEN' AND ps.estado<>'PDS' AND ps.estado<>'ANU'"
								+ fltFch
								+ ")  GROUP BY det.codClasifVta,det.detalle ORDER BY det.codClasifVta,det.detalle")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();
		

		/*prodServList = getEntityManager()
				.createQuery(
						"SELECT det.codClasifVta, det.codExacto, det.detalle, SUM(det.cantidad) AS cantidad, SUM(det.monto*det.cantidad) AS  monto FROM DetVentaProdServ det WHERE det.venta IN "
								+ "(SELECT ps.id FROM VentaProdServ ps WHERE (ps.sucursal = :suc or ps.sucursal IN (:subSuc) ) AND ps.estado = 'APR' "
								+ fltFch
								+ ") GROUP BY codClasifVta, det.codExacto, det.detalle ORDER BY det.codClasifVta, det.codExacto")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();

		prodServList2 = getEntityManager()
				.createQuery(
						"SELECT det.codClasifVta, det.codExacto, det.detalle, SUM(det.cantidad) AS cantidad, SUM(det.monto*0) AS  monto FROM DetVentaProdServ det WHERE det.venta IN "
								+ "(SELECT ps.id FROM VentaProdServ ps WHERE (ps.sucursal = :suc or ps.sucursal IN (:subSuc) ) AND ps.estado = 'ABN' "
								+ fltFch
								+ ") GROUP BY codClasifVta, det.codExacto, det.detalle ORDER BY det.codClasifVta, det.codExacto")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();

		List<Object[]> prodServList3 = getEntityManager()
				.createQuery(
						"SELECT det.codClasifVta, det.codExacto, det.detalle, SUM(det.cantidad) AS cantidad, SUM(det.monto*0) AS  monto FROM DetVentaProdServ det WHERE det.venta IN "
								+ "(SELECT ps.id FROM VentaProdServ ps WHERE (ps.sucursal = :suc or ps.sucursal IN (:subSuc) ) AND ps.estado = 'ABF' "
								+ fltFch
								+ ") GROUP BY codClasifVta, det.codExacto, det.detalle ORDER BY det.codClasifVta, det.codExacto")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();

		for (Object[] bj : prodServList2) {

			prodServList.add(bj);
		}

		for (Object[] bj2 : prodServList3) {

			prodServList.add(bj2);
		}*/

	}
	
	
	
	public void revertirVenta()
	{
		
		
		try {
			
			verificarCuentasPorCobrarExistentes();
			
			
			instance.setEstado("PEN");
			instance.setCantidadDescuento(null);
			instance.setTipoDescuento(null);
			instance.setUsrDescuento(null);
			
			recuperarValorVenta();
			
			System.out.println("Estado actual  venta ****** "+instance.getEstado());
			modify();
			
			System.out.println("Estado despues  venta ****** "+instance.getEstado());
			
			FacesMessages.instance().add(Severity.INFO,"Venta editada exitosamente");
			
		} catch (Exception e) {
			
			FacesMessages.instance().add(Severity.ERROR,"La venta no pudo ser editada");
			e.printStackTrace();
		}
	
		
	}
	
	
	public void anularVenta()
	{
		
		try {
			
			verificarCuentasPorCobrarExistentes();
			instance.setEstado("ANU");
			modify();
			FacesMessages.instance().add(Severity.INFO,"Venta editada exitosamente");
			
		} catch (Exception e) {
			
			FacesMessages.instance().add(Severity.ERROR,"La venta no pudo ser editada");
			e.printStackTrace();
		}
			
		
	}
	
	
	public void verificarCuentasPorCobrarExistentes() throws Exception
	{
		List<CuentaCobrar> cxcVenta = new ArrayList<CuentaCobrar>();
		cxcVenta = getEntityManager().createQuery("SELECT c FROM CuentaCobrar c where c.id_venta=:idVenta")
				.setParameter("idVenta", instance.getId()).getResultList();
		
		if(cxcVenta.size()>0)
		{
			
			List<PagoCuentaPend> pagosCxc = new ArrayList<PagoCuentaPend>();
			pagosCxc = getEntityManager().createQuery("SELECT p FROM PagoCuentaPend p where p.cuentaCobrar.id=:idCuenta").setParameter("idCuenta", cxcVenta.get(0).getId()).getResultList();
			
			if(pagosCxc.size()>0)
			{
				for(PagoCuentaPend pago:pagosCxc)
				{
					getEntityManager().remove(pago);
				}
				
				
				
				List<VentaProdServ> ventasPagos = new ArrayList<VentaProdServ>();
				ventasPagos = getEntityManager().createQuery("SELECT v FROM VentaProdServ v where v.codTipoVenta=:codigo and v.id!=:idActual")
						.setParameter("codigo", instance.getCodTipoVenta())
						.setParameter("idActual", instance.getId())
						.getResultList();
				
				if(ventasPagos.size()>0)
				{
					
					for(VentaProdServ venta: ventasPagos)
					{
						for(DetVentaProdServ det: venta.getDetVenta())
						{
							getEntityManager().remove(det);
						}
						
						getEntityManager().remove(venta);
					}
				}
				
				
				FacesMessages.instance().add(Severity.INFO,"Se eliminaron los pagos de la cuenta por cobrar");
			}
			
			
			
			FacesMessages.instance().add(Severity.INFO,"Se eliminaron las cuentas por cobrar");
			getEntityManager().remove(cxcVenta.get(0));
		}
	}
	
	public void recuperarValorVenta()
	{
		Float total=0f;
		for(DetVentaProdServ det:instance.getDetVenta())
		{
			total+=(det.getMonto()*det.getCantidad());
			det.setDescuentoCorp(null);
			getEntityManager().merge(det);
		}
		
		instance.setMonto(total);
		instance.setTotalDescuentoCorp(null);
	}
	

	// Devuelve el total de cobros pendientes
	public float getTotalPend() {
		setTotal(0.0f);
		try {
			resultList = getEntityManager()
					.createQuery(
							"SELECT v FROM VentaProdServ v WHERE v.cliente = :cli AND v.estado='PEN'")
					.setParameter("cli", instance.getCliente()).getResultList();
			if (resultList.size() > 0) {
				for (VentaProdServ tmpCobro : resultList) {
					total = total + tmpCobro.getMonto();
					System.out.println("TOTAL cobros pend: " + total);
				}
			}
			return total;
		} catch (Exception e) {
			e.printStackTrace();
			return 0.0f;
		}
	}
	
	public float getTotalCxcPend()
	{
		totalCxcP=0f;
		
		
		for(CuentaCobrar cxc: listaCuentasPendientes)
		{
			totalCxcP+=cxc.getRemanente();
		}
		
		return totalCxcP;
	}

	public void getVentasPendDesc() {
		// 4/18/2016 se pidió que las ventas con descuento se muestren por
		// Usuario que autoriza y no por sucursal.
		/*
		 * List<Sucursal> subSucFlt = getEntityManager() .createQuery(
		 * "SELECT s FROM Sucursal s WHERE (s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc) "
		 * ) .setParameter("suc", loginUser.getUser().getSucursal())
		 * .setParameter("otraSuc",
		 * loginUser.getUser().getSucursal().getSucursalSuperior() ==
		 * null?loginUser
		 * .getUser().getSucursal():loginUser.getUser().getSucursal
		 * ().getSucursalSuperior()) .getResultList();
		 * 
		 * if(subSucFlt == null || subSucFlt.size() <= 0) subSucFlt = new
		 * ArrayList<Sucursal>();
		 * 
		 * subSucFlt.add(loginUser.getUser().getSucursal());
		 * subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior()
		 * ==
		 * null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal
		 * ().getSucursalSuperior());
		 */
		List<Sucursal> subSucFlt = getEntityManager()
				.createQuery(
						"SELECT s FROM Sucursal s WHERE (s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc) ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter(
						"otraSuc",
						loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
								.getUser().getSucursal() : loginUser.getUser()
								.getSucursal().getSucursalSuperior())
				.getResultList();

		if (subSucFlt == null || subSucFlt.size() <= 0)
			subSucFlt = new ArrayList<Sucursal>();

		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt
				.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
						.getUser().getSucursal() : loginUser.getUser()
						.getSucursal().getSucursalSuperior());

		String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
		if (getFechaPFlt1() != null && getFechaPFlt2() != null) {
			setFechaPFlt1(truncDate(getFechaPFlt1(), false));
			setFechaPFlt2(truncDate(getFechaPFlt2(), true));
			fltFch = " AND v.fechaVenta BETWEEN :fch1 AND :fch2 ";
		}

		resultListDesc = getEntityManager()//(v.sucursal = :suc or v.sucursal IN (:subSuc) ) se quito de la consulta el 07/06/2017
				.createQuery(
						"SELECT v FROM VentaProdServ v WHERE  v.estado = 'PDS' "
								+ fltFch + " ORDER BY v.fechaVenta DESC ")
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
				.getResultList();//.setParameter(
		/*"subSuc",
		subSucFlt == null ? new ArrayList<Sucursal>()
				: subSucFlt)*/
		//.setParameter("suc", loginUser.getUser().getSucursal())
	}

	public void fltVtasRep() {
		//
		totalCostos = 0d;
		// Vamos filtrando de manera manual las fechas y algun otro filtro
		// futuro
		for (VentaProdServ tmpVta : resultList)
			totalCostos += tmpVta.getMonto();

	}

	public void setDA(boolean a, boolean b) {
		setDiaActual();
		if (a && b) {
			getVentasGeneral();
		} else if (a && !b) {
			getVentasPendDesc();
		} else if (!a && !b) {
			getVentasAprovadas();
			getServProdList();
		}
	}

	public void setR7(boolean a) {
		if (a) {
			setRangoUlt7dias();
			getVentasGeneral();
		} else {
			setRangoUlt7dias();
			getVentasPendDesc();
		}
	}

	public void setR14(boolean a) {
		if (a) {
			setRangoUlt14dias();
			getVentasGeneral();
		} else {
			setRangoUlt14dias();
			getVentasPendDesc();
		}
	}

	public void setR30(boolean a) {
		if (a) {
			setRangoUlt30dias();
			getVentasGeneral();
		} else {
			setRangoUlt30dias();
			getVentasPendDesc();
		}
	}

	public void setDiaAtrasAdelante(boolean a) {
		if (a) {
			setDiaAtras();
		} else {
			setDiaAdelante();
		}
		getVentasAprovadas();
		getServProdList();
	}

	public void setRangoVentasAprovadas() {
		getVentasAprovadas();
		getServProdList();
	}

	public void getVentasComboList() {
		resultList = getEntityManager()
				.createQuery(
						"SELECT v FROM VentaProdServ v "
								+ "	WHERE v.empresa = :emp AND v.estado <> 'ANU' ORDER BY v.fechaVenta DESC")
				.setParameter("emp",
						loginUser.getUser().getSucursal().getEmpresa())
				.getResultList();
	}

	public void setMontoCompletoCxc() {
		Double montoFact = new Double(0);
		montoFact = new Long(Math.round(instance.getMonto() * 100)) / 100.0;
		setAbonoCxc(montoFact);
	}

	public void calcCargosTarjeta() {
		//if (formaPago != null && formaPago.getPagos() != -1)//cambiar el -1
		if (formaPago != null && formaPago.getPorcentaje()>0)
			instance.setMonto(totalCrgs.floatValue() + formaPago.getPorcentaje()/ 100 * instance.getMonto());//instance.setMonto(instance.getMonto() + formaPago.getPorcentaje()/ 100 * instance.getMonto());
		else
			instance.setMonto(totalCrgs.floatValue());

	}

	public boolean anularVta() {
		// Revertimos las existencias de inventario
		instance.setEstado("ANL");
		if (instance.getMovimiento() != null) { // Revertimos el movimiento de
												// inventario
			Movimiento mov = new Movimiento();
			mov.setFecha(new Date());
			mov.setSucursal(loginUser.getUser().getSucursal());
			mov.setTipoMovimiento("E");
			mov.setUsuario(loginUser.getUser());
			mov.setRazon("V");
			mov.setObservacion("Reversion de la venta generada automaticamente por su anulacion, detalle:"
					+ instance.getDetalle());
			movimientoHome.select(mov);
			// movimientoHome.setItemsAgregados(instance.getMovimiento().getItems());
			movimientoHome.save();
			getEntityManager().refresh(movimientoHome.getInstance());
			instance.setMovimiento(movimientoHome.getInstance());

			for (Item item : instance.getMovimiento().getItems()) {
				Item nwItm = new Item();
				nwItm.setItemId(new ItemId());
				nwItm.getItemId().setInventarioId(item.getInventario().getId());
				nwItm.getItemId().setMovimientoId(
						movimientoHome.getInstance().getId());
				nwItm.setCantidad(item.getCantidad());
				/*
				 * if(item.getCodProducto() != null)
				 * nwItm.setCodProducto(item.getCodProducto());
				 */
				nwItm.setInventario(item.getInventario());
				nwItm.setMovimiento(mov);

				itemHome.setInstance(nwItm);
				itemHome.save();
				// Guardamos los codigos si es que tienen codigos
				/*
				 * if(item.getInventario().getProducto().getCategoria().
				 * isTieneNumSerie() ||
				 * item.getInventario().getProducto().getCategoria
				 * ().isTieneNumLote()) { ArrayList<CodProducto> codsProds =
				 * lstCodsProductos
				 * .get(item.getInventario().getProducto().getReferencia());
				 * for(CodProducto tmpCd: codsProds) { if(tmpCd.isTransferido())
				 * { tmpCd.setEstado("USD"); getEntityManager().merge(tmpCd); }
				 * } }
				 */

			}
		}

		return true;
	}
	
	// Creado el 25/01/2017
	public boolean aprobarDescuento()
	{
		
		try
		{
			instance.setEstado("APL");
			getEntityManager().merge(instance);
			getEntityManager().flush();
		}
		catch (Exception e) {
			return false;
		}
		
		
		
		return true;
	}
	

	public boolean aprobarVta() {
		boolean res = false;
		
		
		//Aplicar los descuentos a proveedor si los necesitan
				if(isCuentaCobrar())//Ya tuvo que haberse seleccionado el cliente corporativo
				{
					System.out.println("Entro  a verificar descuentos corp");
					if(instance.getTotalDescuentoCorp()!=null && instance.getTotalDescuentoCorp()!=0f)
					{
						System.out.println("Total lleno ********************");
						instance.setMonto(instance.getMonto()-instance.getTotalDescuentoCorp());
					}
					else 
					{
						System.out.println("Total vacio *****************");
					}
					
				}

		// Problablemente la variable de monto, mnt hay q declararla aqui
		Double mnt = (new Long(Math.round(instance.getMonto() * 100)) / 100.0);
		Double montoOriginal = (new Long(Math.round(instance.getMonto() * 100)) / 100.0);
		if (!llevaDescuento) {
			// Generamos el asiento contable  // && getAbonoCxc() != null && getAbonoCxc() > 0
			if (!isCuentaCobrar()
					|| isCuentaCobrar()) {

				if ((new Long(Math.round(getAbonoCxc() * 100)) / 100.0)
						- (new Long(Math.round(instance.getMonto() * 100)) / 100.0) > 0) {
					FacesMessages.instance().add(
							sainv_messages.get("vtagnr_err_abomuch"));
					return false;
				}

				// Double mnt = instance.getMonto().doubleValue();
				Float abn = 0f;
				abn = getAbonoCxc().floatValue();

				Float remt = montoOriginal.floatValue() - abn;

				System.out.println(" mnt " + mnt);

				if (getAbonoCxc() != null && getAbonoCxc() > 0)
					mnt = (new Long(Math.round(instance.getMonto() * 100)) / 100.0)
							- (new Long(Math.round(getAbonoCxc() * 100)) / 100.0);

				if (instance.getEstado().equals("PDS"))
					mnt = totalConDesc;

				if (isCuentaCobrar()) // Generamos la cuenta por cobrar
				{
					// Double mnt = instance.getMonto().doubleValue();
					CuentaCobrar cxc = new CuentaCobrar();
					cxc.setCliente(instance.getCliente());
					cxc.setComentario(instance.getDetalle());
					if (cliCorp != null) {
						cxc.setCliCorp(cliCorp);
						instance.setCliCorp(cliCorp);
					}
					cxc.setComprobante("VTA" + instance.getTipoVenta()
							+ instance.getId().toString());
					conceptoMovHome.setConcepto("VENTA PENDIENTE DE COBRAR");
					conceptoMovHome.guardarConcepto();
					cxc.setConcepto(conceptoMovHome.getInstance());
					CondicionPago cndPg = (CondicionPago) getEntityManager()
							.createQuery(
									"SELECT c FROM CondicionPago c WHERE UPPER(c.nombre) = UPPER(:nom)")
							.setParameter("nom", "EFECTIVO").getSingleResult();
					cxc.setCondicionPago(cndPg);
					cxc.setDiasPlazo(15);
					cxc.setFechaIngreso(new Date());

					cxc.setId_venta(instance.getId());
					/*
					 * if(getAbonoCxc() != null && getAbonoCxc() > 0) {
					 */// cxc.setMonto(getAbonoCxc().floatValue()); el q se
						// estaba guardando
					cxc.setMonto(montoOriginal.floatValue());
					// No esta asignado remanente, hay que calcularlo
					cxc.setRemanente(remt);
					System.out
							.println("Realizo el ingreso a cuenta por pagar: ");
					System.out.println("Monto original " + instance.getMonto());
					System.out.println("Remanente " + remt);
					System.out.println("Variable Total Desc " + totalConDesc);
					System.out.println("Remantente dentro del objeto "
							+ cxc.getRemanente());

					/*
					 * }
					 * 
					 * else { cxc.setMonto(instance.getMonto().floatValue()); }
					 */

					cxc.setSucursal(instance.getSucursal());
					cuentaCobrarHome.select(cxc);
					cuentaCobrarHome.save();

					/*
					 * cxc.setSucursal(instance.getSucursal());
					 * cuentaCobrarHome.select(cxc); cuentaCobrarHome.save();
					 */

					System.out.println("Id Cuenta por cobrar: " + cxc.getId());
					// CuentaCobrar ccP =
					// (CuentaCobrar)getEntityManager().createQuery("selec c from CuentaCobrar c where c.id =: id").setParameter("id",
					// )
					
					if(getAbonoCxc() > 0)
					{
						
						System.out.println("*** Entro a primer abono");
						System.out.println("***Abono "+ getAbonoCxc());
						
						// Llenamos un objeto de pgo
						PagoCuentaPend pagoCxc = new PagoCuentaPend();
						// pagoCxc.setComentario(descPagoCxc);
						pagoCxc.setComentario("pago abono");
						/*
						 * if((mnt.floatValue()-getAbonoCxc().floatValue()) <
						 * getAbonoCxc()) {
						 * pagoCxc.setMonto(getAbonoCxc().floatValue());
						 * pagoCxc.setRemanente(0f); } else {
						 * pagoCxc.setMonto(mnt.floatValue());
						 * pagoCxc.setRemanente((
						 * mnt.floatValue()-getAbonoCxc().floatValue())); }
						 */
	
						pagoCxc.setMonto(abn);
						pagoCxc.setRemanente(remt);
	
						System.out.println(remt);
	
						pagoCxc.setCondicionPago(cxc.getCondicionPago());
						pagoCxc.setCuentaCobrar(cxc);
						pagoCxc.setFechaIngreso(new Date());
						pagoCxc.setSucursal(loginUser.getUser().getSucursal());
						pagoCxc.setEstado("Aprobado");
						CondicionPago condPagoCxc;
	
						// Registramos el pago
						getEntityManager().persist(pagoCxc);
	
						// Generamos un asiento contable por el pago efectuado
						AsientoContable asi = asientoContableHome
								.genAsientoParametrizado(
										"CXCVSVMD",
										"CTCAPCNT",
										pagoCxc.getMonto(),
										"PAGO DE CxC",
										pagoCxc.getComentario()
												+ " - # comprobante CxC: "
												+ cxc.getComprobante(),
										instance.getCliente(),
										instance.getCliCorp(), "ABO", "CRG");
						pagoCxc.setAsiento(asi);
	
						getEntityManager().merge(pagoCxc);
					}
					// Verificamos el remanente si es cero
					/*
					 * if(pagoCxc.getRemanente() <= 0) {
					 * instance.setRemanente(0f); instance.setEstado("PGD");
					 * instance.setFechaFinalizacion(new Date()); }
					 */
					// instance.setRemanente(pagoCxc.getRemanente());

					// getEntityManager().persist(pagoCxc);
					// getEntityManager().flush();
					// getEntityManager().refresh(instance);
					/*
					 * setDescPagoCxc(null); setMontoPagoCxc(null);
					 * setCondPagoCxc(null); sainv_messages.clear();
					 * FacesMessages.instance().add(
					 * sainv_messages.get("ctxcb_cxcpagadas"));
					 */
					instance.setEstado("ABN");
					instance.setMonto(0f);
					instance.setCodTipoVenta(cxc.getComprobante());
					if (modify()) {
						res = true;
						FacesMessages.instance().add(
								sainv_messages.get("vtagnr_msg_vtapli"));
					}
					
					
					if(getAbonoCxc() > 0)
					{
						// Insertar nueva venta
						// Cliente cliVenta = instance.getCliente();
						Empresa empVenta = instance.getEmpresa();
						Usuario userEfec = instance.getUsrEfectua();
						String tipoDescVta = instance.getTipoDescuento();
						// String tipoCodVenta = instance
						instance = new VentaProdServ();
						instance.setFechaVenta(new Date());
						instance.setTipoVenta("ABN");
						instance.setCodTipoVenta(cxc.getComprobante());
						instance.setMonto(abn);
						instance.setDetalle("Primer abono, comprobante: "
								+ cxc.getComprobante());
						instance.setIdDetalle(0);
						instance.setCliente(cxc.getCliente());
						instance.setEstado("APR");
						instance.setEmpresa(empVenta);
						instance.setUsrEfectua(userEfec);
						instance.setSucursal(cxc.getSucursal());
						instance.setTipoDescuento(tipoDescVta);
						instance.setCliCorp(cxc.getCliCorp());
						getEntityManager().persist(instance);
					
					
						DetVentaProdServ detalleAbn = new DetVentaProdServ();
						detalleAbn.setCantidad(1);
						detalleAbn.setMonto(abn);
						detalleAbn.setDetalle("Abono a venta");
						detalleAbn.setVenta(instance);
						detalleAbn.setCodClasifVta("ABN");
						detalleAbn.setCosto(abn);
						detalleAbn.setCodExacto(cxc.getComprobante());
	
						getEntityManager().persist(detalleAbn);
	
						getEntityManager().refresh(instance);
						getEntityManager().refresh(detalleAbn);
					}
					

				} else {

					System.out.println(" 2 mnt " + mnt);
					if (mnt > 0) {

						System.out.println("entro al if (mnt>0)");
						if (instance.getTipoVenta().equals("CMB"))
							asientoContableHome.genAsientoParametrizado(
									"CTCSVAMP", "CTCCVTAIT", mnt.floatValue(),
									"VENTA DE COMBO DE APARATO AUDITIVO",
									instance.getDetalle(),
									instance.getCliente(), null, "CRG", "ABO");
						else if (instance.getTipoVenta().equals("CST"))
							asientoContableHome.genAsientoParametrizado(
									"CTCSVRVM", "CTCCVTAIT", mnt.floatValue(),
									"COBRO POR SERVICIOS MEDICOS",
									instance.getDetalle(),
									instance.getCliente(), null, "CRG", "ABO");
						else if (instance.getTipoVenta().equals("TLL"))
							asientoContableHome.genAsientoParametrizado(
									"CTCSVREP", "CTCCVTAIT", mnt.floatValue(),
									"COBRO POR SERVICIO DE TALLER",
									instance.getDetalle(),
									instance.getCliente(), null, "CRG", "ABO");
						else if (instance.getTipoVenta().equals("ITM"))
							asientoContableHome.genAsientoParametrizado(
									"CTCSVAMP", "CTCCVTAIT", mnt.floatValue(),
									"VENTA DE PRODUCTOS INDIVIDUALES",
									instance.getDetalle(),
									instance.getCliente(), null, "CRG", "ABO");
						else if (instance.getTipoVenta().equals("EXA"))
							asientoContableHome.genAsientoParametrizado(
									"CTCSVRVM", "CTCCVTAIT", mnt.floatValue(),
									"SERVICIO DE EXAMENES MEDICOS",
									instance.getDetalle(),
									instance.getCliente(), null, "CRG", "ABO");
						asientoContableHome.clearInstance();
						asientoContableHome
								.setCtasActivo(new ArrayList<AsientoContDet>());
						asientoContableHome
								.setCtasPasivo(new ArrayList<AsientoContDet>());

					}

					//NOTA: ver que pasa con la venta q se registra como cuenta por cobrar
					
					instance.setFormaPago(formaPago);
						
					instance.setEstado("APR");
					if (modify()) {
						res = true;
						FacesMessages.instance().add(
								sainv_messages.get("vtagnr_msg_vtapli"));
					}

				}
			}

			/*
			 * instance.setEstado("APR"); if(modify()) { res = true;
			 * FacesMessages.instance().add(
			 * sainv_messages.get("vtagnr_msg_vtapli")); }
			 */
		}

		else {
			// Solo cambiamos el estado de la vetna como pendiente por descuento
			// y la guardamos para que salga en otra pantalla
			if (instance.getCantidadDescuento() == null
					|| instance.getCantidadDescuento() <= 0) {
				sainv_messages.clear();
				FacesMessages.instance().add(
						sainv_messages.get("vtagnr_msg_vtactdsc"));
				res = false;
				return res;
			}

			if (instance.getUsrDescuento() == null) {
				sainv_messages.clear();
				FacesMessages.instance().add(
						sainv_messages.get("vtagnr_msg_vtaurdsc"));
				res = false;
				return res;
			}

			instance.setEstado("PDS");
			getEntityManager().merge(instance);
			getEntityManager().flush();
			res = true;
		}
		
		
		
		
		return res;

	}

	public void crearSolicitudImpresion() {
		
		System.out.println("Entro al metodo solicitud de impresion");
		
		
		
		SolicitudImpresion soli = new SolicitudImpresion();

		soli.setFecha(new Date());
		soli.setUsuario(loginUser.getUser());
		soli.setVenta(instance);

		// Guardando la solicitud de impresion
		if (instance.getId() != null) {
			getEntityManager().merge(soli);
			getEntityManager().flush();
		}
	}
	
	
	public void descuentoCliCorp(ClienteCorporativo cliCorp) //NOTA: Actualizar el monto de la venta principal hasta que se de click en aplicar
	{
		
		setCliCorp(cliCorp);
		
			
			
		try {
			
			
			//Verificar si tiene codigos de descuento;
			CodigoDescuentoCorp codigos = new CodigoDescuentoCorp(); //Si el cliente corporativo puede tener mas de un codigo de descuento, esta variable tiene q ser una lista. O pedir directamente el codigo y en base al codigo ingresado asi aplicar los descuenntos
			Float totalDescuentoDet=0f;
			codigos = (CodigoDescuentoCorp) getEntityManager().createQuery("SELECT d FROM CodigoDescuentoCorp d where d.clienteCorp=:clienteCorp and d.estado='ACT'").setParameter("clienteCorp", cliCorp).getSingleResult();
			
			limpiarDetalles();
			
			if(codigos!=null)
			{
				
				for(DetDescuentoCorp detCod: codigos.getDetDescuentos())//Por cada item con descuento se comparara con todos los detalles para asignarle el descuento
				{
					
					for(DetVentaProdServ detVenta: instance.getDetVenta())
					{
						
						if(detVenta.getProducto()!=null && detCod.getProducto()!=null)
						{
							if(detVenta.getProducto().getId().equals(detCod.getProducto().getId()))
							{
								detVenta.setDescuentoCorp(detCod.getDescuento());  //Descuento a aplicar
								//detVenta.setMonto((detVenta.getMonto()-detCod.getDescuento()));  //Descuento aplicado al detalle
								totalDescuentoDet+=detCod.getDescuento()*detVenta.getCantidad();  //Total descontado del detalle
								//instance.setMonto(instance.getMonto()-totalDescuentoDet);
								
								//getEntityManager().merge(detVenta); //actualizar el detalle y la venta al final
								//getEntityManager().merge(instance);
								//getEntityManager().flush();
								//getEntityManager().refresh(instance);
								break;
							}
							
						}
						else if(detVenta.getServicio()!=null && detCod.getService()!=null)
						{
							
							if(detVenta.getServicio().getId().equals(detCod.getService().getId()))
							{
								detVenta.setDescuentoCorp(detCod.getDescuento());  //Descuento a aplicar
								//detVenta.setMonto((detVenta.getMonto()-detCod.getDescuento()));  //Descuento aplicado al detalle
								totalDescuentoDet+=detCod.getDescuento()*detVenta.getCantidad();  //Total descontado del detalle
								//instance.setMonto(instance.getMonto()-totalDescuentoDet);
								
								//getEntityManager().merge(detVenta); //actualizar el detalle y la venta al final
								//getEntityManager().merge(instance);
								//getEntityManager().flush();
								//getEntityManager().refresh(instance);
								
								break;
							}
							
							
						}
						
						
					}
					
				}
				
				System.out.println("Termino los for");
				if(totalDescuentoDet!=0f)
					instance.setTotalDescuentoCorp(moneyDecimal(totalDescuentoDet).floatValue());//instance.setTotalDescuentoCorp(totalDescuentoDet);
				
			}
			
		} catch (Exception e) {
			
			limpiarDetalles();
			instance.setTotalDescuentoCorp(null);
			
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println("Lanso un error");
		}	
			
		
	}
	
	public void descuentoCliCorp() //NOTA: Actualizar el monto de la venta principal hasta que se de click en aplicar
	{
		System.out.println("codigo del cupon ******"+getCuponCod());
		if(getCuponCod().equals(""))
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el codigo");
			return;
		}
		
	
		try {
			
			
			//Verificar si tiene codigos de descuento;
			CodigoDescuentoCorp codigos = new CodigoDescuentoCorp(); //Si el cliente corporativo puede tener mas de un codigo de descuento, esta variable tiene q ser una lista. O pedir directamente el codigo y en base al codigo ingresado asi aplicar los descuenntos
			Float totalDescuentoDet=0f;
			codigos = (CodigoDescuentoCorp) getEntityManager().createQuery("SELECT d FROM CodigoDescuentoCorp d where d.codigo=:codigo and d.estado='ACT'").setParameter("codigo", cuponCod).getSingleResult();
			
			limpiarDetalles();
			
			if(codigos!=null)
			{
				
				for(DetDescuentoCorp detCod: codigos.getDetDescuentos())//Por cada item con descuento se comparara con todos los detalles para asignarle el descuento
				{
					
					for(DetVentaProdServ detVenta: instance.getDetVenta())
					{
						
						if(detVenta.getProducto()!=null && detCod.getProducto()!=null)
						{
							if(detVenta.getProducto().getId().equals(detCod.getProducto().getId()))
							{
								detVenta.setDescuentoCorp(detCod.getDescuento());  //Descuento a aplicar
								//detVenta.setMonto((detVenta.getMonto()-detCod.getDescuento()));  //Descuento aplicado al detalle
								totalDescuentoDet+=detCod.getDescuento()*detVenta.getCantidad();  //Total descontado del detalle
								//instance.setMonto(instance.getMonto()-totalDescuentoDet);
								
								//getEntityManager().merge(detVenta); //actualizar el detalle y la venta al final
								//getEntityManager().merge(instance);
								//getEntityManager().flush();
								//getEntityManager().refresh(instance);
								break;
							}
							
						}
						else if(detVenta.getServicio()!=null && detCod.getService()!=null)
						{
							
							if(detVenta.getServicio().getId().equals(detCod.getService().getId()))
							{
								detVenta.setDescuentoCorp(detCod.getDescuento());  //Descuento a aplicar
								//detVenta.setMonto((detVenta.getMonto()-detCod.getDescuento()));  //Descuento aplicado al detalle
								totalDescuentoDet+=detCod.getDescuento()*detVenta.getCantidad();  //Total descontado del detalle
								//instance.setMonto(instance.getMonto()-totalDescuentoDet);
								
								//getEntityManager().merge(detVenta); //actualizar el detalle y la venta al final
								//getEntityManager().merge(instance);
								//getEntityManager().flush();
								//getEntityManager().refresh(instance);
								
								break;
							}
							
							
						}
						
						
					}
					
				}
				
				System.out.println("Termino los for");
				if(totalDescuentoDet!=0f)
					instance.setTotalDescuentoCorp(moneyDecimal(totalDescuentoDet).floatValue());
				
			}
			else
			{
				FacesMessages.instance().add(Severity.WARN,"Cupo invalido");
				
			}
			
		} catch (Exception e) {
			
			limpiarDetalles();
			instance.setTotalDescuentoCorp(null);
			
			FacesMessages.instance().add(Severity.WARN,"Cupo invalido");
			
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println("Lanso un error");
		}	
			
		
	}
	
	public void limpiarDetalles()
	{
		for(DetVentaProdServ detVenta:instance.getDetVenta())
		{
			detVenta.setDescuentoCorp(null);
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
		
		System.out.println("Imprimir*********");
		//crearSolicitudImpresion();

	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub

	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub

	}

	public Integer getVtaPrsId() {
		return vtaPrsId;
	}

	public void setVtaPrsId(Integer vtaPrsId) {
		this.vtaPrsId = vtaPrsId;
	}

	public List<VentaProdServ> getResultList() {
		return resultList;
	}

	public void esultList(List<VentaProdServ> resultList) {
		this.resultList = resultList;
	}

	public Double getTotalCostos() {
		return totalCostos;
	}

	public void setTotalCostos(Double totalCostos) {
		this.totalCostos = totalCostos;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public boolean isCuentaCobrar() {
		return cuentaCobrar;
	}

	public void setCuentaCobrar(boolean cuentaCobrar) {
		this.cuentaCobrar = cuentaCobrar;
	}

	public Double getAbonoCxc() {
		if (abonoCxc == null)
			return 0.00;
		return abonoCxc;
	}

	public void setAbonoCxc(Double abonoCxc) {
		this.abonoCxc = abonoCxc;
	}

	public ClienteCorporativo getCliCorp() {
		return cliCorp;
	}

	public void setCliCorp(ClienteCorporativo cliCorp) {
		this.cliCorp = cliCorp;
	}

	public boolean isLlevaDescuento() {
		return llevaDescuento;
	}

	public void ponerDescuento() {
		setLlevaDescuento(true);
		comprobarDescuento();
	}

	public void quitarDescuento() {
		setLlevaDescuento(false);
	}

	public void setLlevaDescuento(boolean llevaDescuento) {
		this.llevaDescuento = llevaDescuento;
		if (!llevaDescuento) {
			System.out.println("lleva Descuento false");
			instance.setCantidadDescuento(null);
			instance.setTipoDescuento(null);
			instance.setUsrDescuento(null);
			desEfectivo = 0f;
			desPorcentaje = 0f;
		}
	}

	public void comprobarDescuento() {
		if (instance.getUsrDescuento() != null) {
			if (instance.getCantidadDescuento() != null) {
				if (instance.getTipoDescuento() != null) {
					if (instance.getTipoDescuento().equals("M")
							&& instance.getCantidadDescuento().floatValue() > instance
									.getMonto()) {
						FacesMessages.instance().add(
								sainv_messages.get("monto_elevado"));
						instance.setCantidadDescuento(null);
						setLlevaDescuento(false);
						return;
					} else if (instance.getTipoDescuento().equals("P")
							&& instance.getCantidadDescuento() > 100) {
						FacesMessages.instance().add(
								sainv_messages.get("porcentaje_elevado"));
						instance.setCantidadDescuento(null);
						setLlevaDescuento(false);
						return;
					}
				} else {
					FacesMessages.instance().add(sainv_messages.get("no_type"));
					instance.setCantidadDescuento(null);
					setLlevaDescuento(false);
					System.out.println("No lleva tipo");
					return;
				}
			} else {
				FacesMessages.instance().add(sainv_messages.get("no_mount"));
				instance.setCantidadDescuento(null);
				setLlevaDescuento(false);
				System.out.println("No lleva cantidad");
				return;
			}
		} else {
			FacesMessages.instance().add(sainv_messages.get("no_user"));
			instance.setCantidadDescuento(null);
			setLlevaDescuento(false);
			return;
		}

	}

	public boolean isCobroDesc() {
		return cobroDesc;
	}

	public void setCobroDesc(boolean cobroDesc) {
		this.cobroDesc = cobroDesc;
	}

	public Double getTotalConDesc() {
		return totalConDesc;
	}

	public void setTotalConDesc(Double totalConDesc) {
		this.totalConDesc = totalConDesc;
	}

	public TasaTarjetaCred getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(TasaTarjetaCred formaPago) {
		this.formaPago = formaPago;
	}

	public Double getTotalCrgs() {
		return totalCrgs;
	}

	public void setTotalCrgs(Double totalCrgs) {
		this.totalCrgs = totalCrgs;
	}

	public List<Object[]> getProdServList() {
		return prodServList;
	}

	public void setProdServList(List<Object[]> prodServList) {
		this.prodServList = prodServList;
	}

	public List<Object[]> getMontoDiario() {
		return montoDiario;
	}

	public void setMontoDiario(List<Object[]> montoDiario) {
		this.montoDiario = montoDiario;
	}

	public Object getMt() {
		return mt;
	}

	public void setMt(Object mt) {
		this.mt = mt;
	}

	public Object getMtDiario() {
		return mtDiario;
	}

	public void setMtDiario(Object mtDiario) {
		this.mtDiario = mtDiario;
	}

	public Double getTotalRango() {
		return totalRango;
	}

	public void setTotalRango(Double totalRango) {
		this.totalRango = totalRango;
	}

	public Double getTotalDescuento() {
		return totalDescuento;
	}

	public void setTotalDescuento(Double totalDescuento) {
		this.totalDescuento = totalDescuento;
	}

	public Double getTotalMonto() {
		return totalMonto;
	}

	public void setTotalMonto(Double totalMonto) {
		this.totalMonto = totalMonto;
	}

	public Double getTotalRestante() {
		return totalRestante;
	}

	public void setTotalRestante(Double totalRestante) {
		this.totalRestante = totalRestante;
	}

	public List<VentaProdServ> getResultListDesc() {
		return resultListDesc;
	}

	public void setResultListDesc(List<VentaProdServ> resultListDesc) {
		this.resultListDesc = resultListDesc;
	}

	public Float getDesEfectivo() {
		return desEfectivo;
	}

	public void setDesEfectivo(Float desEfectivo) {
		this.desEfectivo = desEfectivo;
	}

	public Float getDesPorcentaje() {
		return desPorcentaje;
	}

	public void setDesPorcentaje(Float desPorcentaje) {
		this.desPorcentaje = desPorcentaje;
	}

	public CuentaCobrar getInfoCxc() {
		return infoCxc;
	}

	public void setInfoCxc(CuentaCobrar infoCxc) {
		this.infoCxc = infoCxc;
	}

	public String getEstadoFilter() {
		return estadoFilter;
	}

	public void setEstadoFilter(String estadoFilter) {
		this.estadoFilter = estadoFilter;
	}

	public Float getNumCero() {
		return numCero;
	}

	public void setNumCero(Float numCero) {
		this.numCero = numCero;
	}


	public List<CuentaCobrar> getListaCuentasPendientes() {
		return listaCuentasPendientes;
	}


	public void setListaCuentasPendientes(List<CuentaCobrar> listaCuentasPendientes) {
		this.listaCuentasPendientes = listaCuentasPendientes;
	}


	public boolean isCupon() {
		return cupon;
	}


	public void setCupon(boolean cupon) {
		this.cupon = cupon;
	}


	public String getCuponCod() {
		return cuponCod;
	}


	public void setCuponCod(String cuponCod) {
		this.cuponCod = cuponCod;
	}
	
	
	

}
