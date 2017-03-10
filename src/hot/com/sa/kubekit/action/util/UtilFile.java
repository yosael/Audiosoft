package com.sa.kubekit.action.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

//version 1
@Name("utilFile")
@Scope(ScopeType.CONVERSATION)
public class UtilFile {

	public static String obtainFileExtension(String uri) {
		char[] extension = new char[uri.length()];
		extension = uri.toCharArray();
		String ext = new String();
		for (int i = (extension.length - 1); i >= 0; i--) {
			ext = extension[i] + ext;
			if (extension[i] == '.') {
				break;
			}
		}
		return ext;
	}

	public static void copyFile(String src, String trg) {
		try {
			File fTarget = new File(trg);
			if (!fTarget.exists()) {
				fTarget.createNewFile();
			} else {
				fTarget.delete();
				fTarget.createNewFile();
			}
			FileInputStream fis = new FileInputStream(src);
			FileOutputStream fos = new FileOutputStream(trg);
			FileChannel source = fis.getChannel();
			FileChannel destiny = fos.getChannel();
			source.transferTo(0, source.size(), destiny);
			fis.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String obtainPath() {

		javax.faces.context.FacesContext fc = javax.faces.context.FacesContext
				.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();
		HttpSession session = request.getSession();
		String path = null;

		// este metodo regresa la carpeta donde se van a guardar los archivos
		path = session.getServletContext().getRealPath("/");
		// String path = "C:" + System.getProperty("file.separator") +
		// "empleados" +
		// System.getProperty("file.separator") +
		// file.getNombre();

		return path;
	}

}