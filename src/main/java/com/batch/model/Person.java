package com.batch.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Person {
	@Id
	public int id;
	public String name;
	public String city;
	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", city=" + city + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Person(int id, String name, String city) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
	}
	public Person() {
		super();
	}

}
