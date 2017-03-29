package com.chaitanya.web.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.chaitanya.expense.model.ExpenseDetailDTO;
import com.chaitanya.expense.model.ExpenseHeaderDTO;
import com.chaitanya.expense.service.IExpenseService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ExpenseController {
	
	@Autowired
	@Qualifier("expenseService")
	private IExpenseService expenseService;
	
	@RequestMapping(value="/expense",method=RequestMethod.GET)
	public ModelAndView createExpense() throws JsonGenerationException, JsonMappingException, IOException{
		ModelAndView model=new ModelAndView();
		model.addObject("ExpenseHeaderDTO", new ExpenseHeaderDTO());
		model.setViewName("expense/expenseJSP");
		return model;
	}
	
	
	@RequestMapping(value="/saveExpense",method=RequestMethod.POST)
	public String saveExpense(@ModelAttribute("ExpenseHeaderDTO") ExpenseHeaderDTO expenseHeaderDTO,@RequestParam("file") List<MultipartFile> file, String data,HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper= new ObjectMapper();
		try{
			String addList=mapper.readTree(data).get("addList").toString();
			List<ExpenseDetailDTO> addedExpenseDetailDTOList =
				    mapper.readValue(addList, new TypeReference<List<ExpenseDetailDTO>>(){});
			for(int i=0; i< addedExpenseDetailDTOList.size(); i++){
				ExpenseDetailDTO expenseDetailDTO=addedExpenseDetailDTOList.get(i);
				MultipartFile receipt=file.get(i);
				expenseDetailDTO.setReceipt(receipt);
			}
			expenseHeaderDTO.setAddedExpenseDetailsDTOList(addedExpenseDetailDTOList);
			expenseService.addExpense(expenseHeaderDTO);
		}
		catch(Exception e){
			System.out.println(e);
		}
		return "expense/expenseJSP";
	}
	
}