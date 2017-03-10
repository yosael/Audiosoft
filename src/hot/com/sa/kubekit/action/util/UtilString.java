package com.sa.kubekit.action.util;

import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.ConversationEntry;

//version 1
@Name("utilString")
@Scope(ScopeType.CONVERSATION)
public class UtilString {

	// Conjunto de palabras comunes
	private static String words[] = { "EL", "LA", "LOS", "LAS", "EN", "POR",
			"PARA", "DE", "CON", "COMO", "DEL", "DONDE" };

	// utilidad para consultas
	/**
	 * Este m�todo tokeniza un string la cual se resibe en el parametro
	 * 'consulta' para formar una consulta con el operador 'and' el parametro
	 * 'variable' resibe el alias asignado a la tabla en la consulta y el
	 * parametro 'parametro' recibe la columna a ser consultada en la base de
	 * datos
	 */
	public String addAndToQuery(String query, String variable, String parameter) {
		if (query == null || query.isEmpty())
			query = "%";
		Vector<String> vector = splitString(query);
		String newQuery = "(" + " ( " + variable + "." + parameter + " )"
				+ " LIKE ";

		for (int i = 0; i < vector.size(); i++) {
			newQuery = newQuery + "'%" + vector.elementAt(i) + "%' ";
			if (i < (vector.size() - 1)) {
				newQuery = newQuery + "AND " + variable + "." + parameter
						+ " LIKE ";
			} else {
				newQuery = newQuery + ")";
			}
		}
		return newQuery;

	}

	// utilidad para consultas
	public String addOrToQuery(String query, String variable, String parameter) {
		Vector<String> vector = splitString(query);
		String newQuery = "(" + variable + "." + parameter + " LIKE ";
		for (int i = 0; i < vector.size(); i++) {
			newQuery = newQuery + "'%" + vector.elementAt(i) + "%' ";
			if (i < (vector.size() - 1)) {
				newQuery = newQuery + "OR " + variable + "." + parameter
						+ " LIKE ";
			} else {
				newQuery = newQuery + ")";
			}
		}
		return newQuery;

	}

	// Parte una cadena de palabras
	private Vector<String> splitString(String cadena) {
		Vector<String> vec = new Vector<String>();
		StringTokenizer token = new StringTokenizer(cadena);
		while (token.hasMoreTokens()) {
			String cad = token.nextToken().toUpperCase();
			if (!isCommonWord(cad)) {
				vec.add(cad.toUpperCase());
			}
		}
		if (vec.isEmpty())
			vec.add("%");
		return vec;

	}

	// valida si la palabra es comun
	private boolean isCommonWord(String cad) {
		for (int i = 0; i < words.length; i++)
			if (cad.equals(words[i]))
				return true;
		return false;
	}

	// quita palabras comunes de una cadena
	public String tokenizer(String cad) {
		cad = cad.replaceAll("-", "");
		System.out.println("CADENA SIN - :" + cad);
		StringBuffer sb = new StringBuffer("");
		for (String s : splitString(cad)) {
			sb.append(s + " ");
		}
		return sb.toString();
	}

	// Permite obtener formatos de tiempo para servicios de tiempo Quartz
	@SuppressWarnings("deprecation")
	public static String cronFromat(Date date) {
		String cron = "";
		cron += date.getSeconds() + " ";
		cron += date.getMinutes() + " ";
		cron += date.getHours() + " ";
		cron += (date.getDate()) + " ";
		cron += (date.getMonth() + 1) + " ";
		cron += "?";
		return cron;
	}

	// Se envia una cadena y una expresion de wildcars como *kube*pattern y
	// el booelan enviado indica si se va a buscar en orden las palabras o no
	// retorna la cantidad de cards encontradas
	public static int wildCardMatch(String text, String pattern, boolean bol) {
		// Crea las cards usando el metodo split
		int cardNum = 0;
		String[] cards = pattern.split("\\*");

		// Itera sobre las cards
		for (String card : cards) {
			int idx = text.toUpperCase().indexOf(card.toUpperCase());

			// Se encontro la card
			if (idx != -1) {
				cardNum++;
			}

			// Move ahead, towards the right of the text.
			if (bol)
				text = text.substring(idx + card.length());
		}

		return cardNum;
	}

	public static String obtenerViewId(String conversation,
			ConversationEntries conversationEntries) {
		ConversationEntry entry = conversationEntries
				.getConversationEntry(conversation);
		System.out.println("LA PAGINA PADRE ES: " + entry.getViewId());
		return entry.getViewId();
	}

}