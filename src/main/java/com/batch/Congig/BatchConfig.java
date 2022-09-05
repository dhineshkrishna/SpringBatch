package com.batch.Congig;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.batch.model.Person;
import com.batch.repo.PersonRepo;
import com.batch.step.Processor;
@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	PersonRepo personRepo;
//	
//	@Bean
//	public Job processJob(JobBuilderFactory builderFactory,StepBuilderFactory builderFactory2, ItemReader<Person> itemReader, ItemProcessor<Person, Person> itemProcessor,ItemWriter<Person> itemWriter) {
//
//		Step step= stepBuilderFactory.get("ETL-file-load").<Person, Person>chunk(100)
//				.reader(itemReader).processor(itemProcessor).writer(itemWriter).build();
//
//		return jobBuilderFactory.get("ETL-Load")
//				.incrementer(new RunIdIncrementer()).start(step)
//				.build();
//	}

	@Bean
	public FlatFileItemReader<Person> fileItemReader(){
		FlatFileItemReader<Person> fileItemReader=new FlatFileItemReader<>();
		fileItemReader.setResource(new FileSystemResource("src/main/resources/person.csv"));
		fileItemReader.setName("CSV_Reader");
		fileItemReader.setLinesToSkip(1);
		fileItemReader.setLineMapper(lineMapper ());
		return fileItemReader;
	}

	@Bean
	public LineMapper<Person> lineMapper(){
		DefaultLineMapper<Person> defaultLineMapper=new DefaultLineMapper<>();
		DelimitedLineTokenizer delimitedLineTokenizer=new DelimitedLineTokenizer();
		delimitedLineTokenizer.setDelimiter(",");
		delimitedLineTokenizer.setStrict(false);
		delimitedLineTokenizer.setNames("id","name","city");
//		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

		BeanWrapperFieldSetMapper<Person> beanWrapperFieldSetMapper=new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(Person.class);

		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		return defaultLineMapper;
	}
	@Bean
	JdbcBatchItemWriter<Person> batchItemWriter(DataSource ds){
		return new JdbcBatchItemWriterBuilder<Person>().dataSource(ds).sql("insert into person(id, name, city) values(:id, :name, :city)").beanMapped().build();
	}
	@Bean
	public Processor customer() {
		return new Processor();
	}
	
	@Bean
	public RepositoryItemWriter<Person> itemWriter(){
		RepositoryItemWriter<Person> write=new RepositoryItemWriter<>(); 
        write.setRepository(personRepo);
        write.setMethodName("save");
		return write;
	}
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("csv-step").<Person,Person>chunk(10).reader(fileItemReader()).processor(customer()).writer(itemWriter()).taskExecutor(taskExecutor()).build();
		
	}
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(10);
		return asyncTaskExecutor;
	}
	@Bean
	public Job runJob() {
		return jobBuilderFactory.get("importperson").flow(step1()).end().build();
	}

}