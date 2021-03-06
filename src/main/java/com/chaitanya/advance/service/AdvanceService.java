package com.chaitanya.advance.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chaitanya.advance.convertor.AdvanceConvertor;
import com.chaitanya.advance.dao.IAdvanceDAO;
import com.chaitanya.advance.model.AdvanceDTO;
import com.chaitanya.base.BaseDTO;
import com.chaitanya.base.BaseDTO.ServiceStatus;
import com.chaitanya.employee.convertor.EmployeeConvertor;
import com.chaitanya.event.convertor.EventConvertor;
import com.chaitanya.jpa.AdvanceJPA;
import com.chaitanya.jpa.AdvanceProcessHistoryJPA;
import com.chaitanya.jpa.AdvanceProcessInstanceJPA;
import com.chaitanya.jpa.EmployeeJPA;
import com.chaitanya.jpa.VoucherStatusJPA;
import com.chaitanya.utility.Validation;

@Service("advanceService")
@Transactional(rollbackFor=Exception.class)
public class AdvanceService implements IAdvanceService{
	
	@Autowired
	private IAdvanceDAO advanceDAO;
	
	private Logger logger= LoggerFactory.getLogger(AdvanceService.class);

	@Override
	public BaseDTO saveAdvance(BaseDTO baseDTO) throws ParseException {
		logger.debug("AdvanceService: saveAdvance-Start");
		validateAdvanceDTO(baseDTO);
		try{
			AdvanceJPA advanceJPA=AdvanceConvertor.setAdvanceDTOToJPA((AdvanceDTO)baseDTO);
			if (Validation.validateForNullObject(advanceJPA)) {
				AdvanceProcessHistoryJPA processHistoryJPA = AdvanceConvertor.setExpenseHeaderJPAtoProcessHistoryJPA(advanceJPA);
				processHistoryJPA.setAdvanceJPA(advanceJPA);
				List<AdvanceProcessHistoryJPA> processHistoryJPAList= new ArrayList<AdvanceProcessHistoryJPA>();
				processHistoryJPAList.add(processHistoryJPA);
				advanceJPA.setProcessHistoryJPA(processHistoryJPAList);
				
				advanceJPA=advanceDAO.saveUpdateAdvance(advanceJPA);
				
				//Create process instance if voucher not saved as draft.
				if(advanceJPA.getVoucherStatusJPA().getVoucherStatusId() != 1){
					if(! Validation.validateForEmptyString(advanceJPA.getAdvanceNumber())){
						String voucherNumber = advanceDAO.generateAdvanceNumber(advanceJPA);
						advanceJPA.setAdvanceNumber(voucherNumber);
					}
					else{
						advanceJPA.setAdvanceNumber(advanceJPA.getAdvanceNumber()+"*");
					}
					advanceDAO.updateProcessInstance(advanceJPA,advanceJPA.getVoucherStatusJPA().getVoucherStatusId(),null);
				}
				if(Validation.validateForNullObject(advanceJPA)){
					baseDTO=AdvanceConvertor.setAdvanceJPAtoDTO(advanceJPA);
					baseDTO.setServiceStatus(ServiceStatus.SUCCESS);
				}
			}
			else{
				baseDTO.setServiceStatus(ServiceStatus.BUSINESS_VALIDATION_FAILURE);
			}
		}
		catch(DataIntegrityViolationException e){
			baseDTO.setServiceStatus(ServiceStatus.BUSINESS_VALIDATION_FAILURE);
			baseDTO.setMessage(new StringBuilder(e.getMessage()));
			logger.error("AdvanceService: saveAdvance ",e);
		}
		logger.debug("AdvanceService: saveAdvance-End");
		return baseDTO;
	}

	
	private void validateAdvanceDTO(BaseDTO baseDTO) {
		if( baseDTO == null  || !(baseDTO instanceof AdvanceDTO)){
			throw new IllegalArgumentException("Object expected of AdvanceDTO type.");
		}
	}


