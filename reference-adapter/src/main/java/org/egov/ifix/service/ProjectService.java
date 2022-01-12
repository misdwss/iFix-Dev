package org.egov.ifix.service;

import com.google.gson.JsonObject;

public interface ProjectService {
    String getProjectId(String projectCode, JsonObject jsonObjectData);
}
