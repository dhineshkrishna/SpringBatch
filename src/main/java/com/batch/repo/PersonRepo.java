package com.batch.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batch.model.Person;
@Repository
public interface PersonRepo extends JpaRepository<Person, Integer>{

	void save(List<? extends Person> person);

}