	@Override
	public List<AdvanceDTO> getDraftAdvanceList(BaseDTO baseDTO) {
		logger.debug("AdvanceService: getDraftAdvanceList-Start");
		validateAdvanceDTO(baseDTO);

		
		List<AdvanceDTO> advanceDTOList= null;
		if (Validation.validateForNullObject(baseDTO)) {
			AdvanceDTO advanceDTO=(AdvanceDTO) baseDTO;;
			List<AdvanceJPA> advanceJPAList =advanceDAO.getDraftAdvanceList(advanceDTO);
			if(Validation.validateForNullObject(advanceJPAList)){
				advanceDTOList= new ArrayList<AdvanceDTO>();
				for(AdvanceJPA advanceJPA: advanceJPAList){
					AdvanceDTO advDTO=AdvanceConvertor.setAdvanceJPAtoDTO(advanceJPA);
					if(Validation.validateForNullObject(advanceJPA.getEventJPA())){
						advDTO.setEventDTO(EventConvertor.setEventJPAtoDTO(advanceJPA.getEventJPA()));
					}
					advanceDTOList.add(advDTO);
				}
				baseDTO.setServiceStatus(ServiceStatus.SUCCESS);
			}
		}
		else{
			baseDTO.setServiceStatus(ServiceStatus.BUSINESS_VALIDATION_FAILURE);
		}
		logger.debug("AdvanceService: getDraftAdvanceList-End");
		return  advanceDTOList;
	}


	@Override
	public BaseDTO getAdvance(BaseDTO baseDTO) {
		logger.debug("AdvanceService: getAdvance-Start");
		validateAdvanceDTO(baseDTO);
		
		if (Validation.validateForNullObject(baseDTO)) {
			AdvanceDTO advanceDTO=(AdvanceDTO) baseDTO;;
			AdvanceJPA advanceJPA =advanceDAO.getAdvance(advanceDTO);
			if(Validation.validateForNullObject(advanceJPA)){
				baseDTO=AdvanceConvertor.setAdvanceJPAtoDTO(advanceJPA);
				baseDTO.setServiceStatus(ServiceStatus.SUCCESS);
			}
		}
		else{
			baseDTO.setServiceStatus(ServiceStatus.BUSINESS_VALIDATION_FAILURE);
		}
		logger.debug("AdvanceService: getAdvance-End");
		return  baseDTO;
	}


	@Override
	public List<AdvanceDTO> getPendingAdvanceList(BaseDTO baseDTO) {
		logger.debug("AdvanceService: getPendingAdvanceList-Start");
		validateAdvanceDTO(baseDTO);
		
		List<AdvanceDTO> advanceDTOList= null;
		if (Validation.validateForNullObject(baseDTO)) {
			AdvanceDTO advanceDTO=(AdvanceDTO) baseDTO;;
			List<AdvanceJPA> advanceJPAList =advanceDAO.getPendingAdvanceList(advanceDTO);
			if(Validation.validateForNullObject(advanceJPAList)){
				advanceDTOList= new ArrayList<AdvanceDTO>();
				for(AdvanceJPA advanceJPA: advanceJPAList){
					AdvanceDTO advDTO=AdvanceConvertor.setAdvanceJPAtoDTO(advanceJPA);
					if(Validation.validateForNullObject(advanceJPA.getEventJPA())){
						advDTO.setEventDTO(EventConvertor.setEventJPAtoDTO(advanceJPA.getEventJPA()));
					}
					advanceDTOList.add(advDTO);
				}
				baseDTO.setServiceStatus(ServiceStatus.SUCCESS);
			}
		}
		else{
			baseDTO.setServiceStatus(ServiceStatus.BUSINESS_VALIDATION_FAILURE);
		}
		
		logger.debug("AdvanceService: getPendingAdvanceList-End");
		return  advanceDTOList;
	}


	@Override
	public List<AdvanceDTO> getAdvanceToBeApprove(BaseDTO baseDTO) {

		logger.debug("AdvanceService: getAdvanceToBeApprove-Start");
		validateAdvanceDTO(baseDTO);
		
		List<AdvanceDTO> advanceDTOList= null;
		try{
			if (Validation.validateForNullObject(baseDTO)) {
				AdvanceDTO advanceDTO=(AdvanceDTO) baseDTO;;
				List<AdvanceJPA> advanceJPAList =advanceDAO.getAdvanceToBeApprove(advanceDTO);
				if(Validation.validateForNullObject(advanceJPAList)){
					advanceDTOList= new ArrayList<AdvanceDTO>();
					for(AdvanceJPA expenseHeaderJPA: advanceJPAList){
						AdvanceDTO advDTO=AdvanceConvertor.setAdvanceJPAtoDTO(expenseHeaderJPA);
						if(Validation.validateForNullObject(expenseHeaderJPA.getEmployeeJPA())){
							advDTO.setEmployeeDTO(EmployeeConvertor.setEmployeeJPAToEmployeeDTO(expenseHeaderJPA.getEmployeeJPA()));
						}
						advanceDTOList.add(advDTO);
					}
					baseDTO.setServiceStatus(ServiceStatus.SUCCESS);
				}
			}
			else{
				baseDTO.setServiceStatus(ServiceStatus.BUSINESS_VALIDATION_FAILURE);
			}
		}
		catch(Exception e){
			baseDTO.setServiceStatus(ServiceStatus.SYSTEM_FAILURE);
			logger.error("AdvanceService: Exception",e);
		}
		logger.debug("AdvanceService: getAdvanceToBeApprove-End");
		return  advanceDTOList;
	}


