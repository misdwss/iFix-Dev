CREATE TABLE eg_ifix_fiscal_event (
id varchar,
tenantid varchar NOT NULL,
sender varchar NOT NULL,
receivers JSONB,
eventtype  varchar NOT NULL,
ingestiontime bigint NOT NULL,
eventtime bigint NOT NULL,
referenceid varchar NOT NULL,
linkedEventid varchar,
linkedreferenceid  varchar,
attributes JSONB,
createdtime bigint,
createdby varchar,
lastmodifiedtime bigint,
lastmodifiedby varchar,

CONSTRAINT pk_ifix_fiscalevent PRIMARY KEY(id,tenantid),
CONSTRAINT uk_ifix_fiscalevent UNIQUE(id) 
);





CREATE TABLE eg_ifix_amount_detail (
id varchar,
tenantid varchar,
fiscaleventid varchar NOT NULL,
amount varchar NOT NULL,
coaid  varchar,
frombillingperiod bigint NOT NULL,
tobillingperiod bigint NOT NULL,
attributes JSONB,
createdtime bigint NOT NULL,
createdby varchar NOT NULL,
lastmodifiedtime bigint NOT NULL,
lastmodifiedby varchar NOT NULL,

CONSTRAINT pk_ifix_amount_detail PRIMARY KEY(id,tenantid)
, CONSTRAINT fk_eg_ifix_fiscal_event
      FOREIGN KEY(fiscaleventid) 
	  REFERENCES eg_ifix_fiscal_event(id)
    ON UPDATE CASCADE
  ON DELETE CASCADE

);