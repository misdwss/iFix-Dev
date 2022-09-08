import { useQuery } from "react-query";
import { DSSService } from "../../services/elements/DSS";

const useGetHierarchy = (tenantId, departmentId, config={}) => {
  let data = {
        "requestHeader": {
         "ts": 1627193067,
         "version": "2.0.0",
         "msgId": "Unknown",
         "signature": "NON"
       },
       "criteria": {
         tenantId,
         departmentId
       }
     }
  return useQuery(["DSS_HIERARCHY_LIST", tenantId, departmentId], () => DSSService.getHierarchyData(data), config);
};

export default useGetHierarchy;
