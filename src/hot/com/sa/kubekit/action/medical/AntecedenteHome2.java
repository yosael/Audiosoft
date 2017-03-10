package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.jboss.seam.international.StatusMessage.Severity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Antecedente;
import com.sa.model.crm.Cliente;
import com.sa.model.crm.PacienteAntecedente;


@Name("antecedenteHome2")
@Scope(ScopeType.CONVERSATION)//Probar cambiar a CONVERSATION
public class AntecedenteHome2 extends KubeDAO<Antecedente> {

	private static final long serialVersionUID = 1L;
	
	private List<Antecedente> resultList= new ArrayList<Antecedente>();
	private Integer antecedenteId;
	private List<PacienteAntecedente> resultListPA;
	
	
	public void load()
	{
		resultList = getEntityManager().createQuery("SELECT a FROM Antecedente a").getResultList();
	}
	
	public void loadMaster()
	{
		//resultList = getEntityManager().createNativeQuery("SELECT a FROM Antecedente a where a.id=:id").setParameter("id", antecedenteId).getSingleResult();
		try {
			setInstance(getEntityManager().find(Antecedente.class, antecedenteId));
		} catch (Exception e) {
			clearInstance();
			instance = new Antecedente();
			
		}
	}
	
	public void cargarAntecedentesPaciente(Cliente cliente)
	{
		resultListPA = new ArrayList<PacienteAntecedente>();
		resultListPA= getEntityManager().createQuery("SELECT pa FROM PacienteAntecedente pa where pa.paciente.id=:idCliente").setParameter("idCliente", cliente.getId()).getResultList();
		//Esta sera la lista que tendra los antecedentes del paciente.
		
		//Al agregar una nueva lista de antecedente revisar que los que no tienen id se tengan que registrar 
	}
	
	public void quitarPacienteAntecedente(PacienteAntecedente pa)
	{
		if(pa.getId()==null)
		{
			resultListPA.remove(pa);//Puede ser que sea un antecedente nuevo o uno del paciente. DESDe memoria
		}
		else
		{
			resultListPA.remove(pa);
			getEntityManager().remove(pa); //Desde la DB
			//getEntityManager().flush();
		}
	}
	
	public void addPacienteAntecedente(Antecedente ant, Cliente paciente)
	{
		PacienteAntecedente pa = new PacienteAntecedente();
		pa.setAntecedente(ant);
		pa.setPaciente(paciente);
		pa.setFechaAntecedente(new Date());
		
		for(PacienteAntecedente antAc: resultListPA)
		{
			if(antAc.getAntecedente().getId()==ant.getId())
			{
				FacesMessages.instance().add(Severity.WARN,"El antecedente ya esta agregado");
				return;
			}
			
		}
		resultListPA.add(pa);
	}
	
	public void persistirAntecedentesLista()
	{
		try {
			
			for(PacienteAntecedente pa: resultListPA)
			{
				if(pa.getId()==null)
				{
					getEntityManager().persist(pa);
					
				}
			}
			getEntityManager().flush();
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}
	}

	@Override
	public boolean preSave() {
		FacesMessages.instance().clear();
		// TODO Auto-generated method stub
		System.out.println("Entro a presave");
		
		if(getInstance().getNombreAntecedente()==null)
		{
			FacesMessages.instance().add("EL nombre es necesario");
			return false;
		}
		
		
		return true;
	}

	@Override
	public boolean preModify() {
		
		System.out.println("Entro a premodify");
		
		if(getInstance().getNombreAntecedente()==null)
		{
			FacesMessages.instance().add("EL nombre es necesario");
			return false;
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
		System.out.println("Entro a possave");
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		System.out.println("Entro a posModify");
		antecedenteId=null;
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}


	public List<Antecedente> getResultList() {
		return resultList;
	}

	public void setResultList(List<Antecedente> resultList) {
		this.resultList = resultList;
	}

	public Integer getAntecedenteId() {
		return antecedenteId;
	}

	public void setAntecedenteId(Integer antecedenteId) {
		this.antecedenteId = antecedenteId;
	}

	public List<PacienteAntecedente> getResultListPA() {
		return resultListPA;
	}

	public void setResultListPA(List<PacienteAntecedente> resultListPA) {
		this.resultListPA = resultListPA;
	}
	
	
	

}
