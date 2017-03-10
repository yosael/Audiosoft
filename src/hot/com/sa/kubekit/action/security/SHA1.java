/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sa.kubekit.action.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Jesus
 */
public class SHA1 {
 
    private MessageDigest md;
    private byte[] buffer, digest;
    private String hash = "";
	private String salt = "6b7f60574cbd49e95af8a8a7cf9aa73ca4668850";
    
    
    public String getHash(String message) throws NoSuchAlgorithmException {
        buffer = message.getBytes();
        md = MessageDigest.getInstance("SHA1");
        md.update(buffer);
        digest = md.digest();

        for(byte aux : digest) {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) hash += "0";
            hash += Integer.toHexString(b);
        }

        return hash;
    }


	public String getSalt() {
		return salt;
	}


	public void setSalt(String salt) {
		this.salt = salt;
	}
}
