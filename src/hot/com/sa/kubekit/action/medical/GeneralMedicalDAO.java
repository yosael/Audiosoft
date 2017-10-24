package com.sa.kubekit.action.medical;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.UploadEvent;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.GeneralMedical;
import com.sa.model.medical.MedicamentoConsulta;
import com.sa.model.medical.MotivoConsulta;

@Name("generalMedicalDAO")
@Scope(ScopeType.CONVERSATION)
public class GeneralMedicalDAO extends KubeDAO<GeneralMedical> {

	private static final long serialVersionUID = 1L;
	private List<MedicamentoConsulta> medicamentos = new ArrayList<MedicamentoConsulta>();
	private boolean toggle=true;
	private boolean toggle2=true;
	private boolean toggle3=true;
	private List<MotivoConsulta> motivosAgregados = new ArrayList<MotivoConsulta>();
	private boolean imgSize = true;

	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("generalMedicalDAO_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("generalMedicalDAO_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("generalMedicalDAO_deleted")));
	}

	public void load() {
		try {
			GeneralMedical generalMedical = getEntityManager().find(
					GeneralMedical.class, Integer.parseInt((String) getId()));
			select(generalMedical);
		} catch (Exception e) {
			e.printStackTrace();
			clearInstance();
		}
	}
	
	
	public void agregarMotivoConsulta(MotivoConsulta motivo)
	{
		
		System.out.println("ENTRO a metodo agregar motivo consulta");
		
		/*if(motivosAgregados.contains(motivo))
		{
			
			FacesMessages.instance().add(Severity.WARN,"El motivo de consulta ya fue agregado");
			return;
		}
		
		if(instance.getConsultationReason()==null)
		{
			instance.setConsultationReason("");
		}*/
		
		if(motivo.getSelected())
		{
			motivosAgregados.add(motivo);
			generarMotivoConsulta();
		}
		else
		{
			motivosAgregados.remove(motivo);
			generarMotivoConsulta();
		}
		
		
		/*if(instance.getConsultationReason().contains(motivo.getDescripcion()))
		{
			FacesMessages.instance().add(Severity.WARN,"El motivo de consulta ya fue agregado");
			return;
		}
		
		instance.setConsultationReason(instance.getConsultationReason()+". "+motivo.getDescripcion());
		FacesMessages.instance().add(Severity.INFO,"Motivo de consulta agregado");*/
		
		//motivosAgregados.add(motivo);
		
	}
	
	public void generarMotivoConsulta()
	{
		instance.setConsultationReason("");
		
		if(motivosAgregados.size()>0)
		{
			//instance.getConsultationReason();
			StringBuilder blMotivos = new StringBuilder();
			for(MotivoConsulta motivo:motivosAgregados)
			{
				blMotivos.append(motivo.getDescripcion()).append(". ");
			}
			
			instance.setConsultationReason(blMotivos.toString());
		}
		
	}
	
	public void openToggle(boolean bol)
	{
		setToggle(bol);
		
	}
	
	public void openToggles2(boolean bol)
	{
		setToggle2(bol);
		
	}
	
	public void openToggles3(boolean bol)
	{
		setToggle3(bol);
		
	}

	@Override
	public void posDelete() {

	}

	@Override
	public void posModify() {

	}

	@Override
	public void posSave() {
		if(medicamentos != null) {
			for(MedicamentoConsulta item: medicamentos){
				item.setConsulta(instance);
				getEntityManager().persist(item);
			}
		}
	}

	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public boolean preModify() {
		getInstance().setLastModificationDate(new Date());
		return true;
	}

	@Override
	public boolean preSave() {
		
		/*DateFormat newdateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Date ndia1=null;
		String fechaString=newdateFormat1.format(new Date());
		Date fechafinal=new Date();
		
		try {
			
			fechafinal= newdateFormat1.parse(fechaString);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("*********Fecha Final "+fechafinal);*/
		
		getInstance().setCreationDate(new Date());
		getInstance().setLastModificationDate(new Date());
		
		return true;
	}
	
	public void loadImage(UploadEvent e) 
	{
		String path = e.getUploadItem().getFileName();
		//System.out.println("Path var: "+path);
		
		File file = new File(e.getUploadItem().getFile().getPath());
		int length = e.getUploadItem().getFileName().length();
		// validamos que la imagen no exceda los 100 KB
		if (file.length() > 100000) {
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("productoHome_error_image"));
			setImgSize(false);
		}  
		else 
		{
			setImgSize(true);
			byte[] bFile = new byte[(int) file.length()];
			try 
			{
				FileInputStream fileInputStream = new FileInputStream(file);
				// convert file into array of bytes
				fileInputStream.read(bFile);
				fileInputStream.close();
				instance.setImagenExaAudiologia(bFile);
				
				System.out.println(instance.getImagenExaAudiologia().length);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public List<MedicamentoConsulta> getMedicamentos() {
		return medicamentos;
	}

	public void setMedicamentos(List<MedicamentoConsulta> medicamentos) {
		this.medicamentos = medicamentos;
	}

	public boolean isToggle() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}

	public boolean isToggle2() {
		return toggle2;
	}

	public void setToggle2(boolean toggle2) {
		this.toggle2 = toggle2;
	}

	public boolean isToggle3() {
		return toggle3;
	}

	public void setToggle3(boolean toggle3) {
		this.toggle3 = toggle3;
	}

	public boolean isImgSize() {
		return imgSize;
	}

	public void setImgSize(boolean imgSize) {
		this.imgSize = imgSize;
	}
	
	
	
	

}