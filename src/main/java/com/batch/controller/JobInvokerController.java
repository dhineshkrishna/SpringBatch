package com.batch.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobInvokerController {
 
    @Autowired
    JobLauncher jobLauncher;
 
    @Autowired
    Job processJob;
 
    @GetMapping("/invokejob")
    public BatchStatus handle() throws Exception {
 
           Map<String, JobParameter> maps=new HashMap<>();
           maps.put("time", new JobParameter(System.currentTimeMillis()));
           JobParameters jobParameters=new JobParameters(maps);
           JobExecution jobExecution = jobLauncher.run(processJob, jobParameters);
       while(jobExecution.isRunning()) {
    	   System.out.println(".....");
       }
           return jobExecution.getStatus();
    }
}