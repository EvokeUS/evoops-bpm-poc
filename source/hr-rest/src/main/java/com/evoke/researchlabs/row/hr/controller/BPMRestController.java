package com.evoke.researchlabs.row.hr.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.evoke.researchlabs.row.hr.domain.BPMConfiguration;
import com.evoke.researchlabs.row.hr.util.BPMUtil;

@RestController
@RequestMapping("/bpm")
public class BPMRestController {
	private Logger logger = Logger.getLogger(BPMRestController.class); 
	
	@RequestMapping(value = "/verify" , method = RequestMethod.POST)
	public Boolean login(@RequestBody BPMConfiguration bpmConfiguration, UriComponentsBuilder ucBuilder) {
		logger.info(bpmConfiguration);
		return BPMUtil.checkConnection(bpmConfiguration);
	}
	
	@RequestMapping(value = "/installOrganization" , method = RequestMethod.POST)
	public Boolean installOrganization(@RequestBody BPMConfiguration bpmConfiguration, UriComponentsBuilder ucBuilder) {
		logger.info(bpmConfiguration);
		return BPMUtil.importOrganization(bpmConfiguration);
	}
	
	@RequestMapping(value = "/updateProfile" , method = RequestMethod.POST)
	public Boolean updateProfile(@RequestBody BPMConfiguration bpmConfiguration, UriComponentsBuilder ucBuilder) {
		logger.info(bpmConfiguration);
		return BPMUtil.updateProfile(bpmConfiguration);
	}
	
	@RequestMapping(value = "/installBDM" , method = RequestMethod.POST)
	public Boolean installBDM(@RequestBody BPMConfiguration bpmConfiguration, UriComponentsBuilder ucBuilder) {
		logger.info(bpmConfiguration);
		return BPMUtil.installBDM(bpmConfiguration);
	}
	
	@RequestMapping(value = "/installProcess" , method = RequestMethod.POST)
	public Boolean installProcess(@RequestBody BPMConfiguration bpmConfiguration, UriComponentsBuilder ucBuilder) {
		logger.info(bpmConfiguration);
		return BPMUtil.installProcess(bpmConfiguration);
	}
	
	@RequestMapping(value = "/setup" , method = RequestMethod.POST)
	public Boolean setupBonitaEnvironment(@RequestBody BPMConfiguration bpmConfiguration, UriComponentsBuilder ucBuilder) {
		logger.info(bpmConfiguration);
		return BPMUtil.setupBonitaEnvironment(bpmConfiguration);
	}
}
