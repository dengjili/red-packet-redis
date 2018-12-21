package priv.dengjl.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RedPacketBean implements Serializable {
	private static final long serialVersionUID = -895522204387870727L;

	private Integer id;

	private Integer userId;

	private BigDecimal amount;

	private Date sendDate;

	private Integer total;

	private Long unitAmount;

	private Integer stock;

	private Integer vsersion;

	private String note;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Long getUnitAmount() {
		return unitAmount;
	}

	public void setUnitAmount(Long unitAmount) {
		this.unitAmount = unitAmount;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Integer getVsersion() {
		return vsersion;
	}

	public void setVsersion(Integer vsersion) {
		this.vsersion = vsersion;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note == null ? null : note.trim();
	}
}