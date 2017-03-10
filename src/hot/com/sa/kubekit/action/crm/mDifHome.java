package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.MedioDifusion;


@Name("mDifHome")
@Scope(ScopeType.CONVERSATION)
public class mDifHome extends KubeDAO<MedioDifusion>{
	private static final long serialVersionUID = 1L;
	@In
	private LoginUser loginUser;
	private int mDifId;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("sucursalHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("sucursalHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("sucursalHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(MedioDifusion.class, mDifId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new MedioDifusion());
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
		return true;
		
	}

	@Override
	public void posSave() {

	}

	@Override
	public void posModify() {

	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public int getmDifId() {
		return mDifId;
	}

	public void setmDifId(int mDifId) {
		this.mDifId = mDifId;
	}
}
