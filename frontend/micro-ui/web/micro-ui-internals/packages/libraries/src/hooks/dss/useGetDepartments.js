import { useQuery } from "react-query";
import { DSSService } from "../../services/elements/DSS";

const useGetDepartments = (tenantId, config = {}) => {
  let data = {
    "requestHeader": {
      "ts": 1627193067,
      "version": "2.0.0",
      "msgId": "Unknown",
      "signature": "NON"
    },
    "criteria": {
      tenantId
    }
  }

  return useQuery(["DSS_HIERARCHY_list", tenantId], () => DSSService.getDepartments(data), config);

};

export default useGetDepartments;
