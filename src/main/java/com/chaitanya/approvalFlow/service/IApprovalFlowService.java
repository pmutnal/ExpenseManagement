package com.chaitanya.approvalFlow.service;

import java.util.List;

import com.chaitanya.approvalFlow.model.ApprovalFlowDTO;
import com.chaitanya.base.BaseDTO;

public interface IApprovalFlowService {

	List<ApprovalFlowDTO> findFunctionalFlowUnderBranch(BaseDTO baseDTO);

	BaseDTO deactivateFunctionalFlow(BaseDTO baseDTO);

	BaseDTO addFunctionalFlow(BaseDTO baseDTO);

	List<ApprovalFlowDTO> findFinanceFlowUnderBranch(BaseDTO baseDTO);

	List<ApprovalFlowDTO> findBranchFlowUnderBranch(BaseDTO baseDTO);
}
