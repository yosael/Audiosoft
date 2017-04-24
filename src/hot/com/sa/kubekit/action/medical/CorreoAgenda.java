package com.sa.kubekit.action.medical;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CorreoAgenda {
	
	
	private String origen;
	private String destino;
	private final String host="localhost";
	private Properties properties;
	private MimeMessage mensaje;
	private Session session;
	private String asunto;
	private String contenidoHtml;
	
	
	public CorreoAgenda(String destino,String asunto,String contenidoHtml)
	{
		this.origen=origen;
		this.destino=destino;
		properties=System.getProperties();
		this.contenidoHtml=contenidoHtml;
		this.asunto=asunto;
	}
	
	
	public void enviarCorreoSimple()
	{
		
		try {
			
			//properties.setProperty("mail.smtp.host", this.host);
			//properties.setProperty("mail.smtp.socketFactory.port","465");
			
			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.port","587");
			properties.put("mail.smtp.mail.sender","2511962013@mail.utec.edu.sv");//webmasteraudiomed@gmail.com
			//properties.put("mail.smtp.user", "usuario");
			properties.put("mail.smtp.auth", "true");
			properties.setProperty("mail.debug", "true");
			
			
			session = Session.getDefaultInstance(this.properties);
			mensaje = new MimeMessage(session);
			//Desde
			mensaje.setFrom(new InternetAddress((String)properties.get("mail.smtp.mail.sender")));
			
			//Para
			mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
			
			//Asunto
			mensaje.setSubject(asunto);
			
			//Texto
			mensaje.setText("Prueba texto");
			
			//Contenido html
			mensaje.setContent(contenidoHtml, "text/html");
			
			//Enviar correo
			//Transport.send(mensaje);
			
			Transport t = session.getTransport("smtp");
			t.connect((String)properties.get("mail.smtp.user"), "blessUtec1402!");//Audiomed01!
			t.sendMessage(mensaje, mensaje.getAllRecipients());
			t.close();
			
			System.out.println("Mensaje enviado");
			
			
		} catch (Exception e) {
			e.printStackTrace();	
		}
		
	}
	

}
