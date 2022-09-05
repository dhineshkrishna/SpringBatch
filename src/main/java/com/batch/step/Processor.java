package com.batch.step;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.batch.model.Person;
@Component
public class Processor implements ItemProcessor<Person, Person>{
@Override
	public Person process(Person person) {
		return person;
	}
}
