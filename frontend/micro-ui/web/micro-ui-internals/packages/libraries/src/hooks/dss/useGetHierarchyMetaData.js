import { useQuery } from "react-query";
import { DSSService } from "../../services/elements/DSS";

const useGetHierarchyMetaData = (tenantId, departmentId, config={}) => {
  let data = {
    "requestHeader": {
        "ts": 1627193067,
        "version": "2.0.0",
        "msgId": "Unknown",
        "signature": "NON",
        "userInfo": {
            "uuid": "admin"
        }
    },
    "criteria": {
        tenantId,
        departmentId
    }
  }
  // return DSSService.getDepartments(data);
  return useQuery(["DSS_HIERARCHY_METADATA", tenantId, departmentId], () => DSSService.getHierarchyMetaData(data), config);
};

export default useGetHierarchyMetaData;
