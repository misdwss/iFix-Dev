import pandas as pd
from pydruid.client import *
from pydruid.db import connect
from pydruid.utils.filters import *
from datetime import date,datetime, timedelta
from kafka import KafkaProducer
from json import dumps
import schedule
import time
import os
import simplejson
# from dotenv import load_dotenv


def fetchDataFromDruid():
    # load_dotenv()
    
    print(os.environ['HOME'])
    print(os.environ.get('KAFKA_URL'))
    #connecting to druid and scanning raw fiscal event dataset
    # query = PyDruid('https://druid.ifix.org.in/', 'druid/v2/')
    query = PyDruid(os.environ.get('DRUID_INSTANCE'), 'druid/v2/')
    datetoday = date.today().strftime("%Y-%m-%d")
    datetomorrow = (date.today() + timedelta(1)).strftime("%Y-%m-%d")
    # datetoday='2021-08-24'
    # datetomorrow='2021-08-26'
    query.scan(datasource= os.environ.get('DATASOURCE_NAME'),
           granularity='all',
            intervals= 
              [datetoday+"/"+datetomorrow])
    dataset = query.export_pandas()
    # print(dataset)
    print('Fetching data')    
    return dataset
    
def aggregateData(incomingdata):
    dataset = incomingdata
    # dataset = pd.read_csv("/home/priyanka/Desktop/PyDruid/incomingData.csv")
    
    #group by project
    dataset['eventType'] = dataset['eventType'].str.lower().str.title()
    group = dataset.groupby(['project.name','eventType'])['amount'].sum().reset_index()
    
    #Mapping amounts to respective columns
    group.loc[group['eventType'] == 'Receipt', 'Total_Receipt'] = group['amount']
    group.loc[group['eventType'] == 'Payment', 'Total_Payment'] = group['amount']
    group.loc[group['eventType'] == 'Bill', 'Total_Bill'] = group['amount']
    group.loc[group['eventType'] == 'Demand', 'Total_Demand'] = group['amount']
    
    ##aggregating dataset
    aggdata = pd.DataFrame(columns=['project.name','Total_Receipt','Total_Payment','Total_Bill','Total_Demand','Total_Expenditure','Total_Revenue','Fiscal_Position','Cash_Position'])
    aggdata['project.name']=pd.unique(group['project.name']).tolist()

    for i in range(len(group)):
        if group.loc[i,"eventType"]=='Receipt':
            aggdata.loc[aggdata["project.name"]==group.loc[i,"project.name"], "Total_Receipt"] = group.loc[i, "Total_Receipt"]
        if group.loc[i,"eventType"]=='Payment':
            aggdata.loc[aggdata["project.name"]==group.loc[i,"project.name"], "Total_Payment"] = group.loc[i, "Total_Payment"]
        if group.loc[i,"eventType"]=='Bill':
            aggdata.loc[aggdata["project.name"]==group.loc[i,"project.name"], "Total_Bill"] = group.loc[i, "Total_Bill"]
        if group.loc[i,"eventType"]=='Demand':
            aggdata.loc[aggdata["project.name"]==group.loc[i,"project.name"], "Total_Demand"] = group.loc[i, "Total_Demand"]

    aggdata['Total_Expenditure']=0
    aggdata['Total_Revenue']=0 
    aggdata.fillna(0,inplace=True)
    aggdata['Fiscal_Position'] = aggdata.apply(lambda x: x.Total_Demand-x.Total_Bill, axis=1)
    aggdata['Cash_Position'] = aggdata.apply(lambda x: x.Total_Receipt-x.Total_Payment, axis=1)
    
    #Final Dataset
    finaldataset = pd.merge(dataset, aggdata, how="left", on=["project.name"])
    finaldataset.fillna('null')
    print('Aggregating Dataset...')
    # print(finaldataset)
    return finaldataset

def pushToKafka(finaldataset):
    producer = KafkaProducer(bootstrap_servers=[os.environ.get('KAFKA_URL')],
                         value_serializer=lambda x: 
                         simplejson.dumps(x, ignore_nan=True).encode('utf-8'))
    for index, row in finaldataset.iterrows():
        # print(row.to_dict())
        # producer.send('Topic-Python1',value= row.to_dict())
        
        producer.send(os.environ.get('TOPIC_NAME'),value= row.to_dict())
        
    print('Pushing to Kafka')
    
def controller():
    # connecting to druid and importing data from there 
    incomingdata = fetchDataFromDruid()
    
    #aggregating data
    finaldataset = aggregateData(incomingdata) 
    
    #pushing dataset to Kafka
    pushToKafka(finaldataset)
    # print('Running...')    

# def printJob():
#     print('I am working!')   
     
if __name__ == '__main__':
    # scheduling()

    # print(type(os.environ.get('TIME_STRING')))
    # print(type(os.environ.get('KAFKA_URL')))

    # schedule.every().day.at(os.environ.get('TIME_STRING')).do(controller)
    # schedule.every().minute.do(printJob)
    # while True:
    #     schedule.run_pending()
    #     time.sleep(1)
    controller()
     