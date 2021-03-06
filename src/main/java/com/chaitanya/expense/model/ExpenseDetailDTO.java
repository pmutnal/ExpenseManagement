package com.chaitanya.expense.model;

import org.springframework.web.multipart.MultipartFile;

import com.chaitanya.expenseCategory.model.ExpenseCategoryDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpenseDetailDTO {
	private Long expenseDetailId;
	private Long expenseCategoryId;
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="expenseName")
	@JsonIdentityReference(alwaysAsId=true)
	private ExpenseCategoryDTO expenseCategoryDTO;
	private String date;
	private String fromLocation;
	private String toLocation;
	private String description;
	private Integer unit;
	private Double amount;
	@JsonIgnore
	private MultipartFile receipt;
	private String fileName;

	
	public Long getExpenseDetailId() {
		return expenseDetailId;
	}
	public void setExpenseDetailId(Long expenseDetailId) {
		this.expenseDetailId = expenseDetailId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getFromLocation() {
		return fromLocation;
	}
	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}
	public String getToLocation() {
		return toLocation;
	}
	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public MultipartFile getReceipt() {
		return receipt;
	}
	public void setReceipt(MultipartFile receipt) {
		this.receipt = receipt;
	}
	public Long getExpenseCategoryId() {
		return expenseCategoryId;
	}
	public void setExpenseCategoryId(Long expenseCategoryId) {
		this.expenseCategoryId = expenseCategoryId;
		ExpenseCategoryDTO expenseCategoryDTO=new ExpenseCategoryDTO();
		expenseCategoryDTO.setExpenseCategoryId(this.expenseCategoryId);
	}
	@JsonIgnore
	public ExpenseCategoryDTO getExpenseCategoryDTO() {
		return expenseCategoryDTO;
	}
	public void setExpenseCategoryDTO(ExpenseCategoryDTO expenseCategoryDTO) {
		this.expenseCategoryDTO = expenseCategoryDTO;
		this.expenseCategoryId=expenseCategoryDTO.getExpenseCategoryId();
	}
	public Integer getUnit() {
		return unit;
	}
	public void setUnit(Integer unit) {
		this.unit = unit;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
