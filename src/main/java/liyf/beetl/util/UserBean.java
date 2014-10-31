package liyf.beetl.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserBean {
	private String name;
	private String[] loves;
	private Short age;
	private Map<String, Integer> map;
	private List<String> list;
	private Date date;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Short getAge() {
		return age;
	}
	public void setAge(Short age) {
		this.age = age;
	}
	public String[] getLoves() {
		return loves;
	}
	public void setLoves(String[] loves) {
		this.loves = loves;
	}
	public Map<String, Integer> getMap() {
		return map;
	}
	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