	@Override
	public BaseDTO approveRejectAdvance(BaseDTO baseDTO) {
		logger.debug("AdvanceService: approveRejectAdvance-Start");
		validateAdvanceDTO(baseDTO);
		
		if (Validation.validateForNullObject(baseDTO)) {
			AdvanceDTO advanceDTO=(AdvanceDTO) baseDTO;;
			AdvanceJPA advanceJPA =advanceDAO.approveRejectAdvance(advanceDTO);
			Integer statusID= advanceJPA.getProcessInstanceJPA().getVoucherStatusJPA().getVoucherStatusId();
			
			VoucherStatusJPA voucherStatusJPA= new VoucherStatusJPA();
			
			if(Validation.validateForNullObject(advanceJPA)){
				if(advanceDTO.getVoucherStatusId() == 3){// Approved
					// Set Voucher Status in ExpenseHEader
					voucherStatusJPA.setVoucherStatusId(statusID+2);
					advanceJPA.setVoucherStatusJPA(voucherStatusJPA);
					advanceDAO.updateProcessInstance(advanceJPA,advanceJPA.getProcessInstanceJPA().getVoucherStatusJPA().getVoucherStatusId(),advanceDTO.getApprovedByEmployeeDTO());
					
				}
				else if(advanceDTO.getVoucherStatusId() == 4){//Rejected
					voucherStatusJPA.setVoucherStatusId(statusID+3);
					advanceJPA.setVoucherStatusJPA(voucherStatusJPA);
					
					AdvanceProcessInstanceJPA processInstanceJPA = advanceJPA.getProcessInstanceJPA();
					if(! Validation.validateForNullObject(processInstanceJPA)){
						processInstanceJPA= new AdvanceProcessInstanceJPA();
					}
					
					EmployeeJPA pendingAt = new EmployeeJPA();
					pendingAt.setEmployeeId(advanceJPA.getEmployeeJPA().getEmployeeId());
					processInstanceJPA.setPendingAt(pendingAt);
					
					EmployeeJPA approveBy = new EmployeeJPA();
					approveBy.setEmployeeId(advanceDTO.getApprovedByEmployeeDTO().getEmployeeId());
					processInstanceJPA.setPendingAt(approveBy);
					
					VoucherStatusJPA voucherStatus = new VoucherStatusJPA();
					voucherStatus.setVoucherStatusId(1);
					processInstanceJPA.setVoucherStatusJPA(voucherStatus);
					
					processInstanceJPA.setAdvanceJPA(advanceJPA);
					advanceJPA.setProcessInstanceJPA(processInstanceJPA);
				}
				
				// Set Process History
				AdvanceProcessHistoryJPA processHistoryJPA = new AdvanceProcessHistoryJPA();
				processHistoryJPA.setVoucherStatusJPA(voucherStatusJPA);
				processHistoryJPA.setAdvanceJPA(advanceJPA);
				List<AdvanceProcessHistoryJPA> processHistoryJPAList= new ArrayList<AdvanceProcessHistoryJPA>();
				processHistoryJPAList.add(processHistoryJPA);
				advanceJPA.setProcessHistoryJPA(processHistoryJPAList);
				
				
				baseDTO=AdvanceConvertor.setAdvanceJPAtoDTO(advanceJPA);
				baseDTO.setServiceStatus(ServiceStatus.SUCCESS);
			}
		}
		else{
			baseDTO.setServiceStatus(ServiceStatus.BUSINESS_VALIDATION_FAILURE);
		}
		logger.debug("AdvanceService: approveRejectAdvance-End");
		return  baseDTO;
	}
}
