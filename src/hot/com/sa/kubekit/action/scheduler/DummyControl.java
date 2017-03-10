package com.sa.kubekit.action.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import com.sa.kubekit.action.util.EmailService;
/**
* @author SAPLIC
*/
@Name("dummyControl")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class DummyControl {
   
  @In
	private EmailService emailService;
 
  @Asynchronous
  public QuartzTriggerHandle doJob(@Expiration Date when, @IntervalCron String interval) {
    
    List<Map> lstDestinatarios = new ArrayList<Map>();
    Map mapi1 = new HashMap();
    mapi1.put("email", "jfuentes@solucionesaplicativas.com");
    mapi1.put("name", "Jonathan Analista");
    lstDestinatarios.add(mapi1);
    
    Map mapa = new HashMap();
    mapa.put("lst_to", lstDestinatarios);
    mapa.put("title_em", "Correo de prueba");

    mapa.put("imp_em", "low"); //low, normal, high en importance
    mapa.put("req_response", "true"); //true, false en requestReadReceipt
    
    List<Map> lstReply = new ArrayList<Map>();
    Map mapi3 = new HashMap();
    mapi3.put("email", "jzepeda@solucionesaplicativas.com");
    lstReply.add(mapi3);
    mapa.put("lst_reply_to", lstReply); 
    
    mapa.put("message", "<b>Mensaje</b> de prueba");
    emailService.sendMessage(10000, "/email/normalmsg.xhtml", mapa);
    
    return null;
  }
}