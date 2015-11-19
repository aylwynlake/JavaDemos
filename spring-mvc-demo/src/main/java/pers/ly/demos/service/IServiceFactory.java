package pers.ly.demos.service;

public interface IServiceFactory {
	IService getService(String serviceName);
}
