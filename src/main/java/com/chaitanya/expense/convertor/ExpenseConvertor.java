package com.chaitanya.expense.convertor;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.springframework.web.multipart.MultipartFile;

import com.chaitanya.expense.model.ExpenseDetailDTO;
import com.chaitanya.expense.model.ExpenseHeaderDTO;
import com.chaitanya.jpa.EmployeeJPA;
import com.chaitanya.jpa.ExpenseDetailJPA;
import com.chaitanya.jpa.ExpenseHeaderJPA;
import com.chaitanya.utility.Convertor;
import com.chaitanya.utility.Validation;

public class ExpenseConvertor {
	
	public static ExpenseHeaderDTO setExpenseHeaderJPAtoDTO(ExpenseHeaderJPA expenseHeaderJPA){
		ExpenseHeaderDTO expenseHeaderDTO=null;
		
		if(Validation.validateForNullObject(expenseHeaderJPA)){
			expenseHeaderDTO=new ExpenseHeaderDTO(); 
			expenseHeaderDTO.setExpenseHeaderId(expenseHeaderJPA.getExpenseHeaderId());
			expenseHeaderDTO.setStartDate(Convertor.calendartoString(expenseHeaderJPA.getStartDate()));
			expenseHeaderDTO.setEndDate(Convertor.calendartoString(expenseHeaderJPA.getEndDate()));
			expenseHeaderDTO.setTitle(expenseHeaderJPA.getTitle());
			expenseHeaderDTO.setPurpose(expenseHeaderJPA.getPurpose());
			
		}
		return expenseHeaderDTO;
	}
	
	public static ExpenseDetailDTO setExpenseDetailJPAtoDTO(ExpenseDetailJPA expenseDetailJPA){
		ExpenseDetailDTO expenseDetailDTO=null;
		
		if(Validation.validateForNullObject(expenseDetailJPA)){
			expenseDetailDTO=new ExpenseDetailDTO(); 
			expenseDetailDTO.setExpenseDetailId(expenseDetailJPA.getExpenseDetailId());
			expenseDetailDTO.setDate(Convertor.calendartoString(expenseDetailJPA.getDate()));
			expenseDetailDTO.setFromLocation(expenseDetailJPA.getFromLocation());
			expenseDetailDTO.setToLocation(expenseDetailJPA.getToLocation());
			expenseDetailDTO.setAmount(expenseDetailJPA.getAmount());
		}
		return expenseDetailDTO;
	}
	
	
	public static ExpenseHeaderJPA setExpenseHeaderDTOToJPA(ExpenseHeaderDTO expenseHeaderDTO) throws ParseException
	{
		ExpenseHeaderJPA expenseHeaderJPA=null;
		if(Validation.validateForNullObject(expenseHeaderDTO)){
			expenseHeaderJPA=new ExpenseHeaderJPA();
			expenseHeaderJPA.setExpenseHeaderId(expenseHeaderDTO.getExpenseHeaderId());
			EmployeeJPA employeeJPA=new EmployeeJPA();
			employeeJPA.setEmployeeId(expenseHeaderDTO.getEmployeeDTO().getEmployeeId());
			expenseHeaderJPA.setEmployeeJPA(employeeJPA);
			expenseHeaderJPA.setStartDate(Convertor.stringToCalendar(expenseHeaderDTO.getStartDate(),"dd-MMMM-yyyy"));
			expenseHeaderJPA.setEndDate(Convertor.stringToCalendar(expenseHeaderDTO.getEndDate(),"dd-MMMM-yyyy"));
			expenseHeaderJPA.setPurpose(expenseHeaderDTO.getPurpose());
			expenseHeaderJPA.setTitle(expenseHeaderDTO.getTitle());
		}
		return expenseHeaderJPA;
	}
	
	public static ExpenseDetailJPA setExpenseDetailDTOToJPA(ExpenseDetailDTO expenseDetailDTO) throws ParseException, IOException
	{
		ExpenseDetailJPA expenseDetailJPA=null;
		if(Validation.validateForNullObject(expenseDetailDTO)){
			expenseDetailJPA=new ExpenseDetailJPA();
			expenseDetailJPA.setExpenseDetailId(expenseDetailDTO.getExpenseDetailId());
			expenseDetailJPA.setDate(Convertor.stringToCalendar(expenseDetailDTO.getDate(),"dd-MMMM-yyyy"));
			expenseDetailJPA.setFromLocation(expenseDetailDTO.getFromLocation());
			expenseDetailJPA.setToLocation(expenseDetailDTO.getToLocation());
			expenseDetailJPA.setAmount(expenseDetailDTO.getAmount());
			if(!expenseDetailDTO.getReceipt().isEmpty()){
				MultipartFile receipt= expenseDetailDTO.getReceipt();
				File convFile = new File( receipt.getOriginalFilename());
				receipt.transferTo(convFile);
				expenseDetailJPA.setReceipt(convFile);
				expenseDetailJPA.setFileName(receipt.getOriginalFilename());
			}
		}
		return expenseDetailJPA;
	}
}
