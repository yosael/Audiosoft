package com.sa.kubekit.action.util;

public class InvoiceGenerator {
		
	public static String genLine(String item, String detail, int lengthLine, String charSpacer) {
		StringBuilder strb = new StringBuilder();
		
		int lengthItem = item.length();
		String newItem = "", tmpItm = "";
		
		if(item.length() > lengthLine/2) {
			int idxBreak = 0;
			boolean breakLoop = false;
			while(idxBreak < item.length()) {
				try {
					tmpItm = item.substring(idxBreak, idxBreak + lengthLine/2);
				} catch(IndexOutOfBoundsException ex) {
					tmpItm = item.substring(idxBreak);
					breakLoop = true;
				}
				if(tmpItm.lastIndexOf(" ") != -1 && tmpItm.lastIndexOf(" ") != 0 && !breakLoop) {
					newItem += "\n" + tmpItm.substring(0, tmpItm.lastIndexOf(" "));
					idxBreak += tmpItm.lastIndexOf(" ");
				} else {
					newItem += "\n" + tmpItm;
					idxBreak += tmpItm.length();
				}
			}

			lengthItem = newItem.substring(newItem.lastIndexOf("\n")).length();
		} else 
			newItem += "\n" + item;
		
		lengthItem += 2 + detail.length();
		lengthItem = lengthLine - lengthItem;
		
		for(int cnt = 0; cnt < lengthItem; cnt++)
			strb.append(charSpacer);
		
		
		return newItem + " " + strb.toString() + " " + detail;
		
	}

}
