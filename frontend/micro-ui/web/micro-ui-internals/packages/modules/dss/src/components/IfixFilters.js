import { CloseSvg, FilterIcon, MultiSelectDropdown, RefreshIcon, Dropdown } from "@egovernments/digit-ui-react-components";
import React, { useContext, useEffect, useMemo, useState } from "react";
import DateRange from "./DateRange";
import FilterContext from "./FilterContext";
import Switch from "./Switch";

const IfixFilters = ({
  t,
  services,
  isOpen,
  closeFilters,
  departments,
  hierarchyLevels,
  hierarchyList,
  showDateRange = true,
  showDenomination = true,
  showModuleFilter = true,
  changeDepartment,
}) => {
  const { value, setValue } = useContext(FilterContext);
  const [selectedDept, setSelectedDept] = useState({})
  const [hierarchyLevelMapList, setHierarchyLevelMapList] = useState([]);
  const [hierarchyListMap, setHierarchyListMap] = useState({});
  const [selected, setSelected] = useState({});
  
  const customStyle = {
    filterInput: {
      marginLeft: '0%',
      marginRight: '2%'
    },
    filterInputLast: {
      marginLeft: '0%',
      flexBasis: "16%"
    },
  };

  useEffect(() => {
    if (departments && departments.length) {
      setSelectedDept(departments[0])
      setValue({ ...value, filters: {'Department':  departments[0]?.code}});
    }
    if (departments && departments.length && hierarchyLevels && hierarchyLevels.length && hierarchyList && hierarchyList.length) {
      let hierarchyLevelMap = {};
      let hierarchyKeyMap = {};
      let hierarchyListMapById = {}
      hierarchyLevels.forEach((hLevel) => {
          if (hLevel.level) {
            hLevel['hierarchies'] = []
            hLevel['filterIds'] = [];
            hLevel['selectedCodes'] = [];
            hierarchyLevelMap[hLevel.level] = hLevel
          }
        });
        hierarchyList.forEach((data, idx) => {
          // create hierarchy list's map by id
          hierarchyListMapById[data?.id] = data;
          if (data.hierarchyLevel && hierarchyLevelMap[data.hierarchyLevel])
            hierarchyLevelMap[data.hierarchyLevel]['hierarchies'].push(data)
        })
        hierarchyLevelMap = Object.values(hierarchyLevelMap);
        hierarchyLevelMap = hierarchyLevelMap.sort((x, y) => x?.hierarchyLevel?.localeCompare(y?.hierarchyLevel))
        setHierarchyLevelMapList(hierarchyLevelMap);
        setSelected(hierarchyKeyMap);
        setHierarchyListMap(hierarchyListMapById);
    }
  }, [departments, hierarchyLevels, hierarchyList])


  const [selectService, setSelectedService] = useState(() =>
    services?.filter((module) => value?.moduleLevel === module?.code)
  )

  useEffect(() => {
    setSelectedService(services?.filter((module) => value?.moduleLevel === module?.code));
  }, [value?.moduleLevel]);

  const handleFilterChange = (data) => {
    setValue({ ...value, ...data });
  };

  const selectServicesFilters = (e, data) => {
    setValue({ ...value, moduleLevel: e?.code });
  };

  const selectDepartmentFilters = (e, data) => {
    setHierarchyLevelMapList([])
    setSelectedDept(e);
    changeDepartment(e);
    setValue({ ...value, filters: {'Department':  e?.code}});
  };

  const selectHierarchyFilter = (hierarchyLevel) => {
    return (e, data) => {
      if ((hierarchyLevel?.selectedCodes?.length && !e.length) || e.length) {
        changeHierarchyAndFilterChild(hierarchyLevel, e, data)
      }
    };
  }  

  const changeHierarchyAndFilterChild = (hierarchyLevel, e, data) => {
    // const hierarchyLevel = hierarchyLevels.filter((hl) => {return hl.level === e?.[0]?.[1].hierarchyLevel;})[0];
    if (hierarchyLevel) {
      let key = hierarchyLevel['label'];
      let hierarchyFilter = {};
      hierarchyFilter[key] = []
      let selectedHierarchies = [] 
      e.forEach((appHierarchy) => { selectedHierarchies.push(appHierarchy[1]); hierarchyFilter[key].push(appHierarchy[1].code) })
      // check is any change on filter
      const isChanged = isHierarchyFilterChanged(selectedHierarchies, selected[key])
      if (!isChanged) return;
      // If change in filter update the filters
      setValue({ ...value, filters: { ...hierarchyFilter, 'Department':  selectedDept?.code } });
      let childHierarchyIdList = {};
      let selectedDD = selected; 
      // reset all chields
      let hierarchyLevelMapUpdated = hierarchyLevelMapList.map((hierarchy, idx) => {
        if (hierarchy.level == hierarchyLevel.level) {
          selectedDD[hierarchy.label] = selectedHierarchies;
          let nextLevel = hierarchy.level + 1;
          childHierarchyIdList[nextLevel] = []
          hierarchy['selectedCodes'] = selectedHierarchies.map((h) => {
            if (h.children) childHierarchyIdList[nextLevel] = childHierarchyIdList[nextLevel].concat(h.children);
            return h.code;
          })
        } else if (hierarchy.level > hierarchyLevel.level && childHierarchyIdList[hierarchy.level]) {
          // Set filterIds list 
          hierarchy['filterIds'] = childHierarchyIdList[hierarchy.level];
          selectedDD[hierarchy.label] = [];
          let nextLevel = hierarchy.level + 1;
          childHierarchyIdList[nextLevel] = [];
          childHierarchyIdList[hierarchy.level].forEach(id => {
            // Prepare child list for next level
            if (hierarchyListMap[id] && hierarchyListMap[id]?.children) childHierarchyIdList[nextLevel] = childHierarchyIdList[nextLevel].concat(hierarchyListMap[id].children);
          });
        }
        return hierarchy;
      })
      setHierarchyLevelMapList(hierarchyLevelMapUpdated);
      setSelected(selectedDD);
    }
  };

  const isHierarchyFilterChanged = (selectedList, prevSelectedList) => {
    if (selectedList && prevSelectedList) {
      let selectedIds = selectedList.map(a => a.id);
      let prevSelectedIds = prevSelectedList.map(a => a.id);
      if (selectedIds.sort().join(',') === prevSelectedIds.sort().join(',')) {
        return false;
      } else {
        return true;
      }
    }
    return true;
  }

  const getFilteredHierarchy = (hierarchyLevel) => {
    if (hierarchyLevel["filterIds"].length) {
      return hierarchyLevel["hierarchies"].filter((hierarchy) => hierarchyLevel["filterIds"].indexOf(hierarchy.id) != -1)
    }
    return hierarchyLevel["hierarchies"];
  }

  const handleClear = () => {
    setValue({
      denomination: "Unit",
      range: Digit.Utils.dss.getInitialRange(),
    });
  };
  return (
    <div className={`filters-wrapper ${isOpen ? "filters-modal" : ""}`} style={{
      justifyContent: window.location.href.includes("dss/dashboard/finance") && !isOpen ? "space-between" : "unset",
      paddingRight: window.location.href.includes("dss/dashboard/finance") && !isOpen ? "24px" : "0px",
      paddingBottom: window.location.href.includes("dss/dashboard/finance") && !isOpen ? "20px" : "unset",
      flexWrap: "wrap"
    }}>
      <span className="filter-close" onClick={() => closeFilters()}>
        <CloseSvg />
      </span>
      {isOpen && (
        <div className="filter-header">
          <FilterIcon />
          <p>{t(`DSS_FILTERS`)}</p>
          <span onClick={handleClear}>
            <RefreshIcon />
          </span>
        </div>
      )}
      {showDateRange && (
        <div className="filters-input" style={customStyle.filterInput}>
          <DateRange onFilterChange={handleFilterChange} values={value?.range} t={t} />
        </div>
      )}
      <div className={"filters-input " + (departments && departments.length>1 ? "":"display-none")} style={customStyle.filterInput}>
        <div className="mbsm">{t("ES_DSS_IFIX_DEPARTMENTS")}</div>
        <Dropdown
          option={departments}
          optionKey="name"
          select={selectDepartmentFilters}
          selected={selectedDept}
          placeholder={t("ES_DSS_ALL_SERVICES_SELECTED")}
        />
      </div>

      {hierarchyLevelMapList && hierarchyLevelMapList.map((hierarchyLevel) => {
            return <div className="filters-input" style={customStyle.filterInput} key={hierarchyLevel?.level}>
              <div className="mbsm">{t("ES_DSS_IFIX_"+hierarchyLevel?.label)}</div>
              <MultiSelectDropdown
                options={getFilteredHierarchy(hierarchyLevel)}
                optionsKey="name"
                onSelect={selectHierarchyFilter(hierarchyLevel)}
                selected={selected[hierarchyLevel.label]}
                defaultLabel={t("ES_DSS_IFIX_ALL_"+ hierarchyLevel?.label)}
                defaultUnit={t("ES_DSS_IFIX_"+hierarchyLevel?.label)}
              />
            </div>
        })
      }

      {showModuleFilter && (
        <div className="filters-input" style={customStyle.filterInput}>
          <div className="mbsm">{t("ES_DSS_SERVICES")}</div>
          <Dropdown
            option={services}
            optionKey="name"
            select={selectServicesFilters}
            selected={selectService}
            placeholder={t("ES_DSS_ALL_SERVICES_SELECTED")}
          />
        </div>
      )}
      {showDenomination && (
        <div className="filters-input" style={customStyle.filterInputLast}>
          <Switch onSelect={handleFilterChange} t={t} />
        </div>
      )}
    </div>
  );
};

export default IfixFilters;