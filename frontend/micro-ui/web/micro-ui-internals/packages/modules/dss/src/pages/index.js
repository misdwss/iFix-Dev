import {
  DownloadIcon,
  EmailIcon,
  FilterIcon,
  Header,
  Loader,
  MultiLink,
  RemoveableTag,
  ShareIcon,
  WhatsappIcon,
} from "@egovernments/digit-ui-react-components";
import { format } from "date-fns";
import React, { useEffect, Fragment, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { checkCurrentScreen } from "../components/DSSCard";
import FilterContext from "../components/FilterContext";
import Filters from "../components/Filters";
import FiltersNational from "../components/FiltersNational";
import IfixFilters from "../components/IfixFilters";
import Layout from "../components/Layout";

const key = "DSS_FILTERS";

const getInitialRange = () => {
  const data = Digit.SessionStorage.get(key);
  const startDate = data?.range?.startDate ? new Date(data?.range?.startDate) : Digit.Utils.dss.getDefaultFinacialYear().startDate;
  const endDate = data?.range?.endDate ? new Date(data?.range?.endDate) : Digit.Utils.dss.getDefaultFinacialYear().endDate;
  const title = `${format(startDate, "MMM d, yyyy")} - ${format(endDate, "MMM d, yyyy")}`;
  const interval = Digit.Utils.dss.getDuration(startDate, endDate);
  const denomination = data?.denomination || "Lac";
  const tenantId = data?.filters?.tenantId || [];
  const moduleLevel = data?.moduleLevel || "";
  const preFilters = data?.filters || {};
  return { startDate, endDate, title, interval, denomination, tenantId, moduleLevel, preFilters };
};

const DashBoard = ({ stateCode }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const [filters, setFilters] = useState(() => {
    const { startDate, endDate, title, interval, denomination, tenantId, moduleLevel, preFilters } = getInitialRange();
    return {
      denomination,
      range: { startDate, endDate, title, interval },
      requestDate: {
        startDate: startDate.getTime(),
        endDate: endDate.getTime(),
        interval: interval,
        title: title,
      },
      filters: {
        ...preFilters,
        tenantId
      },
      moduleLevel: moduleLevel,
    };
  });
  const [isFilterModalOpen, setIsFilterModalOpen] = useState(false);
  const isNational = checkCurrentScreen();
  const { moduleCode } = useParams();
  const [department, setDepartment] = useState({});

  const language = Digit.StoreData.getCurrentLanguage();

  // For updating the ifix dashboard on title
  useEffect(() => {
    document.title = "iFIX Dashboard"
  }, []);

  const { isLoading: localizationLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });
  const { data: screenConfig, isLoading: isServicesLoading } = Digit.Hooks.dss.useMDMS(stateCode, "dss-dashboard", "DssDashboard", {
    select: (data) => {
      let screenConfig = data?.["dss-dashboard"]["dashboard-config"][0].MODULE_LEVEL;
      let reduced_array = [];
      for (let i = 0; i < screenConfig.length; i++) {
        if (screenConfig[i].dashboard !== null) {
          reduced_array.push(screenConfig[i]);
        }
      }

      const serviceJS = reduced_array.map((obj, idx) => {
        return {
          code: obj[Object.keys(obj)[0]].filterKey,
          name: Digit.Utils.locale.getTransformedLocale(`DSS_${obj[Object.keys(obj)[0]].services_name}`),
        };
      });
      return serviceJS;
    },
  });
  const { data: nationalInfo, isLoadingNAT } = Digit.Hooks.dss.useMDMS(stateCode, "tenant", ["nationalInfo"], {
    select: (data) => {
      let nationalInfo = data?.tenant?.nationalInfo || [];
      let combinedResult = nationalInfo.reduce((acc, curr) => {
        if (acc[curr.stateCode]) {
          acc[curr.stateCode].push(curr);
        } else {
          acc[curr.stateCode] = [curr];
        }
        return { ...acc };
      }, {});
      let formattedResponse = { ddr: [], ulb: [] };
      Object.keys(combinedResult).map((key) => {
        let stateName = combinedResult[key]?.[0].stateName;
        formattedResponse.ddr.push({ code: key, ddrKey: stateName, ulbKey: stateName });
        formattedResponse.ulb.push(...combinedResult[key].map((e) => ({ code: e.code, ulbKey: e.name, ddrKey: e.stateName })));
      });
      return formattedResponse;
    },
    enabled: isNational,
  });

  const { data: response, isLoading } = Digit.Hooks.dss.useDashboardConfig(moduleCode);
  const { data: ulbTenants, isLoading: isUlbLoading } = Digit.Hooks.useModuleTenants("DSS");
  const [showOptions, setShowOptions] = useState(false);
  const [showFilters, setShowFilters] = useState(false);
  const [tabState, setTabState] = useState("");

  const handleFilters = (data) => {
    Digit.SessionStorage.set(key, data);
    setFilters(data);
  };
  const fullPageRef = useRef();
  const provided = useMemo(
    () => ({
      value: filters,
      setValue: handleFilters,
      ulbTenants: isNational ? nationalInfo : ulbTenants,
      screenConfig: screenConfig,
    }),
    [filters, isUlbLoading, isServicesLoading]
  );

  const mobileView = window.Digit.Utils.browser.isMobile();

  const handlePrint = () => Digit.Download.PDF(fullPageRef, t(dashboardConfig?.[0]?.name));

  const removeULB = (id) => {
    handleFilters({
      ...filters,
      filters: { ...filters?.filters, tenantId: [...filters?.filters?.tenantId].filter((tenant, index) => index !== id) },
    });
  };
  const removeST = (id) => {
    let newStates = [...filters?.filters?.state].filter((tenant, index) => index !== id);
    let newUlbs = filters?.filters?.ulb || [];
    if (newStates?.length == 0) {
      newUlbs = [];
    } else {
      let filteredUlbs = nationalInfo?.ulb?.filter((e) => Digit.Utils.dss.getCitiesAvailable(e, newStates))?.map((ulbs) => ulbs?.code);
      newUlbs = newUlbs.filter((ulb) => filteredUlbs.includes(ulb));
    }
    handleFilters({
      ...filters,
      filters: { ...filters?.filters, state: newStates, ulb: newUlbs },
    });
  };

  const removeService = () => {
    handleFilters({
      ...filters,
      moduleLevel: "",
    });
  };

  const removeTenant = (id) => {
    handleFilters({
      ...filters,
      filters: { ...filters?.filters, ulb: [...filters?.filters?.ulb].filter((tenant, index) => index !== id) },
    });
  };

  const handleClear = () => {
    handleFilters({ ...filters, filters: { ...filters?.filters, tenantId: [] } });
  };

  const clearAllTn = () => {
    handleFilters({ ...filters, filters: { ...filters?.filters, ulb: [] } });
  };
  const clearAllSt = () => {
    handleFilters({ ...filters, filters: { ...filters?.filters, state: [], ulb: [] } });
  };
  const clearAllServices = () => {
    handleFilters({ ...filters, moduleLevel: "" });
  };

  const removeHierarchy = (id) => {
    let elem = appliedHierarchyChips[id];
    let appliedFilters= filters?.filters;
    hierarchyLevels.forEach((hLevel) => {
      if (hLevel.level == elem.level) appliedFilters[elem.label] = appliedFilters[elem.label].filter((el) => el !== elem.code)
      else if (hLevel.level > elem.level) delete appliedFilters[hLevel.label];
    })
    if (!appliedFilters[elem.label]?.length) delete appliedFilters[elem.label];
    handleFilters({...filters, filters: { ...appliedFilters }});
  };

  const clearAllHierarchy = () => {
    let appliedFilters= filters?.filters;
    // Remove all filters from hierarchy
    hierarchyLevels.forEach((hLevel) => {
      if (hLevel.level) delete appliedFilters[hLevel.label];
    })
    handleFilters({...filters, filters: { ...appliedFilters }});
  };

  const dashboardConfig = response?.responseData;
  let tabArrayObj =
    dashboardConfig?.[0]?.visualizations?.reduce((curr, acc) => {
      curr[acc.name] = 0;
      return { ...curr };
    }, {}) || {};
  let tabArray = Object.keys(tabArrayObj).map((key) => key);

  useEffect(() => {
    if (tabArray?.length > 0 && tabState == "") {
      setTabState(tabArray[0]);
    }
  }, [tabArray]);

  let isEnableIFixFilter = !isLoading && Digit.Utils.dss.isIFixDashboard(dashboardConfig);
  const { data: departments, isLoading: isDeptLoading } = Digit.Hooks.dss.useGetDepartments(stateCode, {
    enabled: isEnableIFixFilter,
    select: (data) => {
      if (data.department) {
        // let deptList = data.department.filter((dept) => { if (!dept.parent) return dept; });
        let deptList = data.department;
        if (filters?.filters?.['Department']) {
          let idx = deptList.findIndex(p => p.code==filters?.filters?.['Department']);
          setDepartment(deptList[idx]);
        } else  {
          setDepartment(deptList[0]);
          handleFilters({
            ...filters,
            filters: { ...filters?.filters, 'Department': deptList?.[0]?.code },
          })
        }
        return deptList;
      };
      return [];
    },
  });

  const { data: hierarchyLevels, isLoading: isHierarchyMetaLoading } =  Digit.Hooks.dss.useGetHierarchyMetaData(stateCode, department?.id, {
    enabled: isEnableIFixFilter && !isDeptLoading && department?.id ? true : false,
    select: (data) => {
      if (data.departmentHierarchyLevel) {
        return _.sortBy(data.departmentHierarchyLevel, 'level')
      };
      return [];
    },
  });
  
  const { data: hierarchyList, isLoading: isHierarchyLoading } = Digit.Hooks.dss.useGetHierarchy(stateCode, department?.id, {
    enabled: isEnableIFixFilter && !isDeptLoading && department?.id ? true : false,
    select: (data) => {
      if (data.departmentEntity) {
        let idValueMap = {};
        // This is for demo only to represent the data of table with text.
        data.departmentEntity.forEach((hierarchy) => idValueMap[hierarchy.code] = hierarchy.name)
        localStorage.setItem("Digit.dss.iFixCodeNameMap", JSON.stringify(idValueMap));
        return _.sortBy(data.departmentEntity, 'hierarchyLevel')
      };
      return [];
    },
  });

  const changeDepartment = (e) => {
    setDepartment(e);
  }

  const [appliedHierarchyChips, setAppliedHierarchyChips] = useState([]);
  useEffect(() => {
    if (filters?.filters && hierarchyLevels?.length && hierarchyList?.length) {
      let filtersChips = [];
      hierarchyLevels.forEach((hierarchy) => {
        let appliedFilterCodes = filters.filters?.[hierarchy.label];
        if (hierarchy.level && appliedFilterCodes) {
          let filteredHierarchies = _.filter(hierarchyList, (hierarchyVal) => {return appliedFilterCodes.indexOf(hierarchyVal.code) != -1 && hierarchyVal.hierarchyLevel == hierarchy.level})
          _.forEach(appliedFilterCodes, (code) => {
            filtersChips.push({
              id: hierarchy.id,
              level: hierarchy.level,
              label: hierarchy.label,
              code,
              name: _.result(_.find(filteredHierarchies, (obj) => { return obj.code === code; }), 'name') || code
            })
          })
        }
      })
      setAppliedHierarchyChips(filtersChips);
    } else {
      setAppliedHierarchyChips([])
    }
  }, [filters?.filters, hierarchyLevels, hierarchyList]);

  const shareOptions =
    // navigator.share
    //   ? [
    //       {
    //         label: t("ES_DSS_SHARE_PDF"),
    //         onClick: (e) => {
    //           setShowOptions(!showOptions);
    //           setTimeout(() => {
    //             return Digit.ShareFiles.DownloadImage(tenantId, fullPageRef, t(dashboardConfig?.[0]?.name));
    //           }, 500);
    //         },
    //       },
    //       {
    //         label: t("ES_DSS_SHARE_IMAGE"),
    //         onClick: () => {
    //           setShowOptions(!showOptions);
    //           setTimeout(() => {
    //             return Digit.ShareFiles.DownloadImage(tenantId, fullPageRef, t(dashboardConfig?.[0]?.name));
    //           }, 500);
    //         },
    //       },
    //     ]
    //   :
    [
      /*
        {
          icon: <EmailIcon />,
          label: t("ES_DSS_SHARE_PDF"),
          onClick: () => {
            setShowOptions(!showOptions);
            setTimeout(() => {
              return Digit.ShareFiles.PDF(tenantId, fullPageRef, t(dashboardConfig?.[0]?.name), "mail");
            }, 500);
          },
        },
        {
          icon: <WhatsappIcon />,
          label: t("ES_DSS_SHARE_PDF"),
          onClick: () => {
            setShowOptions(!showOptions);
            setTimeout(() => {
              return Digit.ShareFiles.PDF(tenantId, fullPageRef, t(dashboardConfig?.[0]?.name), "whatsapp");
            }, 500);
          },
        },
        */
      {
        icon: <EmailIcon />,
        label: t("ES_DSS_SHARE_IMAGE"),
        onClick: () => {
          setShowOptions(!showOptions);
          setTimeout(() => {
            return Digit.ShareFiles.DownloadImage(tenantId, fullPageRef, t(dashboardConfig?.[0]?.name), "mail");
          }, 500);
        },
      },
      {
        icon: <WhatsappIcon />,
        label: t("ES_DSS_SHARE_IMAGE"),
        onClick: () => {
          setShowOptions(!showOptions);
          setTimeout(() => {
            return Digit.ShareFiles.DownloadImage(tenantId, fullPageRef, t(dashboardConfig?.[0]?.name), "whatsapp");
          }, 500);
        },
      },
    ];
  if (isLoading || isUlbLoading || localizationLoading || isLoadingNAT || isServicesLoading || (isEnableIFixFilter && (isHierarchyLoading || isDeptLoading || isHierarchyMetaLoading))) {
    return <Loader />;
  }
  return (
    <FilterContext.Provider value={provided}>
      <div ref={fullPageRef} id="divToPrint">
        <div className="options">
          <Header styles={mobileView ? { marginLeft: "0px", whiteSpace: "pre-line" } : { marginBottom: "0px", whiteSpace: "pre" }}>
            {!isEnableIFixFilter && t(dashboardConfig?.[0]?.name)}
          </Header>
          {mobileView ? null : (
            <div className="divToBeHidden">
              {/* <div className="mrlg divToBeHidden">
                <MultiLink
                  className="multilink-block-wrapper divToBeHidden"
                  label={t(`ES_DSS_SHARE`)}
                  icon={<ShareIcon className="mrsm" />}
                  // showOptions={(e) => {
                  // setShowOptions(e)}
                  // }
                  onHeadClick={(e) => {
                    setShowOptions(!showOptions);
                  }}
                  displayOptions={showOptions}
                  options={shareOptions}
                />
              </div> */}
              <div className="mrsm divToBeHidden" onClick={handlePrint}>
                <DownloadIcon className="mrsm divToBeHidden" />
                {t(`ES_DSS_DOWNLOAD`)}
              </div>
            </div>
          )}
        </div>
        {isNational ? (
          <FiltersNational
            t={t}
            ulbTenants={nationalInfo}
            isOpen={isFilterModalOpen}
            closeFilters={() => setIsFilterModalOpen(false)}
            isNational={isNational}
          />
        ) : 
          isEnableIFixFilter ? (
            <IfixFilters
              t={t}
              showModuleFilter={false}
              services={screenConfig}
              departments={departments}
              hierarchyLevels={hierarchyLevels}
              hierarchyList={hierarchyList}
              isOpen={isFilterModalOpen}
              closeFilters={() => setIsFilterModalOpen(false)}
              showDateRange={true}
              changeDepartment={changeDepartment}
            />
          ) : (
            <Filters
              t={t}
              showModuleFilter={!isNational && dashboardConfig?.[0]?.name.includes("OVERVIEW") ? true : false}
              services={screenConfig}
              ulbTenants={isNational ? nationalInfo : ulbTenants}
              isOpen={isFilterModalOpen}
              closeFilters={() => setIsFilterModalOpen(false)}
              isNational={isNational}
              showDateRange={dashboardConfig?.[0]?.name.includes("DSS_FINANCE_DASHBOARD") ? false : true}
            />
          )
        }
        {filters?.filters?.tenantId?.length > 0 && (
          <div className="tag-container">
            {!showFilters &&
              filters?.filters?.tenantId &&
              filters.filters.tenantId
                .slice(0, 5)
                .map((filter, id) => <RemoveableTag key={id} text={`${t(`DSS_HEADER_ULB`)}: ${t(filter)}`} onClick={() => removeULB(id)} />)}
            {filters?.filters?.tenantId?.length > 6 && (
              <>
                {showFilters &&
                  filters.filters.tenantId.map((filter, id) => (
                    <RemoveableTag key={id} text={`${t(`DSS_HEADER_ULB`)}: ${t(filter)}`} onClick={() => removeULB(id)} />
                  ))}
                {!showFilters && (
                  <p className="clearText cursorPointer" onClick={() => setShowFilters(true)}>
                    {t(`DSS_FILTER_SHOWALL`)}
                  </p>
                )}
                {showFilters && (
                  <p className="clearText cursorPointer" onClick={() => setShowFilters(false)}>
                    {t(`DSS_FILTER_SHOWLESS`)}
                  </p>
                )}
              </>
            )}
            <p className="clearText cursorPointer" onClick={handleClear}>
              {t(`DSS_FILTER_CLEAR`)}
            </p>
          </div>
        )}
        {filters?.filters?.state?.length > 0 && (
          <div className="tag-container">
            {!showFilters &&
              filters?.filters?.state &&
              filters.filters.state
                .slice(0, 5)
                .map((filter, id) => (
                  <RemoveableTag
                    key={id}
                    text={`${t(`DSS_HEADER_STATE`)}: ${t(`DSS_TB_${Digit.Utils.locale.getTransformedLocale(filter)}`)}`}
                    onClick={() => removeST(id)}
                  />
                ))}
            {filters?.filters?.state?.length > 6 && (
              <>
                {showFilters &&
                  filters.filters.state.map((filter, id) => (
                    <RemoveableTag
                      key={id}
                      text={`${t(`DSS_HEADER_STATE`)}: ${t(`DSS_TB_${Digit.Utils.locale.getTransformedLocale(filter)}`)}`}
                      onClick={() => removeST(id)}
                    />
                  ))}
                {!showFilters && (
                  <p className="clearText cursorPointer" onClick={() => setShowFilters(true)}>
                    {t(`DSS_FILTER_SHOWALL`)}
                  </p>
                )}
                {showFilters && (
                  <p className="clearText cursorPointer" onClick={() => setShowFilters(false)}>
                    {t(`DSS_FILTER_SHOWLESS`)}
                  </p>
                )}
              </>
            )}
            <p className="clearText cursorPointer" onClick={clearAllSt}>
              {t(`DSS_FILTER_CLEAR_ST`)}
            </p>
          </div>
        )}
        {filters?.filters?.ulb?.length > 0 && (
          <div className="tag-container">
            {!showFilters &&
              filters?.filters?.ulb &&
              filters.filters.ulb
                .slice(0, 5)
                .map((filter, id) => (
                  <RemoveableTag
                    key={id}
                    text={`${t(`DSS_HEADER_ULB`)}: ${t(`DSS_TB_${Digit.Utils.locale.getTransformedLocale(filter)}`)}`}
                    onClick={() => removeTenant(id)}
                  />
                ))}
            {filters?.filters?.ulb?.length > 6 && (
              <>
                {showFilters &&
                  filters.filters.ulb.map((filter, id) => (
                    <RemoveableTag
                      key={id}
                      text={`${t(`DSS_HEADER_ULB`)}: ${t(`DSS_TB_${Digit.Utils.locale.getTransformedLocale(filter)}`)}`}
                      onClick={() => removeTenant(id)}
                    />
                  ))}
                {!showFilters && (
                  <p className="clearText cursorPointer" onClick={() => setShowFilters(true)}>
                    {t(`DSS_FILTER_SHOWALL`)}
                  </p>
                )}
                {showFilters && (
                  <p className="clearText cursorPointer" onClick={() => setShowFilters(false)}>
                    {t(`DSS_FILTER_SHOWLESS`)}
                  </p>
                )}
              </>
            )}
            <p className="clearText cursorPointer" onClick={clearAllTn}>
              {t(`DSS_FILTER_CLEAR_TN`)}
            </p>
          </div>
        )}
        {filters?.moduleLevel?.length > 0 && (
          <div className="tag-container">
            {!showFilters && filters?.moduleLevel && (
              <RemoveableTag
                key={filters?.moduleLevel}
                text={`${t(`DSS_HEADER_SERVICE`)}: ${t(filters?.moduleLevel)}`}
                onClick={() => removeService()}
              />
            )}
            <p className="clearText cursorPointer" onClick={clearAllServices}>
              {t(`DSS_FILTER_CLEAR`)}
            </p>
          </div>
        )}

        {appliedHierarchyChips?.length > 0 && (
          <div className="tag-container">
            {!showFilters &&
              appliedHierarchyChips
                .map((filter, id) => (
                  <RemoveableTag
                    key={id}
                    text={`${t(filter?.label)}: ${t(filter?.name)}`}
                    onClick={() => removeHierarchy(id)}
                  />
                ))}
            <p className="clearText cursorPointer" onClick={clearAllHierarchy}>
              {t(`DSS_FILTER_CLEAR`)}
            </p>
          </div>
        )}
        {mobileView ? (
          <div className="options-m">
            <div>
              <FilterIcon onClick={() => setIsFilterModalOpen(!isFilterModalOpen)} style />
            </div>
            <div className="divToBeHidden">
              <MultiLink
                className="multilink-block-wrapper"
                label={t(`ES_DSS_SHARE`)}
                icon={<ShareIcon className="mrsm" />}
                onHeadClick={(e) => {
                  setShowOptions(!showOptions);
                }}
                displayOptions={showOptions}
                options={shareOptions}
              />
            </div>
            <div onClick={handlePrint} className="divToBeHidden">
              <DownloadIcon />
              {t(`ES_DSS_DOWNLOAD`)}
            </div>
          </div>
        ) : null}
        <div>
          {tabArray && tabArray?.length > 1 && (
            <div className="dss-switch-tabs chart-row">
              <div className="dss-switch-tab-wrapper">
                {tabArray?.map((key) => (
                  <div className={tabState === key ? "dss-switch-tab-selected" : "dss-switch-tab-unselected"} onClick={() => setTabState(key)}>
                    {t(key)}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
        {(!isEnableIFixFilter || (!isDeptLoading && isEnableIFixFilter)) && dashboardConfig?.[0]?.visualizations
          .filter((row) => row.name === tabState)
          .map((row, key) => {
            return <Layout rowData={row} key={key} services={screenConfig} configName={dashboardConfig?.[0]?.name} />;
          })}
      </div>
    </FilterContext.Provider>
  );
};

export default DashBoard;
