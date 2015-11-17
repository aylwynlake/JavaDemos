package pers.ly.demos.junit.spring;

import org.springframework.stereotype.Component;

@Component
public class ServiceImp implements IService {

	public String getMessage() {
		return "Message Service Done!";
	}

}
