package com.chaitanya.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="advance_process_instance")
public class AdvanceProcessInstanceJPA {

	@Id @GeneratedValue
	@Column(name="process_instance_id")
	private Long processInstanceId;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="advance_detail_id")
	private AdvanceJPA advanceJPA;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="voucher_status_id")
	private VoucherStatusJPA voucherStatusJPA;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="pending_at")
	private EmployeeJPA pendingAt;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="approved_by")
	private EmployeeJPA approvedBy;

	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}


	public VoucherStatusJPA getVoucherStatusJPA() {
		return voucherStatusJPA;
	}

	public void setVoucherStatusJPA(VoucherStatusJPA voucherStatusJPA) {
		this.voucherStatusJPA = voucherStatusJPA;
	}

	public EmployeeJPA getPendingAt() {
		return pendingAt;
	}

	public void setPendingAt(EmployeeJPA pendingAt) {
		this.pendingAt = pendingAt;
	}

	public EmployeeJPA getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(EmployeeJPA approvedBy) {
		this.approvedBy = approvedBy;
	}
	
	public AdvanceJPA getAdvanceJPA() {
		return advanceJPA;
	}

	public void setAdvanceJPA(AdvanceJPA advanceJPA) {
		this.advanceJPA = advanceJPA;
	}
}
