package com.sa.model.inventory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.inventory.id.ItemPrescriptionId;
import com.sa.model.medical.Prescription;

@Entity
@Table(name = "item_prescription")
public class ItemPrescription implements Serializable{

	private static final long serialVersionUID = 1L;
	private ItemPrescriptionId itemPrescriptionId;
	private Inventario inventario;
	private Integer cantidad;
	private Prescription prescription;
	
	
	public ItemPrescription(){
		this.setItemPrescriptionId(new ItemPrescriptionId());
	}
	
	@EmbeddedId
	public ItemPrescriptionId getItemPrescriptionId() {
		return itemPrescriptionId;
	}

	public void setItemPrescriptionId(ItemPrescriptionId itemPrescriptionId) {
		this.itemPrescriptionId = itemPrescriptionId;
	}

	@Column(name = "cantidad", nullable = false)
	@Min(value=1)
	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prescription_id", nullable = false, insertable = false, updatable = false)
	@ForeignKey(name = "fk_item_prescription_prescription")
	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventario_id", nullable = false, insertable = false, updatable = false)
	@ForeignKey(name = "fk_item_prescription_inventario")
	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}

}
