while getopts h:u:p:s:c:d: flag
do
    case "${flag}" in
        h) HOST_NAME=${OPTARG};;
        u) USERNAME=${OPTARG};;
        p) PASSWORD=${OPTARG};;
        s) SOURCE_DB=${OPTARG};;
        c) COLLECTIONS+=(${OPTARG});;
        d) DESTINATION_DB=${OPTARG};;
    esac
done
OUT=/tmp/ifix_dump
for col in ${COLLECTIONS[@]};
do
    mongodump --host ${HOST_NAME} -u ${USERNAME} -p ${PASSWORD} --db ${SOURCE_DB} --collection ${col}  --out ${OUT}
done
echo "MongoDB dump collected"
mongorestore --host ${HOST_NAME} -u ${USERNAME} -p ${PASSWORD} --db ${DESTINATION_DB} ${OUT}/${SOURCE_DB}
echo "Mongo restored successfully into ${DESTINATION_DB} database"
rm -rf ${OUT}
echo "MongoDB dump deleted"