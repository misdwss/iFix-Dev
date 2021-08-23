package org.egov.ifix.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@Getter
@Setter
@ToString
public class ApplicationConfiguration {
	
	@Value("${kafka.topics.ifix.adaptor.mapper}")
	private String mapperTopicName;
	
	@Value("${state.goverment.code}")
	private String tenantId;

}
