package pers.ly.demos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class DynamicInjectService {
	private IServiceFactory factory;

	public DynamicInjectService(IServiceFactory factory) {
		this.factory = factory;
	}
	
	public void serve(String serviceName) {
		IService service = factory.getService(serviceName);
		service.serve();
	}
}
